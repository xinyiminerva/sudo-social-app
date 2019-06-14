package edu.lehigh.cse216.aztecs.backend;

import spark.Spark;

public abstract class MessagesAPI {
	public static void createAPI(Database db) {
		// Define a filter to handle user authentication:
		Spark.before("/messages/:id", (req, res) -> {
			// Don't check for GET/POST methods:
			String method = req.requestMethod();
			if(method != "PUT" && method != "DELETE")
				return;
			
			int idx;
			try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message ID");
        	}
			String authHeader = req.headers("Authorization");
			Message message = db.messages.readOneSimple(idx);
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
            if (!Authorizer.validateUser(authHeader, message.author_id))
            	throw new AccessForbiddenException("Unauthorized update to message " + idx);
		});

		
		// GET route that returns all Message objects.  All we do is get
        // the data, turn it into JSON, and return it.
		// If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/messages", (req, res) -> {
        	// Handle user identification:
            String auth = req.headers("Authorization");
            Integer author_id = null;
         	if(auth != null)
         		author_id = Authorizer.getUIDFromToken(auth);
         	if(author_id == null)
         		throw new AuthorizationFailedException("Authorization required to read messages.");
         	User caller = db.users.readOne(author_id);
        	
        	Integer thread = req.queryMap("thread").integerValue();
        	if(thread != null) {
        		// Get the message:
        		Message message = db.messages.readOne(thread, caller);
        		
        		// ensure status 200 OK, with a MIME type of JSON
                res.status(200);
                res.type("application/json");
                return Message.serialize(db.messages.readAll(message, caller));
        	}
        	
            // ensure status 200 OK, with a MIME type of JSON
            res.status(200);
            res.type("application/json");
            return Message.serialize(db.messages.readAll(caller));
        });

        // GET route that returns everything for a single message in the Database.
        // The ":id" suffix in the first parameter to get() becomes
        // req.params("id"), so that we can get the requested user ID.  If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a user with data.
        Spark.get("/messages/:id", (req, res) -> {
        	// Handle user identification:
            String auth = req.headers("Authorization");
            Integer author_id = null;
         	if(auth != null)
         		author_id = Authorizer.getUIDFromToken(auth);
         	if(author_id == null)
         		throw new AuthorizationFailedException("Authorization required to read message.");
         	User caller = db.users.readOne(author_id);
         	
        	// Parse the ID:
        	int idx;
        	try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse user ID");
        	}
        		
        	// establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Message.serialize(db.messages.readOne(idx, caller));
        });

        // POST route for adding a new message to the Database.  This will read
        // JSON from the body of the request, turn it into a Message
        // object, insert it, and return the newly created message.
        Spark.post("/messages", (req, res) -> {
        	// Handle user identification:
            String auth = req.headers("Authorization");
            Integer author_id = null;
         	if(auth != null)
         		author_id = Authorizer.getUIDFromToken(auth);
         	if(author_id == null)
         		throw new AuthorizationFailedException("Authorization required to post message.");
         	User caller = db.users.readOne(author_id);
         	
            // Parse the message body:
        	Message message;
        	try {
        		message = Message.deserialize(req.body());
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message body");
        	}
        	message.author_id = caller.id;
        	
            // Create the new user:
            message = db.messages.create(message, caller);
            
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Message.serialize(message);
        });

        // PUT route for updating a message in the Database. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (req, res) -> {
        	// Handle user identification:
            String auth = req.headers("Authorization");
            Integer author_id = null;
         	if(auth != null)
         		author_id = Authorizer.getUIDFromToken(auth);
         	if(author_id == null)
         		throw new AuthorizationFailedException("Authorization required to update like.");
         	User caller = db.users.readOne(author_id);
         	
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx;
            Message message;
            try {
            	idx = Integer.parseInt(req.params("id"));
        		message = Message.deserialize(req.body());
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message body");
        	}
            message.id = idx;
            
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Message.serialize(db.messages.updateOne(message, caller));
        });

        // DELETE route for removing a message from the Database
        Spark.delete("/messages/:id", (req, res) -> {
            // Parse user ID:
        	int idx;
        	try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message ID");
        	}
            
            // Delete the user:
            db.messages.deleteOne(idx, db);

            res.status(200);
            return "Ok";
		});

		
        // For all methods which are intentionally not implemented, return a 501 error:
        Spark.put("/messages", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.delete("/messages", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.post("/messages/:id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
	}
}

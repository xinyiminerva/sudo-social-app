package edu.lehigh.cse216.aztecs.backend;

import spark.Spark;

public abstract class LikesAPI {
	public static void createAPI(Database db) {
		// Define a filter to handle user authentication:
		Spark.before("/likes/:message_id/:user_id", (req, res) -> {
			// Don't check for GET/POST methods:
			String method = req.requestMethod();
			if(method != "PUT" && method != "DELETE")
				return;
			
			int idx;
			try {
        		idx = Integer.parseInt(req.params("user_id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse user ID");
        	}
			String authHeader = req.headers("Authorization");
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
			if (!Authorizer.validateUser(authHeader, idx))
				throw new AccessForbiddenException("Unauthorized update to like");
		});

		
		// GET route that returns all Like objects.  All we do is get
        // the data, turn it into JSON, and return it.
		// If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/likes", (req, res) -> {
        	Integer user_id = req.queryMap("user").integerValue();
        	if(user_id != null) {
        		// Get a handle to the user:
        		User user = db.users.readOne(user_id);
        		
        		// ensure status 200 OK, with a MIME type of JSON
                res.status(200);
                res.type("application/json");
                return Like.serialize(db.likes.readUnderAuthor(user));
        	}
        	
            // ensure status 200 OK, with a MIME type of JSON
            res.status(200);
            res.type("application/json");
            return Like.serialize(db.likes.readAll());
        });

        // GET route that returns everything for all likes in the Database under the given message.
        // The ":message_id" suffix in the first parameter to get() becomes
        // req.params("message_id"), so that we can get the requested message ID.  If
        // ":message_id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a like with data.
        Spark.get("/likes/:message_id", (req, res) -> {
            int message_id = Integer.parseInt(req.params("message_id"));
            
            // Get a handle to the message:
            Message message = db.messages.readOneSimple(message_id);
            
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Like.serialize(db.likes.readUnderMessage(message));
        });
        
        // GET route that returns everything for a single like in the Database.
        // The ":message_id" and ":user_id" suffixes in the parameters to get() become
        // req.params("message_id") and req.params("user_id"), so that we can get the
        // requested like compositeID.  If ":message_id" isn't a number, Spark will reply
        // with a status 500 Internal Server Error.
        // Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a like with data.
        Spark.get("/likes/:message_id/:user_id", (req, res) -> {
            int message_id = Integer.parseInt(req.params("message_id"));
            int user_id = Integer.parseInt(req.params("user_id"));

            // Make sure that the message & user both exist:
            db.messages.readOneSimple(message_id);
            db.users.readOne(user_id);
            
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Like.serialize(db.likes.readOne(message_id, user_id));
        });

        // POST route for adding a new like to the Database.  This will read
        // JSON from the body of the request, turn it into a Like
        // object, insert it, and return the newly created like.
        Spark.post("/likes", (req, res) -> {
            // Parse the body:
            Like like;
            try {
        		like = Like.deserialize(req.body());
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message body");
        	}
            
            // Handle user identification:
            String auth = req.headers("Authorization");
            Integer author_id = null;
         	if(auth != null)
         		author_id = Authorizer.getUIDFromToken(auth);
         	if(author_id == null)
         		throw new AuthorizationFailedException("Authorization required to post like.");
         	like.author_id = author_id;
            
         	// Confirm that the User & Message both actually exist:
         	db.users.readOne(like.author_id);
         	db.messages.readOneSimple(like.message_id);
         	
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Like.serialize(db.likes.create(like));
        });

        // PUT route for updating a like in the Database. This is almost
        // exactly the same as POST
        Spark.put("/likes/:message_id/:user_id", (req, res) -> {
        	// Parse the body:
            Like like;
            try {
        		like = Like.deserialize(req.body());
        		like.message_id = Integer.parseInt(req.params("message_id"));
                like.author_id = Integer.parseInt(req.params("user_id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message body");
        	}
            
            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            return Like.serialize(db.likes.updateOne(like));
        });

        // DELETE route for removing a user from the Database
        Spark.delete("/likes/:message_id/:user_id", (req, res) -> {
            // Parse the Like ID:
        	int message_id, user_id;
        	try {
        		message_id = Integer.parseInt(req.params("message_id"));
        		user_id = Integer.parseInt(req.params("user_id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse Like ID");
        	}
        		
            // Delete the like from the database:
            db.likes.deleteOne(message_id, user_id);

            res.status(200);
            return "Ok";
        });
        
        // For all methods which are intentionally not implemented, return a 501 error:
        Spark.put("/likes", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.delete("/likes", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.post("/likes/:message_id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.put("/likes/:message_id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.delete("/likes/:message_id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.post("/likes/:message_id/:user_id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
	}
}

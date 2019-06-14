package edu.lehigh.cse216.aztecs.backend;

import spark.Spark;

import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public abstract class UsersAPI {
	

	public static void createAPI(Database db) {
		// Define a filter to handle user authentication:
		Spark.before("/users/:id", (req, res) -> {
			// Don't check for GET/POST methods:
			String method = req.requestMethod();
			if(method != "PUT" && method != "DELETE")
				return;
			
			int idx;
			try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse user ID");
        	}
			String authHeader = req.headers("Authorization");
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
            if (!Authorizer.validateUser(authHeader, idx)) 
            	throw new AccessForbiddenException("Unauthorized update to user " + idx);
		});

		
		// GET route that returns all User objects.  All we do is get
        // the data, turn it into JSON, and return it.
		// If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/users", (req, res) -> {

        	//Query to get a complete user object from just a name
			String username = req.queryMap("username").value();
			if(username != null){
				User user;
				user = db.users.readOne(username);
				res.status(200);
				return User.serialize(user);
			}

			//Query to authenticate a user & give them a token. Must have user id and password (hash)
        	String idToke = req.queryMap("authenticate").value();
        	if(idToke!= null) {
				User user = Authorizer.authorize(idToke, db);

        		// ensure status 200 OK, with a MIME type of JSON
                res.status(200);
                res.type("application/json");
				return User.serialize(user);
        	}
        	
            // ensure status 200 OK, with a MIME type of JSON
            res.status(200);
            res.type("application/json");
            return User.serialize(db.users.readAll());
        });


			// GET route that returns everything for a single user in the Database.
			// The ":id" suffix in the first parameter to get() becomes
			// req.params("id"), so that we can get the requested user ID.  If
			// ":id" isn't a number, Spark will reply with a status 500 Internal
			// Server Error.  Otherwise, we have an integer, and the only possible
			// error is that it doesn't correspond to a user with data.
			Spark.get("/users/:id", (req, res) -> {
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
				User user = db.users.readOne(idx);
				return User.serialize(user);
			});

			// POST route for adding a new user to the Database.  This will read
			// JSON from the body of the request, turn it into a User
			// object, insert it, and return the newly created user.
			//
			// Note: removed during phase1 since we were supposed to remove this functionality, but left here in case it is needed in the future.
			/*
			Spark.post("/users", (req, res) -> {
				// Parse the message body:
				User user;
				try {
					user = User.deserialize(req.body());
				} catch(Throwable e) {
					throw new MalformedRequestException("Unable to parse message body");
				}
				
				// Create the new user:
				user = db.users.create(user);
				
				// establish a MIME type of JSON
				res.type("application/json");
				res.status(200);
				return User.serialize(user);
			});
			*/

        // PUT route for updating a user in the Database. This is almost
        // exactly the same as POST. Currently only allows updating of password/id number based on updateOne in UsersTable.java
        Spark.put("/users/:id", (req, res) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx;
            User user;
            try {
            	idx = Integer.parseInt(req.params("id"));
        		user = User.deserialize(req.body());
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message body");
        	}
            user.id = idx;

            // establish a MIME type of JSON
            res.type("application/json");
            res.status(200);
            user = db.users.updateOne(user);
            return User.serialize(user);
        });

        // DELETE route for removing a user from the Database
        Spark.delete("/users/:id", (req, res) -> {
            // Parse user ID:
        	int idx;
        	try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse user ID");
        	}
            
            // Delete the user:
            db.users.deleteOne(idx);

            res.status(200);
            return "Ok";
        });

        // For all methods which are intentionally not implemented, return a 501 error:
		Spark.post("/users", (req, res) -> {
			throw new MethodNotImplementedException();
		});
        Spark.put("/users", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.delete("/users", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
        Spark.post("/users/:id", (req, res) -> {
        	throw new MethodNotImplementedException();
        });
	}
}

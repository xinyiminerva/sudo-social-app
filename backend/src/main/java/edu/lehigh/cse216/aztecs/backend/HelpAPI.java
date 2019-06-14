
package edu.lehigh.cse216.aztecs.backend;

import spark.Spark;
import javax.servlet.MultipartConfigElement;
//import com.google.api.services.drive.model.File;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.OutputStream;

import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;

//import java.util.Collections;
//import java.util.List;

public abstract class HelpAPI {

			// Define a static JSON serializer:
			private static final Gson gson = new Gson();

	public static void createAPI(Database db) {

		// Define a filter to handle user authentication:
		Spark.before("/slack", (req, res) -> {
			// Don't check for GET/POST methods:
			String method = req.requestMethod();
			if(method != "POST")
				return;
			
			String msg;
			try {
      	msg = req.body();
      } catch(Throwable e) {
      	throw new MalformedRequestException("Unable to get message");
      }
			String authHeader = req.headers("Authorization");
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
			int id = 4; //Authorizer.getUIDFromToken(authHeader);
			return;
		});

		//Post route for sending a message to slack
    Spark.post("/slack", (req, res) -> {
			// Parse parent ID:
			int idx;
			
			
			// Check permissions:
			String authHeader = req.headers("Authorization");
			// Validate the origin's authorization:
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("POST requires authorization token");

			int id = Authorizer.getUIDFromToken(authHeader);
			String msg = req.body().split("text")[1].replaceAll("\"", "").replaceAll("}", "").substring(2);
			//System.err.println("MESSAGE  " + msg);
			// Send to Slack new message
			try {
					String s = "User " + id + " said: " + msg;
					//System.err.println("{\n\t\"text\": \"" + s + "\"\n}");
					HttpResponse<String> response = Unirest.post("https://hooks.slack.com/services/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")
  					.header("Content-Type", "application/json")
  					.header("Authorization", "" + id)
  					.header("cache-control", "no-cache")
  					.header("Postman-Token", "aad191f6-3d77-4d7a-8a1d-6fd82d9ee976")
  					.body("{\n\t\"text\": \"" + s + "\"\n}")
  					.asString();
			} catch (Throwable cause) {
					cause.printStackTrace();
			}

			

			res.status(200);
        return gson.toJson("Good");
		});
			

		//Post route for sending a message to slack
    Spark.put("/slack", (req, res) -> {
			// Parse parent ID:
			int idx;
			
			
			// Check permissions:
			String authHeader = req.headers("Authorization");
			// Validate the origin's authorization:
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("POST requires authorization token");

			int id = Authorizer.getUIDFromToken(authHeader);
			String msg = req.body().split("msg")[1].substring(2).replaceAll("\"", "").replaceAll("}", "").replaceAll(" ", "");
			//System.err.println("MESSAGE  " + msg);
			// Send to Slack new message
			try {
					String s = "User " + id + " flagged message " + msg;
					System.err.println("{\n\t\"text\": \"" + s + "\"\n}");
					HttpResponse<String> response = Unirest.post("https://hooks.slack.com/services/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")
  					.header("Content-Type", "application/json")
  					.header("Authorization", "" + id)
  					.header("cache-control", "no-cache")
  					.header("Postman-Token", "aad191f6-3d77-4d7a-8a1d-6fd82d9ee976")
  					.body("{\n\t\"text\": \"" + s + "\"\n}")
  					.asString();
			} catch (Throwable cause) {
					cause.printStackTrace();
			}

			res.status(200);
        return gson.toJson("Gooder");
		});
		
	}
}

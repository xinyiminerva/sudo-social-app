
package edu.lehigh.cse216.aztecs.backend;

import spark.Spark;
import javax.servlet.MultipartConfigElement;
//import com.google.api.services.drive.model.File;

import java.io.OutputStream;

import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;

//import java.util.Collections;
//import java.util.List;

public abstract class AttachmentsAPI {
	public static void createAPI(Database db) {

		// Define a filter to handle user authentication:
		Spark.before("/attachments/:id", (req, res) -> {
			// Don't check for GET/POST methods:
			String method = req.requestMethod();
			if(method != "PUT" && method != "DELETE")
				return;
			
			int idx;
			try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse attachment ID");
        	}
			String authHeader = req.headers("Authorization");
			Attachment a = db.attachments.readOne(idx);
			Message m = db.messages.readOneSimple(a.message);
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
            if (!Authorizer.validateUser(authHeader, m.author_id))
            	throw new AccessForbiddenException("Unauthorized update to attachment " + idx);
		});

		//Get route for downloading a file
		Spark.get("/attachments/:id", (req, res) -> {
            // Parse user ID:
        	int idx;
        	try {
        		idx = Integer.parseInt(req.params("id"));
        	} catch(Throwable e) {
        		throw new MalformedRequestException("Unable to parse message ID");
			}

			Attachment a = db.attachments.readOne(idx);
			a = GoogleStorage.download(a);

			res.header("Content-disposition", "attachment; filename=File;");
			res.raw().setContentType(a.mime_type);
			OutputStream ostream = res.raw().getOutputStream();	
			ostream.write(Files.readAllBytes(a.file.toPath()));
            ostream.flush();
    
			res.status(200);
			return res;
        });
		
		//Post route for uploading a file
        Spark.post("/attachments/:mid", (req, res) -> {
			// Parse parent ID:
			int idx;
			try {
				idx = Integer.parseInt(req.params("mid"));
			} catch(Throwable e){
				throw new MalformedRequestException("Unable to parse message ID");
			}
			
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
			
			// Check permissions:
			String authHeader = req.headers("Authorization");
			Message m = db.messages.readOneSimple(idx);
			
			// Ensure that the header is present:
			if(authHeader == null)
				throw new AuthorizationFailedException("Request requires authorization token");
			
			// Validate the origin's authorization:
            if (!Authorizer.validateUser(authHeader, m.author_id))
            	throw new AccessForbiddenException("Unauthorized update to message " + idx);


			Attachment a = new Attachment(
				null,
				idx,
				req.raw().getPart("file").getContentType(),
				req.raw().getPart("file").getInputStream()
			);

			GoogleStorage.upload(a);

			a = db.attachments.create(a);

			res.status(200);
            return Attachment.serialize(a);
		});
			
		//Delete route for deleting a file
		Spark.delete("/attachments/:id", (req, res) -> {
			// Parse user ID:
			int idx;
			try {
				idx = Integer.parseInt(req.params("id"));
			} catch(Throwable e){
				throw new MalformedRequestException("Unable to parse message ID");
			}

			Attachment a = db.attachments.deleteOne(idx); //delete attachment from Database table

			res.status(200);
            return Attachment.serialize(a);
        });
		
		//Put route for updating a file
		Spark.put("/attachments/:id", (req, res) -> {
			// Parse parent ID:
			int idx;
			try {
				idx = Integer.parseInt(req.params("id"));
			} catch(Throwable e){
				throw new MalformedRequestException("Unable to parse message ID");
			}

			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

			Attachment a = db.attachments.readOne(idx);
			a = new Attachment(
				a.id,
				a.message,
				req.raw().getPart("file").getContentType(),
				a.file_id,
				req.raw().getPart("file").getInputStream()
			);

			GoogleStorage.update(a);
			
			res.status(200);
			return Attachment.serialize(a);
        });
	}
}

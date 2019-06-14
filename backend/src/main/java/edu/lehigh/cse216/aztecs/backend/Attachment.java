package edu.lehigh.cse216.aztecs.backend;

import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.Gson;

/**
 * This class is designed to represent a single Attachment, and can be converted to
 * and from JSON for communication with the clients.
 * See the API documentation for more detail about what exactly each field does.
 */
public class Attachment {

	// Define all public fields of the Attachment:
	public Integer id;
	public Integer message; // parent ID number
	public String mime_type;
	public transient File file; //ignored by JSON
	public String file_id; //fileId that google used for file

    /**
     * Default Constructor
	 * Create an attachment
	 */
	public Attachment() {
		this.id = null;
		this.message = null;
        this.mime_type = null;
		this.file = null;
		this.file_id = null;
	}

	/**
	 * Create an attachment object by requiring all parameters
	 */
	public Attachment(Integer id, Integer message, String mime_type, File file) {
		this.id = id;
		this.message = message;
        this.mime_type = mime_type;
		this.file = file;
		this.file_id = null;
	}
    
    /**
     * Create an attachment object without the File handle
     * @param id The ID of the attachment
     * @param message The ID of the parent
     * @param mime_type The type of the attachment
     */
    public Attachment(Integer id, Integer message, String mime_type) {
        this.id = id;
		this.message = message;
		this.mime_type = mime_type;	
		this.file_id = null;
	}
	
    /**
     * Create an attachment object without the File handle
     * @param id The ID of the attachment
     * @param message The ID of the parent
     * @param mime_type The type of the attachment
	 * @param file_id The file ID google provided
     */
    public Attachment(Integer id, Integer message, String mime_type, String file_id) {
        this.id = id;
		this.message = message;
		this.mime_type = mime_type;	
		this.file_id = file_id;
    }

	/**
	 * Create an attachment object to accomidate a POST request
	 */
	public Attachment(Integer id, Integer message, String mime_type, InputStream istream) throws IOException {
		this.id = id;
		this.message = message;
		this.mime_type = mime_type;	
		this.file_id = null; 
        
        // Allocate the temporary file for storage:
        this.file = File.createTempFile("TheBuzz", "tmp");
        this.file.deleteOnExit();
        Files.copy(istream, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Create an attachment object to accomidate a PUT request
	 */
	public Attachment(Integer id, Integer message, String mime_type, String file_id, InputStream istream) throws IOException {
		this.id = id;
		this.message = message;
		this.mime_type = mime_type;	
		this.file_id = file_id; 
        
        // Allocate the temporary file for storage:
        this.file = File.createTempFile("TheBuzz", "tmp");
        this.file.deleteOnExit();
        Files.copy(istream, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Copy Constructor
	 */
	public Attachment(Attachment other) {
		this.id = other.id;
		this.message = other.message;
        this.mime_type = other.mime_type;
		this.file = other.file;
		this.file_id = other.file_id;
	}
	
	/**
	 * Weird Copy Constructor
	 */
	public Attachment(Attachment other, InputStream istream) throws IOException {
		this.id = other.id;
		this.message = other.message;
        this.mime_type = other.mime_type;
		this.file_id = other.file_id;

		// Allocate the temporary file for storage:
        this.file = File.createTempFile("TheBuzz", "tmp");
        this.file.deleteOnExit();
        Files.copy(istream, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Weird Copy Constructor #2
	 */
	public Attachment(Attachment other, File file) {
		this.id = other.id;
		this.message = other.message;
        this.mime_type = other.mime_type;
		this.file_id = other.file_id;

		// Allocate the temporary file for storage:
        this.file = file;
    }
	
	// Define a static JSON serializer:
	private static final Gson gson = new Gson();
		
	/**
	 * Define a static method for serializing Attachment objects to JSON
	 * @param attachment A reference to the Attachment object to serialize
	 * @return A JSON encoded String representing the attachment data
	 */
	public static String serialize(Attachment attachment) {
		return gson.toJson(attachment, Attachment.class);
	}
		
	/**
	 * Define an overloaded serializer method to serialize ArrayLists of Attachment Objects
	 * @param attachment A reference to the Attachment object ArrayList to serialize
	 * @return A JSON encoded String representing the attachment data
	 */
	public static String serialize(List<Attachment> attachment) {
		return gson.toJson(attachment, List.class);
	}
		
	/**
	 * Define a static method for deserializing Attachment objects from JSON
	 * @param json A JSON encoded String representing the attachment data
	 * @return A deserialized Attachment object
	 */
	public static Attachment deserialize(String json) {
		return gson.fromJson(json, Attachment.class);
	}

	
}
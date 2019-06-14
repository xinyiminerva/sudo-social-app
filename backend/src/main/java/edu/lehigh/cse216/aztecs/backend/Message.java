package edu.lehigh.cse216.aztecs.backend;

import java.util.List;
import com.google.gson.Gson;

/**
 * This class is designed to represent a single Message, and can be converted to
 * and from JSON for communication with the clients.
 * See the API documentation for more detail about what exactly each field does.
 */
public class Message {
	// Define all public fields of the Message:
	public Integer id;
	public Integer author_id;
	public Integer parent_id;
	public String message;
	public Integer likes;
	public Boolean liked;	
	public Integer comments;
	public List<Integer> attachments;
	
	/**
	 * Create a Message object by requiring all parameters
	 */
	public Message(Integer id, Integer author_id, Integer parent_id, String message, Integer likes, Boolean liked, Integer comments, List<Integer> attachments) {
		this.id = id;
		this.author_id = author_id;
		this.parent_id = parent_id;
		this.message = message;
		this.likes = likes;
		this.liked = liked;
		this.comments = comments;
		this.attachments = attachments;
	}
	
	/**
	 * Create a message object to accomidate a POST request
	 */
	public Message(Integer id, Integer author_id, Integer parent_id, String message, List<Integer> attachments) {
		this.id = id;
		this.author_id = author_id;
		this.parent_id = parent_id;
		this.message = message;
		this.likes = null;
		this.liked = null;
		this.comments = null;
		this.attachments = attachments;
	}
	
	/**
	 * Create a message object to accomidate a PUT request
	 */
	public Message(Integer id, String message) {
		this.id = id;
		this.author_id = null;
		this.parent_id = null;
		this.message = message;
		this.likes = null;
		this.liked = null;
		this.comments = null;
		this.attachments = null;
	}
	
	/**
	 * Copy Constructor
	 */
	public Message(Message other) {
		this.id = other.id;
		this.author_id = other.author_id;
		this.parent_id = other.parent_id;
		this.message = other.message;
		this.likes = other.likes;
		this.liked = other.liked;
		this.comments = other.comments;
		this.attachments = other.attachments;
	}
	
	// Define a static JSON serializer:
	private static final Gson gson = new Gson();
		
	/**
	 * Define a static method for serializing Message objects to JSON
	 * @param message A reference to the Message object to serialize
	 * @return A JSON encoded String representing the message data
	 */
	public static String serialize(Message message) {
		return gson.toJson(message, Message.class);
	}
		
	/**
	 * Define an overloaded serializer method to serialize ArrayLists of Message Objects
	 * @param message A reference to the Message object ArrayList to serialize
	 * @return A JSON encoded String representing the message data
	 */
	public static String serialize(List<Message> message) {
		return gson.toJson(message, List.class);
	}
		
	/**
	 * Define a static method for deserializing Message objects from JSON
	 * @param json A JSON encoded String representing the message data
	 * @return A deserialized Message object
	 */
	public static Message deserialize(String json) {
		return gson.fromJson(json, Message.class);
	}

	
}

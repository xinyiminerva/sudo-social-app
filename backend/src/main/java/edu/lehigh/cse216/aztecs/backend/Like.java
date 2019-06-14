package edu.lehigh.cse216.aztecs.backend;

import java.util.List;

import com.google.gson.Gson;

/**
 * This class is designed to represent a single Like, and can be converted to
 * and from JSON for communication with the clients.
 * See the API documentation for more detail about what exactly each field does.
 */
public class Like {
	public Boolean liked;
	public Integer author_id;
	public Integer message_id;
	
	/**
	 * Create a Like object by requiring all parameters
	 */
	public Like(Boolean liked, Integer author_id, Integer message_id) {
		this.liked = liked;
		this.author_id = author_id;
		this.message_id = message_id;
	}
	
	/**
	 * Create a Like object to accomidate a PUT request
	 */
	public Like(Boolean liked) {
		this.liked = liked;
		this.author_id = null;
		this.message_id = null;
	}
	
	/**
	 * Copy Constructor
	 */
	public Like(Like other) {
		this.liked = other.liked;
		this.author_id = other.author_id;
		this.message_id = other.message_id;
	}
	
	// Define a static JSON serializer:
	private static final Gson gson = new Gson();
			
	/**
	 * Define a static method for serializing Like objects to JSON
	 * @param like A reference to the Like object to serialize
	 * @return A JSON encoded String representing the like data
	 */
	public static String serialize(Like like) {
		return gson.toJson(like, Like.class);
	}
		
	/**
	 * Define an overloaded serializer method to serialize ArrayLists of Like Objects
	 * @param like A reference to the Like object ArrayList to serialize
	 * @return A JSON encoded String representing the like data
	 */
	public static String serialize(List<Like> like) {
		return gson.toJson(like, List.class);
	}
		
	/**
	 * Define a static method for deserializing Like objects from JSON
	 * @param json A JSON encoded String representing the like data
	 * @return A deserialized Like object
	 */
	public static Like deserialize(String json) {
		return gson.fromJson(json, Like.class);
	}
}

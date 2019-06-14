package edu.lehigh.cse216.aztecs.backend;

import com.google.gson.Gson;
import java.util.List;

/**
 * This class is designed to represent a single User, and can be converted to
 * and from JSON for communication with the clients.
 * See the API documentation for more detail about what exactly each field does.
 */
public class User {
	// Define all public fields of the User:
	public Integer id;
	public String name;
	public String token;
	public String bio;
	
	/**
	 * Create a User object by requiring all parameters
	 * @param id The ID number of the user
	 * @param name The user's username
	 * @param salt The user's unique salt
	 * @param pwd_hash The user's hashed password
	 * @param token The user's authorization token
	 * @param bio The user's bio
	 */
	public User(Integer id, String name, String bio, String token) {
		this.id = id;
		this.name = name;
		this.bio = bio;
		this.token = token;
	}

	/**
	 * Create a User object without a token or bio
	 * @param id The ID number of the user
	 * @param name The user's username
	 * @param salt The user's unique salt
	 * @param pwd_hash The user's unique hashed password
	 */
	public User(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.token = null;
		this.bio = null;
	}
	
	/**
	 * Copy Constructor
	 * @param other The other User object from which to copy all data fields
	 */
	public User(User other) {
		this.id = other.id;
		this.name = other.name;
		this.token = other.token;
		this.bio = other.bio;
	}
	
	// Define a static JSON serializer:
	private static final Gson gson = new Gson();
	
	/**
	 * Define a static method for serializing User objects to JSON
	 * @param user A reference to the User object to serialize
	 * @return A JSON encoded String representing the user data
	 */
	public static String serialize(User user) {
		return gson.toJson(user, User.class);
	}
	
	/**
	 * Define an overloaded serializer method to serialize ArrayLists of User Objects
	 * @param users A reference to the User object ArrayList to serialize
	 * @return A JSON encoded String representing the user data
	 */
	public static String serialize(List<User> users) {
		return gson.toJson(users, List.class);
	}
	
	/**
	 * Define a static method for deserializing User objects from JSON
	 * @param json A JSON encoded String representing the user data
	 * @return A deserialized User object
	 */
	public static User deserialize(String json) {
		return gson.fromJson(json, User.class);
	}
}

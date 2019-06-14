package edu.lehigh.cse216.aztecs.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Defines a class to manage interactions with the "users" table within the database.
 */
public class UsersTable {
	// Define a set of static SQL queries to run against the database:
	private static final String SELECT_ALL = "SELECT * FROM users";
	private static final String SELECT_ONE = "SELECT * FROM users WHERE id = ? LIMIT 1";
	private static final String SELECT_ONE_BY_NAME = "SELECT * FROM users WHERE username = ? LIMIT 1";
	private static final String DELETE_ONE = "DELETE FROM users WHERE id = ?";
	private static final String INSERT_ONE = "INSERT INTO users VALUES (default, ?) RETURNING id";
	// TODO: add ability to change username, and make sure the new username is in email format
    private static final String UPDATE_ONE = "UPDATE users SET bio = ? WHERE id = ?"; //updates bio
	

	// Declare a set of prepared statements to allow for more responsive queries:
	private PreparedStatement selectAll;
	private PreparedStatement selectOne;
	private PreparedStatement selectOneByName;
	private PreparedStatement deleteOne;
	private PreparedStatement insertOne;
	private PreparedStatement updateOne;
	
	/**
	 * Initialize the table
	 */
	public UsersTable(Connection connection) throws SQLException {
		this.selectAll = connection.prepareStatement(UsersTable.SELECT_ALL);
		this.selectOne = connection.prepareStatement(UsersTable.SELECT_ONE);
		this.selectOneByName = connection.prepareStatement(UsersTable.SELECT_ONE_BY_NAME);
		this.deleteOne = connection.prepareStatement(UsersTable.DELETE_ONE);
		this.insertOne = connection.prepareStatement(UsersTable.INSERT_ONE);
		this.updateOne = connection.prepareStatement(UsersTable.UPDATE_ONE);
	}
	
	/**
     * Creates a new User
     * @param user The User Object representing the user-data
     * @return The new User object
     */
    public User create(User user) throws APIException {
    	// Check for a bad request:
    	if (user == null || user.name == null)
          throw new MalformedRequestException("Cannot create a null user");

    	// Make sure that the username isn't already in use:
    	try {
    		readOne(user.name);
    		throw new ConflictingEntryException("Username " + user.name + " is unavailable");
    	} catch(ResourceNotFoundException e) {}
    	
    	// Run the query:
        try {
            insertOne.setString(1, user.name);
            ResultSet rs = insertOne.executeQuery();
            if(!rs.next())
            	throw new DatabaseFailureException("Unable to create user");
            return readOne(rs.getInt("id"));
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        } catch(ResourceNotFoundException e) {
        	e.printStackTrace();
        	throw new DatabaseFailureException("Failed to create user");
        }
    }
	
	/**
     * Query the database for a list of all Users
     * @return All Users, as an ArrayList
     */
    public ArrayList<User> readAll() throws APIException {
    	ArrayList<User> res = new ArrayList<User>();
    	try {
    		ResultSet rs = selectAll.executeQuery();
    		while (rs.next()) {
    			res.add(new User(rs.getInt("id"), rs.getString("username"), null, null));
    		}
    		rs.close();
    		return res;
    	} catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
	
    /**
     * Get all data for a specific User by their ID
     * @param id The id of the User being requested
     * @return The User Object, or null if the ID was invalid
     */
    public User readOne(int id) throws APIException {
    	try {
    		selectOne.setInt(1, id);
    		ResultSet rs = selectOne.executeQuery();
    		if (!rs.next())
    			throw new ResourceNotFoundException("User #" + id + " not found");
    		
    		User res = new User(rs.getInt("id"), rs.getString("username"),  rs.getString("bio"), null);
    		rs.close();
    		return res;
    	} catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Get all data for a specific User by their username
     * @param name The username of the User being requested
     * @return The User Object
     */
    public User readOne(String name) throws APIException {
        try {
            selectOneByName.setString(1, name);
            ResultSet rs = selectOneByName.executeQuery();
            if (!rs.next())
            	throw new ResourceNotFoundException("User \"" + name + "\" not found");
            return new User(rs.getInt("id"), rs.getString("username"),  rs.getString("bio"), null);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Update the user-data for a User in the database
     * @param user The User Object to apply to the database
     * @return The new User Object
     */
    public User updateOne(User user) throws APIException {
    	// Stage the update:
        try {
            if(user.bio != null) {
                updateOne.setString(1, user.bio);
                updateOne.setInt(2, user.id);
                updateOne.executeUpdate();
            }

            return this.readOne(user.id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Delete a user by ID
     * @param id The id of the user to delete
     */
   void deleteOne(int id) throws APIException {
    	// Check that the entry exists:
    	readOne(id);
    	
        try {
            deleteOne.setInt(1, id);
            int count = deleteOne.executeUpdate();
            if (count <= 0)
            	throw new DatabaseFailureException("Failed to delete user");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
}

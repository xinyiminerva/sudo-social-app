package edu.lehigh.cse216.aztecs.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Defines a class to manage interactions with the "likes" table within the database.
 */
public class LikesTable {
	// Define a set of static SQL queries to run against the database:
	private static final String SELECT_ALL = "SELECT * FROM likes";
	private static final String SELECT_ONE = "SELECT * FROM likes WHERE message_id = ? AND liker = ? LIMIT 1";
	private static final String DELETE_ONE = "DELETE FROM likes WHERE message_id = ? AND liker = ?";
	private static final String INSERT_ONE = "INSERT INTO likes VALUES (?, ?, ?)";
	private static final String UPDATE_ONE = "UPDATE likes SET liked = ? WHERE message_id = ? AND liker = ?";
	
	private static final String SELECT_UNDER_MESSAGE = "SELECT * FROM likes WHERE message_id = ?";
	private static final String SELECT_UNDER_AUTHOR = "SELECT * FROM likes WHERE liker = ?";
	

	// Declare a set of prepared statements to allow for more responsive queries:
	private PreparedStatement selectAll;
	private PreparedStatement selectOne;
	private PreparedStatement deleteOne;
	private PreparedStatement insertOne;
	private PreparedStatement updateOne;
	
	private PreparedStatement selectUnderMessage;
	private PreparedStatement selectUnderAuthor;
	
	/**
	 * Initialize the table
	 */
	public LikesTable(Connection connection) throws SQLException {
		this.selectAll = connection.prepareStatement(LikesTable.SELECT_ALL);
		this.selectOne = connection.prepareStatement(LikesTable.SELECT_ONE);
		this.deleteOne = connection.prepareStatement(LikesTable.DELETE_ONE);
		this.insertOne = connection.prepareStatement(LikesTable.INSERT_ONE);
		this.updateOne = connection.prepareStatement(LikesTable.UPDATE_ONE);
		
		this.selectUnderAuthor = connection.prepareStatement(LikesTable.SELECT_UNDER_AUTHOR);
		this.selectUnderMessage = connection.prepareStatement(LikesTable.SELECT_UNDER_MESSAGE);
	}
	
	/**
     * Creates a new Like
     * @param like The Like Object representing the like-data
     * @return The new Like object
     */
    public Like create(Like like) throws APIException {
    	// Check for a bad request:
    	if (like == null)
    		throw new MalformedRequestException("Cannot create a null like");
    	
    	// Run the query:
        try {
            insertOne.setBoolean(1, like.liked);
            insertOne.setInt(2, like.author_id);
            insertOne.setInt(3, like.message_id);
            insertOne.execute();
            return readOne(like.message_id, like.author_id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        } catch(ResourceNotFoundException e) {
        	e.printStackTrace();
        	throw new DatabaseFailureException("Failed to create user");
        }
    }
	
	/**
     * Query the database for a list of all Likes
     * @return All Likes, as an ArrayList
     */
    public ArrayList<Like> readAll() throws APIException {
    	ArrayList<Like> res = new ArrayList<Like>();
        try {
            ResultSet rs = selectAll.executeQuery();
            while (rs.next()) {
                res.add(new Like(rs.getBoolean("liked"), rs.getInt("liker"), rs.getInt("message_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Query the database for a list of all Likes related to the given message
     * @param message The message to lookup
     * @return All related Likes, as an ArrayList
     */
    public ArrayList<Like> readUnderMessage(Message message) throws APIException {
        ArrayList<Like> res = new ArrayList<Like>();
        try {
        	selectUnderMessage.setInt(1, message.id);
            ResultSet rs = selectUnderMessage.executeQuery();
            while (rs.next()) {
                res.add(new Like(rs.getBoolean("liked"), rs.getInt("liker"), rs.getInt("message_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Query the database for a list of all Likes posted by the given user
     * @param author The user to lookup
     * @return All related Likes, as an ArrayList
     */
    public ArrayList<Like> readUnderAuthor(User author) throws APIException {
        ArrayList<Like> res = new ArrayList<Like>();
        try {
        	selectUnderAuthor.setInt(1, author.id);
            ResultSet rs = selectUnderAuthor.executeQuery();
            while (rs.next()) {
                res.add(new Like(rs.getBoolean("liked"), rs.getInt("liker"), rs.getInt("message_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
	
    /**
     * Get all data for a specific Like by it's composite ID
     * @param message_id The ID number of the message with which the like is associated
     * @param author_id The ID number of the user with which the like is associated
     * @return The Like Object
     */
    public Like readOne(int message_id, int author_id) throws APIException {
        try {
            selectOne.setInt(1, message_id);
            selectOne.setInt(2, author_id);
            ResultSet rs = selectOne.executeQuery();
            if (!rs.next())
            	throw new ResourceNotFoundException("Like \"" + message_id + ":" + author_id + "\" not found");
            
            return new Like(rs.getBoolean("liked"), rs.getInt("liker"), rs.getInt("message_id"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Update the like-data for a Like in the database
     * @param user The Like Object to apply to the database
     * @return The new Like Object
     */
    public Like updateOne(Like like) throws APIException {
    	// Try to update the Like object:
        try {
            updateOne.setBoolean(1, like.liked);
            updateOne.setInt(2, like.message_id);
            updateOne.setInt(3, like.author_id);
            updateOne.executeUpdate();
            return this.readOne(like.message_id, like.author_id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Delete a like by composite ID
     * @param message_id The ID number of the message with which the like is associated
     * @param author_id The ID number of the user with which the like is associated
     */
    void deleteOne(int message_id, int author_id) throws APIException {
        try {
            deleteOne.setInt(1, message_id);
            deleteOne.setInt(2, author_id);
            int count = deleteOne.executeUpdate();
            if (count <= 0)
              throw new DatabaseFailureException("Failed to delete like " + message_id + ":" + author_id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
}

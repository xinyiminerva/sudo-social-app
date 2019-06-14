package edu.lehigh.cse216.aztecs.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Defines a class to manage interactions with the "users" table within the database.
 */
public class MessagesTable {
	// Define a set of static SQL queries to run against the database:
	private static final String SELECT_ONE = "SELECT * FROM messages WHERE id = ?";
	private static final String DELETE_ONE = "DELETE FROM messages WHERE id = ?";
	private static final String INSERT_ONE = "INSERT INTO messages VALUES (default, ?, ?) RETURNING id";
	private static final String UPDATE_ONE = "UPDATE messages SET message = ? WHERE id = ?";

	private static final String INSERT_PARENT = "INSERT INTO parent VALUES (?, ?)";
	private static final String SELECT_PARENT = "SELECT parent_id FROM parent WHERE message_id = ? LIMIT 1";

	private static final String SELECT_ALL = "SELECT * FROM messages WHERE id NOT IN (SELECT message_id from parent)";
	private static final String SELECT_THREAD = "SELECT * FROM messages WHERE id IN (SELECT message_id FROM parent WHERE parent_id = ?)";
	private static final String COUNT_LIKES = "SELECT SUM(CASE WHEN liked = true THEN 1 ELSE -1 END) AS net_likes FROM likes WHERE message_id = ?";
	private static final String CHECK_LIKED = "SELECT liked FROM likes WHERE liker = ? AND message_id = ? LIMIT 1";
    private static final String COUNT_COMMENTS = "SELECT COUNT(*) AS comment_count FROM parent WHERE parent_id = ?";
    private static final String SELECT_ATTACHMENTS = "SELECT id FROM attachments WHERE message = ?";
	
	// Declare a set of prepared statements to allow for more responsive queries:
	private PreparedStatement selectAll;
	private PreparedStatement selectOne;
	private PreparedStatement deleteOne;
	private PreparedStatement insertOne;
	private PreparedStatement updateOne;

	private PreparedStatement insertParent;
	private PreparedStatement readParentId;
	private PreparedStatement selectThread;
	private PreparedStatement countLikes;
	private PreparedStatement checkLiked;
    private PreparedStatement countComments;
    private PreparedStatement selectAttachments;

	/**
	 * Initialize the table
	 */
	public MessagesTable(Connection connection) throws SQLException {
		this.selectAll = connection.prepareStatement(MessagesTable.SELECT_ALL);
		this.selectOne = connection.prepareStatement(MessagesTable.SELECT_ONE);
		this.deleteOne = connection.prepareStatement(MessagesTable.DELETE_ONE);
		this.insertOne = connection.prepareStatement(MessagesTable.INSERT_ONE);
		this.updateOne = connection.prepareStatement(MessagesTable.UPDATE_ONE);

		this.insertParent = connection.prepareStatement(MessagesTable.INSERT_PARENT);
		this.readParentId = connection.prepareStatement(MessagesTable.SELECT_PARENT);
		this.selectThread = connection.prepareStatement(MessagesTable.SELECT_THREAD);
		this.countLikes = connection.prepareStatement(MessagesTable.COUNT_LIKES);
		this.checkLiked = connection.prepareStatement(MessagesTable.CHECK_LIKED);
        this.countComments = connection.prepareStatement(MessagesTable.COUNT_COMMENTS);
        this.selectAttachments = connection.prepareStatement(MessagesTable.SELECT_ATTACHMENTS);
	}
    
    /**
    * Get a list of all attachments registered under a given message
    */
    protected ArrayList<Integer> getAttachments(int id) throws APIException {
        ArrayList<Integer> res = new ArrayList<Integer>();
        try {
            selectAttachments.setInt(1, id);
            ResultSet rs = selectAttachments.executeQuery();
            while (rs.next()) {
            	// Parse the attachment objects from the database:
                res.add(rs.getInt("id"));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }

	/**
     * Define a static method to collect all necessary data to construct a Message Object out of the database
     */
    protected Message constructMessageObject(Integer id, Integer author_id, String message, User caller) throws APIException {
    	Message out = new Message(id, author_id, null, message, getAttachments(id));
    	
    	try {
    		// Read the parent:
    		readParentId.setInt(1, out.id);
    		ResultSet rs = readParentId.executeQuery();
    		if(rs.next())
    			out.parent_id = rs.getInt("parent_id");
    		rs.close();
    		
    		// Count the likes:
    		countLikes.setInt(1, out.id);
    		rs = countLikes.executeQuery();
    		if(rs.next())
    			out.likes = rs.getInt("net_likes");
    		rs.close();
    		
    		// Check liked:
    		checkLiked.setInt(1, caller.id);
    		checkLiked.setInt(2, out.id);
    		rs = checkLiked.executeQuery();
    		if(rs.next())
    			out.liked = rs.getBoolean("liked");
    		
    		// Check comments:
    		countComments.setInt(1, out.id);
    		rs = countComments.executeQuery();
    		if(rs.next())
    			out.comments = rs.getInt("comment_count");
    		else
                out.comments = 0;

    	} catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }	
        
        return out;
    }
	
	/**
     * Creates a new Message
     * @param user The Message Object representing the message-data
     * @return The new Message object
     */
    public Message create(Message message, User caller) throws APIException {
    	// Check for a bad request:
    	if (message == null)
          throw new MalformedRequestException("Cannot create a null message");

      	// Run the query:
        try {
             insertOne.setString(1, message.message);
             insertOne.setInt(2, message.author_id);
             ResultSet rs = insertOne.executeQuery();
              if(!rs.next())
            	  throw new DatabaseFailureException("Unable to create message");
              
              // Insert a parent entry:
              if(message.parent_id != null) {
            	  insertParent.setInt(1, message.parent_id);
            	  message = readOne(rs.getInt("id"), caller);
            	  insertParent.setInt(2, message.id);
            	  insertParent.execute();
              }
              
              return readOne(rs.getInt("id"), caller);
          } catch(SQLException e) {
              e.printStackTrace();
              throw new DatabaseFailureException(e.getMessage());
          } catch(ResourceNotFoundException e) {
          	e.printStackTrace();
          	throw new DatabaseFailureException("Failed to create user");
          }
      }
	
	/**
     * Query the database for a list of all Messages without any children
     * @param caller A handle to the user who called the API method
     * @return All relevant Messages, as an ArrayList
     */
    public ArrayList<Message> readAll(User caller) throws APIException {
        ArrayList<Message> res = new ArrayList<Message>();
        try {
            ResultSet rs = selectAll.executeQuery();
            while (rs.next()) {
            	// Parse the message object from the database:
                res.add(constructMessageObject(rs.getInt("id"), rs.getInt("author_id"), rs.getString("message"), caller));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Query the database for a list of all Messages without any children
     * @param caller A handle to the user who called the API method
     * @return All relevant Messages, as an ArrayList
     */
    public ArrayList<Message> readAll(Message message, User caller) throws APIException {
        ArrayList<Message> res = new ArrayList<Message>();
        res.add(message);
        
        // Run Query:
        try {
        	selectThread.setInt(1, message.id);
            ResultSet rs = selectThread.executeQuery();
            while (rs.next()) {
            	// Parse the message object from the database:
                res.add(constructMessageObject(rs.getInt("id"), rs.getInt("author_id"), rs.getString("message"), caller));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
	
    /**
     * Get all data for a specific Message by their ID
     * @param id The id of the Message being requested
     * @param caller A handle to the user who called the API method
     * @return The Message Object, or null if the ID was invalid
     */
    public Message readOne(int id, User caller) throws APIException {
        try {
            selectOne.setInt(1, id);
            ResultSet rs = selectOne.executeQuery();
            if (!rs.next())
            	throw new ResourceNotFoundException("Message #" + id + " not found");
            
            // Parse the message object from the database:
            return constructMessageObject(rs.getInt("id"), rs.getInt("author_id"), rs.getString("message"), caller);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Reads the ID of a message from the messages table
     * @param id The id of the Message to lookup
     * @return the corresponding Message object (with a few fields missing)
     */
    public Message readOneSimple(int id) throws APIException {
    	try {
            selectOne.setInt(1, id);
            ResultSet rs = selectOne.executeQuery();
            if (!rs.next())
            	throw new ResourceNotFoundException("Message #" + id + " not found");
            
            // Parse the message object from the database:
            return new Message(rs.getInt("id"), rs.getInt("author_id"), null, rs.getString("message"), getAttachments(rs.getInt("id")));
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
    public Message updateOne(Message message, User caller) throws APIException {
        try {
            updateOne.setString(1, message.message);
            updateOne.setInt(2, message.id);
            updateOne.executeUpdate();
            return this.readOne(message.id, caller);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Delete a user by ID
     * @param id The id of the user to delete
     */
    void deleteOne(int id, Database db) throws APIException {
        Message m = readOneSimple(id);
        for (Integer i : m.attachments)
            db.attachments.deleteOne(i);

        try {
            deleteOne.setInt(1, id);
            int count = deleteOne.executeUpdate();
            if (count <= 0)
            	throw new DatabaseFailureException("Failed to delete message");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

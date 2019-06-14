package edu.lehigh.cse216.aztecs.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Defines a class to manage interactions with the "users" table within the database.
 */
public class AttachmentsTable {
    // Define a set of static SQL queries to run against the database:
    private static final String SELECT_ALL = "SELECT * FROM attachments";
	private static final String SELECT_ONE = "SELECT * FROM attachments WHERE id = ?";
	private static final String DELETE_ONE = "DELETE FROM attachments WHERE id = ?";
	private static final String INSERT_ONE = "INSERT INTO attachments (id, message, mime_type, file_id) VALUES (default, ?, ?, ?) RETURNING id";
	private static final String UPDATE_ONE = "UPDATE attachments SET message = ?, mime_type = ? WHERE id = ?";
	
	// Declare a set of prepared statements to allow for more responsive queries:
	private PreparedStatement selectAll;
	private PreparedStatement selectOne;
	private PreparedStatement deleteOne;
	private PreparedStatement insertOne;
	private PreparedStatement updateOne;
	
	/**
	 * Initialize the table
	 */
	public AttachmentsTable(Connection connection) throws SQLException {
		this.selectAll = connection.prepareStatement(AttachmentsTable.SELECT_ALL);
		this.selectOne = connection.prepareStatement(AttachmentsTable.SELECT_ONE);
		this.deleteOne = connection.prepareStatement(AttachmentsTable.DELETE_ONE);
		this.insertOne = connection.prepareStatement(AttachmentsTable.INSERT_ONE);
        this.updateOne = connection.prepareStatement(AttachmentsTable.UPDATE_ONE);
	}
	
	
	/**
     * Creates a new Attachment
     * @param attachment The Attachment Object representing the file-metadata
     * @return The new Attachment object
     */
    public Attachment create(Attachment attachment) throws APIException {
    	// Check for a bad request:
    	if (attachment == null)
          throw new MalformedRequestException("Cannot create a null message");

      	// Run the query:
        try {
             insertOne.setInt(1, attachment.message);
             insertOne.setString(2, attachment.mime_type);
             insertOne.setString(3, attachment.file_id);

             ResultSet rs = insertOne.executeQuery();
              if(!rs.next())
            	  throw new DatabaseFailureException("Unable to create message");
              
              return readOne(rs.getInt("id"));
          } catch(SQLException e) {
              e.printStackTrace();
              throw new DatabaseFailureException(e.getMessage());
          } catch(ResourceNotFoundException e) {
          	e.printStackTrace();
          	throw new DatabaseFailureException("Failed to create attachment");
          }
      }
	
	/**
     * Query the database for a list of all Attachments
     * @return All Attachments, as an ArrayList
     */
    public ArrayList<Attachment> readAll() throws APIException {
        ArrayList<Attachment> res = new ArrayList<Attachment>();
        try {
            ResultSet rs = selectAll.executeQuery();
            while (rs.next()) {
            	// Parse the attachment objects from the database:
                res.add(new Attachment(rs.getInt("id"), rs.getInt("message"), rs.getString("mime_type"), rs.getString("file_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
	
    /**
     * Get all data for a specific Attachment by it's ID
     * @param id The id of the Attachment being requested
     * @return The Message Object, or null if the ID was invalid
     */
    public Attachment readOne(int id) throws APIException {
        try {
            selectOne.setInt(1, id);
            ResultSet rs = selectOne.executeQuery();
            if (!rs.next())
            	throw new ResourceNotFoundException("Attachment #" + id + " not found");
            
            // Parse the message object from the database:
            return new Attachment(rs.getInt("id"), rs.getInt("message"), rs.getString("mime_type"), rs.getString("file_id"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Update the data for an Attachment in the database
     * @param attachment The Attachment Object to apply to the database
     * @return The new Attachment Object
     */
    public Attachment updateOne(Attachment attachment) throws APIException {
        try {
            updateOne.setInt(1, attachment.message);
            updateOne.setString(2, attachment.mime_type);
            updateOne.setInt(3, attachment.id);
            updateOne.executeUpdate();
            return this.readOne(attachment.id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseFailureException(e.getMessage());
        }
    }
    
    /**
     * Delete an attachment by ID
     * @param id The id of the attachment to delete
     */
    public Attachment deleteOne(int id) throws APIException {
        Attachment a = readOne(id);
        GoogleStorage.delete(a);

        try {
            deleteOne.setInt(1, id);
            int count = deleteOne.executeUpdate();
            if (count <= 0)
            	throw new DatabaseFailureException("Failed to delete attachment");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return a;
    }
}

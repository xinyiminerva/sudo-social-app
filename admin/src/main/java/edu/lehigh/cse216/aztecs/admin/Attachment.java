package edu.lehigh.cse216.aztecs.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Command line interface serves as a way of managing the message experience while
 * using the admin app. This includes mechanical things of managing user input and
 * walking through the logic decisions.
 * App.java serves as a container for the project
 * CommandLineInterface.java serves as a means of managing the user input
 * Database.java serves as a connectoin manager for interfacing with the database
 * User.java manages all User commands
 * Message.java manages all Message commands
 * Parent.java manages all Parent-Reply commands
 * Like.java manages all Like commands
 */
public class Attachment {

    /**
     * Attachment class contains methods for the Attachment related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public Attachment (Connection mConnection) throws SQLException {
        attachmentSelectAll = mConnection.prepareStatement("SELECT * FROM ATTACHMENTS");
        attachmentSelectOne = mConnection.prepareStatement("SELECT * FROM ATTACHMENTS WHERE id = ?");
        attachmentDeleteOne = mConnection.prepareStatement("DELETE FROM ATTACHMENTS WHERE id = ?");
        attachmentInsertOne = mConnection.prepareStatement("INSERT INTO ATTACHMENTS (id, mime_type, file_id, message) VALUES (default, ?, ?, ?)");
        attachmentUpdateOne = mConnection.prepareStatement("UPDATE ATTACHMENTS SET mime_type = ?, file_id = ? WHERE ID = ?");
        createAttachmentTable = mConnection.prepareStatement("CREATE TABLE ATTACHMENTS (id SERIAL PRIMARY KEY, mime_type VARCHAR(50), file_id VARCHAR(100), message SERIAL, FOREIGN KEY(message) REFERENCES messages(id) )");
        dropAttachmentTable = mConnection.prepareStatement("DROP TABLE ATTACHMENTS");
        this.mConnection = mConnection;
    }

    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement  attachmentSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement  attachmentSelectOne;

    /**
     * A prepared statement for deleting a row from the database
     */
    private static PreparedStatement  attachmentDeleteOne;

    /**
     * A prepared statement for inserting into the database
     */
    private static PreparedStatement  attachmentInsertOne;

    /**
     * A prepared statement for updating a single row in the database
     */
    private static PreparedStatement  attachmentUpdateOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createAttachmentTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropAttachmentTable;

    /**
     * We make AttachmentData a static class of Database because we don't really want
     * to encourage Attachmentto think of AttachmentData as being anything other than an
     * abstract representation of a row of the database.  AttachmentData and the
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class AttachmentData {
        /**
         * The ID of this row of the database
         */
        int id;
        /**
         * The parent message of this row of the database
         */
        int message;
        /**
         * The mime_type stored in this row
         */
        String mime_type;

		/**
		 * The ID of the file relative to Google's Filesystem
		 */
		String file_id;

        /**
         * Construct an AttachementData object by providing values for its fields
         */
        public AttachmentData(int id, int message, String mime_type, String file_id) {
            this.id = id;
            this.message = message;
            this.mime_type = mime_type;
        }


        public int getId () {
            return this.id;
        }

        public int getMessage () {
            return this.message;
        }

        public String getMine_type () {
            return this.mime_type;
        }
    }

    /**
     * Insert a row into the database
     *
     * @param mime_type the mime_type to be inserted
     * @param message the message
     *
     * @return The number of rows that were inserted
     */
    int insertAttachment(String mime_type, int message, String file_id) {
        int count = 0;
        try {
            attachmentInsertOne.setString(1, mime_type);
			attachmentInsertOne.setString(2, file_id);
            attachmentInsertOne.setInt(3, message);
            count += attachmentInsertOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting message");
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     *
     * @return All rows, as an ArrayList
     */
    ArrayList<AttachmentData> selectAllAttachments() {
        ArrayList<AttachmentData> res = new ArrayList<AttachmentData>();
        try {
            ResultSet rs = attachmentSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new AttachmentData(rs.getInt("id"), rs.getInt("message"), rs.getString("mime_type"), rs.getString("file_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error retrieving all messages");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     *
     * @param id The id of the row being requested
     *
     * @return The data for the requested row, or null if the ID was invalid
     */
    AttachmentData selectOneAttachment(int id) {
        AttachmentData res = null;
        try {
            attachmentSelectOne.setInt(1, id);
            ResultSet rs = attachmentSelectOne.executeQuery();
            if (rs.next()) {
                res = new AttachmentData(rs.getInt("id"), rs.getInt("message"), rs.getString("mime_type"), rs.getString("file_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error selecting message by ID");
            e.printStackTrace();
            return null;
        }
        return res;
    }

    /**
     * Delete a row by ID
     *
     * @param id The id of the row to delete
     *
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteAttachment(int id) {
        int res = -1;
        try {
            attachmentDeleteOne.setInt(1, id);
            res = attachmentDeleteOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting attachment, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the attachment for a row in the database
     *
     * @param id The id of the row to update
     * @param mime_type The mime_type
     *
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneAttachment(int id, String mime_type, String file_id) {
        int res = -1;
        try {
            attachmentUpdateOne.setString(1, mime_type);
            attachmentUpdateOne.setString(2, file_id);
			attachmentUpdateOne.setInt(3, id);
            res = attachmentUpdateOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating message, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createAttachmentTable() {
        try {
            createAttachmentTable.execute();
            System.err.println("Attachment table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating attachment table, it may already exist.");
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, this will tell the Attachment that
     */
    void dropAttachmentTable() {
        try {
            dropAttachmentTable.execute();
            System.err.println("Attachment table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping Attachment table, it may not exist.");
            e.printStackTrace();
        }
    }

}

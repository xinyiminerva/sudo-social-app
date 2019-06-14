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
public class Parent { 

    /**
     * Parent class contains methods for the parent-child related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public Parent (Connection mConnection) throws SQLException {
        parentSelectAll = mConnection.prepareStatement("SELECT * FROM parent");
        createReply = mConnection.prepareStatement("INSERT INTO parent (parent_id, message_id) VALUES (?, ?)");
        deleteReply = mConnection.prepareStatement("DELETE FROM parent WHERE parent_id = ? and message_id = ?");
        replySelectOne = mConnection.prepareStatement("SELECT * FROM parent WHERE parent_id = ? AND message_id = ?");
        replySelectByParentId = mConnection.prepareStatement("SELECT * FROM parent WHERE parent_id = ?");
        replySelectByMessageId = mConnection.prepareStatement("SELECT * FROM parent WHERE MESSAGE_ID = ?");
        getChildrenComments = mConnection.prepareStatement("SELECT count(*) FROM PARENT WHERE parent_id = ?");
        getChildrenCommentCount = mConnection.prepareStatement("SELECT COUNT(*) AS comment_count FROM parent WHERE parent_id = ?");
        createParentTable = mConnection.prepareStatement("CREATE TABLE PARENT (parent_id SERIAL, message_id SERIAL, PRIMARY KEY (parent_id, message_id), FOREIGN KEY (parent_id) REFERENCES messages(id) ON DELETE CASCADE, FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE ) ");
        dropParentTable = mConnection.prepareStatement("DROP TABLE PARENT");
        wipeParentTable = mConnection.prepareStatement("TRUNCATE TABLE PARENT CASCADE");
        // checkUserTableExists = mConnection.prepareStatement("SELECT to_regclass('PUBLIC.USERS')");
    }

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement parentSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement replySelectOne;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement replySelectByMessageId;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement replySelectByParentId;
    
    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createReply;
    
    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement deleteReply;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createParentTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropParentTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement wipeParentTable;


    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement getChildrenComments;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement getChildrenCommentCount;

    /**
     * A prepared statement for checking whether the parent Table exists
     */
    // private static PreparedStatement checkparentTableExists;

    /**
     * We make ParentData a static class of Database because we don't really want
     * to encourage users to think of ParentData as being anything other than an
     * abstract representation of a row of the database.  ParentData and the 
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class ParentData {
        /**
         * The subject stored in this row
         */
        int message_id;

        /**
         * The subject stored in this row
         */
        int parent_id;

        /**
         * Construct a ParentData object by providing values for its fields
         */
        public ParentData(int parent_id, int message_id) {
            this.parent_id = parent_id;
            this.message_id = message_id;
        }

        public int getMessageId () {
            return this.message_id;
        }

        public int getParentId () {
            return this.parent_id;
        }
    }

    /**
     * Get the child comment count for a parent id
     * 
     * @param parent_id the id of the parent comment
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    int getChildrenCommentCount (int parent_id) {
        int res = 0;
        try {
            getChildrenCommentCount.setInt(1, parent_id);
            ResultSet rs = getChildrenCommentCount.executeQuery();
            if (rs.next()) {
                res = rs.getInt("comment_count");
            }
        } catch (SQLException e) {
            System.err.println("Error finding total parent by given message id, please try again.");
            // e.printStackTrace();

        }
        return res;
    }

    
    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<ParentData> selectAllParent() {
        ArrayList<ParentData> res = new ArrayList<ParentData>();
        try {
            ResultSet rs = parentSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new ParentData(rs.getInt("parent_id"), rs.getInt("message_id")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.err.println("Error retrieving all parents, please try again.");
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param parent_id the id of the parent comment
     * @param message_id the id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    ParentData selectOneParent(int parent_id, int message_id) {
        ParentData res = null;
        try {
            replySelectOne.setInt(1, parent_id);
            replySelectOne.setInt(2, message_id);
            ResultSet rs = replySelectOne.executeQuery();
            if (rs.next()) {
                res = new ParentData(rs.getInt("parent_id"), rs.getInt("message_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting parent by given id, please try again.");
            // e.printStackTrace();

        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param parent_id The message id of the parent comment
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    ArrayList<ParentData> selectReplyByParentId(int parent_id) {
        ArrayList<ParentData> res = new ArrayList<ParentData>();
        try {
            replySelectByParentId.setInt(1, parent_id);
            ResultSet rs = replySelectByParentId.executeQuery();
            while (rs.next()) {
                res.add(new ParentData(rs.getInt("parent_id"), rs.getInt("message_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting parent by given parent id, please try again.");
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param message_id the id of the message
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    ArrayList<ParentData> selectReplyByMessageId(int message_id) {
        ArrayList<ParentData> res = new ArrayList<ParentData>();
        try {
            replySelectByMessageId.setInt(1, message_id);
            ResultSet rs = replySelectByMessageId.executeQuery();
            while (rs.next()) {
                res.add(new ParentData(rs.getInt("parent_id"), rs.getInt("message_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting parent by given message id, please try again.");
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Create a reply specific row, by ID
     * 
     * @param parent_id the id of the parent comment
     * @param message_id the id of the row being requested
     * 
     * @return The status of the update, or null if the ID was invalid
     */
    int createReply(int parent_id, int message_id) {
        int response = -1;
        try {
            createReply.setInt(1, parent_id);
            createReply.setInt(2, message_id);
            response = createReply.executeUpdate();
            if (response == -1) {
                System.err.println("Error creating parent-reply to parent message " + parent_id + " with reply " + message_id);
            } else if (response == 1) {
                System.err.println("Created parent-reply to parent message " + parent_id + " with reply " + message_id);
            }
        } catch (SQLException e) {
            System.err.println("Error creating parent-reply by given ID's, please try again.");
            // e.printStackTrace();

        }
        return response;
    }

    /**
     * Delete a specific row, by ID
     * 
     * @param parent_id the id of the parent comment
     * @param message_id the id of the row being requested
     * 
     * @return The status of the update, or null if the ID was invalid
     */
    int deleteReply(int parent_id, int message_id) {
        int response = -1;
        try {
            deleteReply.setInt(1, parent_id);
            deleteReply.setInt(2, message_id);
            response = deleteReply.executeUpdate();
            if (response == -1) {
                System.err.println("Error deleting parent-reply to parent message " + parent_id + " with reply " + message_id);
            } else if (response == 1) {
                System.err.println("Deleted parent-reply to parent message " + parent_id + " with reply " + message_id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting parent-reply by given ID's, please try again.");
            // e.printStackTrace();

        }
        return response;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createParentTable() {
        try {
            createParentTable.execute();
            System.err.println("Parent table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating parent table, it may already exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database. 
     * If it does not exist, this will tell the user that
     */
    void dropParentTable() {
        try {
            dropParentTable.execute();
            System.err.println("Parent table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping parent table, it may not exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.  
     * If it does not exist, it will tell the user that.
     */
    void wipeParentTable() {
        try {
            wipeParentTable.execute();
            System.err.println("Parent table successfully wiped.");
        } catch (SQLException e) {
            System.err.println("Error wiping parent table, it may already exist.");
            // e.printStackTrace();
        }
    }

}
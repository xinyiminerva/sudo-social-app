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
public class Message {

    /**
     * Message class contains methods for the Message related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public Message (Connection mConnection) throws SQLException {
        messageSelectAll = mConnection.prepareStatement("SELECT * FROM MESSAGES");
        messageSelectOne = mConnection.prepareStatement("SELECT * FROM MESSAGES WHERE id = ?");
        messageDeleteOne = mConnection.prepareStatement("DELETE FROM MESSAGES WHERE id = ?");
        messageInsertOne = mConnection.prepareStatement("INSERT INTO MESSAGES (message, author_id) VALUES (?, ?)");
        messageUpdateOne = mConnection.prepareStatement("UPDATE MESSAGES SET message = ? WHERE ID = ?");
        createMessageTable = mConnection.prepareStatement("CREATE TABLE MESSAGES (id SERIAL PRIMARY KEY, message varchar(280) NOT NULL, author_id SERIAL, FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE)");
        dropMessageTable = mConnection.prepareStatement("DROP TABLE MESSAGES");
        wipeMessageTable = mConnection.prepareStatement("TRUNCATE TABLE MESSAGES CASCADE");
        getAuthorUsername = mConnection.prepareStatement("SELECT USERNAME FROM USERS WHERE ID = ?");
        this.mConnection = mConnection;
        // checkMessageTableExists = mConnection.prepareStatement("SELECT to_regclass('PUBLIC.MESSAGES')");
    }

    private Connection mConnection;

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement messageSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement messageSelectOne;

    /**
     * A prepared statement for deleting a row from the database
     */
    private static PreparedStatement messageDeleteOne;

    /**
     * A prepared statement for inserting into the database
     */
    private static PreparedStatement messageInsertOne;

    /**
     * A prepared statement for updating a single row in the database
     */
    private static PreparedStatement messageUpdateOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createMessageTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropMessageTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement wipeMessageTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement getAuthorUsername;



    /**
     * A prepared statement for checking whether the Message Table exists
     */
    // private static PreparedStatement checkMessageTableExists;

    /**
     * We make MessageData a static class of Database because we don't really want
     * to encourage messages to think of MessageData as being anything other than an
     * abstract representation of a row of the database.  MessageData and the
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class MessageData {
        /**
         * The ID of this row of the database
         */
        int id;
        /**
         * The author_id of this row of the database
         */
        int author_id;
        /**
         * The message stored in this row
         */
        String message;
        /**
         * The authorUsername stored in this row
         */
        String authorUsername;


        /**
         * Construct a MessageData object by providing values for its fields
         */
        public MessageData(int id, int author_id, String message, Connection mConnection) {
            this.id = id;
            this.author_id = author_id;
            this.message = message;
            this.authorUsername = this.determineAuthor(mConnection);
        }


        public int getId () {
            return this.id;
        }

        public String getMessage () {
            return this.message;
        }

        public String getAuthorUsername () {
            return this.authorUsername;
        }

        /** Return the author for a given author_id */
        public String determineAuthor (Connection mConnection) {
            ResultSet rs = null;
            try {
                getAuthorUsername.setInt(1, this.author_id);
                rs = getAuthorUsername.executeQuery();
                String ret = null;
                while (rs.next()) {
                    ret = rs.getString("username");
                }
                return ret;
            } catch (SQLException e) {
                System.out.println("Error finding author username for message " + this.id);
                // e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Insert a row into the database
     *
     * @param message the message to be inserted
     * @param author_id the id of the author
     *
     * @return The number of rows that were inserted
     */
    int insertMessage(String message, int author_id) {
        int count = 0;
        try {
            messageInsertOne.setString(1, message);
            messageInsertOne.setInt(2, author_id);
            count += messageInsertOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting message");
            // e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     *
     * @return All rows, as an ArrayList
     */
    ArrayList<MessageData> selectAllMessages() {
        ArrayList<MessageData> res = new ArrayList<MessageData>();
        try {
            ResultSet rs = messageSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new MessageData(rs.getInt("id"), rs.getInt("author_id"), rs.getString("message"), this.mConnection));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error retrieving all messages");
            // e.printStackTrace();
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
    MessageData selectOneMessage(int id) {
        MessageData res = null;
        try {
            messageSelectOne.setInt(1, id);
            ResultSet rs = messageSelectOne.executeQuery();
            if (rs.next()) {
                res = new MessageData(rs.getInt("id"), rs.getInt("author_id"), rs.getString("message"), this.mConnection);
            }
        } catch (SQLException e) {
            System.out.println("Error selecting message by ID");
            // e.printStackTrace();
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
    int deleteMessage(int id) {
        int res = -1;
        try {
            messageDeleteOne.setInt(1, id);
            res = messageDeleteOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting message, please try again.");
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     *
     * @param id The id of the row to update
     * @param message The new message
     *
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneMessage(int id, String message) {
        int res = -1;
        try {
            messageUpdateOne.setString(1, message);
            messageUpdateOne.setInt(2, id);
            res = messageUpdateOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating message, please try again.");
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createMessageTable() {
        try {
            createMessageTable.execute();
            System.err.println("Message table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating message table, it may already exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, this will tell the message that
     */
    void dropMessageTable() {
        try {
            dropMessageTable.execute();
            System.err.println("Message table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping message table, it may not exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, it will tell the message that.
     */
    void wipeMessageTable() {
        try {
            wipeMessageTable.execute();
            System.err.println("Message table successfully wiped.");
        } catch (SQLException e) {
            System.err.println("Error wiping message table, it may not exist.");
            // e.printStackTrace();
        }
    }

}

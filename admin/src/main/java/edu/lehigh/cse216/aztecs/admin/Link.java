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
 * Attachment.java manages all Attachment commands
 * Link.java manages all linkcommands
 */
public class Link {

    /**
     * Link class contains methods for the User related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public Link (Connection mConnection) throws SQLException {
        linkSelectAll = mConnection.prepareStatement("SELECT * FROM LINKS");
        linkSelectOneById = mConnection.prepareStatement("SELECT * FROM LINKSq WHERE message = ?");
        linkDeleteOne = mConnection.prepareStatement("DELETE FROM LINKS WHERE message = ?");
        linkInsertOne = mConnection.prepareStatement("INSERT INTO LINKS (link,message) VALUES (?, ?)");
        linkUpdateOne = mConnection.prepareStatement("UPDATE LINKS SET link = ?,  WHERE message = ?");
        createLinkTable = mConnection.prepareStatement("CREATE TABLE IF NOT EXISTS LINKS (message SERIAL, link VARCHAR(50), FOREIGN KEY (message) REFERENCES messages(id),PRIMARY KEY(link,message))");
        dropLinkTable = mConnection.prepareStatement("DROP TABLE LINKS");
        wipeLinkTable = mConnection.prepareStatement("TRUNCATE TABLE LINKS CASCADE");
        // checkUserTableExists = mConnection.prepareStatement("SELECT to_regclass('PUBLIC.USERS')");
    }

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement linkSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement linkSelectOneById;

    /**
     * A prepared statement for deleting a row from the database
     */
    private static PreparedStatement linkDeleteOne;

    /**
     * A prepared statement for inserting into the database
     */
    private static PreparedStatement linkInsertOne;

    /**
     * A prepared statement for updating a single row in the database
     */
    private static PreparedStatement linkUpdateOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createLinkTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropLinkTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement wipeLinkTable;

    /**
     * A prepared statement for checking whether the User Table exists
     */
    // private static PreparedStatement checkUserTableExists;

    /**
     * We make UserData a static class of Database because we don't really want
     * to encourage users to think of UserData as being anything other than an
     * abstract representation of a row of the database.  UserData and the
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class LinkData {
        /**
         * The ID of this row of the database
         */
        int message;
        /**
         * The subject stored in this row
         */
        String link;

        /**
         * Construct a UserData object by providing values for its fields
         */
        public LinkData(int message, String link) {
            this.message = message;
            this.link = link;
        }

        public int getMessage () {
            return this.message;
        }

        public String getLink () {
            return this.link;
        }
    }

    /**
     * Insert a row into the database
     *@param message the messsage id  to be inserted
     * @param link the link to be inserted
     * @return The number of rows that were inserted
     */
    int insertLink(String link,int message) {
        int count = 0;
        try {
            linkInsertOne.setInt(2, message);
            linkInsertOne.setString(1, link);
	    count += linkInsertOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting link, please try again.");
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * @return All rows, as an ArrayList
     */
    ArrayList<LinkData> selectAllLinks() {
        ArrayList<LinkData> res = new ArrayList<LinkData>();
        try {
            ResultSet rs = linkSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new LinkData(rs.getInt("message"), rs.getString("link")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error retrieving all links, please try again.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * @param message The id of the row being requested
     * @return The data for the requested row, or null if the ID was invalid
     */
    LinkData selectOneLinkById(int message) {
        LinkData res = null;
        try {
            linkSelectOneById.setInt(1, message);
            ResultSet rs = linkSelectOneById.executeQuery();
            if (rs.next()) {
                res = new LinkData(rs.getInt("message"), rs.getString("link"));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting user by given id, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by message ID
     * @param id The id of the row to delete
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteLink(int message) {
        int res = -1;
        try {
            linkDeleteOne.setInt(1, message);
            res = linkDeleteOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting link, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * @param message The id of the row to update
     * @param link The new link
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneLink(int message, String link) {
        int res = -1;
        try {
            linkUpdateOne.setString(1, link);
            res = linkUpdateOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating link, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createLinkTable() {
        try {
            createLinkTable.execute();
            System.err.println("Link table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating link table, it may already exist.");
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, this will tell the user that
     */
    void dropLinkTable() {
        try {
            dropLinkTable.execute();
            System.err.println("Link table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping link table, it may not exist.");
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, it will tell the user that.
     */
    void wipeLinkTable() {
        try {
            wipeLinkTable.execute();
            System.err.println("User table successfully wiped.");
        } catch (SQLException e) {
            System.err.println("Error wiping link table, it may already exist.");
            e.printStackTrace();
        }
    }

}

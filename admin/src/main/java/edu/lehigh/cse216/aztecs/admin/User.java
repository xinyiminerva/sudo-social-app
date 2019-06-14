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
public class User {

    /**
     * User class contains methods for the User related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public User (Connection mConnection) throws SQLException {
        userSelectAll = mConnection.prepareStatement("SELECT * FROM USERS");
        userSelectOneById = mConnection.prepareStatement("SELECT * FROM USERS WHERE id = ?");
        userSelectOneByUsername = mConnection.prepareStatement("SELECT * FROM USERS WHERE username = ?");
        userDeleteOne = mConnection.prepareStatement("DELETE FROM USERS WHERE id = ?");
        userInsertOne = mConnection.prepareStatement("INSERT INTO USERS (username, bio) VALUES (?, ?)");
        userUpdateOne = mConnection.prepareStatement("UPDATE USERS SET username = ?, bio = ? WHERE ID = ?");
        createUserTable = mConnection.prepareStatement("CREATE TABLE USERS (id SERIAL PRIMARY KEY, username varchar(50) UNIQUE, bio varchar(100))");
        dropUserTable = mConnection.prepareStatement("DROP TABLE USERS");
        wipeUserTable = mConnection.prepareStatement("TRUNCATE TABLE USERS CASCADE");
        // checkUserTableExists = mConnection.prepareStatement("SELECT to_regclass('PUBLIC.USERS')");
    }

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement userSelectAll;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement userSelectOneById;

    /**
     * A prepared statement for getting one row of user via username
     */
    private static PreparedStatement userSelectOneByUsername;

    /**
     * A prepared statement for deleting a row from the database
     */
    private static PreparedStatement userDeleteOne;

    /**
     * A prepared statement for inserting into the database
     */
    private static PreparedStatement userInsertOne;

    /**
     * A prepared statement for updating a single row in the database
     */
    private static PreparedStatement userUpdateOne;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createUserTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropUserTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement wipeUserTable;

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
    public static class UserData {
        /**
         * The ID of this row of the database
         */
        int id;
        /**
         * The subject stored in this row
         */
        String username;

	/**
	* The user bio
	*/
	String bio;

        /**
         * Construct a UserData object by providing values for its fields
         */
        public UserData(int id, String username, String bio) {
            this.id = id;
            this.username = username;
	    this.bio = bio;
        }

        public int getId () {
            return this.id;
        }

        public String getUsername () {
            return this.username;
        }

	public String getBio() {
	    return this.bio;
	}
    }

    /**
     * Insert a row into the database
     *
     * @param username the username to be inserted
     * @return The number of rows that were inserted
     */
    int insertUser(String username) {
        int count = 0;
        try {
            userInsertOne.setString(1, username);
            userInsertOne.setString(2, "Your Bio Here");
	    count += userInsertOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting user, please try again.");
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * @return All rows, as an ArrayList
     */
    ArrayList<UserData> selectAllUsers() {
        ArrayList<UserData> res = new ArrayList<UserData>();
        try {
            ResultSet rs = userSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("bio")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            System.err.println("Error retrieving all users, please try again.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * @param id The id of the row being requested
     * @return The data for the requested row, or null if the ID was invalid
     */
    UserData selectOneUserById(int id) {
        UserData res = null;
        try {
            userSelectOneById.setInt(1, id);
            ResultSet rs = userSelectOneById.executeQuery();
            if (rs.next()) {
                res = new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("bio"));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting user by given id, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * @param username The username of the user being requested
     * @return The data for the requested row, or null if the ID was invalid
     */
    UserData selectOneUserByUsername(String username) {
        UserData res = null;
        try {
            userSelectOneByUsername.setString(1, username);
            ResultSet rs = userSelectOneByUsername.executeQuery();
            if (rs.next()) {
                res = new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("bio"));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting user by given username, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * @param id The id of the row to delete
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteUser(int id) {
        int res = -1;
        try {
            userDeleteOne.setInt(1, id);
            res = userDeleteOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * @param id The id of the row to update
     * @param username The new username
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOneUser(int id, String username, String bio) {
        int res = -1;
        try {
            userUpdateOne.setString(1, username);
	    userUpdateOne.setString(2, bio);
            userUpdateOne.setInt(3, id);
            res = userUpdateOne.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user, please try again.");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createUserTable() {
        try {
            createUserTable.execute();
            System.err.println("User table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating user table, it may already exist.");
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, this will tell the user that
     */
    void dropUserTable() {
        try {
            dropUserTable.execute();
            System.err.println("User table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping user table, it may not exist.");
            e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.
     * If it does not exist, it will tell the user that.
     */
    void wipeUserTable() {
        try {
            wipeUserTable.execute();
            System.err.println("User table successfully wiped.");
        } catch (SQLException e) {
            System.err.println("Error wiping user table, it may already exist.");
            e.printStackTrace();
        }
    }

}

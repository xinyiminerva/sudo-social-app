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
public class Like { 

    /**
     * Like class contains methods for the Like related queries on the database, as
     * well as the handling of those data items.
     * @param mConnection
     * @throws SQLException
     */
    public Like (Connection mConnection) throws SQLException {
        likesSelectAll = mConnection.prepareStatement("SELECT * FROM LIKES");
        likeMessage = mConnection.prepareStatement("INSERT INTO likes (liked, message_id, liker) VALUES (?, ?, ?) ON CONFLICT (message_id, liker) DO UPDATE SET liked = ?");
        noReactionMessage = mConnection.prepareStatement("DELETE FROM likes WHERE MESSAGE_ID = ? AND LIKER = ?");
        likesSelectOne = mConnection.prepareStatement("SELECT * FROM likes WHERE LIKER = ? AND MESSAGE_ID = ?");
        likesSelectByLiker = mConnection.prepareStatement("SELECT * FROM likes WHERE LIKER = ?");
        likesSelectByMessageId = mConnection.prepareStatement("SELECT * FROM likes WHERE MESSAGE_ID = ?");
        getLikes = mConnection.prepareStatement("SELECT SUM(CASE WHEN liked = true THEN 1 ELSE -1 END) AS net_likes FROM likes WHERE message_id = ?");
        createLikeTable = mConnection.prepareStatement("CREATE TABLE likes (liked boolean, liker SERIAL, message_id SERIAL, PRIMARY KEY (liker, message_id), FOREIGN KEY (liker) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE ) ");
        dropLikeTable = mConnection.prepareStatement("DROP TABLE LIKES");
        wipeLikeTable = mConnection.prepareStatement("TRUNCATE TABLE LIKES CASCADE");
        // checkUserTableExists = mConnection.prepareStatement("SELECT to_regclass('PUBLIC.USERS')");
    }

    /**
     * A prepared statement for getting all data in the database
     */
    private static PreparedStatement likesSelectAll;

    /**
     * A callable statement for getting the likes right
     */
    private static PreparedStatement likeMessage;

    /**
     * A callable statement for getting the likes right
     */
    private static PreparedStatement noReactionMessage;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement likesSelectOne;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement likesSelectByMessageId;

    /**
     * A prepared statement for getting one row from the database
     */
    private static PreparedStatement likesSelectByLiker;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement getLikes;

    /**
     * A prepared statement for creating the table in our database
     */
    private static PreparedStatement createLikeTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement dropLikeTable;

    /**
     * A prepared statement for dropping the table in our database
     */
    private static PreparedStatement wipeLikeTable;

    /**
     * A prepared statement for checking whether the Like Table exists
     */
    // private static PreparedStatement checkLikeTableExists;

    /**
     * We make LikesData a static class of Database because we don't really want
     * to encourage users to think of LikesData as being anything other than an
     * abstract representation of a row of the database.  LikesData and the 
     * Database are tightly coupled: if one changes, the other should too.
     */
    public static class LikesData {
        /**
         * The ID of this row of the database
         */
        Boolean likeFlag = null;
        /**
         * The subject stored in this row
         */
        int message_id;

        /**
         * The subject stored in this row
         */
        int liker;

        /**
         * Construct a LikesData object by providing values for its fields
         */
        public LikesData(Boolean flag, int message_id, int liker) {
            this.likeFlag = flag;
            this.message_id = message_id;
            this.liker = liker;
        }

        public Boolean getLikeStatus () {
            return this.likeFlag;
        }

        public int getMessageId () {
            return this.message_id;
        }

        public int getLikerId () {
            return this.liker;
        }
    }

    /**
     * Insert a row into the database
     * 
     * @param message_id the message id
     * 
     * @return The number of likes on the message
     */   
    int getMessageLikes (int message_id) {
        int res = 0;
        try {
            getLikes.setInt(1, message_id);
            ResultSet rs = getLikes.executeQuery();
            if (rs.next()) {
                res = rs.getInt("net_likes");
            }
        } catch (SQLException e) {
            System.err.println("Error finding total likes by given message id, please try again.");
            // e.printStackTrace();

        }
        return res;
    }

    /**
     * Insert a row into the database
     * 
     * @param status the like/dislike/neutral to be inserted
     * @param message_id the message id
     * @param liker the id of user
     * 
     * @return The number of rows that were inserted
     */
    int messageLike (Boolean status, int message_id, int liker) {
        int response = -1;
        try {
            if (status != null) {
                likeMessage.setBoolean(1, status);
                likeMessage.setInt(2, message_id);
                likeMessage.setInt(3, liker);
                likeMessage.setBoolean(4, status);
                response = likeMessage.executeUpdate();
                if (response == -1) {
                    System.err.println("Error updating reaction to message " + message_id + " by user " + liker);
                } else if (response == 1 && !status) {
                    System.err.println("Message " + message_id + " was Disliked by User " + liker);
                } else if (response == 1 && status) {
                    System.err.println("Message " + message_id + " was Liked by User " + liker);
                }
            } else {
                noReactionMessage.setInt(1, message_id);
                noReactionMessage.setInt(2, liker);
                response = noReactionMessage.executeUpdate();
                if (response == -1) {
                    System.err.println("Error updating reaction to message " + message_id + " by user " + liker);
                } else if (response == 1) {
                    System.err.println("Message " + message_id + " was No Reactioned by User " + liker);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error modifying like, for user with id " + liker + " on message " + message_id + " please try again.");
            // e.printStackTrace();
        }
        return response;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<LikesData> selectAllLikes() {
        ArrayList<LikesData> res = new ArrayList<LikesData>();
        try {
            ResultSet rs = likesSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new LikesData(rs.getBoolean("liked"), rs.getInt("message_id"), rs.getInt("liker")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.err.println("Error retrieving all likess, please try again.");
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param status the new like status
     * @param message_id the id of the row being requested
     * @param liker the id of the liker user
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    LikesData selectOneLikes(Boolean status, int message_id, int liker) {
        LikesData res = null;
        try {
            likesSelectOne.setInt(1, message_id);
            likesSelectOne.setInt(2, liker);
            ResultSet rs = likesSelectOne.executeQuery();
            if (rs.next()) {
                res = new LikesData(rs.getBoolean("liked"), rs.getInt("message_id"), rs.getInt("liker"));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting likes by given id, please try again.");
            // e.printStackTrace();

        }
        return res;
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param liker The user id of the person liking
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    ArrayList<LikesData> selectLikesByLiker(int liker) {
        ArrayList<LikesData> res = new ArrayList<LikesData>();
        try {
            likesSelectByLiker.setInt(1, liker);
            ResultSet rs = likesSelectByLiker.executeQuery();
            if (rs.next()) {
                res.add(new LikesData(rs.getBoolean("liked"), rs.getInt("message_id"), rs.getInt("liker")));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting likes by given liker id, please try again.");
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
    ArrayList<LikesData> selectLikesByMessageId(int message_id) {
        ArrayList<LikesData> res = new ArrayList<LikesData>();
        try {
            likesSelectByMessageId.setInt(1, message_id);
            ResultSet rs = likesSelectByMessageId.executeQuery();
            if (rs.next()) {
                res.add(new LikesData(rs.getBoolean("liked"), rs.getInt("message_id"), rs.getInt("liker")));
            }
        } catch (SQLException e) {
            System.err.println("Error selecting likes by given liker id, please try again.");
            // e.printStackTrace();
        }
        return res;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     */
    void createLikesTable() {
        try {
            createLikeTable.execute();
            System.err.println("Likes table successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating likes table, it may already exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database. 
     * If it does not exist, this will tell the user that
     */
    void dropLikesTable() {
        try {
            dropLikeTable.execute();
            System.err.println("Likes table successfully dropped.");
        } catch (SQLException e) {
            System.err.println("Error dropping likes table, it may not exist.");
            // e.printStackTrace();
        }
    }

    /**
     * Remove tblData from the database.  
     * If it does not exist, it will tell the user that.
     */
    void wipeLikesTable() {
        try {
            wipeLikeTable.execute();
            System.err.println("Likes table successfully wiped.");
        } catch (SQLException e) {
            System.err.println("Error wiping likes table, it may already exist.");
            // e.printStackTrace();
        }
    }

}
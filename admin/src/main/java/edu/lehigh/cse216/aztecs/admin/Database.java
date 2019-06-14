package edu.lehigh.cse216.aztecs.admin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    private User user;
    private Message message;
    private Like likes;
    private Parent parent;
    private Attachment attachment;
    private Link link;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {

    }

    User getUser () {
        return this.user;
    }

    Message getMessage () {
        return this.message;
    }

    Like getLikes () {
        return this.likes;
    }

    Parent getParent () {
        return this.parent;
    }

    Attachment getAttachment() {
	return this.attachment;
    }
    Link getLink() {
        return this.link;
        }

    void wipeTables () {
        this.getLikes().wipeLikesTable();
        this.getParent().wipeParentTable();
        this.getMessage().wipeMessageTable();
        this.getUser().wipeUserTable();
        this.getLink().wipeLinkTable();
    }

    void dropTables () {
        this.getLikes().dropLikesTable();
        this.getParent().dropParentTable();
        this.getMessage().dropMessageTable();
        this.getUser().dropUserTable();
        this.getAttachment().dropAttachmentTable();
        this.getLink().dropLinkTable();
    }

    void createTables () {
        this.getUser().createUserTable();
        this.getMessage().createMessageTable();
        this.getLikes().createLikesTable();
        this.getParent().createParentTable();
        this.getAttachment().createAttachmentTable();
        this.getLink().createLinkTable();
    }

    /**
     * Get a fully-configured connection to the database
     *
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     *
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String url) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No class found for postgres driver");
            return null;
        } catch (URISyntaxException e) {
            System.err.println("URI Syntax Error");
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these
        // fail, the whole getDatabase() call should fail
        try {
            // NB: we can easily get ourselves in trouble here by typing the
            //     SQL incorrectly.  We really should have things like "tblData"
            //     as constants, and then build the strings for the statements
            //     from those constants.

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception

            db.user = new User(db.mConnection);
            db.message = new Message(db.mConnection);
            db.likes = new Like(db.mConnection);
            db.parent = new Parent(db.mConnection);
           db.attachment = new Attachment(db.mConnection);
           db.link = new Link(db.mConnection);

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     *
     * NB: The connection will always be null after this call, even if an
     *     error occurred during the closing operation.
     *
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }


}

package edu.lehigh.cse216.aztecs.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.net.URI;
import java.net.URISyntaxException;

public class Database {
	// TODO: Define all constant query strings:

    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    // Expose the table-management objects publicly:
    public UsersTable users;
    public MessagesTable messages;
    public LikesTable likes;
    public AttachmentsTable attachments;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
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
        	String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
            db.users = new UsersTable(conn);
            db.messages = new MessagesTable(conn);
            db.likes = new LikesTable(conn);
            db.attachments = new AttachmentsTable(conn);
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Unable to find postgresql driver");
            return null;
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
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

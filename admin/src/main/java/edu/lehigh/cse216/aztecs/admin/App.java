package edu.lehigh.cse216.aztecs.admin;

import java.util.Map;

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
public class App {

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {

        // Get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String url = env.get("DATABASE_URL");

        // Get a fully-configured connection to the database, or exit immediately
        Database db = Database.getDatabase(url);
        if (db == null) {
            System.out.println("No database could be found. Please check to make sure your environment settings are correct.");
            return;
        }

        // Begin taking input from the user and interacting with the database
        CommandLineInterface.start(System.in, db);
        
        // Disconnect from the database when the program exits to prevent zombie connections
        db.disconnect();
    }
}

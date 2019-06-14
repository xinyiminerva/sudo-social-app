package edu.lehigh.cse216.aztecs.backend;

import java.util.Map;
import java.util.Random;
import com.auth0.jwt.interfaces.Payload;
import com.fasterxml.jackson.core.JsonFactory;
import org.eclipse.jetty.server.HttpTransport;

import spark.Spark;


    /**
     * For now, our application creates an HTTP server that can only get and add data.
     */
    public class App {
    
        /**
         * Get an integer environment varible if it exists, and otherwise return the
         * default value.
         * @envar      The name of the environment variable to get.
         * @defaultVal The integer value to use as the default if envar isn't found
         * @returns The best answer we could come up with for a value for envar
         */
        static int getIntFromEnv(String envar, int defaultVal) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (processBuilder.environment().get(envar) != null) {
                return Integer.parseInt(processBuilder.environment().get(envar));
            }
            return defaultVal;
        }

        public static void main(String[] args) {
            try {
                Authorizer.init();
            } catch(Exception e) {
                System.err.println("Unable to initialize the Authorization system");
                e.printStackTrace();
                System.err.println(e.toString());
                System.exit(1);
            }

            // Read the Postgres configuration from the environment:
            Map<String, String> env = System.getenv();
            String url = env.get("DATABASE_URL");

            // Create the database handle:
            Database db = Database.getDatabase(url);
            if (db == null)
                return;

            // Set the port for Spark:
            Spark.port(getIntFromEnv("PORT", 4567));
            
            // Define the static file location where the front-end will reside:
            String static_location_override = System.getenv("STATIC_LOCATION");
            if (static_location_override == null) {
                Spark.staticFileLocation("/web");
            } else {
                Spark.staticFiles.externalLocation(static_location_override);
            }

            // Serve the front-end page at "/" and "/index.html":
            Spark.get("/", (req, res) -> {
                res.redirect("/index.html");
                return "";
            });

            // Serve the Users API on the "/api/users" sub-directory:
            Spark.path("/api", () -> {
                UsersAPI.createAPI(db);
                MessagesAPI.createAPI(db);
                LikesAPI.createAPI(db);
                AttachmentsAPI.createAPI(db);
                HelpAPI.createAPI(db);
                
                // Set a specified handle for Authentication Failures:
                Spark.exception(AuthorizationFailedException.class, (exception, req, res) -> {
                    System.err.println(exception.getMessage());
                    res.status(exception.getStatus());
                    res.header("WWW-Authenticate", "Bearer");
                    res.body(exception.getMessage());
                }); 
                
                // Set a generic handle for all HTTP Errors:
                Spark.exception(APIException.class, (exception, req, res) -> {
                    System.err.println(exception.getMessage());
                    exception.printStackTrace();
                    res.status(exception.getStatus());
                    res.body(exception.getMessage());
                });
            });
           
        }
    }

        


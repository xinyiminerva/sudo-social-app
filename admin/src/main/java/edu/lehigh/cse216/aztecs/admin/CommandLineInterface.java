package edu.lehigh.cse216.aztecs.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.lang.StringBuilder;

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
public class CommandLineInterface {
    /**
     * Method to verify if username is an e-mail
     * @param email The username field
     * @return True if the username is an e-mail and False if the username is not an e-mail
     */
    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                            "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /**
     * Print the menu for our program
     */
    static void help (String menu) {
        if (menu.equals("Main")) {
            System.out.println("Main Menu");
            System.out.println("  [U] User Table");
            System.out.println("  [M] Message Table");
            System.out.println("  [L] Likes Table");
            System.out.println("  [A] Attachment Table");
            System.out.println("  [N] Link Table");
            System.out.println("  [P] Comment Parent Table");
            System.out.println("  [~] Reset ALL Tables");
            System.out.println("  [+] Create ALL Tables");
            System.out.println("  [-] Delete ALL Tables");
            System.out.println("  [q] Quit Program");
            System.out.println("  [?] Help (this message)");
        } else if (menu.equals("User")) {
            System.out.println("User Menu");
            System.out.println("  [*] Show Entire User Table");
            System.out.println("  [s] Show User");
            System.out.println("  [i] Insert User");
            System.out.println("  [m] Modify User");
            System.out.println("  [d] Delete User");
            System.out.println("  [q] Quit User Submenu");
            System.out.println("  [?] Help (this message)");
        } else if (menu.equals("Message")) {
            System.out.println("Message Menu");
            System.out.println("  [*] Show Entire Message Table");
            System.out.println("  [s] Show Message");
            System.out.println("  [i] Insert Message");
            System.out.println("  [m] Modify Message");
            System.out.println("  [d] Delete Message");
            System.out.println("  [q] Quit Message Submenu");
            System.out.println("  [?] Help (this message)");
        } else if (menu.equals("Likes")) {
            System.out.println("Likes Menu");
            System.out.println("  [*] Show Entire Likes Table");
            System.out.println("  [~] Have User No Reaction on Message");
            System.out.println("  [+] Have User Like Message");
            System.out.println("  [-] Have User Dislike Message");
            System.out.println("  [q] Quit Like Submenu");
            System.out.println("  [?] Help (this message)");
        } else if (menu.equals("Parent")) {
            System.out.println("Parent Menu");
            System.out.println("  [*] Show Entire Parent Table");
            System.out.println("  [c] Show Child Replies");
            System.out.println("  [+] Add Reply Message");
            System.out.println("  [-] Delete Reply Message");
            System.out.println("  [q] Quit Parent Submenu");
            System.out.println("  [?] Help (this message)");
        }else if (menu.equals("Attachment")) {
            System.out.println("Attachment Menu");
            System.out.println("  [*] Show Entire Attachment Table");
            System.out.println("  [s] Show Attachment");
            System.out.println("  [i] Insert Attachment");
            System.out.println("  [m] Modify Attachment");
            System.out.println("  [d] Delete Attachment");
            System.out.println("  [q] Quit Attachment Submenu");
            System.out.println("  [?] Help (this attachment)");
        }else if (menu.equals("Link")) {
            System.out.println("Link Menu");
            System.out.println("  [*] Show Entire Link Table");
            System.out.println("  [i] Insert Link");
            System.out.println("  [m] Modify Link");
            System.out.println("  [d] Delete Link");
            System.out.println("  [q] Quit Link Submenu");
            System.out.println("  [?] Help (this link)");
        }

    }

    /**
     * Print this upon entry into The Buzz
     */
    static void intro () {
        System.out.println("*****Welcome to The Buzz Administrator App*****");
        System.out.println("\nHere, you will be able to modify The Buzz database from");
        System.out.println("a command-line interface. Type '?' to get a help message.");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     *
     * @param in A BufferedReader, for reading from the keyboard
     *
     * @return The character corresponding to the chosen menu option
     */
    static char prompt (BufferedReader in, String actions) {
        // We repeat until a valid single-character option is selected
        while (true) {
            System.out.print("\tOptions [" + actions + "]\t::> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                System.out.println("\nUnknown input error, please try again");
                // e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * @return The string that the user provided.  May be "".
     */
    static String getString (BufferedReader in, String message) {
        String s;
        try {
            System.out.print("\t\t" + message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            System.out.println("Error with field entry, please try again.");
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt (BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print("\t\t" + message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            System.out.println("   Unknown input error, please try again.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("   Please enter an Integer.");
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Begins the process of the CLI
     * @param userInput Passed in input parameter.
     * @param db Passed in database connection
     */
    public static void start (InputStream userInput, Database db) {
        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(userInput));
        // Begin the CLI at the root level
        mainMenu(in, db);
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * @param in Passed in input parameter
     * @param db Passed in database connection
     */
    public static void mainMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        intro();
        help("Main");

        // The valid actions:
        String actions = "ANUMLP~+-q?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Main");
            } else if (action == 'q') {
                cli = false;
            } else if (action == 'U') {
                userMenu(in, db);
            }else if (action == 'A') {
                attachmentMenu(in, db);
            }else if (action == 'M') {
                messageMenu(in, db);
            } else if (action == 'N') {
                linkMenu(in, db);
            } else if (action == 'L') {
                likesMenu(in, db);}
            else if (action == 'P') {
                parentMenu(in, db);
            } else if (action == '~') {
                db.wipeTables();
            } else if (action == '+') {
                db.createTables();
            } else if (action == '-') {
                db.dropTables();
            }
        }
    }

    static String password123;

    public static String getPassword(){
        return password123;
    }

    /**
     * The interface for interacting with the User table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void userMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("User");

        // The valid actions:
        String actions = "*simdq?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("User");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<User.UserData> res = db.getUser().selectAllUsers();
                if (res == null) {
                    System.out.println("  User Database Does Not Exist!");
                } else {
                    System.out.println("  Current User Database Contents");
                    System.out.println("  ------------------------------");
                    for (User.UserData rd : res) {
                        System.out.println("[ID]: [" + rd.id + "]" + " [USERNAME]:" + rd.username);
                    }
                }
            } else if (action == 's') {
                int id = getInt(in, "Enter the user ID");
                if (id == -1) {
                    continue;
                }
                User.UserData res = db.getUser().selectOneUserById(id);
                if (res == null) {
                    System.out.println("No user exists for the given id.");
                } else {
                    System.out.println(" [ID]: [" + res.id + "]" + " [USERNAME]:" + res.username);
                }
            } else if (action == 'i') {
                String username = getString(in, "Enter the username");
                boolean isEmail = isValid(username); //Test if username is an e-mail

                if (username.equals("")) {
                    System.out.println("Cannot have empty username");
                    continue; //Can't have an empty username
                }
                if(isEmail == false) {
                    System.out.println("Username is not an e-mail");
                    continue;
                }

                try {
                    int res = db.getUser().insertUser(username);
                    if (res != 1) {
                        System.out.println("Error inserting new user into the database.");
                        continue;
                    }

                } catch (Exception e) {
                    System.out.println("Error: unable to insert new user");
                    e.printStackTrace();
                }

                int id = db.getUser().selectOneUserByUsername(username).getId();
                System.out.println("User " + username + " added with id " + id);

            }  else if (action == 'm') {
                int id = getInt(in, "Enter the user ID");
                if (id == -1) {
                    continue;
                }
                String username = getString(in, "Enter the new username");
                if (username.equals("")) {
                    continue;
                }
		String bio = getString(in, "Enter a new bio");
		if (bio.equals("")) {
		    continue;
		}
                int res = db.getUser().updateOneUser(id, username, bio);
                if (res == 1) {
                    System.out.println("Username updated");
                } else {
                    System.out.println("Error updating username");
                }
            } else if (action == 'd') {
                int id = getInt(in, "Enter the user ID");
                if (id == -1) {
                    continue;
                }
                int res = db.getUser().deleteUser(id);
                if (res == 1) {
                    System.out.println("User deleted");
                } else {
                    System.out.println("Error deleting user");
                }
            }
        }
    }

    /**
     * The interface for interacting with the Message table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void messageMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("Message");

        // The valid actions:
        String actions = "*simdq?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Message");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<Message.MessageData> res = db.getMessage().selectAllMessages();
                if (res == null) {
                    System.out.println("  Message Database Does Not Exist!");
                } else {
                    System.out.println("  Current Message Database Contents");
                    System.out.println("  ---------------------------------");
                    for (Message.MessageData rd : res) {
                        System.out.println("  [" + rd.id + "] " + rd.message + "\n     By: " + rd.getAuthorUsername() + " has " + db.getLikes().getMessageLikes(rd.id) + " net likes.");
                    }
                }
            } else if (action == 's') {
                int id = getInt(in, "Enter the message ID");
                if (id == -1) {
                    continue;
                }
                Message.MessageData res = db.getMessage().selectOneMessage(id);
                if (res == null) {
                    System.out.println("No message exists for the given id.");
                } else {
                    System.out.println("  [" + res.id + "] " + res.message + "\n     By: " + res.getAuthorUsername());
                }
            } else if (action == 'i') {
                String message = getString(in, "Enter the message");
                if (message.equals("")) {
                    System.out.println("Error entering message. Please try again.");
                    continue;
                }
                int author_id = getInt(in, "Enter the author id");
                if (author_id == -1) {
                    System.out.println("Error entering author id. Please try again");
                    continue;
                }
                int res = db.getMessage().insertMessage(message, author_id);
                System.out.println(res + " rows added");


            }  else if (action == 'm') {
                int id = getInt(in, "Enter the message ID");
                if (id == -1) {
                    continue;
                }

                String message = getString(in, "Enter the new message");
                if (message.equals("")) {
                    continue;
                }
                int res = db.getMessage().updateOneMessage(id, message);
                if (res == 1) {
                    System.out.println("Message updated");
                } else {
                    System.out.println("Error updating message");
                }
            } else if (action == 'd') {
                int id = getInt(in, "Enter the message ID");
                if (id == -1) {
                    continue;
                }
                int res = db.getMessage().deleteMessage(id);
                if (res == 1) {
                    System.out.println("Message deleted");
                } else {
                    System.out.println("Error deleting message");
                }
            }
        }
    }
     /**
     * The interface for interacting with the Attachment table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void attachmentMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("Attachment");

        // The valid actions:
        String actions = "*simdq?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Attachment");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<Attachment.AttachmentData> res = db.getAttachment().selectAllAttachments();
                if (res == null) {
                    System.out.println("  Attachment Database Does Not Exist!");
                } else {
                    System.out.println("  Current Attachment Database Contents");
                    System.out.println("  ---------------------------------");
                    for (Attachment.AttachmentData rd : res) {
                        System.out.println("  [" + rd.id + "] " + rd.message + "\n     By: " + rd.mime_type);
                    }
                }
            } else if (action == 's') {
                int id = getInt(in, "Enter the attachment ID");
                if (id == -1) {
                    continue;
                }
                Attachment.AttachmentData res = db.getAttachment().selectOneAttachment(id);
                if (res == null) {
                    System.out.println("No Attachment exists for the given id.");
                } else {
                    System.out.println("["+ res.id + "] " + res.message + "  " + res.mime_type + " " + res.file_id);
                }
            } else if (action == 'i') {
                String mime_type = getString(in, "Enter the mime_type");
                if (mime_type.equals("")) {
                    System.out.println("Error entering mime_type. Please try again.");
                    continue;
                }
                int message = getInt(in, "Enter the message");
                if (message == -1) {
                    System.out.println("Error entering message. Please try again");
                    continue;
                }
				String file_id = getString(in, "Enter the Google Drive file ID");
				if (file_id.equals("")) {
					System.out.println("Error entering file ID. Please try again.");
					continue;
				}
                int res = db.getAttachment().insertAttachment(mime_type, message, file_id);
                System.out.println(res + " rows added");

            }  else if (action == 'm') {
                int id = getInt(in, "Enter the attachment ID");
                if (id == -1) {
					System.out.println("Invalid attachment ID. Please try again.");
                    continue;
                }

                String mime_type = getString(in, "Enter the new mime_type");
                if (mime_type.equals("")) {
					System.out.println("Invalid mime type. Please try again.");
					continue;
                }

				String file_id = getString(in, "Enter the new Google file ID");
				if (file_id.equals("")) {
						System.out.println("Invalid file ID. Please try again.");
				}

                int res = db.getAttachment().updateOneAttachment(id, mime_type, file_id);
                if (res == 1) {
                    System.out.println("Mine_type updated");
                } else {
                    System.out.println("Error updating mime_type");
                }
            } else if (action == 'd') {
                int id = getInt(in, "Enter the ID");
                if (id == -1) {
                    continue;
                }
                int res = db.getAttachment().deleteAttachment(id);
                if (res == 1) {
                    System.out.println("Attachment deleted");
                } else {
                    System.out.println("Error deleting Attachment");
                }
            }
        }
    }
/**
     * The interface for interacting with the link table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void linkMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("Link");

        // The valid actions:
        String actions = "*simdq?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Link");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<Link.LinkData> res = db.getLink().selectAllLinks();
                if (res == null) {
                    System.out.println("  Link Database Does Not Exist!");
                } else {
                    System.out.println("  Current Link Database Contents");
                    System.out.println("  ---------------------------------");
                    for (Link.LinkData rd : res) {
                        System.out.println( rd.message + "  " + rd.link);
                    }
                }
            } else if (action == 'i') {
                String link = getString(in, "Enter link");
                if (link.equals("")) {
                    System.out.println("Error entering link. Please try again.");
                    continue;
                }
                int message = getInt(in, "Enter the message");
                if (message == -1) {
                    System.out.println("Error entering message. Please try again");
                    continue;
                }
                int res = db.getLink().insertLink(link,message);
                System.out.println(res + " rows added");

            }  else if (action == 'm') {
                String link = getString(in, "Enter the new link");
                if (link.equals("")) {
					System.out.println("Invalid link. Please try again.");
					continue;
                }

				int message = getInt(in, "Enter the message");
                if (message == -1) {
                    System.out.println("Error entering message. Please try again");
                    continue;
                }
                int res = db.getLink().updateOneLink(message,link);
                if (res == 1) {
                    System.out.println("link updated");
                } else {
                    System.out.println("Error updating link");
                }
            } else if (action == 'd') {
                int message = getInt(in, "Enter the message");
                if (message == -1) {
                    System.out.println("Error entering message. Please try again");
                    continue;
                }
                int res = db.getLink().deleteLink(message);
                if (res == 1) {
                    System.out.println("Link deleted");
                } else {
                    System.out.println("Error deleting Link");
                }
            }
        }
    }

    /**
     * The interface for interacting with the Likes table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void likesMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("Likes");

        // The valid actions:
        String actions = "*~+-q?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Likes");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<Like.LikesData> res = db.getLikes().selectAllLikes();
                if (res == null) {
                    System.out.println("  Likes Database Does Not Exist!");
                } else {
                    System.out.println("  Current Likes Database Contents");
                    System.out.println("  -------------------------------");
                    String word = null;
                    for (Like.LikesData rd : res) {
                        if (rd.likeFlag == true) {
                            word = "Likes";
                        } else if (rd.likeFlag == false) {
                            word = "Dislikes";
                        }
                        if (word != null) {
                            System.out.println("  Message " + rd.message_id + " " + word + " By User " + rd.liker);
                        }
                        word = null;
                    }
                }
            }  else if (action == '+' || action == '~' || action == '-') {
                int message_id = getInt(in, "Enter the message ID");
                if (message_id == -1) {
                    continue;
                }
                int liker = getInt(in, "Enter the user ID");
                if (liker == -1) {
                    continue;
                }
                Boolean status = null;
                if (action == '+') {
                    status = true;
                } else if (action == '-') {
                    status = false;
                }
                int res = db.getLikes().messageLike(status, message_id, liker);
                if (res == 1) {
                    System.out.println("Like updated");
                } else {
                    System.out.println("Error updating like");
                }
            }
        }
    }

    /**
     * The interface for interacting with the Parent table
     *
     * @param in Passed in input parameter
     * @param db Passed in database connection
     * @
     */
    public static void parentMenu (BufferedReader in, Database db) {
        Boolean cli = true;
        help("Parent");

        // The valid actions:
        String actions = "*c+-q?";
        while (cli) {
            char action = prompt(in, actions);
            if (action == '?') {
                help("Parent");
            } else if (action == 'q') {
                cli = false;
            } else if (action == '*') {
                ArrayList<Parent.ParentData> res = db.getParent().selectAllParent();
                if (res == null) {
                    System.out.println("  Parent Database Does Not Exist!");
                } else {
                    System.out.println("  Current Parent Database Contents");
                    System.out.println("  --------------------------------");

                    for (Parent.ParentData rd : res) {
                        System.out.println("  Message " + rd.parent_id +  " has reply with message " + rd.message_id);
                    }
                }
            } else if (action == '+') {
                int parent_id = getInt(in, "Enter the parent message ID");
                if (parent_id == -1) {
                    continue;
                }
                int message_id = getInt(in, "Enter the reply message ID");
                if (message_id == -1) {
                    continue;
                }

                int res = db.getParent().createReply(parent_id, message_id);
                if (res == 1) {
                    System.out.println("Reply status updated");
                } else {
                    System.out.println("Error updating reply status");
                }

            } else if (action == '-') {
                int parent_id = getInt(in, "Enter the parent message ID");
                if (parent_id == -1) {
                    continue;
                }
                int message_id = getInt(in, "Enter the reply message ID");
                if (message_id == -1) {
                    continue;
                }

                int res = db.getParent().deleteReply(parent_id, message_id);
                if (res == 1) {
                    System.out.println("Reply status updated");
                } else {
                    System.out.println("Error removing reply");
                }
            } else if (action == 'c') {
                int parent_id = getInt(in, "Enter the parent message ID");
                if (parent_id == -1) {
                    continue;
                }

                ArrayList<Parent.ParentData> res = db.getParent().selectReplyByParentId(parent_id);
                int numReplies = db.getParent().getChildrenCommentCount(parent_id);
                if (res == null) {
                    System.out.println("  Parent comment does not have any replies");
                } else {
                    System.out.println("  Parent comment " + parent_id + " has " + numReplies + " replies:");
                    for (Parent.ParentData rd : res) {
                        System.out.println("\tMessage " + rd.message_id);
                    }
                }
            }
        }
    }

}


package edu.lehigh.cse216.aztecs.backend;

/**
 * This exception should be thrown anytime there appears to be an error with the Database itself.
 */
public class DatabaseFailureException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public DatabaseFailureException(String message) {
		super(500, message);
	}
}

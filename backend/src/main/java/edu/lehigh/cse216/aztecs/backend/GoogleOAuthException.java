package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the connection to google's oauth service fails.
 */
public class GoogleOAuthException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public GoogleOAuthException(String message) {
		super(500, message);
	}
}

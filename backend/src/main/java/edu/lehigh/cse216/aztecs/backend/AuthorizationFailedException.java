package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the user's request lacks the required Authorization.
 */
public class AuthorizationFailedException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public AuthorizationFailedException(String message) {
		super(401, message);
	}
}

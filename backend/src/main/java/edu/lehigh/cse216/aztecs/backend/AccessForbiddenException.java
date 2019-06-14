package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the user's is forbidden to access the requested resource.
 */
public class AccessForbiddenException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public AccessForbiddenException(String message) {
		super(403, message);
	}
}

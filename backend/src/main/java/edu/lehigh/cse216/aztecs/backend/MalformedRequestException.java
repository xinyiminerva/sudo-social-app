package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the user's request appears nonsensical.
 */
public class MalformedRequestException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public MalformedRequestException(String message) {
		super(400, message);
	}
}

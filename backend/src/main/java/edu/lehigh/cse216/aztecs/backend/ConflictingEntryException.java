package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the user's request conflicts with an existing resource.
 */
public class ConflictingEntryException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public ConflictingEntryException(String message) {
		super(409, message);
	}
}
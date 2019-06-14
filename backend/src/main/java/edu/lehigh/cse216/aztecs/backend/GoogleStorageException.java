package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime the connection to google drive fails.
 */
public class GoogleStorageException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public GoogleStorageException(String message, Exception e) {
		super(500, message, e);
	}
}

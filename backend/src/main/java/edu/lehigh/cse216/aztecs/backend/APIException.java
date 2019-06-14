package edu.lehigh.cse216.aztecs.backend;

/**
 * This is a generic Exception class designed to manage errors within the API.
 * It contains a field for the appropriate HTTP Status Code, as well as a message to accompany
 * the error.
 */
public abstract class APIException extends Exception {
	public static final long serialVersionUID = 0;
	
	protected int status;
	
	/**
	 * Define the default constructor
	 * @param status The HTTP Status code of the Exception
	 * @param message The reason for failure
	 */
	public APIException(int status, String message) {
		super("Error " + status + ": " + message);
		this.status = status;
	}
	
	/**
	 * Allow for stacktrace preservation
	 * @param status The HTTP Status code of the Exception
	 * @param message The reason for failure
	 */
	public APIException(int status, String message, Exception e) {
		super("Error " + status + ": " + message, e);
		this.status = status;
	}

	/**
	 * Provide a public interface to retrieve the HTTP status code.
	 * We'll rely on the inherited "getMessage" method from Exception to get the actual message.
	 * @return The HTTP Status Code
	 */
	public int getStatus() {
		return this.status;
	}
}

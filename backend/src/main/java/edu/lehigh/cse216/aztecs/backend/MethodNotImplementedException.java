package edu.lehigh.cse216.aztecs.backend;

/**
 * This error should be thrown anytime an endpoint is called with an unsuported HTTP method
 */
public class MethodNotImplementedException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor.
	 * @param message The reason for failure.
	 */
	public MethodNotImplementedException() {
		super(501, "Method Not Implemented");
	}
}

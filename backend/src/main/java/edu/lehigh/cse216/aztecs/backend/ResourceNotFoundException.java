package edu.lehigh.cse216.aztecs.backend;

/**
 * This class should be thrown be database methods in the event that a resource is not found to exist
 */
public class ResourceNotFoundException extends APIException {
	public static final long serialVersionUID = 0;
	
	/**
	 * Default constructor
	 * @param reason The reason for failure
	 */
	public ResourceNotFoundException(String reason) {
		super(404, reason);
	}
}

package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author David
 *
 */
public class EmailException extends SamsoniteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3087163644578920908L;

	public EmailException(String message) {
		super(message);
	}

	public EmailException(String message, Throwable ex) {
		super(message, ex);
	}

}

package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author David
 *
 */
public class SamsoniteException extends RuntimeException {

	private static final long serialVersionUID = 8301569341261366316L;

	public SamsoniteException(String message) {
		super(message);
	}

	public SamsoniteException(String message, Throwable ex) {
		super(message, ex);
	}

}

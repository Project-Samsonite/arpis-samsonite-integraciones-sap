package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author David Nilo
 *
 */
public class LecturaRespuestaException extends SamsoniteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2575523825672091434L;

	public LecturaRespuestaException(String message) {
		super(message);
	}

	public LecturaRespuestaException(String message, Throwable ex) {
		super(message, ex);
	}

}

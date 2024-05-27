package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author arpis
 *
 */
public class PoolDbException extends PoolException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -532216649458147963L;

	public PoolDbException(String message) {
		super(message);
	}

	public PoolDbException(String message, Throwable ex) {
		super(message, ex);
	}

}

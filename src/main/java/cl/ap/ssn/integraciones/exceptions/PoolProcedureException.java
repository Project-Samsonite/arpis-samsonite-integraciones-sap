package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author arpis
 *
 */
public class PoolProcedureException extends PoolException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4427367376833903471L;

	public PoolProcedureException(String message) {
		super(message);
	}

	public PoolProcedureException(String message, Throwable ex) {
		super(message, ex);
	}

}

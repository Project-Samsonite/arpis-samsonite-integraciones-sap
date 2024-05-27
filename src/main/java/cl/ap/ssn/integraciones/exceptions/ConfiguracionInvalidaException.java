package cl.ap.ssn.integraciones.exceptions;

/**
 * 
 * @author David
 *
 */
public class ConfiguracionInvalidaException extends SamsoniteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1928484546502581374L;

	public ConfiguracionInvalidaException(String message) {
		super(message);
	}

	public ConfiguracionInvalidaException(String message, Throwable ex) {
		super(message, ex);
	}

}

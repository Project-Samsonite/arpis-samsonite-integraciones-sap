package cl.ap.ssn.integraciones.exceptions;

import org.springframework.util.ObjectUtils;

/**
 * 
 * @author arpis
 *
 */
public class PoolException extends SamsoniteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1492087819923069793L;

	public PoolException(String message) {
		super(message);
	}

	public PoolException(String message, Throwable ex) {
		super(message, ex);
	}

	public String getCauseMessage() {
		if(ObjectUtils.isEmpty(getCause())) {
			return "";
		} else {
			return getCause().getMessage();
		}
	}

}

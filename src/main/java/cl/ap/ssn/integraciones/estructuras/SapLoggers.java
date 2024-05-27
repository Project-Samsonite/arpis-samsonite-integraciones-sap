package cl.ap.ssn.integraciones.estructuras;

import java.io.Serializable;
import java.util.Map;

import ch.qos.logback.classic.Logger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SapLoggers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -565402718756189419L;

	private Map<String, Logger> loggers;

}

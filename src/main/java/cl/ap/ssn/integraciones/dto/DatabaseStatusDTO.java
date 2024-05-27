package cl.ap.ssn.integraciones.dto;

import java.io.Serializable;

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
public class DatabaseStatusDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 198103198081301620L;

	@Builder.Default
	private Boolean estado = Boolean.TRUE;

}

package cl.ap.ssn.integraciones.dto;

import java.io.Serializable;

import cl.ap.ssn.integraciones.enums.CanalEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author David
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IntegracionBaseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -251536082785614889L;

	@Builder.Default
	private EmpresaServicio empresa = new EmpresaServicio();
	private CanalEnum canal;
	private String logger;

}

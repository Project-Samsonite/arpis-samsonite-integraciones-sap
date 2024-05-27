package cl.ap.ssn.integraciones.dto.pool;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import cl.ap.ssn.integraciones.enums.CanalEnum;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
public class GuardarIntegracionInputDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6802046524210688042L;

	private Short idEmpresa;
	private CanalEnum canal;
	private Integer idIntegracion;
	private String informacionEnviada;
	private String informacionRecepcion;

}

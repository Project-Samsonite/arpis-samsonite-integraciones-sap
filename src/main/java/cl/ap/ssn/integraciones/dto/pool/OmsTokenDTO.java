package cl.ap.ssn.integraciones.dto.pool;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
public class OmsTokenDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1600361912663190545L;

	private String idCanal;
	private String idEmpresa;
	private String idTienda;
	private Date fechaModificacion;
	private String tokenAcceso;
	private Date tokenAccesoExpiracion;
	private Long tokenAccesoDuracion;
	private String tokenAccesoEstado;
	private String tokenRefresco;
	private Date tokenRefrescoExpiracion;

}

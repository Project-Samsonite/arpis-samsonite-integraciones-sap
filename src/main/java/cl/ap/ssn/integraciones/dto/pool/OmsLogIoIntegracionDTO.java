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
public class OmsLogIoIntegracionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3574229362111991713L;

	private Long id;
	private String idEmpresa;
	private String idCanal;
	private Date fechaProceso;
	private Integer idInternoOrden;
	private Integer idIntegracion;
	private String informacionEnviada;
	private String informacionRecepcion;
	private String estado;
	private String respuesta;
	private Short numeroReintento;

}

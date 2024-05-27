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
public class OmsIntegracionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6458300369791919967L;

	private Integer idIntegracion;
	private String tipoIntegracion;
	private String idTipoIntegracion;
	private String formatoIntegracion;
	private String urlIntegracion;
	private Date fechaModificacion;
	private Integer idModificadoPor;
	private String idAuthUsuario;
	private String idAuthPass;
	private String idEmpresa;
	private String idCanal;
	private String codigo;
	private String descripcion;

}

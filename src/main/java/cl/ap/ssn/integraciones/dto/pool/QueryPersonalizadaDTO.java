package cl.ap.ssn.integraciones.dto.pool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import cl.ap.ssn.integraciones.enums.QueryTipoSeccionEnum;
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
public class QueryPersonalizadaDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5252434010227842569L;

	@NotEmpty
	@Builder.Default
	private Map<QueryTipoSeccionEnum, Map<String, Object>> entrada = new HashMap<QueryTipoSeccionEnum, Map<String, Object>>();

}

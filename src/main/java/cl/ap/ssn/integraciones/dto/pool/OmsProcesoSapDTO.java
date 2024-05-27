package cl.ap.ssn.integraciones.dto.pool;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import cl.ap.ssn.integraciones.utils.DateUtils;
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
public class OmsProcesoSapDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5853798361482986994L;

	private String nombre;
	private Integer idEmpresa;
	private Integer numeroError;
	@JsonFormat(pattern = DateUtils.SYSTEM_DATETIME_GENERAL_FORMAT)
	private Date fechaError;
	private Integer numeroErrorHistorico;

}

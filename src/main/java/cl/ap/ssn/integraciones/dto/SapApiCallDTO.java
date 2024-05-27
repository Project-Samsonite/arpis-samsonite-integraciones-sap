package cl.ap.ssn.integraciones.dto;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;
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
public class SapApiCallDTO implements Serializable {

	private static final long serialVersionUID = 5938010232426397261L;

	private String endpoint;
	private String user;
	private String password;
	private String queryFechas;
	private String uriFechasFiltro;
	@Builder.Default
	private Short paginaActual = (short)1;
	private Map<CodigoIntegracionEnum, Integer> idsInt;

	public String getFinalUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.endpoint);
		sb.append(this.paginaActual);
		if(!ObjectUtils.isEmpty(this.uriFechasFiltro)) {
			sb.append(this.uriFechasFiltro);
		}
		return sb.toString();
	}

	public void aumentarPagina() {
		this.paginaActual++;
	}

}

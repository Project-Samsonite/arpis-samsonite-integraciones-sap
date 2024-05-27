package cl.ap.ssn.integraciones.dto;

import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 
 * @author David Nilo
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class IntegracionSapGenerarDTO extends IntegracionBaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4533182717876711226L;

	private CodigoIntegracionEnum codigoPost;
	private CodigoIntegracionEnum codigoGetPost;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", codigoPost=");
		sb.append(codigoPost);
		sb.append(", codigoGetPost=");
		sb.append(codigoGetPost);
		return sb.toString();
	}

	public String toStringSuper() {
		return super.toString();
	}

}

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
public class IntegracionSapEnvioDteDTO extends IntegracionBaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5191712637237854318L;

	private CodigoIntegracionEnum codigoModifica;
	private CodigoIntegracionEnum codigoModificaGet;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", codigoModifica=");
		sb.append(codigoModifica);
		sb.append(", codigoModificaGet=");
		sb.append(codigoModificaGet);
		return sb.toString();
	}

	public String toStringSuper() {
		return super.toString();
	}

}

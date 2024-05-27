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
public class IntegracionSapCancelarDTO extends IntegracionBaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5030119325151823717L;

	private CodigoIntegracionEnum codigoCancelar;
	private CodigoIntegracionEnum codigoCancelarGet;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", codigoCancelar=");
		sb.append(codigoCancelar);
		sb.append(", codigoCancelarGet=");
		sb.append(codigoCancelarGet);
		return sb.toString();
	}

	public String toStringSuper() {
		return super.toString();
	}

}

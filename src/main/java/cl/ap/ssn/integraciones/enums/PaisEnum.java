package cl.ap.ssn.integraciones.enums;

import cl.ap.ssn.integraciones.exceptions.ConfiguracionInvalidaException;

/**
 * 
 * @author David
 *
 */
public enum PaisEnum {

	CHILE("chile"),
	PERU("peru"),
	COLOMBIA("colombia"),
	ARGENTINA("argentina"),
	MEXICO("mexico")
	;

	private String value;

	PaisEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static PaisEnum findByValue(String value){
		for (PaisEnum v : values()) {
			if (v.getValue().equals(value)) {
				return v;
			}
		}
		throw new ConfiguracionInvalidaException(String.format("Pais incorrecto: %s", value));
	}

}

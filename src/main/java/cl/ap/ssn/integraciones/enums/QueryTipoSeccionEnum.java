package cl.ap.ssn.integraciones.enums;

import cl.ap.ssn.integraciones.exceptions.ConfiguracionInvalidaException;

/**
 * 
 * @author David
 *
 */
public enum QueryTipoSeccionEnum {

	WHERE("WHERE"),
	SET("SET"),
	;

	private String value;

	QueryTipoSeccionEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static QueryTipoSeccionEnum findByValue(String value){
		for (QueryTipoSeccionEnum v : values()) {
			if (v.getValue().equals(value)) {
				return v;
			}
		}
		throw new ConfiguracionInvalidaException(String.format("Incorrecto: %s", value));
	}

}

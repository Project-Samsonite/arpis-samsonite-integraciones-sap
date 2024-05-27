package cl.ap.ssn.integraciones.enums;

import cl.ap.ssn.integraciones.exceptions.ConfiguracionInvalidaException;

/**
 * 
 * @author David
 *
 */
public enum CanalEnum {

	SAP((short) 0),
	MULTIVENDE((short) 1),
	SHOPIFY((short) 2)
	;

	private Short value;

	CanalEnum(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return this.value;
	}

	public static CanalEnum findByValue(short value){
		for (CanalEnum v : values()) {
			if (v.getValue().shortValue() == value) {
				return v;
			}
		}
		throw new ConfiguracionInvalidaException(String.format("Canal incorrecto: %s", value));
	}

}

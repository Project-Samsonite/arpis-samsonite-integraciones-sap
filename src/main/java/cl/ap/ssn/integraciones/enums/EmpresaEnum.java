package cl.ap.ssn.integraciones.enums;

import cl.ap.ssn.integraciones.exceptions.ConfiguracionInvalidaException;

/**
 * 
 * @author David
 *
 */
public enum EmpresaEnum {

	SAMSONITE("samsonite"),
	TUMI("tumi"),
	XTREM("xtrem"),
	SECRET("secret"),
	SAXOLINE("saxoline")
	;

	private String value;

	EmpresaEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static EmpresaEnum findByValue(String value){
		for (EmpresaEnum v : values()) {
			if (v.getValue().equals(value)) {
				return v;
			}
		}
		throw new ConfiguracionInvalidaException(String.format("Empresa incorrecta: %s", value));
	}

}

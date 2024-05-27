package cl.ap.ssn.integraciones.enums;

import cl.ap.ssn.integraciones.exceptions.ConfiguracionInvalidaException;

/**
 * 
 * @author David
 *
 */
public enum AccionEnum {

	GENERAR_SAP("generar-sap"),
	ENVIO_DTE_SAP("envio-dte-sap"),
	GENERAR_SAP_NC("generar-sap-nc"),
	ENVIO_NC_SAP("envio-nc-sap"),
	CANCELAR_SAP("cancelar-sap"),
	;

	private String value;

	AccionEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static AccionEnum findByValue(String value){
		for (AccionEnum v : values()) {
			if (v.getValue().equals(value)) {
				return v;
			}
		}
		throw new ConfiguracionInvalidaException(String.format("Accion incorrecta: %s", value));
	}

}

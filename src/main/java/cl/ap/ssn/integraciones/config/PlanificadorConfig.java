package cl.ap.ssn.integraciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.ap.ssn.integraciones.utils.DateUtils;

/**
 * 
 * @author David
 *
 */
@Configuration
public class PlanificadorConfig {

	@Value("${sap.process.generar-sap.interval:180}")
	private String generarSapInterval;

	@Value("${sap.process.envio-dte-sap.interval:180}")
	private String envioDteSapInterval;

	@Value("${sap.process.generar-sap-nc.interval:180}")
	private String generarSapNcInterval;

	@Value("${sap.process.envio-nc-sap.interval:180}")
	private String envioNcSapInterval;

	@Value("${sap.process.cancelar-sap.interval:180}")
	private String cancelarSapInterval;

	@Bean
	long generarSapInterval() {
		return Long.valueOf(this.generarSapInterval) * DateUtils.MULTIPLO_MILISEC;
	}

	@Bean
	long envioDteSapInterval() {
		return Long.valueOf(this.envioDteSapInterval) * DateUtils.MULTIPLO_MILISEC;
	}

	@Bean
	long generarSapNcInterval() {
		return Long.valueOf(this.generarSapNcInterval) * DateUtils.MULTIPLO_MILISEC;
	}

	@Bean
	long envioNcSapInterval() {
		return Long.valueOf(this.envioNcSapInterval) * DateUtils.MULTIPLO_MILISEC;
	}

	@Bean
	long cancelarSapInterval() {
		return Long.valueOf(this.cancelarSapInterval) * DateUtils.MULTIPLO_MILISEC;
	}

}

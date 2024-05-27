package cl.ap.ssn.integraciones.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import cl.ap.ssn.integraciones.dto.IntegracionBaseDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapCancelarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioDteDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioNcDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarNcDTO;
import cl.ap.ssn.integraciones.enums.AccionEnum;
import cl.ap.ssn.integraciones.estructuras.SapLoggers;

/**
 * 
 * @author David
 *
 */
@Configuration
public class SapLoggerConfig {

	@Value("${logger.pattern}")
	private String loggerPattern;
	@Value("${logger.level}")
	private String loggerLevel;
	@Value("${logger.base.path}")
	private String loggerPath;
	@Value("${logger.max.history.files}")
	private Integer loggerMaxHistoryFiles;
	@Value("${spring.application.name}")
	private String applicationName;

	private List<IntegracionSapGenerarDTO> sapGenerar;
	private List<IntegracionSapEnvioDteDTO> sapEnvioDte;
	private List<IntegracionSapGenerarNcDTO> sapGenerarNc;
	private List<IntegracionSapEnvioNcDTO> sapEnvioNc;
	private List<IntegracionSapCancelarDTO> sapCancelar;

	public SapLoggerConfig(List<IntegracionSapGenerarDTO> sapGenerar,
			List<IntegracionSapEnvioDteDTO> sapEnvioDte,
			List<IntegracionSapGenerarNcDTO> sapGenerarNc,
			List<IntegracionSapEnvioNcDTO> sapEnvioNc,
			List<IntegracionSapCancelarDTO> sapCancelar) {
		this.sapGenerar = sapGenerar;
		this.sapEnvioDte = sapEnvioDte;
		this.sapGenerarNc = sapGenerarNc;
		this.sapEnvioNc = sapEnvioNc;
		this.sapCancelar = sapCancelar;
	}

	@Bean
	SapLoggers sapLoggers() {
		final Map<String, Logger> loggers = new HashMap<>();
		for(IntegracionSapGenerarDTO integracion : this.sapGenerar) {
			loggers.put(integracion.getLogger(), this.crearLogger(integracion, AccionEnum.GENERAR_SAP.getValue()));
		}
		for(IntegracionSapEnvioDteDTO integracion : this.sapEnvioDte) {
			loggers.put(integracion.getLogger(), this.crearLogger(integracion, AccionEnum.ENVIO_DTE_SAP.getValue()));
		}
		for(IntegracionSapGenerarNcDTO integracion : this.sapGenerarNc) {
			loggers.put(integracion.getLogger(), this.crearLogger(integracion, AccionEnum.GENERAR_SAP_NC.getValue()));
		}
		for(IntegracionSapEnvioNcDTO integracion : this.sapEnvioNc) {
			loggers.put(integracion.getLogger(), this.crearLogger(integracion, AccionEnum.ENVIO_NC_SAP.getValue()));
		}
		for(IntegracionSapCancelarDTO integracion : this.sapCancelar) {
			loggers.put(integracion.getLogger(), this.crearLogger(integracion, AccionEnum.CANCELAR_SAP.getValue()));
		}
		return SapLoggers.builder().loggers(loggers).build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Logger crearLogger(final IntegracionBaseDTO integracion, final String directorio) {
		// Get context
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger log = null;
		PatternLayoutEncoder logEncoder = null;
		ConsoleAppender consoleAppender = null;
		RollingFileAppender fileAppender = null;
		TimeBasedRollingPolicy filePolicy = null;
		/*
		 * Console appender
		 */
		logEncoder = new PatternLayoutEncoder();
		logEncoder.setContext(context);
		logEncoder.setPattern(this.loggerPattern);
		logEncoder.start();
		consoleAppender = new ConsoleAppender();
		consoleAppender.setContext(context);
		consoleAppender.setName(String.format("console-%s", integracion.getLogger()));
		consoleAppender.setEncoder(logEncoder);
		consoleAppender.start();
		/*
		 * MV appender
		 */
		fileAppender = new RollingFileAppender();
		fileAppender.setContext(context);
		fileAppender.setName(String.format("file-%s", integracion.getLogger()));
		fileAppender.setEncoder(logEncoder);
		fileAppender.setPrudent(true);
		fileAppender.setAppend(true);
		filePolicy = new TimeBasedRollingPolicy();
		filePolicy.setContext(context);
		filePolicy.setParent(fileAppender);
		filePolicy.setFileNamePattern(String.format("%s/%s/%s/%s/%s/%s_%s_%s.log",
				this.loggerPath, this.applicationName,
				integracion.getEmpresa().getPais().getValue(),
				integracion.getEmpresa().getEmpresa().getValue(),
				directorio,
				integracion.getLogger(), this.applicationName, "%d{yyyy-MM-dd}"));
		filePolicy.setMaxHistory(this.loggerMaxHistoryFiles);
		filePolicy.start();
		fileAppender.setRollingPolicy(filePolicy);
		fileAppender.start();
		/*
		 * Logger
		 */
		log = context.getLogger(String.format("main-%s", integracion.getLogger()));
		log.setAdditive(false);
		log.setLevel(Level.toLevel(this.loggerLevel));
		log.addAppender(consoleAppender);
		log.addAppender(fileAppender);
		return log;
	}

}

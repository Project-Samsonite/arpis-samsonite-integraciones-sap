package cl.ap.ssn.integraciones.planificadores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.dto.DatabaseStatusDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapCancelarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioDteDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioNcDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarNcDTO;
import cl.ap.ssn.integraciones.feign.clients.DbCheckerClient;
import cl.ap.ssn.integraciones.services.ProcessService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author David
 *
 */
@Component
@Slf4j
public class SapProcess {

	private DbCheckerClient dbCheckerClient;
	private ProcessService<IntegracionSapGenerarDTO> generarService;
	private ProcessService<IntegracionSapEnvioDteDTO> enviarDteService;
	private ProcessService<IntegracionSapGenerarNcDTO> generarNcService;
	private ProcessService<IntegracionSapEnvioNcDTO> enviarNcService;
	private ProcessService<IntegracionSapCancelarDTO> cancelarService;
	private List<IntegracionSapGenerarDTO> sapGenerar;
	private List<IntegracionSapEnvioDteDTO> sapEnvioDte;
	private List<IntegracionSapGenerarNcDTO> sapGenerarNc;
	private List<IntegracionSapEnvioNcDTO> sapEnvioNc;
	private List<IntegracionSapCancelarDTO> sapCancelar;

	public SapProcess(DbCheckerClient dbCheckerClient,
			ProcessService<IntegracionSapGenerarDTO> generarService,
			ProcessService<IntegracionSapEnvioDteDTO> enviarDteService,
			ProcessService<IntegracionSapGenerarNcDTO> generarNcService,
			ProcessService<IntegracionSapEnvioNcDTO> enviarNcService,
			ProcessService<IntegracionSapCancelarDTO> cancelarService,
			List<IntegracionSapGenerarDTO> sapGenerar,
			List<IntegracionSapEnvioDteDTO> sapEnvioDte,
			List<IntegracionSapGenerarNcDTO> sapGenerarNc,
			List<IntegracionSapEnvioNcDTO> sapEnvioNc,
			List<IntegracionSapCancelarDTO> sapCancelar) {
		this.dbCheckerClient = dbCheckerClient;
		// Servicios
		this.generarService = generarService;
		this.enviarDteService = enviarDteService;
		this.generarNcService = generarNcService;
		this.enviarNcService = enviarNcService;
		this.cancelarService = cancelarService;
		// Datos
		this.sapGenerar = sapGenerar;
		this.sapEnvioDte = sapEnvioDte;
		this.sapGenerarNc = sapGenerarNc;
		this.sapEnvioNc = sapEnvioNc;
		this.sapCancelar = sapCancelar;
	}

	@Scheduled(fixedDelayString = "#{@generarSapInterval}")
	private void generarSapProcess() {
		// Valida el estado de la DB que se revisa a traves del servicio PING
		try {
			final ResponseEntity<DatabaseStatusDTO> estado = this.dbCheckerClient.estado();
			if(estado.hasBody() && estado.getBody().getEstado()) {
				if(ObjectUtils.isEmpty(this.sapGenerar)) {
					log.warn("SAP generar deshabilitado");
					return;
				}
				for(IntegracionSapGenerarDTO integracion : this.sapGenerar) {
					this.generarService.procesoIntegracion(integracion);
				}
			} else {
				log.warn("Base de datos con problemas");
			}
		} catch(FeignException ex) {
			log.warn("Servicio de estado de  Base de datos con problemas. {}", ex.getMessage());
		}
	}

	@Scheduled(fixedDelayString = "#{@envioDteSapInterval}")
	private void envioDteSapProcess() {
		// Valida el estado de la DB que se revisa a traves del servicio PING
		try {
			final ResponseEntity<DatabaseStatusDTO> estado = this.dbCheckerClient.estado();
			if(estado.hasBody() && estado.getBody().getEstado()) {
				if(ObjectUtils.isEmpty(this.sapEnvioDte)) {
					log.warn("SAP envio DTE deshabilitado");
					return;
				}
				for(IntegracionSapEnvioDteDTO integracion : this.sapEnvioDte) {
					this.enviarDteService.procesoIntegracion(integracion);
				}
			} else {
				log.warn("Base de datos con problemas");
			}
		} catch(FeignException ex) {
			log.warn("Servicio de estado de  Base de datos con problemas. {}", ex.getMessage());
		}
	}

	@Scheduled(fixedDelayString = "#{@generarSapNcInterval}")
	private void generarSapNcProcess() {
		// Valida el estado de la DB que se revisa a traves del servicio PING
		try {
			final ResponseEntity<DatabaseStatusDTO> estado = this.dbCheckerClient.estado();
			if(estado.hasBody() && estado.getBody().getEstado()) {
				if(ObjectUtils.isEmpty(this.sapGenerarNc)) {
					log.warn("SAP generar NC deshabilitado");
					return;
				}
				for(IntegracionSapGenerarNcDTO integracion : this.sapGenerarNc) {
					this.generarNcService.procesoIntegracion(integracion);
				}
			} else {
				log.warn("Base de datos con problemas");
			}
		} catch(FeignException ex) {
			log.warn("Servicio de estado de  Base de datos con problemas. {}", ex.getMessage());
		}
	}

	@Scheduled(fixedDelayString = "#{@envioNcSapInterval}")
	private void envioNcSapProcess() {
		// Valida el estado de la DB que se revisa a traves del servicio PING
		try {
			final ResponseEntity<DatabaseStatusDTO> estado = this.dbCheckerClient.estado();
			if(estado.hasBody() && estado.getBody().getEstado()) {
				if(ObjectUtils.isEmpty(this.sapEnvioNc)) {
					log.warn("SAP envio NC deshabilitado");
					return;
				}
				for(IntegracionSapEnvioNcDTO integracion : this.sapEnvioNc) {
					this.enviarNcService.procesoIntegracion(integracion);
				}
			} else {
				log.warn("Base de datos con problemas");
			}
		} catch(FeignException ex) {
			log.warn("Servicio de estado de  Base de datos con problemas. {}", ex.getMessage());
		}
	}

	@Scheduled(fixedDelayString = "#{@cancelarSapInterval}")
	private void cancelarSapProcess() {
		// Valida el estado de la DB que se revisa a traves del servicio PING
		try {
			final ResponseEntity<DatabaseStatusDTO> estado = this.dbCheckerClient.estado();
			if(estado.hasBody() && estado.getBody().getEstado()) {
				if(ObjectUtils.isEmpty(this.sapCancelar)) {
					log.warn("SAP cancelar deshabilitado");
					return;
				}
				for(IntegracionSapCancelarDTO integracion : this.sapCancelar) {
					this.cancelarService.procesoIntegracion(integracion);
				}
			} else {
				log.warn("Base de datos con problemas");
			}
		} catch(FeignException ex) {
			log.warn("Servicio de estado de  Base de datos con problemas. {}", ex.getMessage());
		}
	}

}

package cl.ap.ssn.integraciones.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.dto.EmailMessageDTO;
import cl.ap.ssn.integraciones.dto.IntegracionBaseDTO;
import cl.ap.ssn.integraciones.dto.pool.QueryPersonalizadaDTO;
import cl.ap.ssn.integraciones.dto.pool.RespuestaProcedimientoDTO;
import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;
import cl.ap.ssn.integraciones.exceptions.PoolDbException;
import cl.ap.ssn.integraciones.exceptions.PoolException;
import cl.ap.ssn.integraciones.exceptions.PoolProcedureException;
import cl.ap.ssn.integraciones.feign.clients.PoolSapClient;
import cl.ap.ssn.integraciones.utils.IntegracionUtils;
import cl.ap.ssn.integraciones.utils.JsonUtils;
import feign.FeignException;

/**
 * 
 * @author arpis
 *
 * @param <T>
 */
public abstract class AbstractProcessService<T> implements ProcessService<T> {

	private EmailService emailService;
	private PoolSapClient poolSapClient;

	public AbstractProcessService(EmailService emailService,
			PoolSapClient poolSapClient) {
		this.emailService = emailService;
		this.poolSapClient = poolSapClient;
	}

	protected void manejoErrorDB(final IntegracionBaseDTO integracion, final PoolException ex) {
		final EmailMessageDTO mensaje = this.emailService.crearMensajeBase();
		mensaje.setAsunto(String.format("%s: Problema ORA - Empresa %s", mensaje.getAsunto(),
				integracion.getEmpresa().getCodigo().toString()));
		if(ObjectUtils.isEmpty(ex.getCause())) {
			mensaje.setContenido(String.format("Empresa: %s\n\nORA Error:\n%s", integracion.toString(),
					ex.getMessage()));
		} else {
			mensaje.setContenido(String.format("Empresa: %s\n\nORA Error:\n%s\n%s", integracion.toString(),
					ex.getMessage(), ex.getCauseMessage()));
		}
		this.emailService.enviarEmailMime(mensaje);
	}

	protected <U> boolean manejoMensajeDatabase(final Logger log, final IntegracionBaseDTO integracion,
			final Optional<RespuestaProcedimientoDTO> resultadoProc, final U jsonClass, final CodigoIntegracionEnum etapa) {
		if(resultadoProc.isPresent()) {
			switch (resultadoProc.get().getEstado()) {
				case "OK":
					// Proceder normalmente
					return true;
				case "ERROR":
					// Detener proceso y enviar correo
					log.error(String.format("[%s] %s: %s, XML: %s", JsonUtils.objectToJsonString(integracion), etapa.getValue(),
							IntegracionUtils.convertirJson(resultadoProc.get()), IntegracionUtils.convertirJson(jsonClass)));
					final EmailMessageDTO mensaje = this.emailService.crearMensajeBase();
					mensaje.setAsunto(String.format("%s: Problemas con procedimiento DB", mensaje.getAsunto()));
					mensaje.setContenido(String.format("Proceso:\n%s\n\nResultado:\n%s",
							IntegracionUtils.convertirJson(integracion), IntegracionUtils.convertirJson(resultadoProc.get())));
					this.emailService.enviarEmailMime(mensaje);
					return false;
				default:
					// Solamente detener proceso
					log.info(String.format("[%s] %s: %s, XML: %s", JsonUtils.objectToJsonString(integracion), etapa.getValue(),
							IntegracionUtils.convertirJson(resultadoProc.get()), IntegracionUtils.convertirJson(jsonClass)));
					return false;
			}
		} else {
			// Solamente detener proceso
			log.error(String.format("[%s] %s: No hay respuesta de la DB. XML: %s", JsonUtils.objectToJsonString(integracion),
					etapa.getValue(), IntegracionUtils.convertirJson(jsonClass)));
			return false;
		}
	}

	protected void customActualizaProcesoSap(final QueryPersonalizadaDTO query) {
		try {
			this.poolSapClient.customActualizaProcesoSap(query);
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	protected Optional<String> procesarRespuestaSap(final IntegracionBaseDTO integracion, final Integer idInternoOrden,
			final String valorRespuestaOrden) {
		try {
			final ResponseEntity<String> respuesta = this.poolSapClient.procProcesarRespuestaSap(
					integracion.getEmpresa().getCodigo(), idInternoOrden.longValue(), valorRespuestaOrden);
			if(respuesta.hasBody()) {
				return Optional.of(respuesta.getBody());
			}
			return Optional.empty();
		} catch (FeignException e) {
			if(HttpStatus.UNPROCESSABLE_ENTITY.value() == e.status()) {
				throw new PoolProcedureException(e.getMessage(), e);
			} else if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	protected Optional<String> procesarRespuestaSapNc(final IntegracionBaseDTO integracion, final Integer idInternoOrden,
			final String valorRespuestaOrden) {
		try {
			final ResponseEntity<String> respuesta = this.poolSapClient.procProcesarRespuestaSapNc(
					integracion.getEmpresa().getCodigo(), idInternoOrden.longValue(), valorRespuestaOrden);
			if(respuesta.hasBody()) {
				return Optional.of(respuesta.getBody());
			}
			return Optional.empty();
		} catch (FeignException e) {
			if(HttpStatus.UNPROCESSABLE_ENTITY.value() == e.status()) {
				throw new PoolProcedureException(e.getMessage(), e);
			} else if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	protected void procesarErrorSap(final Short codigoEmpresa, final String informacionEnviada, final String proceso,
			final String esExcepcion, final String respuesta) {
		try {
			this.poolSapClient.procesarErrorSap(codigoEmpresa, informacionEnviada, proceso, esExcepcion,
					ObjectUtils.isEmpty(respuesta) ? "S/R" : respuesta);
		} catch (FeignException e) {
			if(HttpStatus.UNPROCESSABLE_ENTITY.value() == e.status()) {
				throw new PoolProcedureException(e.getMessage(), e);
			} else if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

}

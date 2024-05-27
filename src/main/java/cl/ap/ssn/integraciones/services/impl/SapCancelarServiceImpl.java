package cl.ap.ssn.integraciones.services.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.dto.IntegracionBaseDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapCancelarDTO;
import cl.ap.ssn.integraciones.dto.SapApiCallDTO;
import cl.ap.ssn.integraciones.dto.pool.GuardarIntegracionInputDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsLogIoIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsProcesoSapDTO;
import cl.ap.ssn.integraciones.dto.pool.QueryPersonalizadaDTO;
import cl.ap.ssn.integraciones.dto.pool.RespuestaProcedimientoDTO;
import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;
import cl.ap.ssn.integraciones.enums.QueryTipoSeccionEnum;
import cl.ap.ssn.integraciones.estructuras.SapLoggers;
import cl.ap.ssn.integraciones.exceptions.LecturaRespuestaException;
import cl.ap.ssn.integraciones.exceptions.PoolException;
import cl.ap.ssn.integraciones.feign.clients.PoolSapClient;
import cl.ap.ssn.integraciones.feign.clients.SapClient;
import cl.ap.ssn.integraciones.services.AbstractProcessService;
import cl.ap.ssn.integraciones.services.DbGeneralPoolService;
import cl.ap.ssn.integraciones.services.EmailService;
import cl.ap.ssn.integraciones.utils.DateUtils;
import cl.ap.ssn.integraciones.utils.IntegracionUtils;
import cl.ap.ssn.integraciones.utils.JsonUtils;
import cl.ap.ssn.integraciones.utils.XmlUtils;
import feign.FeignException;

/**
 * 
 * @author David Nilo
 *
 */
@Service("cancelarService")
public class SapCancelarServiceImpl extends AbstractProcessService<IntegracionSapCancelarDTO> {

	@Value("${sap.soap.header.soap-action}")
	private String soapAction;
	private String proceso;

	private SapLoggers sapLoggers;
	private SapClient sapClient;
	private DbGeneralPoolService dbGeneralService;

	public SapCancelarServiceImpl(EmailService emailService,
			SapClient sapClient,
			SapLoggers sapLoggers,
			DbGeneralPoolService dbGeneralService,
			PoolSapClient poolSapClient) {
		super(emailService, poolSapClient);
		this.sapLoggers = sapLoggers;
		this.sapClient = sapClient;
		this.dbGeneralService = dbGeneralService;
		this.proceso = "CancelarSAP";
	}

	@Override
	public void procesoIntegracion(final IntegracionSapCancelarDTO integracion) {
		final Logger log = this.sapLoggers.getLoggers().get(integracion.getLogger());
		log.info(String.format("[%s] ********** INICIO **********", integracion.toStringSuper()));
		Optional<OmsLogIoIntegracionDTO> pendiente = null;
		OmsLogIoIntegracionDTO logIoTmp = null;
		ResponseEntity<String> respuestaSap = null;
		Optional<RespuestaProcedimientoDTO> resultadoProc = null;
		try {
			/*
			 * Obtener API info
			 */
			final Optional<SapApiCallDTO> apiInfo = this.obtenerApiInfo(integracion);
			if(apiInfo.isEmpty()) {
				log.warn(String.format("[%s] Sin datos para integracion", integracion.toStringSuper()));
				return;
			}
			/*
			 * Obtener datos para procesar
			 */
			pendiente = this.dbGeneralService.obtenerPendiente(integracion.getEmpresa(), null,
					apiInfo.get(), integracion.getCodigoCancelar());
			if(pendiente.isEmpty()) {
				log.warn(String.format("[%s] Sin pendiente", integracion.toStringSuper()));
				return;
			}
			/*
			 * Actualizar estado
			 */
			logIoTmp = this.actualizarLogIo(integracion, pendiente.get(), "EN PROCESO", null);
			/*
			 * Actualizar SAP
			 */
			try {
				respuestaSap = this.sapClient.consumirSoapSap(URI.create(apiInfo.get().getEndpoint()), this.soapAction,
						this.crearAutorizacion(apiInfo.get()), logIoTmp.getInformacionEnviada());
				if(!respuestaSap.hasBody()) {
					log.error(String.format("[%s]", integracion.toStringSuper()));
					// Actualizar Log
					this.actualizarLogIo(integracion, logIoTmp, "ERROR", null);
					return;
				} else if(!"OK".equals(XmlUtils.leerEstadoRespuesta(respuestaSap.getBody()))) {
					log.error(String.format("[%s]", integracion.toStringSuper()));
					// Actualizar Log
					logIoTmp = this.actualizarLogIo(integracion, logIoTmp, "ERROR", respuestaSap.getBody());
					return;
				}
			} catch(LecturaRespuestaException ex) {
				final StringBuilder sb = new StringBuilder();
				sb.append("Principal: ");
				sb.append(ex.getMessage());
				if(!ObjectUtils.isEmpty(ex.getCause())) {
					sb.append(", ");
					sb.append(ex.getCause().getMessage());
				}
				log.error(sb.toString());
				// Actualizar Log
				this.actualizarLogIo(integracion, logIoTmp, "ERROR", sb.toString());
				return;
			} catch(FeignException ex) {
				log.error(ex.contentUTF8());
				// Actualizar Log
				this.actualizarLogIo(integracion, logIoTmp, "ERROR", ex.contentUTF8());
				return;
			}
			// Actualizar Log
			logIoTmp = this.actualizarLogIo(integracion, logIoTmp, "EN PROCESO", respuestaSap.getBody());
			/*
			 * Agregar Orden
			 */
			resultadoProc = this.dbGeneralService.guardarIntegracion(integracion,
					GuardarIntegracionInputDTO.builder()
						.idEmpresa(integracion.getEmpresa().getCodigo())
						.idIntegracion(apiInfo.get().getIdsInt().get(integracion.getCodigoCancelarGet()))
						.informacionEnviada(logIoTmp.getInformacionEnviada())
						.informacionRecepcion(logIoTmp.getInformacionRecepcion())
						.canal(integracion.getCanal())
						.build());
			if(!this.manejoMensajeDatabase(log, integracion, resultadoProc, respuestaSap.getBody(),
					integracion.getCodigoCancelarGet())) {
				log.info(String.format("Problema en proceso %s", apiInfo.get().getEndpoint()));
				this.actualizarLogIo(integracion, apiInfo.get(), integracion.getCodigoCancelar(),
						logIoTmp.getId(), "PENDIENTE", (short) 0);
				return;
			}
			/*
			 * Finalizar pendiente
			 */
			this.actualizarLogIo(integracion, apiInfo.get(), integracion.getCodigoCancelar(),
					logIoTmp.getId(), "FINALIZADO", (short) 0);
			this.actualizarLogIo(integracion, apiInfo.get(), integracion.getCodigoCancelarGet(),
					Long.valueOf(resultadoProc.get().getValor()), "FINALIZADO", (short) 0);
			/*
			 * Actualizar Proceso SAP
			 */
			this.actualizarEstadoSap(integracion, this.proceso);
		} catch (PoolException ex) {
			log.error(ex.getMessage());
			if(!ObjectUtils.isEmpty(respuestaSap) && !ObjectUtils.isEmpty(respuestaSap.getBody())) {
				log.error(String.format("DB Exception: Json respuesta: %s", JsonUtils.objectToJsonString(respuestaSap.getBody())));
			}
			// Actualizar logIo si es que fue recuperado antes del error
			if(!ObjectUtils.isEmpty(logIoTmp)) {
				this.actualizarLogIo(integracion, logIoTmp, "ERROR",
						String.format("%s, %s", ex.getMessage(), ex.getCauseMessage()));
			} else if(!ObjectUtils.isEmpty(pendiente)) {
				this.actualizarLogIo(integracion, pendiente.get(), "ERROR",
						String.format("%s, %s", ex.getMessage(), ex.getCauseMessage()));
			}
			// Correo
			this.manejoErrorDB(integracion, ex);
		} finally {
			log.info(String.format("[%s] ********** FIN **********", integracion.toStringSuper()));
		}
	}

	/* =====================================================================================
	 * =====================================================================================
	 * PRIVATE SECTION
	 * =====================================================================================
	 * ===================================================================================== */

	private Optional<SapApiCallDTO> obtenerApiInfo(final IntegracionSapCancelarDTO integracion) {
		/*
		 * Obtener integraciones
		 */
		final List<OmsIntegracionDTO> infoCancela = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoCancelar(), null);
		final List<OmsIntegracionDTO> infoCancelaGet = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoCancelarGet(), null);
		if(infoCancela.isEmpty() || infoCancelaGet.isEmpty()) {
			return Optional.empty();
		}
		/*
		 * Datos para API SAP
		 */
		return Optional.of(SapApiCallDTO.builder()
				.endpoint(infoCancela.get(0).getUrlIntegracion())
				.user(infoCancela.get(0).getIdAuthUsuario())
				.password(infoCancela.get(0).getIdAuthPass())
				.idsInt(new HashMap<CodigoIntegracionEnum, Integer>() {
					private static final long serialVersionUID = 1L;
					{
						put(integracion.getCodigoCancelar(), infoCancela.get(0).getIdIntegracion());
						put(integracion.getCodigoCancelarGet(), infoCancelaGet.get(0).getIdIntegracion());
					}})
				.build());
	}

	private OmsLogIoIntegracionDTO actualizarLogIo(final IntegracionBaseDTO base, final OmsLogIoIntegracionDTO log,
			final String estado, final String datos) {
		log.setEstado(estado);
		log.setFechaProceso(DateUtils.getCurrentDate());
		log.setNumeroReintento((short)0);
		if(!ObjectUtils.isEmpty(datos)) {
			log.setInformacionRecepcion(String.format("Respuesta: %s", datos));
		}
		return this.dbGeneralService.actualizarLogIo(base, log);
	}

	private void actualizarLogIo(final IntegracionBaseDTO base, final SapApiCallDTO api, final CodigoIntegracionEnum proceso,
			final Long idLogIo, final String estado, final Short reintento) {
		final OmsLogIoIntegracionDTO datos = OmsLogIoIntegracionDTO.builder()
				// SET
				.estado(estado)
				.fechaProceso(DateUtils.getCurrentDate())
				.numeroReintento(reintento)
				// WHERE
				.idEmpresa(base.getEmpresa().getCodigo().toString())
				.idIntegracion(api.getIdsInt().get(proceso))
				.id(idLogIo)
				.build();
		final QueryPersonalizadaDTO query = QueryPersonalizadaDTO.builder().build();
		IntegracionUtils.completarQueryPersonalizada(query.getEntrada(), QueryTipoSeccionEnum.SET,
				datos, "estado", "fechaProceso", "numeroReintento");
		IntegracionUtils.completarQueryPersonalizada(query.getEntrada(), QueryTipoSeccionEnum.WHERE,
				datos, "idEmpresa", "idIntegracion", "id");
		this.dbGeneralService.customActualizaIntegracionLogIo(query);
	}

	private void actualizarEstadoSap(final IntegracionBaseDTO base, final String nombre) {
		final OmsProcesoSapDTO datos = OmsProcesoSapDTO.builder()
				// SET
				.numeroError(null)
				// WHERE
				.nombre(nombre)
				.idEmpresa(base.getEmpresa().getCodigo().intValue())
				.build();
		final QueryPersonalizadaDTO query = QueryPersonalizadaDTO.builder().build();
		IntegracionUtils.completarQueryPersonalizada(query.getEntrada(), QueryTipoSeccionEnum.SET,
				datos, "numeroError", "fechaError");
		IntegracionUtils.completarQueryPersonalizada(query.getEntrada(), QueryTipoSeccionEnum.WHERE,
				datos, "nombre", "idEmpresa");
		this.customActualizaProcesoSap(query);
	}

}

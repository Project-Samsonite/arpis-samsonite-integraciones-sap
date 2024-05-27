package cl.ap.ssn.integraciones.services.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import ch.qos.logback.classic.Logger;
import cl.ap.ssn.integraciones.dto.IntegracionBaseDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioNcDTO;
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
@Service("enviarNcService")
public class SapEnviarNcServiceImpl extends AbstractProcessService<IntegracionSapEnvioNcDTO> {

	@Value("${sap.soap.header.soap-action}")
	private String soapAction;
	private String proceso;

	private SapLoggers sapLoggers;
	private SapClient sapClient;
	private DbGeneralPoolService dbGeneralService;

	public SapEnviarNcServiceImpl(EmailService emailService,
			SapClient sapClient,
			SapLoggers sapLoggers,
			DbGeneralPoolService dbGeneralService,
			PoolSapClient poolSapClient) {
		super(emailService, poolSapClient);
		this.sapLoggers = sapLoggers;
		this.sapClient = sapClient;
		this.dbGeneralService = dbGeneralService;
		this.proceso = "ActualizarSAPNotaCredito";
	}

	@Override
	public void procesoIntegracion(final IntegracionSapEnvioNcDTO integracion) {
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
			 * Obtener DTEs para procesar
			 */
			pendiente = this.dbGeneralService.obtenerPendiente(integracion.getEmpresa(), null,
					apiInfo.get(), integracion.getCodigoModifica());
			if(pendiente.isEmpty()) {
				log.warn(String.format("[%s] Sin NC pendiente", integracion.toStringSuper()));
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
					// Procesar error
					this.procesarErrorSap(integracion, logIoTmp, this.proceso, "NO", respuestaSap.getBody());
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
				// Procesar error
				this.procesarErrorSap(integracion, logIoTmp, this.proceso, "SI", sb.toString());
				return;
			} catch(FeignException ex) {
				log.error(ex.contentUTF8());
				// Actualizar Log
				this.actualizarLogIo(integracion, logIoTmp, "ERROR", ex.contentUTF8());
				// Procesar error
				this.procesarErrorSap(integracion, logIoTmp, this.proceso, "SI", ex.contentUTF8());
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
						.idIntegracion(apiInfo.get().getIdsInt().get(integracion.getCodigoModificaGet()))
						.informacionEnviada(logIoTmp.getInformacionEnviada())
						.informacionRecepcion(logIoTmp.getInformacionRecepcion())
						.canal(integracion.getCanal())
						.build());
			if(!this.manejoMensajeDatabase(log, integracion, resultadoProc, respuestaSap.getBody(),
					integracion.getCodigoModificaGet())) {
				log.info(String.format("Problema en proceso NC %s", apiInfo.get().getEndpoint()));
				this.actualizarLogIo(integracion, logIoTmp, "PENDIENTE", resultadoProc.get().getEstado());
				return;
			}
			/*
			 * Finalizar pendiente
			 */
			this.actualizarLogIo(integracion, apiInfo.get(), integracion.getCodigoModifica(),
					logIoTmp.getId(), "FINALIZADO", (short) 0);
			this.actualizarLogIo(integracion, apiInfo.get(), integracion.getCodigoModificaGet(),
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

	private Optional<SapApiCallDTO> obtenerApiInfo(final IntegracionSapEnvioNcDTO integracion) {
		/*
		 * Obtener integraciones
		 */
		final List<OmsIntegracionDTO> infoModifica = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoModifica(), null);
		final List<OmsIntegracionDTO> infoModificaGet = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoModificaGet(), null);
		if(infoModifica.isEmpty()|| infoModificaGet.isEmpty()) {
			return Optional.empty();
		}
		/*
		 * Datos para API SAP
		 */
		return Optional.of(SapApiCallDTO.builder()
				.endpoint(infoModifica.get(0).getUrlIntegracion())
				.user(infoModifica.get(0).getIdAuthUsuario())
				.password(infoModifica.get(0).getIdAuthPass())
				.idsInt(new HashMap<CodigoIntegracionEnum, Integer>() {
					private static final long serialVersionUID = 1L;
					{
						put(integracion.getCodigoModifica(), infoModifica.get(0).getIdIntegracion());
						put(integracion.getCodigoModificaGet(), infoModificaGet.get(0).getIdIntegracion());
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

	private void procesarErrorSap(final IntegracionBaseDTO integracion, final OmsLogIoIntegracionDTO datos,
			final String proceso, final String esExcepcion, final String respuesta) {
		this.procesarErrorSap(integracion.getEmpresa().getCodigo(), datos.getInformacionEnviada(),
				proceso, esExcepcion, respuesta);
	}

}

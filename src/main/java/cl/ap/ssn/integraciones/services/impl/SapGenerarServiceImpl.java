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
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarDTO;
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
@Service("generarService")
public class SapGenerarServiceImpl extends AbstractProcessService<IntegracionSapGenerarDTO> {

	@Value("${sap.soap.header.soap-action}")
	private String soapAction;
	private String proceso;

	private SapLoggers sapLoggers;
	private SapClient sapClient;
	private DbGeneralPoolService dbGeneralService;

	public SapGenerarServiceImpl(EmailService emailService,
			SapClient sapClient,
			SapLoggers sapLoggers,
			DbGeneralPoolService dbGeneralService,
			PoolSapClient poolSapClient) {
		super(emailService, poolSapClient);
		this.sapLoggers = sapLoggers;
		this.sapClient = sapClient;
		this.dbGeneralService = dbGeneralService;
		this.proceso = "GenerarSAP";
	}

	@Override
	public void procesoIntegracion(final IntegracionSapGenerarDTO integracion) {
		final Logger log = this.sapLoggers.getLoggers().get(integracion.getLogger());
		log.info(String.format("[%s] ********** INICIO **********", integracion.toStringSuper()));
		List<OmsLogIoIntegracionDTO> ordenesPendientes = null;
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
			ordenesPendientes = this.dbGeneralService.obtenerPendientes(integracion.getEmpresa(), null,
					apiInfo.get(), integracion.getCodigoPost());
			if(ordenesPendientes.isEmpty()) {
				log.warn(String.format("[%s] Sin DTEs pendientes", integracion.toStringSuper()));
				return;
			}
			OmsLogIoIntegracionDTO logIoTmp = null;
			ResponseEntity<String> respuestaSap = null;
			Optional<RespuestaProcedimientoDTO> resultadoProc = null;
			for(OmsLogIoIntegracionDTO ordenPendiente : ordenesPendientes) {
				try {
					/*
					 * Actualizar Estado Reintento Sin Canal
					 */
					logIoTmp = this.actualizarLogIo(integracion, ordenPendiente, "EN PROCESO", null);
					/*
					 * Orden SAP
					 */
					try {
						respuestaSap = this.sapClient.consumirSoapSap(URI.create(apiInfo.get().getEndpoint()), this.soapAction,
								this.crearAutorizacion(apiInfo.get()), logIoTmp.getInformacionEnviada());
						if(!respuestaSap.hasBody()) {
							// Actualizar Log
							this.actualizarLogIo(integracion, logIoTmp, "ERROR", null);
							continue;
						} else if(!"OK".equals(XmlUtils.leerEstadoRespuesta(respuestaSap.getBody()))) {
							// Actualizar Log
							logIoTmp = this.actualizarLogIo(integracion, logIoTmp, "ERROR", respuestaSap.getBody());
							// Procesar error
							this.procesarErrorSap(integracion, logIoTmp, this.proceso, "NO", respuestaSap.getBody());
							continue;
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
						continue;
					} catch(FeignException ex) {
						log.error(ex.contentUTF8());
						// Actualizar Log
						this.actualizarLogIo(integracion, logIoTmp, "ERROR", ex.contentUTF8());
						// Procesar error
						this.procesarErrorSap(integracion, logIoTmp, this.proceso, "SI", ex.contentUTF8());
						continue;
					}
					// Actualizar Log
					logIoTmp = this.actualizarLogIo(integracion, logIoTmp, "EN PROCESO", respuestaSap.getBody());
					/*
					 * Agregar Orden
					 */
					resultadoProc = this.dbGeneralService.guardarIntegracion(integracion,
							GuardarIntegracionInputDTO.builder()
								.idEmpresa(integracion.getEmpresa().getCodigo())
								.idIntegracion(apiInfo.get().getIdsInt().get(integracion.getCodigoGetPost()))
								.informacionEnviada(logIoTmp.getInformacionEnviada())
								.informacionRecepcion(logIoTmp.getInformacionRecepcion())
								.canal(integracion.getCanal())
								.build());
					if(!this.manejoMensajeDatabase(log, integracion, resultadoProc, respuestaSap.getBody(),
							integracion.getCodigoGetPost())) {
						log.info(String.format("Problema en proceso generar %s", apiInfo.get().getEndpoint()));
						this.actualizarLogIo(integracion, logIoTmp, "PENDIENTE", resultadoProc.get().getEstado());
						continue;
					}
					/*
					 * Procesar orden SAP
					 */
					final Optional<String> prcSap = this.procesarRespuestaSap(integracion, logIoTmp.getIdInternoOrden(),
							resultadoProc.get().getValor());
					if(prcSap.isPresent()) {
						log.info("[{}] Proceso respuesta SAP: {}", integracion.toStringSuper(), prcSap.get());
					}
					/*
					 * Actualizar Proceso SAP
					 */
					this.actualizarEstadoSap(integracion, this.proceso);
					/*
					 * Finalizar pendiente
					 */
					this.actualizarLogIo(integracion, logIoTmp, "FINALIZADO", null);
				} catch (PoolException ex) {
					log.error(ex.getMessage());
					if(!ObjectUtils.isEmpty(respuestaSap) && !ObjectUtils.isEmpty(respuestaSap.getBody())) {
						log.error(String.format("DB Exception: Json respuesta: %s", JsonUtils.objectToJsonString(respuestaSap.getBody())));
					}
					// Actualizar logIo si es que fue recuperado antes del error
					if(!ObjectUtils.isEmpty(logIoTmp)) {
						this.actualizarLogIo(integracion, logIoTmp, "ERROR",
								String.format("%s, %s", ex.getMessage(), ex.getCauseMessage()));
					} else if(!ObjectUtils.isEmpty(ordenPendiente)) {
						this.actualizarLogIo(integracion, ordenPendiente, "ERROR",
								String.format("%s, %s", ex.getMessage(), ex.getCauseMessage()));
					}
					// Correo
					this.manejoErrorDB(integracion, ex);
				}
			}
		} catch (PoolException ex) {
			log.error(ex.getMessage());
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

	private Optional<SapApiCallDTO> obtenerApiInfo(final IntegracionSapGenerarDTO integracion) {
		/*
		 * Obtener integraciones
		 */
		final List<OmsIntegracionDTO> infoPost = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoPost(), null);
		final List<OmsIntegracionDTO> infoGetPost = this.dbGeneralService.obtenerIntegracionesOms(
				integracion.getEmpresa(), integracion.getCodigoGetPost(), null);
		if(infoPost.isEmpty() || infoGetPost.isEmpty()) {
			return Optional.empty();
		}
		/*
		 * Datos para API SAP
		 */
		return Optional.of(SapApiCallDTO.builder()
				.endpoint(infoPost.get(0).getUrlIntegracion())
				.user(infoPost.get(0).getIdAuthUsuario())
				.password(infoPost.get(0).getIdAuthPass())
				.idsInt(new HashMap<CodigoIntegracionEnum, Integer>() {
					private static final long serialVersionUID = 1L;
					{
						put(integracion.getCodigoPost(), infoPost.get(0).getIdIntegracion());
						put(integracion.getCodigoGetPost(), infoGetPost.get(0).getIdIntegracion());
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

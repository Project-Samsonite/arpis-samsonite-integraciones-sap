package cl.ap.ssn.integraciones.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import cl.ap.ssn.integraciones.dto.EmpresaServicio;
import cl.ap.ssn.integraciones.dto.IntegracionBaseDTO;
import cl.ap.ssn.integraciones.dto.SapApiCallDTO;
import cl.ap.ssn.integraciones.dto.pool.GuardarIntegracionInputDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsLogIoIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.QueryPersonalizadaDTO;
import cl.ap.ssn.integraciones.dto.pool.RespuestaProcedimientoDTO;
import cl.ap.ssn.integraciones.enums.CanalEnum;
import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;
import cl.ap.ssn.integraciones.exceptions.PoolDbException;
import cl.ap.ssn.integraciones.exceptions.PoolException;
import cl.ap.ssn.integraciones.exceptions.PoolProcedureException;
import cl.ap.ssn.integraciones.feign.clients.PoolGeneralClient;
import cl.ap.ssn.integraciones.services.DbGeneralPoolService;
import feign.FeignException;

/**
 * 
 * @author arpis
 *
 */
@Service
public class DbGeneralPoolServiceImpl implements DbGeneralPoolService {

	private PoolGeneralClient poolGeneralClient;

	public DbGeneralPoolServiceImpl(PoolGeneralClient poolGeneralClient) {
		this.poolGeneralClient = poolGeneralClient;
	}

	@Override
	public void customActualizaIntegracionLogIo(final QueryPersonalizadaDTO query) {
		try {
			this.poolGeneralClient.customActualizaIntegracionLogIo(query);
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	@Override
	public List<OmsIntegracionDTO> obtenerIntegracionesOms(final EmpresaServicio empresa,
			final CodigoIntegracionEnum codigo, final CanalEnum canal) {
		try {
			final ResponseEntity<List<OmsIntegracionDTO>> resp = this.poolGeneralClient
					.obtenerIntegracionesOms(empresa.getCodigo(), codigo, canal);
			if(resp.hasBody()) {
				return resp.getBody();
			}
			return new ArrayList<>();
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	@Override
	public Optional<OmsLogIoIntegracionDTO> obtenerPendiente(final EmpresaServicio empresa, final CanalEnum canal,
			final SapApiCallDTO apiInfo, final CodigoIntegracionEnum codigoIngegracion) {
		try {
			final ResponseEntity<OmsLogIoIntegracionDTO> resp = this.poolGeneralClient.obtenerPendiente(
					empresa.getCodigo(), apiInfo.getIdsInt().get(codigoIngegracion), canal);
			if(resp.hasBody()) {
				return Optional.of(resp.getBody());
			}
			return Optional.empty();
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	@Override
	public List<OmsLogIoIntegracionDTO> obtenerPendientes(final EmpresaServicio empresa, final CanalEnum canal,
			final SapApiCallDTO apiInfo, final CodigoIntegracionEnum codigoIngegracion) {
		try {
			final ResponseEntity<List<OmsLogIoIntegracionDTO>> resp = this.poolGeneralClient.obtenerPendientes(
					empresa.getCodigo(), apiInfo.getIdsInt().get(codigoIngegracion), canal);
			if(resp.hasBody()) {
				return resp.getBody();
			}
			return new ArrayList<>();
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	@Override
	public OmsLogIoIntegracionDTO actualizarLogIo(final IntegracionBaseDTO integracion,
			final OmsLogIoIntegracionDTO logIo) {
		try {
			final ResponseEntity<OmsLogIoIntegracionDTO> resp = this.poolGeneralClient.actualizarEstadoLogIo(
					integracion.getEmpresa().getCodigo(), integracion.getCanal(), logIo);
			if(resp.hasBody()) {
				return resp.getBody();
			}
			throw new PoolException("No se pudo actualizar estado");
		} catch (FeignException e) {
			if(HttpStatus.SERVICE_UNAVAILABLE.value() == e.status()) {
				throw new PoolDbException(e.getMessage(), e);
			}
			throw new PoolException(e.getMessage(), e);
		}
	}

	@Override
	public Optional<RespuestaProcedimientoDTO> guardarIntegracion(final IntegracionBaseDTO base,
			final GuardarIntegracionInputDTO input) {
		try {
			final ResponseEntity<RespuestaProcedimientoDTO> respuesta = this.poolGeneralClient
					.procGuardarIntegracion(base.getEmpresa().getCodigo(), input);
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

}

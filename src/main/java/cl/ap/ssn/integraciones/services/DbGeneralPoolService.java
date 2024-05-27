package cl.ap.ssn.integraciones.services;

import java.util.List;
import java.util.Optional;

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

/**
 * 
 * @author arpis
 *
 */
public interface DbGeneralPoolService {

	void customActualizaIntegracionLogIo(QueryPersonalizadaDTO query);

	List<OmsIntegracionDTO> obtenerIntegracionesOms(EmpresaServicio empresa, CodigoIntegracionEnum codigo,
			CanalEnum canal);

	Optional<OmsLogIoIntegracionDTO> obtenerPendiente(EmpresaServicio empresa, CanalEnum canal, SapApiCallDTO apiInfo,
			CodigoIntegracionEnum codigoIngegracion);

	List<OmsLogIoIntegracionDTO> obtenerPendientes(EmpresaServicio empresa, CanalEnum canal, SapApiCallDTO apiInfo,
			CodigoIntegracionEnum codigoIngegracion);

	OmsLogIoIntegracionDTO actualizarLogIo(IntegracionBaseDTO integracion, OmsLogIoIntegracionDTO log);

	Optional<RespuestaProcedimientoDTO> guardarIntegracion(IntegracionBaseDTO base, GuardarIntegracionInputDTO input);

}

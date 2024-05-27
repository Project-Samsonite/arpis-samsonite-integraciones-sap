package cl.ap.ssn.integraciones.feign.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import cl.ap.ssn.integraciones.dto.pool.GuardarIntegracionInputDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsLogIoIntegracionDTO;
import cl.ap.ssn.integraciones.dto.pool.OmsTokenDTO;
import cl.ap.ssn.integraciones.dto.pool.QueryPersonalizadaDTO;
import cl.ap.ssn.integraciones.dto.pool.RespuestaProcedimientoDTO;
import cl.ap.ssn.integraciones.enums.CanalEnum;
import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;

/**
 * 
 * @author David
 *
 */
@FeignClient(name = "poolGeneralClient", url = "${feign.url.pool.db.v1}")
public interface PoolGeneralClient {

	@PostMapping(path = "/pool/oms/custom-query/texto",
			consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> ejecutarQueryRetornaString(
			@RequestBody(required = true) String query);

	@PutMapping(path = "/pool/oms/custom-query/integracion/update",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> customActualizaIntegracion(
			@RequestBody(required = true) QueryPersonalizadaDTO query);

	@PutMapping(path = "/pool/oms/custom-query/integracion-log-io/update",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> customActualizaIntegracionLogIo(
			@RequestBody(required = true) QueryPersonalizadaDTO query);

	@PutMapping(path = "/pool/oms/custom-query/token/update",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> customActualizaToken(
			@RequestBody(required = true) QueryPersonalizadaDTO query);

	@GetMapping(path = "/pool/oms/{codigo_empresa}/token/{canal}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsTokenDTO> obtenerToken(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@PathVariable(name = "canal", required = true) CanalEnum canal);

	@PutMapping(path = "/pool/oms/{codigo_empresa}/integraciones/log-io",
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsLogIoIntegracionDTO> actualizarEstadoLogIo(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@RequestParam(name = "canal", required = false) CanalEnum canal,
			@RequestBody(required = true) OmsLogIoIntegracionDTO logIo);

	@GetMapping(path = "/pool/oms/{codigo_empresa}/integraciones/{codigo_integracion}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OmsIntegracionDTO>> obtenerIntegracionesOms(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@PathVariable(name = "codigo_integracion", required = true) CodigoIntegracionEnum codigoIntegracion,
			@RequestParam(name = "canal", required = false) CanalEnum canal);

	@GetMapping(path = "/pool/oms/{codigo_empresa}/integraciones/log-io/pendientes/{id_integracion}/antiguo",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsLogIoIntegracionDTO> obtenerPendiente(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@PathVariable(name = "id_integracion", required = true) Integer idIntegracion,
			@RequestParam(name = "canal", required = false) CanalEnum canal);

	@GetMapping(path = "/pool/oms/{codigo_empresa}/integraciones/log-io/pendientes/{id_integracion}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OmsLogIoIntegracionDTO>> obtenerPendientes(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@PathVariable(name = "id_integracion", required = true) Integer idIntegracion,
			@RequestParam(name = "canal", required = false) CanalEnum canal);

	@PostMapping(path = "/pool/oms/{codigo_empresa}/integraciones/proc",
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaProcedimientoDTO> procGuardarIntegracion(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@RequestBody(required = true) GuardarIntegracionInputDTO input);

}

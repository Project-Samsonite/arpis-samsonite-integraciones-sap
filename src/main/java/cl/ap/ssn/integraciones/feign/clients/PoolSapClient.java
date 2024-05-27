package cl.ap.ssn.integraciones.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

import cl.ap.ssn.integraciones.dto.pool.QueryPersonalizadaDTO;

/**
 * 
 * @author David
 *
 */
@FeignClient(name = "poolSapClient", url = "${feign.url.pool.db.v1}")
public interface PoolSapClient {

	@PutMapping(path = "/pool/sap/oms/custom-query/proceso-sap/update",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> customActualizaProcesoSap(
			@RequestBody(required = true) QueryPersonalizadaDTO query);

	@PostMapping(path = "/pool/sap/oms/{codigo_empresa}/integraciones/proc",
			consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> procProcesarRespuestaSap(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@RequestHeader(name = "id_interno_orden", required = true) Long idInternoOrden,
			@RequestBody(required = true) String valorRespuestaOrden);

	@PostMapping(path = "/pool/sap/oms/{codigo_empresa}/integraciones/proc/nc",
			consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> procProcesarRespuestaSapNc(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@RequestHeader(name = "id_interno_orden", required = true) Long idInternoOrden,
			@RequestBody(required = true) String valorRespuestaOrden);

	@PostMapping(path = "/pool/sap/oms/{codigo_empresa}/integraciones/proc/error",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<Void> procesarErrorSap(
			@PathVariable(name = "codigo_empresa", required = true) Short codigoEmpresa,
			@RequestPart(name = "informacion_enviada", required = true) String informacionEnviada,
			@RequestPart(name = "proceso", required = true) String proceso,
			@RequestPart(name = "es_excepcion", required = true) String esExcepcion,
			@RequestPart(name = "respuesta", required = true) String respuesta);

}

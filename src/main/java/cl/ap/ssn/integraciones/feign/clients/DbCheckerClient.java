package cl.ap.ssn.integraciones.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import cl.ap.ssn.integraciones.dto.DatabaseStatusDTO;

/**
 * 
 * @author David
 *
 */
@FeignClient(name = "dbCheckerClient", url = "${feign.url.db.checker.v1}")
public interface DbCheckerClient {

	@GetMapping(path = "/db/checker/estado",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DatabaseStatusDTO> estado();

}

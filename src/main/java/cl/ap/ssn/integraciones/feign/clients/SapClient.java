package cl.ap.ssn.integraciones.feign.clients;

import java.net.URI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 
 * @author David
 *
 */
@FeignClient(name = "sapClient", url = "http://dummy.url")
public interface SapClient {

	@PostMapping(path = "",
			consumes = "text/xml; charset=UTF-8")
	public ResponseEntity<String> consumirSoapSap(
			URI baseUrl,
			@RequestHeader(name = "SOAPAction", required = true) String soapAction,
			@RequestHeader(name = "Authorization", required = true) String authorization,
			@RequestBody(required = true) String body);

}

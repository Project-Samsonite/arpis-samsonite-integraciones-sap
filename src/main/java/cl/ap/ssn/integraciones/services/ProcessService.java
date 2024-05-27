package cl.ap.ssn.integraciones.services;

import java.util.Base64;

import cl.ap.ssn.integraciones.dto.SapApiCallDTO;

/**
 * 
 * @author David
 * @param <T>
 *
 */
public interface ProcessService<T> {

	void procesoIntegracion(T integracion);

	default String crearAutorizacion(final SapApiCallDTO apiInfo) {
		return String.format("Basic %s", Base64.getEncoder().encodeToString(
				String.format("%s:%s", apiInfo.getUser(), apiInfo.getPassword()).getBytes()));
	}

}

package cl.ap.ssn.integraciones.services;

import cl.ap.ssn.integraciones.dto.EmailMessageDTO;

/**
 * 
 * @author David
 *
 */
public interface EmailService {

	void enviarEmailMime(EmailMessageDTO message);

	EmailMessageDTO crearMensajeBase();

}

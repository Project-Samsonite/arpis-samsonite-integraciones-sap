package cl.ap.ssn.integraciones.services.impl;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import cl.ap.ssn.integraciones.dto.EmailMessageDTO;
import cl.ap.ssn.integraciones.exceptions.EmailException;
import cl.ap.ssn.integraciones.services.EmailService;

/**
 * 
 * @author David
 *
 */
@Service
public class EmailServiceImpl implements EmailService {

	@Value("${spring.profiles.active}")
	private String profiles;
	@Value("${mail.data.from}")
	private String origen;
	@Value("#{'${mail.data.to:}'.split(',')}")
	private List<String> receptores;

	private JavaMailSender emailSender;

	public EmailServiceImpl(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	@Override
	public void enviarEmailMime(final EmailMessageDTO message) {
		try {
			MimeMessage mimeMessage = this.emailSender.createMimeMessage();
			mimeMessage.setSubject(message.getAsunto(), "ISO-8859-1");
			mimeMessage.setFrom(message.getOrigen());
			message.getReceptores().stream().forEach(r -> {
				try {
					mimeMessage.addRecipients(RecipientType.TO, r);
				} catch (MessagingException e) {
					throw new EmailException("TO config", e);
				}
			});
			if(!message.getReceptoresEnCopia().isEmpty()) {
				message.getReceptoresEnCopia().stream().forEach(r -> {
					try {
						mimeMessage.addRecipients(RecipientType.CC, r);
					} catch (MessagingException e) {
						throw new EmailException("CC config", e);
					}
				});
			}
			mimeMessage.setContent(message.getContenido(), "text/plain; charset=ISO-8859-1");
			this.emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new EmailException("Error general", e);
		} catch (MailAuthenticationException e) {
			throw new EmailException("Error Auth correo", e);
		}
	}

	@Override
	public EmailMessageDTO crearMensajeBase() {
		return EmailMessageDTO.builder()
				.asunto(String.format("[%s]", this.profiles))
				.origen(this.origen)
				.receptores(this.receptores)
				.build();
	}

}

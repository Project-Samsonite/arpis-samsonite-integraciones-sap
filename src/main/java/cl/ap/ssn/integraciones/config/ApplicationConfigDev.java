package cl.ap.ssn.integraciones.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author david
 *
 */
@Configuration
@Profile("dev")
@EnableScheduling
@PropertySources({
	@PropertySource("classpath:config.properties"),
	@PropertySource("classpath:mail-dev.properties"),
	@PropertySource("classpath:servicios-dev.properties"),
	@PropertySource("classpath:procesos/empresas.properties"),
	@PropertySource("classpath:procesos/sap-dev.properties"),
})
@EnableFeignClients(basePackages = {
	"cl.ap.ssn.integraciones.feign.clients",
})
public class ApplicationConfigDev {

}

package cl.ap.ssn.integraciones.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.dto.EmpresaServicio;
import cl.ap.ssn.integraciones.enums.EmpresaEnum;
import cl.ap.ssn.integraciones.enums.PaisEnum;
import cl.ap.ssn.integraciones.exceptions.SamsoniteException;
import cl.ap.ssn.integraciones.utils.NumberUtils;

/**
 * 
 * @author David
 *
 */
@Configuration
public class EmpresaConfig {

	private Environment env;

	public EmpresaConfig(Environment env) {
		this.env = env;
	}

	@Bean
	List<EmpresaServicio> empresas() {
		final List<EmpresaServicio> valores = new ArrayList<>();
		final List<EmpresaEnum> empresas = Arrays.asList(EmpresaEnum.values());
		final List<PaisEnum> paises = Arrays.asList(PaisEnum.values());
		String habilitado = null;
		String codigo = null;
		for(EmpresaEnum empresa : empresas) {
			for(PaisEnum pais : paises) {
				habilitado = this.env.getProperty(String.format("empresa.%s.%s.habilitado", pais.getValue(), empresa.getValue()));
				if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
					codigo = this.env.getProperty(String.format("empresa.%s.%s", pais.getValue(), empresa.getValue()));
					if(NumberUtils.isNumeric(codigo)) {
						valores.add(EmpresaServicio.builder()
								.pais(pais)
								.empresa(empresa)
								.codigo(Short.valueOf(codigo))
								.build());
					} else {
						throw new SamsoniteException(String.format("Codigo debe ser numerico: '%s'", codigo));
					}
				}
			}
		}
		return valores;
	}

}

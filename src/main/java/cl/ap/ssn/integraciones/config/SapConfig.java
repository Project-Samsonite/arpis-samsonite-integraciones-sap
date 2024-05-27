package cl.ap.ssn.integraciones.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.dto.EmpresaServicio;
import cl.ap.ssn.integraciones.dto.IntegracionSapCancelarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioDteDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapEnvioNcDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarDTO;
import cl.ap.ssn.integraciones.dto.IntegracionSapGenerarNcDTO;
import cl.ap.ssn.integraciones.enums.AccionEnum;
import cl.ap.ssn.integraciones.enums.CanalEnum;
import cl.ap.ssn.integraciones.enums.CodigoIntegracionEnum;

/**
 * 
 * @author David
 *
 */
@Configuration
public class SapConfig {

	private Environment env;
	private List<EmpresaServicio> empresas;

	public SapConfig(Environment env,
			List<EmpresaServicio> empresas) {
		this.env = env;
		this.empresas = empresas;
	}

	@Bean
	List<IntegracionSapGenerarDTO> sapGenerar() {
		final List<IntegracionSapGenerarDTO> ordenes = new ArrayList<>();
		String habilitado = null;
		// Buscar usando empresas maestras
		for(EmpresaServicio empresa : this.empresas) {
			habilitado = this.env.getProperty(String.format("sap.%s.%s.%s.habilitado",
					empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP.getValue()));
			if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
				ordenes.add(IntegracionSapGenerarDTO.builder()
						.empresa(empresa)
						.codigoPost(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-post",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP.getValue()))))
						.codigoGetPost(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-get-post",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP.getValue()))))
						.logger(String.format("%s-%s-%s-%s", empresa.getPais().getValue(), CanalEnum.SAP.name().toLowerCase(),
								empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP.getValue()))
						.build());
			}
		}
		return ordenes;
	}

	@Bean
	List<IntegracionSapEnvioDteDTO> sapEnvioDte() {
		final List<IntegracionSapEnvioDteDTO> envioEstados = new ArrayList<>();
		String habilitado = null;
		// Buscar usando empresas maestras
		for(EmpresaServicio empresa : this.empresas) {
			habilitado = this.env.getProperty(String.format("sap.%s.%s.%s.habilitado",
					empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_DTE_SAP.getValue()));
			if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
				envioEstados.add(IntegracionSapEnvioDteDTO.builder()
						.empresa(empresa)
						.codigoModifica(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-modifica",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_DTE_SAP.getValue()))))
						.codigoModificaGet(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-modifica-get",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_DTE_SAP.getValue()))))
						.logger(String.format("%s-%s-%s-%s", empresa.getPais().getValue(), CanalEnum.SAP.name().toLowerCase(),
								empresa.getEmpresa().getValue(), AccionEnum.ENVIO_DTE_SAP.getValue()))
						.build());
			}
		}
		return envioEstados;
	}

	@Bean
	List<IntegracionSapGenerarNcDTO> sapGenerarNc() {
		final List<IntegracionSapGenerarNcDTO> ordenes = new ArrayList<>();
		String habilitado = null;
		// Buscar usando empresas maestras
		for(EmpresaServicio empresa : this.empresas) {
			habilitado = this.env.getProperty(String.format("sap.%s.%s.%s.habilitado",
					empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP_NC.getValue()));
			if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
				ordenes.add(IntegracionSapGenerarNcDTO.builder()
						.empresa(empresa)
						.codigoPost(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-post",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP_NC.getValue()))))
						.codigoGetPost(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-get-post",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP_NC.getValue()))))
						.logger(String.format("%s-%s-%s-%s", empresa.getPais().getValue(), CanalEnum.SAP.name().toLowerCase(),
								empresa.getEmpresa().getValue(), AccionEnum.GENERAR_SAP_NC.getValue()))
						.build());
			}
		}
		return ordenes;
	}

	@Bean
	List<IntegracionSapEnvioNcDTO> sapEnvioNc() {
		final List<IntegracionSapEnvioNcDTO> envioEstados = new ArrayList<>();
		String habilitado = null;
		// Buscar usando empresas maestras
		for(EmpresaServicio empresa : this.empresas) {
			habilitado = this.env.getProperty(String.format("sap.%s.%s.%s.habilitado",
					empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_NC_SAP.getValue()));
			if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
				envioEstados.add(IntegracionSapEnvioNcDTO.builder()
						.empresa(empresa)
						.codigoModifica(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-modifica",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_NC_SAP.getValue()))))
						.codigoModificaGet(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-modifica-get",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.ENVIO_NC_SAP.getValue()))))
						.logger(String.format("%s-%s-%s-%s", empresa.getPais().getValue(), CanalEnum.SAP.name().toLowerCase(),
								empresa.getEmpresa().getValue(), AccionEnum.ENVIO_NC_SAP.getValue()))
						.build());
			}
		}
		return envioEstados;
	}

	@Bean
	List<IntegracionSapCancelarDTO> sapCancelar() {
		final List<IntegracionSapCancelarDTO> cancelaciones = new ArrayList<>();
		String habilitado = null;
		// Buscar usando empresas maestras
		for(EmpresaServicio empresa : this.empresas) {
			habilitado = this.env.getProperty(String.format("sap.%s.%s.%s.habilitado",
					empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.CANCELAR_SAP.getValue()));
			if(!ObjectUtils.isEmpty(habilitado) && Boolean.valueOf(habilitado)) {
				cancelaciones.add(IntegracionSapCancelarDTO.builder()
						.empresa(empresa)
						.codigoCancelar(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-cancelar",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.CANCELAR_SAP.getValue()))))
						.codigoCancelarGet(CodigoIntegracionEnum.findByValue(this.env.getProperty(String.format("sap.%s.%s.%s.codigo-cancelar-get",
								empresa.getPais().getValue(), empresa.getEmpresa().getValue(), AccionEnum.CANCELAR_SAP.getValue()))))
						.logger(String.format("%s-%s-%s-%s", empresa.getPais().getValue(), CanalEnum.SAP.name().toLowerCase(),
								empresa.getEmpresa().getValue(), AccionEnum.CANCELAR_SAP.getValue()))
						.build());
			}
		}
		return cancelaciones;
	}

}

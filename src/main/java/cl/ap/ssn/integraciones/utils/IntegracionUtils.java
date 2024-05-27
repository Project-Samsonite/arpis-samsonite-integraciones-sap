package cl.ap.ssn.integraciones.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import cl.ap.ssn.integraciones.enums.QueryTipoSeccionEnum;
import cl.ap.ssn.integraciones.exceptions.SamsoniteException;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.FeignException.FeignServerException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author David
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public class IntegracionUtils {

	public static String getFeignExceptionResponseContent(final FeignException ex) {
		if(ex instanceof FeignClientException) {
			return ((FeignClientException) ex).contentUTF8();
		} else if(ex instanceof FeignServerException) {
			return ((FeignServerException) ex).contentUTF8();
		} else {
			return "Respuesta sin contenido";
		}
	}

	public static <T> String convertirJson(final T jsonClass) {
		if(ObjectUtils.isEmpty(jsonClass)) {
			return "Sin datos";
		}
		return JsonUtils.objectToJsonString(jsonClass);
	}

	public static String inputStreamToString(final InputStream is) {
		try {
			return new String(is.readAllBytes(), StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			throw new SamsoniteException(e.getMessage(), e);
		}
	}
	
	public static Optional<String> procesoSap(final String proceso) {
		switch (proceso) {
			case "GenerarSAP":
				return Optional.of("GENERAR PEDIDO");
			case "ActualizarSAP":
				return Optional.of("ACTUALIZAR PEDIDO");
			case "CancelarSAP":
				return Optional.of("CANCELAR PEDIDO");
			case "GenerarSAPNotaCredito":
				return Optional.of("GENERAR NC");
			case "ActualizarSAPNotaCredito":
				return Optional.of("ACTUALIZAR NC");
			case "CancelarSAPNotaCredito":
				return Optional.of("CANCELAR NC");
		}
		return Optional.empty();
	}

	@SafeVarargs
	public static MultiValueMap<String, Object> crearRequestPersonalizado(Object object, String ... fields) {
		MultiValueMap<String, Object> request = null;
		if(fields != null && fields.length > 0) {
			request = new LinkedMultiValueMap<>();
			Object value = null;
			for(String field : fields) {
				try {
					value = ReflectionUtils.getObjectValueFromClass(object, field);
					request.add(ReflectionUtils.getField(object.getClass(), field).getName(), value == null ? "null" : value);
				} catch (NoSuchFieldException e) {
					log.error(e.getMessage());
				}
			}
		}
		return request;
	}

	@SafeVarargs
	public static void completarQueryPersonalizada(Map<QueryTipoSeccionEnum, Map<String, Object>> request,
			QueryTipoSeccionEnum type, Object object, String ... fields) {
		if(request != null && object != null) {
			request.put(type, crearValoresQueryPersonalizada(object, fields));
		}
	}

	@SafeVarargs
	public static Map<String, Object> crearValoresQueryPersonalizada(Object object, String ... fields){
		Map<String, Object> request = null;
		if(fields != null && fields.length > 0) {
			request = new HashMap<String, Object>();
			Object value = null;
			for(String field : fields) {
				value = ReflectionUtils.getObjectValueFromClass(object, field);
				try {
					if(value instanceof Date) {
						request.put(ReflectionUtils.getField(object.getClass(), field).getName(), DateUtils.fixDateType(value));
					} else {
						request.put(ReflectionUtils.getField(object.getClass(), field).getName(), value);
					}
				} catch (NoSuchFieldException e) {
					log.error(e.getMessage());
				}
			}
		}
		return request;
	}

}

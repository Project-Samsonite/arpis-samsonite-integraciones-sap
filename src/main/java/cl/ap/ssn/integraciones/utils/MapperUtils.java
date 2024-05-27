package cl.ap.ssn.integraciones.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class MapperUtils {

	private final static ModelMapper MODEL_MAPPER;
	private final static ObjectMapper OBJECT_MAPPER;

	static {
		MODEL_MAPPER = new ModelMapper();
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T, U> List<T> mapAsList(List<U> sourceList, Class<T> destinationClass) {
		if (sourceList == null) {
			return new ArrayList<T>();
		}
		return sourceList.stream().map(e -> map(e, destinationClass)).collect(Collectors.toList());
	}

	public static <T> T map(Object source, Class<T> destinationClass) {
		if (source == null) {
			return null;
		}
		return MODEL_MAPPER.map(source, destinationClass);
	}

}

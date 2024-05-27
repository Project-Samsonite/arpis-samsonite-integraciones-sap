package cl.ap.ssn.integraciones.utils;

import java.io.Reader;
import java.io.StringReader;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

	private final static ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	public static final String objectToJsonString(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	public static final <T> T jsonStringToObject(Class<T> type, String value) {
		try {
			Reader reader = new StringReader(value);
			return OBJECT_MAPPER.readValue(reader, type);
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

}

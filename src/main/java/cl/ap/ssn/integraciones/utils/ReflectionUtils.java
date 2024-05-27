package cl.ap.ssn.integraciones.utils;

import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author arpis
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReflectionUtils {

	public static String getStringValueFromClass(Object o, String attributeName) {
		Object response = getObjectValueFromClass(o, attributeName);
		if(response != null) {
			return (String) response;
		}
		return null;
	}

	public static Object getObjectValueFromClass(Object o, String attributeName) {
		Class<?> myClass = o.getClass();
		Field myField;
		try {
			myField = getField(myClass, attributeName);
			myField.setAccessible(true);
			return myField.get(o);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass == null) {
				throw e;
			} else {
				return getField(superClass, fieldName);
			}
		}
	}

}

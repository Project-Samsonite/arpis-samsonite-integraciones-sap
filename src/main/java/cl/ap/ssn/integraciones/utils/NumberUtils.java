package cl.ap.ssn.integraciones.utils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class NumberUtils {

	private final static Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

	public static String formatoDecimalCantidad(final Float cantidad) {
		final DecimalFormat df = new DecimalFormat("#0.000");
		return String.valueOf(df.format(cantidad)).replace(",", ".");
	}

	public static String formatoDecimalPrecio(final Float precio) {
		final DecimalFormat df = new DecimalFormat("#0.0000");
		return String.valueOf(df.format(precio)).replace(",", ".");
	}

	public static String formatoDecimalPrecio(final Integer precio) {
		final DecimalFormat df = new DecimalFormat("#0.0000");
		return String.valueOf(df.format(precio)).replace(",", ".");
	}

	public static String validarEntero(final Integer valor) {
		return null != valor ? valor.toString() : "0";
	}

	public static boolean isNumeric(String value) {
		if(ObjectUtils.isEmpty(value)) {
			return false; 
		}
		return NUMBER_PATTERN.matcher(value).matches();
	}

}

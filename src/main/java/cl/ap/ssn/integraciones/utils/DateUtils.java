package cl.ap.ssn.integraciones.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cl.ap.ssn.integraciones.exceptions.SamsoniteException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author david
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

	public static final String SYSTEM_TIMEZONE = "America/Santiago";
	public static final String SYSTEM_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SYSTEM_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SYSTEM_DATETIME_GENERAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final long MULTIPLO_MILISEC = 1000;

	public static TimeZone getTimeZone() {
		return TimeZone.getTimeZone(SYSTEM_TIMEZONE);
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance(DateUtils.getTimeZone());
	}

	public static Date getCurrentDate() {
		return DateUtils.getCalendar().getTime();
	}

	public static Date tokenDateStringToDate(final String date) {
		try {
			return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).parse(date);
		} catch (ParseException e) {
			throw new SamsoniteException("Problemas leyendo fecha", e);
		}
	}

	public static Object fixDateType(Object object) {
		if(object instanceof Date) {
			return new SimpleDateFormat(SYSTEM_DATETIME_GENERAL_FORMAT).format((Date) object);
		} else {
			return object;
		}
	}

}

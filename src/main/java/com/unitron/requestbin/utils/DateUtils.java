package com.unitron.requestbin.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
	
	public static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";

	public static Date plusMillies(Date date, int timeInMillies) {
		return new Date((date.getTime()+timeInMillies));
	}
	
	public static ZonedDateTime stringToDate(String date, String pattern) {
		return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		//return ZonedDateTime.parse(date,DateTimeFormatter.ofPattern(pattern));
	}

	public static ZonedDateTime stringToZonedDateTime(Date lastSeen, String timezone) {
		if(timezone == null) {
			timezone = "Z";
		}
		return 	lastSeen == null || timezone == null ? null : ZonedDateTime.ofInstant(lastSeen.toInstant(), ZoneOffset.of(timezone));
	}
	
	public static String ZonedDateTimeToString(ZonedDateTime date) {
		return date == null ? null : date.toString();
	}
}

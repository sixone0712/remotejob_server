package jp.co.canon.rss.logmanager.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public abstract class DateUtils {

	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
}
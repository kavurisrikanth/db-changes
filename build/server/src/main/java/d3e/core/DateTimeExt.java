package d3e.core;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.Instant;

public class DateTimeExt {
	static final long monday = 1l;
	static final long tuesday = 2l;
	static final long wednesday = 3l;
	static final long thursday = 4l;
	static final long friday = 5l;
	static final long saturday = 6l;
	static final long sunday = 7l;
	static final long daysPerWeek = 7l;

	static final long january = 1l;
	static final long february = 2l;
	static final long march = 3l;
	static final long april = 4l;
	static final long may = 5l;
	static final long june = 6l;
	static final long july = 7l;
	static final long august = 8l;
	static final long september = 9l;
	static final long october = 10l;
	static final long november = 11l;
	static final long december = 12l;
	static final long monthsPerYear = 12l;

	private final static long microPerMilli = 1000l;
	private final static long nanoPerMicro = 1000l;
	private final static long millisPerSecond = 1000l;

	boolean isUtc;

	private static LocalDateTime value;
	private ZonedDateTime zonedValue;
	
	

	

	public static LocalDateTime DateTime(long year, long month, long day, long hour, long minute, long second,
			long millisecond, long microsecond) {
		long nanoSecond = (microsecond * nanoPerMicro) + (millisecond * microPerMilli * nanoPerMicro);

		value = LocalDateTime.of((int) (long) year, (int) (long) month, (int) (long) day, (int) (long) hour,
				(int) (long) minute, (int) (long) second, (int) (long) nanoSecond);
		return value;
	}

	/**
	 * TODO: Returning local DateTime for now. Check how to get the UTC version.
	 */
	public static LocalDateTime utc(long year, long month, long day, long hour, long minute, long second, long millisecond,
			long microsecond) {
		return DateTime(year, month, day, hour, minute, second, millisecond, microsecond);
	}

//	public static LocalDateTime parse(String formattedString) {
//		return new DateTime(LocalDateTime.parse(formattedString));
//	}

	public static LocalDateTime tryParse(String formattedString) {
		return LocalDateTime.parse(formattedString);
	}

	public static LocalDateTime fromMillisecondsSinceEpoch(long millisecondsSinceEpoch, boolean isUtc) {	    
		return fromMicrosecondsSinceEpoch(millisecondsSinceEpoch * microPerMilli, isUtc);
	}

	public static LocalDateTime fromMicrosecondsSinceEpoch(long microsecondsSinceEpoch, boolean isUtc) {
	    long secsSinceEpoch = microsecondsSinceEpoch / (millisPerSecond * microPerMilli);
	    long nanoSecsSinceEpoch = (microsecondsSinceEpoch % (millisPerSecond * microPerMilli)) * nanoPerMicro;
	    return LocalDateTime.ofInstant(Instant.ofEpochSecond(secsSinceEpoch, nanoSecsSinceEpoch), ZoneId.systemDefault());
	}

	/*
	 * 
	 * This method clashes with equals() in Object. TODO: Check how to do this.
	 * 
	 * public boolean equals(Object other) { // TODO: Implement this return null; }
	 */

	public boolean isBefore(LocalDateTime other) {
		return value.isBefore(other);
	}

	public boolean isAfter(LocalDateTime other) {
		return value.isAfter(other);
	}

	public static boolean isAtSameMomentAs(LocalDateTime other,LocalDateTime local) {
		return value.isEqual(other);
	}

	public static long compareTo(LocalDateTime of, LocalDateTime other) {
		return (long) of.compareTo(other);
	}

	public static boolean gt(LocalDateTime of, LocalDateTime other) {
    	return of.compareTo(other) > 0 ;
	}

	public static boolean lt(LocalDateTime of, LocalDateTime other) {
		return of.compareTo(other) < 0 ;
	}

	public static boolean gte(LocalDateTime of, LocalDateTime other) {
		return of.compareTo(other) >= 0 ;
	}

	public static boolean lte(LocalDateTime of, LocalDateTime other) {
		return of.compareTo(other) <= 0 ;
	}

	public long getHashCode() {
		return (long) value.hashCode();
	}

	public static LocalDateTime toLocal(LocalDateTime other) {
		ZonedDateTime ldtZoned = other.atZone(ZoneId.systemDefault());
		return ldtZoned.toLocalDateTime();
	}

	public static LocalDateTime toUtc(LocalDateTime other) {
		ZonedDateTime ldtZoned = other.atZone(ZoneId.systemDefault());

		ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
		
		return utcZoned.toLocalDateTime();
	}

	/**
	 * Human readable String.
	 * 
	 * @return
	 */
	public String toString() {
		return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public static String toIso8601String(LocalDateTime other) {
		return other.toString();
	}

	public static LocalDateTime add(LocalDateTime other, Duration duration) {
		return other.plus(duration);
	}

	public static LocalDateTime subtract(LocalDateTime local, Duration duration) {
		return local.minus(duration);
	}

	public static Duration difference(LocalDateTime local,LocalDateTime other) {
		return Duration.between(local, other);
	}

	public static long millisecondsSinceEpoch(LocalDateTime other) {
	    ZonedDateTime zdt = other.atZone(ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}

	public static long microsecondsSinceEpoch(LocalDateTime other) {
		return millisecondsSinceEpoch(other) * microPerMilli;
	}

	public static String timeZoneName(LocalDateTime other) {
		return ZoneId.systemDefault().getId();
	}

	public static Duration timeZoneOffset(LocalDateTime other) {
		// TODO: Implement this
		return null;
	}

	public static long getYear() {
		return (long) value.getYear();
	}

	public static long getMonth() {
		return (long) value.getMonth().getValue();
	}

	public static long day(LocalDateTime other) {
		return (long) other.getDayOfMonth();
	}

	public static long getHour() {
		return (long) value.getHour();
	}

	public static long getMinute() {
		return (long) value.getMinute();
	}

	public static long getSecond() {
		return (long) value.getSecond();
	}

	public static long millisecond(LocalDateTime other) {
		return other.getNano() / (nanoPerMicro * microPerMilli);
	}

	public static long microsecond(LocalDateTime other) {
		return other.getNano() / nanoPerMicro;
	}

	public static long weekday(LocalDateTime other) {
		return (long) other.getDayOfWeek().getValue();
	}
	public static LocalDate getDate(LocalDateTime other) {
		return other.toLocalDate();
	}
	public static LocalTime getTime(LocalDateTime other) {
		return other.toLocalTime();
	}

	public static long month(LocalDateTime of) {
		return (long) of.getMonth().getValue();
	}
}

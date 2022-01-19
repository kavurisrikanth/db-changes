package d3e.core;

import java.time.Duration;

public class DurationExt {
	static final long microsecondsPerMillisecond = 1000l;
	static final long millisecondsPerSecond = 1000l;
	static final long secondsPerMinute = 60l;
	static final long minutesPerHour = 60l;
	static final long hoursPerDay = 24l;

	static final long microsecondsPerSecond = millisecondsPerSecond * microsecondsPerMillisecond;
	static final long microsecondsPerMinute = secondsPerMinute * microsecondsPerSecond;
	static final long microsecondsPerHour = minutesPerHour * microsecondsPerMinute;
	static final long microsecondsPerDay = hoursPerDay * microsecondsPerHour;

	static final long millisecondsPerMinute = secondsPerMinute * millisecondsPerSecond;
	static final long millisecondsPerHour = minutesPerHour * millisecondsPerMinute;
	static final long millisecondsPerDay = hoursPerDay * millisecondsPerHour;

	static final long secondsPerHour = minutesPerHour * secondsPerMinute;
	static final long secondsPerDay = hoursPerDay * secondsPerHour;

	static final long minutesPerDay = hoursPerDay * minutesPerHour;

	static Duration zero;

	/*
	 * Additional helpers
	 */
	private final long mirosPerMilliSecond = 1000l;
	private final static long nanosPerMicroSecond = 1000l;

	private static java.time.Duration value;

	public static Duration Duration(long days, long hours, long minutes, long seconds, long milliseconds,
			long microseconds) {
		value = java.time.Duration.ofNanos(getNanoSeconds(days, hours, minutes, seconds, milliseconds, microseconds));
		return value;
	}
	
	public static Duration fromString(String str) {
    value = java.time.Duration.ofSeconds(Long.parseLong(str));
		return value;
	}

//	private Duration(java.time.Duration value) {
//		this.value = value;
//	}

//	private Duration Duration(Duration other) {
//		this.value = other.value;
//	}

	private static long getNanoSeconds(long days, long hours, long minutes, long seconds, long milliseconds,
			long microseconds) {
		long ans = microseconds * nanosPerMicroSecond;
		ans += (milliseconds * microsecondsPerMillisecond * nanosPerMicroSecond);
		ans += (seconds * microsecondsPerSecond * nanosPerMicroSecond);
		ans += (minutes * microsecondsPerMinute * nanosPerMicroSecond);
		ans += (hours * microsecondsPerHour * nanosPerMicroSecond);
		ans += (days * microsecondsPerDay * nanosPerMicroSecond);
		return ans;
	}

	public static Duration plus(Duration of, Duration other) {
		return of.plus(other);
	}

	public Duration minus(Duration of, Duration other) {
		return of.minus(other);
	}

	/**
	 * Direct multiplication equivalent not found so performing repeated addition.
	 * 
	 * @param factor
	 * @return
	 */
	public static Duration times(Duration of, Number factor) {
//		Duration temp = new Duration(this);
//		for (int i = 0; i < factor.longValue(); i++) {
//			temp = temp.plus(this);
//		}
//		return temp;
		// TODO
		return null;
	}

	public static Duration divToInt(Duration of, long quotient) {
		// TODO
		// return new Duration(value.dividedBy(quotient));
		return null;
	}

	public static long inDays(Duration other) {
		return other.toDays();
	}

	public static long inHours(Duration other) {
		return other.toHours();
	}

	public static long inMinutes(Duration other) {
		return other.toMinutes();
	}

	public static long inSeconds(Duration other) {
		return other.toSeconds();
	}

	public static long inMilliseconds(Duration other) {
		return other.toMillis();
	}

	public static long inMicroseconds(Duration other) {
		// TODO
    return DurationExt.inMilliseconds(other) * microsecondsPerMillisecond;
	}

	public long getHashCode() {
		return (long) super.hashCode();
	}

	public long compareTo(Duration source, Duration other) {
		return (long) source.compareTo(other);
	}

	public String toString() {
		return value.toString();
	}

	public static boolean isNegative(Duration other) {
		return value.isNegative();
	}

	public static Duration abs(Duration other) {
		// TODO
		return null;

		// return new Duration(other.abs());
	}

	public static Duration negate(Duration other) {
		// TODO
		return null;
		// return new Duration(value.negated());
	}
}

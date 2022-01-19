package d3e.core;

import java.time.LocalTime;

public class TimeExt {
  public static LocalTime now() {
    return LocalTime.now();
  }

  public static LocalTime of(long hour, long minutes, long seconds, long milliSeconds, long microSeconds) {
    // TODO
    return LocalTime.of((int) hour, (int) minutes, (int) seconds, 0);
  }

  public static long getHour(LocalTime of) {
    return of.getHour();
  }

  public static long getMinutes(LocalTime of) {
    return of.getMinute();
  }

  public static long getSeconds(LocalTime of) {
    return of.getSecond();
  }

  public static long getMillisecond(LocalTime of) {
    return of.getSecond() * 1000;
  }

  public static long getMicroSeconds(LocalTime of) {
    return getMillisecond(of) * 1000;
  }

  public static boolean isBefore(LocalTime now, LocalTime other) {
    return now.compareTo(other) < 0;
  }

  public static boolean isAfter(LocalTime now, LocalTime other) {
    return now.compareTo(other) > 0;
  }

  public static boolean isAtSameMomentAs(LocalTime now, LocalTime other) {
    return now.compareTo(other) == 0;
  }

  public static LocalTime plusHours(LocalTime now, long hours) {
    return now.plusHours(hours);
  }

  public static LocalTime plusMinutes(LocalTime now, long mins) {
    return now.plusMinutes(mins);
  }

  public static LocalTime plusSeconds(LocalTime now, long seconds) {
    return now.plusSeconds(seconds);
  }

  public static LocalTime plusMilliSeconds(LocalTime now, long milliSeconds) {
    return now.plusNanos(milliSeconds * 1000 * 1000);
  }

  public static LocalTime plusMicroSeconds(LocalTime now, long microSeconds) {
    return now.plusNanos(microSeconds * 1000);
  }
}

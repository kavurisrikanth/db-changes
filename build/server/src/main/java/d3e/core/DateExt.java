package d3e.core;

import java.time.LocalDate;

public class DateExt {

  public static long compareTo(LocalDate of, LocalDate other) {
    return of.compareTo(other);
  }

  public static boolean gt(LocalDate of, LocalDate other) {
    return of.compareTo(other) > 0 ;
  }

  public static boolean lt(LocalDate of, LocalDate other) {
    return of.compareTo(other) < 0 ;
  }

  public static boolean gte(LocalDate of, LocalDate other) {
    return of.compareTo(other) >= 0 ;
  }

  public static boolean lte(LocalDate of, LocalDate other) {
    return of.compareTo(other) <= 0 ;
  }

  public static long getDayOfMonth(LocalDate of) {
    return of.getDayOfMonth();
  }

  public static long getDayOfWeek(LocalDate of) {
    return of.getDayOfWeek().getValue();
  }

  public static long getDay(LocalDate of) {
    return of.getDayOfMonth();
  }

  public static long getMonth(LocalDate of) {
    return of.getMonthValue();
  }

  public static long getYear(LocalDate of) {
    return of.getYear();
  }

  public static boolean isLeapYear(LocalDate of) {
    return of.isLeapYear();
  }

  public static LocalDate now() {
    return LocalDate.now();
  }

  public static LocalDate of(long year, long month, long dayOfMonth) {
    return LocalDate.of((int) year, (int) month, (int) dayOfMonth);
  }

  public static LocalDate parse(String formattedString) {
    return LocalDate.parse(formattedString);
  }

  public static boolean isBefore(LocalDate now, LocalDate other) {
    return now.compareTo(other) < 0;
  }

  public static boolean isAfter(LocalDate now, LocalDate other) {
    return now.compareTo(other) > 0;
  }

  public static LocalDate plusYears(LocalDate now, long years) {
    return now.plusYears(years);
  }

  public static LocalDate plusMonths(LocalDate now, long months) {
    return now.plusMonths(months);
  }

  public static LocalDate plusWeeks(LocalDate now, long weeks) {
    return now.plusWeeks(weeks);
  }

  public static LocalDate plusDays(LocalDate now, long days) {
    return now.plusDays(days);
  }
}

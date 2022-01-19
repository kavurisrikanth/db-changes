package classes;

import d3e.core.DateExt;
import d3e.core.DateTimeExt;
import d3e.core.ListExt;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CalenderUtils {
  public CalenderUtils() {}

  public static long getMonthNumber(String month) {
    switch (month) {
      case "Jan":
        {
          return 1l;
        }
      case "Feb":
        {
          return 2l;
        }
      case "Mar":
        {
          return 3l;
        }
      case "Apr":
        {
          return 4l;
        }
      case "May":
        {
          return 5l;
        }
      case "June":
        {
          return 6l;
        }
      case "July":
        {
          return 7l;
        }
      case "Aug":
        {
          return 8l;
        }
      case "Sep":
        {
          return 9l;
        }
      case "Oct":
        {
          return 10l;
        }
      case "Nov":
        {
          return 11l;
        }
      case "Dec":
        {
          return 12l;
        }
      default:
        {
        }
    }
    return 0l;
  }

  public static String getMonthName(long month) {
    switch (((int) month)) {
      case ((int) 1l):
        {
          return "January";
        }
      case ((int) 2l):
        {
          return "February";
        }
      case ((int) 3l):
        {
          return "March";
        }
      case ((int) 4l):
        {
          return "April";
        }
      case ((int) 5l):
        {
          return "May";
        }
      case ((int) 6l):
        {
          return "June";
        }
      case ((int) 7l):
        {
          return "July";
        }
      case ((int) 8l):
        {
          return "August";
        }
      case ((int) 9l):
        {
          return "September";
        }
      case ((int) 10l):
        {
          return "October";
        }
      case ((int) 11l):
        {
          return "November";
        }
      case ((int) 12l):
        {
          return "December";
        }
      default:
        {
        }
    }
    return "";
  }

  public static List<LocalDate> prepareCalenderData(LocalDate date) {
    LocalDateTime firstDay =
        DateTimeExt.DateTime(DateExt.getYear(date), DateExt.getMonth(date), 1l, 0l, 0l, 0l, 0l, 0l);
    long weekDay = (DateTimeExt.weekday(firstDay)) + 1l;
    LocalDate firstDayDate =
        DateExt.of(firstDay.getYear(), DateTimeExt.month(firstDay), DateTimeExt.day(firstDay));
    return CalenderUtils.getListOfDates(firstDayDate, 1l - weekDay, 43l - weekDay);
  }

  public static List<LocalDate> getListOfDates(LocalDate firstDay, long statsWith, long endsWith) {
    List<LocalDate> list_of_dates = ListExt.asList();
    for (long i = statsWith; i < endsWith; i++) {
      LocalDate day = DateExt.plusDays(firstDay, i);
      list_of_dates.add(day);
    }
    return list_of_dates;
  }

  public static List<Long> getYearsList(long year, boolean forward) {
    List<Long> list_of_years = ListExt.asList();
    list_of_years.add(year);
    if (forward) {
      for (long i = 1l; i < 12l; i++) {
        list_of_years.add(year + i);
      }
    } else {
      for (long i = 1l; i < 12l; i++) {
        ListExt.insert(list_of_years, 0l, (year - i));
      }
    }
    return list_of_years;
  }
}

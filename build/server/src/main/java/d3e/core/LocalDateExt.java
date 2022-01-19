package d3e.core;

import java.time.LocalDate;

public class LocalDateExt {

	public static LocalDate of(long year, long month, long dayOfMonth) {
    return LocalDate.of((int) year, (int) month, (int) dayOfMonth);
	}
}

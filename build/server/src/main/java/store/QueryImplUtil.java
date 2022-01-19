package store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Query;
import javax.persistence.TemporalType;

public class QueryImplUtil {
	public static void setParameter(Query query, String name, DatabaseObject value) {
		if (value == null) {
			query.setParameter(name, 0l);
		} else {
			query.setParameter(name, value.getId());
		}
	}

	public static void setParameter(Query query, String name, Enum<?> value) {
		if (value == null) {
			query.setParameter(name, "");
		} else {
			query.setParameter(name, value.name());
		}
	}

	public static void setParameter(Query query, String name, Object value) {
		query.setParameter(name, value);
	}

	public static void setParameter(Query query, String name, LocalDate value) {
		if (value == null) {
			query.setParameter(name, (Date) null, TemporalType.DATE);
		} else {
			query.setParameter(name, value);

		}
	}

	public static void setParameter(Query query, String name, LocalDateTime value) {
		if (value == null) {
			query.setParameter(name, (Date) null, TemporalType.TIMESTAMP);
		} else {
			query.setParameter(name, value);
		}
	}

	public static void setParameter(Query query, String name, LocalTime value) {
		if (value == null) {
			query.setParameter(name, (Date) null, TemporalType.TIME);
		} else {
			query.setParameter(name, value);
		}
	}

	public static void setParameter(Query query, String name, String value) {
		if (value == null) {
			query.setParameter(name, "");
			return;
		}
		query.setParameter(name, value);
	}
}

package lists;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import classes.ClassUtils;

public class NativeObj {

	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private Object[] row;

	private long id;

	public NativeObj(long id) {
		row = new Object[] { id };
		this.id = id;
	}

	public NativeObj(int size) {
		row = new Object[size];
	}

	public NativeObj(Object row, int id) {
		if (!ClassUtils.getClass(row).isArray()) {
			row = new Object[] { row };
		}
		this.row = (Object[]) row;
		setId(id);
	}

	public void setId(int id) {
		if (id != -1) {
			this.id = getInteger(id);
		}
	}

	public String getString(int index) {
		return (String) row[index];
	}

	public long getInteger(int index) {
		Object o = this.row[index];
		if (o instanceof BigInteger) {
			return ((BigInteger) o).longValue();
		} else if (o instanceof Long) {
			return (long) o;
		}
		return 0l;
	}

	public boolean getBoolean(int index) {
		return (Boolean) row[index];
	}

	public NativeObj getRef(int index) {
		return new NativeObj(row[index], 0);
	}

	public long getId() {
		return id;
	}

	public Object[] getRow() {
		return row;
	}

	public void set(int index, Object val) {
		row[index] = val;
	}

	public List<NativeObj> getListRef(int index) {
		String ids = (String) row[index];
		List<NativeObj> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> new NativeObj(Long.parseLong(i.trim())))
				.collect(Collectors.toList());
		return allIds;
	}

	public static List<NativeObj> getListStruct(List<?>... props) {
		List<NativeObj> rows = new ArrayList<>();
		int rowsSize = props[0].size();
		for (int r = 0; r < rowsSize; r++) {
			Object[] result = new Object[props.length];
			for (int i = 0; i < props.length; i++) {
				List<?> p = props[i];
				Object row = p.get(r);
				result[i] = row;
			}
			rows.add(new NativeObj(result, -1));
		}
		return rows;
	}

	public List<LocalDate> getListDate(int index) {
		String ids = (String) row[index];
		List<LocalDate> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> LocalDate.parse(i.substring(1, i.length() - 1), dtf))
				.collect(Collectors.toList());
		return allIds;
	}

	public LocalDate getDate(int index) {
		Object val = this.row[index];
		if (val == null) {
			return null;
		}
		if (val instanceof LocalDate) {
			return (LocalDate) val;
		} else if (val instanceof Date) {
			return Instant.ofEpochMilli(((Date) val).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return ((Timestamp) val).toLocalDateTime().toLocalDate();
	}

	public int size() {
		return row.length;
	}

	public List<Double> getListDouble(int index) {
		String ids = (String) row[index];
		List<Double> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> Double.valueOf(i)).collect(Collectors.toList());
		return allIds;
	}

	public List<String> getListString(int index) {
		String ids = (String) row[index];
		List<String> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).collect(Collectors.toList());
		return allIds;
	}

	public LocalDateTime getDateTime(int index) {
		Object val = this.row[index];
		if (val == null) {
			return null;
		}
		if (val instanceof LocalDateTime) {
			return (LocalDateTime) val;
		}
		return ((Timestamp) val).toLocalDateTime();
	}

	public List<LocalDateTime> getListDateTime(int index) {
		String ids = (String) row[index];
		List<LocalDateTime> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> LocalDateTime.parse(i.substring(1, i.length() - 1), dtf))
				.collect(Collectors.toList());
		return allIds;
	}

	public Long getLong(int index) {
		Object o = this.row[index];
		if (o instanceof BigInteger) {
			return ((BigInteger) o).longValue();
		} else if (o instanceof Long) {
			return (long) o;
		}
		return 0l;
	}

	public List<Boolean> getListBoolean(int index) {
		String ids = (String) row[index];
		List<Boolean> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> i.equals("t")).collect(Collectors.toList());
		return allIds;
	}

	public List<Integer> getListInteger(int index) {
		String ids = (String) row[index];
		List<Integer> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(","))
				.filter(i -> !i.equals("NULL")).map(i -> Integer.valueOf(i)).collect(Collectors.toList());
		return allIds;
	}

	public <T extends Enum<T>> T getEnum(int index, Class<T> enm) {
		if (row[index] instanceof Enum) {
			return (T) row[index];
		}
		String name = (String) row[index];
		if (name == null) {
			return null;
		}
		return getEnum(name, enm);
	}

	private <T extends Enum<T>> T getEnum(String name, Class<T> enm) {
		T[] constants = enm.getEnumConstants();
		for (T t : constants) {
			if (t.name().equals(name)) {
				return t;
			}
		}
		return null;
	}

	public <T extends Enum<T>> List<T> getListEnum(int index, Class<T> enm) {
		String ids = (String) row[index];
		List<T> allIds = Arrays.stream(ids.substring(1, ids.length() - 1).split(",")).filter(i -> !i.equals("NULL"))
				.map(i -> getEnum(i, enm)).collect(Collectors.toList());
		return allIds;
	}
}

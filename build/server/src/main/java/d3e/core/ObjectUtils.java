package d3e.core;

import java.util.Objects;

import classes.ClassUtils;
import store.DatabaseObject;

public class ObjectUtils {

	static boolean isEquals(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (ClassUtils.getClass(a) == ClassUtils.getClass(a) && a instanceof DatabaseObject) {
			return ((DatabaseObject) a).getId() == ((DatabaseObject) b).getId();
		}
		return false;
	}

	static boolean isNotEquals(Object a, Object b) {
		return !isEquals(a, b);
	}

	public static <T> int compare(T a, T b) {
		if (a == null || b == null) {
			if (a == null && b == null) {
				return 0;
			}
			if (a == null) {
				return -1;
			}
			return 1;
		}

		if (a.getClass() != b.getClass()) {
			throw new RuntimeException("Cannot compare two different types");
		}

		if (a instanceof Comparable) {
			Comparable one = (Comparable) a;
			Comparable two = (Comparable) b;
			return one.compareTo(two);
		}

		String aStr = a.toString();
		String bStr = b.toString();
		return Objects.compare(aStr, bStr, String::compareTo);
	}

	public static <T> boolean isLessThan(T a, T b) {
		return compare(a, b) == -1;
	}

	public static <T> boolean isGreaterThan(T a, T b) {
		return compare(a, b) == 0;
	}
}

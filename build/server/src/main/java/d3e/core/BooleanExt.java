package d3e.core;

public class BooleanExt {

  public static long getHashCode(Boolean of) {
		return (long) of.hashCode();
	}

	public static boolean and(boolean of, boolean other) {
		if (!of) {
			return false;
		}
    return Boolean.logicalAnd(of, other);
	}

	public static boolean or(boolean of, boolean other) {
		if (of) {
			return true;
		}
    return Boolean.logicalOr(of, other);
	}

	public static boolean not(boolean of) {
		return !of;
	}

	public static boolean xor(boolean of, boolean other) {
    return Boolean.logicalXor(of, other);
	}
	
	public static String toString(boolean of) {
		return String.valueOf(of);
	}
}

package d3e.core;

public class StringBuilderExt {

	public static StringBuilder StringBuffer(Object content) {
		if (content == null) {
			return new StringBuilder();
		}
		return new StringBuilder(content.toString());
	}

	public static void write(StringBuilder b, Object obj) {
		b.append(obj);
	}

	public static void writeCharCode(StringBuilder b, long code) {
    b.append(Character.toString((char) (int) code));
	}

	public static void writeAll(StringBuilder b, Iterable<Object> objs) {
		objs.forEach(obj -> b.append(obj));
	}

	public static void writeAll(StringBuilder b, Iterable<Object> objs, String sep) {
		write(b, IterableExt.join(objs, sep));
	}

	public static void writeln(StringBuilder b) {
		writeln(b, "");
	}

	public static void writeln(StringBuilder b, Object obj) {
		b.append('\n').append(obj);
	}

	public static void clear(StringBuilder b) {
		b.delete(0, b.length());
	}

	public static boolean isEmpty(StringBuilder b) {
		return b.length() == 0;
	}

	public static boolean isNotEmpty(StringBuilder b) {
		return !isEmpty(b);
	}
}

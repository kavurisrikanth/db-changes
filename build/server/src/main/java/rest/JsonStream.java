package rest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JsonStream {
	private Writer writer;
	private boolean needComma;

	public JsonStream(Writer writer) {
		this.writer = writer;
	}

	public <T> void put(T obj, Consumer<T> consumer) {
		append('{');
		needComma = false;
		consumer.accept(obj);
		append('}');
		needComma = true;
	}

	public void put(String key, long value) {
		writeComma();
		quote(key);
		append(':');
		append(String.valueOf(value));
		needComma = true;
	}

	public void put(String key, double value) {
		writeComma();
		quote(key);
		append(':');
		append(String.valueOf(value));
		needComma = true;
	}

	public void put(String key, LocalDateTime value) {
		writeComma();
		quote(key);
		append(':');
		append(String.valueOf(value));
		needComma = true;
	}

	public void put(String key, LocalTime value) {
		writeComma();
		quote(key);
		append(':');
		append(String.valueOf(value));
		needComma = true;
	}

	public void put(String key, Enum<?> value) {
		writeComma();
		quote(key);
		append(':');
		if (value == null) {
			append("null");
		} else {
			quote(value.name());
		}
		needComma = true;
	}

	private void append(String str) {
		try {
			writer.append(str);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void append(char c) {
		try {
			writer.append(c);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void quote(String key) {
		append('"');
		append(key);
		append('"');
	}

	private void escape(String s) {
		final int len = s.length();
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				append("\\\"");
				break;
			case '\\':
				append("\\\\");
				break;
			case '\b':
				append("\\b");
				break;
			case '\f':
				append("\\f");
				break;
			case '\n':
				append("\\n");
				break;
			case '\r':
				append("\\r");
				break;
			case '\t':
				append("\\t");
				break;
			case '/':
				append("\\/");
				break;
			default:
				if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F')
						|| (ch >= '\u2000' && ch <= '\u20FF')) {
					String ss = Integer.toHexString(ch);
					append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						append('0');
					}
					append(ss.toUpperCase());
				} else {
					append(ch);
				}
			}
		}
	}

	private void writeComma() {
		if (needComma) {
			append(',');
		}
	}

	public void put(String key, boolean value) {
		writeComma();
		quote(key);
		append(':');
		append(String.valueOf(value));
		needComma = true;
	}

	public void put(String key, String value) {
		writeComma();
		quote(key);
		append(':');
		if (value == null) {
			append("null");
		} else {
			append('"');
			escape(value);
			append('"');
		}
		needComma = true;
	}

	public <T> void put(String key, T obj, Consumer<T> consumer) {
		writeComma();
		quote(key);
		append(':');
		if (obj == null) {
			append("null");
		} else {
			put(obj, consumer);
		}
		needComma = true;
	}

	public void putList(String key, List<String> values) {
		writeComma();
		quote(key);
		append(':');
		append('[');
		for (int x = 0; x < values.size(); x++) {
			if (x != 0) {
				append(',');
			}
			append('"');
			escape(values.get(x));
			append('"');
		}
		append(']');
		needComma = true;
	}

	public <T extends Enum<T>> void putEnums(String key, List<T> values) {
		writeComma();
		quote(key);
		append(':');
		append('[');
		for (int x = 0; x < values.size(); x++) {
			if (x != 0) {
				append(',');
			}
			append('"');
			append(values.get(x).name());
			append('"');
		}
		append(']');
		needComma = true;
	}

	public <T> void putObjs(String key, List<T> values, Consumer<T> consumer) {
		writeComma();
		quote(key);
		append(':');
		append('[');
		for (int x = 0; x < values.size(); x++) {
			if (x != 0) {
				append(',');
			}
			put(values.get(x), consumer);
		}
		append(']');
		needComma = true;
	}

	public <T> void putObjSet(String key, List<T> values, Consumer<T> consumer) {
		putObjs(key, new ArrayList<T>(values), consumer);
	}
}

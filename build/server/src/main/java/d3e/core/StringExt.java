package d3e.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

public class StringExt {

	/**
	 * Construct String from char codes.
	 * 
	 * @param charCodes
	 * @param start
	 * @param end
	 * @return
	 */
  public static String fromCharCodes(Iterable<Long> charCodes, long start, long end) {
    ArrayList<Long> codesList = new ArrayList<>();

		int iter = 0;
		for (long codeObj : charCodes) {
			if (iter < start) {
				iter++;
				continue;
			}

			if (iter == end)
				break;

			codesList.add(codeObj);
			iter++;
		}

		int[] codesArr = new int[codesList.size()];
		int i = 0;
		for (long code : codesList)
			codesArr[i++] = (int) (long) code;

		return new java.lang.String(codesArr, (int) (long) start, (int) (long) (end - start + 1));
	}

	public static String fromEnvironment(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static List<String> split(String source, String regex) {
		return ListExt.of(Arrays.asList(source.split(regex)), false);
	}

	public static String substring(String source, long beginIndex, long endIndex) {
		if (endIndex == 0) {
			return source.substring((int)beginIndex);
		} else {
			return source.substring((int)beginIndex, (int)endIndex);
		}
	}

	public static String fromCharCode(long charCode) {
		int[] codes = { (int) (long) charCode };
		return new java.lang.String(codes, 0, 1);
	}

	public static long compareTo(String on, String anotherString) {
		return (long) on.compareTo(anotherString);
	}

	/**
	 * Return a new String made of the one character at that index.
	 * 
	 * @param on
	 * @param index
	 * @return
	 */
	public static String get(String on, long index) {
		return Character.toString(on.charAt((int) (long) index));
	}

	/**
	 * Return the code unit at the specific index.
	 * 
	 * @param on
	 * @param index
	 * @return
	 */
	public static long codeUnitAt(String on, long index) {
		return (long) Character.getNumericValue(on.charAt((int) (long) index));
	}

	public static long length(String on) {
		return (long) on.length();
	}

	public static long getLength(String on) {
		return (long) on.length();
	}

	public static long getHashCode(String on) {
		return (long) on.hashCode();
	}

//	public static boolean startsWith(String on, Pattern pattern, long index) {
//		return on.startsWith(pattern.pattern(), (int) (long) index);
//	}

	public static boolean startsWith(String on, String pattern, long index) {
    return on.startsWith(pattern, (int) index);
	}

//	public static long indexOf(String on, Pattern pattern, long start) {
//		return (long) on.indexOf(pattern.pattern(), (int) (long) start);
//	}

//	public static long lastIndexOf(String on, Pattern pattern, long start) {
//		return (long) on.lastIndexOf(pattern.pattern(), (int) (long) start);
//	}

	public static long lastIndexOf(String on, String what, long start) {
		return (long) on.lastIndexOf(what, (int) (long) start);
	}

	public static boolean getIsEmpty(String on) {
		return on.isEmpty();
	}

	public static boolean getIsNotEmpty(String on) {
		return !on.isEmpty();
	}

	public static String plus(String on, Object other) {
		return new String(on + other);
	}

	public static String trimLeft(String on) {
		int i = 0;
		while (i < on.length() && Character.isWhitespace(on.charAt(i))) {
			i++;
		}
		return on.substring(i);
	}

	public static String trimRight(String on) {
		int i = on.length() - 1;
		while (i >= 0 && Character.isWhitespace(on.charAt(i))) {
			i--;
		}
		return on.substring(i);
	}

	public static String times(String on, long times) {
		StringBuilder sb = new StringBuilder(on);
		for (int i = 0; i < times; i++)
			sb.append(on);
		return sb.toString();
	}

	public static String padLeft(String on, long width, String padding) {
		return pad(on, (int) (long) width, padding, true);
	}

	public static String padRight(String on, long width, String padding) {
		return pad(on, (int) (long) width, padding, false);
	}

	/**
	 * Helper method for padding.
	 * 
	 * @param on
	 * @param width
	 * @param padding
	 * @param left
	 * @return
	 */
	private static String pad(String on, int width, String padding, boolean left) {
		// Validations
		if (width < 0)
			width = 0;

		if (width <= getLength(on))
			return on;

		// Get Java lang String versions.
		String padString = padding;

		// Append the padding and then the String.
		StringBuilder newSb = new StringBuilder(on);
		int diff = (int) (width - getLength(on));

		if (left) {
			// If left, pad first.
			while (diff > 0) {
				newSb.append(padString);
				diff--;
			}
		}
		// Insert the string.
		newSb.append(on);
		if (!left) {
			// If right (which is not left), pad later.
			while (diff > 0) {
				newSb.append(padString);
				diff--;
			}
		}

		return newSb.toString();
	}

	public static boolean contains(String on, String other, long startIndex) {
    int index = (int) startIndex;
		return Pattern.compile(other).matcher(on.substring(index)).find();
	}

	public static String replaceFirst(String on, RegExp from, String to, long startIndex) {
		String fromStart = on.substring((int) (long) startIndex);
		return fromStart.replaceFirst(from.pattern(), to);
	}
	
	public static String replaceFirst(String on, Pattern from, String to, long startIndex) {
		String fromStart = on.substring((int) (long) startIndex);
		return fromStart.replaceFirst(from.pattern(), to);
	}

	public static String replaceFirstMapped(String on, Pattern from, Function<Match, String> replace, long startIndex) {
		// TODO
		return null;
	}

	public static String replaceAll(String on, Pattern from, String replace) {
		return on.replaceAll(from.pattern(), replace);
	}

	public static String replaceAll(String on, String from, String replace) {
		return on.replaceAll(from, replace);
	}

	public static String replaceAllMapped(String on, Pattern from, Function<Match, String> replace) {
		// TODO
		return null;
	}

	public static String replaceRange(String on, long start, long end, String replacement) {
		return on.substring(0, (int) (long) start) + replacement + on.substring((int) (long) end);
	}

	public static List<String> split(String on, Pattern pattern) {
		return Arrays.asList(on.split(pattern.pattern()));
	}

	public static String join(Iterable<?> what, String with) {
		return String.join(with, Streams.stream(what).map(a -> a == null ? null : a.toString())
				.map(a -> a == null ? "null" : a).collect(Collectors.toList()));
	}

	/**
	 * TODO: Implement this. Need to split by regex and then match.
	 */
	public static String splitMapJoin(String on, Pattern pattern, @Param("onMatch") Function<Match, String> onMatch,
			@Param("onNonMatch") Function<Match, String> onNonMatch) {
		for (String piece : on.split(pattern.pattern())) {

		}
		// TODO
		return null;
	}

	public static Iterable<Match> allMatches(String on, String string, long start) {
		// TODO
		return null;
	}

	public static Match matchAsPrefix(String on, String string, long start) {
		// TODO
		return null;
	}

	public static String join(List<?> what, String with) {
		return join((Iterable<?>) what, with);
	}

	/**
	 * Code units are treated as the integer equivalent of the
	 * 
	 * @param on
	 * @return
	 */
  public static List<Long> getCodeUnits(String on) {
    List<Long> codeUnits = new ArrayList<>();
		for (int i = 0; i < on.length(); i++) {
			codeUnits.add((long) Character.getNumericValue(on.charAt(i)));
		}
		return codeUnits;
	}

	/**
	 * As per the implementation in Dart, Runes are long code points. So, we can
	 * return an object of type Runes that has an iterator of Integers.
	 * 
	 * @param on
	 * @return
	 */
	public static Runes getRunes(String on) {
		return new Runes(on);
	}

	public static long indexOf(String lookIn, String lookFor, long fromIndex) {
	  	return lookIn.indexOf(lookFor, (int) fromIndex);
	}
}

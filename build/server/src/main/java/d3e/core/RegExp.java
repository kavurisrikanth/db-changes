package d3e.core;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {
	private Pattern exp;
	private boolean caseSensitive;
	private boolean multiLine;

	public RegExp(String source, @Param("caseSensitive") boolean caseSensitive, @Param("multiLine") boolean multiLine) {
		this.caseSensitive = caseSensitive;
		this.multiLine = multiLine;
		this.exp = Pattern.compile(source);
	}

	public static String escape(String text) {
		// TODO
		return null;
	}

	public Match firstMatch(String input) {
		// TODO
		return null;
	}

	public Iterable<Match> allMatches(String input) {
		return allMatches(input, 0l);
	}

	public Iterable<Match> allMatches(String input, long start) {
		Matcher matcher = exp.matcher(input);
		return new Iterable<Match>() {

			@Override
			public Iterator<Match> iterator() {
				return new Iterator<Match>() {

					@Override
					public Match next() {
						return new Match(matcher, input);
					}

					@Override
					public boolean hasNext() {
						return matcher.find();
					}
				};
			}
		};
	}

	public boolean hasMatch(String input) {
		return exp.matcher(input).matches();
	}

	public String stringMatch(String input) {
		return exp.matcher(input).group();
	}

	public String pattern() {
		return exp.pattern();
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}
}

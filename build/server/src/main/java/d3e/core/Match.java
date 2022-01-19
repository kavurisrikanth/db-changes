package d3e.core;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Match {

	private Matcher matcher;
	private String input;

	public Match(Matcher matcher, String input) {
		this.matcher = matcher;
		this.input = input;
	}

	public long getStart() {
		return (long) matcher.start();
	}

	public long getEnd() {
		return (long) matcher.end();
	}

	public String group(long group) {
    return matcher.group((int) group);
	}

	public String get(long group) {
    return matcher.group((int) group);
	}

  public List<String> groups(List<Long> groupIndices) {
		return groupIndices.stream().map(this::group).collect(Collectors.toList());
	}

	public Integer getGroupCount() {
		return matcher.groupCount();
	}

	public String getInput() {
		return input;
	}

	public Pattern getPattern() {
		return matcher.pattern();
	}

}

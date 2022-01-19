package d3e.core;

public class ComparableExt {

	public static <R> int compare(Comparable<R> a, Comparable<R> b) {
		return a.compareTo((R) b);
	}
}

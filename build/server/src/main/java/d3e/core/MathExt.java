package d3e.core;

public class MathExt {
	static final double e = Math.E;
	static final double ln2 = 0.6931471805599453;
	static final double ln10 = 2.302585092994046;
	static final double log2e = 1.4426950408889634;
	static final double log10e = 0.4342944819032518;
	static final double pi = Math.PI;
	static final double sqrt1_2 = 0.7071067811865476;
	static final double sqrt2 = 1.4142135623730951;

	static int max(int a, int b) {
		return Math.max(a, b);
	}

	static int min(int a, int b) {
		return Math.min(a, b);
	}

	public static int maxInt(long a, long b) {
		return max((int) a, (int) b);
	}
	  
	public static int minInt(long a, long b) {
		return min((int) a, (int) b);
	}
}

package gqltosql2;

public class AliasGenerator {

	private static char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private int i;

	private String prefix = "";

	private AliasGenerator pre;

	public AliasGenerator() {
	}

	public String next() {
		if (i == chars.length) {
			i = 0;
			if (pre == null) {
				pre = new AliasGenerator();
			}
			prefix = pre.next();
		}
		return prefix + chars[i++];
	}
}

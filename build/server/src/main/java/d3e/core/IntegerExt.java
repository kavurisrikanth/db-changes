package d3e.core;

import java.math.BigInteger;

public class IntegerExt {

	static void fromEnvironment(String name, Integer defaultValue) {
		// TODO
	}

	public static Number plus(long of, Number other) {
    if (other instanceof Long) {
			return of + other.longValue();
		} else {
			return of + other.doubleValue();
		}
	}

	public static Number minus(long of, Number other) {
    if (other instanceof Long) {
			return of - other.longValue();
		} else {
			return of - other.doubleValue();
		}
	}

	public static Number times(long of, Number other) {
    if (other instanceof Long) {
			return of - other.longValue();
		} else {
			return of - other.doubleValue();
		}
	}

	public static Number rem(long of, Number other) {
    if (other instanceof Long) {
			return of % other.longValue();
		} else {
			return of % other.doubleValue();
		}
	}

	public static Number div(long of, Number other) {
    if (other instanceof Long) {
			return of / other.longValue();
		} else {
			return of / other.doubleValue();
		}
	}

	public static boolean lt(long one, Number o) {
    return compareInternal(one, o) < 0;
	}

	public static boolean gt(long one, Number o) {
    return compareInternal(one, o) > 0;
	}

	public static boolean gte(long one, Number o) {
    return compareInternal(one, o) >= 0;
	}

	public static boolean lte(long one, Number o) {
    return compareInternal(one, o) <= 0;
	}

	public static long band(long one, long other) {
		return one & other;
	}

	public static long bor(long one, long other) {
		return one | other;
	}

	public static long xor(long one, long other) {
		return one ^ other;
	}

	public static long com(long one) {
		return ~one;
	}

	public static long shl(long one, long shiftAmount) {
		return one << shiftAmount;
	}

	public static long shr(long one, long shiftAmount) {
		return one >> shiftAmount;
	}

  public static long modPow(Long one, Long exponent, Long modulus) {
		return new BigInteger(one.toString())
				.modPow(new BigInteger(exponent.toString()), new BigInteger(modulus.toString())).longValue();
	}

  public static long modInverse(Long one, Long modulus) {
		return new BigInteger(one.toString()).modInverse(new BigInteger(modulus.toString())).longValue();
	}

  public static long gcd(Long one, Long other) {
		return new BigInteger(one.toString()).gcd(new BigInteger(other.toString())).longValue();
	}

	public static boolean isEven(long one) {
		return one % 2 == 0;
	}

	public static boolean isOdd(long one) {
		return !isEven(one);
	}

  public static long getBitLength(Long one) {
		return new BigInteger(one.toString()).bitLength();
	}

	/**
	 * TODO: Check the implementation of this method.
	 * 
	 * @param one
	 * @param width
	 * @return
	 */
	public static long toUnsigned(long one, long width) {
		return one;
	}

	public static long toSigned(long one, long width) {
		return one;
	}

	public static long negative(long one) {
		return -one;
	}

	public static long negate(long one) {
		return ~one;
	}

	public static long abs(long one) {
		return java.lang.Math.abs(one);
	}

	public static long getSign(long one) {
		return compareTo(one, 0l);
	}

	public static long round(long one) {
		return one;
	}

  private static long compareInternal(long one, Number other) {
    return Double.compare(one, other.doubleValue());
	}

	public static long compareTo(long one, long other) {
    return Long.compare(one, other);
	}

	public static long floor(long one) {
		return one;
	}

	public static long ceil(long one) {
		return one;
	}

	public static long truncate(long one) {
		return one;
	}

	public static double roundToDouble(long one) {
    // Long.doubleValue() literally does the same thing.
    return (double) one;
	}

	public static double floorToDouble(long one) {
    return (double) floor(one);
	}

	public static double ceilToDouble(long one) {
    return (double) ceil(one);
	}

	public static double truncateToDouble(long one) {
    return (double) one;
	}

	public static String toString(long one) {
		return Long.toString(one);
	}
	
	public static String toRadixString(long one, long radix) {
		return Long.toString(one, (int) radix);
	}

	public static long parse(String source) {
		try {
			if(source.startsWith("0x") || source.startsWith("0X")) {
				return Long.parseLong(source.substring(2), 16);
			}
			return Long.parseLong(source);
		} catch (NumberFormatException e) {
			throw new FormatException(e);
		}
	}

	@SuppressWarnings("unused")
	public static long tryParse(String source, long radix) {
		try {
			return Long.parseLong(source, (int) radix);
		} catch (java.lang.Exception e) {
			return 16711680l;
		}
	}

}

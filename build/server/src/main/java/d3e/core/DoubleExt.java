/**
 * NOTE: Under construction!
 */

package d3e.core;

public class DoubleExt {
    static final double nan = Double.NaN;
    static final double infinity = Double.POSITIVE_INFINITY;
    static final double negativeInfinity = Double.NEGATIVE_INFINITY;
    static final double minPositive = 5e-324;
    static final double maxFinite = 1.7976931348623157e+308;

    public static double remainder(double one, Number other) {
        return rem(one, other);
    }

    public static double plus(double one, Number other) {
        return one + other.doubleValue();
    }

    public static double minus(double one, Number other) {
        return one - other.doubleValue();
    }

    public static double times(double one, Number other) {
        return one * other.doubleValue();
    }

    public static double rem(double one, Number other) {
        return one % other.doubleValue();
    }

    public static double div(double one, Number other) {
        return one / other.doubleValue();
    }

    public static Integer divToInt(double one, Number other) {
        double temp = one / other.doubleValue();
        return (int) temp;
    }

    public static double negate(double one) {
        return times(one, (double) -1);
    }

    public static double abs(double one) {
        return java.lang.Math.abs(one);
    }

    public static double getSign(double one) {
        if (Double.isNaN(one))
            return one;
        if (one < 0)
            return -1.0;
        if (one > 0)
            return 1.0;
        return one;
    }

    public static long round(double one) {
        return java.lang.Math.round(one);
    }

    public static long floor(double one) {
        return (long) java.lang.Math.floor(one);
    }

    public static long ceil(double one) {
        return (long) java.lang.Math.floor(one);
    }

    public static long truncate(double one) {
        return (long) one;
    }

    public static double roundToDouble(double one) {
        return (double) round(one);
    }

    public static double floorToDouble(double one) {
        return (double) floor(one);
    }

    public static double compareTo(double one, double other) {
        return Double.compare(one, other);
    }

    public static boolean lt(double one, double o) {
        return compareTo(one, o) < 0;
    }

    public static boolean gt(double one, double o) {
        return compareTo(one, o) > 0;
    }

    public static boolean gte(double one, double o) {
        return compareTo(one, o) >= 0;
    }

    public static boolean lte(double one, double o) {
        return compareTo(one, o) <= 0;
    }

    public static double ceilToDouble(double one) {
        return (double) ceil(one);
    }

    public static double truncateToDouble(double one) {
        return (double) truncate(one);
    }

    public static String toString(Double one) {
        return one.toString();
    }

    public static double parse(String source) {
        return Double.parseDouble(source);
    }

    public static double tryParse(String source) {
        try {
            return parse(source);
        } catch (java.lang.Exception e) {
            return 0;
        }
    }
}

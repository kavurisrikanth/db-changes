package d3e.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.text.ParseException;
import d3e.core.StringExt;

/**
 * Floating point number. See num in dart:core.
 */
 
public class NumberExt {
//	java.lang.Number number;
    public static boolean equals(Number of, Object other) {
        return of.equals(other);
	}


    public static long getHashCode(Number of) {
        return (long)of.hashCode();
	}


    public static long compareTo(Number of, Number other) {
        return (long)new BigDecimal(of.toString()).compareTo(new BigDecimal(other.toString()));
	}

    /** Addition operator. */
    public static Number plus(Number of, Number other) {
        return new BigDecimal(of.toString()).add(new BigDecimal(other.toString()));
	}

    /** Subtraction operator. */
    public static Number minus(Number of, Number other) {
        return new BigDecimal(of.toString()).subtract(new BigDecimal(other.toString()));
	}

    /** Multiplication operator. */
    public static Number times(Number of, Number other) {
        return new BigDecimal(of.toString()).multiply(new BigDecimal(other.toString()));
	}

    /**
     * Euclidean modulo operator.
     *
     * Returns the remainder of the Euclidean division. The Euclidean division of
     * two integers `a` and `b` yields two integers `q` and `r` such that
     * `a == b * q + r` and `0 <= r < b.abs()`.
     *
     * The Euclidean division is only defined for integers, but can be easily
     * extended to work with doubles. In that case `r` may have a non-integer
     * value, but it still verifies `0 <= r < |b|`.
     *
     * The sign of the returned value `r` is always positive.
     *
     * See [remainder] for the remainder of the truncating division.
     */
    public static Number rem(Number of, Number other) {
        return new BigDecimal(of.toString()).remainder(new BigDecimal(other.toString()));
	}

    /** Division operator. */
    public static double div(Number of, Number other) {
        return (new BigDecimal(of.toString()).divide(new BigDecimal(other.toString()))).doubleValue();
	}

    /**
     * Truncating division operator.
     *
     * If either operand is a [double] then the result of the truncating division
     * `a ~/ b` is equivalent to `(a / b).truncate().toInt()`.
     *
     * If both operands are [long]s then `a ~/ b` performs the truncating
     * integer division.
     */
    public static long divToInt(Number of, Number other) {
        return (new BigDecimal(of.toString()).divide(new BigDecimal(other.toString()))).longValue();
	}

    /** Negate operator. */
    public static Number minus(Number of) {
        return new BigDecimal(of.toString()).negate();
	}

    /**
     * Returns the remainder of the truncating division of `this` by [other].
     *
     * The result `r` of this operation satisfies:
     * `this == (this ~/ other) * other + r`.
     * As a consequence the remainder `r` has the same sign as the divider `this`.
     */
    public static Number remainder(Number of, Number other) {
        return rem(of, other);
	}
   
    /** True if the number is the double Not-a-Number value; otherwise, false. */
    public static boolean getIsNaN(Number of) {
        if (!(of instanceof  Double)) return false;
        return Double.isNaN((double) of);
	}

    /**
     * True if the number is negative; otherwise, false.
     *
     * Negative numbers are those less than zero, and the double `-0.0`.
     */
    public static boolean getIsNegative(Number of) {
        return new BigDecimal(of.toString()).compareTo(BigDecimal.ZERO) < 0;
	}

    /**
     * True if the number is positive infinity or negative infinity; otherwise,
     * false.
     */
    public static boolean getIsInfinite(Number of) {
        return of.equals(Double.POSITIVE_INFINITY);
	}

    /**
     * True if the number is finite; otherwise, false.
     *
     * The only non-finite numbers are NaN, positive infinity, and
     * negative infinity.
     */
    public static boolean isFinite(Number of) {
        return !getIsInfinite(of);
	}

    /** Returns the absolute value of this [Number]. */
    public static Number abs(Number of) {
        return new BigDecimal(of.toString()).abs();
	}

    /**
     * Returns minus one, zero or plus one depending on the sign and
     * numerical value of the number.
     *
     * Returns minus one if the number is less than zero,
     * plus one if the number is greater than zero,
     * and zero if the number is equal to zero.
     *
     * Returns NaN if the number is the double NaN value.
     *
     * Returns a number of the same type as this number.
     * For doubles, `-0.0.sign == -0.0`.

     * The result satisfies:
     *
     *     n == n.sign * n.abs()
     *
     * for all numbers `n` (except NaN, because NaN isn't `==` to itself).
     */
    public static Number getSign(Number of) {
        return compareTo(of, BigDecimal.ZERO);
	}

    /**
     * Returns the integer closest to `this`.
     *
     * Rounds away from zero when there is no closest integer:
     *  `(3.5).round() == 4` and `(-3.5).round() == -4`.
     *
     * If `this` is not finite (`NaN` or infinity), throws an [UnsupportedError].
     */
    public static long round(Number of) {
        return new BigDecimal(of.toString()).round(MathContext.UNLIMITED).longValue();
	}

    /**
     * Returns the greatest integer no greater than `this`.
     *
     * If `this` is not finite (`NaN` or infinity), throws an [UnsupportedError].
     */
    public static long floor(Number of) {
        return (long)java.lang.Math.floor((double) of);
	}

    /**
     * Returns the least integer no smaller than `this`.
     *
     * If `this` is not finite (`NaN` or infinity), throws an [UnsupportedError].
     */
    public static long ceil(Number of) {
        return (long) java.lang.Math.ceil((double) of);
	}

    /**
     * Returns the integer obtained by discarding any fractional
     * digits from `this`.
     *
     * If `this` is not finite (`NaN` or infinity), throws an [UnsupportedError].
     */
    public static long truncate(Number of) {
        return (long)of;
	}


    public static double roundToDouble(Number of) {
        return java.lang.Math.floor((double) of);
	}

    /**
     * Returns the greatest double integer value no greater than `this`.
     *
     * If this is already an integer valued double, including `-0.0`, or it is a
     * non-finite double value, the value is returned unmodified.
     *
     * For the purpose of rounding, `-0.0` is considered to be below `0.0`.
     * A number `d` in the range `0.0 < d < 1.0` will return `0.0`.
     *
     * The result is always a double.
     * If this is a numerically large integer, the result may be an infinite
     * double.
     */
    public static double floorToDouble(Number of) {
        return java.lang.Math.floor((double) of);
	}

    /**
     * Returns the least double integer value no smaller than `this`.
     *
     * If this is already an integer valued double, including `-0.0`, or it is a
     * non-finite double value, the value is returned unmodified.
     *
     * For the purpose of rounding, `-0.0` is considered to be below `0.0`.
     * A number `d` in the range `-1.0 < d < 0.0` will return `-0.0`.
     *
     * The result is always a double.
     * If this is a numerically large integer, the result may be an infinite
     * double.
     */
    public static double ceilToDouble(Number of) {
        return java.lang.Math.ceil((double) of);
	}


    public static double truncateToDouble(Number of) {
        return (double) truncate(of);
	}

    /**
     * Returns this [Number] clamped to be in the range [lowerLimit]-[upperLimit].
     *
     * The comparison is done using [compareTo] and therefore takes `-0.0` into
     * account. This also implies that [double.nan] is treated as the maximal
     * double value.
     *
     * The arguments [lowerLimit] and [upperLimit] must form a valid range where
     * `lowerLimit.compareTo(upperLimit) <= 0`.
     */
    public static Number clamp(Number of, Number lowerLimit, Number upperLimit) {
        if (compareTo(of, lowerLimit) < 0) return lowerLimit;
        if (compareTo(of, upperLimit) > 0) return upperLimit;
        return of;
	}

    /** Truncates this [Number] to an integer and returns the result as an [long]. */
    public static long toInt(Number of) {
        return (long) of;
	}

    /**
     * Return this [Number] as a [double].
     *
     * If the number is not representable as a [double], an
     * approximation is returned. For numerically large integers, the
     * approximation may be infinite.
     */
    public static double toDouble(Number of) {
        return (double) of;
	}

    /**
     * Returning toString value as is for the next 3 methods.
     * TODO: Check proper implementation.
     */
    public static String toStringAsFixed(Number of, long fractionDigits) {
        return toString(of);
	}


    public static String toStringAsExponential(Number of, long fractionDigits) {
        return toString(of);
	}

    public static String toStringAsPrecision(Number of, long precision) {
        return toString(of);
	}

    public static String toString(Number of) {
        return of.toString();
	}

    public static Number parse(String input) throws ParseException {
        return NumberFormat.getInstance().parse(input);
	}

    public static Number tryParse(String input) {
        try {
            return parse(input);
        } catch (ParseException pe) {
            return null;
        }
	}
    
}

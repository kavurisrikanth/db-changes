package d3e.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class DecimalExt {

  public static BigDecimal parse(String value) {
    return new BigDecimal(value);
  }

//  public static BigDecimal parse(String value, RoundingMode rm) {
//    BigDecimal decimal = new BigDecimal(value);
//    decimal.setScale(decimal.scale(), rm);
//    return decimal;
//  }

  public static BigDecimal fromInt(long value) {
    return new BigDecimal(value);
  }

//  public static BigDecimal fromInt(long value, RoundingMode rm) {
//    BigDecimal decimal = new BigDecimal(value);
//    // A somewhat roundabout way to set the RoundingMode.
//    // TODO: Confirm it's correct.
//    decimal.setScale(decimal.scale(), rm);
//    return decimal;
//  }

  public static BigDecimal tryParse(String value) {
    try {
      return parse(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

//  public static BigDecimal tryParse(String value, RoundingMode rm) {
//    BigDecimal decimal = tryParse(value);
//    if (decimal == null) {
//      return null;
//    }
//    decimal.setScale(decimal.scale(), rm);
//    return decimal;
//  }

  public static BigDecimal add(BigDecimal of, BigDecimal other) {
    return of.add(other);
  }

  public static BigDecimal subtract(BigDecimal of, BigDecimal other) {
    return of.subtract(other);
  }

  public static BigDecimal multiply(BigDecimal of, BigDecimal other) {
    return of.multiply(other);
  }

  public static BigDecimal divide(BigDecimal of, BigDecimal other) {
    return of.divide(other);
  }

  public static BigDecimal divideTrunc(BigDecimal of, BigDecimal other) {
    return of.divideToIntegralValue(other);
  }

  public static BigDecimal divide(BigDecimal of, BigDecimal other, RoundingMode rm) {
    MathContext mc = new MathContext(of.precision(), rm);
    return of.divide(other, mc);
  }

  public static BigDecimal divideTrunc(BigDecimal of, BigDecimal other, RoundingMode rm) {
    MathContext mc = new MathContext(of.precision(), rm);
    return of.divideToIntegralValue(other, mc);
  }

  public static BigDecimal rem(BigDecimal of, BigDecimal other) {
    return of.remainder(other);
  }

  public static BigDecimal rem(BigDecimal of, BigDecimal other, RoundingMode rm) {
    MathContext mc = new MathContext(of.precision(), rm);
    return of.remainder(other, mc);
  }

  public static BigDecimal remainder(BigDecimal of, BigDecimal other) {
    return rem(of, other);
  }

  public static BigDecimal remainder(BigDecimal of, BigDecimal other, RoundingMode rm) {
    return rem(of, other, rm);
  }

  public static BigDecimal neg(BigDecimal of) {
    return of.negate();
  }

  public static boolean equals(BigDecimal of, BigDecimal other) {
    return of.compareTo(other) == 0;
  }

  public static boolean lt(BigDecimal of, BigDecimal other) {
    return of.compareTo(other) < 0;
  }

  public static boolean gt(BigDecimal of, BigDecimal other) {
    return of.compareTo(other) > 0;
  }

  public static boolean lte(BigDecimal of, BigDecimal other) {
    return of.compareTo(other) <= 0;
  }

  public static boolean gte(BigDecimal of, BigDecimal other) {
    return of.compareTo(other) >= 0;
  }

  public static BigDecimal abs(BigDecimal of) {
    return of.abs();
  }

//  public static BigDecimal abs(BigDecimal of, RoundingMode rm) {
//    MathContext mc = new MathContext(of.precision(), rm);
//    return of.abs(mc);
//  }

  public static BigDecimal pow(BigDecimal of, long exponent) {
    return pow(of, exponent, RoundingMode.UP);
  }

  public static BigDecimal pow(BigDecimal of, long exponent, RoundingMode rm) {
    MathContext mc = new MathContext(of.precision(), rm);
    return of.pow((int) exponent, mc);
  }

  public static BigDecimal floor(BigDecimal of) {
    return of.round(new MathContext(of.precision(), RoundingMode.FLOOR));
  }

  public static double floorToDouble(BigDecimal of) {
    return floor(of).doubleValue();
  }

  public static BigDecimal ceil(BigDecimal of) {
    return of.round(new MathContext(of.precision(), RoundingMode.CEILING));
  }

  public static double ceilToDouble(BigDecimal of) {
    return ceil(of).doubleValue();
  }

  public static BigDecimal round(BigDecimal of) {
    return round(of, RoundingMode.UP);
  }

  public static BigDecimal round(BigDecimal of, RoundingMode rm) {
    return of.round(new MathContext(of.precision(), rm));
  }

  public static double roundToDouble(BigDecimal of) {
    return round(of).doubleValue();
  }

  public static double roundToDouble(BigDecimal of, RoundingMode rm) {
    return round(of, rm).doubleValue();
  }

  public static BigDecimal truncate(BigDecimal of) {
    return new BigDecimal(of.intValue());
  }

  public static double truncateToDouble(BigDecimal of) {
    return truncate(of).doubleValue();
  }

  public static BigDecimal clamp(BigDecimal of, BigDecimal lowerLimit, BigDecimal upperLimit) {
    // TODO
    if (of.compareTo(lowerLimit) < 0) {
      return lowerLimit;
    }

    if (of.compareTo(upperLimit) > 0) {
      return upperLimit;
    }

    return of;
  }

  public static boolean isInteger(BigDecimal of) {
    // TODO: Check if this is the correct way to do this.
    try {
      long value = of.intValueExact();
      return true;
    } catch (ArithmeticException e) {
      return false;
    }
  }

  public static BigDecimal getInverse(BigDecimal of) {
    // Multiplicative inverse
    return BigDecimal.ONE.divide(of);
  }

  public static long getHashCode(BigDecimal of) {
    return (long) of.hashCode();
  }

  public static boolean isNaN(BigDecimal of) {
    Double value = of.doubleValue();
    return value.equals(Double.NaN);
  }

  public static boolean isNegative(BigDecimal of) {
    return of.signum() < 0;
  }

  public static boolean isInfinite(BigDecimal of) {
    Double value = of.doubleValue();
    return value.equals(Double.POSITIVE_INFINITY) || value.equals(Double.NEGATIVE_INFINITY);
  }

/// The signum function value of this [num].
///
/// E.e. -1, 0 or 1 as the value of this [num] is negative, zero or positive.
  public static long getSignum(BigDecimal of) {
    return of.signum();
  }

/// Truncates this [num] to an integer and returns the result as an [long].
  public static long toInt(BigDecimal of) {
    return of.longValue();
  }

/// Return this [num] as a [double].
///
/// If the number is not representable as a [double], an approximation is
/// returned. For numerically large integers, the approximation may be
/// infinite.
  public static double toDouble(BigDecimal of) {
    return of.doubleValue();
  }

/// Inspect if this [num] has a finite precision.
  public static boolean getHasFinitePrecision(BigDecimal of) {
    // TODO
    return true;
  }

/// The precision of this [num].
///
/// The sum of the number of digits before and after the BigDecimal point.
///
/// Throws [StateError] if the precision is infinite, i.e. when
/// [hasFinitePrecision] is `false`.
  public static long getPrecision(BigDecimal of) {
    return of.precision();
  }

/// The scale of this [num].
///
/// The number of digits after the BigDecimal point.
///
/// Throws [StateError] if the scale is infinite, i.e. when
/// [hasFinitePrecision] is `false`.
  public static long getScale(BigDecimal of) {
    return of.scale();
  }

/// Converts a [num] to a string representation with [fractionDigits] digits
/// after the BigDecimal point.
  public static String toStringAsFixed(BigDecimal of, long fractionDigits) {
    BigDecimal copy = new BigDecimal(of.unscaledValue(), of.scale());
    copy.setScale((int) fractionDigits);
    return copy.toString();
  }

/// Converts a [num] to a string in BigDecimal exponential notation with
/// [fractionDigits] digits after the BigDecimal point.
  public static String toStringAsExponential(BigDecimal of) {
    return of.toEngineeringString();
  }

  public static String toStringAsExponential(BigDecimal of, long fractionDigits) {
    return of.toEngineeringString();
  }

/// Converts a [num] to a string representation with [precision] significant
/// digits.
  public static String toStringAsPrecision(BigDecimal of, long precision) {
    // TODO: is this correct?
    return toStringAsFixed(of, precision);
  }

}

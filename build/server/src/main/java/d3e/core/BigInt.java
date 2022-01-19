package d3e.core;

import java.math.BigInteger;

public class BigInt extends BigInteger {

	private BigInt() {
		super("10");
	}

	private BigInt(String num) {
		super(num);
	}

	private BigInt(String num, long radix) {
		super(num, (int) (long) radix);
	}

	public static BigInt getZero() {
		return new BigInt(BigInt.ZERO.toString());
	}

	public static BigInt getOne() {
		return new BigInt(BigInt.ONE.toString());
	}

	public static BigInt getTwo() {
		return new BigInt(BigInt.TWO.toString());
	}

	public static BigInt parse(String source, long radix) {
		return new BigInt(source, radix);
	}

	public static BigInt tryParse(String source, long radix) {
		try {
			return parse(source, radix);
		} catch (java.lang.Exception e) {
			return null;
		}
	}

	public static BigInt from(Number value) {
		return new BigInt(value.toString());
	}

	public static BigInt abs(BigInt of) {
		return (BigInt) of.abs();
	}

	public BigInt minus() {
		return (BigInt) negate();
	}

	public BigInt plus(BigInt other) {
		return (BigInt) add(other);
	}

	public BigInt minus(BigInt of) {
		return (BigInt) this.subtract(of);
	}

	public BigInt times(BigInt other) {
		return (BigInt) this.multiply(other);
	}

	public double div(BigInt other) {
		return this.divide(other).doubleValue();
	}

	public BigInt divToInt(BigInt other) {
		return new BigInt(Integer.toString(this.divide(other).intValue()));
	}

	public BigInt rem(BigInt other) {
		return (BigInt) this.remainder(other);
	}

	public BigInt remainder(BigInt other) {
		return this.rem(other);
	}

	public BigInt shl(long shiftAmount) {
		return (BigInt) this.shiftLeft((int) (long) shiftAmount);
	}

	public BigInt shr(long shiftAmount) {
		return (BigInt) this.shiftRight((int) (long) shiftAmount);
	}

	public BigInt band(BigInt other) {
		return (BigInt) and(other);
	}

	public BigInt bor(BigInt other) {
		return (BigInt) or(other);
	}

	public BigInt xor(BigInt of, BigInt other) {
		return (BigInt) of.xor(other);
	}

	public long compareTo(BigInt of, BigInt other) {
		return (long) of.compareTo(other);
	}

	public long getBitLength(BigInt of) {
		return (long) of.bitLength();
	}

	public Integer sign() {

		return this.signum();
	}

	public boolean isEven() {
		return this.mod(BigInt.TWO).compareTo(BigInt.ZERO) == 0;
	}

	public boolean isOdd() {
		return !isEven();
	}

	public boolean isNegative() {
		return sign() < 0;
	}

	public BigInt pow(BigInt of, Integer exponent) {
		return (BigInt) of.pow(exponent);
	}

	public BigInt modPow(BigInt of, BigInt exponent, BigInt modulus) {
		return (BigInt) of.modPow(exponent, modulus);
	}

	public BigInt modInverse(BigInt of, BigInt modulus) {
		return (BigInt) of.modInverse(modulus);
	}

	public BigInt gcd(BigInt of, BigInt other) {
		return (BigInt) of.gcd(other);
	}

	public BigInt toUnsigned(Integer width) {
		return this.toUnsigned(width);

	}

	public BigInt toSigned(Integer width) {
		return this.toSigned(width);
	}

	public boolean isValidInt() {
//		  	if(of==BigInt.valueOf(of.intValue()))
//		  		return true;
//		  	else 
		// TODO
		return false;
	}

	public long toInt() {
    return this.intValue();
	}

	public double toDouble() {
		// TODO
    return this.doubleValue();
	}

	public static String toString(BigInt of) {
		return of.toString();
	}

	public String toRadixString(Integer radix) {
		return toString(radix);
	}

	public BigInt negative() {
		// TODO
		return null;
	}

	public BigInt com() {
		// TODO
		return null;
	}
}

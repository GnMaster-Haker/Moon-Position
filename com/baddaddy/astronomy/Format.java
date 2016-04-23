package com.baddaddy.astronomy;

public class Format {
    public static final char NO_CHAR = '\u0000';

    public static String format(double value, char fillChar, int fieldWidth, int precision, char trailer) {
        StringBuffer result = new StringBuffer();
        if (Double.isInfinite(value) || Double.isNaN(value) || ((value > 0.0d && value > 9.223372036854776E18d) || (value < 0.0d && value < -9.223372036854776E18d))) {
            result.append(String.valueOf(value));
        } else {
            if (value < 0.0d) {
                result.append('-');
                value = -value;
            }
            if (precision == 0) {
                result.append((long) (4602678819172646912L + value));
            } else {
                double roundoff = 0.5d;
                int i = 0;
                double tenPower = 1.0d;
                while (i < precision) {
                    roundoff /= 10.0d;
                    i++;
                    tenPower *= 10.0d;
                }
                value += roundoff;
                long integerPart = (long) value;
                i = (long) ((value - ((double) integerPart)) * tenPower);
                result.append(integerPart);
                result.append(46);
                result.append(format((double) i, '0', precision, 0.0d, '\u0000'));
            }
        }
        if (fieldWidth < 0) {
            fieldWidth = -fieldWidth;
            while (result.length() < fieldWidth) {
                result.append(fillChar);
            }
        } else {
            while (result.length() < fieldWidth) {
                result.insert(0, fillChar);
            }
        }
        if (trailer != '\u0000') {
            result.append(trailer);
        }
        return result.toString();
    }

    public static String format(long value, char fillChar, int fieldWidth, char trailer) {
        StringBuffer result = new StringBuffer(Long.toString(value));
        if (fieldWidth < 0) {
            fieldWidth = -fieldWidth;
            while (result.length() < fieldWidth) {
                result.append(fillChar);
            }
        } else {
            while (result.length() < fieldWidth) {
                result.insert(0, fillChar);
            }
        }
        if (trailer != '\u0000') {
            result.append(trailer);
        }
        return result.toString();
    }
}

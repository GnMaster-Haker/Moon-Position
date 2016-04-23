package com.baddaddy.astronomy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class AstroFormat implements AstroConstants {
    public static final boolean ASTRO_FORMAT = false;
    public static final boolean CIVIL_FORMAT = true;
    private static final String DMY = "EEE, dd MMM yyyy";
    private static final String MDY = "EEE, MMM dd, yyyy";
    private static final String YMD = "yyyy MMM dd, EEE";
    private static final char degreeSign = '\u00b0';
    private static final char minuteSign = '\'';
    private static final char secondSign = '\"';

    public static String getLocalDateString(Date instant) {
        return DateFormat.getDateTimeInstance().format(instant);
    }

    public static String getLocalDateString(Date instant, TimeZone timeZone) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(instant);
    }

    public static String getGMTDateString(Date instant) {
        try {
            SimpleTimeZone tzGMT = (SimpleTimeZone) TimeZone.getTimeZone("GMT");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm zz");
            dateFormat.setTimeZone(tzGMT);
            return dateFormat.format(instant);
        } catch (Exception e) {
            return "";
        }
    }

    public static double dms(int degree, int minute, int second) {
        return hms(degree, minute, second);
    }

    public static double hms(int hour, int minute, int second) {
        return hms(hour, minute, (double) second);
    }

    public static double hms(int hour, int minute, double second) {
        return (((double) hour) + (((double) minute) / 60.0d)) + (second / 3600.0d);
    }

    public static String dm(double value, String tag) {
        StringBuffer text = new StringBuffer(dm(Math.abs(value)));
        if (value != 0.0d) {
            if (value > 0.0d) {
                text.insert(0, tag.charAt(0));
            } else {
                text.insert(0, tag.charAt(1));
            }
        }
        return text.toString();
    }

    public static String dms(double value, String tag) {
        StringBuffer text = new StringBuffer(dms(Math.abs(value)));
        if (value != 0.0d) {
            if (value > 0.0d) {
                text.insert(0, tag.charAt(0));
            } else {
                text.insert(0, tag.charAt(1));
            }
        }
        return text.toString();
    }

    public static String dm(double value) {
        boolean isNegative;
        if (value < 0.0d) {
            isNegative = CIVIL_FORMAT;
        } else {
            isNegative = ASTRO_FORMAT;
        }
        if (isNegative) {
            value = -value;
        }
        int degree = (int) Math.floor(value);
        value = (value - ((double) degree)) * 60.0d;
        int minute = (int) Math.floor(value);
        int second = (int) ((value - ((double) minute)) * 60.0d);
        if (second > 30 || (second == 30 && (minute & 1) == 1)) {
            minute++;
        }
        if (minute >= 60) {
            degree++;
            minute -= 60;
        }
        if (isNegative) {
            if (degree > 0) {
                degree = -degree;
            } else if (minute > 0) {
                minute = -minute;
            }
        }
        return new StringBuffer().append(Format.format((double) degree, ' ', 4, 0, degreeSign)).append(Format.format((double) minute, '0', 2, 0, minuteSign)).toString();
    }

    public static String dms(double value) {
        boolean isNegative = value < 0.0d ? CIVIL_FORMAT : ASTRO_FORMAT;
        if (isNegative) {
            value = -value;
        }
        int degree = (int) Math.floor(value);
        value = (value - ((double) degree)) * 60.0d;
        int minute = (int) Math.floor(value);
        int second = (int) ((value - ((double) minute)) * 60.0d);
        if (isNegative) {
            if (degree > 0) {
                degree = -degree;
            } else if (minute > 0) {
                minute = -minute;
            } else {
                second = -second;
            }
        }
        return new StringBuffer().append(Format.format((long) degree, ' ', 4, degreeSign)).append(Format.format((long) minute, '0', 2, minuteSign)).append(Format.format((long) second, '0', 4, secondSign)).toString();
    }

    public static String hms(double value) {
        return hms(value, 0, (boolean) CIVIL_FORMAT);
    }

    public static String hmsAstro(double value) {
        return hms(value, 1, (boolean) ASTRO_FORMAT);
    }

    public static String hms(double value, int secondPrecision, boolean isCivil) {
        int second;
        int second2 = (int) Math.floor(3600.0d * value);
        double fract = (value * 3600.0d) - ((double) second2);
        if (secondPrecision != 0 || (fract <= 0.5d && !(fract == 0.5d && (second2 & 1) == 1))) {
            second = second2;
        } else {
            second = second2 + 1;
        }
        int hour = second / 1440;
        second %= 1440;
        second2 = second / 60;
        int second3 = second % 60;
        char hourChar = isCivil ? ':' : 'h';
        char minuteChar = isCivil ? ':' : 'm';
        char secondChar = isCivil ? ASTRO_FORMAT : ASTRO_FORMAT;
        StringBuffer result = new StringBuffer().append(Format.format((long) hour, 48, 2, hourChar)).append(Format.format((long) second2, '0', 2, minuteChar));
        if (secondPrecision == 0) {
            result.append(Format.format((long) second3, '0', 2, secondChar));
        } else {
            result.append(Format.format(fract + ((double) second3), 48, '\u0002', secondPrecision, secondChar));
        }
        return result.toString();
    }

    public static String hm(double value) {
        String str = "none";
        String str2;
        if (value == AstroConstants.ABOVE_HORIZON) {
            str2 = "none";
            return str;
        } else if (value == AstroConstants.BELOW_HORIZON) {
            str2 = "none";
            return str;
        } else {
            int hour = (int) Math.floor(value);
            int minute = (int) Math.round((value - ((double) hour)) * 60.0d);
            if (minute >= 60) {
                hour++;
                minute -= 60;
            }
            if (hour >= 24) {
                hour -= 24;
            }
            return new StringBuffer().append(Format.format((long) hour, '0', 2, ':')).append(Format.format((long) minute, '0', 2, '\u0000')).toString();
        }
    }

    public static String formatTime(DateFormat dateFormat, double time) {
        SimpleDateFormat hmFormat = new SimpleDateFormat("HH:mm");
        String result = hm(time);
        if (!"none".equals(result)) {
            try {
                result = dateFormat.format(hmFormat.parse(result));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String formatDate(DateFormat dateFormat, Date date) {
        Calendar c = Calendar.getInstance();
        c.set(2010, 11, 31);
        String fmtDt = dateFormat.format(c.getTime());
        if ("12/31/2010".equals(fmtDt)) {
            return new SimpleDateFormat(MDY).format(date);
        }
        if ("31/12/2010".equals(fmtDt)) {
            return new SimpleDateFormat(DMY).format(date);
        }
        if ("2010/12/31".equals(fmtDt)) {
            return new SimpleDateFormat(YMD).format(date);
        }
        return dateFormat.format(date);
    }

    public static String diff(double value) {
        String str = "";
        String str2;
        if (value == AstroConstants.ABOVE_HORIZON) {
            str2 = "";
            return str;
        } else if (value == AstroConstants.BELOW_HORIZON) {
            str2 = "";
            return str;
        } else {
            Calendar c = Calendar.getInstance();
            double currentTime = ((double) c.get(11)) + (((double) c.get(12)) / 60.0d);
            if (currentTime > value) {
                return "-" + hm(currentTime - value);
            }
            return "+" + hm(value - currentTime);
        }
    }

    public static String timeZoneString(long timezone) {
        StringBuffer result = new StringBuffer();
        timezone /= 60000;
        if (timezone > 0) {
            result.append(Format.format((double) (timezone / 60), '0', 2, 0, ':')).append(Format.format(timezone % 60, '0', 2, '\u0000')).append(" East");
        } else if (timezone == 0) {
            result.append("00:00");
        } else {
            timezone = -timezone;
            result.append(Format.format((double) (timezone / 60), '0', 2, 0, ':')).append(Format.format(timezone % 60, '0', 2, '\u0000')).append(" West");
        }
        return result.toString();
    }
}

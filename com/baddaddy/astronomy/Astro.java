package com.baddaddy.astronomy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Astro implements AstroConstants {
    public static final double ARC = 206264.8062d;
    public static final double CosEPS = 0.91748d;
    public static final double P2 = 6.283185307d;
    public static final double SinEPS = 0.39778d;
    public static final long YEAR_END = 365;
    public static final long YEAR_MID = 183;
    public static final long YEAR_START = 0;

    public static long getTimeZoneOffset() {
        return getTimeZoneOffset(new Date());
    }

    public static long getTimeZoneOffset(Date javaDate) {
        return getTimeZoneOffset(javaDate, TimeZone.getDefault());
    }

    public static long getTimeZoneOffset(Date javaDate, TimeZone timeZone) {
        long timezoneOffset = 0;
        try {
            timezoneOffset = (long) timeZone.getRawOffset();
            if (timeZone.inDaylightTime(javaDate)) {
                return timezoneOffset + 3600000;
            }
            return timezoneOffset;
        } catch (Exception e) {
            System.err.println("Can't get System timezone: " + e);
            return timezoneOffset;
        }
    }

    public static TimeZone getTimeZone(String timeZoneID) {
        TimeZone result = TimeZone.getTimeZone(timeZoneID);
        int value = 0;
        if (result != null) {
            return result;
        }
        try {
            value = Integer.parseInt(timeZoneID);
        } catch (NumberFormatException e) {
            int colon = timeZoneID.indexOf(58);
            String hourString = "0";
            String minuteString = "0";
            if (colon > 0) {
                int sign = 1;
                hourString = timeZoneID.substring(colon - 1);
                minuteString = timeZoneID.substring(colon + 1, timeZoneID.length());
                if (minuteString.endsWith("E") || minuteString.endsWith("e")) {
                    minuteString = minuteString.substring(minuteString.length() - 1);
                } else if (minuteString.endsWith("W") || minuteString.endsWith("w")) {
                    minuteString = minuteString.substring(minuteString.length() - 1);
                    sign = -1;
                }
                try {
                    value = sign * ((Integer.parseInt(hourString) * 60) + Integer.parseInt(minuteString));
                } catch (NumberFormatException e2) {
                }
            }
        }
        String[] ids = TimeZone.getAvailableIDs(value);
        if (ids == null || ids.length <= 0) {
            return result;
        }
        return TimeZone.getTimeZone(ids[0]);
    }

    public static double midnightMJD(Date javaDate) {
        return Math.floor(MJD(javaDate));
    }

    public static double MJD(Date javaDate) {
        return (((double) (javaDate.getTime() + getTimeZoneOffset())) / AstroConstants.MSEC_PER_DAY) + AstroConstants.JAVA_EPOCH_MJD;
    }

    public static boolean isAbove(double[] value) {
        return value[0] == AstroConstants.ABOVE_HORIZON || value[1] == AstroConstants.ABOVE_HORIZON;
    }

    public static boolean isBelow(double[] value) {
        return value[0] == AstroConstants.BELOW_HORIZON || value[1] == AstroConstants.BELOW_HORIZON;
    }

    public static boolean isEvent(double[] value) {
        return (value[0] == AstroConstants.ABOVE_HORIZON || value[0] == AstroConstants.BELOW_HORIZON || value[1] == AstroConstants.ABOVE_HORIZON || value[1] == AstroConstants.BELOW_HORIZON) ? false : true;
    }

    public static double[] solarEphemeris(double MJD) {
        double T = (MJD - 51544.5d) / 36525.0d;
        double M = FRAC(0.993133d + (99.997361d * T)) * P2;
        MJD = FRAC(((((6893.0d * Math.sin(M)) + (72.0d * Math.sin(2.0d * M))) + (T * 6191.2d)) / 1296000.0d) + ((M / P2) + 0.7859453d)) * P2;
        M = Math.sin(MJD);
        T = Math.cos(MJD);
        double Y = CosEPS * M;
        M *= SinEPS;
        MJD = Math.sqrt(1.0d - (M * M));
        double[] result = new double[]{Math.atan2(M, MJD) * 57.29577951471995d, Math.atan2(Y, MJD + T) * 7.639437268629327d};
        if (result[0] < 0.0d) {
            result[0] = result[0] + 24.0d;
        }
        return result;
    }

    public static double[] lunarEphemeris(double MJD) {
        double T = (MJD - 51544.5d) / 36525.0d;
        double L0 = FRAC(0.606433d + (1336.855225d * T));
        double L = P2 * FRAC(0.374897d + (1325.55241d * T));
        double LS = P2 * FRAC(0.993133d + (99.997361d * T));
        MJD = P2 * FRAC(0.827361d + (1236.853086d * T));
        double F = P2 * FRAC((T * 1342.227825d) + 0.259086d);
        T = (((((((((((((22640.0d * Math.sin(L)) - (4586.0d * Math.sin(L - (2.0d * MJD)))) + (2370.0d * Math.sin(2.0d * MJD))) + (769.0d * Math.sin(2.0d * L))) - (668.0d * Math.sin(LS))) - (412.0d * Math.sin(2.0d * F))) - (212.0d * Math.sin((2.0d * L) - (2.0d * MJD)))) - (206.0d * Math.sin((L + LS) - (2.0d * MJD)))) + (192.0d * Math.sin((2.0d * MJD) + L))) - (165.0d * Math.sin(LS - (2.0d * MJD)))) - (125.0d * Math.sin(MJD))) - (110.0d * Math.sin(L + LS))) + (148.0d * Math.sin(L - LS))) - (55.0d * Math.sin((2.0d * F) - (2.0d * MJD)));
        double S = ((((412.0d * Math.sin(2.0d * F)) + T) + (541.0d * Math.sin(LS))) / ARC) + F;
        MJD = F - (MJD * 2.0d);
        MJD = (((Math.sin(MJD + (-LS)) * 11.0d) + ((((-526.0d * Math.sin(MJD)) + (44.0d * Math.sin(L + MJD))) - (31.0d * Math.sin((-L) + MJD))) - (23.0d * Math.sin(LS + MJD)))) - (25.0d * Math.sin((-2.0d * L) + F))) + (Math.sin(F + (-L)) * 21.0d);
        F = P2 * FRAC((T / 1296000.0d) + L0);
        MJD = (MJD + (18520.0d * Math.sin(S))) / ARC;
        T = Math.cos(MJD);
        L = Math.cos(F) * T;
        T *= Math.sin(F);
        MJD = Math.sin(MJD);
        F = (CosEPS * T) - (SinEPS * MJD);
        T = (T * SinEPS) + (MJD * CosEPS);
        MJD = Math.sqrt(1.0d - (T * T));
        double[] result = new double[]{Math.atan(T / MJD) * 57.29577951471995d, Math.atan(F / (MJD + L)) * 7.639437268629327d};
        if (result[0] < 0.0d) {
            result[0] = result[0] + 24.0d;
        }
        return result;
    }

    public static double LMST(double MJD, double longitude) {
        double MJD0 = Math.floor(MJD);
        double UT = 24.0d * (MJD - MJD0);
        MJD = (MJD0 - 51544.5d) / 36525.0d;
        return FRAC(((((MJD * (8640184.812866d + ((0.093104d - (6.2E-6d * MJD)) * MJD))) / 3600.0d) + (6.697374558d + (UT * 1.0027379093d))) + (longitude / 15.0d)) / 24.0d) * 24.0d;
    }

    public static double TRUNC(double value) {
        double result = Math.floor(Math.abs(value));
        if (value < 0.0d) {
            return -result;
        }
        return result;
    }

    public static double FRAC(double value) {
        double result = value - TRUNC(value);
        if (result < 0.0d) {
            return result + 1.0d;
        }
        return result;
    }

    public static boolean isEvent(double value) {
        return (value == AstroConstants.ABOVE_HORIZON || value == AstroConstants.BELOW_HORIZON) ? false : true;
    }

    public static String twilightString(Date date, int timezone, double latitude, double longitude) {
        return twilightString(new SunTimes(date, 0.0d, longitude, (long) timezone).getTimes(latitude, AstroConstants.TWILIGHT), "Twilight");
    }

    public static String twilightString(Date date, int timezone, double latitude, double longitude, double whichEvent, String eventName) {
        return twilightString(new SunTimes(date, 0.0d, longitude, (long) timezone).getTimes(latitude, whichEvent), eventName);
    }

    public static String twilightString(double[] riseSet, String twilightName) {
        return twilightString(riseSet[0], riseSet[1], twilightName);
    }

    public static String twilightString(double sunrise, double sunset, String twilightName) {
        String str = " begins at ";
        if (isEvent(sunrise)) {
            String str2;
            if (isEvent(sunset)) {
                str2 = " begins at ";
                return new StringBuilder(String.valueOf(twilightName)).append(getRiseSetString(sunrise, str)).append(getRiseSetString(sunset, " and ends at ")).toString();
            }
            str2 = " begins at ";
            return new StringBuilder(String.valueOf(twilightName)).append(getRiseSetString(sunrise, str)).toString();
        } else if (isEvent(sunset)) {
            return new StringBuilder(String.valueOf(twilightName)).append(getRiseSetString(sunset, " ends at ")).toString();
        } else {
            return new StringBuilder(String.valueOf(twilightName)).append(" does not occur").toString();
        }
    }

    public static String riseSetString(Date date, int timezone, double latitude, double longitude) {
        return riseSetString(new SunTimes(date, 0.0d, longitude, (long) timezone).getTimes(latitude, AstroConstants.SUNRISE));
    }

    public static String riseSetString(double[] riseSet) {
        return riseSetString(riseSet[0], riseSet[1]);
    }

    public static String riseSetString(double sunrise, double sunset) {
        StringBuffer text = new StringBuffer();
        if (sunrise == AstroConstants.ABOVE_HORIZON && sunset == AstroConstants.ABOVE_HORIZON) {
            text.append("Sun is above horizon all day");
        } else if (sunrise == AstroConstants.BELOW_HORIZON && sunset == AstroConstants.BELOW_HORIZON) {
            text.append("Sun does not rise today");
        } else if (isEvent(sunrise)) {
            text.append(getRiseSetString(sunrise, "Sunrise at "));
            if (isEvent(sunset)) {
                text.append(getRiseSetString(sunset, ", Sunset at "));
            } else {
                text.append(", Sun does not set");
            }
        } else {
            text.append(getRiseSetString(sunset, "Sunset at "));
        }
        return text.toString();
    }

    public static String getRiseSetString(double eventValue, String whichEvent) {
        if (isEvent(eventValue)) {
            return new StringBuilder(String.valueOf(whichEvent)).append(AstroFormat.hm(eventValue)).toString();
        }
        return "";
    }

    public static double mod(double numerator, double denomenator) {
        double result = Math.IEEEremainder(numerator, denomenator);
        if (result < 0.0d) {
            return result + denomenator;
        }
        return result;
    }

    public static double sin(double value) {
        return Math.sin(AstroConstants.DegRad * value);
    }

    public static double cos(double value) {
        return Math.cos(AstroConstants.DegRad * value);
    }

    public static double acos(double value) {
        return Math.acos(value) / AstroConstants.DegRad;
    }

    public static double atan2Deg(double numerator, double denomenator) {
        double result;
        if ((denomenator == 0.0d || Double.isNaN(denomenator)) && (numerator == 0.0d || Double.isNaN(numerator))) {
            result = 0.0d;
        } else {
            result = Math.atan2(numerator, denomenator);
        }
        return result / AstroConstants.DegRad;
    }

    public static void printAllTimeZones() {
        String[] id = TimeZone.getAvailableIDs();
        Date now = new Date();
        System.out.println(id.length + " timezones");
        for (int i = 0; i < id.length; i++) {
            System.out.println("TimeZone[" + id[i] + "] " + getTimeZoneInfo(id[i], now));
        }
    }

    public static String getTimeZoneInfo(String timezoneID, Date thisDate) {
        return getTimeZoneInfo(timezoneID, thisDate, false);
    }

    public static String getTimeZoneInfo(String timezoneID, Date thisDate, boolean appendDSTRule) {
        StringBuffer text = new StringBuffer(timezoneID);
        try {
            TimeZone tz = TimeZone.getTimeZone(timezoneID);
            if (tz == null) {
                text.append(" undefined");
            } else {
                long tzOffset = (long) tz.getRawOffset();
                if (tz.inDaylightTime(thisDate)) {
                    tzOffset += 3600000;
                }
                text.append(" " + AstroFormat.timeZoneString(tzOffset));
                if (tz.inDaylightTime(thisDate)) {
                    text.append(" (DST)");
                }
                if (appendDSTRule) {
                    if (tz.useDaylightTime()) {
                        text.append(". " + getDSTChange(tz, 0, YEAR_MID));
                        text.append(" and " + getDSTChange(tz, YEAR_MID, YEAR_END));
                    } else {
                        text.append(" (never uses DST)");
                    }
                }
            }
        } catch (Exception e) {
            text.append(", error: " + e);
        }
        return text.toString();
    }

    public static String getDSTChange(TimeZone tz, long startDay, long endDay) {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
        int year = cal.get(1);
        cal.clear();
        cal.set(year, 0, 1);
        long yearStart = cal.getTime().getTime();
        long startTime = yearStart + (startDay * 86400000);
        startDay = (86400000 * endDay) + yearStart;
        boolean dstAtStart = tz.inDaylightTime(new Date(startTime));
        if (dstAtStart == tz.inDaylightTime(new Date(startDay))) {
            dstAtStart = startTime;
            tz = startDay;
            return "no DST transition";
        }
        Date midDate = new Date();
        long startTime2 = startTime;
        startTime = 0;
        yearStart = startDay;
        while (true) {
            startDay = (yearStart - startTime2) / 2;
            if (startDay <= 0) {
                break;
            }
            startTime = startTime2 + startDay;
            if (tz.inDaylightTime(new Date(startTime)) == tz.inDaylightTime(new Date(startTime2))) {
                startTime2 = startTime;
            } else {
                yearStart = startTime;
            }
        }
        String midDateText = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date(startTime));
        if (dstAtStart) {
            String result = "DST ends at " + midDateText;
            dstAtStart = startTime2;
            tz = yearStart;
            return result;
        }
        result = "DST starts at " + midDateText;
        return result;
    }
}

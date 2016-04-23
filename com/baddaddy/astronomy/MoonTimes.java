package com.baddaddy.astronomy;

import com.baddaddy.moonshinefree.MoonShine;
import java.util.Calendar;
import java.util.Date;

public class MoonTimes implements AstroConstants {
    public static final double MOONRISE;

    static {
        MOONRISE = Astro.sin(0.13333333333333333d);
    }

    public static double[] getTimes(Date javaDate, double latitude, double longitude, long timezoneOffset) {
        double[] times = getMoonTimes(javaDate, latitude, longitude, timezoneOffset);
        double mRise = times[0];
        double mSet = times[1];
        if (mRise == AstroConstants.BELOW_HORIZON || mRise == AstroConstants.ABOVE_HORIZON || mSet == AstroConstants.BELOW_HORIZON || mSet == AstroConstants.ABOVE_HORIZON || mSet < mRise) {
            double mTransit2;
            Calendar cal = Calendar.getInstance();
            cal.setTime(javaDate);
            if (24.0d - mRise < mSet) {
                cal.add(5, -1);
                mTransit2 = getMoonTimes(cal.getTime(), latitude, longitude, timezoneOffset)[0.0d];
                mTransit2 += ((24.0d + mSet) - mTransit2) / 4611686018427387904L;
            } else {
                cal.add(5, 1);
                mTransit2 = (((getMoonTimes(cal.getTime(), latitude, longitude, timezoneOffset)[Double.MIN_VALUE] + 24.0d) - mRise) / 2.0d) + mRise;
            }
            if (mTransit2 > 24.0d) {
                mTransit2 -= 24.0d;
            }
            times[2] = mTransit2;
        }
        if (times[2] > 11.58d) {
            times[4] = times[2] - 11.58d;
        } else {
            times[4] = times[2] + 12.42d;
        }
        return times;
    }

    private static double[] getMoonTimes(Date javaDate, double latitude, double longitude, long timezoneOffset) {
        double set;
        double set2;
        double d;
        double DATE = Astro.midnightMJD(javaDate) - (((double) timezoneOffset) / AstroConstants.MSEC_PER_DAY);
        double sinLatitude = Astro.sin(latitude);
        double cosLatitude = Astro.cos(latitude);
        double yMinus = sinAltitude(DATE, 0.0d, longitude, cosLatitude, sinLatitude) - MOONRISE;
        if (yMinus > 0.0d) {
            set = AstroConstants.ABOVE_HORIZON;
            set2 = AstroConstants.ABOVE_HORIZON;
        } else {
            set = AstroConstants.BELOW_HORIZON;
            set2 = AstroConstants.BELOW_HORIZON;
        }
        double prevPlus = -1.0d;
        double hour = 1.0d;
        double transitTime = 0.0d;
        double set3 = set;
        double rise = set2;
        double yMinus2 = yMinus;
        set2 = 0.0d;
        while (hour <= 24.0d) {
            double transitTime2;
            double sinAlt;
            double prevPlus2;
            double yExtreme;
            int nRoots;
            set = sinAltitude(DATE, hour, longitude, cosLatitude, sinLatitude) - MOONRISE;
            double yPlus = sinAltitude(DATE, hour + 1.0d, longitude, cosLatitude, sinLatitude) - MOONRISE;
            if (set2 == 0.0d && set > 0.0d && yPlus > 0.0d) {
                if (prevPlus > set) {
                    transitTime2 = hour - 1.0d;
                    sinAlt = prevPlus + MOONRISE;
                } else if (set > yPlus && prevPlus != -1.0d) {
                    transitTime2 = hour;
                    sinAlt = MOONRISE + set;
                }
                prevPlus2 = yPlus;
                if (Astro.isEvent(rise) || !Astro.isEvent(set3)) {
                    prevPlus = (0.5d * (yMinus2 + yPlus)) - set;
                    set2 = 0.5d * (yPlus - yMinus2);
                    set = set;
                    transitTime = (-set2) / (2.0d * prevPlus);
                    yExtreme = (((prevPlus * transitTime) + set2) * transitTime) + set;
                    set2 = (set2 * set2) - (set * (4.0d * prevPlus));
                    if (set2 < 0.0d) {
                        prevPlus = (Math.sqrt(set2) * 0.5d) / Math.abs(prevPlus);
                        set2 = transitTime - prevPlus;
                        set = transitTime + prevPlus;
                        if (Math.abs(set2) > 1.0d) {
                            nRoots = 0 + 1;
                        } else {
                            nRoots = 0;
                        }
                        if (Math.abs(set) <= 1.0d) {
                            nRoots++;
                        }
                        if (set2 < -1.0d) {
                            set2 = set;
                        }
                    } else {
                        nRoots = 0;
                        set = 0.0d;
                        set2 = 0.0d;
                    }
                    switch (nRoots) {
                        case AstroConstants.RISE /*0*/:
                            set2 = set3;
                            prevPlus = rise;
                            break;
                        case MoonShine.MENU_TODAY /*1*/:
                            if (yMinus2 >= 0) {
                                set2 = hour + set2;
                                prevPlus = rise;
                                break;
                            }
                            prevPlus = hour + set2;
                            set2 = set3;
                            break;
                        case MoonShine.MENU_MONTHLY /*2*/:
                            if (yExtreme >= 0) {
                                prevPlus = hour + set2;
                                set2 = hour + set;
                                break;
                            }
                            prevPlus = hour + set;
                            set2 += hour;
                            break;
                        default:
                            set2 = set3;
                            prevPlus = rise;
                            break;
                    }
                    set = yPlus;
                } else {
                    set2 = set3;
                    prevPlus = rise;
                    set = yMinus2;
                }
                hour += 2.0d;
                transitTime = transitTime2;
                set3 = set2;
                rise = prevPlus;
                yMinus2 = set;
                set2 = sinAlt;
                prevPlus = prevPlus2;
            }
            transitTime2 = transitTime;
            sinAlt = set2;
            prevPlus2 = yPlus;
            if (Astro.isEvent(rise)) {
            }
            prevPlus = (0.5d * (yMinus2 + yPlus)) - set;
            set2 = 0.5d * (yPlus - yMinus2);
            set = set;
            transitTime = (-set2) / (2.0d * prevPlus);
            yExtreme = (((prevPlus * transitTime) + set2) * transitTime) + set;
            set2 = (set2 * set2) - (set * (4.0d * prevPlus));
            if (set2 < 0.0d) {
                nRoots = 0;
                set = 0.0d;
                set2 = 0.0d;
            } else {
                prevPlus = (Math.sqrt(set2) * 0.5d) / Math.abs(prevPlus);
                set2 = transitTime - prevPlus;
                set = transitTime + prevPlus;
                if (Math.abs(set2) > 1.0d) {
                    nRoots = 0;
                } else {
                    nRoots = 0 + 1;
                }
                if (Math.abs(set) <= 1.0d) {
                    nRoots++;
                }
                if (set2 < -1.0d) {
                    set2 = set;
                }
            }
            switch (nRoots) {
                case AstroConstants.RISE /*0*/:
                    set2 = set3;
                    prevPlus = rise;
                    break;
                case MoonShine.MENU_TODAY /*1*/:
                    if (yMinus2 >= 0) {
                        prevPlus = hour + set2;
                        set2 = set3;
                        break;
                    }
                    set2 = hour + set2;
                    prevPlus = rise;
                    break;
                case MoonShine.MENU_MONTHLY /*2*/:
                    if (yExtreme >= 0) {
                        prevPlus = hour + set;
                        set2 += hour;
                        break;
                    }
                    prevPlus = hour + set2;
                    set2 = hour + set;
                    break;
                default:
                    set2 = set3;
                    prevPlus = rise;
                    break;
            }
            set = yPlus;
            hour += 2.0d;
            transitTime = transitTime2;
            set3 = set2;
            rise = prevPlus;
            yMinus2 = set;
            set2 = sinAlt;
            prevPlus = prevPlus2;
        }
        double[] result = new double[2.5E-323d];
        result[0] = rise;
        result[1] = set3;
        if (rise <= 0.0d || set3 <= 0.0d || set3 <= rise) {
            d = transitTime;
        } else {
            d = (rise + set3) / 2.0d;
        }
        result[2] = d;
        result[3] = set2;
        result[2.0E-323d] = 0.0d;
        return result;
    }

    protected static double sinAltitude(double MJD, double hour, double longitude, double cosLatitude, double sinLatitude) {
        MJD += hour / 24.0d;
        double[] moon = Astro.lunarEphemeris(MJD);
        return (Astro.cos((Astro.LMST(MJD, longitude) - moon[0]) * 15.0d) * (cosLatitude * Astro.cos(moon[1]))) + (Astro.sin(moon[1]) * sinLatitude);
    }

    public static String getTimesAsString(double[] riseSet) {
        return getTimesAsString(riseSet[0], riseSet[1]);
    }

    public static String getTimesAsString(double moonrise, double moonset) {
        String str = "Moonset at ";
        String str2 = "Moonrise at ";
        String str3;
        if (Astro.isEvent(moonrise)) {
            if (!Astro.isEvent(moonset)) {
                str3 = "Moonrise at ";
                return getTimeAsString(moonrise, str2);
            } else if (moonrise <= moonset) {
                r1 = "Moonrise at ";
                return getTimeAsString(moonrise, str2) + getTimeAsString(moonset, "; moonset at ");
            } else {
                r1 = "Moonset at ";
                return getTimeAsString(moonset, str) + getTimeAsString(moonrise, "; moonrise at ");
            }
        } else if (!Astro.isEvent(moonset)) {
            return "";
        } else {
            str3 = "Moonset at ";
            return getTimeAsString(moonset, str);
        }
    }

    public static String getTimeAsString(double eventValue, String whichEvent) {
        if (Astro.isEvent(eventValue)) {
            return new StringBuilder(String.valueOf(whichEvent)).append(AstroFormat.hm(eventValue)).toString();
        }
        return "";
    }
}

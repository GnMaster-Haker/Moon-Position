package com.baddaddy.astronomy;

import com.baddaddy.moonshinefree.MoonShine;
import java.util.Date;

public class MoonInfo {
    private static int APOGEE = 0;
    private static double DegRad = 0.0d;
    private static int FIRST_QUARTER = 0;
    private static int FULL_MOON = 0;
    private static int LAST_QUARTER = 0;
    private static int NEW_MOON = 0;
    private static int PERIGEE = 0;
    private static int WANING_CRESCENT = 0;
    private static int WANING_GIBBOUS = 0;
    private static int WAXING_CRESCENT = 0;
    private static int WAXING_GIBBOUS = 0;
    private static final double eccent = 0.016718d;
    private static final double elonge = 278.83354d;
    private static final double elongp = 282.596403d;
    private static final double epoch = 44238.0d;
    private static final double kEpsilon = 1.0E-6d;
    private static final double mangsiz = 0.5181d;
    private static final double mecc = 0.0549d;
    private static final double minc = 5.145396d;
    private static final double mlnode = 151.950429d;
    private static final double mmlong = 64.975464d;
    private static final double mmlongp = 349.383063d;
    private static final double mparallax = 0.9507d;
    private static final double msmax = 384401.0d;
    private static final double sunangsiz = 0.533128d;
    private static final double sunsmax = 1.495985E8d;
    private static final double synmonth = 29.53058868d;
    private double age;
    public double angularDiameter;
    private double distance;
    private double fraction;
    private double illuminatedFraction;
    public double limbPositionAngle;
    private double perigeePercent;
    private int phase;
    public double sunAngularDiameter;
    public double sunDistance;

    static {
        DegRad = AstroConstants.DegRad;
        APOGEE = 406000;
        PERIGEE = 363000;
        NEW_MOON = 1;
        WAXING_CRESCENT = 2;
        FIRST_QUARTER = 3;
        WAXING_GIBBOUS = 4;
        FULL_MOON = 5;
        WANING_GIBBOUS = 6;
        LAST_QUARTER = 7;
        WANING_CRESCENT = 8;
    }

    public MoonInfo(Date date, double lat, double lon) {
        setDate(date);
    }

    public void setDate(Date javaDate) {
        double MJD = Astro.MJD(javaDate);
        double Day = MJD - epoch;
        double M = fixAngle((elonge + fixAngle(0.9856473320990837d * Day)) - elongp);
        double Ec = (2.0d * Math.atan(Math.sqrt(1.0340044870138985d) * Math.tan(kepler(M, eccent) / 2.0d))) / DegRad;
        double Lambdasun = fixAngle(elongp + Ec);
        double F = (1.0d + (eccent * Astro.cos(Ec))) / 0.999720508476d;
        double SunDist = sunsmax / F;
        double SunAng = F * sunangsiz;
        double ml = fixAngle((13.1763966d * Day) + mmlong);
        double MM = fixAngle((ml - (0.1114041d * Day)) - mmlongp);
        double MN = fixAngle(mlnode - (0.0529539d * Day));
        double Ev = 1.2739d * Astro.sin((2.0d * (ml - Lambdasun)) - MM);
        double Ae = 0.1858d * Astro.sin(M);
        double MmP = ((MM + Ev) - Ae) - (0.37d * Astro.sin(M));
        double mEc = 6.2886d * Astro.sin(MmP);
        double lP = (((ml + Ev) + mEc) - Ae) + (0.214d * Astro.sin(2.0d * MmP));
        double lPP = lP + (0.6583d * Astro.sin(2.0d * (lP - Lambdasun)));
        double NP = MN - (0.16d * Astro.sin(M));
        double Lambdamoon = Astro.atan2Deg(Astro.sin(lPP - NP) * Astro.cos(minc), Astro.cos(lPP - NP)) + NP;
        double BetaM = Math.asin(Astro.sin(lPP - NP) * Astro.sin(minc)) / DegRad;
        double MoonAgeDegrees = lPP - Lambdasun;
        double MoonDist = 383242.41154199d / (1.0d + (mecc * Astro.cos(MmP + mEc)));
        double MoonDFrac = MoonDist / msmax;
        double MoonAng = mangsiz / MoonDFrac;
        double MoonPar = mparallax / MoonDFrac;
        this.illuminatedFraction = (1.0d - Astro.cos(MoonAgeDegrees)) / 2.0d;
        this.age = synmonth * (fixAngle(MoonAgeDegrees) / 360.0d);
        this.distance = MoonDist;
        this.angularDiameter = MoonAng;
        this.sunDistance = SunDist;
        this.sunAngularDiameter = SunAng;
        this.fraction = fixAngle(MoonAgeDegrees) / 360.0d;
        this.phase = calculatePhase(this.age, this.illuminatedFraction);
        this.perigeePercent = (double) calculatePerigeePercent(this.distance);
        double[] solar = Astro.solarEphemeris(MJD);
        double[] lunar = Astro.lunarEphemeris(MJD);
        double cosSunDec = Astro.cos(solar[1]);
        double cosMoonDec = Astro.cos(lunar[1]);
        double cosDeltaRA = Astro.cos(lunar[0] - solar[0]);
        double sinDeltaRA = Astro.sin(lunar[0] - solar[0]);
        double sinSunDec = Astro.sin(solar[1]);
        double sinMoonDec = Astro.sin(lunar[1]);
        this.limbPositionAngle = Astro.atan2Deg(cosSunDec * sinDeltaRA, (cosMoonDec * sinSunDec) - (cosSunDec * cosDeltaRA));
    }

    private double kepler(double m, double ecc) {
        m *= DegRad;
        double e = m;
        double delta = 1.0d;
        while (Math.abs(delta) > kEpsilon) {
            delta = (e - (Math.sin(e) * ecc)) - m;
            e -= delta / (1.0d - (Math.cos(e) * ecc));
        }
        return e;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getIlluminatedFraction() {
        return this.illuminatedFraction;
    }

    public double getAge() {
        return this.age;
    }

    public double getFraction() {
        return this.fraction;
    }

    public int getPhase() {
        return this.phase;
    }

    public double getPerigeePercent() {
        return this.perigeePercent;
    }

    public double getLimbPositionAngle() {
        return this.limbPositionAngle;
    }

    public String toString() {
        int days = (int) Math.floor(this.age);
        double fraction = this.age - Math.floor(this.age);
        int hours = (int) Math.floor(24.0d * fraction);
        int minutes = ((int) (1440.0d * fraction)) % 60;
        return new String(new StringBuilder(String.valueOf(days)).append(days == 1 ? " day, " : " days, ").append(hours).append(hours == 1 ? " hour, " : " hours, ").append(minutes).append(minutes == 1 ? " minute" : " minutes").append(", ").append((int) (this.illuminatedFraction * 100.0d)).append("% full").toString());
    }

    private double fixAngle(double angle) {
        return angle - (Math.floor(angle / 360.0d) * 360.0d);
    }

    private int calculatePerigeePercent(double distance) {
        return (int) Math.round(((((double) APOGEE) - distance) / ((double) (APOGEE - PERIGEE))) * 100.0d);
    }

    private int calculatePhase(double age, double illuminatedFrac) {
        int vis = (int) Math.round(100.0d * illuminatedFrac);
        if (vis > 0 && vis < 50) {
            return age < 15.0d ? WAXING_CRESCENT : WANING_CRESCENT;
        } else {
            if (vis > 50 && vis < 100) {
                return age < 15.0d ? WAXING_GIBBOUS : WANING_GIBBOUS;
            } else if (vis == 50) {
                return age < 15.0d ? FIRST_QUARTER : LAST_QUARTER;
            } else if (vis == 0) {
                return NEW_MOON;
            } else {
                return FULL_MOON;
            }
        }
    }

    public String getPhaseText() {
        switch (this.phase) {
            case MoonShine.MENU_TODAY /*1*/:
                return "New Moon";
            case MoonShine.MENU_MONTHLY /*2*/:
                return "Waxing Crescent";
            case MoonShine.MENU_LOCATIONS /*3*/:
                return "First Quarter";
            case AstroConstants.UNDERFOOT /*4*/:
                return "Waxing Gibbous";
            case MoonShine.MENU_REFRESH /*5*/:
                return "Full Moon";
            case MoonShine.MENU_ABOUT /*6*/:
                return "Waning Gibbous";
            case MoonShine.MENU_HELP /*7*/:
                return "Last Quarter";
            case MoonShine.MENU_UPGRADE /*8*/:
                return "Waning Crescent";
            default:
                return "";
        }
    }

    public String getPhaseTextShort() {
        switch (this.phase) {
            case MoonShine.MENU_TODAY /*1*/:
                return "New";
            case MoonShine.MENU_MONTHLY /*2*/:
                return "Wax Cre";
            case MoonShine.MENU_LOCATIONS /*3*/:
                return "1st Qtr";
            case AstroConstants.UNDERFOOT /*4*/:
                return "Wax Gib";
            case MoonShine.MENU_REFRESH /*5*/:
                return "Full";
            case MoonShine.MENU_ABOUT /*6*/:
                return "Wan Gib";
            case MoonShine.MENU_HELP /*7*/:
                return "Lst Qtr";
            case MoonShine.MENU_UPGRADE /*8*/:
                return "Wan Cre";
            default:
                return "";
        }
    }

    public int getPhaseImageIndex() {
        double frac = this.illuminatedFraction * 100.0d;
        switch (this.phase) {
            case MoonShine.MENU_TODAY /*1*/:
                return 0;
            case MoonShine.MENU_MONTHLY /*2*/:
                if (frac < 8.0d) {
                    return 1;
                }
                if (frac < 15.0d) {
                    return 2;
                }
                if (frac < 22.0d) {
                    return 3;
                }
                if (frac < 29.0d) {
                    return 4;
                }
                if (frac < 36.0d) {
                    return 5;
                }
                if (frac < 43.0d) {
                    return 6;
                }
                return frac < 49.0d ? 7 : 8;
            case MoonShine.MENU_LOCATIONS /*3*/:
                return 8;
            case AstroConstants.UNDERFOOT /*4*/:
                if (frac < 53.0d) {
                    return 8;
                }
                if (frac < 60.0d) {
                    return 9;
                }
                if (frac < 67.0d) {
                    return 10;
                }
                if (frac < 75.0d) {
                    return 11;
                }
                if (frac < 83.0d) {
                    return 12;
                }
                if (frac < 92.0d) {
                    return 13;
                }
                return 14;
            case MoonShine.MENU_REFRESH /*5*/:
                return 15;
            case MoonShine.MENU_ABOUT /*6*/:
                if (frac < 53.0d) {
                    return 22;
                }
                if (frac < 60.0d) {
                    return 21;
                }
                if (frac < 69.0d) {
                    return 20;
                }
                if (frac < 78.0d) {
                    return 19;
                }
                if (frac < 86.0d) {
                    return 18;
                }
                if (frac < 94.0d) {
                    return 17;
                }
                return 16;
            case MoonShine.MENU_HELP /*7*/:
                return 22;
            case MoonShine.MENU_UPGRADE /*8*/:
                if (frac < 8.0d) {
                    return 29;
                }
                if (frac < 15.0d) {
                    return 28;
                }
                if (frac < 22.0d) {
                    return 27;
                }
                if (frac < 29.0d) {
                    return 26;
                }
                if (frac < 36.0d) {
                    return 25;
                }
                if (frac < 43.0d) {
                    return 24;
                }
                return frac < 49.0d ? 23 : 22;
            default:
                return 0;
        }
    }

    public int getSizePercent() {
        return (int) (79 + Math.round((21.0d * this.perigeePercent) / 100.0d));
    }

    public int getMaxBrightness() {
        return (int) Math.round(100.0d - ((Math.pow(this.distance / ((double) PERIGEE), 2.0d) * 100.0d) - 100.0d));
    }

    public int getCurrentBrightness() {
        return (int) Math.round((Math.pow(this.distance / ((double) PERIGEE), 2.0d) * 100.0d) - 100.0d);
    }
}

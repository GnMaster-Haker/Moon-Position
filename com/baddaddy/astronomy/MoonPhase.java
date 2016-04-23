package com.baddaddy.astronomy;

import java.util.Date;

public class MoonPhase {
    private static double DegRad = 0.0d;
    private static final double earthrad = 6378.16d;
    private static final double eccent = 0.016718d;
    private static final double elonge = 278.83354d;
    private static final double elongp = 282.596403d;
    private static final double epoch = 44238.0d;
    private static final double kEpsilon = 1.0E-6d;
    private static final double lunatbase = 2423436.0d;
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
    public double angularDiameter;
    public double distance;
    public double illuminatedFraction;
    public Date javaDate;
    public double limbPositionAngle;
    public double moonAge;
    public double moonFraction;
    public double sunAngularDiameter;
    public double sunDistance;

    static {
        DegRad = AstroConstants.DegRad;
    }

    public MoonPhase() {
        this(new Date());
    }

    public MoonPhase(Date javaDate) {
        setDate(javaDate);
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
        this.moonAge = synmonth * (fixAngle(MoonAgeDegrees) / 360.0d);
        this.distance = MoonDist;
        this.angularDiameter = MoonAng;
        this.sunDistance = SunDist;
        this.sunAngularDiameter = SunAng;
        this.moonFraction = fixAngle(MoonAgeDegrees) / 360.0d;
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

    public double getPhase() {
        return this.moonFraction;
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

    public String toString() {
        getPhase();
        int days = (int) Math.floor(this.moonAge);
        double fraction = this.moonAge - Math.floor(this.moonAge);
        int hours = (int) Math.floor(24.0d * fraction);
        int minutes = ((int) (1440.0d * fraction)) % 60;
        return new String(new StringBuilder(String.valueOf(days)).append(days == 1 ? " day, " : " days, ").append(hours).append(hours == 1 ? " hour, " : " hours, ").append(minutes).append(minutes == 1 ? " minute" : " minutes").append(", ").append((int) (this.illuminatedFraction * 100.0d)).append("% full").toString());
    }

    private double fixAngle(double angle) {
        return angle - (Math.floor(angle / 360.0d) * 360.0d);
    }
}

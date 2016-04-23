package com.baddaddy.astronomy;

import com.baddaddy.moonshinefree.MoonShine;
import java.util.Date;

public class SunTimes implements AstroConstants {
    private double MJD;
    private Date javaDate;
    private double latitude;
    private double longitude;
    private long timezoneOffset;

    public SunTimes() {
        setDate(new Date());
        setLatitude(0.0d);
        setLongitude(0.0d);
        setTimezoneOffset(Astro.getTimeZoneOffset());
    }

    public SunTimes(Date javaDate, double longitude, long timezoneOffset) {
        this(javaDate, 0.0d, longitude, timezoneOffset);
    }

    public SunTimes(Date javaDate, double latitude, double longitude, long timezoneOffset) {
        setDate(javaDate);
        setLatitude(latitude);
        setLongitude(longitude);
        setTimezoneOffset(timezoneOffset);
    }

    public Date getDate() {
        return this.javaDate;
    }

    public long getTimeZoneOffset() {
        return this.timezoneOffset;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setDate(Date javaDate) {
        this.javaDate = javaDate;
        this.MJD = Astro.midnightMJD(javaDate) - (((double) this.timezoneOffset) / AstroConstants.MSEC_PER_DAY);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTimezoneOffset(long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
        this.MJD = Astro.midnightMJD(this.javaDate) - (((double) timezoneOffset) / AstroConstants.MSEC_PER_DAY);
    }

    public double[] getTimes(double latitude, double horizon) {
        setLatitude(latitude);
        return getTimes(horizon);
    }

    public double[] getTimes(double horizon) {
        double[] result;
        double sinHorizon = Astro.sin(horizon);
        double yMinus = sinAltitude(0.0d) - sinHorizon;
        boolean aboveHorizon = yMinus > 0.0d;
        double rise = AstroConstants.BELOW_HORIZON;
        double set = AstroConstants.BELOW_HORIZON;
        if (aboveHorizon) {
            rise = AstroConstants.ABOVE_HORIZON;
            set = AstroConstants.ABOVE_HORIZON;
        }
        for (double hour = 1.0d; hour <= 24.0d; hour += 2.0d) {
            double yThis = sinAltitude(hour) - sinHorizon;
            double yPlus = sinAltitude(1.0d + hour) - sinHorizon;
            double root1 = 0.0d;
            double root2 = 0.0d;
            int nRoots = 0;
            double A = (0.5d * (yMinus + yPlus)) - yThis;
            double B = 0.5d * (yPlus - yMinus);
            double C = yThis;
            double xExtreme = (-B) / (2.0d * A);
            double yExtreme = (((A * xExtreme) + B) * xExtreme) + C;
            double discriminant = (B * B) - ((4.0d * A) * C);
            if (discriminant >= 0.0d) {
                double DX = (0.5d * Math.sqrt(discriminant)) / Math.abs(A);
                root1 = xExtreme - DX;
                root2 = xExtreme + DX;
                if (Math.abs(root1) <= 1.0d) {
                    nRoots = 0 + 1;
                }
                if (Math.abs(root2) <= 1.0d) {
                    nRoots++;
                }
                if (root1 < -1.0d) {
                    root1 = root2;
                }
            }
            switch (nRoots) {
                case MoonShine.MENU_TODAY /*1*/:
                    if (yMinus >= 0.0d) {
                        set = hour + root1;
                        break;
                    }
                    rise = hour + root1;
                    break;
                case MoonShine.MENU_MONTHLY /*2*/:
                    if (yExtreme >= 0.0d) {
                        rise = hour + root1;
                        set = hour + root2;
                        break;
                    }
                    rise = hour + root2;
                    set = hour + root1;
                    break;
            }
            yMinus = yPlus;
            if (Astro.isEvent(rise) && Astro.isEvent(set)) {
                result = new double[2];
                if (Astro.isEvent(rise)) {
                    rise = Astro.mod(rise, 24.0d);
                }
                if (Astro.isEvent(set)) {
                    set = Astro.mod(set, 24.0d);
                }
                result[0] = rise;
                result[1] = set;
                return result;
            }
        }
        result = new double[2];
        if (Astro.isEvent(rise)) {
            rise = Astro.mod(rise, 24.0d);
        }
        if (Astro.isEvent(set)) {
            set = Astro.mod(set, 24.0d);
        }
        result[0] = rise;
        result[1] = set;
        return result;
    }

    public double sinAltitude(double hour) {
        double mjd = this.MJD + (hour / 24.0d);
        double[] sun = Astro.solarEphemeris(mjd);
        return (Astro.sin(this.latitude) * Astro.sin(sun[1])) + ((Astro.cos(this.latitude) * Astro.cos(sun[1])) * Astro.cos(15.0d * (Astro.LMST(mjd, this.longitude) - sun[0])));
    }
}

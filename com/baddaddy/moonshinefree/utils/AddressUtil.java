package com.baddaddy.moonshinefree.utils;

import android.location.Address;

public class AddressUtil {
    private Address address;

    public AddressUtil(Address addr) {
        this.address = addr;
    }

    public String getCity() {
        return this.address.getLocality();
    }

    public String getCounty() {
        return this.address.getSubAdminArea();
    }

    public String getState() {
        return this.address.getAdminArea();
    }

    public String getCountryName() {
        return this.address.getCountryName();
    }

    public String getLocationName() {
        String str = "";
        String locName = "";
        if (this.address.getLocality() != null) {
            String str2 = "";
            if (!str.equals(this.address.getLocality())) {
                return this.address.getLocality();
            }
        }
        if (this.address.getSubAdminArea() != null) {
            str2 = "";
            if (!str.equals(this.address.getSubAdminArea())) {
                return this.address.getSubAdminArea();
            }
        }
        if (this.address.getAdminArea() != null) {
            str2 = "";
            if (!str.equals(this.address.getAdminArea())) {
                return new StringBuilder(String.valueOf(this.address.getAdminArea())).append(", ").append(this.address.getCountryName()).toString();
            }
        }
        if (this.address.getCountryName() == null) {
            return locName;
        }
        str2 = "";
        if (str.equals(this.address.getCountryName())) {
            return locName;
        }
        return this.address.getCountryName();
    }

    public String getCityState() {
        String str;
        String str2 = ", ";
        String str3 = "";
        String locName = "";
        if (this.address.getLocality() != null) {
            str = "";
            if (!(str3.equals(this.address.getLocality()) || this.address.getAdminArea() == null)) {
                str = "";
                if (!str3.equals(this.address.getAdminArea())) {
                    str3 = ", ";
                    return new StringBuilder(String.valueOf(this.address.getLocality())).append(str2).append(this.address.getAdminArea()).toString();
                }
            }
        }
        if (this.address.getLocality() != null) {
            str = "";
            if (!str3.equals(this.address.getLocality())) {
                return this.address.getLocality();
            }
        }
        if (this.address.getSubAdminArea() != null) {
            str = "";
            if (!str3.equals(this.address.getSubAdminArea())) {
                return this.address.getSubAdminArea();
            }
        }
        if (this.address.getAdminArea() != null) {
            str = "";
            if (!str3.equals(this.address.getAdminArea())) {
                str3 = ", ";
                return new StringBuilder(String.valueOf(this.address.getAdminArea())).append(str2).append(this.address.getCountryName()).toString();
            }
        }
        if (this.address.getCountryName() == null) {
            return locName;
        }
        str = "";
        if (str3.equals(this.address.getCountryName())) {
            return locName;
        }
        return this.address.getCountryName();
    }

    public String toString() {
        String str;
        String str2 = "\n";
        StringBuffer sb = new StringBuffer();
        sb.append("AddressHelper.toString():\n");
        for (int i = 0; i < this.address.getMaxAddressLineIndex(); i++) {
            str = "\n";
            sb.append("       Address ").append(i).append(": ").append(this.address.getAddressLine(i)).append(str2);
        }
        str = "\n";
        sb.append("    Sub-Locality: ").append(this.address.getSubLocality()).append(str2);
        str = "\n";
        sb.append("        Locality: ").append(this.address.getLocality()).append(str2);
        str = "\n";
        sb.append("  Sub-Admin Area: ").append(this.address.getSubAdminArea()).append(str2);
        str = "\n";
        sb.append("      Admin Area: ").append(this.address.getAdminArea()).append(str2);
        str = "\n";
        sb.append("    Country Code: ").append(this.address.getCountryCode()).append(str2);
        str = "\n";
        sb.append("    Country Name: ").append(this.address.getCountryName()).append(str2);
        return sb.toString();
    }
}

package com.baddaddy.moonshinefree.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build.VERSION;

public class GeocoderUtil {
    public static String reverseGeocodeCityState(Context context, double lat, double lon, int tries) {
        String localityName;
        int count = 0;
        String localityName2 = "";
        while (count < tries) {
            try {
                return new AddressUtil((Address) new Geocoder(context).getFromLocation(lat, lon, 1).get(0)).getCityState();
            } catch (Exception e) {
                try {
                    if ("2.2".compareToIgnoreCase(VERSION.RELEASE) > -1) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e2) {
                        }
                        String localityName3 = HttpGeocoder.reverseGeocodeCityState(lat, lon);
                        if (localityName3 != null) {
                            return localityName3;
                        }
                        localityName = localityName3;
                    } else {
                        localityName = localityName2;
                    }
                    int count2 = count + 1;
                    try {
                        Thread.sleep(1500);
                        count = count2;
                        localityName2 = localityName;
                    } catch (Exception e3) {
                        count = count2;
                        localityName2 = localityName;
                    }
                } catch (Exception e4) {
                    return "";
                }
            }
        }
        return localityName2;
    }

    public static String reverseGeocodeLocality(Context context, double lat, double lon, int tries) {
        int count = 0;
        String localityName = "";
        while (count < tries) {
            try {
                return new AddressUtil((Address) new Geocoder(context).getFromLocation(lat, lon, 1).get(0)).getLocationName();
            } catch (Exception e) {
                try {
                    String localityName2 = HttpGeocoder.reverseGeocodeLocality(lat, lon);
                    if (localityName2 != null) {
                        return localityName2;
                    }
                    int count2 = count + 1;
                    try {
                        Thread.sleep(2000);
                        count = count2;
                        localityName = localityName2;
                    } catch (Exception e2) {
                        count = count2;
                        localityName = localityName2;
                    }
                } catch (Exception e3) {
                    return "";
                }
            }
        }
        return localityName;
    }
}

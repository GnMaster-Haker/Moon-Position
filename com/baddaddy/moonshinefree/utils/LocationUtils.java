package com.baddaddy.moonshinefree.utils;

import android.location.Location;

public class LocationUtils {
    private static final int TWO_MINUTES = 120000;

    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null || location.getLatitude() == 0.0d) {
            return null;
        }
        if (currentBestLocation == null || currentBestLocation.getLatitude() == 0.0d) {
            return true;
        }
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 120000;
        boolean isSignificantlyOlder = timeDelta < -120000;
        boolean isNewer = timeDelta > 0;
        if (isSignificantlyNewer) {
            return true;
        }
        if (isSignificantlyOlder) {
            return null;
        }
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        isSignificantlyOlder = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        isSignificantlyNewer = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
        if (isMoreAccurate) {
            return true;
        }
        if (isNewer && !isSignificantlyOlder) {
            return true;
        }
        if (isNewer && !isSignificantlyNewer && isFromSameProvider) {
            return true;
        }
        return null;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        } else {
            return provider1.equals(provider2);
        }
    }
}

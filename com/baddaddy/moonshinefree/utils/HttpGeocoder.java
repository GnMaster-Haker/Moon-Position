package com.baddaddy.moonshinefree.utils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.xpath.XPathConstants;

public class HttpGeocoder {
    public static String reverseGeocodeCityState(double lat, double lon) {
        URL url;
        Exception ex;
        String localityName = null;
        try {
            URL serverAddress = new URL("http://maps.googleapis.com/maps/api/geocode/xml?latlng=" + Double.toString(lat) + "," + Double.toString(lon) + "&sensor=true");
            try {
                HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setReadTimeout(10000);
                connection.connect();
                try {
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    XPathReader reader = new XPathReader(connection.getInputStream());
                    if ("OK".equals((String) reader.read("/GeocodeResponse/status", XPathConstants.STRING))) {
                        localityName = (String) reader.read("/GeocodeResponse/result[type=\"locality\"]/address_component[type=\"locality\"]/short_name", XPathConstants.STRING);
                        url = serverAddress;
                    }
                } catch (VerifyError e) {
                    url = serverAddress;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    url = serverAddress;
                }
            } catch (Exception e22) {
                ex = e22;
                url = serverAddress;
                ex.printStackTrace();
                return localityName;
            }
        } catch (Exception e222) {
            ex = e222;
            ex.printStackTrace();
            return localityName;
        }
        return localityName;
    }

    public static String reverseGeocodeLocality(double lat, double lon) {
        URL url;
        Exception ex;
        String localityName = null;
        try {
            URL serverAddress = new URL("http://maps.googleapis.com/maps/api/geocode/xml?latlng=" + Double.toString(lat) + "," + Double.toString(lon) + "&sensor=true");
            try {
                HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setReadTimeout(10000);
                connection.connect();
                try {
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    XPathReader reader = new XPathReader(connection.getInputStream());
                    if ("OK".equals((String) reader.read("/GeocodeResponse/status", XPathConstants.STRING))) {
                        localityName = (String) reader.read("/GeocodeResponse/result[type=\"locality\"]/address_component[type=\"locality\"]/short_name", XPathConstants.STRING);
                        url = serverAddress;
                    }
                } catch (VerifyError e) {
                    url = serverAddress;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    url = serverAddress;
                }
            } catch (Exception e22) {
                ex = e22;
                url = serverAddress;
                ex.printStackTrace();
                return localityName;
            }
        } catch (Exception e222) {
            ex = e222;
            ex.printStackTrace();
            return localityName;
        }
        return localityName;
    }
}

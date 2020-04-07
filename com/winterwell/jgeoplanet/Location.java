// 
// Decompiled by Procyon v0.5.30
// 

package com.winterwell.jgeoplanet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Serializable;

public class Location implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final double DIAMETER_OF_EARTH = 1.27562E7;
    public final double longitude;
    public final double latitude;
    public static final Pattern latLongLocn;
    
    static {
        latLongLocn = Pattern.compile("\\s*(-?[\\d\\.]+),\\s*(-?[\\d\\.]+)\\s*");
    }
    
    public Location(final double latitude, double longitude) throws IllegalArgumentException {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude + ", " + longitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            longitude %= 360.0;
            if (longitude > 180.0) {
                longitude = 360.0 - longitude;
            }
            else if (longitude < -180.0) {
                longitude += 360.0;
            }
            assert longitude <= 180.0 : longitude;
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return this.latitude;
    }
    
    public double[] getLatLong() {
        return new double[] { this.latitude, this.longitude };
    }
    
    public double getLongitude() {
        return this.longitude;
    }
    
    public Dx distance(final Location other) {
        final double lat = this.latitude * 3.141592653589793 / 180.0;
        final double lon = this.longitude * 3.141592653589793 / 180.0;
        final double olat = other.latitude * 3.141592653589793 / 180.0;
        final double olon = other.longitude * 3.141592653589793 / 180.0;
        double sin2lat = Math.sin((lat - olat) / 2.0);
        sin2lat *= sin2lat;
        double sin2long = Math.sin((lon - olon) / 2.0);
        sin2long *= sin2long;
        final double m = 1.27562E7 * Math.asin(Math.sqrt(sin2lat + Math.cos(lat) * Math.cos(olat) * sin2long));
        return new Dx(m, LengthUnit.METRE);
    }
    
    public Location move(final double metresNorth, final double metresEast) {
        final double fracNorth = metresNorth / 2.003739210386106E10;
        double lat = this.latitude + fracNorth * 180.0;
        if (lat > 90.0) {
            lat = 90.0;
        }
        else if (lat < -90.0) {
            lat = -90.0;
        }
        double lng;
        for (lng = this.longitude + metresEast; lng > 180.0; lng -= 360.0) {}
        while (lng < -180.0) {
            lng += 360.0;
        }
        return new Location(lat, lng);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.latitude);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.longitude);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location)obj;
        return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(other.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(other.longitude);
    }
    
    @Override
    public String toString() {
        return "(" + this.latitude + " N, " + this.longitude + " E)";
    }
    
    public String toSimpleCoords() {
        return String.valueOf(this.latitude) + "," + this.longitude;
    }
    
    public static Location parse(final String locnDesc) {
        final Matcher m = Location.latLongLocn.matcher(locnDesc);
        if (!m.matches()) {
            return null;
        }
        final String lat = m.group(1);
        final String lng = m.group(2);
        final double _lat = Double.valueOf(lat);
        if (Math.abs(_lat) > 90.0) {
            return null;
        }
        return new Location(_lat, Double.valueOf(lng));
    }
}

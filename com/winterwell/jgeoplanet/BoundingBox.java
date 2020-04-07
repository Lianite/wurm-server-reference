// 
// Decompiled by Procyon v0.5.30
// 

package com.winterwell.jgeoplanet;

import winterwell.json.JSONException;
import winterwell.json.JSONObject;

public class BoundingBox
{
    final Location northEast;
    final Location southWest;
    
    public BoundingBox(final Location northEast, final Location southWest) {
        if (northEast.latitude < southWest.latitude) {
            throw new IllegalArgumentException("North east corner is south of south west corner");
        }
        this.northEast = northEast;
        this.southWest = southWest;
    }
    
    public Location getCenter() {
        final Location ne = this.northEast;
        final Location sw = this.southWest;
        double tempLat = (ne.latitude + sw.latitude) / 2.0;
        if (Math.abs(ne.latitude - sw.latitude) > 90.0) {
            if (tempLat <= 0.0) {
                tempLat += 90.0;
            }
            else {
                tempLat -= 90.0;
            }
        }
        double tempLong = (ne.longitude + sw.longitude) / 2.0;
        if (Math.abs(ne.longitude - sw.longitude) > 180.0) {
            if (tempLong <= 0.0) {
                tempLong += 180.0;
            }
            else {
                tempLong -= 180.0;
            }
        }
        final Location tempCentroid = new Location(tempLat, tempLong);
        return tempCentroid;
    }
    
    BoundingBox(final JSONObject bbox) throws JSONException {
        this(getLocation(bbox.getJSONObject("northEast")), getLocation(bbox.getJSONObject("southWest")));
    }
    
    public BoundingBox(final Location centre, final Dx radius) {
        final double r = radius.getMetres();
        this.northEast = centre.move(r, r);
        this.southWest = centre.move(-r, -r);
    }
    
    public Location getNorthEast() {
        return this.northEast;
    }
    
    public Location getSouthWest() {
        return this.southWest;
    }
    
    public Location getNorthWest() {
        return new Location(this.northEast.latitude, this.southWest.longitude);
    }
    
    public Location getSouthEast() {
        return new Location(this.southWest.latitude, this.northEast.longitude);
    }
    
    public boolean contains(final Location location) {
        if (location.latitude > this.northEast.latitude) {
            return false;
        }
        if (location.latitude < this.southWest.latitude) {
            return false;
        }
        if (this.northEast.longitude < 0.0 && this.southWest.longitude >= 0.0 && this.southWest.longitude > this.northEast.longitude) {
            if (location.longitude < 0.0 && location.longitude > this.northEast.longitude) {
                return false;
            }
            if (location.longitude >= 0.0 && location.longitude < this.southWest.longitude) {
                return false;
            }
        }
        else {
            if (location.longitude > this.northEast.longitude) {
                return false;
            }
            if (location.longitude < this.southWest.longitude) {
                return false;
            }
        }
        return true;
    }
    
    public boolean contains(final BoundingBox other) {
        return this.contains(other.southWest) && this.contains(other.northEast);
    }
    
    public boolean intersects(final BoundingBox other) {
        return this.contains(other.northEast) || this.contains(other.southWest) || this.contains(other.getNorthWest()) || this.contains(other.getSouthEast());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.northEast == null) ? 0 : this.northEast.hashCode());
        result = 31 * result + ((this.southWest == null) ? 0 : this.southWest.hashCode());
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
        final BoundingBox other = (BoundingBox)obj;
        if (this.northEast == null) {
            if (other.northEast != null) {
                return false;
            }
        }
        else if (!this.northEast.equals(other.northEast)) {
            return false;
        }
        if (this.southWest == null) {
            if (other.southWest != null) {
                return false;
            }
        }
        else if (!this.southWest.equals(other.southWest)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "BoundingBox [northEast=" + this.northEast + ", southWest=" + this.southWest + "]";
    }
    
    static Location getLocation(final JSONObject jo) throws JSONException {
        return new Location(jo.getDouble("latitude"), jo.getDouble("longitude"));
    }
    
    public boolean isPoint() {
        return this.northEast.equals(this.southWest);
    }
}

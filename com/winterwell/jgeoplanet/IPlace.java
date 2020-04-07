// 
// Decompiled by Procyon v0.5.30
// 

package com.winterwell.jgeoplanet;

public interface IPlace
{
    public static final String TYPE_CITY = "city";
    public static final String TYPE_COUNTRY = "country";
    
    String getName();
    
    String getCountryName();
    
    IPlace getParent();
    
    Location getCentroid();
    
    BoundingBox getBoundingBox();
    
    String getType();
    
    String getUID();
}

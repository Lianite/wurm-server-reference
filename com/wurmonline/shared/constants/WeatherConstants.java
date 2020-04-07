// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

public final class WeatherConstants
{
    private static final double DEGS_TO_RADS = 0.017453292519943295;
    
    public static final float getNormalizedWindX(final float windRotation) {
        return -(float)Math.sin(windRotation * 0.017453292519943295);
    }
    
    public static final float getNormalizedWindY(final float windRotation) {
        return (float)Math.cos(windRotation * 0.017453292519943295);
    }
    
    public static final float getWindX(final float windRotation, final float windPower) {
        return -(float)Math.sin(windRotation * 0.017453292519943295) * Math.abs(windPower);
    }
    
    public static final float getWindY(final float windRotation, final float windPower) {
        return (float)Math.cos(windRotation * 0.017453292519943295) * Math.abs(windPower);
    }
}

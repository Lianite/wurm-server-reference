// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

public final class WMath
{
    private static final float pi = 3.1415927f;
    public static final float pi2 = 6.2831855f;
    public static final float DEG_TO_RAD = 0.017453292f;
    public static final float RAD_TO_DEG = 57.295776f;
    public static final float FAR_AWAY = Float.MAX_VALUE;
    
    public static float atan2(final float y, final float x) {
        if (y == 0.0f) {
            return 0.0f;
        }
        final float coeff_1 = 0.7853982f;
        final float coeff_2 = 2.3561945f;
        final float abs_y = Math.abs(y);
        float angle = 0.0f;
        if (x >= 0.0f) {
            final float r = (x - abs_y) / (x + abs_y);
            angle = 0.7853982f - 0.7853982f * r;
        }
        else {
            final float r = (x + abs_y) / (abs_y - x);
            angle = 2.3561945f - 0.7853982f * r;
        }
        if (y < 0.0f) {
            return -angle;
        }
        return angle;
    }
    
    public static int floor(final float f) {
        return (f > 0.0f) ? ((int)f) : (-(int)(-f));
    }
    
    public static float abs(final float f) {
        return (f >= 0.0f) ? f : (-f);
    }
    
    public static float getRadFromDeg(final float deg) {
        return 0.017453292f * deg;
    }
    
    public static float getDegFromRad(final float rad) {
        return 57.295776f * rad;
    }
}

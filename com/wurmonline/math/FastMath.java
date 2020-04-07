// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.math;

import java.util.Random;

public final class FastMath
{
    public static final double DBL_EPSILON = 2.220446049250313E-16;
    public static final float FLT_EPSILON = 1.1920929E-7f;
    public static final float ZERO_TOLERANCE = 1.0E-4f;
    public static final float ONE_THIRD = 0.33333334f;
    public static final float PI = 3.1415927f;
    public static final float TWO_PI = 6.2831855f;
    public static final float HALF_PI = 1.5707964f;
    public static final float QUARTER_PI = 0.7853982f;
    public static final float INV_PI = 0.31830987f;
    public static final float INV_TWO_PI = 0.15915494f;
    public static final float DEG_TO_RAD = 0.017453292f;
    public static final float RAD_TO_DEG = 57.295776f;
    public static final Random rand;
    
    public static boolean isPowerOfTwo(final int number) {
        return number > 0 && (number & number - 1) == 0x0;
    }
    
    public static int nearestPowerOfTwo(final int number) {
        return (int)Math.pow(2.0, Math.ceil(Math.log(number) / Math.log(2.0)));
    }
    
    public static float lERP(final float percent, final float startValue, final float endValue) {
        if (startValue == endValue) {
            return startValue;
        }
        return (1.0f - percent) * startValue + percent * endValue;
    }
    
    public static float acos(final float fValue) {
        if (-1.0f >= fValue) {
            return 3.1415927f;
        }
        if (fValue < 1.0f) {
            return (float)Math.acos(fValue);
        }
        return 0.0f;
    }
    
    public static float asin(final float fValue) {
        if (-1.0f >= fValue) {
            return -1.5707964f;
        }
        if (fValue < 1.0f) {
            return (float)Math.asin(fValue);
        }
        return 1.5707964f;
    }
    
    public static float atan(final float fValue) {
        return (float)Math.atan(fValue);
    }
    
    public static float atan2(final float fY, final float fX) {
        return (float)Math.atan2(fY, fX);
    }
    
    public static float ceil(final float fValue) {
        return (float)Math.ceil(fValue);
    }
    
    public static float reduceSinAngle(float radians) {
        radians %= 6.2831855f;
        if (Math.abs(radians) > 3.1415927f) {
            radians -= 6.2831855f;
        }
        if (Math.abs(radians) > 1.5707964f) {
            radians = 3.1415927f - radians;
        }
        return radians;
    }
    
    public static float sin(float fValue) {
        fValue = reduceSinAngle(fValue);
        if (Math.abs(fValue) <= 0.7853981633974483) {
            return (float)Math.sin(fValue);
        }
        return (float)Math.cos(1.5707963267948966 - fValue);
    }
    
    public static float cos(final float fValue) {
        return sin(fValue + 1.5707964f);
    }
    
    public static float exp(final float fValue) {
        return (float)Math.exp(fValue);
    }
    
    public static float abs(final float fValue) {
        if (fValue < 0.0f) {
            return -fValue;
        }
        return fValue;
    }
    
    public static float floor(final float fValue) {
        return (float)Math.floor(fValue);
    }
    
    public static float invSqrt(final float fValue) {
        return (float)(1.0 / Math.sqrt(fValue));
    }
    
    public static float log(final float fValue) {
        return (float)Math.log(fValue);
    }
    
    public static float log(final float value, final float base) {
        return (float)(Math.log(value) / Math.log(base));
    }
    
    public static float pow(final float fBase, final float fExponent) {
        return (float)Math.pow(fBase, fExponent);
    }
    
    public static float sqr(final float fValue) {
        return fValue * fValue;
    }
    
    public static float sqrt(final float fValue) {
        return (float)Math.sqrt(fValue);
    }
    
    public static float tan(final float fValue) {
        return (float)Math.tan(fValue);
    }
    
    public static int sign(final int iValue) {
        if (iValue > 0) {
            return 1;
        }
        if (iValue < 0) {
            return -1;
        }
        return 0;
    }
    
    public static float sign(final float fValue) {
        return Math.signum(fValue);
    }
    
    public static float determinant(final double m00, final double m01, final double m02, final double m03, final double m10, final double m11, final double m12, final double m13, final double m20, final double m21, final double m22, final double m23, final double m30, final double m31, final double m32, final double m33) {
        final double det01 = m20 * m31 - m21 * m30;
        final double det2 = m20 * m32 - m22 * m30;
        final double det3 = m20 * m33 - m23 * m30;
        final double det4 = m21 * m32 - m22 * m31;
        final double det5 = m21 * m33 - m23 * m31;
        final double det6 = m22 * m33 - m23 * m32;
        return (float)(m00 * (m11 * det6 - m12 * det5 + m13 * det4) - m01 * (m10 * det6 - m12 * det3 + m13 * det2) + m02 * (m10 * det5 - m11 * det3 + m13 * det01) - m03 * (m10 * det4 - m11 * det2 + m12 * det01));
    }
    
    public static float nextRandomFloat() {
        return FastMath.rand.nextFloat();
    }
    
    public static int nextRandomInt(final int min, final int max) {
        return (int)(nextRandomFloat() * (max - min + 1)) + min;
    }
    
    public static int nextRandomInt() {
        return FastMath.rand.nextInt();
    }
    
    public static Vector3f sphericalToCartesian(final Vector3f sphereCoords, final Vector3f store) {
        store.y = sphereCoords.x * sin(sphereCoords.z);
        final float a = sphereCoords.x * cos(sphereCoords.z);
        store.x = a * cos(sphereCoords.y);
        store.z = a * sin(sphereCoords.y);
        return store;
    }
    
    public static Vector3f cartesianToSpherical(final Vector3f cartCoords, final Vector3f store) {
        if (cartCoords.x == 0.0f) {
            cartCoords.x = 1.1920929E-7f;
        }
        store.x = sqrt(cartCoords.x * cartCoords.x + cartCoords.y * cartCoords.y + cartCoords.z * cartCoords.z);
        store.y = atan(cartCoords.z / cartCoords.x);
        if (cartCoords.x < 0.0f) {
            store.y += 3.1415927f;
        }
        store.z = asin(cartCoords.y / store.x);
        return store;
    }
    
    public static Vector3f sphericalToCartesianZ(final Vector3f sphereCoords, final Vector3f store) {
        store.z = sphereCoords.x * sin(sphereCoords.z);
        final float a = sphereCoords.x * cos(sphereCoords.z);
        store.x = a * cos(sphereCoords.y);
        store.y = a * sin(sphereCoords.y);
        return store;
    }
    
    public static Vector3f cartesianZToSpherical(final Vector3f cartCoords, final Vector3f store) {
        if (cartCoords.x == 0.0f) {
            cartCoords.x = 1.1920929E-7f;
        }
        store.x = sqrt(cartCoords.x * cartCoords.x + cartCoords.y * cartCoords.y + cartCoords.z * cartCoords.z);
        store.z = atan(cartCoords.z / cartCoords.x);
        if (cartCoords.x < 0.0f) {
            store.z += 3.1415927f;
        }
        store.y = asin(cartCoords.y / store.x);
        return store;
    }
    
    public static float normalize(float val, final float min, final float max) {
        if (Float.isInfinite(val) || Float.isNaN(val)) {
            return 0.0f;
        }
        float range;
        for (range = max - min; val > max; val -= range) {}
        while (val < min) {
            val += range;
        }
        return val;
    }
    
    public static float copysign(final float x, final float y) {
        if (y >= 0.0f && x <= 0.0f) {
            return -x;
        }
        if (y < 0.0f && x >= 0.0f) {
            return -x;
        }
        return x;
    }
    
    public static float clamp(final float input, final float min, final float max) {
        return (input < min) ? min : ((input > max) ? max : input);
    }
    
    static {
        rand = new Random(System.currentTimeMillis());
    }
}

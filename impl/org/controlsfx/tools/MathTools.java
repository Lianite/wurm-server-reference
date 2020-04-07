// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.tools;

import java.util.Objects;

public class MathTools
{
    public static boolean isInInterval(final double lowerBound, final double value, final double upperBound) {
        return lowerBound <= value && value <= upperBound;
    }
    
    public static double inInterval(final double lowerBound, final double value, final double upperBound) {
        if (value < lowerBound) {
            return lowerBound;
        }
        if (upperBound < value) {
            return upperBound;
        }
        return value;
    }
    
    public static double min(final double... values) {
        Objects.requireNonNull(values, "The specified value array must not be null.");
        if (values.length == 0) {
            throw new IllegalArgumentException("The specified value array must contain at least one element.");
        }
        double min = Double.MAX_VALUE;
        for (final double value : values) {
            min = Math.min(value, min);
        }
        return min;
    }
}

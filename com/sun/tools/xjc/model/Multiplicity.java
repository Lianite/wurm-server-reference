// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

public final class Multiplicity
{
    public final int min;
    public final Integer max;
    public static final Multiplicity ZERO;
    public static final Multiplicity ONE;
    public static final Multiplicity OPTIONAL;
    public static final Multiplicity STAR;
    public static final Multiplicity PLUS;
    
    public static Multiplicity create(final int min, final Integer max) {
        if (min == 0 && max == null) {
            return Multiplicity.STAR;
        }
        if (min == 1 && max == null) {
            return Multiplicity.PLUS;
        }
        if (max != null) {
            if (min == 0 && max == 0) {
                return Multiplicity.ZERO;
            }
            if (min == 0 && max == 1) {
                return Multiplicity.OPTIONAL;
            }
            if (min == 1 && max == 1) {
                return Multiplicity.ONE;
            }
        }
        return new Multiplicity(min, max);
    }
    
    private Multiplicity(final int min, final Integer max) {
        this.min = min;
        this.max = max;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof Multiplicity)) {
            return false;
        }
        final Multiplicity that = (Multiplicity)o;
        if (this.min != that.min) {
            return false;
        }
        if (this.max != null) {
            if (this.max.equals(that.max)) {
                return true;
            }
        }
        else if (that.max == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = this.min;
        result = 29 * result + ((this.max != null) ? this.max.hashCode() : 0);
        return result;
    }
    
    public boolean isUnique() {
        return this.max != null && this.min == 1 && this.max == 1;
    }
    
    public boolean isOptional() {
        return this.max != null && this.min == 0 && this.max == 1;
    }
    
    public boolean isAtMostOnce() {
        return this.max != null && this.max <= 1;
    }
    
    public boolean isZero() {
        return this.max != null && this.max == 0;
    }
    
    public boolean includes(final Multiplicity rhs) {
        return rhs.min >= this.min && (this.max == null || (rhs.max != null && rhs.max <= this.max));
    }
    
    public String getMaxString() {
        if (this.max == null) {
            return "unbounded";
        }
        return this.max.toString();
    }
    
    public String toString() {
        return "(" + this.min + ',' + this.getMaxString() + ')';
    }
    
    public static Multiplicity choice(final Multiplicity lhs, final Multiplicity rhs) {
        return create(Math.min(lhs.min, rhs.min), (lhs.max == null || rhs.max == null) ? null : Math.max(lhs.max, rhs.max));
    }
    
    public static Multiplicity group(final Multiplicity lhs, final Multiplicity rhs) {
        return create(lhs.min + rhs.min, (lhs.max == null || rhs.max == null) ? null : (lhs.max + rhs.max));
    }
    
    public static Multiplicity multiply(final Multiplicity lhs, final Multiplicity rhs) {
        final int min = lhs.min * rhs.min;
        Integer max;
        if (isZero(lhs.max) || isZero(rhs.max)) {
            max = 0;
        }
        else if (lhs.max == null || rhs.max == null) {
            max = null;
        }
        else {
            max = lhs.max * rhs.max;
        }
        return create(min, max);
    }
    
    private static boolean isZero(final Integer i) {
        return i != null && i == 0;
    }
    
    public static Multiplicity oneOrMore(final Multiplicity c) {
        if (c.max == null) {
            return c;
        }
        if (c.max == 0) {
            return c;
        }
        return create(c.min, null);
    }
    
    public Multiplicity makeOptional() {
        if (this.min == 0) {
            return this;
        }
        return create(0, this.max);
    }
    
    public Multiplicity makeRepeated() {
        if (this.max == null || this.max == 0) {
            return this;
        }
        return create(this.min, null);
    }
    
    static {
        ZERO = new Multiplicity(0, 0);
        ONE = new Multiplicity(1, 1);
        OPTIONAL = new Multiplicity(0, 1);
        STAR = new Multiplicity(0, null);
        PLUS = new Multiplicity(1, null);
    }
}

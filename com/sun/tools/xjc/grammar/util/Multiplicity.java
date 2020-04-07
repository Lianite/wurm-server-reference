// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.Expression;

public final class Multiplicity
{
    public final int min;
    public final Integer max;
    public static final Multiplicity zero;
    public static final Multiplicity one;
    public static final Multiplicity star;
    
    public Multiplicity(final int min, final Integer max) {
        this.min = min;
        this.max = max;
    }
    
    public Multiplicity(final int min, final int max) {
        this.min = min;
        this.max = new Integer(max);
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
        return "(" + this.min + "," + this.getMaxString() + ")";
    }
    
    public static Multiplicity calc(final Expression exp, final MultiplicityCounter calc) {
        return (Multiplicity)exp.visit((ExpressionVisitor)calc);
    }
    
    public static Multiplicity choice(final Multiplicity lhs, final Multiplicity rhs) {
        return new Multiplicity(Math.min(lhs.min, rhs.min), (lhs.max == null || rhs.max == null) ? null : new Integer(Math.max(lhs.max, rhs.max)));
    }
    
    public static Multiplicity group(final Multiplicity lhs, final Multiplicity rhs) {
        return new Multiplicity(lhs.min + rhs.min, (lhs.max == null || rhs.max == null) ? null : new Integer(lhs.max + rhs.max));
    }
    
    public static Multiplicity oneOrMore(final Multiplicity c) {
        if (c.max == null) {
            return c;
        }
        if (c.max == 0) {
            return c;
        }
        return new Multiplicity(c.min, (Integer)null);
    }
    
    static {
        Multiplicity.zero = new Multiplicity(0, 0);
        Multiplicity.one = new Multiplicity(1, 1);
        Multiplicity.star = new Multiplicity(0, (Integer)null);
    }
}

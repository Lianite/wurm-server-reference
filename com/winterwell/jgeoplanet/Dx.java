// 
// Decompiled by Procyon v0.5.30
// 

package com.winterwell.jgeoplanet;

public final class Dx implements Comparable<Dx>
{
    private static final long serialVersionUID = 1L;
    private final LengthUnit unit;
    private final double n;
    
    public static Dx ZERO() {
        return new Dx(0.0, LengthUnit.METRE);
    }
    
    public Dx(final double metres) {
        this(metres, LengthUnit.METRE);
    }
    
    public Dx(final double n, final LengthUnit unit) {
        this.n = n;
        this.unit = unit;
        assert unit != null;
    }
    
    public double getMetres() {
        return this.unit.metres * this.n;
    }
    
    public double getValue() {
        return this.n;
    }
    
    public LengthUnit geKLength() {
        return this.unit;
    }
    
    @Override
    public String toString() {
        return String.valueOf((float)this.n) + " " + this.unit.toString().toLowerCase() + ((this.n != 1.0) ? "s" : "");
    }
    
    public boolean isShorterThan(final Dx Dx2) {
        assert Dx2 != null;
        return Math.abs(this.getMetres()) < Math.abs(Dx2.getMetres());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != Dx.class) {
            return false;
        }
        final Dx Dx = (Dx)obj;
        return this.getMetres() == Dx.getMetres();
    }
    
    @Override
    public int hashCode() {
        return new Double(this.getMetres()).hashCode();
    }
    
    public Dx multiply(final double x) {
        return new Dx(x * this.n, this.unit);
    }
    
    @Override
    public int compareTo(final Dx Dx2) {
        final double ms = this.getMetres();
        final double ms2 = Dx2.getMetres();
        if (ms == ms2) {
            return 0;
        }
        return (ms < ms2) ? -1 : 1;
    }
    
    public Dx convertTo(final LengthUnit unit2) {
        if (this.unit == unit2) {
            return this;
        }
        final double n2 = this.divide(unit2.dx);
        return new Dx(n2, unit2);
    }
    
    public double divide(final Dx other) {
        if (this.n == 0.0) {
            return 0.0;
        }
        return this.n * this.unit.metres / (other.n * other.unit.metres);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.winterwell.jgeoplanet;

public enum LengthUnit
{
    METRE("METRE", 0, 1.0), 
    KILOMETRE("KILOMETRE", 1, 1000.0), 
    MILE("MILE", 2, 1609.344);
    
    public final double metres;
    public final Dx dx;
    
    private LengthUnit(final String s, final int n, final double metres) {
        this.metres = metres;
        this.dx = new Dx(1.0, this);
    }
    
    public Dx getDx() {
        return this.dx;
    }
    
    public double getMetres() {
        return this.metres;
    }
    
    @Deprecated
    public double convert(final double amount, final LengthUnit otherUnit) {
        final Dx Dx2 = new Dx(amount, otherUnit);
        return Dx2.convertTo(this).getValue();
    }
}

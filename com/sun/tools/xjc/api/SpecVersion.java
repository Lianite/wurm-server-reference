// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

public enum SpecVersion
{
    V2_0, 
    V2_1;
    
    public static final SpecVersion LATEST;
    
    public boolean isLaterThan(final SpecVersion t) {
        return this.ordinal() >= t.ordinal();
    }
    
    public static SpecVersion parse(final String token) {
        if (token.equals("2.0")) {
            return SpecVersion.V2_0;
        }
        if (token.equals("2.1")) {
            return SpecVersion.V2_1;
        }
        return null;
    }
    
    static {
        LATEST = SpecVersion.V2_1;
    }
}

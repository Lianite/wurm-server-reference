// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

public final class XSVariety
{
    public static final XSVariety ATOMIC;
    public static final XSVariety UNION;
    public static final XSVariety LIST;
    private final String name;
    
    private XSVariety(final String _name) {
        this.name = _name;
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        ATOMIC = new XSVariety("atomic");
        UNION = new XSVariety("union");
        LIST = new XSVariety("list");
    }
}

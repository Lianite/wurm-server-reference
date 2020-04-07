// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.ct;

final class ComplexTypeFlag
{
    private final String name;
    static final ComplexTypeFlag NORMAL;
    static final ComplexTypeFlag FALLBACK_CONTENT;
    static final ComplexTypeFlag FALLBACK_REST;
    
    private ComplexTypeFlag(final String name) {
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        NORMAL = new ComplexTypeFlag("normal");
        FALLBACK_CONTENT = new ComplexTypeFlag("fallback(content)");
        FALLBACK_REST = new ComplexTypeFlag("fallback(rest)");
    }
}

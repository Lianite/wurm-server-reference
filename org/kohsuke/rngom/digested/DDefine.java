// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

public class DDefine
{
    private final String name;
    private DPattern pattern;
    private Boolean nullable;
    DAnnotation annotation;
    
    public DDefine(final String name) {
        this.name = name;
    }
    
    public DPattern getPattern() {
        return this.pattern;
    }
    
    public DAnnotation getAnnotation() {
        if (this.annotation == null) {
            return DAnnotation.EMPTY;
        }
        return this.annotation;
    }
    
    public void setPattern(final DPattern pattern) {
        this.pattern = pattern;
        this.nullable = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isNullable() {
        if (this.nullable == null) {
            this.nullable = (this.pattern.isNullable() ? Boolean.TRUE : Boolean.FALSE);
        }
        return this.nullable;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.gbind;

public abstract class Expression
{
    public static final Expression EPSILON;
    
    abstract ElementSet lastSet();
    
    abstract boolean isNullable();
    
    abstract void buildDAG(final ElementSet p0);
    
    static {
        EPSILON = new Expression() {
            ElementSet lastSet() {
                return ElementSet.EMPTY_SET;
            }
            
            boolean isNullable() {
                return true;
            }
            
            void buildDAG(final ElementSet incoming) {
            }
            
            public String toString() {
                return "-";
            }
        };
    }
}

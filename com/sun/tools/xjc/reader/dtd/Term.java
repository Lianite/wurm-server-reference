// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd;

import java.util.List;

abstract class Term
{
    static final Term EMPTY;
    
    abstract void normalize(final List<Block> p0, final boolean p1);
    
    abstract void addAllElements(final Block p0);
    
    abstract boolean isOptional();
    
    abstract boolean isRepeated();
    
    static {
        EMPTY = new Term() {
            void normalize(final List<Block> r, final boolean optional) {
            }
            
            void addAllElements(final Block b) {
            }
            
            boolean isOptional() {
                return false;
            }
            
            boolean isRepeated() {
                return false;
            }
        };
    }
}

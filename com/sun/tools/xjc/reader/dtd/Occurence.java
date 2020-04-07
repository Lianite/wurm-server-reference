// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd;

import java.util.List;

final class Occurence extends Term
{
    final Term term;
    final boolean isOptional;
    final boolean isRepeated;
    
    Occurence(final Term term, final boolean optional, final boolean repeated) {
        this.term = term;
        this.isOptional = optional;
        this.isRepeated = repeated;
    }
    
    static Term wrap(final Term t, final int occurence) {
        switch (occurence) {
            case 3: {
                return t;
            }
            case 1: {
                return new Occurence(t, false, true);
            }
            case 0: {
                return new Occurence(t, true, true);
            }
            case 2: {
                return new Occurence(t, true, false);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    void normalize(final List<Block> r, final boolean optional) {
        if (this.isRepeated) {
            final Block b = new Block(this.isOptional || optional, true);
            this.addAllElements(b);
            r.add(b);
        }
        else {
            this.term.normalize(r, optional || this.isOptional);
        }
    }
    
    void addAllElements(final Block b) {
        this.term.addAllElements(b);
    }
    
    boolean isOptional() {
        return this.isOptional || this.term.isOptional();
    }
    
    boolean isRepeated() {
        return this.isRepeated || this.term.isRepeated();
    }
}

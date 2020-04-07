// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.nc.ChoiceNameClass;
import org.kohsuke.rngom.nc.NameClass;

class Alphabet
{
    private NameClass nameClass;
    
    boolean isEmpty() {
        return this.nameClass == null;
    }
    
    void addElement(final NameClass nc) {
        if (this.nameClass == null) {
            this.nameClass = nc;
        }
        else if (nc != null) {
            this.nameClass = new ChoiceNameClass(this.nameClass, nc);
        }
    }
    
    void addAlphabet(final Alphabet a) {
        this.addElement(a.nameClass);
    }
    
    void checkOverlap(final Alphabet a) throws RestrictionViolationException {
        if (this.nameClass != null && a.nameClass != null && this.nameClass.hasOverlapWith(a.nameClass)) {
            throw new RestrictionViolationException("interleave_element_overlap");
        }
    }
}

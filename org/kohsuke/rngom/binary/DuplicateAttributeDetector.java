// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.nc.NameClass;
import java.util.ArrayList;
import java.util.List;

class DuplicateAttributeDetector
{
    private List nameClasses;
    private Alternative alternatives;
    
    DuplicateAttributeDetector() {
        this.nameClasses = new ArrayList();
        this.alternatives = null;
    }
    
    boolean addAttribute(final NameClass nc) {
        int lim = this.nameClasses.size();
        for (Alternative a = this.alternatives; a != null; a = a.parent) {
            for (int i = a.endIndex; i < lim; ++i) {
                if (nc.hasOverlapWith(this.nameClasses.get(i))) {
                    return false;
                }
            }
            lim = a.startIndex;
        }
        for (int j = 0; j < lim; ++j) {
            if (nc.hasOverlapWith(this.nameClasses.get(j))) {
                return false;
            }
        }
        this.nameClasses.add(nc);
        return true;
    }
    
    void startChoice() {
        this.alternatives = new Alternative(this.nameClasses.size(), this.alternatives);
    }
    
    void alternative() {
        this.alternatives.endIndex = this.nameClasses.size();
    }
    
    void endChoice() {
        this.alternatives = this.alternatives.parent;
    }
    
    private static class Alternative
    {
        private int startIndex;
        private int endIndex;
        private Alternative parent;
        
        private Alternative(final int startIndex, final Alternative parent) {
            this.startIndex = startIndex;
            this.endIndex = startIndex;
            this.parent = parent;
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class EmptyPattern extends Pattern
{
    EmptyPattern() {
        super(true, 0, 5);
    }
    
    boolean samePattern(final Pattern other) {
        return other instanceof EmptyPattern;
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitEmpty();
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseEmpty(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 7: {
                throw new RestrictionViolationException("data_except_contains_empty");
            }
            case 0: {
                throw new RestrictionViolationException("start_contains_empty");
            }
            default: {}
        }
    }
}

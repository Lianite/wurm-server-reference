// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class TextPattern extends Pattern
{
    TextPattern() {
        super(true, 2, 1);
    }
    
    boolean samePattern(final Pattern other) {
        return other instanceof TextPattern;
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitText();
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseText(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 7: {
                throw new RestrictionViolationException("data_except_contains_text");
            }
            case 0: {
                throw new RestrictionViolationException("start_contains_text");
            }
            case 6: {
                throw new RestrictionViolationException("list_contains_text");
            }
            default: {}
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class NotAllowedPattern extends Pattern
{
    NotAllowedPattern() {
        super(false, 0, 7);
    }
    
    boolean isNotAllowed() {
        return true;
    }
    
    boolean samePattern(final Pattern other) {
        return other.getClass() == this.getClass();
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitNotAllowed();
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseNotAllowed(this);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class ErrorPattern extends Pattern
{
    ErrorPattern() {
        super(false, 0, 3);
    }
    
    boolean samePattern(final Pattern other) {
        return other instanceof ErrorPattern;
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitError();
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseError(this);
    }
}

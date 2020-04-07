// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.kohsuke.rngom.binary.visitor.PatternFunction;

public class AfterPattern extends BinaryPattern
{
    AfterPattern(final Pattern p1, final Pattern p2) {
        super(false, Pattern.combineHashCode(41, p1.hashCode(), p2.hashCode()), p1, p2);
    }
    
    boolean isNotAllowed() {
        return this.p1.isNotAllowed();
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseAfter(this);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitAfter(this.p1, this.p2);
    }
}

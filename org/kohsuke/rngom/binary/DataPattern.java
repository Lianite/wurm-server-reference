// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;

public class DataPattern extends StringPattern
{
    private Datatype dt;
    
    DataPattern(final Datatype dt) {
        super(Pattern.combineHashCode(31, dt.hashCode()));
        this.dt = dt;
    }
    
    boolean samePattern(final Pattern other) {
        return other.getClass() == this.getClass() && this.dt.equals(((DataPattern)other).dt);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitData(this.dt);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseData(this);
    }
    
    Datatype getDatatype() {
        return this.dt;
    }
    
    boolean allowsAnyString() {
        return false;
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 0: {
                throw new RestrictionViolationException("start_contains_data");
            }
            default: {}
        }
    }
}

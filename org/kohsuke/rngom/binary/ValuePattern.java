// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;

public class ValuePattern extends StringPattern
{
    Object obj;
    Datatype dt;
    
    ValuePattern(final Datatype dt, final Object obj) {
        super(Pattern.combineHashCode(27, obj.hashCode()));
        this.dt = dt;
        this.obj = obj;
    }
    
    boolean samePattern(final Pattern other) {
        return this.getClass() == other.getClass() && other instanceof ValuePattern && this.dt.equals(((ValuePattern)other).dt) && this.dt.sameValue(this.obj, ((ValuePattern)other).obj);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitValue(this.dt, this.obj);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseValue(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 0: {
                throw new RestrictionViolationException("start_contains_value");
            }
            default: {}
        }
    }
    
    Datatype getDatatype() {
        return this.dt;
    }
    
    Object getValue() {
        return this.obj;
    }
}

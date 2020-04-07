// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

public class DataExceptPattern extends DataPattern
{
    private Pattern except;
    private Locator loc;
    
    DataExceptPattern(final Datatype dt, final Pattern except, final Locator loc) {
        super(dt);
        this.except = except;
        this.loc = loc;
    }
    
    boolean samePattern(final Pattern other) {
        return super.samePattern(other) && this.except.samePattern(((DataExceptPattern)other).except);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitDataExcept(this.getDatatype(), this.except);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseDataExcept(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        super.checkRestrictions(context, dad, alpha);
        try {
            this.except.checkRestrictions(7, null, null);
        }
        catch (RestrictionViolationException e) {
            e.maybeSetLocator(this.loc);
            throw e;
        }
    }
    
    Pattern getExcept() {
        return this.except;
    }
}

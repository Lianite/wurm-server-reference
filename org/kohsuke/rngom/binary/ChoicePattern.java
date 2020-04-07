// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class ChoicePattern extends BinaryPattern
{
    ChoicePattern(final Pattern p1, final Pattern p2) {
        super(p1.isNullable() || p2.isNullable(), Pattern.combineHashCode(11, p1.hashCode(), p2.hashCode()), p1, p2);
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        final Pattern ep1 = this.p1.expand(b);
        final Pattern ep2 = this.p2.expand(b);
        if (ep1 != this.p1 || ep2 != this.p2) {
            return b.makeChoice(ep1, ep2);
        }
        return this;
    }
    
    boolean containsChoice(final Pattern p) {
        return this.p1.containsChoice(p) || this.p2.containsChoice(p);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitChoice(this.p1, this.p2);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseChoice(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        if (dad != null) {
            dad.startChoice();
        }
        this.p1.checkRestrictions(context, dad, alpha);
        if (dad != null) {
            dad.alternative();
        }
        this.p2.checkRestrictions(context, dad, alpha);
        if (dad != null) {
            dad.endChoice();
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.SAXException;

public class OneOrMorePattern extends Pattern
{
    Pattern p;
    
    OneOrMorePattern(final Pattern p) {
        super(p.isNullable(), p.getContentType(), Pattern.combineHashCode(19, p.hashCode()));
        this.p = p;
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        final Pattern ep = this.p.expand(b);
        if (ep != this.p) {
            return b.makeOneOrMore(ep);
        }
        return this;
    }
    
    void checkRecursion(final int depth) throws SAXException {
        this.p.checkRecursion(depth);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 0: {
                throw new RestrictionViolationException("start_contains_one_or_more");
            }
            case 7: {
                throw new RestrictionViolationException("data_except_contains_one_or_more");
            }
            default: {
                this.p.checkRestrictions((context == 1) ? 2 : context, dad, alpha);
                if (context != 6 && !Pattern.contentTypeGroupable(this.p.getContentType(), this.p.getContentType())) {
                    throw new RestrictionViolationException("one_or_more_string");
                }
            }
        }
    }
    
    boolean samePattern(final Pattern other) {
        return other instanceof OneOrMorePattern && this.p == ((OneOrMorePattern)other).p;
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitOneOrMore(this.p);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseOneOrMore(this);
    }
    
    Pattern getOperand() {
        return this.p;
    }
}

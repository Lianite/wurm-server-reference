// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

public class ListPattern extends Pattern
{
    Pattern p;
    Locator locator;
    
    ListPattern(final Pattern p, final Locator locator) {
        super(false, 3, Pattern.combineHashCode(37, p.hashCode()));
        this.p = p;
        this.locator = locator;
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        final Pattern ep = this.p.expand(b);
        if (ep != this.p) {
            return b.makeList(ep, this.locator);
        }
        return this;
    }
    
    void checkRecursion(final int depth) throws SAXException {
        this.p.checkRecursion(depth);
    }
    
    boolean samePattern(final Pattern other) {
        return other instanceof ListPattern && this.p == ((ListPattern)other).p;
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitList(this.p);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseList(this);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 7: {
                throw new RestrictionViolationException("data_except_contains_list");
            }
            case 0: {
                throw new RestrictionViolationException("start_contains_list");
            }
            case 6: {
                throw new RestrictionViolationException("list_contains_list");
            }
            default: {
                try {
                    this.p.checkRestrictions(6, dad, null);
                }
                catch (RestrictionViolationException e) {
                    e.maybeSetLocator(this.locator);
                    throw e;
                }
            }
        }
    }
    
    Pattern getOperand() {
        return this.p;
    }
}

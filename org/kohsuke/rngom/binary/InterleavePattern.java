// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class InterleavePattern extends BinaryPattern
{
    InterleavePattern(final Pattern p1, final Pattern p2) {
        super(p1.isNullable() && p2.isNullable(), Pattern.combineHashCode(17, p1.hashCode(), p2.hashCode()), p1, p2);
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        final Pattern ep1 = this.p1.expand(b);
        final Pattern ep2 = this.p2.expand(b);
        if (ep1 != this.p1 || ep2 != this.p2) {
            return b.makeInterleave(ep1, ep2);
        }
        return this;
    }
    
    void checkRestrictions(int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 0: {
                throw new RestrictionViolationException("start_contains_interleave");
            }
            case 7: {
                throw new RestrictionViolationException("data_except_contains_interleave");
            }
            case 6: {
                throw new RestrictionViolationException("list_contains_interleave");
            }
            default: {
                if (context == 2) {
                    context = 4;
                }
                Alphabet a1;
                if (alpha != null && alpha.isEmpty()) {
                    a1 = alpha;
                }
                else {
                    a1 = new Alphabet();
                }
                this.p1.checkRestrictions(context, dad, a1);
                if (a1.isEmpty()) {
                    this.p2.checkRestrictions(context, dad, a1);
                }
                else {
                    final Alphabet a2 = new Alphabet();
                    this.p2.checkRestrictions(context, dad, a2);
                    a1.checkOverlap(a2);
                    if (alpha != null) {
                        if (alpha != a1) {
                            alpha.addAlphabet(a1);
                        }
                        alpha.addAlphabet(a2);
                    }
                }
                if (context != 6 && !Pattern.contentTypeGroupable(this.p1.getContentType(), this.p2.getContentType())) {
                    throw new RestrictionViolationException("interleave_string");
                }
                if (this.p1.getContentType() == 2 && this.p2.getContentType() == 2) {
                    throw new RestrictionViolationException("interleave_text_overlap");
                }
            }
        }
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitInterleave(this.p1, this.p2);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseInterleave(this);
    }
}

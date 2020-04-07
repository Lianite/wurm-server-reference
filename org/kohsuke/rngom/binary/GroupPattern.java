// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;

public class GroupPattern extends BinaryPattern
{
    GroupPattern(final Pattern p1, final Pattern p2) {
        super(p1.isNullable() && p2.isNullable(), Pattern.combineHashCode(13, p1.hashCode(), p2.hashCode()), p1, p2);
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        final Pattern ep1 = this.p1.expand(b);
        final Pattern ep2 = this.p2.expand(b);
        if (ep1 != this.p1 || ep2 != this.p2) {
            return b.makeGroup(ep1, ep2);
        }
        return this;
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        switch (context) {
            case 0: {
                throw new RestrictionViolationException("start_contains_group");
            }
            case 7: {
                throw new RestrictionViolationException("data_except_contains_group");
            }
            default: {
                super.checkRestrictions((context == 2) ? 3 : context, dad, alpha);
                if (context != 6 && !Pattern.contentTypeGroupable(this.p1.getContentType(), this.p2.getContentType())) {
                    throw new RestrictionViolationException("group_string");
                }
            }
        }
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitGroup(this.p1, this.p2);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseGroup(this);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.xml.sax.SAXException;

public abstract class BinaryPattern extends Pattern
{
    protected final Pattern p1;
    protected final Pattern p2;
    
    BinaryPattern(final boolean nullable, final int hc, final Pattern p1, final Pattern p2) {
        super(nullable, Math.max(p1.getContentType(), p2.getContentType()), hc);
        this.p1 = p1;
        this.p2 = p2;
    }
    
    void checkRecursion(final int depth) throws SAXException {
        this.p1.checkRecursion(depth);
        this.p2.checkRecursion(depth);
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        this.p1.checkRestrictions(context, dad, alpha);
        this.p2.checkRestrictions(context, dad, alpha);
    }
    
    boolean samePattern(final Pattern other) {
        if (this.getClass() != other.getClass()) {
            return false;
        }
        final BinaryPattern b = (BinaryPattern)other;
        return this.p1 == b.p1 && this.p2 == b.p2;
    }
    
    public final Pattern getOperand1() {
        return this.p1;
    }
    
    public final Pattern getOperand2() {
        return this.p2;
    }
    
    public final void fillChildren(final Collection col) {
        this.fillChildren(this.getClass(), this.p1, col);
        this.fillChildren(this.getClass(), this.p2, col);
    }
    
    public final Pattern[] getChildren() {
        final List lst = new ArrayList();
        this.fillChildren(lst);
        return lst.toArray(new Pattern[lst.size()]);
    }
    
    private void fillChildren(final Class c, final Pattern p, final Collection col) {
        if (p.getClass() == c) {
            final BinaryPattern bp = (BinaryPattern)p;
            bp.fillChildren(c, bp.p1, col);
            bp.fillChildren(c, bp.p2, col);
        }
        else {
            col.add(p);
        }
    }
}

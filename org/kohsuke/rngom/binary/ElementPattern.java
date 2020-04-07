// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.kohsuke.rngom.nc.NameClass;

public final class ElementPattern extends Pattern
{
    private Pattern p;
    private NameClass origNameClass;
    private NameClass nameClass;
    private boolean expanded;
    private boolean checkedRestrictions;
    private Locator loc;
    
    ElementPattern(final NameClass nameClass, final Pattern p, final Locator loc) {
        super(false, 1, Pattern.combineHashCode(23, nameClass.hashCode(), p.hashCode()));
        this.expanded = false;
        this.checkedRestrictions = false;
        this.nameClass = nameClass;
        this.origNameClass = nameClass;
        this.p = p;
        this.loc = loc;
    }
    
    void checkRestrictions(final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException {
        if (alpha != null) {
            alpha.addElement(this.origNameClass);
        }
        if (this.checkedRestrictions) {
            return;
        }
        switch (context) {
            case 7: {
                throw new RestrictionViolationException("data_except_contains_element");
            }
            case 6: {
                throw new RestrictionViolationException("list_contains_element");
            }
            case 5: {
                throw new RestrictionViolationException("attribute_contains_element");
            }
            default: {
                this.checkedRestrictions = true;
                try {
                    this.p.checkRestrictions(1, new DuplicateAttributeDetector(), null);
                }
                catch (RestrictionViolationException e) {
                    this.checkedRestrictions = false;
                    e.maybeSetLocator(this.loc);
                    throw e;
                }
            }
        }
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        if (!this.expanded) {
            this.expanded = true;
            this.p = this.p.expand(b);
            if (this.p.isNotAllowed()) {
                this.nameClass = NameClass.NULL;
            }
        }
        return this;
    }
    
    boolean samePattern(final Pattern other) {
        if (!(other instanceof ElementPattern)) {
            return false;
        }
        final ElementPattern ep = (ElementPattern)other;
        return this.nameClass.equals(ep.nameClass) && this.p == ep.p;
    }
    
    void checkRecursion(final int depth) throws SAXException {
        this.p.checkRecursion(depth + 1);
    }
    
    public void accept(final PatternVisitor visitor) {
        visitor.visitElement(this.nameClass, this.p);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseElement(this);
    }
    
    void setContent(final Pattern p) {
        this.p = p;
    }
    
    public Pattern getContent() {
        return this.p;
    }
    
    public NameClass getNameClass() {
        return this.nameClass;
    }
    
    public Locator getLocator() {
        return this.loc;
    }
}

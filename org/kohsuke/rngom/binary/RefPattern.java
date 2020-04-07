// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.kohsuke.rngom.binary.visitor.PatternFunction;
import org.kohsuke.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;

public class RefPattern extends Pattern
{
    private Pattern p;
    private Locator refLoc;
    private String name;
    private int checkRecursionDepth;
    private boolean combineImplicit;
    private byte combineType;
    private byte replacementStatus;
    private boolean expanded;
    static final byte REPLACEMENT_KEEP = 0;
    static final byte REPLACEMENT_REQUIRE = 1;
    static final byte REPLACEMENT_IGNORE = 2;
    static final byte COMBINE_NONE = 0;
    static final byte COMBINE_CHOICE = 1;
    static final byte COMBINE_INTERLEAVE = 2;
    
    RefPattern(final String name) {
        this.checkRecursionDepth = -1;
        this.combineImplicit = false;
        this.combineType = 0;
        this.replacementStatus = 0;
        this.expanded = false;
        this.name = name;
    }
    
    Pattern getPattern() {
        return this.p;
    }
    
    void setPattern(final Pattern p) {
        this.p = p;
    }
    
    Locator getRefLocator() {
        return this.refLoc;
    }
    
    void setRefLocator(final Locator loc) {
        this.refLoc = loc;
    }
    
    void checkRecursion(final int depth) throws SAXException {
        if (this.checkRecursionDepth == -1) {
            this.checkRecursionDepth = depth;
            this.p.checkRecursion(depth);
            this.checkRecursionDepth = -2;
        }
        else if (depth == this.checkRecursionDepth) {
            throw new SAXParseException(SchemaBuilderImpl.localizer.message("recursive_reference", this.name), this.refLoc);
        }
    }
    
    Pattern expand(final SchemaPatternBuilder b) {
        if (!this.expanded) {
            this.p = this.p.expand(b);
            this.expanded = true;
        }
        return this.p;
    }
    
    boolean samePattern(final Pattern other) {
        return false;
    }
    
    public void accept(final PatternVisitor visitor) {
        this.p.accept(visitor);
    }
    
    public Object apply(final PatternFunction f) {
        return f.caseRef(this);
    }
    
    byte getReplacementStatus() {
        return this.replacementStatus;
    }
    
    void setReplacementStatus(final byte replacementStatus) {
        this.replacementStatus = replacementStatus;
    }
    
    boolean isCombineImplicit() {
        return this.combineImplicit;
    }
    
    void setCombineImplicit() {
        this.combineImplicit = true;
    }
    
    byte getCombineType() {
        return this.combineType;
    }
    
    void setCombineType(final byte combineType) {
        this.combineType = combineType;
    }
    
    String getName() {
        return this.name;
    }
}

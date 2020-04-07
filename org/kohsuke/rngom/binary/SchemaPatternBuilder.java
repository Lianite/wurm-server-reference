// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;
import org.kohsuke.rngom.nc.NameClass;

public class SchemaPatternBuilder extends PatternBuilder
{
    private boolean idTypes;
    private final Pattern unexpandedNotAllowed;
    private final TextPattern text;
    private final PatternInterner schemaInterner;
    
    public SchemaPatternBuilder() {
        this.unexpandedNotAllowed = new NotAllowedPattern() {
            boolean isNotAllowed() {
                return false;
            }
            
            Pattern expand(final SchemaPatternBuilder b) {
                return b.makeNotAllowed();
            }
        };
        this.text = new TextPattern();
        this.schemaInterner = new PatternInterner();
    }
    
    public boolean hasIdTypes() {
        return this.idTypes;
    }
    
    Pattern makeElement(final NameClass nameClass, final Pattern content, final Locator loc) {
        final Pattern p = new ElementPattern(nameClass, content, loc);
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeAttribute(final NameClass nameClass, final Pattern value, final Locator loc) {
        if (value == this.notAllowed) {
            return value;
        }
        final Pattern p = new AttributePattern(nameClass, value, loc);
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeData(final Datatype dt) {
        this.noteDatatype(dt);
        final Pattern p = new DataPattern(dt);
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeDataExcept(final Datatype dt, final Pattern except, final Locator loc) {
        this.noteDatatype(dt);
        final Pattern p = new DataExceptPattern(dt, except, loc);
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeValue(final Datatype dt, final Object obj) {
        this.noteDatatype(dt);
        final Pattern p = new ValuePattern(dt, obj);
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeText() {
        return this.text;
    }
    
    Pattern makeOneOrMore(final Pattern p) {
        if (p == this.text) {
            return p;
        }
        return super.makeOneOrMore(p);
    }
    
    Pattern makeUnexpandedNotAllowed() {
        return this.unexpandedNotAllowed;
    }
    
    Pattern makeError() {
        final Pattern p = new ErrorPattern();
        return this.schemaInterner.intern(p);
    }
    
    Pattern makeChoice(final Pattern p1, final Pattern p2) {
        if (p1 == this.notAllowed || p1 == p2) {
            return p2;
        }
        if (p2 == this.notAllowed) {
            return p1;
        }
        return super.makeChoice(p1, p2);
    }
    
    Pattern makeList(final Pattern p, final Locator loc) {
        if (p == this.notAllowed) {
            return p;
        }
        final Pattern p2 = new ListPattern(p, loc);
        return this.schemaInterner.intern(p2);
    }
    
    Pattern makeMixed(final Pattern p) {
        return this.makeInterleave(this.text, p);
    }
    
    private void noteDatatype(final Datatype dt) {
        if (dt.getIdType() != 0) {
            this.idTypes = true;
        }
    }
}

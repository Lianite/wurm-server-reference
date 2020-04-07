// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import org.kohsuke.rngom.ast.builder.Scope;

public class ScopeHost extends GrammarSectionHost implements Scope
{
    protected final Scope lhs;
    protected final Scope rhs;
    
    protected ScopeHost(final Scope lhs, final Scope rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public ParsedPattern makeParentRef(final String name, final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeParentRef(name, loc.lhs, anno.lhs), this.rhs.makeParentRef(name, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeRef(final String name, final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeRef(name, loc.lhs, anno.lhs), this.rhs.makeRef(name, loc.rhs, anno.rhs));
    }
}

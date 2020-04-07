// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.Grammar;

public class GrammarHost extends ScopeHost implements Grammar
{
    final Grammar lhs;
    final Grammar rhs;
    
    public GrammarHost(final Grammar lhs, final Grammar rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public ParsedPattern endGrammar(final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.endGrammar(loc.lhs, anno.lhs), this.rhs.endGrammar(loc.rhs, anno.rhs));
    }
}

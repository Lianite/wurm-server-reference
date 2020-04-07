// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import org.kohsuke.rngom.ast.builder.Include;

public class IncludeHost extends GrammarSectionHost implements Include
{
    private final Include lhs;
    private final Include rhs;
    
    IncludeHost(final Include lhs, final Include rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void endInclude(final Parseable current, final String uri, final String ns, final Location _loc, final Annotations _anno) throws BuildException, IllegalSchemaException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        this.lhs.endInclude(current, uri, ns, loc.lhs, anno.lhs);
        this.rhs.endInclude(current, uri, ns, loc.rhs, anno.rhs);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import org.kohsuke.rngom.ast.builder.Div;

public class DivHost extends GrammarSectionHost implements Div
{
    private final Div lhs;
    private final Div rhs;
    
    DivHost(final Div lhs, final Div rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void endDiv(final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        this.lhs.endDiv(loc.lhs, anno.lhs);
        this.rhs.endDiv(loc.rhs, anno.rhs);
    }
}

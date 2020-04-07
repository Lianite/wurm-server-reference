// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.parse.Context;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public interface DataPatternBuilder<P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>>
{
    void addParam(final String p0, final String p1, final Context p2, final String p3, final L p4, final A p5) throws BuildException;
    
    void annotation(final E p0);
    
    P makePattern(final L p0, final A p1) throws BuildException;
    
    P makePattern(final P p0, final L p1, final A p2) throws BuildException;
}

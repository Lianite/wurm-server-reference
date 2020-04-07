// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public interface Scope<P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>> extends GrammarSection<P, E, L, A, CL>
{
    P makeParentRef(final String p0, final L p1, final A p2) throws BuildException;
    
    P makeRef(final String p0, final L p1, final A p2) throws BuildException;
}

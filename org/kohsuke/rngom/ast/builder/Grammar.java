// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public interface Grammar<P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>> extends GrammarSection<P, E, L, A, CL>, Scope<P, E, L, A, CL>
{
    P endGrammar(final L p0, final A p1) throws BuildException;
}
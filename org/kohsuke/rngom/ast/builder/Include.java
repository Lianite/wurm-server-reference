// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;

public interface Include<P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>> extends GrammarSection<P, E, L, A, CL>
{
    void endInclude(final Parseable p0, final String p1, final String p2, final L p3, final A p4) throws BuildException, IllegalSchemaException;
}

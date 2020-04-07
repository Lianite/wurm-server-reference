// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import java.util.List;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedNameClass;

public interface NameClassBuilder<N extends ParsedNameClass, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>>
{
    N annotate(final N p0, final A p1) throws BuildException;
    
    N annotateAfter(final N p0, final E p1) throws BuildException;
    
    N commentAfter(final N p0, final CL p1) throws BuildException;
    
    N makeChoice(final List<N> p0, final L p1, final A p2);
    
    N makeName(final String p0, final String p1, final String p2, final L p3, final A p4);
    
    N makeNsName(final String p0, final L p1, final A p2);
    
    N makeNsName(final String p0, final N p1, final L p2, final A p3);
    
    N makeAnyName(final L p0, final A p1);
    
    N makeAnyName(final N p0, final L p1, final A p2);
    
    N makeErrorNameClass();
}

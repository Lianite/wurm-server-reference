// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;

public interface Annotations<E extends ParsedElementAnnotation, L extends Location, CL extends CommentList<L>>
{
    void addAttribute(final String p0, final String p1, final String p2, final String p3, final L p4) throws BuildException;
    
    void addElement(final E p0) throws BuildException;
    
    void addComment(final CL p0) throws BuildException;
    
    void addLeadingComment(final CL p0) throws BuildException;
}

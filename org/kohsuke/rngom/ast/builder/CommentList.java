// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.ast.om.Location;

public interface CommentList<L extends Location>
{
    void addComment(final String p0, final L p1) throws BuildException;
}

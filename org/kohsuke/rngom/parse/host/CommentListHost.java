// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.CommentList;

class CommentListHost extends Base implements CommentList
{
    final CommentList lhs;
    final CommentList rhs;
    
    CommentListHost(final CommentList lhs, final CommentList rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void addComment(final String value, final Location _loc) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        if (this.lhs != null) {
            this.lhs.addComment(value, loc.lhs);
        }
        if (this.rhs != null) {
            this.rhs.addComment(value, loc.rhs);
        }
    }
}

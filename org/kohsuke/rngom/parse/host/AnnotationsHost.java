// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.Annotations;

class AnnotationsHost extends Base implements Annotations
{
    final Annotations lhs;
    final Annotations rhs;
    
    AnnotationsHost(final Annotations lhs, final Annotations rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void addAttribute(final String ns, final String localName, final String prefix, final String value, final Location _loc) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        this.lhs.addAttribute(ns, localName, prefix, value, loc.lhs);
        this.rhs.addAttribute(ns, localName, prefix, value, loc.rhs);
    }
    
    public void addComment(final CommentList _comments) throws BuildException {
        final CommentListHost comments = (CommentListHost)_comments;
        this.lhs.addComment((comments == null) ? null : comments.lhs);
        this.rhs.addComment((comments == null) ? null : comments.rhs);
    }
    
    public void addElement(final ParsedElementAnnotation _ea) throws BuildException {
        final ParsedElementAnnotationHost ea = (ParsedElementAnnotationHost)_ea;
        this.lhs.addElement(ea.lhs);
        this.rhs.addElement(ea.rhs);
    }
    
    public void addLeadingComment(final CommentList _comments) throws BuildException {
        final CommentListHost comments = (CommentListHost)_comments;
        this.lhs.addLeadingComment(comments.lhs);
        this.rhs.addLeadingComment(comments.rhs);
    }
}

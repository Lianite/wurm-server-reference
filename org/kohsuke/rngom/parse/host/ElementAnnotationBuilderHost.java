// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;

final class ElementAnnotationBuilderHost extends AnnotationsHost implements ElementAnnotationBuilder
{
    final ElementAnnotationBuilder lhs;
    final ElementAnnotationBuilder rhs;
    
    ElementAnnotationBuilderHost(final ElementAnnotationBuilder lhs, final ElementAnnotationBuilder rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void addText(final String value, final Location _loc, final CommentList _comments) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final CommentListHost comments = (CommentListHost)_comments;
        this.lhs.addText(value, loc.lhs, (comments == null) ? null : comments.lhs);
        this.rhs.addText(value, loc.rhs, (comments == null) ? null : comments.rhs);
    }
    
    public ParsedElementAnnotation makeElementAnnotation() throws BuildException {
        return new ParsedElementAnnotationHost(this.lhs.makeElementAnnotation(), this.rhs.makeElementAnnotation());
    }
}

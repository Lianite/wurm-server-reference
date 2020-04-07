// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.Include;
import org.kohsuke.rngom.ast.builder.Div;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.GrammarSection;

public class GrammarSectionHost extends Base implements GrammarSection
{
    private final GrammarSection lhs;
    private final GrammarSection rhs;
    
    GrammarSectionHost(final GrammarSection lhs, final GrammarSection rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        if (lhs == null || rhs == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public void define(final String name, final Combine combine, final ParsedPattern _pattern, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost pattern = (ParsedPatternHost)_pattern;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        this.lhs.define(name, combine, pattern.lhs, loc.lhs, anno.lhs);
        this.rhs.define(name, combine, pattern.rhs, loc.rhs, anno.rhs);
    }
    
    public Div makeDiv() {
        return new DivHost(this.lhs.makeDiv(), this.rhs.makeDiv());
    }
    
    public Include makeInclude() {
        final Include l = this.lhs.makeInclude();
        if (l == null) {
            return null;
        }
        return new IncludeHost(l, this.rhs.makeInclude());
    }
    
    public void topLevelAnnotation(final ParsedElementAnnotation _ea) throws BuildException {
        final ParsedElementAnnotationHost ea = (ParsedElementAnnotationHost)_ea;
        this.lhs.topLevelAnnotation((ea == null) ? null : ea.lhs);
        this.rhs.topLevelAnnotation((ea == null) ? null : ea.rhs);
    }
    
    public void topLevelComment(final CommentList _comments) throws BuildException {
        final CommentListHost comments = (CommentListHost)_comments;
        this.lhs.topLevelComment((comments == null) ? null : comments.lhs);
        this.rhs.topLevelComment((comments == null) ? null : comments.rhs);
    }
}

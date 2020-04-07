// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import java.util.ArrayList;
import org.kohsuke.rngom.ast.om.Location;
import java.util.List;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;

final class NameClassBuilderHost extends Base implements NameClassBuilder
{
    final NameClassBuilder lhs;
    final NameClassBuilder rhs;
    
    NameClassBuilderHost(final NameClassBuilder lhs, final NameClassBuilder rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public ParsedNameClass annotate(final ParsedNameClass _nc, final Annotations _anno) throws BuildException {
        final ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.annotate(nc.lhs, anno.lhs), this.rhs.annotate(nc.rhs, anno.rhs));
    }
    
    public ParsedNameClass annotateAfter(final ParsedNameClass _nc, final ParsedElementAnnotation _e) throws BuildException {
        final ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
        final ParsedElementAnnotationHost e = (ParsedElementAnnotationHost)_e;
        return new ParsedNameClassHost(this.lhs.annotateAfter(nc.lhs, e.lhs), this.rhs.annotateAfter(nc.rhs, e.rhs));
    }
    
    public ParsedNameClass commentAfter(final ParsedNameClass _nc, final CommentList _comments) throws BuildException {
        final ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
        final CommentListHost comments = (CommentListHost)_comments;
        return new ParsedNameClassHost(this.lhs.commentAfter(nc.lhs, (comments == null) ? null : comments.lhs), this.rhs.commentAfter(nc.rhs, (comments == null) ? null : comments.rhs));
    }
    
    public ParsedNameClass makeChoice(final List _nameClasses, final Location _loc, final Annotations _anno) {
        final List<ParsedNameClass> lnc = new ArrayList<ParsedNameClass>();
        final List<ParsedNameClass> rnc = new ArrayList<ParsedNameClass>();
        for (int i = 0; i < _nameClasses.size(); ++i) {
            lnc.add(_nameClasses.get(i).lhs);
            rnc.add(_nameClasses.get(i).rhs);
        }
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeChoice(lnc, loc.lhs, anno.lhs), this.rhs.makeChoice(rnc, loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeName(final String ns, final String localName, final String prefix, final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeName(ns, localName, prefix, loc.lhs, anno.lhs), this.rhs.makeName(ns, localName, prefix, loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeNsName(final String ns, final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeNsName(ns, loc.lhs, anno.lhs), this.rhs.makeNsName(ns, loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeNsName(final String ns, final ParsedNameClass _except, final Location _loc, final Annotations _anno) {
        final ParsedNameClassHost except = (ParsedNameClassHost)_except;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeNsName(ns, except.lhs, loc.lhs, anno.lhs), this.rhs.makeNsName(ns, except.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeAnyName(final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeAnyName(loc.lhs, anno.lhs), this.rhs.makeAnyName(loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeAnyName(final ParsedNameClass _except, final Location _loc, final Annotations _anno) {
        final ParsedNameClassHost except = (ParsedNameClassHost)_except;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedNameClassHost(this.lhs.makeAnyName(except.lhs, loc.lhs, anno.lhs), this.rhs.makeAnyName(except.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedNameClass makeErrorNameClass() {
        return new ParsedNameClassHost(this.lhs.makeErrorNameClass(), this.rhs.makeErrorNameClass());
    }
}

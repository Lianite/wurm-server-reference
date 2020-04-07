// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.builder.Grammar;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.parse.Context;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;

public class SchemaBuilderHost extends Base implements SchemaBuilder
{
    final SchemaBuilder lhs;
    final SchemaBuilder rhs;
    
    public SchemaBuilderHost(final SchemaBuilder lhs, final SchemaBuilder rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public ParsedPattern annotate(final ParsedPattern _p, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final AnnotationsHost a = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.annotate(p.lhs, a.lhs), this.rhs.annotate(p.lhs, a.lhs));
    }
    
    public ParsedPattern annotateAfter(final ParsedPattern _p, final ParsedElementAnnotation _e) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final ParsedElementAnnotationHost e = (ParsedElementAnnotationHost)_e;
        return new ParsedPatternHost(this.lhs.annotateAfter(p.lhs, e.lhs), this.rhs.annotateAfter(p.rhs, e.rhs));
    }
    
    public ParsedPattern commentAfter(final ParsedPattern _p, final CommentList _comments) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final CommentListHost comments = (CommentListHost)_comments;
        return new ParsedPatternHost(this.lhs.commentAfter(p.lhs, (comments == null) ? null : comments.lhs), this.rhs.commentAfter(p.rhs, (comments == null) ? null : comments.rhs));
    }
    
    public ParsedPattern expandPattern(final ParsedPattern _p) throws BuildException, IllegalSchemaException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        return new ParsedPatternHost(this.lhs.expandPattern(p.lhs), this.rhs.expandPattern(p.rhs));
    }
    
    public NameClassBuilder getNameClassBuilder() throws BuildException {
        return new NameClassBuilderHost(this.lhs.getNameClassBuilder(), this.rhs.getNameClassBuilder());
    }
    
    public Annotations makeAnnotations(final CommentList _comments, final Context context) {
        final CommentListHost comments = (CommentListHost)_comments;
        final Annotations l = this.lhs.makeAnnotations((comments != null) ? comments.lhs : null, context);
        final Annotations r = this.rhs.makeAnnotations((comments != null) ? comments.rhs : null, context);
        if (l == null || r == null) {
            throw new IllegalArgumentException("annotations cannot be null");
        }
        return new AnnotationsHost(l, r);
    }
    
    public ParsedPattern makeAttribute(final ParsedNameClass _nc, final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeAttribute(nc.lhs, p.lhs, loc.lhs, anno.lhs), this.rhs.makeAttribute(nc.rhs, p.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeChoice(final List patterns, final Location _loc, final Annotations _anno) throws BuildException {
        final List<ParsedPattern> lp = new ArrayList<ParsedPattern>();
        final List<ParsedPattern> rp = new ArrayList<ParsedPattern>();
        for (int i = 0; i < patterns.size(); ++i) {
            lp.add(patterns.get(i).lhs);
            rp.add(patterns.get(i).rhs);
        }
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeChoice(lp, loc.lhs, anno.lhs), this.rhs.makeChoice(rp, loc.rhs, anno.rhs));
    }
    
    public CommentList makeCommentList() {
        return new CommentListHost(this.lhs.makeCommentList(), this.rhs.makeCommentList());
    }
    
    public DataPatternBuilder makeDataPatternBuilder(final String datatypeLibrary, final String type, final Location _loc) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        return new DataPatternBuilderHost(this.lhs.makeDataPatternBuilder(datatypeLibrary, type, loc.lhs), this.rhs.makeDataPatternBuilder(datatypeLibrary, type, loc.rhs));
    }
    
    public ParsedPattern makeElement(final ParsedNameClass _nc, final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedNameClassHost nc = (ParsedNameClassHost)_nc;
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeElement(nc.lhs, p.lhs, loc.lhs, anno.lhs), this.rhs.makeElement(nc.rhs, p.rhs, loc.rhs, anno.rhs));
    }
    
    public ElementAnnotationBuilder makeElementAnnotationBuilder(final String ns, final String localName, final String prefix, final Location _loc, final CommentList _comments, final Context context) {
        final LocationHost loc = this.cast(_loc);
        final CommentListHost comments = (CommentListHost)_comments;
        return new ElementAnnotationBuilderHost(this.lhs.makeElementAnnotationBuilder(ns, localName, prefix, loc.lhs, (comments == null) ? null : comments.lhs, context), this.rhs.makeElementAnnotationBuilder(ns, localName, prefix, loc.rhs, (comments == null) ? null : comments.rhs, context));
    }
    
    public ParsedPattern makeEmpty(final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeEmpty(loc.lhs, anno.lhs), this.rhs.makeEmpty(loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeErrorPattern() {
        return new ParsedPatternHost(this.lhs.makeErrorPattern(), this.rhs.makeErrorPattern());
    }
    
    public ParsedPattern makeExternalRef(final Parseable current, final String uri, final String ns, final Scope _scope, final Location _loc, final Annotations _anno) throws BuildException, IllegalSchemaException {
        final ScopeHost scope = (ScopeHost)_scope;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeExternalRef(current, uri, ns, scope.lhs, loc.lhs, anno.lhs), this.rhs.makeExternalRef(current, uri, ns, scope.rhs, loc.rhs, anno.rhs));
    }
    
    public Grammar makeGrammar(final Scope _parent) {
        final ScopeHost parent = (ScopeHost)_parent;
        return new GrammarHost(this.lhs.makeGrammar((parent != null) ? parent.lhs : null), this.rhs.makeGrammar((parent != null) ? parent.rhs : null));
    }
    
    public ParsedPattern makeGroup(final List patterns, final Location _loc, final Annotations _anno) throws BuildException {
        final List<ParsedPattern> lp = new ArrayList<ParsedPattern>();
        final List<ParsedPattern> rp = new ArrayList<ParsedPattern>();
        for (int i = 0; i < patterns.size(); ++i) {
            lp.add(patterns.get(i).lhs);
            rp.add(patterns.get(i).rhs);
        }
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeGroup(lp, loc.lhs, anno.lhs), this.rhs.makeGroup(rp, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeInterleave(final List patterns, final Location _loc, final Annotations _anno) throws BuildException {
        final List<ParsedPattern> lp = new ArrayList<ParsedPattern>();
        final List<ParsedPattern> rp = new ArrayList<ParsedPattern>();
        for (int i = 0; i < patterns.size(); ++i) {
            lp.add(patterns.get(i).lhs);
            rp.add(patterns.get(i).rhs);
        }
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeInterleave(lp, loc.lhs, anno.lhs), this.rhs.makeInterleave(rp, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeList(final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeList(p.lhs, loc.lhs, anno.lhs), this.rhs.makeList(p.rhs, loc.rhs, anno.rhs));
    }
    
    public Location makeLocation(final String systemId, final int lineNumber, final int columnNumber) {
        return new LocationHost(this.lhs.makeLocation(systemId, lineNumber, columnNumber), this.rhs.makeLocation(systemId, lineNumber, columnNumber));
    }
    
    public ParsedPattern makeMixed(final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeMixed(p.lhs, loc.lhs, anno.lhs), this.rhs.makeMixed(p.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeNotAllowed(final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeNotAllowed(loc.lhs, anno.lhs), this.rhs.makeNotAllowed(loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeOneOrMore(final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeOneOrMore(p.lhs, loc.lhs, anno.lhs), this.rhs.makeOneOrMore(p.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeZeroOrMore(final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeZeroOrMore(p.lhs, loc.lhs, anno.lhs), this.rhs.makeZeroOrMore(p.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeOptional(final ParsedPattern _p, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost p = (ParsedPatternHost)_p;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeOptional(p.lhs, loc.lhs, anno.lhs), this.rhs.makeOptional(p.rhs, loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeText(final Location _loc, final Annotations _anno) {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeText(loc.lhs, anno.lhs), this.rhs.makeText(loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makeValue(final String datatypeLibrary, final String type, final String value, final Context c, final String ns, final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makeValue(datatypeLibrary, type, value, c, ns, loc.lhs, anno.lhs), this.rhs.makeValue(datatypeLibrary, type, value, c, ns, loc.rhs, anno.rhs));
    }
    
    public boolean usesComments() {
        return this.lhs.usesComments() || this.rhs.usesComments();
    }
}

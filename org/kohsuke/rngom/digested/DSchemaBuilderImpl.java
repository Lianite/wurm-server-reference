// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.om.ParsedNameClass;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;
import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.builder.Grammar;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.parse.Context;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import java.util.Iterator;
import java.util.List;
import org.kohsuke.rngom.ast.builder.BuildException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.kohsuke.rngom.nc.NameClassBuilderImpl;
import org.w3c.dom.Document;
import org.kohsuke.rngom.ast.builder.NameClassBuilder;
import org.kohsuke.rngom.ast.util.LocatorImpl;
import org.kohsuke.rngom.nc.NameClass;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;

public class DSchemaBuilderImpl implements SchemaBuilder<NameClass, DPattern, ElementWrapper, LocatorImpl, Annotation, CommentListImpl>
{
    private final NameClassBuilder ncb;
    private final Document dom;
    
    public DSchemaBuilderImpl() {
        this.ncb = new NameClassBuilderImpl();
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            this.dom = dbf.newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new InternalError(e.getMessage());
        }
    }
    
    public NameClassBuilder getNameClassBuilder() throws BuildException {
        return this.ncb;
    }
    
    static DPattern wrap(final DPattern p, final LocatorImpl loc, final Annotation anno) {
        p.location = loc;
        if (anno != null) {
            p.annotation = anno.getResult();
        }
        return p;
    }
    
    static DContainerPattern addAll(final DContainerPattern parent, final List<DPattern> children) {
        for (final DPattern c : children) {
            parent.add(c);
        }
        return parent;
    }
    
    static DUnaryPattern addBody(final DUnaryPattern parent, final ParsedPattern _body, final LocatorImpl loc) {
        parent.setChild((DPattern)_body);
        return parent;
    }
    
    public DPattern makeChoice(final List<DPattern> patterns, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addAll(new DChoicePattern(), patterns), loc, anno);
    }
    
    public DPattern makeInterleave(final List<DPattern> patterns, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addAll(new DInterleavePattern(), patterns), loc, anno);
    }
    
    public DPattern makeGroup(final List<DPattern> patterns, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addAll(new DGroupPattern(), patterns), loc, anno);
    }
    
    public DPattern makeOneOrMore(final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DOneOrMorePattern(), p, loc), loc, anno);
    }
    
    public DPattern makeZeroOrMore(final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DZeroOrMorePattern(), p, loc), loc, anno);
    }
    
    public DPattern makeOptional(final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DOptionalPattern(), p, loc), loc, anno);
    }
    
    public DPattern makeList(final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DListPattern(), p, loc), loc, anno);
    }
    
    public DPattern makeMixed(final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DMixedPattern(), p, loc), loc, anno);
    }
    
    public DPattern makeEmpty(final LocatorImpl loc, final Annotation anno) {
        return wrap(new DEmptyPattern(), loc, anno);
    }
    
    public DPattern makeNotAllowed(final LocatorImpl loc, final Annotation anno) {
        return wrap(new DNotAllowedPattern(), loc, anno);
    }
    
    public DPattern makeText(final LocatorImpl loc, final Annotation anno) {
        return wrap(new DTextPattern(), loc, anno);
    }
    
    public DPattern makeAttribute(final NameClass nc, final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DAttributePattern(nc), p, loc), loc, anno);
    }
    
    public DPattern makeElement(final NameClass nc, final DPattern p, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(addBody(new DElementPattern(nc), p, loc), loc, anno);
    }
    
    public DataPatternBuilder makeDataPatternBuilder(final String datatypeLibrary, final String type, final LocatorImpl loc) throws BuildException {
        return new DataPatternBuilderImpl(datatypeLibrary, type, loc);
    }
    
    public DPattern makeValue(final String datatypeLibrary, final String type, final String value, final Context c, final String ns, final LocatorImpl loc, final Annotation anno) throws BuildException {
        return wrap(new DValuePattern(datatypeLibrary, type, value, c.copy(), ns), loc, anno);
    }
    
    public Grammar makeGrammar(final Scope parent) {
        return new GrammarBuilderImpl(new DGrammarPattern(), parent, this);
    }
    
    public DPattern annotate(final DPattern p, final Annotation anno) throws BuildException {
        return p;
    }
    
    public DPattern annotateAfter(final DPattern p, final ElementWrapper e) throws BuildException {
        return p;
    }
    
    public DPattern commentAfter(final DPattern p, final CommentListImpl comments) throws BuildException {
        return p;
    }
    
    public DPattern makeExternalRef(final Parseable current, final String uri, final String ns, final Scope<DPattern, ElementWrapper, LocatorImpl, Annotation, CommentListImpl> scope, final LocatorImpl loc, final Annotation anno) throws BuildException, IllegalSchemaException {
        return null;
    }
    
    public LocatorImpl makeLocation(final String systemId, final int lineNumber, final int columnNumber) {
        return new LocatorImpl(systemId, lineNumber, columnNumber);
    }
    
    public Annotation makeAnnotations(final CommentListImpl comments, final Context context) {
        return new Annotation();
    }
    
    public ElementAnnotationBuilder makeElementAnnotationBuilder(final String ns, final String localName, final String prefix, final LocatorImpl loc, final CommentListImpl comments, final Context context) {
        String qname;
        if (prefix == null) {
            qname = localName;
        }
        else {
            qname = prefix + ':' + localName;
        }
        return new ElementAnnotationBuilderImpl(this.dom.createElementNS(ns, qname));
    }
    
    public CommentListImpl makeCommentList() {
        return null;
    }
    
    public DPattern makeErrorPattern() {
        return new DNotAllowedPattern();
    }
    
    public boolean usesComments() {
        return false;
    }
    
    public DPattern expandPattern(final DPattern p) throws BuildException, IllegalSchemaException {
        return p;
    }
}

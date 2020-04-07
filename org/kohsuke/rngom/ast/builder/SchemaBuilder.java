// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.parse.Context;
import java.util.List;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.om.ParsedNameClass;

public interface SchemaBuilder<N extends ParsedNameClass, P extends ParsedPattern, E extends ParsedElementAnnotation, L extends Location, A extends Annotations<E, L, CL>, CL extends CommentList<L>>
{
    NameClassBuilder<N, E, L, A, CL> getNameClassBuilder() throws BuildException;
    
    P makeChoice(final List<P> p0, final L p1, final A p2) throws BuildException;
    
    P makeInterleave(final List<P> p0, final L p1, final A p2) throws BuildException;
    
    P makeGroup(final List<P> p0, final L p1, final A p2) throws BuildException;
    
    P makeOneOrMore(final P p0, final L p1, final A p2) throws BuildException;
    
    P makeZeroOrMore(final P p0, final L p1, final A p2) throws BuildException;
    
    P makeOptional(final P p0, final L p1, final A p2) throws BuildException;
    
    P makeList(final P p0, final L p1, final A p2) throws BuildException;
    
    P makeMixed(final P p0, final L p1, final A p2) throws BuildException;
    
    P makeEmpty(final L p0, final A p1);
    
    P makeNotAllowed(final L p0, final A p1);
    
    P makeText(final L p0, final A p1);
    
    P makeAttribute(final N p0, final P p1, final L p2, final A p3) throws BuildException;
    
    P makeElement(final N p0, final P p1, final L p2, final A p3) throws BuildException;
    
    DataPatternBuilder makeDataPatternBuilder(final String p0, final String p1, final L p2) throws BuildException;
    
    P makeValue(final String p0, final String p1, final String p2, final Context p3, final String p4, final L p5, final A p6) throws BuildException;
    
    Grammar<P, E, L, A, CL> makeGrammar(final Scope<P, E, L, A, CL> p0);
    
    P annotate(final P p0, final A p1) throws BuildException;
    
    P annotateAfter(final P p0, final E p1) throws BuildException;
    
    P commentAfter(final P p0, final CL p1) throws BuildException;
    
    P makeExternalRef(final Parseable p0, final String p1, final String p2, final Scope<P, E, L, A, CL> p3, final L p4, final A p5) throws BuildException, IllegalSchemaException;
    
    L makeLocation(final String p0, final int p1, final int p2);
    
    A makeAnnotations(final CL p0, final Context p1);
    
    ElementAnnotationBuilder<P, E, L, A, CL> makeElementAnnotationBuilder(final String p0, final String p1, final String p2, final L p3, final CL p4, final Context p5);
    
    CL makeCommentList();
    
    P makeErrorPattern();
    
    boolean usesComments();
    
    P expandPattern(final P p0) throws BuildException, IllegalSchemaException;
}

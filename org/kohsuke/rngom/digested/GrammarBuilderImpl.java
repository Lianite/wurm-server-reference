// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.util.LocatorImpl;
import org.kohsuke.rngom.ast.builder.Include;
import org.kohsuke.rngom.ast.builder.CommentList;
import java.util.ArrayList;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import org.kohsuke.rngom.ast.builder.BuildException;
import java.util.Collection;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.w3c.dom.Element;
import java.util.List;
import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.Div;
import org.kohsuke.rngom.ast.builder.Grammar;

class GrammarBuilderImpl implements Grammar, Div
{
    protected final DGrammarPattern grammar;
    protected final Scope parent;
    protected final DSchemaBuilderImpl sb;
    private List<Element> additionalElementAnnotations;
    
    public GrammarBuilderImpl(final DGrammarPattern p, final Scope parent, final DSchemaBuilderImpl sb) {
        this.grammar = p;
        this.parent = parent;
        this.sb = sb;
    }
    
    public ParsedPattern endGrammar(final Location loc, final Annotations anno) throws BuildException {
        if (anno != null) {
            this.grammar.annotation = ((Annotation)anno).getResult();
        }
        if (this.additionalElementAnnotations != null) {
            if (this.grammar.annotation == null) {
                this.grammar.annotation = new DAnnotation();
            }
            this.grammar.annotation.contents.addAll(this.additionalElementAnnotations);
        }
        return this.grammar;
    }
    
    public void endDiv(final Location loc, final Annotations anno) throws BuildException {
    }
    
    public void define(final String name, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc, final Annotations anno) throws BuildException {
        if (name == "\u0000#start\u0000") {
            this.grammar.start = (DPattern)pattern;
        }
        else {
            final DDefine d = this.grammar.getOrAdd(name);
            d.setPattern((DPattern)pattern);
            if (anno != null) {
                d.annotation = ((Annotation)anno).getResult();
            }
        }
    }
    
    public void topLevelAnnotation(final ParsedElementAnnotation ea) throws BuildException {
        if (this.additionalElementAnnotations == null) {
            this.additionalElementAnnotations = new ArrayList<Element>();
        }
        this.additionalElementAnnotations.add(((ElementWrapper)ea).element);
    }
    
    public void topLevelComment(final CommentList comments) throws BuildException {
    }
    
    public Div makeDiv() {
        return this;
    }
    
    public Include makeInclude() {
        return new IncludeImpl(this.grammar, this.parent, this.sb);
    }
    
    public ParsedPattern makeParentRef(final String name, final Location loc, final Annotations anno) throws BuildException {
        return this.parent.makeRef(name, loc, anno);
    }
    
    public ParsedPattern makeRef(final String name, final Location loc, final Annotations anno) throws BuildException {
        return DSchemaBuilderImpl.wrap(new DRefPattern(this.grammar.getOrAdd(name)), (LocatorImpl)loc, (Annotation)anno);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.w3c.dom.Node;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.om.Location;
import org.w3c.dom.Element;
import org.kohsuke.rngom.ast.builder.ElementAnnotationBuilder;

class ElementAnnotationBuilderImpl implements ElementAnnotationBuilder
{
    private final Element e;
    
    public ElementAnnotationBuilderImpl(final Element e) {
        this.e = e;
    }
    
    public void addText(final String value, final Location loc, final CommentList comments) throws BuildException {
        this.e.appendChild(this.e.getOwnerDocument().createTextNode(value));
    }
    
    public ParsedElementAnnotation makeElementAnnotation() throws BuildException {
        return new ElementWrapper(this.e);
    }
    
    public void addAttribute(final String ns, final String localName, final String prefix, final String value, final Location loc) throws BuildException {
        this.e.setAttributeNS(ns, localName, value);
    }
    
    public void addElement(final ParsedElementAnnotation ea) throws BuildException {
        this.e.appendChild(((ElementWrapper)ea).element);
    }
    
    public void addComment(final CommentList comments) throws BuildException {
    }
    
    public void addLeadingComment(final CommentList comments) throws BuildException {
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.CommentList;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import org.kohsuke.rngom.ast.util.LocatorImpl;
import org.kohsuke.rngom.ast.builder.Annotations;

class Annotation implements Annotations<ElementWrapper, LocatorImpl, CommentListImpl>
{
    private final DAnnotation a;
    
    Annotation() {
        this.a = new DAnnotation();
    }
    
    public void addAttribute(final String ns, final String localName, final String prefix, final String value, final LocatorImpl loc) throws BuildException {
        this.a.attributes.put(new QName(ns, localName, prefix), new DAnnotation.Attribute(ns, localName, prefix, value, loc));
    }
    
    public void addElement(final ElementWrapper ea) throws BuildException {
        this.a.contents.add(ea.element);
    }
    
    public void addComment(final CommentListImpl comments) throws BuildException {
    }
    
    public void addLeadingComment(final CommentListImpl comments) throws BuildException {
    }
    
    DAnnotation getResult() {
        return this.a;
    }
}

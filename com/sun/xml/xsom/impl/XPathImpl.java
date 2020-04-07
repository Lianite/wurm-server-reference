// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSXPath;

public class XPathImpl extends ComponentImpl implements XSXPath
{
    private XSIdentityConstraint parent;
    private final XmlString xpath;
    
    public XPathImpl(final SchemaDocumentImpl _owner, final AnnotationImpl _annon, final Locator _loc, final ForeignAttributesImpl fa, final XmlString xpath) {
        super(_owner, _annon, _loc, fa);
        this.xpath = xpath;
    }
    
    public void setParent(final XSIdentityConstraint parent) {
        this.parent = parent;
    }
    
    public XSIdentityConstraint getParent() {
        return this.parent;
    }
    
    public XmlString getXPath() {
        return this.xpath;
    }
    
    public void visit(final XSVisitor visitor) {
        visitor.xpath(this);
    }
    
    public <T> T apply(final XSFunction<T> function) {
        return function.xpath(this);
    }
}

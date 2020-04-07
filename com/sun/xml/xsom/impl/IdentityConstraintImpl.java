// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSVisitor;
import java.util.Iterator;
import java.util.Collections;
import org.xml.sax.Locator;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import java.util.List;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSIdentityConstraint;

public class IdentityConstraintImpl extends ComponentImpl implements XSIdentityConstraint, Ref.IdentityConstraint
{
    private XSElementDecl parent;
    private final short category;
    private final String name;
    private final XSXPath selector;
    private final List<XSXPath> fields;
    private final Ref.IdentityConstraint refer;
    
    public IdentityConstraintImpl(final SchemaDocumentImpl _owner, final AnnotationImpl _annon, final Locator _loc, final ForeignAttributesImpl fa, final short category, final String name, final XPathImpl selector, final List<XPathImpl> fields, final Ref.IdentityConstraint refer) {
        super(_owner, _annon, _loc, fa);
        this.category = category;
        this.name = name;
        ((XPathImpl)(this.selector = selector)).setParent(this);
        this.fields = Collections.unmodifiableList((List<? extends XSXPath>)fields);
        for (final XPathImpl xp : fields) {
            xp.setParent(this);
        }
        this.refer = refer;
    }
    
    public void visit(final XSVisitor visitor) {
        visitor.identityConstraint(this);
    }
    
    public <T> T apply(final XSFunction<T> function) {
        return function.identityConstraint(this);
    }
    
    public void setParent(final ElementDecl parent) {
        this.parent = parent;
        parent.getOwnerSchema().addIdentityConstraint(this);
    }
    
    public XSElementDecl getParent() {
        return this.parent;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTargetNamespace() {
        return this.getParent().getTargetNamespace();
    }
    
    public short getCategory() {
        return this.category;
    }
    
    public XSXPath getSelector() {
        return this.selector;
    }
    
    public List<XSXPath> getFields() {
        return this.fields;
    }
    
    public XSIdentityConstraint getReferencedKey() {
        if (this.category == 1) {
            return this.refer.get();
        }
        throw new IllegalStateException("not a keyref");
    }
    
    public XSIdentityConstraint get() {
        return this;
    }
}

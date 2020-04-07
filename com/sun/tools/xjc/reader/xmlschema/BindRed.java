// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.bind.v2.TODO;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;

public final class BindRed extends ColorBinder
{
    private final ComplexTypeFieldBuilder ctBuilder;
    
    public BindRed() {
        this.ctBuilder = Ring.get(ComplexTypeFieldBuilder.class);
    }
    
    public void complexType(final XSComplexType ct) {
        this.ctBuilder.build(ct);
    }
    
    public void wildcard(final XSWildcard xsWildcard) {
        TODO.checkSpec();
        throw new UnsupportedOperationException();
    }
    
    public void elementDecl(final XSElementDecl e) {
        final SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        stb.refererStack.push(e);
        this.builder.ying(e.getType(), e);
        stb.refererStack.pop();
    }
    
    public void simpleType(final XSSimpleType type) {
        final SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        stb.refererStack.push(type);
        this.createSimpleTypeProperty(type, "Value");
        stb.refererStack.pop();
    }
    
    public void attGroupDecl(final XSAttGroupDecl ag) {
        throw new IllegalStateException();
    }
    
    public void attributeDecl(final XSAttributeDecl ad) {
        throw new IllegalStateException();
    }
    
    public void attributeUse(final XSAttributeUse au) {
        throw new IllegalStateException();
    }
    
    public void empty(final XSContentType xsContentType) {
        throw new IllegalStateException();
    }
    
    public void modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        throw new IllegalStateException();
    }
    
    public void modelGroup(final XSModelGroup xsModelGroup) {
        throw new IllegalStateException();
    }
    
    public void particle(final XSParticle p) {
        throw new IllegalStateException();
    }
}

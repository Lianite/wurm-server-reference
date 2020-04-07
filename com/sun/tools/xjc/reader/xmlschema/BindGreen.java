// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSWildcard;
import java.util.Iterator;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;

public final class BindGreen extends ColorBinder
{
    private final ComplexTypeFieldBuilder ctBuilder;
    
    public BindGreen() {
        this.ctBuilder = Ring.get(ComplexTypeFieldBuilder.class);
    }
    
    public void attGroupDecl(final XSAttGroupDecl ag) {
        this.attContainer(ag);
    }
    
    public void attContainer(final XSAttContainer cont) {
        Iterator itr = cont.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            this.builder.ying(itr.next(), cont);
        }
        itr = cont.iterateAttGroups();
        while (itr.hasNext()) {
            this.builder.ying(itr.next(), cont);
        }
        final XSWildcard w = cont.getAttributeWildcard();
        if (w != null) {
            this.builder.ying(w, cont);
        }
    }
    
    public void complexType(final XSComplexType ct) {
        this.ctBuilder.build(ct);
    }
    
    public void attributeDecl(final XSAttributeDecl xsAttributeDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void wildcard(final XSWildcard xsWildcard) {
        throw new UnsupportedOperationException();
    }
    
    public void modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void modelGroup(final XSModelGroup xsModelGroup) {
        throw new UnsupportedOperationException();
    }
    
    public void elementDecl(final XSElementDecl xsElementDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void particle(final XSParticle xsParticle) {
        throw new UnsupportedOperationException();
    }
    
    public void empty(final XSContentType xsContentType) {
        throw new UnsupportedOperationException();
    }
    
    public void simpleType(final XSSimpleType xsSimpleType) {
        throw new IllegalStateException();
    }
    
    public void attributeUse(final XSAttributeUse use) {
        throw new IllegalStateException();
    }
}

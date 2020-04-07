// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSComplexType;

public final class BindYellow extends ColorBinder
{
    public void complexType(final XSComplexType ct) {
    }
    
    public void wildcard(final XSWildcard xsWildcard) {
        throw new UnsupportedOperationException();
    }
    
    public void elementDecl(final XSElementDecl xsElementDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void simpleType(final XSSimpleType xsSimpleType) {
        throw new UnsupportedOperationException();
    }
    
    public void attributeDecl(final XSAttributeDecl xsAttributeDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void attGroupDecl(final XSAttGroupDecl xsAttGroupDecl) {
        throw new IllegalStateException();
    }
    
    public void attributeUse(final XSAttributeUse use) {
        throw new IllegalStateException();
    }
    
    public void modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        throw new IllegalStateException();
    }
    
    public void modelGroup(final XSModelGroup xsModelGroup) {
        throw new IllegalStateException();
    }
    
    public void particle(final XSParticle xsParticle) {
        throw new IllegalStateException();
    }
    
    public void empty(final XSContentType xsContentType) {
        throw new IllegalStateException();
    }
}

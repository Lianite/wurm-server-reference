// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;

final class BindBlue extends ColorBinder
{
    public void complexType(final XSComplexType ct) {
        throw new UnsupportedOperationException();
    }
    
    public void elementDecl(final XSElementDecl e) {
        throw new UnsupportedOperationException();
    }
    
    public void wildcard(final XSWildcard xsWildcard) {
        throw new UnsupportedOperationException();
    }
    
    public void attGroupDecl(final XSAttGroupDecl xsAttGroupDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void attributeDecl(final XSAttributeDecl xsAttributeDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void attributeUse(final XSAttributeUse use) {
        throw new UnsupportedOperationException();
    }
    
    public void modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        throw new UnsupportedOperationException();
    }
    
    public void modelGroup(final XSModelGroup xsModelGroup) {
        throw new UnsupportedOperationException();
    }
    
    public void particle(final XSParticle xsParticle) {
        throw new UnsupportedOperationException();
    }
    
    public void empty(final XSContentType xsContentType) {
        throw new UnsupportedOperationException();
    }
    
    public void simpleType(final XSSimpleType type) {
        throw new IllegalStateException();
    }
}

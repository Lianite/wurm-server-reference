// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSAnnotation;

abstract class ClassBinderFilter implements ClassBinder
{
    private final ClassBinder core;
    
    protected ClassBinderFilter(final ClassBinder core) {
        this.core = core;
    }
    
    public CElement annotation(final XSAnnotation xsAnnotation) {
        return this.core.annotation(xsAnnotation);
    }
    
    public CElement attGroupDecl(final XSAttGroupDecl xsAttGroupDecl) {
        return this.core.attGroupDecl(xsAttGroupDecl);
    }
    
    public CElement attributeDecl(final XSAttributeDecl xsAttributeDecl) {
        return this.core.attributeDecl(xsAttributeDecl);
    }
    
    public CElement attributeUse(final XSAttributeUse xsAttributeUse) {
        return this.core.attributeUse(xsAttributeUse);
    }
    
    public CElement complexType(final XSComplexType xsComplexType) {
        return this.core.complexType(xsComplexType);
    }
    
    public CElement schema(final XSSchema xsSchema) {
        return this.core.schema(xsSchema);
    }
    
    public CElement facet(final XSFacet xsFacet) {
        return this.core.facet(xsFacet);
    }
    
    public CElement notation(final XSNotation xsNotation) {
        return this.core.notation(xsNotation);
    }
    
    public CElement simpleType(final XSSimpleType xsSimpleType) {
        return this.core.simpleType(xsSimpleType);
    }
    
    public CElement particle(final XSParticle xsParticle) {
        return this.core.particle(xsParticle);
    }
    
    public CElement empty(final XSContentType xsContentType) {
        return this.core.empty(xsContentType);
    }
    
    public CElement wildcard(final XSWildcard xsWildcard) {
        return this.core.wildcard(xsWildcard);
    }
    
    public CElement modelGroupDecl(final XSModelGroupDecl xsModelGroupDecl) {
        return this.core.modelGroupDecl(xsModelGroupDecl);
    }
    
    public CElement modelGroup(final XSModelGroup xsModelGroup) {
        return this.core.modelGroup(xsModelGroup);
    }
    
    public CElement elementDecl(final XSElementDecl xsElementDecl) {
        return this.core.elementDecl(xsElementDecl);
    }
    
    public CElement identityConstraint(final XSIdentityConstraint xsIdentityConstraint) {
        return this.core.identityConstraint(xsIdentityConstraint);
    }
    
    public CElement xpath(final XSXPath xsxPath) {
        return this.core.xpath(xsxPath);
    }
}

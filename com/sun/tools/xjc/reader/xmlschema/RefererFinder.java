// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;
import java.util.Iterator;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import com.sun.xml.xsom.XSComponent;
import java.util.Map;
import java.util.Set;
import com.sun.xml.xsom.visitor.XSVisitor;

final class RefererFinder implements XSVisitor
{
    private final Set<Object> visited;
    private final Map<XSComponent, Set<XSComponent>> referers;
    
    RefererFinder() {
        this.visited = new HashSet<Object>();
        this.referers = new HashMap<XSComponent, Set<XSComponent>>();
    }
    
    public Set<XSComponent> getReferer(final XSComponent src) {
        final Set<XSComponent> r = this.referers.get(src);
        if (r == null) {
            return Collections.emptySet();
        }
        return r;
    }
    
    public void schemaSet(final XSSchemaSet xss) {
        if (!this.visited.add(xss)) {
            return;
        }
        for (final XSSchema xs : xss.getSchemas()) {
            this.schema(xs);
        }
    }
    
    public void schema(final XSSchema xs) {
        if (!this.visited.add(xs)) {
            return;
        }
        for (final XSComplexType ct : xs.getComplexTypes().values()) {
            this.complexType(ct);
        }
        for (final XSElementDecl e : xs.getElementDecls().values()) {
            this.elementDecl(e);
        }
    }
    
    public void elementDecl(final XSElementDecl e) {
        if (!this.visited.add(e)) {
            return;
        }
        this.refer(e, e.getType());
        e.getType().visit(this);
    }
    
    public void complexType(final XSComplexType ct) {
        if (!this.visited.add(ct)) {
            return;
        }
        this.refer(ct, ct.getBaseType());
        ct.getBaseType().visit(this);
        ct.getContentType().visit(this);
    }
    
    public void modelGroupDecl(final XSModelGroupDecl decl) {
        if (!this.visited.add(decl)) {
            return;
        }
        this.modelGroup(decl.getModelGroup());
    }
    
    public void modelGroup(final XSModelGroup group) {
        if (!this.visited.add(group)) {
            return;
        }
        for (final XSParticle p : group.getChildren()) {
            this.particle(p);
        }
    }
    
    public void particle(final XSParticle particle) {
        particle.getTerm().visit(this);
    }
    
    public void simpleType(final XSSimpleType simpleType) {
    }
    
    public void annotation(final XSAnnotation ann) {
    }
    
    public void attGroupDecl(final XSAttGroupDecl decl) {
    }
    
    public void attributeDecl(final XSAttributeDecl decl) {
    }
    
    public void attributeUse(final XSAttributeUse use) {
    }
    
    public void facet(final XSFacet facet) {
    }
    
    public void notation(final XSNotation notation) {
    }
    
    public void identityConstraint(final XSIdentityConstraint decl) {
    }
    
    public void xpath(final XSXPath xp) {
    }
    
    public void wildcard(final XSWildcard wc) {
    }
    
    public void empty(final XSContentType empty) {
    }
    
    private void refer(final XSComponent source, final XSType target) {
        Set<XSComponent> r = this.referers.get(target);
        if (r == null) {
            r = new HashSet<XSComponent>();
            this.referers.put(target, r);
        }
        r.add(source);
    }
}

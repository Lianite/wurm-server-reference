// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAnnotation;
import java.util.Iterator;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.XSComponent;

abstract class AbstractAxisImpl<T extends XSComponent> implements Axis<T>, XSFunction<Iterator<T>>
{
    protected final Iterator<T> singleton(final T t) {
        return Iterators.singleton(t);
    }
    
    protected final Iterator<T> union(final T... items) {
        return new Iterators.Array<T>(items);
    }
    
    protected final Iterator<T> union(final Iterator<? extends T> first, final Iterator<? extends T> second) {
        return new Iterators.Union<T>(first, second);
    }
    
    public Iterator<T> iterator(final XSComponent contextNode) {
        return contextNode.apply(this);
    }
    
    public String getName() {
        return this.toString();
    }
    
    public Iterator<T> iterator(final Iterator<? extends XSComponent> contextNodes) {
        return (Iterator<T>)new Iterators.Map<T, XSComponent>(contextNodes) {
            protected Iterator<? extends T> apply(final XSComponent u) {
                return AbstractAxisImpl.this.iterator(u);
            }
        };
    }
    
    public boolean isModelGroup() {
        return false;
    }
    
    public Iterator<T> annotation(final XSAnnotation ann) {
        return this.empty();
    }
    
    public Iterator<T> attGroupDecl(final XSAttGroupDecl decl) {
        return this.empty();
    }
    
    public Iterator<T> attributeDecl(final XSAttributeDecl decl) {
        return this.empty();
    }
    
    public Iterator<T> attributeUse(final XSAttributeUse use) {
        return this.empty();
    }
    
    public Iterator<T> complexType(final XSComplexType type) {
        final XSParticle p = type.getContentType().asParticle();
        if (p != null) {
            return this.particle(p);
        }
        return this.empty();
    }
    
    public Iterator<T> schema(final XSSchema schema) {
        return this.empty();
    }
    
    public Iterator<T> facet(final XSFacet facet) {
        return this.empty();
    }
    
    public Iterator<T> notation(final XSNotation notation) {
        return this.empty();
    }
    
    public Iterator<T> identityConstraint(final XSIdentityConstraint decl) {
        return this.empty();
    }
    
    public Iterator<T> xpath(final XSXPath xpath) {
        return this.empty();
    }
    
    public Iterator<T> simpleType(final XSSimpleType simpleType) {
        return this.empty();
    }
    
    public Iterator<T> particle(final XSParticle particle) {
        return this.empty();
    }
    
    public Iterator<T> empty(final XSContentType empty) {
        return this.empty();
    }
    
    public Iterator<T> wildcard(final XSWildcard wc) {
        return this.empty();
    }
    
    public Iterator<T> modelGroupDecl(final XSModelGroupDecl decl) {
        return this.empty();
    }
    
    public Iterator<T> modelGroup(final XSModelGroup group) {
        return (Iterator<T>)new Iterators.Map<T, XSParticle>(group.iterator()) {
            protected Iterator<? extends T> apply(final XSParticle p) {
                return AbstractAxisImpl.this.particle(p);
            }
        };
    }
    
    public Iterator<T> elementDecl(final XSElementDecl decl) {
        return this.empty();
    }
    
    protected final Iterator<T> empty() {
        return Iterators.empty();
    }
}

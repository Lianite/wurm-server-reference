// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.UName;
import com.sun.xml.xsom.XSDeclaration;
import java.util.Iterator;
import com.sun.xml.xsom.XSComponent;

public abstract class Step<T extends XSComponent>
{
    public final Axis<? extends T> axis;
    int predicate;
    
    protected Step(final Axis<? extends T> axis) {
        this.predicate = -1;
        this.axis = axis;
    }
    
    protected abstract Iterator<? extends T> filter(final Iterator<? extends T> p0);
    
    public final Iterator<T> evaluate(final Iterator<XSComponent> nodeSet) {
        Iterator<T> r = (Iterator<T>)new Iterators.Map<T, XSComponent>(nodeSet) {
            protected Iterator<? extends T> apply(final XSComponent contextNode) {
                return Step.this.filter(Step.this.axis.iterator(contextNode));
            }
        };
        r = new Iterators.Unique<T>((Iterator<? extends T>)r);
        if (this.predicate >= 0) {
            T item = null;
            for (int i = this.predicate; i > 0; --i) {
                if (!r.hasNext()) {
                    return Iterators.empty();
                }
                item = r.next();
            }
            return new Iterators.Singleton<T>(item);
        }
        return r;
    }
    
    static final class Any extends Step<XSComponent>
    {
        public Any(final Axis<? extends XSComponent> axis) {
            super(axis);
        }
        
        protected Iterator<? extends XSComponent> filter(final Iterator<? extends XSComponent> base) {
            return base;
        }
    }
    
    private abstract static class Filtered<T extends XSComponent> extends Step<T>
    {
        protected Filtered(final Axis<? extends T> axis) {
            super(axis);
        }
        
        protected Iterator<T> filter(final Iterator<? extends T> base) {
            return new Iterators.Filter<T>(base) {
                protected boolean matches(final T d) {
                    return Filtered.this.match(d);
                }
            };
        }
        
        protected abstract boolean match(final T p0);
    }
    
    static final class Named extends Filtered<XSDeclaration>
    {
        private final String nsUri;
        private final String localName;
        
        public Named(final Axis<? extends XSDeclaration> axis, final UName n) {
            this(axis, n.getNamespaceURI(), n.getName());
        }
        
        public Named(final Axis<? extends XSDeclaration> axis, final String nsUri, final String localName) {
            super(axis);
            this.nsUri = nsUri;
            this.localName = localName;
        }
        
        protected boolean match(final XSDeclaration d) {
            return d.getName().equals(this.localName) && d.getTargetNamespace().equals(this.nsUri);
        }
    }
    
    static final class AnonymousType extends Filtered<XSType>
    {
        public AnonymousType(final Axis<? extends XSType> axis) {
            super(axis);
        }
        
        protected boolean match(final XSType node) {
            return node.isLocal();
        }
    }
    
    static final class Facet extends Filtered<XSFacet>
    {
        private final String name;
        
        public Facet(final Axis<XSFacet> axis, final String facetName) {
            super(axis);
            this.name = facetName;
        }
        
        protected boolean match(final XSFacet f) {
            return f.getName().equals(this.name);
        }
    }
    
    static final class Schema extends Filtered<XSSchema>
    {
        private final String uri;
        
        public Schema(final Axis<XSSchema> axis, final String uri) {
            super(axis);
            this.uri = uri;
        }
        
        protected boolean match(final XSSchema d) {
            return d.getTargetNamespace().equals(this.uri);
        }
    }
}

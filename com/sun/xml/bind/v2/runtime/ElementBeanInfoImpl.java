// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import com.sun.xml.bind.v2.model.core.ClassInfo;
import com.sun.xml.bind.v2.model.core.ElementInfo;
import com.sun.xml.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.bind.v2.runtime.unmarshaller.Intercepter;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.bind.v2.runtime.unmarshaller.Discarder;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.util.QNameMap;
import com.sun.xml.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeElementInfo;
import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.runtime.property.Property;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import javax.xml.bind.JAXBElement;

public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement>
{
    private Loader loader;
    private final Property property;
    private final QName tagName;
    public final Class expectedType;
    private final Class scope;
    private final Constructor<? extends JAXBElement> constructor;
    
    ElementBeanInfoImpl(final JAXBContextImpl grammar, final RuntimeElementInfo rei) {
        super(grammar, rei, rei.getType(), true, false, true);
        this.property = PropertyFactory.create(grammar, rei.getProperty());
        this.tagName = rei.getElementName();
        this.expectedType = Navigator.REFLECTION.erasure((Type)((ElementInfo<Type, C>)rei).getContentInMemoryType());
        this.scope = ((rei.getScope() == null) ? JAXBElement.GlobalScope.class : ((ClassInfo<T, Class<JAXBElement.GlobalScope>>)rei.getScope()).getClazz());
        final Class type = Navigator.REFLECTION.erasure((Type)rei.getType());
        if (type == JAXBElement.class) {
            this.constructor = null;
        }
        else {
            try {
                this.constructor = type.getConstructor(this.expectedType);
            }
            catch (NoSuchMethodException e) {
                final NoSuchMethodError x = new NoSuchMethodError("Failed to find the constructor for " + type + " with " + this.expectedType);
                x.initCause(e);
                throw x;
            }
        }
    }
    
    protected ElementBeanInfoImpl(final JAXBContextImpl grammar) {
        super(grammar, null, JAXBElement.class, true, false, true);
        this.tagName = null;
        this.expectedType = null;
        this.scope = null;
        this.constructor = null;
        this.property = new Property<JAXBElement>() {
            public void reset(final JAXBElement o) {
                throw new UnsupportedOperationException();
            }
            
            public void serializeBody(final JAXBElement e, final XMLSerializer target, final Object outerPeer) throws SAXException, IOException, XMLStreamException {
                Class scope = e.getScope();
                if (e.isGlobalScope()) {
                    scope = null;
                }
                final QName n = e.getName();
                final ElementBeanInfoImpl bi = grammar.getElement(scope, n);
                if (bi == null) {
                    JaxBeanInfo tbi;
                    try {
                        tbi = grammar.getBeanInfo((Class<Object>)e.getDeclaredType(), true);
                    }
                    catch (JAXBException x) {
                        target.reportError(null, x);
                        return;
                    }
                    final Object value = e.getValue();
                    target.startElement(n.getNamespaceURI(), n.getLocalPart(), n.getPrefix(), null);
                    if (value == null) {
                        target.writeXsiNilTrue();
                    }
                    else {
                        target.childAsXsiType(value, "value", tbi);
                    }
                    target.endElement();
                }
                else {
                    try {
                        bi.property.serializeBody(e, target, e);
                    }
                    catch (AccessorException x2) {
                        target.reportError(null, x2);
                    }
                }
            }
            
            public void serializeURIs(final JAXBElement o, final XMLSerializer target) {
            }
            
            public boolean hasSerializeURIAction() {
                return false;
            }
            
            public String getIdValue(final JAXBElement o) {
                return null;
            }
            
            public PropertyKind getKind() {
                return PropertyKind.ELEMENT;
            }
            
            public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
            }
            
            public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
                throw new UnsupportedOperationException();
            }
            
            public void wrapUp() {
            }
        };
    }
    
    public String getElementNamespaceURI(final JAXBElement e) {
        return e.getName().getNamespaceURI();
    }
    
    public String getElementLocalName(final JAXBElement e) {
        return e.getName().getLocalPart();
    }
    
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (this.loader == null) {
            final UnmarshallerChain c = new UnmarshallerChain(context);
            final QNameMap<ChildLoader> result = new QNameMap<ChildLoader>();
            this.property.buildChildElementUnmarshallers(c, result);
            if (result.size() == 1) {
                this.loader = new IntercepterLoader(result.getOne().getValue().loader);
            }
            else {
                this.loader = Discarder.INSTANCE;
            }
        }
        return this.loader;
    }
    
    public final JAXBElement createInstance(final UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.createInstanceFromValue(null);
    }
    
    public final JAXBElement createInstanceFromValue(final Object o) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (this.constructor == null) {
            return new JAXBElement(this.tagName, this.expectedType, this.scope, (T)o);
        }
        return (JAXBElement)this.constructor.newInstance(o);
    }
    
    public boolean reset(final JAXBElement e, final UnmarshallingContext context) {
        e.setValue(null);
        return true;
    }
    
    public String getId(final JAXBElement e, final XMLSerializer target) {
        final Object o = e.getValue();
        if (o instanceof String) {
            return (String)o;
        }
        return null;
    }
    
    public void serializeBody(final JAXBElement element, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        try {
            this.property.serializeBody(element, target, null);
        }
        catch (AccessorException x) {
            target.reportError(null, x);
        }
    }
    
    public void serializeRoot(final JAXBElement e, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        this.serializeBody(e, target);
    }
    
    public void serializeAttributes(final JAXBElement e, final XMLSerializer target) {
    }
    
    public void serializeURIs(final JAXBElement e, final XMLSerializer target) {
    }
    
    public final Transducer<JAXBElement> getTransducer() {
        return null;
    }
    
    public void wrapUp() {
        super.wrapUp();
        this.property.wrapUp();
    }
    
    public void link(final JAXBContextImpl grammar) {
        super.link(grammar);
        this.getLoader(grammar, true);
    }
    
    private final class IntercepterLoader extends Loader implements Intercepter
    {
        private final Loader core;
        
        public IntercepterLoader(final Loader core) {
            this.core = core;
        }
        
        public final void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
            state.loader = this.core;
            state.intercepter = this;
            final UnmarshallingContext context = state.getContext();
            Object child = context.getOuterPeer();
            if (child != null && ElementBeanInfoImpl.this.jaxbType != child.getClass()) {
                child = null;
            }
            if (child != null) {
                ElementBeanInfoImpl.this.reset((JAXBElement)child, context);
            }
            if (child == null) {
                child = context.createInstance(ElementBeanInfoImpl.this);
            }
            this.fireBeforeUnmarshal(ElementBeanInfoImpl.this, child, state);
            context.recordOuterPeer(child);
            final UnmarshallingContext.State p = state.prev;
            p.backup = p.target;
            p.target = child;
            this.core.startElement(state, ea);
        }
        
        public Object intercept(final UnmarshallingContext.State state, final Object o) throws SAXException {
            final JAXBElement e = (JAXBElement)state.target;
            state.target = state.backup;
            state.backup = null;
            if (o != null) {
                e.setValue(o);
            }
            this.fireAfterUnmarshal(ElementBeanInfoImpl.this, e, state);
            return e;
        }
    }
}

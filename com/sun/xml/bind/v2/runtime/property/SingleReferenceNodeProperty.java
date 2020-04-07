// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.model.core.ReferencePropertyInfo;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.bind.v2.runtime.ElementBeanInfoImpl;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.api.AccessorException;
import java.util.Iterator;
import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import javax.xml.bind.annotation.DomHandler;
import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.bind.v2.util.QNameMap;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;

final class SingleReferenceNodeProperty<BeanT, ValueT> extends PropertyImpl<BeanT>
{
    private final Accessor<BeanT, ValueT> acc;
    private final QNameMap<JaxBeanInfo> expectedElements;
    private final DomHandler domHandler;
    private final WildcardMode wcMode;
    
    public SingleReferenceNodeProperty(final JAXBContextImpl context, final RuntimeReferencePropertyInfo prop) {
        super(context, prop);
        this.expectedElements = new QNameMap<JaxBeanInfo>();
        this.acc = prop.getAccessor().optimize(context);
        for (final RuntimeElement e : prop.getElements()) {
            this.expectedElements.put(e.getElementName(), context.getOrCreate(e));
        }
        if (prop.getWildcard() != null) {
            this.domHandler = ClassFactory.create(((ReferencePropertyInfo<T, Class<DomHandler>>)prop).getDOMHandler());
            this.wcMode = prop.getWildcard();
        }
        else {
            this.domHandler = null;
            this.wcMode = null;
        }
    }
    
    public void reset(final BeanT bean) throws AccessorException {
        this.acc.set(bean, null);
    }
    
    public String getIdValue(final BeanT beanT) {
        return null;
    }
    
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        final ValueT v = this.acc.get(o);
        if (v != null) {
            try {
                final JaxBeanInfo bi = w.grammar.getBeanInfo(v, true);
                if (bi.jaxbType == Object.class && this.domHandler != null) {
                    w.writeDom(v, this.domHandler, o, this.fieldName);
                }
                else {
                    bi.serializeRoot(v, w);
                }
            }
            catch (JAXBException e) {
                w.reportError(this.fieldName, e);
            }
        }
    }
    
    public void buildChildElementUnmarshallers(final UnmarshallerChain chain, final QNameMap<ChildLoader> handlers) {
        for (final QNameMap.Entry<JaxBeanInfo> n : this.expectedElements.entrySet()) {
            handlers.put(n.nsUri, n.localName, new ChildLoader(n.getValue().getLoader(chain.context, true), this.acc));
        }
        if (this.domHandler != null) {
            handlers.put(SingleReferenceNodeProperty.CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), this.acc));
        }
    }
    
    public PropertyKind getKind() {
        return PropertyKind.REFERENCE;
    }
    
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        final JaxBeanInfo bi = this.expectedElements.get(nsUri, localName);
        if (bi == null) {
            return null;
        }
        if (bi instanceof ElementBeanInfoImpl) {
            final ElementBeanInfoImpl ebi = (ElementBeanInfoImpl)bi;
            return new Accessor<BeanT, Object>(ebi.expectedType) {
                public Object get(final BeanT bean) throws AccessorException {
                    final ValueT r = SingleReferenceNodeProperty.this.acc.get(bean);
                    if (r instanceof JAXBElement) {
                        return ((JAXBElement)r).getValue();
                    }
                    return r;
                }
                
                public void set(final BeanT bean, Object value) throws AccessorException {
                    if (value != null) {
                        try {
                            value = ebi.createInstanceFromValue(value);
                        }
                        catch (IllegalAccessException e) {
                            throw new AccessorException(e);
                        }
                        catch (InvocationTargetException e2) {
                            throw new AccessorException(e2);
                        }
                        catch (InstantiationException e3) {
                            throw new AccessorException(e3);
                        }
                    }
                    SingleReferenceNodeProperty.this.acc.set(bean, value);
                }
            };
        }
        return this.acc;
    }
}

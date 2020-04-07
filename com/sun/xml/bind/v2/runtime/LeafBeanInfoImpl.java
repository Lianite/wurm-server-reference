// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.TextLoader;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeLeafInfo;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;

final class LeafBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT>
{
    private final Loader loader;
    private final Loader loaderWithSubst;
    private final Transducer<BeanT> xducer;
    private final Name tagName;
    
    public LeafBeanInfoImpl(final JAXBContextImpl grammar, final RuntimeLeafInfo li) {
        super(grammar, li, li.getClazz(), li.getTypeNames(), li.isElement(), true, false);
        this.xducer = li.getTransducer();
        this.loader = new TextLoader(this.xducer);
        this.loaderWithSubst = new XsiTypeLoader(this);
        if (this.isElement()) {
            this.tagName = grammar.nameBuilder.createElementName(li.getElementName());
        }
        else {
            this.tagName = null;
        }
    }
    
    public QName getTypeName(final BeanT instance) {
        final QName tn = this.xducer.getTypeName(instance);
        if (tn != null) {
            return tn;
        }
        return super.getTypeName(instance);
    }
    
    public final String getElementNamespaceURI(final BeanT _) {
        return this.tagName.nsUri;
    }
    
    public final String getElementLocalName(final BeanT _) {
        return this.tagName.localName;
    }
    
    public BeanT createInstance(final UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean reset(final BeanT bean, final UnmarshallingContext context) {
        return false;
    }
    
    public final String getId(final BeanT bean, final XMLSerializer target) {
        return null;
    }
    
    public final void serializeBody(final BeanT bean, final XMLSerializer w) throws SAXException, IOException, XMLStreamException {
        try {
            this.xducer.writeText(w, bean, null);
        }
        catch (AccessorException e) {
            w.reportError(null, e);
        }
    }
    
    public final void serializeAttributes(final BeanT bean, final XMLSerializer target) {
    }
    
    public final void serializeRoot(final BeanT bean, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        if (this.tagName == null) {
            target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(bean.getClass().getName()), null, null));
        }
        else {
            target.startElement(this.tagName, bean);
            target.childAsSoleContent(bean, null);
            target.endElement();
        }
    }
    
    public final void serializeURIs(final BeanT bean, final XMLSerializer target) throws SAXException {
        if (this.xducer.useNamespace()) {
            try {
                this.xducer.declareNamespace(bean, target);
            }
            catch (AccessorException e) {
                target.reportError(null, e);
            }
        }
    }
    
    public final Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        if (typeSubstitutionCapable) {
            return this.loaderWithSubst;
        }
        return this.loader;
    }
    
    public Transducer<BeanT> getTransducer() {
        return this.xducer;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import org.xml.sax.SAXException;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.bind.api.CompositeStructure;

public class CompositeStructureBeanInfo extends JaxBeanInfo<CompositeStructure>
{
    public CompositeStructureBeanInfo(final JAXBContextImpl context) {
        super(context, null, CompositeStructure.class, false, true, false);
    }
    
    public String getElementNamespaceURI(final CompositeStructure o) {
        throw new UnsupportedOperationException();
    }
    
    public String getElementLocalName(final CompositeStructure o) {
        throw new UnsupportedOperationException();
    }
    
    public CompositeStructure createInstance(final UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
        throw new UnsupportedOperationException();
    }
    
    public boolean reset(final CompositeStructure o, final UnmarshallingContext context) throws SAXException {
        throw new UnsupportedOperationException();
    }
    
    public String getId(final CompositeStructure o, final XMLSerializer target) throws SAXException {
        return null;
    }
    
    public Loader getLoader(final JAXBContextImpl context, final boolean typeSubstitutionCapable) {
        throw new UnsupportedOperationException();
    }
    
    public void serializeRoot(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        target.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(o.getClass().getName()), null, null));
    }
    
    public void serializeURIs(final CompositeStructure o, final XMLSerializer target) throws SAXException {
    }
    
    public void serializeAttributes(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
    }
    
    public void serializeBody(final CompositeStructure o, final XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        for (int len = o.bridges.length, i = 0; i < len; ++i) {
            final Object value = o.values[i];
            final InternalBridge bi = (InternalBridge)o.bridges[i];
            bi.marshal(value, target);
        }
    }
    
    public Transducer<CompositeStructure> getTransducer() {
        return null;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;

abstract class PropertyImpl<BeanT> implements Property<BeanT>
{
    protected final String fieldName;
    
    public PropertyImpl(final JAXBContextImpl context, final RuntimePropertyInfo prop) {
        this.fieldName = prop.getName();
    }
    
    public void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
    }
    
    public void serializeURIs(final BeanT o, final XMLSerializer w) throws SAXException, AccessorException {
    }
    
    public boolean hasSerializeURIAction() {
        return false;
    }
    
    public Accessor getElementPropertyAccessor(final String nsUri, final String localName) {
        return null;
    }
    
    public void wrapUp() {
    }
}

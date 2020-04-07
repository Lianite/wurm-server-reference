// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import com.sun.xml.bind.api.JAXBRIContext;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.bind.api.Bridge;

abstract class InternalBridge<T> extends Bridge<T>
{
    protected InternalBridge(final JAXBContextImpl context) {
        super(context);
    }
    
    public JAXBContextImpl getContext() {
        return this.context;
    }
    
    abstract void marshal(final T p0, final XMLSerializer p1) throws IOException, SAXException, XMLStreamException;
}

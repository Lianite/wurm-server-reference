// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.SAXException;
import com.sun.xml.bind.JAXBObject;

public interface XMLSerializable extends JAXBObject
{
    void serializeBody(final XMLSerializer p0) throws SAXException;
    
    void serializeAttributes(final XMLSerializer p0) throws SAXException;
    
    void serializeURIs(final XMLSerializer p0) throws SAXException;
}

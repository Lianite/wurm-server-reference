// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.xml.bind.JAXBObject;
import com.sun.xml.bind.marshaller.IdentifiableObject;
import org.xml.sax.SAXException;
import com.sun.xml.bind.serializer.AbortSerializationException;
import javax.xml.bind.ValidationEvent;

public interface XMLSerializer
{
    void reportError(final ValidationEvent p0) throws AbortSerializationException;
    
    void startElement(final String p0, final String p1) throws SAXException;
    
    void endNamespaceDecls() throws SAXException;
    
    void endAttributes() throws SAXException;
    
    void endElement() throws SAXException;
    
    void text(final String p0, final String p1) throws SAXException;
    
    void startAttribute(final String p0, final String p1) throws SAXException;
    
    void endAttribute() throws SAXException;
    
    NamespaceContext2 getNamespaceContext();
    
    String onID(final IdentifiableObject p0, final String p1) throws SAXException;
    
    String onIDREF(final IdentifiableObject p0) throws SAXException;
    
    void childAsBody(final JAXBObject p0, final String p1) throws SAXException;
    
    void childAsAttributes(final JAXBObject p0, final String p1) throws SAXException;
    
    void childAsURIs(final JAXBObject p0, final String p1) throws SAXException;
}

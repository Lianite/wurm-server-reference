// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.xml.bind.unmarshaller.Tracer;
import javax.xml.bind.ValidationEvent;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;

public interface UnmarshallingContext extends NamespaceContext
{
    GrammarInfo getGrammarInfo();
    
    void pushContentHandler(final UnmarshallingEventHandler p0, final int p1);
    
    void popContentHandler() throws SAXException;
    
    UnmarshallingEventHandler getCurrentHandler();
    
    String[] getNewlyDeclaredPrefixes();
    
    String[] getAllDeclaredPrefixes();
    
    void pushAttributes(final Attributes p0, final boolean p1);
    
    void popAttributes();
    
    int getAttribute(final String p0, final String p1);
    
    Attributes getUnconsumedAttributes();
    
    void consumeAttribute(final int p0) throws SAXException;
    
    String eatAttribute(final int p0) throws SAXException;
    
    void addPatcher(final Runnable p0);
    
    String addToIdTable(final String p0);
    
    Object getObjectFromId(final String p0);
    
    Locator getLocator();
    
    void handleEvent(final ValidationEvent p0, final boolean p1) throws SAXException;
    
    String resolveNamespacePrefix(final String p0);
    
    String getBaseUri();
    
    boolean isUnparsedEntity(final String p0);
    
    boolean isNotation(final String p0);
    
    Tracer getTracer();
}

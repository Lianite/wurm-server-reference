// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.unmarshaller;

import com.sun.xml.bind.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface InfosetScanner<XmlNode>
{
    void scan(final XmlNode p0) throws SAXException;
    
    void setContentHandler(final ContentHandler p0);
    
    ContentHandler getContentHandler();
    
    XmlNode getCurrentElement();
    
    LocatorEx getLocator();
}

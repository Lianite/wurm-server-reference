// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEvent;
import org.xml.sax.ContentHandler;
import com.sun.xml.bind.unmarshaller.InterningXMLReader;

final class InterningUnmarshallerHandler extends InterningXMLReader implements SAXUnmarshallerHandler
{
    private final SAXUnmarshallerHandler core;
    
    InterningUnmarshallerHandler(final SAXUnmarshallerHandler core) {
        this.setContentHandler((ContentHandler)core);
        this.core = core;
    }
    
    public void handleEvent(final ValidationEvent event, final boolean canRecover) throws SAXException {
        this.core.handleEvent(event, canRecover);
    }
    
    public Object getResult() throws JAXBException, IllegalStateException {
        return this.core.getResult();
    }
}

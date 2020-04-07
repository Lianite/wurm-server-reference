// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.xml.bind.validator.Locator;
import org.xml.sax.ErrorHandler;

public class ErrorHandlerAdaptor implements ErrorHandler
{
    private final SAXUnmarshallerHandler host;
    private final Locator locator;
    
    public ErrorHandlerAdaptor(final SAXUnmarshallerHandler _host, final Locator locator) {
        this.host = _host;
        this.locator = locator;
    }
    
    public void error(final SAXParseException exception) throws SAXException {
        this.propagateEvent(1, exception);
    }
    
    public void warning(final SAXParseException exception) throws SAXException {
        this.propagateEvent(0, exception);
    }
    
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.propagateEvent(2, exception);
    }
    
    private void propagateEvent(final int severity, final SAXParseException saxException) throws SAXException {
        final ValidationEventLocator vel = this.locator.getLocation(saxException);
        final ValidationEventImpl ve = new ValidationEventImpl(severity, saxException.getMessage(), vel);
        final Exception e = saxException.getException();
        if (e != null) {
            ve.setLinkedException(e);
        }
        else {
            ve.setLinkedException(saxException);
        }
        this.host.handleEvent((ValidationEvent)ve, severity != 2);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.xml.bind.validator.DOMLocator;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.bind.unmarshaller.InterningXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.sun.xml.bind.validator.Locator;
import com.sun.xml.bind.validator.SAXLocator;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.DatatypeConverter;
import com.sun.xml.bind.DatatypeConverterImpl;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.bind.helpers.AbstractUnmarshallerImpl;

public class UnmarshallerImpl extends AbstractUnmarshallerImpl
{
    private DefaultJAXBContextImpl context;
    private final GrammarInfo grammarInfo;
    private static final DefaultHandler dummyHandler;
    
    public UnmarshallerImpl(final DefaultJAXBContextImpl context, final GrammarInfo gi) {
        this.context = null;
        this.context = context;
        this.grammarInfo = gi;
        DatatypeConverter.setDatatypeConverter(DatatypeConverterImpl.theInstance);
    }
    
    public void setValidating(final boolean validating) throws JAXBException {
        if (MetaVariable.W) {
            super.setValidating(validating);
            if (validating) {
                this.context.getGrammar();
            }
            return;
        }
        throw new UnsupportedOperationException("When generating this code, the compiler option was specified not to generate code for the unmarshal-time validation");
    }
    
    public UnmarshallerHandler getUnmarshallerHandler() {
        return (UnmarshallerHandler)new InterningUnmarshallerHandler(this.createUnmarshallerHandler((Locator)new SAXLocator()));
    }
    
    private SAXUnmarshallerHandler createUnmarshallerHandler(final Locator locator) {
        SAXUnmarshallerHandler unmarshaller = (SAXUnmarshallerHandler)new SAXUnmarshallerHandlerImpl(this, this.grammarInfo);
        try {
            if (this.isValidating()) {
                unmarshaller = (SAXUnmarshallerHandler)ValidatingUnmarshaller.create(this.context.getGrammar(), unmarshaller, locator);
            }
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return unmarshaller;
    }
    
    protected Object unmarshal(XMLReader reader, final InputSource source) throws JAXBException {
        final SAXLocator locator = new SAXLocator();
        final SAXUnmarshallerHandler handler = this.createUnmarshallerHandler((Locator)locator);
        reader = InterningXMLReader.adapt(reader);
        reader.setContentHandler((ContentHandler)handler);
        reader.setErrorHandler(new ErrorHandlerAdaptor(handler, (Locator)locator));
        try {
            reader.parse(source);
        }
        catch (IOException e) {
            throw new JAXBException(e);
        }
        catch (SAXException e2) {
            throw this.createUnmarshalException(e2);
        }
        final Object result = handler.getResult();
        reader.setContentHandler(UnmarshallerImpl.dummyHandler);
        reader.setErrorHandler(UnmarshallerImpl.dummyHandler);
        return result;
    }
    
    public final Object unmarshal(final Node node) throws JAXBException {
        try {
            final DOMScanner scanner = new DOMScanner();
            final UnmarshallerHandler handler = (UnmarshallerHandler)new InterningUnmarshallerHandler(this.createUnmarshallerHandler((Locator)new DOMLocator(scanner)));
            if (node instanceof Element) {
                scanner.parse((Element)node, handler);
            }
            else {
                if (!(node instanceof Document)) {
                    throw new IllegalArgumentException();
                }
                scanner.parse(((Document)node).getDocumentElement(), handler);
            }
            return handler.getResult();
        }
        catch (SAXException e) {
            throw this.createUnmarshalException(e);
        }
    }
    
    static {
        UnmarshallerImpl.dummyHandler = new DefaultHandler();
    }
}

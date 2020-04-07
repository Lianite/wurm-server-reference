// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.xml.bind.api.ClassResolver;
import javax.xml.bind.PropertyException;
import java.io.InputStream;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.bind.unmarshaller.Messages;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.xml.bind.unmarshaller.DOMScanner;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import java.io.IOException;
import javax.xml.bind.UnmarshalException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.bind.JAXBElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import com.sun.xml.bind.unmarshaller.InfosetScanner;
import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.v2.runtime.AssociationMap;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.xml.bind.IDResolver;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.AbstractUnmarshallerImpl;

public final class UnmarshallerImpl extends AbstractUnmarshallerImpl implements ValidationEventHandler
{
    protected final JAXBContextImpl context;
    private Schema schema;
    public final UnmarshallingContext coordinator;
    private Unmarshaller.Listener externalListener;
    private AttachmentUnmarshaller attachmentUnmarshaller;
    private IDResolver idResolver;
    private static final DefaultHandler dummyHandler;
    public static final String FACTORY = "com.sun.xml.bind.ObjectFactory";
    
    public UnmarshallerImpl(final JAXBContextImpl context, final AssociationMap assoc) {
        this.idResolver = new DefaultIDResolver();
        this.context = context;
        this.coordinator = new UnmarshallingContext(this, assoc);
        try {
            this.setEventHandler(this);
        }
        catch (JAXBException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public UnmarshallerHandler getUnmarshallerHandler() {
        return this.getUnmarshallerHandler(true, null);
    }
    
    private SAXConnector getUnmarshallerHandler(final boolean intern, final JaxBeanInfo expectedType) {
        XmlVisitor h = this.createUnmarshallerHandler(null, false, expectedType);
        if (intern) {
            h = new InterningXmlVisitor(h);
        }
        return new SAXConnector(h, null);
    }
    
    public final XmlVisitor createUnmarshallerHandler(final InfosetScanner scanner, final boolean inplace, final JaxBeanInfo expectedType) {
        this.coordinator.reset(scanner, inplace, expectedType, this.idResolver);
        XmlVisitor unmarshaller = this.coordinator;
        if (this.schema != null) {
            unmarshaller = new ValidatingUnmarshaller(this.schema, unmarshaller);
        }
        if (this.attachmentUnmarshaller != null && this.attachmentUnmarshaller.isXOPPackage()) {
            unmarshaller = new MTOMDecorator(this, unmarshaller, this.attachmentUnmarshaller);
        }
        return unmarshaller;
    }
    
    public static boolean needsInterning(final XMLReader reader) {
        try {
            reader.setFeature("http://xml.org/sax/features/string-interning", true);
        }
        catch (SAXException ex) {}
        try {
            if (reader.getFeature("http://xml.org/sax/features/string-interning")) {
                return false;
            }
        }
        catch (SAXException ex2) {}
        return true;
    }
    
    protected Object unmarshal(final XMLReader reader, final InputSource source) throws JAXBException {
        return this.unmarshal0(reader, source, null);
    }
    
    protected <T> JAXBElement<T> unmarshal(final XMLReader reader, final InputSource source, final Class<T> expectedType) throws JAXBException {
        if (expectedType == null) {
            throw new IllegalArgumentException();
        }
        return (JAXBElement<T>)this.unmarshal0(reader, source, this.getBeanInfo(expectedType));
    }
    
    private Object unmarshal0(final XMLReader reader, final InputSource source, final JaxBeanInfo expectedType) throws JAXBException {
        final SAXConnector connector = this.getUnmarshallerHandler(needsInterning(reader), expectedType);
        reader.setContentHandler(connector);
        reader.setErrorHandler(this.coordinator);
        try {
            reader.parse(source);
        }
        catch (IOException e) {
            throw new UnmarshalException(e);
        }
        catch (SAXException e2) {
            throw this.createUnmarshalException(e2);
        }
        final Object result = connector.getResult();
        reader.setContentHandler(UnmarshallerImpl.dummyHandler);
        reader.setErrorHandler(UnmarshallerImpl.dummyHandler);
        return result;
    }
    
    public <T> JAXBElement<T> unmarshal(final Source source, final Class<T> expectedType) throws JAXBException {
        if (source instanceof SAXSource) {
            final SAXSource ss = (SAXSource)source;
            XMLReader reader = ss.getXMLReader();
            if (reader == null) {
                reader = this.getXMLReader();
            }
            return this.unmarshal(reader, ss.getInputSource(), expectedType);
        }
        if (source instanceof StreamSource) {
            return this.unmarshal(this.getXMLReader(), streamSourceToInputSource((StreamSource)source), expectedType);
        }
        if (source instanceof DOMSource) {
            return this.unmarshal(((DOMSource)source).getNode(), expectedType);
        }
        throw new IllegalArgumentException();
    }
    
    public Object unmarshal0(final Source source, final JaxBeanInfo expectedType) throws JAXBException {
        if (source instanceof SAXSource) {
            final SAXSource ss = (SAXSource)source;
            XMLReader reader = ss.getXMLReader();
            if (reader == null) {
                reader = this.getXMLReader();
            }
            return this.unmarshal0(reader, ss.getInputSource(), expectedType);
        }
        if (source instanceof StreamSource) {
            return this.unmarshal0(this.getXMLReader(), streamSourceToInputSource((StreamSource)source), expectedType);
        }
        if (source instanceof DOMSource) {
            return this.unmarshal0(((DOMSource)source).getNode(), expectedType);
        }
        throw new IllegalArgumentException();
    }
    
    public final ValidationEventHandler getEventHandler() {
        try {
            return super.getEventHandler();
        }
        catch (JAXBException e) {
            throw new AssertionError();
        }
    }
    
    public final boolean hasEventHandler() {
        return this.getEventHandler() != this;
    }
    
    public <T> JAXBElement<T> unmarshal(final Node node, final Class<T> expectedType) throws JAXBException {
        if (expectedType == null) {
            throw new IllegalArgumentException();
        }
        return (JAXBElement<T>)this.unmarshal0(node, this.getBeanInfo(expectedType));
    }
    
    public final Object unmarshal(final Node node) throws JAXBException {
        return this.unmarshal0(node, null);
    }
    
    @Deprecated
    public final Object unmarshal(final SAXSource source) throws JAXBException {
        return super.unmarshal((Source)source);
    }
    
    public final Object unmarshal0(final Node node, final JaxBeanInfo expectedType) throws JAXBException {
        try {
            final DOMScanner scanner = new DOMScanner();
            final InterningXmlVisitor handler = new InterningXmlVisitor(this.createUnmarshallerHandler(null, false, expectedType));
            scanner.setContentHandler(new SAXConnector(handler, scanner));
            if (node instanceof Element) {
                scanner.scan((Element)node);
            }
            else {
                if (!(node instanceof Document)) {
                    throw new IllegalArgumentException("Unexpected node type: " + node);
                }
                scanner.scan((Document)node);
            }
            return handler.getContext().getResult();
        }
        catch (SAXException e) {
            throw this.createUnmarshalException(e);
        }
    }
    
    public Object unmarshal(final XMLStreamReader reader) throws JAXBException {
        return this.unmarshal0(reader, null);
    }
    
    public <T> JAXBElement<T> unmarshal(final XMLStreamReader reader, final Class<T> expectedType) throws JAXBException {
        if (expectedType == null) {
            throw new IllegalArgumentException();
        }
        return (JAXBElement<T>)this.unmarshal0(reader, this.getBeanInfo(expectedType));
    }
    
    public Object unmarshal0(final XMLStreamReader reader, final JaxBeanInfo expectedType) throws JAXBException {
        if (reader == null) {
            throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader"));
        }
        final int eventType = reader.getEventType();
        if (eventType != 1 && eventType != 7) {
            throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", eventType));
        }
        final XmlVisitor h = this.createUnmarshallerHandler(null, false, expectedType);
        final StAXConnector connector = StAXStreamConnector.create(reader, h);
        try {
            connector.bridge();
        }
        catch (XMLStreamException e) {
            throw handleStreamException(e);
        }
        return h.getContext().getResult();
    }
    
    public <T> JAXBElement<T> unmarshal(final XMLEventReader reader, final Class<T> expectedType) throws JAXBException {
        if (expectedType == null) {
            throw new IllegalArgumentException();
        }
        return (JAXBElement<T>)this.unmarshal0(reader, this.getBeanInfo(expectedType));
    }
    
    public Object unmarshal(final XMLEventReader reader) throws JAXBException {
        return this.unmarshal0(reader, null);
    }
    
    private Object unmarshal0(final XMLEventReader reader, final JaxBeanInfo expectedType) throws JAXBException {
        if (reader == null) {
            throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader"));
        }
        try {
            final XMLEvent event = reader.peek();
            if (!event.isStartElement() && !event.isStartDocument()) {
                throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", event.getEventType()));
            }
            final boolean isZephyr = reader.getClass().getName().equals("com.sun.xml.stream.XMLReaderImpl");
            XmlVisitor h = this.createUnmarshallerHandler(null, false, expectedType);
            if (!isZephyr) {
                h = new InterningXmlVisitor(h);
            }
            new StAXEventConnector(reader, h).bridge();
            return h.getContext().getResult();
        }
        catch (XMLStreamException e) {
            throw handleStreamException(e);
        }
    }
    
    public Object unmarshal0(final InputStream input, final JaxBeanInfo expectedType) throws JAXBException {
        return this.unmarshal0(this.getXMLReader(), new InputSource(input), expectedType);
    }
    
    private static JAXBException handleStreamException(final XMLStreamException e) {
        final Throwable ne = e.getNestedException();
        if (ne instanceof JAXBException) {
            return (JAXBException)ne;
        }
        if (ne instanceof SAXException) {
            return new UnmarshalException(ne);
        }
        return new UnmarshalException(e);
    }
    
    public Object getProperty(final String name) throws PropertyException {
        if (name.equals(IDResolver.class.getName())) {
            return this.idResolver;
        }
        return super.getProperty(name);
    }
    
    public void setProperty(final String name, final Object value) throws PropertyException {
        if (name.equals("com.sun.xml.bind.ObjectFactory")) {
            this.coordinator.setFactories(value);
            return;
        }
        if (name.equals(IDResolver.class.getName())) {
            this.idResolver = (IDResolver)value;
            return;
        }
        if (name.equals(ClassResolver.class.getName())) {
            this.coordinator.classResolver = (ClassResolver)value;
            return;
        }
        super.setProperty(name, value);
    }
    
    public void setSchema(final Schema schema) {
        this.schema = schema;
    }
    
    public Schema getSchema() {
        return this.schema;
    }
    
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return this.attachmentUnmarshaller;
    }
    
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller au) {
        this.attachmentUnmarshaller = au;
    }
    
    public boolean isValidating() {
        throw new UnsupportedOperationException();
    }
    
    public void setValidating(final boolean validating) {
        throw new UnsupportedOperationException();
    }
    
    public <A extends XmlAdapter> void setAdapter(final Class<A> type, final A adapter) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.coordinator.putAdapter(type, adapter);
    }
    
    public <A extends XmlAdapter> A getAdapter(final Class<A> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (this.coordinator.containsAdapter(type)) {
            return this.coordinator.getAdapter(type);
        }
        return null;
    }
    
    public UnmarshalException createUnmarshalException(final SAXException e) {
        return super.createUnmarshalException(e);
    }
    
    public boolean handleEvent(final ValidationEvent event) {
        return event.getSeverity() != 2;
    }
    
    private static InputSource streamSourceToInputSource(final StreamSource ss) {
        final InputSource is = new InputSource();
        is.setSystemId(ss.getSystemId());
        is.setByteStream(ss.getInputStream());
        is.setCharacterStream(ss.getReader());
        return is;
    }
    
    public <T> JaxBeanInfo<T> getBeanInfo(final Class<T> clazz) throws JAXBException {
        return this.context.getBeanInfo(clazz, true);
    }
    
    public Unmarshaller.Listener getListener() {
        return this.externalListener;
    }
    
    public void setListener(final Unmarshaller.Listener listener) {
        this.externalListener = listener;
    }
    
    static {
        dummyHandler = new DefaultHandler();
    }
}

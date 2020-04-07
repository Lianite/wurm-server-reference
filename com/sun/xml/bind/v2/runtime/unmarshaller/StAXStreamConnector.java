// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.sun.xml.bind.WhiteSpaceProcessor;
import javax.xml.stream.Location;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.Constructor;
import org.xml.sax.Attributes;
import javax.xml.stream.XMLStreamReader;

class StAXStreamConnector extends StAXConnector
{
    private final XMLStreamReader staxStreamReader;
    protected final StringBuilder buffer;
    protected boolean textReported;
    private final Attributes attributes;
    private static final Class FI_STAX_READER_CLASS;
    private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR;
    private static final Class STAX_EX_READER_CLASS;
    private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR;
    
    public static StAXConnector create(final XMLStreamReader reader, XmlVisitor visitor) {
        final Class readerClass = reader.getClass();
        if (StAXStreamConnector.FI_STAX_READER_CLASS != null && StAXStreamConnector.FI_STAX_READER_CLASS.isAssignableFrom(readerClass) && StAXStreamConnector.FI_CONNECTOR_CTOR != null) {
            try {
                return (StAXConnector)StAXStreamConnector.FI_CONNECTOR_CTOR.newInstance(reader, visitor);
            }
            catch (Exception ex) {}
        }
        final boolean isZephyr = readerClass.getName().equals("com.sun.xml.stream.XMLReaderImpl");
        if (!isZephyr) {
            if (!checkImplementaionNameOfSjsxp(reader)) {
                if (!getBoolProp(reader, "org.codehaus.stax2.internNames") || !getBoolProp(reader, "org.codehaus.stax2.internNsUris")) {
                    visitor = new InterningXmlVisitor(visitor);
                }
            }
        }
        if (StAXStreamConnector.STAX_EX_READER_CLASS != null && StAXStreamConnector.STAX_EX_READER_CLASS.isAssignableFrom(readerClass)) {
            try {
                return (StAXConnector)StAXStreamConnector.STAX_EX_CONNECTOR_CTOR.newInstance(reader, visitor);
            }
            catch (Exception ex2) {}
        }
        return new StAXStreamConnector(reader, visitor);
    }
    
    private static boolean checkImplementaionNameOfSjsxp(final XMLStreamReader reader) {
        try {
            final Object name = reader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
            return name != null && name.equals("sjsxp");
        }
        catch (Exception e) {
            return false;
        }
    }
    
    private static boolean getBoolProp(final XMLStreamReader r, final String n) {
        try {
            final Object o = r.getProperty(n);
            return o instanceof Boolean && (boolean)o;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    protected StAXStreamConnector(final XMLStreamReader staxStreamReader, final XmlVisitor visitor) {
        super(visitor);
        this.buffer = new StringBuilder();
        this.textReported = false;
        this.attributes = new Attributes() {
            public int getLength() {
                return StAXStreamConnector.this.staxStreamReader.getAttributeCount();
            }
            
            public String getURI(final int index) {
                final String uri = StAXStreamConnector.this.staxStreamReader.getAttributeNamespace(index);
                if (uri == null) {
                    return "";
                }
                return uri;
            }
            
            public String getLocalName(final int index) {
                return StAXStreamConnector.this.staxStreamReader.getAttributeLocalName(index);
            }
            
            public String getQName(final int index) {
                final String prefix = StAXStreamConnector.this.staxStreamReader.getAttributePrefix(index);
                if (prefix == null || prefix.length() == 0) {
                    return this.getLocalName(index);
                }
                return prefix + ':' + this.getLocalName(index);
            }
            
            public String getType(final int index) {
                return StAXStreamConnector.this.staxStreamReader.getAttributeType(index);
            }
            
            public String getValue(final int index) {
                return StAXStreamConnector.this.staxStreamReader.getAttributeValue(index);
            }
            
            public int getIndex(final String uri, final String localName) {
                for (int i = this.getLength() - 1; i >= 0; --i) {
                    if (localName.equals(this.getLocalName(i)) && uri.equals(this.getURI(i))) {
                        return i;
                    }
                }
                return -1;
            }
            
            public int getIndex(final String qName) {
                for (int i = this.getLength() - 1; i >= 0; --i) {
                    if (qName.equals(this.getQName(i))) {
                        return i;
                    }
                }
                return -1;
            }
            
            public String getType(final String uri, final String localName) {
                final int index = this.getIndex(uri, localName);
                if (index < 0) {
                    return null;
                }
                return this.getType(index);
            }
            
            public String getType(final String qName) {
                final int index = this.getIndex(qName);
                if (index < 0) {
                    return null;
                }
                return this.getType(index);
            }
            
            public String getValue(final String uri, final String localName) {
                final int index = this.getIndex(uri, localName);
                if (index < 0) {
                    return null;
                }
                return this.getValue(index);
            }
            
            public String getValue(final String qName) {
                final int index = this.getIndex(qName);
                if (index < 0) {
                    return null;
                }
                return this.getValue(index);
            }
        };
        this.staxStreamReader = staxStreamReader;
    }
    
    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            int event = this.staxStreamReader.getEventType();
            if (event == 7) {
                while (!this.staxStreamReader.isStartElement()) {
                    event = this.staxStreamReader.next();
                }
            }
            if (event != 1) {
                throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);
            }
            this.handleStartDocument(this.staxStreamReader.getNamespaceContext());
        Block_4:
            while (true) {
                switch (event) {
                    case 1: {
                        this.handleStartElement();
                        ++depth;
                        break;
                    }
                    case 2: {
                        --depth;
                        this.handleEndElement();
                        if (depth == 0) {
                            break Block_4;
                        }
                        break;
                    }
                    case 4:
                    case 6:
                    case 12: {
                        this.handleCharacters();
                        break;
                    }
                }
                event = this.staxStreamReader.next();
            }
            this.staxStreamReader.next();
            this.handleEndDocument();
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected Location getCurrentLocation() {
        return this.staxStreamReader.getLocation();
    }
    
    protected String getCurrentQName() {
        return this.getQName(this.staxStreamReader.getPrefix(), this.staxStreamReader.getLocalName());
    }
    
    private void handleEndElement() throws SAXException {
        this.processText(false);
        this.tagName.uri = StAXConnector.fixNull(this.staxStreamReader.getNamespaceURI());
        this.tagName.local = this.staxStreamReader.getLocalName();
        this.visitor.endElement(this.tagName);
        final int nsCount = this.staxStreamReader.getNamespaceCount();
        for (int i = nsCount - 1; i >= 0; --i) {
            this.visitor.endPrefixMapping(StAXConnector.fixNull(this.staxStreamReader.getNamespacePrefix(i)));
        }
    }
    
    private void handleStartElement() throws SAXException {
        this.processText(true);
        for (int nsCount = this.staxStreamReader.getNamespaceCount(), i = 0; i < nsCount; ++i) {
            this.visitor.startPrefixMapping(StAXConnector.fixNull(this.staxStreamReader.getNamespacePrefix(i)), StAXConnector.fixNull(this.staxStreamReader.getNamespaceURI(i)));
        }
        this.tagName.uri = StAXConnector.fixNull(this.staxStreamReader.getNamespaceURI());
        this.tagName.local = this.staxStreamReader.getLocalName();
        this.tagName.atts = this.attributes;
        this.visitor.startElement(this.tagName);
    }
    
    protected void handleCharacters() throws XMLStreamException, SAXException {
        if (this.predictor.expectText()) {
            this.buffer.append(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength());
        }
    }
    
    private void processText(final boolean ignorable) throws SAXException {
        if (this.predictor.expectText() && (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer))) {
            if (this.textReported) {
                this.textReported = false;
            }
            else {
                this.visitor.text(this.buffer);
            }
        }
        this.buffer.setLength(0);
    }
    
    private static Class initFIStAXReaderClass() {
        try {
            final Class fisr = UnmarshallerImpl.class.getClassLoader().loadClass("org.jvnet.fastinfoset.stax.FastInfosetStreamReader");
            final Class sdp = UnmarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.fastinfoset.stax.StAXDocumentParser");
            if (fisr.isAssignableFrom(sdp)) {
                return sdp;
            }
            return null;
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass() {
        try {
            if (StAXStreamConnector.FI_STAX_READER_CLASS == null) {
                return null;
            }
            final Class c = UnmarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.bind.v2.runtime.unmarshaller.FastInfosetConnector");
            return c.getConstructor(StAXStreamConnector.FI_STAX_READER_CLASS, XmlVisitor.class);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Class initStAXExReader() {
        try {
            return UnmarshallerImpl.class.getClassLoader().loadClass("org.jvnet.staxex.XMLStreamReaderEx");
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends StAXConnector> initStAXExConnector() {
        try {
            final Class c = UnmarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.bind.v2.runtime.unmarshaller.StAXExConnector");
            return c.getConstructor(StAXStreamConnector.STAX_EX_READER_CLASS, XmlVisitor.class);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    static {
        FI_STAX_READER_CLASS = initFIStAXReaderClass();
        FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();
        STAX_EX_READER_CLASS = initStAXExReader();
        STAX_EX_CONNECTOR_CTOR = initStAXExConnector();
    }
}

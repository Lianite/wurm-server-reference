// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.output;

import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.bind.v2.runtime.MarshallerImpl;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterOutput extends XmlOutputAbstractImpl
{
    private final XMLStreamWriter out;
    protected final char[] buf;
    private static final Class FI_STAX_WRITER_CLASS;
    private static final Constructor<? extends XmlOutput> FI_OUTPUT_CTOR;
    private static final Class STAXEX_WRITER_CLASS;
    private static final Constructor<? extends XmlOutput> STAXEX_OUTPUT_CTOR;
    
    public static XmlOutput create(final XMLStreamWriter out, final JAXBContextImpl context) {
        final Class writerClass = out.getClass();
        if (writerClass == XMLStreamWriterOutput.FI_STAX_WRITER_CLASS) {
            try {
                return (XmlOutput)XMLStreamWriterOutput.FI_OUTPUT_CTOR.newInstance(out, context);
            }
            catch (Exception ex) {}
        }
        if (XMLStreamWriterOutput.STAXEX_WRITER_CLASS != null && XMLStreamWriterOutput.STAXEX_WRITER_CLASS.isAssignableFrom(writerClass)) {
            try {
                return (XmlOutput)XMLStreamWriterOutput.STAXEX_OUTPUT_CTOR.newInstance(out);
            }
            catch (Exception ex2) {}
        }
        return new XMLStreamWriterOutput(out);
    }
    
    protected XMLStreamWriterOutput(final XMLStreamWriter out) {
        this.buf = new char[256];
        this.out = out;
    }
    
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        if (!fragment) {
            this.out.writeStartDocument();
        }
    }
    
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        if (!fragment) {
            this.out.writeEndDocument();
            this.out.flush();
        }
        super.endDocument(fragment);
    }
    
    public void beginStartTag(final int prefix, final String localName) throws IOException, XMLStreamException {
        this.out.writeStartElement(this.nsContext.getPrefix(prefix), localName, this.nsContext.getNamespaceURI(prefix));
        final NamespaceContextImpl.Element nse = this.nsContext.getCurrent();
        if (nse.count() > 0) {
            for (int i = nse.count() - 1; i >= 0; --i) {
                final String uri = nse.getNsUri(i);
                if (uri.length() != 0 || nse.getBase() != 1) {
                    this.out.writeNamespace(nse.getPrefix(i), uri);
                }
            }
        }
    }
    
    public void attribute(final int prefix, final String localName, final String value) throws IOException, XMLStreamException {
        if (prefix == -1) {
            this.out.writeAttribute(localName, value);
        }
        else {
            this.out.writeAttribute(this.nsContext.getPrefix(prefix), this.nsContext.getNamespaceURI(prefix), localName, value);
        }
    }
    
    public void endStartTag() throws IOException, SAXException {
    }
    
    public void endTag(final int prefix, final String localName) throws IOException, SAXException, XMLStreamException {
        this.out.writeEndElement();
    }
    
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        this.out.writeCharacters(value);
    }
    
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (needsSeparatingWhitespace) {
            this.out.writeCharacters(" ");
        }
        final int len = value.length();
        if (len < this.buf.length) {
            value.writeTo(this.buf, 0);
            this.out.writeCharacters(this.buf, 0, len);
        }
        else {
            this.out.writeCharacters(value.toString());
        }
    }
    
    private static Class initFIStAXWriterClass() {
        try {
            final Class llfisw = MarshallerImpl.class.getClassLoader().loadClass("org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter");
            final Class sds = MarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.fastinfoset.stax.StAXDocumentSerializer");
            if (llfisw.isAssignableFrom(sds)) {
                return sds;
            }
            return null;
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends XmlOutput> initFastInfosetOutputClass() {
        try {
            if (XMLStreamWriterOutput.FI_STAX_WRITER_CLASS == null) {
                return null;
            }
            final Class c = UnmarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.bind.v2.runtime.output.FastInfosetStreamWriterOutput");
            return c.getConstructor(XMLStreamWriterOutput.FI_STAX_WRITER_CLASS, JAXBContextImpl.class);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Class initStAXExWriterClass() {
        try {
            return MarshallerImpl.class.getClassLoader().loadClass("org.jvnet.staxex.XMLStreamWriterEx");
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    private static Constructor<? extends XmlOutput> initStAXExOutputClass() {
        try {
            final Class c = UnmarshallerImpl.class.getClassLoader().loadClass("com.sun.xml.bind.v2.runtime.output.StAXExStreamWriterOutput");
            return c.getConstructor(XMLStreamWriterOutput.STAXEX_WRITER_CLASS);
        }
        catch (Throwable e) {
            return null;
        }
    }
    
    static {
        FI_STAX_WRITER_CLASS = initFIStAXWriterClass();
        FI_OUTPUT_CTOR = initFastInfosetOutputClass();
        STAXEX_WRITER_CLASS = initStAXExWriterClass();
        STAXEX_OUTPUT_CTOR = initStAXExOutputClass();
    }
}

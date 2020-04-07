// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.PropertyException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import com.sun.xml.bind.marshaller.DataWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import com.sun.xml.bind.marshaller.DumbEscapeHandler;
import com.sun.xml.bind.marshaller.NioEscapeHandler;
import com.sun.xml.bind.marshaller.MinimumEscapeHandler;
import org.xml.sax.SAXException;
import com.sun.xml.bind.JAXBObject;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.xml.bind.marshaller.SchemaLocationFilter;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.marshaller.XMLWriter;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.xml.bind.JAXBAssertionError;
import org.xml.sax.ContentHandler;
import com.sun.xml.bind.marshaller.SAX2DOMEx;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.bind.MarshalException;
import com.sun.xml.bind.marshaller.Messages;
import javax.xml.transform.Result;
import javax.xml.bind.DatatypeConverter;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import javax.xml.bind.helpers.AbstractMarshallerImpl;

public class MarshallerImpl extends AbstractMarshallerImpl
{
    private String indent;
    private NamespacePrefixMapper prefixMapper;
    private CharacterEscapeHandler escapeHandler;
    private boolean printXmlDeclaration;
    private String header;
    final DefaultJAXBContextImpl context;
    private static final String INDENT_STRING = "com.sun.xml.bind.indentString";
    private static final String PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
    private static final String ENCODING_HANDLER = "com.sun.xml.bind.characterEscapeHandler";
    private static final String XMLDECLARATION = "com.sun.xml.bind.xmlDeclaration";
    private static final String XML_HEADERS = "com.sun.xml.bind.xmlHeaders";
    
    public MarshallerImpl(final DefaultJAXBContextImpl c) {
        this.indent = "    ";
        this.prefixMapper = null;
        this.escapeHandler = null;
        this.printXmlDeclaration = true;
        this.header = null;
        DatatypeConverter.setDatatypeConverter(DatatypeConverterImpl.theInstance);
        this.context = c;
    }
    
    public void marshal(final Object obj, final Result result) throws JAXBException {
        final XMLSerializable so = this.context.getGrammarInfo().castToXMLSerializable(obj);
        if (so == null) {
            throw new MarshalException(Messages.format("MarshallerImpl.NotMarshallable"));
        }
        if (result instanceof SAXResult) {
            this.write(so, ((SAXResult)result).getHandler());
            return;
        }
        if (result instanceof DOMResult) {
            final Node node = ((DOMResult)result).getNode();
            if (node == null) {
                try {
                    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setNamespaceAware(true);
                    final DocumentBuilder db = dbf.newDocumentBuilder();
                    final Document doc = db.newDocument();
                    ((DOMResult)result).setNode(doc);
                    this.write(so, new SAX2DOMEx(doc));
                    return;
                }
                catch (ParserConfigurationException pce) {
                    throw new JAXBAssertionError((Throwable)pce);
                }
            }
            this.write(so, new SAX2DOMEx(node));
            return;
        }
        if (!(result instanceof StreamResult)) {
            throw new MarshalException(Messages.format("MarshallerImpl.UnsupportedResult"));
        }
        final StreamResult sr = (StreamResult)result;
        XMLWriter w = null;
        if (sr.getWriter() != null) {
            w = this.createWriter(sr.getWriter());
        }
        else if (sr.getOutputStream() != null) {
            w = this.createWriter(sr.getOutputStream());
        }
        else if (sr.getSystemId() != null) {
            String fileURL = sr.getSystemId();
            if (fileURL.startsWith("file:///")) {
                if (fileURL.substring(8).indexOf(":") > 0) {
                    fileURL = fileURL.substring(8);
                }
                else {
                    fileURL = fileURL.substring(7);
                }
            }
            try {
                w = this.createWriter(new FileOutputStream(fileURL));
            }
            catch (IOException e) {
                throw new MarshalException(e);
            }
        }
        if (w == null) {
            throw new IllegalArgumentException();
        }
        this.write(so, w);
    }
    
    private void write(final XMLSerializable obj, ContentHandler writer) throws JAXBException {
        try {
            if (this.getSchemaLocation() != null || this.getNoNSSchemaLocation() != null) {
                writer = (ContentHandler)new SchemaLocationFilter(this.getSchemaLocation(), this.getNoNSSchemaLocation(), writer);
            }
            final SAXMarshaller serializer = new SAXMarshaller(writer, this.prefixMapper, this);
            writer.setDocumentLocator(new LocatorImpl());
            writer.startDocument();
            serializer.childAsBody((JAXBObject)obj, (String)null);
            writer.endDocument();
            serializer.reconcileID();
        }
        catch (SAXException e) {
            throw new MarshalException(e);
        }
    }
    
    protected CharacterEscapeHandler createEscapeHandler(final String encoding) {
        if (this.escapeHandler != null) {
            return this.escapeHandler;
        }
        if (encoding.startsWith("UTF")) {
            return MinimumEscapeHandler.theInstance;
        }
        try {
            return new NioEscapeHandler(this.getJavaEncoding(encoding));
        }
        catch (Throwable e) {
            return DumbEscapeHandler.theInstance;
        }
    }
    
    public XMLWriter createWriter(Writer w, final String encoding) throws JAXBException {
        w = new BufferedWriter(w);
        final CharacterEscapeHandler ceh = this.createEscapeHandler(encoding);
        XMLWriter xw;
        if (this.isFormattedOutput()) {
            final DataWriter d = new DataWriter(w, encoding, ceh);
            d.setIndentStep(this.indent);
            xw = d;
        }
        else {
            xw = new XMLWriter(w, encoding, ceh);
        }
        xw.setXmlDecl(this.printXmlDeclaration);
        xw.setHeader(this.header);
        return xw;
    }
    
    public XMLWriter createWriter(final Writer w) throws JAXBException {
        return this.createWriter(w, this.getEncoding());
    }
    
    public XMLWriter createWriter(final OutputStream os) throws JAXBException {
        return this.createWriter(os, this.getEncoding());
    }
    
    public XMLWriter createWriter(final OutputStream os, final String encoding) throws JAXBException {
        try {
            return this.createWriter(new OutputStreamWriter(os, this.getJavaEncoding(encoding)), encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new MarshalException(Messages.format("MarshallerImpl.UnsupportedEncoding", encoding), e);
        }
    }
    
    public Object getProperty(final String name) throws PropertyException {
        if ("com.sun.xml.bind.indentString".equals(name)) {
            return this.indent;
        }
        if ("com.sun.xml.bind.characterEscapeHandler".equals(name)) {
            return this.escapeHandler;
        }
        if ("com.sun.xml.bind.namespacePrefixMapper".equals(name)) {
            return this.prefixMapper;
        }
        if ("com.sun.xml.bind.xmlDeclaration".equals(name)) {
            return this.printXmlDeclaration ? Boolean.TRUE : Boolean.FALSE;
        }
        if ("com.sun.xml.bind.xmlHeaders".equals(name)) {
            return this.header;
        }
        return super.getProperty(name);
    }
    
    public void setProperty(final String name, final Object value) throws PropertyException {
        if ("com.sun.xml.bind.indentString".equals(name) && value instanceof String) {
            this.indent = (String)value;
            return;
        }
        if ("com.sun.xml.bind.characterEscapeHandler".equals(name)) {
            this.escapeHandler = (CharacterEscapeHandler)value;
            return;
        }
        if ("com.sun.xml.bind.namespacePrefixMapper".equals(name)) {
            this.prefixMapper = (NamespacePrefixMapper)value;
            return;
        }
        if ("com.sun.xml.bind.xmlDeclaration".equals(name)) {
            this.printXmlDeclaration = (boolean)value;
            return;
        }
        if ("com.sun.xml.bind.xmlHeaders".equals(name)) {
            this.header = (String)value;
            return;
        }
        super.setProperty(name, value);
    }
}

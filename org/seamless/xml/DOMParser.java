// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import org.w3c.dom.NodeList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.InputStream;
import java.io.File;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSResourceResolver;
import java.util.Map;
import java.net.URI;
import java.util.HashMap;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import java.net.URL;
import java.util.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public abstract class DOMParser<D extends DOM> implements ErrorHandler, EntityResolver
{
    private static Logger log;
    public static final URL XML_SCHEMA_RESOURCE;
    protected Source[] schemaSources;
    protected Schema schema;
    
    public DOMParser() {
        this(null);
    }
    
    public DOMParser(final Source[] schemaSources) {
        this.schemaSources = schemaSources;
    }
    
    public Schema getSchema() {
        if (this.schema == null) {
            try {
                final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                schemaFactory.setResourceResolver(new CatalogResourceResolver(new HashMap<URI, URL>() {
                    {
                        this.put(DOM.XML_SCHEMA_NAMESPACE, DOMParser.XML_SCHEMA_RESOURCE);
                    }
                }));
                if (this.schemaSources != null) {
                    this.schema = schemaFactory.newSchema(this.schemaSources);
                }
                else {
                    this.schema = schemaFactory.newSchema();
                }
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return this.schema;
    }
    
    protected abstract D createDOM(final Document p0);
    
    public DocumentBuilderFactory createFactory(final boolean validating) throws ParserException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setNamespaceAware(true);
            if (validating) {
                factory.setXIncludeAware(true);
                factory.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
                factory.setFeature("http://apache.org/xml/features/xinclude/fixup-language", false);
                factory.setSchema(this.getSchema());
                factory.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            }
        }
        catch (ParserConfigurationException ex) {
            throw new ParserException(ex);
        }
        return factory;
    }
    
    public Transformer createTransformer(final String method, final int indent, final boolean standalone) throws ParserException {
        try {
            final TransformerFactory transFactory = TransformerFactory.newInstance();
            if (indent > 0) {
                try {
                    transFactory.setAttribute("indent-number", indent);
                }
                catch (IllegalArgumentException ex2) {}
            }
            final Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", standalone ? "no" : "yes");
            if (standalone) {
                try {
                    transformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
                }
                catch (IllegalArgumentException ex3) {}
            }
            transformer.setOutputProperty("indent", (indent > 0) ? "yes" : "no");
            if (indent > 0) {
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
            }
            transformer.setOutputProperty("method", method);
            return transformer;
        }
        catch (Exception ex) {
            throw new ParserException(ex);
        }
    }
    
    public D createDocument() {
        try {
            return this.createDOM(this.createFactory(false).newDocumentBuilder().newDocument());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public D parse(final URL url) throws ParserException {
        return this.parse(url, true);
    }
    
    public D parse(final String string) throws ParserException {
        return this.parse(string, true);
    }
    
    public D parse(final File file) throws ParserException {
        return this.parse(file, true);
    }
    
    public D parse(final InputStream stream) throws ParserException {
        return this.parse(stream, true);
    }
    
    public D parse(final URL url, final boolean validate) throws ParserException {
        if (url == null) {
            throw new IllegalArgumentException("Can't parse null URL");
        }
        try {
            return this.parse(url.openStream(), validate);
        }
        catch (Exception ex) {
            throw new ParserException("Parsing URL failed: " + url, ex);
        }
    }
    
    public D parse(final String string, final boolean validate) throws ParserException {
        if (string == null) {
            throw new IllegalArgumentException("Can't parse null string");
        }
        return this.parse(new InputSource(new StringReader(string)), validate);
    }
    
    public D parse(final File file, final boolean validate) throws ParserException {
        if (file == null) {
            throw new IllegalArgumentException("Can't parse null file");
        }
        try {
            return this.parse(file.toURI().toURL(), validate);
        }
        catch (Exception ex) {
            throw new ParserException("Parsing file failed: " + file, ex);
        }
    }
    
    public D parse(final InputStream stream, final boolean validate) throws ParserException {
        return this.parse(new InputSource(stream), validate);
    }
    
    public D parse(final InputSource source, final boolean validate) throws ParserException {
        try {
            final DocumentBuilder parser = this.createFactory(validate).newDocumentBuilder();
            parser.setEntityResolver(this);
            parser.setErrorHandler(this);
            final Document dom = parser.parse(source);
            dom.normalizeDocument();
            return this.createDOM(dom);
        }
        catch (Exception ex) {
            throw this.unwrapException(ex);
        }
    }
    
    public void validate(final URL url) throws ParserException {
        if (url == null) {
            throw new IllegalArgumentException("Can't validate null URL");
        }
        DOMParser.log.fine("Validating XML of URL: " + url);
        this.validate(new StreamSource(url.toString()));
    }
    
    public void validate(final String string) throws ParserException {
        if (string == null) {
            throw new IllegalArgumentException("Can't validate null string");
        }
        DOMParser.log.fine("Validating XML string characters: " + string.length());
        this.validate(new SAXSource(new InputSource(new StringReader(string))));
    }
    
    public void validate(final Document document) throws ParserException {
        this.validate(new DOMSource(document));
    }
    
    public void validate(final DOM dom) throws ParserException {
        this.validate(new DOMSource(dom.getW3CDocument()));
    }
    
    public void validate(final Source source) throws ParserException {
        try {
            final Validator validator = this.getSchema().newValidator();
            validator.setErrorHandler(this);
            validator.validate(source);
        }
        catch (Exception ex) {
            throw this.unwrapException(ex);
        }
    }
    
    public XPathFactory createXPathFactory() {
        return XPathFactory.newInstance();
    }
    
    public XPath createXPath(final NamespaceContext nsContext) {
        final XPath xpath = this.createXPathFactory().newXPath();
        xpath.setNamespaceContext(nsContext);
        return xpath;
    }
    
    public XPath createXPath(final XPathFactory factory, final NamespaceContext nsContext) {
        final XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(nsContext);
        return xpath;
    }
    
    public Object getXPathResult(final DOM dom, final XPath xpath, final String expr, final QName result) {
        return this.getXPathResult(dom.getW3CDocument(), xpath, expr, result);
    }
    
    public Object getXPathResult(final DOMElement element, final XPath xpath, final String expr, final QName result) {
        return this.getXPathResult(element.getW3CElement(), xpath, expr, result);
    }
    
    public Object getXPathResult(final Node context, final XPath xpath, final String expr, final QName result) {
        try {
            DOMParser.log.fine("Evaluating xpath query: " + expr);
            return xpath.evaluate(expr, context, result);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public String print(final DOM dom) throws ParserException {
        return this.print(dom, 4, true);
    }
    
    public String print(final DOM dom, final int indent) throws ParserException {
        return this.print(dom, indent, true);
    }
    
    public String print(final DOM dom, final boolean standalone) throws ParserException {
        return this.print(dom, 4, standalone);
    }
    
    public String print(final DOM dom, final int indent, final boolean standalone) throws ParserException {
        return this.print(dom.getW3CDocument(), indent, standalone);
    }
    
    public String print(final Document document, final int indent, final boolean standalone) throws ParserException {
        this.removeIgnorableWSNodes(document.getDocumentElement());
        return this.print(new DOMSource(document.getDocumentElement()), indent, standalone);
    }
    
    public String print(final String string, final int indent, final boolean standalone) throws ParserException {
        return this.print(new StreamSource(new StringReader(string)), indent, standalone);
    }
    
    public String print(final Source source, final int indent, final boolean standalone) throws ParserException {
        try {
            final Transformer transformer = this.createTransformer("xml", indent, standalone);
            transformer.setOutputProperty("encoding", "utf-8");
            final StringWriter out = new StringWriter();
            transformer.transform(source, new StreamResult(out));
            out.flush();
            return out.toString();
        }
        catch (Exception e) {
            throw new ParserException(e);
        }
    }
    
    public String printHTML(final Document dom) throws ParserException {
        return this.printHTML(dom, 4, true, true);
    }
    
    public String printHTML(Document dom, final int indent, final boolean standalone, final boolean doctype) throws ParserException {
        dom = (Document)dom.cloneNode(true);
        accept(dom.getDocumentElement(), new NodeVisitor(4) {
            public void visit(final Node node) {
                final CDATASection cdata = (CDATASection)node;
                cdata.getParentNode().setTextContent(cdata.getData());
            }
        });
        this.removeIgnorableWSNodes(dom.getDocumentElement());
        try {
            final Transformer transformer = this.createTransformer("html", indent, standalone);
            if (doctype) {
                transformer.setOutputProperty("doctype-public", "-//W3C//DTD HTML 4.01 Transitional//EN");
                transformer.setOutputProperty("doctype-system", "http://www.w3.org/TR/html4/loose.dtd");
            }
            final StringWriter out = new StringWriter();
            transformer.transform(new DOMSource(dom), new StreamResult(out));
            out.flush();
            String output = out.toString();
            final String meta = "\\s*<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
            output = output.replaceFirst(meta, "");
            final String xmlns = "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
            output = output.replaceFirst(xmlns, "<html>");
            return output;
        }
        catch (Exception ex) {
            throw new ParserException(ex);
        }
    }
    
    public void removeIgnorableWSNodes(final Element element) {
        Node nextNode = element.getFirstChild();
        while (nextNode != null) {
            final Node child = nextNode;
            nextNode = child.getNextSibling();
            if (this.isIgnorableWSNode(child)) {
                element.removeChild(child);
            }
            else {
                if (child.getNodeType() != 1) {
                    continue;
                }
                this.removeIgnorableWSNodes((Element)child);
            }
        }
    }
    
    public boolean isIgnorableWSNode(final Node node) {
        return node.getNodeType() == 3 && node.getTextContent().matches("[\\t\\n\\x0B\\f\\r\\s]+");
    }
    
    public void warning(final SAXParseException e) throws SAXException {
        DOMParser.log.warning(e.toString());
    }
    
    public void error(final SAXParseException e) throws SAXException {
        throw new SAXException(new ParserException(e));
    }
    
    public void fatalError(final SAXParseException e) throws SAXException {
        throw new SAXException(new ParserException(e));
    }
    
    protected ParserException unwrapException(final Exception ex) {
        if (ex.getCause() != null && ex.getCause() instanceof ParserException) {
            return (ParserException)ex.getCause();
        }
        return new ParserException(ex);
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        InputSource is;
        if (systemId.startsWith("file://")) {
            is = new InputSource(new FileInputStream(new File(URI.create(systemId))));
        }
        else {
            is = new InputSource(new ByteArrayInputStream(new byte[0]));
        }
        is.setPublicId(publicId);
        is.setSystemId(systemId);
        return is;
    }
    
    public static String escape(final String string) {
        return escape(string, false, false);
    }
    
    public static String escape(final String string, final boolean convertNewlines, final boolean convertSpaces) {
        if (string == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            String entity = null;
            final char c = string.charAt(i);
            switch (c) {
                case '<': {
                    entity = "&#60;";
                    break;
                }
                case '>': {
                    entity = "&#62;";
                    break;
                }
                case '&': {
                    entity = "&#38;";
                    break;
                }
                case '\"': {
                    entity = "&#34;";
                    break;
                }
            }
            if (entity != null) {
                sb.append(entity);
            }
            else {
                sb.append(c);
            }
        }
        String result = sb.toString();
        if (convertSpaces) {
            final Matcher matcher = Pattern.compile("(\\n+)(\\s*)(.*)").matcher(result);
            final StringBuffer temp = new StringBuffer();
            while (matcher.find()) {
                final String group = matcher.group(2);
                final StringBuilder spaces = new StringBuilder();
                for (int j = 0; j < group.length(); ++j) {
                    spaces.append("&#160;");
                }
                matcher.appendReplacement(temp, "$1" + spaces.toString() + "$3");
            }
            matcher.appendTail(temp);
            result = temp.toString();
        }
        if (convertNewlines) {
            result = result.replaceAll("\n", "<br/>");
        }
        return result;
    }
    
    public static String stripElements(final String xml) {
        if (xml == null) {
            return null;
        }
        return xml.replaceAll("<([a-zA-Z]|/).*?>", "");
    }
    
    public static void accept(final Node node, final NodeVisitor visitor) {
        if (node == null) {
            return;
        }
        if (visitor.isHalted()) {
            return;
        }
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            final boolean cont = true;
            if (child.getNodeType() == visitor.nodeType) {
                visitor.visit(child);
                if (visitor.isHalted()) {
                    break;
                }
            }
            accept(child, visitor);
        }
    }
    
    public static String wrap(final String wrapperName, final String fragment) {
        return wrap(wrapperName, null, fragment);
    }
    
    public static String wrap(final String wrapperName, final String xmlns, final String fragment) {
        final StringBuilder wrapper = new StringBuilder();
        wrapper.append("<").append(wrapperName);
        if (xmlns != null) {
            wrapper.append(" xmlns=\"").append(xmlns).append("\"");
        }
        wrapper.append(">");
        wrapper.append(fragment);
        wrapper.append("</").append(wrapperName).append(">");
        return wrapper.toString();
    }
    
    static {
        DOMParser.log = Logger.getLogger(DOMParser.class.getName());
        XML_SCHEMA_RESOURCE = Thread.currentThread().getContextClassLoader().getResource("org/seamless/schemas/xml.xsd");
    }
    
    public abstract static class NodeVisitor
    {
        private short nodeType;
        
        protected NodeVisitor(final short nodeType) {
            assert nodeType < 12;
            this.nodeType = nodeType;
        }
        
        public boolean isHalted() {
            return false;
        }
        
        public abstract void visit(final Node p0);
    }
}

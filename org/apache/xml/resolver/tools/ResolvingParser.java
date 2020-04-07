// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.tools;

import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import org.apache.xml.resolver.helpers.Debug;
import org.apache.xml.resolver.CatalogManager;
import java.net.MalformedURLException;
import java.util.Locale;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.apache.xml.resolver.Catalog;
import javax.xml.parsers.SAXParserFactory;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.DocumentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Parser;

public class ResolvingParser implements Parser, DTDHandler, DocumentHandler, EntityResolver
{
    public static boolean namespaceAware;
    public static boolean validating;
    public static boolean suppressExplanation;
    private SAXParser saxParser;
    private Parser parser;
    private DocumentHandler documentHandler;
    private DTDHandler dtdHandler;
    private CatalogResolver catalogResolver;
    private CatalogResolver piCatalogResolver;
    private boolean allowXMLCatalogPI;
    private boolean oasisXMLCatalogPI;
    private URL baseURL;
    
    public ResolvingParser() {
        this.saxParser = null;
        this.parser = null;
        this.documentHandler = null;
        this.dtdHandler = null;
        this.catalogResolver = new CatalogResolver();
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        final SAXParserFactory instance = SAXParserFactory.newInstance();
        instance.setNamespaceAware(ResolvingParser.namespaceAware);
        instance.setValidating(ResolvingParser.validating);
        try {
            this.saxParser = instance.newSAXParser();
            this.parser = this.saxParser.getParser();
            this.documentHandler = null;
            this.dtdHandler = null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Catalog getCatalog() {
        return this.catalogResolver.getCatalog();
    }
    
    public void parse(final InputSource inputSource) throws IOException, SAXException {
        this.setupParse(inputSource.getSystemId());
        try {
            this.parser.parse(inputSource);
        }
        catch (InternalError internalError) {
            this.explain(inputSource.getSystemId());
            throw internalError;
        }
    }
    
    public void parse(final String s) throws IOException, SAXException {
        this.setupParse(s);
        try {
            this.parser.parse(s);
        }
        catch (InternalError internalError) {
            this.explain(s);
            throw internalError;
        }
    }
    
    public void setDocumentHandler(final DocumentHandler documentHandler) {
        this.documentHandler = documentHandler;
    }
    
    public void setDTDHandler(final DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.parser.setErrorHandler(errorHandler);
    }
    
    public void setLocale(final Locale locale) throws SAXException {
        this.parser.setLocale(locale);
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.characters(array, n, n2);
        }
    }
    
    public void endDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endDocument();
        }
    }
    
    public void endElement(final String s) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endElement(s);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.ignorableWhitespace(array, n, n2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (s.equals("oasis-xml-catalog")) {
            URL url = null;
            final int index = s2.indexOf("catalog=");
            if (index >= 0) {
                final String substring = s2.substring(index + 8);
                if (substring.length() > 1) {
                    final String substring2 = substring.substring(0, 1);
                    final String substring3 = substring.substring(1);
                    final int index2 = substring3.indexOf(substring2);
                    if (index2 >= 0) {
                        final String substring4 = substring3.substring(0, index2);
                        try {
                            if (this.baseURL != null) {
                                url = new URL(this.baseURL, substring4);
                            }
                            else {
                                url = new URL(substring4);
                            }
                        }
                        catch (MalformedURLException ex) {}
                    }
                }
            }
            if (this.allowXMLCatalogPI) {
                if (CatalogManager.allowOasisXMLCatalogPI()) {
                    Debug.message(4, "oasis-xml-catalog PI", s2);
                    if (url != null) {
                        try {
                            Debug.message(4, "oasis-xml-catalog", url.toString());
                            this.oasisXMLCatalogPI = true;
                            if (this.piCatalogResolver == null) {
                                this.piCatalogResolver = new CatalogResolver(true);
                            }
                            this.piCatalogResolver.getCatalog().parseCatalog(url.toString());
                        }
                        catch (Exception ex2) {
                            Debug.message(3, "Exception parsing oasis-xml-catalog: " + url.toString());
                        }
                    }
                    else {
                        Debug.message(3, "PI oasis-xml-catalog unparseable: " + s2);
                    }
                }
                else {
                    Debug.message(4, "PI oasis-xml-catalog ignored: " + s2);
                }
            }
            else {
                Debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + s2);
            }
        }
        else if (this.documentHandler != null) {
            this.documentHandler.processingInstruction(s, s2);
        }
    }
    
    public void setDocumentLocator(final Locator documentLocator) {
        if (this.documentHandler != null) {
            this.documentHandler.setDocumentLocator(documentLocator);
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.startDocument();
        }
    }
    
    public void startElement(final String s, final AttributeList list) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.documentHandler != null) {
            this.documentHandler.startElement(s, list);
        }
    }
    
    public void notationDecl(final String s, final String s2, final String s3) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.dtdHandler != null) {
            this.dtdHandler.notationDecl(s, s2, s3);
        }
    }
    
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.dtdHandler != null) {
            this.dtdHandler.unparsedEntityDecl(s, s2, s3, s4);
        }
    }
    
    public InputSource resolveEntity(final String publicId, final String s) {
        this.allowXMLCatalogPI = false;
        String s2 = this.catalogResolver.getResolvedEntity(publicId, s);
        if (s2 == null && this.piCatalogResolver != null) {
            s2 = this.piCatalogResolver.getResolvedEntity(publicId, s);
        }
        if (s2 != null) {
            try {
                final InputSource inputSource = new InputSource(s2);
                inputSource.setPublicId(publicId);
                inputSource.setByteStream(new URL(s2).openStream());
                return inputSource;
            }
            catch (Exception ex) {
                Debug.message(1, "Failed to create InputSource", s2);
                return null;
            }
        }
        return null;
    }
    
    private void setupParse(final String s) {
        this.allowXMLCatalogPI = true;
        this.parser.setEntityResolver(this);
        this.parser.setDocumentHandler(this);
        this.parser.setDTDHandler(this);
        final String property = System.getProperty("user.dir");
        property.replace('\\', '/');
        URL url;
        try {
            url = new URL("file:///" + property + "/basename");
        }
        catch (MalformedURLException ex) {
            url = null;
        }
        try {
            this.baseURL = new URL(s);
        }
        catch (MalformedURLException ex2) {
            if (url != null) {
                try {
                    this.baseURL = new URL(url, s);
                }
                catch (MalformedURLException ex3) {
                    this.baseURL = null;
                }
            }
            else {
                this.baseURL = null;
            }
        }
    }
    
    private void explain(final String s) {
        if (!ResolvingParser.suppressExplanation) {
            System.out.println("Parser probably encountered bad URI in " + s);
            System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
        }
    }
    
    static {
        ResolvingParser.namespaceAware = true;
        ResolvingParser.validating = false;
        ResolvingParser.suppressExplanation = false;
    }
}

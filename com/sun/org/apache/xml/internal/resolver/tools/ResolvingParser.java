// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.io.InputStream;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import java.net.MalformedURLException;
import java.util.Locale;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import javax.xml.parsers.SAXParserFactory;
import java.net.URL;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
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
    private CatalogManager catalogManager;
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
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.initParser();
    }
    
    public ResolvingParser(final CatalogManager manager) {
        this.saxParser = null;
        this.parser = null;
        this.documentHandler = null;
        this.dtdHandler = null;
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.catalogManager = manager;
        this.initParser();
    }
    
    private void initParser() {
        this.catalogResolver = new CatalogResolver(this.catalogManager);
        final SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(ResolvingParser.namespaceAware);
        spf.setValidating(ResolvingParser.validating);
        try {
            this.saxParser = spf.newSAXParser();
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
    
    public void parse(final InputSource input) throws IOException, SAXException {
        this.setupParse(input.getSystemId());
        try {
            this.parser.parse(input);
        }
        catch (InternalError ie) {
            this.explain(input.getSystemId());
            throw ie;
        }
    }
    
    public void parse(final String systemId) throws IOException, SAXException {
        this.setupParse(systemId);
        try {
            this.parser.parse(systemId);
        }
        catch (InternalError ie) {
            this.explain(systemId);
            throw ie;
        }
    }
    
    public void setDocumentHandler(final DocumentHandler handler) {
        this.documentHandler = handler;
    }
    
    public void setDTDHandler(final DTDHandler handler) {
        this.dtdHandler = handler;
    }
    
    public void setEntityResolver(final EntityResolver resolver) {
    }
    
    public void setErrorHandler(final ErrorHandler handler) {
        this.parser.setErrorHandler(handler);
    }
    
    public void setLocale(final Locale locale) throws SAXException {
        this.parser.setLocale(locale);
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.characters(ch, start, length);
        }
    }
    
    public void endDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endDocument();
        }
    }
    
    public void endElement(final String name) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.endElement(name);
        }
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.ignorableWhitespace(ch, start, length);
        }
    }
    
    public void processingInstruction(final String target, final String pidata) throws SAXException {
        if (target.equals("oasis-xml-catalog")) {
            URL catalog = null;
            int pos = pidata.indexOf("catalog=");
            if (pos >= 0) {
                String data = pidata.substring(pos + 8);
                if (data.length() > 1) {
                    final String quote = data.substring(0, 1);
                    data = data.substring(1);
                    pos = data.indexOf(quote);
                    if (pos >= 0) {
                        data = data.substring(0, pos);
                        try {
                            if (this.baseURL != null) {
                                catalog = new URL(this.baseURL, data);
                            }
                            else {
                                catalog = new URL(data);
                            }
                        }
                        catch (MalformedURLException ex) {}
                    }
                }
            }
            if (this.allowXMLCatalogPI) {
                if (this.catalogManager.getAllowOasisXMLCatalogPI()) {
                    this.catalogManager.debug.message(4, "oasis-xml-catalog PI", pidata);
                    if (catalog != null) {
                        try {
                            this.catalogManager.debug.message(4, "oasis-xml-catalog", catalog.toString());
                            this.oasisXMLCatalogPI = true;
                            if (this.piCatalogResolver == null) {
                                this.piCatalogResolver = new CatalogResolver(true);
                            }
                            this.piCatalogResolver.getCatalog().parseCatalog(catalog.toString());
                        }
                        catch (Exception e) {
                            this.catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + catalog.toString());
                        }
                    }
                    else {
                        this.catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + pidata);
                    }
                }
                else {
                    this.catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + pidata);
                }
            }
            else {
                this.catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + pidata);
            }
        }
        else if (this.documentHandler != null) {
            this.documentHandler.processingInstruction(target, pidata);
        }
    }
    
    public void setDocumentLocator(final Locator locator) {
        if (this.documentHandler != null) {
            this.documentHandler.setDocumentLocator(locator);
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.documentHandler != null) {
            this.documentHandler.startDocument();
        }
    }
    
    public void startElement(final String name, final AttributeList atts) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.documentHandler != null) {
            this.documentHandler.startElement(name, atts);
        }
    }
    
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.dtdHandler != null) {
            this.dtdHandler.notationDecl(name, publicId, systemId);
        }
    }
    
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        this.allowXMLCatalogPI = false;
        if (this.dtdHandler != null) {
            this.dtdHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) {
        this.allowXMLCatalogPI = false;
        String resolved = this.catalogResolver.getResolvedEntity(publicId, systemId);
        if (resolved == null && this.piCatalogResolver != null) {
            resolved = this.piCatalogResolver.getResolvedEntity(publicId, systemId);
        }
        if (resolved != null) {
            try {
                final InputSource iSource = new InputSource(resolved);
                iSource.setPublicId(publicId);
                final URL url = new URL(resolved);
                final InputStream iStream = url.openStream();
                iSource.setByteStream(iStream);
                return iSource;
            }
            catch (Exception e) {
                this.catalogManager.debug.message(1, "Failed to create InputSource", resolved);
                return null;
            }
        }
        return null;
    }
    
    private void setupParse(final String systemId) {
        this.allowXMLCatalogPI = true;
        this.parser.setEntityResolver(this);
        this.parser.setDocumentHandler(this);
        this.parser.setDTDHandler(this);
        URL cwd = null;
        try {
            cwd = FileURL.makeURL("basename");
        }
        catch (MalformedURLException mue) {
            cwd = null;
        }
        try {
            this.baseURL = new URL(systemId);
        }
        catch (MalformedURLException mue) {
            if (cwd != null) {
                try {
                    this.baseURL = new URL(cwd, systemId);
                }
                catch (MalformedURLException mue2) {
                    this.baseURL = null;
                }
            }
            else {
                this.baseURL = null;
            }
        }
    }
    
    private void explain(final String systemId) {
        if (!ResolvingParser.suppressExplanation) {
            System.out.println("Parser probably encountered bad URI in " + systemId);
            System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
        }
    }
    
    static {
        ResolvingParser.namespaceAware = true;
        ResolvingParser.validating = false;
        ResolvingParser.suppressExplanation = false;
    }
}

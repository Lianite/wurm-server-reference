// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver.readers;

import org.xml.sax.Attributes;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import javax.xml.parsers.SAXParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import java.net.UnknownHostException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import java.io.InputStream;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import java.io.IOException;
import java.net.URLConnection;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import java.util.Hashtable;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;

public class SAXCatalogReader implements CatalogReader, ContentHandler, DocumentHandler
{
    protected SAXParserFactory parserFactory;
    protected String parserClass;
    protected Hashtable namespaceMap;
    private SAXCatalogParser saxParser;
    private boolean abandonHope;
    private Catalog catalog;
    protected Debug debug;
    
    public void setParserFactory(final SAXParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }
    
    public void setParserClass(final String parserClass) {
        this.parserClass = parserClass;
    }
    
    public SAXParserFactory getParserFactory() {
        return this.parserFactory;
    }
    
    public String getParserClass() {
        return this.parserClass;
    }
    
    public SAXCatalogReader() {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.debug = CatalogManager.getStaticManager().debug;
        this.parserFactory = null;
        this.parserClass = null;
    }
    
    public SAXCatalogReader(final SAXParserFactory parserFactory) {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.debug = CatalogManager.getStaticManager().debug;
        this.parserFactory = parserFactory;
    }
    
    public SAXCatalogReader(final String parserClass) {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.debug = CatalogManager.getStaticManager().debug;
        this.parserClass = parserClass;
    }
    
    public void setCatalogParser(final String namespaceURI, final String rootElement, final String parserClass) {
        if (namespaceURI == null) {
            this.namespaceMap.put(rootElement, parserClass);
        }
        else {
            this.namespaceMap.put("{" + namespaceURI + "}" + rootElement, parserClass);
        }
    }
    
    public String getCatalogParser(final String namespaceURI, final String rootElement) {
        if (namespaceURI == null) {
            return this.namespaceMap.get(rootElement);
        }
        return this.namespaceMap.get("{" + namespaceURI + "}" + rootElement);
    }
    
    public void readCatalog(final Catalog catalog, final String fileUrl) throws MalformedURLException, IOException, CatalogException {
        URL url = null;
        try {
            url = new URL(fileUrl);
        }
        catch (MalformedURLException e) {
            url = new URL("file:///" + fileUrl);
        }
        this.debug = catalog.getCatalogManager().debug;
        try {
            final URLConnection urlCon = url.openConnection();
            this.readCatalog(catalog, urlCon.getInputStream());
        }
        catch (FileNotFoundException e2) {
            catalog.getCatalogManager().debug.message(1, "Failed to load catalog, file not found", url.toString());
        }
    }
    
    public void readCatalog(final Catalog catalog, final InputStream is) throws IOException, CatalogException {
        if (this.parserFactory == null && this.parserClass == null) {
            this.debug.message(1, "Cannot read SAX catalog without a parser");
            throw new CatalogException(6);
        }
        this.debug = catalog.getCatalogManager().debug;
        final EntityResolver bResolver = catalog.getCatalogManager().getBootstrapResolver();
        this.catalog = catalog;
        try {
            if (this.parserFactory != null) {
                final SAXParser parser = this.parserFactory.newSAXParser();
                final SAXParserHandler spHandler = new SAXParserHandler();
                spHandler.setContentHandler(this);
                if (bResolver != null) {
                    spHandler.setEntityResolver(bResolver);
                }
                parser.parse(new InputSource(is), spHandler);
            }
            else {
                final Parser parser2 = (Parser)Class.forName(this.parserClass).newInstance();
                parser2.setDocumentHandler(this);
                if (bResolver != null) {
                    parser2.setEntityResolver(bResolver);
                }
                parser2.parse(new InputSource(is));
            }
        }
        catch (ClassNotFoundException cnfe) {
            throw new CatalogException(6);
        }
        catch (IllegalAccessException iae) {
            throw new CatalogException(6);
        }
        catch (InstantiationException ie) {
            throw new CatalogException(6);
        }
        catch (ParserConfigurationException pce) {
            throw new CatalogException(5);
        }
        catch (SAXException se) {
            final Exception e = se.getException();
            final UnknownHostException uhe = new UnknownHostException();
            final FileNotFoundException fnfe = new FileNotFoundException();
            if (e != null) {
                if (e.getClass() == uhe.getClass()) {
                    throw new CatalogException(7, e.toString());
                }
                if (e.getClass() == fnfe.getClass()) {
                    throw new CatalogException(7, e.toString());
                }
            }
            throw new CatalogException(se);
        }
    }
    
    public void setDocumentLocator(final Locator locator) {
        if (this.saxParser != null) {
            this.saxParser.setDocumentLocator(locator);
        }
    }
    
    public void startDocument() throws SAXException {
        this.saxParser = null;
        this.abandonHope = false;
    }
    
    public void endDocument() throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.endDocument();
        }
    }
    
    public void startElement(final String name, final AttributeList atts) throws SAXException {
        if (this.abandonHope) {
            return;
        }
        if (this.saxParser == null) {
            String prefix = "";
            if (name.indexOf(58) > 0) {
                prefix = name.substring(0, name.indexOf(58));
            }
            String localName = name;
            if (localName.indexOf(58) > 0) {
                localName = localName.substring(localName.indexOf(58) + 1);
            }
            String namespaceURI = null;
            if (prefix.equals("")) {
                namespaceURI = atts.getValue("xmlns");
            }
            else {
                namespaceURI = atts.getValue("xmlns:" + prefix);
            }
            final String saxParserClass = this.getCatalogParser(namespaceURI, localName);
            if (saxParserClass == null) {
                this.abandonHope = true;
                if (namespaceURI == null) {
                    this.debug.message(2, "No Catalog parser for " + name);
                }
                else {
                    this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + name);
                }
                return;
            }
            try {
                (this.saxParser = (SAXCatalogParser)Class.forName(saxParserClass).newInstance()).setCatalog(this.catalog);
                this.saxParser.startDocument();
                this.saxParser.startElement(name, atts);
            }
            catch (ClassNotFoundException cnfe) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, cnfe.toString());
            }
            catch (InstantiationException ie) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, ie.toString());
            }
            catch (IllegalAccessException iae) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, iae.toString());
            }
            catch (ClassCastException cce) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, cce.toString());
            }
        }
        else {
            this.saxParser.startElement(name, atts);
        }
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.abandonHope) {
            return;
        }
        if (this.saxParser == null) {
            final String saxParserClass = this.getCatalogParser(namespaceURI, localName);
            if (saxParserClass == null) {
                this.abandonHope = true;
                if (namespaceURI == null) {
                    this.debug.message(2, "No Catalog parser for " + localName);
                }
                else {
                    this.debug.message(2, "No Catalog parser for {" + namespaceURI + "}" + localName);
                }
                return;
            }
            try {
                (this.saxParser = (SAXCatalogParser)Class.forName(saxParserClass).newInstance()).setCatalog(this.catalog);
                this.saxParser.startDocument();
                this.saxParser.startElement(namespaceURI, localName, qName, atts);
            }
            catch (ClassNotFoundException cnfe) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, cnfe.toString());
            }
            catch (InstantiationException ie) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, ie.toString());
            }
            catch (IllegalAccessException iae) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, iae.toString());
            }
            catch (ClassCastException cce) {
                this.saxParser = null;
                this.abandonHope = true;
                this.debug.message(2, cce.toString());
            }
        }
        else {
            this.saxParser.startElement(namespaceURI, localName, qName, atts);
        }
    }
    
    public void endElement(final String name) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.endElement(name);
        }
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.endElement(namespaceURI, localName, qName);
        }
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.characters(ch, start, length);
        }
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.ignorableWhitespace(ch, start, length);
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.processingInstruction(target, data);
        }
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.startPrefixMapping(prefix, uri);
        }
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.endPrefixMapping(prefix);
        }
    }
    
    public void skippedEntity(final String name) throws SAXException {
        if (this.saxParser != null) {
            this.saxParser.skippedEntity(name);
        }
    }
}

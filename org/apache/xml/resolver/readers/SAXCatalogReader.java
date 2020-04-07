// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.xml.sax.Attributes;
import org.xml.sax.AttributeList;
import org.xml.sax.Locator;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import java.net.UnknownHostException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import java.io.InputStream;
import org.apache.xml.resolver.CatalogException;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.xml.resolver.helpers.Debug;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xml.resolver.Catalog;
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
    
    public void setCatalogParser(final String s, final String s2, final String s3) {
        if (s == null) {
            this.namespaceMap.put(s2, s3);
        }
        else {
            this.namespaceMap.put("{" + s + "}" + s2, s3);
        }
    }
    
    public String getCatalogParser(final String s, final String s2) {
        if (s == null) {
            return this.namespaceMap.get(s2);
        }
        return this.namespaceMap.get("{" + s + "}" + s2);
    }
    
    public SAXCatalogReader() {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.parserFactory = null;
        this.parserClass = null;
    }
    
    public SAXCatalogReader(final SAXParserFactory parserFactory) {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.parserFactory = parserFactory;
    }
    
    public SAXCatalogReader(final String parserClass) {
        this.parserFactory = null;
        this.parserClass = null;
        this.namespaceMap = new Hashtable();
        this.saxParser = null;
        this.abandonHope = false;
        this.parserClass = parserClass;
    }
    
    public void readCatalog(final Catalog catalog, final String s) throws MalformedURLException, IOException, CatalogException {
        URL url;
        try {
            url = new URL(s);
        }
        catch (MalformedURLException ex) {
            url = new URL("file:///" + s);
        }
        try {
            this.readCatalog(catalog, url.openConnection().getInputStream());
        }
        catch (FileNotFoundException ex2) {
            Debug.message(1, "Failed to load catalog, file not found", url.toString());
        }
    }
    
    public void readCatalog(final Catalog catalog, final InputStream inputStream) throws IOException, CatalogException {
        if (this.parserFactory == null && this.parserClass == null) {
            Debug.message(1, "Cannot read SAX catalog without a parser");
            throw new CatalogException(6);
        }
        this.catalog = catalog;
        try {
            if (this.parserFactory != null) {
                final SAXParser saxParser = this.parserFactory.newSAXParser();
                final SAXParserHandler saxParserHandler = new SAXParserHandler();
                saxParserHandler.setContentHandler((ContentHandler)this);
                saxParser.parse(new InputSource(inputStream), (DefaultHandler)saxParserHandler);
            }
            else {
                final Parser parser = (Parser)Class.forName(this.parserClass).newInstance();
                parser.setDocumentHandler(this);
                parser.parse(new InputSource(inputStream));
            }
        }
        catch (ClassNotFoundException ex4) {
            throw new CatalogException(6);
        }
        catch (IllegalAccessException ex5) {
            throw new CatalogException(6);
        }
        catch (InstantiationException ex6) {
            throw new CatalogException(6);
        }
        catch (ParserConfigurationException ex7) {
            throw new CatalogException(5);
        }
        catch (SAXException ex) {
            final Exception exception = ex.getException();
            final UnknownHostException ex2 = new UnknownHostException();
            final FileNotFoundException ex3 = new FileNotFoundException();
            if (exception != null) {
                if (exception.getClass() == ex2.getClass()) {
                    throw new CatalogException(7, exception.toString());
                }
                if (exception.getClass() == ex3.getClass()) {
                    throw new CatalogException(7, exception.toString());
                }
            }
            throw new CatalogException((Exception)ex);
        }
    }
    
    public void setDocumentLocator(final Locator documentLocator) {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).setDocumentLocator(documentLocator);
        }
    }
    
    public void startDocument() throws SAXException {
        this.saxParser = null;
        this.abandonHope = false;
    }
    
    public void endDocument() throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).endDocument();
        }
    }
    
    public void startElement(final String s, final AttributeList list) throws SAXException {
        if (this.abandonHope) {
            return;
        }
        if (this.saxParser == null) {
            String substring = "";
            if (s.indexOf(58) > 0) {
                substring = s.substring(0, s.indexOf(58));
            }
            String substring2 = s;
            if (substring2.indexOf(58) > 0) {
                substring2 = substring2.substring(substring2.indexOf(58) + 1);
            }
            String s2;
            if (substring.equals("")) {
                s2 = list.getValue("xmlns");
            }
            else {
                s2 = list.getValue("xmlns:" + substring);
            }
            final String catalogParser = this.getCatalogParser(s2, substring2);
            if (catalogParser == null) {
                this.abandonHope = true;
                if (s2 == null) {
                    Debug.message(2, "No Catalog parser for " + s);
                }
                else {
                    Debug.message(2, "No Catalog parser for {" + s2 + "}" + s);
                }
                return;
            }
            try {
                (this.saxParser = (SAXCatalogParser)Class.forName(catalogParser).newInstance()).setCatalog(this.catalog);
                ((ContentHandler)this.saxParser).startDocument();
                ((DocumentHandler)this.saxParser).startElement(s, list);
            }
            catch (ClassNotFoundException ex) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex.toString());
            }
            catch (InstantiationException ex2) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex2.toString());
            }
            catch (IllegalAccessException ex3) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex3.toString());
            }
            catch (ClassCastException ex4) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex4.toString());
            }
        }
        else {
            ((DocumentHandler)this.saxParser).startElement(s, list);
        }
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.abandonHope) {
            return;
        }
        if (this.saxParser == null) {
            final String catalogParser = this.getCatalogParser(s, s2);
            if (catalogParser == null) {
                this.abandonHope = true;
                if (s == null) {
                    Debug.message(2, "No Catalog parser for " + s2);
                }
                else {
                    Debug.message(2, "No Catalog parser for {" + s + "}" + s2);
                }
                return;
            }
            try {
                (this.saxParser = (SAXCatalogParser)Class.forName(catalogParser).newInstance()).setCatalog(this.catalog);
                ((ContentHandler)this.saxParser).startDocument();
                ((ContentHandler)this.saxParser).startElement(s, s2, s3, attributes);
            }
            catch (ClassNotFoundException ex) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex.toString());
            }
            catch (InstantiationException ex2) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex2.toString());
            }
            catch (IllegalAccessException ex3) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex3.toString());
            }
            catch (ClassCastException ex4) {
                this.saxParser = null;
                this.abandonHope = true;
                Debug.message(2, ex4.toString());
            }
        }
        else {
            ((ContentHandler)this.saxParser).startElement(s, s2, s3, attributes);
        }
    }
    
    public void endElement(final String s) throws SAXException {
        if (this.saxParser != null) {
            ((DocumentHandler)this.saxParser).endElement(s);
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).endElement(s, s2, s3);
        }
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).characters(array, n, n2);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).ignorableWhitespace(array, n, n2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).processingInstruction(s, s2);
        }
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).startPrefixMapping(s, s2);
        }
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).endPrefixMapping(s);
        }
    }
    
    public void skippedEntity(final String s) throws SAXException {
        if (this.saxParser != null) {
            ((ContentHandler)this.saxParser).skippedEntity(s);
        }
    }
}

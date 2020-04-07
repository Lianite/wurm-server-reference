// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.apache.xml.resolver.helpers.Debug;
import org.apache.xml.resolver.helpers.Namespaces;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.resolver.CatalogException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import org.apache.xml.resolver.Catalog;
import java.util.Hashtable;

public class DOMCatalogReader implements CatalogReader
{
    protected Hashtable namespaceMap;
    
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
    
    public DOMCatalogReader() {
        this.namespaceMap = new Hashtable();
    }
    
    public void readCatalog(final Catalog catalog, final InputStream inputStream) throws IOException, CatalogException {
        final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
        instance.setNamespaceAware(false);
        instance.setValidating(false);
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = instance.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            throw new CatalogException(6);
        }
        Document parse;
        try {
            parse = documentBuilder.parse(inputStream);
        }
        catch (SAXException ex2) {
            throw new CatalogException(5);
        }
        final Element documentElement = parse.getDocumentElement();
        final String namespaceURI = Namespaces.getNamespaceURI(documentElement);
        final String localName = Namespaces.getLocalName(documentElement);
        final String catalogParser = this.getCatalogParser(namespaceURI, localName);
        if (catalogParser == null) {
            if (namespaceURI == null) {
                Debug.message(1, "No Catalog parser for " + localName);
            }
            else {
                Debug.message(1, "No Catalog parser for {" + namespaceURI + "}" + localName);
            }
            return;
        }
        DOMCatalogParser domCatalogParser;
        try {
            domCatalogParser = (DOMCatalogParser)Class.forName(catalogParser).newInstance();
        }
        catch (ClassNotFoundException ex3) {
            Debug.message(1, "Cannot load XML Catalog Parser class", catalogParser);
            throw new CatalogException(6);
        }
        catch (InstantiationException ex4) {
            Debug.message(1, "Cannot instantiate XML Catalog Parser class", catalogParser);
            throw new CatalogException(6);
        }
        catch (IllegalAccessException ex5) {
            Debug.message(1, "Cannot access XML Catalog Parser class", catalogParser);
            throw new CatalogException(6);
        }
        catch (ClassCastException ex6) {
            Debug.message(1, "Cannot cast XML Catalog Parser class", catalogParser);
            throw new CatalogException(6);
        }
        for (Node node = documentElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            domCatalogParser.parseCatalogEntry(catalog, node);
        }
    }
    
    public void readCatalog(final Catalog catalog, final String s) throws MalformedURLException, IOException, CatalogException {
        this.readCatalog(catalog, new URL(s).openConnection().getInputStream());
    }
}

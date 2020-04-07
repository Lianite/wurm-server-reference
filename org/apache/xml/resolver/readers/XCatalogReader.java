// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.CatalogEntry;
import org.apache.xml.resolver.helpers.PublicId;
import org.apache.xml.resolver.helpers.Debug;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import javax.xml.parsers.SAXParserFactory;
import org.apache.xml.resolver.Catalog;

public class XCatalogReader extends SAXCatalogReader implements SAXCatalogParser
{
    protected Catalog catalog;
    
    public void setCatalog(final Catalog catalog) {
        this.catalog = catalog;
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    public XCatalogReader(final SAXParserFactory saxParserFactory) {
        super(saxParserFactory);
        this.catalog = null;
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        int n = -1;
        final Vector<String> vector = new Vector<String>();
        if (s2.equals("Base")) {
            n = Catalog.BASE;
            vector.add(attributes.getValue("HRef"));
            Debug.message(4, "Base", attributes.getValue("HRef"));
        }
        else if (s2.equals("Delegate")) {
            n = Catalog.DELEGATE_PUBLIC;
            vector.add(attributes.getValue("PublicId"));
            vector.add(attributes.getValue("HRef"));
            Debug.message(4, "Delegate", PublicId.normalize(attributes.getValue("PublicId")), attributes.getValue("HRef"));
        }
        else if (s2.equals("Extend")) {
            n = Catalog.CATALOG;
            vector.add(attributes.getValue("HRef"));
            Debug.message(4, "Extend", attributes.getValue("HRef"));
        }
        else if (s2.equals("Map")) {
            n = Catalog.PUBLIC;
            vector.add(attributes.getValue("PublicId"));
            vector.add(attributes.getValue("HRef"));
            Debug.message(4, "Map", PublicId.normalize(attributes.getValue("PublicId")), attributes.getValue("HRef"));
        }
        else if (s2.equals("Remap")) {
            n = Catalog.SYSTEM;
            vector.add(attributes.getValue("SystemId"));
            vector.add(attributes.getValue("HRef"));
            Debug.message(4, "Remap", attributes.getValue("SystemId"), attributes.getValue("HRef"));
        }
        else if (!s2.equals("XMLCatalog")) {
            Debug.message(1, "Invalid catalog entry type", s2);
        }
        if (n >= 0) {
            try {
                this.catalog.addEntry(new CatalogEntry(n, (Vector)vector));
            }
            catch (CatalogException ex) {
                if (ex.getExceptionType() == 3) {
                    Debug.message(1, "Invalid catalog entry type", s2);
                }
                else if (ex.getExceptionType() == 2) {
                    Debug.message(1, "Invalid catalog entry", s2);
                }
            }
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
    }
}

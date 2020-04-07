// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.xml.sax.SAXException;
import org.apache.xml.resolver.Resolver;
import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.CatalogEntry;
import org.apache.xml.resolver.helpers.Debug;
import org.apache.xml.resolver.Catalog;
import java.util.Vector;
import org.xml.sax.Attributes;

public class ExtendedXMLCatalogReader extends OASISXMLCatalogReader
{
    public static final String extendedNamespaceName = "http://nwalsh.com/xcatalog/1.0";
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        final boolean inExtensionNamespace = this.inExtensionNamespace();
        super.startElement(s, s2, s3, attributes);
        int n = -1;
        Vector<String> vector = new Vector<String>();
        if (s != null && "http://nwalsh.com/xcatalog/1.0".equals(s) && !inExtensionNamespace) {
            if (attributes.getValue("xml:base") != null) {
                final String value = attributes.getValue("xml:base");
                final int base = Catalog.BASE;
                vector.add(value);
                super.baseURIStack.push(value);
                Debug.message(4, "xml:base", value);
                try {
                    super.catalog.addEntry(new CatalogEntry(base, (Vector)vector));
                }
                catch (CatalogException ex) {
                    if (ex.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry (base)", s2);
                    }
                }
                n = -1;
                vector = new Vector<String>();
            }
            else {
                super.baseURIStack.push(super.baseURIStack.peek());
            }
            if (s2.equals("uriSuffix")) {
                if (this.checkAttributes(attributes, "suffix", "uri")) {
                    n = Resolver.URISUFFIX;
                    vector.add(attributes.getValue("suffix"));
                    vector.add(attributes.getValue("uri"));
                    Debug.message(4, "uriSuffix", attributes.getValue("suffix"), attributes.getValue("uri"));
                }
            }
            else if (s2.equals("systemSuffix")) {
                if (this.checkAttributes(attributes, "suffix", "uri")) {
                    n = Resolver.SYSTEMSUFFIX;
                    vector.add(attributes.getValue("suffix"));
                    vector.add(attributes.getValue("uri"));
                    Debug.message(4, "systemSuffix", attributes.getValue("suffix"), attributes.getValue("uri"));
                }
            }
            else {
                Debug.message(1, "Invalid catalog entry type", s2);
            }
            if (n >= 0) {
                try {
                    super.catalog.addEntry(new CatalogEntry(n, (Vector)vector));
                }
                catch (CatalogException ex2) {
                    if (ex2.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex2.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry", s2);
                    }
                }
            }
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        super.endElement(s, s2, s3);
        final boolean inExtensionNamespace = this.inExtensionNamespace();
        final Vector<String> vector = new Vector<String>();
        if (s != null && "http://nwalsh.com/xcatalog/1.0".equals(s) && !inExtensionNamespace) {
            final String s4 = super.baseURIStack.pop();
            final String s5 = super.baseURIStack.peek();
            if (!s5.equals(s4)) {
                final int base = Catalog.BASE;
                vector.add(s5);
                Debug.message(4, "(reset) xml:base", s5);
                try {
                    super.catalog.addEntry(new CatalogEntry(base, (Vector)vector));
                }
                catch (CatalogException ex) {
                    if (ex.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry (rbase)", s2);
                    }
                }
            }
        }
    }
}

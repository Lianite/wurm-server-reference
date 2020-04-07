// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.apache.xml.resolver.helpers.PublicId;
import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.CatalogEntry;
import org.apache.xml.resolver.helpers.Debug;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import java.util.Enumeration;
import java.util.Stack;
import org.apache.xml.resolver.Catalog;

public class OASISXMLCatalogReader extends SAXCatalogReader implements SAXCatalogParser
{
    protected Catalog catalog;
    public static final String namespaceName = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
    public static final String tr9401NamespaceName = "urn:oasis:names:tc:entity:xmlns:tr9401:catalog";
    protected Stack baseURIStack;
    protected Stack overrideStack;
    protected Stack namespaceStack;
    
    public OASISXMLCatalogReader() {
        this.catalog = null;
        this.baseURIStack = new Stack();
        this.overrideStack = new Stack();
        this.namespaceStack = new Stack();
    }
    
    public void setCatalog(final Catalog catalog) {
        this.catalog = catalog;
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    protected boolean inExtensionNamespace() {
        boolean b = false;
        String s;
        for (Enumeration elements = this.namespaceStack.elements(); !b && elements.hasMoreElements(); b = (s == null || (!s.equals("urn:oasis:names:tc:entity:xmlns:tr9401:catalog") && !s.equals("urn:oasis:names:tc:entity:xmlns:xml:catalog")))) {
            s = elements.nextElement();
        }
        return b;
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void startDocument() throws SAXException {
        this.baseURIStack.push(this.catalog.getCurrentBase());
        this.overrideStack.push(this.catalog.getDefaultOverride());
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        int n = -1;
        Vector<String> vector = new Vector<String>();
        this.namespaceStack.push(s);
        final boolean inExtensionNamespace = this.inExtensionNamespace();
        if (s != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(s) && !inExtensionNamespace) {
            if (attributes.getValue("xml:base") != null) {
                final String value = attributes.getValue("xml:base");
                final int base = Catalog.BASE;
                vector.add(value);
                this.baseURIStack.push(value);
                Debug.message(4, "xml:base", value);
                try {
                    this.catalog.addEntry(new CatalogEntry(base, (Vector)vector));
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
                this.baseURIStack.push(this.baseURIStack.peek());
            }
            if ((s2.equals("catalog") || s2.equals("group")) && attributes.getValue("prefer") != null) {
                final String value2 = attributes.getValue("prefer");
                String defaultOverride;
                if (value2.equals("public")) {
                    defaultOverride = "yes";
                }
                else if (value2.equals("system")) {
                    defaultOverride = "no";
                }
                else {
                    Debug.message(1, "Invalid prefer: must be 'system' or 'public'", s2);
                    defaultOverride = this.catalog.getDefaultOverride();
                }
                final int override = Catalog.OVERRIDE;
                vector.add(defaultOverride);
                this.overrideStack.push(defaultOverride);
                Debug.message(4, "override", defaultOverride);
                try {
                    this.catalog.addEntry(new CatalogEntry(override, (Vector)vector));
                }
                catch (CatalogException ex2) {
                    if (ex2.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex2.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry (override)", s2);
                    }
                }
                n = -1;
                vector = new Vector<String>();
            }
            else {
                this.overrideStack.push(this.overrideStack.peek());
            }
            if (s2.equals("delegatePublic")) {
                if (this.checkAttributes(attributes, "publicIdStartString", "catalog")) {
                    n = Catalog.DELEGATE_PUBLIC;
                    vector.add(attributes.getValue("publicIdStartString"));
                    vector.add(attributes.getValue("catalog"));
                    Debug.message(4, "delegatePublic", PublicId.normalize(attributes.getValue("publicIdStartString")), attributes.getValue("catalog"));
                }
            }
            else if (s2.equals("delegateSystem")) {
                if (this.checkAttributes(attributes, "systemIdStartString", "catalog")) {
                    n = Catalog.DELEGATE_SYSTEM;
                    vector.add(attributes.getValue("systemIdStartString"));
                    vector.add(attributes.getValue("catalog"));
                    Debug.message(4, "delegateSystem", attributes.getValue("systemIdStartString"), attributes.getValue("catalog"));
                }
            }
            else if (s2.equals("delegateURI")) {
                if (this.checkAttributes(attributes, "uriStartString", "catalog")) {
                    n = Catalog.DELEGATE_URI;
                    vector.add(attributes.getValue("uriStartString"));
                    vector.add(attributes.getValue("catalog"));
                    Debug.message(4, "delegateURI", attributes.getValue("uriStartString"), attributes.getValue("catalog"));
                }
            }
            else if (s2.equals("rewriteSystem")) {
                if (this.checkAttributes(attributes, "systemIdStartString", "rewritePrefix")) {
                    n = Catalog.REWRITE_SYSTEM;
                    vector.add(attributes.getValue("systemIdStartString"));
                    vector.add(attributes.getValue("rewritePrefix"));
                    Debug.message(4, "rewriteSystem", attributes.getValue("systemIdStartString"), attributes.getValue("rewritePrefix"));
                }
            }
            else if (s2.equals("rewriteURI")) {
                if (this.checkAttributes(attributes, "uriStartString", "rewritePrefix")) {
                    n = Catalog.REWRITE_URI;
                    vector.add(attributes.getValue("uriStartString"));
                    vector.add(attributes.getValue("rewritePrefix"));
                    Debug.message(4, "rewriteURI", attributes.getValue("uriStartString"), attributes.getValue("rewritePrefix"));
                }
            }
            else if (s2.equals("nextCatalog")) {
                if (this.checkAttributes(attributes, "catalog")) {
                    n = Catalog.CATALOG;
                    vector.add(attributes.getValue("catalog"));
                    Debug.message(4, "nextCatalog", attributes.getValue("catalog"));
                }
            }
            else if (s2.equals("public")) {
                if (this.checkAttributes(attributes, "publicId", "uri")) {
                    n = Catalog.PUBLIC;
                    vector.add(attributes.getValue("publicId"));
                    vector.add(attributes.getValue("uri"));
                    Debug.message(4, "public", PublicId.normalize(attributes.getValue("publicId")), attributes.getValue("uri"));
                }
            }
            else if (s2.equals("system")) {
                if (this.checkAttributes(attributes, "systemId", "uri")) {
                    n = Catalog.SYSTEM;
                    vector.add(attributes.getValue("systemId"));
                    vector.add(attributes.getValue("uri"));
                    Debug.message(4, "system", attributes.getValue("systemId"), attributes.getValue("uri"));
                }
            }
            else if (s2.equals("uri")) {
                if (this.checkAttributes(attributes, "name", "uri")) {
                    n = Catalog.URI;
                    vector.add(attributes.getValue("name"));
                    vector.add(attributes.getValue("uri"));
                    Debug.message(4, "uri", attributes.getValue("name"), attributes.getValue("uri"));
                }
            }
            else if (!s2.equals("catalog")) {
                if (!s2.equals("group")) {
                    Debug.message(1, "Invalid catalog entry type", s2);
                }
            }
            if (n >= 0) {
                try {
                    this.catalog.addEntry(new CatalogEntry(n, (Vector)vector));
                }
                catch (CatalogException ex3) {
                    if (ex3.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex3.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry", s2);
                    }
                }
            }
        }
        if (s != null && "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(s) && !inExtensionNamespace) {
            if (attributes.getValue("xml:base") != null) {
                final String value3 = attributes.getValue("xml:base");
                final int base2 = Catalog.BASE;
                vector.add(value3);
                this.baseURIStack.push(value3);
                Debug.message(4, "xml:base", value3);
                try {
                    this.catalog.addEntry(new CatalogEntry(base2, (Vector)vector));
                }
                catch (CatalogException ex4) {
                    if (ex4.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex4.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry (base)", s2);
                    }
                }
                n = -1;
                vector = new Vector<String>();
            }
            else {
                this.baseURIStack.push(this.baseURIStack.peek());
            }
            if (s2.equals("doctype")) {
                n = Catalog.DOCTYPE;
                vector.add(attributes.getValue("name"));
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("document")) {
                n = Catalog.DOCUMENT;
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("dtddecl")) {
                n = Catalog.DTDDECL;
                vector.add(attributes.getValue("publicId"));
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("entity")) {
                n = Catalog.ENTITY;
                vector.add(attributes.getValue("name"));
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("linktype")) {
                n = Catalog.LINKTYPE;
                vector.add(attributes.getValue("name"));
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("notation")) {
                n = Catalog.NOTATION;
                vector.add(attributes.getValue("name"));
                vector.add(attributes.getValue("uri"));
            }
            else if (s2.equals("sgmldecl")) {
                n = Catalog.SGMLDECL;
                vector.add(attributes.getValue("uri"));
            }
            else {
                Debug.message(1, "Invalid catalog entry type", s2);
            }
            if (n >= 0) {
                try {
                    this.catalog.addEntry(new CatalogEntry(n, (Vector)vector));
                }
                catch (CatalogException ex5) {
                    if (ex5.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex5.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry", s2);
                    }
                }
            }
        }
    }
    
    public boolean checkAttributes(final Attributes attributes, final String s) {
        if (attributes.getValue(s) == null) {
            Debug.message(1, "Error: required attribute " + s + " missing.");
            return false;
        }
        return true;
    }
    
    public boolean checkAttributes(final Attributes attributes, final String s, final String s2) {
        return this.checkAttributes(attributes, s) && this.checkAttributes(attributes, s2);
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        final Vector<String> vector = new Vector<String>();
        final boolean inExtensionNamespace = this.inExtensionNamespace();
        if (s != null && !inExtensionNamespace && ("urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(s) || "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(s))) {
            final String s4 = this.baseURIStack.pop();
            final String s5 = this.baseURIStack.peek();
            if (!s5.equals(s4)) {
                final int base = Catalog.BASE;
                vector.add(s5);
                Debug.message(4, "(reset) xml:base", s5);
                try {
                    this.catalog.addEntry(new CatalogEntry(base, (Vector)vector));
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
        if (s != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(s) && !inExtensionNamespace && (s2.equals("catalog") || s2.equals("group"))) {
            final String s6 = this.overrideStack.pop();
            final String s7 = this.overrideStack.peek();
            if (!s7.equals(s6)) {
                final int override = Catalog.OVERRIDE;
                vector.add(s7);
                this.overrideStack.push(s7);
                Debug.message(4, "(reset) override", s7);
                try {
                    this.catalog.addEntry(new CatalogEntry(override, (Vector)vector));
                }
                catch (CatalogException ex2) {
                    if (ex2.getExceptionType() == 3) {
                        Debug.message(1, "Invalid catalog entry type", s2);
                    }
                    else if (ex2.getExceptionType() == 2) {
                        Debug.message(1, "Invalid catalog entry (roverride)", s2);
                    }
                }
            }
        }
        this.namespaceStack.pop();
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
    }
    
    public void skippedEntity(final String s) throws SAXException {
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
    }
}

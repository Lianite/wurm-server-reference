// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

import java.io.UnsupportedEncodingException;
import org.apache.xml.resolver.helpers.PublicId;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xml.resolver.helpers.Debug;
import java.util.Enumeration;
import org.apache.xml.resolver.readers.TR9401CatalogReader;
import org.apache.xml.resolver.readers.CatalogReader;
import org.apache.xml.resolver.readers.SAXCatalogReader;
import javax.xml.parsers.SAXParserFactory;
import java.util.Hashtable;
import java.util.Vector;
import java.net.URL;

public class Catalog
{
    public static final int BASE;
    public static final int CATALOG;
    public static final int DOCUMENT;
    public static final int OVERRIDE;
    public static final int SGMLDECL;
    public static final int DELEGATE_PUBLIC;
    public static final int DELEGATE_SYSTEM;
    public static final int DELEGATE_URI;
    public static final int DOCTYPE;
    public static final int DTDDECL;
    public static final int ENTITY;
    public static final int LINKTYPE;
    public static final int NOTATION;
    public static final int PUBLIC;
    public static final int SYSTEM;
    public static final int URI;
    public static final int REWRITE_SYSTEM;
    public static final int REWRITE_URI;
    protected URL base;
    protected URL catalogCwd;
    protected Vector catalogEntries;
    protected boolean default_override;
    protected Vector catalogFiles;
    protected Vector localCatalogFiles;
    protected Vector catalogs;
    protected Vector localDelegate;
    protected Hashtable readerMap;
    protected Vector readerArr;
    
    public void setupReaders() {
        final SAXParserFactory instance = SAXParserFactory.newInstance();
        instance.setNamespaceAware(true);
        instance.setValidating(false);
        final SAXCatalogReader saxCatalogReader = new SAXCatalogReader(instance);
        saxCatalogReader.setCatalogParser((String)null, "XMLCatalog", "org.apache.xml.resolver.readers.XCatalogReader");
        saxCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "org.apache.xml.resolver.readers.OASISXMLCatalogReader");
        this.addReader("application/xml", (CatalogReader)saxCatalogReader);
        this.addReader("text/plain", (CatalogReader)new TR9401CatalogReader());
    }
    
    public void addReader(final String s, final CatalogReader catalogReader) {
        if (this.readerMap.contains(s)) {
            this.readerArr.set(this.readerMap.get(s), catalogReader);
        }
        else {
            this.readerArr.add(catalogReader);
            this.readerMap.put(s, new Integer(this.readerArr.size() - 1));
        }
    }
    
    protected void copyReaders(final Catalog catalog) {
        final Vector vector = new Vector<String>(this.readerMap.size());
        for (int i = 0; i < this.readerMap.size(); ++i) {
            vector.add(null);
        }
        final Enumeration<String> keys = (Enumeration<String>)this.readerMap.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            vector.set((int)this.readerMap.get(s), s);
        }
        for (int j = 0; j < vector.size(); ++j) {
            final String s2 = vector.get(j);
            catalog.addReader(s2, (CatalogReader)this.readerArr.get((int)this.readerMap.get(s2)));
        }
    }
    
    protected Catalog newCatalog() {
        final String name = this.getClass().getName();
        try {
            final Catalog catalog = (Catalog)Class.forName(name).newInstance();
            this.copyReaders(catalog);
            return catalog;
        }
        catch (ClassNotFoundException ex) {
            Debug.message(1, "Class Not Found Exception: " + name);
        }
        catch (IllegalAccessException ex2) {
            Debug.message(1, "Illegal Access Exception: " + name);
        }
        catch (InstantiationException ex3) {
            Debug.message(1, "Instantiation Exception: " + name);
        }
        catch (ClassCastException ex4) {
            Debug.message(1, "Class Cast Exception: " + name);
        }
        catch (Exception ex5) {
            Debug.message(1, "Other Exception: " + name);
        }
        final Catalog catalog2 = new Catalog();
        this.copyReaders(catalog2);
        return catalog2;
    }
    
    public Catalog() {
        this.catalogEntries = new Vector();
        this.default_override = true;
        this.catalogFiles = new Vector();
        this.localCatalogFiles = new Vector();
        this.catalogs = new Vector();
        this.localDelegate = new Vector();
        this.readerMap = new Hashtable();
        this.readerArr = new Vector();
        this.default_override = CatalogManager.preferPublic();
    }
    
    public String getCurrentBase() {
        return this.base.toString();
    }
    
    public String getDefaultOverride() {
        if (this.default_override) {
            return "yes";
        }
        return "no";
    }
    
    public void loadSystemCatalogs() throws MalformedURLException, IOException {
        final Vector catalogFiles = CatalogManager.catalogFiles();
        if (catalogFiles != null) {
            for (int i = 0; i < catalogFiles.size(); ++i) {
                this.catalogFiles.addElement(catalogFiles.elementAt(i));
            }
        }
        if (this.catalogFiles.size() > 0) {
            final String s = this.catalogFiles.lastElement();
            this.catalogFiles.removeElement(s);
            this.parseCatalog(s);
        }
    }
    
    public synchronized void parseCatalog(final String s) throws MalformedURLException, IOException {
        Debug.message(4, "Parse catalog: " + s);
        this.catalogFiles.addElement(s);
        this.parsePendingCatalogs();
    }
    
    public synchronized void parseCatalog(final String s, final InputStream inputStream) throws IOException, CatalogException {
        Debug.message(4, "Parse " + s + " catalog on input stream");
        CatalogReader catalogReader = null;
        if (this.readerMap.containsKey(s)) {
            catalogReader = this.readerArr.get(this.readerMap.get(s));
        }
        if (catalogReader == null) {
            final String string = "No CatalogReader for MIME type: " + s;
            Debug.message(2, string);
            throw new CatalogException(6, string);
        }
        catalogReader.readCatalog(this, inputStream);
        this.parsePendingCatalogs();
    }
    
    protected synchronized void parsePendingCatalogs() throws MalformedURLException, IOException {
        if (!this.localCatalogFiles.isEmpty()) {
            final Vector<String> catalogFiles = new Vector<String>();
            final Enumeration<String> elements = this.localCatalogFiles.elements();
            while (elements.hasMoreElements()) {
                catalogFiles.addElement(elements.nextElement());
            }
            for (int i = 0; i < this.catalogFiles.size(); ++i) {
                catalogFiles.addElement((String)this.catalogFiles.elementAt(i));
            }
            this.catalogFiles = catalogFiles;
            this.localCatalogFiles.clear();
        }
        if (this.catalogFiles.isEmpty() && !this.localDelegate.isEmpty()) {
            final Enumeration<Object> elements2 = this.localDelegate.elements();
            while (elements2.hasMoreElements()) {
                this.catalogEntries.addElement(elements2.nextElement());
            }
            this.localDelegate.clear();
        }
        while (!this.catalogFiles.isEmpty()) {
            final String s = this.catalogFiles.elementAt(0);
            try {
                this.catalogFiles.remove(0);
            }
            catch (ArrayIndexOutOfBoundsException ex2) {}
            if (this.catalogEntries.size() == 0 && this.catalogs.size() == 0) {
                try {
                    this.parseCatalogFile(s);
                }
                catch (CatalogException ex) {
                    System.out.println("FIXME: " + ex.toString());
                }
            }
            else {
                this.catalogs.addElement(s);
            }
            if (!this.localCatalogFiles.isEmpty()) {
                final Vector<String> catalogFiles2 = new Vector<String>();
                final Enumeration<String> elements3 = this.localCatalogFiles.elements();
                while (elements3.hasMoreElements()) {
                    catalogFiles2.addElement(elements3.nextElement());
                }
                for (int j = 0; j < this.catalogFiles.size(); ++j) {
                    catalogFiles2.addElement((String)this.catalogFiles.elementAt(j));
                }
                this.catalogFiles = catalogFiles2;
                this.localCatalogFiles.clear();
            }
            if (!this.localDelegate.isEmpty()) {
                final Enumeration<Object> elements4 = this.localDelegate.elements();
                while (elements4.hasMoreElements()) {
                    this.catalogEntries.addElement(elements4.nextElement());
                }
                this.localDelegate.clear();
            }
        }
        this.catalogFiles.clear();
    }
    
    protected synchronized void parseCatalogFile(String string) throws MalformedURLException, IOException, CatalogException {
        try {
            this.catalogCwd = new URL("file:" + this.fixSlashes(System.getProperty("user.dir")) + "/basename");
        }
        catch (MalformedURLException ex2) {
            Debug.message(1, "Malformed URL on cwd", this.fixSlashes(System.getProperty("user.dir")));
            this.catalogCwd = null;
        }
        try {
            this.base = new URL(this.catalogCwd, this.fixSlashes(string));
        }
        catch (MalformedURLException ex3) {
            try {
                this.base = new URL("file:" + this.fixSlashes(string));
            }
            catch (MalformedURLException ex4) {
                Debug.message(1, "Malformed URL on catalog filename", this.fixSlashes(string));
                this.base = null;
            }
        }
        Debug.message(2, "Loading catalog", string);
        Debug.message(4, "Default BASE", this.base.toString());
        string = this.base.toString();
        boolean b = false;
        boolean b2 = false;
        for (int n = 0; !b && n < this.readerArr.size(); ++n) {
            final CatalogReader catalogReader = this.readerArr.get(n);
            DataInputStream dataInputStream;
            try {
                b2 = false;
                dataInputStream = new DataInputStream(this.base.openStream());
            }
            catch (FileNotFoundException ex5) {
                b2 = true;
                break;
            }
            try {
                catalogReader.readCatalog(this, (InputStream)dataInputStream);
                b = true;
            }
            catch (CatalogException ex) {
                if (ex.getExceptionType() == 7) {
                    break;
                }
            }
            try {
                dataInputStream.close();
            }
            catch (IOException ex6) {}
        }
        if (!b) {
            if (b2) {
                Debug.message(3, "Catalog does not exist", string);
            }
            else {
                Debug.message(1, "Failed to parse catalog", string);
            }
        }
    }
    
    public void addEntry(final CatalogEntry catalogEntry) {
        final int entryType = catalogEntry.getEntryType();
        if (entryType == Catalog.BASE) {
            String s = catalogEntry.getEntryArg(0);
            Debug.message(5, "BASE CUR", this.base.toString());
            Debug.message(4, "BASE STR", s);
            URL base;
            try {
                s = this.fixSlashes(s);
                base = new URL(this.base, s);
            }
            catch (MalformedURLException ex) {
                try {
                    base = new URL("file:" + s);
                }
                catch (MalformedURLException ex2) {
                    Debug.message(1, "Malformed URL on base", s);
                    base = null;
                }
            }
            if (base != null) {
                this.base = base;
            }
            Debug.message(5, "BASE NEW", this.base.toString());
        }
        else if (entryType == Catalog.CATALOG) {
            final String absolute = this.makeAbsolute(catalogEntry.getEntryArg(0));
            Debug.message(4, "CATALOG", absolute);
            this.localCatalogFiles.addElement(absolute);
        }
        else if (entryType == Catalog.PUBLIC) {
            final String normalize = PublicId.normalize(catalogEntry.getEntryArg(0));
            final String absolute2 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalize);
            catalogEntry.setEntryArg(1, absolute2);
            Debug.message(4, "PUBLIC", normalize, absolute2);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.SYSTEM) {
            final String normalizeURI = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute3 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute3);
            Debug.message(4, "SYSTEM", normalizeURI, absolute3);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.URI) {
            final String normalizeURI2 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute4 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute4);
            Debug.message(4, "URI", normalizeURI2, absolute4);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.DOCUMENT) {
            final String absolute5 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(0)));
            catalogEntry.setEntryArg(0, absolute5);
            Debug.message(4, "DOCUMENT", absolute5);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.OVERRIDE) {
            Debug.message(4, "OVERRIDE", catalogEntry.getEntryArg(0));
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.SGMLDECL) {
            final String absolute6 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(0)));
            catalogEntry.setEntryArg(0, absolute6);
            Debug.message(4, "SGMLDECL", absolute6);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.DELEGATE_PUBLIC) {
            final String normalize2 = PublicId.normalize(catalogEntry.getEntryArg(0));
            final String absolute7 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalize2);
            catalogEntry.setEntryArg(1, absolute7);
            Debug.message(4, "DELEGATE_PUBLIC", normalize2, absolute7);
            this.addDelegate(catalogEntry);
        }
        else if (entryType == Catalog.DELEGATE_SYSTEM) {
            final String normalizeURI3 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute8 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalizeURI3);
            catalogEntry.setEntryArg(1, absolute8);
            Debug.message(4, "DELEGATE_SYSTEM", normalizeURI3, absolute8);
            this.addDelegate(catalogEntry);
        }
        else if (entryType == Catalog.DELEGATE_URI) {
            final String normalizeURI4 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute9 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalizeURI4);
            catalogEntry.setEntryArg(1, absolute9);
            Debug.message(4, "DELEGATE_URI", normalizeURI4, absolute9);
            this.addDelegate(catalogEntry);
        }
        else if (entryType == Catalog.REWRITE_SYSTEM) {
            final String normalizeURI5 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute10 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalizeURI5);
            catalogEntry.setEntryArg(1, absolute10);
            Debug.message(4, "REWRITE_SYSTEM", normalizeURI5, absolute10);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.REWRITE_URI) {
            final String normalizeURI6 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute11 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(0, normalizeURI6);
            catalogEntry.setEntryArg(1, absolute11);
            Debug.message(4, "REWRITE_URI", normalizeURI6, absolute11);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.DOCTYPE) {
            final String absolute12 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute12);
            Debug.message(4, "DOCTYPE", catalogEntry.getEntryArg(0), absolute12);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.DTDDECL) {
            final String normalize3 = PublicId.normalize(catalogEntry.getEntryArg(0));
            catalogEntry.setEntryArg(0, normalize3);
            final String absolute13 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute13);
            Debug.message(4, "DTDDECL", normalize3, absolute13);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.ENTITY) {
            final String absolute14 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute14);
            Debug.message(4, "ENTITY", catalogEntry.getEntryArg(0), absolute14);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.LINKTYPE) {
            final String absolute15 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute15);
            Debug.message(4, "LINKTYPE", catalogEntry.getEntryArg(0), absolute15);
            this.catalogEntries.addElement(catalogEntry);
        }
        else if (entryType == Catalog.NOTATION) {
            final String absolute16 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute16);
            Debug.message(4, "NOTATION", catalogEntry.getEntryArg(0), absolute16);
            this.catalogEntries.addElement(catalogEntry);
        }
        else {
            this.catalogEntries.addElement(catalogEntry);
        }
    }
    
    public void unknownEntry(final Vector vector) {
        if (vector != null && vector.size() > 0) {
            Debug.message(2, "Unrecognized token parsing catalog", (String)vector.elementAt(0));
        }
    }
    
    public void parseAllCatalogs() throws MalformedURLException, IOException {
        for (int i = 0; i < this.catalogs.size(); ++i) {
            try {
                final Catalog catalog = this.catalogs.elementAt(i);
            }
            catch (ClassCastException ex) {
                final String s = this.catalogs.elementAt(i);
                final Catalog catalog2 = this.newCatalog();
                catalog2.parseCatalog(s);
                this.catalogs.setElementAt(catalog2, i);
                catalog2.parseAllCatalogs();
            }
        }
        final Enumeration<CatalogEntry> elements = (Enumeration<CatalogEntry>)this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.DELEGATE_PUBLIC || catalogEntry.getEntryType() == Catalog.DELEGATE_SYSTEM || catalogEntry.getEntryType() == Catalog.DELEGATE_URI) {
                this.newCatalog().parseCatalog(catalogEntry.getEntryArg(1));
            }
        }
    }
    
    public String resolveDoctype(final String s, String decodeURN, String s2) throws MalformedURLException, IOException {
        Debug.message(3, "resolveDoctype(" + s + "," + decodeURN + "," + s2 + ")");
        s2 = this.normalizeURI(s2);
        if (decodeURN != null && decodeURN.startsWith("urn:publicid:")) {
            decodeURN = PublicId.decodeURN(decodeURN);
        }
        if (s2 != null && s2.startsWith("urn:publicid:")) {
            s2 = PublicId.decodeURN(s2);
            if (decodeURN != null && !decodeURN.equals(s2)) {
                Debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                s2 = null;
            }
            else {
                decodeURN = s2;
                s2 = null;
            }
        }
        if (s2 != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s2);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        if (decodeURN != null) {
            final String resolveLocalPublic = this.resolveLocalPublic(Catalog.DOCTYPE, s, decodeURN, s2);
            if (resolveLocalPublic != null) {
                return resolveLocalPublic;
            }
        }
        boolean b = this.default_override;
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.OVERRIDE) {
                b = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (catalogEntry.getEntryType() == Catalog.DOCTYPE && catalogEntry.getEntryArg(0).equals(s) && (b || s2 == null)) {
                    return catalogEntry.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.DOCTYPE, s, decodeURN, s2);
    }
    
    public String resolveDocument() throws MalformedURLException, IOException {
        Debug.message(3, "resolveDocument");
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.DOCUMENT) {
                return catalogEntry.getEntryArg(1);
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.DOCUMENT, null, null, null);
    }
    
    public String resolveEntity(final String s, String decodeURN, String s2) throws MalformedURLException, IOException {
        Debug.message(3, "resolveEntity(" + s + "," + decodeURN + "," + s2 + ")");
        s2 = this.normalizeURI(s2);
        if (decodeURN != null && decodeURN.startsWith("urn:publicid:")) {
            decodeURN = PublicId.decodeURN(decodeURN);
        }
        if (s2 != null && s2.startsWith("urn:publicid:")) {
            s2 = PublicId.decodeURN(s2);
            if (decodeURN != null && !decodeURN.equals(s2)) {
                Debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                s2 = null;
            }
            else {
                decodeURN = s2;
                s2 = null;
            }
        }
        if (s2 != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s2);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        if (decodeURN != null) {
            final String resolveLocalPublic = this.resolveLocalPublic(Catalog.ENTITY, s, decodeURN, s2);
            if (resolveLocalPublic != null) {
                return resolveLocalPublic;
            }
        }
        boolean b = this.default_override;
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.OVERRIDE) {
                b = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (catalogEntry.getEntryType() == Catalog.ENTITY && catalogEntry.getEntryArg(0).equals(s) && (b || s2 == null)) {
                    return catalogEntry.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.ENTITY, s, decodeURN, s2);
    }
    
    public String resolveNotation(final String s, String decodeURN, String s2) throws MalformedURLException, IOException {
        Debug.message(3, "resolveNotation(" + s + "," + decodeURN + "," + s2 + ")");
        s2 = this.normalizeURI(s2);
        if (decodeURN != null && decodeURN.startsWith("urn:publicid:")) {
            decodeURN = PublicId.decodeURN(decodeURN);
        }
        if (s2 != null && s2.startsWith("urn:publicid:")) {
            s2 = PublicId.decodeURN(s2);
            if (decodeURN != null && !decodeURN.equals(s2)) {
                Debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                s2 = null;
            }
            else {
                decodeURN = s2;
                s2 = null;
            }
        }
        if (s2 != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s2);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        if (decodeURN != null) {
            final String resolveLocalPublic = this.resolveLocalPublic(Catalog.NOTATION, s, decodeURN, s2);
            if (resolveLocalPublic != null) {
                return resolveLocalPublic;
            }
        }
        boolean b = this.default_override;
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.OVERRIDE) {
                b = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (catalogEntry.getEntryType() == Catalog.NOTATION && catalogEntry.getEntryArg(0).equals(s) && (b || s2 == null)) {
                    return catalogEntry.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.NOTATION, s, decodeURN, s2);
    }
    
    public String resolvePublic(String decodeURN, String s) throws MalformedURLException, IOException {
        Debug.message(3, "resolvePublic(" + decodeURN + "," + s + ")");
        s = this.normalizeURI(s);
        if (decodeURN != null && decodeURN.startsWith("urn:publicid:")) {
            decodeURN = PublicId.decodeURN(decodeURN);
        }
        if (s != null && s.startsWith("urn:publicid:")) {
            s = PublicId.decodeURN(s);
            if (decodeURN != null && !decodeURN.equals(s)) {
                Debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                s = null;
            }
            else {
                decodeURN = s;
                s = null;
            }
        }
        if (s != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        final String resolveLocalPublic = this.resolveLocalPublic(Catalog.PUBLIC, null, decodeURN, s);
        if (resolveLocalPublic != null) {
            return resolveLocalPublic;
        }
        return this.resolveSubordinateCatalogs(Catalog.PUBLIC, null, decodeURN, s);
    }
    
    protected synchronized String resolveLocalPublic(final int n, final String s, String normalize, final String s2) throws MalformedURLException, IOException {
        normalize = PublicId.normalize(normalize);
        if (s2 != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s2);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        boolean b = this.default_override;
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.OVERRIDE) {
                b = catalogEntry.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (catalogEntry.getEntryType() == Catalog.PUBLIC && catalogEntry.getEntryArg(0).equals(normalize) && (b || s2 == null)) {
                    return catalogEntry.getEntryArg(1);
                }
                continue;
            }
        }
        boolean b2 = this.default_override;
        final Enumeration<CatalogEntry> elements2 = this.catalogEntries.elements();
        final Vector<String> vector = new Vector<String>();
        while (elements2.hasMoreElements()) {
            final CatalogEntry catalogEntry2 = elements2.nextElement();
            if (catalogEntry2.getEntryType() == Catalog.OVERRIDE) {
                b2 = catalogEntry2.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (catalogEntry2.getEntryType() != Catalog.DELEGATE_PUBLIC || (!b2 && s2 != null)) {
                    continue;
                }
                final String entryArg = catalogEntry2.getEntryArg(0);
                if (entryArg.length() > normalize.length() || !entryArg.equals(normalize.substring(0, entryArg.length()))) {
                    continue;
                }
                vector.addElement(catalogEntry2.getEntryArg(1));
            }
        }
        if (vector.size() > 0) {
            final Enumeration<String> elements3 = vector.elements();
            if (Debug.getDebug() > 1) {
                Debug.message(2, "Switching to delegated catalog(s):");
                while (elements3.hasMoreElements()) {
                    Debug.message(2, "\t" + elements3.nextElement());
                }
            }
            final Catalog catalog = this.newCatalog();
            final Enumeration<String> elements4 = vector.elements();
            while (elements4.hasMoreElements()) {
                catalog.parseCatalog(elements4.nextElement());
            }
            return catalog.resolvePublic(normalize, null);
        }
        return null;
    }
    
    public String resolveSystem(String s) throws MalformedURLException, IOException {
        Debug.message(3, "resolveSystem(" + s + ")");
        s = this.normalizeURI(s);
        if (s != null && s.startsWith("urn:publicid:")) {
            s = PublicId.decodeURN(s);
            return this.resolvePublic(s, null);
        }
        if (s != null) {
            final String resolveLocalSystem = this.resolveLocalSystem(s);
            if (resolveLocalSystem != null) {
                return resolveLocalSystem;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, s);
    }
    
    protected String resolveLocalSystem(final String s) throws MalformedURLException, IOException {
        final boolean b = System.getProperty("os.name").indexOf("Windows") >= 0;
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.SYSTEM && (catalogEntry.getEntryArg(0).equals(s) || (b && catalogEntry.getEntryArg(0).equalsIgnoreCase(s)))) {
                return catalogEntry.getEntryArg(1);
            }
        }
        final Enumeration<CatalogEntry> elements2 = this.catalogEntries.elements();
        String s2 = null;
        String entryArg = null;
        while (elements2.hasMoreElements()) {
            final CatalogEntry catalogEntry2 = elements2.nextElement();
            if (catalogEntry2.getEntryType() == Catalog.REWRITE_SYSTEM) {
                final String entryArg2 = catalogEntry2.getEntryArg(0);
                if (entryArg2.length() <= s.length() && entryArg2.equals(s.substring(0, entryArg2.length())) && (s2 == null || entryArg2.length() > s2.length())) {
                    s2 = entryArg2;
                    entryArg = catalogEntry2.getEntryArg(1);
                }
            }
            if (entryArg != null) {
                return entryArg + s.substring(s2.length());
            }
        }
        final Enumeration<CatalogEntry> elements3 = this.catalogEntries.elements();
        final Vector<String> vector = new Vector<String>();
        while (elements3.hasMoreElements()) {
            final CatalogEntry catalogEntry3 = elements3.nextElement();
            if (catalogEntry3.getEntryType() == Catalog.DELEGATE_SYSTEM) {
                final String entryArg3 = catalogEntry3.getEntryArg(0);
                if (entryArg3.length() > s.length() || !entryArg3.equals(s.substring(0, entryArg3.length()))) {
                    continue;
                }
                vector.addElement(catalogEntry3.getEntryArg(1));
            }
        }
        if (vector.size() > 0) {
            final Enumeration<String> elements4 = vector.elements();
            if (Debug.getDebug() > 1) {
                Debug.message(2, "Switching to delegated catalog(s):");
                while (elements4.hasMoreElements()) {
                    Debug.message(2, "\t" + elements4.nextElement());
                }
            }
            final Catalog catalog = this.newCatalog();
            final Enumeration<String> elements5 = vector.elements();
            while (elements5.hasMoreElements()) {
                catalog.parseCatalog(elements5.nextElement());
            }
            return catalog.resolveSystem(s);
        }
        return null;
    }
    
    public String resolveURI(String s) throws MalformedURLException, IOException {
        Debug.message(3, "resolveURI(" + s + ")");
        s = this.normalizeURI(s);
        if (s != null && s.startsWith("urn:publicid:")) {
            s = PublicId.decodeURN(s);
            return this.resolvePublic(s, null);
        }
        if (s != null) {
            final String resolveLocalURI = this.resolveLocalURI(s);
            if (resolveLocalURI != null) {
                return resolveLocalURI;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.URI, null, null, s);
    }
    
    protected String resolveLocalURI(final String s) throws MalformedURLException, IOException {
        final Enumeration<CatalogEntry> elements = this.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.URI && catalogEntry.getEntryArg(0).equals(s)) {
                return catalogEntry.getEntryArg(1);
            }
        }
        final Enumeration<CatalogEntry> elements2 = this.catalogEntries.elements();
        String s2 = null;
        String entryArg = null;
        while (elements2.hasMoreElements()) {
            final CatalogEntry catalogEntry2 = elements2.nextElement();
            if (catalogEntry2.getEntryType() == Catalog.REWRITE_URI) {
                final String entryArg2 = catalogEntry2.getEntryArg(0);
                if (entryArg2.length() <= s.length() && entryArg2.equals(s.substring(0, entryArg2.length())) && (s2 == null || entryArg2.length() > s2.length())) {
                    s2 = entryArg2;
                    entryArg = catalogEntry2.getEntryArg(1);
                }
            }
            if (entryArg != null) {
                return entryArg + s.substring(s2.length());
            }
        }
        final Enumeration<CatalogEntry> elements3 = this.catalogEntries.elements();
        final Vector<String> vector = new Vector<String>();
        while (elements3.hasMoreElements()) {
            final CatalogEntry catalogEntry3 = elements3.nextElement();
            if (catalogEntry3.getEntryType() == Catalog.DELEGATE_URI) {
                final String entryArg3 = catalogEntry3.getEntryArg(0);
                if (entryArg3.length() > s.length() || !entryArg3.equals(s.substring(0, entryArg3.length()))) {
                    continue;
                }
                vector.addElement(catalogEntry3.getEntryArg(1));
            }
        }
        if (vector.size() > 0) {
            final Enumeration<String> elements4 = vector.elements();
            if (Debug.getDebug() > 1) {
                Debug.message(2, "Switching to delegated catalog(s):");
                while (elements4.hasMoreElements()) {
                    Debug.message(2, "\t" + elements4.nextElement());
                }
            }
            final Catalog catalog = this.newCatalog();
            final Enumeration<String> elements5 = vector.elements();
            while (elements5.hasMoreElements()) {
                catalog.parseCatalog(elements5.nextElement());
            }
            return catalog.resolveURI(s);
        }
        return null;
    }
    
    protected synchronized String resolveSubordinateCatalogs(final int n, final String s, final String s2, final String s3) throws MalformedURLException, IOException {
        for (int i = 0; i < this.catalogs.size(); ++i) {
            Catalog catalog;
            try {
                catalog = this.catalogs.elementAt(i);
            }
            catch (ClassCastException ex) {
                final String s4 = this.catalogs.elementAt(i);
                catalog = this.newCatalog();
                try {
                    catalog.parseCatalog(s4);
                }
                catch (MalformedURLException ex2) {
                    Debug.message(1, "Malformed Catalog URL", s4);
                }
                catch (FileNotFoundException ex3) {
                    Debug.message(1, "Failed to load catalog, file not found", s4);
                }
                catch (IOException ex4) {
                    Debug.message(1, "Failed to load catalog, I/O error", s4);
                }
                this.catalogs.setElementAt(catalog, i);
            }
            String s5 = null;
            if (n == Catalog.DOCTYPE) {
                s5 = catalog.resolveDoctype(s, s2, s3);
            }
            else if (n == Catalog.DOCUMENT) {
                s5 = catalog.resolveDocument();
            }
            else if (n == Catalog.ENTITY) {
                s5 = catalog.resolveEntity(s, s2, s3);
            }
            else if (n == Catalog.NOTATION) {
                s5 = catalog.resolveNotation(s, s2, s3);
            }
            else if (n == Catalog.PUBLIC) {
                s5 = catalog.resolvePublic(s2, s3);
            }
            else if (n == Catalog.SYSTEM) {
                s5 = catalog.resolveSystem(s3);
            }
            else if (n == Catalog.URI) {
                s5 = catalog.resolveURI(s3);
            }
            if (s5 != null) {
                return s5;
            }
        }
        return null;
    }
    
    protected String fixSlashes(final String s) {
        return s.replace('\\', '/');
    }
    
    protected String makeAbsolute(String fixSlashes) {
        URL url = null;
        fixSlashes = this.fixSlashes(fixSlashes);
        try {
            url = new URL(this.base, fixSlashes);
        }
        catch (MalformedURLException ex) {
            Debug.message(1, "Malformed URL on system identifier", fixSlashes);
        }
        if (url != null) {
            return url.toString();
        }
        return fixSlashes;
    }
    
    protected String normalizeURI(final String s) {
        String s2 = "";
        if (s == null) {
            return null;
        }
        byte[] bytes;
        try {
            bytes = s.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            Debug.message(1, "UTF-8 is an unsupported encoding!?");
            return s;
        }
        for (int i = 0; i < bytes.length; ++i) {
            final int n = bytes[i] & 0xFF;
            if (n <= 32 || n > 127 || n == 34 || n == 60 || n == 62 || n == 92 || n == 94 || n == 96 || n == 123 || n == 124 || n == 125 || n == 127) {
                s2 += this.encodedByte(n);
            }
            else {
                s2 += (char)bytes[i];
            }
        }
        return s2;
    }
    
    protected String encodedByte(final int n) {
        final String upperCase = Integer.toHexString(n).toUpperCase();
        if (upperCase.length() < 2) {
            return "%0" + upperCase;
        }
        return "%" + upperCase;
    }
    
    protected void addDelegate(final CatalogEntry catalogEntry) {
        int n = 0;
        final String entryArg = catalogEntry.getEntryArg(0);
        final Enumeration<CatalogEntry> elements = (Enumeration<CatalogEntry>)this.localDelegate.elements();
        while (elements.hasMoreElements()) {
            final String entryArg2 = elements.nextElement().getEntryArg(0);
            if (entryArg2.equals(entryArg)) {
                return;
            }
            if (entryArg2.length() > entryArg.length()) {
                ++n;
            }
            if (entryArg2.length() < entryArg.length()) {
                break;
            }
        }
        if (this.localDelegate.size() == 0) {
            this.localDelegate.addElement(catalogEntry);
        }
        else {
            this.localDelegate.insertElementAt(catalogEntry, n);
        }
    }
    
    static {
        BASE = CatalogEntry.addEntryType("BASE", 1);
        CATALOG = CatalogEntry.addEntryType("CATALOG", 1);
        DOCUMENT = CatalogEntry.addEntryType("DOCUMENT", 1);
        OVERRIDE = CatalogEntry.addEntryType("OVERRIDE", 1);
        SGMLDECL = CatalogEntry.addEntryType("SGMLDECL", 1);
        DELEGATE_PUBLIC = CatalogEntry.addEntryType("DELEGATE_PUBLIC", 2);
        DELEGATE_SYSTEM = CatalogEntry.addEntryType("DELEGATE_SYSTEM", 2);
        DELEGATE_URI = CatalogEntry.addEntryType("DELEGATE_URI", 2);
        DOCTYPE = CatalogEntry.addEntryType("DOCTYPE", 2);
        DTDDECL = CatalogEntry.addEntryType("DTDDECL", 2);
        ENTITY = CatalogEntry.addEntryType("ENTITY", 2);
        LINKTYPE = CatalogEntry.addEntryType("LINKTYPE", 2);
        NOTATION = CatalogEntry.addEntryType("NOTATION", 2);
        PUBLIC = CatalogEntry.addEntryType("PUBLIC", 2);
        SYSTEM = CatalogEntry.addEntryType("SYSTEM", 2);
        URI = CatalogEntry.addEntryType("URI", 2);
        REWRITE_SYSTEM = CatalogEntry.addEntryType("REWRITE_SYSTEM", 2);
        REWRITE_URI = CatalogEntry.addEntryType("REWRITE_URI", 2);
    }
}

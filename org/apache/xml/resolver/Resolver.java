// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

import java.io.FileNotFoundException;
import java.util.Vector;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import org.apache.xml.resolver.helpers.Debug;
import org.apache.xml.resolver.readers.TR9401CatalogReader;
import org.apache.xml.resolver.readers.CatalogReader;
import org.apache.xml.resolver.readers.SAXCatalogReader;
import javax.xml.parsers.SAXParserFactory;

public class Resolver extends Catalog
{
    public static final int URISUFFIX;
    public static final int SYSTEMSUFFIX;
    public static final int RESOLVER;
    public static final int SYSTEMREVERSE;
    
    public void setupReaders() {
        final SAXParserFactory instance = SAXParserFactory.newInstance();
        instance.setNamespaceAware(true);
        instance.setValidating(false);
        final SAXCatalogReader saxCatalogReader = new SAXCatalogReader(instance);
        saxCatalogReader.setCatalogParser((String)null, "XMLCatalog", "org.apache.xml.resolver.readers.XCatalogReader");
        saxCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "org.apache.xml.resolver.readers.ExtendedXMLCatalogReader");
        this.addReader("application/xml", (CatalogReader)saxCatalogReader);
        this.addReader("text/plain", (CatalogReader)new TR9401CatalogReader());
    }
    
    public void addEntry(final CatalogEntry catalogEntry) {
        final int entryType = catalogEntry.getEntryType();
        if (entryType == Resolver.URISUFFIX) {
            final String normalizeURI = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute);
            Debug.message(4, "URISUFFIX", normalizeURI, absolute);
        }
        else if (entryType == Resolver.SYSTEMSUFFIX) {
            final String normalizeURI2 = this.normalizeURI(catalogEntry.getEntryArg(0));
            final String absolute2 = this.makeAbsolute(this.normalizeURI(catalogEntry.getEntryArg(1)));
            catalogEntry.setEntryArg(1, absolute2);
            Debug.message(4, "SYSTEMSUFFIX", normalizeURI2, absolute2);
        }
        super.addEntry(catalogEntry);
    }
    
    public String resolveURI(final String s) throws MalformedURLException, IOException {
        final String resolveURI = super.resolveURI(s);
        if (resolveURI != null) {
            return resolveURI;
        }
        final Enumeration<CatalogEntry> elements = (Enumeration<CatalogEntry>)super.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Resolver.RESOLVER) {
                final String resolveExternalSystem = this.resolveExternalSystem(s, catalogEntry.getEntryArg(0));
                if (resolveExternalSystem != null) {
                    return resolveExternalSystem;
                }
                continue;
            }
            else {
                if (catalogEntry.getEntryType() != Resolver.URISUFFIX) {
                    continue;
                }
                final String entryArg = catalogEntry.getEntryArg(0);
                final String entryArg2 = catalogEntry.getEntryArg(1);
                if (entryArg.length() <= s.length() && s.substring(s.length() - entryArg.length()).equals(entryArg)) {
                    return entryArg2;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.URI, null, null, s);
    }
    
    public String resolveSystem(final String s) throws MalformedURLException, IOException {
        final String resolveSystem = super.resolveSystem(s);
        if (resolveSystem != null) {
            return resolveSystem;
        }
        final Enumeration<CatalogEntry> elements = (Enumeration<CatalogEntry>)super.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Resolver.RESOLVER) {
                final String resolveExternalSystem = this.resolveExternalSystem(s, catalogEntry.getEntryArg(0));
                if (resolveExternalSystem != null) {
                    return resolveExternalSystem;
                }
                continue;
            }
            else {
                if (catalogEntry.getEntryType() != Resolver.SYSTEMSUFFIX) {
                    continue;
                }
                final String entryArg = catalogEntry.getEntryArg(0);
                final String entryArg2 = catalogEntry.getEntryArg(1);
                if (entryArg.length() <= s.length() && s.substring(s.length() - entryArg.length()).equals(entryArg)) {
                    return entryArg2;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, s);
    }
    
    public String resolvePublic(final String s, final String s2) throws MalformedURLException, IOException {
        final String resolvePublic = super.resolvePublic(s, s2);
        if (resolvePublic != null) {
            return resolvePublic;
        }
        final Enumeration<CatalogEntry> elements = super.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Resolver.RESOLVER) {
                if (s2 != null) {
                    final String resolveExternalSystem = this.resolveExternalSystem(s2, catalogEntry.getEntryArg(0));
                    if (resolveExternalSystem != null) {
                        return resolveExternalSystem;
                    }
                }
                final String resolveExternalPublic = this.resolveExternalPublic(s, catalogEntry.getEntryArg(0));
                if (resolveExternalPublic != null) {
                    return resolveExternalPublic;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.PUBLIC, null, s, s2);
    }
    
    protected String resolveExternalSystem(final String s, final String s2) throws MalformedURLException, IOException {
        final Resolver queryResolver = this.queryResolver(s2, "i2l", s, null);
        if (queryResolver != null) {
            return queryResolver.resolveSystem(s);
        }
        return null;
    }
    
    protected String resolveExternalPublic(final String s, final String s2) throws MalformedURLException, IOException {
        final Resolver queryResolver = this.queryResolver(s2, "fpi2l", s, null);
        if (queryResolver != null) {
            return queryResolver.resolvePublic(s, null);
        }
        return null;
    }
    
    protected Resolver queryResolver(final String s, final String s2, final String s3, final String s4) {
        final String string = s + "?command=" + s2 + "&format=tr9401&uri=" + s3 + "&uri2=" + s4;
        try {
            final URLConnection openConnection = new URL(string).openConnection();
            openConnection.setUseCaches(false);
            final Resolver resolver = (Resolver)this.newCatalog();
            String s5 = openConnection.getContentType();
            if (s5.indexOf(";") > 0) {
                s5 = s5.substring(0, s5.indexOf(";"));
            }
            resolver.parseCatalog(s5, openConnection.getInputStream());
            return resolver;
        }
        catch (CatalogException ex) {
            if (ex.getExceptionType() == 6) {
                Debug.message(1, "Unparseable catalog: " + string);
            }
            else if (ex.getExceptionType() == 5) {
                Debug.message(1, "Unknown catalog format: " + string);
            }
            return null;
        }
        catch (MalformedURLException ex2) {
            Debug.message(1, "Malformed resolver URL: " + string);
            return null;
        }
        catch (IOException ex3) {
            Debug.message(1, "I/O Exception opening resolver: " + string);
            return null;
        }
    }
    
    private Vector appendVector(final Vector vector, final Vector vector2) {
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                vector.addElement(vector2.elementAt(i));
            }
        }
        return vector;
    }
    
    public Vector resolveAllSystemReverse(final String s) throws MalformedURLException, IOException {
        Vector appendVector = new Vector();
        if (s != null) {
            appendVector = this.appendVector(appendVector, this.resolveLocalSystemReverse(s));
        }
        return this.appendVector(appendVector, this.resolveAllSubordinateCatalogs(Resolver.SYSTEMREVERSE, null, null, s));
    }
    
    public String resolveSystemReverse(final String s) throws MalformedURLException, IOException {
        final Vector resolveAllSystemReverse = this.resolveAllSystemReverse(s);
        if (resolveAllSystemReverse != null && resolveAllSystemReverse.size() > 0) {
            return resolveAllSystemReverse.elementAt(0);
        }
        return null;
    }
    
    public Vector resolveAllSystem(final String s) throws MalformedURLException, IOException {
        Vector appendVector = new Vector();
        if (s != null) {
            appendVector = this.appendVector(appendVector, this.resolveAllLocalSystem(s));
        }
        final Vector appendVector2 = this.appendVector(appendVector, this.resolveAllSubordinateCatalogs(Catalog.SYSTEM, null, null, s));
        if (appendVector2.size() > 0) {
            return appendVector2;
        }
        return null;
    }
    
    private Vector resolveAllLocalSystem(final String s) {
        final Vector vector = new Vector<String>();
        final boolean b = System.getProperty("os.name").indexOf("Windows") >= 0;
        final Enumeration<CatalogEntry> elements = super.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.SYSTEM && (catalogEntry.getEntryArg(0).equals(s) || (b && catalogEntry.getEntryArg(0).equalsIgnoreCase(s)))) {
                vector.addElement(catalogEntry.getEntryArg(1));
            }
        }
        if (vector.size() == 0) {
            return null;
        }
        return vector;
    }
    
    private Vector resolveLocalSystemReverse(final String s) {
        final Vector vector = new Vector<String>();
        final boolean b = System.getProperty("os.name").indexOf("Windows") >= 0;
        final Enumeration<CatalogEntry> elements = super.catalogEntries.elements();
        while (elements.hasMoreElements()) {
            final CatalogEntry catalogEntry = elements.nextElement();
            if (catalogEntry.getEntryType() == Catalog.SYSTEM && (catalogEntry.getEntryArg(1).equals(s) || (b && catalogEntry.getEntryArg(1).equalsIgnoreCase(s)))) {
                vector.addElement(catalogEntry.getEntryArg(0));
            }
        }
        if (vector.size() == 0) {
            return null;
        }
        return vector;
    }
    
    private synchronized Vector resolveAllSubordinateCatalogs(final int n, final String s, final String s2, final String s3) throws MalformedURLException, IOException {
        Vector<String> vector = new Vector<String>();
        for (int i = 0; i < super.catalogs.size(); ++i) {
            Resolver resolver;
            try {
                resolver = super.catalogs.elementAt(i);
            }
            catch (ClassCastException ex) {
                final String s4 = super.catalogs.elementAt(i);
                resolver = (Resolver)this.newCatalog();
                try {
                    resolver.parseCatalog(s4);
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
                super.catalogs.setElementAt(resolver, i);
            }
            if (n == Catalog.DOCTYPE) {
                final String resolveDoctype = resolver.resolveDoctype(s, s2, s3);
                if (resolveDoctype != null) {
                    vector.addElement(resolveDoctype);
                    return vector;
                }
            }
            else if (n == Catalog.DOCUMENT) {
                final String resolveDocument = resolver.resolveDocument();
                if (resolveDocument != null) {
                    vector.addElement(resolveDocument);
                    return vector;
                }
            }
            else if (n == Catalog.ENTITY) {
                final String resolveEntity = resolver.resolveEntity(s, s2, s3);
                if (resolveEntity != null) {
                    vector.addElement(resolveEntity);
                    return vector;
                }
            }
            else if (n == Catalog.NOTATION) {
                final String resolveNotation = resolver.resolveNotation(s, s2, s3);
                if (resolveNotation != null) {
                    vector.addElement(resolveNotation);
                    return vector;
                }
            }
            else if (n == Catalog.PUBLIC) {
                final String resolvePublic = resolver.resolvePublic(s2, s3);
                if (resolvePublic != null) {
                    vector.addElement(resolvePublic);
                    return vector;
                }
            }
            else {
                if (n == Catalog.SYSTEM) {
                    vector = (Vector<String>)this.appendVector(vector, resolver.resolveAllSystem(s3));
                    break;
                }
                if (n == Resolver.SYSTEMREVERSE) {
                    vector = (Vector<String>)this.appendVector(vector, resolver.resolveAllSystemReverse(s3));
                }
            }
        }
        if (vector != null) {
            return vector;
        }
        return null;
    }
    
    static {
        URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);
        SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);
        RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);
        SYSTEMREVERSE = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);
    }
}

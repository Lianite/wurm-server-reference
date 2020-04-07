// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.tools;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import java.net.URL;
import org.xml.sax.InputSource;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xml.resolver.helpers.Debug;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.Catalog;
import javax.xml.transform.URIResolver;
import org.xml.sax.EntityResolver;

public class CatalogResolver implements EntityResolver, URIResolver
{
    public boolean namespaceAware;
    public boolean validating;
    private static Catalog staticCatalog;
    private Catalog catalog;
    
    public CatalogResolver() {
        this.namespaceAware = true;
        this.validating = false;
        this.catalog = null;
        this.initializeCatalogs(false);
    }
    
    public CatalogResolver(final boolean b) {
        this.namespaceAware = true;
        this.validating = false;
        this.catalog = null;
        this.initializeCatalogs(b);
    }
    
    private void initializeCatalogs(final boolean b) {
        this.catalog = CatalogResolver.staticCatalog;
        Label_0167: {
            if (!b) {
                if (this.catalog != null) {
                    break Label_0167;
                }
            }
            try {
                final String catalogClassName = CatalogManager.catalogClassName();
                if (catalogClassName == null) {
                    this.catalog = new Catalog();
                }
                else {
                    try {
                        this.catalog = (Catalog)Class.forName(catalogClassName).newInstance();
                    }
                    catch (ClassNotFoundException ex2) {
                        Debug.message(1, "Catalog class named '" + catalogClassName + "' could not be found. Using default.");
                        this.catalog = new Catalog();
                    }
                    catch (ClassCastException ex3) {
                        Debug.message(1, "Class named '" + catalogClassName + "' is not a Catalog. Using default.");
                        this.catalog = new Catalog();
                    }
                }
                this.catalog.setupReaders();
                if (!b) {
                    this.catalog.loadSystemCatalogs();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (!b && this.catalog != null && CatalogManager.staticCatalog()) {
            CatalogResolver.staticCatalog = this.catalog;
        }
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    public String getResolvedEntity(final String s, final String s2) {
        String s3 = null;
        if (this.catalog == null) {
            Debug.message(1, "Catalog resolution attempted with null catalog; ignored");
            return null;
        }
        if (s2 != null) {
            try {
                s3 = this.catalog.resolveSystem(s2);
            }
            catch (MalformedURLException ex) {
                Debug.message(1, "Malformed URL exception trying to resolve", s);
                s3 = null;
            }
            catch (IOException ex2) {
                Debug.message(1, "I/O exception trying to resolve", s);
                s3 = null;
            }
        }
        if (s3 == null) {
            if (s != null) {
                try {
                    s3 = this.catalog.resolvePublic(s, s2);
                }
                catch (MalformedURLException ex3) {
                    Debug.message(1, "Malformed URL exception trying to resolve", s);
                }
                catch (IOException ex4) {
                    Debug.message(1, "I/O exception trying to resolve", s);
                }
            }
            if (s3 != null) {
                Debug.message(2, "Resolved public", s, s3);
            }
        }
        else {
            Debug.message(2, "Resolved system", s2, s3);
        }
        return s3;
    }
    
    public InputSource resolveEntity(final String publicId, final String s) {
        final String resolvedEntity = this.getResolvedEntity(publicId, s);
        if (resolvedEntity != null) {
            try {
                final InputSource inputSource = new InputSource(resolvedEntity);
                inputSource.setPublicId(publicId);
                inputSource.setByteStream(new URL(resolvedEntity).openStream());
                return inputSource;
            }
            catch (Exception ex) {
                Debug.message(1, "Failed to create InputSource", resolvedEntity);
                return null;
            }
        }
        return null;
    }
    
    public Source resolve(final String s, final String s2) throws TransformerException {
        String substring = s;
        final int index = s.indexOf("#");
        if (index >= 0) {
            substring = s.substring(0, index);
            s.substring(index + 1);
        }
        String s3 = null;
        try {
            s3 = this.catalog.resolveURI(s);
        }
        catch (Exception ex2) {}
        if (s3 == null) {
            try {
                if (s2 == null) {
                    s3 = new URL(substring).toString();
                }
                else {
                    final URL url = new URL(s2);
                    s3 = ((s.length() == 0) ? url : new URL(url, substring)).toString();
                }
            }
            catch (MalformedURLException ex) {
                final String absolute = this.makeAbsolute(s2);
                if (!absolute.equals(s2)) {
                    return this.resolve(s, absolute);
                }
                throw new TransformerException("Malformed URL " + s + "(base " + s2 + ")", ex);
            }
        }
        Debug.message(2, "Resolved URI", s, s3);
        final SAXSource saxSource = new SAXSource();
        saxSource.setInputSource(new InputSource(s3));
        return saxSource;
    }
    
    private String makeAbsolute(String s) {
        if (s == null) {
            s = "";
        }
        try {
            return new URL(s).toString();
        }
        catch (MalformedURLException ex) {
            final String property = System.getProperty("user.dir");
            String s2;
            if (property.endsWith("/")) {
                s2 = "file://" + property + s;
            }
            else {
                s2 = "file://" + property + "/" + s;
            }
            try {
                return new URL(s2).toString();
            }
            catch (MalformedURLException ex2) {
                return s;
            }
        }
    }
    
    static {
        CatalogResolver.staticCatalog = null;
    }
}

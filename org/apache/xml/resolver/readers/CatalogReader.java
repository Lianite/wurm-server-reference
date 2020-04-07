// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import java.io.InputStream;
import org.apache.xml.resolver.CatalogException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xml.resolver.Catalog;

public interface CatalogReader
{
    void readCatalog(final Catalog p0, final String p1) throws MalformedURLException, IOException, CatalogException;
    
    void readCatalog(final Catalog p0, final InputStream p1) throws IOException, CatalogException;
}

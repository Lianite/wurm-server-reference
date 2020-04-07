// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.apache.xml.resolver.Catalog;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;

public interface SAXCatalogParser extends ContentHandler, DocumentHandler
{
    void setCatalog(final Catalog p0);
}

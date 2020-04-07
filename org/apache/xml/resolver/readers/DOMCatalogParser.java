// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

import org.w3c.dom.Node;
import org.apache.xml.resolver.Catalog;

public interface DOMCatalogParser
{
    void parseCatalogEntry(final Catalog p0, final Node p1);
}

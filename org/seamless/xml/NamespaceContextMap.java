// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;

public abstract class NamespaceContextMap extends HashMap<String, String> implements NamespaceContext
{
    public String getNamespaceURI(final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        }
        if (prefix.equals("")) {
            return this.getDefaultNamespaceURI();
        }
        if (this.get(prefix) != null) {
            return ((HashMap<K, String>)this).get(prefix);
        }
        return "";
    }
    
    public String getPrefix(final String namespaceURI) {
        return null;
    }
    
    public Iterator getPrefixes(final String s) {
        return null;
    }
    
    protected abstract String getDefaultNamespaceURI();
}

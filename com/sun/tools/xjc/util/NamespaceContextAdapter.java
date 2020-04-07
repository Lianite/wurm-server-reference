// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.Collections;
import java.util.Iterator;
import com.sun.xml.xsom.XmlString;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextAdapter implements NamespaceContext
{
    private XmlString xstr;
    
    public NamespaceContextAdapter(final XmlString xstr) {
        this.xstr = xstr;
    }
    
    public String getNamespaceURI(final String prefix) {
        return this.xstr.resolvePrefix(prefix);
    }
    
    public String getPrefix(final String namespaceURI) {
        return null;
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
        return Collections.EMPTY_LIST.iterator();
    }
}

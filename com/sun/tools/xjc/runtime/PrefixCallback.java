// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.SAXException;

public interface PrefixCallback
{
    void onPrefixMapping(final String p0, final String p1) throws SAXException;
}

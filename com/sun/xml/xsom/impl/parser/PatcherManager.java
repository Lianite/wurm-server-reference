// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;

public interface PatcherManager
{
    void addPatcher(final Patch p0);
    
    void reportError(final String p0, final Locator p1) throws SAXException;
    
    public interface Patcher
    {
        void run() throws SAXException;
    }
}

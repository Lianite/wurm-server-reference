// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.parser;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public interface XMLParser
{
    void parse(final InputSource p0, final ContentHandler p1, final ErrorHandler p2, final EntityResolver p3) throws SAXException, IOException;
}

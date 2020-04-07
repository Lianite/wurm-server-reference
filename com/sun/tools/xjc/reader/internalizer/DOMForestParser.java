// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import com.sun.xml.xsom.parser.XMLParser;

class DOMForestParser implements XMLParser
{
    private final DOMForest forest;
    private final DOMForestScanner scanner;
    private final XMLParser fallbackParser;
    
    DOMForestParser(final DOMForest forest, final XMLParser fallbackParser) {
        this.forest = forest;
        this.scanner = new DOMForestScanner(forest);
        this.fallbackParser = fallbackParser;
    }
    
    public void parse(final InputSource source, final ContentHandler contentHandler, final ErrorHandler errorHandler, final EntityResolver entityResolver) throws SAXException, IOException {
        final String systemId = source.getSystemId();
        final Document dom = this.forest.get(systemId);
        if (dom == null) {
            this.fallbackParser.parse(source, contentHandler, errorHandler, entityResolver);
            return;
        }
        this.scanner.scan(dom, contentHandler);
    }
}

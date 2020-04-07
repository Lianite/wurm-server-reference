// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.xml.xsom.parser.AnnotationContext;
import com.sun.xml.xsom.parser.AnnotationParser;

class DefaultAnnotationParser extends AnnotationParser
{
    public static final AnnotationParser theInstance;
    
    public ContentHandler getContentHandler(final AnnotationContext contest, final String elementName, final ErrorHandler errorHandler, final EntityResolver entityResolver) {
        return new DefaultHandler();
    }
    
    public Object getResult(final Object existing) {
        return null;
    }
    
    static {
        theInstance = new DefaultAnnotationParser();
    }
}

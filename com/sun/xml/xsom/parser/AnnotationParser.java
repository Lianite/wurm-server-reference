// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.parser;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public abstract class AnnotationParser
{
    public abstract ContentHandler getContentHandler(final AnnotationContext p0, final String p1, final ErrorHandler p2, final EntityResolver p3);
    
    public abstract Object getResult(final Object p0);
}

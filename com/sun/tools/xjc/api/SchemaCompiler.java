// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.istack.NotNull;
import com.sun.tools.xjc.Options;
import org.xml.sax.EntityResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;

public interface SchemaCompiler
{
    ContentHandler getParserHandler(final String p0);
    
    void parseSchema(final InputSource p0);
    
    void setTargetVersion(final SpecVersion p0);
    
    void parseSchema(final String p0, final Element p1);
    
    void parseSchema(final String p0, final XMLStreamReader p1) throws XMLStreamException;
    
    void setErrorListener(final ErrorListener p0);
    
    void setEntityResolver(final EntityResolver p0);
    
    void setDefaultPackageName(final String p0);
    
    void forcePackageName(final String p0);
    
    void setClassNameAllocator(final ClassNameAllocator p0);
    
    void resetSchema();
    
    S2JJAXBModel bind();
    
    @NotNull
    Options getOptions();
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2.output;

import java.io.PrintStream;

public class DumpSerializer implements XmlSerializer
{
    private final PrintStream out;
    
    public DumpSerializer(final PrintStream out) {
        this.out = out;
    }
    
    public void beginStartTag(final String uri, final String localName, final String prefix) {
        this.out.println('<' + prefix + ':' + localName);
    }
    
    public void writeAttribute(final String uri, final String localName, final String prefix, final StringBuilder value) {
        this.out.println('@' + prefix + ':' + localName + '=' + (Object)value);
    }
    
    public void writeXmlns(final String prefix, final String uri) {
        this.out.println("xmlns:" + prefix + '=' + uri);
    }
    
    public void endStartTag(final String uri, final String localName, final String prefix) {
        this.out.println('>');
    }
    
    public void endTag() {
        this.out.println("</  >");
    }
    
    public void text(final StringBuilder text) {
        this.out.println(text);
    }
    
    public void cdata(final StringBuilder text) {
        this.out.println("<![CDATA[");
        this.out.println(text);
        this.out.println("]]>");
    }
    
    public void comment(final StringBuilder comment) {
        this.out.println("<!--");
        this.out.println(comment);
        this.out.println("-->");
    }
    
    public void startDocument() {
        this.out.println("<?xml?>");
    }
    
    public void endDocument() {
        this.out.println("done");
    }
    
    public void flush() {
        this.out.println("flush");
    }
}

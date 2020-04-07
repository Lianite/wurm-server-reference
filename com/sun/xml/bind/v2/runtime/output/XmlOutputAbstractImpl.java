// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.output;

import com.sun.xml.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

public abstract class XmlOutputAbstractImpl implements XmlOutput
{
    protected int[] nsUriIndex2prefixIndex;
    protected NamespaceContextImpl nsContext;
    protected XMLSerializer serializer;
    
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        this.nsUriIndex2prefixIndex = nsUriIndex2prefixIndex;
        this.nsContext = nsContext;
        this.serializer = serializer;
    }
    
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.serializer = null;
    }
    
    public void beginStartTag(final Name name) throws IOException, XMLStreamException {
        this.beginStartTag(this.nsUriIndex2prefixIndex[name.nsUriIndex], name.localName);
    }
    
    public abstract void beginStartTag(final int p0, final String p1) throws IOException, XMLStreamException;
    
    public void attribute(final Name name, final String value) throws IOException, XMLStreamException {
        final short idx = name.nsUriIndex;
        if (idx == -1) {
            this.attribute(-1, name.localName, value);
        }
        else {
            this.attribute(this.nsUriIndex2prefixIndex[idx], name.localName, value);
        }
    }
    
    public abstract void attribute(final int p0, final String p1, final String p2) throws IOException, XMLStreamException;
    
    public abstract void endStartTag() throws IOException, SAXException;
    
    public void endTag(final Name name) throws IOException, SAXException, XMLStreamException {
        this.endTag(this.nsUriIndex2prefixIndex[name.nsUriIndex], name.localName);
    }
    
    public abstract void endTag(final int p0, final String p1) throws IOException, SAXException, XMLStreamException;
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class ContentHandlerNamespacePrefixAdapter extends XMLFilterImpl
{
    private boolean namespacePrefixes;
    private String[] nsBinding;
    private int len;
    private final AttributesImpl atts;
    private static final String PREFIX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
    
    public ContentHandlerNamespacePrefixAdapter() {
        this.namespacePrefixes = false;
        this.nsBinding = new String[8];
        this.atts = new AttributesImpl();
    }
    
    public ContentHandlerNamespacePrefixAdapter(final XMLReader parent) {
        this.namespacePrefixes = false;
        this.nsBinding = new String[8];
        this.atts = new AttributesImpl();
        this.setParent(parent);
    }
    
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return this.namespacePrefixes;
        }
        return super.getFeature(name);
    }
    
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            this.namespacePrefixes = value;
            return;
        }
        if (name.equals("http://xml.org/sax/features/namespaces") && value) {
            return;
        }
        super.setFeature(name, value);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.len == this.nsBinding.length) {
            final String[] buf = new String[this.nsBinding.length * 2];
            System.arraycopy(this.nsBinding, 0, buf, 0, this.nsBinding.length);
            this.nsBinding = buf;
        }
        super.startPrefixMapping(this.nsBinding[this.len++] = prefix, this.nsBinding[this.len++] = uri);
    }
    
    public void startElement(final String uri, final String localName, final String qName, Attributes atts) throws SAXException {
        if (this.namespacePrefixes) {
            this.atts.setAttributes(atts);
            for (int i = 0; i < this.len; i += 2) {
                final String prefix = this.nsBinding[i];
                if (prefix.length() == 0) {
                    this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", "xmlns", "xmlns", "CDATA", this.nsBinding[i + 1]);
                }
                else {
                    this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", prefix, "xmlns:" + prefix, "CDATA", this.nsBinding[i + 1]);
                }
            }
            atts = this.atts;
        }
        this.len = 0;
        super.startElement(uri, localName, qName, atts);
    }
}

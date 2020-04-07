// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.w3c.dom.ls.LSInput;

public class LSInputSAXWrapper implements LSInput
{
    private InputSource core;
    
    public LSInputSAXWrapper(final InputSource inputSource) {
        assert inputSource != null;
        this.core = inputSource;
    }
    
    public Reader getCharacterStream() {
        return this.core.getCharacterStream();
    }
    
    public void setCharacterStream(final Reader characterStream) {
        this.core.setCharacterStream(characterStream);
    }
    
    public InputStream getByteStream() {
        return this.core.getByteStream();
    }
    
    public void setByteStream(final InputStream byteStream) {
        this.core.setByteStream(byteStream);
    }
    
    public String getStringData() {
        return null;
    }
    
    public void setStringData(final String stringData) {
    }
    
    public String getSystemId() {
        return this.core.getSystemId();
    }
    
    public void setSystemId(final String systemId) {
        this.core.setSystemId(systemId);
    }
    
    public String getPublicId() {
        return this.core.getPublicId();
    }
    
    public void setPublicId(final String publicId) {
        this.core.setPublicId(publicId);
    }
    
    public String getBaseURI() {
        return null;
    }
    
    public void setBaseURI(final String baseURI) {
    }
    
    public String getEncoding() {
        return this.core.getEncoding();
    }
    
    public void setEncoding(final String encoding) {
        this.core.setEncoding(encoding);
    }
    
    public boolean getCertifiedText() {
        return true;
    }
    
    public void setCertifiedText(final boolean certifiedText) {
    }
}

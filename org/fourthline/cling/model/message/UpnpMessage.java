// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import org.seamless.util.MimeType;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import java.io.UnsupportedEncodingException;

public abstract class UpnpMessage<O extends UpnpOperation>
{
    private int udaMajorVersion;
    private int udaMinorVersion;
    private O operation;
    private UpnpHeaders headers;
    private Object body;
    private BodyType bodyType;
    
    protected UpnpMessage(final UpnpMessage<O> source) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = source.getOperation();
        this.headers = source.getHeaders();
        this.body = source.getBody();
        this.bodyType = source.getBodyType();
        this.udaMajorVersion = source.getUdaMajorVersion();
        this.udaMinorVersion = source.getUdaMinorVersion();
    }
    
    protected UpnpMessage(final O operation) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = operation;
    }
    
    protected UpnpMessage(final O operation, final BodyType bodyType, final Object body) {
        this.udaMajorVersion = 1;
        this.udaMinorVersion = 0;
        this.headers = new UpnpHeaders();
        this.bodyType = BodyType.STRING;
        this.operation = operation;
        this.bodyType = bodyType;
        this.body = body;
    }
    
    public int getUdaMajorVersion() {
        return this.udaMajorVersion;
    }
    
    public void setUdaMajorVersion(final int udaMajorVersion) {
        this.udaMajorVersion = udaMajorVersion;
    }
    
    public int getUdaMinorVersion() {
        return this.udaMinorVersion;
    }
    
    public void setUdaMinorVersion(final int udaMinorVersion) {
        this.udaMinorVersion = udaMinorVersion;
    }
    
    public UpnpHeaders getHeaders() {
        return this.headers;
    }
    
    public void setHeaders(final UpnpHeaders headers) {
        this.headers = headers;
    }
    
    public Object getBody() {
        return this.body;
    }
    
    public void setBody(final String string) {
        this.bodyType = BodyType.STRING;
        this.body = string;
    }
    
    public void setBody(final BodyType bodyType, final Object body) {
        this.bodyType = bodyType;
        this.body = body;
    }
    
    public void setBodyCharacters(final byte[] characterData) throws UnsupportedEncodingException {
        this.setBody(BodyType.STRING, new String(characterData, (this.getContentTypeCharset() != null) ? this.getContentTypeCharset() : "UTF-8"));
    }
    
    public boolean hasBody() {
        return this.getBody() != null;
    }
    
    public BodyType getBodyType() {
        return this.bodyType;
    }
    
    public void setBodyType(final BodyType bodyType) {
        this.bodyType = bodyType;
    }
    
    public String getBodyString() {
        try {
            if (!this.hasBody()) {
                return null;
            }
            if (this.getBodyType().equals(BodyType.STRING)) {
                String body = (String)this.getBody();
                if (body.charAt(0) == '\ufeff') {
                    body = body.substring(1);
                }
                return body;
            }
            return new String((byte[])this.getBody(), "UTF-8");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public byte[] getBodyBytes() {
        try {
            if (!this.hasBody()) {
                return null;
            }
            if (this.getBodyType().equals(BodyType.STRING)) {
                return this.getBodyString().getBytes();
            }
            return (byte[])this.getBody();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public O getOperation() {
        return this.operation;
    }
    
    public boolean isContentTypeMissingOrText() {
        final ContentTypeHeader contentTypeHeader = this.getContentTypeHeader();
        return contentTypeHeader == null || contentTypeHeader.isText();
    }
    
    public ContentTypeHeader getContentTypeHeader() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
    }
    
    public boolean isContentTypeText() {
        final ContentTypeHeader ct = this.getContentTypeHeader();
        return ct != null && ct.isText();
    }
    
    public boolean isContentTypeTextUDA() {
        final ContentTypeHeader ct = this.getContentTypeHeader();
        return ct != null && ct.isUDACompliantXML();
    }
    
    public String getContentTypeCharset() {
        final ContentTypeHeader ct = this.getContentTypeHeader();
        return (ct != null) ? ct.getValue().getParameters().get("charset") : null;
    }
    
    public boolean hasHostHeader() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.HOST) != null;
    }
    
    public boolean isBodyNonEmptyString() {
        return this.hasBody() && this.getBodyType().equals(BodyType.STRING) && this.getBodyString().length() > 0;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") " + this.getOperation().toString();
    }
    
    public enum BodyType
    {
        STRING, 
        BYTES;
    }
}

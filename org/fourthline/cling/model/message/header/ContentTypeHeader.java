// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.seamless.util.MimeType;

public class ContentTypeHeader extends UpnpHeader<MimeType>
{
    public static final MimeType DEFAULT_CONTENT_TYPE;
    public static final MimeType DEFAULT_CONTENT_TYPE_UTF8;
    
    public ContentTypeHeader() {
        this.setValue(ContentTypeHeader.DEFAULT_CONTENT_TYPE);
    }
    
    public ContentTypeHeader(final MimeType contentType) {
        this.setValue(contentType);
    }
    
    public ContentTypeHeader(final String s) throws InvalidHeaderException {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        this.setValue(MimeType.valueOf(s));
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
    
    public boolean isUDACompliantXML() {
        return this.isText() && this.getValue().getSubtype().equals(ContentTypeHeader.DEFAULT_CONTENT_TYPE.getSubtype());
    }
    
    public boolean isText() {
        return this.getValue() != null && this.getValue().getType().equals(ContentTypeHeader.DEFAULT_CONTENT_TYPE.getType());
    }
    
    static {
        DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/xml");
        DEFAULT_CONTENT_TYPE_UTF8 = MimeType.valueOf("text/xml;charset=\"utf-8\"");
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import org.seamless.util.MimeType;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.ContentTypeHeader;

public class StreamResponseMessage extends UpnpMessage<UpnpResponse>
{
    public StreamResponseMessage(final StreamResponseMessage source) {
        super(source);
    }
    
    public StreamResponseMessage(final UpnpResponse.Status status) {
        super(new UpnpResponse(status));
    }
    
    public StreamResponseMessage(final UpnpResponse operation) {
        super(operation);
    }
    
    public StreamResponseMessage(final UpnpResponse operation, final String body) {
        super(operation, BodyType.STRING, body);
    }
    
    public StreamResponseMessage(final String body) {
        super(new UpnpResponse(UpnpResponse.Status.OK), BodyType.STRING, body);
    }
    
    public StreamResponseMessage(final UpnpResponse operation, final byte[] body) {
        super(operation, BodyType.BYTES, body);
    }
    
    public StreamResponseMessage(final byte[] body) {
        super(new UpnpResponse(UpnpResponse.Status.OK), BodyType.BYTES, body);
    }
    
    public StreamResponseMessage(final String body, final ContentTypeHeader contentType) {
        this(body);
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }
    
    public StreamResponseMessage(final String body, final MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }
    
    public StreamResponseMessage(final byte[] body, final ContentTypeHeader contentType) {
        this(body);
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }
    
    public StreamResponseMessage(final byte[] body, final MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }
}

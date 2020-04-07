// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.entity;

import org.apache.http.HttpEntity;
import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.Header;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.apache.http.impl.io.IdentityInputStream;
import java.io.InputStream;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.annotation.Immutable;

@Immutable
public class EntityDeserializer
{
    private final ContentLengthStrategy lenStrategy;
    
    public EntityDeserializer(final ContentLengthStrategy lenStrategy) {
        if (lenStrategy == null) {
            throw new IllegalArgumentException("Content length strategy may not be null");
        }
        this.lenStrategy = lenStrategy;
    }
    
    protected BasicHttpEntity doDeserialize(final SessionInputBuffer inbuffer, final HttpMessage message) throws HttpException, IOException {
        final BasicHttpEntity entity = new BasicHttpEntity();
        final long len = this.lenStrategy.determineLength(message);
        if (len == -2L) {
            entity.setChunked(true);
            entity.setContentLength(-1L);
            entity.setContent(new ChunkedInputStream(inbuffer));
        }
        else if (len == -1L) {
            entity.setChunked(false);
            entity.setContentLength(-1L);
            entity.setContent(new IdentityInputStream(inbuffer));
        }
        else {
            entity.setChunked(false);
            entity.setContentLength(len);
            entity.setContent(new ContentLengthInputStream(inbuffer, len));
        }
        final Header contentTypeHeader = message.getFirstHeader("Content-Type");
        if (contentTypeHeader != null) {
            entity.setContentType(contentTypeHeader);
        }
        final Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
        if (contentEncodingHeader != null) {
            entity.setContentEncoding(contentEncodingHeader);
        }
        return entity;
    }
    
    public HttpEntity deserialize(final SessionInputBuffer inbuffer, final HttpMessage message) throws HttpException, IOException {
        if (inbuffer == null) {
            throw new IllegalArgumentException("Session input buffer may not be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        return this.doDeserialize(inbuffer, message);
    }
}

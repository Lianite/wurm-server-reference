// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl;

import org.apache.http.StatusLine;
import java.util.Locale;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpResponseFactory;

@Immutable
public class DefaultHttpResponseFactory implements HttpResponseFactory
{
    protected final ReasonPhraseCatalog reasonCatalog;
    
    public DefaultHttpResponseFactory(final ReasonPhraseCatalog catalog) {
        if (catalog == null) {
            throw new IllegalArgumentException("Reason phrase catalog must not be null.");
        }
        this.reasonCatalog = catalog;
    }
    
    public DefaultHttpResponseFactory() {
        this(EnglishReasonPhraseCatalog.INSTANCE);
    }
    
    public HttpResponse newHttpResponse(final ProtocolVersion ver, final int status, final HttpContext context) {
        if (ver == null) {
            throw new IllegalArgumentException("HTTP version may not be null");
        }
        final Locale loc = this.determineLocale(context);
        final String reason = this.reasonCatalog.getReason(status, loc);
        final StatusLine statusline = new BasicStatusLine(ver, status, reason);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }
    
    public HttpResponse newHttpResponse(final StatusLine statusline, final HttpContext context) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        }
        final Locale loc = this.determineLocale(context);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }
    
    protected Locale determineLocale(final HttpContext context) {
        return Locale.getDefault();
    }
}

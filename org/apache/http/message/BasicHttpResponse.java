// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.ProtocolVersion;
import java.util.Locale;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.HttpResponse;

@NotThreadSafe
public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse
{
    private StatusLine statusline;
    private HttpEntity entity;
    private ReasonPhraseCatalog reasonCatalog;
    private Locale locale;
    
    public BasicHttpResponse(final StatusLine statusline, final ReasonPhraseCatalog catalog, final Locale locale) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null.");
        }
        this.statusline = statusline;
        this.reasonCatalog = catalog;
        this.locale = ((locale != null) ? locale : Locale.getDefault());
    }
    
    public BasicHttpResponse(final StatusLine statusline) {
        this(statusline, null, null);
    }
    
    public BasicHttpResponse(final ProtocolVersion ver, final int code, final String reason) {
        this(new BasicStatusLine(ver, code, reason), null, null);
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.statusline.getProtocolVersion();
    }
    
    public StatusLine getStatusLine() {
        return this.statusline;
    }
    
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void setStatusLine(final StatusLine statusline) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        }
        this.statusline = statusline;
    }
    
    public void setStatusLine(final ProtocolVersion ver, final int code) {
        this.statusline = new BasicStatusLine(ver, code, this.getReason(code));
    }
    
    public void setStatusLine(final ProtocolVersion ver, final int code, final String reason) {
        this.statusline = new BasicStatusLine(ver, code, reason);
    }
    
    public void setStatusCode(final int code) {
        final ProtocolVersion ver = this.statusline.getProtocolVersion();
        this.statusline = new BasicStatusLine(ver, code, this.getReason(code));
    }
    
    public void setReasonPhrase(final String reason) {
        if (reason != null && (reason.indexOf(10) >= 0 || reason.indexOf(13) >= 0)) {
            throw new IllegalArgumentException("Line break in reason phrase.");
        }
        this.statusline = new BasicStatusLine(this.statusline.getProtocolVersion(), this.statusline.getStatusCode(), reason);
    }
    
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    public void setLocale(final Locale loc) {
        if (loc == null) {
            throw new IllegalArgumentException("Locale may not be null.");
        }
        this.locale = loc;
        final int code = this.statusline.getStatusCode();
        this.statusline = new BasicStatusLine(this.statusline.getProtocolVersion(), code, this.getReason(code));
    }
    
    protected String getReason(final int code) {
        return (this.reasonCatalog == null) ? null : this.reasonCatalog.getReason(code, this.locale);
    }
    
    public String toString() {
        return this.statusline + " " + this.headergroup;
    }
}

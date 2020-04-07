// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.HeaderIterator;
import org.apache.http.Header;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.HttpMessage;

@NotThreadSafe
public abstract class AbstractHttpMessage implements HttpMessage
{
    protected HeaderGroup headergroup;
    protected HttpParams params;
    
    protected AbstractHttpMessage(final HttpParams params) {
        this.headergroup = new HeaderGroup();
        this.params = params;
    }
    
    protected AbstractHttpMessage() {
        this(null);
    }
    
    public boolean containsHeader(final String name) {
        return this.headergroup.containsHeader(name);
    }
    
    public Header[] getHeaders(final String name) {
        return this.headergroup.getHeaders(name);
    }
    
    public Header getFirstHeader(final String name) {
        return this.headergroup.getFirstHeader(name);
    }
    
    public Header getLastHeader(final String name) {
        return this.headergroup.getLastHeader(name);
    }
    
    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }
    
    public void addHeader(final Header header) {
        this.headergroup.addHeader(header);
    }
    
    public void addHeader(final String name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name may not be null");
        }
        this.headergroup.addHeader(new BasicHeader(name, value));
    }
    
    public void setHeader(final Header header) {
        this.headergroup.updateHeader(header);
    }
    
    public void setHeader(final String name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name may not be null");
        }
        this.headergroup.updateHeader(new BasicHeader(name, value));
    }
    
    public void setHeaders(final Header[] headers) {
        this.headergroup.setHeaders(headers);
    }
    
    public void removeHeader(final Header header) {
        this.headergroup.removeHeader(header);
    }
    
    public void removeHeaders(final String name) {
        if (name == null) {
            return;
        }
        final HeaderIterator i = this.headergroup.iterator();
        while (i.hasNext()) {
            final Header header = i.nextHeader();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
    }
    
    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }
    
    public HeaderIterator headerIterator(final String name) {
        return this.headergroup.iterator(name);
    }
    
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }
    
    public void setParams(final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.params = params;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.util.Collection;
import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

public class HttpServletResponseWrapper extends ServletResponseWrapper implements HttpServletResponse
{
    public HttpServletResponseWrapper(final HttpServletResponse response) {
        super(response);
    }
    
    private HttpServletResponse _getHttpServletResponse() {
        return (HttpServletResponse)super.getResponse();
    }
    
    public void addCookie(final Cookie cookie) {
        this._getHttpServletResponse().addCookie(cookie);
    }
    
    public boolean containsHeader(final String name) {
        return this._getHttpServletResponse().containsHeader(name);
    }
    
    public String encodeURL(final String url) {
        return this._getHttpServletResponse().encodeURL(url);
    }
    
    public String encodeRedirectURL(final String url) {
        return this._getHttpServletResponse().encodeRedirectURL(url);
    }
    
    public String encodeUrl(final String url) {
        return this._getHttpServletResponse().encodeUrl(url);
    }
    
    public String encodeRedirectUrl(final String url) {
        return this._getHttpServletResponse().encodeRedirectUrl(url);
    }
    
    public void sendError(final int sc, final String msg) throws IOException {
        this._getHttpServletResponse().sendError(sc, msg);
    }
    
    public void sendError(final int sc) throws IOException {
        this._getHttpServletResponse().sendError(sc);
    }
    
    public void sendRedirect(final String location) throws IOException {
        this._getHttpServletResponse().sendRedirect(location);
    }
    
    public void setDateHeader(final String name, final long date) {
        this._getHttpServletResponse().setDateHeader(name, date);
    }
    
    public void addDateHeader(final String name, final long date) {
        this._getHttpServletResponse().addDateHeader(name, date);
    }
    
    public void setHeader(final String name, final String value) {
        this._getHttpServletResponse().setHeader(name, value);
    }
    
    public void addHeader(final String name, final String value) {
        this._getHttpServletResponse().addHeader(name, value);
    }
    
    public void setIntHeader(final String name, final int value) {
        this._getHttpServletResponse().setIntHeader(name, value);
    }
    
    public void addIntHeader(final String name, final int value) {
        this._getHttpServletResponse().addIntHeader(name, value);
    }
    
    public void setStatus(final int sc) {
        this._getHttpServletResponse().setStatus(sc);
    }
    
    public void setStatus(final int sc, final String sm) {
        this._getHttpServletResponse().setStatus(sc, sm);
    }
    
    public int getStatus() {
        return this._getHttpServletResponse().getStatus();
    }
    
    public String getHeader(final String name) {
        return this._getHttpServletResponse().getHeader(name);
    }
    
    public Collection<String> getHeaders(final String name) {
        return this._getHttpServletResponse().getHeaders(name);
    }
    
    public Collection<String> getHeaderNames() {
        return this._getHttpServletResponse().getHeaderNames();
    }
}

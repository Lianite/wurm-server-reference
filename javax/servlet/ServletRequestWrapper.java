// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.Locale;
import java.io.BufferedReader;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

public class ServletRequestWrapper implements ServletRequest
{
    private ServletRequest request;
    
    public ServletRequestWrapper(final ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }
    
    public ServletRequest getRequest() {
        return this.request;
    }
    
    public void setRequest(final ServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        this.request = request;
    }
    
    public Object getAttribute(final String name) {
        return this.request.getAttribute(name);
    }
    
    public Enumeration<String> getAttributeNames() {
        return this.request.getAttributeNames();
    }
    
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }
    
    public void setCharacterEncoding(final String enc) throws UnsupportedEncodingException {
        this.request.setCharacterEncoding(enc);
    }
    
    public int getContentLength() {
        return this.request.getContentLength();
    }
    
    public String getContentType() {
        return this.request.getContentType();
    }
    
    public ServletInputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }
    
    public String getParameter(final String name) {
        return this.request.getParameter(name);
    }
    
    public Map<String, String[]> getParameterMap() {
        return this.request.getParameterMap();
    }
    
    public Enumeration<String> getParameterNames() {
        return this.request.getParameterNames();
    }
    
    public String[] getParameterValues(final String name) {
        return this.request.getParameterValues(name);
    }
    
    public String getProtocol() {
        return this.request.getProtocol();
    }
    
    public String getScheme() {
        return this.request.getScheme();
    }
    
    public String getServerName() {
        return this.request.getServerName();
    }
    
    public int getServerPort() {
        return this.request.getServerPort();
    }
    
    public BufferedReader getReader() throws IOException {
        return this.request.getReader();
    }
    
    public String getRemoteAddr() {
        return this.request.getRemoteAddr();
    }
    
    public String getRemoteHost() {
        return this.request.getRemoteHost();
    }
    
    public void setAttribute(final String name, final Object o) {
        this.request.setAttribute(name, o);
    }
    
    public void removeAttribute(final String name) {
        this.request.removeAttribute(name);
    }
    
    public Locale getLocale() {
        return this.request.getLocale();
    }
    
    public Enumeration<Locale> getLocales() {
        return this.request.getLocales();
    }
    
    public boolean isSecure() {
        return this.request.isSecure();
    }
    
    public RequestDispatcher getRequestDispatcher(final String path) {
        return this.request.getRequestDispatcher(path);
    }
    
    public String getRealPath(final String path) {
        return this.request.getRealPath(path);
    }
    
    public int getRemotePort() {
        return this.request.getRemotePort();
    }
    
    public String getLocalName() {
        return this.request.getLocalName();
    }
    
    public String getLocalAddr() {
        return this.request.getLocalAddr();
    }
    
    public int getLocalPort() {
        return this.request.getLocalPort();
    }
    
    public ServletContext getServletContext() {
        return this.request.getServletContext();
    }
    
    public AsyncContext startAsync() throws IllegalStateException {
        return this.request.startAsync();
    }
    
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
        return this.request.startAsync(servletRequest, servletResponse);
    }
    
    public boolean isAsyncStarted() {
        return this.request.isAsyncStarted();
    }
    
    public boolean isAsyncSupported() {
        return this.request.isAsyncSupported();
    }
    
    public AsyncContext getAsyncContext() {
        return this.request.getAsyncContext();
    }
    
    public boolean isWrapperFor(final ServletRequest wrapped) {
        return this.request == wrapped || (this.request instanceof ServletRequestWrapper && ((ServletRequestWrapper)this.request).isWrapperFor(wrapped));
    }
    
    public boolean isWrapperFor(final Class wrappedType) {
        if (!ServletRequest.class.isAssignableFrom(wrappedType)) {
            throw new IllegalArgumentException("Given class " + wrappedType.getName() + " not a subinterface of " + ServletRequest.class.getName());
        }
        return wrappedType.isAssignableFrom(this.request.getClass()) || (this.request instanceof ServletRequestWrapper && ((ServletRequestWrapper)this.request).isWrapperFor(wrappedType));
    }
    
    public DispatcherType getDispatcherType() {
        return this.request.getDispatcherType();
    }
}

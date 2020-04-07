// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.Locale;
import java.io.PrintWriter;
import java.io.IOException;

public class ServletResponseWrapper implements ServletResponse
{
    private ServletResponse response;
    
    public ServletResponseWrapper(final ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }
    
    public ServletResponse getResponse() {
        return this.response;
    }
    
    public void setResponse(final ServletResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        this.response = response;
    }
    
    public void setCharacterEncoding(final String charset) {
        this.response.setCharacterEncoding(charset);
    }
    
    public String getCharacterEncoding() {
        return this.response.getCharacterEncoding();
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        return this.response.getOutputStream();
    }
    
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }
    
    public void setContentLength(final int len) {
        this.response.setContentLength(len);
    }
    
    public void setContentType(final String type) {
        this.response.setContentType(type);
    }
    
    public String getContentType() {
        return this.response.getContentType();
    }
    
    public void setBufferSize(final int size) {
        this.response.setBufferSize(size);
    }
    
    public int getBufferSize() {
        return this.response.getBufferSize();
    }
    
    public void flushBuffer() throws IOException {
        this.response.flushBuffer();
    }
    
    public boolean isCommitted() {
        return this.response.isCommitted();
    }
    
    public void reset() {
        this.response.reset();
    }
    
    public void resetBuffer() {
        this.response.resetBuffer();
    }
    
    public void setLocale(final Locale loc) {
        this.response.setLocale(loc);
    }
    
    public Locale getLocale() {
        return this.response.getLocale();
    }
    
    public boolean isWrapperFor(final ServletResponse wrapped) {
        return this.response == wrapped || (this.response instanceof ServletResponseWrapper && ((ServletResponseWrapper)this.response).isWrapperFor(wrapped));
    }
    
    public boolean isWrapperFor(final Class wrappedType) {
        if (!ServletResponse.class.isAssignableFrom(wrappedType)) {
            throw new IllegalArgumentException("Given class " + wrappedType.getName() + " not a subinterface of " + ServletResponse.class.getName());
        }
        return wrappedType.isAssignableFrom(this.response.getClass()) || (this.response instanceof ServletResponseWrapper && ((ServletResponseWrapper)this.response).isWrapperFor(wrappedType));
    }
}

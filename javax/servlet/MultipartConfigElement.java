// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import javax.servlet.annotation.MultipartConfig;

public class MultipartConfigElement
{
    private String location;
    private long maxFileSize;
    private long maxRequestSize;
    private int fileSizeThreshold;
    
    public MultipartConfigElement(final String location) {
        if (location == null) {
            this.location = "";
        }
        else {
            this.location = location;
        }
        this.maxFileSize = -1L;
        this.maxRequestSize = -1L;
        this.fileSizeThreshold = 0;
    }
    
    public MultipartConfigElement(final String location, final long maxFileSize, final long maxRequestSize, final int fileSizeThreshold) {
        if (location == null) {
            this.location = "";
        }
        else {
            this.location = location;
        }
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
        this.fileSizeThreshold = fileSizeThreshold;
    }
    
    public MultipartConfigElement(final MultipartConfig annotation) {
        this.location = annotation.location();
        this.fileSizeThreshold = annotation.fileSizeThreshold();
        this.maxFileSize = annotation.maxFileSize();
        this.maxRequestSize = annotation.maxRequestSize();
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public long getMaxFileSize() {
        return this.maxFileSize;
    }
    
    public long getMaxRequestSize() {
        return this.maxRequestSize;
    }
    
    public int getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }
}

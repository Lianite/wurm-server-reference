// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.net;

import java.io.BufferedInputStream;
import java.net.URL;

public interface HttpResponse
{
    URL getRequest();
    
    int getStatusCode();
    
    int getContentLength();
    
    long getLastModified();
    
    String getContentType();
    
    String getResponseHeader(final String p0);
    
    BufferedInputStream getInputStream();
    
    void disconnect();
    
    String getContentEncoding();
}

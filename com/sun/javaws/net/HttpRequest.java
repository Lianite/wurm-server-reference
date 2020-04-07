// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.net;

import java.io.IOException;
import java.net.URL;

public interface HttpRequest
{
    public static final String JNLP_MIME_TYPE = "application/x-java-jnlp-file";
    public static final String ERROR_MIME_TYPE = "application/x-java-jnlp-error";
    public static final String JAR_MIME_TYPE = "application/x-java-archive";
    public static final String PACK200_MIME_TYPE = "application/x-java-pack200";
    public static final String JARDIFF_MIME_TYPE = "application/x-java-archive-diff";
    public static final String GIF_MIME_TYPE = "image/gif";
    public static final String JPEG_MIME_TYPE = "image/jpeg";
    public static final String GZIP_ENCODING = "gzip";
    public static final String PACK200_GZIP_ENCODING = "pack200-gzip";
    public static final String CONTENT_ENCODING = "content-encoding";
    public static final String ACCEPT_ENCODING = "accept-encoding";
    public static final String CONTENT_TYPE = "content-type";
    
    HttpResponse doHeadRequest(final URL p0) throws IOException;
    
    HttpResponse doGetRequest(final URL p0) throws IOException;
    
    HttpResponse doHeadRequest(final URL p0, final boolean p1) throws IOException;
    
    HttpResponse doGetRequest(final URL p0, final boolean p1) throws IOException;
    
    HttpResponse doHeadRequest(final URL p0, final String[] p1, final String[] p2) throws IOException;
    
    HttpResponse doGetRequest(final URL p0, final String[] p1, final String[] p2) throws IOException;
    
    HttpResponse doHeadRequest(final URL p0, final String[] p1, final String[] p2, final boolean p3) throws IOException;
    
    HttpResponse doGetRequest(final URL p0, final String[] p1, final String[] p2, final boolean p3) throws IOException;
}

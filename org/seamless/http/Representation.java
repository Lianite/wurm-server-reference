// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import java.util.Date;
import java.net.URLConnection;
import java.net.URL;
import java.io.Serializable;

public class Representation<E> implements Serializable
{
    private URL url;
    private CacheControl cacheControl;
    private Integer contentLength;
    private String contentType;
    private Long lastModified;
    private String entityTag;
    private E entity;
    
    public Representation(final CacheControl cacheControl, final Integer contentLength, final String contentType, final Long lastModified, final String entityTag, final E entity) {
        this(null, cacheControl, contentLength, contentType, lastModified, entityTag, entity);
    }
    
    public Representation(final URL url, final CacheControl cacheControl, final Integer contentLength, final String contentType, final Long lastModified, final String entityTag, final E entity) {
        this.url = url;
        this.cacheControl = cacheControl;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.lastModified = lastModified;
        this.entityTag = entityTag;
        this.entity = entity;
    }
    
    public Representation(final URLConnection urlConnection, final E entity) {
        this(urlConnection.getURL(), CacheControl.valueOf(urlConnection.getHeaderField("Cache-Control")), urlConnection.getContentLength(), urlConnection.getContentType(), urlConnection.getLastModified(), urlConnection.getHeaderField("Etag"), entity);
    }
    
    public URL getUrl() {
        return this.url;
    }
    
    public CacheControl getCacheControl() {
        return this.cacheControl;
    }
    
    public Integer getContentLength() {
        return (this.contentLength == null || this.contentLength == -1) ? null : this.contentLength;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public Long getLastModified() {
        return (this.lastModified == 0L) ? null : this.lastModified;
    }
    
    public String getEntityTag() {
        return this.entityTag;
    }
    
    public E getEntity() {
        return this.entity;
    }
    
    public Long getMaxAgeOrNull() {
        return (this.getCacheControl() == null || this.getCacheControl().getMaxAge() == -1 || this.getCacheControl().getMaxAge() == 0) ? null : ((long)this.getCacheControl().getMaxAge());
    }
    
    public boolean isExpired(final long storedOn, final long maxAge) {
        return storedOn + maxAge * 1000L < new Date().getTime();
    }
    
    public boolean isExpired(final long storedOn) {
        return this.getMaxAgeOrNull() == null || this.isExpired(storedOn, this.getMaxAgeOrNull());
    }
    
    public boolean isNoStore() {
        return this.getCacheControl() != null && this.getCacheControl().isNoStore();
    }
    
    public boolean isNoCache() {
        return this.getCacheControl() != null && this.getCacheControl().isNoCache();
    }
    
    public boolean mustRevalidate() {
        return this.getCacheControl() != null && this.getCacheControl().isProxyRevalidate();
    }
    
    public boolean hasEntityTagChanged(final String currentEtag) {
        return this.getEntityTag() != null && !this.getEntityTag().equals(currentEtag);
    }
    
    public boolean hasBeenModified(final long currentModificationTime) {
        return this.getLastModified() == null || this.getLastModified() < currentModificationTime;
    }
    
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") CT: " + this.getContentType();
    }
}

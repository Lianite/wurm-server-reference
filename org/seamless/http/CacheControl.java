// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class CacheControl
{
    private int maxAge;
    private int sharedMaxAge;
    private boolean noCache;
    private List<String> noCacheFields;
    private boolean privateFlag;
    private List<String> privateFields;
    private boolean noStore;
    private boolean noTransform;
    private boolean mustRevalidate;
    private boolean proxyRevalidate;
    private Map<String, String> cacheExtensions;
    
    public CacheControl() {
        this.maxAge = -1;
        this.sharedMaxAge = -1;
        this.noCache = false;
        this.noCacheFields = new ArrayList<String>();
        this.privateFlag = false;
        this.privateFields = new ArrayList<String>();
        this.noStore = false;
        this.noTransform = true;
        this.mustRevalidate = false;
        this.proxyRevalidate = false;
        this.cacheExtensions = new HashMap<String, String>();
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public int getSharedMaxAge() {
        return this.sharedMaxAge;
    }
    
    public void setSharedMaxAge(final int sharedMaxAge) {
        this.sharedMaxAge = sharedMaxAge;
    }
    
    public boolean isNoCache() {
        return this.noCache;
    }
    
    public void setNoCache(final boolean noCache) {
        this.noCache = noCache;
    }
    
    public List<String> getNoCacheFields() {
        return this.noCacheFields;
    }
    
    public void setNoCacheFields(final List<String> noCacheFields) {
        this.noCacheFields = noCacheFields;
    }
    
    public boolean isPrivateFlag() {
        return this.privateFlag;
    }
    
    public void setPrivateFlag(final boolean privateFlag) {
        this.privateFlag = privateFlag;
    }
    
    public List<String> getPrivateFields() {
        return this.privateFields;
    }
    
    public void setPrivateFields(final List<String> privateFields) {
        this.privateFields = privateFields;
    }
    
    public boolean isNoStore() {
        return this.noStore;
    }
    
    public void setNoStore(final boolean noStore) {
        this.noStore = noStore;
    }
    
    public boolean isNoTransform() {
        return this.noTransform;
    }
    
    public void setNoTransform(final boolean noTransform) {
        this.noTransform = noTransform;
    }
    
    public boolean isMustRevalidate() {
        return this.mustRevalidate;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
    
    public boolean isProxyRevalidate() {
        return this.proxyRevalidate;
    }
    
    public void setProxyRevalidate(final boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }
    
    public Map<String, String> getCacheExtensions() {
        return this.cacheExtensions;
    }
    
    public void setCacheExtensions(final Map<String, String> cacheExtensions) {
        this.cacheExtensions = cacheExtensions;
    }
    
    public static CacheControl valueOf(final String s) throws IllegalArgumentException {
        if (s == null) {
            return null;
        }
        final CacheControl result = new CacheControl();
        final String[] arr$;
        final String[] directives = arr$ = s.split(",");
        for (String directive : arr$) {
            directive = directive.trim();
            final String[] nameValue = directive.split("=");
            final String name = nameValue[0].trim();
            String value = null;
            if (nameValue.length > 1) {
                value = nameValue[1].trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
            }
            final String lowercase = name.toLowerCase();
            if ("no-cache".equals(lowercase)) {
                result.setNoCache(true);
                if (value != null && !"".equals(value)) {
                    result.getNoCacheFields().add(value);
                }
            }
            else if ("private".equals(lowercase)) {
                result.setPrivateFlag(true);
                if (value != null && !"".equals(value)) {
                    result.getPrivateFields().add(value);
                }
            }
            else if ("no-store".equals(lowercase)) {
                result.setNoStore(true);
            }
            else if ("max-age".equals(lowercase)) {
                if (value == null) {
                    throw new IllegalArgumentException("CacheControl max-age header does not have a value: " + value);
                }
                result.setMaxAge(Integer.valueOf(value));
            }
            else if ("s-maxage".equals(lowercase)) {
                if (value == null) {
                    throw new IllegalArgumentException("CacheControl s-maxage header does not have a value: " + value);
                }
                result.setSharedMaxAge(Integer.valueOf(value));
            }
            else if ("no-transform".equals(lowercase)) {
                result.setNoTransform(true);
            }
            else if ("must-revalidate".equals(lowercase)) {
                result.setMustRevalidate(true);
            }
            else if ("proxy-revalidate".equals(lowercase)) {
                result.setProxyRevalidate(true);
            }
            else if (!"public".equals(lowercase)) {
                if (value == null) {
                    value = "";
                }
                result.getCacheExtensions().put(name, value);
            }
        }
        return result;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (!this.isPrivateFlag()) {
            sb.append("public");
        }
        if (this.isMustRevalidate()) {
            this.append("must-revalidate", sb);
        }
        if (this.isNoTransform()) {
            this.append("no-transform", sb);
        }
        if (this.isNoStore()) {
            this.append("no-store", sb);
        }
        if (this.isProxyRevalidate()) {
            this.append("proxy-revalidate", sb);
        }
        if (this.getSharedMaxAge() > -1) {
            this.append("s-maxage", sb).append("=").append(this.getSharedMaxAge());
        }
        if (this.getMaxAge() > -1) {
            this.append("max-age", sb).append("=").append(this.getMaxAge());
        }
        if (this.isNoCache()) {
            final List<String> fields = this.getNoCacheFields();
            if (fields.size() < 1) {
                this.append("no-cache", sb);
            }
            else {
                for (final String field : this.getNoCacheFields()) {
                    this.append("no-cache", sb).append("=\"").append(field).append("\"");
                }
            }
        }
        if (this.isPrivateFlag()) {
            final List<String> fields = this.getPrivateFields();
            if (fields.size() < 1) {
                this.append("private", sb);
            }
            else {
                for (final String field : this.getPrivateFields()) {
                    this.append("private", sb).append("=\"").append(field).append("\"");
                }
            }
        }
        for (final String key : this.getCacheExtensions().keySet()) {
            final String val = this.getCacheExtensions().get(key);
            this.append(key, sb);
            if (val != null && !"".equals(val)) {
                sb.append("=\"").append(val).append("\"");
            }
        }
        return sb.toString();
    }
    
    private StringBuilder append(final String s, final StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(s);
        return sb;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CacheControl that = (CacheControl)o;
        return this.maxAge == that.maxAge && this.mustRevalidate == that.mustRevalidate && this.noCache == that.noCache && this.noStore == that.noStore && this.noTransform == that.noTransform && this.privateFlag == that.privateFlag && this.proxyRevalidate == that.proxyRevalidate && this.sharedMaxAge == that.sharedMaxAge && this.cacheExtensions.equals(that.cacheExtensions) && this.noCacheFields.equals(that.noCacheFields) && this.privateFields.equals(that.privateFields);
    }
    
    public int hashCode() {
        int result = this.maxAge;
        result = 31 * result + this.sharedMaxAge;
        result = 31 * result + (this.noCache ? 1 : 0);
        result = 31 * result + this.noCacheFields.hashCode();
        result = 31 * result + (this.privateFlag ? 1 : 0);
        result = 31 * result + this.privateFields.hashCode();
        result = 31 * result + (this.noStore ? 1 : 0);
        result = 31 * result + (this.noTransform ? 1 : 0);
        result = 31 * result + (this.mustRevalidate ? 1 : 0);
        result = 31 * result + (this.proxyRevalidate ? 1 : 0);
        result = 31 * result + this.cacheExtensions.hashCode();
        return result;
    }
}

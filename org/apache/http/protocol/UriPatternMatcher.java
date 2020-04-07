// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.util.Iterator;
import java.util.HashMap;
import org.apache.http.annotation.GuardedBy;
import java.util.Map;
import org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public class UriPatternMatcher<T>
{
    @GuardedBy("this")
    private final Map<String, T> map;
    
    public UriPatternMatcher() {
        this.map = new HashMap<String, T>();
    }
    
    public synchronized void register(final String pattern, final T obj) {
        if (pattern == null) {
            throw new IllegalArgumentException("URI request pattern may not be null");
        }
        this.map.put(pattern, obj);
    }
    
    public synchronized void unregister(final String pattern) {
        if (pattern == null) {
            return;
        }
        this.map.remove(pattern);
    }
    
    @Deprecated
    public synchronized void setHandlers(final Map<String, T> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map of handlers may not be null");
        }
        this.map.clear();
        this.map.putAll((Map<? extends String, ? extends T>)map);
    }
    
    public synchronized void setObjects(final Map<String, T> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map of handlers may not be null");
        }
        this.map.clear();
        this.map.putAll((Map<? extends String, ? extends T>)map);
    }
    
    public synchronized Map<String, T> getObjects() {
        return this.map;
    }
    
    public synchronized T lookup(String requestURI) {
        if (requestURI == null) {
            throw new IllegalArgumentException("Request URI may not be null");
        }
        final int index = requestURI.indexOf("?");
        if (index != -1) {
            requestURI = requestURI.substring(0, index);
        }
        T obj = this.map.get(requestURI);
        if (obj == null) {
            String bestMatch = null;
            for (final String pattern : this.map.keySet()) {
                if (this.matchUriRequestPattern(pattern, requestURI) && (bestMatch == null || bestMatch.length() < pattern.length() || (bestMatch.length() == pattern.length() && pattern.endsWith("*")))) {
                    obj = this.map.get(pattern);
                    bestMatch = pattern;
                }
            }
        }
        return obj;
    }
    
    protected boolean matchUriRequestPattern(final String pattern, final String requestUri) {
        return pattern.equals("*") || (pattern.endsWith("*") && requestUri.startsWith(pattern.substring(0, pattern.length() - 1))) || (pattern.startsWith("*") && requestUri.endsWith(pattern.substring(1, pattern.length())));
    }
}

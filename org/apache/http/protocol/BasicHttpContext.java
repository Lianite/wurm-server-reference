// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class BasicHttpContext implements HttpContext
{
    private final HttpContext parentContext;
    private Map<String, Object> map;
    
    public BasicHttpContext() {
        this(null);
    }
    
    public BasicHttpContext(final HttpContext parentContext) {
        this.map = null;
        this.parentContext = parentContext;
    }
    
    public Object getAttribute(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        Object obj = null;
        if (this.map != null) {
            obj = this.map.get(id);
        }
        if (obj == null && this.parentContext != null) {
            obj = this.parentContext.getAttribute(id);
        }
        return obj;
    }
    
    public void setAttribute(final String id, final Object obj) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (this.map == null) {
            this.map = new HashMap<String, Object>();
        }
        this.map.put(id, obj);
    }
    
    public Object removeAttribute(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (this.map != null) {
            return this.map.remove(id);
        }
        return null;
    }
    
    public void clear() {
        if (this.map != null) {
            this.map.clear();
        }
    }
    
    public String toString() {
        if (this.map != null) {
            return this.map.toString();
        }
        return "{}";
    }
}

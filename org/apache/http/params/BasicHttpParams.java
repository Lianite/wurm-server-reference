// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.params;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.http.annotation.NotThreadSafe;
import java.io.Serializable;

@NotThreadSafe
public class BasicHttpParams extends AbstractHttpParams implements Serializable, Cloneable
{
    private static final long serialVersionUID = -7086398485908701455L;
    private final HashMap<String, Object> parameters;
    
    public BasicHttpParams() {
        this.parameters = new HashMap<String, Object>();
    }
    
    public Object getParameter(final String name) {
        return this.parameters.get(name);
    }
    
    public HttpParams setParameter(final String name, final Object value) {
        this.parameters.put(name, value);
        return this;
    }
    
    public boolean removeParameter(final String name) {
        if (this.parameters.containsKey(name)) {
            this.parameters.remove(name);
            return true;
        }
        return false;
    }
    
    public void setParameters(final String[] names, final Object value) {
        for (int i = 0; i < names.length; ++i) {
            this.setParameter(names[i], value);
        }
    }
    
    public boolean isParameterSet(final String name) {
        return this.getParameter(name) != null;
    }
    
    public boolean isParameterSetLocally(final String name) {
        return this.parameters.get(name) != null;
    }
    
    public void clear() {
        this.parameters.clear();
    }
    
    @Deprecated
    public HttpParams copy() {
        try {
            return (HttpParams)this.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException("Cloning not supported");
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BasicHttpParams clone = (BasicHttpParams)super.clone();
        this.copyParams(clone);
        return clone;
    }
    
    public void copyParams(final HttpParams target) {
        for (final Map.Entry<String, Object> me : this.parameters.entrySet()) {
            if (me.getKey() instanceof String) {
                target.setParameter(me.getKey(), me.getValue());
            }
        }
    }
    
    public Set<String> getNames() {
        return new HashSet<String>(this.parameters.keySet());
    }
}

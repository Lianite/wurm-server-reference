// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.support.shared.AbstractMap;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Map;

public abstract class EventedValue<V>
{
    protected final V value;
    
    public EventedValue(final V value) {
        this.value = value;
    }
    
    public EventedValue(final Map.Entry<String, String>[] attributes) {
        try {
            this.value = this.valueOf(attributes);
        }
        catch (InvalidValueException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public String getName() {
        return this.getClass().getSimpleName();
    }
    
    public V getValue() {
        return this.value;
    }
    
    public Map.Entry<String, String>[] getAttributes() {
        return (Map.Entry<String, String>[])new Map.Entry[] { new AbstractMap.SimpleEntry("val", this.toString()) };
    }
    
    protected V valueOf(final Map.Entry<String, String>[] attributes) throws InvalidValueException {
        V v = null;
        for (final Map.Entry<String, String> attribute : attributes) {
            if (attribute.getKey().equals("val")) {
                v = this.valueOf(attribute.getValue());
            }
        }
        return v;
    }
    
    protected V valueOf(final String s) throws InvalidValueException {
        return this.getDatatype().valueOf(s);
    }
    
    @Override
    public String toString() {
        return this.getDatatype().getString(this.getValue());
    }
    
    protected abstract Datatype getDatatype();
}

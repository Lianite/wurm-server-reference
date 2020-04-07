// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractDatatype<V> implements Datatype<V>
{
    private Builtin builtin;
    
    protected Class<V> getValueType() {
        return (Class<V>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    @Override
    public boolean isHandlingJavaType(final Class type) {
        return this.getValueType().isAssignableFrom(type);
    }
    
    @Override
    public V valueOf(final String s) throws InvalidValueException {
        return null;
    }
    
    @Override
    public Builtin getBuiltin() {
        return this.builtin;
    }
    
    public void setBuiltin(final Builtin builtin) {
        this.builtin = builtin;
    }
    
    @Override
    public String getString(final V value) throws InvalidValueException {
        if (value == null) {
            return "";
        }
        if (!this.isValid(value)) {
            throw new InvalidValueException("Value is not valid: " + value);
        }
        return value.toString();
    }
    
    @Override
    public boolean isValid(final V value) {
        return value == null || this.getValueType().isAssignableFrom(value.getClass());
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
    
    @Override
    public String getDisplayString() {
        if (this instanceof CustomDatatype) {
            return ((CustomDatatype)this).getName();
        }
        if (this.getBuiltin() != null) {
            return this.getBuiltin().getDescriptorName();
        }
        return this.getValueType().getSimpleName();
    }
}

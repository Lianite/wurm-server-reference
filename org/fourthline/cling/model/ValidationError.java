// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

public class ValidationError
{
    private Class clazz;
    private String propertyName;
    private String message;
    
    public ValidationError(final Class clazz, final String message) {
        this.clazz = clazz;
        this.message = message;
    }
    
    public ValidationError(final Class clazz, final String propertyName, final String message) {
        this.clazz = clazz;
        this.propertyName = propertyName;
        this.message = message;
    }
    
    public Class getClazz() {
        return this.clazz;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " (Class: " + this.getClazz().getSimpleName() + ", propertyName: " + this.getPropertyName() + "): " + this.message;
    }
}

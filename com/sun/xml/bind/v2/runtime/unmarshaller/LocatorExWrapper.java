// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.bind.ValidationEventLocator;
import org.xml.sax.Locator;

class LocatorExWrapper implements LocatorEx
{
    private final Locator locator;
    
    public LocatorExWrapper(final Locator locator) {
        this.locator = locator;
    }
    
    public ValidationEventLocator getLocation() {
        return new ValidationEventLocatorImpl(this.locator);
    }
    
    public String getPublicId() {
        return this.locator.getPublicId();
    }
    
    public String getSystemId() {
        return this.locator.getSystemId();
    }
    
    public int getLineNumber() {
        return this.locator.getLineNumber();
    }
    
    public int getColumnNumber() {
        return this.locator.getColumnNumber();
    }
}

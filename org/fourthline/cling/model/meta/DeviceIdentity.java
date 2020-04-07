// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.Validatable;

public class DeviceIdentity implements Validatable
{
    private final UDN udn;
    private final Integer maxAgeSeconds;
    
    public DeviceIdentity(final UDN udn, final DeviceIdentity template) {
        this.udn = udn;
        this.maxAgeSeconds = template.getMaxAgeSeconds();
    }
    
    public DeviceIdentity(final UDN udn) {
        this.udn = udn;
        this.maxAgeSeconds = 1800;
    }
    
    public DeviceIdentity(final UDN udn, final Integer maxAgeSeconds) {
        this.udn = udn;
        this.maxAgeSeconds = maxAgeSeconds;
    }
    
    public UDN getUdn() {
        return this.udn;
    }
    
    public Integer getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DeviceIdentity that = (DeviceIdentity)o;
        return this.udn.equals(that.udn);
    }
    
    @Override
    public int hashCode() {
        return this.udn.hashCode();
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") UDN: " + this.getUdn();
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getUdn() == null) {
            errors.add(new ValidationError(this.getClass(), "major", "Device has no UDN"));
        }
        return errors;
    }
}

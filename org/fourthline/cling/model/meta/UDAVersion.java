// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.Validatable;

public class UDAVersion implements Validatable
{
    private int major;
    private int minor;
    
    public UDAVersion() {
        this.major = 1;
        this.minor = 0;
    }
    
    public UDAVersion(final int major, final int minor) {
        this.major = 1;
        this.minor = 0;
        this.major = major;
        this.minor = minor;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getMajor() != 1) {
            errors.add(new ValidationError(this.getClass(), "major", "UDA major spec version must be 1"));
        }
        if (this.getMajor() < 0) {
            errors.add(new ValidationError(this.getClass(), "minor", "UDA minor spec version must be equal or greater 0"));
        }
        return errors;
    }
}

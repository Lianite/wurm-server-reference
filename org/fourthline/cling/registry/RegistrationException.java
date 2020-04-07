// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.ValidationError;
import java.util.List;

public class RegistrationException extends RuntimeException
{
    public List<ValidationError> errors;
    
    public RegistrationException(final String s) {
        super(s);
    }
    
    public RegistrationException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public RegistrationException(final String s, final List<ValidationError> errors) {
        super(s);
        this.errors = errors;
    }
    
    public List<ValidationError> getErrors() {
        return this.errors;
    }
}

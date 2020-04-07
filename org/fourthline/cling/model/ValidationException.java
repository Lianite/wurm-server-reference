// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import java.util.List;

public class ValidationException extends Exception
{
    public List<ValidationError> errors;
    
    public ValidationException(final String s) {
        super(s);
    }
    
    public ValidationException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public ValidationException(final String s, final List<ValidationError> errors) {
        super(s);
        this.errors = errors;
    }
    
    public List<ValidationError> getErrors() {
        return this.errors;
    }
}

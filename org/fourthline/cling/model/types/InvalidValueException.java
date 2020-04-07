// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class InvalidValueException extends RuntimeException
{
    public InvalidValueException(final String s) {
        super(s);
    }
    
    public InvalidValueException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}

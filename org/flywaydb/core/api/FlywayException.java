// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api;

public class FlywayException extends RuntimeException
{
    public FlywayException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public FlywayException(final Throwable cause) {
        super(cause);
    }
    
    public FlywayException(final String message) {
        super(message);
    }
    
    public FlywayException() {
    }
}

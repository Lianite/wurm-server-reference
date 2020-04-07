// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn;

import org.apache.http.annotation.Immutable;
import java.io.InterruptedIOException;

@Immutable
public class ConnectTimeoutException extends InterruptedIOException
{
    private static final long serialVersionUID = -4816682903149535989L;
    
    public ConnectTimeoutException() {
    }
    
    public ConnectTimeoutException(final String message) {
        super(message);
    }
    
    public ConnectTimeoutException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

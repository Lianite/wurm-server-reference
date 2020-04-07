// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport;

public class RouterException extends Exception
{
    public RouterException() {
    }
    
    public RouterException(final String s) {
        super(s);
    }
    
    public RouterException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public RouterException(final Throwable throwable) {
        super(throwable);
    }
}

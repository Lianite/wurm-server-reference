// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.statemachine;

public class TransitionException extends RuntimeException
{
    public TransitionException(final String s) {
        super(s);
    }
    
    public TransitionException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}

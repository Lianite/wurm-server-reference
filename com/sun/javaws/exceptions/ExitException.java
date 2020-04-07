// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.exceptions;

public class ExitException extends Exception
{
    private int _reason;
    private Exception _exception;
    public static final int OK = 0;
    public static final int REBOOT = 1;
    public static final int LAUNCH_ERROR = 2;
    
    public ExitException(final Exception exception, final int reason) {
        this._exception = exception;
        this._reason = reason;
    }
    
    public Exception getException() {
        return this._exception;
    }
    
    public int getReason() {
        return this._reason;
    }
    
    public String toString() {
        String s = "ExitException[ " + this.getReason() + "]";
        if (this._exception != null) {
            s += this._exception.toString();
        }
        return s;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.builder;

public class BuildException extends RuntimeException
{
    private final Throwable cause;
    
    public BuildException(final Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("null cause");
        }
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}

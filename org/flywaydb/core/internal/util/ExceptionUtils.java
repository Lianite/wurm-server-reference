// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

public class ExceptionUtils
{
    public static Throwable getRootCause(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable cause;
        Throwable rootCause;
        for (cause = throwable; (rootCause = cause.getCause()) != null; cause = rootCause) {}
        return cause;
    }
}

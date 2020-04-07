// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging;

public interface Log
{
    void debug(final String p0);
    
    void info(final String p0);
    
    void warn(final String p0);
    
    void error(final String p0);
    
    void error(final String p0, final Exception p1);
}

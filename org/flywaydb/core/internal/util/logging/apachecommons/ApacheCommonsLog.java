// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.apachecommons;

import org.flywaydb.core.internal.util.logging.Log;

public class ApacheCommonsLog implements Log
{
    private final org.apache.commons.logging.Log logger;
    
    public ApacheCommonsLog(final org.apache.commons.logging.Log logger) {
        this.logger = logger;
    }
    
    @Override
    public void debug(final String message) {
        this.logger.debug(message);
    }
    
    @Override
    public void info(final String message) {
        this.logger.info(message);
    }
    
    @Override
    public void warn(final String message) {
        this.logger.warn(message);
    }
    
    @Override
    public void error(final String message) {
        this.logger.error(message);
    }
    
    @Override
    public void error(final String message, final Exception e) {
        this.logger.error(message, e);
    }
}

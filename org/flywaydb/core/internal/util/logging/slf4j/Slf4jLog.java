// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.slf4j;

import org.slf4j.Logger;
import org.flywaydb.core.internal.util.logging.Log;

public class Slf4jLog implements Log
{
    private final Logger logger;
    
    public Slf4jLog(final Logger logger) {
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
        this.logger.error(message, (Throwable)e);
    }
}

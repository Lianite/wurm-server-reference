// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc.log;

import org.apache.commons.logging.LogFactory;

public class CommonsLogger implements Log
{
    private org.apache.commons.logging.Log logger;
    
    public CommonsLogger(final String instanceName) {
        this.logger = LogFactory.getLog(instanceName);
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }
    
    public boolean isFatalEnabled() {
        return this.logger.isFatalEnabled();
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }
    
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }
    
    public void logDebug(final Object msg) {
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logDebug(final Object msg, final Throwable thrown) {
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logError(final Object msg) {
        this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logError(final Object msg, final Throwable thrown) {
        this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logFatal(final Object msg) {
        this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logFatal(final Object msg, final Throwable thrown) {
        this.logger.fatal(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logInfo(final Object msg) {
        this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logInfo(final Object msg, final Throwable thrown) {
        this.logger.info(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logTrace(final Object msg) {
        this.logger.trace(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logTrace(final Object msg, final Throwable thrown) {
        this.logger.trace(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logWarn(final Object msg) {
        this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logWarn(final Object msg, final Throwable thrown) {
        this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
}

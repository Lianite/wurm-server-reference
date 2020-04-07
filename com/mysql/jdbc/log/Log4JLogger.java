// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc.log;

import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4JLogger implements Log
{
    private Logger logger;
    
    public Log4JLogger(final String instanceName) {
        this.logger = Logger.getLogger(instanceName);
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor((Priority)Level.ERROR);
    }
    
    public boolean isFatalEnabled() {
        return this.logger.isEnabledFor((Priority)Level.FATAL);
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public boolean isTraceEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor((Priority)Level.WARN);
    }
    
    public void logDebug(final Object msg) {
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(LogUtils.expandProfilerEventIfNecessary(msg)));
    }
    
    public void logDebug(final Object msg, final Throwable thrown) {
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logError(final Object msg) {
        this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logError(final Object msg, final Throwable thrown) {
        this.logger.error(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
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
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logTrace(final Object msg, final Throwable thrown) {
        this.logger.debug(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
    
    public void logWarn(final Object msg) {
        this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg));
    }
    
    public void logWarn(final Object msg, final Throwable thrown) {
        this.logger.warn(LogUtils.expandProfilerEventIfNecessary(msg), thrown);
    }
}

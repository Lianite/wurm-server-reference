// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.logging.Log;

public final class FeatureDetector
{
    private static final Log LOG;
    private ClassLoader classLoader;
    private Boolean apacheCommonsLoggingAvailable;
    private Boolean slf4jAvailable;
    private Boolean springJdbcAvailable;
    private Boolean jbossVFSv2Available;
    private Boolean jbossVFSv3Available;
    private Boolean osgiFrameworkAvailable;
    private Boolean androidAvailable;
    
    public FeatureDetector(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public boolean isApacheCommonsLoggingAvailable() {
        if (this.apacheCommonsLoggingAvailable == null) {
            this.apacheCommonsLoggingAvailable = ClassUtils.isPresent("org.apache.commons.logging.Log", this.classLoader);
        }
        return this.apacheCommonsLoggingAvailable;
    }
    
    public boolean isSlf4jAvailable() {
        if (this.slf4jAvailable == null) {
            this.slf4jAvailable = ClassUtils.isPresent("org.slf4j.Logger", this.classLoader);
        }
        return this.slf4jAvailable;
    }
    
    public boolean isSpringJdbcAvailable() {
        if (this.springJdbcAvailable == null) {
            this.springJdbcAvailable = ClassUtils.isPresent("org.springframework.jdbc.core.JdbcTemplate", this.classLoader);
            FeatureDetector.LOG.debug("Spring Jdbc available: " + this.springJdbcAvailable);
        }
        return this.springJdbcAvailable;
    }
    
    public boolean isJBossVFSv2Available() {
        if (this.jbossVFSv2Available == null) {
            this.jbossVFSv2Available = ClassUtils.isPresent("org.jboss.virtual.VFS", this.classLoader);
            FeatureDetector.LOG.debug("JBoss VFS v2 available: " + this.jbossVFSv2Available);
        }
        return this.jbossVFSv2Available;
    }
    
    public boolean isJBossVFSv3Available() {
        if (this.jbossVFSv3Available == null) {
            this.jbossVFSv3Available = ClassUtils.isPresent("org.jboss.vfs.VFS", this.classLoader);
            FeatureDetector.LOG.debug("JBoss VFS v3 available: " + this.jbossVFSv3Available);
        }
        return this.jbossVFSv3Available;
    }
    
    public boolean isOsgiFrameworkAvailable() {
        if (this.osgiFrameworkAvailable == null) {
            this.osgiFrameworkAvailable = ClassUtils.isPresent("org.osgi.framework.Bundle", FeatureDetector.class.getClassLoader());
            FeatureDetector.LOG.debug("OSGi framework available: " + this.osgiFrameworkAvailable);
        }
        return this.osgiFrameworkAvailable;
    }
    
    public boolean isAndroidAvailable() {
        if (this.androidAvailable == null) {
            this.androidAvailable = "Android Runtime".equals(System.getProperty("java.runtime.name"));
        }
        return this.androidAvailable;
    }
    
    static {
        LOG = LogFactory.getLog(FeatureDetector.class);
    }
}

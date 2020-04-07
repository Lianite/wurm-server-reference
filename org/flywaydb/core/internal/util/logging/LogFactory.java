// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging;

import org.flywaydb.core.internal.util.logging.javautil.JavaUtilLogCreator;
import org.flywaydb.core.internal.util.logging.apachecommons.ApacheCommonsLogCreator;
import org.flywaydb.core.internal.util.logging.slf4j.Slf4jLogCreator;
import org.flywaydb.core.internal.util.logging.android.AndroidLogCreator;
import org.flywaydb.core.internal.util.FeatureDetector;

public class LogFactory
{
    private static LogCreator logCreator;
    private static LogCreator fallbackLogCreator;
    
    public static void setLogCreator(final LogCreator logCreator) {
        LogFactory.logCreator = logCreator;
    }
    
    public static void setFallbackLogCreator(final LogCreator fallbackLogCreator) {
        LogFactory.fallbackLogCreator = fallbackLogCreator;
    }
    
    public static Log getLog(final Class<?> clazz) {
        if (LogFactory.logCreator == null) {
            final FeatureDetector featureDetector = new FeatureDetector(Thread.currentThread().getContextClassLoader());
            if (featureDetector.isAndroidAvailable()) {
                LogFactory.logCreator = new AndroidLogCreator();
            }
            else if (featureDetector.isSlf4jAvailable()) {
                LogFactory.logCreator = new Slf4jLogCreator();
            }
            else if (featureDetector.isApacheCommonsLoggingAvailable()) {
                LogFactory.logCreator = new ApacheCommonsLogCreator();
            }
            else if (LogFactory.fallbackLogCreator == null) {
                LogFactory.logCreator = new JavaUtilLogCreator();
            }
            else {
                LogFactory.logCreator = LogFactory.fallbackLogCreator;
            }
        }
        return LogFactory.logCreator.createLogger(clazz);
    }
}

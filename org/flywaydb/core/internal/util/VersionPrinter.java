// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;
import org.flywaydb.core.internal.util.logging.Log;

public class VersionPrinter
{
    private static final Log LOG;
    private static boolean printed;
    
    public static void printVersion() {
        if (VersionPrinter.printed) {
            return;
        }
        VersionPrinter.printed = true;
        final String version = new ClassPathResource("org/flywaydb/core/internal/version.txt", VersionPrinter.class.getClassLoader()).loadAsString("UTF-8");
        VersionPrinter.LOG.info("Flyway " + version + " by Boxfuse");
    }
    
    static {
        LOG = LogFactory.getLog(VersionPrinter.class);
    }
}

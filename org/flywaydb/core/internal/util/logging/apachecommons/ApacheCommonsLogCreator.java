// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.apachecommons;

import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogCreator;

public class ApacheCommonsLogCreator implements LogCreator
{
    @Override
    public Log createLogger(final Class<?> clazz) {
        return new ApacheCommonsLog(LogFactory.getLog(clazz));
    }
}

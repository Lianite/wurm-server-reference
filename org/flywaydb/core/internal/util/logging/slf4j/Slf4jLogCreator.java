// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.slf4j;

import org.slf4j.LoggerFactory;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogCreator;

public class Slf4jLogCreator implements LogCreator
{
    @Override
    public Log createLogger(final Class<?> clazz) {
        return new Slf4jLog(LoggerFactory.getLogger((Class)clazz));
    }
}

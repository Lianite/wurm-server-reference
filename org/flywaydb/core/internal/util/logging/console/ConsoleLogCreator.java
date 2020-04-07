// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.console;

import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogCreator;

public class ConsoleLogCreator implements LogCreator
{
    private final ConsoleLog.Level level;
    
    public ConsoleLogCreator(final ConsoleLog.Level level) {
        this.level = level;
    }
    
    @Override
    public Log createLogger(final Class<?> clazz) {
        return new ConsoleLog(this.level);
    }
}

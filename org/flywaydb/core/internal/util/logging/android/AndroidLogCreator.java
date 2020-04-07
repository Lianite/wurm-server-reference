// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.android;

import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogCreator;

public class AndroidLogCreator implements LogCreator
{
    @Override
    public Log createLogger(final Class<?> clazz) {
        return new AndroidLog();
    }
}

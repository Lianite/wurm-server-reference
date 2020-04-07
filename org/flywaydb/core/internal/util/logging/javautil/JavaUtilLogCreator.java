// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.logging.javautil;

import java.util.logging.Logger;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogCreator;

public class JavaUtilLogCreator implements LogCreator
{
    @Override
    public Log createLogger(final Class<?> clazz) {
        return new JavaUtilLog(Logger.getLogger(clazz.getName()));
    }
}

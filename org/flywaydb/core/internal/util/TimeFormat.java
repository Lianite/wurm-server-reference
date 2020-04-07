// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

public class TimeFormat
{
    public static String format(final long millis) {
        return String.format("%02d:%02d.%03ds", millis / 60000L, millis % 60000L / 1000L, millis % 1000L);
    }
}

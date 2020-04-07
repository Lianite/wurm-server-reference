// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils
{
    public static String formatDateAsIsoString(final Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}

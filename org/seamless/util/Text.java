// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.util.Date;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.math.BigDecimal;

public class Text
{
    public static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
    
    public static String ltrim(final String s) {
        return s.replaceAll("(?s)^\\s+", "");
    }
    
    public static String rtrim(final String s) {
        return s.replaceAll("(?s)\\s+$", "");
    }
    
    public static String displayFilesize(final long fileSizeInBytes) {
        if (fileSizeInBytes >= 1073741824L) {
            return new BigDecimal(fileSizeInBytes / 1024L / 1024L / 1024L) + " GiB";
        }
        if (fileSizeInBytes >= 1048576L) {
            return new BigDecimal(fileSizeInBytes / 1024L / 1024L) + " MiB";
        }
        if (fileSizeInBytes >= 1024L) {
            return new BigDecimal(fileSizeInBytes / 1024L) + " KiB";
        }
        return new BigDecimal(fileSizeInBytes) + " bytes";
    }
    
    public static Calendar fromISO8601String(final TimeZone targetTimeZone, final String s) {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        format.setTimeZone(targetTimeZone);
        try {
            final Calendar cal = new GregorianCalendar();
            cal.setTime(format.parse(s));
            return cal;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String toISO8601String(final TimeZone targetTimeZone, final Date datetime) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(datetime);
        return toISO8601String(targetTimeZone, cal);
    }
    
    public static String toISO8601String(final TimeZone targetTimeZone, final long unixTime) {
        final Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(unixTime);
        return toISO8601String(targetTimeZone, cal);
    }
    
    public static String toISO8601String(final TimeZone targetTimeZone, final Calendar cal) {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        format.setTimeZone(targetTimeZone);
        try {
            return format.format(cal.getTime());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

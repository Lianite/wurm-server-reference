// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.util.IllegalFormatException;
import java.util.logging.Level;
import java.util.Locale;
import java.util.logging.Logger;

public class StringUtil
{
    private static final Logger logger;
    private static Locale locale;
    
    public static String format(final String format, final Object... args) {
        try {
            return String.format(StringUtil.locale, format, args);
        }
        catch (IllegalFormatException ife) {
            StringUtil.logger.log(Level.WARNING, format, ife);
            return "";
        }
    }
    
    public static String toLowerCase(final String original) {
        return original.toLowerCase(StringUtil.locale);
    }
    
    public static String toLowerCase(final Object obj) {
        if (obj == null) {
            return "";
        }
        return toLowerCase(obj.toString());
    }
    
    public static String toUpperCase(final String original) {
        return original.toUpperCase(StringUtil.locale);
    }
    
    public static String toUpperCase(final Object obj) {
        if (obj == null) {
            return "";
        }
        return toUpperCase(obj.toString());
    }
    
    static {
        logger = Logger.getLogger(StringUtil.class.getName());
        StringUtil.locale = Locale.ENGLISH;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
    public static String trimOrPad(final String str, final int length) {
        return trimOrPad(str, length, ' ');
    }
    
    public static String trimOrPad(final String str, final int length, final char padChar) {
        String result;
        if (str == null) {
            result = "";
        }
        else {
            result = str;
        }
        if (result.length() > length) {
            return result.substring(0, length);
        }
        while (result.length() < length) {
            result += padChar;
        }
        return result;
    }
    
    public static boolean isNumeric(final String str) {
        return str != null && str.matches("\\d*");
    }
    
    public static String collapseWhitespace(final String str) {
        return str.replaceAll("\\s+", " ");
    }
    
    public static String left(final String str, final int count) {
        if (str == null) {
            return null;
        }
        if (str.length() < count) {
            return str;
        }
        return str.substring(0, count);
    }
    
    public static String replaceAll(final String str, final String originalToken, final String replacementToken) {
        return str.replaceAll(Pattern.quote(originalToken), Matcher.quoteReplacement(replacementToken));
    }
    
    public static boolean hasLength(final String str) {
        return str != null && str.length() > 0;
    }
    
    public static String arrayToCommaDelimitedString(final Object[] strings) {
        if (strings == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(String.valueOf(strings[i]));
        }
        return builder.toString();
    }
    
    public static boolean hasText(final String s) {
        return s != null && s.trim().length() > 0;
    }
    
    public static String[] tokenizeToStringArray(final String str, final String delimiters) {
        if (str == null) {
            return null;
        }
        final String[] tokens = str.split("[" + delimiters + "]");
        for (int i = 0; i < tokens.length; ++i) {
            tokens[i] = tokens[i].trim();
        }
        return tokens;
    }
    
    public static int countOccurrencesOf(final String str, final String token) {
        if (str == null || token == null || str.length() == 0 || token.length() == 0) {
            return 0;
        }
        int count = 0;
        int idx;
        for (int pos = 0; (idx = str.indexOf(token, pos)) != -1; pos = idx + token.length()) {
            ++count;
        }
        return count;
    }
    
    public static String replace(final String inString, final String oldPattern, final String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        int index = inString.indexOf(oldPattern);
        final int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        return sb.toString();
    }
    
    public static String collectionToCommaDelimitedString(final Collection<?> collection) {
        return collectionToDelimitedString(collection, ", ");
    }
    
    public static String collectionToDelimitedString(final Collection<?> collection, final String delimiter) {
        if (collection == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator it = collection.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
    
    public static String trimLeadingWhitespace(final String str) {
        if (!hasLength(str)) {
            return str;
        }
        final StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }
    
    public static String trimTrailingWhitespace(final String str) {
        if (!hasLength(str)) {
            return str;
        }
        final StringBuilder buf = new StringBuilder(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }
}

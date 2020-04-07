// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import java.awt.Frame;
import java.util.StringTokenizer;
import java.util.Locale;
import java.util.ArrayList;

public class GeneralUtil
{
    public static boolean prefixMatchStringList(final String[] array, final String s) {
        if (array == null) {
            return true;
        }
        if (s == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (s.startsWith(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static String[] getStringList(final String s) {
        if (s == null) {
            return null;
        }
        final ArrayList list = new ArrayList<String>();
        int i = 0;
        final int length = s.length();
        StringBuffer sb = null;
        while (i < length) {
            final char char1 = s.charAt(i);
            if (char1 == ' ') {
                if (sb != null) {
                    list.add(sb.toString());
                    sb = null;
                }
            }
            else if (char1 == '\\') {
                if (i + 1 < length) {
                    final char char2 = s.charAt(++i);
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    sb.append(char2);
                }
            }
            else {
                if (sb == null) {
                    sb = new StringBuffer();
                }
                sb.append(char1);
            }
            ++i;
        }
        if (sb != null) {
            list.add(sb.toString());
        }
        if (list.size() == 0) {
            return null;
        }
        return list.toArray(new String[list.size()]);
    }
    
    public static boolean matchLocale(final String[] array, final Locale locale) {
        if (array == null) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            if (matchLocale(array[i], locale)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean matchLocale(final String s, final Locale locale) {
        if (s == null || s.length() == 0) {
            return true;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "_", false);
        return (!stringTokenizer.hasMoreElements() || locale.getLanguage().length() <= 0 || stringTokenizer.nextToken().equalsIgnoreCase(locale.getLanguage())) && (!stringTokenizer.hasMoreElements() || locale.getCountry().length() <= 0 || stringTokenizer.nextToken().equalsIgnoreCase(locale.getCountry())) && (!stringTokenizer.hasMoreElements() || locale.getVariant().length() <= 0 || stringTokenizer.nextToken().equalsIgnoreCase(locale.getVariant()));
    }
    
    public static long heapValToLong(String s) {
        if (s == null) {
            return -1L;
        }
        long n = 1L;
        if (s.toLowerCase().lastIndexOf(109) != -1) {
            n = 1048576L;
            s = s.substring(0, s.length() - 1);
        }
        else if (s.toLowerCase().lastIndexOf(107) != -1) {
            n = 1024L;
            s = s.substring(0, s.length() - 1);
        }
        long n2;
        try {
            n2 = Long.parseLong(s) * n;
        }
        catch (NumberFormatException ex) {
            n2 = -1L;
        }
        return n2;
    }
    
    public static Frame getActiveTopLevelFrame() {
        final Frame[] frames = Frame.getFrames();
        int n = -1;
        if (frames == null) {
            return null;
        }
        for (int i = 0; i < frames.length; ++i) {
            if (frames[i].getFocusOwner() != null) {
                n = i;
            }
        }
        return (n >= 0) ? frames[n] : null;
    }
}

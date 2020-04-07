// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class StringUtilities
{
    public static final long SECOND_MILLIS = 1000L;
    public static final long MINUTE_MILLIS = 60000L;
    public static final long HOUR_MILLIS = 3600000L;
    public static final long DAY_MILLIS = 86400000L;
    private static final String UPPER_LOWER_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String VILLAGE_LEGAL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- ";
    private static final String SENTENCE_LEGAL_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,";
    private static final String EMPTY_STRING = "";
    private static final String SPACE_AND_SPACE = " and ";
    private static final char[] HEX_DIGITS;
    
    public static String raiseFirstLetter(final String oldString) {
        if (oldString != null && !oldString.isEmpty()) {
            return oldString.substring(0, 1).toUpperCase() + oldString.substring(1).toLowerCase();
        }
        return oldString;
    }
    
    public static String raiseFirstLetterOnly(final String oldString) {
        if (oldString != null && !oldString.isEmpty()) {
            return oldString.substring(0, 1).toUpperCase() + oldString.substring(1);
        }
        return oldString;
    }
    
    public static boolean containsIllegalPlayerNameCharacters(final String name) {
        return containsIllegalCharacters(name, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
    
    public static boolean containsNonSentenceCharacters(final String name) {
        return containsIllegalCharacters(name, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,");
    }
    
    public static boolean containsIllegalVillageCharacters(final String name) {
        return containsIllegalCharacters(name, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- ");
    }
    
    private static boolean containsIllegalCharacters(final String name, final String illegalCharacters) {
        final char[] chars = name.toCharArray();
        for (int x = 0; x < chars.length; ++x) {
            if (illegalCharacters.indexOf(chars[x]) < 0) {
                return true;
            }
        }
        return false;
    }
    
    public static String getTimeString(final long timeleft) {
        String times = "";
        if (timeleft < 60000L) {
            final long secs = timeleft / 1000L;
            times = times + secs + " seconds";
        }
        else {
            final long daysleft = timeleft / 86400000L;
            final long hoursleft = (timeleft - daysleft * 86400000L) / 3600000L;
            final long minutesleft = (timeleft - daysleft * 86400000L - hoursleft * 3600000L) / 60000L;
            if (daysleft > 0L) {
                times = times + daysleft + " days";
            }
            if (hoursleft > 0L) {
                String aft = "";
                if (daysleft > 0L && minutesleft > 0L) {
                    times += ", ";
                    aft += " and ";
                }
                else if (daysleft > 0L) {
                    times += " and ";
                }
                else if (minutesleft > 0L) {
                    aft += " and ";
                }
                times = times + hoursleft + " hours" + aft;
            }
            if (minutesleft > 0L) {
                String aft = "";
                if (daysleft > 0L && hoursleft == 0L) {
                    aft = " and ";
                }
                times = times + aft + minutesleft + " minutes";
            }
        }
        if (times.length() == 0) {
            times = "nothing";
        }
        return times;
    }
    
    public static String getWordForNumber(final int number) {
        String toReturn = null;
        switch (number) {
            case 1: {
                toReturn = "one";
                break;
            }
            case 2: {
                toReturn = "two";
                break;
            }
            case 3: {
                toReturn = "three";
                break;
            }
            case 4: {
                toReturn = "four";
                break;
            }
            case 5: {
                toReturn = "five";
                break;
            }
            case 6: {
                toReturn = "six";
                break;
            }
            case 7: {
                toReturn = "seven";
                break;
            }
            case 8: {
                toReturn = "eight";
                break;
            }
            case 9: {
                toReturn = "nine";
                break;
            }
            case 10: {
                toReturn = "ten";
                break;
            }
            default: {
                toReturn = String.valueOf(number);
                break;
            }
        }
        return toReturn;
    }
    
    public static String replace(final String target, final String from, final String to) {
        int start = target.indexOf(from);
        if (start == -1) {
            return target;
        }
        final int lf = from.length();
        final char[] targetChars = target.toCharArray();
        final StringBuilder buffer = new StringBuilder();
        int copyFrom;
        for (copyFrom = 0; start != -1; start = target.indexOf(from, copyFrom)) {
            buffer.append(targetChars, copyFrom, start - copyFrom);
            buffer.append(to);
            copyFrom = start + lf;
        }
        buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
        return buffer.toString();
    }
    
    public static boolean isVowel(final char aLetter) {
        return "aeiouAEIOU".indexOf(aLetter) != -1;
    }
    
    public static boolean isConsonant(final char aLetter) {
        return !isVowel(aLetter);
    }
    
    public static String htmlify(final String aLine) {
        String lLine = aLine.replaceAll("&", "&amp;");
        lLine = lLine.replaceAll("<", "&lt;");
        lLine = lLine.replaceAll(">", "&gt;");
        return lLine;
    }
    
    public static String toHexString(final byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xFF;
            sb.append(StringUtilities.HEX_DIGITS[b >> 4]);
            sb.append(StringUtilities.HEX_DIGITS[b & 0xF]);
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public static String addGenus(final String name) {
        final char firstLetter = name.charAt(0);
        final char lastLetter = name.charAt(name.length() - 1);
        final StringBuilder builder2 = new StringBuilder(name.length() + 5);
        builder2.setLength(0);
        if (lastLetter == 's') {
            builder2.append("some ");
        }
        else if (isVowel(firstLetter)) {
            builder2.append("an ");
        }
        else {
            builder2.append("a ");
        }
        builder2.append(name);
        return builder2.toString();
    }
    
    public static String addGenus(final String name, final boolean plural) {
        final char firstLetter = name.charAt(0);
        final StringBuilder builder2 = new StringBuilder(name.length() + 5);
        builder2.setLength(0);
        if (plural) {
            builder2.append("some ");
        }
        else if (isVowel(firstLetter)) {
            builder2.append("an ");
        }
        else {
            builder2.append("a ");
        }
        builder2.append(name);
        return builder2.toString();
    }
    
    static {
        HEX_DIGITS = "0123456789abcdef".toCharArray();
    }
}

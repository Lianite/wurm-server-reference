// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.dtdparser;

public class XmlChars
{
    public static boolean isChar(final int ucs4char) {
        return (ucs4char >= 32 && ucs4char <= 55295) || ucs4char == 10 || ucs4char == 9 || ucs4char == 13 || (ucs4char >= 57344 && ucs4char <= 65533) || (ucs4char >= 65536 && ucs4char <= 1114111);
    }
    
    public static boolean isNameChar(final char c) {
        return isLetter2(c) || (c != '>' && (c == '.' || c == '-' || c == '_' || c == ':' || isExtender(c)));
    }
    
    public static boolean isNCNameChar(final char c) {
        return c != ':' && isNameChar(c);
    }
    
    public static boolean isSpace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
    
    public static boolean isLetter(final char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c == '/') {
            return false;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        switch (Character.getType(c)) {
            case 1:
            case 2:
            case 3:
            case 5:
            case 10: {
                return !isCompatibilityChar(c) && (c < '\u20dd' || c > '\u20e0');
            }
            default: {
                return (c >= '\u02bb' && c <= '\u02c1') || c == '\u0559' || c == '\u06e5' || c == '\u06e6';
            }
        }
    }
    
    private static boolean isCompatibilityChar(final char c) {
        switch (c >> 8 & '\u00ff') {
            case '\0': {
                return c == 'ª' || c == 'µ' || c == 'º';
            }
            case '\u0001': {
                return (c >= '\u0132' && c <= '\u0133') || (c >= '\u013f' && c <= '\u0140') || c == '\u0149' || c == '\u017f' || (c >= '\u01c4' && c <= '\u01cc') || (c >= '\u01f1' && c <= '\u01f3');
            }
            case '\u0002': {
                return (c >= '\u02b0' && c <= '\u02b8') || (c >= '\u02e0' && c <= '\u02e4');
            }
            case '\u0003': {
                return c == '\u037a';
            }
            case '\u0005': {
                return c == '\u0587';
            }
            case '\u000e': {
                return c >= '\u0edc' && c <= '\u0edd';
            }
            case '\u0011': {
                return c == '\u1101' || c == '\u1104' || c == '\u1108' || c == '\u110a' || c == '\u110d' || (c >= '\u1113' && c <= '\u113b') || c == '\u113d' || c == '\u113f' || (c >= '\u1141' && c <= '\u114b') || c == '\u114d' || c == '\u114f' || (c >= '\u1151' && c <= '\u1153') || (c >= '\u1156' && c <= '\u1158') || c == '\u1162' || c == '\u1164' || c == '\u1166' || c == '\u1168' || (c >= '\u116a' && c <= '\u116c') || (c >= '\u116f' && c <= '\u1171') || c == '\u1174' || (c >= '\u1176' && c <= '\u119d') || (c >= '\u119f' && c <= '\u11a2') || (c >= '\u11a9' && c <= '\u11aa') || (c >= '\u11ac' && c <= '\u11ad') || (c >= '\u11b0' && c <= '\u11b6') || c == '\u11b9' || c == '\u11bb' || (c >= '\u11c3' && c <= '\u11ea') || (c >= '\u11ec' && c <= '\u11ef') || (c >= '\u11f1' && c <= '\u11f8');
            }
            case ' ': {
                return c == '\u207f';
            }
            case '!': {
                return c == '\u2102' || c == '\u2107' || (c >= '\u210a' && c <= '\u2113') || c == '\u2115' || (c >= '\u2118' && c <= '\u211d') || c == '\u2124' || c == '\u2128' || (c >= '\u212c' && c <= '\u212d') || (c >= '\u212f' && c <= '\u2138') || (c >= '\u2160' && c <= '\u217f');
            }
            case '0': {
                return c >= '\u309b' && c <= '\u309c';
            }
            case '1': {
                return c >= '\u3131' && c <= '\u318e';
            }
            case '\u00f9':
            case '\u00fa':
            case '\u00fb':
            case '\u00fc':
            case '\u00fd':
            case '\u00fe':
            case '\u00ff': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static boolean isLetter2(final char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c == '>') {
            return false;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        switch (Character.getType(c)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10: {
                return !isCompatibilityChar(c) && (c < '\u20dd' || c > '\u20e0');
            }
            default: {
                return c == '\u0387';
            }
        }
    }
    
    private static boolean isDigit(final char c) {
        return Character.isDigit(c) && (c < '\uff10' || c > '\uff19');
    }
    
    private static boolean isExtender(final char c) {
        return c == '·' || c == '\u02d0' || c == '\u02d1' || c == '\u0387' || c == '\u0640' || c == '\u0e46' || c == '\u0ec6' || c == '\u3005' || (c >= '\u3031' && c <= '\u3035') || (c >= '\u309d' && c <= '\u309e') || (c >= '\u30fc' && c <= '\u30fe');
    }
}

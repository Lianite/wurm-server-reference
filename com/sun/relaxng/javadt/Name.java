// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.relaxng.javadt;

import java.util.HashSet;

public class Name
{
    private static HashSet reservedKeywords;
    
    static {
        Name.reservedKeywords = new HashSet();
        final String[] words = { "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null", "assert" };
        for (int i = 0; i < words.length; ++i) {
            Name.reservedKeywords.add(words[i]);
        }
    }
    
    public static boolean isJavaIdentifier(final String token) {
        if (token.length() == 0) {
            return false;
        }
        if (Name.reservedKeywords.contains(token)) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(token.charAt(0))) {
            return false;
        }
        for (int i = 1; i < token.length(); ++i) {
            if (!Character.isJavaIdentifierPart(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isJavaPackageName(String s) {
        while (s.length() != 0) {
            int idx = s.indexOf(46);
            if (idx == -1) {
                idx = s.length();
            }
            if (!isJavaIdentifier(s.substring(0, idx))) {
                return false;
            }
            s = s.substring(idx);
            if (s.length() == 0) {
                continue;
            }
            s = s.substring(1);
        }
        return true;
    }
}

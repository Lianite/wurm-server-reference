// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.helpers;

public abstract class PublicId
{
    public static String normalize(final String s) {
        String s2;
        int index;
        for (s2 = s.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ').trim(); (index = s2.indexOf("  ")) >= 0; s2 = s2.substring(0, index) + s2.substring(index + 1)) {}
        return s2;
    }
    
    public static String encodeURN(final String s) {
        return "urn:publicid:" + stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(normalize(s), "%", "%25"), ";", "%3B"), "'", "%27"), "?", "%3F"), "#", "%23"), "+", "%2B"), " ", "+"), "::", ";"), ":", "%3A"), "//", ":"), "/", "%2F");
    }
    
    public static String decodeURN(final String s) {
        if (s.startsWith("urn:publicid:")) {
            return stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(stringReplace(s.substring(13), "%2F", "/"), ":", "//"), "%3A", ":"), ";", "::"), "+", " "), "%2B", "+"), "%23", "#"), "%3F", "?"), "%27", "'"), "%3B", ";"), "%25", "%");
        }
        return s;
    }
    
    private static String stringReplace(String substring, final String s, final String s2) {
        String string = "";
        for (int i = substring.indexOf(s); i >= 0; i = substring.indexOf(s)) {
            string = string + substring.substring(0, i) + s2;
            substring = substring.substring(i + 1);
        }
        return string + substring;
    }
}

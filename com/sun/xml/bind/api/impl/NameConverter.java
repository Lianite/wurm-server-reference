// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api.impl;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface NameConverter
{
    public static final NameConverter standard = new Standard();
    public static final NameConverter jaxrpcCompatible = new Standard() {
        protected boolean isPunct(final char c) {
            return c == '.' || c == '-' || c == ';' || c == '·' || c == '\u0387' || c == '\u06dd' || c == '\u06de';
        }
        
        protected boolean isLetter(final char c) {
            return super.isLetter(c) || c == '_';
        }
        
        protected int classify(final char c0) {
            if (c0 == '_') {
                return 2;
            }
            return super.classify(c0);
        }
    };
    public static final NameConverter smart = new Standard() {
        public String toConstantName(final String token) {
            final String name = super.toConstantName(token);
            if (NameUtil.isJavaIdentifier(name)) {
                return name;
            }
            return '_' + name;
        }
    };
    
    String toClassName(final String p0);
    
    String toInterfaceName(final String p0);
    
    String toPropertyName(final String p0);
    
    String toConstantName(final String p0);
    
    String toVariableName(final String p0);
    
    String toPackageName(final String p0);
    
    public static class Standard extends NameUtil implements NameConverter
    {
        public String toClassName(final String s) {
            return this.toMixedCaseName(this.toWordList(s), true);
        }
        
        public String toVariableName(final String s) {
            return this.toMixedCaseName(this.toWordList(s), false);
        }
        
        public String toInterfaceName(final String token) {
            return this.toClassName(token);
        }
        
        public String toPropertyName(final String s) {
            String prop = this.toClassName(s);
            if (prop.equals("Class")) {
                prop = "Clazz";
            }
            return prop;
        }
        
        public String toConstantName(final String token) {
            return super.toConstantName(token);
        }
        
        public String toPackageName(String nsUri) {
            int idx = nsUri.indexOf(58);
            String scheme = "";
            if (idx >= 0) {
                scheme = nsUri.substring(0, idx);
                if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("urn")) {
                    nsUri = nsUri.substring(idx + 1);
                }
            }
            final ArrayList<String> tokens = tokenize(nsUri, "/: ");
            if (tokens.size() == 0) {
                return null;
            }
            if (tokens.size() > 1) {
                String lastToken = tokens.get(tokens.size() - 1);
                idx = lastToken.lastIndexOf(46);
                if (idx > 0) {
                    lastToken = lastToken.substring(0, idx);
                    tokens.set(tokens.size() - 1, lastToken);
                }
            }
            String domain = tokens.get(0);
            idx = domain.indexOf(58);
            if (idx >= 0) {
                domain = domain.substring(0, idx);
            }
            final ArrayList<String> r = reverse(tokenize(domain, scheme.equals("urn") ? ".-" : "."));
            if (r.get(r.size() - 1).equalsIgnoreCase("www")) {
                r.remove(r.size() - 1);
            }
            tokens.addAll(1, r);
            tokens.remove(0);
            for (int i = 0; i < tokens.size(); ++i) {
                String token = tokens.get(i);
                token = removeIllegalIdentifierChars(token);
                if (!NameUtil.isJavaIdentifier(token)) {
                    token = '_' + token;
                }
                tokens.set(i, token.toLowerCase());
            }
            return combine(tokens, '.');
        }
        
        private static String removeIllegalIdentifierChars(final String token) {
            final StringBuffer newToken = new StringBuffer();
            for (int i = 0; i < token.length(); ++i) {
                final char c = token.charAt(i);
                if (i == 0 && !Character.isJavaIdentifierStart(c)) {
                    newToken.append('_').append(c);
                }
                else if (!Character.isJavaIdentifierPart(c)) {
                    newToken.append('_');
                }
                else {
                    newToken.append(c);
                }
            }
            return newToken.toString();
        }
        
        private static ArrayList<String> tokenize(final String str, final String sep) {
            final StringTokenizer tokens = new StringTokenizer(str, sep);
            final ArrayList<String> r = new ArrayList<String>();
            while (tokens.hasMoreTokens()) {
                r.add(tokens.nextToken());
            }
            return r;
        }
        
        private static <T> ArrayList<T> reverse(final List<T> a) {
            final ArrayList<T> r = new ArrayList<T>();
            for (int i = a.size() - 1; i >= 0; --i) {
                r.add(a.get(i));
            }
            return r;
        }
        
        private static String combine(final List r, final char sep) {
            final StringBuilder buf = new StringBuilder(r.get(0).toString());
            for (int i = 1; i < r.size(); ++i) {
                buf.append(sep);
                buf.append(r.get(i));
            }
            return buf.toString();
        }
    }
}

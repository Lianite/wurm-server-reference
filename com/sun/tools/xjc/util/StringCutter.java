// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;

public final class StringCutter
{
    private final String original;
    private String s;
    private boolean ignoreWhitespace;
    
    public StringCutter(final String s, final boolean ignoreWhitespace) {
        this.original = s;
        this.s = s;
        this.ignoreWhitespace = ignoreWhitespace;
    }
    
    public void skip(final String regexp) throws ParseException {
        this.next(regexp);
    }
    
    public String next(final String regexp) throws ParseException {
        this.trim();
        final Pattern p = Pattern.compile(regexp);
        final Matcher m = p.matcher(this.s);
        if (m.lookingAt()) {
            final String r = m.group();
            this.s = this.s.substring(r.length());
            this.trim();
            return r;
        }
        throw this.error();
    }
    
    private ParseException error() {
        return new ParseException(this.original, this.original.length() - this.s.length());
    }
    
    public String until(final String regexp) throws ParseException {
        final Pattern p = Pattern.compile(regexp);
        final Matcher m = p.matcher(this.s);
        if (m.find()) {
            String r = this.s.substring(0, m.start());
            this.s = this.s.substring(m.start());
            if (this.ignoreWhitespace) {
                r = r.trim();
            }
            return r;
        }
        String r = this.s;
        this.s = "";
        return r;
    }
    
    public char peek() {
        return this.s.charAt(0);
    }
    
    private void trim() {
        if (this.ignoreWhitespace) {
            this.s = this.s.trim();
        }
    }
    
    public int length() {
        return this.s.length();
    }
}

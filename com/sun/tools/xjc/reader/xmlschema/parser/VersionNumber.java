// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.util.StringTokenizer;

public class VersionNumber implements Comparable
{
    private final int[] digits;
    
    public VersionNumber(final String num) {
        final StringTokenizer tokens = new StringTokenizer(num, ".");
        this.digits = new int[tokens.countTokens()];
        if (this.digits.length < 2) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        while (tokens.hasMoreTokens()) {
            this.digits[i++] = Integer.parseInt(tokens.nextToken());
        }
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.digits.length; ++i) {
            if (i != 0) {
                buf.append('.');
            }
            buf.append(Integer.toString(this.digits[i]));
        }
        return buf.toString();
    }
    
    public boolean isOlderThan(final VersionNumber rhs) {
        return this.compareTo(rhs) < 0;
    }
    
    public boolean isNewerThan(final VersionNumber rhs) {
        return this.compareTo(rhs) > 0;
    }
    
    public boolean equals(final Object o) {
        return this.compareTo(o) == 0;
    }
    
    public int hashCode() {
        int x = 0;
        for (int i = 0; i < this.digits.length; ++i) {
            x = (x << 1 | this.digits[i]);
        }
        return x;
    }
    
    public int compareTo(final Object o) {
        final VersionNumber rhs = (VersionNumber)o;
        for (int i = 0; i != this.digits.length || i != rhs.digits.length; ++i) {
            if (i == this.digits.length) {
                return -1;
            }
            if (i == rhs.digits.length) {
                return 1;
            }
            final int r = this.digits[i] - rhs.digits[i];
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }
}

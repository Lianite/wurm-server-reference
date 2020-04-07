// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.util;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

class Surrogate
{
    public static final char MIN_HIGH = '\ud800';
    public static final char MAX_HIGH = '\udbff';
    public static final char MIN_LOW = '\udc00';
    public static final char MAX_LOW = '\udfff';
    public static final char MIN = '\ud800';
    public static final char MAX = '\udfff';
    public static final int UCS4_MIN = 65536;
    public static final int UCS4_MAX = 1114111;
    
    public static boolean isHigh(final int c) {
        return 55296 <= c && c <= 56319;
    }
    
    public static boolean isLow(final int c) {
        return 56320 <= c && c <= 57343;
    }
    
    public static boolean is(final int c) {
        return 55296 <= c && c <= 57343;
    }
    
    public static boolean neededFor(final int uc) {
        return uc >= 65536 && uc <= 1114111;
    }
    
    public static char high(final int uc) {
        return (char)(0xD800 | (uc - 65536 >> 10 & 0x3FF));
    }
    
    public static char low(final int uc) {
        return (char)(0xDC00 | (uc - 65536 & 0x3FF));
    }
    
    public static int toUCS4(final char c, final char d) {
        return ((c & '\u03ff') << 10 | (d & '\u03ff')) + 65536;
    }
    
    public static class Parser
    {
        private int character;
        private CoderResult error;
        private boolean isPair;
        
        public Parser() {
            this.error = CoderResult.UNDERFLOW;
        }
        
        public int character() {
            return this.character;
        }
        
        public boolean isPair() {
            return this.isPair;
        }
        
        public int increment() {
            return this.isPair ? 2 : 1;
        }
        
        public CoderResult error() {
            return this.error;
        }
        
        public CoderResult unmappableResult() {
            return CoderResult.unmappableForLength(this.isPair ? 2 : 1);
        }
        
        public int parse(final char c, final CharBuffer in) {
            if (Surrogate.isHigh(c)) {
                if (!in.hasRemaining()) {
                    this.error = CoderResult.UNDERFLOW;
                    return -1;
                }
                final char d = in.get();
                if (Surrogate.isLow(d)) {
                    this.character = Surrogate.toUCS4(c, d);
                    this.isPair = true;
                    this.error = null;
                    return this.character;
                }
                this.error = CoderResult.malformedForLength(1);
                return -1;
            }
            else {
                if (Surrogate.isLow(c)) {
                    this.error = CoderResult.malformedForLength(1);
                    return -1;
                }
                this.character = c;
                this.isPair = false;
                this.error = null;
                return this.character;
            }
        }
        
        public int parse(final char c, final char[] ia, final int ip, final int il) {
            if (Surrogate.isHigh(c)) {
                if (il - ip < 2) {
                    this.error = CoderResult.UNDERFLOW;
                    return -1;
                }
                final char d = ia[ip + 1];
                if (Surrogate.isLow(d)) {
                    this.character = Surrogate.toUCS4(c, d);
                    this.isPair = true;
                    this.error = null;
                    return this.character;
                }
                this.error = CoderResult.malformedForLength(1);
                return -1;
            }
            else {
                if (Surrogate.isLow(c)) {
                    this.error = CoderResult.malformedForLength(1);
                    return -1;
                }
                this.character = c;
                this.isPair = false;
                this.error = null;
                return this.character;
            }
        }
    }
    
    public static class Generator
    {
        private CoderResult error;
        
        public Generator() {
            this.error = CoderResult.OVERFLOW;
        }
        
        public CoderResult error() {
            return this.error;
        }
        
        public int generate(final int uc, final int len, final CharBuffer dst) {
            if (uc <= 65535) {
                if (Surrogate.is(uc)) {
                    this.error = CoderResult.malformedForLength(len);
                    return -1;
                }
                if (dst.remaining() < 1) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                dst.put((char)uc);
                this.error = null;
                return 1;
            }
            else {
                if (uc < 65536) {
                    this.error = CoderResult.malformedForLength(len);
                    return -1;
                }
                if (uc > 1114111) {
                    this.error = CoderResult.unmappableForLength(len);
                    return -1;
                }
                if (dst.remaining() < 2) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                dst.put(Surrogate.high(uc));
                dst.put(Surrogate.low(uc));
                this.error = null;
                return 2;
            }
        }
        
        public int generate(final int uc, final int len, final char[] da, final int dp, final int dl) {
            if (uc <= 65535) {
                if (Surrogate.is(uc)) {
                    this.error = CoderResult.malformedForLength(len);
                    return -1;
                }
                if (dl - dp < 1) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                da[dp] = (char)uc;
                this.error = null;
                return 1;
            }
            else {
                if (uc < 65536) {
                    this.error = CoderResult.malformedForLength(len);
                    return -1;
                }
                if (uc > 1114111) {
                    this.error = CoderResult.unmappableForLength(len);
                    return -1;
                }
                if (dl - dp < 2) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                da[dp] = Surrogate.high(uc);
                da[dp + 1] = Surrogate.low(uc);
                this.error = null;
                return 2;
            }
        }
    }
}

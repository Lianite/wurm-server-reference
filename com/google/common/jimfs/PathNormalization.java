// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Ascii;
import com.ibm.icu.lang.UCharacter;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Iterator;
import com.google.common.base.Function;

public enum PathNormalization implements Function<String, String>
{
    NONE(0) {
        @Override
        public String apply(final String string) {
            return string;
        }
    }, 
    NFC(128) {
        @Override
        public String apply(final String string) {
            return Normalizer.normalize(string, Normalizer.Form.NFC);
        }
    }, 
    NFD(128) {
        @Override
        public String apply(final String string) {
            return Normalizer.normalize(string, Normalizer.Form.NFD);
        }
    }, 
    CASE_FOLD_UNICODE(66) {
        @Override
        public String apply(final String string) {
            try {
                return UCharacter.foldCase(string, true);
            }
            catch (NoClassDefFoundError e) {
                final NoClassDefFoundError error = new NoClassDefFoundError("PathNormalization.CASE_FOLD_UNICODE requires ICU4J. Did you forget to include it on your classpath?");
                error.initCause(e);
                throw error;
            }
        }
    }, 
    CASE_FOLD_ASCII(2) {
        @Override
        public String apply(final String string) {
            return Ascii.toLowerCase(string);
        }
    };
    
    private final int patternFlags;
    
    private PathNormalization(final int patternFlags) {
        this.patternFlags = patternFlags;
    }
    
    @Override
    public abstract String apply(final String p0);
    
    public int patternFlags() {
        return this.patternFlags;
    }
    
    public static String normalize(final String string, final Iterable<PathNormalization> normalizations) {
        String result = string;
        for (final PathNormalization normalization : normalizations) {
            result = normalization.apply(result);
        }
        return result;
    }
    
    public static Pattern compilePattern(final String regex, final Iterable<PathNormalization> normalizations) {
        int flags = 0;
        for (final PathNormalization normalization : normalizations) {
            flags |= normalization.patternFlags();
        }
        return Pattern.compile(regex, flags);
    }
}

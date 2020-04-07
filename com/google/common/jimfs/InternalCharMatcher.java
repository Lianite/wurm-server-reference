// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Arrays;

final class InternalCharMatcher
{
    private final char[] chars;
    
    public static InternalCharMatcher anyOf(final String chars) {
        return new InternalCharMatcher(chars);
    }
    
    private InternalCharMatcher(final String chars) {
        Arrays.sort(this.chars = chars.toCharArray());
    }
    
    public boolean matches(final char c) {
        return Arrays.binarySearch(this.chars, c) >= 0;
    }
}

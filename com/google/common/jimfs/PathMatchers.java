// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.MoreObjects;
import java.nio.file.Path;
import java.util.regex.Pattern;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import java.nio.file.PathMatcher;
import com.google.common.collect.ImmutableSet;

final class PathMatchers
{
    public static PathMatcher getPathMatcher(final String syntaxAndPattern, final String separators, final ImmutableSet<PathNormalization> normalizations) {
        final int syntaxSeparator = syntaxAndPattern.indexOf(58);
        Preconditions.checkArgument(syntaxSeparator > 0, "Must be of the form 'syntax:pattern': %s", syntaxAndPattern);
        final String syntax = Ascii.toLowerCase(syntaxAndPattern.substring(0, syntaxSeparator));
        String pattern = syntaxAndPattern.substring(syntaxSeparator + 1);
        final String s = syntax;
        switch (s) {
            case "glob": {
                pattern = GlobToRegex.toRegex(pattern, separators);
            }
            case "regex": {
                return fromRegex(pattern, normalizations);
            }
            default: {
                throw new UnsupportedOperationException("Invalid syntax: " + syntaxAndPattern);
            }
        }
    }
    
    private static PathMatcher fromRegex(final String regex, final Iterable<PathNormalization> normalizations) {
        return new RegexPathMatcher(PathNormalization.compilePattern(regex, normalizations));
    }
    
    @VisibleForTesting
    static final class RegexPathMatcher implements PathMatcher
    {
        private final Pattern pattern;
        
        private RegexPathMatcher(final Pattern pattern) {
            this.pattern = Preconditions.checkNotNull(pattern);
        }
        
        @Override
        public boolean matches(final Path path) {
            return this.pattern.matcher(path.toString()).matches();
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).addValue(this.pattern).toString();
        }
    }
}

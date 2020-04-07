// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.collect.Iterables;
import com.google.common.base.Preconditions;
import java.net.URISyntaxException;
import java.net.URI;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import com.google.common.base.Splitter;
import com.google.common.base.Joiner;

public abstract class PathType
{
    private final boolean allowsMultipleRoots;
    private final String separator;
    private final String otherSeparators;
    private final Joiner joiner;
    private final Splitter splitter;
    private static final char[] regexReservedChars;
    
    public static PathType unix() {
        return UnixPathType.INSTANCE;
    }
    
    public static PathType windows() {
        return WindowsPathType.INSTANCE;
    }
    
    protected PathType(final boolean allowsMultipleRoots, final char separator, final char... otherSeparators) {
        this.separator = String.valueOf(separator);
        this.allowsMultipleRoots = allowsMultipleRoots;
        this.otherSeparators = String.valueOf(otherSeparators);
        this.joiner = Joiner.on(separator);
        this.splitter = createSplitter(separator, otherSeparators);
    }
    
    private static boolean isRegexReserved(final char c) {
        return Arrays.binarySearch(PathType.regexReservedChars, c) >= 0;
    }
    
    private static Splitter createSplitter(final char separator, final char... otherSeparators) {
        if (otherSeparators.length == 0) {
            return Splitter.on(separator).omitEmptyStrings();
        }
        final StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("[");
        appendToRegex(separator, patternBuilder);
        for (final char other : otherSeparators) {
            appendToRegex(other, patternBuilder);
        }
        patternBuilder.append("]");
        return Splitter.onPattern(patternBuilder.toString()).omitEmptyStrings();
    }
    
    private static void appendToRegex(final char separator, final StringBuilder patternBuilder) {
        if (isRegexReserved(separator)) {
            patternBuilder.append("\\");
        }
        patternBuilder.append(separator);
    }
    
    public final boolean allowsMultipleRoots() {
        return this.allowsMultipleRoots;
    }
    
    public final String getSeparator() {
        return this.separator;
    }
    
    public final String getOtherSeparators() {
        return this.otherSeparators;
    }
    
    public final Joiner joiner() {
        return this.joiner;
    }
    
    public final Splitter splitter() {
        return this.splitter;
    }
    
    protected final ParseResult emptyPath() {
        return new ParseResult(null, ImmutableList.of(""));
    }
    
    public abstract ParseResult parsePath(final String p0);
    
    public abstract String toString(@Nullable final String p0, final Iterable<String> p1);
    
    protected abstract String toUriPath(final String p0, final Iterable<String> p1, final boolean p2);
    
    protected abstract ParseResult parseUriPath(final String p0);
    
    public final URI toUri(final URI fileSystemUri, final String root, final Iterable<String> names, final boolean directory) {
        final String path = this.toUriPath(root, names, directory);
        try {
            return new URI(fileSystemUri.getScheme(), fileSystemUri.getUserInfo(), fileSystemUri.getHost(), fileSystemUri.getPort(), path, null, null);
        }
        catch (URISyntaxException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public final ParseResult fromUri(final URI uri) {
        return this.parseUriPath(uri.getPath());
    }
    
    static {
        Arrays.sort(regexReservedChars = "^$.?+*\\[]{}()".toCharArray());
    }
    
    public static final class ParseResult
    {
        @Nullable
        private final String root;
        private final Iterable<String> names;
        
        public ParseResult(@Nullable final String root, final Iterable<String> names) {
            this.root = root;
            this.names = Preconditions.checkNotNull(names);
        }
        
        public boolean isAbsolute() {
            return this.root != null;
        }
        
        public boolean isRoot() {
            return this.root != null && Iterables.isEmpty(this.names);
        }
        
        @Nullable
        public String root() {
            return this.root;
        }
        
        public Iterable<String> names() {
            return this.names;
        }
    }
}

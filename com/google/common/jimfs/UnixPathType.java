// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.nio.file.InvalidPathException;

final class UnixPathType extends PathType
{
    static final PathType INSTANCE;
    
    private UnixPathType() {
        super(false, '/', new char[0]);
    }
    
    @Override
    public ParseResult parsePath(final String path) {
        if (path.isEmpty()) {
            return this.emptyPath();
        }
        checkValid(path);
        final String root = path.startsWith("/") ? "/" : null;
        return new ParseResult(root, this.splitter().split(path));
    }
    
    private static void checkValid(final String path) {
        final int nulIndex = path.indexOf(0);
        if (nulIndex != -1) {
            throw new InvalidPathException(path, "nul character not allowed", nulIndex);
        }
    }
    
    @Override
    public String toString(@Nullable final String root, final Iterable<String> names) {
        final StringBuilder builder = new StringBuilder();
        if (root != null) {
            builder.append(root);
        }
        this.joiner().appendTo(builder, (Iterable<?>)names);
        return builder.toString();
    }
    
    public String toUriPath(final String root, final Iterable<String> names, final boolean directory) {
        final StringBuilder builder = new StringBuilder();
        for (final String name : names) {
            builder.append('/').append(name);
        }
        if (directory || builder.length() == 0) {
            builder.append('/');
        }
        return builder.toString();
    }
    
    public ParseResult parseUriPath(final String uriPath) {
        Preconditions.checkArgument(uriPath.startsWith("/"), "uriPath (%s) must start with /", uriPath);
        return this.parsePath(uriPath);
    }
    
    static {
        INSTANCE = new UnixPathType();
    }
}

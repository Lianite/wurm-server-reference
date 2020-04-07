// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.nio.file.InvalidPathException;
import java.util.regex.Pattern;

final class WindowsPathType extends PathType
{
    static final WindowsPathType INSTANCE;
    private static final Pattern WORKING_DIR_WITH_DRIVE;
    private static final Pattern TRAILING_SPACES;
    private static final Pattern UNC_ROOT;
    private static final Pattern DRIVE_LETTER_ROOT;
    
    private WindowsPathType() {
        super(true, '\\', new char[] { '/' });
    }
    
    @Override
    public ParseResult parsePath(String path) {
        final String original = path;
        path = path.replace('/', '\\');
        if (WindowsPathType.WORKING_DIR_WITH_DRIVE.matcher(path).matches()) {
            throw new InvalidPathException(original, "Jimfs does not currently support the Windows syntax for a relative path on a specific drive (e.g. \"C:foo\\bar\"");
        }
        String root;
        if (path.startsWith("\\\\")) {
            root = this.parseUncRoot(path, original);
        }
        else {
            if (path.startsWith("\\")) {
                throw new InvalidPathException(original, "Jimfs does not currently support the Windows syntax for an absolute path on the current drive (e.g. \"\\foo\\bar\"");
            }
            root = this.parseDriveRoot(path);
        }
        int i;
        for (int startIndex = i = ((root == null || root.length() > 3) ? 0 : root.length()); i < path.length(); ++i) {
            final char c = path.charAt(i);
            if (isReserved(c)) {
                throw new InvalidPathException(original, "Illegal char <" + c + ">", i);
            }
        }
        final Matcher trailingSpaceMatcher = WindowsPathType.TRAILING_SPACES.matcher(path);
        if (trailingSpaceMatcher.find()) {
            throw new InvalidPathException(original, "Trailing char < >", trailingSpaceMatcher.start());
        }
        if (root != null) {
            path = path.substring(root.length());
            if (!root.endsWith("\\")) {
                root += "\\";
            }
        }
        return new ParseResult(root, this.splitter().split(path));
    }
    
    private String parseUncRoot(final String path, final String original) {
        final Matcher uncMatcher = WindowsPathType.UNC_ROOT.matcher(path);
        if (!uncMatcher.find()) {
            throw new InvalidPathException(original, "Invalid UNC path");
        }
        final String host = uncMatcher.group(2);
        if (host == null) {
            throw new InvalidPathException(original, "UNC path is missing hostname");
        }
        final String share = uncMatcher.group(3);
        if (share == null) {
            throw new InvalidPathException(original, "UNC path is missing sharename");
        }
        return path.substring(uncMatcher.start(), uncMatcher.end());
    }
    
    @Nullable
    private String parseDriveRoot(final String path) {
        final Matcher drivePathMatcher = WindowsPathType.DRIVE_LETTER_ROOT.matcher(path);
        if (drivePathMatcher.find()) {
            return path.substring(drivePathMatcher.start(), drivePathMatcher.end());
        }
        return null;
    }
    
    private static boolean isReserved(final char c) {
        switch (c) {
            case '\"':
            case '*':
            case ':':
            case '<':
            case '>':
            case '?':
            case '|': {
                return true;
            }
            default: {
                return c <= '\u001f';
            }
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
    
    public String toUriPath(String root, final Iterable<String> names, final boolean directory) {
        if (root.startsWith("\\\\")) {
            root = root.replace('\\', '/');
        }
        else {
            root = "/" + root.replace('\\', '/');
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(root);
        final Iterator<String> iter = names.iterator();
        if (iter.hasNext()) {
            builder.append(iter.next());
            while (iter.hasNext()) {
                builder.append('/').append(iter.next());
            }
        }
        if (directory && builder.charAt(builder.length() - 1) != '/') {
            builder.append('/');
        }
        return builder.toString();
    }
    
    public ParseResult parseUriPath(String uriPath) {
        uriPath = uriPath.replace('/', '\\');
        if (uriPath.charAt(0) == '\\' && uriPath.charAt(1) != '\\') {
            uriPath = uriPath.substring(1);
        }
        return this.parsePath(uriPath);
    }
    
    static {
        INSTANCE = new WindowsPathType();
        WORKING_DIR_WITH_DRIVE = Pattern.compile("^[a-zA-Z]:([^\\\\].*)?$");
        TRAILING_SPACES = Pattern.compile("[ ]+(\\\\|$)");
        UNC_ROOT = Pattern.compile("^(\\\\\\\\)([^\\\\]+)?(\\\\[^\\\\]+)?");
        DRIVE_LETTER_ROOT = Pattern.compile("^[a-zA-Z]:\\\\");
    }
}

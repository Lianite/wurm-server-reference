// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.UUID;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Map;
import java.nio.file.FileSystems;
import com.google.common.collect.ImmutableMap;
import com.google.common.base.Preconditions;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.file.FileSystem;

public final class Jimfs
{
    public static final String URI_SCHEME = "jimfs";
    
    public static FileSystem newFileSystem() {
        return newFileSystem(newRandomFileSystemName());
    }
    
    public static FileSystem newFileSystem(final String name) {
        return newFileSystem(name, Configuration.forCurrentPlatform());
    }
    
    public static FileSystem newFileSystem(final Configuration configuration) {
        return newFileSystem(newRandomFileSystemName(), configuration);
    }
    
    public static FileSystem newFileSystem(final String name, final Configuration configuration) {
        try {
            final URI uri = new URI("jimfs", name, null, null);
            return newFileSystem(uri, configuration);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @VisibleForTesting
    static FileSystem newFileSystem(final URI uri, final Configuration config) {
        Preconditions.checkArgument("jimfs".equals(uri.getScheme()), "uri (%s) must have scheme %s", uri, "jimfs");
        try {
            final JimfsFileSystem fileSystem = JimfsFileSystems.newFileSystem(JimfsFileSystemProvider.instance(), uri, config);
            final ImmutableMap<String, ?> env = ImmutableMap.of("fileSystem", (Object)fileSystem);
            FileSystems.newFileSystem(uri, env, SystemJimfsFileSystemProvider.class.getClassLoader());
            return fileSystem;
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    private static String newRandomFileSystemName() {
        return UUID.randomUUID().toString();
    }
}

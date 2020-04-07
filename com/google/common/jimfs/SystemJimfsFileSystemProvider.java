// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.collect.MapMaker;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.LinkOption;
import java.nio.file.AccessMode;
import java.nio.file.FileStore;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.OpenOption;
import java.util.Set;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import com.google.common.base.Strings;
import java.nio.file.Path;
import java.nio.file.FileSystemNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemAlreadyExistsException;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.nio.file.FileSystem;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import com.google.auto.service.AutoService;
import java.nio.file.spi.FileSystemProvider;

@AutoService(FileSystemProvider.class)
public final class SystemJimfsFileSystemProvider extends FileSystemProvider
{
    static final String FILE_SYSTEM_KEY = "fileSystem";
    private static final ConcurrentMap<URI, FileSystem> fileSystems;
    
    @Override
    public String getScheme() {
        return "jimfs";
    }
    
    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IOException {
        Preconditions.checkArgument(uri.getScheme().equalsIgnoreCase("jimfs"), "uri (%s) scheme must be '%s'", uri, "jimfs");
        Preconditions.checkArgument(isValidFileSystemUri(uri), "uri (%s) may not have a path, query or fragment", uri);
        Preconditions.checkArgument(env.get("fileSystem") instanceof FileSystem, "env map (%s) must contain key '%s' mapped to an instance of %s", env, "fileSystem", FileSystem.class);
        final FileSystem fileSystem = (FileSystem)env.get("fileSystem");
        if (SystemJimfsFileSystemProvider.fileSystems.putIfAbsent(uri, fileSystem) != null) {
            throw new FileSystemAlreadyExistsException(uri.toString());
        }
        return fileSystem;
    }
    
    @Override
    public FileSystem getFileSystem(final URI uri) {
        final FileSystem fileSystem = SystemJimfsFileSystemProvider.fileSystems.get(uri);
        if (fileSystem == null) {
            throw new FileSystemNotFoundException(uri.toString());
        }
        return fileSystem;
    }
    
    @Override
    public Path getPath(final URI uri) {
        Preconditions.checkArgument("jimfs".equalsIgnoreCase(uri.getScheme()), "uri scheme does not match this provider: %s", uri);
        final String path = uri.getPath();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "uri must have a path: %s", uri);
        return toPath(this.getFileSystem(toFileSystemUri(uri)), uri);
    }
    
    private static boolean isValidFileSystemUri(final URI uri) {
        return Strings.isNullOrEmpty(uri.getPath()) && Strings.isNullOrEmpty(uri.getQuery()) && Strings.isNullOrEmpty(uri.getFragment());
    }
    
    private static URI toFileSystemUri(final URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        }
        catch (URISyntaxException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    private static Path toPath(final FileSystem fileSystem, final URI uri) {
        try {
            final Method toPath = fileSystem.getClass().getDeclaredMethod("toPath", URI.class);
            return (Path)toPath.invoke(fileSystem, uri);
        }
        catch (NoSuchMethodException e2) {
            throw new IllegalArgumentException("invalid file system: " + fileSystem);
        }
        catch (InvocationTargetException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env) throws IOException {
        final FileSystemProvider realProvider = path.getFileSystem().provider();
        return realProvider.newFileSystem(path, env);
    }
    
    public static Runnable removeFileSystemRunnable(final URI uri) {
        return new Runnable() {
            @Override
            public void run() {
                SystemJimfsFileSystemProvider.fileSystems.remove(uri);
            }
        };
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void delete(final Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void move(final Path source, final Path target, final CopyOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isHidden(final Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    static {
        fileSystems = new MapMaker().weakValues().makeMap();
    }
}

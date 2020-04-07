// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.AccessMode;
import java.nio.file.FileStore;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.CopyOption;
import java.nio.file.LinkOption;
import java.nio.file.DirectoryStream;
import java.nio.file.StandardOpenOption;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.nio.channels.SeekableByteChannel;
import com.google.common.collect.ImmutableSet;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.ProviderMismatchException;
import java.nio.file.FileSystems;
import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Map;
import java.net.URI;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;

final class JimfsFileSystemProvider extends FileSystemProvider
{
    private static final JimfsFileSystemProvider INSTANCE;
    private static final FileAttribute<?>[] NO_ATTRS;
    
    static JimfsFileSystemProvider instance() {
        return JimfsFileSystemProvider.INSTANCE;
    }
    
    @Override
    public String getScheme() {
        return "jimfs";
    }
    
    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IOException {
        throw new UnsupportedOperationException("This method should not be called directly;use an overload of Jimfs.newFileSystem() to create a FileSystem.");
    }
    
    @Override
    public FileSystem getFileSystem(final URI uri) {
        throw new UnsupportedOperationException("This method should not be called directly; use FileSystems.getFileSystem(URI) instead.");
    }
    
    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        Preconditions.checkNotNull(env);
        final URI pathUri = checkedPath.toUri();
        final URI jarUri = URI.create("jar:" + pathUri);
        try {
            return FileSystems.newFileSystem(jarUri, env);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
    @Override
    public Path getPath(final URI uri) {
        throw new UnsupportedOperationException("This method should not be called directly; use Paths.get(URI) instead.");
    }
    
    private static JimfsPath checkPath(final Path path) {
        if (path instanceof JimfsPath) {
            return (JimfsPath)path;
        }
        throw new ProviderMismatchException("path " + path + " is not associated with a Jimfs file system");
    }
    
    private static JimfsFileSystem getFileSystem(final Path path) {
        return (JimfsFileSystem)checkPath(path).getFileSystem();
    }
    
    private static FileSystemView getDefaultView(final JimfsPath path) {
        return getFileSystem(path).getDefaultView();
    }
    
    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        if (!checkedPath.getJimfsFileSystem().getFileStore().supportsFeature(Feature.FILE_CHANNEL)) {
            throw new UnsupportedOperationException();
        }
        return this.newJimfsFileChannel(checkedPath, options, attrs);
    }
    
    private JimfsFileChannel newJimfsFileChannel(final JimfsPath path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        final ImmutableSet<OpenOption> opts = Options.getOptionsForChannel(options);
        final FileSystemView view = getDefaultView(path);
        final RegularFile file = view.getOrCreateRegularFile(path, opts, attrs);
        return new JimfsFileChannel(file, opts, view.state());
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        final JimfsFileChannel channel = this.newJimfsFileChannel(checkedPath, options, attrs);
        return checkedPath.getJimfsFileSystem().getFileStore().supportsFeature(Feature.FILE_CHANNEL) ? channel : new DowngradedSeekableByteChannel(channel);
    }
    
    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> options, @Nullable ExecutorService executor, final FileAttribute<?>... attrs) throws IOException {
        final JimfsFileChannel channel = (JimfsFileChannel)this.newFileChannel(path, options, attrs);
        if (executor == null) {
            final JimfsFileSystem fileSystem = (JimfsFileSystem)path.getFileSystem();
            executor = fileSystem.getDefaultThreadPool();
        }
        return channel.asAsynchronousFileChannel(executor);
    }
    
    @Override
    public InputStream newInputStream(final Path path, final OpenOption... options) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        final ImmutableSet<OpenOption> opts = Options.getOptionsForInputStream(options);
        final FileSystemView view = getDefaultView(checkedPath);
        final RegularFile file = view.getOrCreateRegularFile(checkedPath, opts, JimfsFileSystemProvider.NO_ATTRS);
        return new JimfsInputStream(file, view.state());
    }
    
    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... options) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        final ImmutableSet<OpenOption> opts = Options.getOptionsForOutputStream(options);
        final FileSystemView view = getDefaultView(checkedPath);
        final RegularFile file = view.getOrCreateRegularFile(checkedPath, opts, JimfsFileSystemProvider.NO_ATTRS);
        return new JimfsOutputStream(file, opts.contains(StandardOpenOption.APPEND), view.state());
    }
    
    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        final JimfsPath checkedPath = checkPath(dir);
        return getDefaultView(checkedPath).newDirectoryStream(checkedPath, filter, Options.FOLLOW_LINKS, checkedPath);
    }
    
    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        final JimfsPath checkedPath = checkPath(dir);
        final FileSystemView view = getDefaultView(checkedPath);
        view.createDirectory(checkedPath, attrs);
    }
    
    @Override
    public void createLink(final Path link, final Path existing) throws IOException {
        final JimfsPath linkPath = checkPath(link);
        final JimfsPath existingPath = checkPath(existing);
        Preconditions.checkArgument(linkPath.getFileSystem().equals(existingPath.getFileSystem()), (Object)"link and existing paths must belong to the same file system instance");
        final FileSystemView view = getDefaultView(linkPath);
        view.link(linkPath, getDefaultView(existingPath), existingPath);
    }
    
    @Override
    public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs) throws IOException {
        final JimfsPath linkPath = checkPath(link);
        final JimfsPath targetPath = checkPath(target);
        Preconditions.checkArgument(linkPath.getFileSystem().equals(targetPath.getFileSystem()), (Object)"link and target paths must belong to the same file system instance");
        final FileSystemView view = getDefaultView(linkPath);
        view.createSymbolicLink(linkPath, targetPath, attrs);
    }
    
    @Override
    public Path readSymbolicLink(final Path link) throws IOException {
        final JimfsPath checkedPath = checkPath(link);
        return getDefaultView(checkedPath).readSymbolicLink(checkedPath);
    }
    
    @Override
    public void delete(final Path path) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        final FileSystemView view = getDefaultView(checkedPath);
        view.deleteFile(checkedPath, FileSystemView.DeleteMode.ANY);
    }
    
    @Override
    public void copy(final Path source, final Path target, final CopyOption... options) throws IOException {
        this.copy(source, target, Options.getCopyOptions(options), false);
    }
    
    @Override
    public void move(final Path source, final Path target, final CopyOption... options) throws IOException {
        this.copy(source, target, Options.getMoveOptions(options), true);
    }
    
    private void copy(final Path source, final Path target, final ImmutableSet<CopyOption> options, final boolean move) throws IOException {
        final JimfsPath sourcePath = checkPath(source);
        final JimfsPath targetPath = checkPath(target);
        final FileSystemView sourceView = getDefaultView(sourcePath);
        final FileSystemView targetView = getDefaultView(targetPath);
        sourceView.copy(sourcePath, targetView, targetPath, options, move);
    }
    
    @Override
    public boolean isSameFile(final Path path, final Path path2) throws IOException {
        if (path.equals(path2)) {
            return true;
        }
        if (!(path instanceof JimfsPath) || !(path2 instanceof JimfsPath)) {
            return false;
        }
        final JimfsPath checkedPath = (JimfsPath)path;
        final JimfsPath checkedPath2 = (JimfsPath)path2;
        final FileSystemView view = getDefaultView(checkedPath);
        final FileSystemView view2 = getDefaultView(checkedPath2);
        return view.isSameFile(checkedPath, view2, checkedPath2);
    }
    
    @Override
    public boolean isHidden(final Path path) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        final FileSystemView view = getDefaultView(checkedPath);
        if (this.getFileStore(path).supportsFileAttributeView("dos")) {
            return view.readAttributes(checkedPath, DosFileAttributes.class, Options.NOFOLLOW_LINKS).isHidden();
        }
        return path.getNameCount() > 0 && path.getFileName().toString().startsWith(".");
    }
    
    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        return getFileSystem(path).getFileStore();
    }
    
    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        getDefaultView(checkedPath).checkAccess(checkedPath);
    }
    
    @Nullable
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
        final JimfsPath checkedPath = checkPath(path);
        return getDefaultView(checkedPath).getFileAttributeView(checkedPath, type, Options.getLinkOptions(options));
    }
    
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        return getDefaultView(checkedPath).readAttributes(checkedPath, type, Options.getLinkOptions(options));
    }
    
    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        return getDefaultView(checkedPath).readAttributes(checkedPath, attributes, Options.getLinkOptions(options));
    }
    
    @Override
    public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options) throws IOException {
        final JimfsPath checkedPath = checkPath(path);
        getDefaultView(checkedPath).setAttribute(checkedPath, attribute, value, Options.getLinkOptions(options));
    }
    
    static {
        INSTANCE = new JimfsFileSystemProvider();
        try {
            Handler.register();
        }
        catch (Throwable t) {}
        NO_ATTRS = new FileAttribute[0];
    }
}

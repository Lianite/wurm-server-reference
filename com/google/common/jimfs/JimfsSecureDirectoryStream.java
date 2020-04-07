// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.DirectoryIteratorException;
import javax.annotation.Nullable;
import com.google.common.collect.AbstractIterator;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.CopyOption;
import java.nio.file.ProviderMismatchException;
import com.google.common.collect.ImmutableSet;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.util.Set;
import java.nio.file.LinkOption;
import java.nio.file.ClosedDirectoryStreamException;
import java.io.Closeable;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;

final class JimfsSecureDirectoryStream implements SecureDirectoryStream<Path>
{
    private final FileSystemView view;
    private final DirectoryStream.Filter<? super Path> filter;
    private final FileSystemState fileSystemState;
    private boolean open;
    private Iterator<Path> iterator;
    public static final DirectoryStream.Filter<Object> ALWAYS_TRUE_FILTER;
    
    public JimfsSecureDirectoryStream(final FileSystemView view, final DirectoryStream.Filter<? super Path> filter, final FileSystemState fileSystemState) {
        this.open = true;
        this.iterator = new DirectoryIterator();
        this.view = Preconditions.checkNotNull(view);
        this.filter = Preconditions.checkNotNull(filter);
        (this.fileSystemState = fileSystemState).register(this);
    }
    
    private JimfsPath path() {
        return this.view.getWorkingDirectoryPath();
    }
    
    @Override
    public synchronized Iterator<Path> iterator() {
        this.checkOpen();
        final Iterator<Path> result = this.iterator;
        Preconditions.checkState(result != null, (Object)"iterator() has already been called once");
        this.iterator = null;
        return result;
    }
    
    @Override
    public synchronized void close() {
        this.open = false;
        this.fileSystemState.unregister(this);
    }
    
    protected synchronized void checkOpen() {
        if (!this.open) {
            throw new ClosedDirectoryStreamException();
        }
    }
    
    @Override
    public SecureDirectoryStream<Path> newDirectoryStream(final Path path, final LinkOption... options) throws IOException {
        this.checkOpen();
        final JimfsPath checkedPath = checkPath(path);
        return (SecureDirectoryStream<Path>)(SecureDirectoryStream)this.view.newDirectoryStream(checkedPath, JimfsSecureDirectoryStream.ALWAYS_TRUE_FILTER, Options.getLinkOptions(options), this.path().resolve((Path)checkedPath));
    }
    
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        this.checkOpen();
        final JimfsPath checkedPath = checkPath(path);
        final ImmutableSet<OpenOption> opts = Options.getOptionsForChannel(options);
        return new JimfsFileChannel(this.view.getOrCreateRegularFile(checkedPath, opts, (FileAttribute<?>[])new FileAttribute[0]), opts, this.fileSystemState);
    }
    
    @Override
    public void deleteFile(final Path path) throws IOException {
        this.checkOpen();
        final JimfsPath checkedPath = checkPath(path);
        this.view.deleteFile(checkedPath, FileSystemView.DeleteMode.NON_DIRECTORY_ONLY);
    }
    
    @Override
    public void deleteDirectory(final Path path) throws IOException {
        this.checkOpen();
        final JimfsPath checkedPath = checkPath(path);
        this.view.deleteFile(checkedPath, FileSystemView.DeleteMode.DIRECTORY_ONLY);
    }
    
    @Override
    public void move(final Path srcPath, final SecureDirectoryStream<Path> targetDir, final Path targetPath) throws IOException {
        this.checkOpen();
        final JimfsPath checkedSrcPath = checkPath(srcPath);
        final JimfsPath checkedTargetPath = checkPath(targetPath);
        if (!(targetDir instanceof JimfsSecureDirectoryStream)) {
            throw new ProviderMismatchException("targetDir isn't a secure directory stream associated with this file system");
        }
        final JimfsSecureDirectoryStream checkedTargetDir = (JimfsSecureDirectoryStream)targetDir;
        this.view.copy(checkedSrcPath, checkedTargetDir.view, checkedTargetPath, (Set<CopyOption>)ImmutableSet.of(), true);
    }
    
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Class<V> type) {
        return this.getFileAttributeView(this.path().getFileSystem().getPath(".", new String[0]), type, new LinkOption[0]);
    }
    
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type, final LinkOption... options) {
        this.checkOpen();
        final JimfsPath checkedPath = checkPath(path);
        final ImmutableSet<LinkOption> optionsSet = Options.getLinkOptions(options);
        return this.view.getFileAttributeView(new FileLookup() {
            @Override
            public File lookup() throws IOException {
                JimfsSecureDirectoryStream.this.checkOpen();
                return JimfsSecureDirectoryStream.this.view.lookUpWithLock(checkedPath, optionsSet).requireExists(checkedPath).file();
            }
        }, type);
    }
    
    private static JimfsPath checkPath(final Path path) {
        if (path instanceof JimfsPath) {
            return (JimfsPath)path;
        }
        throw new ProviderMismatchException("path " + path + " is not associated with a Jimfs file system");
    }
    
    static {
        ALWAYS_TRUE_FILTER = new DirectoryStream.Filter<Object>() {
            @Override
            public boolean accept(final Object entry) throws IOException {
                return true;
            }
        };
    }
    
    private final class DirectoryIterator extends AbstractIterator<Path>
    {
        @Nullable
        private Iterator<Name> fileNames;
        
        @Override
        protected synchronized Path computeNext() {
            JimfsSecureDirectoryStream.this.checkOpen();
            try {
                if (this.fileNames == null) {
                    this.fileNames = JimfsSecureDirectoryStream.this.view.snapshotWorkingDirectoryEntries().iterator();
                }
                while (this.fileNames.hasNext()) {
                    final Name name = this.fileNames.next();
                    final Path path = JimfsSecureDirectoryStream.this.view.getWorkingDirectoryPath().resolve(name);
                    if (JimfsSecureDirectoryStream.this.filter.accept(path)) {
                        return path;
                    }
                }
                return this.endOfData();
            }
            catch (IOException e) {
                throw new DirectoryIteratorException(e);
            }
        }
    }
}

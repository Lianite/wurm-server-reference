// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import javax.annotation.Nullable;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.FileAlreadyExistsException;
import com.google.common.base.Supplier;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Iterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.util.Set;
import com.google.common.base.Preconditions;

final class FileSystemView
{
    private final JimfsFileStore store;
    private final Directory workingDirectory;
    private final JimfsPath workingDirectoryPath;
    
    public FileSystemView(final JimfsFileStore store, final Directory workingDirectory, final JimfsPath workingDirectoryPath) {
        this.store = Preconditions.checkNotNull(store);
        this.workingDirectory = Preconditions.checkNotNull(workingDirectory);
        this.workingDirectoryPath = Preconditions.checkNotNull(workingDirectoryPath);
    }
    
    private boolean isSameFileSystem(final FileSystemView other) {
        return this.store == other.store;
    }
    
    public FileSystemState state() {
        return this.store.state();
    }
    
    public JimfsPath getWorkingDirectoryPath() {
        return this.workingDirectoryPath;
    }
    
    DirectoryEntry lookUpWithLock(final JimfsPath path, final Set<? super LinkOption> options) throws IOException {
        this.store.readLock().lock();
        try {
            return this.lookUp(path, options);
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    private DirectoryEntry lookUp(final JimfsPath path, final Set<? super LinkOption> options) throws IOException {
        return this.store.lookUp(this.workingDirectory, path, options);
    }
    
    public DirectoryStream<Path> newDirectoryStream(final JimfsPath dir, final DirectoryStream.Filter<? super Path> filter, final Set<? super LinkOption> options, final JimfsPath basePathForStream) throws IOException {
        final Directory file = (Directory)this.lookUpWithLock(dir, options).requireDirectory(dir).file();
        final FileSystemView view = new FileSystemView(this.store, file, basePathForStream);
        final JimfsSecureDirectoryStream stream = new JimfsSecureDirectoryStream(view, filter, this.state());
        return (DirectoryStream<Path>)(this.store.supportsFeature(Feature.SECURE_DIRECTORY_STREAM) ? stream : new DowngradedDirectoryStream(stream));
    }
    
    public ImmutableSortedSet<Name> snapshotWorkingDirectoryEntries() {
        this.store.readLock().lock();
        try {
            final ImmutableSortedSet<Name> names = this.workingDirectory.snapshot();
            this.workingDirectory.updateAccessTime();
            return names;
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    public ImmutableMap<Name, Long> snapshotModifiedTimes(final JimfsPath path) throws IOException {
        final ImmutableMap.Builder<Name, Long> modifiedTimes = ImmutableMap.builder();
        this.store.readLock().lock();
        try {
            final Directory dir = (Directory)this.lookUp(path, Options.FOLLOW_LINKS).requireDirectory(path).file();
            for (final DirectoryEntry entry : dir) {
                if (!entry.name().equals(Name.SELF) && !entry.name().equals(Name.PARENT)) {
                    modifiedTimes.put(entry.name(), entry.file().getLastModifiedTime());
                }
            }
            return modifiedTimes.build();
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    public boolean isSameFile(final JimfsPath path, final FileSystemView view2, final JimfsPath path2) throws IOException {
        if (!this.isSameFileSystem(view2)) {
            return false;
        }
        this.store.readLock().lock();
        try {
            final File file = this.lookUp(path, Options.FOLLOW_LINKS).fileOrNull();
            final File file2 = view2.lookUp(path2, Options.FOLLOW_LINKS).fileOrNull();
            return file != null && Objects.equals(file, file2);
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    public JimfsPath toRealPath(final JimfsPath path, final PathService pathService, final Set<? super LinkOption> options) throws IOException {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(options);
        this.store.readLock().lock();
        try {
            DirectoryEntry entry = this.lookUp(path, options).requireExists(path);
            final List<Name> names = new ArrayList<Name>();
            names.add(entry.name());
            while (!entry.file().isRootDirectory()) {
                entry = entry.directory().entryInParent();
                names.add(entry.name());
            }
            final List<Name> reversed = Lists.reverse(names);
            final Name root = reversed.remove(0);
            return pathService.createPath(root, reversed);
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    public Directory createDirectory(final JimfsPath path, final FileAttribute<?>... attrs) throws IOException {
        return (Directory)this.createFile(path, this.store.directoryCreator(), true, attrs);
    }
    
    public SymbolicLink createSymbolicLink(final JimfsPath path, final JimfsPath target, final FileAttribute<?>... attrs) throws IOException {
        if (!this.store.supportsFeature(Feature.SYMBOLIC_LINKS)) {
            throw new UnsupportedOperationException();
        }
        return (SymbolicLink)this.createFile(path, this.store.symbolicLinkCreator(target), true, attrs);
    }
    
    private File createFile(final JimfsPath path, final Supplier<? extends File> fileCreator, final boolean failIfExists, final FileAttribute<?>... attrs) throws IOException {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(fileCreator);
        this.store.writeLock().lock();
        try {
            final DirectoryEntry entry = this.lookUp(path, Options.NOFOLLOW_LINKS);
            if (!entry.exists()) {
                final Directory parent = entry.directory();
                final File newFile = (File)fileCreator.get();
                this.store.setInitialAttributes(newFile, attrs);
                parent.link(path.name(), newFile);
                parent.updateModifiedTime();
                return newFile;
            }
            if (failIfExists) {
                throw new FileAlreadyExistsException(path.toString());
            }
            return entry.file();
        }
        finally {
            this.store.writeLock().unlock();
        }
    }
    
    public RegularFile getOrCreateRegularFile(final JimfsPath path, final Set<OpenOption> options, final FileAttribute<?>... attrs) throws IOException {
        Preconditions.checkNotNull(path);
        if (!options.contains(StandardOpenOption.CREATE_NEW)) {
            final RegularFile file = this.lookUpRegularFile(path, options);
            if (file != null) {
                return file;
            }
        }
        if (options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.CREATE_NEW)) {
            return this.getOrCreateRegularFileWithWriteLock(path, options, attrs);
        }
        throw new NoSuchFileException(path.toString());
    }
    
    @Nullable
    private RegularFile lookUpRegularFile(final JimfsPath path, final Set<OpenOption> options) throws IOException {
        this.store.readLock().lock();
        try {
            final DirectoryEntry entry = this.lookUp(path, options);
            if (!entry.exists()) {
                return null;
            }
            final File file = entry.file();
            if (!file.isRegularFile()) {
                throw new FileSystemException(path.toString(), null, "not a regular file");
            }
            return open((RegularFile)file, options);
        }
        finally {
            this.store.readLock().unlock();
        }
    }
    
    private RegularFile getOrCreateRegularFileWithWriteLock(final JimfsPath path, final Set<OpenOption> options, final FileAttribute<?>[] attrs) throws IOException {
        this.store.writeLock().lock();
        try {
            final File file = this.createFile(path, this.store.regularFileCreator(), options.contains(StandardOpenOption.CREATE_NEW), attrs);
            if (!file.isRegularFile()) {
                throw new FileSystemException(path.toString(), null, "not a regular file");
            }
            return open((RegularFile)file, options);
        }
        finally {
            this.store.writeLock().unlock();
        }
    }
    
    private static RegularFile open(final RegularFile file, final Set<OpenOption> options) {
        if (options.contains(StandardOpenOption.TRUNCATE_EXISTING) && options.contains(StandardOpenOption.WRITE)) {
            file.writeLock().lock();
            try {
                file.truncate(0L);
            }
            finally {
                file.writeLock().unlock();
            }
        }
        file.opened();
        return file;
    }
    
    public JimfsPath readSymbolicLink(final JimfsPath path) throws IOException {
        if (!this.store.supportsFeature(Feature.SYMBOLIC_LINKS)) {
            throw new UnsupportedOperationException();
        }
        final SymbolicLink symbolicLink = (SymbolicLink)this.lookUpWithLock(path, Options.NOFOLLOW_LINKS).requireSymbolicLink(path).file();
        return symbolicLink.target();
    }
    
    public void checkAccess(final JimfsPath path) throws IOException {
        this.lookUpWithLock(path, Options.FOLLOW_LINKS).requireExists(path);
    }
    
    public void link(final JimfsPath link, final FileSystemView existingView, final JimfsPath existing) throws IOException {
        Preconditions.checkNotNull(link);
        Preconditions.checkNotNull(existingView);
        Preconditions.checkNotNull(existing);
        if (!this.store.supportsFeature(Feature.LINKS)) {
            throw new UnsupportedOperationException();
        }
        if (!this.isSameFileSystem(existingView)) {
            throw new FileSystemException(link.toString(), existing.toString(), "can't link: source and target are in different file system instances");
        }
        final Name linkName = link.name();
        this.store.writeLock().lock();
        try {
            final File existingFile = existingView.lookUp(existing, Options.FOLLOW_LINKS).requireExists(existing).file();
            if (!existingFile.isRegularFile()) {
                throw new FileSystemException(link.toString(), existing.toString(), "can't link: not a regular file");
            }
            final Directory linkParent = this.lookUp(link, Options.NOFOLLOW_LINKS).requireDoesNotExist(link).directory();
            linkParent.link(linkName, existingFile);
            linkParent.updateModifiedTime();
        }
        finally {
            this.store.writeLock().unlock();
        }
    }
    
    public void deleteFile(final JimfsPath path, final DeleteMode deleteMode) throws IOException {
        this.store.writeLock().lock();
        try {
            final DirectoryEntry entry = this.lookUp(path, Options.NOFOLLOW_LINKS).requireExists(path);
            this.delete(entry, deleteMode, path);
        }
        finally {
            this.store.writeLock().unlock();
        }
    }
    
    private void delete(final DirectoryEntry entry, final DeleteMode deleteMode, final JimfsPath pathForException) throws IOException {
        final Directory parent = entry.directory();
        final File file = entry.file();
        this.checkDeletable(file, deleteMode, pathForException);
        parent.unlink(entry.name());
        parent.updateModifiedTime();
        file.deleted();
    }
    
    private void checkDeletable(final File file, final DeleteMode mode, final Path path) throws IOException {
        if (file.isRootDirectory()) {
            throw new FileSystemException(path.toString(), null, "can't delete root directory");
        }
        if (file.isDirectory()) {
            if (mode == DeleteMode.NON_DIRECTORY_ONLY) {
                throw new FileSystemException(path.toString(), null, "can't delete: is a directory");
            }
            this.checkEmpty((Directory)file, path);
        }
        else if (mode == DeleteMode.DIRECTORY_ONLY) {
            throw new FileSystemException(path.toString(), null, "can't delete: is not a directory");
        }
        if (file == this.workingDirectory && !path.isAbsolute()) {
            throw new FileSystemException(path.toString(), null, "invalid argument");
        }
    }
    
    private void checkEmpty(final Directory dir, final Path pathForException) throws FileSystemException {
        if (!dir.isEmpty()) {
            throw new DirectoryNotEmptyException(pathForException.toString());
        }
    }
    
    public void copy(final JimfsPath source, final FileSystemView destView, final JimfsPath dest, final Set<CopyOption> options, final boolean move) throws IOException {
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(destView);
        Preconditions.checkNotNull(dest);
        Preconditions.checkNotNull(options);
        final boolean sameFileSystem = this.isSameFileSystem(destView);
        File copyFile = null;
        lockBoth(this.store.writeLock(), destView.store.writeLock());
        File sourceFile;
        try {
            final DirectoryEntry sourceEntry = this.lookUp(source, options).requireExists(source);
            final DirectoryEntry destEntry = destView.lookUp(dest, Options.NOFOLLOW_LINKS);
            final Directory sourceParent = sourceEntry.directory();
            sourceFile = sourceEntry.file();
            final Directory destParent = destEntry.directory();
            if (move && sourceFile.isDirectory()) {
                if (sameFileSystem) {
                    this.checkMovable(sourceFile, source);
                    this.checkNotAncestor(sourceFile, destParent, destView);
                }
                else {
                    this.checkDeletable(sourceFile, DeleteMode.ANY, source);
                }
            }
            if (destEntry.exists()) {
                if (destEntry.file().equals(sourceFile)) {
                    return;
                }
                if (!options.contains(StandardCopyOption.REPLACE_EXISTING)) {
                    throw new FileAlreadyExistsException(dest.toString());
                }
                destView.delete(destEntry, DeleteMode.ANY, dest);
            }
            if (move && sameFileSystem) {
                sourceParent.unlink(source.name());
                sourceParent.updateModifiedTime();
                destParent.link(dest.name(), sourceFile);
                destParent.updateModifiedTime();
            }
            else {
                AttributeCopyOption attributeCopyOption = AttributeCopyOption.NONE;
                if (move) {
                    attributeCopyOption = AttributeCopyOption.BASIC;
                }
                else if (options.contains(StandardCopyOption.COPY_ATTRIBUTES)) {
                    attributeCopyOption = (sameFileSystem ? AttributeCopyOption.ALL : AttributeCopyOption.BASIC);
                }
                copyFile = destView.store.copyWithoutContent(sourceFile, attributeCopyOption);
                destParent.link(dest.name(), copyFile);
                destParent.updateModifiedTime();
                this.lockSourceAndCopy(sourceFile, copyFile);
                if (move) {
                    this.delete(sourceEntry, DeleteMode.ANY, source);
                }
            }
        }
        finally {
            destView.store.writeLock().unlock();
            this.store.writeLock().unlock();
        }
        if (copyFile != null) {
            try {
                sourceFile.copyContentTo(copyFile);
            }
            finally {
                this.unlockSourceAndCopy(sourceFile, copyFile);
            }
        }
    }
    
    private void checkMovable(final File file, final JimfsPath path) throws FileSystemException {
        if (file.isRootDirectory()) {
            throw new FileSystemException(path.toString(), null, "can't move root directory");
        }
    }
    
    private static void lockBoth(final Lock sourceWriteLock, final Lock destWriteLock) {
        while (true) {
            sourceWriteLock.lock();
            if (destWriteLock.tryLock()) {
                return;
            }
            sourceWriteLock.unlock();
            destWriteLock.lock();
            if (sourceWriteLock.tryLock()) {
                return;
            }
            destWriteLock.unlock();
        }
    }
    
    private void checkNotAncestor(final File source, final Directory destParent, final FileSystemView destView) throws IOException {
        if (!this.isSameFileSystem(destView)) {
            return;
        }
        for (Directory current = destParent; !current.equals(source); current = current.parent()) {
            if (current.isRootDirectory()) {
                return;
            }
        }
        throw new IOException("invalid argument: can't move directory into a subdirectory of itself");
    }
    
    private void lockSourceAndCopy(final File sourceFile, final File copyFile) {
        sourceFile.opened();
        final ReadWriteLock sourceLock = sourceFile.contentLock();
        if (sourceLock != null) {
            sourceLock.readLock().lock();
        }
        final ReadWriteLock copyLock = copyFile.contentLock();
        if (copyLock != null) {
            copyLock.writeLock().lock();
        }
    }
    
    private void unlockSourceAndCopy(final File sourceFile, final File copyFile) {
        final ReadWriteLock sourceLock = sourceFile.contentLock();
        if (sourceLock != null) {
            sourceLock.readLock().unlock();
        }
        final ReadWriteLock copyLock = copyFile.contentLock();
        if (copyLock != null) {
            copyLock.writeLock().unlock();
        }
        sourceFile.closed();
    }
    
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(final FileLookup lookup, final Class<V> type) {
        return this.store.getFileAttributeView(lookup, type);
    }
    
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(final JimfsPath path, final Class<V> type, final Set<? super LinkOption> options) {
        return this.store.getFileAttributeView(new FileLookup() {
            @Override
            public File lookup() throws IOException {
                return FileSystemView.this.lookUpWithLock(path, options).requireExists(path).file();
            }
        }, type);
    }
    
    public <A extends BasicFileAttributes> A readAttributes(final JimfsPath path, final Class<A> type, final Set<? super LinkOption> options) throws IOException {
        final File file = this.lookUpWithLock(path, options).requireExists(path).file();
        return this.store.readAttributes(file, type);
    }
    
    public ImmutableMap<String, Object> readAttributes(final JimfsPath path, final String attributes, final Set<? super LinkOption> options) throws IOException {
        final File file = this.lookUpWithLock(path, options).requireExists(path).file();
        return this.store.readAttributes(file, attributes);
    }
    
    public void setAttribute(final JimfsPath path, final String attribute, final Object value, final Set<? super LinkOption> options) throws IOException {
        final File file = this.lookUpWithLock(path, options).requireExists(path).file();
        this.store.setAttribute(file, attribute, value);
    }
    
    public enum DeleteMode
    {
        ANY, 
        NON_DIRECTORY_ONLY, 
        DIRECTORY_ONLY;
    }
}

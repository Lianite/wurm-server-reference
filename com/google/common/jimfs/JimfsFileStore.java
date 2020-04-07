// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import com.google.common.collect.ImmutableMap;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileAttribute;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSortedSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.Lock;
import com.google.common.collect.ImmutableSet;
import java.nio.file.FileStore;

final class JimfsFileStore extends FileStore
{
    private final FileTree tree;
    private final HeapDisk disk;
    private final AttributeService attributes;
    private final FileFactory factory;
    private final ImmutableSet<Feature> supportedFeatures;
    private final FileSystemState state;
    private final Lock readLock;
    private final Lock writeLock;
    
    public JimfsFileStore(final FileTree tree, final FileFactory factory, final HeapDisk disk, final AttributeService attributes, final ImmutableSet<Feature> supportedFeatures, final FileSystemState state) {
        this.tree = Preconditions.checkNotNull(tree);
        this.factory = Preconditions.checkNotNull(factory);
        this.disk = Preconditions.checkNotNull(disk);
        this.attributes = Preconditions.checkNotNull(attributes);
        this.supportedFeatures = Preconditions.checkNotNull(supportedFeatures);
        this.state = Preconditions.checkNotNull(state);
        final ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    FileSystemState state() {
        return this.state;
    }
    
    Lock readLock() {
        return this.readLock;
    }
    
    Lock writeLock() {
        return this.writeLock;
    }
    
    ImmutableSortedSet<Name> getRootDirectoryNames() {
        this.state.checkOpen();
        return this.tree.getRootDirectoryNames();
    }
    
    @Nullable
    Directory getRoot(final Name name) {
        final DirectoryEntry entry = this.tree.getRoot(name);
        return (entry == null) ? null : ((Directory)entry.file());
    }
    
    boolean supportsFeature(final Feature feature) {
        return this.supportedFeatures.contains(feature);
    }
    
    DirectoryEntry lookUp(final File workingDirectory, final JimfsPath path, final Set<? super LinkOption> options) throws IOException {
        this.state.checkOpen();
        return this.tree.lookUp(workingDirectory, path, options);
    }
    
    Supplier<RegularFile> regularFileCreator() {
        this.state.checkOpen();
        return this.factory.regularFileCreator();
    }
    
    Supplier<Directory> directoryCreator() {
        this.state.checkOpen();
        return this.factory.directoryCreator();
    }
    
    Supplier<SymbolicLink> symbolicLinkCreator(final JimfsPath target) {
        this.state.checkOpen();
        return this.factory.symbolicLinkCreator(target);
    }
    
    File copyWithoutContent(final File file, final AttributeCopyOption attributeCopyOption) throws IOException {
        final File copy = this.factory.copyWithoutContent(file);
        this.setInitialAttributes(copy, (FileAttribute<?>[])new FileAttribute[0]);
        this.attributes.copyAttributes(file, copy, attributeCopyOption);
        return copy;
    }
    
    void setInitialAttributes(final File file, final FileAttribute<?>... attrs) {
        this.state.checkOpen();
        this.attributes.setInitialAttributes(file, attrs);
    }
    
    @Nullable
     <V extends FileAttributeView> V getFileAttributeView(final FileLookup lookup, final Class<V> type) {
        this.state.checkOpen();
        return this.attributes.getFileAttributeView(lookup, type);
    }
    
    ImmutableMap<String, Object> readAttributes(final File file, final String attributes) {
        this.state.checkOpen();
        return this.attributes.readAttributes(file, attributes);
    }
    
     <A extends BasicFileAttributes> A readAttributes(final File file, final Class<A> type) {
        this.state.checkOpen();
        return this.attributes.readAttributes(file, type);
    }
    
    void setAttribute(final File file, final String attribute, final Object value) {
        this.state.checkOpen();
        this.attributes.setAttribute(file, attribute, value, false);
    }
    
    ImmutableSet<String> supportedFileAttributeViews() {
        this.state.checkOpen();
        return this.attributes.supportedFileAttributeViews();
    }
    
    @Override
    public String name() {
        return "jimfs";
    }
    
    @Override
    public String type() {
        return "jimfs";
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public long getTotalSpace() throws IOException {
        this.state.checkOpen();
        return this.disk.getTotalSpace();
    }
    
    @Override
    public long getUsableSpace() throws IOException {
        this.state.checkOpen();
        return this.getUnallocatedSpace();
    }
    
    @Override
    public long getUnallocatedSpace() throws IOException {
        this.state.checkOpen();
        return this.disk.getUnallocatedSpace();
    }
    
    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        this.state.checkOpen();
        return this.attributes.supportsFileAttributeView(type);
    }
    
    @Override
    public boolean supportsFileAttributeView(final String name) {
        this.state.checkOpen();
        return this.attributes.supportedFileAttributeViews().contains(name);
    }
    
    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> type) {
        this.state.checkOpen();
        return null;
    }
    
    @Override
    public Object getAttribute(final String attribute) throws IOException {
        throw new UnsupportedOperationException();
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Comparator;
import com.google.common.collect.ComparisonChain;
import java.util.AbstractList;
import java.io.File;
import java.net.URI;
import java.nio.file.Watchable;
import java.util.Arrays;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.io.IOException;
import java.util.Set;
import java.nio.file.LinkOption;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.nio.file.ProviderMismatchException;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.List;
import java.nio.file.FileSystem;
import com.google.common.collect.Iterables;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import java.nio.file.Path;

final class JimfsPath implements Path
{
    @Nullable
    private final Name root;
    private final ImmutableList<Name> names;
    private final PathService pathService;
    
    public JimfsPath(final PathService pathService, @Nullable final Name root, final Iterable<Name> names) {
        this.pathService = Preconditions.checkNotNull(pathService);
        this.root = root;
        this.names = ImmutableList.copyOf((Iterable<? extends Name>)names);
    }
    
    @Nullable
    public Name root() {
        return this.root;
    }
    
    public ImmutableList<Name> names() {
        return this.names;
    }
    
    @Nullable
    public Name name() {
        if (!this.names.isEmpty()) {
            return Iterables.getLast(this.names);
        }
        return this.root;
    }
    
    public boolean isEmptyPath() {
        return this.root == null && this.names.size() == 1 && this.names.get(0).toString().isEmpty();
    }
    
    @Override
    public FileSystem getFileSystem() {
        return this.pathService.getFileSystem();
    }
    
    public JimfsFileSystem getJimfsFileSystem() {
        return (JimfsFileSystem)this.pathService.getFileSystem();
    }
    
    @Override
    public boolean isAbsolute() {
        return this.root != null;
    }
    
    @Override
    public JimfsPath getRoot() {
        if (this.root == null) {
            return null;
        }
        return this.pathService.createRoot(this.root);
    }
    
    @Override
    public JimfsPath getFileName() {
        return this.names.isEmpty() ? null : this.getName(this.names.size() - 1);
    }
    
    @Override
    public JimfsPath getParent() {
        if (this.names.isEmpty() || (this.names.size() == 1 && this.root == null)) {
            return null;
        }
        return this.pathService.createPath(this.root, this.names.subList(0, this.names.size() - 1));
    }
    
    @Override
    public int getNameCount() {
        return this.names.size();
    }
    
    @Override
    public JimfsPath getName(final int index) {
        Preconditions.checkArgument(index >= 0 && index < this.names.size(), "index (%s) must be >= 0 and < name count (%s)", index, this.names.size());
        return this.pathService.createFileName(this.names.get(index));
    }
    
    @Override
    public JimfsPath subpath(final int beginIndex, final int endIndex) {
        Preconditions.checkArgument(beginIndex >= 0 && endIndex <= this.names.size() && endIndex > beginIndex, "beginIndex (%s) must be >= 0; endIndex (%s) must be <= name count (%s) and > beginIndex", beginIndex, endIndex, this.names.size());
        return this.pathService.createRelativePath(this.names.subList(beginIndex, endIndex));
    }
    
    private static boolean startsWith(final List<?> list, final List<?> other) {
        return list.size() >= other.size() && list.subList(0, other.size()).equals(other);
    }
    
    @Override
    public boolean startsWith(final Path other) {
        final JimfsPath otherPath = this.checkPath(other);
        return otherPath != null && this.getFileSystem().equals(otherPath.getFileSystem()) && Objects.equals(this.root, otherPath.root) && startsWith(this.names, otherPath.names);
    }
    
    @Override
    public boolean startsWith(final String other) {
        return this.startsWith(this.pathService.parsePath(other, new String[0]));
    }
    
    @Override
    public boolean endsWith(final Path other) {
        final JimfsPath otherPath = this.checkPath(other);
        if (otherPath == null) {
            return false;
        }
        if (otherPath.isAbsolute()) {
            return this.compareTo((Path)otherPath) == 0;
        }
        return startsWith(this.names.reverse(), otherPath.names.reverse());
    }
    
    @Override
    public boolean endsWith(final String other) {
        return this.endsWith(this.pathService.parsePath(other, new String[0]));
    }
    
    @Override
    public JimfsPath normalize() {
        if (this.isNormal()) {
            return this;
        }
        final Deque<Name> newNames = new ArrayDeque<Name>();
        for (final Name name : this.names) {
            if (name.equals(Name.PARENT)) {
                final Name lastName = newNames.peekLast();
                if (lastName != null && !lastName.equals(Name.PARENT)) {
                    newNames.removeLast();
                }
                else {
                    if (this.isAbsolute()) {
                        continue;
                    }
                    newNames.add(name);
                }
            }
            else {
                if (name.equals(Name.SELF)) {
                    continue;
                }
                newNames.add(name);
            }
        }
        return newNames.equals(this.names) ? this : this.pathService.createPath(this.root, newNames);
    }
    
    private boolean isNormal() {
        if (this.getNameCount() == 0 || (this.getNameCount() == 1 && !this.isAbsolute())) {
            return true;
        }
        boolean foundNonParentName = this.isAbsolute();
        boolean normal = true;
        for (final Name name : this.names) {
            if (name.equals(Name.PARENT)) {
                if (foundNonParentName) {
                    normal = false;
                    break;
                }
                continue;
            }
            else {
                if (name.equals(Name.SELF)) {
                    normal = false;
                    break;
                }
                foundNonParentName = true;
            }
        }
        return normal;
    }
    
    JimfsPath resolve(final Name name) {
        if (name.toString().isEmpty()) {
            return this;
        }
        return this.pathService.createPathInternal(this.root, ImmutableList.builder().addAll(this.names).add(name).build());
    }
    
    @Override
    public JimfsPath resolve(final Path other) {
        final JimfsPath otherPath = this.checkPath(other);
        if (otherPath == null) {
            throw new ProviderMismatchException(other.toString());
        }
        if (this.isEmptyPath() || otherPath.isAbsolute()) {
            return otherPath;
        }
        if (otherPath.isEmptyPath()) {
            return this;
        }
        return this.pathService.createPath(this.root, (Iterable<Name>)ImmutableList.builder().addAll(this.names).addAll(otherPath.names).build());
    }
    
    @Override
    public JimfsPath resolve(final String other) {
        return this.resolve((Path)this.pathService.parsePath(other, new String[0]));
    }
    
    @Override
    public JimfsPath resolveSibling(final Path other) {
        final JimfsPath otherPath = this.checkPath(other);
        if (otherPath == null) {
            throw new ProviderMismatchException(other.toString());
        }
        if (otherPath.isAbsolute()) {
            return otherPath;
        }
        final JimfsPath parent = this.getParent();
        if (parent == null) {
            return otherPath;
        }
        return parent.resolve(other);
    }
    
    @Override
    public JimfsPath resolveSibling(final String other) {
        return this.resolveSibling((Path)this.pathService.parsePath(other, new String[0]));
    }
    
    @Override
    public JimfsPath relativize(final Path other) {
        final JimfsPath otherPath = this.checkPath(other);
        if (otherPath == null) {
            throw new ProviderMismatchException(other.toString());
        }
        Preconditions.checkArgument(Objects.equals(this.root, otherPath.root), "Paths have different roots: %s, %s", this, other);
        if (this.equals(other)) {
            return this.pathService.emptyPath();
        }
        if (this.isEmptyPath()) {
            return otherPath;
        }
        final ImmutableList<Name> otherNames = otherPath.names;
        int sharedSubsequenceLength = 0;
        for (int i = 0; i < Math.min(this.getNameCount(), otherNames.size()) && this.names.get(i).equals(otherNames.get(i)); ++i) {
            ++sharedSubsequenceLength;
        }
        final int extraNamesInThis = Math.max(0, this.getNameCount() - sharedSubsequenceLength);
        final ImmutableList<Name> extraNamesInOther = (otherNames.size() <= sharedSubsequenceLength) ? ImmutableList.of() : otherNames.subList(sharedSubsequenceLength, otherNames.size());
        final List<Name> parts = new ArrayList<Name>(extraNamesInThis + extraNamesInOther.size());
        parts.addAll(Collections.nCopies(extraNamesInThis, Name.PARENT));
        parts.addAll(extraNamesInOther);
        return this.pathService.createRelativePath(parts);
    }
    
    @Override
    public JimfsPath toAbsolutePath() {
        return this.isAbsolute() ? this : this.getJimfsFileSystem().getWorkingDirectory().resolve((Path)this);
    }
    
    @Override
    public JimfsPath toRealPath(final LinkOption... options) throws IOException {
        return this.getJimfsFileSystem().getDefaultView().toRealPath(this, this.pathService, Options.getLinkOptions(options));
    }
    
    @Override
    public WatchKey register(final WatchService watcher, final WatchEvent.Kind<?>[] events, final WatchEvent.Modifier... modifiers) throws IOException {
        Preconditions.checkNotNull(modifiers);
        return this.register(watcher, events);
    }
    
    @Override
    public WatchKey register(final WatchService watcher, final WatchEvent.Kind<?>... events) throws IOException {
        Preconditions.checkNotNull(watcher);
        Preconditions.checkNotNull(events);
        if (!(watcher instanceof AbstractWatchService)) {
            throw new IllegalArgumentException("watcher (" + watcher + ") is not associated with this file system");
        }
        final AbstractWatchService service = (AbstractWatchService)watcher;
        return service.register(this, Arrays.asList(events));
    }
    
    @Override
    public URI toUri() {
        return this.getJimfsFileSystem().toUri(this);
    }
    
    @Override
    public File toFile() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<Path> iterator() {
        return this.asList().iterator();
    }
    
    private List<Path> asList() {
        return new AbstractList<Path>() {
            @Override
            public Path get(final int index) {
                return JimfsPath.this.getName(index);
            }
            
            @Override
            public int size() {
                return JimfsPath.this.getNameCount();
            }
        };
    }
    
    @Override
    public int compareTo(final Path other) {
        final JimfsPath otherPath = (JimfsPath)other;
        return ComparisonChain.start().compare(this.getJimfsFileSystem().getUri(), ((JimfsPath)other).getJimfsFileSystem().getUri()).compare(this, otherPath, this.pathService).result();
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        return obj instanceof JimfsPath && this.compareTo((Path)obj) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.pathService.hash(this);
    }
    
    @Override
    public String toString() {
        return this.pathService.toString(this);
    }
    
    @Nullable
    private JimfsPath checkPath(final Path other) {
        if (Preconditions.checkNotNull(other) instanceof JimfsPath && other.getFileSystem().equals(this.getFileSystem())) {
            return (JimfsPath)other;
        }
        return null;
    }
}

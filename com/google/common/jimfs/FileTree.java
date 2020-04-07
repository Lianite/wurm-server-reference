// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Iterator;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import com.google.common.base.Preconditions;
import java.nio.file.LinkOption;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;
import java.util.Map;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableList;

final class FileTree
{
    private static final int MAX_SYMBOLIC_LINK_DEPTH = 40;
    private static final ImmutableList<Name> EMPTY_PATH_NAMES;
    private final ImmutableSortedMap<Name, Directory> roots;
    
    FileTree(final Map<Name, Directory> roots) {
        this.roots = ImmutableSortedMap.copyOf((Map<? extends Name, ? extends Directory>)roots, (Comparator<? super Name>)Name.canonicalOrdering());
    }
    
    public ImmutableSortedSet<Name> getRootDirectoryNames() {
        return this.roots.keySet();
    }
    
    @Nullable
    public DirectoryEntry getRoot(final Name name) {
        final Directory dir = this.roots.get(name);
        return (dir == null) ? null : dir.entryInParent();
    }
    
    public DirectoryEntry lookUp(final File workingDirectory, final JimfsPath path, final Set<? super LinkOption> options) throws IOException {
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(options);
        final DirectoryEntry result = this.lookUp(workingDirectory, path, options, 0);
        if (result == null) {
            throw new NoSuchFileException(path.toString());
        }
        return result;
    }
    
    @Nullable
    private DirectoryEntry lookUp(File dir, final JimfsPath path, final Set<? super LinkOption> options, final int linkDepth) throws IOException {
        ImmutableList<Name> names = path.names();
        if (path.isAbsolute()) {
            final DirectoryEntry entry = this.getRoot(path.root());
            if (entry == null) {
                return null;
            }
            if (names.isEmpty()) {
                return entry;
            }
            dir = entry.file();
        }
        else if (isEmpty(names)) {
            names = FileTree.EMPTY_PATH_NAMES;
        }
        return this.lookUp(dir, names, options, linkDepth);
    }
    
    @Nullable
    private DirectoryEntry lookUp(File dir, final Iterable<Name> names, final Set<? super LinkOption> options, final int linkDepth) throws IOException {
        final Iterator<Name> nameIterator = names.iterator();
        Name name = nameIterator.next();
        while (nameIterator.hasNext()) {
            final Directory directory = this.toDirectory(dir);
            if (directory == null) {
                return null;
            }
            final DirectoryEntry entry = directory.get(name);
            if (entry == null) {
                return null;
            }
            final File file = entry.file();
            if (file.isSymbolicLink()) {
                final DirectoryEntry linkResult = this.followSymbolicLink(dir, (SymbolicLink)file, linkDepth);
                if (linkResult == null) {
                    return null;
                }
                dir = linkResult.fileOrNull();
            }
            else {
                dir = file;
            }
            name = nameIterator.next();
        }
        return this.lookUpLast(dir, name, options, linkDepth);
    }
    
    @Nullable
    private DirectoryEntry lookUpLast(@Nullable final File dir, final Name name, final Set<? super LinkOption> options, final int linkDepth) throws IOException {
        final Directory directory = this.toDirectory(dir);
        if (directory == null) {
            return null;
        }
        final DirectoryEntry entry = directory.get(name);
        if (entry == null) {
            return new DirectoryEntry(directory, name, null);
        }
        final File file = entry.file();
        if (!options.contains(LinkOption.NOFOLLOW_LINKS) && file.isSymbolicLink()) {
            return this.followSymbolicLink(dir, (SymbolicLink)file, linkDepth);
        }
        return this.getRealEntry(entry);
    }
    
    @Nullable
    private DirectoryEntry followSymbolicLink(final File dir, final SymbolicLink link, final int linkDepth) throws IOException {
        if (linkDepth >= 40) {
            throw new IOException("too many levels of symbolic links");
        }
        return this.lookUp(dir, link.target(), Options.FOLLOW_LINKS, linkDepth + 1);
    }
    
    @Nullable
    private DirectoryEntry getRealEntry(final DirectoryEntry entry) {
        final Name name = entry.name();
        if (!name.equals(Name.SELF) && !name.equals(Name.PARENT)) {
            return entry;
        }
        final Directory dir = this.toDirectory(entry.file());
        assert dir != null;
        return dir.entryInParent();
    }
    
    @Nullable
    private Directory toDirectory(@Nullable final File file) {
        return (file == null || !file.isDirectory()) ? null : ((Directory)file);
    }
    
    private static boolean isEmpty(final ImmutableList<Name> names) {
        return names.isEmpty() || (names.size() == 1 && names.get(0).toString().isEmpty());
    }
    
    static {
        EMPTY_PATH_NAMES = ImmutableList.of(Name.SELF);
    }
}

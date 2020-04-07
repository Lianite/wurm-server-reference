// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.nio.file.NotLinkException;
import java.nio.file.NotDirectoryException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

final class DirectoryEntry
{
    private final Directory directory;
    private final Name name;
    @Nullable
    private final File file;
    @Nullable
    DirectoryEntry next;
    
    DirectoryEntry(final Directory directory, final Name name, @Nullable final File file) {
        this.directory = Preconditions.checkNotNull(directory);
        this.name = Preconditions.checkNotNull(name);
        this.file = file;
    }
    
    public boolean exists() {
        return this.file != null;
    }
    
    public DirectoryEntry requireExists(final Path pathForException) throws NoSuchFileException {
        if (!this.exists()) {
            throw new NoSuchFileException(pathForException.toString());
        }
        return this;
    }
    
    public DirectoryEntry requireDoesNotExist(final Path pathForException) throws FileAlreadyExistsException {
        if (this.exists()) {
            throw new FileAlreadyExistsException(pathForException.toString());
        }
        return this;
    }
    
    public DirectoryEntry requireDirectory(final Path pathForException) throws NoSuchFileException, NotDirectoryException {
        this.requireExists(pathForException);
        if (!this.file().isDirectory()) {
            throw new NotDirectoryException(pathForException.toString());
        }
        return this;
    }
    
    public DirectoryEntry requireSymbolicLink(final Path pathForException) throws NoSuchFileException, NotLinkException {
        this.requireExists(pathForException);
        if (!this.file().isSymbolicLink()) {
            throw new NotLinkException(pathForException.toString());
        }
        return this;
    }
    
    public Directory directory() {
        return this.directory;
    }
    
    public Name name() {
        return this.name;
    }
    
    public File file() {
        Preconditions.checkState(this.exists());
        return this.file;
    }
    
    @Nullable
    public File fileOrNull() {
        return this.file;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DirectoryEntry) {
            final DirectoryEntry other = (DirectoryEntry)obj;
            return this.directory.equals(other.directory) && this.name.equals(other.name) && Objects.equals(this.file, other.file);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.directory, this.name, this.file);
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("directory", this.directory).add("name", this.name).add("file", this.file).toString();
    }
}

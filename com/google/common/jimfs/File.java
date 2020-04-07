// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.ReadWriteLock;
import java.io.IOException;
import javax.annotation.Nullable;
import com.google.common.collect.Table;

public abstract class File
{
    private final int id;
    private int links;
    private long creationTime;
    private long lastAccessTime;
    private long lastModifiedTime;
    @Nullable
    private Table<String, String, Object> attributes;
    
    File(final int id) {
        this.id = id;
        final long now = System.currentTimeMillis();
        this.creationTime = now;
        this.lastAccessTime = now;
        this.lastModifiedTime = now;
    }
    
    public int id() {
        return this.id;
    }
    
    public long size() {
        return 0L;
    }
    
    public final boolean isDirectory() {
        return this instanceof Directory;
    }
    
    public final boolean isRegularFile() {
        return this instanceof RegularFile;
    }
    
    public final boolean isSymbolicLink() {
        return this instanceof SymbolicLink;
    }
    
    abstract File copyWithoutContent(final int p0);
    
    void copyContentTo(final File file) throws IOException {
    }
    
    @Nullable
    ReadWriteLock contentLock() {
        return null;
    }
    
    void opened() {
    }
    
    void closed() {
    }
    
    void deleted() {
    }
    
    final boolean isRootDirectory() {
        return this.isDirectory() && this.equals(((Directory)this).parent());
    }
    
    public final synchronized int links() {
        return this.links;
    }
    
    void linked(final DirectoryEntry entry) {
        Preconditions.checkNotNull(entry);
    }
    
    void unlinked() {
    }
    
    final synchronized void incrementLinkCount() {
        ++this.links;
    }
    
    final synchronized void decrementLinkCount() {
        --this.links;
    }
    
    public final synchronized long getCreationTime() {
        return this.creationTime;
    }
    
    public final synchronized long getLastAccessTime() {
        return this.lastAccessTime;
    }
    
    public final synchronized long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    final synchronized void setCreationTime(final long creationTime) {
        this.creationTime = creationTime;
    }
    
    final synchronized void setLastAccessTime(final long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    
    final synchronized void setLastModifiedTime(final long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    
    final void updateAccessTime() {
        this.setLastAccessTime(System.currentTimeMillis());
    }
    
    final void updateModifiedTime() {
        this.setLastModifiedTime(System.currentTimeMillis());
    }
    
    public final synchronized ImmutableSet<String> getAttributeNames(final String view) {
        if (this.attributes == null) {
            return ImmutableSet.of();
        }
        return ImmutableSet.copyOf((Collection<? extends String>)this.attributes.row(view).keySet());
    }
    
    @VisibleForTesting
    final synchronized ImmutableSet<String> getAttributeKeys() {
        if (this.attributes == null) {
            return ImmutableSet.of();
        }
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (final Table.Cell<String, String, Object> cell : this.attributes.cellSet()) {
            builder.add(cell.getRowKey() + ':' + cell.getColumnKey());
        }
        return builder.build();
    }
    
    @Nullable
    public final synchronized Object getAttribute(final String view, final String attribute) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(view, attribute);
    }
    
    public final synchronized void setAttribute(final String view, final String attribute, final Object value) {
        if (this.attributes == null) {
            this.attributes = (Table<String, String, Object>)HashBasedTable.create();
        }
        this.attributes.put(view, attribute, value);
    }
    
    public final synchronized void deleteAttribute(final String view, final String attribute) {
        if (this.attributes != null) {
            this.attributes.remove(view, attribute);
        }
    }
    
    final synchronized void copyBasicAttributes(final File target) {
        target.setFileTimes(this.creationTime, this.lastModifiedTime, this.lastAccessTime);
    }
    
    private synchronized void setFileTimes(final long creationTime, final long lastModifiedTime, final long lastAccessTime) {
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
        this.lastAccessTime = lastAccessTime;
    }
    
    final synchronized void copyAttributes(final File target) {
        this.copyBasicAttributes(target);
        target.putAll(this.attributes);
    }
    
    private synchronized void putAll(@Nullable final Table<String, String, Object> attributes) {
        if (attributes != null && this.attributes != attributes) {
            if (this.attributes == null) {
                this.attributes = (Table<String, String, Object>)HashBasedTable.create();
            }
            this.attributes.putAll(attributes);
        }
    }
    
    @Override
    public final String toString() {
        return MoreObjects.toStringHelper(this).add("id", this.id()).toString();
    }
}

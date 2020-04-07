// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import java.util.Comparator;
import com.google.common.collect.ImmutableSortedSet;
import javax.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;

final class Directory extends File implements Iterable<DirectoryEntry>
{
    private DirectoryEntry entryInParent;
    private static final int INITIAL_CAPACITY = 16;
    private static final int INITIAL_RESIZE_THRESHOLD = 12;
    private DirectoryEntry[] table;
    private int resizeThreshold;
    private int entryCount;
    
    public static Directory create(final int id) {
        return new Directory(id);
    }
    
    public static Directory createRoot(final int id, final Name name) {
        return new Directory(id, name);
    }
    
    private Directory(final int id) {
        super(id);
        this.table = new DirectoryEntry[16];
        this.resizeThreshold = 12;
        this.put(new DirectoryEntry(this, Name.SELF, this));
    }
    
    private Directory(final int id, final Name rootName) {
        this(id);
        this.linked(new DirectoryEntry(this, rootName, this));
    }
    
    @Override
    Directory copyWithoutContent(final int id) {
        return create(id);
    }
    
    public DirectoryEntry entryInParent() {
        return this.entryInParent;
    }
    
    public Directory parent() {
        return this.entryInParent.directory();
    }
    
    @Override
    void linked(final DirectoryEntry entry) {
        final File parent = entry.directory();
        this.entryInParent = entry;
        this.forcePut(new DirectoryEntry(this, Name.PARENT, parent));
    }
    
    @Override
    void unlinked() {
        this.parent().decrementLinkCount();
    }
    
    @VisibleForTesting
    int entryCount() {
        return this.entryCount;
    }
    
    public boolean isEmpty() {
        return this.entryCount() == 2;
    }
    
    @Nullable
    public DirectoryEntry get(final Name name) {
        final int index = bucketIndex(name, this.table.length);
        for (DirectoryEntry entry = this.table[index]; entry != null; entry = entry.next) {
            if (name.equals(entry.name())) {
                return entry;
            }
        }
        return null;
    }
    
    public void link(final Name name, final File file) {
        final DirectoryEntry entry = new DirectoryEntry(this, checkNotReserved(name, "link"), file);
        this.put(entry);
        file.linked(entry);
    }
    
    public void unlink(final Name name) {
        final DirectoryEntry entry = this.remove(checkNotReserved(name, "unlink"));
        entry.file().unlinked();
    }
    
    public ImmutableSortedSet<Name> snapshot() {
        final ImmutableSortedSet.Builder<Name> builder = new ImmutableSortedSet.Builder<Name>(Name.displayOrdering());
        for (final DirectoryEntry entry : this) {
            if (!isReserved(entry.name())) {
                builder.add(entry.name());
            }
        }
        return builder.build();
    }
    
    private static Name checkNotReserved(final Name name, final String action) {
        if (isReserved(name)) {
            throw new IllegalArgumentException("cannot " + action + ": " + name);
        }
        return name;
    }
    
    private static boolean isReserved(final Name name) {
        return name == Name.SELF || name == Name.PARENT;
    }
    
    private static int bucketIndex(final Name name, final int tableLength) {
        return name.hashCode() & tableLength - 1;
    }
    
    @VisibleForTesting
    void put(final DirectoryEntry entry) {
        this.put(entry, false);
    }
    
    private void forcePut(final DirectoryEntry entry) {
        this.put(entry, true);
    }
    
    private void put(final DirectoryEntry entry, final boolean overwriteExisting) {
        int index = bucketIndex(entry.name(), this.table.length);
        DirectoryEntry prev = null;
        DirectoryEntry curr = this.table[index];
        while (curr != null) {
            if (curr.name().equals(entry.name())) {
                if (overwriteExisting) {
                    if (prev != null) {
                        prev.next = entry;
                    }
                    else {
                        this.table[index] = entry;
                    }
                    entry.next = curr.next;
                    curr.next = null;
                    entry.file().incrementLinkCount();
                    return;
                }
                throw new IllegalArgumentException("entry '" + entry.name() + "' already exists");
            }
            else {
                prev = curr;
                curr = curr.next;
            }
        }
        ++this.entryCount;
        if (this.expandIfNeeded()) {
            index = bucketIndex(entry.name(), this.table.length);
            addToBucket(index, this.table, entry);
        }
        else if (prev != null) {
            prev.next = entry;
        }
        else {
            this.table[index] = entry;
        }
        entry.file().incrementLinkCount();
    }
    
    private boolean expandIfNeeded() {
        if (this.entryCount <= this.resizeThreshold) {
            return false;
        }
        final DirectoryEntry[] newTable = new DirectoryEntry[this.table.length << 1];
        for (DirectoryEntry entry : this.table) {
            while (entry != null) {
                final int index = bucketIndex(entry.name(), newTable.length);
                addToBucket(index, newTable, entry);
                final DirectoryEntry next = entry.next;
                entry.next = null;
                entry = next;
            }
        }
        this.table = newTable;
        this.resizeThreshold <<= 1;
        return true;
    }
    
    private static void addToBucket(final int bucketIndex, final DirectoryEntry[] table, final DirectoryEntry entryToAdd) {
        DirectoryEntry prev = null;
        for (DirectoryEntry existing = table[bucketIndex]; existing != null; existing = existing.next) {
            prev = existing;
        }
        if (prev != null) {
            prev.next = entryToAdd;
        }
        else {
            table[bucketIndex] = entryToAdd;
        }
    }
    
    @VisibleForTesting
    DirectoryEntry remove(final Name name) {
        final int index = bucketIndex(name, this.table.length);
        DirectoryEntry prev = null;
        for (DirectoryEntry entry = this.table[index]; entry != null; entry = entry.next) {
            if (name.equals(entry.name())) {
                if (prev != null) {
                    prev.next = entry.next;
                }
                else {
                    this.table[index] = entry.next;
                }
                entry.next = null;
                --this.entryCount;
                entry.file().decrementLinkCount();
                return entry;
            }
            prev = entry;
        }
        throw new IllegalArgumentException("no entry matching '" + name + "' in this directory");
    }
    
    @Override
    public Iterator<DirectoryEntry> iterator() {
        return new AbstractIterator<DirectoryEntry>() {
            int index;
            @Nullable
            DirectoryEntry entry;
            
            @Override
            protected DirectoryEntry computeNext() {
                if (this.entry != null) {
                    this.entry = this.entry.next;
                }
                while (this.entry == null && this.index < Directory.this.table.length) {
                    this.entry = Directory.this.table[this.index++];
                }
                return (this.entry != null) ? this.entry : this.endOfData();
            }
        };
    }
}

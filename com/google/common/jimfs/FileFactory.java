// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.atomic.AtomicInteger;

final class FileFactory
{
    private final AtomicInteger idGenerator;
    private final HeapDisk disk;
    private final Supplier<Directory> directorySupplier;
    private final Supplier<RegularFile> regularFileSupplier;
    
    public FileFactory(final HeapDisk disk) {
        this.idGenerator = new AtomicInteger();
        this.directorySupplier = new DirectorySupplier();
        this.regularFileSupplier = new RegularFileSupplier();
        this.disk = Preconditions.checkNotNull(disk);
    }
    
    private int nextFileId() {
        return this.idGenerator.getAndIncrement();
    }
    
    public Directory createDirectory() {
        return Directory.create(this.nextFileId());
    }
    
    public Directory createRootDirectory(final Name name) {
        return Directory.createRoot(this.nextFileId(), name);
    }
    
    @VisibleForTesting
    RegularFile createRegularFile() {
        return RegularFile.create(this.nextFileId(), this.disk);
    }
    
    @VisibleForTesting
    SymbolicLink createSymbolicLink(final JimfsPath target) {
        return SymbolicLink.create(this.nextFileId(), target);
    }
    
    public File copyWithoutContent(final File file) throws IOException {
        return file.copyWithoutContent(this.nextFileId());
    }
    
    public Supplier<Directory> directoryCreator() {
        return this.directorySupplier;
    }
    
    public Supplier<RegularFile> regularFileCreator() {
        return this.regularFileSupplier;
    }
    
    public Supplier<SymbolicLink> symbolicLinkCreator(final JimfsPath target) {
        return new SymbolicLinkSupplier(target);
    }
    
    private final class DirectorySupplier implements Supplier<Directory>
    {
        @Override
        public Directory get() {
            return FileFactory.this.createDirectory();
        }
    }
    
    private final class RegularFileSupplier implements Supplier<RegularFile>
    {
        @Override
        public RegularFile get() {
            return FileFactory.this.createRegularFile();
        }
    }
    
    private final class SymbolicLinkSupplier implements Supplier<SymbolicLink>
    {
        private final JimfsPath target;
        
        protected SymbolicLinkSupplier(final JimfsPath target) {
            this.target = Preconditions.checkNotNull(target);
        }
        
        @Override
        public SymbolicLink get() {
            return FileFactory.this.createSymbolicLink(this.target);
        }
    }
}

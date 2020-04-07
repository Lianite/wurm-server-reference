// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import com.google.common.collect.ImmutableMap;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import com.google.common.collect.ImmutableSet;

final class BasicAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    
    @Override
    public String name() {
        return "basic";
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return BasicAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public Object get(final File file, final String attribute) {
        switch (attribute) {
            case "size": {
                return file.size();
            }
            case "fileKey": {
                return file.id();
            }
            case "isDirectory": {
                return file.isDirectory();
            }
            case "isRegularFile": {
                return file.isRegularFile();
            }
            case "isSymbolicLink": {
                return file.isSymbolicLink();
            }
            case "isOther": {
                return !file.isDirectory() && !file.isRegularFile() && !file.isSymbolicLink();
            }
            case "creationTime": {
                return FileTime.fromMillis(file.getCreationTime());
            }
            case "lastAccessTime": {
                return FileTime.fromMillis(file.getLastAccessTime());
            }
            case "lastModifiedTime": {
                return FileTime.fromMillis(file.getLastModifiedTime());
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        switch (attribute) {
            case "creationTime": {
                AttributeProvider.checkNotCreate(view, attribute, create);
                file.setCreationTime(AttributeProvider.checkType(view, attribute, value, FileTime.class).toMillis());
                break;
            }
            case "lastAccessTime": {
                AttributeProvider.checkNotCreate(view, attribute, create);
                file.setLastAccessTime(AttributeProvider.checkType(view, attribute, value, FileTime.class).toMillis());
                break;
            }
            case "lastModifiedTime": {
                AttributeProvider.checkNotCreate(view, attribute, create);
                file.setLastModifiedTime(AttributeProvider.checkType(view, attribute, value, FileTime.class).toMillis());
                break;
            }
            case "size":
            case "fileKey":
            case "isDirectory":
            case "isRegularFile":
            case "isSymbolicLink":
            case "isOther": {
                throw AttributeProvider.unsettable(view, attribute);
            }
        }
    }
    
    @Override
    public Class<BasicFileAttributeView> viewType() {
        return BasicFileAttributeView.class;
    }
    
    @Override
    public BasicFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup);
    }
    
    @Override
    public Class<BasicFileAttributes> attributesType() {
        return BasicFileAttributes.class;
    }
    
    @Override
    public BasicFileAttributes readAttributes(final File file) {
        return new Attributes(file);
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("size", "fileKey", "isDirectory", "isRegularFile", "isSymbolicLink", "isOther", "creationTime", "lastAccessTime", "lastModifiedTime");
    }
    
    private static final class View extends AbstractAttributeView implements BasicFileAttributeView
    {
        protected View(final FileLookup lookup) {
            super(lookup);
        }
        
        @Override
        public String name() {
            return "basic";
        }
        
        @Override
        public BasicFileAttributes readAttributes() throws IOException {
            return new Attributes(this.lookupFile());
        }
        
        @Override
        public void setTimes(@Nullable final FileTime lastModifiedTime, @Nullable final FileTime lastAccessTime, @Nullable final FileTime createTime) throws IOException {
            final File file = this.lookupFile();
            if (lastModifiedTime != null) {
                file.setLastModifiedTime(lastModifiedTime.toMillis());
            }
            if (lastAccessTime != null) {
                file.setLastAccessTime(lastAccessTime.toMillis());
            }
            if (createTime != null) {
                file.setCreationTime(createTime.toMillis());
            }
        }
    }
    
    static class Attributes implements BasicFileAttributes
    {
        private final FileTime lastModifiedTime;
        private final FileTime lastAccessTime;
        private final FileTime creationTime;
        private final boolean regularFile;
        private final boolean directory;
        private final boolean symbolicLink;
        private final long size;
        private final Object fileKey;
        
        protected Attributes(final File file) {
            this.lastModifiedTime = FileTime.fromMillis(file.getLastModifiedTime());
            this.lastAccessTime = FileTime.fromMillis(file.getLastAccessTime());
            this.creationTime = FileTime.fromMillis(file.getCreationTime());
            this.regularFile = file.isRegularFile();
            this.directory = file.isDirectory();
            this.symbolicLink = file.isSymbolicLink();
            this.size = file.size();
            this.fileKey = file.id();
        }
        
        @Override
        public FileTime lastModifiedTime() {
            return this.lastModifiedTime;
        }
        
        @Override
        public FileTime lastAccessTime() {
            return this.lastAccessTime;
        }
        
        @Override
        public FileTime creationTime() {
            return this.creationTime;
        }
        
        @Override
        public boolean isRegularFile() {
            return this.regularFile;
        }
        
        @Override
        public boolean isDirectory() {
            return this.directory;
        }
        
        @Override
        public boolean isSymbolicLink() {
            return this.symbolicLink;
        }
        
        @Override
        public boolean isOther() {
            return false;
        }
        
        @Override
        public long size() {
            return this.size;
        }
        
        @Override
        public Object fileKey() {
            return this.fileKey;
        }
    }
}

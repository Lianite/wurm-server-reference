// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import java.util.List;
import java.nio.file.attribute.FileAttributeView;
import com.google.common.collect.ImmutableMap;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.ByteBuffer;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import com.google.common.collect.ImmutableSet;

final class UserDefinedAttributeProvider extends AttributeProvider
{
    @Override
    public String name() {
        return "user";
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return ImmutableSet.of();
    }
    
    @Override
    public boolean supports(final String attribute) {
        return true;
    }
    
    @Override
    public ImmutableSet<String> attributes(final File file) {
        return userDefinedAttributes(file);
    }
    
    private static ImmutableSet<String> userDefinedAttributes(final File file) {
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (final String attribute : file.getAttributeNames("user")) {
            builder.add(attribute);
        }
        return builder.build();
    }
    
    @Override
    public Object get(final File file, final String attribute) {
        final Object value = file.getAttribute("user", attribute);
        if (value instanceof byte[]) {
            final byte[] bytes = (byte[])value;
            return bytes.clone();
        }
        return null;
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        Preconditions.checkNotNull(value);
        AttributeProvider.checkNotCreate(view, attribute, create);
        byte[] bytes;
        if (value instanceof byte[]) {
            bytes = ((byte[])value).clone();
        }
        else {
            if (!(value instanceof ByteBuffer)) {
                throw AttributeProvider.invalidType(view, attribute, value, byte[].class, ByteBuffer.class);
            }
            final ByteBuffer buffer = (ByteBuffer)value;
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
        }
        file.setAttribute("user", attribute, bytes);
    }
    
    @Override
    public Class<UserDefinedFileAttributeView> viewType() {
        return UserDefinedFileAttributeView.class;
    }
    
    @Override
    public UserDefinedFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup);
    }
    
    private static class View extends AbstractAttributeView implements UserDefinedFileAttributeView
    {
        public View(final FileLookup lookup) {
            super(lookup);
        }
        
        @Override
        public String name() {
            return "user";
        }
        
        @Override
        public List<String> list() throws IOException {
            return (List<String>)userDefinedAttributes(this.lookupFile()).asList();
        }
        
        private byte[] getStoredBytes(final String name) throws IOException {
            final byte[] bytes = (byte[])this.lookupFile().getAttribute(this.name(), name);
            if (bytes == null) {
                throw new IllegalArgumentException("attribute '" + this.name() + ":" + name + "' is not set");
            }
            return bytes;
        }
        
        @Override
        public int size(final String name) throws IOException {
            return this.getStoredBytes(name).length;
        }
        
        @Override
        public int read(final String name, final ByteBuffer dst) throws IOException {
            final byte[] bytes = this.getStoredBytes(name);
            dst.put(bytes);
            return bytes.length;
        }
        
        @Override
        public int write(final String name, final ByteBuffer src) throws IOException {
            final byte[] bytes = new byte[src.remaining()];
            src.get(bytes);
            this.lookupFile().setAttribute(this.name(), name, bytes);
            return bytes.length;
        }
        
        @Override
        public void delete(final String name) throws IOException {
            this.lookupFile().deleteAttribute(this.name(), name);
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Arrays;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.BasicFileAttributes;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.nio.file.attribute.FileAttributeView;
import com.google.common.collect.ImmutableSet;

public abstract class AttributeProvider
{
    public abstract String name();
    
    public ImmutableSet<String> inherits() {
        return ImmutableSet.of();
    }
    
    public abstract Class<? extends FileAttributeView> viewType();
    
    public abstract FileAttributeView view(final FileLookup p0, final ImmutableMap<String, FileAttributeView> p1);
    
    public ImmutableMap<String, ?> defaultValues(final Map<String, ?> userDefaults) {
        return ImmutableMap.of();
    }
    
    public abstract ImmutableSet<String> fixedAttributes();
    
    public boolean supports(final String attribute) {
        return this.fixedAttributes().contains(attribute);
    }
    
    public ImmutableSet<String> attributes(final File file) {
        return this.fixedAttributes();
    }
    
    @Nullable
    public abstract Object get(final File p0, final String p1);
    
    public abstract void set(final File p0, final String p1, final String p2, final Object p3, final boolean p4);
    
    @Nullable
    public Class<? extends BasicFileAttributes> attributesType() {
        return null;
    }
    
    public BasicFileAttributes readAttributes(final File file) {
        throw new UnsupportedOperationException();
    }
    
    protected static IllegalArgumentException unsettable(final String view, final String attribute) {
        throw new IllegalArgumentException("cannot set attribute '" + view + ":" + attribute + "'");
    }
    
    protected static void checkNotCreate(final String view, final String attribute, final boolean create) {
        if (create) {
            throw new UnsupportedOperationException("cannot set attribute '" + view + ":" + attribute + "' during file creation");
        }
    }
    
    protected static <T> T checkType(final String view, final String attribute, final Object value, final Class<T> type) {
        Preconditions.checkNotNull(value);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw invalidType(view, attribute, value, type);
    }
    
    protected static IllegalArgumentException invalidType(final String view, final String attribute, final Object value, final Class<?>... expectedTypes) {
        final Object expected = (expectedTypes.length == 1) ? expectedTypes[0] : ("one of " + Arrays.toString(expectedTypes));
        throw new IllegalArgumentException("invalid type " + value.getClass() + " for attribute '" + view + ":" + attribute + "': expected " + expected);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.FileTime;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import com.google.common.collect.ImmutableSet;

final class DosAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    private static final ImmutableSet<String> INHERITED_VIEWS;
    
    @Override
    public String name() {
        return "dos";
    }
    
    @Override
    public ImmutableSet<String> inherits() {
        return DosAttributeProvider.INHERITED_VIEWS;
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return DosAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public ImmutableMap<String, ?> defaultValues(final Map<String, ?> userProvidedDefaults) {
        return ImmutableMap.of("dos:readonly", getDefaultValue("dos:readonly", userProvidedDefaults), "dos:hidden", getDefaultValue("dos:hidden", userProvidedDefaults), "dos:archive", getDefaultValue("dos:archive", userProvidedDefaults), "dos:system", (Object)getDefaultValue("dos:system", userProvidedDefaults));
    }
    
    private static Boolean getDefaultValue(final String attribute, final Map<String, ?> userProvidedDefaults) {
        final Object userProvidedValue = userProvidedDefaults.get(attribute);
        if (userProvidedValue != null) {
            return AttributeProvider.checkType("dos", attribute, userProvidedValue, Boolean.class);
        }
        return false;
    }
    
    @Nullable
    @Override
    public Object get(final File file, final String attribute) {
        if (DosAttributeProvider.ATTRIBUTES.contains(attribute)) {
            return file.getAttribute("dos", attribute);
        }
        return null;
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        if (this.supports(attribute)) {
            AttributeProvider.checkNotCreate(view, attribute, create);
            file.setAttribute("dos", attribute, AttributeProvider.checkType(view, attribute, value, Boolean.class));
        }
    }
    
    @Override
    public Class<DosFileAttributeView> viewType() {
        return DosFileAttributeView.class;
    }
    
    @Override
    public DosFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup, inheritedViews.get("basic"));
    }
    
    @Override
    public Class<DosFileAttributes> attributesType() {
        return DosFileAttributes.class;
    }
    
    @Override
    public DosFileAttributes readAttributes(final File file) {
        return new Attributes(file);
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("readonly", "hidden", "archive", "system");
        INHERITED_VIEWS = ImmutableSet.of("basic", "owner");
    }
    
    private static final class View extends AbstractAttributeView implements DosFileAttributeView
    {
        private final BasicFileAttributeView basicView;
        
        public View(final FileLookup lookup, final BasicFileAttributeView basicView) {
            super(lookup);
            this.basicView = Preconditions.checkNotNull(basicView);
        }
        
        @Override
        public String name() {
            return "dos";
        }
        
        @Override
        public DosFileAttributes readAttributes() throws IOException {
            return new Attributes(this.lookupFile());
        }
        
        @Override
        public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) throws IOException {
            this.basicView.setTimes(lastModifiedTime, lastAccessTime, createTime);
        }
        
        @Override
        public void setReadOnly(final boolean value) throws IOException {
            this.lookupFile().setAttribute("dos", "readonly", value);
        }
        
        @Override
        public void setHidden(final boolean value) throws IOException {
            this.lookupFile().setAttribute("dos", "hidden", value);
        }
        
        @Override
        public void setSystem(final boolean value) throws IOException {
            this.lookupFile().setAttribute("dos", "system", value);
        }
        
        @Override
        public void setArchive(final boolean value) throws IOException {
            this.lookupFile().setAttribute("dos", "archive", value);
        }
    }
    
    static class Attributes extends BasicAttributeProvider.Attributes implements DosFileAttributes
    {
        private final boolean readOnly;
        private final boolean hidden;
        private final boolean archive;
        private final boolean system;
        
        protected Attributes(final File file) {
            super(file);
            this.readOnly = (boolean)file.getAttribute("dos", "readonly");
            this.hidden = (boolean)file.getAttribute("dos", "hidden");
            this.archive = (boolean)file.getAttribute("dos", "archive");
            this.system = (boolean)file.getAttribute("dos", "system");
        }
        
        @Override
        public boolean isReadOnly() {
            return this.readOnly;
        }
        
        @Override
        public boolean isHidden() {
            return this.hidden;
        }
        
        @Override
        public boolean isArchive() {
            return this.archive;
        }
        
        @Override
        public boolean isSystem() {
            return this.system;
        }
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.FileTime;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;
import com.google.common.collect.Sets;
import java.nio.file.attribute.PosixFilePermissions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.GroupPrincipal;
import com.google.common.collect.ImmutableSet;

final class PosixAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    private static final ImmutableSet<String> INHERITED_VIEWS;
    private static final GroupPrincipal DEFAULT_GROUP;
    private static final ImmutableSet<PosixFilePermission> DEFAULT_PERMISSIONS;
    
    @Override
    public String name() {
        return "posix";
    }
    
    @Override
    public ImmutableSet<String> inherits() {
        return PosixAttributeProvider.INHERITED_VIEWS;
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return PosixAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public ImmutableMap<String, ?> defaultValues(final Map<String, ?> userProvidedDefaults) {
        final Object userProvidedGroup = userProvidedDefaults.get("posix:group");
        UserPrincipal group = PosixAttributeProvider.DEFAULT_GROUP;
        if (userProvidedGroup != null) {
            if (!(userProvidedGroup instanceof String)) {
                throw new IllegalArgumentException("invalid type " + userProvidedGroup.getClass().getName() + " for attribute 'posix:group': should be one of " + String.class + " or " + GroupPrincipal.class);
            }
            group = UserLookupService.createGroupPrincipal((String)userProvidedGroup);
        }
        final Object userProvidedPermissions = userProvidedDefaults.get("posix:permissions");
        Set<PosixFilePermission> permissions = PosixAttributeProvider.DEFAULT_PERMISSIONS;
        if (userProvidedPermissions != null) {
            if (userProvidedPermissions instanceof String) {
                permissions = Sets.immutableEnumSet((Iterable<PosixFilePermission>)PosixFilePermissions.fromString((String)userProvidedPermissions));
            }
            else {
                if (!(userProvidedPermissions instanceof Set)) {
                    throw new IllegalArgumentException("invalid type " + userProvidedPermissions.getClass().getName() + " for attribute 'posix:permissions': should be one of " + String.class + " or " + Set.class);
                }
                permissions = toPermissions((Set<?>)userProvidedPermissions);
            }
        }
        return ImmutableMap.of("posix:group", group, "posix:permissions", permissions);
    }
    
    @Nullable
    @Override
    public Object get(final File file, final String attribute) {
        switch (attribute) {
            case "group": {
                return file.getAttribute("posix", "group");
            }
            case "permissions": {
                return file.getAttribute("posix", "permissions");
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        switch (attribute) {
            case "group": {
                AttributeProvider.checkNotCreate(view, attribute, create);
                GroupPrincipal group = AttributeProvider.checkType(view, attribute, value, GroupPrincipal.class);
                if (!(group instanceof UserLookupService.JimfsGroupPrincipal)) {
                    group = UserLookupService.createGroupPrincipal(group.getName());
                }
                file.setAttribute("posix", "group", group);
                break;
            }
            case "permissions": {
                file.setAttribute("posix", "permissions", toPermissions(AttributeProvider.checkType(view, attribute, value, (Class<Set<?>>)Set.class)));
                break;
            }
        }
    }
    
    private static ImmutableSet<PosixFilePermission> toPermissions(final Set<?> set) {
        final ImmutableSet<?> copy = ImmutableSet.copyOf((Collection<?>)set);
        for (final Object obj : copy) {
            if (!(obj instanceof PosixFilePermission)) {
                throw new IllegalArgumentException("invalid element for attribute 'posix:permissions': should be Set<PosixFilePermission>, found element of type " + obj.getClass());
            }
        }
        return Sets.immutableEnumSet((Iterable<PosixFilePermission>)copy);
    }
    
    @Override
    public Class<PosixFileAttributeView> viewType() {
        return PosixFileAttributeView.class;
    }
    
    @Override
    public PosixFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup, inheritedViews.get("basic"), inheritedViews.get("owner"));
    }
    
    @Override
    public Class<PosixFileAttributes> attributesType() {
        return PosixFileAttributes.class;
    }
    
    @Override
    public PosixFileAttributes readAttributes(final File file) {
        return new Attributes(file);
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("group", "permissions");
        INHERITED_VIEWS = ImmutableSet.of("basic", "owner");
        DEFAULT_GROUP = UserLookupService.createGroupPrincipal("group");
        DEFAULT_PERMISSIONS = Sets.immutableEnumSet((Iterable<PosixFilePermission>)PosixFilePermissions.fromString("rw-r--r--"));
    }
    
    private static class View extends AbstractAttributeView implements PosixFileAttributeView
    {
        private final BasicFileAttributeView basicView;
        private final FileOwnerAttributeView ownerView;
        
        protected View(final FileLookup lookup, final BasicFileAttributeView basicView, final FileOwnerAttributeView ownerView) {
            super(lookup);
            this.basicView = Preconditions.checkNotNull(basicView);
            this.ownerView = Preconditions.checkNotNull(ownerView);
        }
        
        @Override
        public String name() {
            return "posix";
        }
        
        @Override
        public PosixFileAttributes readAttributes() throws IOException {
            return new Attributes(this.lookupFile());
        }
        
        @Override
        public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) throws IOException {
            this.basicView.setTimes(lastModifiedTime, lastAccessTime, createTime);
        }
        
        @Override
        public void setPermissions(final Set<PosixFilePermission> perms) throws IOException {
            this.lookupFile().setAttribute("posix", "permissions", ImmutableSet.copyOf((Collection<?>)perms));
        }
        
        @Override
        public void setGroup(final GroupPrincipal group) throws IOException {
            this.lookupFile().setAttribute("posix", "group", Preconditions.checkNotNull(group));
        }
        
        @Override
        public UserPrincipal getOwner() throws IOException {
            return this.ownerView.getOwner();
        }
        
        @Override
        public void setOwner(final UserPrincipal owner) throws IOException {
            this.ownerView.setOwner(owner);
        }
    }
    
    static class Attributes extends BasicAttributeProvider.Attributes implements PosixFileAttributes
    {
        private final UserPrincipal owner;
        private final GroupPrincipal group;
        private final ImmutableSet<PosixFilePermission> permissions;
        
        protected Attributes(final File file) {
            super(file);
            this.owner = (UserPrincipal)file.getAttribute("owner", "owner");
            this.group = (GroupPrincipal)file.getAttribute("posix", "group");
            this.permissions = (ImmutableSet<PosixFilePermission>)file.getAttribute("posix", "permissions");
        }
        
        @Override
        public UserPrincipal owner() {
            return this.owner;
        }
        
        @Override
        public GroupPrincipal group() {
            return this.group;
        }
        
        @Override
        public ImmutableSet<PosixFilePermission> permissions() {
            return this.permissions;
        }
    }
}

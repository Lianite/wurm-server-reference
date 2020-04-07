// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.FileTime;
import java.util.Set;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.FileAttributeView;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.collect.ImmutableSet;

final class UnixAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    private static final ImmutableSet<String> INHERITED_VIEWS;
    private final AtomicInteger uidGenerator;
    private final ConcurrentMap<Object, Integer> idCache;
    
    UnixAttributeProvider() {
        this.uidGenerator = new AtomicInteger();
        this.idCache = new ConcurrentHashMap<Object, Integer>();
    }
    
    @Override
    public String name() {
        return "unix";
    }
    
    @Override
    public ImmutableSet<String> inherits() {
        return UnixAttributeProvider.INHERITED_VIEWS;
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return UnixAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public Class<UnixFileAttributeView> viewType() {
        return UnixFileAttributeView.class;
    }
    
    @Override
    public UnixFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        throw new UnsupportedOperationException();
    }
    
    private Integer getUniqueId(final Object object) {
        Integer id = this.idCache.get(object);
        if (id == null) {
            id = this.uidGenerator.incrementAndGet();
            final Integer existing = this.idCache.putIfAbsent(object, id);
            if (existing != null) {
                return existing;
            }
        }
        return id;
    }
    
    @Override
    public Object get(final File file, final String attribute) {
        switch (attribute) {
            case "uid": {
                final UserPrincipal user = (UserPrincipal)file.getAttribute("owner", "owner");
                return this.getUniqueId(user);
            }
            case "gid": {
                final GroupPrincipal group = (GroupPrincipal)file.getAttribute("posix", "group");
                return this.getUniqueId(group);
            }
            case "mode": {
                final Set<PosixFilePermission> permissions = (Set<PosixFilePermission>)file.getAttribute("posix", "permissions");
                return toMode(permissions);
            }
            case "ctime": {
                return FileTime.fromMillis(file.getCreationTime());
            }
            case "rdev": {
                return 0L;
            }
            case "dev": {
                return 1L;
            }
            case "ino": {
                return file.id();
            }
            case "nlink": {
                return file.links();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        throw AttributeProvider.unsettable(view, attribute);
    }
    
    private static int toMode(final Set<PosixFilePermission> permissions) {
        int result = 0;
        for (final PosixFilePermission permission : permissions) {
            Preconditions.checkNotNull(permission);
            switch (permission) {
                case OWNER_READ: {
                    result |= 0x100;
                    continue;
                }
                case OWNER_WRITE: {
                    result |= 0x80;
                    continue;
                }
                case OWNER_EXECUTE: {
                    result |= 0x40;
                    continue;
                }
                case GROUP_READ: {
                    result |= 0x20;
                    continue;
                }
                case GROUP_WRITE: {
                    result |= 0x10;
                    continue;
                }
                case GROUP_EXECUTE: {
                    result |= 0x8;
                    continue;
                }
                case OTHERS_READ: {
                    result |= 0x4;
                    continue;
                }
                case OTHERS_WRITE: {
                    result |= 0x2;
                    continue;
                }
                case OTHERS_EXECUTE: {
                    result |= 0x1;
                    continue;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        return result;
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("uid", "ino", "dev", "nlink", "rdev", "ctime", "mode", "gid");
        INHERITED_VIEWS = ImmutableSet.of("basic", "owner", "posix");
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.attribute.UserPrincipal;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.nio.file.attribute.AclEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

final class AclAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    private static final ImmutableSet<String> INHERITED_VIEWS;
    private static final ImmutableList<AclEntry> DEFAULT_ACL;
    
    @Override
    public String name() {
        return "acl";
    }
    
    @Override
    public ImmutableSet<String> inherits() {
        return AclAttributeProvider.INHERITED_VIEWS;
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return AclAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public ImmutableMap<String, ?> defaultValues(final Map<String, ?> userProvidedDefaults) {
        final Object userProvidedAcl = userProvidedDefaults.get("acl:acl");
        ImmutableList<AclEntry> acl = AclAttributeProvider.DEFAULT_ACL;
        if (userProvidedAcl != null) {
            acl = toAcl(AttributeProvider.checkType("acl", "acl", userProvidedAcl, (Class<List<?>>)List.class));
        }
        return ImmutableMap.of("acl:acl", (Object)acl);
    }
    
    @Nullable
    @Override
    public Object get(final File file, final String attribute) {
        if (attribute.equals("acl")) {
            return file.getAttribute("acl", "acl");
        }
        return null;
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        if (attribute.equals("acl")) {
            AttributeProvider.checkNotCreate(view, attribute, create);
            file.setAttribute("acl", "acl", toAcl(AttributeProvider.checkType(view, attribute, value, (Class<List<?>>)List.class)));
        }
    }
    
    private static ImmutableList<AclEntry> toAcl(final List<?> list) {
        final ImmutableList<?> copy = ImmutableList.copyOf((Collection<?>)list);
        for (final Object obj : copy) {
            if (!(obj instanceof AclEntry)) {
                throw new IllegalArgumentException("invalid element for attribute 'acl:acl': should be List<AclEntry>, found element of type " + obj.getClass());
            }
        }
        return (ImmutableList<AclEntry>)copy;
    }
    
    @Override
    public Class<AclFileAttributeView> viewType() {
        return AclFileAttributeView.class;
    }
    
    @Override
    public AclFileAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup, inheritedViews.get("owner"));
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("acl");
        INHERITED_VIEWS = ImmutableSet.of("owner");
        DEFAULT_ACL = ImmutableList.of();
    }
    
    private static final class View extends AbstractAttributeView implements AclFileAttributeView
    {
        private final FileOwnerAttributeView ownerView;
        
        public View(final FileLookup lookup, final FileOwnerAttributeView ownerView) {
            super(lookup);
            this.ownerView = Preconditions.checkNotNull(ownerView);
        }
        
        @Override
        public String name() {
            return "acl";
        }
        
        @Override
        public List<AclEntry> getAcl() throws IOException {
            return (List<AclEntry>)this.lookupFile().getAttribute("acl", "acl");
        }
        
        @Override
        public void setAcl(final List<AclEntry> acl) throws IOException {
            Preconditions.checkNotNull(acl);
            this.lookupFile().setAttribute("acl", "acl", ImmutableList.copyOf((Collection<?>)acl));
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
}

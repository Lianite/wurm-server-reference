// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.nio.file.attribute.UserPrincipal;
import com.google.common.collect.ImmutableSet;

final class OwnerAttributeProvider extends AttributeProvider
{
    private static final ImmutableSet<String> ATTRIBUTES;
    private static final UserPrincipal DEFAULT_OWNER;
    
    @Override
    public String name() {
        return "owner";
    }
    
    @Override
    public ImmutableSet<String> fixedAttributes() {
        return OwnerAttributeProvider.ATTRIBUTES;
    }
    
    @Override
    public ImmutableMap<String, ?> defaultValues(final Map<String, ?> userProvidedDefaults) {
        final Object userProvidedOwner = userProvidedDefaults.get("owner:owner");
        UserPrincipal owner = OwnerAttributeProvider.DEFAULT_OWNER;
        if (userProvidedOwner != null) {
            if (!(userProvidedOwner instanceof String)) {
                throw AttributeProvider.invalidType("owner", "owner", userProvidedOwner, String.class, UserPrincipal.class);
            }
            owner = UserLookupService.createUserPrincipal((String)userProvidedOwner);
        }
        return ImmutableMap.of("owner:owner", (Object)owner);
    }
    
    @Nullable
    @Override
    public Object get(final File file, final String attribute) {
        if (attribute.equals("owner")) {
            return file.getAttribute("owner", "owner");
        }
        return null;
    }
    
    @Override
    public void set(final File file, final String view, final String attribute, final Object value, final boolean create) {
        if (attribute.equals("owner")) {
            UserPrincipal user = AttributeProvider.checkType(view, attribute, value, UserPrincipal.class);
            if (!(user instanceof UserLookupService.JimfsUserPrincipal)) {
                user = UserLookupService.createUserPrincipal(user.getName());
            }
            file.setAttribute("owner", "owner", user);
        }
    }
    
    @Override
    public Class<FileOwnerAttributeView> viewType() {
        return FileOwnerAttributeView.class;
    }
    
    @Override
    public FileOwnerAttributeView view(final FileLookup lookup, final ImmutableMap<String, FileAttributeView> inheritedViews) {
        return new View(lookup);
    }
    
    static {
        ATTRIBUTES = ImmutableSet.of("owner");
        DEFAULT_OWNER = UserLookupService.createUserPrincipal("user");
    }
    
    private static final class View extends AbstractAttributeView implements FileOwnerAttributeView
    {
        public View(final FileLookup lookup) {
            super(lookup);
        }
        
        @Override
        public String name() {
            return "owner";
        }
        
        @Override
        public UserPrincipal getOwner() throws IOException {
            return (UserPrincipal)this.lookupFile().getAttribute("owner", "owner");
        }
        
        @Override
        public void setOwner(final UserPrincipal owner) throws IOException {
            this.lookupFile().setAttribute("owner", "owner", Preconditions.checkNotNull(owner));
        }
    }
}

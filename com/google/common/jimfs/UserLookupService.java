// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

final class UserLookupService extends UserPrincipalLookupService
{
    private final boolean supportsGroups;
    
    public UserLookupService(final boolean supportsGroups) {
        this.supportsGroups = supportsGroups;
    }
    
    @Override
    public UserPrincipal lookupPrincipalByName(final String name) {
        return createUserPrincipal(name);
    }
    
    @Override
    public GroupPrincipal lookupPrincipalByGroupName(final String group) throws IOException {
        if (!this.supportsGroups) {
            throw new UserPrincipalNotFoundException(group);
        }
        return createGroupPrincipal(group);
    }
    
    static UserPrincipal createUserPrincipal(final String name) {
        return new JimfsUserPrincipal(name);
    }
    
    static GroupPrincipal createGroupPrincipal(final String name) {
        return new JimfsGroupPrincipal(name);
    }
    
    private abstract static class NamedPrincipal implements UserPrincipal
    {
        protected final String name;
        
        private NamedPrincipal(final String name) {
            this.name = Preconditions.checkNotNull(name);
        }
        
        @Override
        public final String getName() {
            return this.name;
        }
        
        @Override
        public final int hashCode() {
            return this.name.hashCode();
        }
        
        @Override
        public final String toString() {
            return this.name;
        }
    }
    
    static final class JimfsUserPrincipal extends NamedPrincipal
    {
        private JimfsUserPrincipal(final String name) {
            super(name);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof JimfsUserPrincipal && this.getName().equals(((JimfsUserPrincipal)obj).getName());
        }
    }
    
    static final class JimfsGroupPrincipal extends NamedPrincipal implements GroupPrincipal
    {
        private JimfsGroupPrincipal(final String name) {
            super(name);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof JimfsGroupPrincipal && ((JimfsGroupPrincipal)obj).name.equals(this.name);
        }
    }
}

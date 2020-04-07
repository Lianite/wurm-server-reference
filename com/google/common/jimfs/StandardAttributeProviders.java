// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;

final class StandardAttributeProviders
{
    private static final ImmutableMap<String, AttributeProvider> PROVIDERS;
    
    @Nullable
    public static AttributeProvider get(final String view) {
        final AttributeProvider provider = StandardAttributeProviders.PROVIDERS.get(view);
        if (provider == null && view.equals("unix")) {
            return new UnixAttributeProvider();
        }
        return provider;
    }
    
    static {
        PROVIDERS = new ImmutableMap.Builder<String, BasicAttributeProvider>().put("basic", new BasicAttributeProvider()).put("owner", (BasicAttributeProvider)new OwnerAttributeProvider()).put("posix", (BasicAttributeProvider)new PosixAttributeProvider()).put("dos", (BasicAttributeProvider)new DosAttributeProvider()).put("acl", (BasicAttributeProvider)new AclAttributeProvider()).put("user", (BasicAttributeProvider)new UserDefinedAttributeProvider()).build();
    }
}

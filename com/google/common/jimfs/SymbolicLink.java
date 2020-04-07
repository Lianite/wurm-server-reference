// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Preconditions;

final class SymbolicLink extends File
{
    private final JimfsPath target;
    
    public static SymbolicLink create(final int id, final JimfsPath target) {
        return new SymbolicLink(id, target);
    }
    
    private SymbolicLink(final int id, final JimfsPath target) {
        super(id);
        this.target = Preconditions.checkNotNull(target);
    }
    
    JimfsPath target() {
        return this.target;
    }
    
    @Override
    File copyWithoutContent(final int id) {
        return create(id, this.target);
    }
}

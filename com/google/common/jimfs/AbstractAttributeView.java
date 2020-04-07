// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.file.attribute.FileAttributeView;

abstract class AbstractAttributeView implements FileAttributeView
{
    private final FileLookup lookup;
    
    protected AbstractAttributeView(final FileLookup lookup) {
        this.lookup = Preconditions.checkNotNull(lookup);
    }
    
    protected final File lookupFile() throws IOException {
        return this.lookup.lookup();
    }
}

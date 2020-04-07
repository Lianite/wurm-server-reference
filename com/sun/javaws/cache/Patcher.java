// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.io.IOException;
import java.io.OutputStream;

public interface Patcher
{
    void applyPatch(final PatchDelegate p0, final String p1, final String p2, final OutputStream p3) throws IOException;
    
    public interface PatchDelegate
    {
        void patching(final int p0);
    }
}

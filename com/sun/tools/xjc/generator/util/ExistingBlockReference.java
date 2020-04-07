// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.util;

import com.sun.codemodel.JBlock;

public class ExistingBlockReference implements BlockReference
{
    private final JBlock block;
    
    public ExistingBlockReference(final JBlock _block) {
        this.block = _block;
    }
    
    public JBlock get(final boolean create) {
        return this.block;
    }
}

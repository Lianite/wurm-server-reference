// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.util;

import com.sun.codemodel.JBlock;

public abstract class LazyBlockReference implements BlockReference
{
    private JBlock block;
    
    public LazyBlockReference() {
        this.block = null;
    }
    
    protected abstract JBlock create();
    
    public JBlock get(final boolean create) {
        if (!create) {
            return this.block;
        }
        if (this.block == null) {
            this.block = this.create();
        }
        return this.block;
    }
}

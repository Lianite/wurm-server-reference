// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.runtime.JAXBContextImpl;

public final class UnmarshallerChain
{
    private int offset;
    public final JAXBContextImpl context;
    
    public UnmarshallerChain(final JAXBContextImpl context) {
        this.offset = 0;
        this.context = context;
    }
    
    public int allocateOffset() {
        return this.offset++;
    }
    
    public int getScopeSize() {
        return this.offset;
    }
}

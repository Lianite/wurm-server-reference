// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack;

import java.util.Collection;
import java.util.ArrayList;

public final class FinalArrayList<T> extends ArrayList<T>
{
    public FinalArrayList(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public FinalArrayList() {
    }
    
    public FinalArrayList(final Collection<? extends T> ts) {
        super(ts);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api;

public abstract class RawAccessor<B, V>
{
    public abstract V get(final B p0) throws AccessorException;
    
    public abstract void set(final B p0, final V p1) throws AccessorException;
}

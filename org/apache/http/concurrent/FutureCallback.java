// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.concurrent;

public interface FutureCallback<T>
{
    void completed(final T p0);
    
    void failed(final Exception p0);
    
    void cancelled();
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface Pool<T>
{
    @NotNull
    T take();
    
    void recycle(@NotNull final T p0);
    
    public abstract static class Impl<T> extends ConcurrentLinkedQueue<T> implements Pool<T>
    {
        @NotNull
        public final T take() {
            final T t = super.poll();
            if (t == null) {
                return this.create();
            }
            return t;
        }
        
        public final void recycle(final T t) {
            super.offer(t);
        }
        
        @NotNull
        protected abstract T create();
    }
}

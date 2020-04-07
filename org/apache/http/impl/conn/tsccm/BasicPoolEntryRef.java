// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import org.apache.http.conn.routing.HttpRoute;
import java.lang.ref.WeakReference;

@Deprecated
public class BasicPoolEntryRef extends WeakReference<BasicPoolEntry>
{
    private final HttpRoute route;
    
    public BasicPoolEntryRef(final BasicPoolEntry entry, final ReferenceQueue<Object> queue) {
        super(entry, queue);
        if (entry == null) {
            throw new IllegalArgumentException("Pool entry must not be null.");
        }
        this.route = entry.getPlannedRoute();
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.AbstractPoolEntry;
import org.apache.http.impl.conn.AbstractPooledConnAdapter;

@Deprecated
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter
{
    protected BasicPooledConnAdapter(final ThreadSafeClientConnManager tsccm, final AbstractPoolEntry entry) {
        super(tsccm, entry);
        this.markReusable();
    }
    
    protected ClientConnectionManager getManager() {
        return super.getManager();
    }
    
    protected AbstractPoolEntry getPoolEntry() {
        return super.getPoolEntry();
    }
    
    protected void detach() {
        super.detach();
    }
}

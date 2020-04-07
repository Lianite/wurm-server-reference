// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.pool;

import org.apache.http.pool.PoolEntry;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.ConnFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.pool.AbstractConnPool;

@ThreadSafe
public class BasicConnPool extends AbstractConnPool<HttpHost, HttpClientConnection, BasicPoolEntry>
{
    private static AtomicLong COUNTER;
    
    public BasicConnPool(final ConnFactory<HttpHost, HttpClientConnection> connFactory) {
        super(connFactory, 2, 20);
    }
    
    public BasicConnPool(final HttpParams params) {
        super(new BasicConnFactory(params), 2, 20);
    }
    
    protected BasicPoolEntry createEntry(final HttpHost host, final HttpClientConnection conn) {
        return new BasicPoolEntry(Long.toString(BasicConnPool.COUNTER.getAndIncrement()), host, conn);
    }
    
    static {
        BasicConnPool.COUNTER = new AtomicLong();
    }
}

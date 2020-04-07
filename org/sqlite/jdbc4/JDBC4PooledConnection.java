// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc4;

import javax.sql.StatementEventListener;
import javax.sql.PooledConnection;

public abstract class JDBC4PooledConnection implements PooledConnection
{
    @Override
    public void addStatementEventListener(final StatementEventListener listener) {
    }
    
    @Override
    public void removeStatementEventListener(final StatementEventListener listener) {
    }
}

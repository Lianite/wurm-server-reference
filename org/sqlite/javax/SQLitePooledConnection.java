// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.javax;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import javax.sql.ConnectionEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.ConnectionEventListener;
import java.util.List;
import java.sql.Connection;
import javax.sql.PooledConnection;
import org.sqlite.jdbc4.JDBC4PooledConnection;

public class SQLitePooledConnection extends JDBC4PooledConnection implements PooledConnection
{
    protected Connection physicalConn;
    protected volatile Connection handleConn;
    protected List<ConnectionEventListener> listeners;
    
    protected SQLitePooledConnection(final Connection physicalConn) {
        this.listeners = new ArrayList<ConnectionEventListener>();
        this.physicalConn = physicalConn;
    }
    
    @Override
    public void close() throws SQLException {
        if (this.handleConn != null) {
            this.listeners.clear();
            this.handleConn.close();
        }
        if (this.physicalConn != null) {
            try {
                this.physicalConn.close();
            }
            finally {
                this.physicalConn = null;
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (this.handleConn != null) {
            this.handleConn.close();
        }
        return this.handleConn = (Connection)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { Connection.class }, new InvocationHandler() {
            boolean isClosed;
            
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    final String name = method.getName();
                    if ("close".equals(name)) {
                        final ConnectionEvent event = new ConnectionEvent(SQLitePooledConnection.this);
                        for (int i = SQLitePooledConnection.this.listeners.size() - 1; i >= 0; --i) {
                            SQLitePooledConnection.this.listeners.get(i).connectionClosed(event);
                        }
                        if (!SQLitePooledConnection.this.physicalConn.getAutoCommit()) {
                            SQLitePooledConnection.this.physicalConn.rollback();
                        }
                        SQLitePooledConnection.this.physicalConn.setAutoCommit(true);
                        this.isClosed = true;
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        if (!this.isClosed) {
                            this.isClosed = (boolean)method.invoke(SQLitePooledConnection.this.physicalConn, args);
                        }
                        return this.isClosed;
                    }
                    if (this.isClosed) {
                        throw new SQLException("Connection is closed");
                    }
                    return method.invoke(SQLitePooledConnection.this.physicalConn, args);
                }
                catch (SQLException e) {
                    if ("database connection closed".equals(e.getMessage())) {
                        final ConnectionEvent event = new ConnectionEvent(SQLitePooledConnection.this, e);
                        for (int i = SQLitePooledConnection.this.listeners.size() - 1; i >= 0; --i) {
                            SQLitePooledConnection.this.listeners.get(i).connectionErrorOccurred(event);
                        }
                    }
                    throw e;
                }
                catch (InvocationTargetException ex) {
                    throw ex.getCause();
                }
            }
        });
    }
    
    @Override
    public void addConnectionEventListener(final ConnectionEventListener listener) {
        this.listeners.add(listener);
    }
    
    @Override
    public void removeConnectionEventListener(final ConnectionEventListener listener) {
        this.listeners.remove(listener);
    }
}

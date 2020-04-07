// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc4;

import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import java.sql.Statement;
import org.sqlite.jdbc3.JDBC3Statement;

public class JDBC4Statement extends JDBC3Statement implements Statement
{
    public JDBC4Statement(final SQLiteConnection conn) {
        super(conn);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
}

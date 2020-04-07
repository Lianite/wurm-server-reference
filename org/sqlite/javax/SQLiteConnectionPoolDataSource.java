// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.javax;

import java.sql.SQLException;
import javax.sql.PooledConnection;
import org.sqlite.SQLiteConfig;
import javax.sql.ConnectionPoolDataSource;
import org.sqlite.SQLiteDataSource;

public class SQLiteConnectionPoolDataSource extends SQLiteDataSource implements ConnectionPoolDataSource
{
    public SQLiteConnectionPoolDataSource() {
    }
    
    public SQLiteConnectionPoolDataSource(final SQLiteConfig config) {
        super(config);
    }
    
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection(null, null);
    }
    
    @Override
    public PooledConnection getPooledConnection(final String user, final String password) throws SQLException {
        return new SQLitePooledConnection(this.getConnection(user, password));
    }
}

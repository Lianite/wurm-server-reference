// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite;

import java.util.concurrent.Executor;
import java.sql.SQLException;
import java.util.Properties;
import org.sqlite.jdbc4.JDBC4Connection;

public class SQLiteConnection extends JDBC4Connection
{
    public SQLiteConnection(final String url, final String fileName) throws SQLException {
        this(url, fileName, new Properties());
    }
    
    public SQLiteConnection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
    }
    
    @Override
    public String getSchema() throws SQLException {
        return null;
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }
}

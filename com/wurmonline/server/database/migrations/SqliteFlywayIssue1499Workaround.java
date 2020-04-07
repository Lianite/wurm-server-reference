// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Connection;
import org.sqlite.SQLiteDataSource;
import javax.sql.DataSource;

public class SqliteFlywayIssue1499Workaround implements DataSource
{
    private final SQLiteDataSource dataSource;
    private Connection connection;
    
    public SqliteFlywayIssue1499Workaround(final SQLiteDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return this.getConnection();
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.dataSource.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return SQLiteDataSource.class.equals(iface);
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSource.getLogWriter();
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.dataSource.getParentLogger();
    }
}

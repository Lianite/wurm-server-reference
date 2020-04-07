// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc4;

import java.sql.Array;
import java.sql.SQLClientInfoException;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.sqlite.SQLiteConnection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;
import org.sqlite.jdbc3.JDBC3Connection;

public abstract class JDBC4Connection extends JDBC3Connection implements Connection
{
    public JDBC4Connection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        if (this.meta == null) {
            this.meta = new JDBC4DatabaseMetaData((SQLiteConnection)this);
        }
        return (DatabaseMetaData)this.meta;
    }
    
    @Override
    public Statement createStatement(final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4Statement((SQLiteConnection)this);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int rst, final int rsc, final int rsh) throws SQLException {
        this.checkOpen();
        this.checkCursor(rst, rsc, rsh);
        return new JDBC4PreparedStatement((SQLiteConnection)this, sql);
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.db == null;
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
    public Clob createClob() throws SQLException {
        return null;
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        if (this.db == null) {
            return false;
        }
        final Statement statement = this.createStatement();
        try {
            return statement.execute("select 1");
        }
        finally {
            statement.close();
        }
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return null;
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return null;
    }
}

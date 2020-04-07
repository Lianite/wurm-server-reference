// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc4;

import java.sql.SQLXML;
import java.io.InputStream;
import java.sql.NClob;
import java.io.Reader;
import java.sql.RowId;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import org.sqlite.jdbc3.JDBC3PreparedStatement;

public class JDBC4PreparedStatement extends JDBC3PreparedStatement implements PreparedStatement, ParameterMetaData
{
    public JDBC4PreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn, sql);
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
}

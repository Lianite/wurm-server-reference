// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc4;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.net.URL;
import java.sql.Ref;
import java.util.Map;
import java.sql.Clob;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.Array;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import org.sqlite.core.CoreStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import org.sqlite.jdbc3.JDBC3ResultSet;

public class JDBC4ResultSet extends JDBC3ResultSet implements ResultSet, ResultSetMetaData
{
    public JDBC4ResultSet(final CoreStatement stmt) {
        super(stmt);
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
    public RowId getRowId(final int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        return null;
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return !this.isOpen();
    }
    
    @Override
    public void updateNString(final int columnIndex, final String nString) throws SQLException {
    }
    
    @Override
    public void updateNString(final String columnLabel, final String nString) throws SQLException {
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        return null;
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return null;
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        return null;
    }
    
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return null;
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return null;
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return null;
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        return null;
    }
    
    protected SQLException unused() {
        return new SQLException("not implemented by SQLite JDBC driver");
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Array getArray(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getAsciiStream(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getAsciiStream(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public BigDecimal getBigDecimal(final int col, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public BigDecimal getBigDecimal(final String col, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Blob getBlob(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Blob getBlob(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Clob getClob(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Clob getClob(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Object getObject(final int col, final Map map) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Object getObject(final String col, final Map map) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public Ref getRef(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getUnicodeStream(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public InputStream getUnicodeStream(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public URL getURL(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public URL getURL(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean last() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean previous() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void afterLast() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public boolean first() throws SQLException {
        throw new SQLException("ResultSet is TYPE_FORWARD_ONLY");
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateArray(final int col, final Array x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateArray(final String col, final Array x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateAsciiStream(final int col, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateAsciiStream(final String col, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBigDecimal(final int col, final BigDecimal x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBigDecimal(final String col, final BigDecimal x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBinaryStream(final int c, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBinaryStream(final String c, final InputStream x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBlob(final int col, final Blob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBlob(final String col, final Blob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBoolean(final int col, final boolean x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBoolean(final String col, final boolean x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateByte(final int col, final byte x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateByte(final String col, final byte x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBytes(final int col, final byte[] x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateBytes(final String col, final byte[] x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateCharacterStream(final int c, final Reader x, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateCharacterStream(final String c, final Reader r, final int l) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateClob(final int col, final Clob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateClob(final String col, final Clob x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDate(final int col, final Date x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDate(final String col, final Date x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDouble(final int col, final double x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateDouble(final String col, final double x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateFloat(final int col, final float x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateFloat(final String col, final float x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateInt(final int col, final int x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateInt(final String col, final int x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateLong(final int col, final long x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateLong(final String col, final long x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateNull(final int col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateNull(final String col) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final int c, final Object x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final int c, final Object x, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final String col, final Object x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateObject(final String c, final Object x, final int s) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRef(final int col, final Ref x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRef(final String c, final Ref x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateRow() throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateShort(final int c, final short x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateShort(final String c, final short x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateString(final int c, final String x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateString(final String c, final String x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTime(final int c, final Time x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTime(final String c, final Time x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTimestamp(final int c, final Timestamp x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void updateTimestamp(final String c, final Timestamp x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        throw this.unused();
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc3;

import java.net.URL;
import java.sql.Ref;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.io.Reader;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Statement;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import org.sqlite.core.CoreStatement;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import org.sqlite.core.CorePreparedStatement;

public abstract class JDBC3PreparedStatement extends CorePreparedStatement
{
    protected JDBC3PreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn, sql);
    }
    
    public void clearParameters() throws SQLException {
        this.checkOpen();
        this.db.clear_bindings(this.pointer);
        this.batch = null;
    }
    
    public boolean execute() throws SQLException {
        this.checkOpen();
        this.rs.close();
        this.db.reset(this.pointer);
        this.checkParameters();
        this.resultsWaiting = this.db.execute(this, this.batch);
        return this.columnCount != 0;
    }
    
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        if (this.columnCount == 0) {
            throw new SQLException("Query does not return results");
        }
        this.rs.close();
        this.db.reset(this.pointer);
        this.checkParameters();
        this.resultsWaiting = this.db.execute(this, this.batch);
        return this.getResultSet();
    }
    
    public int executeUpdate() throws SQLException {
        this.checkOpen();
        if (this.columnCount != 0) {
            throw new SQLException("Query returns results");
        }
        this.rs.close();
        this.db.reset(this.pointer);
        this.checkParameters();
        return this.db.executeUpdate(this, this.batch);
    }
    
    public void addBatch() throws SQLException {
        this.checkOpen();
        this.batchPos += this.paramCount;
        if (this.batchPos + this.paramCount > this.batch.length) {
            final Object[] nb = new Object[this.batch.length * 2];
            System.arraycopy(this.batch, 0, nb, 0, this.batch.length);
            this.batch = nb;
        }
        System.arraycopy(this.batch, this.batchPos - this.paramCount, this.batch, this.batchPos, this.paramCount);
    }
    
    public ParameterMetaData getParameterMetaData() {
        return (ParameterMetaData)this;
    }
    
    public int getParameterCount() throws SQLException {
        this.checkOpen();
        return this.paramCount;
    }
    
    public String getParameterClassName(final int param) throws SQLException {
        this.checkOpen();
        return "java.lang.String";
    }
    
    public String getParameterTypeName(final int pos) {
        return "VARCHAR";
    }
    
    public int getParameterType(final int pos) {
        return 12;
    }
    
    public int getParameterMode(final int pos) {
        return 1;
    }
    
    public int getPrecision(final int pos) {
        return 0;
    }
    
    public int getScale(final int pos) {
        return 0;
    }
    
    public int isNullable(final int pos) {
        return 1;
    }
    
    public boolean isSigned(final int pos) {
        return true;
    }
    
    public Statement getStatement() {
        return this;
    }
    
    public void setBigDecimal(final int pos, final BigDecimal value) throws SQLException {
        this.batch(pos, (value == null) ? null : value.toString());
    }
    
    private byte[] readBytes(final InputStream istream, final int length) throws SQLException {
        if (length < 0) {
            final SQLException exception = new SQLException("Error reading stream. Length should be non-negative");
            throw exception;
        }
        final byte[] bytes = new byte[length];
        try {
            istream.read(bytes);
            return bytes;
        }
        catch (IOException cause) {
            final SQLException exception2 = new SQLException("Error reading stream");
            exception2.initCause(cause);
            throw exception2;
        }
    }
    
    public void setBinaryStream(final int pos, final InputStream istream, final int length) throws SQLException {
        if (istream == null && length == 0) {
            this.setBytes(pos, null);
        }
        this.setBytes(pos, this.readBytes(istream, length));
    }
    
    public void setAsciiStream(final int pos, final InputStream istream, final int length) throws SQLException {
        this.setUnicodeStream(pos, istream, length);
    }
    
    public void setUnicodeStream(final int pos, final InputStream istream, final int length) throws SQLException {
        if (istream == null && length == 0) {
            this.setString(pos, null);
        }
        this.setString(pos, new String(this.readBytes(istream, length)));
    }
    
    public void setBoolean(final int pos, final boolean value) throws SQLException {
        this.setInt(pos, value ? 1 : 0);
    }
    
    public void setByte(final int pos, final byte value) throws SQLException {
        this.setInt(pos, value);
    }
    
    public void setBytes(final int pos, final byte[] value) throws SQLException {
        this.batch(pos, value);
    }
    
    public void setDouble(final int pos, final double value) throws SQLException {
        this.batch(pos, new Double(value));
    }
    
    public void setFloat(final int pos, final float value) throws SQLException {
        this.batch(pos, new Float(value));
    }
    
    public void setInt(final int pos, final int value) throws SQLException {
        this.batch(pos, new Integer(value));
    }
    
    public void setLong(final int pos, final long value) throws SQLException {
        this.batch(pos, new Long(value));
    }
    
    public void setNull(final int pos, final int u1) throws SQLException {
        this.setNull(pos, u1, null);
    }
    
    public void setNull(final int pos, final int u1, final String u2) throws SQLException {
        this.batch(pos, null);
    }
    
    public void setObject(final int pos, final Object value) throws SQLException {
        if (value == null) {
            this.batch(pos, null);
        }
        else if (value instanceof Date) {
            this.setDateByMilliseconds(pos, ((Date)value).getTime());
        }
        else if (value instanceof java.sql.Date) {
            this.setDateByMilliseconds(pos, new Long(((java.sql.Date)value).getTime()));
        }
        else if (value instanceof Time) {
            this.setDateByMilliseconds(pos, new Long(((Time)value).getTime()));
        }
        else if (value instanceof Timestamp) {
            this.setDateByMilliseconds(pos, new Long(((Timestamp)value).getTime()));
        }
        else if (value instanceof Long) {
            this.batch(pos, value);
        }
        else if (value instanceof Integer) {
            this.batch(pos, value);
        }
        else if (value instanceof Short) {
            this.batch(pos, new Integer((int)value));
        }
        else if (value instanceof Float) {
            this.batch(pos, value);
        }
        else if (value instanceof Double) {
            this.batch(pos, value);
        }
        else if (value instanceof Boolean) {
            this.setBoolean(pos, (boolean)value);
        }
        else if (value instanceof byte[]) {
            this.batch(pos, value);
        }
        else if (value instanceof BigDecimal) {
            this.setBigDecimal(pos, (BigDecimal)value);
        }
        else {
            this.batch(pos, value.toString());
        }
    }
    
    public void setObject(final int p, final Object v, final int t) throws SQLException {
        this.setObject(p, v);
    }
    
    public void setObject(final int p, final Object v, final int t, final int s) throws SQLException {
        this.setObject(p, v);
    }
    
    public void setShort(final int pos, final short value) throws SQLException {
        this.setInt(pos, value);
    }
    
    public void setString(final int pos, final String value) throws SQLException {
        this.batch(pos, value);
    }
    
    public void setCharacterStream(final int pos, final Reader reader, final int length) throws SQLException {
        try {
            final StringBuffer sb = new StringBuffer();
            final char[] cbuf = new char[8192];
            int cnt;
            while ((cnt = reader.read(cbuf)) > 0) {
                sb.append(cbuf, 0, cnt);
            }
            this.setString(pos, sb.toString());
        }
        catch (IOException e) {
            throw new SQLException("Cannot read from character stream, exception message: " + e.getMessage());
        }
    }
    
    public void setDate(final int pos, final java.sql.Date x) throws SQLException {
        this.setObject(pos, x);
    }
    
    public void setDate(final int pos, final java.sql.Date x, final Calendar cal) throws SQLException {
        this.setObject(pos, x);
    }
    
    public void setTime(final int pos, final Time x) throws SQLException {
        this.setObject(pos, x);
    }
    
    public void setTime(final int pos, final Time x, final Calendar cal) throws SQLException {
        this.setObject(pos, x);
    }
    
    public void setTimestamp(final int pos, final Timestamp x) throws SQLException {
        this.setObject(pos, x);
    }
    
    public void setTimestamp(final int pos, final Timestamp x, final Calendar cal) throws SQLException {
        this.setObject(pos, x);
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        return (ResultSetMetaData)this.rs;
    }
    
    @Override
    protected SQLException unused() {
        return new SQLException("not implemented by SQLite JDBC driver");
    }
    
    public void setArray(final int i, final Array x) throws SQLException {
        throw this.unused();
    }
    
    public void setBlob(final int i, final Blob x) throws SQLException {
        throw this.unused();
    }
    
    public void setClob(final int i, final Clob x) throws SQLException {
        throw this.unused();
    }
    
    public void setRef(final int i, final Ref x) throws SQLException {
        throw this.unused();
    }
    
    public void setURL(final int pos, final URL x) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        throw this.unused();
    }
    
    @Override
    public void addBatch(final String sql) throws SQLException {
        throw this.unused();
    }
}

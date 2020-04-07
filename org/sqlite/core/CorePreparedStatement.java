// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import org.sqlite.SQLiteConfig;
import java.sql.Date;
import java.sql.SQLException;
import org.sqlite.SQLiteConnection;
import org.sqlite.jdbc4.JDBC4Statement;

public abstract class CorePreparedStatement extends JDBC4Statement
{
    protected int columnCount;
    protected int paramCount;
    
    protected CorePreparedStatement(final SQLiteConnection conn, final String sql) throws SQLException {
        super(conn);
        this.sql = sql;
        this.db.prepare(this);
        this.rs.colsMeta = this.db.column_names(this.pointer);
        this.columnCount = this.db.column_count(this.pointer);
        this.paramCount = this.db.bind_parameter_count(this.pointer);
        this.batch = null;
        this.batchPos = 0;
    }
    
    @Override
    protected void finalize() throws SQLException {
        this.close();
    }
    
    protected void checkParameters() throws SQLException {
        if (this.batch == null && this.paramCount > 0) {
            throw new SQLException("Values not bound to statement");
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        if (this.batchPos == 0) {
            return new int[0];
        }
        this.checkParameters();
        try {
            return this.db.executeBatch(this.pointer, this.batchPos / this.paramCount, this.batch);
        }
        finally {
            this.clearBatch();
        }
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        if (this.pointer == 0L || this.resultsWaiting || this.rs.isOpen()) {
            return -1;
        }
        return this.db.changes();
    }
    
    protected void batch(final int pos, final Object value) throws SQLException {
        this.checkOpen();
        if (this.batch == null) {
            this.batch = new Object[this.paramCount];
        }
        this.batch[this.batchPos + pos - 1] = value;
    }
    
    protected void setDateByMilliseconds(final int pos, final Long value) throws SQLException {
        switch (this.conn.dateClass) {
            case TEXT: {
                this.batch(pos, this.conn.dateFormat.format(new Date(value)));
                break;
            }
            case REAL: {
                this.batch(pos, new Double(value / 8.64E7 + 2440587.5));
                break;
            }
            default: {
                this.batch(pos, new Long(value / this.conn.dateMultiplier));
                break;
            }
        }
    }
}

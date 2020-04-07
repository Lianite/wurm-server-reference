// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.sqlite.jdbc4.JDBC4ResultSet;
import org.sqlite.SQLiteConnection;

public abstract class CoreStatement implements Codes
{
    public final SQLiteConnection conn;
    protected final DB db;
    protected final CoreResultSet rs;
    protected CoreDatabaseMetaData metadata;
    public long pointer;
    protected String sql;
    protected int batchPos;
    protected Object[] batch;
    protected boolean resultsWaiting;
    
    protected CoreStatement(final SQLiteConnection c) {
        this.sql = null;
        this.batch = null;
        this.resultsWaiting = false;
        this.conn = c;
        this.db = this.conn.db();
        this.rs = new JDBC4ResultSet(this);
    }
    
    protected final void checkOpen() throws SQLException {
        if (this.pointer == 0L) {
            throw new SQLException("statement is not executing");
        }
    }
    
    boolean isOpen() throws SQLException {
        return this.pointer != 0L;
    }
    
    protected boolean exec() throws SQLException {
        if (this.sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        boolean rc = false;
        try {
            rc = this.db.execute(this, null);
        }
        finally {
            this.resultsWaiting = rc;
        }
        return this.db.column_count(this.pointer) != 0;
    }
    
    protected boolean exec(final String sql) throws SQLException {
        if (sql == null) {
            throw new SQLException("SQLiteJDBC internal error: sql==null");
        }
        if (this.rs.isOpen()) {
            throw new SQLException("SQLite JDBC internal error: rs.isOpen() on exec.");
        }
        boolean rc = false;
        try {
            rc = this.db.execute(sql);
        }
        finally {
            this.resultsWaiting = rc;
        }
        return this.db.column_count(this.pointer) != 0;
    }
    
    protected void internalClose() throws SQLException {
        if (this.db.conn.isClosed()) {
            throw DB.newSQLException(1, "Connection is closed");
        }
        if (this.pointer == 0L) {
            return;
        }
        this.rs.close();
        this.batch = null;
        this.batchPos = 0;
        final int resp = this.db.finalize(this);
        if (resp != 0 && resp != 21) {
            this.db.throwex();
        }
    }
    
    public abstract ResultSet executeQuery(final String p0, final boolean p1) throws SQLException;
}

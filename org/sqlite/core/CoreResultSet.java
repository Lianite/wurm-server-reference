// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import java.util.HashMap;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Map;

public abstract class CoreResultSet implements Codes
{
    protected final CoreStatement stmt;
    protected final DB db;
    public boolean open;
    public int maxRows;
    public String[] cols;
    public String[] colsMeta;
    protected boolean[][] meta;
    protected int limitRows;
    protected int row;
    protected int lastCol;
    public boolean closeStmt;
    protected Map<String, Integer> columnNameToIndex;
    
    protected CoreResultSet(final CoreStatement stmt) {
        this.open = false;
        this.cols = null;
        this.colsMeta = null;
        this.meta = null;
        this.row = 0;
        this.columnNameToIndex = null;
        this.stmt = stmt;
        this.db = stmt.db;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    protected void checkOpen() throws SQLException {
        if (!this.open) {
            throw new SQLException("ResultSet closed");
        }
    }
    
    public int checkCol(int col) throws SQLException {
        if (this.colsMeta == null) {
            throw new IllegalStateException("SQLite JDBC: inconsistent internal state");
        }
        if (col < 1 || col > this.colsMeta.length) {
            throw new SQLException("column " + col + " out of bounds [1," + this.colsMeta.length + "]");
        }
        return --col;
    }
    
    protected int markCol(int col) throws SQLException {
        this.checkOpen();
        this.checkCol(col);
        this.lastCol = col;
        return --col;
    }
    
    public void checkMeta() throws SQLException {
        this.checkCol(1);
        if (this.meta == null) {
            this.meta = this.db.column_metadata(this.stmt.pointer);
        }
    }
    
    public void close() throws SQLException {
        this.cols = null;
        this.colsMeta = null;
        this.meta = null;
        this.open = false;
        this.limitRows = 0;
        this.row = 0;
        this.lastCol = -1;
        this.columnNameToIndex = null;
        if (this.stmt == null) {
            return;
        }
        if (this.stmt != null && this.stmt.pointer != 0L) {
            this.db.reset(this.stmt.pointer);
            if (this.closeStmt) {
                this.closeStmt = false;
                ((Statement)this.stmt).close();
            }
        }
    }
    
    protected Integer findColumnIndexInCache(final String col) {
        if (this.columnNameToIndex == null) {
            return null;
        }
        return this.columnNameToIndex.get(col);
    }
    
    protected int addColumnIndexInCache(final String col, final int index) {
        if (this.columnNameToIndex == null) {
            this.columnNameToIndex = new HashMap<String, Integer>(this.cols.length);
        }
        this.columnNameToIndex.put(col, index);
        return index;
    }
}

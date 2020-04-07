// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

public class SqlStatement
{
    private int lineNumber;
    private String sql;
    private boolean pgCopy;
    
    public SqlStatement(final int lineNumber, final String sql, final boolean pgCopy) {
        this.lineNumber = lineNumber;
        this.sql = sql;
        this.pgCopy = pgCopy;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public boolean isPgCopy() {
        return this.pgCopy;
    }
}

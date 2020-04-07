// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.jdbc3;

import java.sql.Struct;
import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import org.sqlite.SQLiteOpenMode;
import java.util.Map;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.sqlite.core.CoreConnection;

public abstract class JDBC3Connection extends CoreConnection
{
    private final AtomicInteger savePoint;
    
    protected JDBC3Connection(final String url, final String fileName, final Properties prop) throws SQLException {
        super(url, fileName, prop);
        this.savePoint = new AtomicInteger(0);
    }
    
    public String getCatalog() throws SQLException {
        this.checkOpen();
        return null;
    }
    
    public void setCatalog(final String catalog) throws SQLException {
        this.checkOpen();
    }
    
    public int getHoldability() throws SQLException {
        this.checkOpen();
        return 2;
    }
    
    public void setHoldability(final int h) throws SQLException {
        this.checkOpen();
        if (h != 2) {
            throw new SQLException("SQLite only supports CLOSE_CURSORS_AT_COMMIT");
        }
    }
    
    public int getTransactionIsolation() {
        return this.transactionIsolation;
    }
    
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkOpen();
        switch (level) {
            case 8: {
                this.db.exec("PRAGMA read_uncommitted = false;");
                break;
            }
            case 1: {
                this.db.exec("PRAGMA read_uncommitted = true;");
                break;
            }
            default: {
                throw new SQLException("SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED.");
            }
        }
        this.transactionIsolation = level;
    }
    
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLException("not yet implemented");
    }
    
    public void setTypeMap(final Map map) throws SQLException {
        throw new SQLException("not yet implemented");
    }
    
    public boolean isReadOnly() throws SQLException {
        return (this.openModeFlags & SQLiteOpenMode.READONLY.flag) != 0x0;
    }
    
    public void setReadOnly(final boolean ro) throws SQLException {
        if (ro != this.isReadOnly()) {
            throw new SQLException("Cannot change read-only flag after establishing a connection. Use SQLiteConfig#setReadOnly and SQLiteConfig.createConnection().");
        }
    }
    
    public abstract DatabaseMetaData getMetaData() throws SQLException;
    
    public String nativeSQL(final String sql) {
        return sql;
    }
    
    public void clearWarnings() throws SQLException {
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        return this.autoCommit;
    }
    
    public void setAutoCommit(final boolean ac) throws SQLException {
        this.checkOpen();
        if (this.autoCommit == ac) {
            return;
        }
        this.autoCommit = ac;
        this.db.exec(this.autoCommit ? "commit;" : JDBC3Connection.beginCommandMap.get(this.transactionMode));
    }
    
    public void commit() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("commit;");
        this.db.exec(JDBC3Connection.beginCommandMap.get(this.transactionMode));
    }
    
    public void rollback() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec("rollback;");
        this.db.exec(JDBC3Connection.beginCommandMap.get(this.transactionMode));
    }
    
    public Statement createStatement() throws SQLException {
        return this.createStatement(1003, 1007, 2);
    }
    
    public Statement createStatement(final int rsType, final int rsConcurr) throws SQLException {
        return this.createStatement(rsType, rsConcurr, 2);
    }
    
    public abstract Statement createStatement(final int p0, final int p1, final int p2) throws SQLException;
    
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return this.prepareCall(sql, 1003, 1007, 2);
    }
    
    public CallableStatement prepareCall(final String sql, final int rst, final int rsc) throws SQLException {
        return this.prepareCall(sql, rst, rsc, 2);
    }
    
    public CallableStatement prepareCall(final String sql, final int rst, final int rsc, final int rsh) throws SQLException {
        throw new SQLException("SQLite does not support Stored Procedures");
    }
    
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return this.prepareStatement(sql, 1003, 1007);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int autoC) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int[] colInds) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    public PreparedStatement prepareStatement(final String sql, final String[] colNames) throws SQLException {
        return this.prepareStatement(sql);
    }
    
    public PreparedStatement prepareStatement(final String sql, final int rst, final int rsc) throws SQLException {
        return this.prepareStatement(sql, rst, rsc, 2);
    }
    
    public abstract PreparedStatement prepareStatement(final String p0, final int p1, final int p2, final int p3) throws SQLException;
    
    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            this.autoCommit = false;
        }
        final Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet());
        this.db.exec(String.format("SAVEPOINT %s", sp.getSavepointName()));
        return sp;
    }
    
    public Savepoint setSavepoint(final String name) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            this.autoCommit = false;
        }
        final Savepoint sp = new JDBC3Savepoint(this.savePoint.incrementAndGet(), name);
        this.db.exec(String.format("SAVEPOINT %s", sp.getSavepointName()));
        return sp;
    }
    
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec(String.format("RELEASE SAVEPOINT %s", savepoint.getSavepointName()));
    }
    
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        if (this.autoCommit) {
            throw new SQLException("database in auto-commit mode");
        }
        this.db.exec(String.format("ROLLBACK TO SAVEPOINT %s", savepoint.getSavepointName()));
    }
    
    public Struct createStruct(final String t, final Object[] attr) throws SQLException {
        throw new SQLException("unsupported by SQLite");
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import org.sqlite.SQLiteErrorCode;
import java.sql.BatchUpdateException;
import org.sqlite.Function;
import java.util.Iterator;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.sqlite.SQLiteConnection;

public abstract class DB implements Codes
{
    SQLiteConnection conn;
    long begin;
    long commit;
    private final Map<Long, CoreStatement> stmts;
    
    public DB() {
        this.conn = null;
        this.begin = 0L;
        this.commit = 0L;
        this.stmts = new HashMap<Long, CoreStatement>();
    }
    
    public abstract void interrupt() throws SQLException;
    
    public abstract void busy_timeout(final int p0) throws SQLException;
    
    abstract String errmsg() throws SQLException;
    
    public abstract String libversion() throws SQLException;
    
    public abstract int changes() throws SQLException;
    
    public abstract int total_changes() throws SQLException;
    
    public abstract int shared_cache(final boolean p0) throws SQLException;
    
    public abstract int enable_load_extension(final boolean p0) throws SQLException;
    
    public final synchronized void exec(final String sql) throws SQLException {
        long pointer = 0L;
        try {
            pointer = this.prepare(sql);
            switch (this.step(pointer)) {
                case 101: {
                    this.ensureAutoCommit();
                }
                case 100: {}
                default: {
                    this.throwex();
                    break;
                }
            }
        }
        finally {
            this.finalize(pointer);
        }
    }
    
    public final synchronized void open(final SQLiteConnection conn, final String file, final int openFlags) throws SQLException {
        this.conn = conn;
        this._open(file, openFlags);
    }
    
    public final synchronized void close() throws SQLException {
        synchronized (this.stmts) {
            final Iterator<Map.Entry<Long, CoreStatement>> i = this.stmts.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<Long, CoreStatement> entry = i.next();
                final CoreStatement stmt = entry.getValue();
                this.finalize(entry.getKey());
                if (stmt != null) {
                    stmt.pointer = 0L;
                }
                i.remove();
            }
        }
        this.free_functions();
        if (this.begin != 0L) {
            this.finalize(this.begin);
            this.begin = 0L;
        }
        if (this.commit != 0L) {
            this.finalize(this.commit);
            this.commit = 0L;
        }
        this._close();
    }
    
    public final synchronized void prepare(final CoreStatement stmt) throws SQLException {
        if (stmt.pointer != 0L) {
            this.finalize(stmt);
        }
        stmt.pointer = this.prepare(stmt.sql);
        this.stmts.put(new Long(stmt.pointer), stmt);
    }
    
    public final synchronized int finalize(final CoreStatement stmt) throws SQLException {
        if (stmt.pointer == 0L) {
            return 0;
        }
        int rc = 1;
        try {
            rc = this.finalize(stmt.pointer);
        }
        finally {
            this.stmts.remove(new Long(stmt.pointer));
            stmt.pointer = 0L;
        }
        return rc;
    }
    
    protected abstract void _open(final String p0, final int p1) throws SQLException;
    
    protected abstract void _close() throws SQLException;
    
    public abstract int _exec(final String p0) throws SQLException;
    
    protected abstract long prepare(final String p0) throws SQLException;
    
    protected abstract int finalize(final long p0) throws SQLException;
    
    public abstract int step(final long p0) throws SQLException;
    
    public abstract int reset(final long p0) throws SQLException;
    
    public abstract int clear_bindings(final long p0) throws SQLException;
    
    abstract int bind_parameter_count(final long p0) throws SQLException;
    
    public abstract int column_count(final long p0) throws SQLException;
    
    public abstract int column_type(final long p0, final int p1) throws SQLException;
    
    public abstract String column_decltype(final long p0, final int p1) throws SQLException;
    
    public abstract String column_table_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_name(final long p0, final int p1) throws SQLException;
    
    public abstract String column_text(final long p0, final int p1) throws SQLException;
    
    public abstract byte[] column_blob(final long p0, final int p1) throws SQLException;
    
    public abstract double column_double(final long p0, final int p1) throws SQLException;
    
    public abstract long column_long(final long p0, final int p1) throws SQLException;
    
    public abstract int column_int(final long p0, final int p1) throws SQLException;
    
    abstract int bind_null(final long p0, final int p1) throws SQLException;
    
    abstract int bind_int(final long p0, final int p1, final int p2) throws SQLException;
    
    abstract int bind_long(final long p0, final int p1, final long p2) throws SQLException;
    
    abstract int bind_double(final long p0, final int p1, final double p2) throws SQLException;
    
    abstract int bind_text(final long p0, final int p1, final String p2) throws SQLException;
    
    abstract int bind_blob(final long p0, final int p1, final byte[] p2) throws SQLException;
    
    public abstract void result_null(final long p0) throws SQLException;
    
    public abstract void result_text(final long p0, final String p1) throws SQLException;
    
    public abstract void result_blob(final long p0, final byte[] p1) throws SQLException;
    
    public abstract void result_double(final long p0, final double p1) throws SQLException;
    
    public abstract void result_long(final long p0, final long p1) throws SQLException;
    
    public abstract void result_int(final long p0, final int p1) throws SQLException;
    
    public abstract void result_error(final long p0, final String p1) throws SQLException;
    
    public abstract int value_bytes(final Function p0, final int p1) throws SQLException;
    
    public abstract String value_text(final Function p0, final int p1) throws SQLException;
    
    public abstract byte[] value_blob(final Function p0, final int p1) throws SQLException;
    
    public abstract double value_double(final Function p0, final int p1) throws SQLException;
    
    public abstract long value_long(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_int(final Function p0, final int p1) throws SQLException;
    
    public abstract int value_type(final Function p0, final int p1) throws SQLException;
    
    public abstract int create_function(final String p0, final Function p1) throws SQLException;
    
    public abstract int destroy_function(final String p0) throws SQLException;
    
    abstract void free_functions() throws SQLException;
    
    public abstract int backup(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    public abstract int restore(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    abstract boolean[][] column_metadata(final long p0) throws SQLException;
    
    public final synchronized String[] column_names(final long stmt) throws SQLException {
        final String[] names = new String[this.column_count(stmt)];
        for (int i = 0; i < names.length; ++i) {
            names[i] = this.column_name(stmt, i);
        }
        return names;
    }
    
    final synchronized int sqlbind(final long stmt, int pos, final Object v) throws SQLException {
        ++pos;
        if (v == null) {
            return this.bind_null(stmt, pos);
        }
        if (v instanceof Integer) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Short) {
            return this.bind_int(stmt, pos, (int)v);
        }
        if (v instanceof Long) {
            return this.bind_long(stmt, pos, (long)v);
        }
        if (v instanceof Float) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof Double) {
            return this.bind_double(stmt, pos, (double)v);
        }
        if (v instanceof String) {
            return this.bind_text(stmt, pos, (String)v);
        }
        if (v instanceof byte[]) {
            return this.bind_blob(stmt, pos, (byte[])v);
        }
        throw new SQLException("unexpected param type: " + v.getClass());
    }
    
    final synchronized int[] executeBatch(final long stmt, final int count, final Object[] vals) throws SQLException {
        if (count < 1) {
            throw new SQLException("count (" + count + ") < 1");
        }
        final int params = this.bind_parameter_count(stmt);
        final int[] changes = new int[count];
        try {
            for (int i = 0; i < count; ++i) {
                this.reset(stmt);
                for (int j = 0; j < params; ++j) {
                    if (this.sqlbind(stmt, j, vals[i * params + j]) != 0) {
                        this.throwex();
                    }
                }
                final int rc = this.step(stmt);
                if (rc != 101) {
                    this.reset(stmt);
                    if (rc == 100) {
                        throw new BatchUpdateException("batch entry " + i + ": query returns results", changes);
                    }
                    this.throwex();
                }
                changes[i] = this.changes();
            }
        }
        finally {
            this.ensureAutoCommit();
        }
        this.reset(stmt);
        return changes;
    }
    
    public final synchronized boolean execute(final CoreStatement stmt, final Object[] vals) throws SQLException {
        if (vals != null) {
            final int params = this.bind_parameter_count(stmt.pointer);
            if (params != vals.length) {
                throw new SQLException("assertion failure: param count (" + params + ") != value count (" + vals.length + ")");
            }
            for (int i = 0; i < params; ++i) {
                if (this.sqlbind(stmt.pointer, i, vals[i]) != 0) {
                    this.throwex();
                }
            }
        }
        final int statusCode = this.step(stmt.pointer);
        switch (statusCode) {
            case 101: {
                this.reset(stmt.pointer);
                this.ensureAutoCommit();
                return false;
            }
            case 100: {
                return true;
            }
            case 5:
            case 6:
            case 21: {
                throw this.newSQLException(statusCode);
            }
            default: {
                this.finalize(stmt);
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    final synchronized boolean execute(final String sql) throws SQLException {
        final int statusCode = this._exec(sql);
        switch (statusCode) {
            case 0: {
                return false;
            }
            case 101: {
                this.ensureAutoCommit();
                return false;
            }
            case 100: {
                return true;
            }
            default: {
                throw this.newSQLException(statusCode);
            }
        }
    }
    
    public final synchronized int executeUpdate(final CoreStatement stmt, final Object[] vals) throws SQLException {
        if (this.execute(stmt, vals)) {
            throw new SQLException("query returns results");
        }
        this.reset(stmt.pointer);
        return this.changes();
    }
    
    final void throwex() throws SQLException {
        throw new SQLException(this.errmsg());
    }
    
    public final void throwex(final int errorCode) throws SQLException {
        throw this.newSQLException(errorCode);
    }
    
    final void throwex(final int errorCode, final String errorMessage) throws SQLException {
        throw newSQLException(errorCode, errorMessage);
    }
    
    public static SQLException newSQLException(final int errorCode, final String errorMessage) throws SQLException {
        final SQLiteErrorCode code = SQLiteErrorCode.getErrorCode(errorCode);
        final SQLException e = new SQLException(String.format("%s (%s)", code, errorMessage), null, code.code);
        return e;
    }
    
    private SQLException newSQLException(final int errorCode) throws SQLException {
        return newSQLException(errorCode, this.errmsg());
    }
    
    final void ensureAutoCommit() throws SQLException {
        if (!this.conn.getAutoCommit()) {
            return;
        }
        if (this.begin == 0L) {
            this.begin = this.prepare("begin;");
        }
        if (this.commit == 0L) {
            this.commit = this.prepare("commit;");
        }
        try {
            if (this.step(this.begin) != 101) {
                return;
            }
            if (this.step(this.commit) != 101) {
                this.reset(this.commit);
                this.throwex();
            }
        }
        finally {
            this.reset(this.begin);
            this.reset(this.commit);
        }
    }
    
    public interface ProgressObserver
    {
        void progress(final int p0, final int p1);
    }
}

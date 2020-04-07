// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import org.sqlite.Function;
import java.sql.SQLException;
import org.sqlite.SQLiteJDBCLoader;

public final class NativeDB extends DB
{
    long pointer;
    private static boolean isLoaded;
    private static boolean loadSucceeded;
    private final long udfdatalist = 0L;
    
    public NativeDB() {
        this.pointer = 0L;
    }
    
    public static boolean load() throws Exception {
        if (NativeDB.isLoaded) {
            return NativeDB.loadSucceeded;
        }
        NativeDB.loadSucceeded = SQLiteJDBCLoader.initialize();
        NativeDB.isLoaded = true;
        return NativeDB.loadSucceeded;
    }
    
    @Override
    protected synchronized native void _open(final String p0, final int p1) throws SQLException;
    
    @Override
    protected synchronized native void _close() throws SQLException;
    
    @Override
    public synchronized native int _exec(final String p0) throws SQLException;
    
    @Override
    public synchronized native int shared_cache(final boolean p0);
    
    @Override
    public synchronized native int enable_load_extension(final boolean p0);
    
    @Override
    public native void interrupt();
    
    @Override
    public synchronized native void busy_timeout(final int p0);
    
    @Override
    protected synchronized native long prepare(final String p0) throws SQLException;
    
    @Override
    synchronized native String errmsg();
    
    @Override
    public synchronized native String libversion();
    
    @Override
    public synchronized native int changes();
    
    @Override
    public synchronized native int total_changes();
    
    @Override
    protected synchronized native int finalize(final long p0);
    
    @Override
    public synchronized native int step(final long p0);
    
    @Override
    public synchronized native int reset(final long p0);
    
    @Override
    public synchronized native int clear_bindings(final long p0);
    
    @Override
    synchronized native int bind_parameter_count(final long p0);
    
    @Override
    public synchronized native int column_count(final long p0);
    
    @Override
    public synchronized native int column_type(final long p0, final int p1);
    
    @Override
    public synchronized native String column_decltype(final long p0, final int p1);
    
    @Override
    public synchronized native String column_table_name(final long p0, final int p1);
    
    @Override
    public synchronized native String column_name(final long p0, final int p1);
    
    @Override
    public synchronized native String column_text(final long p0, final int p1);
    
    @Override
    public synchronized native byte[] column_blob(final long p0, final int p1);
    
    @Override
    public synchronized native double column_double(final long p0, final int p1);
    
    @Override
    public synchronized native long column_long(final long p0, final int p1);
    
    @Override
    public synchronized native int column_int(final long p0, final int p1);
    
    @Override
    synchronized native int bind_null(final long p0, final int p1);
    
    @Override
    synchronized native int bind_int(final long p0, final int p1, final int p2);
    
    @Override
    synchronized native int bind_long(final long p0, final int p1, final long p2);
    
    @Override
    synchronized native int bind_double(final long p0, final int p1, final double p2);
    
    @Override
    synchronized native int bind_text(final long p0, final int p1, final String p2);
    
    @Override
    synchronized native int bind_blob(final long p0, final int p1, final byte[] p2);
    
    @Override
    public synchronized native void result_null(final long p0);
    
    @Override
    public synchronized native void result_text(final long p0, final String p1);
    
    @Override
    public synchronized native void result_blob(final long p0, final byte[] p1);
    
    @Override
    public synchronized native void result_double(final long p0, final double p1);
    
    @Override
    public synchronized native void result_long(final long p0, final long p1);
    
    @Override
    public synchronized native void result_int(final long p0, final int p1);
    
    @Override
    public synchronized native void result_error(final long p0, final String p1);
    
    @Override
    public synchronized native int value_bytes(final Function p0, final int p1);
    
    @Override
    public synchronized native String value_text(final Function p0, final int p1);
    
    @Override
    public synchronized native byte[] value_blob(final Function p0, final int p1);
    
    @Override
    public synchronized native double value_double(final Function p0, final int p1);
    
    @Override
    public synchronized native long value_long(final Function p0, final int p1);
    
    @Override
    public synchronized native int value_int(final Function p0, final int p1);
    
    @Override
    public synchronized native int value_type(final Function p0, final int p1);
    
    @Override
    public synchronized native int create_function(final String p0, final Function p1);
    
    @Override
    public synchronized native int destroy_function(final String p0);
    
    @Override
    synchronized native void free_functions();
    
    @Override
    public synchronized native int backup(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    @Override
    public synchronized native int restore(final String p0, final String p1, final ProgressObserver p2) throws SQLException;
    
    @Override
    synchronized native boolean[][] column_metadata(final long p0);
    
    static void throwex(final String msg) throws SQLException {
        throw new SQLException(msg);
    }
    
    static {
        if ("The Android Project".equals(System.getProperty("java.vm.vendor"))) {
            System.loadLibrary("sqlitejdbc");
            NativeDB.isLoaded = true;
            NativeDB.loadSucceeded = true;
        }
        else {
            NativeDB.isLoaded = false;
            NativeDB.loadSucceeded = false;
        }
    }
}

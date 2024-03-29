// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite;

public enum SQLiteErrorCode
{
    UNKNOWN_ERROR(-1, "unknown error"), 
    SQLITE_OK(0, "Successful result"), 
    SQLITE_ERROR(1, "SQL error or missing database"), 
    SQLITE_INTERNAL(2, "Internal logic error in SQLite"), 
    SQLITE_PERM(3, " Access permission denied"), 
    SQLITE_ABORT(4, " Callback routine requested an abort"), 
    SQLITE_BUSY(5, " The database file is locked"), 
    SQLITE_LOCKED(6, " A table in the database is locked"), 
    SQLITE_NOMEM(7, " A malloc() failed"), 
    SQLITE_READONLY(8, " Attempt to write a readonly database"), 
    SQLITE_INTERRUPT(9, " Operation terminated by sqlite3_interrupt()"), 
    SQLITE_IOERR(10, " Some kind of disk I/O error occurred"), 
    SQLITE_CORRUPT(11, " The database disk image is malformed"), 
    SQLITE_NOTFOUND(12, " NOT USED. Table or record not found"), 
    SQLITE_FULL(13, " Insertion failed because database is full"), 
    SQLITE_CANTOPEN(14, " Unable to open the database file"), 
    SQLITE_PROTOCOL(15, " NOT USED. Database lock protocol error"), 
    SQLITE_EMPTY(16, " Database is empty"), 
    SQLITE_SCHEMA(17, " The database schema changed"), 
    SQLITE_TOOBIG(18, " String or BLOB exceeds size limit"), 
    SQLITE_CONSTRAINT(19, " Abort due to constraint violation"), 
    SQLITE_MISMATCH(20, " Data type mismatch"), 
    SQLITE_MISUSE(21, " Library used incorrectly"), 
    SQLITE_NOLFS(22, " Uses OS features not supported on host"), 
    SQLITE_AUTH(23, " Authorization denied"), 
    SQLITE_FORMAT(24, " Auxiliary database format error"), 
    SQLITE_RANGE(25, " 2nd parameter to sqlite3_bind out of range"), 
    SQLITE_NOTADB(26, " File opened that is not a database file"), 
    SQLITE_ROW(100, " sqlite3_step() has another row ready"), 
    SQLITE_DONE(101, " sqlite3_step() has finished executing");
    
    public final int code;
    public final String message;
    
    private SQLiteErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
    
    public static SQLiteErrorCode getErrorCode(final int errorCode) {
        for (final SQLiteErrorCode each : values()) {
            if (errorCode == each.code) {
                return each;
            }
        }
        return SQLiteErrorCode.UNKNOWN_ERROR;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", this.name(), this.message);
    }
}

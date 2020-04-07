// 
// Decompiled by Procyon v0.5.30
// 

package com.mysql.jdbc.exceptions.jdbc4;

import com.mysql.jdbc.exceptions.DeadlockTimeoutRollbackMarker;
import java.sql.SQLTransactionRollbackException;

public class MySQLTransactionRollbackException extends SQLTransactionRollbackException implements DeadlockTimeoutRollbackMarker
{
    public MySQLTransactionRollbackException(final String reason, final String SQLState, final int vendorCode) {
        super(reason, SQLState, vendorCode);
    }
    
    public MySQLTransactionRollbackException(final String reason, final String SQLState) {
        super(reason, SQLState);
    }
    
    public MySQLTransactionRollbackException(final String reason) {
        super(reason);
    }
    
    public MySQLTransactionRollbackException() {
    }
}

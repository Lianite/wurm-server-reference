// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.jdbc;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;

public class TransactionTemplate
{
    private static final Log LOG;
    private final Connection connection;
    private final boolean rollbackOnException;
    
    public TransactionTemplate(final Connection connection) {
        this(connection, true);
    }
    
    public TransactionTemplate(final Connection connection, final boolean rollbackOnException) {
        this.connection = connection;
        this.rollbackOnException = rollbackOnException;
    }
    
    public <T> T execute(final TransactionCallback<T> transactionCallback) {
        boolean oldAutocommit = true;
        try {
            oldAutocommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
            final T result = transactionCallback.doInTransaction();
            this.connection.commit();
            return result;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to commit transaction", e);
        }
        catch (RuntimeException e2) {
            if (this.rollbackOnException) {
                try {
                    TransactionTemplate.LOG.debug("Rolling back transaction...");
                    this.connection.rollback();
                    TransactionTemplate.LOG.debug("Transaction rolled back");
                }
                catch (SQLException se) {
                    TransactionTemplate.LOG.error("Unable to rollback transaction", se);
                }
            }
            else {
                try {
                    this.connection.commit();
                }
                catch (SQLException se) {
                    TransactionTemplate.LOG.error("Unable to commit transaction", se);
                }
            }
            throw e2;
        }
        finally {
            try {
                this.connection.setAutoCommit(oldAutocommit);
            }
            catch (SQLException e3) {
                TransactionTemplate.LOG.error("Unable to restore autocommit to original value for connection", e3);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(TransactionTemplate.class);
    }
}

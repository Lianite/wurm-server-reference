// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.hsql;

import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class HsqlSqlStatementBuilder extends SqlStatementBuilder
{
    private boolean insideAtomicBlock;
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (line.contains("BEGIN ATOMIC")) {
            this.insideAtomicBlock = true;
        }
        if (line.endsWith("END;")) {
            this.insideAtomicBlock = false;
        }
        if (this.insideAtomicBlock) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.resolver;

import java.sql.SQLException;
import java.sql.Connection;

public interface MigrationExecutor
{
    void execute(final Connection p0) throws SQLException;
    
    boolean executeInTransaction();
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.jdbc;

import java.sql.SQLException;

public interface TransactionCallback<T>
{
    T doInTransaction() throws SQLException;
}

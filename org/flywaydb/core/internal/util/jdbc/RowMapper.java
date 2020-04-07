// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.jdbc;

import java.sql.SQLException;
import java.sql.ResultSet;

public interface RowMapper<T>
{
    T mapRow(final ResultSet p0) throws SQLException;
}

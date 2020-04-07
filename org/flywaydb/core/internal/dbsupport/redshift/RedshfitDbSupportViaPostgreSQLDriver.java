// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.redshift;

import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;

public class RedshfitDbSupportViaPostgreSQLDriver extends RedshiftDbSupport
{
    public RedshfitDbSupportViaPostgreSQLDriver(final Connection connection) {
        super(new JdbcTemplate(connection, 0));
    }
}

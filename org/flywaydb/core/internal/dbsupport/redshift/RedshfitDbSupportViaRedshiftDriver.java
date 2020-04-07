// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.redshift;

import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.sql.Connection;

public class RedshfitDbSupportViaRedshiftDriver extends RedshiftDbSupport
{
    public RedshfitDbSupportViaRedshiftDriver(final Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2;

import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Type;

public class DB2Type extends Type
{
    public DB2Type(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TYPE " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.h2;

import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Table;

public class H2Table extends Table
{
    public H2Table(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.exists(null, this.schema, this.name, new String[0]);
    }
    
    @Override
    protected void doLock() throws SQLException {
        this.jdbcTemplate.execute("select * from " + this + " for update", new Object[0]);
    }
}

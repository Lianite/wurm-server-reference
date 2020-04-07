// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.oracle;

import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Table;

public class OracleTable extends Table
{
    public OracleTable(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name) + " CASCADE CONSTRAINTS PURGE", new Object[0]);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.exists(null, this.schema, this.name, new String[0]);
    }
    
    @Override
    protected void doLock() throws SQLException {
        this.jdbcTemplate.execute("LOCK TABLE " + this + " IN EXCLUSIVE MODE", new Object[0]);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.phoenix;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Table;

public class PhoenixTable extends Table
{
    private static final Log LOG;
    
    public PhoenixTable(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        final ResultSet rs = this.jdbcTemplate.getMetaData().getTables(null, this.schema.getName(), this.name, new String[] { "TABLE" });
        if (rs.next()) {
            final String tableName = rs.getString("TABLE_NAME");
            if (tableName != null) {
                return tableName.equals(this.name);
            }
        }
        return false;
    }
    
    @Override
    protected void doLock() throws SQLException {
        PhoenixTable.LOG.debug("Unable to lock " + this + " as Phoenix does not support locking. No concurrent migration supported.");
    }
    
    static {
        LOG = LogFactory.getLog(PhoenixTable.class);
    }
}

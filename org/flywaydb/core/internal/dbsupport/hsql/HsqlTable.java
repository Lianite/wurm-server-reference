// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.hsql;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Table;

public class HsqlTable extends Table
{
    private static final Log LOG;
    private boolean version18;
    
    public HsqlTable(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
        try {
            final int majorVersion = jdbcTemplate.getMetaData().getDatabaseMajorVersion();
            this.version18 = (majorVersion < 2);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to determine the Hsql version", e);
        }
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
        if (this.version18) {
            HsqlTable.LOG.debug("Unable to lock " + this + " as Hsql 1.8 does not support locking. No concurrent migration supported.");
        }
        else {
            this.jdbcTemplate.execute("LOCK TABLE " + this + " WRITE", new Object[0]);
        }
    }
    
    static {
        LOG = LogFactory.getLog(HsqlDbSupport.class);
    }
}

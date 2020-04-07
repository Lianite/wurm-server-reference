// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlite;

import java.util.Collections;
import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.util.Collection;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Table;

public class SQLiteTable extends Table
{
    private static final Log LOG;
    private static final Collection<String> SYSTEM_TABLES;
    private final boolean undroppable;
    
    public SQLiteTable(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name) {
        super(jdbcTemplate, dbSupport, schema, name);
        this.undroppable = SQLiteTable.SYSTEM_TABLES.contains(name);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        if (this.undroppable) {
            SQLiteTable.LOG.debug("SQLite system table " + this + " cannot be dropped. Ignoring.");
        }
        else {
            this.jdbcTemplate.execute("DROP TABLE " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
        }
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT count(tbl_name) FROM " + this.dbSupport.quote(this.schema.getName()) + ".sqlite_master WHERE type='table' AND tbl_name='" + this.name + "'", new String[0]) > 0;
    }
    
    @Override
    protected void doLock() throws SQLException {
        SQLiteTable.LOG.debug("Unable to lock " + this + " as SQLite does not support locking. No concurrent migration supported.");
    }
    
    static {
        LOG = LogFactory.getLog(SQLiteTable.class);
        SYSTEM_TABLES = Collections.singleton("sqlite_sequence");
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2zos;

import java.sql.SQLException;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Function;

public class DB2zosFunction extends Function
{
    public DB2zosFunction(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name, final String... args) {
        super(jdbcTemplate, dbSupport, schema, name, args);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP FUNCTION " + this.dbSupport.quote(this.schema.getName(), this.name) + "(" + StringUtils.arrayToCommaDelimitedString(this.args) + ")", new Object[0]);
    }
    
    @Override
    public String toString() {
        return super.toString() + "(" + StringUtils.arrayToCommaDelimitedString(this.args) + ")";
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import java.util.Collection;
import org.flywaydb.core.internal.dbsupport.Function;

public class DB2Function extends Function
{
    private static final Collection<String> typesWithLength;
    
    public DB2Function(final JdbcTemplate jdbcTemplate, final DbSupport dbSupport, final Schema schema, final String name, final String... args) {
        super(jdbcTemplate, dbSupport, schema, name, args);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        try {
            this.jdbcTemplate.execute("DROP FUNCTION " + this.dbSupport.quote(this.schema.getName(), this.name) + "(" + this.argsToCommaSeparatedString(this.args) + ")", new Object[0]);
        }
        catch (SQLException e) {
            this.jdbcTemplate.execute("DROP FUNCTION " + this.dbSupport.quote(this.schema.getName(), this.name), new Object[0]);
        }
    }
    
    private String argsToCommaSeparatedString(final String[] args) {
        final StringBuilder buf = new StringBuilder();
        for (final String arg : args) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append(arg);
            if (DB2Function.typesWithLength.contains(arg.toLowerCase())) {
                buf.append("()");
            }
        }
        return buf.toString();
    }
    
    @Override
    public String toString() {
        return super.toString() + "(" + StringUtils.arrayToCommaDelimitedString(this.args) + ")";
    }
    
    static {
        typesWithLength = Arrays.asList("character", "char", "varchar", "graphic", "vargraphic", "decimal", "float", "varbinary");
    }
}

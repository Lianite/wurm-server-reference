// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sybase.ase;

import java.util.Iterator;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.Table;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;

public class SybaseASESchema extends Schema<SybaseASEDbSupport>
{
    public SybaseASESchema(final JdbcTemplate jdbcTemplate, final SybaseASEDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return true;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.jdbcTemplate.queryForInt("select count(*) from sysobjects ob where (ob.type='U' or ob.type = 'V' or ob.type = 'P' or ob.type = 'TR') and ob.name != 'sysquerymetrics'", new String[0]) == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
    }
    
    @Override
    protected void doDrop() throws SQLException {
    }
    
    @Override
    protected void doClean() throws SQLException {
        this.dropObjects("U");
        this.dropObjects("V");
        this.dropObjects("P");
        this.dropObjects("TR");
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.retrieveAllTableNames();
        final Table[] result = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            final String tableName = tableNames.get(i);
            result[i] = new SybaseASETable(this.jdbcTemplate, this.dbSupport, this, tableName);
        }
        return result;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new SybaseASETable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    private List<String> retrieveAllTableNames() throws SQLException {
        final List<String> objNames = this.jdbcTemplate.queryForStringList("select ob.name from sysobjects ob where ob.type=? order by ob.name", "U");
        return objNames;
    }
    
    private void dropObjects(final String sybaseObjType) throws SQLException {
        final List<String> objNames = this.jdbcTemplate.queryForStringList("select ob.name from sysobjects ob where ob.type=? order by ob.name", sybaseObjType);
        for (final String name : objNames) {
            String sql = "";
            if ("U".equals(sybaseObjType)) {
                sql = "drop table ";
            }
            else if ("V".equals(sybaseObjType)) {
                sql = "drop view ";
            }
            else if ("P".equals(sybaseObjType)) {
                sql = "drop procedure ";
            }
            else {
                if (!"TR".equals(sybaseObjType)) {
                    throw new IllegalArgumentException("Unknown database object type " + sybaseObjType);
                }
                sql = "drop trigger ";
            }
            this.jdbcTemplate.execute(sql + name, new Object[0]);
        }
    }
}

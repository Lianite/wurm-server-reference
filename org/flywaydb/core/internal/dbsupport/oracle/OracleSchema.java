// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.oracle;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.internal.dbsupport.Table;
import java.util.Iterator;
import org.flywaydb.core.api.FlywayException;
import java.sql.SQLException;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.dbsupport.Schema;

public class OracleSchema extends Schema<OracleDbSupport>
{
    private static final Log LOG;
    
    public OracleSchema(final JdbcTemplate jdbcTemplate, final OracleDbSupport dbSupport, final String name) {
        super(jdbcTemplate, dbSupport, name);
    }
    
    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_users WHERE username=?", this.name) > 0;
    }
    
    @Override
    protected boolean doEmpty() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT count(*) FROM all_objects WHERE owner = ?", this.name) == 0;
    }
    
    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE USER " + ((OracleDbSupport)this.dbSupport).quote(this.name) + " IDENTIFIED BY flyway", new Object[0]);
        this.jdbcTemplate.execute("GRANT RESOURCE TO " + ((OracleDbSupport)this.dbSupport).quote(this.name), new Object[0]);
        this.jdbcTemplate.execute("GRANT UNLIMITED TABLESPACE TO " + ((OracleDbSupport)this.dbSupport).quote(this.name), new Object[0]);
    }
    
    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP USER " + ((OracleDbSupport)this.dbSupport).quote(this.name) + " CASCADE", new Object[0]);
    }
    
    @Override
    protected void doClean() throws SQLException {
        if ("SYSTEM".equals(this.name.toUpperCase())) {
            throw new FlywayException("Clean not supported on Oracle for user 'SYSTEM'! You should NEVER add your own objects to the SYSTEM schema!");
        }
        final String user = ((OracleDbSupport)this.dbSupport).doGetCurrentSchemaName();
        final boolean defaultSchemaForUser = user.equalsIgnoreCase(this.name);
        if (!defaultSchemaForUser) {
            OracleSchema.LOG.warn("Cleaning schema " + this.name + " by a different user (" + user + "): " + "spatial extensions, queue tables, flashback tables and scheduled jobs will not be cleaned due to Oracle limitations");
        }
        for (final String statement : this.generateDropStatementsForSpatialExtensions(defaultSchemaForUser)) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        if (defaultSchemaForUser) {
            for (final String statement : this.generateDropStatementsForQueueTables()) {
                try {
                    this.jdbcTemplate.execute(statement, new Object[0]);
                }
                catch (SQLException e) {
                    if (e.getErrorCode() == 65040) {
                        OracleSchema.LOG.error("Missing required grant to clean queue tables: GRANT EXECUTE ON DBMS_AQADM");
                    }
                    throw e;
                }
            }
            if (this.flashbackAvailable()) {
                this.executeAlterStatementsForFlashbackTables();
            }
        }
        for (final String statement : this.generateDropStatementsForScheduledJobs()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("TRIGGER", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("SEQUENCE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("FUNCTION", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("MATERIALIZED VIEW", "PRESERVE TABLE")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("PACKAGE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("PROCEDURE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("SYNONYM", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("VIEW", "CASCADE CONSTRAINTS")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final Table table : this.allTables()) {
            table.drop();
        }
        for (final String statement : this.generateDropStatementsForXmlTables()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("CLUSTER", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("TYPE", "FORCE")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (final String statement : this.generateDropStatementsForObjectType("JAVA SOURCE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        this.jdbcTemplate.execute("PURGE RECYCLEBIN", new Object[0]);
    }
    
    private void executeAlterStatementsForFlashbackTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM DBA_FLASHBACK_ARCHIVE_TABLES WHERE owner_name = ?", this.name);
        for (final String tableName : tableNames) {
            this.jdbcTemplate.execute("ALTER TABLE " + ((OracleDbSupport)this.dbSupport).quote(this.name, tableName) + " NO FLASHBACK ARCHIVE", new Object[0]);
            final String queryForOracleTechnicalTables = "SELECT count(archive_table_name) FROM user_flashback_archive_tables WHERE table_name = ?";
            while (this.jdbcTemplate.queryForInt(queryForOracleTechnicalTables, tableName) > 0) {
                try {
                    OracleSchema.LOG.debug("Actively waiting for Flashback cleanup on table: " + tableName);
                    Thread.sleep(1000L);
                    continue;
                }
                catch (InterruptedException e) {
                    throw new FlywayException("Waiting for Flashback cleanup interrupted", e);
                }
                break;
            }
        }
    }
    
    private boolean flashbackAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("select count(*) from all_objects where object_name like 'DBA_FLASHBACK_ARCHIVE_TABLES'", new String[0]) > 0;
    }
    
    private List<String> generateDropStatementsForXmlTables() throws SQLException {
        final List<String> dropStatements = new ArrayList<String>();
        if (!this.xmlDBExtensionsAvailable()) {
            OracleSchema.LOG.debug("Oracle XML DB Extensions are not available. No cleaning of XML tables.");
            return dropStatements;
        }
        final List<String> objectNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM all_xml_tables WHERE owner = ?", this.name);
        for (final String objectName : objectNames) {
            dropStatements.add("DROP TABLE " + ((OracleDbSupport)this.dbSupport).quote(this.name, objectName) + " PURGE");
        }
        return dropStatements;
    }
    
    private boolean xmlDBExtensionsAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_users WHERE username = 'XDB'", new String[0]) > 0 && this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_views WHERE view_name = 'RESOURCE_VIEW'", new String[0]) > 0;
    }
    
    private List<String> generateDropStatementsForObjectType(final String objectType, final String extraArguments) throws SQLException {
        final String query = "SELECT object_name FROM all_objects WHERE object_type = ? AND owner = ? AND object_name NOT LIKE 'MDRS_%$' AND object_name NOT LIKE 'ISEQ$$_%'";
        final List<String> objectNames = this.jdbcTemplate.queryForStringList(query, objectType, this.name);
        final List<String> dropStatements = new ArrayList<String>();
        for (final String objectName : objectNames) {
            dropStatements.add("DROP " + objectType + " " + ((OracleDbSupport)this.dbSupport).quote(this.name, objectName) + " " + extraArguments);
        }
        return dropStatements;
    }
    
    private List<String> generateDropStatementsForSpatialExtensions(final boolean defaultSchemaForUser) throws SQLException {
        final List<String> statements = new ArrayList<String>();
        if (!this.spatialExtensionsAvailable()) {
            OracleSchema.LOG.debug("Oracle Spatial Extensions are not available. No cleaning of MDSYS tables and views.");
            return statements;
        }
        if (!((OracleDbSupport)this.dbSupport).getCurrentSchemaName().equalsIgnoreCase(this.name)) {
            int count = this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM all_sdo_geom_metadata WHERE owner=?", this.name);
            count += this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM all_sdo_index_info WHERE sdo_index_owner=?", this.name);
            if (count > 0) {
                OracleSchema.LOG.warn("Unable to clean Oracle Spatial objects for schema '" + this.name + "' as they do not belong to the default schema for this connection!");
            }
            return statements;
        }
        if (defaultSchemaForUser) {
            statements.add("DELETE FROM mdsys.user_sdo_geom_metadata");
            final List<String> indexNames = this.jdbcTemplate.queryForStringList("select INDEX_NAME from USER_SDO_INDEX_INFO", new String[0]);
            for (final String indexName : indexNames) {
                statements.add("DROP INDEX \"" + indexName + "\"");
            }
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForScheduledJobs() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        final List<String> jobNames = this.jdbcTemplate.queryForStringList("select JOB_NAME from ALL_SCHEDULER_JOBS WHERE owner=?", this.name);
        for (final String jobName : jobNames) {
            statements.add("begin DBMS_SCHEDULER.DROP_JOB(job_name => '" + jobName + "', defer => false, force => true); end;");
        }
        return statements;
    }
    
    private List<String> generateDropStatementsForQueueTables() throws SQLException {
        final List<String> statements = new ArrayList<String>();
        final List<String> queueTblNames = this.jdbcTemplate.queryForStringList("select QUEUE_TABLE from USER_QUEUE_TABLES", new String[0]);
        for (final String queueTblName : queueTblNames) {
            statements.add("begin DBMS_AQADM.drop_queue_table (queue_table=> '" + queueTblName + "', FORCE => TRUE); end;");
        }
        return statements;
    }
    
    private boolean spatialExtensionsAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_views WHERE owner = 'MDSYS' AND view_name = 'USER_SDO_GEOM_METADATA'", new String[0]) > 0;
    }
    
    @Override
    protected Table[] doAllTables() throws SQLException {
        final List<String> tableNames = this.jdbcTemplate.queryForStringList(" SELECT r FROM   (SELECT CONNECT_BY_ROOT t r FROM     (SELECT DISTINCT c1.table_name f, NVL(c2.table_name, at.table_name) t     FROM all_constraints c1       RIGHT JOIN all_constraints c2 ON c2.constraint_name = c1.r_constraint_name       RIGHT JOIN all_tables at ON at.table_name = c2.table_name     WHERE at.owner = ?       AND at.table_name NOT LIKE 'BIN$%'       AND at.table_name NOT LIKE 'MDRT_%$'       AND at.table_name NOT LIKE 'MLOG$%' AND at.table_name NOT LIKE 'RUPD$%'       AND at.table_name NOT LIKE 'DR$%'       AND at.table_name NOT LIKE 'SYS_IOT_OVER_%'       AND at.nested != 'YES'       AND at.secondary != 'Y')   CONNECT BY NOCYCLE PRIOR f = t) GROUP BY r ORDER BY COUNT(*)", this.name);
        final Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new OracleTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }
    
    @Override
    public Table getTable(final String tableName) {
        return new OracleTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
    
    static {
        LOG = LogFactory.getLog(OracleSchema.class);
    }
}

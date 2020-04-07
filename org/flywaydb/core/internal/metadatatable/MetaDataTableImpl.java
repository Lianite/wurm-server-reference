// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.metadatatable;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Schema;
import java.util.Date;
import java.sql.ResultSet;
import org.flywaydb.core.internal.util.jdbc.RowMapper;
import java.util.ArrayList;
import org.flywaydb.core.api.MigrationType;
import java.util.List;
import org.flywaydb.core.api.MigrationVersion;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import java.util.Map;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import java.util.HashMap;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Table;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.util.logging.Log;

public class MetaDataTableImpl implements MetaDataTable
{
    private static final Log LOG;
    private final DbSupport dbSupport;
    private final Table table;
    private final JdbcTemplate jdbcTemplate;
    
    public MetaDataTableImpl(final DbSupport dbSupport, final Table table) {
        this.jdbcTemplate = dbSupport.getJdbcTemplate();
        this.dbSupport = dbSupport;
        this.table = table;
    }
    
    @Override
    public boolean upgradeIfNecessary() {
        if (this.table.exists() && this.table.hasColumn("version_rank")) {
            MetaDataTableImpl.LOG.info("Upgrading metadata table " + this.table + " to the Flyway 4.0 format ...");
            final String resourceName = "org/flywaydb/core/internal/dbsupport/" + this.dbSupport.getDbName() + "/upgradeMetaDataTable.sql";
            final String source = new ClassPathResource(resourceName, this.getClass().getClassLoader()).loadAsString("UTF-8");
            final Map<String, String> placeholders = new HashMap<String, String>();
            placeholders.put("schema", this.table.getSchema().getName());
            placeholders.put("table", this.table.getName());
            final String sourceNoPlaceholders = new PlaceholderReplacer(placeholders, "${", "}").replacePlaceholders(source);
            final SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, this.dbSupport);
            sqlScript.execute(this.jdbcTemplate);
            return true;
        }
        return false;
    }
    
    private void createIfNotExists() {
        if (this.table.exists()) {
            return;
        }
        MetaDataTableImpl.LOG.info("Creating Metadata table: " + this.table);
        final String resourceName = "org/flywaydb/core/internal/dbsupport/" + this.dbSupport.getDbName() + "/createMetaDataTable.sql";
        final String source = new ClassPathResource(resourceName, this.getClass().getClassLoader()).loadAsString("UTF-8");
        final Map<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("schema", this.table.getSchema().getName());
        placeholders.put("table", this.table.getName());
        final String sourceNoPlaceholders = new PlaceholderReplacer(placeholders, "${", "}").replacePlaceholders(source);
        final SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, this.dbSupport);
        sqlScript.execute(this.jdbcTemplate);
        MetaDataTableImpl.LOG.debug("Metadata table " + this.table + " created.");
    }
    
    @Override
    public void lock() {
        this.createIfNotExists();
        this.table.lock();
    }
    
    @Override
    public void addAppliedMigration(final AppliedMigration appliedMigration) {
        this.createIfNotExists();
        final MigrationVersion version = appliedMigration.getVersion();
        try {
            final String versionStr = (version == null) ? null : version.toString();
            final String resourceName = "org/flywaydb/core/internal/dbsupport/" + this.dbSupport.getDbName() + "/updateMetaDataTable.sql";
            final ClassPathResource classPathResource = new ClassPathResource(resourceName, this.getClass().getClassLoader());
            if (classPathResource.exists()) {
                final String source = classPathResource.loadAsString("UTF-8");
                final Map<String, String> placeholders = new HashMap<String, String>();
                placeholders.put("schema", this.table.getSchema().getName());
                placeholders.put("table", this.table.getName());
                placeholders.put("installed_rank_val", String.valueOf(this.calculateInstalledRank()));
                placeholders.put("version_val", versionStr);
                placeholders.put("description_val", appliedMigration.getDescription());
                placeholders.put("type_val", appliedMigration.getType().name());
                placeholders.put("script_val", appliedMigration.getScript());
                placeholders.put("checksum_val", String.valueOf(appliedMigration.getChecksum()));
                placeholders.put("installed_by_val", this.dbSupport.getCurrentUserFunction());
                placeholders.put("execution_time_val", String.valueOf(appliedMigration.getExecutionTime() * 1000L));
                placeholders.put("success_val", String.valueOf(appliedMigration.isSuccess()));
                final String sourceNoPlaceholders = new PlaceholderReplacer(placeholders, "${", "}").replacePlaceholders(source);
                final SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, this.dbSupport);
                sqlScript.execute(this.jdbcTemplate);
            }
            else {
                this.jdbcTemplate.update("INSERT INTO " + this.table + " (" + this.dbSupport.quote("installed_rank") + "," + this.dbSupport.quote("version") + "," + this.dbSupport.quote("description") + "," + this.dbSupport.quote("type") + "," + this.dbSupport.quote("script") + "," + this.dbSupport.quote("checksum") + "," + this.dbSupport.quote("installed_by") + "," + this.dbSupport.quote("execution_time") + "," + this.dbSupport.quote("success") + ")" + " VALUES (?, ?, ?, ?, ?, ?, " + this.dbSupport.getCurrentUserFunction() + ", ?, ?)", this.calculateInstalledRank(), versionStr, appliedMigration.getDescription(), appliedMigration.getType().name(), appliedMigration.getScript(), appliedMigration.getChecksum(), appliedMigration.getExecutionTime(), appliedMigration.isSuccess());
            }
            MetaDataTableImpl.LOG.debug("MetaData table " + this.table + " successfully updated to reflect changes");
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to insert row for version '" + version + "' in metadata table " + this.table, e);
        }
    }
    
    private int calculateInstalledRank() throws SQLException {
        final int currentMax = this.jdbcTemplate.queryForInt("SELECT MAX(" + this.dbSupport.quote("installed_rank") + ")" + " FROM " + this.table, new String[0]);
        return currentMax + 1;
    }
    
    @Override
    public List<AppliedMigration> allAppliedMigrations() {
        return this.findAppliedMigrations(new MigrationType[0]);
    }
    
    private List<AppliedMigration> findAppliedMigrations(final MigrationType... migrationTypes) {
        if (!this.table.exists()) {
            return new ArrayList<AppliedMigration>();
        }
        this.createIfNotExists();
        String query = "SELECT " + this.dbSupport.quote("installed_rank") + "," + this.dbSupport.quote("version") + "," + this.dbSupport.quote("description") + "," + this.dbSupport.quote("type") + "," + this.dbSupport.quote("script") + "," + this.dbSupport.quote("checksum") + "," + this.dbSupport.quote("installed_on") + "," + this.dbSupport.quote("installed_by") + "," + this.dbSupport.quote("execution_time") + "," + this.dbSupport.quote("success") + " FROM " + this.table;
        if (migrationTypes.length > 0) {
            query = query + " WHERE " + this.dbSupport.quote("type") + " IN (";
            for (int i = 0; i < migrationTypes.length; ++i) {
                if (i > 0) {
                    query += ",";
                }
                query = query + "'" + migrationTypes[i] + "'";
            }
            query += ")";
        }
        query = query + " ORDER BY " + this.dbSupport.quote("installed_rank");
        try {
            return this.jdbcTemplate.query(query, (RowMapper<AppliedMigration>)new RowMapper<AppliedMigration>() {
                @Override
                public AppliedMigration mapRow(final ResultSet rs) throws SQLException {
                    Integer checksum = rs.getInt("checksum");
                    if (rs.wasNull()) {
                        checksum = null;
                    }
                    return new AppliedMigration(rs.getInt("installed_rank"), (rs.getString("version") != null) ? MigrationVersion.fromVersion(rs.getString("version")) : null, rs.getString("description"), MigrationType.valueOf(rs.getString("type")), rs.getString("script"), checksum, rs.getTimestamp("installed_on"), rs.getString("installed_by"), rs.getInt("execution_time"), rs.getBoolean("success"));
                }
            });
        }
        catch (SQLException e) {
            throw new FlywayException("Error while retrieving the list of applied migrations from metadata table " + this.table, e);
        }
    }
    
    @Override
    public void addBaselineMarker(final MigrationVersion baselineVersion, final String baselineDescription) {
        this.addAppliedMigration(new AppliedMigration(baselineVersion, baselineDescription, MigrationType.BASELINE, baselineDescription, null, 0, true));
    }
    
    @Override
    public void removeFailedMigrations() {
        if (!this.table.exists()) {
            MetaDataTableImpl.LOG.info("Repair of failed migration in metadata table " + this.table + " not necessary. No failed migration detected.");
            return;
        }
        this.createIfNotExists();
        try {
            final int failedCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("success") + "=" + this.dbSupport.getBooleanFalse(), new String[0]);
            if (failedCount == 0) {
                MetaDataTableImpl.LOG.info("Repair of failed migration in metadata table " + this.table + " not necessary. No failed migration detected.");
                return;
            }
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check the metadata table " + this.table + " for failed migrations", e);
        }
        try {
            this.jdbcTemplate.execute("DELETE FROM " + this.table + " WHERE " + this.dbSupport.quote("success") + " = " + this.dbSupport.getBooleanFalse(), new Object[0]);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to repair metadata table " + this.table, e);
        }
    }
    
    @Override
    public void addSchemasMarker(final Schema[] schemas) {
        this.createIfNotExists();
        this.addAppliedMigration(new AppliedMigration(MigrationVersion.fromVersion("0"), "<< Flyway Schema Creation >>", MigrationType.SCHEMA, StringUtils.arrayToCommaDelimitedString(schemas), null, 0, true));
    }
    
    @Override
    public boolean hasSchemasMarker() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            final int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + "='SCHEMA'", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has a schema marker migration", e);
        }
    }
    
    @Override
    public boolean hasBaselineMarker() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            final int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + "='INIT' OR " + this.dbSupport.quote("type") + "='BASELINE'", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has an baseline marker migration", e);
        }
    }
    
    @Override
    public AppliedMigration getBaselineMarker() {
        final List<AppliedMigration> appliedMigrations = this.findAppliedMigrations(MigrationType.BASELINE);
        return appliedMigrations.isEmpty() ? null : appliedMigrations.get(0);
    }
    
    @Override
    public boolean hasAppliedMigrations() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            final int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + " NOT IN ('SCHEMA', 'INIT', 'BASELINE')", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has applied migrations", e);
        }
    }
    
    @Override
    public void updateChecksum(final MigrationVersion version, final Integer checksum) {
        MetaDataTableImpl.LOG.info("Updating checksum of " + version + " to " + checksum + " ...");
        try {
            final String resourceName = "org/flywaydb/core/internal/dbsupport/" + this.dbSupport.getDbName() + "/updateChecksum.sql";
            final String source = new ClassPathResource(resourceName, this.getClass().getClassLoader()).loadAsString("UTF-8");
            final Map<String, String> placeholders = new HashMap<String, String>();
            placeholders.put("schema", this.table.getSchema().getName());
            placeholders.put("table", this.table.getName());
            placeholders.put("version_val", version.toString());
            placeholders.put("checksum_val", String.valueOf(checksum));
            final String sourceNoPlaceholders = new PlaceholderReplacer(placeholders, "${", "}").replacePlaceholders(source);
            final SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, this.dbSupport);
            sqlScript.execute(this.jdbcTemplate);
        }
        catch (FlywayException fe) {
            try {
                this.jdbcTemplate.update("UPDATE " + this.table + " SET " + this.dbSupport.quote("checksum") + "=" + checksum + " WHERE " + this.dbSupport.quote("version") + "='" + version + "'", new Object[0]);
            }
            catch (SQLException e) {
                throw new FlywayException("Unable to update checksum in metadata table " + this.table + " for version " + version + " to " + checksum, e);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.table.toString();
    }
    
    static {
        LOG = LogFactory.getLog(MetaDataTableImpl.class);
    }
}

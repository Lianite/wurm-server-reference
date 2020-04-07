// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.callback;

import java.util.Arrays;
import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.api.MigrationInfo;
import java.sql.Connection;
import org.flywaydb.core.internal.util.scanner.Resource;
import java.util.Iterator;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.Location;
import java.util.HashMap;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.Locations;
import org.flywaydb.core.internal.util.scanner.Scanner;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import java.util.Map;
import java.util.List;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.api.callback.FlywayCallback;

public class SqlScriptFlywayCallback implements FlywayCallback
{
    private static final Log LOG;
    private static final String BEFORE_CLEAN = "beforeClean";
    private static final String AFTER_CLEAN = "afterClean";
    private static final String BEFORE_MIGRATE = "beforeMigrate";
    private static final String AFTER_MIGRATE = "afterMigrate";
    private static final String BEFORE_EACH_MIGRATE = "beforeEachMigrate";
    private static final String AFTER_EACH_MIGRATE = "afterEachMigrate";
    private static final String BEFORE_VALIDATE = "beforeValidate";
    private static final String AFTER_VALIDATE = "afterValidate";
    private static final String BEFORE_BASELINE = "beforeBaseline";
    private static final String AFTER_BASELINE = "afterBaseline";
    private static final String BEFORE_REPAIR = "beforeRepair";
    private static final String AFTER_REPAIR = "afterRepair";
    private static final String BEFORE_INFO = "beforeInfo";
    private static final String AFTER_INFO = "afterInfo";
    public static final List<String> ALL_CALLBACKS;
    private final Map<String, SqlScript> scripts;
    
    public SqlScriptFlywayCallback(final DbSupport dbSupport, final Scanner scanner, final Locations locations, final PlaceholderReplacer placeholderReplacer, final String encoding, final String sqlMigrationSuffix) {
        this.scripts = new HashMap<String, SqlScript>();
        for (final String callback : SqlScriptFlywayCallback.ALL_CALLBACKS) {
            this.scripts.put(callback, null);
        }
        SqlScriptFlywayCallback.LOG.debug("Scanning for SQL callbacks ...");
        for (final Location location : locations.getLocations()) {
            Resource[] resources;
            try {
                resources = scanner.scanForResources(location, "", sqlMigrationSuffix);
            }
            catch (FlywayException e) {
                continue;
            }
            for (final Resource resource : resources) {
                final String key = resource.getFilename().replace(sqlMigrationSuffix, "");
                if (this.scripts.keySet().contains(key)) {
                    final SqlScript existing = this.scripts.get(key);
                    if (existing != null) {
                        throw new FlywayException("Found more than 1 SQL callback script for " + key + "!\n" + "Offenders:\n" + "-> " + existing.getResource().getLocationOnDisk() + "\n" + "-> " + resource.getLocationOnDisk());
                    }
                    this.scripts.put(key, new SqlScript(dbSupport, resource, placeholderReplacer, encoding));
                }
            }
        }
    }
    
    @Override
    public void beforeClean(final Connection connection) {
        this.execute("beforeClean", connection);
    }
    
    @Override
    public void afterClean(final Connection connection) {
        this.execute("afterClean", connection);
    }
    
    @Override
    public void beforeMigrate(final Connection connection) {
        this.execute("beforeMigrate", connection);
    }
    
    @Override
    public void afterMigrate(final Connection connection) {
        this.execute("afterMigrate", connection);
    }
    
    @Override
    public void beforeEachMigrate(final Connection connection, final MigrationInfo info) {
        this.execute("beforeEachMigrate", connection);
    }
    
    @Override
    public void afterEachMigrate(final Connection connection, final MigrationInfo info) {
        this.execute("afterEachMigrate", connection);
    }
    
    @Override
    public void beforeValidate(final Connection connection) {
        this.execute("beforeValidate", connection);
    }
    
    @Override
    public void afterValidate(final Connection connection) {
        this.execute("afterValidate", connection);
    }
    
    @Override
    public void beforeBaseline(final Connection connection) {
        this.execute("beforeBaseline", connection);
    }
    
    @Override
    public void afterBaseline(final Connection connection) {
        this.execute("afterBaseline", connection);
    }
    
    @Override
    public void beforeRepair(final Connection connection) {
        this.execute("beforeRepair", connection);
    }
    
    @Override
    public void afterRepair(final Connection connection) {
        this.execute("afterRepair", connection);
    }
    
    @Override
    public void beforeInfo(final Connection connection) {
        this.execute("beforeInfo", connection);
    }
    
    @Override
    public void afterInfo(final Connection connection) {
        this.execute("afterInfo", connection);
    }
    
    private void execute(final String key, final Connection connection) {
        final SqlScript sqlScript = this.scripts.get(key);
        if (sqlScript != null) {
            SqlScriptFlywayCallback.LOG.info("Executing SQL callback: " + key);
            sqlScript.execute(new JdbcTemplate(connection, 0));
        }
    }
    
    static {
        LOG = LogFactory.getLog(SqlScriptFlywayCallback.class);
        ALL_CALLBACKS = Arrays.asList("beforeClean", "afterClean", "beforeMigrate", "beforeEachMigrate", "afterEachMigrate", "afterMigrate", "beforeValidate", "afterValidate", "beforeBaseline", "afterBaseline", "beforeRepair", "afterRepair", "beforeInfo", "afterInfo");
    }
}

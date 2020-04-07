// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Set;
import org.flywaydb.core.internal.metadatatable.MetaDataTableImpl;
import org.flywaydb.core.internal.callback.SqlScriptFlywayCallback;
import java.util.LinkedHashSet;
import java.util.Arrays;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import org.flywaydb.core.internal.util.VersionPrinter;
import java.util.Iterator;
import java.util.Properties;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.resolver.CompositeMigrationResolver;
import org.flywaydb.core.internal.util.ConfigurationInjectionUtils;
import org.flywaydb.core.internal.util.scanner.Scanner;
import org.flywaydb.core.internal.command.DbRepair;
import org.flywaydb.core.internal.info.MigrationInfoServiceImpl;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.internal.command.DbClean;
import org.flywaydb.core.internal.command.DbValidate;
import org.flywaydb.core.internal.command.DbMigrate;
import java.util.Collection;
import org.flywaydb.core.internal.command.DbBaseline;
import java.util.ArrayList;
import org.flywaydb.core.internal.command.DbSchemas;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import java.sql.Connection;
import java.util.List;
import org.flywaydb.core.internal.util.ClassUtils;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.util.Location;
import java.util.HashMap;
import javax.sql.DataSource;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.callback.FlywayCallback;
import java.util.Map;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.internal.util.Locations;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.api.configuration.FlywayConfiguration;

public class Flyway implements FlywayConfiguration
{
    private static final Log LOG;
    private static final String PLACEHOLDERS_PROPERTY_PREFIX = "flyway.placeholders.";
    private Locations locations;
    private String encoding;
    private String[] schemaNames;
    private String table;
    private MigrationVersion target;
    private boolean placeholderReplacement;
    private Map<String, String> placeholders;
    private String placeholderPrefix;
    private String placeholderSuffix;
    private String sqlMigrationPrefix;
    private String repeatableSqlMigrationPrefix;
    private String sqlMigrationSeparator;
    private String sqlMigrationSuffix;
    private boolean ignoreFutureMigrations;
    @Deprecated
    private boolean ignoreFailedFutureMigration;
    private boolean validateOnMigrate;
    private boolean cleanOnValidationError;
    private boolean cleanDisabled;
    private MigrationVersion baselineVersion;
    private String baselineDescription;
    private boolean baselineOnMigrate;
    private boolean outOfOrder;
    private FlywayCallback[] callbacks;
    private boolean skipDefaultCallbacks;
    private MigrationResolver[] resolvers;
    private boolean skipDefaultResolvers;
    private boolean createdDataSource;
    private DataSource dataSource;
    private ClassLoader classLoader;
    private boolean dbConnectionInfoPrinted;
    
    public Flyway() {
        this.locations = new Locations(new String[] { "db/migration" });
        this.encoding = "UTF-8";
        this.schemaNames = new String[0];
        this.table = "schema_version";
        this.target = MigrationVersion.LATEST;
        this.placeholderReplacement = true;
        this.placeholders = new HashMap<String, String>();
        this.placeholderPrefix = "${";
        this.placeholderSuffix = "}";
        this.sqlMigrationPrefix = "V";
        this.repeatableSqlMigrationPrefix = "R";
        this.sqlMigrationSeparator = "__";
        this.sqlMigrationSuffix = ".sql";
        this.ignoreFutureMigrations = true;
        this.validateOnMigrate = true;
        this.baselineVersion = MigrationVersion.fromVersion("1");
        this.baselineDescription = "<< Flyway Baseline >>";
        this.callbacks = new FlywayCallback[0];
        this.resolvers = new MigrationResolver[0];
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    
    @Override
    public String[] getLocations() {
        final String[] result = new String[this.locations.getLocations().size()];
        for (int i = 0; i < this.locations.getLocations().size(); ++i) {
            result[i] = this.locations.getLocations().get(i).toString();
        }
        return result;
    }
    
    @Override
    public String getEncoding() {
        return this.encoding;
    }
    
    @Override
    public String[] getSchemas() {
        return this.schemaNames;
    }
    
    @Override
    public String getTable() {
        return this.table;
    }
    
    @Override
    public MigrationVersion getTarget() {
        return this.target;
    }
    
    @Override
    public boolean isPlaceholderReplacement() {
        return this.placeholderReplacement;
    }
    
    @Override
    public Map<String, String> getPlaceholders() {
        return this.placeholders;
    }
    
    @Override
    public String getPlaceholderPrefix() {
        return this.placeholderPrefix;
    }
    
    @Override
    public String getPlaceholderSuffix() {
        return this.placeholderSuffix;
    }
    
    @Override
    public String getSqlMigrationPrefix() {
        return this.sqlMigrationPrefix;
    }
    
    @Override
    public String getRepeatableSqlMigrationPrefix() {
        return this.repeatableSqlMigrationPrefix;
    }
    
    @Override
    public String getSqlMigrationSeparator() {
        return this.sqlMigrationSeparator;
    }
    
    @Override
    public String getSqlMigrationSuffix() {
        return this.sqlMigrationSuffix;
    }
    
    public boolean isIgnoreFutureMigrations() {
        return this.ignoreFutureMigrations;
    }
    
    @Deprecated
    public boolean isIgnoreFailedFutureMigration() {
        Flyway.LOG.warn("ignoreFailedFutureMigration has been deprecated and will be removed in Flyway 5.0. Use the more generic ignoreFutureMigrations instead.");
        return this.ignoreFailedFutureMigration;
    }
    
    public boolean isValidateOnMigrate() {
        return this.validateOnMigrate;
    }
    
    public boolean isCleanOnValidationError() {
        return this.cleanOnValidationError;
    }
    
    public boolean isCleanDisabled() {
        return this.cleanDisabled;
    }
    
    @Override
    public MigrationVersion getBaselineVersion() {
        return this.baselineVersion;
    }
    
    @Override
    public String getBaselineDescription() {
        return this.baselineDescription;
    }
    
    public boolean isBaselineOnMigrate() {
        return this.baselineOnMigrate;
    }
    
    public boolean isOutOfOrder() {
        return this.outOfOrder;
    }
    
    @Override
    public MigrationResolver[] getResolvers() {
        return this.resolvers;
    }
    
    @Override
    public boolean isSkipDefaultResolvers() {
        return this.skipDefaultResolvers;
    }
    
    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public void setIgnoreFutureMigrations(final boolean ignoreFutureMigrations) {
        this.ignoreFutureMigrations = ignoreFutureMigrations;
    }
    
    @Deprecated
    public void setIgnoreFailedFutureMigration(final boolean ignoreFailedFutureMigration) {
        Flyway.LOG.warn("ignoreFailedFutureMigration has been deprecated and will be removed in Flyway 5.0. Use the more generic ignoreFutureMigrations instead.");
        this.ignoreFailedFutureMigration = ignoreFailedFutureMigration;
    }
    
    public void setValidateOnMigrate(final boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
    }
    
    public void setCleanOnValidationError(final boolean cleanOnValidationError) {
        this.cleanOnValidationError = cleanOnValidationError;
    }
    
    public void setCleanDisabled(final boolean cleanDisabled) {
        this.cleanDisabled = cleanDisabled;
    }
    
    public void setLocations(final String... locations) {
        this.locations = new Locations(locations);
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setSchemas(final String... schemas) {
        this.schemaNames = schemas;
    }
    
    public void setTable(final String table) {
        this.table = table;
    }
    
    public void setTarget(final MigrationVersion target) {
        this.target = target;
    }
    
    public void setTargetAsString(final String target) {
        this.target = MigrationVersion.fromVersion(target);
    }
    
    public void setPlaceholderReplacement(final boolean placeholderReplacement) {
        this.placeholderReplacement = placeholderReplacement;
    }
    
    public void setPlaceholders(final Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }
    
    public void setPlaceholderPrefix(final String placeholderPrefix) {
        if (!StringUtils.hasLength(placeholderPrefix)) {
            throw new FlywayException("placeholderPrefix cannot be empty!");
        }
        this.placeholderPrefix = placeholderPrefix;
    }
    
    public void setPlaceholderSuffix(final String placeholderSuffix) {
        if (!StringUtils.hasLength(placeholderSuffix)) {
            throw new FlywayException("placeholderSuffix cannot be empty!");
        }
        this.placeholderSuffix = placeholderSuffix;
    }
    
    public void setSqlMigrationPrefix(final String sqlMigrationPrefix) {
        this.sqlMigrationPrefix = sqlMigrationPrefix;
    }
    
    public void setRepeatableSqlMigrationPrefix(final String repeatableSqlMigrationPrefix) {
        this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
    }
    
    public void setSqlMigrationSeparator(final String sqlMigrationSeparator) {
        if (!StringUtils.hasLength(sqlMigrationSeparator)) {
            throw new FlywayException("sqlMigrationSeparator cannot be empty!");
        }
        this.sqlMigrationSeparator = sqlMigrationSeparator;
    }
    
    public void setSqlMigrationSuffix(final String sqlMigrationSuffix) {
        this.sqlMigrationSuffix = sqlMigrationSuffix;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.createdDataSource = false;
    }
    
    public void setDataSource(final String url, final String user, final String password, final String... initSqls) {
        this.dataSource = new DriverDataSource(this.classLoader, null, url, user, password, initSqls);
        this.createdDataSource = true;
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void setBaselineVersion(final MigrationVersion baselineVersion) {
        this.baselineVersion = baselineVersion;
    }
    
    public void setBaselineVersionAsString(final String baselineVersion) {
        this.baselineVersion = MigrationVersion.fromVersion(baselineVersion);
    }
    
    public void setBaselineDescription(final String baselineDescription) {
        this.baselineDescription = baselineDescription;
    }
    
    public void setBaselineOnMigrate(final boolean baselineOnMigrate) {
        this.baselineOnMigrate = baselineOnMigrate;
    }
    
    public void setOutOfOrder(final boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }
    
    @Override
    public FlywayCallback[] getCallbacks() {
        return this.callbacks;
    }
    
    @Override
    public boolean isSkipDefaultCallbacks() {
        return this.skipDefaultCallbacks;
    }
    
    public void setCallbacks(final FlywayCallback... callbacks) {
        this.callbacks = callbacks;
    }
    
    public void setCallbacksAsClassNames(final String... callbacks) {
        final List<FlywayCallback> callbackList = ClassUtils.instantiateAll(callbacks, this.classLoader);
        this.setCallbacks((FlywayCallback[])callbackList.toArray(new FlywayCallback[callbacks.length]));
    }
    
    public void setSkipDefaultCallbacks(final boolean skipDefaultCallbacks) {
        this.skipDefaultCallbacks = skipDefaultCallbacks;
    }
    
    public void setResolvers(final MigrationResolver... resolvers) {
        this.resolvers = resolvers;
    }
    
    public void setResolversAsClassNames(final String... resolvers) {
        final List<MigrationResolver> resolverList = ClassUtils.instantiateAll(resolvers, this.classLoader);
        this.setResolvers((MigrationResolver[])resolverList.toArray(new MigrationResolver[resolvers.length]));
    }
    
    public void setSkipDefaultResolvers(final boolean skipDefaultResolvers) {
        this.skipDefaultResolvers = skipDefaultResolvers;
    }
    
    public int migrate() throws FlywayException {
        return this.execute((Command<Integer>)new Command<Integer>() {
            @Override
            public Integer execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                if (Flyway.this.validateOnMigrate) {
                    Flyway.this.doValidate(connectionMetaDataTable, dbSupport, migrationResolver, metaDataTable, schemas, flywayCallbacks, true);
                }
                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
                if (!metaDataTable.hasSchemasMarker() && !metaDataTable.hasBaselineMarker() && !metaDataTable.hasAppliedMigrations()) {
                    final List<Schema> nonEmptySchemas = new ArrayList<Schema>();
                    for (final Schema schema : schemas) {
                        if (!schema.empty()) {
                            nonEmptySchemas.add(schema);
                        }
                    }
                    if (Flyway.this.baselineOnMigrate || nonEmptySchemas.isEmpty()) {
                        if (Flyway.this.baselineOnMigrate && !nonEmptySchemas.isEmpty()) {
                            new DbBaseline(connectionMetaDataTable, dbSupport, metaDataTable, schemas[0], Flyway.this.baselineVersion, Flyway.this.baselineDescription, flywayCallbacks).baseline();
                        }
                    }
                    else {
                        if (nonEmptySchemas.size() != 1) {
                            throw new FlywayException("Found non-empty schemas " + StringUtils.collectionToCommaDelimitedString(nonEmptySchemas) + " without metadata table! Use baseline()" + " or set baselineOnMigrate to true to initialize the metadata table.");
                        }
                        final Schema schema2 = nonEmptySchemas.get(0);
                        if (schema2.allTables().length != 1 || !schema2.getTable(Flyway.this.table).exists()) {
                            throw new FlywayException("Found non-empty schema " + schema2 + " without metadata table! Use baseline()" + " or set baselineOnMigrate to true to initialize the metadata table.");
                        }
                    }
                }
                final DbMigrate dbMigrate = new DbMigrate(connectionMetaDataTable, connectionUserObjects, dbSupport, metaDataTable, schemas[0], migrationResolver, Flyway.this.target, Flyway.this.ignoreFutureMigrations, Flyway.this.ignoreFailedFutureMigration, Flyway.this.outOfOrder, flywayCallbacks);
                return dbMigrate.migrate();
            }
        });
    }
    
    public void validate() throws FlywayException {
        this.execute((Command<Object>)new Command<Void>() {
            @Override
            public Void execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                Flyway.this.doValidate(connectionMetaDataTable, dbSupport, migrationResolver, metaDataTable, schemas, flywayCallbacks, false);
                return null;
            }
        });
    }
    
    private void doValidate(final Connection connectionMetaDataTable, final DbSupport dbSupport, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final Schema[] schemas, final FlywayCallback[] flywayCallbacks, final boolean pending) {
        final String validationError = new DbValidate(connectionMetaDataTable, dbSupport, metaDataTable, schemas[0], migrationResolver, this.target, this.outOfOrder, pending, this.ignoreFutureMigrations, flywayCallbacks).validate();
        if (validationError != null) {
            if (!this.cleanOnValidationError) {
                throw new FlywayException("Validate failed: " + validationError);
            }
            new DbClean(connectionMetaDataTable, dbSupport, metaDataTable, schemas, flywayCallbacks, this.cleanDisabled).clean();
        }
    }
    
    public void clean() {
        this.execute((Command<Object>)new Command<Void>() {
            @Override
            public Void execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                new DbClean(connectionMetaDataTable, dbSupport, metaDataTable, schemas, flywayCallbacks, Flyway.this.cleanDisabled).clean();
                return null;
            }
        });
    }
    
    public MigrationInfoService info() {
        return this.execute((Command<MigrationInfoService>)new Command<MigrationInfoService>() {
            @Override
            public MigrationInfoService execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                try {
                    for (final FlywayCallback callback : flywayCallbacks) {
                        new TransactionTemplate(connectionMetaDataTable).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                            @Override
                            public Object doInTransaction() throws SQLException {
                                dbSupport.changeCurrentSchemaTo(schemas[0]);
                                callback.beforeInfo(connectionMetaDataTable);
                                return null;
                            }
                        });
                    }
                    final MigrationInfoServiceImpl migrationInfoService = new MigrationInfoServiceImpl(migrationResolver, metaDataTable, Flyway.this.target, Flyway.this.outOfOrder, true, true);
                    migrationInfoService.refresh();
                    for (final FlywayCallback callback2 : flywayCallbacks) {
                        new TransactionTemplate(connectionMetaDataTable).execute((TransactionCallback<Object>)new TransactionCallback<Object>() {
                            @Override
                            public Object doInTransaction() throws SQLException {
                                dbSupport.changeCurrentSchemaTo(schemas[0]);
                                callback2.afterInfo(connectionMetaDataTable);
                                return null;
                            }
                        });
                    }
                    return migrationInfoService;
                }
                finally {
                    dbSupport.restoreCurrentSchema();
                }
            }
        });
    }
    
    public void baseline() throws FlywayException {
        this.execute((Command<Object>)new Command<Void>() {
            @Override
            public Void execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                new DbSchemas(connectionMetaDataTable, schemas, metaDataTable).create();
                new DbBaseline(connectionMetaDataTable, dbSupport, metaDataTable, schemas[0], Flyway.this.baselineVersion, Flyway.this.baselineDescription, flywayCallbacks).baseline();
                return null;
            }
        });
    }
    
    public void repair() throws FlywayException {
        this.execute((Command<Object>)new Command<Void>() {
            @Override
            public Void execute(final Connection connectionMetaDataTable, final Connection connectionUserObjects, final MigrationResolver migrationResolver, final MetaDataTable metaDataTable, final DbSupport dbSupport, final Schema[] schemas, final FlywayCallback[] flywayCallbacks) {
                new DbRepair(dbSupport, connectionMetaDataTable, schemas[0], migrationResolver, metaDataTable, flywayCallbacks).repair();
                return null;
            }
        });
    }
    
    private MigrationResolver createMigrationResolver(final DbSupport dbSupport, final Scanner scanner) {
        for (final MigrationResolver resolver : this.resolvers) {
            ConfigurationInjectionUtils.injectFlywayConfiguration(resolver, this);
        }
        return new CompositeMigrationResolver(dbSupport, scanner, this, this.locations, this.encoding, this.sqlMigrationPrefix, this.repeatableSqlMigrationPrefix, this.sqlMigrationSeparator, this.sqlMigrationSuffix, this.createPlaceholderReplacer(), this.resolvers);
    }
    
    private PlaceholderReplacer createPlaceholderReplacer() {
        if (this.placeholderReplacement) {
            return new PlaceholderReplacer(this.placeholders, this.placeholderPrefix, this.placeholderSuffix);
        }
        return PlaceholderReplacer.NO_PLACEHOLDERS;
    }
    
    public void configure(final Properties properties) {
        final Map<String, String> props = new HashMap<String, String>();
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            props.put(entry.getKey().toString(), entry.getValue().toString());
        }
        final String driverProp = this.getValueAndRemoveEntry(props, "flyway.driver");
        final String urlProp = this.getValueAndRemoveEntry(props, "flyway.url");
        final String userProp = this.getValueAndRemoveEntry(props, "flyway.user");
        final String passwordProp = this.getValueAndRemoveEntry(props, "flyway.password");
        if (StringUtils.hasText(urlProp)) {
            this.setDataSource(new DriverDataSource(this.classLoader, driverProp, urlProp, userProp, passwordProp, new String[0]));
        }
        else if (!StringUtils.hasText(urlProp) && (StringUtils.hasText(driverProp) || StringUtils.hasText(userProp) || StringUtils.hasText(passwordProp))) {
            Flyway.LOG.warn("Discarding INCOMPLETE dataSource configuration! flyway.url must be set.");
        }
        final String locationsProp = this.getValueAndRemoveEntry(props, "flyway.locations");
        if (locationsProp != null) {
            this.setLocations(StringUtils.tokenizeToStringArray(locationsProp, ","));
        }
        final String placeholderReplacementProp = this.getValueAndRemoveEntry(props, "flyway.placeholderReplacement");
        if (placeholderReplacementProp != null) {
            this.setPlaceholderReplacement(Boolean.parseBoolean(placeholderReplacementProp));
        }
        final String placeholderPrefixProp = this.getValueAndRemoveEntry(props, "flyway.placeholderPrefix");
        if (placeholderPrefixProp != null) {
            this.setPlaceholderPrefix(placeholderPrefixProp);
        }
        final String placeholderSuffixProp = this.getValueAndRemoveEntry(props, "flyway.placeholderSuffix");
        if (placeholderSuffixProp != null) {
            this.setPlaceholderSuffix(placeholderSuffixProp);
        }
        final String sqlMigrationPrefixProp = this.getValueAndRemoveEntry(props, "flyway.sqlMigrationPrefix");
        if (sqlMigrationPrefixProp != null) {
            this.setSqlMigrationPrefix(sqlMigrationPrefixProp);
        }
        final String repeatableSqlMigrationPrefixProp = this.getValueAndRemoveEntry(props, "flyway.repeatableSqlMigrationPrefix");
        if (repeatableSqlMigrationPrefixProp != null) {
            this.setRepeatableSqlMigrationPrefix(repeatableSqlMigrationPrefixProp);
        }
        final String sqlMigrationSeparatorProp = this.getValueAndRemoveEntry(props, "flyway.sqlMigrationSeparator");
        if (sqlMigrationSeparatorProp != null) {
            this.setSqlMigrationSeparator(sqlMigrationSeparatorProp);
        }
        final String sqlMigrationSuffixProp = this.getValueAndRemoveEntry(props, "flyway.sqlMigrationSuffix");
        if (sqlMigrationSuffixProp != null) {
            this.setSqlMigrationSuffix(sqlMigrationSuffixProp);
        }
        final String encodingProp = this.getValueAndRemoveEntry(props, "flyway.encoding");
        if (encodingProp != null) {
            this.setEncoding(encodingProp);
        }
        final String schemasProp = this.getValueAndRemoveEntry(props, "flyway.schemas");
        if (schemasProp != null) {
            this.setSchemas(StringUtils.tokenizeToStringArray(schemasProp, ","));
        }
        final String tableProp = this.getValueAndRemoveEntry(props, "flyway.table");
        if (tableProp != null) {
            this.setTable(tableProp);
        }
        final String cleanOnValidationErrorProp = this.getValueAndRemoveEntry(props, "flyway.cleanOnValidationError");
        if (cleanOnValidationErrorProp != null) {
            this.setCleanOnValidationError(Boolean.parseBoolean(cleanOnValidationErrorProp));
        }
        final String cleanDisabledProp = this.getValueAndRemoveEntry(props, "flyway.cleanDisabled");
        if (cleanDisabledProp != null) {
            this.setCleanDisabled(Boolean.parseBoolean(cleanDisabledProp));
        }
        final String validateOnMigrateProp = this.getValueAndRemoveEntry(props, "flyway.validateOnMigrate");
        if (validateOnMigrateProp != null) {
            this.setValidateOnMigrate(Boolean.parseBoolean(validateOnMigrateProp));
        }
        final String baselineVersionProp = this.getValueAndRemoveEntry(props, "flyway.baselineVersion");
        if (baselineVersionProp != null) {
            this.setBaselineVersion(MigrationVersion.fromVersion(baselineVersionProp));
        }
        final String baselineDescriptionProp = this.getValueAndRemoveEntry(props, "flyway.baselineDescription");
        if (baselineDescriptionProp != null) {
            this.setBaselineDescription(baselineDescriptionProp);
        }
        final String baselineOnMigrateProp = this.getValueAndRemoveEntry(props, "flyway.baselineOnMigrate");
        if (baselineOnMigrateProp != null) {
            this.setBaselineOnMigrate(Boolean.parseBoolean(baselineOnMigrateProp));
        }
        final String ignoreFutureMigrationsProp = this.getValueAndRemoveEntry(props, "flyway.ignoreFutureMigrations");
        if (ignoreFutureMigrationsProp != null) {
            this.setIgnoreFutureMigrations(Boolean.parseBoolean(ignoreFutureMigrationsProp));
        }
        final String ignoreFailedFutureMigrationProp = this.getValueAndRemoveEntry(props, "flyway.ignoreFailedFutureMigration");
        if (ignoreFailedFutureMigrationProp != null) {
            this.setIgnoreFailedFutureMigration(Boolean.parseBoolean(ignoreFailedFutureMigrationProp));
        }
        final String targetProp = this.getValueAndRemoveEntry(props, "flyway.target");
        if (targetProp != null) {
            this.setTarget(MigrationVersion.fromVersion(targetProp));
        }
        final String outOfOrderProp = this.getValueAndRemoveEntry(props, "flyway.outOfOrder");
        if (outOfOrderProp != null) {
            this.setOutOfOrder(Boolean.parseBoolean(outOfOrderProp));
        }
        final String resolversProp = this.getValueAndRemoveEntry(props, "flyway.resolvers");
        if (StringUtils.hasLength(resolversProp)) {
            this.setResolversAsClassNames(StringUtils.tokenizeToStringArray(resolversProp, ","));
        }
        final String skipDefaultResolversProp = this.getValueAndRemoveEntry(props, "flyway.skipDefaultResolvers");
        if (skipDefaultResolversProp != null) {
            this.setSkipDefaultResolvers(Boolean.parseBoolean(skipDefaultResolversProp));
        }
        final String callbacksProp = this.getValueAndRemoveEntry(props, "flyway.callbacks");
        if (StringUtils.hasLength(callbacksProp)) {
            this.setCallbacksAsClassNames(StringUtils.tokenizeToStringArray(callbacksProp, ","));
        }
        final String skipDefaultCallbacksProp = this.getValueAndRemoveEntry(props, "flyway.skipDefaultCallbacks");
        if (skipDefaultCallbacksProp != null) {
            this.setSkipDefaultCallbacks(Boolean.parseBoolean(skipDefaultCallbacksProp));
        }
        final Map<String, String> placeholdersFromProps = new HashMap<String, String>(this.placeholders);
        final Iterator<Map.Entry<String, String>> iterator = props.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> entry2 = iterator.next();
            final String propertyName = entry2.getKey();
            if (propertyName.startsWith("flyway.placeholders.") && propertyName.length() > "flyway.placeholders.".length()) {
                final String placeholderName = propertyName.substring("flyway.placeholders.".length());
                final String placeholderValue = entry2.getValue();
                placeholdersFromProps.put(placeholderName, placeholderValue);
                iterator.remove();
            }
        }
        this.setPlaceholders(placeholdersFromProps);
        for (final String key : props.keySet()) {
            if (key.startsWith("flyway.")) {
                Flyway.LOG.warn("Unknown configuration property: " + key);
            }
        }
    }
    
    private String getValueAndRemoveEntry(final Map<String, String> map, final String key) {
        final String value = map.get(key);
        map.remove(key);
        return value;
    }
    
     <T> T execute(final Command<T> command) {
        VersionPrinter.printVersion();
        Connection connectionMetaDataTable = null;
        Connection connectionUserObjects = null;
        T result;
        try {
            if (this.dataSource == null) {
                throw new FlywayException("Unable to connect to the database. Configure the url, user and password!");
            }
            connectionMetaDataTable = JdbcUtils.openConnection(this.dataSource);
            connectionUserObjects = JdbcUtils.openConnection(this.dataSource);
            final DbSupport dbSupport = DbSupportFactory.createDbSupport(connectionMetaDataTable, !this.dbConnectionInfoPrinted);
            this.dbConnectionInfoPrinted = true;
            Flyway.LOG.debug("DDL Transactions Supported: " + dbSupport.supportsDdlTransactions());
            if (this.schemaNames.length == 0) {
                final Schema currentSchema = dbSupport.getOriginalSchema();
                if (currentSchema == null) {
                    throw new FlywayException("Unable to determine schema for the metadata table. Set a default schema for the connection or specify one using the schemas property!");
                }
                this.setSchemas(currentSchema.getName());
            }
            if (this.schemaNames.length == 1) {
                Flyway.LOG.debug("Schema: " + this.schemaNames[0]);
            }
            else {
                Flyway.LOG.debug("Schemas: " + StringUtils.arrayToCommaDelimitedString(this.schemaNames));
            }
            final Schema[] schemas = new Schema[this.schemaNames.length];
            for (int i = 0; i < this.schemaNames.length; ++i) {
                schemas[i] = dbSupport.getSchema(this.schemaNames[i]);
            }
            final Scanner scanner = new Scanner(this.classLoader);
            final MigrationResolver migrationResolver = this.createMigrationResolver(dbSupport, scanner);
            final Set<FlywayCallback> flywayCallbacks = new LinkedHashSet<FlywayCallback>(Arrays.asList(this.callbacks));
            if (!this.skipDefaultCallbacks) {
                flywayCallbacks.add(new SqlScriptFlywayCallback(dbSupport, scanner, this.locations, this.createPlaceholderReplacer(), this.encoding, this.sqlMigrationSuffix));
            }
            for (final FlywayCallback callback : flywayCallbacks) {
                ConfigurationInjectionUtils.injectFlywayConfiguration(callback, this);
            }
            final FlywayCallback[] flywayCallbacksArray = flywayCallbacks.toArray(new FlywayCallback[flywayCallbacks.size()]);
            final MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport, schemas[0].getTable(this.table));
            if (metaDataTable.upgradeIfNecessary()) {
                new DbRepair(dbSupport, connectionMetaDataTable, schemas[0], migrationResolver, metaDataTable, flywayCallbacksArray).repairChecksums();
                Flyway.LOG.info("Metadata table " + this.table + " successfully upgraded to the Flyway 4.0 format.");
            }
            result = command.execute(connectionMetaDataTable, connectionUserObjects, migrationResolver, metaDataTable, dbSupport, schemas, flywayCallbacksArray);
        }
        finally {
            JdbcUtils.closeConnection(connectionUserObjects);
            JdbcUtils.closeConnection(connectionMetaDataTable);
            if (this.dataSource instanceof DriverDataSource && this.createdDataSource) {
                ((DriverDataSource)this.dataSource).close();
            }
        }
        return result;
    }
    
    static {
        LOG = LogFactory.getLog(Flyway.class);
    }
    
    interface Command<T>
    {
        T execute(final Connection p0, final Connection p1, final MigrationResolver p2, final MetaDataTable p3, final DbSupport p4, final Schema[] p5, final FlywayCallback[] p6);
    }
}

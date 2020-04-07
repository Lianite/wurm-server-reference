// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.database.migrations.MysqlMigrationStrategy;
import java.util.Iterator;
import java.util.List;
import com.wurmonline.server.database.migrations.SqliteMigrationStrategy;
import java.util.ArrayList;
import com.wurmonline.server.items.DbItem;
import com.wurmonline.server.creatures.CreaturePos;
import com.wurmonline.server.gui.folders.DistEntity;
import com.wurmonline.server.gui.folders.Folders;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.wurmonline.server.database.migrations.MigrationResult;
import java.util.logging.Level;
import java.sql.SQLException;
import com.wurmonline.server.database.MysqlConnectionFactory;
import com.wurmonline.server.database.SqliteConnectionFactory;
import com.wurmonline.server.database.migrations.MigrationStrategy;
import org.sqlite.SQLiteConfig;
import java.util.regex.Pattern;
import com.wurmonline.server.database.ConnectionFactory;
import java.sql.Connection;
import java.nio.file.Path;
import com.wurmonline.server.database.WurmDatabaseSchema;
import java.util.EnumMap;
import java.util.logging.Logger;

public class DbConnector implements TimeConstants
{
    private static final Logger logger;
    private static boolean sqlite;
    private static boolean isInitialized;
    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private static EnumMap<WurmDatabaseSchema, DbConnector> CONNECTORS;
    private static boolean isTrackingOpenDatabaseResources;
    private static final Path MIGRATIONS_DIR;
    private Connection connection;
    private long lastUsed;
    private final String loggingName;
    private final ConnectionFactory connectionFactory;
    private static final Pattern portPattern;
    private static final SQLiteConfig config;
    private static MigrationStrategy MIGRATION_STRATEGY;
    
    public static void initialize() {
        initialize(false);
    }
    
    public static void initialize(final boolean reinitialize) {
        if (DbConnector.isInitialized && !reinitialize) {
            return;
        }
        ConfigHelper<? extends ConnectionFactory> configHelper;
        String driver;
        if (isUseSqlite()) {
            configHelper = new SqliteConfigHelper();
            driver = "org.sqlite.JDBC";
        }
        else {
            configHelper = new MysqlConfigHelper();
            driver = Constants.dbDriver;
        }
        try {
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            DbConnector.logger.warning("No class found for database driver: " + Constants.dbDriver);
            e.printStackTrace();
        }
        DbConnector.CONNECTORS = configHelper.buildConnectors();
        DbConnector.MIGRATION_STRATEGY = configHelper.newMigrationStrategy();
        setInitialized(true);
    }
    
    protected DbConnector(final String driver, final String host, final String port, final WurmDatabaseSchema schema, final String user, final String password, final String loggingName) {
        this.lastUsed = System.currentTimeMillis();
        if (isUseSqlite()) {
            DbConnector.config.setJournalMode(SQLiteConfig.JournalMode.WAL);
            DbConnector.config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
            this.connectionFactory = new SqliteConnectionFactory(host, schema, DbConnector.config);
        }
        else {
            this.connectionFactory = new MysqlConnectionFactory(host, asPort(port), user, password, schema);
        }
        this.loggingName = loggingName;
    }
    
    protected DbConnector(final ConnectionFactory connectionFactory, final String loggingName) {
        this.lastUsed = System.currentTimeMillis();
        this.connectionFactory = connectionFactory;
        this.loggingName = loggingName;
    }
    
    public static boolean isUseSqlite() {
        return DbConnector.sqlite;
    }
    
    protected void beforeStaleClose() throws SQLException {
    }
    
    private void refreshDbConnection() throws SQLException {
        if (this.connectionFactory.isStale(this.lastUsed, this.connection)) {
            DbConnector.logger.log(Level.INFO, "Recreating " + this.loggingName);
            if (this.connection != null) {
                try {
                    this.beforeStaleClose();
                    this.attemptClose();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    DbConnector.logger.log(Level.WARNING, "Unable to perform pre-close on stale " + this.loggingName, e);
                }
            }
        }
        if (!this.connectionFactory.isValid(this.connection)) {
            try {
                this.connection = this.connectionFactory.createConnection();
            }
            catch (Exception e2) {
                e2.printStackTrace();
                DbConnector.logger.log(Level.WARNING, "Problem opening the " + this.loggingName, e2);
            }
        }
        this.lastUsed = System.currentTimeMillis();
    }
    
    public static boolean hasPendingMigrations() {
        return DbConnector.MIGRATION_STRATEGY.hasPendingMigrations();
    }
    
    public static MigrationResult performMigrations() {
        return DbConnector.MIGRATION_STRATEGY.migrate();
    }
    
    public static Connection getLoginDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.LOGIN);
    }
    
    public static Connection getCreatureDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.CREATURES);
    }
    
    public static Connection getDeityDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.DEITIES);
    }
    
    public static Connection getEconomyDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.ECONOMY);
    }
    
    public static Connection getPlayerDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.PLAYERS);
    }
    
    public static Connection getItemDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.ITEMS);
    }
    
    public static Connection getTemplateDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.TEMPLATES);
    }
    
    public static Connection getZonesDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.ZONES);
    }
    
    public static Connection getLogsDbCon() throws SQLException {
        return refreshConnectionForSchema(WurmDatabaseSchema.LOGS);
    }
    
    private void attemptClose() {
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
                DbConnector.logger.log(Level.WARNING, "Problem closing the " + this.loggingName, ex);
            }
            this.connection = null;
        }
    }
    
    public static void closeAll() {
        DbConnector.logger.info("Starting to close all Database Connections.");
        DbConnector.CONNECTORS.values().forEach(DbConnector::attemptClose);
        DbConnector.logger.info("Finished closing all Database Connections.");
    }
    
    public static void returnConnection(@Nullable final Connection aConnection) {
        if (DbConnector.logger.isLoggable(Level.FINEST)) {
            DbConnector.logger.finest("Returning Connection: " + aConnection);
        }
        if (!DbConnector.isTrackingOpenDatabaseResources && aConnection != null) {
            MysqlConnectionFactory.logActiveStatementCount(aConnection);
        }
    }
    
    private static Connection refreshConnectionForSchema(final WurmDatabaseSchema schema) throws SQLException {
        if (!isInitialized()) {
            initialize();
        }
        final DbConnector connector = DbConnector.CONNECTORS.get(schema);
        connector.refreshDbConnection();
        return connector.connection;
    }
    
    public static Connection getConnectionForSchema(@Nonnull final WurmDatabaseSchema aSchema) throws SQLException {
        if (DbConnector.logger.isLoggable(Level.FINER)) {
            DbConnector.logger.finer("Getting database connection for schema: " + aSchema);
        }
        if (!isInitialized()) {
            initialize();
        }
        final DbConnector connector = DbConnector.CONNECTORS.get(aSchema);
        if (connector != null) {
            if (connector.connection == null) {
                DbConnector.logger.warning("Null connection found for connector " + connector.loggingName);
                connector.refreshDbConnection();
            }
            return connector.connection;
        }
        assert false : aSchema;
        DbConnector.logger.warning("Returning null for an unexpected WurmDatabaseSchema: " + aSchema);
        return null;
    }
    
    public static void setUseSqlite(final boolean sqlite) {
        DbConnector.sqlite = sqlite;
    }
    
    private static Integer asPort(@Nullable final String portProperty) {
        if (portProperty != null && !portProperty.isEmpty()) {
            final Matcher m = DbConnector.portPattern.matcher(portProperty);
            if (m.matches()) {
                try {
                    return Integer.parseInt(m.group(1));
                }
                catch (NumberFormatException e) {
                    DbConnector.logger.warning("Unexpected error, could not converted matched port number into integer: " + portProperty);
                    return null;
                }
            }
            DbConnector.logger.warning("Database port property does not match expected pattern: " + portProperty);
        }
        return null;
    }
    
    private static boolean isInitialized() {
        return DbConnector.isInitialized;
    }
    
    private static void setInitialized(final boolean isInitialized) {
        DbConnector.isInitialized = isInitialized;
    }
    
    static {
        logger = Logger.getLogger(DbConnector.class.getName());
        DbConnector.sqlite = true;
        DbConnector.isInitialized = false;
        DbConnector.CONNECTORS = new EnumMap<WurmDatabaseSchema, DbConnector>(WurmDatabaseSchema.class);
        DbConnector.isTrackingOpenDatabaseResources = false;
        MIGRATIONS_DIR = Folders.getDist().getPathFor(DistEntity.Migrations);
        portPattern = Pattern.compile(":?([0-9]+)");
        config = new SQLiteConfig();
    }
    
    private abstract static class ConfigHelper<B extends ConnectionFactory>
    {
        public abstract B factoryForSchema(final WurmDatabaseSchema p0);
        
        protected boolean loadIsTrackingOpenDatabaseResources() {
            if (Constants.trackOpenDatabaseResources) {
                DbConnector.logger.warning("Cannot set tracking of open database resources as this is not supported for this driver");
            }
            return false;
        }
        
        EnumMap<WurmDatabaseSchema, DbConnector> buildConnectors() {
            DbConnector.isTrackingOpenDatabaseResources = this.loadIsTrackingOpenDatabaseResources();
            if (Constants.usePooledDb) {
                DbConnector.logger.warning("Database connection pooling is set to true, but is not currently supported");
            }
            final EnumMap<WurmDatabaseSchema, B> factories = new EnumMap<WurmDatabaseSchema, B>(WurmDatabaseSchema.class);
            for (final WurmDatabaseSchema schema : WurmDatabaseSchema.values()) {
                factories.put(schema, this.factoryForSchema(schema));
            }
            final EnumMap<WurmDatabaseSchema, DbConnector> newConnectors = new EnumMap<WurmDatabaseSchema, DbConnector>(WurmDatabaseSchema.class);
            newConnectors.put(WurmDatabaseSchema.PLAYERS, new DbConnector((B)factories.get(WurmDatabaseSchema.PLAYERS), "playerDbcon") {
                @Override
                protected void beforeStaleClose() throws SQLException {
                    CreaturePos.clearBatches();
                }
            });
            newConnectors.put(WurmDatabaseSchema.CREATURES, new DbConnector((B)factories.get(WurmDatabaseSchema.CREATURES), "creatureDbcon") {
                @Override
                protected void beforeStaleClose() throws SQLException {
                    CreaturePos.clearBatches();
                }
            });
            newConnectors.put(WurmDatabaseSchema.ITEMS, new DbConnector((B)factories.get(WurmDatabaseSchema.ITEMS), "itemdbcon") {
                @Override
                protected void beforeStaleClose() throws SQLException {
                    DbItem.clearBatches();
                }
            });
            newConnectors.put(WurmDatabaseSchema.TEMPLATES, new DbConnector(factories.get(WurmDatabaseSchema.TEMPLATES), "templateDbcon"));
            newConnectors.put(WurmDatabaseSchema.ZONES, new DbConnector(factories.get(WurmDatabaseSchema.ZONES), "zonesDbcon"));
            newConnectors.put(WurmDatabaseSchema.ECONOMY, new DbConnector(factories.get(WurmDatabaseSchema.ECONOMY), "economyDbcon"));
            newConnectors.put(WurmDatabaseSchema.DEITIES, new DbConnector(factories.get(WurmDatabaseSchema.DEITIES), "deityDbcon"));
            newConnectors.put(WurmDatabaseSchema.LOGIN, new DbConnector(factories.get(WurmDatabaseSchema.LOGIN), "loginDbcon"));
            newConnectors.put(WurmDatabaseSchema.LOGS, new DbConnector(factories.get(WurmDatabaseSchema.LOGS), "logsDbcon"));
            return newConnectors;
        }
        
        abstract MigrationStrategy newMigrationStrategy();
    }
    
    private static class SqliteConfigHelper extends ConfigHelper<SqliteConnectionFactory>
    {
        SqliteConfigHelper() {
            DbConnector.config.setJournalMode(SQLiteConfig.JournalMode.WAL);
            DbConnector.config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        }
        
        @Override
        public SqliteConnectionFactory factoryForSchema(final WurmDatabaseSchema schema) {
            return new SqliteConnectionFactory(Constants.dbHost, schema, DbConnector.config);
        }
        
        @Override
        MigrationStrategy newMigrationStrategy() {
            final List<SqliteConnectionFactory> sqliteConnectionFactories = new ArrayList<SqliteConnectionFactory>();
            for (final DbConnector connector : DbConnector.CONNECTORS.values()) {
                final SqliteConnectionFactory factory = (SqliteConnectionFactory)connector.connectionFactory;
                sqliteConnectionFactories.add(factory);
            }
            return new SqliteMigrationStrategy(sqliteConnectionFactories, DbConnector.MIGRATIONS_DIR);
        }
    }
    
    private static class MysqlConfigHelper extends ConfigHelper<MysqlConnectionFactory>
    {
        @Override
        protected boolean loadIsTrackingOpenDatabaseResources() {
            return Constants.trackOpenDatabaseResources;
        }
        
        @Override
        public MysqlConnectionFactory factoryForSchema(final WurmDatabaseSchema schema) {
            return new MysqlConnectionFactory(Constants.dbHost, asPort(Constants.dbPort), Constants.dbUser, Constants.dbPass, schema);
        }
        
        public MigrationStrategy newMigrationStrategy() {
            return new MysqlMigrationStrategy((MysqlConnectionFactory)DbConnector.CONNECTORS.get(MysqlMigrationStrategy.MIGRATION_SCHEMA).connectionFactory);
        }
    }
}

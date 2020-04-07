// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database;

import java.util.Hashtable;
import com.wurmonline.shared.exceptions.WurmException;
import com.mysql.jdbc.ConnectionImpl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Driver;
import java.util.logging.Level;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Connection;
import com.wurmonline.server.Constants;
import java.util.logging.Logger;

public class MysqlConnectionFactory extends ConnectionFactory
{
    private static final Logger logger;
    private static final String DB_CACHE_PREP_STMTS = "&cachePrepStmts=true&useServerPrepStmts=true&prepStmtCacheSqlLimit=512&prepStmtCacheSize=100&useCompression=false&allowMultiQueries=true&elideSetAutoCommits=true&maintainTimeStats=false";
    private static final String DB_GATHER_STATS = "&gatherPerfMetrics=true&reportMetricsIntervalMillis=600000&profileSQL=false&useUsageAdvisor=false&useNanosForElapsedTime=false";
    private static final int DATABASE_IS_VALID_TIMEOUT = 0;
    private final String user;
    private final String password;
    
    public MysqlConnectionFactory(final String host, final int port, final String user, final String password, final WurmDatabaseSchema schema) {
        super(createConnectionUrl(host, port, schema), schema);
        this.user = user;
        this.password = password;
    }
    
    private static StringBuilder addOptionalJdbcConnectionProperties(final StringBuilder urlBuilder) {
        if (Constants.trackOpenDatabaseResources) {
            urlBuilder.append("dontTrackOpenResources=false");
        }
        else {
            urlBuilder.append("dontTrackOpenResources=true");
        }
        if (Constants.usePrepStmts) {
            urlBuilder.append("&cachePrepStmts=true&useServerPrepStmts=true&prepStmtCacheSqlLimit=512&prepStmtCacheSize=100&useCompression=false&allowMultiQueries=true&elideSetAutoCommits=true&maintainTimeStats=false");
        }
        if (Constants.gatherDbStats) {
            urlBuilder.append("&gatherPerfMetrics=true&reportMetricsIntervalMillis=600000&profileSQL=false&useUsageAdvisor=false&useNanosForElapsedTime=false");
        }
        return urlBuilder;
    }
    
    public static String createConnectionUrl(final String host, final int port, final WurmDatabaseSchema schema) {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://");
        urlBuilder.append(host);
        urlBuilder.append(":");
        urlBuilder.append(port);
        urlBuilder.append("/");
        urlBuilder.append(schema.getDatabase());
        urlBuilder.append("?");
        return addOptionalJdbcConnectionProperties(urlBuilder).toString();
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        final Driver driver = DriverManager.getDriver(this.getUrl());
        final Properties connectionInfo = new Properties();
        ((Hashtable<String, String>)connectionInfo).put("user", this.user);
        ((Hashtable<String, String>)connectionInfo).put("password", this.password);
        final Connection con = driver.connect(this.getUrl(), connectionInfo);
        if (MysqlConnectionFactory.logger.isLoggable(Level.FINE)) {
            MysqlConnectionFactory.logger.fine("JDBC Driver Class: " + driver.getClass() + ", version: " + driver.getMajorVersion() + '.' + driver.getMinorVersion());
        }
        return con;
    }
    
    @Override
    public boolean isValid(@Nullable final Connection con) throws SQLException {
        return con != null && con.isValid(0);
    }
    
    @Override
    public boolean isStale(final long lastUsed, @Nullable final Connection connection) throws SQLException {
        return System.currentTimeMillis() - lastUsed > 3600000L || (connection != null && !connection.isValid(0));
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public static void logActiveStatementCount(@Nonnull final Connection aConnection) {
        if (aConnection instanceof ConnectionImpl) {
            final int lActiveStatementCount = ((ConnectionImpl)aConnection).getActiveStatementCount();
            if (lActiveStatementCount > 0) {
                MysqlConnectionFactory.logger.log(Level.WARNING, "Returned connection: " + aConnection.getClass() + ", active statement count: " + lActiveStatementCount, new WurmException("SQL Statements still open when returning connection"));
            }
        }
    }
    
    static {
        logger = Logger.getLogger(MysqlConnectionFactory.class.getName());
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.jdbc;

import java.util.Hashtable;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.sql.Statement;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.FeatureDetector;
import org.flywaydb.core.internal.util.ClassUtils;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.StringUtils;
import java.sql.Connection;
import java.util.Properties;
import java.sql.Driver;
import javax.sql.DataSource;

public class DriverDataSource implements DataSource
{
    private static final String MARIADB_JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    private static final String MYSQL_JDBC_URL_PREFIX = "jdbc:mysql:";
    private static final String ORACLE_JDBC_URL_PREFIX = "jdbc:oracle:";
    private static final String MYSQL_5_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private Driver driver;
    private final String url;
    private final String user;
    private final String password;
    private final String[] initSqls;
    private final Properties defaultProps;
    private final ClassLoader classLoader;
    private boolean singleConnectionMode;
    private Connection originalSingleConnection;
    private Connection uncloseableSingleConnection;
    
    public DriverDataSource(final ClassLoader classLoader, String driverClass, final String url, final String user, final String password, String... initSqls) throws FlywayException {
        this.classLoader = classLoader;
        this.url = this.detectFallbackUrl(url);
        if (!StringUtils.hasLength(driverClass)) {
            driverClass = this.detectDriverForUrl(url);
            if (!StringUtils.hasLength(driverClass)) {
                throw new FlywayException("Unable to autodetect JDBC driver for url: " + url);
            }
        }
        this.defaultProps = this.detectPropsForUrl(url);
        try {
            this.driver = ClassUtils.instantiate(driverClass, classLoader);
        }
        catch (Exception e) {
            final String backupDriverClass = this.detectBackupDriverForUrl(url);
            if (backupDriverClass == null) {
                throw new FlywayException("Unable to instantiate JDBC driver: " + driverClass, e);
            }
            try {
                this.driver = ClassUtils.instantiate(backupDriverClass, classLoader);
            }
            catch (Exception e2) {
                throw new FlywayException("Unable to instantiate JDBC driver: " + driverClass, e);
            }
        }
        this.user = this.detectFallbackUser(user);
        this.password = this.detectFallbackPassword(password);
        if (initSqls == null) {
            initSqls = new String[0];
        }
        this.initSqls = initSqls;
    }
    
    private String detectFallbackUrl(final String url) {
        if (!StringUtils.hasText(url)) {
            final String boxfuseDatabaseUrl = System.getenv("BOXFUSE_DATABASE_URL");
            if (StringUtils.hasText(boxfuseDatabaseUrl)) {
                return boxfuseDatabaseUrl;
            }
            throw new FlywayException("Missing required JDBC URL. Unable to create DataSource!");
        }
        else {
            if (!url.toLowerCase().startsWith("jdbc:")) {
                throw new FlywayException("Invalid JDBC URL (should start with jdbc:) : " + url);
            }
            return url;
        }
    }
    
    private String detectFallbackUser(final String user) {
        if (!StringUtils.hasText(user)) {
            final String boxfuseDatabaseUser = System.getenv("BOXFUSE_DATABASE_USER");
            if (StringUtils.hasText(boxfuseDatabaseUser)) {
                return boxfuseDatabaseUser;
            }
        }
        return user;
    }
    
    private String detectFallbackPassword(final String password) {
        if (!StringUtils.hasText(password)) {
            final String boxfuseDatabasePassword = System.getenv("BOXFUSE_DATABASE_PASSWORD");
            if (StringUtils.hasText(boxfuseDatabasePassword)) {
                return boxfuseDatabasePassword;
            }
        }
        return password;
    }
    
    private Properties detectPropsForUrl(final String url) {
        final Properties result = new Properties();
        if (url.startsWith("jdbc:oracle:")) {
            final String osUser = System.getProperty("user.name");
            ((Hashtable<String, String>)result).put("v$session.osuser", osUser.substring(0, Math.min(osUser.length(), 30)));
            ((Hashtable<String, String>)result).put("v$session.program", "Flyway by Boxfuse");
        }
        return result;
    }
    
    private String detectBackupDriverForUrl(final String url) {
        if (url.startsWith("jdbc:mysql:")) {
            if (ClassUtils.isPresent("com.mysql.jdbc.Driver", this.classLoader)) {
                return "com.mysql.jdbc.Driver";
            }
            return "org.mariadb.jdbc.Driver";
        }
        else {
            if (url.startsWith("jdbc:redshift:")) {
                return "com.amazon.redshift.jdbc4.Driver";
            }
            return null;
        }
    }
    
    private String detectDriverForUrl(final String url) {
        if (url.startsWith("jdbc:db2:")) {
            return "com.ibm.db2.jcc.DB2Driver";
        }
        if (url.startsWith("jdbc:derby://")) {
            return "org.apache.derby.jdbc.ClientDriver";
        }
        if (url.startsWith("jdbc:derby:")) {
            return "org.apache.derby.jdbc.EmbeddedDriver";
        }
        if (url.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        if (url.startsWith("jdbc:hsqldb:")) {
            return "org.hsqldb.jdbcDriver";
        }
        if (url.startsWith("jdbc:sqlite:")) {
            this.singleConnectionMode = true;
            if (new FeatureDetector(this.classLoader).isAndroidAvailable()) {
                return "org.sqldroid.SQLDroidDriver";
            }
            return "org.sqlite.JDBC";
        }
        else {
            if (url.startsWith("jdbc:sqldroid:")) {
                return "org.sqldroid.SQLDroidDriver";
            }
            if (url.startsWith("jdbc:mysql:")) {
                return "com.mysql.cj.jdbc.Driver";
            }
            if (url.startsWith("jdbc:mariadb:")) {
                return "org.mariadb.jdbc.Driver";
            }
            if (url.startsWith("jdbc:google:")) {
                return "com.mysql.jdbc.GoogleDriver";
            }
            if (url.startsWith("jdbc:oracle:")) {
                return "oracle.jdbc.OracleDriver";
            }
            if (url.startsWith("jdbc:phoenix")) {
                return "org.apache.phoenix.jdbc.PhoenixDriver";
            }
            if (url.startsWith("jdbc:postgresql:")) {
                return "org.postgresql.Driver";
            }
            if (url.startsWith("jdbc:redshift:")) {
                return "com.amazon.redshift.jdbc41.Driver";
            }
            if (url.startsWith("jdbc:jtds:")) {
                return "net.sourceforge.jtds.jdbc.Driver";
            }
            if (url.startsWith("jdbc:sqlserver:")) {
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            }
            if (url.startsWith("jdbc:vertica:")) {
                return "com.vertica.jdbc.Driver";
            }
            if (url.startsWith("jdbc:sap:")) {
                return "com.sap.db.jdbc.Driver";
            }
            return null;
        }
    }
    
    public Driver getDriver() {
        return this.driver;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String[] getInitSqls() {
        return this.initSqls;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriver(this.getUser(), this.getPassword());
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return this.getConnectionFromDriver(username, password);
    }
    
    protected Connection getConnectionFromDriver(final String username, final String password) throws SQLException {
        if (this.singleConnectionMode && this.uncloseableSingleConnection != null) {
            return this.uncloseableSingleConnection;
        }
        final Properties props = new Properties(this.defaultProps);
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        Connection connection;
        try {
            connection = this.driver.connect(this.url, props);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to obtain Jdbc connection from DataSource (" + this.url + ") for user '" + this.user + "': " + e.getMessage(), e);
        }
        for (final String initSql : this.initSqls) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(initSql);
            }
            finally {
                JdbcUtils.closeStatement(statement);
            }
        }
        if (this.singleConnectionMode) {
            this.originalSingleConnection = connection;
            final InvocationHandler suppressCloseHandler = new SuppressCloseHandler(this.originalSingleConnection);
            return this.uncloseableSingleConnection = (Connection)Proxy.newProxyInstance(this.classLoader, new Class[] { Connection.class }, suppressCloseHandler);
        }
        return connection;
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public void setLoginTimeout(final int timeout) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }
    
    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }
    
    @Override
    public void setLogWriter(final PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }
    
    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException("getParentLogger");
    }
    
    public void close() {
        this.uncloseableSingleConnection = null;
        JdbcUtils.closeConnection(this.originalSingleConnection);
        this.originalSingleConnection = null;
    }
    
    private static class SuppressCloseHandler implements InvocationHandler
    {
        private final Connection connection;
        
        public SuppressCloseHandler(final Connection connection) {
            this.connection = connection;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (!"close".equals(method.getName())) {
                return method.invoke(this.connection, args);
            }
            return null;
        }
    }
}

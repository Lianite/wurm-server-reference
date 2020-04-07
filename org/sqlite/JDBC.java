// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.sql.Driver;

public class JDBC implements Driver
{
    public static final String PREFIX = "jdbc:sqlite:";
    
    @Override
    public int getMajorVersion() {
        return SQLiteJDBCLoader.getMajorVersion();
    }
    
    @Override
    public int getMinorVersion() {
        return SQLiteJDBCLoader.getMinorVersion();
    }
    
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
    
    @Override
    public boolean acceptsURL(final String url) {
        return isValidURL(url);
    }
    
    public static boolean isValidURL(final String url) {
        return url != null && url.toLowerCase().startsWith("jdbc:sqlite:");
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        return SQLiteConfig.getDriverPropertyInfo();
    }
    
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        return createConnection(url, info);
    }
    
    static String extractAddress(final String url) {
        return "jdbc:sqlite:".equalsIgnoreCase(url) ? ":memory:" : url.substring("jdbc:sqlite:".length());
    }
    
    public static Connection createConnection(String url, final Properties prop) throws SQLException {
        if (!isValidURL(url)) {
            return null;
        }
        url = url.trim();
        return new SQLiteConnection(url, extractAddress(url), prop);
    }
    
    static {
        try {
            DriverManager.registerDriver(new JDBC());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

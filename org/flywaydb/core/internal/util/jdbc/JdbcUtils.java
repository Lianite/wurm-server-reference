// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.jdbc;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.flywaydb.core.api.FlywayException;
import java.sql.Connection;
import javax.sql.DataSource;
import org.flywaydb.core.internal.util.logging.Log;

public class JdbcUtils
{
    private static final Log LOG;
    
    public static Connection openConnection(final DataSource dataSource) throws FlywayException {
        try {
            final Connection connection = dataSource.getConnection();
            if (connection == null) {
                throw new FlywayException("Unable to obtain Jdbc connection from DataSource");
            }
            return connection;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to obtain Jdbc connection from DataSource", e);
        }
    }
    
    public static void closeConnection(final Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        }
        catch (SQLException e) {
            JdbcUtils.LOG.error("Error while closing Jdbc connection", e);
        }
    }
    
    public static void closeStatement(final Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        }
        catch (SQLException e) {
            JdbcUtils.LOG.error("Error while closing Jdbc statement", e);
        }
    }
    
    public static void closeResultSet(final ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        }
        catch (SQLException e) {
            JdbcUtils.LOG.error("Error while closing Jdbc resultSet", e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(JdbcUtils.class);
    }
}

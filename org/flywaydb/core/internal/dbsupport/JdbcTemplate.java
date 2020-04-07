// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.jdbc.RowMapper;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.sql.Connection;
import org.flywaydb.core.internal.util.logging.Log;

public class JdbcTemplate
{
    private static final Log LOG;
    private final Connection connection;
    private final int nullType;
    
    public JdbcTemplate(final Connection connection, final int nullType) {
        this.connection = connection;
        this.nullType = nullType;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public List<Map<String, String>> queryForList(final String query, final String... params) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Map<String, String>> result;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = new ArrayList<Map<String, String>>();
            while (resultSet.next()) {
                final Map<String, String> rowMap = new HashMap<String, String>();
                for (int j = 1; j <= resultSet.getMetaData().getColumnCount(); ++j) {
                    rowMap.put(resultSet.getMetaData().getColumnLabel(j), resultSet.getString(j));
                }
                result.add(rowMap);
            }
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    public List<String> queryForStringList(final String query, final String... params) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<String> result;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = new ArrayList<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    public int queryForInt(final String query, final String... params) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int result;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            resultSet.next();
            result = resultSet.getInt(1);
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    public String queryForString(final String query, final String... params) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String result;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = null;
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return result;
    }
    
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }
    
    public void execute(final String sql, final Object... params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = this.prepareStatement(sql, params);
            statement.execute();
        }
        finally {
            JdbcUtils.closeStatement(statement);
        }
    }
    
    public void executeStatement(final String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = this.connection.createStatement();
            statement.setEscapeProcessing(false);
            boolean hasResults = false;
            try {
                hasResults = statement.execute(sql);
            }
            finally {
                for (SQLWarning warning = statement.getWarnings(); warning != null; warning = warning.getNextWarning()) {
                    if ("00000".equals(warning.getSQLState())) {
                        JdbcTemplate.LOG.info("DB: " + warning.getMessage());
                    }
                    else {
                        JdbcTemplate.LOG.warn("DB: " + warning.getMessage() + " (SQL State: " + warning.getSQLState() + " - Error Code: " + warning.getErrorCode() + ")");
                    }
                }
                int updateCount = -1;
                while (hasResults || (updateCount = statement.getUpdateCount()) != -1) {
                    if (updateCount != -1) {
                        JdbcTemplate.LOG.debug("Update Count: " + updateCount);
                    }
                    hasResults = statement.getMoreResults();
                }
            }
        }
        finally {
            JdbcUtils.closeStatement(statement);
        }
    }
    
    public void update(final String sql, final Object... params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = this.prepareStatement(sql, params);
            statement.executeUpdate();
        }
        finally {
            JdbcUtils.closeStatement(statement);
        }
    }
    
    private PreparedStatement prepareStatement(final String sql, final Object[] params) throws SQLException {
        final PreparedStatement statement = this.connection.prepareStatement(sql);
        for (int i = 0; i < params.length; ++i) {
            if (params[i] == null) {
                statement.setNull(i + 1, this.nullType);
            }
            else if (params[i] instanceof Integer) {
                statement.setInt(i + 1, (int)params[i]);
            }
            else if (params[i] instanceof Boolean) {
                statement.setBoolean(i + 1, (boolean)params[i]);
            }
            else {
                statement.setString(i + 1, (String)params[i]);
            }
        }
        return statement;
    }
    
    public <T> List<T> query(final String query, final RowMapper<T> rowMapper) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        List<T> results;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery(query);
            results = new ArrayList<T>();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
        }
        finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
        }
        return results;
    }
    
    static {
        LOG = LogFactory.getLog(JdbcTemplate.class);
    }
}

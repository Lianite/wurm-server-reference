// 
// Decompiled by Procyon v0.5.30
// 

package org.sqlite.core;

import java.util.regex.Matcher;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.sql.PreparedStatement;
import org.sqlite.SQLiteConnection;

public abstract class CoreDatabaseMetaData
{
    protected SQLiteConnection conn;
    protected PreparedStatement getTables;
    protected PreparedStatement getTableTypes;
    protected PreparedStatement getTypeInfo;
    protected PreparedStatement getCatalogs;
    protected PreparedStatement getSchemas;
    protected PreparedStatement getUDTs;
    protected PreparedStatement getColumnsTblName;
    protected PreparedStatement getSuperTypes;
    protected PreparedStatement getSuperTables;
    protected PreparedStatement getTablePrivileges;
    protected PreparedStatement getIndexInfo;
    protected PreparedStatement getProcedures;
    protected PreparedStatement getProcedureColumns;
    protected PreparedStatement getAttributes;
    protected PreparedStatement getBestRowIdentifier;
    protected PreparedStatement getVersionColumns;
    protected PreparedStatement getColumnPrivileges;
    protected PreparedStatement getGeneratedKeys;
    public int refCount;
    protected static final Pattern PK_UNNAMED_PATTERN;
    protected static final Pattern PK_NAMED_PATTERN;
    
    protected CoreDatabaseMetaData(final SQLiteConnection conn) {
        this.getTables = null;
        this.getTableTypes = null;
        this.getTypeInfo = null;
        this.getCatalogs = null;
        this.getSchemas = null;
        this.getUDTs = null;
        this.getColumnsTblName = null;
        this.getSuperTypes = null;
        this.getSuperTables = null;
        this.getTablePrivileges = null;
        this.getIndexInfo = null;
        this.getProcedures = null;
        this.getProcedureColumns = null;
        this.getAttributes = null;
        this.getBestRowIdentifier = null;
        this.getVersionColumns = null;
        this.getColumnPrivileges = null;
        this.getGeneratedKeys = null;
        this.refCount = 1;
        this.conn = conn;
    }
    
    public abstract ResultSet getGeneratedKeys() throws SQLException;
    
    protected void checkOpen() throws SQLException {
        if (this.conn == null) {
            throw new SQLException("connection closed");
        }
    }
    
    public synchronized void close() throws SQLException {
        if (this.conn == null || this.refCount > 0) {
            return;
        }
        try {
            if (this.getTables != null) {
                this.getTables.close();
            }
            if (this.getTableTypes != null) {
                this.getTableTypes.close();
            }
            if (this.getTypeInfo != null) {
                this.getTypeInfo.close();
            }
            if (this.getCatalogs != null) {
                this.getCatalogs.close();
            }
            if (this.getSchemas != null) {
                this.getSchemas.close();
            }
            if (this.getUDTs != null) {
                this.getUDTs.close();
            }
            if (this.getColumnsTblName != null) {
                this.getColumnsTblName.close();
            }
            if (this.getSuperTypes != null) {
                this.getSuperTypes.close();
            }
            if (this.getSuperTables != null) {
                this.getSuperTables.close();
            }
            if (this.getTablePrivileges != null) {
                this.getTablePrivileges.close();
            }
            if (this.getIndexInfo != null) {
                this.getIndexInfo.close();
            }
            if (this.getProcedures != null) {
                this.getProcedures.close();
            }
            if (this.getProcedureColumns != null) {
                this.getProcedureColumns.close();
            }
            if (this.getAttributes != null) {
                this.getAttributes.close();
            }
            if (this.getBestRowIdentifier != null) {
                this.getBestRowIdentifier.close();
            }
            if (this.getVersionColumns != null) {
                this.getVersionColumns.close();
            }
            if (this.getColumnPrivileges != null) {
                this.getColumnPrivileges.close();
            }
            if (this.getGeneratedKeys != null) {
                this.getGeneratedKeys.close();
            }
            this.getTables = null;
            this.getTableTypes = null;
            this.getTypeInfo = null;
            this.getCatalogs = null;
            this.getSchemas = null;
            this.getUDTs = null;
            this.getColumnsTblName = null;
            this.getSuperTypes = null;
            this.getSuperTables = null;
            this.getTablePrivileges = null;
            this.getIndexInfo = null;
            this.getProcedures = null;
            this.getProcedureColumns = null;
            this.getAttributes = null;
            this.getBestRowIdentifier = null;
            this.getVersionColumns = null;
            this.getColumnPrivileges = null;
            this.getGeneratedKeys = null;
        }
        finally {
            this.conn = null;
        }
    }
    
    protected static String quote(final String tableName) {
        if (tableName == null) {
            return "null";
        }
        return String.format("'%s'", tableName);
    }
    
    protected String escape(final String val) {
        final int len = val.length();
        final StringBuilder buf = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            if (val.charAt(i) == '\'') {
                buf.append('\'');
            }
            buf.append(val.charAt(i));
        }
        return buf.toString();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
    
    static {
        PK_UNNAMED_PATTERN = Pattern.compile(".*\\sPRIMARY\\s+KEY\\s+\\((.*?,+.*?)\\).*", 34);
        PK_NAMED_PATTERN = Pattern.compile(".*\\sCONSTRAINT\\s+(.*?)\\s+PRIMARY\\s+KEY\\s+\\((.*?)\\).*", 34);
    }
    
    public class PrimaryKeyFinder
    {
        String table;
        String pkName;
        String[] pkColumns;
        
        public PrimaryKeyFinder(final String table) throws SQLException {
            this.pkName = null;
            this.pkColumns = null;
            this.table = table;
            if (table == null || table.trim().length() == 0) {
                throw new SQLException("Invalid table name: '" + this.table + "'");
            }
            Statement stat = null;
            ResultSet rs = null;
            try {
                stat = CoreDatabaseMetaData.this.conn.createStatement();
                rs = stat.executeQuery("select sql from sqlite_master where lower(name) = lower('" + CoreDatabaseMetaData.this.escape(table) + "') and type = 'table'");
                if (!rs.next()) {
                    throw new SQLException("Table not found: '" + table + "'");
                }
                Matcher matcher = CoreDatabaseMetaData.PK_NAMED_PATTERN.matcher(rs.getString(1));
                if (matcher.find()) {
                    this.pkName = '\'' + CoreDatabaseMetaData.this.escape(matcher.group(1).toLowerCase()) + '\'';
                    this.pkColumns = matcher.group(2).split(",");
                }
                else {
                    matcher = CoreDatabaseMetaData.PK_UNNAMED_PATTERN.matcher(rs.getString(1));
                    if (matcher.find()) {
                        this.pkColumns = matcher.group(1).split(",");
                    }
                }
                if (this.pkColumns == null) {
                    rs = stat.executeQuery("pragma table_info('" + CoreDatabaseMetaData.this.escape(table) + "');");
                    while (rs.next()) {
                        if (rs.getBoolean(6)) {
                            this.pkColumns = new String[] { rs.getString(2) };
                        }
                    }
                }
                if (this.pkColumns != null) {
                    for (int i = 0; i < this.pkColumns.length; ++i) {
                        this.pkColumns[i] = this.pkColumns[i].toLowerCase().trim();
                    }
                }
            }
            finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Exception ex) {}
                try {
                    if (stat != null) {
                        stat.close();
                    }
                }
                catch (Exception ex2) {}
            }
        }
        
        public String getName() {
            return this.pkName;
        }
        
        public String[] getColumns() {
            return this.pkColumns;
        }
    }
}

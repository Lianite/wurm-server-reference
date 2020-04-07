// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.annotation.WillClose;
import javax.annotation.Nullable;
import java.sql.Statement;
import java.util.logging.Logger;

public final class DbUtilities
{
    private static Logger logger;
    
    public static void closeDatabaseObjects(@Nullable @WillClose Statement aStatementToClose, @Nullable @WillClose ResultSet aResultSetToClose) {
        if (aResultSetToClose != null) {
            try {
                aResultSetToClose.close();
            }
            catch (Exception ex) {}
            aResultSetToClose = null;
        }
        if (aStatementToClose != null) {
            try {
                aStatementToClose.close();
            }
            catch (Exception ex2) {}
            aStatementToClose = null;
        }
    }
    
    public static void performAdminOnAllTables(final Connection aConnection, final DbAdminAction aAction) {
        DbUtilities.logger.info("Performing " + aAction + " on all Wurm database tables");
        final long start = System.nanoTime();
        ResultSet rsCatalogs = null;
        ResultSet rsTables = null;
        ResultSet rsOperationStatus = null;
        Statement lStmt = null;
        final boolean problemsEncountered = false;
        try {
            final DatabaseMetaData dbmd = aConnection.getMetaData();
            rsCatalogs = dbmd.getCatalogs();
            while (rsCatalogs.next()) {
                final String lCatalogName = rsCatalogs.getString("TABLE_CAT");
                boolean proceed = true;
                if (aAction == DbAdminAction.CHECK_MEDIUM && lCatalogName.toUpperCase().startsWith("WURMLOGS")) {
                    proceed = Constants.checkWurmLogs;
                }
                if (lCatalogName.toUpperCase().startsWith("WURM") && proceed) {
                    DbUtilities.logger.info("Performing " + aAction + " on CatalogName: " + lCatalogName);
                    lStmt = aConnection.createStatement();
                    rsTables = dbmd.getTables(lCatalogName, null, null, null);
                    while (rsTables.next()) {
                        final String lTableName = rsTables.getString("TABLE_NAME");
                        String lAdminQuery = null;
                        switch (aAction) {
                            case ANALYZE: {
                                lAdminQuery = "ANALYZE LOCAL TABLE " + lCatalogName + '.' + lTableName;
                                break;
                            }
                            case CHECK_CHANGED: {
                                lAdminQuery = "CHECK TABLE " + lCatalogName + '.' + lTableName + " CHANGED";
                                break;
                            }
                            case CHECK_EXTENDED: {
                                lAdminQuery = "CHECK TABLE " + lCatalogName + '.' + lTableName + " EXTENDED";
                                break;
                            }
                            case CHECK_FAST: {
                                lAdminQuery = "CHECK TABLE " + lCatalogName + '.' + lTableName + " FAST";
                                break;
                            }
                            case CHECK_MEDIUM: {
                                lAdminQuery = "CHECK TABLE " + lCatalogName + '.' + lTableName + " MEDIUM";
                                break;
                            }
                            case CHECK_QUICK: {
                                lAdminQuery = "CHECK TABLE " + lCatalogName + '.' + lTableName + " QUICK";
                                break;
                            }
                            case OPTIMIZE: {
                                lAdminQuery = "OPTIMIZE LOCAL TABLE " + lCatalogName + '.' + lTableName;
                                break;
                            }
                        }
                        lStmt = aConnection.createStatement();
                        if (lStmt.execute(lAdminQuery)) {
                            rsOperationStatus = lStmt.getResultSet();
                            if (rsOperationStatus.next()) {
                                final String lMsgType = rsOperationStatus.getString("Msg_type");
                                final String lMsgText = rsOperationStatus.getString("Msg_text");
                                if ("OK".equals(lMsgText) && "status".equals(lMsgType)) {
                                    if (DbUtilities.logger.isLoggable(Level.FINE)) {
                                        DbUtilities.logger.fine("TableName: " + lAdminQuery + " - OK");
                                    }
                                }
                                else {
                                    DbUtilities.logger.warning("TableName: " + lAdminQuery + " - " + lMsgType + ": " + lMsgText);
                                    if (!"status".equals(lMsgType) || lMsgText == null || !lMsgText.contains("is not BASE TABLE")) {}
                                }
                            }
                            rsOperationStatus.close();
                        }
                        else if (DbUtilities.logger.isLoggable(Level.FINE)) {
                            DbUtilities.logger.fine("TableName: " + lAdminQuery);
                        }
                        lStmt.close();
                    }
                    rsTables.close();
                }
                else {
                    if (!DbUtilities.logger.isLoggable(Level.FINE)) {
                        continue;
                    }
                    DbUtilities.logger.fine("Not performing " + aAction + " on non-Wurm CatalogName: " + lCatalogName);
                }
            }
            rsCatalogs.close();
        }
        catch (SQLException e) {
            DbUtilities.logger.log(Level.WARNING, e.getMessage(), e);
        }
        finally {
            closeDatabaseObjects(lStmt, rsCatalogs);
            closeDatabaseObjects(null, rsTables);
            closeDatabaseObjects(null, rsOperationStatus);
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            DbUtilities.logger.info("Finished performing " + aAction + " on all database tables, which took " + lElapsedTime + " millis.");
            if (problemsEncountered) {
                DbUtilities.logger.severe("\n\n**** At least one problem was encountered while performing admin actions ***********\n\n");
            }
        }
    }
    
    public static Timestamp getTimestampOrNull(final String timestampString) {
        if (timestampString.contains(":")) {
            try {
                return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestampString).getTime());
            }
            catch (ParseException e) {
                DbUtilities.logger.warning("Unable to convert '" + timestampString + "' into a timestamp, expected format: yyyy-MM-dd HH:mm:ss");
                return null;
            }
        }
        try {
            return new Timestamp(Long.parseLong(timestampString));
        }
        catch (NumberFormatException e2) {
            DbUtilities.logger.warning("Unable to convert '" + timestampString + "' into a timestamp, value is not valid for type 'long'");
            return null;
        }
    }
    
    static {
        DbUtilities.logger = Logger.getLogger(DbUtilities.class.getName());
    }
    
    public enum DbAdminAction
    {
        ANALYZE, 
        CHECK_QUICK, 
        CHECK_FAST, 
        CHECK_CHANGED, 
        CHECK_MEDIUM, 
        CHECK_EXTENDED, 
        OPTIMIZE;
    }
}

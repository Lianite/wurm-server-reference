// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import com.wurmonline.server.Constants;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import com.wurmonline.server.database.WurmDatabaseSchema;
import java.util.logging.Logger;

public final class DbIndexManager
{
    private static final Logger logger;
    
    private static void createIndex(final WurmDatabaseSchema aSchema, final String aIndexCreationQuery) {
        if (aIndexCreationQuery != null && aIndexCreationQuery.startsWith("ALTER TABLE")) {
            final long start = System.nanoTime();
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Going to create an index in schema: " + aSchema + " using: " + aIndexCreationQuery);
            }
            Connection lDbConnection = null;
            Statement lCreateIndexStatement = null;
            try {
                lDbConnection = DbConnector.getConnectionForSchema(aSchema);
                lCreateIndexStatement = lDbConnection.createStatement();
                lCreateIndexStatement.execute(aIndexCreationQuery);
            }
            catch (SQLException sqx) {
                DbIndexManager.logger.log(Level.WARNING, "Problems creating an index in schema: " + aSchema + " using: " + aIndexCreationQuery + " due to " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(lCreateIndexStatement, null);
                DbConnector.returnConnection(lDbConnection);
                if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                    final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                    DbIndexManager.logger.fine("Creating an index in schema: " + aSchema + " using: " + aIndexCreationQuery + " took " + lElapsedTime + " millis.");
                }
            }
        }
        else {
            DbIndexManager.logger.warning("SQL query must start with ALTER TABLE. Schema: " + aSchema + ", SQL: " + aIndexCreationQuery);
        }
    }
    
    private static void removeIndex(final WurmDatabaseSchema aSchema, final String aIndexCreationQuery) {
        if (aIndexCreationQuery != null && aIndexCreationQuery.startsWith("ALTER TABLE")) {
            final long start = System.nanoTime();
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Going to drop an index in schema: " + aSchema + " using: " + aIndexCreationQuery);
            }
            Connection lDbConnection = null;
            Statement lCreateIndexStatement = null;
            try {
                lDbConnection = DbConnector.getConnectionForSchema(aSchema);
                lCreateIndexStatement = lDbConnection.createStatement();
                lCreateIndexStatement.execute(aIndexCreationQuery);
            }
            catch (SQLException sqx) {
                DbIndexManager.logger.log(Level.WARNING, "Problems dropping an index in schema: " + aSchema + " using: " + aIndexCreationQuery + " due to " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(lCreateIndexStatement, null);
                DbConnector.returnConnection(lDbConnection);
                if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                    final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                    DbIndexManager.logger.fine("Dropping an index in schema: " + aSchema + " using: " + aIndexCreationQuery + " took " + lElapsedTime + " millis.");
                }
            }
        }
        else {
            DbIndexManager.logger.warning("SQL query must start with ALTER TABLE. Schema: " + aSchema + ", SQL: " + aIndexCreationQuery);
        }
    }
    
    public static void createIndexes() {
        if (DbConnector.isUseSqlite()) {
            return;
        }
        DbIndexManager.logger.info("Starting to create database indices");
        final long start = System.nanoTime();
        if (Constants.checkAllDbTables) {
            DbIndexManager.logger.info("The database tables have already been checked so no need to repair them before creating indices.");
        }
        else {
            repairDatabaseTables();
        }
        createIndex(WurmDatabaseSchema.CREATURES, "ALTER TABLE SKILLS ADD INDEX OWNERID (OWNER)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE BODYPARTS ADD INDEX BODYZONEID (ZONEID)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE COINS ADD INDEX COINSZONEID (ZONEID)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE EFFECTS ADD INDEX OWNERID (OWNER)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE FROZENITEMS ADD INDEX FROZENITEMS_TEMPLATEID (TEMPLATEID)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE ITEMS ADD INDEX ITEMS_TEMPLATEID (TEMPLATEID)");
        createIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE ITEMS ADD INDEX ITEMZONEID (ZONEID)");
        createIndex(WurmDatabaseSchema.ZONES, "ALTER TABLE FENCES ADD INDEX FENCEZONEID (ZONEID)");
        createIndex(WurmDatabaseSchema.ZONES, "ALTER TABLE WALLS ADD INDEX WALLSSTRUCTUREID (STRUCTURE)");
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        DbIndexManager.logger.info("Created database indices took " + lElapsedTime + " millis.");
    }
    
    public static void removeIndexes() {
        if (DbConnector.isUseSqlite()) {
            return;
        }
        DbIndexManager.logger.info("Starting to remove database indices");
        final long start = System.nanoTime();
        removeIndex(WurmDatabaseSchema.CREATURES, "ALTER TABLE SKILLS DROP INDEX OWNERID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE BODYPARTS DROP INDEX BODYZONEID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE COINS DROP INDEX COINSZONEID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE EFFECTS DROP INDEX OWNERID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE FROZENITEMS DROP INDEX FROZENITEMS_TEMPLATEID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE ITEMS DROP INDEX ITEMS_TEMPLATEID");
        removeIndex(WurmDatabaseSchema.ITEMS, "ALTER TABLE ITEMS DROP INDEX ITEMZONEID");
        removeIndex(WurmDatabaseSchema.ZONES, "ALTER TABLE FENCES DROP INDEX FENCEZONEID");
        removeIndex(WurmDatabaseSchema.ZONES, "ALTER TABLE WALLS DROP INDEX WALLSSTRUCTUREID");
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        DbIndexManager.logger.info("Removed database indices took " + lElapsedTime + " millis.");
    }
    
    private static void repairDatabaseTables() {
        if (DbConnector.isUseSqlite()) {
            return;
        }
        Connection dbcon = null;
        Statement stmt = null;
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Items database table");
            }
            dbcon = DbConnector.getItemDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE ITEMS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Coins database table");
            }
            dbcon = DbConnector.getItemDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE COINS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Bodyparts database table");
            }
            dbcon = DbConnector.getItemDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE BODYPARTS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Walls database table");
            }
            dbcon = DbConnector.getZonesDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE WALLS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Fences database table");
            }
            dbcon = DbConnector.getZonesDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE FENCES");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Players database table");
            }
            dbcon = DbConnector.getPlayerDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE PLAYERS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Skills database table");
            }
            dbcon = DbConnector.getPlayerDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE SKILLS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Creatures database table");
            }
            dbcon = DbConnector.getCreatureDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE CREATURES");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            if (DbIndexManager.logger.isLoggable(Level.FINE)) {
                DbIndexManager.logger.fine("Checking and, if necessary, repairing Effects database table");
            }
            dbcon = DbConnector.getCreatureDbCon();
            stmt = dbcon.createStatement();
            stmt.execute("REPAIR TABLE EFFECTS");
        }
        catch (SQLException sqx) {
            DbIndexManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(DbIndexManager.class.getName());
    }
}

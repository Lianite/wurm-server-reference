// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.sql.Connection;
import java.util.logging.Logger;

public class ItemOwnerDatabaseUpdater extends DatabaseUpdater<ItemOwnerDatabaseUpdatable>
{
    private static final Logger logger;
    
    public ItemOwnerDatabaseUpdater(final String aUpdaterDescription, final int aMaxUpdatablesToRemovePerCycle) {
        super(aUpdaterDescription, ItemOwnerDatabaseUpdatable.class, aMaxUpdatablesToRemovePerCycle);
        ItemOwnerDatabaseUpdater.logger.info("Creating Item Owner Updater.");
    }
    
    @Override
    Connection getDatabaseConnection() throws SQLException {
        return DbConnector.getItemDbCon();
    }
    
    @Override
    void addUpdatableToBatch(final PreparedStatement updateStatement, final ItemOwnerDatabaseUpdatable aDbUpdatable) throws SQLException {
        if (ItemOwnerDatabaseUpdater.logger.isLoggable(Level.FINEST)) {
            ItemOwnerDatabaseUpdater.logger.finest("Adding to batch: " + aDbUpdatable);
        }
        updateStatement.setLong(1, aDbUpdatable.getOwner());
        updateStatement.setLong(2, aDbUpdatable.getId());
        updateStatement.addBatch();
    }
    
    static {
        logger = Logger.getLogger(ItemOwnerDatabaseUpdater.class.getName());
    }
}

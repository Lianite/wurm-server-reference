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

public class ItemParentDatabaseUpdater extends DatabaseUpdater<ItemParentDatabaseUpdatable>
{
    private static final Logger logger;
    
    public ItemParentDatabaseUpdater(final String aUpdaterDescription, final int aMaxUpdatablesToRemovePerCycle) {
        super(aUpdaterDescription, ItemParentDatabaseUpdatable.class, aMaxUpdatablesToRemovePerCycle);
        ItemParentDatabaseUpdater.logger.info("Creating Item Parent Updater.");
    }
    
    @Override
    Connection getDatabaseConnection() throws SQLException {
        return DbConnector.getItemDbCon();
    }
    
    @Override
    void addUpdatableToBatch(final PreparedStatement updateStatement, final ItemParentDatabaseUpdatable aDbUpdatable) throws SQLException {
        if (ItemParentDatabaseUpdater.logger.isLoggable(Level.FINEST)) {
            ItemParentDatabaseUpdater.logger.finest("Adding to batch: " + aDbUpdatable);
        }
        updateStatement.setLong(1, aDbUpdatable.getOwner());
        updateStatement.setLong(2, aDbUpdatable.getId());
        updateStatement.addBatch();
    }
    
    static {
        logger = Logger.getLogger(ItemParentDatabaseUpdater.class.getName());
    }
}

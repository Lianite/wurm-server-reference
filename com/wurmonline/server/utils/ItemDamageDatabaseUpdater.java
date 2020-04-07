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

public final class ItemDamageDatabaseUpdater extends DatabaseUpdater<ItemDamageDatabaseUpdatable>
{
    private static final Logger logger;
    
    public ItemDamageDatabaseUpdater(final String aUpdaterDescription, final int aMaxUpdatablesToRemovePerCycle) {
        super(aUpdaterDescription, ItemDamageDatabaseUpdatable.class, aMaxUpdatablesToRemovePerCycle);
        ItemDamageDatabaseUpdater.logger.info("Creating Item Damage Updater.");
    }
    
    @Override
    Connection getDatabaseConnection() throws SQLException {
        return DbConnector.getItemDbCon();
    }
    
    @Override
    void addUpdatableToBatch(final PreparedStatement updateStatement, final ItemDamageDatabaseUpdatable aDbUpdatable) throws SQLException {
        if (ItemDamageDatabaseUpdater.logger.isLoggable(Level.FINEST)) {
            ItemDamageDatabaseUpdater.logger.finest("Adding to batch: " + aDbUpdatable);
        }
        updateStatement.setFloat(1, aDbUpdatable.getDamage());
        updateStatement.setLong(2, aDbUpdatable.getLastMaintained());
        updateStatement.setLong(3, aDbUpdatable.getId());
        updateStatement.addBatch();
    }
    
    static {
        logger = Logger.getLogger(ItemDamageDatabaseUpdater.class.getName());
    }
}

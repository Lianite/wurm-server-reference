// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils.logging;

import java.sql.SQLException;
import java.sql.Date;
import java.sql.PreparedStatement;
import com.wurmonline.server.Constants;
import java.util.logging.Logger;

public final class ItemTransferDatabaseLogger extends DatabaseLogger<ItemTransfer>
{
    private static Logger logger;
    
    public ItemTransferDatabaseLogger(final String aLoggerDescription, final int aMaxLoggablesToRemovePerCycle) {
        super(aLoggerDescription, ItemTransfer.class, aMaxLoggablesToRemovePerCycle);
        ItemTransferDatabaseLogger.logger.info("Creating Item Transfer logger, System useItemTransferLog option: " + Constants.useItemTransferLog);
    }
    
    @Override
    void addLoggableToBatch(final PreparedStatement logsStatement, final ItemTransfer object) throws SQLException {
        logsStatement.setLong(1, object.getItemId());
        logsStatement.setString(2, object.getItemName());
        logsStatement.setLong(3, object.getOldOwnerId());
        logsStatement.setString(4, object.getOldOwnerName());
        logsStatement.setLong(5, object.getNewOwnerId());
        logsStatement.setString(6, object.getNewOwnerName());
        logsStatement.setDate(7, new Date(object.getTransferTime()));
        logsStatement.addBatch();
    }
    
    static {
        ItemTransferDatabaseLogger.logger = Logger.getLogger(ItemTransferDatabaseLogger.class.getName());
    }
}

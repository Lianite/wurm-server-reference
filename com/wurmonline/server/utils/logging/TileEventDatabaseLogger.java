// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils.logging;

import java.util.logging.Level;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import com.wurmonline.server.Constants;
import java.util.logging.Logger;

public class TileEventDatabaseLogger extends DatabaseLogger<TileEvent>
{
    private static final Logger logger;
    private int numsBatched;
    private static final int pruneInterval = 10000;
    
    public TileEventDatabaseLogger(final String aLoggerDescription, final int aMaxLoggablesToRemovePerCycle) {
        super(aLoggerDescription, TileEvent.class, aMaxLoggablesToRemovePerCycle);
        this.numsBatched = 0;
        TileEventDatabaseLogger.logger.info("Creating Tile Event logger, System useTileLog option: " + Constants.useTileEventLog);
    }
    
    public void addLoggableToBatch(final PreparedStatement logsStatement, final TileEvent object) throws SQLException {
        logsStatement.setInt(1, object.getTileX());
        logsStatement.setInt(2, object.getTileY());
        logsStatement.setInt(3, object.getLayer());
        logsStatement.setLong(4, object.getPerformer());
        logsStatement.setInt(5, object.getAction());
        logsStatement.setLong(6, object.getDate());
        logsStatement.addBatch();
        ++this.numsBatched;
        this.checkPruneLimit();
    }
    
    private void checkPruneLimit() {
        if (this.numsBatched > 10000) {
            TileEventDatabaseLogger.logger.log(Level.INFO, "Pruning entries");
            TileEvent.pruneLogEntries();
            this.numsBatched = 0;
        }
    }
    
    static {
        logger = Logger.getLogger(TileEventDatabaseLogger.class.getName());
    }
}

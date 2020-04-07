// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import com.wurmonline.server.creatures.Creature;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerPositionDatabaseUpdater extends DatabaseUpdater<PlayerPositionDbUpdatable>
{
    private static final Logger logger;
    private final Map<Long, PlayerPositionDbUpdatable> updatesMap;
    
    public PlayerPositionDatabaseUpdater(final String aUpdaterDescription, final int aMaxUpdatablesToRemovePerCycle) {
        super(aUpdaterDescription, PlayerPositionDbUpdatable.class, aMaxUpdatablesToRemovePerCycle);
        this.updatesMap = new ConcurrentHashMap<Long, PlayerPositionDbUpdatable>();
        PlayerPositionDatabaseUpdater.logger.info("Creating Player Position Updater.");
    }
    
    @Override
    Connection getDatabaseConnection() throws SQLException {
        return DbConnector.getPlayerDbCon();
    }
    
    @Override
    public void addToQueue(final PlayerPositionDbUpdatable updatable) {
        if (updatable != null) {
            final PlayerPositionDbUpdatable waiting = this.updatesMap.get(updatable.getId());
            if (waiting != null) {
                this.queue.remove(waiting);
            }
            this.updatesMap.put(updatable.getId(), updatable);
            this.queue.add((T)updatable);
        }
    }
    
    @Override
    void addUpdatableToBatch(final PreparedStatement updateStatement, final PlayerPositionDbUpdatable aDbUpdatable) throws SQLException {
        this.updatesMap.remove(aDbUpdatable.getId());
        updateStatement.setFloat(1, aDbUpdatable.getPositionX());
        updateStatement.setFloat(2, aDbUpdatable.getPositionY());
        updateStatement.setFloat(3, aDbUpdatable.getPositionZ());
        final float rot = Creature.normalizeAngle(aDbUpdatable.getRotation());
        updateStatement.setFloat(4, rot);
        updateStatement.setInt(5, aDbUpdatable.getZoneid());
        updateStatement.setInt(6, aDbUpdatable.getLayer());
        updateStatement.setLong(7, aDbUpdatable.getBridgeId());
        updateStatement.setLong(8, aDbUpdatable.getId());
        updateStatement.addBatch();
    }
    
    static {
        logger = Logger.getLogger(PlayerPositionDatabaseUpdater.class.getName());
    }
}

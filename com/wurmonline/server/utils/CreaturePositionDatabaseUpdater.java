// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import com.wurmonline.server.creatures.Creature;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CreaturePositionDatabaseUpdater extends DatabaseUpdater<CreaturePositionDbUpdatable>
{
    private static final Logger logger;
    private final Map<Long, CreaturePositionDbUpdatable> updatesMap;
    
    public CreaturePositionDatabaseUpdater(final String aUpdaterDescription, final int aMaxUpdatablesToRemovePerCycle) {
        super(aUpdaterDescription, CreaturePositionDbUpdatable.class, aMaxUpdatablesToRemovePerCycle);
        this.updatesMap = new ConcurrentHashMap<Long, CreaturePositionDbUpdatable>();
        CreaturePositionDatabaseUpdater.logger.info("Creating Creature Position Updater.");
    }
    
    @Override
    public void addToQueue(final CreaturePositionDbUpdatable updatable) {
        if (updatable != null) {
            if (CreaturePositionDatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                CreaturePositionDatabaseUpdater.logger.finest("Adding to database " + updatable + " updatable queue: " + updatable);
            }
            final CreaturePositionDbUpdatable waiting = this.updatesMap.get(updatable.getId());
            if (waiting != null) {
                this.queue.remove(waiting);
            }
            this.updatesMap.put(updatable.getId(), updatable);
            this.queue.add((T)updatable);
        }
    }
    
    @Override
    Connection getDatabaseConnection() throws SQLException {
        return DbConnector.getCreatureDbCon();
    }
    
    @Override
    void addUpdatableToBatch(final PreparedStatement updateStatement, final CreaturePositionDbUpdatable aDbUpdatable) throws SQLException {
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
        logger = Logger.getLogger(CreaturePositionDatabaseUpdater.class.getName());
    }
}

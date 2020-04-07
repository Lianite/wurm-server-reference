// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils.logging;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.logging.Logger;

public abstract class DatabaseLogger<T extends WurmLoggable> implements Runnable
{
    private static final Logger logger;
    private final Queue<T> queue;
    private final String iLoggerDescription;
    private final Class<T> iLoggableClass;
    private final int iMaxLoggablesToRemovePerCycle;
    
    public DatabaseLogger(final String aLoggerDescription, final Class<T> aLoggableClass, final int aMaxLoggablesToRemovePerCycle) {
        this.queue = new ConcurrentLinkedQueue<T>();
        this.iLoggerDescription = aLoggerDescription;
        this.iLoggableClass = aLoggableClass;
        this.iMaxLoggablesToRemovePerCycle = aMaxLoggablesToRemovePerCycle;
        DatabaseLogger.logger.info("Creating Database logger " + aLoggerDescription + " for WurmLoggable type: " + aLoggableClass.getName() + ", MaxLoggablesToRemovePerCycle: " + aMaxLoggablesToRemovePerCycle);
    }
    
    @Override
    public final void run() {
        Connection logsConnection = null;
        PreparedStatement logsStatement = null;
        try {
            if (DatabaseLogger.logger.isLoggable(Level.FINEST)) {
                DatabaseLogger.logger.finest("Starting DatabaseLogger.run() " + this.iLoggerDescription + " for WurmLoggable type: " + this.iLoggableClass.getName());
            }
            if (!this.queue.isEmpty()) {
                int objectsRemoved = 0;
                logsConnection = DbConnector.getLogsDbCon();
                while (!this.queue.isEmpty() && objectsRemoved <= this.iMaxLoggablesToRemovePerCycle) {
                    final T object = this.queue.remove();
                    ++objectsRemoved;
                    if (DatabaseLogger.logger.isLoggable(Level.FINEST)) {
                        DatabaseLogger.logger.finest("Removed from FIFO queue: " + object);
                    }
                    logsStatement = logsConnection.prepareStatement(object.getDatabaseInsertStatement());
                    this.addLoggableToBatch(logsStatement, object);
                }
                logsStatement.executeBatch();
                if (DatabaseLogger.logger.isLoggable(Level.FINER) || (!this.queue.isEmpty() && DatabaseLogger.logger.isLoggable(Level.FINE))) {
                    DatabaseLogger.logger.fine("Removed " + this.iLoggableClass.getName() + ' ' + objectsRemoved + " objects from FIFO queue, which now contains " + this.queue.size() + " objects");
                }
            }
        }
        catch (SQLException e) {
            DatabaseLogger.logger.log(Level.WARNING, "Problem getting WurmLogs connection due to " + e.getMessage(), e);
        }
        finally {
            if (DatabaseLogger.logger.isLoggable(Level.FINEST)) {
                DatabaseLogger.logger.finest("Ending DatabaseLogger.run() " + this.iLoggerDescription + " for WurmLoggable type: " + this.iLoggableClass.getName());
            }
            DbUtilities.closeDatabaseObjects(logsStatement, null);
            DbConnector.returnConnection(logsConnection);
        }
    }
    
    abstract void addLoggableToBatch(final PreparedStatement p0, final T p1) throws SQLException;
    
    public final void addToQueue(final T loggable) {
        if (loggable != null) {
            if (DatabaseLogger.logger.isLoggable(Level.FINEST)) {
                DatabaseLogger.logger.finest("Adding to database " + this.iLoggerDescription + " loggable queue: " + loggable);
            }
            this.queue.add(loggable);
        }
    }
    
    int getNumberOfLoggableObjectsInQueue() {
        return this.queue.size();
    }
    
    final String getLoggerDescription() {
        return this.iLoggerDescription;
    }
    
    final Class<T> getLoggableClass() {
        return this.iLoggableClass;
    }
    
    final int getMaxLoggablesToRemovePerCycle() {
        return this.iMaxLoggablesToRemovePerCycle;
    }
    
    static {
        logger = Logger.getLogger(DatabaseLogger.class.getName());
    }
}

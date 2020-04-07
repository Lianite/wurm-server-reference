// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.sql.PreparedStatement;
import java.sql.Connection;
import com.wurmonline.server.DbConnector;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import com.wurmonline.server.Constants;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.logging.Logger;

public abstract class DatabaseUpdater<T extends WurmDbUpdatable> implements Runnable
{
    private static final Logger logger;
    protected final Queue<T> queue;
    private final String iUpdaterDescription;
    private final Class<T> iUpdatableClass;
    private final int iMaxUpdatablesToRemovePerCycle;
    
    public DatabaseUpdater(final String aUpdaterDescription, final Class<T> aUpdatableClass, final int aMaxUpdatablesToRemovePerCycle) {
        this.queue = new ConcurrentLinkedQueue<T>();
        this.iUpdaterDescription = aUpdaterDescription;
        this.iUpdatableClass = aUpdatableClass;
        this.iMaxUpdatablesToRemovePerCycle = aMaxUpdatablesToRemovePerCycle;
        DatabaseUpdater.logger.info("Creating Database updater " + aUpdaterDescription + " for WurmDbUpdatable type: " + aUpdatableClass.getName() + ", MaxUpdatablesToRemovePerCycle: " + aMaxUpdatablesToRemovePerCycle);
    }
    
    @Override
    public final void run() {
        Connection updaterConnection = null;
        PreparedStatement updaterStatement = null;
        try {
            if (DatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                DatabaseUpdater.logger.finest("Starting DatabaseUpdater.run() " + this.iUpdaterDescription + " for WurmDbUpdatable type: " + this.iUpdatableClass.getName());
            }
            if (!this.queue.isEmpty()) {
                final long start = System.nanoTime();
                int objectsRemoved = 0;
                updaterConnection = this.getDatabaseConnection();
                updaterStatement = null;
                while (!this.queue.isEmpty() && objectsRemoved <= this.iMaxUpdatablesToRemovePerCycle) {
                    final T object = this.queue.remove();
                    ++objectsRemoved;
                    if (updaterStatement == null) {
                        updaterStatement = updaterConnection.prepareStatement(object.getDatabaseUpdateStatement());
                    }
                    this.addUpdatableToBatch(updaterStatement, object);
                }
                if (updaterStatement != null) {
                    updaterStatement.executeBatch();
                }
                final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                if (DatabaseUpdater.logger.isLoggable(Level.FINER) || (this.queue.size() > this.iMaxUpdatablesToRemovePerCycle && DatabaseUpdater.logger.isLoggable(Level.FINE)) || lElapsedTime > Constants.lagThreshold) {
                    DatabaseUpdater.logger.fine("Removed " + this.iUpdatableClass.getName() + ' ' + objectsRemoved + " objects from FIFO queue, which now contains " + this.queue.size() + " objects and took " + lElapsedTime + " millis.");
                }
            }
        }
        catch (SQLException e) {
            DatabaseUpdater.logger.log(Level.INFO, "Error in DatabaseUpdater.run() " + this.iUpdaterDescription + " for WurmDbUpdatable type: " + this.iUpdatableClass.getName());
            DatabaseUpdater.logger.log(Level.WARNING, "Problem getting WurmLogs connection due to " + e.getMessage(), e);
        }
        finally {
            if (DatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                DatabaseUpdater.logger.finest("Ending DatabaseUpdater.run() " + this.iUpdaterDescription + " for WurmDbUpdatable type: " + this.iUpdatableClass.getName());
            }
            DbUtilities.closeDatabaseObjects(updaterStatement, null);
            DbConnector.returnConnection(updaterConnection);
        }
    }
    
    public final void saveImmediately() {
        Connection updaterConnection = null;
        PreparedStatement updaterStatement = null;
        try {
            if (DatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                DatabaseUpdater.logger.finest("Starting DatabaseUpdater.run() " + this.iUpdaterDescription + " for WurmDbUpdatable type: " + this.iUpdatableClass.getName());
            }
            if (!this.queue.isEmpty()) {
                final long start = System.nanoTime();
                int objectsRemoved = 0;
                updaterConnection = this.getDatabaseConnection();
                updaterStatement = null;
                while (!this.queue.isEmpty()) {
                    final T object = this.queue.remove();
                    ++objectsRemoved;
                    if (updaterStatement == null) {
                        updaterStatement = updaterConnection.prepareStatement(object.getDatabaseUpdateStatement());
                    }
                    this.addUpdatableToBatch(updaterStatement, object);
                }
                if (updaterStatement != null) {
                    updaterStatement.executeBatch();
                }
                final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
                if (DatabaseUpdater.logger.isLoggable(Level.FINER) || (this.queue.size() > this.iMaxUpdatablesToRemovePerCycle && DatabaseUpdater.logger.isLoggable(Level.FINE)) || lElapsedTime > Constants.lagThreshold) {
                    DatabaseUpdater.logger.fine("Removed " + this.iUpdatableClass.getName() + ' ' + objectsRemoved + " objects from FIFO queue, which now contains " + this.queue.size() + " objects and took " + lElapsedTime + " millis.");
                }
            }
        }
        catch (SQLException e) {
            DatabaseUpdater.logger.log(Level.WARNING, "Problem getting WurmLogs connection due to " + e.getMessage(), e);
        }
        finally {
            if (DatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                DatabaseUpdater.logger.finest("Ending DatabaseUpdater.run() " + this.iUpdaterDescription + " for WurmDbUpdatable type: " + this.iUpdatableClass.getName());
            }
            DbUtilities.closeDatabaseObjects(updaterStatement, null);
            DbConnector.returnConnection(updaterConnection);
        }
    }
    
    abstract Connection getDatabaseConnection() throws SQLException;
    
    abstract void addUpdatableToBatch(final PreparedStatement p0, final T p1) throws SQLException;
    
    public void addToQueue(final T updatable) {
        if (updatable != null) {
            if (DatabaseUpdater.logger.isLoggable(Level.FINEST)) {
                DatabaseUpdater.logger.finest("Adding to database " + this.iUpdaterDescription + " updatable queue: " + updatable);
            }
            this.queue.add(updatable);
        }
    }
    
    final int getNumberOfUpdatableObjectsInQueue() {
        return this.queue.size();
    }
    
    final String getUpdaterDescription() {
        return this.iUpdaterDescription;
    }
    
    final Class<T> getUpdatableClass() {
        return this.iUpdatableClass;
    }
    
    final int getMaxUpdatablesToRemovePerCycle() {
        return this.iMaxUpdatablesToRemovePerCycle;
    }
    
    static {
        logger = Logger.getLogger(DatabaseUpdater.class.getName());
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.LinkedList;
import java.util.logging.Logger;

public final class HistoryManager
{
    private static final Logger logger;
    private static final String ADD_HISTORY = "INSERT INTO HISTORY(EVENTDATE,SERVER,PERFORMER,EVENT) VALUES (?,?,?,?)";
    private static final String GET_HISTORY = "SELECT EVENTDATE, SERVER, PERFORMER, EVENT FROM HISTORY WHERE SERVER=? ORDER BY EVENTDATE DESC";
    @GuardedBy("HISTORY_RW_LOCK")
    private static final LinkedList<HistoryEvent> HISTORY;
    private static final ReentrantReadWriteLock HISTORY_RW_LOCK;
    
    static HistoryEvent[] getHistoryEvents() {
        HistoryManager.HISTORY_RW_LOCK.readLock().lock();
        try {
            return HistoryManager.HISTORY.toArray(new HistoryEvent[HistoryManager.HISTORY.size()]);
        }
        finally {
            HistoryManager.HISTORY_RW_LOCK.readLock().unlock();
        }
    }
    
    public static String[] getHistory(final int numevents) {
        String[] hist = new String[0];
        HistoryManager.HISTORY_RW_LOCK.readLock().lock();
        int lHistorySize;
        try {
            lHistorySize = HistoryManager.HISTORY.size();
        }
        finally {
            HistoryManager.HISTORY_RW_LOCK.readLock().unlock();
        }
        if (lHistorySize > 0) {
            final int numbersToFetch = Math.min(numevents, lHistorySize);
            hist = new String[numbersToFetch];
            final HistoryEvent[] events = getHistoryEvents();
            for (int x = 0; x < numbersToFetch; ++x) {
                hist[x] = events[x].getLongDesc();
            }
        }
        return hist;
    }
    
    static void loadHistory() {
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        HistoryManager.HISTORY_RW_LOCK.writeLock().lock();
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT EVENTDATE, SERVER, PERFORMER, EVENT FROM HISTORY WHERE SERVER=? ORDER BY EVENTDATE DESC");
            ps.setInt(1, Servers.localServer.id);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoryManager.HISTORY.add(new HistoryEvent(rs.getLong("EVENTDATE"), rs.getString("PERFORMER"), rs.getString("EVENT"), rs.getInt("SERVER")));
            }
        }
        catch (SQLException sqx) {
            HistoryManager.logger.log(Level.WARNING, "Problem loading History for loacl server id: " + Servers.localServer.id + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            HistoryManager.HISTORY_RW_LOCK.writeLock().unlock();
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            HistoryManager.logger.info("Loaded " + HistoryManager.HISTORY.size() + " HISTORY events from the database took " + lElapsedTime + " ms");
        }
    }
    
    public static void addHistory(final String performerName, final String event) {
        addHistory(performerName, event, true);
    }
    
    public static void addHistory(final String performerName, final String event, final boolean twit) {
        HistoryManager.HISTORY_RW_LOCK.writeLock().lock();
        try {
            HistoryManager.HISTORY.addFirst(new HistoryEvent(System.currentTimeMillis(), performerName, event, Servers.localServer.id));
        }
        finally {
            HistoryManager.HISTORY_RW_LOCK.writeLock().unlock();
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO HISTORY(EVENTDATE,SERVER,PERFORMER,EVENT) VALUES (?,?,?,?)");
            ps.setLong(1, System.currentTimeMillis());
            ps.setInt(2, Servers.localServer.id);
            ps.setString(3, performerName);
            ps.setString(4, event);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            HistoryManager.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (twit) {
            Server.getInstance().twitLocalServer(performerName + " " + event);
        }
    }
    
    static {
        logger = Logger.getLogger(HistoryManager.class.getName());
        HISTORY = new LinkedList<HistoryEvent>();
        HISTORY_RW_LOCK = new ReentrantReadWriteLock();
    }
}

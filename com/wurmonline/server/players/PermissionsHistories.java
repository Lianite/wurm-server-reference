// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class PermissionsHistories
{
    private static final Logger logger;
    private static final String GET_HISTORY = "SELECT * FROM PERMISSIONSHISTORY ORDER BY OBJECTID, EVENTDATE";
    private static final String ADD_HISTORY = "INSERT INTO PERMISSIONSHISTORY(OBJECTID, EVENTDATE, PLAYERID, PERFORMER, EVENT) VALUES(?,?,?,?,?)";
    private static final String DELETE_HISTORY = "DELETE FROM PERMISSIONSHISTORY WHERE OBJECTID=?";
    private static final String PURGE_HISTORY = "DELETE FROM PERMISSIONSHISTORY WHERE EVENTDATE<?";
    private static Map<Long, PermissionsHistory> objectHistories;
    
    public static void loadAll() {
        PermissionsHistories.logger.log(Level.INFO, "Purging permissions history over 6 months old.");
        long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PERMISSIONSHISTORY WHERE EVENTDATE<?");
            ps.setLong(1, System.currentTimeMillis() - 14515200000L);
            count = ps.executeUpdate();
        }
        catch (SQLException ex) {
            PermissionsHistories.logger.log(Level.WARNING, "Failed to load history for permissions.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            PermissionsHistories.logger.log(Level.INFO, "Purged " + count + " permissions history. That took " + (end - start) / 1000000.0f + " ms.");
        }
        PermissionsHistories.logger.log(Level.INFO, "Loading all permissions history.");
        count = 0L;
        start = System.nanoTime();
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PERMISSIONSHISTORY ORDER BY OBJECTID, EVENTDATE");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long objectId = rs.getLong("OBJECTID");
                final long eventDate = rs.getLong("EVENTDATE");
                final long playerId = rs.getLong("PLAYERID");
                final String performer = rs.getString("PERFORMER");
                final String event = rs.getString("EVENT");
                add(objectId, eventDate, playerId, performer, event);
                ++count;
            }
        }
        catch (SQLException ex) {
            PermissionsHistories.logger.log(Level.WARNING, "Failed to load history for permissions.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end2 = System.nanoTime();
            PermissionsHistories.logger.log(Level.INFO, "Loaded " + count + " permissions history. That took " + (end2 - start) / 1000000.0f + " ms.");
        }
    }
    
    public static void moveHistories(final long fromId, final long toId) {
        final Long id = fromId;
        if (PermissionsHistories.objectHistories.containsKey(id)) {
            final PermissionsHistory oldHistories = PermissionsHistories.objectHistories.get(id);
            for (final PermissionsHistoryEntry phe : oldHistories.getHistoryEvents()) {
                addHistoryEntry(toId, phe.getEventDate(), phe.getPlayerId(), phe.getPlayerName(), phe.getEvent());
            }
            dbRemove(fromId);
        }
    }
    
    public static PermissionsHistory getPermissionsHistoryFor(final long objectId) {
        final Long id = objectId;
        if (PermissionsHistories.objectHistories.containsKey(id)) {
            return PermissionsHistories.objectHistories.get(id);
        }
        final PermissionsHistory ph = new PermissionsHistory();
        PermissionsHistories.objectHistories.put(id, ph);
        return ph;
    }
    
    private static void add(final long objectId, final long eventTime, final long playerId, final String playerName, final String event) {
        final PermissionsHistory ph = getPermissionsHistoryFor(objectId);
        ph.add(eventTime, playerId, playerName, event);
    }
    
    public static void addHistoryEntry(final long objectId, final long eventTime, final long playerId, final String playerName, final String event) {
        add(objectId, eventTime, playerId, playerName, event);
        dbAddHistoryEvent(objectId, eventTime, playerId, playerName, event);
    }
    
    private static void dbAddHistoryEvent(final long objectId, final long eventTime, final long playerId, final String playerName, final String event) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO PERMISSIONSHISTORY(OBJECTID, EVENTDATE, PLAYERID, PERFORMER, EVENT) VALUES(?,?,?,?,?)");
            String newEvent = event.replace("\"", "'");
            if (newEvent.length() > 255) {
                newEvent = newEvent.substring(0, 250) + "...";
            }
            ps.setLong(1, objectId);
            ps.setLong(2, eventTime);
            ps.setLong(3, playerId);
            ps.setString(4, playerName);
            ps.setString(5, newEvent);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PermissionsHistories.logger.log(Level.WARNING, "Failed to add permissions history for object (" + objectId + ")", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void remove(final long objectId) {
        final Long id = objectId;
        if (PermissionsHistories.objectHistories.containsKey(id)) {
            dbRemove(objectId);
            PermissionsHistories.objectHistories.remove(id);
        }
    }
    
    private static void dbRemove(final long objectId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PERMISSIONSHISTORY WHERE OBJECTID=?");
            ps.setLong(1, objectId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PermissionsHistories.logger.log(Level.WARNING, "Failed to delete permissions history for object " + objectId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public String[] getHistory(final long objectId, final int numevents) {
        final Long id = objectId;
        if (PermissionsHistories.objectHistories.containsKey(id)) {
            final PermissionsHistory ph = PermissionsHistories.objectHistories.get(id);
            return ph.getHistory(numevents);
        }
        return new String[0];
    }
    
    static {
        logger = Logger.getLogger(PermissionsHistories.class.getName());
        PermissionsHistories.objectHistories = new ConcurrentHashMap<Long, PermissionsHistory>();
    }
}

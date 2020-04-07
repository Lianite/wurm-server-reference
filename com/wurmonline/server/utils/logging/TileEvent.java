// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils.logging;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.LinkedList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public final class TileEvent implements WurmLoggable, TimeConstants
{
    private static final Logger logger;
    private static long lastPruned;
    private static long pruneInterval;
    private static final String PRUNE_ENTRIES = "DELETE FROM TILE_LOG WHERE DATE<?";
    private static final String FIND_ENTRIES_FOR_A_TILE = "SELECT * FROM TILE_LOG WHERE TILEX = ? AND TILEY = ? AND LAYER = ? ORDER BY DATE ASC";
    private static final String INSERT_TILE_LOG = "INSERT INTO TILE_LOG (TILEX,TILEY, LAYER, PERFORMER, ACTION, DATE) VALUES ( ?, ?, ?, ?, ?, ?)";
    private final int tilex;
    private final int tiley;
    private final int layer;
    private final long performer;
    private final int action;
    private final long date;
    private static final TileEventDatabaseLogger tileLogger;
    private static final ConcurrentHashMap<Long, TileEvent> playersLog;
    
    public TileEvent(final int _tileX, final int _tileY, final int _layer, final long _performer, final int _action) {
        this.tilex = _tileX;
        this.tiley = _tileY;
        this.layer = _layer;
        this.performer = _performer;
        this.action = _action;
        this.date = System.currentTimeMillis();
    }
    
    public TileEvent(final int _tileX, final int _tileY, final int _layer, final long _performer, final int _action, final long _date) {
        this.tilex = _tileX;
        this.tiley = _tileY;
        this.layer = _layer;
        this.performer = _performer;
        this.action = _action;
        this.date = _date;
    }
    
    public static TileEventDatabaseLogger getTilelogger() {
        return TileEvent.tileLogger;
    }
    
    public static void log(final int _tileX, final int _tileY, final int _layer, final long _performer, final int _action) {
        if (Constants.useTileEventLog) {
            TileEvent lEvent = null;
            try {
                final TileEvent oEvent = TileEvent.playersLog.get(_performer);
                if (oEvent != null && oEvent.tilex == _tileX && oEvent.tiley == _tileY && oEvent.layer == _layer && oEvent.action == _action && oEvent.date > System.currentTimeMillis() - 300000L) {
                    return;
                }
                lEvent = new TileEvent(_tileX, _tileY, _layer, _performer, _action);
                TileEvent.playersLog.put(_performer, lEvent);
                TileEvent.tileLogger.addToQueue(lEvent);
            }
            catch (Exception ex) {
                TileEvent.logger.log(Level.WARNING, "Could not add to queue: " + lEvent + " due to " + ex.getMessage(), ex);
            }
        }
    }
    
    static final int getLogSize() {
        return 0;
    }
    
    public static void pruneLogEntries() {
        if (System.currentTimeMillis() - TileEvent.lastPruned > TileEvent.pruneInterval) {
            TileEvent.lastPruned = System.currentTimeMillis();
            final long cutDate = System.currentTimeMillis() - TileEvent.pruneInterval;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getLogsDbCon();
                ps = dbcon.prepareStatement("DELETE FROM TILE_LOG WHERE DATE<?");
                ps.setLong(1, cutDate);
                ps.execute();
            }
            catch (SQLException sqx) {
                TileEvent.logger.log(Level.WARNING, "Failed to prune log entries.", sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @NonNull
    public static List<TileEvent> getEventsFor(final int aTileX, final int aTileY, final int aLayer) {
        final List<TileEvent> matches = new LinkedList<TileEvent>();
        if (Constants.useTileEventLog) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getLogsDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM TILE_LOG WHERE TILEX = ? AND TILEY = ? AND LAYER = ? ORDER BY DATE ASC");
                ps.setInt(1, aTileX);
                ps.setInt(2, aTileY);
                ps.setInt(3, aLayer);
                rs = ps.executeQuery();
                while (rs.next()) {
                    final TileEvent lEvent = new TileEvent(rs.getInt("TILEX"), rs.getInt("TILEY"), rs.getInt("LAYER"), rs.getLong("PERFORMER"), rs.getInt("ACTION"), rs.getLong("DATE"));
                    matches.add(lEvent);
                }
            }
            catch (SQLException sqx) {
                TileEvent.logger.log(Level.WARNING, "Failed to load log entry.", sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
        return matches;
    }
    
    public int getTileX() {
        return this.tilex;
    }
    
    public int getTileY() {
        return this.tiley;
    }
    
    public int getLayer() {
        return this.layer;
    }
    
    public int getAction() {
        return this.action;
    }
    
    public long getPerformer() {
        return this.performer;
    }
    
    public long getDate() {
        return this.date;
    }
    
    @Override
    public String getDatabaseInsertStatement() {
        return "INSERT INTO TILE_LOG (TILEX,TILEY, LAYER, PERFORMER, ACTION, DATE) VALUES ( ?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    public String toString() {
        return "TileEvent [tilex=" + this.tilex + ", tiley=" + this.tiley + ", layer=" + this.layer + ", performer=" + this.performer + ", action=" + this.action + ", date=" + this.date + "]";
    }
    
    static {
        logger = Logger.getLogger(TileEvent.class.getName());
        TileEvent.lastPruned = System.currentTimeMillis() + 21600000L;
        TileEvent.pruneInterval = 432000000L;
        tileLogger = new TileEventDatabaseLogger("Tile logger", 500);
        playersLog = new ConcurrentHashMap<Long, TileEvent>();
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils.logging;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.sql.ResultSet;
import java.sql.Connection;
import com.wurmonline.server.DbConnector;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.LinkedList;
import java.sql.PreparedStatement;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class TileLog implements TimeConstants, WurmLoggable
{
    private static Logger logger;
    static long lastPruned;
    static int numBatchEvents;
    static long pruneInterval;
    private static PreparedStatement lastmPS;
    private static int lastmPSCount;
    public static int overallLastmPSCount;
    public static final String LOAD_ALL_ENTRIES = "SELECT * FROM TILE_LOG";
    public static final String PRUNE_ENTRIES = "DELETE FROM TILE_LOG WHERE DATE<?";
    static final String INSERT_TILE_LOG = "INSERT INTO TILE_LOG (TILEX,TILEY, LAYER, PERFORMER, ACTION, DATE) VALUES ( ?, ?, ?, ?, ?, ?)";
    int tilex;
    int tiley;
    int layer;
    long performer;
    int action;
    long date;
    public static final LinkedList<TileLog> logEntries;
    
    public TileLog(final int _tx, final int _ty, final int _layer, final long _performer, final int _action, final boolean load) {
        this.tilex = _tx;
        this.tiley = _ty;
        this.layer = _layer;
        this.performer = _performer;
        this.action = _action;
        if (!load) {
            this.date = System.currentTimeMillis();
            this.save();
        }
        TileLog.logEntries.add(this);
    }
    
    public static final int getLogSize() {
        return TileLog.logEntries.size();
    }
    
    public void setDate(final long newDate) {
        this.date = newDate;
    }
    
    public static void clearBatches() {
        try {
            if (TileLog.lastmPS != null) {
                final int[] x = TileLog.lastmPS.executeBatch();
                TileLog.logger.log(Level.INFO, "Saved tile log batch size " + x.length);
                TileLog.lastmPS.close();
                TileLog.lastmPS = null;
                TileLog.lastmPSCount = 0;
            }
        }
        catch (SQLException iox) {
            TileLog.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
    }
    
    public void save() {
        try {
            if (TileLog.lastmPS == null) {
                final Connection dbcon = DbConnector.getLogsDbCon();
                TileLog.lastmPS = dbcon.prepareStatement("INSERT INTO TILE_LOG (TILEX,TILEY, LAYER, PERFORMER, ACTION, DATE) VALUES ( ?, ?, ?, ?, ?, ?)");
            }
            TileLog.lastmPS.setInt(1, this.getTileX());
            TileLog.lastmPS.setInt(2, this.getTileY());
            TileLog.lastmPS.setInt(3, this.getLayer());
            TileLog.lastmPS.setLong(4, this.getPerformer());
            TileLog.lastmPS.setInt(5, this.getAction());
            TileLog.lastmPS.setLong(6, this.getDate());
            TileLog.lastmPS.addBatch();
            ++TileLog.overallLastmPSCount;
            ++TileLog.lastmPSCount;
            if (TileLog.lastmPSCount > TileLog.numBatchEvents) {
                final long checkms = System.currentTimeMillis();
                TileLog.lastmPS.executeBatch();
                TileLog.lastmPS.close();
                TileLog.lastmPS = null;
                if (System.currentTimeMillis() - checkms > 300L || TileLog.logger.isLoggable(Level.FINEST)) {
                    TileLog.logger.log(Level.WARNING, "TileLog batch took " + (System.currentTimeMillis() - checkms) + " ms for " + TileLog.lastmPSCount + " updates.");
                }
                TileLog.lastmPSCount = 0;
            }
        }
        catch (SQLException sqx) {
            TileLog.logger.log(Level.WARNING, "Failed to save log entry.", sqx);
        }
    }
    
    public static void loadAllLogEntries() {
        try {
            final Connection dbcon = DbConnector.getLogsDbCon();
            final PreparedStatement ps = dbcon.prepareStatement("SELECT * FROM TILE_LOG");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final TileLog tl = new TileLog(rs.getInt("TILEX"), rs.getInt("TILEY"), rs.getInt("LAYER"), rs.getLong("PERFORMER"), rs.getInt("ACTION"), true);
                tl.setDate(rs.getLong("DATE"));
            }
        }
        catch (SQLException sqx) {
            TileLog.logger.log(Level.WARNING, "Failed to load log entry.", sqx);
        }
    }
    
    public static void pruneLogEntries() {
        if (System.currentTimeMillis() - TileLog.lastPruned > TileLog.pruneInterval) {
            TileLog.lastPruned = System.currentTimeMillis();
            final long cutDate = System.currentTimeMillis() - TileLog.pruneInterval;
            try {
                final Connection dbcon = DbConnector.getLogsDbCon();
                final PreparedStatement ps = dbcon.prepareStatement("DELETE FROM TILE_LOG WHERE DATE<?");
                ps.setLong(1, cutDate);
                ps.execute();
            }
            catch (SQLException sqx) {
                TileLog.logger.log(Level.WARNING, "Failed to prune log entries.", sqx);
            }
            final ListIterator<TileLog> lit = TileLog.logEntries.listIterator();
            while (lit.hasNext() && lit.next().getDate() < cutDate) {
                lit.remove();
            }
        }
    }
    
    public static final List<TileLog> getEventsFor(final int tilex, final int tiley, final int layer) {
        final LinkedList<TileLog> matches = new LinkedList<TileLog>();
        for (final TileLog t : TileLog.logEntries) {
            if (t.getTileX() == tilex && t.getTileY() == tiley && t.getLayer() == layer) {
                matches.add(t);
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
    public String toString() {
        return "ItemTransfer [tilex=" + this.tilex + ", tiley=" + this.tiley + ", performer=" + this.performer + ", action=" + this.action + ", date=" + this.date + "]";
    }
    
    @Override
    public final String getDatabaseInsertStatement() {
        return "INSERT INTO TILE_LOG (TILEX,TILEY, LAYER, PERFORMER, ACTION, DATE) VALUES ( ?, ?, ?, ?, ?, ?)";
    }
    
    static {
        TileLog.logger = Logger.getLogger(TileLog.class.getName());
        TileLog.lastPruned = 0L;
        TileLog.numBatchEvents = 50;
        TileLog.pruneInterval = 432000000L;
        TileLog.lastmPS = null;
        TileLog.lastmPSCount = 0;
        TileLog.overallLastmPSCount = 0;
        logEntries = new LinkedList<TileLog>();
    }
}

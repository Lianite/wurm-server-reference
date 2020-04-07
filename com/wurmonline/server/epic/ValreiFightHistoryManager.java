// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.TreeMap;
import java.util.logging.Logger;

public class ValreiFightHistoryManager
{
    private static final Logger logger;
    private static final String GET_MAX_FIGHTID = "SELECT MAX(FIGHTID) FROM ENTITYFIGHTS";
    private static final String LOAD_FIGHTS = "SELECT * FROM ENTITYFIGHTS";
    private static final String SAVE_FIGHT = "INSERT INTO ENTITYFIGHTS(FIGHTID,MAPHEXID,MAPHEXNAME,FIGHTTIME,FIGHTER1ID,FIGHTER1NAME,FIGHTER2ID,FIGHTER2NAME) VALUES (?,?,?,?,?,?,?,?)";
    private static ValreiFightHistoryManager instance;
    private static long fightIdCounter;
    private TreeMap<Long, ValreiFightHistory> allFights;
    
    public static long getNextFightId() {
        return ++ValreiFightHistoryManager.fightIdCounter;
    }
    
    public static ValreiFightHistoryManager getInstance() {
        if (ValreiFightHistoryManager.instance == null) {
            ValreiFightHistoryManager.instance = new ValreiFightHistoryManager();
        }
        return ValreiFightHistoryManager.instance;
    }
    
    private ValreiFightHistoryManager() {
        this.loadAllFights();
    }
    
    private void saveFight(final long fightId, final ValreiFightHistory newFight) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("INSERT INTO ENTITYFIGHTS(FIGHTID,MAPHEXID,MAPHEXNAME,FIGHTTIME,FIGHTER1ID,FIGHTER1NAME,FIGHTER2ID,FIGHTER2NAME) VALUES (?,?,?,?,?,?,?,?)");
            ps.setLong(1, fightId);
            ps.setInt(2, newFight.getMapHexId());
            ps.setString(3, newFight.getMapHexName());
            ps.setLong(4, newFight.getFightTime());
            final HashMap<Long, ValreiFightHistory.ValreiFighter> fighters = newFight.getFighters();
            if (fighters.size() >= 2) {
                int val = 5;
                for (final ValreiFightHistory.ValreiFighter f : fighters.values()) {
                    ps.setLong(val++, f.getFighterId());
                    ps.setString(val++, f.getName());
                    if (val >= 8) {
                        break;
                    }
                }
            }
            else {
                ps.setLong(5, -1L);
                ps.setString(6, "Unknown");
                ps.setLong(7, -1L);
                ps.setString(8, "Unknown");
            }
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            ValreiFightHistoryManager.logger.log(Level.WARNING, "Failed to save valrei fight: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void loadAllFights() {
        this.allFights = new TreeMap<Long, ValreiFightHistory>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ENTITYFIGHTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long fightId = rs.getLong("FIGHTID");
                final int mapHexId = rs.getInt("MAPHEXID");
                final String mapHexName = rs.getString("MAPHEXNAME");
                final long fightTime = rs.getLong("FIGHTTIME");
                final long fighter1 = rs.getLong("FIGHTER1ID");
                final String fighter1Name = rs.getString("FIGHTER1NAME");
                final long fighter2 = rs.getLong("FIGHTER2ID");
                final String fighter2Name = rs.getString("FIGHTER2NAME");
                final ValreiFightHistory oldFight = new ValreiFightHistory(fightId, mapHexId, mapHexName, fightTime);
                oldFight.addFighter(fighter1, fighter1Name);
                oldFight.addFighter(fighter2, fighter2Name);
                oldFight.loadActions();
                this.allFights.put(fightId, oldFight);
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            ValreiFightHistoryManager.logger.log(Level.WARNING, "Failed to load all valrei fights: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public int getNumberOfFights() {
        return this.allFights.size();
    }
    
    public void addFight(final long fightId, final ValreiFightHistory newFight) {
        this.addFight(fightId, newFight, true);
    }
    
    public void addFight(final long fightId, final ValreiFightHistory newFight, final boolean save) {
        this.allFights.put(fightId, newFight);
        if (save) {
            this.saveFight(fightId, newFight);
        }
    }
    
    public ValreiFightHistory getFight(final long fightId) {
        return this.allFights.get(fightId);
    }
    
    public ArrayList<ValreiFightHistory> get10Fights(final int listPage) {
        if (this.allFights.size() / 10 < listPage) {
            return null;
        }
        try {
            final ArrayList<ValreiFightHistory> toReturn = new ArrayList<ValreiFightHistory>();
            long finalKey = this.allFights.lastKey();
            for (int i = 0; i < listPage; ++i) {
                for (int k = 0; k < 10 && this.allFights.lowerKey(finalKey) != null; finalKey = this.allFights.lowerKey(finalKey), ++k) {}
            }
            for (int i = 0; i < 10; ++i) {
                toReturn.add(this.allFights.get(finalKey));
                if (this.allFights.lowerKey(finalKey) == null) {
                    break;
                }
                finalKey = this.allFights.lowerKey(finalKey);
            }
            return toReturn;
        }
        catch (NoSuchElementException ne) {
            ValreiFightHistoryManager.logger.log(Level.WARNING, "Unable to load 10 fights for page " + listPage + ". No key exists in allFights map.");
            return null;
        }
    }
    
    public ArrayList<ValreiFightHistory> getAllFights() {
        return new ArrayList<ValreiFightHistory>(this.allFights.values());
    }
    
    public ValreiFightHistory getLatestFight() {
        return this.allFights.get(this.allFights.lastKey());
    }
    
    static {
        logger = Logger.getLogger(ValreiFightHistoryManager.class.getName());
        ValreiFightHistoryManager.fightIdCounter = 1L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT MAX(FIGHTID) FROM ENTITYFIGHTS");
            rs = ps.executeQuery();
            if (rs.next()) {
                ValreiFightHistoryManager.fightIdCounter = rs.getLong("MAX(FIGHTID)");
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            ValreiFightHistoryManager.logger.log(Level.WARNING, "Failed to load max fight id: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
}

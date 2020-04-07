// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.util.ArrayList;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.shared.constants.ValreiConstants;
import java.util.HashMap;
import java.util.logging.Logger;

public class ValreiFightHistory
{
    private static final Logger logger;
    private static final String LOAD_FIGHT_ACTIONS = "SELECT * FROM ENTITYFIGHTACTIONS WHERE FIGHTID=?";
    private static final String SAVE_FIGHT_ACTION = "INSERT INTO ENTITYFIGHTACTIONS(FIGHTID,FIGHTACTIONNUM,ACTIONID,ACTIONDATA) VALUES (?,?,?,?)";
    private final long fightId;
    private int mapHexId;
    private String mapHexName;
    private long fightTime;
    private HashMap<Long, ValreiFighter> fighters;
    private HashMap<Integer, ValreiConstants.ValreiFightAction> allActions;
    private int fightActionNum;
    private boolean fightCompleted;
    
    public ValreiFightHistory(final int mapHexId, final String mapHexName) {
        this.mapHexId = mapHexId;
        this.mapHexName = mapHexName;
        this.fightId = ValreiFightHistoryManager.getNextFightId();
        this.fighters = new HashMap<Long, ValreiFighter>();
        this.allActions = new HashMap<Integer, ValreiConstants.ValreiFightAction>();
        this.fightActionNum = 0;
        this.fightCompleted = false;
        this.fightTime = WurmCalendar.currentTime;
    }
    
    public ValreiFightHistory(final long fightId, final int mapHexId, final String mapHexName, final long fightTime) {
        this.fightId = fightId;
        this.mapHexId = mapHexId;
        this.mapHexName = mapHexName;
        this.fightTime = fightTime;
        this.fighters = new HashMap<Long, ValreiFighter>();
        this.allActions = new HashMap<Integer, ValreiConstants.ValreiFightAction>();
        this.fightCompleted = true;
    }
    
    public void saveActions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            for (final ValreiConstants.ValreiFightAction fa : this.allActions.values()) {
                ps = dbcon.prepareStatement("INSERT INTO ENTITYFIGHTACTIONS(FIGHTID,FIGHTACTIONNUM,ACTIONID,ACTIONDATA) VALUES (?,?,?,?)");
                ps.setLong(1, this.fightId);
                ps.setInt(2, fa.getActionNum());
                ps.setShort(3, fa.getActionId());
                ps.setBytes(4, fa.getActionData());
                ps.executeUpdate();
                ps.close();
            }
        }
        catch (SQLException sqx) {
            ValreiFightHistory.logger.log(Level.WARNING, "Failed to save actions for this valrei fight: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void loadActions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ENTITYFIGHTACTIONS WHERE FIGHTID=?");
            ps.setLong(1, this.fightId);
            rs = ps.executeQuery();
            while (rs.next()) {
                final int actionNum = rs.getInt("FIGHTACTIONNUM");
                final short action = rs.getShort("ACTIONID");
                final byte[] actionData = rs.getBytes("ACTIONDATA");
                if (this.fightActionNum < actionNum) {
                    this.fightActionNum = actionNum;
                }
                this.allActions.put(actionNum, new ValreiConstants.ValreiFightAction(actionNum, action, actionData));
            }
            rs.close();
            ps.close();
        }
        catch (SQLException sqx) {
            ValreiFightHistory.logger.log(Level.WARNING, "Failed to load all valrei fights: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void addFighter(final long fighterId, final String fighterName) {
        if (this.fighters.get(fighterId) == null) {
            this.fighters.put(fighterId, new ValreiFighter(fighterId, fighterName));
        }
        else {
            final ValreiFighter f = this.fighters.get(fighterId);
            f.setName(fighterName);
        }
    }
    
    public HashMap<Long, ValreiFighter> getFighters() {
        return this.fighters;
    }
    
    public void addAction(final short actionType, final byte[] actionData) {
        this.allActions.put(this.fightActionNum, new ValreiConstants.ValreiFightAction(this.fightActionNum, actionType, actionData));
        ++this.fightActionNum;
    }
    
    public ValreiConstants.ValreiFightAction getFightAction(final int actionNum) {
        return this.allActions.get(actionNum);
    }
    
    public String getPreviewString() {
        if (!this.fighters.isEmpty()) {
            final ArrayList<String> fighters = new ArrayList<String>();
            for (final ValreiFighter vf : this.fighters.values()) {
                fighters.add(vf.fighterName);
            }
            return fighters.get(0) + " vs " + fighters.get(1) + " at " + this.getMapHexName() + " on " + WurmCalendar.getDateFor(this.fightTime);
        }
        return "Unknown fight on " + WurmCalendar.getDateFor(this.fightTime);
    }
    
    public long getFightWinner() {
        final byte[] actionData = this.allActions.get(this.fightActionNum).getActionData();
        return ValreiConstants.getEndFightWinner(actionData);
    }
    
    public long getFightId() {
        return this.fightId;
    }
    
    public int getMapHexId() {
        return this.mapHexId;
    }
    
    public String getMapHexName() {
        return this.mapHexName;
    }
    
    public boolean isFightCompleted() {
        return this.fightCompleted;
    }
    
    public void setFightCompleted(final boolean isCompleted) {
        this.fightCompleted = isCompleted;
        --this.fightActionNum;
    }
    
    public long getFightTime() {
        return this.fightTime;
    }
    
    public int getTotalActions() {
        return this.fightActionNum;
    }
    
    static {
        logger = Logger.getLogger(ValreiFightHistoryManager.class.getName());
    }
    
    public class ValreiFighter
    {
        private long fighterId;
        private String fighterName;
        
        ValreiFighter(final long id, final String name) {
            this.setFighterId(id);
            this.fighterName = name;
        }
        
        public String getName() {
            return this.fighterName;
        }
        
        public void setName(final String newName) {
            this.fighterName = newName;
        }
        
        public long getFighterId() {
            return this.fighterId;
        }
        
        public void setFighterId(final long fighterId) {
            this.fighterId = fighterId;
        }
    }
}

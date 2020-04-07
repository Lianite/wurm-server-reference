// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Players;
import java.util.Iterator;
import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.server.creatures.Creature;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class MissionHelper implements MiscConstants
{
    private static final Logger logger;
    private static final String LOAD_ALL_MISSION_HELPERS = "SELECT * FROM MISSIONHELPERS";
    private static final String INSERT_MISSION_HELPER;
    private static final String MOVE_MISSION_HELPER = "UPDATE MISSIONHELPERS SET MISSIONID=? WHERE MISSIONID=?";
    private static final String DELETE_MISSION_HELPER = "DELETE FROM MISSIONHELPERS WHERE MISSIONID=?";
    private static final String UPDATE_MISSION_HELPER = "UPDATE MISSIONHELPERS SET NUMS=? WHERE MISSIONID=? AND PLAYERID=?";
    private static final Map<Long, MissionHelper> MISSION_HELPERS;
    private static boolean INITIALIZED;
    private final Map<Long, Integer> missionsHelped;
    private final long playerId;
    
    public MissionHelper(final long playerid) {
        this.missionsHelped = new ConcurrentHashMap<Long, Integer>();
        this.playerId = playerid;
        addHelper(this);
    }
    
    public final void increaseHelps(final long missionId) {
        this.setHelps(missionId, this.getHelps(missionId) + 1);
    }
    
    public final void increaseHelps(final long missionId, final int nums) {
        this.setHelps(missionId, this.getHelps(missionId) + nums);
    }
    
    public static final void addHelper(final MissionHelper helper) {
        MissionHelper.MISSION_HELPERS.put(helper.getPlayerId(), helper);
    }
    
    private final void setHelpsAtLoad(final long missionId, final int nums) {
        this.missionsHelped.put(missionId, nums);
    }
    
    public static final Map<Long, MissionHelper> getHelpers() {
        return MissionHelper.MISSION_HELPERS;
    }
    
    public static final void loadAll() {
        if (!MissionHelper.INITIALIZED) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM MISSIONHELPERS");
                rs = ps.executeQuery();
                while (rs.next()) {
                    final long helperId = rs.getLong("PLAYERID");
                    MissionHelper helper = MissionHelper.MISSION_HELPERS.get(helperId);
                    if (helper == null) {
                        helper = new MissionHelper(helperId);
                    }
                    helper.setHelpsAtLoad(rs.getLong("MISSIONID"), rs.getInt("NUMS"));
                }
                MissionHelper.INITIALIZED = true;
            }
            catch (SQLException sqx) {
                MissionHelper.logger.log(Level.WARNING, "Failed to load epic item helpers.", sqx);
                MissionHelper.INITIALIZED = false;
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static final void printHelpForMission(final long missionId, final String missionName, final Creature performer) {
        float total = 0.0f;
        if (!MissionHelper.INITIALIZED) {
            loadAll();
        }
        for (final MissionHelper helper : MissionHelper.MISSION_HELPERS.values()) {
            total += helper.getHelps(missionId);
        }
        if (total > 0.0f) {
            final SimplePopup sp = new SimplePopup(performer, "Plaque on " + missionName, "These helped:", missionId, total);
            sp.sendQuestion();
        }
    }
    
    public static final void addKarmaForItem(final long itemId) {
        for (final MissionHelper helper : MissionHelper.MISSION_HELPERS.values()) {
            final int i = helper.getHelps(itemId);
            if (i > 10) {
                try {
                    final Player p = Players.getInstance().getPlayer(helper.getPlayerId());
                    p.modifyKarma(i / 10);
                }
                catch (NoSuchPlayerException nsp) {
                    final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(helper.getPlayerId());
                    pinf.setKarma(pinf.getKarma() + i / 10);
                }
            }
        }
    }
    
    public static final MissionHelper getOrCreateHelper(final long playerId) {
        MissionHelper helper = MissionHelper.MISSION_HELPERS.get(playerId);
        if (helper == null) {
            helper = new MissionHelper(playerId);
        }
        return helper;
    }
    
    public final long getPlayerId() {
        return this.playerId;
    }
    
    public final int getHelps(final long missionId) {
        final Integer nums = this.missionsHelped.get(missionId);
        if (nums == null) {
            return 0;
        }
        return nums;
    }
    
    private final void moveLocalMissionId(final long oldMissionId, final long newMissionId) {
        final int oldHelps = this.getHelps(oldMissionId);
        if (oldHelps > 0) {
            this.missionsHelped.remove(oldMissionId);
            this.missionsHelped.put(newMissionId, oldHelps);
        }
    }
    
    private final void removeMissionId(final long missionId) {
        this.missionsHelped.remove(missionId);
    }
    
    public static final void moveGlobalMissionId(final long oldmissionId, final long newMissionId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE MISSIONHELPERS SET MISSIONID=? WHERE MISSIONID=?");
            ps.setLong(1, newMissionId);
            ps.setLong(2, oldmissionId);
            ps.executeUpdate();
            for (final MissionHelper h : MissionHelper.MISSION_HELPERS.values()) {
                h.moveLocalMissionId(oldmissionId, newMissionId);
            }
        }
        catch (SQLException sqx) {
            MissionHelper.logger.log(Level.WARNING, "Failed to move epic mission helps from mission " + oldmissionId + ", to" + newMissionId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteMissionId(final long missionId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MISSIONHELPERS WHERE MISSIONID=?");
            ps.setLong(1, missionId);
            ps.executeUpdate();
            for (final MissionHelper h : MissionHelper.MISSION_HELPERS.values()) {
                h.removeMissionId(missionId);
            }
        }
        catch (SQLException sqx) {
            MissionHelper.logger.log(Level.WARNING, "Failed to delete epic mission helps for mission " + missionId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void setHelps(final long missionId, final int helps) {
        final int oldHelps = this.getHelps(missionId);
        if (oldHelps != helps) {
            this.missionsHelped.put(missionId, helps);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                if (oldHelps == 0) {
                    ps = dbcon.prepareStatement(MissionHelper.INSERT_MISSION_HELPER);
                }
                else {
                    ps = dbcon.prepareStatement("UPDATE MISSIONHELPERS SET NUMS=? WHERE MISSIONID=? AND PLAYERID=?");
                }
                ps.setInt(1, helps);
                ps.setLong(2, missionId);
                ps.setLong(3, this.playerId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                MissionHelper.logger.log(Level.WARNING, "Failed to save epic item helps " + helps + " for mission " + missionId + ", pid=" + this.playerId, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(MissionHelper.class.getName());
        INSERT_MISSION_HELPER = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO MISSIONHELPERS (NUMS, MISSIONID, PLAYERID) VALUES(?,?,?)" : "INSERT IGNORE INTO MISSIONHELPERS (NUMS, MISSIONID, PLAYERID) VALUES(?,?,?)");
        MISSION_HELPERS = new ConcurrentHashMap<Long, MissionHelper>();
        MissionHelper.INITIALIZED = false;
    }
}

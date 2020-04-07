// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public final class Missions implements MiscConstants
{
    private static final Map<Integer, Mission> missions;
    private static final String LOADALLMISSIONS = "SELECT * FROM MISSIONS";
    private static final String DELETEMISSION = "DELETE FROM MISSIONS WHERE ID=?";
    private static Logger logger;
    public static final byte CREATOR_UNSET = 0;
    public static final byte CREATOR_GM = 1;
    public static final byte CREATOR_SYSTEM = 2;
    public static final byte CREATOR_PLAYER = 3;
    public static final int SHOW_ALL = 0;
    public static final int SHOW_WITH = 1;
    public static final int SHOW_NONE = 2;
    
    public static int getNumMissions() {
        return Missions.missions.size();
    }
    
    public static Mission[] getAllMissions() {
        return Missions.missions.values().toArray(new Mission[Missions.missions.size()]);
    }
    
    public static Mission[] getFilteredMissions(final Creature creature, final int showTriggers, final boolean incInactive, final boolean dontListMine, final boolean listMineOnly, final long listForUser, final String groupName, final boolean onlyCurrent, final long currentTargetId) {
        final Set<Mission> missionSet = new HashSet<Mission>();
        for (final Mission mission : Missions.missions.values()) {
            final boolean own = mission.getOwnerId() == creature.getWurmId();
            boolean show = creature.getPower() > 0 || own;
            final boolean userMatch = mission.getOwnerId() == listForUser;
            if (own) {
                if (dontListMine) {
                    show = false;
                }
            }
            else if (listMineOnly) {
                show = false;
                if (listForUser != -10L && userMatch) {
                    show = true;
                }
            }
            else if (listForUser != -10L) {
                show = false;
                if (userMatch) {
                    show = true;
                }
            }
            if (show && showTriggers == 2 && mission.hasTriggers()) {
                show = false;
            }
            if (show && showTriggers == 1 && !mission.hasTriggers()) {
                show = false;
            }
            if (show && !incInactive && mission.isInactive()) {
                show = false;
            }
            if (show && mission.getCreatorType() == 2 && creature.getPower() < 2) {
                show = false;
            }
            if (show && !groupName.isEmpty() && !mission.getGroupName().equals(groupName)) {
                show = false;
            }
            if (show && onlyCurrent && !mission.hasTargetOf(currentTargetId, creature)) {
                show = false;
            }
            if (show && (mission.getCreatorType() != 2 || mission.getCreatedDate() > System.currentTimeMillis() - 2419200000L)) {
                missionSet.add(mission);
            }
        }
        return missionSet.toArray(new Mission[missionSet.size()]);
    }
    
    public static void addMission(final Mission m) {
        Missions.missions.put(m.getId(), m);
    }
    
    public static Mission getMissionWithId(final int mid) {
        return Missions.missions.get(mid);
    }
    
    public static Mission[] getMissionsWithTargetId(final long tid, final Creature performer) {
        final MissionTrigger[] triggers = MissionTriggers.getAllTriggers();
        final Set<Mission> toReturn = new HashSet<Mission>();
        for (final MissionTrigger mt : triggers) {
            if (mt.getTarget() == tid) {
                final Mission m = getMissionWithId(mt.getMissionRequired());
                if (m != null && (m.getCreatorType() != 2 || performer.getPower() >= 5) && (performer.getPower() > 0 || m.getMissionCreatorName().equals(performer.getName()))) {
                    toReturn.add(m);
                }
            }
        }
        return toReturn.toArray(new Mission[toReturn.size()]);
    }
    
    public static void deleteMission(final int misid) {
        removeMission(misid);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MISSIONS WHERE ID=?");
            ps.setInt(1, misid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Missions.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void loadAllMissions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MISSIONS");
            rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp st = new Timestamp(System.currentTimeMillis());
                try {
                    final String lastModified = rs.getString("LASTMODIFIEDDATE");
                    if (lastModified != null) {
                        st = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastModified).getTime());
                    }
                }
                catch (Exception ex) {
                    Missions.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
                final Mission m = new Mission(rs.getInt("ID"), rs.getString("NAME"), rs.getString("INSTRUCTION"), rs.getBoolean("INACTIVE"), rs.getString("CREATOR"), rs.getString("CREATEDDATE"), rs.getString("LASTMODIFIER"), st, rs.getInt("MAXTIMESECS"), rs.getBoolean("MAYBERESTARTED"));
                m.setCreatorType(rs.getByte("CREATORTYPE"));
                m.setOwnerId(rs.getLong("CREATORID"));
                m.setSecondChance(rs.getBoolean("SECONDCHANCE"));
                m.setFailOnDeath(rs.getBoolean("FAILONDEATH"));
                m.setGroupName(rs.getString("GROUP_NAME"));
                m.setIsHidden(rs.getBoolean("HIDDEN"));
                addMission(m);
            }
        }
        catch (SQLException sqx) {
            Missions.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    protected static void removeMission(final int mid) {
        Missions.missions.remove(mid);
    }
    
    static {
        missions = new HashMap<Integer, Mission>();
        Missions.logger = Logger.getLogger(Missions.class.getName());
        try {
            loadAllMissions();
        }
        catch (Exception ex) {
            Missions.logger.log(Level.WARNING, "Problems loading all Missions", ex);
        }
    }
}

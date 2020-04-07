// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.epic.EpicServerStatus;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import java.util.Date;
import java.text.ParseException;
import java.util.logging.Level;
import java.text.DateFormat;
import javax.annotation.Nullable;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class Mission implements Comparable<Mission>
{
    private static final Logger logger;
    private static final String UPDATE_MISSION = "UPDATE MISSIONS SET NAME=?,INSTRUCTION=?,INACTIVE=?,CREATOR=?,CREATEDDATE=?,LASTMODIFIER=?,MAXTIMESECS=?,SECONDCHANCE=?,MAYBERESTARTED=?,FAILONDEATH=?,GROUP_NAME=?,HIDDEN=? WHERE ID=?";
    private static final String CREATE_MISSION = "INSERT INTO MISSIONS (NAME,INSTRUCTION,INACTIVE,CREATOR,CREATEDDATE,LASTMODIFIER,MAXTIMESECS,SECONDCHANCE,MAYBERESTARTED,CREATORID,CREATORTYPE,FAILONDEATH,GROUP_NAME,HIDDEN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_MISSION = "DELETE FROM MISSIONS WHERE ID=?";
    private int id;
    private String name;
    private String groupName;
    private String instruction;
    private boolean inActive;
    private boolean isHidden;
    private String missionCreatorName;
    private String createdDate;
    private String lastModifierName;
    private Timestamp lastModifiedDate;
    private int maxTimeSeconds;
    private boolean hasSecondChance;
    private boolean mayBeRestarted;
    private long ownerId;
    private byte creatorType;
    private boolean failOnDeath;
    
    public Mission(final String aMissionCreatorName, final String aLastModifierName) {
        this.groupName = "";
        this.instruction = "unknown";
        this.inActive = false;
        this.isHidden = false;
        this.maxTimeSeconds = 0;
        this.hasSecondChance = false;
        this.mayBeRestarted = false;
        this.ownerId = 0L;
        this.creatorType = 0;
        this.failOnDeath = false;
        this.missionCreatorName = aMissionCreatorName;
        this.lastModifierName = aLastModifierName;
    }
    
    Mission(final int aId, final String aName, final String aInstruction, final boolean aInActive, final String aMissionCreatorName, final String aCreatedDate, final String aLastModifierName, final Timestamp aLastModifiedDate, final int aMaxTimeSeconds, final boolean aMayBeRestarted) {
        this.groupName = "";
        this.instruction = "unknown";
        this.inActive = false;
        this.isHidden = false;
        this.maxTimeSeconds = 0;
        this.hasSecondChance = false;
        this.mayBeRestarted = false;
        this.ownerId = 0L;
        this.creatorType = 0;
        this.failOnDeath = false;
        this.id = aId;
        this.name = aName;
        this.instruction = aInstruction;
        this.inActive = aInActive;
        this.missionCreatorName = aMissionCreatorName;
        this.createdDate = aCreatedDate;
        this.lastModifierName = aLastModifierName;
        this.lastModifiedDate = aLastModifiedDate;
        this.maxTimeSeconds = aMaxTimeSeconds;
        this.mayBeRestarted = aMayBeRestarted;
    }
    
    void setId(final int aId) {
        this.id = aId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getInstruction() {
        return this.instruction;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getMissionCreatorName() {
        return this.missionCreatorName;
    }
    
    public String getOwnerName() {
        if (this.getCreatorType() == 2) {
            return "System";
        }
        return PlayerInfoFactory.getPlayerName(this.getOwnerId());
    }
    
    public String getLastModifiedString() {
        return this.getLastModifierName() + ", " + this.getLastModifiedDate();
    }
    
    public String getLastModifierName() {
        return this.lastModifierName;
    }
    
    public boolean hasSecondChance() {
        return this.hasSecondChance;
    }
    
    public void setSecondChance(final boolean restart) {
        this.hasSecondChance = restart;
    }
    
    public boolean isInactive() {
        return this.inActive;
    }
    
    public void setInactive(final boolean inactive) {
        this.inActive = inactive;
    }
    
    public boolean isHidden() {
        return this.isHidden;
    }
    
    public void setIsHidden(final boolean ishidden) {
        this.isHidden = ishidden;
    }
    
    public void setMaxTimeSeconds(final int seconds) {
        this.maxTimeSeconds = seconds;
    }
    
    public int getMaxTimeSeconds() {
        return this.maxTimeSeconds;
    }
    
    public void setCreatorType(final byte aCreatorType) {
        this.creatorType = aCreatorType;
    }
    
    public byte getCreatorType() {
        return this.creatorType;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public void setOwnerId(final long aWurmId) {
        this.ownerId = aWurmId;
    }
    
    public void setMayBeRestarted(final boolean restarted) {
        this.mayBeRestarted = restarted;
    }
    
    public boolean mayBeRestarted() {
        return this.mayBeRestarted;
    }
    
    public void setFailOnDeath(final boolean fail) {
        this.failOnDeath = fail;
    }
    
    public boolean isFailOnDeath() {
        return this.failOnDeath;
    }
    
    public long getOwnerId() {
        return this.ownerId;
    }
    
    public void setInstruction(final String n) {
        this.instruction = n;
        this.instruction = this.instruction.substring(0, Math.min(this.instruction.length(), 400));
    }
    
    public void setName(final String n) {
        this.name = n;
        this.name = this.name.substring(0, Math.min(this.name.length(), 100));
    }
    
    @Nullable
    public void setGroupName(final String gn) {
        if (gn == null) {
            this.groupName = "";
        }
        else {
            this.groupName = gn.substring(0, Math.min(gn.length(), 20));
        }
    }
    
    public void setMissionCreatorName(final String cn) {
        this.missionCreatorName = cn;
        this.missionCreatorName = this.missionCreatorName.substring(0, Math.min(this.missionCreatorName.length(), 40));
    }
    
    public void setLastModifierName(final String mn) {
        this.lastModifierName = mn;
        this.lastModifierName = this.lastModifierName.substring(0, Math.min(this.lastModifierName.length(), 40));
    }
    
    void setCreatedDate(final String aCreatedDate) {
        this.createdDate = aCreatedDate;
    }
    
    public long getCreatedDate() {
        final DateFormat formatter = DateFormat.getDateInstance(2);
        try {
            final Date date = formatter.parse(this.createdDate);
            return date.getTime();
        }
        catch (ParseException e) {
            Mission.logger.log(Level.WARNING, e.getMessage(), e);
            return this.lastModifiedDate.getTime();
        }
    }
    
    void setLastModifiedDate(final Timestamp aLastModifiedDate) {
        this.lastModifiedDate = aLastModifiedDate;
    }
    
    public long getLastModifiedAsLong() {
        return this.lastModifiedDate.getTime();
    }
    
    public String getLastModifiedDate() {
        if (this.lastModifiedDate != null) {
            return DateFormat.getDateInstance(2).format(this.lastModifiedDate);
        }
        return "";
    }
    
    public final boolean hasTargetOf(final long currentTargetId, final Creature performer) {
        final MissionTrigger[] triggers = MissionTriggers.getAllTriggers();
        if ((this.getCreatorType() != 2 || performer.getPower() >= 5) && (performer.getPower() > 0 || this.getMissionCreatorName().equals(performer.getName()))) {
            for (final MissionTrigger mt : triggers) {
                if (mt.getMissionRequired() == this.id && mt.getTarget() == currentTargetId) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE MISSIONS SET NAME=?,INSTRUCTION=?,INACTIVE=?,CREATOR=?,CREATEDDATE=?,LASTMODIFIER=?,MAXTIMESECS=?,SECONDCHANCE=?,MAYBERESTARTED=?,FAILONDEATH=?,GROUP_NAME=?,HIDDEN=? WHERE ID=?");
            ps.setString(1, this.name);
            ps.setString(2, this.instruction);
            ps.setBoolean(3, this.inActive);
            ps.setString(4, this.missionCreatorName);
            ps.setString(5, this.createdDate);
            this.lastModifiedDate = new Timestamp(System.currentTimeMillis());
            ps.setString(6, this.lastModifierName);
            ps.setInt(7, this.maxTimeSeconds);
            ps.setBoolean(8, this.hasSecondChance);
            ps.setBoolean(9, this.mayBeRestarted);
            ps.setBoolean(10, this.failOnDeath);
            ps.setString(11, this.groupName);
            ps.setBoolean(12, this.isHidden);
            ps.setInt(13, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Mission.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MISSIONS (NAME,INSTRUCTION,INACTIVE,CREATOR,CREATEDDATE,LASTMODIFIER,MAXTIMESECS,SECONDCHANCE,MAYBERESTARTED,CREATORID,CREATORTYPE,FAILONDEATH,GROUP_NAME,HIDDEN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
            ps.setString(1, this.name);
            ps.setString(2, this.instruction);
            ps.setBoolean(3, this.inActive);
            ps.setString(4, this.missionCreatorName);
            this.createdDate = DateFormat.getDateInstance(2).format(new Timestamp(System.currentTimeMillis()));
            this.lastModifiedDate = new Timestamp(System.currentTimeMillis());
            ps.setString(5, this.createdDate);
            ps.setString(6, this.lastModifierName);
            ps.setInt(7, this.maxTimeSeconds);
            ps.setBoolean(8, this.hasSecondChance);
            ps.setBoolean(9, this.mayBeRestarted);
            ps.setLong(10, this.ownerId);
            ps.setByte(11, this.creatorType);
            ps.setBoolean(12, this.failOnDeath);
            ps.setString(13, this.groupName);
            ps.setBoolean(14, this.isHidden);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
            Mission.logger.log(Level.INFO, "Mission " + this.name + " (" + this.id + ") created at " + this.createdDate);
        }
        catch (SQLException sqx) {
            Mission.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void destroy() {
        Missions.removeMission(this.id);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MISSIONS WHERE ID=?");
            ps.setInt(1, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Mission.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public int compareTo(final Mission aMission) {
        return this.getName().compareTo(aMission.getName());
    }
    
    @Override
    public String toString() {
        return "Mission [createdDate=" + this.createdDate + ", creatorType=" + this.creatorType + ", hasSecondChance=" + this.hasSecondChance + ", id=" + this.id + ", inActive=" + this.inActive + ", instruction=" + this.instruction + ", lastModifiedDate=" + this.lastModifiedDate + ", lastModifierName=" + this.lastModifierName + ", maxTimeSeconds=" + this.maxTimeSeconds + ", mayBeRestarted=" + this.mayBeRestarted + ", missionCreatorName=" + this.missionCreatorName + ", name=" + this.name + ", ownerId=" + this.ownerId + "]";
    }
    
    public String getRewards() {
        final EpicMission em = EpicServerStatus.getEpicMissionForMission(this.id);
        if (em != null) {
            return em.getRewards();
        }
        return "Unknown";
    }
    
    public byte getDifficulty() {
        final EpicMission em = EpicServerStatus.getEpicMissionForMission(this.id);
        if (em != null) {
            return (byte)em.getDifficulty();
        }
        return -10;
    }
    
    public MissionTrigger[] getTriggers() {
        return MissionTriggers.getMissionTriggers(this.id);
    }
    
    public boolean hasTriggers() {
        return MissionTriggers.hasMissionTriggers(this.id);
    }
    
    static {
        logger = Logger.getLogger(Mission.class.getName());
    }
}

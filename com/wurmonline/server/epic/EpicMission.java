// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import com.wurmonline.server.Players;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.CreationEntry;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.Servers;
import com.wurmonline.server.items.CreationMatrix;
import com.wurmonline.server.tutorial.MissionTriggers;
import java.util.logging.Logger;

public class EpicMission
{
    private static final Logger logger;
    private final int epicEntityId;
    private final int epicScenarioId;
    private final String epicEntityName;
    private final String epicScenarioName;
    private final int missionId;
    private final int serverId;
    private final byte missionType;
    private final int difficulty;
    private long expireTime;
    private final String rewards;
    private long endTime;
    private float missionProgress;
    private boolean current;
    private static final String SAVE_EPIC_MISSION_ENTITY = "INSERT INTO EPICMISSIONS(NAME,SCENARIONAME,MISSION,SCENARIO,TSTAMP,ENTITY,SERVERID,PROGRESS,CURRENT,MISSIONTYPE,DIFFICULTY) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_EPIC_MISSION_ENTITY = "UPDATE EPICMISSIONS SET TSTAMP=?,PROGRESS=?,CURRENT=?,ENDTIME=?,MISSIONTYPE=?,DIFFICULTY=? WHERE MISSION=? AND ENTITY=? AND SERVERID=?";
    private static final String DELETE_EPIC_MISSION = "DELETE FROM EPICMISSIONS WHERE MISSION=? AND ENTITY=?";
    
    public EpicMission(final int entityid, final int scenarioId, final String name, final String scenarioName, final int mission, final byte missionType, final int difficulty, final float progress, final int server, final long time, final boolean load, final boolean isCurrent) {
        this.endTime = 0L;
        this.missionProgress = 0.0f;
        this.current = false;
        this.epicEntityId = entityid;
        this.epicScenarioId = scenarioId;
        this.epicScenarioName = scenarioName;
        this.missionId = mission;
        this.missionType = missionType;
        this.difficulty = difficulty;
        this.rewards = this.generateRewardsString();
        this.serverId = server;
        this.expireTime = time;
        this.epicEntityName = name;
        this.missionProgress = progress;
        this.current = isCurrent;
        if (!load) {
            this.save();
        }
    }
    
    private String generateRewardsString() {
        final StringBuilder sb = new StringBuilder();
        final EpicMissionEnum missionEnum = EpicMissionEnum.getMissionForType(this.getMissionType());
        if (missionEnum == null) {
            return "Unknown";
        }
        final int karmaPerPart = missionEnum.getBaseKarma();
        final int karmaSplit = missionEnum.getKarmaBonusDiffMult() * this.getDifficulty();
        final int sleepPerPart = missionEnum.getBaseSleep();
        final int sleepSplit = missionEnum.getSleepBonusDiffMult() * this.getDifficulty();
        int numReq = EpicServerStatus.getNumberRequired(this.getDifficulty(), missionEnum);
        if (karmaPerPart > 0) {
            sb.append(karmaPerPart + " karma " + ((sleepPerPart > 0) ? "and " : "to"));
        }
        if (sleepPerPart > 0) {
            sb.append(sleepPerPart + "m sleep bonus for");
        }
        if (numReq > 1) {
            sb.append(" each participant upon completion of this mission");
        }
        else {
            sb.append(" the person who completes this mission");
        }
        final boolean creature = EpicMissionEnum.isMissionCreature(missionEnum);
        final boolean item = EpicMissionEnum.isMissionItem(missionEnum);
        final MissionTrigger[] triggers = MissionTriggers.getMissionTriggers(this.getMissionId());
        if (EpicMissionEnum.isNumReqItemEffected(missionEnum) && triggers.length > 0) {
            final int templateId = triggers[0].getItemUsedId();
            final CreationEntry entry = CreationMatrix.getInstance().getCreationEntry(templateId);
            if (entry != null) {
                numReq /= Math.min(100, entry.getTotalNumberOfItems());
            }
        }
        numReq = Math.max(1, numReq);
        if (karmaSplit > 0) {
            if (missionEnum.isKarmaMultProgress()) {
                sb.append(" as well as " + (int)Math.ceil(karmaSplit / numReq) + " karma per " + (creature ? "creature" : (item ? "item" : "action")));
            }
            else if (!EpicMissionEnum.isKarmaSplitNearby(missionEnum)) {
                sb.append(" as well as " + karmaSplit + " karma split between participants");
            }
            else {
                sb.append(" as well as " + karmaSplit + " karma split between nearby players");
            }
        }
        if (sleepSplit > 0) {
            if (missionEnum.isSleepMultNearby()) {
                sb.append(" with " + sleepSplit + "m extra sleep bonus split between nearby players upon completion (30m max each)");
            }
            else {
                sb.append(" with " + sleepSplit + "m extra sleep bonus split between participants upon completion (30m max each)");
            }
        }
        if (Servers.isThisAnEpicServer()) {
            sb.append(". The movement timer for " + Deities.getEntityName(this.epicEntityId) + " will also be reduced by " + EpicMissionEnum.getTimeReductionForMission(this.getMissionType(), this.getDifficulty()) / 3600000L + " hours.");
        }
        return sb.toString();
    }
    
    private final void save() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("INSERT INTO EPICMISSIONS(NAME,SCENARIONAME,MISSION,SCENARIO,TSTAMP,ENTITY,SERVERID,PROGRESS,CURRENT,MISSIONTYPE,DIFFICULTY) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, this.epicEntityName);
            ps.setString(2, this.epicScenarioName);
            ps.setInt(3, this.missionId);
            ps.setInt(4, this.epicScenarioId);
            ps.setLong(5, this.expireTime);
            ps.setInt(6, this.epicEntityId);
            ps.setInt(7, this.serverId);
            ps.setFloat(8, this.missionProgress);
            ps.setBoolean(9, this.current);
            ps.setByte(10, this.missionType);
            ps.setInt(11, this.difficulty);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicMission.logger.log(Level.WARNING, "Failed to save epic mission status.", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    final void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("UPDATE EPICMISSIONS SET TSTAMP=?,PROGRESS=?,CURRENT=?,ENDTIME=?,MISSIONTYPE=?,DIFFICULTY=? WHERE MISSION=? AND ENTITY=? AND SERVERID=?");
            ps.setLong(1, this.expireTime);
            ps.setFloat(2, this.missionProgress);
            ps.setBoolean(3, this.current);
            ps.setLong(4, this.endTime);
            ps.setByte(5, this.missionType);
            ps.setInt(6, this.difficulty);
            ps.setInt(7, this.missionId);
            ps.setInt(8, this.epicEntityId);
            ps.setInt(9, this.serverId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicMission.logger.log(Level.WARNING, "Failed to update epic mission progress.", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    final void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("DELETE FROM EPICMISSIONS WHERE MISSION=? AND ENTITY=?");
            ps.setInt(1, this.missionId);
            ps.setInt(2, this.epicEntityId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EpicMission.logger.log(Level.WARNING, "Failed to delete epic mission.", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void updateProgress(final float newProgress) {
        this.missionProgress = newProgress;
        if (this.missionProgress >= 100.0f) {
            this.endTime = System.currentTimeMillis();
        }
        else if (Servers.localServer.LOGINSERVER && this.missionProgress >= this.getServerId()) {
            this.endTime = System.currentTimeMillis();
        }
        this.update();
        Players.getInstance().sendUpdateEpicMission(this);
    }
    
    public final boolean isCompleted() {
        return this.missionProgress >= 100.0f;
    }
    
    public final int getEpicEntityId() {
        return this.epicEntityId;
    }
    
    public final int getMissionId() {
        return this.missionId;
    }
    
    public final int getServerId() {
        return this.serverId;
    }
    
    public final long getExpireTime() {
        return this.expireTime;
    }
    
    public final void setExpireTime(final long newExpireTime) {
        if (this.expireTime != newExpireTime) {
            this.expireTime = newExpireTime;
            this.update();
            if (!Servers.localServer.LOGINSERVER) {
                Players.getInstance().sendUpdateEpicMission(this);
            }
        }
    }
    
    public final long getEndTime() {
        return this.endTime;
    }
    
    public final float getMissionProgress() {
        return this.missionProgress;
    }
    
    public final String getEntityName() {
        return this.epicEntityName;
    }
    
    public final String getScenarioName() {
        return this.epicScenarioName;
    }
    
    public void setCurrent(final boolean isCurrent) {
        this.current = isCurrent;
        this.update();
    }
    
    public final boolean isCurrent() {
        return this.current;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.epicEntityId;
        result = 31 * result + this.missionId;
        result = 31 * result + this.serverId;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EpicMission)) {
            return false;
        }
        final EpicMission other = (EpicMission)obj;
        return this.epicEntityId == other.epicEntityId && this.missionId == other.missionId && this.serverId == other.serverId;
    }
    
    @Override
    public String toString() {
        final StringBuilder lBuilder = new StringBuilder();
        lBuilder.append("EpicMission [epicEntityId=").append(this.epicEntityId);
        lBuilder.append(", epicScenarioId=").append(this.epicScenarioId);
        lBuilder.append(", epicEntityName=").append(this.epicEntityName);
        lBuilder.append(", epicScenarioName=").append(this.epicScenarioName);
        lBuilder.append(", missionId=").append(this.missionId);
        lBuilder.append(", serverId=").append(this.serverId);
        lBuilder.append(", timeStamp=").append(this.expireTime);
        lBuilder.append(", endTime=").append(this.endTime);
        lBuilder.append(", missionProgress=").append(this.missionProgress);
        lBuilder.append(", difficulty=").append(this.difficulty);
        lBuilder.append(", current=").append(this.current);
        lBuilder.append(", missionType=").append(this.missionType);
        lBuilder.append(']');
        return lBuilder.toString();
    }
    
    public byte getMissionType() {
        return this.missionType;
    }
    
    public int getDifficulty() {
        return this.difficulty;
    }
    
    public String getRewards() {
        return this.rewards;
    }
    
    static {
        logger = Logger.getLogger(EpicMission.class.getName());
    }
}

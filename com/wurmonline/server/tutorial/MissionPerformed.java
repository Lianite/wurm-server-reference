// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.math.BigInteger;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CounterTypes;

public final class MissionPerformed implements CounterTypes
{
    private static Logger logger;
    private static final Map<Long, MissionPerformer> missionsPerformers;
    private static final String LOADALLMISSIONSPERFORMER = "SELECT * FROM MISSIONSPERFORMED";
    private static final String ADDMISSIONSPERFORMED = "INSERT INTO MISSIONSPERFORMED (PERFORMER,MISSION,STATE,STARTTIME) VALUES(?,?,?,?)";
    private static final String DELETEALLMISSIONSPERFORMER = "DELETE FROM MISSIONSPERFORMED WHERE PERFORMER=?";
    private static final String UPDATESTATE = "UPDATE MISSIONSPERFORMED SET STATE=? WHERE MISSION=? AND PERFORMER=?";
    private static final String SETINACTIVATED = "UPDATE MISSIONSPERFORMED SET INACTIVE=? WHERE MISSION=? AND PERFORMER=?";
    private static final String RESTARTMISSION = "UPDATE MISSIONSPERFORMED SET STARTTIME=?,FINISHEDDATE=? WHERE MISSION=? AND PERFORMER=?";
    private static final String UPDATEFINISHEDDATE = "UPDATE MISSIONSPERFORMED SET FINISHEDDATE=?, ENDTIME=? WHERE MISSION=? AND PERFORMER=?";
    public static final float FINISHED = 100.0f;
    public static final float NOTSTARTED = 0.0f;
    public static final float STARTED = 1.0f;
    public static final float FAILED = -1.0f;
    public static final float SOME_COMPLETED = 33.0f;
    private final int mission;
    private float state;
    private long startTime;
    private long endTime;
    private String endDate;
    private boolean inactive;
    private final long wurmid;
    private final MissionPerformer performer;
    private static long tempMissionPerformedCounter;
    
    public MissionPerformed(final int missionId, final MissionPerformer perf) {
        this.state = 0.0f;
        this.startTime = 0L;
        this.endTime = 0L;
        this.endDate = "";
        this.inactive = false;
        this.mission = missionId;
        this.wurmid = generateWurmId(this.mission);
        this.performer = perf;
    }
    
    private static long generateWurmId(final int mission) {
        ++MissionPerformed.tempMissionPerformedCounter;
        return BigInteger.valueOf(MissionPerformed.tempMissionPerformedCounter).shiftLeft(24).longValue() + (mission << 8) + 22L;
    }
    
    public static int decodeMissionId(final long wurmId) {
        return (int)(wurmId >> 8 & -1L);
    }
    
    public long getWurmId() {
        return this.wurmid;
    }
    
    public int getMissionId() {
        return this.mission;
    }
    
    public Mission getMission() {
        return Missions.getMissionWithId(this.mission);
    }
    
    public float getState() {
        return this.state;
    }
    
    public boolean isInactivated() {
        return this.inactive;
    }
    
    public boolean isCompleted() {
        return this.state == 100.0f;
    }
    
    public boolean isFailed() {
        return this.state == -1.0f;
    }
    
    public boolean isStarted() {
        return this.state >= 1.0f;
    }
    
    public long getStartTimeMillis() {
        return this.startTime;
    }
    
    protected String getStartDate() {
        return DateFormat.getDateInstance(1).format(new Timestamp(this.startTime));
    }
    
    protected String getLastTimeToFinish(final int maxSecondsToFinish) {
        return DateFormat.getDateInstance(1).format(new Timestamp(this.startTime + maxSecondsToFinish * 1000));
    }
    
    protected long getFinishTimeAsLong(final int maxSecondsToFinish) {
        return this.startTime + maxSecondsToFinish * 1000;
    }
    
    protected long getStartTime() {
        return this.startTime;
    }
    
    String getEndDate() {
        return this.endDate;
    }
    
    long getEndTime() {
        return this.endTime;
    }
    
    public static MissionPerformer getMissionPerformer(final long id) {
        return MissionPerformed.missionsPerformers.get(id);
    }
    
    public static MissionPerformer[] getAllPerformers() {
        return MissionPerformed.missionsPerformers.values().toArray(new MissionPerformer[MissionPerformed.missionsPerformers.size()]);
    }
    
    public void setInactive(final boolean inactivate) {
        if (this.inactive != inactivate) {
            this.inactive = inactivate;
            PreparedStatement ps = null;
            try {
                final Connection dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("UPDATE MISSIONSPERFORMED SET INACTIVE=? WHERE MISSION=? AND PERFORMER=?");
                ps.setBoolean(1, this.inactive);
                ps.setLong(2, this.performer.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            this.sendUpdate();
        }
    }
    
    private void sendUpdate() {
        if (this.performer != null) {
            this.performer.sendUpdatePerformer(this);
        }
    }
    
    public static void deleteMissionPerformer(final long id) {
        MissionPerformed.missionsPerformers.remove(id);
        PreparedStatement ps = null;
        try {
            final Connection dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MISSIONSPERFORMED WHERE PERFORMER=?");
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public boolean setState(final float newState, final long aPerformer) {
        if (this.state != newState) {
            this.state = newState;
            if (this.state > 100.0f) {
                this.state = 100.0f;
            }
            if (this.state < -1.0f) {
                this.state = -1.0f;
            }
            PreparedStatement ps = null;
            try {
                final Connection dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("UPDATE MISSIONSPERFORMED SET STATE=? WHERE MISSION=? AND PERFORMER=?");
                ps.setFloat(1, this.state);
                ps.setInt(2, this.mission);
                ps.setLong(3, aPerformer);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            if (this.state >= 100.0f || this.state <= -1.0f) {
                this.setFinishDate(DateFormat.getDateInstance(1).format(new Timestamp(System.currentTimeMillis())), aPerformer);
            }
            if (this.state == 1.0f) {
                this.restartMission(this.performer.getWurmId());
            }
            this.sendUpdate();
        }
        return this.state >= 100.0f || this.state <= -1.0f;
    }
    
    private void setFinishDate(final String date, final long aPerformer) {
        this.endDate = date;
        PreparedStatement ps = null;
        try {
            final Connection dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE MISSIONSPERFORMED SET FINISHEDDATE=?, ENDTIME=? WHERE MISSION=? AND PERFORMER=?");
            ps.setString(1, date);
            ps.setLong(2, this.endTime = System.currentTimeMillis());
            ps.setInt(3, this.mission);
            ps.setLong(4, aPerformer);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static MissionPerformer startNewMission(final int mission, final long performerId, final float state) {
        MissionPerformer mp = MissionPerformed.missionsPerformers.get(performerId);
        if (mp == null) {
            mp = new MissionPerformer(performerId);
            MissionPerformed.missionsPerformers.put(performerId, mp);
        }
        final MissionPerformed mpf = new MissionPerformed(mission, mp);
        mpf.state = state;
        mpf.startTime = System.currentTimeMillis();
        mp.addMissionPerformed(mpf);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MISSIONSPERFORMED (PERFORMER,MISSION,STATE,STARTTIME) VALUES(?,?,?,?)");
            ps.setLong(1, performerId);
            ps.setInt(2, mission);
            ps.setFloat(3, state);
            ps.setLong(4, mpf.startTime);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (state == 100.0f) {
            mpf.setFinishDate(DateFormat.getDateInstance(1).format(new Timestamp(System.currentTimeMillis())), performerId);
        }
        mpf.sendUpdate();
        return mp;
    }
    
    private void restartMission(final long aPerformer) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.startTime = System.currentTimeMillis();
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE MISSIONSPERFORMED SET STARTTIME=?,FINISHEDDATE=? WHERE MISSION=? AND PERFORMER=?");
            ps.setLong(1, this.startTime);
            ps.setLong(2, this.endTime);
            ps.setInt(3, this.mission);
            ps.setLong(4, aPerformer);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void loadAllMissionsPerformed() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MISSIONSPERFORMED");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long performer = rs.getLong("PERFORMER");
                MissionPerformer mp = MissionPerformed.missionsPerformers.get(performer);
                if (mp == null) {
                    mp = new MissionPerformer(performer);
                    MissionPerformed.missionsPerformers.put(performer, mp);
                }
                final MissionPerformed mpf = new MissionPerformed(rs.getInt("MISSION"), mp);
                mpf.state = rs.getInt("STATE");
                mpf.startTime = rs.getLong("STARTTIME");
                mpf.endDate = rs.getString("FINISHEDDATE");
                mpf.endTime = rs.getLong("ENDTIME");
                mp.addMissionPerformed(mpf);
            }
        }
        catch (SQLException sqx) {
            MissionPerformed.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        MissionPerformed.logger = Logger.getLogger(MissionPerformed.class.getName());
        missionsPerformers = new HashMap<Long, MissionPerformer>();
        MissionPerformed.tempMissionPerformedCounter = 0L;
        try {
            loadAllMissionsPerformed();
        }
        catch (Exception ex) {
            MissionPerformed.logger.log(Level.WARNING, "Problems loading Missions Performed", ex);
        }
    }
}

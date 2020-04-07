// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public final class MissionPerformer implements MiscConstants
{
    private final Map<Integer, MissionPerformed> missionsPerformed;
    private static final Logger logger;
    private final long wurmid;
    
    public MissionPerformer(final long performerId) {
        this.missionsPerformed = new HashMap<Integer, MissionPerformed>();
        this.wurmid = performerId;
    }
    
    public long getWurmId() {
        return this.wurmid;
    }
    
    void addMissionPerformed(final MissionPerformed mp) {
        this.missionsPerformed.put(mp.getMissionId(), mp);
    }
    
    public MissionPerformed getMission(final int mission) {
        return this.missionsPerformed.get(mission);
    }
    
    public MissionPerformed[] getAllMissionsPerformed() {
        return this.missionsPerformed.values().toArray(new MissionPerformed[this.missionsPerformed.values().size()]);
    }
    
    public MissionPerformed getMissionByWurmId(final long wurmId) {
        for (final MissionPerformed mp : this.missionsPerformed.values()) {
            if (mp.getWurmId() == wurmId) {
                return mp;
            }
        }
        return null;
    }
    
    boolean isMissionCompleted(final int mission) {
        return this.isMissionPerformed(mission, 100.0f);
    }
    
    boolean isMissionPerformed(final int mission, final float state) {
        final MissionPerformed m = this.missionsPerformed.get(mission);
        return m != null && m.getState() >= state;
    }
    
    public static final void sendEpicMissionsPerformed(final Creature creature, final Communicator comm) {
        EpicMission[] em = null;
        if (!Servers.localServer.PVPSERVER) {
            em = EpicServerStatus.getCurrentEpicMissions();
            for (int x = 0; x < em.length; ++x) {
                if (em[x].isCurrent()) {
                    sendEpicMission(em[x], comm);
                }
            }
        }
        else {
            em = EpicServerStatus.getCurrentEpicMissions();
            if (em != null) {
                for (int x = 0; x < em.length; ++x) {
                    sendEpicMissionPvPServer(em[x], creature, comm);
                }
            }
        }
    }
    
    public static final void sendEpicMissionPvPServer(final EpicMission mission, final Creature creature, final Communicator comm) {
        final Mission mis = Missions.getMissionWithId(mission.getMissionId());
        if (mis != null && (!mis.isHidden() || comm.player.getPower() >= 2)) {
            if (Deities.getFavoredKingdom(mission.getEpicEntityId()) == creature.getKingdomTemplateId()) {
                String name = "Everyone: " + mis.getName();
                if (comm.player.getPower() >= 2) {
                    name = "(" + mis.getId() + ") " + name;
                }
                if (mis.isHidden()) {
                    name = "[hidden] " + name;
                }
                comm.sendMissionState(-mission.getMissionId(), name, mis.getInstruction(), mis.getMissionCreatorName(), mission.getMissionProgress(), mis.getLastModifiedAsLong(), mission.getEndTime(), mission.getExpireTime(), false, mis.getDifficulty(), mis.getRewards());
            }
            else {
                String name = "Enemy: " + mis.getName();
                if (comm.player.getPower() >= 2) {
                    name = "(" + mis.getId() + ") " + name;
                }
                if (mis.isHidden()) {
                    name = "[hidden] " + name;
                }
                comm.sendMissionState(-mission.getMissionId(), name, mis.getInstruction(), mis.getMissionCreatorName(), mission.getMissionProgress(), mis.getLastModifiedAsLong(), mission.getEndTime(), mission.getExpireTime(), false, mis.getDifficulty(), mis.getRewards());
            }
        }
    }
    
    public static final void sendEpicMission(final EpicMission mission, final Communicator comm) {
        final Mission mis = Missions.getMissionWithId(mission.getMissionId());
        if (mis != null && (!mis.isHidden() || comm.player.getPower() >= 2)) {
            String name = "Everyone: " + mis.getName();
            if (comm.player.getPower() >= 2) {
                name = "(" + mis.getId() + ") " + name;
            }
            if (mis.isHidden()) {
                name = "[hidden] " + name;
            }
            comm.sendMissionState(-mission.getMissionId(), name, mis.getInstruction(), mis.getMissionCreatorName(), mission.getMissionProgress(), mis.getLastModifiedAsLong(), mission.getEndTime(), mission.getExpireTime(), false, mis.getDifficulty(), mis.getRewards());
        }
    }
    
    public void sendAllMissionPerformed(final Communicator comm) {
        if (comm != null) {
            for (final MissionPerformed mp : this.missionsPerformed.values()) {
                if ((mp.getState() <= -1.0f || mp.getState() >= 100.0f) && mp.getEndTime() < System.currentTimeMillis() - 7257600000L) {
                    continue;
                }
                if (mp.isInactivated()) {
                    continue;
                }
                final Mission mis = Missions.getMissionWithId(mp.getMissionId());
                if (mis != null) {
                    if (mis.isHidden() && comm.player.getPower() < 2) {
                        continue;
                    }
                    String name = mis.getName();
                    if (comm.player.getPower() >= 2) {
                        name = "(" + mis.getId() + ") " + name;
                    }
                    if (mis.isHidden()) {
                        name = "[hidden] " + name;
                    }
                    comm.sendMissionState(mp.getWurmId(), name, mis.getInstruction(), mis.getMissionCreatorName(), mp.getState(), mp.getStartTime(), mp.getEndTime(), mp.getFinishTimeAsLong(mis.getMaxTimeSeconds()), mis.hasSecondChance(), mis.getDifficulty(), mis.getRewards());
                }
                else {
                    comm.sendMissionState(mp.getWurmId(), "Unknown", "N/A", "N/A", mp.getState(), mp.getStartTime(), mp.getEndTime(), 0L, false, (byte)(-10), "Unknown");
                }
            }
        }
        else {
            MissionPerformer.logger.warning("Could not send Mission state as Communicator was null, MissionPerformer: " + this);
        }
    }
    
    void sendUpdatePerformer(final MissionPerformed mp) {
        try {
            final Creature perf = Server.getInstance().getCreature(this.wurmid);
            if (mp.isInactivated()) {
                perf.getCommunicator().sendRemoveMissionState(mp.getWurmId());
            }
            else {
                final Mission mis = Missions.getMissionWithId(mp.getMissionId());
                String name = "Unknown";
                String instruction = "N/A";
                String creatorName = "N/A";
                long maxTime = 0L;
                boolean mayBeRestarted = false;
                byte difficulty = -10;
                String rewards = "Unknown";
                if (mis != null) {
                    if (mis.isHidden() && perf.getPower() == 0) {
                        return;
                    }
                    name = mis.getName();
                    if (perf.getPower() >= 2) {
                        name = "(" + mis.getId() + ") " + name;
                    }
                    if (mis.isHidden()) {
                        name = "[hidden] " + name;
                    }
                    instruction = mis.getInstruction();
                    creatorName = mis.getMissionCreatorName();
                    maxTime = mp.getFinishTimeAsLong(mis.getMaxTimeSeconds());
                    mayBeRestarted = mis.hasSecondChance();
                    difficulty = mis.getDifficulty();
                    rewards = mis.getRewards();
                }
                perf.getCommunicator().sendMissionState(mp.getWurmId(), name, instruction, creatorName, mp.getState(), mp.getStartTime(), mp.getEndTime(), maxTime, mayBeRestarted, difficulty, rewards);
            }
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
    }
    
    @Override
    public String toString() {
        return "MissionPerformer [Number of missions performed=" + this.missionsPerformed.size() + ", performer wurmid=" + this.wurmid + "]";
    }
    
    static {
        logger = Logger.getLogger(MissionPerformer.class.getName());
    }
}

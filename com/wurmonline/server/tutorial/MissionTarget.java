// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.Iterator;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.Set;

public final class MissionTarget
{
    private final Set<MissionTrigger> missionTriggers;
    private static final Logger logger;
    private final long id;
    
    MissionTarget(final long targetId) {
        this.missionTriggers = new HashSet<MissionTrigger>();
        this.id = targetId;
    }
    
    long getId() {
        return this.id;
    }
    
    void addMissionTrigger(final MissionTrigger missionReqs) {
        if (missionReqs != null) {
            this.missionTriggers.add(missionReqs);
        }
    }
    
    void removeMissionTrigger(final MissionTrigger missionReqs) {
        this.missionTriggers.remove(missionReqs);
    }
    
    int getNumTriggers() {
        return this.missionTriggers.size();
    }
    
    private MissionTrigger getMissionTrigger(final int mission, final int state, final boolean checkActive) {
        for (final MissionTrigger mr : this.missionTriggers) {
            if (mr.getMissionRequired() == mission && mr.isTriggered(state, checkActive)) {
                return mr;
            }
        }
        return null;
    }
    
    public MissionTrigger[] getMissionTriggers() {
        return this.missionTriggers.toArray(new MissionTrigger[this.missionTriggers.size()]);
    }
    
    boolean isMissionFulfilled(final int mission, final byte state) {
        for (final MissionTrigger mr : this.missionTriggers) {
            if (mr.getMissionRequired() == mission && mr.getStateRequired() == state) {
                return true;
            }
        }
        return false;
    }
    
    void destroy() {
        MissionTriggers.destroyTriggersForTarget(this.id);
    }
    
    @Override
    public String toString() {
        return "MissionTarget [id=" + this.id + ", missionTriggers=" + this.missionTriggers + ", numTriggers=" + this.getNumTriggers() + "]";
    }
    
    static {
        logger = Logger.getLogger(MissionTarget.class.getName());
    }
}

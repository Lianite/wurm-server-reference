// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import java.util.HashMap;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.WurmId;
import javax.annotation.Nullable;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.shared.constants.CounterTypes;

public final class MissionTargets implements CounterTypes
{
    private static final Map<Long, MissionTarget> missionTargets;
    private static final Logger logger;
    
    public static void destroyMissionTarget(final long missionTarget, final boolean destroyTriggers) {
        final MissionTarget m = MissionTargets.missionTargets.remove(missionTarget);
        if (m != null && destroyTriggers) {
            m.destroy();
        }
    }
    
    public static boolean isMissionTarget(final long potentialTarget) {
        return MissionTargets.missionTargets.containsKey(potentialTarget);
    }
    
    public static MissionTarget getMissionTargetFor(final long potentialTarget) {
        return MissionTargets.missionTargets.get(potentialTarget);
    }
    
    public static Long[] getTargetIds() {
        return MissionTargets.missionTargets.keySet().toArray(new Long[MissionTargets.missionTargets.size()]);
    }
    
    public static boolean destroyStructureTargets(final long structureId, @Nullable final String possibleCreatorName) {
        boolean found = false;
        final Long[] targetIds;
        final Long[] targs = targetIds = getTargetIds();
        for (final Long tid : targetIds) {
            if (tid != null) {
                final long targetId = tid;
                if (WurmId.getType(targetId) == 5) {
                    final Wall w = Wall.getWall(targetId);
                    if (w != null && w.getStructureId() == structureId) {
                        final MissionTarget mt = getMissionTargetFor(targetId);
                        if (mt != null) {
                            final MissionTrigger[] missionTriggers;
                            final MissionTrigger[] mits = missionTriggers = mt.getMissionTriggers();
                            for (final MissionTrigger missionT : missionTriggers) {
                                if (possibleCreatorName == null || missionT.getCreatorName().toLowerCase().equals(possibleCreatorName)) {
                                    found = true;
                                    missionT.destroy();
                                }
                            }
                        }
                    }
                }
            }
        }
        return found;
    }
    
    public static void addMissionTrigger(final MissionTrigger trigger) {
        MissionTarget mt = getMissionTargetFor(trigger.getTarget());
        if (mt == null && trigger.getTarget() > 0L) {
            mt = new MissionTarget(trigger.getTarget());
            MissionTargets.missionTargets.put(trigger.getTarget(), mt);
        }
        if (mt != null) {
            mt.addMissionTrigger(trigger);
        }
    }
    
    public static void removeMissionTrigger(final MissionTrigger trigger, final boolean destroyAllTriggers) {
        if (trigger != null) {
            final MissionTarget mt = getMissionTargetFor(trigger.getTarget());
            if (mt != null) {
                mt.removeMissionTrigger(trigger);
                if (mt.getNumTriggers() == 0) {
                    destroyMissionTarget(mt.getId(), destroyAllTriggers);
                }
            }
        }
    }
    
    static {
        missionTargets = new HashMap<Long, MissionTarget>();
        logger = Logger.getLogger(MissionTargets.class.getName());
    }
}

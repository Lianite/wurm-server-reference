// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.tutorial;

import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.epic.EpicMission;
import java.util.Set;
import java.util.logging.Level;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.Servers;
import com.wurmonline.server.epic.EpicServerStatus;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

final class TriggerRun implements MiscConstants
{
    private static Logger logger;
    private boolean done;
    private boolean triggered;
    private boolean openedDoor;
    private MissionTrigger lastTrigger;
    
    TriggerRun() {
        this.done = false;
        this.triggered = false;
        this.openedDoor = false;
        this.lastTrigger = null;
    }
    
    void run(final Creature performer, final MissionTrigger[] trigs, final int counter) {
        final Set<Integer> doneActions = new HashSet<Integer>();
        this.done = true;
        int maxSeconds = 0;
        if (trigs.length > 0) {
            MissionPerformer mp = MissionPerformed.getMissionPerformer(performer.getWurmId());
            performer.sendToLoggers("Found " + trigs.length + " triggers", (byte)2);
            for (int x = 0; x < trigs.length; ++x) {
                if (!trigs[x].isInactive() && !doneActions.contains(trigs[x].getOnActionPerformed())) {
                    performer.sendToLoggers("Checking " + trigs[x].getName(), (byte)2);
                    if (maxSeconds < trigs[x].getSeconds()) {
                        maxSeconds = trigs[x].getSeconds();
                    }
                    boolean skip = false;
                    final EpicMission mis = EpicServerStatus.getEpicMissionForMission(trigs[x].getMissionRequired());
                    if (mis != null) {
                        if (mis.isCompleted() || !mis.isCurrent()) {
                            skip = true;
                            performer.sendToLoggers("Skipping " + trigs[x].getName() + " because it is not active", (byte)2);
                        }
                        if (!Servers.isThisAChaosServer() && Servers.localServer.PVPSERVER && Deities.getFavoredKingdom(mis.getEpicEntityId()) != performer.getKingdomTemplateId()) {
                            skip = true;
                            performer.sendToLoggers("Skipping " + trigs[x].getName() + " because of kingdom.", (byte)2);
                        }
                    }
                    if (trigs[x].getCreatorType() == 2 && mis != null && mis.isCompleted()) {
                        TriggerRun.logger.log(Level.INFO, "This code shouldn't be REACHED really since MissionPerformers are set to FINISHED at completion.");
                        if (mp != null) {
                            final MissionPerformed mperf = mp.getMission(trigs[x].getMissionRequired());
                            if (mperf != null && mperf.isStarted()) {
                                mperf.setState(100.0f, performer.getWurmId());
                            }
                        }
                    }
                    if (!skip) {
                        if (maxSeconds > 0) {
                            this.done = false;
                        }
                        if (maxSeconds > 0 && counter == 0 && x == trigs.length - 1) {
                            performer.sendActionControl("activating", true, maxSeconds * 10);
                        }
                        if (trigs[x].getSeconds() == counter || (counter == 1 && trigs[x].getSeconds() == 0)) {
                            final Mission mission = Missions.getMissionWithId(trigs[x].getMissionRequired());
                            if (mission != null && !mission.isInactive()) {
                                performer.sendToLoggers("Triggered " + trigs[x].getSeconds() + " counter=" + counter, (byte)2);
                                boolean sendStartPopup = false;
                                if (trigs[x].getStateRequired() == 0.0f) {
                                    if (mp == null) {
                                        mp = MissionPerformed.startNewMission(trigs[x].getMissionRequired(), performer.getWurmId(), 1.0f);
                                        sendStartPopup = true;
                                        performer.sendToLoggers("Starting mission. No existing performer.", (byte)2);
                                    }
                                    else {
                                        final MissionPerformed mperf2 = mp.getMission(trigs[x].getMissionRequired());
                                        if (mperf2 == null) {
                                            MissionPerformed.startNewMission(trigs[x].getMissionRequired(), performer.getWurmId(), 1.0f);
                                            sendStartPopup = true;
                                            performer.sendToLoggers("Starting mission. No existing mission.", (byte)2);
                                        }
                                    }
                                }
                                if (mp != null) {
                                    performer.sendToLoggers("Checking for performer state. Trigger is done=" + this.done, (byte)2);
                                    this.runEffect(performer, mp, trigs[x].getMissionRequired(), trigs[x], sendStartPopup, doneActions);
                                }
                            }
                            else {
                                performer.sendToLoggers("Mission is inactive or null. Trigger is done=" + this.done, (byte)2);
                            }
                        }
                        if (x == trigs.length - 1 && counter == maxSeconds) {
                            this.done = true;
                        }
                    }
                }
            }
        }
    }
    
    public final void runEffect(final Creature performer, final MissionPerformer mp, final int missionId, final MissionTrigger trigger, boolean sendStartPopup, final Set<Integer> doneActions) {
        if (!performer.isPlayer()) {
            return;
        }
        final MissionPerformed mperf = mp.getMission(missionId);
        final Mission mission = Missions.getMissionWithId(missionId);
        if (mperf != null && (mperf.isStarted() || mperf.isFailed())) {
            if (mission != null) {
                final boolean secondChance = (mperf.isFailed() && mission.hasSecondChance()) || (mperf.isCompleted() && mission.mayBeRestarted());
                if (secondChance && trigger.getStateRequired() == 0.0f) {
                    mperf.setState(1.0f, performer.getWurmId());
                    sendStartPopup = true;
                }
                if (!mperf.isFailed()) {
                    performer.sendToLoggers("Mission " + mission.getName() + " is not failed or may be restarted", (byte)2);
                    boolean epicMissionTrigger = false;
                    boolean setEpicFinished = false;
                    final EpicMission mis = EpicServerStatus.getEpicMissionForMission(missionId);
                    if (mis != null && mis.getMissionProgress() < 100.0f && mperf.getState() != 100.0f) {
                        epicMissionTrigger = true;
                        setEpicFinished = !Actions.isMultipleRunEpicAction(trigger.getOnActionPerformed());
                        performer.sendToLoggers("Mission " + mission.getName() + " is epicmissiontrigger. Not finished " + setEpicFinished, (byte)2);
                    }
                    if (mission.getMaxTimeSeconds() > 0 && System.currentTimeMillis() > mperf.getFinishTimeAsLong(mission.getMaxTimeSeconds())) {
                        mperf.setState(-1.0f, performer.getWurmId());
                        final String miss = Server.getTimeFor(System.currentTimeMillis() - mperf.getFinishTimeAsLong(mission.getMaxTimeSeconds()));
                        final SimplePopup pop = new SimplePopup(performer, "Mission failed", "You failed " + mission.getName() + ". You are " + miss + " late.");
                        pop.sendQuestion();
                    }
                    else if ((epicMissionTrigger && trigger.getOnActionPerformed() != 47) || trigger.isTriggered(mperf.getState(), true) || (sendStartPopup && mperf.getState() == 1.0f && trigger.getStateRequired() == 0.0f)) {
                        performer.sendToLoggers("Proper state achieved for mission " + trigger.getMissionRequired(), (byte)2);
                        final TriggerEffect[] eff = TriggerEffects.getEffectsForTrigger(trigger.getId(), true);
                        for (int e = 0; e < eff.length; ++e) {
                            eff[e].effect(performer, mperf, trigger.getTarget(), setEpicFinished, trigger.getOnActionPerformed() != 142);
                            doneActions.add(trigger.getOnActionPerformed());
                            this.triggered = true;
                            if (eff[e].getSpecialEffectId() == 1) {
                                this.openedDoor = true;
                            }
                        }
                        if (this.triggered && trigger.getOnActionPerformed() == 47) {
                            this.lastTrigger = trigger;
                            return;
                        }
                    }
                    else if (epicMissionTrigger && trigger.getOnActionPerformed() == 47) {
                        final SimplePopup pop2 = new SimplePopup(performer, "Already done", "No effect" + mission.getName() + ". You have already shown your respect.");
                        pop2.sendQuestion();
                    }
                    if (sendStartPopup && mission.getInstruction() != null && mission.getInstruction().length() > 0) {
                        if (mperf.getState() != 100.0f) {
                            final SimplePopup pop2 = new SimplePopup(performer, "Mission start", mission.getInstruction());
                            pop2.sendQuestion();
                        }
                        else {
                            performer.sendToLoggers("Not sending mission start popup for mission " + trigger.getMissionRequired() + " since it is already finished.", (byte)2);
                        }
                    }
                }
            }
            else {
                trigger.setInactive(true);
                trigger.update();
                if (performer.getPower() > 0) {
                    performer.getCommunicator().sendNormalServerMessage("The trigger " + trigger.getName() + " was made inactive because it was not connected to any mission.");
                }
            }
        }
    }
    
    boolean isDone() {
        return this.done;
    }
    
    boolean isTriggered() {
        return this.triggered;
    }
    
    boolean isOpenedDoor() {
        return this.openedDoor;
    }
    
    MissionTrigger getLastTrigger() {
        return this.lastTrigger;
    }
    
    static {
        TriggerRun.logger = Logger.getLogger(TriggerRun.class.getName());
    }
}

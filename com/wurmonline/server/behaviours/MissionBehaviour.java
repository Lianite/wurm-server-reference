// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.tutorial.MissionPerformed;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

public class MissionBehaviour extends Behaviour
{
    public MissionBehaviour() {
        super((short)43);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object, final int missionId) {
        return this.getBehavioursFor(performer, missionId);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int missionId) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        toReturn.add(Actions.actionEntrys[16]);
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int missionId, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage("This displays the state of a mission.");
        }
        if (action == 16) {
            final MissionPerformer mp = MissionPerformed.getMissionPerformer(performer.getWurmId());
            final MissionPerformed mpf = mp.getMission(missionId);
            mpf.setInactive(true);
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int missionId, final short action, final float counter) {
        return this.action(act, performer, missionId, action, counter);
    }
}

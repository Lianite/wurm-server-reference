// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.structures.Blocking;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

final class HugeLogBehaviour extends ItemBehaviour
{
    HugeLogBehaviour() {
        super((short)37);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, source, target));
        boolean reachable = false;
        if (target.getOwnerId() == -10L) {
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                final BlockingResult result = Blocking.getBlockerBetween(performer, target, 4);
                if (result == null) {
                    reachable = true;
                }
            }
        }
        else if (target.getOwnerId() == performer.getWurmId()) {
            reachable = true;
        }
        if (reachable && (source.isWeaponAxe() || source.getTemplateId() == 24)) {
            toReturn.add(Actions.actionEntrys[97]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = false;
        boolean reachable = false;
        if (target.getOwnerId() == -10L) {
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                reachable = true;
            }
        }
        else if (target.getOwnerId() == performer.getWurmId()) {
            reachable = true;
        }
        if (reachable) {
            if (action == 97) {
                done = MethodsItems.chop(act, performer, source, target, counter);
            }
            else {
                done = super.action(act, performer, source, target, action, counter);
            }
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
}

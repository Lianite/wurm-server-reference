// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.EntityMoveQuestion;
import com.wurmonline.server.questions.SelectSpellQuestion;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.Servers;
import com.wurmonline.server.structures.Blocking;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

class DomainItemBehaviour extends ItemBehaviour
{
    DomainItemBehaviour() {
        super((short)33);
    }
    
    DomainItemBehaviour(final short type) {
        super(type);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = new ArrayList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, target));
        boolean reachable = true;
        if (target.getOwnerId() == -10L) {
            reachable = false;
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                final BlockingResult result = Blocking.getBlockerBetween(performer, target, 4);
                if (result == null) {
                    reachable = true;
                }
            }
        }
        if (reachable) {
            if (Servers.localServer.EPIC && performer.getDeity() != null) {
                toReturn.add(Actions.actionEntrys[610]);
            }
            toReturn.add(Actions.actionEntrys[141]);
            if (performer.getFaith() >= 10.0f && target.getBless() != null) {
                toReturn.add(Actions.actionEntrys[142]);
                toReturn.add(Actions.actionEntrys[143]);
                if (performer.isPriest()) {
                    toReturn.add(Actions.actionEntrys[452]);
                }
            }
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = new ArrayList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, source, target));
        boolean reachable = true;
        if (target.getOwnerId() == -10L) {
            reachable = false;
            if (performer.isWithinDistanceTo(target.getPosX(), target.getPosY(), target.getPosZ(), 4.0f)) {
                final BlockingResult result = Blocking.getBlockerBetween(performer, target, 4);
                if (result == null) {
                    reachable = true;
                }
            }
        }
        if (reachable) {
            if (Servers.localServer.EPIC && performer.getDeity() != null) {
                toReturn.add(Actions.actionEntrys[610]);
            }
            toReturn.add(Actions.actionEntrys[141]);
            if (performer.getFaith() >= 10.0f) {
                if (target.getBless() != null) {
                    toReturn.add(Actions.actionEntrys[142]);
                    toReturn.add(Actions.actionEntrys[143]);
                }
                if (performer.isPriest()) {
                    toReturn.add(Actions.actionEntrys[452]);
                    if (source.isHolyItem()) {
                        toReturn.add(Actions.actionEntrys[216]);
                    }
                }
            }
            if (source.isRechargeable()) {
                toReturn.add(Actions.actionEntrys[370]);
            }
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 141) {
            done = MethodsReligion.pray(act, performer, target, counter);
        }
        else if (action == 142) {
            done = (performer.getFaith() < 10.0f || MethodsReligion.sacrifice(act, performer, target));
        }
        else if (action == 143) {
            done = (performer.getFaith() < 10.0f || MethodsReligion.desecrate(act, performer, null, target));
        }
        else if (action == 452) {
            if (performer.isPriest()) {
                done = true;
                final SelectSpellQuestion spq = new SelectSpellQuestion(performer, "Spell set", "These are your available spells", target.getWurmId());
                spq.sendQuestion();
            }
        }
        else if (action == 610) {
            done = true;
            if (Servers.localServer.EPIC && performer.getDeity() != null) {
                final EntityMoveQuestion emq = new EntityMoveQuestion(performer);
                emq.sendQuestion();
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = false;
        final boolean reachable = true;
        if (action == 141) {
            done = this.action(act, performer, target, action, counter);
        }
        else if (action == 142) {
            done = (performer.getFaith() < 10.0f || MethodsReligion.sacrifice(act, performer, target));
        }
        else if (action == 143) {
            done = (performer.getFaith() < 10.0f || MethodsReligion.desecrate(act, performer, source, target));
        }
        else if (action == 216) {
            done = true;
            if (performer.isPriest() && source.isHolyItem()) {
                if (target.getParentId() != -10L) {
                    performer.getCommunicator().sendNormalServerMessage("The altar needs to be on the ground to be used.");
                }
                else {
                    done = MethodsReligion.holdSermon(performer, target, source, act, counter);
                }
            }
        }
        else if (action == 370 && source.isRechargeable()) {
            if (target.getParentId() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("The altar needs to be on the ground to be used.");
            }
            else {
                done = MethodsReligion.sendRechargeQuestion(performer, source);
            }
        }
        else if (action == 452) {
            done = this.action(act, performer, target, action, counter);
        }
        else if (action == 610) {
            done = true;
            if (Servers.localServer.EPIC && performer.getDeity() != null) {
                final EntityMoveQuestion emq = new EntityMoveQuestion(performer);
                emq.sendQuestion();
            }
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
}

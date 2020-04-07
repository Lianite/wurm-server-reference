// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

final class ExamineBehaviour extends Behaviour
{
    ExamineBehaviour() {
        super((short)11);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Creature object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item subject, final Creature object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(Actions.actionEntrys[1]);
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage("You see a part of the lands of Wurm.");
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage("You see a part of the lands of Wurm.");
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine(performer));
            target.sendEnchantmentStrings(performer.getCommunicator());
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine(performer));
            target.sendEnchantmentStrings(performer.getCommunicator());
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Creature target, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine());
            target.getCommunicator().sendNormalServerMessage(source.getName() + " takes a long, good look at you.");
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Creature target, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine());
            target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " takes a long, good look at you.");
        }
        return true;
    }
}

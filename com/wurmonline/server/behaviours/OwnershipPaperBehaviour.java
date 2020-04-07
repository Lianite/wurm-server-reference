// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class OwnershipPaperBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    OwnershipPaperBehaviour() {
        super((short)52);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.addAll(this.getBehavioursForPaper());
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.addAll(this.getBehavioursForPaper());
        return toReturn;
    }
    
    List<ActionEntry> getBehavioursForPaper() {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.add(new ActionEntry((short)17, "Read paper", "Reading"));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 1 && target.getTemplateId() == 1000) {
            performer.getCommunicator().sendNormalServerMessage("This is the writ of ownership. It can be traded with another player to transfer ownership.");
        }
        else if (action != 17) {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(OwnershipPaperBehaviour.class.getName());
    }
}

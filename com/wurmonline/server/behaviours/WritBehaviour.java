// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.Structures;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class WritBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    WritBehaviour() {
        super((short)21);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        toReturn.add(Actions.actionEntrys[62]);
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        toReturn.add(Actions.actionEntrys[62]);
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 1) {
            if (target.getTemplateId() == 166) {
                try {
                    final Structure structure = Structures.getStructureForWrit(target.getWurmId());
                    structure.setWritid(-10L, true);
                    performer.getCommunicator().sendNormalServerMessage("The new permissions system does not use writs. Deleting.");
                    Items.destroyItem(target.getWurmId());
                }
                catch (NoSuchStructureException nss) {
                    performer.getCommunicator().sendNormalServerMessage("The structure for this writ does no exist. Deleting.");
                    Items.destroyItem(target.getWurmId());
                }
            }
        }
        else if (action == 62) {
            if (target.getOwnerId() == performer.getWurmId()) {
                try {
                    final Structure structure = Structures.getStructureForWrit(target.getWurmId());
                    structure.setWritid(-10L, true);
                    performer.getCommunicator().sendNormalServerMessage("The new permissions system does not use writs. Deleting.");
                    Items.destroyItem(target.getWurmId());
                }
                catch (NoSuchStructureException nss) {
                    performer.getCommunicator().sendNormalServerMessage("The structure for this writ does no exist. Destroying.");
                    Items.destroyItem(target.getWurmId());
                }
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(WritBehaviour.class.getName());
    }
}

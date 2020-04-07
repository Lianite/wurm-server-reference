// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import java.util.logging.Level;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class VegetableBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    VegetableBehaviour() {
        super((short)16);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, object));
        if (object.getOwnerId() == performer.getWurmId()) {
            if (object.isCrushable()) {
                toReturn.add(Actions.actionEntrys[54]);
            }
            if (object.hasSeeds()) {
                toReturn.add(Actions.actionEntrys[55]);
            }
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, source, object));
        if (object.getOwnerId() == performer.getWurmId()) {
            if (object.isCrushable()) {
                toReturn.add(Actions.actionEntrys[54]);
            }
            if (object.hasSeeds()) {
                toReturn.add(Actions.actionEntrys[55]);
            }
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 54 || action == 55) {
            done = this.action(act, performer, target, action, counter);
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        int nums = -1;
        if (action == 54) {
            if (target.getOwnerId() != performer.getWurmId()) {
                performer.getCommunicator().sendNormalServerMessage("You can't crush that now.");
                return true;
            }
            if (target.isProcessedFood()) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is processed and cannot be crushed.");
                return true;
            }
            final int makes = target.getTemplate().getCrushsTo();
            if (makes > 0) {
                nums = this.crush(action, performer, target, makes);
            }
            if (nums > 0) {
                performer.getCommunicator().sendNormalServerMessage("You crush the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " crushes " + target.getNameWithGenus() + ".", performer, Math.max(3, target.getSizeZ() / 10));
            }
            else if (nums == 0) {
                performer.getCommunicator().sendNormalServerMessage("You fail to crush the " + target.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " tries to crush " + target.getNameWithGenus() + " with " + performer.getHisHerItsString() + " bare hands.", performer, Math.max(3, target.getSizeZ() / 10));
            }
        }
        else if (action == 55) {
            if (target.getOwnerId() != performer.getWurmId()) {
                performer.getCommunicator().sendNormalServerMessage("You can't pick that now.");
                return true;
            }
            if (target.isProcessedFood()) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is processed and there are no seeds to be picked.");
                return true;
            }
            final int makes = target.getTemplate().getPickSeeds();
            if (makes > 0) {
                nums = this.crush(action, performer, target, makes);
            }
            if (nums > 0) {
                performer.getCommunicator().sendNormalServerMessage("You pick the " + target.getName() + " for seeds, ruining it.");
            }
            else if (nums == 0) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " contains almost no seeds.");
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    private int crush(final short action, final Creature performer, final Item target, final int templateId) {
        final int templateWeight = target.getTemplate().getWeightGrams();
        final int nums = target.getWeightGrams() / templateWeight;
        final Item inventory = performer.getInventory();
        for (int x = 0; x < nums; ++x) {
            try {
                if (x != nums - 1 || !target.getParent().isInventory()) {
                    if (!inventory.mayCreatureInsertItem()) {
                        performer.getCommunicator().sendNormalServerMessage("You need more space in your inventory.");
                        return x;
                    }
                }
                final Item toCreate = ItemFactory.createItem(templateId, target.getCurrentQualityLevel(), null);
                if (templateId == 745) {
                    toCreate.setWeight(100, true);
                }
                inventory.insertItem(toCreate);
                target.setWeight(target.getWeightGrams() - templateWeight, true);
            }
            catch (FailedException e) {
                VegetableBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
            catch (NoSuchTemplateException e2) {
                VegetableBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
            catch (NoSuchItemException e3) {
                VegetableBehaviour.logger.log(Level.WARNING, e3.getMessage(), e3);
            }
        }
        return nums;
    }
    
    static {
        logger = Logger.getLogger(VegetableBehaviour.class.getName());
    }
}

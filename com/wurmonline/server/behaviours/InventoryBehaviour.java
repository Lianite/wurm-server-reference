// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.Collection;
import java.util.List;
import com.wurmonline.server.questions.TextInputQuestion;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class InventoryBehaviour extends Behaviour
{
    private static final Logger logger;
    
    public InventoryBehaviour() {
        super((short)49);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        if (action == 1) {
            performer.getCommunicator().sendNormalServerMessage(target.examine(performer));
            target.sendEnchantmentStrings(performer.getCommunicator());
            return true;
        }
        if (action == 567) {
            addGroup(target, performer);
            return true;
        }
        if (action == 59) {
            if (target.getTemplateId() != 824) {
                return true;
            }
            renameGroup(target, performer);
            return true;
        }
        else {
            if (action == 586) {
                removeGroup(target, performer);
                return true;
            }
            if (action == 568 || action == 3) {
                openContainer(target, performer);
                return true;
            }
            if (ManageMenu.isManageAction(performer, action)) {
                return ManageMenu.action(act, performer, action, counter);
            }
            return super.action(act, performer, target, action, counter);
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (action == 1) {
            return this.action(act, performer, target, action, counter);
        }
        if (action == 567 || action == 59 || action == 586) {
            return this.action(act, performer, target, action, counter);
        }
        if (action == 568 || action == 3) {
            return this.action(act, performer, target, action, counter);
        }
        if (ManageMenu.isManageAction(performer, action)) {
            return ManageMenu.action(act, performer, action, counter);
        }
        if (action == 744 && source.canHaveInscription()) {
            return PapyrusBehaviour.addToCookbook(act, performer, source, target, action, counter);
        }
        return super.action(act, performer, source, target, action, counter);
    }
    
    private static void removeGroup(final Item group, final Creature performer) {
        if (group.getTemplateId() != 824) {
            return;
        }
        if (group.getItemsAsArray().length > 0) {
            performer.getCommunicator().sendNormalServerMessage("The group must be empty before you can remove it.");
            return;
        }
        Items.destroyItem(group.getWurmId());
    }
    
    private static void addGroup(final Item inventory, final Creature performer) {
        if ((!inventory.isInventory() && !inventory.isInventoryGroup()) || inventory.getOwnerId() != performer.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("You can only add groups to your inventory or other groups.");
            return;
        }
        final Item[] items = performer.getInventory().getItemsAsArray();
        int groupCount = 0;
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getTemplateId() == 824) {
                ++groupCount;
            }
            if (groupCount == 20) {
                break;
            }
        }
        if (groupCount >= 20) {
            performer.getCommunicator().sendNormalServerMessage("You can only have 20 groups.");
            return;
        }
        try {
            final Item group = ItemFactory.createItem(824, 100.0f, "");
            group.setName("Group");
            inventory.insertItem(group, true);
            renameGroup(group, performer);
        }
        catch (NoSuchTemplateException nst) {
            InventoryBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        catch (FailedException fe) {
            InventoryBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
    }
    
    private static void openContainer(final Item group, final Creature performer) {
        performer.getCommunicator().sendOpenInventoryContainer(group.getWurmId());
    }
    
    private static void renameGroup(final Item group, final Creature performer) {
        final TextInputQuestion tiq = new TextInputQuestion(performer, "Setting name for group.", "Set the new name:", 1, group.getWurmId(), 20, false);
        tiq.setOldtext(group.getName());
        tiq.sendQuestion();
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final int tid = target.getTemplateId();
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        if ((target.isInventory() || target.isInventoryGroup()) && target.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[567]);
        }
        if (tid == 824 && target.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[59]);
            toReturn.add(Actions.actionEntrys[586]);
            toReturn.add(Actions.actionEntrys[568]);
        }
        toReturn.addAll(ManageMenu.getBehavioursFor(performer));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        final int tid = target.getTemplateId();
        if ((target.isInventory() || target.isInventoryGroup()) && target.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[567]);
        }
        if (tid == 824 && target.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[59]);
            toReturn.add(Actions.actionEntrys[586]);
            toReturn.add(Actions.actionEntrys[568]);
        }
        toReturn.addAll(ManageMenu.getBehavioursFor(performer));
        if (target.isInventory() && source.canHaveInscription()) {
            toReturn.addAll(PapyrusBehaviour.getPapyrusBehavioursFor(performer, source));
        }
        return toReturn;
    }
    
    static {
        logger = Logger.getLogger(MethodsItems.class.getName());
    }
}

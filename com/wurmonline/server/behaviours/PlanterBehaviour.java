// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Items;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import java.util.logging.Level;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class PlanterBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    public PlanterBehaviour() {
        super((short)55);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        if (canBePicked(target)) {
            toReturn.add(Actions.actionEntrys[137]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        if (source.isPotable() && target.getTemplateId() == 1161 && source.isRaw() && (source.isPStateNone() || source.isFresh())) {
            toReturn.add(new ActionEntry((short)186, "Plant " + source.getName(), "planting"));
        }
        if (source.getTemplateId() == 176 && performer.getPower() >= 2 && target.getTemplateId() == 1162) {
            toReturn.add(Actions.actionEntrys[188]);
        }
        if (canBePicked(target)) {
            toReturn.add(Actions.actionEntrys[137]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        if (action == 137) {
            return pickHerb(act, performer, target, counter);
        }
        return super.action(act, performer, target, action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        if (action == 1) {
            return this.action(act, performer, target, action, counter);
        }
        if (action == 186) {
            if (source.isSpice() && (source.isFresh() || source.isPStateNone())) {
                return plantHerb(act, performer, source, target, counter);
            }
            return !source.isHerb() || (!source.isFresh() && !source.isPStateNone()) || plantHerb(act, performer, source, target, counter);
        }
        else {
            if (action == 188 && source.getTemplateId() == 176 && performer.getPower() >= 2) {
                target.advancePlanterWeek();
                return true;
            }
            return super.action(act, performer, source, target, action, counter);
        }
    }
    
    private static final boolean pickHerb(final Action act, final Creature performer, final Item pot, final float counter) {
        int time = 0;
        final ItemTemplate growing = pot.getRealTemplate();
        if (growing == null) {
            performer.getCommunicator().sendNormalServerMessage("Not sure what is growing in here.", (byte)3);
            return true;
        }
        if (!Methods.isActionAllowed(performer, act.getNumber())) {
            return true;
        }
        if (!canBePicked(pot)) {
            performer.getCommunicator().sendNormalServerMessage("It is not at correct age to be picked.", (byte)3);
            return true;
        }
        if (!performer.getInventory().mayCreatureInsertItem()) {
            performer.getCommunicator().sendNormalServerMessage("Your inventory is full. You would have no space to put whatever you pick.");
            return true;
        }
        final Skill gardening = performer.getSkills().getSkillOrLearn(10045);
        if (counter == 1.0f) {
            time = Actions.getStandardActionTime(performer, gardening, pot, 0.0) / 5;
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start picking " + growing.getNameWithGenus() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to pick some " + growing.getName() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[137].getVerbString(), true, time);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int age = pot.getAuxData() & 0x7F;
            final int knowledge = (int)gardening.getKnowledge(0.0);
            final float diff = getDifficulty(pot.getRealTemplateId(), knowledge);
            final double power = gardening.skillCheck(diff, 0.0, false, counter);
            try {
                final float ql = Herb.getQL(power, knowledge);
                final Item newItem = ItemFactory.createItem(pot.getRealTemplateId(), Math.max(ql, 1.0f), (byte)0, act.getRarity(), null);
                if (ql < 0.0f) {
                    newItem.setDamage(-ql / 2.0f);
                }
                else {
                    newItem.setIsFresh(true);
                }
                final Item inventory = performer.getInventory();
                inventory.insertItem(newItem);
                performer.achievement(602);
            }
            catch (FailedException fe) {
                PlanterBehaviour.logger.log(Level.WARNING, performer.getName() + " " + fe.getMessage(), fe);
            }
            catch (NoSuchTemplateException nst) {
                PlanterBehaviour.logger.log(Level.WARNING, performer.getName() + " " + nst.getMessage(), nst);
            }
            pot.setLastMaintained(WurmCalendar.currentTime);
            if (power < -50.0) {
                performer.getCommunicator().sendNormalServerMessage("You broke off more than needed and damaged the plant, but still managed to get " + growing.getNameWithGenus() + ".");
                pot.setAuxData((byte)(age + 1));
            }
            else if (power > 0.0) {
                performer.getCommunicator().sendNormalServerMessage("You successfully picked " + growing.getNameWithGenus() + ", it now looks healthier.");
                pot.setAuxData((byte)(age - 1));
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You successfully picked " + growing.getNameWithGenus() + ".");
                pot.setAuxData((byte)age);
            }
            return true;
        }
        return false;
    }
    
    private static boolean canBePicked(final Item pot) {
        if (pot.getTemplateId() != 1162) {
            return false;
        }
        final ItemTemplate temp = pot.getRealTemplate();
        final int age = pot.getAuxData() & 0x7F;
        final boolean pickable = (pot.getAuxData() & 0x80) != 0x0;
        return temp != null && pickable && age > 5 && age < 95;
    }
    
    private static boolean plantHerb(final Action act, final Creature performer, final Item herbSpice, final Item pot, final float counter) {
        if (!Methods.isActionAllowed(performer, act.getNumber())) {
            return true;
        }
        int time = 0;
        if (counter == 1.0f) {
            final String type = herbSpice.isSpice() ? "spice" : "herb";
            final Skill gardening = performer.getSkills().getSkillOrLearn(10045);
            time = Actions.getStandardActionTime(performer, gardening, herbSpice, 0.0);
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start planting the " + herbSpice.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to plant some " + type + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[186].getVerbString(), true, time);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time) {
            float ql = herbSpice.getQualityLevel() + pot.getQualityLevel();
            ql /= 2.0f;
            float dmg = herbSpice.getDamage() + pot.getDamage();
            dmg /= 2.0f;
            final Skill gardening2 = performer.getSkills().getSkillOrLearn(10045);
            try {
                final int toCreate = 1162;
                final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(1162);
                final double power = gardening2.skillCheck(template.getDifficulty() + dmg, ql, false, counter);
                if (power > 0.0) {
                    try {
                        final Item newPot = ItemFactory.createItem(1162, pot.getQualityLevel(), pot.getRarity(), performer.getName());
                        newPot.setRealTemplate(herbSpice.getTemplate().getGrows());
                        newPot.setLastOwnerId(pot.getLastOwnerId());
                        newPot.setDescription(pot.getDescription());
                        newPot.setDamage(pot.getDamage());
                        final Item parent = pot.getParentOrNull();
                        if (parent != null && parent.getTemplateId() == 1110 && parent.getItemsAsArray().length > 30) {
                            performer.getCommunicator().sendNormalServerMessage("The pot will not fit back into the rack, so you place it on the ground.", (byte)2);
                            newPot.setPosXY(pot.getPosX(), pot.getPosY());
                            final VolaTile tile = Zones.getTileOrNull(pot.getTileX(), pot.getTileY(), pot.isOnSurface());
                            if (tile != null) {
                                tile.addItem(newPot, false, false);
                            }
                        }
                        else if (parent == null) {
                            newPot.setPosXYZRotation(pot.getPosX(), pot.getPosY(), pot.getPosZ(), pot.getRotation());
                            newPot.setIsPlanted(pot.isPlanted());
                            final VolaTile tile = Zones.getTileOrNull(pot.getTileX(), pot.getTileY(), pot.isOnSurface());
                            if (tile != null) {
                                tile.addItem(newPot, false, false);
                            }
                        }
                        else {
                            parent.insertItem(newPot, true);
                        }
                        Items.destroyItem(pot.getWurmId());
                        performer.getCommunicator().sendNormalServerMessage("You finished planting the " + herbSpice.getName() + " in the pot.");
                    }
                    catch (NoSuchTemplateException nst) {
                        PlanterBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                    catch (FailedException fe) {
                        PlanterBehaviour.logger.log(Level.WARNING, fe.getMessage(), fe);
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("Sadly, the fragile " + herbSpice.getName() + " do not survive despite your best efforts.", (byte)3);
                }
                Items.destroyItem(herbSpice.getWurmId());
            }
            catch (NoSuchTemplateException nst2) {
                PlanterBehaviour.logger.log(Level.WARNING, nst2.getMessage(), nst2);
            }
            return true;
        }
        return false;
    }
    
    private static float getDifficulty(final int templateId, final int knowledge) {
        final float h = Herb.getDifficulty(templateId, knowledge);
        if (h > 0.0f) {
            return h;
        }
        final float f = Forage.getDifficulty(templateId, knowledge);
        if (f > 0.0f) {
            return f;
        }
        return 0.0f;
    }
    
    static {
        logger = Logger.getLogger(PlanterBehaviour.class.getName());
    }
}

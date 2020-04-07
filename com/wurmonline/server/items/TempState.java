// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import java.util.Set;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempState implements ItemTypes
{
    private final int origItemTemplateId;
    private final int newItemTemplateId;
    private final short temperatureChangeLevel;
    private final boolean atIncrease;
    private static final Logger logger;
    private final boolean keepWeight;
    private final boolean keepMaterial;
    
    public TempState(final int aOrigItemTemplateId, final int aNewItemTemplateId, final short aTemperatureChangeLevel, final boolean aAtIncrease, final boolean aKeepWeight, final boolean aKeepMaterial) {
        this.origItemTemplateId = aOrigItemTemplateId;
        this.newItemTemplateId = aNewItemTemplateId;
        this.temperatureChangeLevel = aTemperatureChangeLevel;
        this.atIncrease = aAtIncrease;
        this.keepWeight = aKeepWeight;
        this.keepMaterial = aKeepMaterial;
    }
    
    public boolean changeItem(final Item parent, final Item item, final short oldTemp, final short newTemp, final float qualityRatio) {
        int itemPrimarySkill = -10;
        if (this.passedLevel(oldTemp, newTemp)) {
            if (newTemp >= this.temperatureChangeLevel) {
                if (this.atIncrease) {
                    try {
                        Item newItem = null;
                        Creature performer = null;
                        if (this.keepMaterial) {
                            newItem = ItemFactory.createItem(this.newItemTemplateId, item.getCurrentQualityLevel() * qualityRatio, item.getMaterial(), item.getRarity(), item.creator);
                        }
                        else {
                            newItem = ItemFactory.createItem(this.newItemTemplateId, item.getCurrentQualityLevel() * qualityRatio, (byte)0, item.getRarity(), item.creator);
                        }
                        newItem.setDescription(item.getDescription());
                        final Set<Item> items = item.getItems();
                        if (items != null) {
                            final Item[] itarr = items.toArray(new Item[items.size()]);
                            for (int x = 0; x < itarr.length; ++x) {
                                try {
                                    item.dropItem(itarr[x].getWurmId(), false);
                                    newItem.insertItem(itarr[x], true);
                                }
                                catch (NoSuchItemException nsi) {
                                    TempState.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                                }
                            }
                        }
                        if (item.isPassFullData()) {
                            newItem.setData(item.getData());
                        }
                        newItem.setLastOwnerId(item.getLastOwnerId());
                        if (newItem.hasPrimarySkill()) {
                            try {
                                itemPrimarySkill = newItem.getPrimarySkill();
                            }
                            catch (NoSuchSkillException ex) {}
                            try {
                                performer = Server.getInstance().getCreature(newItem.getLastOwnerId());
                            }
                            catch (Exception ex2) {}
                        }
                        Items.destroyItem(item.getWurmId());
                        if (this.keepWeight) {
                            newItem.setWeight(item.getWeightGrams(), false);
                        }
                        else {
                            final int currweight = item.getWeightGrams();
                            float mod = currweight / item.getTemplate().getWeightGrams();
                            if (item.getTemplateId() == 684) {
                                mod *= 0.8f;
                            }
                            final int newWeight = (int)(newItem.getTemplate().getWeightGrams() * mod);
                            newItem.setWeight(newWeight, false);
                        }
                        if (newItem.getWeightGrams() > 0) {
                            newItem.setTemperature(newTemp);
                            if (!parent.insertItem(newItem, true)) {
                                TempState.logger.log(Level.WARNING, parent.getName() + " failed to insert item " + newItem.getName());
                                if (newItem.getWeightGrams() > parent.getFreeVolume()) {
                                    TempState.logger.log(Level.INFO, "Old weight=" + newItem.getWeightGrams() + ", trying to set weight to " + parent.getFreeVolume());
                                    newItem.setWeight(parent.getFreeVolume(), true);
                                    if (parent.insertItem(newItem)) {
                                        TempState.logger.log(Level.INFO, "THAT did the trick:)");
                                    }
                                    else {
                                        TempState.logger.log(Level.INFO, "Didn't help.");
                                    }
                                }
                                else {
                                    TempState.logger.log(Level.INFO, newItem.getName() + ": old weight=" + newItem.getWeightGrams() + ", larger than " + parent.getFreeVolume() + " have to change sizes from " + newItem.getSizeX() + ", " + newItem.getSizeY() + ", " + newItem.getSizeZ() + ".");
                                    for (int x2 = 0; x2 < 10; ++x2) {
                                        if (newItem.depleteSizeWith(Math.max(1, newItem.getSizeX() / 10), Math.max(1, newItem.getSizeY() / 10), Math.max(1, newItem.getSizeZ() / 10))) {
                                            TempState.logger.log(Level.INFO, "Item destroyed. Breaking out.");
                                            break;
                                        }
                                        if (parent.insertItem(newItem)) {
                                            TempState.logger.log(Level.INFO, "Managed to insert item with size " + newItem.getSizeX() + ", " + newItem.getSizeY() + ", " + newItem.getSizeZ() + " after " + x2 + " iterations.");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                        }
                        this.giveSkillGainForTemplatePrimarySkill(performer, newItem, itemPrimarySkill);
                    }
                    catch (NoSuchTemplateException nst) {
                        TempState.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                    catch (FailedException fe) {
                        TempState.logger.log(Level.WARNING, fe.getMessage(), fe);
                    }
                    return true;
                }
            }
            else if (newTemp <= this.temperatureChangeLevel && !this.atIncrease) {
                try {
                    Item newItem = null;
                    if (this.keepMaterial) {
                        newItem = ItemFactory.createItem(this.newItemTemplateId, item.getCurrentQualityLevel() * qualityRatio, item.getMaterial(), item.getRarity(), item.creator);
                    }
                    else {
                        newItem = ItemFactory.createItem(this.newItemTemplateId, item.getCurrentQualityLevel() * qualityRatio, item.creator);
                    }
                    newItem.setLastOwnerId(item.getLastOwnerId());
                    Items.destroyItem(item.getWurmId());
                    newItem.setTemperature(newTemp);
                    if (this.keepWeight) {
                        newItem.setWeight(item.getWeightGrams(), false);
                    }
                    if (newItem.getWeightGrams() > 0) {
                        parent.insertItem(newItem, true);
                    }
                    else {
                        Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                    }
                }
                catch (NoSuchTemplateException nst) {
                    TempState.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
                catch (FailedException fe) {
                    TempState.logger.log(Level.WARNING, fe.getMessage(), fe);
                }
                return true;
            }
        }
        else if (item.isFood() && newTemp > 2700) {
            item.setDamage(item.getDamage() + Math.max(0.1f, (newTemp - oldTemp) / 10.0f));
        }
        return false;
    }
    
    boolean passedLevel(final short oldTemp, final short newTemp) {
        return (oldTemp > this.temperatureChangeLevel && newTemp <= this.temperatureChangeLevel) || (oldTemp < this.temperatureChangeLevel && newTemp >= this.temperatureChangeLevel);
    }
    
    int getOrigItemTemplateId() {
        return this.origItemTemplateId;
    }
    
    int getNewItemTemplateId() {
        return this.newItemTemplateId;
    }
    
    short getTemperatureChangeLevel() {
        return this.temperatureChangeLevel;
    }
    
    boolean isAtIncrease() {
        return this.atIncrease;
    }
    
    boolean isKeepWeight() {
        return this.keepWeight;
    }
    
    boolean isKeepMaterial() {
        return this.keepMaterial;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.atIncrease ? 1231 : 1237);
        result = 31 * result + (this.keepMaterial ? 1231 : 1237);
        result = 31 * result + (this.keepWeight ? 1231 : 1237);
        result = 31 * result + this.newItemTemplateId;
        result = 31 * result + this.origItemTemplateId;
        result = 31 * result + this.temperatureChangeLevel;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TempState)) {
            return false;
        }
        final TempState other = (TempState)obj;
        return this.atIncrease == other.atIncrease && this.keepMaterial == other.keepMaterial && this.keepWeight == other.keepWeight && this.newItemTemplateId == other.newItemTemplateId && this.origItemTemplateId == other.origItemTemplateId && this.temperatureChangeLevel == other.temperatureChangeLevel;
    }
    
    @Override
    public String toString() {
        return "TempState [atIncrease=" + this.atIncrease + ", keepMaterial=" + this.keepMaterial + ", keepWeight=" + this.keepWeight + ", newItemTemplateId=" + this.newItemTemplateId + ", origItemTemplateId=" + this.origItemTemplateId + ", temperatureChangeLevel=" + this.temperatureChangeLevel + "]";
    }
    
    private void giveSkillGainForTemplatePrimarySkill(final Creature performer, final Item newItem, final int skillId) {
        if (performer == null) {
            return;
        }
        if (newItem == null) {
            return;
        }
        final Skills skills = performer.getSkills();
        Skill skill;
        try {
            skill = skills.getSkill(skillId);
        }
        catch (NoSuchSkillException ss) {
            skill = skills.learn(skillId, 1.0f);
        }
        final float diff = newItem.getTemplate().getDifficulty();
        if (skill != null) {
            skill.skillCheck(diff, newItem, 0.0, false, 1.0f);
        }
    }
    
    static {
        logger = Logger.getLogger(TempState.class.getName());
    }
}

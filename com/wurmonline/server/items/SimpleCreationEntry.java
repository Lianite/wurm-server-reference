// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.behaviours.MethodsItems;
import java.util.logging.Level;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.SoundNames;

public final class SimpleCreationEntry extends CreationEntry implements SoundNames, TimeConstants
{
    private static final Logger logger;
    
    public SimpleCreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDestroyTarget, final boolean aUseCapacity, final float aPercentageLost, final int aMinTimeSeconds, final boolean aDestroyBoth, final boolean aCreateOnGround, final CreationCategories aCategory) {
        super(aPrimarySkill, aObjectSource, aObjectTarget, aObjectCreated, aDestroyTarget, aUseCapacity, aPercentageLost, aMinTimeSeconds, aDestroyBoth, aCreateOnGround, aCategory);
    }
    
    public SimpleCreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final float aPercentageLost, final boolean aDepleteEqually, final boolean aCreateOnGround, final CreationCategories category) {
        super(aPrimarySkill, aObjectSource, aObjectTarget, aObjectCreated, aDepleteSource, aDepleteTarget, aPercentageLost, aDepleteEqually, aCreateOnGround, category);
    }
    
    public SimpleCreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final float aPercentageLost, final boolean aDepleteEqually, final boolean aCreateOnGround, final int aCustomChanceCutOff, final double aMinimumSkill, final CreationCategories category) {
        super(aPrimarySkill, aObjectSource, aObjectTarget, aObjectCreated, aDepleteSource, aDepleteTarget, aPercentageLost, aDepleteEqually, aCreateOnGround, aCustomChanceCutOff, aMinimumSkill, category);
    }
    
    @Override
    CreationEntry cloneAndRevert() {
        final CreationEntry toReturn = new SimpleCreationEntry(this.primarySkill, this.objectTarget, this.objectSource, this.objectCreated, this.destroyTarget, this.useCapacity, this.percentageLost, this.minTimeSeconds, this.destroyBoth, this.createOnGround, this.getCategory());
        return toReturn;
    }
    
    @Override
    public Item run(final Creature performer, final Item source, final long targetId, final float counter) throws FailedException, NoSuchSkillException, NoSuchItemException, IllegalArgumentException {
        final boolean sendFailedMessage = true;
        final Item target = Items.getItem(targetId);
        Item realSource = source;
        Item realTarget = target;
        boolean create = false;
        if (performer.getVehicle() != -10L) {
            performer.getCommunicator().sendNormalServerMessage("You need to be on solid ground to do that.");
            throw new NoSuchItemException("Need to be on solid ground.");
        }
        if (source.getTemplateId() == this.objectSource && target.getTemplateId() == this.objectTarget) {
            create = true;
        }
        else if (source.getTemplateId() == this.objectTarget && target.getTemplateId() == this.objectSource) {
            create = true;
            realTarget = source;
            realSource = target;
        }
        if (source.deleted || target.deleted) {
            create = false;
        }
        try {
            final int chance = (int)this.getDifficultyFor(realSource, realTarget, performer);
            if (chance == 0 || chance <= 5) {
                performer.getCommunicator().sendNormalServerMessage("This is impossible, perhaps you are not skilled enough.");
                throw new NoSuchItemException("Not enough skill.");
            }
        }
        catch (NoSuchTemplateException nst) {
            throw new NoSuchItemException(nst.getMessage(), nst);
        }
        if (this.objectSourceMaterial != 0 && realSource.getMaterial() != this.objectSourceMaterial) {
            performer.getCommunicator().sendNormalServerMessage("Incorrect source material!");
            throw new NoSuchItemException("Incorrect source material!");
        }
        if (this.objectTargetMaterial != 0 && realTarget.getMaterial() != this.objectTargetMaterial) {
            performer.getCommunicator().sendNormalServerMessage("Incorrect target material!");
            throw new NoSuchItemException("Incorrect target material!");
        }
        if (this.objectCreated == 652) {
            if (realTarget.getMaterial() != 37) {
                performer.getCommunicator().sendNormalServerMessage("You need to use a pine tree.");
                throw new NoSuchItemException("Wrong log type.");
            }
        }
        else if (this.objectCreated == 847) {
            final int creatureId = realSource.getData2();
            if (realSource.getTemplateId() == 302 && creatureId != 12) {
                performer.getCommunicator().sendNormalServerMessage("You can't create this with this type of fur.");
                throw new NoSuchItemException("Wrong fur type.");
            }
        }
        else if (this.objectCreated == 849) {
            final int creatureId = realSource.getData2();
            if (realSource.getTemplateId() == 302 && creatureId != 10) {
                performer.getCommunicator().sendNormalServerMessage("You can't create this with this type of fur.");
                throw new NoSuchItemException("Wrong fur type.");
            }
        }
        else if (this.objectCreated == 846) {
            final int creatureId = realSource.getData2();
            if (realSource.getTemplateId() == 302 && creatureId != 42) {
                performer.getCommunicator().sendNormalServerMessage("You can't create this with this type of fur.");
                throw new NoSuchItemException("Wrong fur type.");
            }
        }
        else if (this.objectCreated == 848) {
            final int creatureId = realSource.getData2();
            if (realSource.getTemplateId() == 313 && creatureId != 14) {
                performer.getCommunicator().sendNormalServerMessage("You can't create this with this type of pelt.");
                throw new NoSuchItemException("Wrong pelt type.");
            }
        }
        else if (this.objectCreated == 1269) {
            if (performer.getInventory().getNumItemsNotCoins() > 95) {
                performer.getCommunicator().sendNormalServerMessage("You don't have space for the labels.");
                throw new NoSuchItemException("No space.");
            }
            switch (realTarget.getAuxData()) {
                case 0: {
                    final InscriptionData ins = target.getInscription();
                    if (ins != null && ins.hasBeenInscribed()) {
                        performer.getCommunicator().sendNormalServerMessage("You can't create labels from " + realTarget.getNameWithGenus() + ".");
                        throw new NoSuchItemException("Inscribed paper.");
                    }
                    break;
                }
                case 1: {
                    performer.getCommunicator().sendNormalServerMessage("You can't create labels from " + realTarget.getNameWithGenus() + ".");
                    throw new NoSuchItemException("Recipe paper.");
                }
                case 2: {
                    performer.getCommunicator().sendNormalServerMessage("You can't create labels from " + realTarget.getNameWithGenus() + ".");
                    throw new NoSuchItemException("Waxed paper.");
                }
                default: {
                    performer.getCommunicator().sendNormalServerMessage("You can't create labels from " + realTarget.getNameWithGenus() + ".");
                    throw new NoSuchItemException("Harvestable report.");
                }
            }
        }
        if (!create) {
            throw new IllegalArgumentException("Illegal parameters for this entry: source=" + realSource.getTemplateId() + ", target=" + realTarget.getTemplateId() + " when creating " + this.objectCreated);
        }
        ItemTemplate template = null;
        try {
            template = ItemTemplateFactory.getInstance().getTemplate(this.objectCreated);
        }
        catch (NoSuchTemplateException nst2) {
            SimpleCreationEntry.logger.log(Level.WARNING, "no template for creating " + this.objectCreated, nst2);
            performer.getCommunicator().sendSafeServerMessage("You cannot create that item right now. Please contact administrators.");
            throw new NoSuchItemException("Failed to locate template");
        }
        if (!this.createOnGround && template.isHollow()) {
            final Item parent = realTarget.getParent();
            if (template.getSizeZ() >= parent.getContainerSizeZ() || template.getSizeY() >= parent.getContainerSizeY() || template.getSizeX() >= parent.getContainerSizeX()) {
                performer.getCommunicator().sendSafeServerMessage("The " + realTarget.getName() + " will not fit in the " + parent.getName() + ".");
                throw new NoSuchItemException("The " + realTarget.getName() + " will not fit in the " + parent.getName() + ".");
            }
        }
        else if (this.createOnGround && !MethodsItems.mayDropOnTile(performer)) {
            performer.getCommunicator().sendSafeServerMessage("There is no room in front of you to create the " + realTarget.getName() + ".");
            throw new NoSuchItemException("There is no room in front of you to create the " + realTarget.getName() + ".");
        }
        final Skills skills = performer.getSkills();
        Skill primSkill = null;
        Skill secondarySkill = null;
        Action act = null;
        try {
            act = performer.getCurrentAction();
        }
        catch (NoSuchActionException nsa) {
            SimpleCreationEntry.logger.log(Level.WARNING, "This action doesn't exist? " + performer.getName(), nsa);
            throw new FailedException("An error occured on the server. This action was not found. Please report.");
        }
        try {
            primSkill = skills.getSkill(this.primarySkill);
        }
        catch (Exception ex2) {
            primSkill = skills.learn(this.primarySkill, 1.0f);
        }
        Label_1281: {
            if (realSource.isWeapon()) {
                if (Weapon.getSkillPenaltyForWeapon(realSource) <= 0.0) {
                    break Label_1281;
                }
            }
            try {
                secondarySkill = skills.getSkill(realSource.getPrimarySkill());
            }
            catch (Exception ex2) {
                try {
                    secondarySkill = skills.learn(realSource.getPrimarySkill(), 1.0f);
                }
                catch (Exception ex3) {}
            }
        }
        int time = 10;
        if (counter == 1.0f) {
            final int start = 150;
            if (realTarget.isMetal()) {
                if (this.primarySkill == 10041 && realSource.getTemperature() < 3500) {
                    performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " must be glowing hot to do this.");
                    throw new NoSuchItemException("Too low temperature.");
                }
                if (realSource.isMetal() && realTarget.getTemperature() < 3500) {
                    performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getName() + " must be glowing hot to do this.");
                    throw new NoSuchItemException("Too low temperature.");
                }
            }
            if (realSource.isForm() && realTarget.getTemperature() < 3500) {
                performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getName() + " must be glowing hot to do this.");
                throw new NoSuchItemException("Too low temperature.");
            }
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("Your inventory is full.");
                throw new NoSuchItemException("Full inventory.");
            }
            final int sourceWeightToRemove = this.getSourceWeightToRemove(realSource, realTarget, template, false);
            int targetWeightToRemove = this.getTargetWeightToRemove(realSource, realTarget, template, false);
            if (this.objectCreated == 652) {
                targetWeightToRemove = realTarget.getWeightGrams();
            }
            else if (this.objectCreated == 1272 || this.objectCreated == 1347) {
                targetWeightToRemove = realTarget.getTemplate().getWeightGrams();
            }
            this.checkSaneAmounts(realSource, sourceWeightToRemove, realTarget, targetWeightToRemove, template, performer, false);
            if (this.objectCreated == 37) {
                final VolaTile tile = performer.getCurrentTile();
                if (tile != null) {
                    for (final Item i : tile.getItems()) {
                        if (i.getTemplateId() == 37 && i.getTemperature() > 200) {
                            performer.getCommunicator().sendNormalServerMessage("There is already a lit campfire here, and no room for another.");
                            throw new NoSuchItemException("No room for more campfires.");
                        }
                    }
                }
            }
            if (template.isOutsideOnly()) {
                final VolaTile tile = performer.getCurrentTile();
                if (tile != null && tile.getStructure() != null) {
                    if (!tile.getStructure().isTypeBridge()) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot create that inside a building.");
                        throw new NoSuchItemException("Can't create inside.");
                    }
                    if (performer.getBridgeId() != -10L) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot create that on a bridge.");
                        throw new NoSuchItemException("Can't create on a bridge.");
                    }
                }
            }
            realTarget.setBusy(true);
            try {
                time = Actions.getItemCreationTime(150, performer, primSkill, this, realSource, realTarget, template.isMassProduction());
            }
            catch (NoSuchTemplateException nst3) {
                SimpleCreationEntry.logger.log(Level.WARNING, "No template when creating with " + realSource.getName() + " and " + realTarget.getName() + "." + nst3.getMessage(), nst3);
                performer.getCommunicator().sendSafeServerMessage("You cannot create that item right now. Please contact administrators.");
                throw new NoSuchItemException("No template.", nst3);
            }
            try {
                performer.getCurrentAction().setTimeLeft(time);
            }
            catch (NoSuchActionException nsa2) {
                SimpleCreationEntry.logger.log(Level.INFO, "This action does not exist?", nsa2);
            }
            performer.sendActionControl(Actions.actionEntrys[148].getVerbString() + " " + template.getName(), true, time);
            if (realSource.isNoTake()) {
                performer.getCommunicator().sendNormalServerMessage("You start to work with the " + realTarget.getName() + " on the " + realSource.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts working with the " + realTarget.getName() + " on the " + realSource.getName() + ".", performer, 5);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You start to work with the " + realSource.getName() + " on the " + realTarget.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts working with the " + realSource.getName() + " on the " + realTarget.getName() + ".", performer, 5);
            }
            if (!this.depleteSource && realSource.isRepairable()) {
                realSource.setDamage(realSource.getDamage() + 0.0025f * realSource.getDamageModifier());
            }
            if (!this.depleteTarget && realTarget.isRepairable()) {
                realTarget.setDamage(realTarget.getDamage() + 0.0025f * realSource.getDamageModifier());
            }
        }
        else {
            try {
                time = performer.getCurrentAction().getTimeLeft();
                if (act.currentSecond() % 5 == 0) {
                    if (!this.depleteEqually && !this.depleteSource && realSource.isRepairable()) {
                        realSource.setDamage(realSource.getDamage() + 0.0025f * realSource.getDamageModifier());
                    }
                    if (!this.depleteEqually && !this.depleteTarget && realTarget.isRepairable()) {
                        realTarget.setDamage(realTarget.getDamage() + 0.0025f * realSource.getDamageModifier());
                    }
                }
                if (act.mayPlaySound()) {
                    this.sendSound(performer, this.objectSource, realTarget, this.objectCreated);
                }
            }
            catch (NoSuchActionException nsa3) {
                SimpleCreationEntry.logger.log(Level.INFO, "This action does not exist?", nsa3);
            }
        }
        if (counter * 10.0f <= time) {
            throw new FailedException("Failed skillcheck.");
        }
        if (act.getRarity() != 0) {
            performer.playPersonalSound("sound.fx.drumroll");
        }
        double bonus = performer.getVillageSkillModifier();
        float skillMultiplier = Math.min(Math.max(1.0f, counter / 3.0f), 20.0f);
        if (Servers.localServer.isChallengeOrEpicServer() && primSkill.hasLowCreationGain()) {
            skillMultiplier /= 3.0f;
        }
        if (secondarySkill != null) {
            bonus += Math.max(-10.0, secondarySkill.skillCheck(template.getDifficulty(), 0.0, false, counter / 10.0f));
        }
        performer.sendToLoggers("Skill multiplier=" + skillMultiplier, (byte)3);
        float power = 1.0f;
        float alc = 0.0f;
        if (performer.isPlayer()) {
            alc = ((Player)performer).getAlcohol();
        }
        if ((Servers.localServer.entryServer && template.newbieItem) || template.getTemplateId() == 156) {
            bonus += 100.0;
        }
        if (template.isRune()) {
            boolean godBonus = false;
            if (performer.getDeity() != null) {
                if ((performer.getDeity().isMountainGod() && this.objectCreated == 1289) || (performer.getDeity().isForestGod() && this.objectCreated == 1290) || (performer.getDeity().isWaterGod() && this.objectCreated == 1291) || (performer.getDeity().isHateGod() && this.objectCreated == 1292)) {
                    if (performer.getFaith() >= 20.0f) {
                        godBonus = true;
                    }
                }
                else if (performer.getFaith() < 20.0f && this.objectCreated == 1293) {
                    godBonus = true;
                }
            }
            else if (this.objectCreated == 1293) {
                godBonus = true;
            }
            if (godBonus) {
                bonus += 100.0;
            }
        }
        if (realSource.isBodyPart()) {
            power = (float)primSkill.skillCheck(template.getDifficulty() + alc, null, bonus, false, skillMultiplier);
        }
        else {
            power = (float)primSkill.skillCheck(template.getDifficulty() + alc, realSource, bonus, false, skillMultiplier);
        }
        if (performer.isRoyalSmith() && template.isMetal() && power < 0.0f && power > -20.0f) {
            power = 10 + Server.rand.nextInt(10);
        }
        byte material = realTarget.getMaterial();
        boolean mixedLiquids = false;
        final int sourceWeightToRemove2 = this.getSourceWeightToRemove(realSource, realTarget, template, false);
        int targetWeightToRemove2 = this.getTargetWeightToRemove(realSource, realTarget, template, false);
        if (this.objectCreated == 652) {
            targetWeightToRemove2 = realTarget.getWeightGrams();
        }
        else if (this.objectCreated == 1272 || this.objectCreated == 1347) {
            targetWeightToRemove2 = realTarget.getTemplate().getWeightGrams();
        }
        this.checkSaneAmounts(realSource, sourceWeightToRemove2, realTarget, targetWeightToRemove2, template, performer, false);
        if (realSource.isLiquid() && realTarget.isLiquid()) {
            material = template.getMaterial();
            mixedLiquids = true;
        }
        else if (realSource.isLiquid() && template.isLiquid()) {
            material = template.getMaterial();
        }
        else if (template.getTemplateId() == 1270) {
            material = realTarget.getMaterial();
        }
        else if ((this.depleteEqually || (this.depleteSource && this.depleteTarget)) && (realTarget.isCombine() || realTarget.isLiquid()) && (realSource.isCombine() || realSource.isLiquid())) {
            material = template.getMaterial();
        }
        if (template.isRune()) {
            material = realTarget.getMaterial();
        }
        if (this.usesFinalMaterial()) {
            material = this.getFinalMaterial();
        }
        Item newItem = null;
        try {
            final double imbueEnhancement = 1.0 + 0.23047 * realSource.getSkillSpellImprovement(primSkill.getNumber()) / 100.0;
            float itq;
            final float qlevel = itq = (float)(power * imbueEnhancement);
            if (realTarget.getCurrentQualityLevel() < qlevel) {
                itq = Math.max(1.0f, realTarget.getCurrentQualityLevel());
            }
            if (realSource.isCrude() || realTarget.isCrude()) {
                itq = 1.0f + Server.rand.nextFloat() * 10.0f;
            }
            if (this.objectCreated == 37 && (this.objectSource == 169 || this.objectTarget == 169)) {
                itq = 1.0f;
            }
            long targetParentId = -10L;
            if (!realTarget.isNoTake()) {
                targetParentId = realTarget.getParentId();
            }
            if (template.isTutorialItem()) {
                itq = Math.max(qlevel, Server.rand.nextInt(20) + 1);
            }
            if (qlevel > 0.0f || (template.isTutorialItem() && Server.rand.nextInt(100) < 90.0f)) {
                if (template.isRepairable() && !template.isImproveItem) {
                    itq = Math.max(1.0f, Math.min(itq, (float)(primSkill.getKnowledge(0.0) / 5.0)));
                }
                if (realSource.getMaterial() == 56 || realSource.getMaterial() == 57) {
                    final float leftToMax = 100.0f - itq;
                    itq += leftToMax / 10.0f;
                }
                if (Item.getMaterialCreationBonus(material) > 0.0f) {
                    final float leftToMax = 100.0f - itq;
                    itq += leftToMax * Item.getMaterialCreationBonus(material);
                }
                if (act.getRarity() > 0 || source.getRarity() > 0 || target.getRarity() > 0) {
                    itq = GeneralUtilities.calcRareQuality(itq, act.getRarity(), source.getRarity(), target.getRarity());
                }
                int newWeight = template.getWeightGrams();
                int t = this.objectCreated;
                int color = -1;
                if (template.isColor) {
                    t = 438;
                    color = WurmColor.getInitialColor(this.objectCreated, Math.max(1.0f, itq));
                    newWeight = Math.min(newWeight, sourceWeightToRemove2 + targetWeightToRemove2 / 10);
                }
                if (realSource.getTemplateId() == 1254) {
                    itq = Math.min(itq * 1.25f, 99.0f);
                }
                if (this.objectCreated == 1270) {
                    itq = Math.min((itq + realSource.getCurrentQualityLevel()) / 2.0f, 99.0f);
                }
                if (this.objectCreated == 1269) {
                    performer.getCommunicator().sendNormalServerMessage("You cut the " + realTarget.getName() + " into five labels.");
                    Server.getInstance().broadCastAction(performer.getName() + " cuts the " + realTarget.getName() + " into five labels.", performer, 5);
                    for (int x = 0; x < 5; ++x) {
                        try {
                            newItem = ItemFactory.createItem(t, Math.max(1.0f, itq), material, act.getRarity(), performer.getName());
                            performer.getInventory().insertItem(newItem, true);
                        }
                        catch (NoSuchTemplateException nst4) {
                            SimpleCreationEntry.logger.log(Level.WARNING, nst4.getMessage());
                        }
                        catch (FailedException fe) {
                            SimpleCreationEntry.logger.log(Level.WARNING, fe.getMessage());
                        }
                    }
                    Items.destroyItem(realTarget.getWurmId());
                }
                else {
                    newItem = ItemFactory.createItem(t, Math.max(1.0f, itq), material, act.getRarity(), performer.getName());
                    if (template.isDragonArmour) {
                        color = realTarget.getDragonColor();
                        newItem.setName(realTarget.getDragonColorName() + " " + newItem.getName());
                    }
                    if (template.isRune()) {
                        newItem.setRealTemplate(realSource.getTemplateId());
                    }
                    if (newItem.getTemplateId() == 488) {
                        newItem.setRealTemplate(488);
                        newItem.setName("endurance sandwich");
                        performer.achievement(196);
                    }
                    if (color != -1) {
                        newItem.setColor(color);
                    }
                    if (realTarget.isMetal() && realSource.isMetal()) {
                        newItem.setTemperature((short)((realTarget.getTemperature() + realSource.getTemperature()) / 2));
                    }
                    else if (realTarget.isMetal() && realTarget.isCombine()) {
                        newItem.setTemperature(realTarget.getTemperature());
                    }
                    else if (realSource.isMetal() && realSource.isCombine()) {
                        newItem.setTemperature(realSource.getTemperature());
                    }
                    if (this.objectCreated == 652) {
                        newWeight = realTarget.getWeightGrams();
                    }
                    newItem.setWeight(newWeight, false);
                    if (template.isLiquid()) {
                        if (this.depleteSource && this.depleteTarget) {
                            if (mixedLiquids) {
                                newWeight = sourceWeightToRemove2 + targetWeightToRemove2;
                                newItem.setWeight(newWeight, false);
                                newItem.setSizes(Math.min(template.getSizeX(), realSource.getSizeX()), Math.min(template.getSizeY(), realSource.getSizeY()), Math.min(template.getSizeZ(), realSource.getSizeZ()));
                            }
                            else {
                                final int nums = this.getTemplateNumbers(realSource, realTarget, template);
                                newWeight = Math.min(sourceWeightToRemove2 + targetWeightToRemove2, nums * template.getWeightGrams());
                                newItem.setWeight(newWeight, false);
                                if (template.isLiquid()) {
                                    newItem.setSizes(Math.min(template.getSizeX(), realSource.getSizeX()), Math.min(template.getSizeY(), realSource.getSizeY()), Math.min(template.getSizeZ(), realSource.getSizeZ()));
                                }
                                else {
                                    newItem.setSizes((int)Math.min(template.getSizeX() * ((nums < 3) ? nums : (2.0f + nums / 3.0f)), realSource.getSizeX()), (int)Math.min(template.getSizeY() * Math.max(1.0f, nums / 3.0f), realSource.getSizeY()), (int)Math.min(template.getSizeZ() * Math.max(1.0f, nums / 3.0f), realSource.getSizeZ()));
                                }
                            }
                        }
                        else if (this.depleteSource) {
                            final int nums = this.getTemplateNumbersForSource(realSource, template);
                            newWeight = Math.min(sourceWeightToRemove2, nums * template.getWeightGrams());
                            newItem.setWeight(newWeight, false);
                            if (template.isLiquid()) {
                                newItem.setSizes(Math.min(template.getSizeX(), realSource.getSizeX()), Math.min(template.getSizeY(), realSource.getSizeY()), Math.min(template.getSizeZ(), realSource.getSizeZ()));
                            }
                            else {
                                newItem.setSizes((int)Math.min(template.getSizeX() * ((nums < 3) ? nums : (2.0f + nums / 3.0f)), realSource.getSizeX()), (int)Math.min(template.getSizeY() * Math.max(1.0f, nums / 3.0f), realSource.getSizeY()), (int)Math.min(template.getSizeZ() * Math.max(1.0f, nums / 3.0f), realSource.getSizeZ()));
                            }
                        }
                        else if (this.depleteTarget) {
                            final int nums = this.getTemplateNumbersForTarget(realTarget, template);
                            newWeight = Math.min(targetWeightToRemove2, nums * template.getWeightGrams());
                            newItem.setWeight(newWeight, false);
                            if (template.isLiquid()) {
                                newItem.setSizes(Math.min(template.getSizeX(), realTarget.getSizeX()), Math.min(template.getSizeY(), realTarget.getSizeY()), Math.min(template.getSizeZ(), realTarget.getSizeZ()));
                            }
                            else {
                                newItem.setSizes((int)Math.min(template.getSizeX() * ((nums < 3) ? nums : (2.0f + nums / 3.0f)), realTarget.getSizeX()), (int)Math.min(template.getSizeY() * Math.max(1.0f, nums / 3.0f), realTarget.getSizeY()), (int)Math.min(template.getSizeZ() * Math.max(1.0f, nums / 3.0f), realTarget.getSizeZ()));
                            }
                        }
                    }
                    if (!performer.skippedTutorial() && performer.getTutorialLevel() != 9999) {
                        if (performer.getTutorialLevel() == 4 && newItem.getTemplateId() == 36) {
                            performer.missionFinished(true, true);
                        }
                        if (performer.getTutorialLevel() == 5 && newItem.getTemplateId() == 37) {
                            performer.missionFinished(true, true);
                        }
                        if (performer.getTutorialLevel() == 5 && newItem.getTemplateId() == 37) {
                            performer.missionFinished(true, true);
                        }
                        if (performer.getTutorialLevel() == 9 && newItem.getTemplateId() == 22) {
                            performer.missionFinished(true, true);
                        }
                    }
                    if (template.isKingdomFlag) {
                        newItem.setAuxData(performer.getKingdomId());
                    }
                    if (newItem.getRarity() > 2) {
                        performer.achievement(300);
                    }
                    else if (newItem.getRarity() > 1) {
                        performer.achievement(302);
                    }
                    else if (newItem.getRarity() > 0) {
                        performer.achievement(301);
                    }
                    if (newItem != null) {
                        if (t == 133 && realSource.getTemplateId() == 1254) {
                            newItem.setRealTemplate(1254);
                        }
                        if (realSource.isKey()) {
                            final long lockId = realSource.getLockId();
                            newItem.setData(lockId);
                        }
                        else if (template.isKey()) {
                            try {
                                newItem.setLockId(realSource.getData());
                                final Item lock = Items.getItem(realSource.getData());
                                lock.addKey(newItem.getWurmId());
                            }
                            catch (NoSuchItemException nsi) {
                                SimpleCreationEntry.logger.log(Level.WARNING, performer.getName() + " No lock for key copy ", nsi);
                            }
                        }
                        else if (realSource.isPassFullData()) {
                            newItem.setData(realSource.getData());
                        }
                        else if (realTarget.isPassFullData()) {
                            newItem.setData(realTarget.getData());
                        }
                        else if (this.objectCreated == 846 || this.objectCreated == 847 || this.objectCreated == 848 || this.objectCreated == 849) {
                            newItem.setData2(realTarget.getData2());
                            if (realTarget.getData2() == 14) {
                                newItem.setWeight(400, true);
                            }
                            newItem.setMaterial((byte)16);
                        }
                        if (newItem.getWeightGrams() <= 0 || newItem.deleted) {
                            Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                            newItem = null;
                            if (sendFailedMessage) {
                                performer.getCommunicator().sendNormalServerMessage("You failed to create " + template.getNameWithGenus() + ".");
                            }
                        }
                    }
                    if (newItem != null) {
                        final int extraWeight = this.getExtraWeight(template);
                        if (extraWeight > 10 && !realTarget.isLiquid()) {
                            final int itc = CreationEntry.getScrapMaterial(material);
                            if (itc != -1) {
                                try {
                                    final Item scrap = ItemFactory.createItem(itc, realTarget.getCurrentQualityLevel() / 10.0f, material, (byte)0, performer.getName());
                                    scrap.setWeight(extraWeight, false);
                                    scrap.setTemperature(realTarget.getTemperature());
                                    performer.getInventory().insertItem(scrap, true);
                                }
                                catch (NoSuchTemplateException nst5) {
                                    SimpleCreationEntry.logger.log(Level.WARNING, performer.getName() + " tid= " + itc + ", " + nst5.getMessage(), nst5);
                                }
                            }
                        }
                        if (newItem.isWeaponSword()) {
                            performer.achievement(198);
                        }
                        else if (newItem.isWeaponCrush()) {
                            performer.achievement(199);
                        }
                        else if (newItem.isWeaponAxe()) {
                            performer.achievement(200);
                        }
                        else if (newItem.isShield()) {
                            performer.achievement(201);
                        }
                        if (newItem.getTemplateId() == 63) {
                            performer.achievement(515);
                        }
                        else if (newItem.getTemplateId() == 218 || newItem.getTemplateId() == 217) {
                            performer.achievement(517);
                        }
                        else if (newItem.getTemplateId() == 223) {
                            performer.achievement(530);
                        }
                        else if (newItem.isHolyItem(performer.getDeity()) && newItem.getMaterial() == 96) {
                            performer.achievement(616);
                        }
                        else if (newItem.getTemplateId() == 324) {
                            performer.achievement(628);
                        }
                        if (newItem.getTemplate().isRune()) {
                            performer.achievement(489);
                        }
                        if (!newItem.getTemplate().isMetalLump()) {
                            switch (newItem.getMaterial()) {
                                case 11: {
                                    performer.achievement(495);
                                    break;
                                }
                                case 56: {
                                    performer.achievement(496);
                                    break;
                                }
                                case 30: {
                                    performer.achievement(497);
                                    break;
                                }
                                case 31: {
                                    performer.achievement(498);
                                    break;
                                }
                                case 10: {
                                    performer.achievement(499);
                                    break;
                                }
                                case 57: {
                                    performer.achievement(500);
                                    break;
                                }
                                case 7: {
                                    performer.achievement(501);
                                    break;
                                }
                                case 12: {
                                    performer.achievement(502);
                                    break;
                                }
                                case 67: {
                                    performer.achievement(503);
                                    break;
                                }
                                case 9: {
                                    performer.achievement(505);
                                    break;
                                }
                                case 34: {
                                    performer.achievement(506);
                                    break;
                                }
                                case 13: {
                                    performer.achievement(507);
                                    break;
                                }
                                case 8: {
                                    performer.achievement(504);
                                    break;
                                }
                                case 96: {
                                    performer.achievement(512);
                                    break;
                                }
                            }
                        }
                    }
                    if (newItem.getTemplateId() == 36) {
                        PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.CREATE_KINDLING);
                    }
                    else if (newItem.getTemplateId() == 37) {
                        PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.CREATE_CAMPFIRE);
                    }
                }
            }
            else {
                String mess = "You realize this was a meaningless effort. The " + template.getName() + " is useless.";
                if (qlevel < 20.0f) {
                    mess = "You almost made it, but the " + template.getName() + " is useless.";
                }
                else if (qlevel < 40.0f) {
                    mess = "This could very well work next time, but the " + template.getName() + " is useless.";
                }
                else if (qlevel < 60.0f) {
                    mess = "Too many problems solved in the wrong way makes the " + template.getName() + " useless.";
                }
                else if (qlevel < 80.0f) {
                    mess = "You fail miserably with the " + template.getName() + ".";
                }
                performer.getCommunicator().sendNormalServerMessage(mess);
                Server.getInstance().broadCastAction(performer.getName() + " fails with the " + template.getName() + ".", performer, 5);
                if (!realTarget.isKey() && !realTarget.isBodyPart()) {
                    if (realTarget.isRepairable() || realTarget.getTemplateId() == 288) {
                        realTarget.setDamage(realTarget.getDamage() + Math.abs(qlevel / 5000.0f));
                    }
                    else if (!realTarget.isCombine() && !realTarget.isLiquid() && realTarget.getQualityLevel() >= 2.0f && realTarget.getQualityLevel() > primSkill.getKnowledge(0.0)) {
                        realTarget.setQualityLevel(realTarget.getQualityLevel() - 0.5f);
                    }
                    else if (this.objectCreated == 1114) {
                        realTarget.setDamage(realTarget.getDamage() + Math.abs(qlevel / 10.0f));
                    }
                    else if (realTarget.isCombine() || realTarget.isLiquid()) {
                        realTarget.setWeight(realTarget.getWeightGrams() - (int)(template.getWeightGrams() / 10.0f), true);
                    }
                }
                if (!realSource.isKey() && !realSource.isBodyPart()) {
                    if (realSource.isRepairable() || realSource.getTemplateId() == 288) {
                        realSource.setDamage(realSource.getDamage() + Math.abs(qlevel / 5000.0f));
                    }
                    else if (!realSource.isCombine() && !realSource.isLiquid() && realSource.getQualityLevel() >= 2.0f && realSource.getQualityLevel() > primSkill.getKnowledge(0.0)) {
                        realSource.setQualityLevel(realSource.getQualityLevel() - 0.5f);
                    }
                    else if (this.objectCreated == 1114) {
                        realSource.setDamage(realSource.getDamage() + Math.abs(qlevel / 10.0f));
                    }
                    else if (realSource.isCombine() || realSource.isLiquid()) {
                        if (realSource.getTemplateId() == 73) {
                            realSource.setWeight(realSource.getWeightGrams() - 50, true);
                        }
                        else {
                            realSource.setWeight(realSource.getWeightGrams() - (int)(template.getWeightGrams() / 10.0f), true);
                        }
                    }
                }
            }
            Item parent2 = realSource;
            Label_6652: {
                if (newItem != null && newItem.isLiquid()) {
                    if (!realSource.isContainerLiquid()) {
                        try {
                            parent2 = realSource.getParent();
                        }
                        catch (NoSuchItemException ex4) {}
                    }
                    if (parent2 != null) {
                        if (parent2.isContainerLiquid()) {
                            break Label_6652;
                        }
                    }
                    try {
                        final Item parent3 = Items.getItem(targetParentId);
                        if (parent3.isEmpty(false)) {
                            parent2 = parent3;
                        }
                    }
                    catch (NoSuchItemException ex5) {}
                }
            }
            if (newItem != null) {
                boolean canTransferSource = false;
                boolean canTransferTarget = false;
                CreationEntry ce = CreationMatrix.getInstance().getCreationEntry(realSource.getTemplateId());
                if (ce != null) {
                    if (ce.getCategory() == CreationCategories.WEAPON_HEADS) {
                        canTransferSource = true;
                    }
                    else if (ce.getCategory() == CreationCategories.TOOL_PARTS) {
                        canTransferSource = true;
                    }
                    else if (ce.getCategory() == CreationCategories.BLADES) {
                        canTransferSource = true;
                    }
                }
                ce = CreationMatrix.getInstance().getCreationEntry(realTarget.getTemplateId());
                if (ce != null) {
                    if (ce.getCategory() == CreationCategories.WEAPON_HEADS) {
                        canTransferTarget = true;
                    }
                    else if (ce.getCategory() == CreationCategories.TOOL_PARTS) {
                        canTransferTarget = true;
                    }
                    else if (ce.getCategory() == CreationCategories.BLADES) {
                        canTransferTarget = true;
                    }
                }
                if (!realSource.isLiquid() && sourceWeightToRemove2 >= realSource.getWeightGrams() && canTransferSource) {
                    if (realSource.getRarity() > newItem.getRarity()) {
                        newItem.setRarity(realSource.getRarity());
                    }
                    if (realSource.getSpellEffects() != null) {
                        final ItemSpellEffects deletedEffects = realSource.getSpellEffects();
                        ItemSpellEffects newEffects = newItem.getSpellEffects();
                        if (newEffects == null) {
                            newEffects = new ItemSpellEffects(newItem.getWurmId());
                        }
                        for (final SpellEffect e : deletedEffects.getEffects()) {
                            if (newEffects.getSpellEffect(e.type) != null) {
                                newEffects.getSpellEffect(e.type).setPower(Math.max(e.getPower(), newEffects.getSpellEffect(e.type).getPower()));
                            }
                            else {
                                newEffects.addSpellEffect(new SpellEffect(newItem.getWurmId(), e.type, e.getPower(), 20000000));
                            }
                        }
                    }
                }
                if (!realTarget.isLiquid() && targetWeightToRemove2 >= realTarget.getWeightGrams() && canTransferTarget) {
                    if (realTarget.getRarity() > newItem.getRarity()) {
                        newItem.setRarity(realTarget.getRarity());
                    }
                    if (realTarget.getSpellEffects() != null) {
                        final ItemSpellEffects deletedEffects = realTarget.getSpellEffects();
                        ItemSpellEffects newEffects = newItem.getSpellEffects();
                        if (newEffects == null) {
                            newEffects = new ItemSpellEffects(newItem.getWurmId());
                        }
                        for (final SpellEffect e : deletedEffects.getEffects()) {
                            if (newEffects.getSpellEffect(e.type) != null) {
                                newEffects.getSpellEffect(e.type).setPower(Math.max(e.getPower(), newEffects.getSpellEffect(e.type).getPower()));
                            }
                            else {
                                newEffects.addSpellEffect(new SpellEffect(newItem.getWurmId(), e.type, e.getPower(), 20000000));
                            }
                        }
                    }
                }
            }
            destroyItems(newItem, realSource, realTarget, mixedLiquids, targetWeightToRemove2, sourceWeightToRemove2);
            if (realTarget.getTemplateId() == 385 && realTarget.getWeightGrams() <= 24000) {
                realTarget.setTemplateId(9);
            }
            if (newItem != null && newItem.isLiquid() && parent2 != null) {
                MethodsItems.fillContainer(act, parent2, newItem, performer, false);
                if (!newItem.deleted && newItem.getParentId() == -10L) {
                    performer.getCommunicator().sendNormalServerMessage("Not all the " + newItem.getName() + " would fit in the " + parent2.getName() + ".");
                    Items.decay(newItem.getWurmId(), newItem.getDbStrings());
                    newItem = null;
                }
            }
        }
        catch (Exception ex) {
            SimpleCreationEntry.logger.log(Level.WARNING, "Failed to create item.", ex);
        }
        performer.getStatus().modifyStamina(-counter * 1000.0f);
        if (newItem != null) {
            MissionTriggers.activateTriggers(performer, newItem, 148, 0L, 1);
            return newItem;
        }
        throw new NoSuchItemException("The item was not created.");
    }
    
    private static final void destroyItems(final Item newItem, final Item realSource, final Item realTarget, final boolean mixedLiquids, final int targetWeightToRemove, final int sourceWeightToRemove) {
        if (newItem != null) {
            if (mixedLiquids) {
                Items.destroyItem(realTarget.getWurmId());
                Items.destroyItem(realSource.getWurmId());
            }
            else {
                if (sourceWeightToRemove > 0) {
                    if (newItem.isLiquid() && realSource.isLiquid()) {
                        Items.destroyItem(realSource.getWurmId());
                    }
                    else if (sourceWeightToRemove < realSource.getWeightGrams()) {
                        realSource.setWeight(realSource.getWeightGrams() - sourceWeightToRemove, true);
                    }
                    else {
                        Items.destroyItem(realSource.getWurmId());
                    }
                }
                else if (realSource.isRepairable()) {
                    realSource.setDamage(realSource.getDamage() + 0.004f * realSource.getDamageModifier());
                }
                if (targetWeightToRemove > 0) {
                    if (targetWeightToRemove < realTarget.getWeightGrams()) {
                        realTarget.setWeight(realTarget.getWeightGrams() - targetWeightToRemove, true);
                    }
                    else {
                        Items.destroyItem(realTarget.getWurmId());
                    }
                }
                else if (realTarget.isRepairable()) {
                    realTarget.setDamage(realTarget.getDamage() + 0.004f * realTarget.getDamageModifier());
                }
            }
        }
    }
    
    private void sendSound(final Creature aPerformer, final int aObjectSource, final Item aTarget, final int aObjectCreated) {
        String soundId = "";
        if (aObjectSource == 24) {
            soundId = "sound.work.carpentry.saw";
        }
        else if (aObjectSource == 8) {
            soundId = "sound.work.carpentry.carvingknife";
        }
        else if (aObjectSource == 62) {
            soundId = "sound.work.smithing.hammer";
        }
        else if (aObjectSource == 64 || aObjectSource == 185) {
            soundId = "sound.work.smithing.hammer";
        }
        else if (aObjectSource == 226) {
            soundId = "sound.work.tailoring.loom";
        }
        else if (aObjectSource == 139) {
            soundId = "sound.work.tailoring.spindle";
        }
        else if (aObjectSource == 97) {
            soundId = "sound.work.stonecutting";
        }
        else if (aObjectCreated == 36) {
            soundId = "sound.work.woodcutting.kindling";
        }
        if (soundId.length() > 0) {
            SoundPlayer.playSound(soundId, aPerformer, 1.0f);
        }
    }
    
    static {
        logger = Logger.getLogger(SimpleCreationEntry.class.getName());
    }
}

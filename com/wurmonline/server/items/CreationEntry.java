// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.FailedException;
import java.util.logging.Level;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.ItemMaterials;

public abstract class CreationEntry implements ItemTypes, ItemMaterials, MiscConstants
{
    final int primarySkill;
    final int objectSource;
    protected byte objectSourceMaterial;
    final int objectTarget;
    protected byte objectTargetMaterial;
    final int objectCreated;
    final boolean destroyTarget;
    final boolean useCapacity;
    final float percentageLost;
    final int minTimeSeconds;
    final boolean destroyBoth;
    private static final Logger logger;
    final boolean createOnGround;
    public boolean depleteEqually;
    public boolean depleteSource;
    public boolean depleteTarget;
    public static final float TUTORIALCHANCE = 90.0f;
    public boolean isOnlyCreateEpicTargetMission;
    public boolean isCreateEpicTargetMission;
    private boolean hasCustomChanceCutoff;
    private int customCreationChanceCutOff;
    private boolean hasMinimumSkillRequirement;
    private double minimumSkill;
    private int depleteFromSource;
    private int depleteFromTarget;
    private CreationCategories category;
    private int deity;
    private boolean useTemplateWeight;
    private boolean colouringCreation;
    private boolean useFinalMaterial;
    private byte finalMaterial;
    protected static final CreationRequirement[] emptyReqs;
    
    CreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDestroyTarget, final boolean aUseCapacity, final float aPercentageLost, final int aMinTimeSeconds, final boolean aDestroyBoth, final boolean aCreateOnGround, final CreationCategories aCategory) {
        this.objectSourceMaterial = 0;
        this.objectTargetMaterial = 0;
        this.depleteSource = false;
        this.depleteTarget = false;
        this.isOnlyCreateEpicTargetMission = false;
        this.isCreateEpicTargetMission = true;
        this.hasCustomChanceCutoff = false;
        this.customCreationChanceCutOff = 0;
        this.hasMinimumSkillRequirement = false;
        this.minimumSkill = 0.0;
        this.depleteFromSource = 0;
        this.depleteFromTarget = 0;
        this.deity = 0;
        this.useTemplateWeight = false;
        this.colouringCreation = false;
        this.useFinalMaterial = false;
        this.finalMaterial = 0;
        this.primarySkill = aPrimarySkill;
        this.objectSource = aObjectSource;
        this.objectTarget = aObjectTarget;
        this.objectCreated = aObjectCreated;
        this.destroyTarget = aDestroyTarget;
        this.depleteTarget = this.destroyTarget;
        this.useCapacity = aUseCapacity;
        this.depleteSource = this.useCapacity;
        this.percentageLost = aPercentageLost;
        this.minTimeSeconds = aMinTimeSeconds;
        this.destroyBoth = aDestroyBoth;
        this.depleteEqually = this.destroyBoth;
        this.createOnGround = aCreateOnGround;
        this.category = aCategory;
    }
    
    public boolean isAdvanced() {
        return false;
    }
    
    CreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final boolean aDepleteEqually, final float aPercentageLost, final boolean aCreateOnGround, final CreationCategories aCategory) {
        this.objectSourceMaterial = 0;
        this.objectTargetMaterial = 0;
        this.depleteSource = false;
        this.depleteTarget = false;
        this.isOnlyCreateEpicTargetMission = false;
        this.isCreateEpicTargetMission = true;
        this.hasCustomChanceCutoff = false;
        this.customCreationChanceCutOff = 0;
        this.hasMinimumSkillRequirement = false;
        this.minimumSkill = 0.0;
        this.depleteFromSource = 0;
        this.depleteFromTarget = 0;
        this.deity = 0;
        this.useTemplateWeight = false;
        this.colouringCreation = false;
        this.useFinalMaterial = false;
        this.finalMaterial = 0;
        this.primarySkill = aPrimarySkill;
        this.objectSource = aObjectSource;
        this.objectTarget = aObjectTarget;
        this.objectCreated = aObjectCreated;
        this.depleteTarget = aDepleteTarget;
        this.destroyTarget = this.depleteTarget;
        this.depleteSource = aDepleteSource;
        this.percentageLost = aPercentageLost;
        this.depleteEqually = false;
        if (aDepleteEqually) {
            this.depleteTarget = true;
            this.depleteSource = true;
        }
        this.destroyBoth = aDepleteEqually;
        this.minTimeSeconds = 5;
        this.useCapacity = this.depleteSource;
        this.createOnGround = aCreateOnGround;
        this.category = aCategory;
    }
    
    CreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final boolean aDepleteEqually, final float aPercentageLost, final boolean aCreateOnGround, final int aCustomCutOffChance, final double aMinimumSkill, final CreationCategories aCategory) {
        this(aPrimarySkill, aObjectSource, aObjectTarget, aObjectCreated, aDepleteSource, aDepleteTarget, aDepleteEqually, aPercentageLost, aCreateOnGround, aCategory);
        if (aCustomCutOffChance != 0) {
            this.customCreationChanceCutOff = aCustomCutOffChance;
            this.hasCustomChanceCutoff = true;
        }
        if (aMinimumSkill != 0.0) {
            this.minimumSkill = aMinimumSkill;
            this.hasMinimumSkillRequirement = true;
        }
    }
    
    CreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final float aPercentageLost, final boolean aDepleteEqually, final boolean aCreateOnGround, final CreationCategories aCategory) {
        this.objectSourceMaterial = 0;
        this.objectTargetMaterial = 0;
        this.depleteSource = false;
        this.depleteTarget = false;
        this.isOnlyCreateEpicTargetMission = false;
        this.isCreateEpicTargetMission = true;
        this.hasCustomChanceCutoff = false;
        this.customCreationChanceCutOff = 0;
        this.hasMinimumSkillRequirement = false;
        this.minimumSkill = 0.0;
        this.depleteFromSource = 0;
        this.depleteFromTarget = 0;
        this.deity = 0;
        this.useTemplateWeight = false;
        this.colouringCreation = false;
        this.useFinalMaterial = false;
        this.finalMaterial = 0;
        this.primarySkill = aPrimarySkill;
        this.objectSource = aObjectSource;
        this.objectTarget = aObjectTarget;
        this.objectCreated = aObjectCreated;
        this.depleteTarget = aDepleteTarget;
        this.destroyTarget = this.depleteTarget;
        this.depleteSource = aDepleteSource;
        this.percentageLost = aPercentageLost;
        this.depleteEqually = aDepleteEqually;
        this.destroyBoth = this.depleteEqually;
        this.minTimeSeconds = 5;
        this.useCapacity = this.depleteSource;
        this.createOnGround = aCreateOnGround;
        this.category = aCategory;
    }
    
    CreationEntry(final int aPrimarySkill, final int aObjectSource, final int aObjectTarget, final int aObjectCreated, final boolean aDepleteSource, final boolean aDepleteTarget, final float aPercentageLost, final boolean aDepleteEqually, final boolean aCreateOnGround, final int aCustomCutOffChance, final double aMinimumSkill, final CreationCategories aCategory) {
        this(aPrimarySkill, aObjectSource, aObjectTarget, aObjectCreated, aDepleteSource, aDepleteTarget, aPercentageLost, aDepleteEqually, aCreateOnGround, aCategory);
        if (aCustomCutOffChance != 0) {
            this.customCreationChanceCutOff = aCustomCutOffChance;
            this.hasCustomChanceCutoff = true;
        }
        if (aMinimumSkill != 0.0) {
            this.minimumSkill = aMinimumSkill;
            this.hasMinimumSkillRequirement = true;
        }
    }
    
    public int getObjectCreated() {
        return this.objectCreated;
    }
    
    public final int getCustomCutOffChance() {
        return this.customCreationChanceCutOff;
    }
    
    public final CreationCategories getCategory() {
        return this.category;
    }
    
    public int getTotalNumberOfItems() {
        if (this.depleteSource && this.depleteTarget) {
            return 2;
        }
        return 1;
    }
    
    public CreationRequirement[] getRequirements() {
        return CreationEntry.emptyReqs;
    }
    
    public int getObjectSource() {
        return this.objectSource;
    }
    
    public byte getObjectSourceMaterial() {
        return this.objectSourceMaterial;
    }
    
    public void setObjectSourceMaterial(final byte sourceMaterial) {
        this.objectSourceMaterial = sourceMaterial;
    }
    
    public int getObjectTarget() {
        return this.objectTarget;
    }
    
    public byte getObjectTargetMaterial() {
        return this.objectTargetMaterial;
    }
    
    public void setObjectTargetMaterial(final byte targetMaterial) {
        this.objectTargetMaterial = targetMaterial;
    }
    
    public int getPrimarySkill() {
        return this.primarySkill;
    }
    
    boolean isDepleteSourceAndTarget() {
        return this.depleteEqually;
    }
    
    void setDepleteSourceAndTarget(final boolean aDepleteSourceAndTarget) {
        this.depleteEqually = aDepleteSourceAndTarget;
    }
    
    boolean isDestroyTarget() {
        return this.destroyTarget;
    }
    
    public final boolean hasCustomCreationChanceCutOff() {
        return this.hasCustomChanceCutoff;
    }
    
    public final boolean hasCustomDepleteFromSource() {
        return this.depleteFromSource != 0;
    }
    
    public final boolean hasCustomDepleteFromTarget() {
        return this.depleteFromTarget != 0;
    }
    
    public void setDepleteFromSource(final int toDeplete) {
        this.depleteFromSource = toDeplete;
    }
    
    public void setDepleteFromTarget(final int toDeplete) {
        this.depleteFromTarget = toDeplete;
    }
    
    public final int getDepleteFromTarget() {
        return this.depleteFromTarget;
    }
    
    public final int getDepleteFromSource() {
        return this.depleteFromSource;
    }
    
    public final boolean isRestrictedToDeityFollower() {
        return this.deity != 0;
    }
    
    public void setDeityRestriction(final int deity) {
        this.deity = deity;
    }
    
    public final int getDeityRestriction() {
        return this.deity;
    }
    
    public final boolean usesFinalMaterial() {
        return this.useFinalMaterial;
    }
    
    public void setFinalMaterial(final byte material) {
        this.finalMaterial = material;
        if (material == 0) {
            this.useFinalMaterial = false;
            return;
        }
        this.useFinalMaterial = true;
    }
    
    public final byte getFinalMaterial() {
        return this.finalMaterial;
    }
    
    boolean isUseCapacity() {
        return this.useCapacity;
    }
    
    float getPercentageLost() {
        return this.percentageLost;
    }
    
    public void setUseTemplateWeight(final boolean templateWeight) {
        this.useTemplateWeight = templateWeight;
    }
    
    public final boolean getUseTempalateWeight() {
        return this.useTemplateWeight;
    }
    
    public final boolean isColouringCreation() {
        return this.colouringCreation;
    }
    
    public void setColouringCreation(final boolean addsColour) {
        this.colouringCreation = addsColour;
    }
    
    public void setIsEpicBuildMissionTarget(final boolean target_ok) {
        this.isCreateEpicTargetMission = target_ok;
    }
    
    int getMinTimeSeconds() {
        return this.minTimeSeconds;
    }
    
    public final double getMinimumSkillRequirement() {
        return this.minimumSkill;
    }
    
    public final boolean hasMinimumSkillRequirement() {
        return this.hasMinimumSkillRequirement;
    }
    
    boolean isDestroyBoth() {
        return this.destroyBoth;
    }
    
    public float getDifficultyFor(final Item source, final Item target, final Creature performer) throws NoSuchTemplateException {
        Item realSource = source;
        if (source.getTemplateId() == this.objectTarget && target.getTemplateId() == this.objectSource && source.getTemplateId() != target.getTemplateId()) {
            realSource = target;
        }
        final Skills skills = performer.getSkills();
        Skill primSkill = null;
        Skill secondarySkill = null;
        double bonus = 0.0;
        try {
            primSkill = skills.getSkill(this.primarySkill);
            if (this.hasMinimumSkillRequirement() && this.getMinimumSkillRequirement() > primSkill.getKnowledge(0.0)) {
                return 0.0f;
            }
        }
        catch (Exception ex) {}
        try {
            secondarySkill = skills.getSkill(realSource.getPrimarySkill());
        }
        catch (Exception ex2) {}
        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(this.objectCreated);
        if (secondarySkill != null) {
            bonus = Math.max(1.0, secondarySkill.getKnowledge(realSource, 0.0) / 10.0);
        }
        float chance = 0.0f;
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
        if (template.isTutorialItem()) {
            return 90.0f;
        }
        if (primSkill != null) {
            chance = (float)primSkill.getChance(template.getDifficulty(), realSource, bonus);
        }
        else {
            chance = 1.0f / (1.0f + template.getDifficulty()) * 100.0f;
        }
        return chance;
    }
    
    public boolean isCreateOnGround() {
        return this.createOnGround;
    }
    
    public static final int getScrapMaterial(final byte material) {
        if (Materials.isWood(material)) {
            return 169;
        }
        if (material == 11) {
            return 46;
        }
        if (material == 17) {
            return 171;
        }
        if (material == 2 || material == 22) {
            return 173;
        }
        if (material == 16) {
            return 172;
        }
        if (material == 10) {
            return 47;
        }
        if (material == 7) {
            return 44;
        }
        if (material == 8) {
            return 45;
        }
        if (material == 13) {
            return 48;
        }
        if (material == 12) {
            return 49;
        }
        if (material == 30) {
            return 221;
        }
        if (material == 31) {
            return 223;
        }
        if (material == 34) {
            return 220;
        }
        if (material == 9) {
            return 205;
        }
        if (material == 56) {
            return 694;
        }
        if (material == 57) {
            return 698;
        }
        if (material == 26) {
            return 634;
        }
        if (material == 67) {
            return 837;
        }
        if (material == 96) {
            return 1411;
        }
        return -1;
    }
    
    protected final void checkSaneAmounts(final Item realSource, final int sourceWeightToRemove, final Item realTarget, final int targetWeightToRemove, final ItemTemplate template, final Creature performer, final boolean advancedItem) throws NoSuchItemException {
        if ((this.depleteSource && sourceWeightToRemove <= 0) || (this.depleteTarget && targetWeightToRemove <= 0)) {
            performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " or the " + realTarget.getName() + " contains too little material to create " + template.getNameWithGenus() + ". You need to find more.");
            throw new NoSuchItemException("Too little material.");
        }
        if (!advancedItem && ((this.depleteSource && this.depleteTarget) || this.depleteEqually) && realSource.getWeightGrams(false) + realTarget.getWeightGrams(false) < template.getWeightGrams()) {
            if (realSource.isCombine() || realSource.isLiquid() || realTarget.isLiquid() || realTarget.isCombine()) {
                performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " and the " + realTarget.getName() + " contains too little material to create " + template.getNameWithGenus() + ".  Try to combine any of them with a similar object to get larger pieces.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " and the " + realTarget.getName() + " contains too little material to create " + template.getNameWithGenus() + ". You need to find larger parts.");
            }
            throw new NoSuchItemException("Too little material.");
        }
        if (!advancedItem && ((this.depleteSource && this.depleteTarget) || this.depleteEqually) && (realTarget.isCombine() || realTarget.isLiquid()) && (realSource.isCombine() || realSource.isLiquid())) {
            int sourceMax = (sourceWeightToRemove <= realSource.getWeightGrams()) ? 1 : 0;
            int targetMax = (targetWeightToRemove <= realTarget.getWeightGrams()) ? 1 : 0;
            if (template.isCombine() && this.objectCreated != 73) {
                sourceMax = (int)(realSource.getWeightGrams() / (template.getWeightGrams() / 2.0f));
                targetMax = (int)(realTarget.getWeightGrams() / (template.getWeightGrams() / 2.0f));
            }
            if (sourceMax == 0 || targetMax == 0) {
                performer.getCommunicator().sendNormalServerMessage("The amount of materials is too low to produce anything.");
                throw new NoSuchItemException("Bad amounts of combined items.");
            }
        }
        if (realSource.getWeightGrams(false) < sourceWeightToRemove) {
            if (realSource.isCombine()) {
                performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " contains too little material to create " + template.getNameWithGenus() + ".  Try to combine it with a similar object to get a larger amount.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + realSource.getName() + " contains too little material to create " + template.getNameWithGenus() + ".");
            }
            throw new NoSuchItemException("Too little material.");
        }
        if (realTarget.getWeightGrams(false) < targetWeightToRemove) {
            if (realTarget.isCombine()) {
                performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getName() + " contains too little material to create " + template.getNameWithGenus() + ".  Try to combine it with a similar object to get a larger amount.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + realTarget.getName() + " contains too little material to create " + template.getNameWithGenus() + ".");
            }
            throw new NoSuchItemException("Too little material.");
        }
        if (realSource.isLiquid() && realTarget.isLiquid()) {
            final int sourceMax = realSource.getWeightGrams() / realSource.getTemplate().getWeightGrams();
            final int targetMax = realTarget.getWeightGrams() / realTarget.getTemplate().getWeightGrams();
            if (sourceMax < 1 || targetMax < 1 || sourceMax / targetMax > 2 || targetMax / sourceMax > 2) {
                if (sourceMax < 1) {
                    performer.getCommunicator().sendNormalServerMessage("You need more " + realSource.getName() + ".");
                }
                if (targetMax < 1) {
                    performer.getCommunicator().sendNormalServerMessage("You need more " + realTarget.getName() + ".");
                }
                else if (sourceMax != targetMax) {
                    if (sourceMax < targetMax) {
                        performer.getCommunicator().sendNormalServerMessage("You need to add more " + realSource.getName() + " or remove some " + realTarget.getName() + ".");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You need to add more " + realTarget.getName() + " or remove some " + realSource.getName() + ".");
                    }
                }
                throw new NoSuchItemException("Not balanced.");
            }
            Item parent = null;
            if (template.isLiquid()) {
                try {
                    parent = realSource.getParent();
                }
                catch (NoSuchItemException ex) {}
                try {
                    final Item parent2 = Items.getItem(realTarget.getParentId());
                    if ((parent == null || !parent.isContainerLiquid()) && parent2.isEmpty(false)) {
                        parent = parent2;
                    }
                }
                catch (NoSuchItemException ex2) {}
                if (parent != null && parent.getVolume() < realSource.getWeightGrams() + realTarget.getWeightGrams()) {
                    performer.getCommunicator().sendNormalServerMessage("Not all the liquid will fit in the " + parent.getName() + " so some will be lost.");
                }
            }
        }
    }
    
    public int getSourceWeightToRemove(final Item realSource, final Item realTarget, final ItemTemplate template, final boolean advancedEntry) {
        int weightToRemove = 0;
        if (this.hasCustomDepleteFromSource()) {
            weightToRemove = this.getDepleteFromSource();
        }
        else if (this.depleteEqually) {
            if (advancedEntry) {
                weightToRemove = realSource.getTemplate().getWeightGrams();
            }
            else {
                weightToRemove = template.getWeightGrams() / 2;
            }
        }
        else if (template.isLiquid()) {
            int nums = 1;
            if (this.depleteTarget && this.depleteSource) {
                nums = this.getTemplateNumbers(realSource, realTarget, template);
                weightToRemove = nums * realSource.getTemplate().getWeightGrams();
            }
            else if (this.depleteSource) {
                nums = this.getTemplateNumbersForSource(realSource, template);
                weightToRemove = nums * realSource.getTemplate().getWeightGrams();
            }
            else {
                nums = 0;
            }
        }
        else if (this.depleteSource && this.depleteTarget && (realTarget.isCombine() || realTarget.isLiquid()) && (realSource.isCombine() || realSource.isLiquid())) {
            int nums = 1;
            if (advancedEntry) {
                nums = 1;
            }
            weightToRemove = nums * realSource.getTemplate().getWeightGrams();
            if (realSource.getTemplateId() == 73) {
                weightToRemove /= 10;
            }
        }
        else if (this.depleteSource) {
            weightToRemove = realSource.getTemplate().getWeightGrams();
            if (this.depleteTarget) {
                if (!advancedEntry && !realTarget.isCombine() && realSource.isCombine()) {
                    weightToRemove = Math.max(realSource.getTemplate().getWeightGrams(), template.getWeightGrams() - realTarget.getWeightGrams());
                }
                if (realSource.getTemplateId() == 9 && realSource.getWeightGrams() > realSource.getTemplate().getWeightGrams() * 0.7f) {
                    weightToRemove = realSource.getWeightGrams();
                }
                if (realSource.getTemplateId() == 73) {
                    weightToRemove /= 10;
                }
            }
        }
        return weightToRemove;
    }
    
    public int getTargetWeightToRemove(final Item realSource, final Item realTarget, final ItemTemplate template, final boolean advancedEntry) {
        int weightToRemove = 0;
        if (this.hasCustomDepleteFromTarget()) {
            weightToRemove = this.getDepleteFromTarget();
        }
        else if (this.depleteEqually) {
            if (advancedEntry) {
                weightToRemove = realTarget.getTemplate().getWeightGrams();
            }
            else {
                weightToRemove = template.getWeightGrams() / 2;
            }
        }
        else if (template.isLiquid()) {
            int nums = 1;
            if (this.depleteTarget && this.depleteSource) {
                nums = this.getTemplateNumbers(realSource, realTarget, template);
            }
            else if (this.depleteTarget) {
                nums = this.getTemplateNumbersForTarget(realTarget, template);
            }
            else {
                nums = 0;
            }
            weightToRemove = nums * realTarget.getTemplate().getWeightGrams();
        }
        else if (this.depleteSource && this.depleteTarget && (realTarget.isCombine() || realTarget.isLiquid()) && (realSource.isCombine() || realSource.isLiquid())) {
            int nums = 1;
            if (advancedEntry) {
                nums = 1;
            }
            weightToRemove = nums * (realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template));
            final int weightCap = (int)((template.getWeightGrams() + this.getExtraWeight(template)) * 1.5f);
            if (weightToRemove > weightCap) {
                weightToRemove = weightCap;
            }
        }
        else if (this.depleteTarget) {
            if (!realTarget.isCombine()) {
                if (!this.depleteSource) {
                    if (advancedEntry) {
                        weightToRemove = realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template);
                    }
                    else {
                        weightToRemove = template.getWeightGrams() + this.getExtraWeight(template);
                    }
                }
                else {
                    weightToRemove = realTarget.getTemplate().getWeightGrams();
                }
            }
            else if (this.depleteSource) {
                if (advancedEntry) {
                    weightToRemove = realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template);
                }
                else {
                    weightToRemove = template.getWeightGrams() - realSource.getTemplate().getWeightGrams() + this.getExtraWeight(template);
                }
            }
            else {
                weightToRemove = template.getWeightGrams() + this.getExtraWeight(template);
            }
        }
        if (weightToRemove < 0) {
            CreationEntry.logger.log(Level.WARNING, template.getName() + " when created depletes less than 0.");
            weightToRemove = realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template);
        }
        return weightToRemove;
    }
    
    protected final int getTemplateNumbersForTarget(final Item realTarget, final ItemTemplate template) {
        return Math.max(1, realTarget.getWeightGrams() / (realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template)));
    }
    
    protected final int getTemplateNumbersForSource(final Item realSource, final ItemTemplate template) {
        return Math.max(1, realSource.getWeightGrams() / (realSource.getTemplate().getWeightGrams() + this.getExtraWeight(template)));
    }
    
    protected final int getTemplateNumbers(final Item realSource, final Item realTarget, final ItemTemplate template) {
        return Math.max(1, Math.min(realSource.getWeightGrams() / realSource.getTemplate().getWeightGrams(), realTarget.getWeightGrams() / (realTarget.getTemplate().getWeightGrams() + this.getExtraWeight(template))));
    }
    
    protected final int getExtraWeight(final ItemTemplate template) {
        if (template.isRune()) {
            return 0;
        }
        if (this.percentageLost > 0.0f) {
            return (int)(this.percentageLost / 100.0f * template.getWeightGrams());
        }
        return 0;
    }
    
    public final boolean meetsCreatureRestriction(final Item source, final Item target) {
        if (this.objectCreated == 848) {
            final int data = 14;
            return source.getData2() == 14 || target.getData2() == 14;
        }
        if (this.objectCreated == 847) {
            final int data = 12;
            return source.getData2() == 12 || target.getData2() == 12;
        }
        if (this.objectCreated == 846) {
            final int data = 42;
            return source.getData2() == 42 || target.getData2() == 42;
        }
        if (this.objectCreated == 849) {
            final int data = 10;
            return source.getData2() == 10 || target.getData2() == 10;
        }
        return true;
    }
    
    public abstract Item run(final Creature p0, final Item p1, final long p2, final float p3) throws FailedException, NoSuchSkillException, NoSuchItemException;
    
    abstract CreationEntry cloneAndRevert();
    
    static {
        logger = Logger.getLogger(CreationEntry.class.getName());
        emptyReqs = new CreationRequirement[0];
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.players.AchievementTemplate;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.util.StringUtilities;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.Iterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class Recipe implements MiscConstants
{
    private static final Logger logger;
    public static final byte TIME = 0;
    public static final byte HEAT = 1;
    public static final byte CREATE = 2;
    public static final short DEBUG_RECIPE = 0;
    private final String name;
    private final short recipeId;
    private boolean known;
    private boolean nameable;
    private String skillName;
    private int skillId;
    private final Map<Short, String> cookers;
    private final Map<Short, Byte> cookersDif;
    private final Map<Short, String> containers;
    private final Map<Short, Byte> containersDif;
    private byte trigger;
    private Ingredient activeItem;
    private Ingredient targetItem;
    private Ingredient resultItem;
    private final List<IngredientGroup> ingredientGroups;
    private int achievementId;
    private String achievementName;
    private final Map<Byte, Ingredient> allIngredients;
    private boolean lootable;
    private int lootableCreature;
    private byte lootableRarity;
    
    public Recipe(final String name, final short recipeId) {
        this.known = false;
        this.nameable = false;
        this.skillName = "";
        this.skillId = -1;
        this.cookers = new HashMap<Short, String>();
        this.cookersDif = new HashMap<Short, Byte>();
        this.containers = new HashMap<Short, String>();
        this.containersDif = new HashMap<Short, Byte>();
        this.trigger = 2;
        this.activeItem = null;
        this.targetItem = null;
        this.resultItem = null;
        this.ingredientGroups = new ArrayList<IngredientGroup>();
        this.achievementId = -1;
        this.achievementName = "";
        this.allIngredients = new HashMap<Byte, Ingredient>();
        this.lootable = false;
        this.lootableCreature = -10;
        this.lootableRarity = 0;
        this.name = name;
        this.recipeId = recipeId;
    }
    
    public Recipe(final short recipeId) {
        this.known = false;
        this.nameable = false;
        this.skillName = "";
        this.skillId = -1;
        this.cookers = new HashMap<Short, String>();
        this.cookersDif = new HashMap<Short, Byte>();
        this.containers = new HashMap<Short, String>();
        this.containersDif = new HashMap<Short, Byte>();
        this.trigger = 2;
        this.activeItem = null;
        this.targetItem = null;
        this.resultItem = null;
        this.ingredientGroups = new ArrayList<IngredientGroup>();
        this.achievementId = -1;
        this.achievementName = "";
        this.allIngredients = new HashMap<Byte, Ingredient>();
        this.lootable = false;
        this.lootableCreature = -10;
        this.lootableRarity = 0;
        this.recipeId = recipeId;
        final Recipe templateRecipe = Recipes.getRecipeById(this.recipeId);
        if (templateRecipe != null) {
            this.name = templateRecipe.name;
            this.setDefaults(templateRecipe);
        }
        else {
            this.name = "Null Recipe " + this.recipeId;
            Recipe.logger.warning("Null recipe with ID: " + this.recipeId);
        }
    }
    
    public Recipe(final DataInputStream dis) throws IOException, NoSuchTemplateException {
        this.known = false;
        this.nameable = false;
        this.skillName = "";
        this.skillId = -1;
        this.cookers = new HashMap<Short, String>();
        this.cookersDif = new HashMap<Short, Byte>();
        this.containers = new HashMap<Short, String>();
        this.containersDif = new HashMap<Short, Byte>();
        this.trigger = 2;
        this.activeItem = null;
        this.targetItem = null;
        this.resultItem = null;
        this.ingredientGroups = new ArrayList<IngredientGroup>();
        this.achievementId = -1;
        this.achievementName = "";
        this.allIngredients = new HashMap<Byte, Ingredient>();
        this.lootable = false;
        this.lootableCreature = -10;
        this.lootableRarity = 0;
        this.recipeId = dis.readShort();
        final Recipe templateRecipe = Recipes.getRecipeById(this.recipeId);
        if (templateRecipe != null) {
            this.name = templateRecipe.name;
            this.setDefaults(templateRecipe);
        }
        else {
            this.name = "Null Recipe " + this.recipeId;
        }
        final byte cookerCount = dis.readByte();
        if (cookerCount > 0) {
            for (int ic = 0; ic < cookerCount; ++ic) {
                final short cookerid = dis.readShort();
                this.addToCookerList(cookerid);
            }
        }
        final byte containerCount = dis.readByte();
        if (containerCount > 0) {
            for (int ic2 = 0; ic2 < containerCount; ++ic2) {
                final short containerid = dis.readShort();
                this.addToContainerList(containerid);
            }
        }
        final boolean hasActiveItem = dis.readBoolean();
        if (hasActiveItem) {
            this.setActiveItem(new Ingredient(dis));
        }
        final boolean hasTargetItem = dis.readBoolean();
        if (hasTargetItem) {
            this.setTargetItem(new Ingredient(dis));
        }
        final byte groupCount = dis.readByte();
        if (groupCount > 0) {
            for (int ic3 = 0; ic3 < groupCount; ++ic3) {
                final IngredientGroup ig = new IngredientGroup(dis);
                if (ig.size() > 0) {
                    this.addToIngredientGroupList(ig);
                }
                else {
                    Recipe.logger.warning("recipe contains empty IngredientGroup: [" + this.recipeId + "] " + this.name);
                }
                for (final Ingredient i : ig.getIngredients()) {
                    this.allIngredients.put(i.getIngredientId(), i);
                }
            }
        }
    }
    
    public void pack(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.recipeId);
        dos.writeByte(this.cookers.size());
        for (final Short cooker : this.cookers.keySet()) {
            dos.writeShort(cooker);
        }
        dos.writeByte(this.containers.size());
        for (final Short container : this.containers.keySet()) {
            dos.writeShort(container);
        }
        dos.writeBoolean(this.hasActiveItem());
        if (this.hasActiveItem()) {
            this.activeItem.pack(dos);
        }
        dos.writeBoolean(this.hasTargetItem());
        if (this.hasTargetItem()) {
            this.targetItem.pack(dos);
        }
        final ArrayList<IngredientGroup> toSend = new ArrayList<IngredientGroup>();
        for (final IngredientGroup ig : this.ingredientGroups) {
            if (ig.size() > 0) {
                toSend.add(ig);
            }
        }
        dos.writeByte(toSend.size());
        for (final IngredientGroup ig : toSend) {
            ig.pack(dos);
        }
    }
    
    public String getRecipeName() {
        return this.name;
    }
    
    public String getName() {
        if (!this.nameable) {
            return this.name;
        }
        final String namer = Recipes.getRecipeNamer(this.recipeId);
        if (namer != null && namer.length() > 0) {
            return namer + "'s " + this.name;
        }
        return this.name + "+";
    }
    
    public short getRecipeId() {
        return this.recipeId;
    }
    
    public byte getRecipeColourCode(final long playerId) {
        int colour = 0;
        if (this.lootable) {
            colour = this.lootableRarity;
        }
        if (this.isKnown()) {
            colour |= 0x4;
        }
        if (RecipesByPlayer.isFavourite(playerId, this.recipeId)) {
            colour |= 0x8;
        }
        if (!RecipesByPlayer.isKnownRecipe(playerId, this.recipeId)) {
            colour |= 0x10;
        }
        return (byte)colour;
    }
    
    public short getMenuId() {
        return (short)(this.recipeId + 8000);
    }
    
    byte getCurrentGroupId() {
        return (byte)(this.ingredientGroups.size() - 1);
    }
    
    public void setLootable(final int creatureId, final byte rarity) {
        if (creatureId != -10) {
            this.lootable = true;
            this.lootableCreature = creatureId;
            this.lootableRarity = rarity;
        }
        else {
            this.lootable = false;
        }
    }
    
    public boolean isLootable() {
        return this.lootable;
    }
    
    public int getLootableCreature() {
        return this.lootableCreature;
    }
    
    public byte getLootableRarity() {
        return this.lootableRarity;
    }
    
    public byte getIngredientCount() {
        return (byte)this.allIngredients.size();
    }
    
    public void addIngredient(final Ingredient ingredient) {
        final byte gId = ingredient.getGroupId();
        if (gId == -3) {
            this.setResultItem(ingredient);
        }
        else {
            final Ingredient old = this.allIngredients.put(ingredient.getIngredientId(), ingredient);
            if (old != null) {
                Recipe.logger.info("Recipe (" + this.recipeId + ") Overridden Ingredient (" + old.getIngredientId() + ") group (" + gId + ") old:" + old.getName(true) + " new:" + ingredient.getName(true) + ".");
            }
            if (gId == -2) {
                this.setActiveItem(ingredient);
            }
            else if (gId == -1) {
                this.setTargetItem(ingredient);
            }
            else {
                final IngredientGroup ig = this.getGroupById(gId);
                if (ig != null) {
                    ig.add(ingredient);
                }
                else {
                    Recipe.logger.log(Level.WARNING, "IngredientGroup is null for groupID: " + gId, new Exception());
                }
            }
        }
    }
    
    public Ingredient getIngredientById(final byte ingredientId) {
        return this.allIngredients.get(ingredientId);
    }
    
    public String getSubMenuName(final Item container) {
        final StringBuilder buf = new StringBuilder();
        if (this.resultItem.hasCState()) {
            buf.append(this.resultItem.getCStateName());
            if (this.resultItem.hasPState()) {
                buf.append(" " + this.resultItem.getPStateName());
            }
            buf.append(" ");
        }
        else if (this.resultItem.hasPState() && this.resultItem.getPState() != 0) {
            buf.append(this.resultItem.getPStateName() + " ");
        }
        buf.append(this.getResultName(container));
        return buf.toString();
    }
    
    void setKnown(final boolean known) {
        this.known = known;
    }
    
    public boolean isKnown() {
        return this.known;
    }
    
    void setNameable(final boolean nameable) {
        this.nameable = nameable;
    }
    
    public boolean isNameable() {
        return this.nameable;
    }
    
    public void setSkill(final int skillId, final String skillName) {
        this.skillName = skillName;
        this.skillId = skillId;
    }
    
    public int getSkillId() {
        return this.skillId;
    }
    
    public String getSkillName() {
        return this.skillName;
    }
    
    public void setTrigger(final byte trigger) {
        this.trigger = trigger;
    }
    
    public byte getTrigger() {
        return this.trigger;
    }
    
    public int getDifficulty(final Item target) {
        int diff = this.resultItem.getDifficulty();
        if (diff == -100) {
            diff = (int)this.getResultTemplate(target).getDifficulty();
        }
        if (target.isFoodMaker()) {
            for (final IngredientGroup ig : this.ingredientGroups) {
                diff += ig.getGroupDifficulty();
            }
        }
        else if (this.hasTargetItem()) {
            diff += this.targetItem.getDifficulty();
        }
        final Item cooker = target.getTopParentOrNull();
        if (cooker != null) {
            final Byte cookerDif = this.cookersDif.get((short)cooker.getTemplateId());
            if (cookerDif != null) {
                diff += cookerDif;
            }
        }
        final Byte containerDif = this.containersDif.get((short)target.getTemplateId());
        if (containerDif != null) {
            diff += containerDif;
        }
        return diff;
    }
    
    public void addToCookerList(final int cookerTemplateId, final String cookerName, final int cookerDif) {
        this.cookers.put((short)cookerTemplateId, cookerName);
        this.cookersDif.put((short)cookerTemplateId, (byte)cookerDif);
    }
    
    public void addToCookerList(final int cookerTemplateId) {
        String name = "";
        try {
            final ItemTemplate cookerIT = ItemTemplateFactory.getInstance().getTemplate(cookerTemplateId);
            name = cookerIT.getName();
        }
        catch (NoSuchTemplateException e) {
            Recipe.logger.log(Level.WARNING, e.getMessage(), e);
        }
        this.addToCookerList(cookerTemplateId, name, 0);
    }
    
    private boolean isCooker(final int cookerTemplateId) {
        return this.cookers.containsKey((short)cookerTemplateId);
    }
    
    public Set<ItemTemplate> getCookerTemplates() {
        final Set<ItemTemplate> cookerTemplates = new HashSet<ItemTemplate>();
        for (final Short sc : this.cookers.keySet()) {
            try {
                final ItemTemplate cookerIT = ItemTemplateFactory.getInstance().getTemplate(sc);
                cookerTemplates.add(cookerIT);
            }
            catch (NoSuchTemplateException ex) {}
        }
        return cookerTemplates;
    }
    
    public void addToContainerList(final int containerTemplateId, final String containerName, final int containerDif) {
        this.containers.put((short)containerTemplateId, containerName);
        this.containersDif.put((short)containerTemplateId, (byte)containerDif);
    }
    
    public void addToContainerList(final int containerTemplateId) {
        String name = "";
        try {
            final ItemTemplate containerIT = ItemTemplateFactory.getInstance().getTemplate(containerTemplateId);
            name = containerIT.getName();
        }
        catch (NoSuchTemplateException e) {
            Recipe.logger.log(Level.WARNING, e.getMessage(), e);
        }
        this.addToContainerList(containerTemplateId, name, 0);
    }
    
    public boolean isContainer(final int containerTemplateId) {
        return this.containers.containsKey((short)containerTemplateId);
    }
    
    public Set<ItemTemplate> getContainerTemplates() {
        final Set<ItemTemplate> containerTemplates = new HashSet<ItemTemplate>();
        for (final Short sc : this.containers.keySet()) {
            try {
                final ItemTemplate cookerIT = ItemTemplateFactory.getInstance().getTemplate(sc);
                containerTemplates.add(cookerIT);
            }
            catch (NoSuchTemplateException ex) {}
        }
        return containerTemplates;
    }
    
    public Map<String, Ingredient> getAllIngredients(final boolean incActiveAndTargetItems) {
        final Map<String, Ingredient> knownIngredients = new HashMap<String, Ingredient>();
        for (final Ingredient ingredient : this.allIngredients.values()) {
            if ((ingredient.getGroupId() >= 0 || incActiveAndTargetItems) && !ingredient.getTemplate().isCookingTool()) {
                knownIngredients.put(ingredient.getName(true), ingredient);
            }
        }
        return knownIngredients;
    }
    
    public void setActiveItem(final Ingredient ingredient) {
        this.activeItem = ingredient;
    }
    
    @Nullable
    public Ingredient getActiveItem() {
        return this.activeItem;
    }
    
    public boolean hasActiveItem() {
        return this.activeItem != null;
    }
    
    private boolean isActiveItem(final Item source) {
        return this.activeItem.getTemplateId() == 14 || (this.activeItem.checkFoodGroup(source) && this.activeItem.checkCorpseData(source) && this.activeItem.checkState(source) && this.activeItem.checkMaterial(source) && this.activeItem.checkRealTemplate(source));
    }
    
    public String getActiveItemName() {
        if (this.hasActiveItem()) {
            return this.activeItem.getName(false);
        }
        return "";
    }
    
    public void setTargetItem(final Ingredient targetIngredient) {
        this.targetItem = targetIngredient;
        if (targetIngredient.getTemplateId() == 1173) {
            this.trigger = 2;
        }
    }
    
    @Nullable
    public Ingredient getTargetItem() {
        return this.targetItem;
    }
    
    public boolean hasTargetItem() {
        return this.targetItem != null;
    }
    
    private boolean isTargetItem(final Item target, final boolean checkLiquids) {
        if (target.isFoodMaker()) {
            for (final Short ii : this.containers.keySet()) {
                if (ii == target.getTemplateId()) {
                    return true;
                }
            }
            return false;
        }
        return this.targetItem != null && this.targetItem.checkFoodGroup(target) && this.targetItem.checkCorpseData(target) && this.targetItem.checkState(target) && this.targetItem.checkMaterial(target) && this.targetItem.checkRealTemplate(target) && (!this.useResultTemplateWeight() || !checkLiquids || this.getTargetLossWeight(target) <= target.getWeightGrams());
    }
    
    public int getTargetLossWeight(final Item target) {
        final int loss = this.targetItem.getLoss();
        if (loss != 100) {
            final int rWeight = (int)(this.resultItem.getTemplate().getWeightGrams() * (1.0f / ((100 - loss) / 100.0f)));
            return rWeight;
        }
        return target.getWeightGrams();
    }
    
    public String getTargetItemName() {
        if (this.hasTargetItem()) {
            return this.targetItem.getName(false);
        }
        return "";
    }
    
    public void setResultItem(final Ingredient resultIngredient) {
        this.resultItem = resultIngredient;
    }
    
    public Ingredient getResultItem() {
        return this.resultItem;
    }
    
    public ItemTemplate getResultTemplate(final Item container) {
        if (this.resultItem.isFoodGroup()) {
            final Item item = this.findIngredient(container, this.resultItem);
            if (item != null) {
                return item.getTemplate();
            }
        }
        return this.resultItem.getTemplate();
    }
    
    public boolean useResultTemplateWeight() {
        return this.resultItem.useResultTemplateWeight();
    }
    
    public String getResultName(final Item container) {
        final String resultName = this.resultItem.getResultName();
        if (resultName.length() > 0) {
            return this.doSubstituation(container, resultName);
        }
        final StringBuilder buf = new StringBuilder();
        if (this.resultItem.isFoodGroup()) {
            final Item item = this.findIngredient(container, this.resultItem);
            if (item != null) {
                buf.append(item.getActualName());
            }
        }
        else {
            buf.append(this.resultItem.getTemplateName());
        }
        return buf.toString();
    }
    
    String doSubstituation(final Item container, final String name) {
        String newName = name;
        if (newName.indexOf(35) >= 0) {
            if (this.resultItem.hasRealTemplateId() && this.resultItem.getRealItemTemplate() != null) {
                newName = newName.replace("#", this.resultItem.getRealItemTemplate().getName());
            }
            else if (this.resultItem.hasRealTemplateRef()) {
                final ItemTemplate realTemplate = this.getResultRealTemplate(container);
                if (realTemplate != null) {
                    newName = newName.replace("#", realTemplate.getName());
                }
                else {
                    newName = newName.replace("# ", "").replace(" #", "");
                }
            }
        }
        if (newName.indexOf(36) >= 0) {
            if (this.resultItem.hasMaterial()) {
                newName = newName.replace("$", this.resultItem.getMaterialName());
            }
            else if (this.resultItem.hasMaterialRef()) {
                final byte material = this.getResultMaterial(container);
                newName = newName.replace("$", Materials.convertMaterialByteIntoString(material));
            }
        }
        return newName.trim();
    }
    
    String getResultName(final Ingredient ingredient) {
        final StringBuilder buf = new StringBuilder();
        String resultName = this.resultItem.getResultName();
        if (resultName.length() > 0) {
            if (this.resultItem.hasCState()) {
                buf.append(this.resultItem.getCStateName());
                if (this.resultItem.hasPState() && this.resultItem.getPStateName().length() > 0) {
                    buf.append(" " + this.resultItem.getPStateName());
                }
                buf.append(" ");
            }
            else if (this.resultItem.hasPState() && this.resultItem.getPStateName().length() > 0) {
                buf.append(this.resultItem.getPStateName() + " ");
            }
            if (resultName.indexOf(35) >= 0) {
                if (ingredient.getRealItemTemplate() != null) {
                    resultName = resultName.replace("#", ingredient.getRealItemTemplate().getName().replace("any ", ""));
                }
                else if (this.resultItem.hasRealTemplateRef()) {
                    resultName = resultName.replace("# ", "").replace(" #", "");
                }
            }
            if (resultName.indexOf(36) >= 0) {
                if (ingredient.hasMaterial()) {
                    resultName = resultName.replace("$", ingredient.getMaterialName());
                }
                else if (this.resultItem.hasMaterialRef()) {
                    resultName = resultName.replace("$ ", "").replace(" $", "");
                }
            }
            buf.append(resultName.trim());
            return buf.toString();
        }
        buf.append(this.resultItem.getName(false));
        if (!this.resultItem.hasMaterial() && ingredient.hasMaterial()) {
            buf.append(" (" + ingredient.getMaterialName() + ")");
        }
        return buf.toString();
    }
    
    public String getResultNameWithGenus(final Item container) {
        return StringUtilities.addGenus(this.getSubMenuName(container), container.isNamePlural());
    }
    
    public boolean hasResultState() {
        return this.resultItem.hasXState();
    }
    
    public byte getResultState() {
        return this.resultItem.getXState();
    }
    
    public byte getResultMaterial(final Item target) {
        if (this.resultItem.hasMaterialRef()) {
            if (this.targetItem != null && this.targetItem.getTemplateName().equalsIgnoreCase(this.resultItem.getMaterialRef())) {
                return target.getMaterial();
            }
            final IngredientGroup group = this.getGroupByType(1);
            if (group != null) {
                final Ingredient ingredient = group.getIngredientByName(this.resultItem.getMaterialRef());
                if (ingredient != null && ingredient.getMaterial() != 0) {
                    final Item item = this.findIngredient(target, ingredient);
                    if (item != null) {
                        return item.getMaterial();
                    }
                }
            }
        }
        if (this.resultItem.hasMaterial()) {
            return this.resultItem.getMaterial();
        }
        return this.resultItem.getTemplate().getMaterial();
    }
    
    public boolean hasDescription() {
        return this.resultItem.hasResultDescription();
    }
    
    public String getResultDescription(final Item container) {
        return this.doSubstituation(container, this.resultItem.getResultDescription());
    }
    
    public void addAchievements(final Creature performer, final Item newItem) {
        if (this.achievementId != -1) {
            final AchievementTemplate at = Achievement.getTemplate(this.achievementId);
            if (at != null) {
                if (at.isInLiters()) {
                    performer.achievement(this.achievementId, newItem.getWeightGrams() / 1000);
                }
                else {
                    performer.achievement(this.achievementId);
                }
            }
        }
    }
    
    public void addAchievementsOffline(final long wurmId, final Item newItem) {
        if (this.achievementId != -1) {
            final AchievementTemplate at = Achievement.getTemplate(this.achievementId);
            if (at != null) {
                if (at.isInLiters()) {
                    Achievements.triggerAchievement(wurmId, this.achievementId, newItem.getWeightGrams() / 1000);
                }
                else {
                    Achievements.triggerAchievement(wurmId, this.achievementId);
                }
            }
        }
    }
    
    @Nullable
    public ItemTemplate getResultRealTemplate(final Item target) {
        if (this.resultItem.getRealTemplateRef().length() > 0) {
            if (this.hasOneContainer()) {
                for (final Map.Entry<Short, String> container : this.containers.entrySet()) {
                    if (container.getValue().equalsIgnoreCase(this.resultItem.getRealTemplateRef())) {
                        return target.getRealTemplate();
                    }
                }
            }
            if (this.targetItem != null && this.targetItem.getTemplateName().equalsIgnoreCase(this.resultItem.getRealTemplateRef())) {
                final ItemTemplate rit = target.getRealTemplate();
                if (rit != null) {
                    return rit;
                }
                return target.getTemplate();
            }
            else {
                final IngredientGroup group = this.getGroupByType(1);
                if (group != null) {
                    final Ingredient ingredient = group.getIngredientByName(this.resultItem.getRealTemplateRef());
                    if (ingredient != null) {
                        final Item item = this.findIngredient(target, ingredient);
                        if (item != null) {
                            final ItemTemplate rit2 = item.getRealTemplate();
                            if (rit2 != null) {
                                return rit2;
                            }
                            return item.getTemplate();
                        }
                    }
                }
            }
        }
        else if (this.resultItem.hasRealTemplate()) {
            return this.resultItem.getRealItemTemplate();
        }
        return null;
    }
    
    @Nullable
    private Item findIngredient(final Item container, final Ingredient ingredient) {
        final int foodGroup = ingredient.isFoodGroup() ? ingredient.getTemplateId() : 0;
        if (container.isFoodMaker() || container.getTemplate().isCooker() || container.getTemplateId() == 1284) {
            for (final Item item : container.getItemsAsArray()) {
                Label_0216: {
                    if (foodGroup > 0) {
                        if (item.getTemplate().getFoodGroup() == foodGroup) {
                            if (!ingredient.hasRealTemplate() || item.getRealTemplateId() == ingredient.getRealTemplateId()) {
                                if (!ingredient.hasMaterial() || item.getMaterial() == ingredient.getMaterial()) {
                                    return item;
                                }
                            }
                        }
                    }
                    else if (item.getTemplateId() == ingredient.getTemplateId()) {
                        if (ingredient.hasRealTemplate() && item.getRealTemplateId() != ingredient.getRealTemplateId()) {
                            if (item.getRealTemplate() == null) {
                                break Label_0216;
                            }
                            if (item.getRealTemplate().getFoodGroup() != ingredient.getRealTemplateId()) {
                                break Label_0216;
                            }
                        }
                        if (!ingredient.hasMaterial() || item.getMaterial() == ingredient.getMaterial()) {
                            return item;
                        }
                    }
                }
            }
        }
        else if (container.getTemplate().getFoodGroup() == foodGroup) {
            if (ingredient.hasRealTemplate() && container.getRealTemplateId() != ingredient.getRealTemplateId()) {
                return null;
            }
            if (ingredient.hasMaterial() && container.getMaterial() != ingredient.getMaterial()) {
                return null;
            }
            return container;
        }
        return null;
    }
    
    @Nullable
    public Ingredient findMatchingIngredient(final Item item) {
        for (final Ingredient ingredient : this.allIngredients.values()) {
            if (ingredient.matches(item)) {
                return ingredient;
            }
        }
        return null;
    }
    
    boolean isPartialMatch(final Item container) {
        if (this.getRecipeId() == 0) {
            System.out.println("isPartialMatch:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        if (this.hasTargetItem()) {
            if (!this.isTargetItem(container, false)) {
                return false;
            }
        }
        else if (this.hasContainer() && !this.isContainer(container.getTemplateId())) {
            return false;
        }
        final Item[] items = container.getItemsAsArray();
        final boolean[] founds = new boolean[items.length];
        for (int x = 0; x < founds.length; ++x) {
            founds[x] = false;
        }
        if (this.getRecipeId() == 0) {
            System.out.println("isPartialMatch2:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        for (final IngredientGroup ig : this.ingredientGroups) {
            ig.clearFound();
            for (int x2 = 0; x2 < items.length; ++x2) {
                if (!founds[x2] && ig.matches(items[x2])) {
                    founds[x2] = true;
                }
            }
        }
        if (this.getRecipeId() == 0) {
            System.out.println("isPartialMatch3:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        for (int x = 0; x < items.length; ++x) {
            if (!founds[x]) {
                return false;
            }
            final Ingredient ingredient = this.findMatchingIngredient(items[x]);
            if (ingredient != null && !ingredient.wasFound(true, false)) {
                return false;
            }
        }
        for (final IngredientGroup ig : this.ingredientGroups) {
            if (ig.getGroupType() == 3 && ig.getFound(false) > 1) {
                return false;
            }
            if (ig.getGroupType() == 2 && ig.getFound(false) > 1) {
                return false;
            }
            if (ig.getGroupType() == 5 && !ig.wasFound()) {
                return false;
            }
        }
        return true;
    }
    
    public Ingredient[] getWhatsMissing() {
        final Set<Ingredient> ingredients = new HashSet<Ingredient>();
        for (final IngredientGroup ig : this.ingredientGroups) {
            if ((ig.getGroupType() == 1 || ig.getGroupType() == 3 || ig.getGroupType() == 4) && !ig.wasFound()) {
                for (final Ingredient ingredient : ig.getIngredients()) {
                    if (!ingredient.wasFound(ig.getGroupType() == 4, false)) {
                        ingredients.add(ingredient);
                    }
                }
            }
        }
        return ingredients.toArray(new Ingredient[ingredients.size()]);
    }
    
    public void addToIngredientGroupList(final IngredientGroup ingredientGroup) {
        this.ingredientGroups.add(ingredientGroup);
    }
    
    public void setDefaults(final Recipe templateRecipe) {
        for (final IngredientGroup ig : templateRecipe.getGroups()) {
            if (ig.size() > 0) {
                this.addToIngredientGroupList(ig.clone());
            }
            else {
                Recipe.logger.warning("recipe contains empty IngredientGroup: [" + templateRecipe.recipeId + "] " + templateRecipe.name);
            }
        }
        this.resultItem = templateRecipe.resultItem.clone(null);
        this.lootable = templateRecipe.lootable;
        this.nameable = templateRecipe.nameable;
        this.lootableCreature = templateRecipe.lootableCreature;
        this.lootableRarity = templateRecipe.lootableRarity;
        this.trigger = templateRecipe.trigger;
        this.skillId = templateRecipe.skillId;
        this.skillName = templateRecipe.skillName;
        this.achievementId = templateRecipe.achievementId;
        this.achievementName = templateRecipe.achievementName;
    }
    
    public void copyGroupsFrom(final Recipe recipe) {
        for (final IngredientGroup ig : recipe.getGroups()) {
            this.addToIngredientGroupList(ig.clone());
        }
    }
    
    @Nullable
    public IngredientGroup getGroupById(final byte groupId) {
        try {
            return this.ingredientGroups.get(groupId);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Nullable
    public IngredientGroup getGroupByType(final int groupType) {
        for (final IngredientGroup ig : this.ingredientGroups) {
            if (ig.getGroupType() == groupType) {
                return ig;
            }
        }
        return null;
    }
    
    public IngredientGroup[] getGroups() {
        return this.ingredientGroups.toArray(new IngredientGroup[this.ingredientGroups.size()]);
    }
    
    public boolean hasCooker() {
        return !this.cookers.isEmpty();
    }
    
    public boolean hasCooker(final int cookerId) {
        return this.cookers.containsKey((short)cookerId);
    }
    
    public boolean hasOneCooker() {
        return this.cookers.size() == 1;
    }
    
    public short getCookerId() {
        final Iterator<Short> iterator = this.cookers.keySet().iterator();
        if (iterator.hasNext()) {
            final Short ss = iterator.next();
            return ss;
        }
        return -10;
    }
    
    public boolean hasContainer() {
        return !this.containers.isEmpty();
    }
    
    public boolean hasOneContainer() {
        return this.containers.size() == 1;
    }
    
    public boolean hasContainer(final int containerId) {
        return this.containers.containsKey((short)containerId);
    }
    
    public boolean hasContainer(final String containerName) {
        for (final Map.Entry<Short, String> container : this.containers.entrySet()) {
            if (container.getValue().equalsIgnoreCase(containerName)) {
                return true;
            }
        }
        return false;
    }
    
    public short getContainerId() {
        final Iterator<Short> iterator = this.containers.keySet().iterator();
        if (iterator.hasNext()) {
            final Short ss = iterator.next();
            return ss;
        }
        return -10;
    }
    
    boolean checkIngredients(final Item container) {
        final Item[] items = container.getItemsAsArray();
        final boolean[] founds = new boolean[items.length];
        for (int x = 0; x < founds.length; ++x) {
            founds[x] = false;
        }
        if (this.getRecipeId() == 0) {
            System.out.println("checkIngredients:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        for (final IngredientGroup ig : this.ingredientGroups) {
            ig.clearFound();
            for (int x2 = 0; x2 < items.length; ++x2) {
                if (ig.matches(items[x2])) {
                    founds[x2] = true;
                }
            }
        }
        if (this.getRecipeId() == 0) {
            System.out.println("checkIngredients2:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        for (int x = 0; x < founds.length; ++x) {
            if (!founds[x]) {
                return false;
            }
        }
        if (this.getRecipeId() == 0) {
            System.out.println("checkIngredients3:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        for (final IngredientGroup ig : this.ingredientGroups) {
            if (!ig.wasFound()) {
                return false;
            }
        }
        if (this.getRecipeId() == 0) {
            System.out.println("checkIngredients4:" + this.getRecipeId() + " " + this.getTriggerName());
        }
        return true;
    }
    
    public float getChanceFor(@Nullable final Item activeItem, final Item target, final Creature performer) {
        final Skills skills = performer.getSkills();
        Skill primSkill = null;
        Skill secondarySkill = null;
        double bonus = 0.0;
        try {
            primSkill = skills.getSkill(this.getSkillId());
        }
        catch (Exception ex) {}
        try {
            if (this.hasActiveItem() && activeItem != null && this.isActiveItem(activeItem)) {
                secondarySkill = skills.getSkill(activeItem.getPrimarySkill());
            }
        }
        catch (Exception ex2) {}
        if (secondarySkill != null) {
            bonus = Math.max(1.0, secondarySkill.getKnowledge(activeItem, 0.0) / 10.0);
        }
        float chance = 0.0f;
        final int diff = this.getDifficulty(target);
        if (primSkill != null) {
            chance = (float)primSkill.getChance(diff, activeItem, bonus);
        }
        else {
            chance = 1 / (1 + diff) * 100;
        }
        return chance;
    }
    
    void setAchievementTriggered(final int achievementId, final String achievementName) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
    }
    
    public String getTriggerName() {
        switch (this.trigger) {
            case 0: {
                return "Time";
            }
            case 1: {
                return "Heat";
            }
            case 2: {
                if (this.isTargetActionType()) {
                    return "Target Action";
                }
                if (this.isContainerActionType()) {
                    return "Container Action";
                }
                return "Create";
            }
            default: {
                return "Unknown";
            }
        }
    }
    
    boolean isRecipeOk(final long playerId, @Nullable final Item activeItem, final Item target, final boolean checkActive, final boolean checkLiquids) {
        if (this.getRecipeId() == 0) {
            System.out.println("isRecipeOk:" + this.getRecipeId() + " " + checkActive + " " + this.getTriggerName() + "(" + target.getName() + ")");
        }
        if (playerId != -10L && this.isLootable() && !RecipesByPlayer.isKnownRecipe(playerId, this.recipeId)) {
            return false;
        }
        if (checkActive && activeItem != null && this.getActiveItem() != null) {
            if (!this.isActiveItem(activeItem)) {
                return false;
            }
            if (checkLiquids && activeItem.isLiquid()) {
                final int weightNeeded = this.getUsedActiveItemWeightGrams(activeItem, target);
                if (activeItem.getWeightGrams() < weightNeeded) {
                    return false;
                }
            }
        }
        if (this.targetItem != null && !this.isTargetItem(target, checkLiquids)) {
            return false;
        }
        if (this.trigger == 1 && checkActive) {
            final Item cooker = target.getTopParentOrNull();
            if (cooker == null) {
                return false;
            }
            if (!this.isCooker((short)cooker.getTemplateId())) {
                return false;
            }
        }
        if (this.targetItem == null) {
            if (this.hasContainer()) {
                if (!this.isContainer((short)target.getTemplateId())) {
                    return false;
                }
            }
            else if (this.hasCooker() && !this.isCooker((short)target.getTemplateId())) {
                return false;
            }
        }
        else if (this.trigger == 1 && checkActive) {
            final Item cooker = target.getTopParentOrNull();
            final Item parent = target.getParentOrNull();
            if (cooker == null || parent == null) {
                return false;
            }
            if (cooker.getTemplateId() != parent.getTemplateId()) {
                return false;
            }
            if (this.hasContainer() && !this.isContainer((short)parent.getTemplateId())) {
                return false;
            }
        }
        if (target.isFoodMaker() || target.getTemplate().isCooker() || (target.isRecipeItem() && target.isHollow())) {
            if (this.getRecipeId() == 0) {
                System.out.println("isRecipeOk2:" + this.getRecipeId() + " " + checkActive);
            }
            if (!this.checkIngredients(target)) {
                return false;
            }
            if (this.getRecipeId() == 0) {
                System.out.println("isRecipeOk3:" + this.getRecipeId() + " " + checkActive);
            }
            return !checkLiquids || this.getNewWeightGrams(target).isSuccess();
        }
        else {
            final int needed = (this.getActiveItem() != null) ? 2 : 1;
            if (this.allIngredients.size() != needed) {
                return false;
            }
            for (final Ingredient ingredient : this.allIngredients.values()) {
                if (ingredient.matches(target)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public int getUsedActiveItemWeightGrams(final Item source, final Item target) {
        final int rat = (this.getActiveItem() != null) ? this.getActiveItem().getRatio() : 0;
        if (source.isLiquid() && rat != 0) {
            return target.getWeightGrams() * rat / 100;
        }
        return source.getWeightGrams();
    }
    
    public LiquidResult getNewWeightGrams(final Item container) {
        final LiquidResult liquidResult = new LiquidResult();
        final Map<Short, Liquid> liquids = new HashMap<Short, Liquid>();
        for (final Ingredient in : this.getAllIngredients(true).values()) {
            if (in.getTemplate().isLiquid()) {
                final short id = (short)in.getTemplateId();
                final int ratio = in.getRatio();
                final String name = Recipes.getIngredientName(in, false);
                final int loss = in.getLoss();
                liquids.put(id, new Liquid(id, name, ratio, loss));
            }
        }
        int solidWeight = 0;
        for (final Item item : container.getItemsAsArray()) {
            if (item.isLiquid()) {
                final short id2 = (short)item.getTemplateId();
                final int liquidWeight = item.getWeightGrams();
                Liquid liquid = liquids.get(id2);
                if (liquid == null) {
                    final short fgid = (short)item.getTemplate().getFoodGroup();
                    liquid = liquids.get(fgid);
                }
                if (liquid != null) {
                    if (liquid.getRatio() != 0) {
                        liquid.setWeight(liquidWeight);
                    }
                }
                else {
                    Recipe.logger.info("Liquid Item " + item.getName() + " missing ingredient?");
                }
            }
            else {
                solidWeight += item.getWeightGrams();
            }
        }
        int newWeight = solidWeight;
        for (final Liquid liquid2 : liquids.values()) {
            if (liquid2.getWeight() > 0) {
                final int neededWeight = solidWeight * liquid2.getRatio() / 100;
                final int minLiquid = (int)(neededWeight * 0.8);
                final int maxLiquid = (int)(neededWeight * 1.2);
                if (liquid2.getWeight() < minLiquid) {
                    liquidResult.add(liquid2.getId(), "not enough " + liquid2.getName() + ", looks like it should use between " + minLiquid + " and " + maxLiquid + " grams.");
                }
                else if (liquid2.getWeight() > maxLiquid) {
                    liquidResult.add(liquid2.getId(), "too much " + liquid2.getName() + ", looks like it should use between " + minLiquid + " and " + maxLiquid + " grams.");
                }
                newWeight += liquid2.getWeight() * (100 - liquid2.getLoss());
            }
        }
        liquidResult.setNewWeight(newWeight);
        return liquidResult;
    }
    
    public boolean isTargetActionType() {
        return this.trigger == 2 && this.containers.isEmpty();
    }
    
    public boolean isContainerActionType() {
        return this.trigger == 2 && !this.containers.isEmpty();
    }
    
    public boolean isHeatType() {
        return this.trigger == 1;
    }
    
    public boolean isTimeType() {
        return this.trigger == 0;
    }
    
    public String[] getCookers() {
        final List<String> cookerList = new ArrayList<String>();
        for (final String cooker : this.cookers.values()) {
            cookerList.add(cooker);
        }
        return cookerList.toArray(new String[cookerList.size()]);
    }
    
    public String getCookersAsString() {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final String s : this.cookers.values()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(",");
            }
            buf.append(s);
        }
        return buf.toString();
    }
    
    public String[] getContainers() {
        final List<String> containerList = new ArrayList<String>();
        for (final String container : this.containers.values()) {
            containerList.add(container);
        }
        return containerList.toArray(new String[containerList.size()]);
    }
    
    public String getContainersAsString() {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final String s : this.containers.values()) {
            if (first) {
                first = false;
            }
            else {
                buf.append(",");
            }
            buf.append(s);
        }
        return buf.toString();
    }
    
    boolean matchesResult(final Ingredient ingredient, final boolean exactOnly) {
        if (this.resultItem.getTemplateId() == ingredient.getTemplateId()) {
            boolean ok = !this.resultItem.hasCState() && !ingredient.hasCState();
            if (!ok) {
                ok = (this.resultItem.hasCState() && ingredient.hasCState() && this.resultItem.getCState() == ingredient.getCState());
            }
            if (!ok) {
                ok = (exactOnly && !ingredient.hasCState() && this.resultItem.hasCState());
            }
            if (!ok) {
                return false;
            }
            ok = (!this.resultItem.hasPState() && !ingredient.hasPState());
            if (!ok) {
                ok = (this.resultItem.hasPState() && ingredient.hasPState() && this.resultItem.getPState() == ingredient.getPState());
            }
            if (!ok) {
                ok = (exactOnly && !ingredient.hasPState() && this.resultItem.hasPState());
            }
            if (!ok) {
                return false;
            }
            if (ingredient.hasRealTemplate()) {
                if (this.resultItem.hasRealTemplate()) {
                    if (this.resultItem.getRealTemplateId() != ingredient.getRealTemplateId()) {
                        if (exactOnly) {
                            return false;
                        }
                        if (this.resultItem.getRealItemTemplate() == null || ingredient.getRealItemTemplate() == null) {
                            return false;
                        }
                        if (this.resultItem.getRealItemTemplate().isFoodGroup() && this.resultItem.getRealItemTemplate().getFoodGroup() != ingredient.getRealItemTemplate().getFoodGroup()) {
                            return false;
                        }
                        if (ingredient.getRealItemTemplate().isFoodGroup() && this.resultItem.getRealItemTemplate().getFoodGroup() != ingredient.getRealItemTemplate().getFoodGroup()) {
                            return false;
                        }
                    }
                }
                else {
                    if (!this.resultItem.hasRealTemplateRef()) {
                        return false;
                    }
                    boolean match = false;
                    if (this.hasTargetItem() && this.targetItem.getTemplateName().equalsIgnoreCase(this.resultItem.getRealTemplateRef())) {
                        final Ingredient refingredient = this.targetItem;
                        if (ingredient.getRealItemTemplate() == null) {
                            if (refingredient.getTemplate() != null) {
                                return false;
                            }
                            match = true;
                        }
                        else if (refingredient.getTemplateId() == ingredient.getRealItemTemplate().getTemplateId()) {
                            match = true;
                        }
                        else {
                            if (exactOnly) {
                                return false;
                            }
                            if (refingredient.getTemplate().getFoodGroup() == ingredient.getRealItemTemplate().getFoodGroup() || (refingredient.getTemplateId() == 369 && ingredient.getRealItemTemplate().getFoodGroup() == 1201)) {
                                match = true;
                            }
                        }
                    }
                    if (!match) {
                        final IngredientGroup group = this.getGroupByType(1);
                        if (group == null) {
                            return false;
                        }
                        final Ingredient refingredient2 = group.getIngredientByName(this.resultItem.getRealTemplateRef());
                        if (refingredient2 == null) {
                            return false;
                        }
                        if (ingredient.getRealItemTemplate() == null) {
                            if (refingredient2.getTemplate() != null) {
                                return false;
                            }
                            match = true;
                        }
                        else if (!refingredient2.hasRealTemplateId()) {
                            if (exactOnly) {
                                return false;
                            }
                            if (refingredient2.getTemplate().getFoodGroup() == ingredient.getRealItemTemplate().getFoodGroup()) {
                                match = true;
                            }
                            else {
                                final Recipe[] ning = Recipes.getRecipesByResult(new Ingredient(refingredient2.getTemplate(), false, refingredient2.getGroupId()));
                                if (ning == null || ning.length == 0) {
                                    return false;
                                }
                            }
                        }
                        else if (refingredient2.getTemplateId() == ingredient.getRealItemTemplate().getTemplateId()) {
                            match = true;
                        }
                        else {
                            if (exactOnly) {
                                return false;
                            }
                            if (refingredient2.getTemplate().getFoodGroup() == ingredient.getRealItemTemplate().getFoodGroup() || (refingredient2.getTemplateId() == 369 && ingredient.getRealItemTemplate().getFoodGroup() == 1201)) {
                                match = true;
                            }
                        }
                    }
                }
            }
            if (ingredient.hasMaterial() && this.resultItem.hasMaterial() && ingredient.getMaterial() != this.resultItem.getMaterial()) {
                return false;
            }
            if (ingredient.hasMaterial() && this.resultItem.hasMaterialRef()) {
                if (this.targetItem != null) {
                    if (!this.isInMaterialGroup(this.targetItem.getTemplateId(), ingredient.getMaterial())) {
                        return false;
                    }
                }
                else {
                    final IngredientGroup group2 = this.getGroupByType(1);
                    if (group2 == null) {
                        return false;
                    }
                    final Ingredient refingredient = group2.getIngredientByName(this.resultItem.getMaterialRef());
                    if (refingredient == null) {
                        return false;
                    }
                    if (!this.isInMaterialGroup(refingredient.getTemplateId(), ingredient.getMaterial())) {
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (this.resultItem.getTemplate().isFoodGroup()) {
                return this.targetItem != null && exactOnly && this.targetItem.getTemplate().getFoodGroup() == ingredient.getTemplate().getFoodGroup() && (!ingredient.hasCState() || !this.resultItem.hasCState() || this.resultItem.getCState() == ingredient.getCState()) && (!ingredient.hasPState() || !this.resultItem.hasPState() || this.resultItem.getPState() == ingredient.getPState());
            }
            return !exactOnly && ingredient.getTemplate().isFoodGroup() && this.resultItem.getTemplate().getFoodGroup() == ingredient.getTemplateId() && (!this.resultItem.hasCState() || this.resultItem.getCState() == ingredient.getCState()) && (!this.resultItem.hasPState() || this.resultItem.getPState() == ingredient.getPState());
        }
    }
    
    private boolean isInMaterialGroup(final int templateGroup, final byte material) {
        switch (templateGroup) {
            case 1261: {
                switch (material) {
                    case 2:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                    case 86:
                    case 87: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 200:
            case 201:
            case 1157: {
                switch (material) {
                    case 3:
                    case 4:
                    case 5:
                    case 6: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            default: {
                return false;
            }
        }
    }
    
    public String getIngredientsAsString() {
        final StringBuilder buf = new StringBuilder();
        byte groupId = -1;
        IngredientGroup group = null;
        for (final Ingredient ingredient : this.allIngredients.values()) {
            group = this.getGroupById(ingredient.getGroupId());
            if (group != null && group.getGroupType() > 0) {
                final byte newGroupId = ingredient.getGroupId();
                if (groupId != newGroupId) {
                    final IngredientGroup oldGroup;
                    if (groupId > -1 && (oldGroup = this.getGroupById(groupId)) != null) {
                        switch (oldGroup.getGroupType()) {
                            case 3: {
                                buf.append(")");
                                break;
                            }
                            case 4: {
                                buf.append(")+");
                                break;
                            }
                            case 2: {
                                buf.append("]");
                                break;
                            }
                        }
                        buf.append(",");
                    }
                    switch (group.getGroupType()) {
                        case 5: {
                            buf.append("[");
                            break;
                        }
                        case 3: {
                            buf.append("(");
                            break;
                        }
                        case 4: {
                            buf.append("(");
                            break;
                        }
                        case 2: {
                            buf.append("[");
                            break;
                        }
                    }
                }
                else {
                    switch (group.getGroupType()) {
                        case 1: {
                            buf.append(",");
                            break;
                        }
                        case 5: {
                            buf.append(",[");
                            break;
                        }
                        case 3: {
                            buf.append("|");
                            break;
                        }
                        case 4: {
                            buf.append("|");
                            break;
                        }
                        case 2: {
                            buf.append("|");
                            break;
                        }
                    }
                }
                buf.append(Recipes.getIngredientName(ingredient));
                groupId = newGroupId;
                switch (group.getGroupType()) {
                    case 5: {
                        buf.append("]");
                        continue;
                    }
                }
            }
        }
        if (group != null) {
            switch (group.getGroupType()) {
                case 3: {
                    buf.append(")");
                    break;
                }
                case 4: {
                    buf.append(")+");
                    break;
                }
                case 2: {
                    buf.append("]");
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    void clearFound() {
        for (final IngredientGroup ig : this.ingredientGroups) {
            ig.clearFound();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Recipe:");
        buf.append("recipeId:" + this.recipeId);
        if (this.name.length() > 0) {
            buf.append(",name:" + this.name);
        }
        if (this.skillId > 0) {
            buf.append(",skill:" + this.skillName + "(" + this.skillId + ")");
        }
        buf.append(",trigger:" + this.getTriggerName());
        if (!this.cookers.isEmpty()) {
            buf.append(",cookers[");
            boolean first = true;
            for (final Map.Entry<Short, String> me : this.cookers.entrySet()) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(",");
                }
                buf.append(me.getValue() + "(" + me.getKey() + "),dif=" + this.cookersDif.get(me.getKey()));
            }
            buf.append("]");
        }
        if (!this.containers.isEmpty()) {
            buf.append(",containers[");
            boolean first = true;
            for (final Map.Entry<Short, String> me : this.containers.entrySet()) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(",");
                }
                buf.append(me.getValue() + "(" + me.getKey() + "),dif=" + this.containersDif.get(me.getKey()));
            }
            buf.append("]");
        }
        if (this.activeItem != null) {
            buf.append(",activeItem:" + this.activeItem.toString());
        }
        if (this.targetItem != null) {
            buf.append(",target:" + this.targetItem.toString());
        }
        if (!this.ingredientGroups.isEmpty()) {
            buf.append(",ingredients{");
            boolean first = true;
            for (final IngredientGroup ig : this.ingredientGroups) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(",");
                }
                buf.append(ig.toString());
            }
            buf.append("}");
        }
        if (this.resultItem != null) {
            buf.append(",result:" + this.resultItem.toString());
        }
        if (this.achievementId != -1) {
            buf.append(",achievementTriggered{");
            buf.append(this.achievementName + "(" + this.achievementId + ")");
            buf.append("}");
        }
        buf.append("}");
        return buf.toString();
    }
    
    static {
        logger = Logger.getLogger(Recipe.class.getName());
    }
    
    public class LiquidResult
    {
        private final Map<Short, String> errors;
        private int newWeight;
        
        LiquidResult() {
            this.errors = new HashMap<Short, String>();
            this.newWeight = 0;
        }
        
        public boolean isSuccess() {
            return this.errors.isEmpty();
        }
        
        public Map<Short, String> getErrors() {
            return this.errors;
        }
        
        void add(final short templateId, final String error) {
            this.errors.put(templateId, error);
        }
        
        void setNewWeight(final int newWeight) {
            this.newWeight = newWeight;
        }
        
        public int getNewWeight() {
            return this.newWeight;
        }
    }
    
    class Liquid
    {
        final short id;
        final int ratio;
        int weight;
        final int loss;
        final String name;
        
        Liquid(final short id, final String name, final int ratio, final int loss) {
            this.weight = 0;
            this.id = id;
            this.name = name;
            this.ratio = ratio;
            this.loss = loss;
        }
        
        short getId() {
            return this.id;
        }
        
        String getName() {
            return this.name;
        }
        
        int getRatio() {
            return this.ratio;
        }
        
        int getAbsRatio() {
            return Math.abs(this.ratio);
        }
        
        int getWeight() {
            return this.weight;
        }
        
        int getLoss() {
            return this.loss;
        }
        
        void setWeight(final int newWeight) {
            this.weight = newWeight;
        }
    }
}

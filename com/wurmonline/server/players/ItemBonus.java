// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.creatures.SpellEffects;
import com.wurmonline.server.spells.SpellEffect;
import java.util.Iterator;
import java.util.logging.Level;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class ItemBonus implements TimeConstants
{
    private static final Logger logger;
    private static final ConcurrentHashMap<Long, Map<Integer, ItemBonus>> playerBonuses;
    private final Item itemOne;
    private Item itemTwo;
    private final long playerId;
    private final int bonusTypeId;
    private float bonusValue;
    private float bonus2Value;
    private boolean stacks;
    private static final long decayTime = 28800L;
    
    private ItemBonus(final Item item, final long playerid, final int bonusType, final float value) {
        this(item, playerid, bonusType, value, false);
    }
    
    private ItemBonus(final Item item, final long playerid, final int bonusType, final float value, final boolean isStacking) {
        this.stacks = false;
        this.itemOne = item;
        this.playerId = playerid;
        this.bonusTypeId = bonusType;
        this.bonusValue = value;
        this.setStacking(isStacking);
    }
    
    public static final void calcAndAddBonus(final Item item, final Creature creature) {
        final SpellEffectsEnum bonusType = SpellEffectsEnum.getEnumForItemTemplateId(item.getTemplateId(), item.getData1());
        if (bonusType != null) {
            final float value = getBonusValueForItem(item);
            if (value > 0.0f) {
                addBonus(item, creature, bonusType.getTypeId(), value, getStacking(item));
            }
        }
    }
    
    public static final void checkDepleteAndRename(final Item usedItem, final Creature owner) {
        if (usedItem.isRiftLoot() && isTimed(usedItem) && WurmCalendar.currentTime - usedItem.getLastMaintained() > 28800L) {
            final SpellEffectsEnum bonusType = SpellEffectsEnum.getEnumForItemTemplateId(usedItem.getTemplateId(), usedItem.getData1());
            if (bonusType != null) {
                final ItemBonus cbonus = getItemBonusObject(owner.getWurmId(), bonusType.getTypeId());
                if (cbonus != null) {
                    if (usedItem.getAuxData() <= 0) {
                        removeBonus(usedItem, owner);
                        return;
                    }
                    usedItem.setAuxData((byte)(usedItem.getAuxData() - 1));
                    usedItem.setLastMaintained(WurmCalendar.currentTime);
                    rename(usedItem);
                }
            }
        }
    }
    
    private static final void rename(final Item usedItem) {
        if (usedItem.getAuxData() > 0 && !usedItem.getActualName().toLowerCase().contains("used")) {
            usedItem.setName("used " + usedItem.getActualName());
        }
        else if (usedItem.getAuxData() <= 0) {
            if (usedItem.getActualName().toLowerCase().contains("used")) {
                usedItem.setName(usedItem.getActualName().replace("used", "depleted"));
            }
            else if (!usedItem.getActualName().toLowerCase().contains("depleted")) {
                usedItem.setName("depleted " + usedItem.getActualName());
            }
        }
    }
    
    private static final void addBonus(final Item item, final Creature creature, final int bonusType, final float value, final boolean isStacking) {
        if (isTimed(item) && item.getAuxData() <= 0) {
            return;
        }
        Map<Integer, ItemBonus> curr = ItemBonus.playerBonuses.get(creature.getWurmId());
        if (curr == null) {
            curr = new ConcurrentHashMap<Integer, ItemBonus>();
            ItemBonus.playerBonuses.put(creature.getWurmId(), curr);
        }
        ItemBonus cbonus = curr.get(bonusType);
        if (cbonus == null) {
            cbonus = new ItemBonus(item, creature.getWurmId(), bonusType, value, isStacking);
            curr.put(bonusType, cbonus);
        }
        else {
            cbonus.setItemTwo(item);
            cbonus.setBonus2Value(value);
        }
        cbonus.sendNewBonusToClient(item, creature);
        checkDepleteAndRename(item, creature);
    }
    
    public final void sendNewBonusToClient(final Item item, final Creature creature) {
        if (item != null) {
            if (!isTimed(item) || this.getSecondsLeft() > 0) {
                final SpellEffectsEnum senum = SpellEffectsEnum.getEnumForItemTemplateId(item.getTemplateId(), item.getData1());
                creature.getCommunicator().sendAddSpellEffect(senum, this.getSecondsLeft(), this.getBonusVal(0.0f));
            }
        }
        else {
            ItemBonus.logger.log(Level.INFO, "Item was null for " + creature.getName(), new Exception());
        }
    }
    
    public static final void sendAllItemBonusToPlayer(final Player player) {
        final Map<Integer, ItemBonus> curr = ItemBonus.playerBonuses.get(player.getWurmId());
        if (curr != null) {
            for (final ItemBonus bonus : curr.values()) {
                bonus.sendNewBonusToClient(bonus.getItemOne(), player);
            }
        }
    }
    
    public final void sendRemoveBonusToClient(final Item item, final Creature creature) {
        final SpellEffectsEnum senum = SpellEffectsEnum.getEnumForItemTemplateId(item.getTemplateId(), item.getData1());
        creature.getCommunicator().sendRemoveSpellEffect(senum.getId(), senum);
        final byte debuff = SpellEffectsEnum.getDebuffForEnum(senum);
        if (debuff != 0) {
            final SpellEffects effs = creature.getSpellEffects();
            SpellEffect edebuff = effs.getSpellEffect(debuff);
            if (edebuff == null) {
                edebuff = new SpellEffect(creature.getWurmId(), debuff, 100.0f, 300, (byte)10, (byte)1, true);
                effs.addSpellEffect(edebuff);
            }
            else {
                edebuff.setTimeleft(300);
            }
        }
    }
    
    public static final void removeBonus(final Item item, final Creature creature) {
        final Map<Integer, ItemBonus> curr = ItemBonus.playerBonuses.get(creature.getWurmId());
        if (curr == null) {
            return;
        }
        final SpellEffectsEnum senum = SpellEffectsEnum.getEnumForItemTemplateId(item.getTemplateId(), item.getData1());
        final ItemBonus cbonus = curr.get(senum.getTypeId());
        if (cbonus != null) {
            if (cbonus.getItemTwo() == item) {
                cbonus.setItemTwo(null);
                cbonus.setBonus2Value(0.0f);
                cbonus.sendNewBonusToClient(item, creature);
                return;
            }
            if (cbonus.getItemOne() == item) {
                if (cbonus.getItemTwo() != null) {
                    final ItemBonus newBonus = new ItemBonus(cbonus.getItemTwo(), creature.getWurmId(), cbonus.getBonusType(), cbonus.getItemTwoBonusValue(0.0f), cbonus.isStacking());
                    curr.put(cbonus.getBonusType(), newBonus);
                    newBonus.sendNewBonusToClient(item, creature);
                }
                else {
                    curr.remove(cbonus.getBonusType());
                    cbonus.sendRemoveBonusToClient(item, creature);
                }
            }
        }
        else {
            ItemBonus.logger.log(Level.INFO, "Failed to remove bonus for " + item.getName() + " for " + creature.getName() + " although it should be registered.");
        }
        if (curr.isEmpty()) {
            ItemBonus.playerBonuses.remove(creature.getWurmId());
        }
    }
    
    public static final void clearBonuses(final long playerid) {
        ItemBonus.playerBonuses.remove(playerid);
    }
    
    private static final float getBonus(final long playerid, final int bonusType) {
        return getBonus(playerid, bonusType, 0.0f);
    }
    
    private static final float getBonus(final long playerid, final int bonusType, final float damageDealt) {
        final ItemBonus bonus = getItemBonusObject(playerid, bonusType);
        if (bonus == null) {
            return 0.0f;
        }
        return bonus.getBonusVal(damageDealt);
    }
    
    private static final ItemBonus getItemBonusObject(final long playerId, final int bonusType) {
        final Map<Integer, ItemBonus> curr = ItemBonus.playerBonuses.get(playerId);
        if (curr == null) {
            return null;
        }
        return curr.get(bonusType);
    }
    
    public final float getBonusVal(final float damageDealt) {
        if (this.isStacking()) {
            return this.getItemOneBonusValue(damageDealt) + this.getItemTwoBonusValue(damageDealt);
        }
        if (this.getItemOneBonusValue(0.0f) > this.getItemTwoBonusValue(0.0f)) {
            return this.getItemOneBonusValue(damageDealt);
        }
        return this.getItemTwoBonusValue(damageDealt);
    }
    
    public final int getSecondsLeft() {
        if (this.isStacking()) {
            return Math.min(this.getSeconds1Left(), this.getSeconds2Left());
        }
        if (this.getItemOneBonusValue(0.0f) > this.getItemTwoBonusValue(0.0f)) {
            return this.getSeconds1Left();
        }
        return this.getSeconds2Left();
    }
    
    public Item getItemOne() {
        return this.itemOne;
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    public int getBonusType() {
        return this.bonusTypeId;
    }
    
    private float getItemOneBonusValue(final float damageDealt) {
        if (damageDealt > 0.0f) {
            this.itemOne.setDamage(this.itemOne.getDamage() + damageDealt);
        }
        return this.bonusValue;
    }
    
    private float getItemTwoBonusValue(final float damageDealt) {
        if (this.itemTwo != null && damageDealt > 0.0f) {
            this.itemTwo.setDamage(this.itemTwo.getDamage() + damageDealt);
        }
        return this.bonus2Value;
    }
    
    public void setBonus2Value(final float bonus2Value) {
        this.bonus2Value = bonus2Value;
    }
    
    public Item getItemTwo() {
        return this.itemTwo;
    }
    
    public void setItemTwo(final Item item2) {
        this.itemTwo = item2;
    }
    
    public boolean isStacking() {
        return this.stacks;
    }
    
    public void setStacking(final boolean stacks) {
        this.stacks = stacks;
    }
    
    public static final float getBonusValueForItem(final Item item) {
        return 0.0f;
    }
    
    public static final boolean isTimed(final Item item) {
        return false;
    }
    
    public static final boolean getStacking(final Item item) {
        return false;
    }
    
    private int getSeconds1Left() {
        if (this.itemOne != null && isTimed(this.itemOne)) {
            return this.itemOne.getAuxData() * 3600;
        }
        return -1;
    }
    
    private int getSeconds2Left() {
        if (this.itemTwo != null && isTimed(this.itemTwo)) {
            return this.itemTwo.getAuxData() * 3600;
        }
        return -1;
    }
    
    public static final float getGlanceBonusFor(final ArmourTemplate.ArmourType armourType, final byte woundType, final Item weapon, final Creature creature) {
        float bonus = 0.0f;
        if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH) {
            if (woundType == 0) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_COTTON_CRUSHING.getTypeId());
            }
            if (woundType == 1) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_COTTON_SLASHING.getTypeId());
            }
        }
        else if (armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER) {
            if (weapon.isTwoHanded()) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_LEATHER_TWOHANDED.getTypeId());
            }
        }
        else if (armourType == ArmourTemplate.ARMOUR_TYPE_STUDDED && weapon.isTwoHanded()) {
            bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_STUDDED_TWOHANDED.getTypeId());
        }
        return bonus;
    }
    
    public static final float getFaceDamReductionBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_FACEDAM.getTypeId());
    }
    
    public static final float getAreaSpellReductionBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_AREASPELL_DAMREDUCT.getTypeId());
    }
    
    public static final float getAreaSpellDamageIncreaseBonus(final Creature creature) {
        return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_AREA_SPELL.getTypeId());
    }
    
    public static final float getWeaponDamageIncreaseBonus(final Creature creature, final Item weapon) {
        final float bonus = 0.0f;
        return 1.0f + bonus;
    }
    
    public static final float getArcheryPenaltyReduction(final Creature creature) {
        if (creature.getArmourLimitingFactor() <= -0.15f) {
            return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_HEAVY_ARCHERY.getTypeId());
        }
        return 1.0f;
    }
    
    public static final float getStaminaReductionBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_STAMINA.getTypeId());
    }
    
    public static final float getDodgeBonus(final Creature creature) {
        return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_DODGE.getTypeId());
    }
    
    public static final float getCRBonus(final Creature creature) {
        return getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_CR.getTypeId());
    }
    
    public static final float getSpellResistBonus(final Creature creature) {
        return getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_SPELLRESIST.getTypeId());
    }
    
    public static final float getHealingBonus(final Creature creature) {
        return getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_HEALING.getTypeId());
    }
    
    public static final float getSkillGainBonus(final Creature creature, final int skillId) {
        return 0.0f;
    }
    
    public static final float getKillEfficiencyBonus(final Creature creature) {
        return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_NECKLACE_SKILLEFF.getTypeId(), 1.0f);
    }
    
    public static final float getImproveSkillMaxBonus(final Creature creature) {
        return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_NECKLACE_SKILLMAX.getTypeId());
    }
    
    public static final float getDrownDamReduction(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_SWIMMING.getTypeId(), 1.0f);
    }
    
    public static final float getStealthBonus(final Creature creature) {
        return 1.0f + getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_STEALTH.getTypeId(), 0.5f);
    }
    
    public static final float getDetectionBonus(final Creature creature) {
        return 50.0f * getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_RING_DETECTION.getTypeId());
    }
    
    public static final float getParryBonus(final Creature creature, final Item weapon) {
        final float bonus = 0.0f;
        return 1.0f - bonus;
    }
    
    public static final float getWeaponSpellDamageIncreaseBonus(final long ownerid) {
        if (ownerid > 0L) {
            return 1.0f + getBonus(ownerid, SpellEffectsEnum.ITEM_BRACELET_ENCHANTDAM.getTypeId());
        }
        return 1.0f;
    }
    
    public static final float getHurtingReductionBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_NECKLACE_HURTING.getTypeId(), 1.0f);
    }
    
    public static final float getFocusBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_NECKLACE_FOCUS.getTypeId());
    }
    
    public static final float getReplenishBonus(final Creature creature) {
        return 1.0f - getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_NECKLACE_REPLENISH.getTypeId());
    }
    
    public static final float getDamReductionBonusFor(final ArmourTemplate.ArmourType armourType, final byte woundType, final Item weapon, final Creature creature) {
        float bonus = 0.0f;
        if (armourType == ArmourTemplate.ARMOUR_TYPE_CLOTH) {
            if (woundType == 1) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_COTTON_SLASHDAM.getTypeId(), 0.1f);
            }
        }
        else if (armourType == ArmourTemplate.ARMOUR_TYPE_LEATHER) {
            if (woundType == 0) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_LEATHER_CRUSHDAM.getTypeId(), 0.1f);
            }
        }
        else if (armourType == ArmourTemplate.ARMOUR_TYPE_CHAIN) {
            if (woundType == 1) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_CHAIN_SLASHDAM.getTypeId(), 0.1f);
            }
            if (woundType == 2) {
                bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_CHAIN_PIERCEDAM.getTypeId(), 0.1f);
            }
        }
        if (weapon.getEnchantmentDamageType() > 0) {
            bonus += getBonus(creature.getWurmId(), SpellEffectsEnum.ITEM_ENCHANT_DAMREDUCT.getTypeId(), 0.1f);
        }
        return 1.0f - bonus;
    }
    
    public static final float getBashDodgeBonusFor(final Creature creature) {
        return 1.0f;
    }
    
    static {
        logger = Logger.getLogger(ItemBonus.class.getName());
        playerBonuses = new ConcurrentHashMap<Long, Map<Integer, ItemBonus>>();
    }
}

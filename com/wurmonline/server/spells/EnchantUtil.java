// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.Enchants;

public class EnchantUtil implements Enchants
{
    protected static final Logger logger;
    public static ArrayList<ArrayList<Byte>> enchantGroups;
    
    public static float getDemiseBonus(final Item item, final Creature defender) {
        if (item.enchantment != 0) {
            if (item.enchantment == 11) {
                if (defender.isAnimal() && !defender.isUnique()) {
                    return 0.03f;
                }
            }
            else if (item.enchantment == 9) {
                if (defender.isPlayer() || defender.isHuman()) {
                    return 0.03f;
                }
            }
            else if (item.enchantment == 10) {
                if (defender.isMonster() && !defender.isUnique()) {
                    return 0.03f;
                }
            }
            else if (item.enchantment == 12 && defender.isUnique()) {
                return 0.03f;
            }
        }
        return 0.0f;
    }
    
    public static float getJewelryDamageIncrease(final Creature attacker, final byte woundType) {
        byte jewelryEnchant = 0;
        switch (woundType) {
            case 4: {
                jewelryEnchant = 2;
                break;
            }
            case 5: {
                jewelryEnchant = 1;
                break;
            }
            case 8: {
                jewelryEnchant = 3;
                break;
            }
            case 10: {
                jewelryEnchant = 4;
                break;
            }
        }
        if (jewelryEnchant == 0) {
            return 1.0f;
        }
        final Item[] bodyItems = attacker.getBody().getContainersAndWornItems();
        float damageMultiplier = 1.0f;
        float totalPower = 0.0f;
        int activeJewelry = 0;
        for (final Item bodyItem : bodyItems) {
            if (bodyItem.isEnchantableJewelry() && bodyItem.getBonusForSpellEffect(jewelryEnchant) > 0.0f) {
                ++activeJewelry;
                totalPower += (bodyItem.getCurrentQualityLevel() + bodyItem.getBonusForSpellEffect(jewelryEnchant)) / 2.0f;
            }
        }
        if (totalPower > 0.0f) {
            float increase = 0.025f * activeJewelry + 0.025f * (totalPower / 100.0f);
            increase *= 2.0f / (activeJewelry + 1);
            damageMultiplier *= 1.0f + increase;
        }
        return damageMultiplier;
    }
    
    public static float getJewelryResistModifier(final Creature defender, final byte woundType) {
        byte jewelryEnchant = 0;
        switch (woundType) {
            case 4: {
                jewelryEnchant = 7;
                break;
            }
            case 5: {
                jewelryEnchant = 8;
                break;
            }
            case 8: {
                jewelryEnchant = 6;
                break;
            }
            case 10: {
                jewelryEnchant = 5;
                break;
            }
        }
        if (jewelryEnchant == 0) {
            return 1.0f;
        }
        final Item[] bodyItems = defender.getBody().getContainersAndWornItems();
        float damageMultiplier = 1.0f;
        float totalPower = 0.0f;
        int activeJewelry = 0;
        for (final Item bodyItem : bodyItems) {
            if (bodyItem.isEnchantableJewelry() && bodyItem.getBonusForSpellEffect(jewelryEnchant) > 0.0f) {
                ++activeJewelry;
                totalPower += (bodyItem.getCurrentQualityLevel() + bodyItem.getBonusForSpellEffect(jewelryEnchant)) / 2.0f;
            }
        }
        if (totalPower > 0.0f) {
            float reduction = 0.025f * activeJewelry + 0.05f * (totalPower / 100.0f);
            reduction *= 2.0f / (activeJewelry + 1);
            damageMultiplier *= 1.0f - reduction;
        }
        return damageMultiplier;
    }
    
    public static SpellEffect hasNegatingEffect(final Item target, final byte enchantment) {
        if (target.getSpellEffects() != null) {
            for (final ArrayList<Byte> group : EnchantUtil.enchantGroups) {
                if (group.contains(enchantment)) {
                    for (final byte ench : group) {
                        if (ench == enchantment) {
                            continue;
                        }
                        if (target.getBonusForSpellEffect(ench) > 0.0f) {
                            return target.getSpellEffect(ench);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean canEnchantDemise(final Creature performer, final Item target) {
        if (!Spell.mayBeEnchanted(target)) {
            performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.", (byte)3);
            return false;
        }
        if (target.enchantment != 0) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted.", (byte)3);
            return false;
        }
        if (target.getCurrentQualityLevel() < 70.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is of too low quality for this enchantment.", (byte)3);
            return false;
        }
        return true;
    }
    
    public static void sendInvalidTargetMessage(final Creature performer, final Spell spell) {
        final StringBuilder str = new StringBuilder();
        str.append("You can only target ");
        final ArrayList<String> targets = new ArrayList<String>();
        if (spell.isTargetArmour()) {
            targets.add("armour");
        }
        if (spell.isTargetWeapon()) {
            targets.add("weapons");
        }
        if (spell.isTargetJewelry()) {
            targets.add("jewelry");
        }
        if (spell.isTargetPendulum()) {
            targets.add("pendulums");
        }
        if (targets.size() == 0) {
            EnchantUtil.logger.warning("Spell " + spell.getName() + " has no valid targets.");
        }
        else if (targets.size() == 1) {
            str.append(targets.get(0));
        }
        else if (targets.size() == 2) {
            str.append(targets.get(0)).append(" or ").append(targets.get(1));
        }
        else {
            final StringBuilder allTargets = new StringBuilder();
            for (final String target : targets) {
                if (allTargets.length() > 0) {
                    allTargets.append(", ");
                }
                allTargets.append(target);
            }
            str.append((CharSequence)allTargets);
        }
        str.append(".");
        performer.getCommunicator().sendNormalServerMessage(str.toString());
    }
    
    public static void sendCannotBeEnchantedMessage(final Creature performer) {
        performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.", (byte)3);
    }
    
    public static void sendNegatingEffectMessage(final String name, final Creature performer, final Item target, final SpellEffect negatingEffect) {
        performer.getCommunicator().sendNormalServerMessage(String.format("The %s is already enchanted with %s, which would negate the effect of %s.", target.getName(), negatingEffect.getName(), name), (byte)3);
    }
    
    public static void initializeEnchantGroups() {
        final ArrayList<Byte> speedEffectGroup = new ArrayList<Byte>();
        speedEffectGroup.add((byte)47);
        speedEffectGroup.add((byte)16);
        speedEffectGroup.add((byte)32);
        EnchantUtil.enchantGroups.add(speedEffectGroup);
        final ArrayList<Byte> skillgainEffectGroup = new ArrayList<Byte>();
        skillgainEffectGroup.add((byte)47);
        skillgainEffectGroup.add((byte)13);
        EnchantUtil.enchantGroups.add(skillgainEffectGroup);
        final ArrayList<Byte> weaponDamageEffectGroup = new ArrayList<Byte>();
        weaponDamageEffectGroup.add((byte)45);
        weaponDamageEffectGroup.add((byte)63);
        weaponDamageEffectGroup.add((byte)14);
        weaponDamageEffectGroup.add((byte)33);
        weaponDamageEffectGroup.add((byte)26);
        weaponDamageEffectGroup.add((byte)18);
        weaponDamageEffectGroup.add((byte)27);
        EnchantUtil.enchantGroups.add(weaponDamageEffectGroup);
        final ArrayList<Byte> armourEffectGroup = new ArrayList<Byte>();
        armourEffectGroup.add((byte)17);
        armourEffectGroup.add((byte)46);
        EnchantUtil.enchantGroups.add(armourEffectGroup);
        final ArrayList<Byte> mailboxEffectGroup = new ArrayList<Byte>();
        mailboxEffectGroup.add((byte)20);
        mailboxEffectGroup.add((byte)44);
        EnchantUtil.enchantGroups.add(mailboxEffectGroup);
        final ArrayList<Byte> jewelryEffectGroup = new ArrayList<Byte>();
        jewelryEffectGroup.add((byte)29);
        jewelryEffectGroup.add((byte)1);
        jewelryEffectGroup.add((byte)5);
        jewelryEffectGroup.add((byte)4);
        jewelryEffectGroup.add((byte)8);
        jewelryEffectGroup.add((byte)2);
        jewelryEffectGroup.add((byte)6);
        jewelryEffectGroup.add((byte)3);
        jewelryEffectGroup.add((byte)7);
        EnchantUtil.enchantGroups.add(jewelryEffectGroup);
    }
    
    static {
        logger = Logger.getLogger(EnchantUtil.class.getName());
        EnchantUtil.enchantGroups = new ArrayList<ArrayList<Byte>>();
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import com.wurmonline.server.Servers;
import java.util.HashMap;
import com.wurmonline.server.Features;
import com.wurmonline.server.Server;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.Item;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public final class Weapon implements MiscConstants
{
    private final int itemid;
    private final float damage;
    private final float speed;
    private final float critchance;
    private final int reach;
    private final int weightGroup;
    private final float parryPercent;
    private final double skillPenalty;
    private static float randomizer;
    private static final Map<Integer, Weapon> weapons;
    private static Weapon toCheck;
    private boolean damagedByMetal;
    private static final float critChanceMod = 5.0f;
    private static final float strengthModifier;
    
    public Weapon(final int _itemid, final float _damage, final float _speed, final float _critchance, final int _reach, final int _weightGroup, final float _parryPercent, final double _skillPenalty) {
        this.damagedByMetal = false;
        this.itemid = _itemid;
        this.damage = _damage;
        this.speed = _speed;
        this.critchance = _critchance / 5.0f;
        this.reach = _reach;
        this.weightGroup = _weightGroup;
        this.parryPercent = _parryPercent;
        this.skillPenalty = _skillPenalty;
        Weapon.weapons.put(this.itemid, this);
    }
    
    public static final float getBaseDamageForWeapon(final Item weapon) {
        if (weapon == null) {
            return 0.0f;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.damage;
        }
        return 0.0f;
    }
    
    public static final double getModifiedDamageForWeapon(final Item weapon, final Skill strength) {
        return getModifiedDamageForWeapon(weapon, strength, false);
    }
    
    public static final double getModifiedDamageForWeapon(final Item weapon, final Skill strength, final boolean fullDam) {
        if (fullDam) {
            Weapon.randomizer = 1.0f;
        }
        else {
            Weapon.randomizer = (50.0f + Server.rand.nextFloat() * 50.0f) / 100.0f;
        }
        double damreturn = 1.0;
        if (weapon.isBodyPartAttached()) {
            damreturn = getBaseDamageForWeapon(weapon);
        }
        else {
            damreturn = getBaseDamageForWeapon(weapon) * weapon.getCurrentQualityLevel() / 100.0f;
        }
        damreturn *= 1.0 + strength.getKnowledge(0.0) / Weapon.strengthModifier;
        damreturn *= Weapon.randomizer;
        return damreturn;
    }
    
    public static final float getBaseSpeedForWeapon(final Item weapon) {
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 1.0f;
        }
        float materialMod = 1.0f;
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (weapon.getMaterial()) {
                case 57: {
                    materialMod = 0.9f;
                    break;
                }
                case 7: {
                    materialMod = 1.05f;
                    break;
                }
                case 67: {
                    materialMod = 0.95f;
                    break;
                }
                case 34: {
                    materialMod = 0.96f;
                    break;
                }
                case 13: {
                    materialMod = 0.95f;
                    break;
                }
                case 96: {
                    materialMod = 1.025f;
                    break;
                }
            }
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.speed * materialMod;
        }
        return 20.0f * materialMod;
    }
    
    public static final float getRarityCritMod(final byte rarity) {
        switch (rarity) {
            case 0: {
                return 1.0f;
            }
            case 1: {
                return 1.1f;
            }
            case 2: {
                return 1.3f;
            }
            case 3: {
                return 1.5f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public static final float getCritChanceForWeapon(final Item weapon) {
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 0.01f;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.critchance * getRarityCritMod(weapon.getRarity());
        }
        return 0.0f;
    }
    
    public static final int getReachForWeapon(final Item weapon) {
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 1;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.reach;
        }
        return 1;
    }
    
    public static final int getWeightGroupForWeapon(final Item weapon) {
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 1;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.weightGroup;
        }
        return 10;
    }
    
    public static final double getSkillPenaltyForWeapon(final Item weapon) {
        if (weapon == null || weapon.isBodyPartAttached()) {
            return 0.0;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.skillPenalty;
        }
        return 7.0;
    }
    
    public static final float getWeaponParryPercent(final Item weapon) {
        if (weapon == null) {
            return 0.0f;
        }
        if (weapon.isBodyPart()) {
            return 0.0f;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        if (Weapon.toCheck != null) {
            return Weapon.toCheck.parryPercent;
        }
        return 0.0f;
    }
    
    void setDamagedByMetal(final boolean aDamagedByMetal) {
        this.damagedByMetal = aDamagedByMetal;
    }
    
    public static final boolean isWeaponDamByMetal(final Item weapon) {
        if (weapon == null) {
            return false;
        }
        if (weapon.isBodyPart() && weapon.isBodyPartRemoved()) {
            return true;
        }
        Weapon.toCheck = Weapon.weapons.get(weapon.getTemplateId());
        return Weapon.toCheck != null && Weapon.toCheck.damagedByMetal;
    }
    
    public static double getMaterialDamageBonus(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 56: {
                    return 1.100000023841858;
                }
                case 30: {
                    return 0.9900000095367432;
                }
                case 31: {
                    return 0.9850000143051147;
                }
                case 10: {
                    return 0.6499999761581421;
                }
                case 7: {
                    return 0.9750000238418579;
                }
                case 12: {
                    return 0.5;
                }
                case 67: {
                    return 1.0499999523162842;
                }
                case 34: {
                    return 0.925000011920929;
                }
                case 13: {
                    return 0.8999999761581421;
                }
            }
        }
        else if (material == 56) {
            return 1.100000023841858;
        }
        return 1.0;
    }
    
    public static double getMaterialHunterDamageBonus(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 8: {
                    return 1.100000023841858;
                }
                case 96: {
                    return 1.0499999523162842;
                }
            }
        }
        return 1.0;
    }
    
    public static double getMaterialArmourDamageBonus(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 30: {
                    return 1.0499999523162842;
                }
                case 31: {
                    return 1.0750000476837158;
                }
                case 7: {
                    return 1.0499999523162842;
                }
                case 9: {
                    return 1.024999976158142;
                }
            }
        }
        return 1.0;
    }
    
    public static float getMaterialParryBonus(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 8: {
                    return 1.025f;
                }
                case 34: {
                    return 1.05f;
                }
            }
        }
        return 1.0f;
    }
    
    public static float getMaterialExtraWoundMod(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 10: {
                    return 0.3f;
                }
                case 12: {
                    return 0.75f;
                }
            }
        }
        return 0.0f;
    }
    
    public static byte getMaterialExtraWoundType(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 10: {
                    return 5;
                }
                case 12: {
                    return 5;
                }
            }
        }
        return 5;
    }
    
    public static double getMaterialBashModifier(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 56: {
                    return 1.0750000476837158;
                }
                case 30: {
                    return 1.0499999523162842;
                }
                case 31: {
                    return 1.024999976158142;
                }
                case 10: {
                    return 0.8999999761581421;
                }
                case 57: {
                    return 1.100000023841858;
                }
                case 7: {
                    return 1.100000023841858;
                }
                case 12: {
                    return 1.2000000476837158;
                }
                case 67: {
                    return 1.0750000476837158;
                }
                case 8: {
                    return 1.100000023841858;
                }
                case 9: {
                    return 1.0499999523162842;
                }
                case 34: {
                    return 0.8999999761581421;
                }
                case 13: {
                    return 0.8500000238418579;
                }
                case 96: {
                    return 1.100000023841858;
                }
            }
        }
        return 1.0;
    }
    
    static {
        Weapon.randomizer = 0.0f;
        weapons = new HashMap<Integer, Weapon>();
        Weapon.toCheck = null;
        strengthModifier = (Servers.localServer.isChallengeOrEpicServer() ? 1000.0f : 300.0f);
    }
}

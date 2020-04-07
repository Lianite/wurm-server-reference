// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class ItemEnchantment extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    ItemEnchantment(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long aCooldown) {
        super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, aCooldown);
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!Spell.mayBeEnchanted(target)) {
            EnchantUtil.sendCannotBeEnchantedMessage(performer);
            return false;
        }
        final SpellEffect negatingEffect = EnchantUtil.hasNegatingEffect(target, this.getEnchantment());
        if (negatingEffect != null) {
            EnchantUtil.sendNegatingEffectMessage(this.getName(), performer, target, negatingEffect);
            return false;
        }
        return true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        this.enchantItem(performer, target, this.getEnchantment(), (float)power);
    }
    
    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        this.checkDestroyItem(power, performer, target);
    }
}

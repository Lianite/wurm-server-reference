// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.Creature;

public class DamageSpell extends ReligiousSpell
{
    DamageSpell(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long aCooldown) {
        super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, aCooldown);
    }
    
    public double calculateDamage(final Creature target, final double power, final double baseDamage, final double damagePerPower) {
        double damage = power * damagePerPower;
        damage += baseDamage;
        final double resistance = SpellResist.getSpellResistance(target, this.getNumber());
        damage *= resistance;
        SpellResist.addSpellResistance(target, this.getNumber(), damage);
        return Spell.modifyDamage(target, damage);
    }
}

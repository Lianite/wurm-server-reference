// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public final class Mend extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    Mend() {
        super("Mend", 251, 20, 29, 20, 29, 0L);
        this.targetItem = true;
        this.description = "removes damage from an item at the cost of some quality";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!Spell.mayBeEnchanted(target) || target.isFood()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot mend that.", (byte)3);
            return false;
        }
        if (target.getDamage() <= 0.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is not damaged.", (byte)3);
            return false;
        }
        if (target.getQualityLevel() <= 2.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " would break under the power of the spell.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        final float oldDamage = target.getDamage();
        float qlReduction = 2.0f;
        if (oldDamage < 20.0f) {
            qlReduction *= oldDamage / 20.0f;
        }
        target.setDamage(Math.max(oldDamage - 20.0f, 0.0f));
        target.setQualityLevel(Math.max(target.getQualityLevel() - qlReduction, 0.0f));
    }
    
    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        if (power < -80.0) {
            performer.getCommunicator().sendNormalServerMessage("You fail miserably and the spell has the opposite effect.", (byte)3);
            target.setDamage(target.getDamage() + 1.0f);
        }
    }
}

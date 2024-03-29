// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class Genesis extends DamageSpell
{
    public static final int RANGE = 4;
    public static final double BASE_DAMAGE = 32767.5;
    public static final double DAMAGE_PER_POWER = 491.5125;
    
    public Genesis() {
        super("Genesis", 408, 10, 30, 40, 70, 30000L);
        this.targetCreature = true;
        this.description = "cleanses a creature of a single negative trait";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        return target != performer && performer.getDeity() != null;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if (target.isReborn()) {
            performer.getCommunicator().sendNormalServerMessage("You rid the " + target.getName() + " of its evil spirit and it collapses.");
            final double damage = this.calculateDamage(target, power, 32767.5, 491.5125);
            target.addWoundOfType(performer, (byte)9, 2, false, 1.0f, false, damage, 0.0f, 0.0f, false, true);
        }
        else if (target.removeRandomNegativeTrait()) {
            performer.getCommunicator().sendNormalServerMessage("You rid the " + target.getName() + " of an evil spirit. It will now produce healthier offspring.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " was not possessed by any evil spirit.", (byte)3);
        }
    }
}

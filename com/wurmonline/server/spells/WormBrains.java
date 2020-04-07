// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.AttitudeConstants;

public class WormBrains extends DamageSpell implements AttitudeConstants
{
    public static final int RANGE = 50;
    public static final double BASE_DAMAGE = 17500.0;
    public static final double DAMAGE_PER_POWER = 120.0;
    
    public WormBrains() {
        super("Worm Brains", 430, 15, 40, 40, 51, 60000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "damages the target severely with internal damage";
        this.type = 2;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && performer.faithful) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept your spell on " + target.getName() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2) {
            performer.modifyFaith(-5.0f);
        }
        final double damage = this.calculateDamage(target, power, 17500.0, 120.0);
        target.addWoundOfType(performer, (byte)9, 1, false, 1.0f, false, damage, 0.0f, 0.0f, false, true);
    }
}

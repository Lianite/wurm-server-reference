// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.AttitudeConstants;

public class Inferno extends DamageSpell implements AttitudeConstants
{
    public static final int RANGE = 50;
    public static final double BASE_DAMAGE = 25000.0;
    public static final double DAMAGE_PER_POWER = 75.0;
    
    public Inferno() {
        super("Inferno", 931, 20, 40, 50, 65, 60000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "damages the target with fire from a volcano";
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
        final double damage = this.calculateDamage(target, power, 25000.0, 75.0);
        target.addWoundOfType(performer, (byte)4, 1, true, 1.0f, false, damage, 0.0f, 0.0f, false, true);
    }
}

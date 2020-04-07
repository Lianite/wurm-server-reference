// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.SpellEffects;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class Purge extends ReligiousSpell
{
    public static final int RANGE = 24;
    
    public Purge() {
        super("Purge", 946, 15, 35, 30, 45, 300000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "dispels all effects on the target";
        this.type = 0;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if (performer != target) {
            target.getCommunicator().sendCombatNormalMessage(performer.getNameWithGenus() + " purges you!");
        }
        if (target.getSpellEffects() == null) {
            performer.getCommunicator().sendCombatNormalMessage(String.format("%s has no effects to dispel.", target.getName()));
            return;
        }
        final SpellEffects effs = target.getSpellEffects();
        final SpellEffect[] effects;
        final SpellEffect[] speffs = effects = effs.getEffects();
        for (final SpellEffect speff : effects) {
            if (speff.type != 64 && speff.type != 74 && speff.type != 73) {
                if (speff.type != 75) {
                    if (speff.type != 66 && speff.type != 67 && speff.type != 68 && speff.type != 69) {
                        if (speff.type != 70) {
                            if (speff.getSpellInfluenceType() == 0) {
                                effs.removeSpellEffect(speff);
                            }
                        }
                    }
                }
            }
        }
        performer.getCommunicator().sendCombatNormalMessage(String.format("%s is completely purged of effects!", target.getName()));
    }
}

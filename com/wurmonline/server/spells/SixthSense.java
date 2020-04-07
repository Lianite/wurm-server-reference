// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class SixthSense extends CreatureEnchantment
{
    public static final int RANGE = 4;
    
    SixthSense() {
        super("Sixth Sense", 376, 10, 15, 20, 6, 0L);
        this.targetCreature = true;
        this.enchantment = 21;
        this.effectdesc = "detect hidden dangers.";
        this.description = "detect hidden creatures and traps";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (!target.isPlayer()) {
            performer.getCommunicator().sendNormalServerMessage("You can only cast that on a person.");
            return false;
        }
        return !target.isReborn() && (target.equals(performer) || performer.getDeity() == null || target.getDeity() == null || !target.getDeity().isHateGod() || performer.isFaithful());
    }
}

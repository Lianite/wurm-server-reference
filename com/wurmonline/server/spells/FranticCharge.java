// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class FranticCharge extends CreatureEnchantment
{
    public static final int RANGE = 4;
    
    public FranticCharge() {
        super("Frantic Charge", 423, 5, 20, 30, 30, 0L);
        this.targetCreature = true;
        this.enchantment = 39;
        this.effectdesc = "faster attack and movement speed.";
        this.description = "increases attack and movement speed of a player";
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (!super.precondition(castSkill, performer, target)) {
            return false;
        }
        if (Servers.isThisAPvpServer() && !target.isPlayer()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot cast " + this.getName() + " on " + target.getNameWithGenus());
            return false;
        }
        return true;
    }
}

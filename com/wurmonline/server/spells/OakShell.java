// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class OakShell extends CreatureEnchantment
{
    public static final int RANGE = 4;
    
    public OakShell() {
        super("Oakshell", 404, 10, 20, 19, 35, 30000L);
        this.enchantment = 22;
        this.effectdesc = "increased natural armour.";
        this.description = "increases the natural armour of a creature or player, does not stack with armour";
        this.type = 2;
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

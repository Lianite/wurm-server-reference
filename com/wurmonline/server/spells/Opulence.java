// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public final class Opulence extends ItemEnchantment
{
    public static final int RANGE = 4;
    
    Opulence() {
        super("Opulence", 280, 20, 10, 10, 15, 0L);
        this.targetItem = true;
        this.enchantment = 15;
        this.effectdesc = "will feed you more.";
        this.description = "causes a food item to be more effective at filling you up";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (!Spell.mayBeEnchanted(target) || !target.isFood()) {
            performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.", (byte)3);
            return false;
        }
        return true;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public final class Sunder extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    Sunder() {
        super("Sunder", 253, 30, 50, 30, 60, 0L);
        this.targetItem = true;
        this.description = "deal damage to item";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (performer.mayDestroy(target) && Methods.isActionAllowed(performer, (short)83, target)) {
            return true;
        }
        if (!Spell.mayBeEnchanted(target)) {
            performer.getCommunicator().sendNormalServerMessage("Your spell will not work on that.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        try {
            if (target.getOwner() != -10L) {
                final Creature owner = Server.getInstance().getCreature(target.getOwner());
                if (!performer.equals(owner)) {
                    owner.getCommunicator().sendNormalServerMessage(performer.getName() + " damages your " + target.getName() + ".", (byte)4);
                }
            }
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
        catch (NotOwnedException ex3) {}
        final float qlMod = 1.0f - target.getQualityLevel() / 200.0f;
        final float damMod = 1.0f - target.getDamage() / 100.0f;
        final float weightMod = Math.min(1.0f, 5000.0f / target.getWeightGrams());
        final float sunderDamage = (float)(power / 5.0) * qlMod * damMod * weightMod;
        target.setDamage(target.getDamage() + sunderDamage);
    }
}

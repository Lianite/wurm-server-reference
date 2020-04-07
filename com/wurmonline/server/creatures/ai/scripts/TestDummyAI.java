// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.bodys.Wound;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;

public class TestDummyAI extends CreatureAI
{
    @Override
    protected boolean pollMovement(final Creature c, final long delta) {
        return false;
    }
    
    @Override
    protected boolean pollAttack(final Creature c, final long delta) {
        return false;
    }
    
    @Override
    protected boolean pollBreeding(final Creature c, final long delta) {
        return false;
    }
    
    @Override
    public CreatureAIData createCreatureAIData() {
        return new TestDummyAIData();
    }
    
    @Override
    public void creatureCreated(final Creature c) {
    }
    
    @Override
    public double receivedWound(final Creature c, @Nullable final Creature performer, final byte dmgType, final int dmgPosition, final float armourMod, final double damage) {
        if (performer != null) {
            try {
                final String message = "You dealt " + String.format("%.2f", damage / 65535.0 * 100.0) + " to " + c.getBody().getBodyPart(dmgPosition).getName() + " of type " + Wound.getName(dmgType) + ".";
                performer.getCommunicator().sendNormalServerMessage(message);
            }
            catch (NoSpaceException ex) {}
        }
        return 0.0;
    }
    
    public class TestDummyAIData extends CreatureAIData
    {
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public final class LocateArtifact extends ReligiousSpell
{
    public static final int RANGE = 4;
    
    LocateArtifact() {
        super("Locate Artifact", 271, 30, 70, 70, 80, 1800000L);
        this.targetTile = true;
        this.description = "locates hidden artifacts";
        this.type = 2;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        if (performer.getPower() > 0 && performer.getPower() < 5) {
            performer.getCommunicator().sendNormalServerMessage("You may not cast this spell.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        performer.getCommunicator().sendNormalServerMessage(EndGameItems.locateRandomEndGameItem(performer));
    }
}

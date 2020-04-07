// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;

public class VillageShowPlan extends Question
{
    private final Village deed;
    
    public VillageShowPlan(final Creature aResponder, final Village tokenVill) {
        super(aResponder, "Plan of " + tokenVill.getName(), "", 125, tokenVill.getId());
        this.deed = tokenVill;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
    }
    
    @Override
    public void sendQuestion() {
        final int perimTiles = this.deed.getTotalPerimeterSize();
        this.getResponder().getCommunicator().sendShowDeedPlan(this.getId(), this.deed.getName(), this.deed.getTokenX(), this.deed.getTokenY(), this.deed.getStartX(), this.deed.getStartY(), this.deed.getEndX(), this.deed.getEndY(), perimTiles);
    }
}

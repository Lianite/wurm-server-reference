// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;

public class BartenderAI extends CreatureAI
{
    private static final long MIN_TIME_TALK = 120000L;
    private static final long MIN_TIME_NEWPATH = 30000L;
    private static final int TIMER_SPECTALK = 0;
    private static final int TIMER_NEWPATH = 1;
    
    @Override
    public void creatureCreated(final Creature c) {
        if (c.getCurrentTile().getVillage() != null) {
            ((BartenderAIData)c.getCreatureAIData()).setHomeVillage(c.getCurrentTile().getVillage());
        }
    }
    
    @Override
    protected boolean pollSpecialFinal(final Creature c, final long delta) {
        this.increaseTimer(c, delta, 0);
        if (!this.isTimerReady(c, 0, 120000L)) {
            return false;
        }
        c.say("Come and get some tasty treats!");
        this.resetTimer(c, 0);
        return false;
    }
    
    @Override
    protected boolean pollMovement(final Creature c, final long delta) {
        final BartenderAIData aiData = (BartenderAIData)c.getCreatureAIData();
        if (aiData.getFoodTarget() != null && aiData.getFoodTarget().getTileX() == c.getTileX() && aiData.getFoodTarget().getTileY() == c.getTileY()) {
            c.say("Hey " + aiData.getFoodTarget().getName() + " you look hungry, come and get some food!");
            aiData.setFoodTarget(null);
        }
        if (c.getStatus().getPath() == null) {
            if (aiData.getHomeVillage() != null && c.getCurrentTile().getVillage() != aiData.getHomeVillage()) {
                c.startPathingToTile(this.getMovementTarget(c, aiData.getHomeVillage().getTokenX(), aiData.getHomeVillage().getTokenY()));
                return false;
            }
            this.increaseTimer(c, delta, 1);
            if (this.isTimerReady(c, 1, 30000L)) {
                if (Server.rand.nextInt(100) < 10) {
                    final Creature[] allCreatures;
                    final Creature[] nearbyCreatures = allCreatures = c.getCurrentTile().getZone().getAllCreatures();
                    for (final Creature otherC : allCreatures) {
                        if (otherC != c) {
                            if (otherC.isPlayer()) {
                                if (otherC.getStatus().isHungry()) {
                                    if (otherC.getCurrentTile().getVillage() == aiData.getHomeVillage()) {
                                        if (otherC.getTileX() != c.getTileX() || otherC.getTileY() != c.getTileY()) {
                                            c.startPathingToTile(this.getMovementTarget(c, otherC.getTileX(), otherC.getTileY()));
                                            aiData.setFoodTarget(otherC);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                this.resetTimer(c, 1);
            }
        }
        else {
            this.pathedMovementTick(c);
            if (c.getStatus().getPath().isEmpty()) {
                c.getStatus().setPath(null);
                c.getStatus().setMoving(false);
            }
        }
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
        return new BartenderAIData();
    }
    
    class BartenderAIData extends CreatureAIData
    {
        private Creature currentFoodTarget;
        private Village homeVillage;
        
        BartenderAIData() {
            this.currentFoodTarget = null;
            this.homeVillage = null;
        }
        
        void setHomeVillage(final Village homeVillage) {
            this.homeVillage = homeVillage;
        }
        
        Village getHomeVillage() {
            return this.homeVillage;
        }
        
        void setFoodTarget(final Creature newFoodTarget) {
            this.currentFoodTarget = newFoodTarget;
        }
        
        Creature getFoodTarget() {
            return this.currentFoodTarget;
        }
    }
}

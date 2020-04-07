// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;

public abstract class CreatureAIData
{
    private Creature creature;
    private long lastPollTime;
    private boolean dropsCorpse;
    private float movementSpeedModifier;
    private float sizeModifier;
    private HashMap<Integer, Long> aiTimerMap;
    
    public CreatureAIData() {
        this.lastPollTime = 0L;
        this.dropsCorpse = true;
        this.movementSpeedModifier = 1.0f;
        this.sizeModifier = 1.0f;
        this.aiTimerMap = new HashMap<Integer, Long>();
    }
    
    public void setTimer(final int timer, final long time) {
        if (!this.aiTimerMap.containsKey(timer)) {
            this.aiTimerMap.put(timer, time);
        }
        else {
            this.aiTimerMap.replace(timer, time);
        }
    }
    
    public long getTimer(final int timer) {
        if (!this.aiTimerMap.containsKey(timer)) {
            this.setTimer(timer, 0L);
        }
        return this.aiTimerMap.get(timer);
    }
    
    public void setCreature(final Creature c) {
        this.creature = c;
    }
    
    public Creature getCreature() {
        return this.creature;
    }
    
    public long getLastPollTime() {
        return this.lastPollTime;
    }
    
    public void setLastPollTime(final long lastPollTime) {
        this.lastPollTime = lastPollTime;
    }
    
    public boolean doesDropCorpse() {
        return this.dropsCorpse;
    }
    
    public void setDropsCorpse(final boolean dropsCorpse) {
        this.dropsCorpse = dropsCorpse;
    }
    
    public float getMovementSpeedModifier() {
        return this.movementSpeedModifier;
    }
    
    public void setMovementSpeedModifier(final float movementModifier) {
        this.movementSpeedModifier = movementModifier;
    }
    
    public float getSpeed() {
        return this.creature.getTemplate().getSpeed();
    }
    
    public float getSizeModifier() {
        return this.sizeModifier;
    }
    
    public void setSizeModifier(final float sizeModifier) {
        this.sizeModifier = sizeModifier;
    }
}

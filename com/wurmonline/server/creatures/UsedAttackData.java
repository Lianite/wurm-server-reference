// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

public class UsedAttackData
{
    private float time;
    private int rounds;
    
    public UsedAttackData(final float swingTime, final int round) {
        this.time = swingTime;
        this.rounds = round;
    }
    
    public final int getRounds() {
        return this.rounds;
    }
    
    public final float getTime() {
        return this.time;
    }
    
    public void setRounds(final int numberOfRounds) {
        this.rounds = numberOfRounds;
    }
    
    public void setTime(final float newTime) {
        this.time = newTime;
    }
    
    public void update(final float newTime) {
        this.time = Math.max(0.0f, newTime);
        this.rounds = Math.max(this.rounds - 1, 0);
    }
}

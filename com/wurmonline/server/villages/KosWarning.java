// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

public class KosWarning
{
    public final long playerId;
    public final int newReputation;
    private int ticks;
    public final Village village;
    public final boolean permanent;
    
    public KosWarning(final long pid, final int newRep, final Village vill, final boolean perma) {
        this.ticks = 0;
        this.playerId = pid;
        this.newReputation = newRep;
        this.village = vill;
        this.permanent = perma;
    }
    
    public final int getTick() {
        return this.ticks;
    }
    
    public final int tick() {
        return ++this.ticks;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

public abstract class JournalReward
{
    private final String rewardDescription;
    
    public abstract void runReward(final Player p0);
    
    public JournalReward(final String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }
    
    public String getRewardDesc() {
        return this.rewardDescription;
    }
}

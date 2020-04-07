// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.Players;
import java.util.ArrayList;

public class JournalTier
{
    private final ArrayList<Integer> achievementList;
    private final byte tierId;
    private final String tierName;
    private final byte lastTierId;
    private final byte nextTierId;
    private final int unlockNextNeeded;
    private final int rewardFlagId;
    private JournalReward reward;
    
    public JournalTier(final byte tierId, final String tierName, final byte lastTierId, final byte nextTierId, final int unlockNextNeeded, final int rewardFlag, final int... achievements) {
        this.reward = null;
        this.tierId = tierId;
        this.tierName = tierName;
        this.lastTierId = lastTierId;
        this.nextTierId = nextTierId;
        this.unlockNextNeeded = unlockNextNeeded;
        this.rewardFlagId = rewardFlag;
        this.achievementList = new ArrayList<Integer>();
        for (final int i : achievements) {
            this.achievementList.add(i);
        }
    }
    
    public byte getTierId() {
        return this.tierId;
    }
    
    public String getTierName() {
        return this.tierName;
    }
    
    public boolean containsAchievement(final int achievementId) {
        return this.achievementList.contains(achievementId);
    }
    
    public boolean isVisible(final long playerId) {
        return this.lastTierId < 0 || PlayerJournal.getAllTiers().get(this.lastTierId).isNextTierUnlocked(playerId);
    }
    
    public boolean isNextTierUnlocked(final long playerId) {
        if (!this.isVisible(playerId)) {
            return false;
        }
        final Achievement[] achieves = Achievements.getAchievements(playerId);
        int countCompleted = 0;
        for (final Achievement a : achieves) {
            if (this.achievementList.contains(a.getTemplate().getNumber())) {
                ++countCompleted;
            }
        }
        return countCompleted >= this.unlockNextNeeded;
    }
    
    public boolean shouldUnlockNextTier(final long playerId) {
        if (!this.isVisible(playerId)) {
            return false;
        }
        final Achievement[] achieves = Achievements.getAchievements(playerId);
        int countCompleted = 0;
        for (final Achievement a : achieves) {
            if (this.achievementList.contains(a.getTemplate().getNumber())) {
                ++countCompleted;
            }
        }
        return countCompleted + 1 == this.unlockNextNeeded;
    }
    
    public boolean isRewardUnlocked(final long playerId) {
        if (!this.isVisible(playerId)) {
            return false;
        }
        final Achievement[] achieves = Achievements.getAchievements(playerId);
        int countCompleted = 0;
        for (final Achievement a : achieves) {
            if (this.achievementList.contains(a.getTemplate().getNumber())) {
                ++countCompleted;
            }
        }
        return countCompleted >= this.achievementList.size();
    }
    
    public boolean hasBeenAwarded(final long playerId) {
        final Player p = Players.getInstance().getPlayerOrNull(playerId);
        if (p != null) {
            if (p.hasFlag(this.getRewardFlag())) {
                return true;
            }
        }
        else {
            final PlayerInfo pInf = PlayerInfoFactory.getPlayerInfoWithWurmId(playerId);
            if (pInf != null && pInf.isFlagSet(this.getRewardFlag())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean shouldUnlockReward(final long playerId) {
        if (!this.isVisible(playerId)) {
            return false;
        }
        if (this.hasBeenAwarded(playerId)) {
            return false;
        }
        final Achievement[] achieves = Achievements.getAchievements(playerId);
        int countCompleted = 0;
        for (final Achievement a : achieves) {
            if (this.achievementList.contains(a.getTemplate().getNumber())) {
                ++countCompleted;
            }
        }
        return countCompleted + 1 == this.achievementList.size();
    }
    
    public int getNextUnlockCount() {
        return this.unlockNextNeeded;
    }
    
    public byte getLastTierId() {
        return this.lastTierId;
    }
    
    public byte getNextTierId() {
        return this.nextTierId;
    }
    
    public int getTotalAchievements() {
        return this.achievementList.size();
    }
    
    public ArrayList<Integer> getAchievementList() {
        return this.achievementList;
    }
    
    public JournalTier getNextTier() {
        return PlayerJournal.getAllTiers().get(this.nextTierId);
    }
    
    public int getRewardFlag() {
        return this.rewardFlagId;
    }
    
    public void setReward(final JournalReward jr) {
        this.reward = jr;
    }
    
    public void awardReward(final long playerId) {
        final Player p = Players.getInstance().getPlayerOrNull(playerId);
        if (p == null) {
            return;
        }
        if (this.reward != null) {
            this.reward.runReward(p);
            p.setFlag(this.getRewardFlag(), true);
            p.getCommunicator().sendSafeServerMessage("Congratulations, you fully completed " + this.getTierName() + " and earned the reward: " + this.reward.getRewardDesc(), (byte)2);
        }
    }
    
    public String getRewardString() {
        if (this.reward != null) {
            return this.reward.getRewardDesc();
        }
        return "";
    }
}

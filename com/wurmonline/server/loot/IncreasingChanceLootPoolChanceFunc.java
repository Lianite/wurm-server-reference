// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.Optional;
import com.wurmonline.server.creatures.Creature;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class IncreasingChanceLootPoolChanceFunc implements LootPoolChanceFunc
{
    protected static final Logger logger;
    private ConcurrentHashMap<Long, Double> progressiveChances;
    private double percentIncrease;
    
    public IncreasingChanceLootPoolChanceFunc() {
        this.progressiveChances = new ConcurrentHashMap<Long, Double>();
        this.percentIncrease = 0.0010000000474974513;
    }
    
    public IncreasingChanceLootPoolChanceFunc setPercentIncrease(final double increase) {
        this.percentIncrease = increase;
        return this;
    }
    
    @Override
    public boolean chance(final Creature victim, final Creature receiver, final LootPool pool) {
        final double r = pool.getRandom().nextDouble();
        final double bonus = Optional.ofNullable(this.progressiveChances.get(receiver.getWurmId())).orElse(0.0);
        final boolean success = r < pool.getLootPoolChance() + bonus;
        if (!success) {
            this.progressiveChances.put(receiver.getWurmId(), bonus + this.percentIncrease);
            IncreasingChanceLootPoolChanceFunc.logger.info(receiver.getName() + " failed loot pool chance for " + pool.getName() + ". Increasing chance by " + this.percentIncrease + " to " + (pool.getLootPoolChance() + this.progressiveChances.get(receiver.getWurmId())));
        }
        else {
            this.progressiveChances.remove(receiver.getWurmId());
            IncreasingChanceLootPoolChanceFunc.logger.info(receiver.getName() + " succeeded loot pool chance for " + pool.getName() + ". Clearing increases.");
        }
        return success;
    }
    
    static {
        logger = Logger.getLogger(IncreasingChanceLootPoolChanceFunc.class.getName());
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class PercentChanceLootPoolChanceFunc implements LootPoolChanceFunc
{
    protected static final Logger logger;
    
    @Override
    public boolean chance(final Creature victim, final Creature receiver, final LootPool pool) {
        final double r = pool.getRandom().nextDouble();
        final boolean success = r < pool.getLootPoolChance();
        if (!success) {
            PercentChanceLootPoolChanceFunc.logger.info(receiver.getName() + " failed loot pool chance for " + pool.getName() + ": " + r + " not less than " + pool.getLootPoolChance());
        }
        return success;
    }
    
    static {
        logger = Logger.getLogger(PercentChanceLootPoolChanceFunc.class.getName());
    }
}

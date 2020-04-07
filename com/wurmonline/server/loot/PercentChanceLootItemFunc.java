// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class PercentChanceLootItemFunc implements LootItemFunc
{
    protected static final Logger logger;
    
    @Override
    public Optional<LootItem> item(final Creature victim, final Creature receiver, final LootPool pool) {
        final List<LootItem> lootItems = pool.getLootItems();
        lootItems.sort(Comparator.comparingDouble(LootItem::getItemChance));
        final double r = pool.getRandom().nextDouble();
        final double n;
        final boolean success;
        return lootItems.stream().filter(i -> {
            success = (n < i.getItemChance());
            if (!success) {
                PercentChanceLootItemFunc.logger.info(receiver.getName() + " failed loot roll for " + i.getItemName() + ": " + n + " not less than " + i.getItemChance());
            }
            return success;
        }).findFirst();
    }
    
    static {
        logger = Logger.getLogger(PercentChanceLootItemFunc.class.getName());
    }
}

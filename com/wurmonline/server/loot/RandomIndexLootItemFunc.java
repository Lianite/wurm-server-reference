// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.Optional;
import com.wurmonline.server.creatures.Creature;

public class RandomIndexLootItemFunc implements LootItemFunc
{
    @Override
    public Optional<LootItem> item(final Creature victim, final Creature receiver, final LootPool pool) {
        return Optional.ofNullable(pool.getLootItems().get(pool.getRandom().nextInt(pool.getLootItems().size())));
    }
}

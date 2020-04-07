// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.Optional;
import com.wurmonline.server.creatures.Creature;

public interface LootItemFunc
{
    Optional<LootItem> item(final Creature p0, final Creature p1, final LootPool p2);
}

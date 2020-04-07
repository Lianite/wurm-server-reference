// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.items.Item;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;

public interface ItemCreateFunc
{
    Optional<Item> create(final Creature p0, final Creature p1, final LootItem p2);
}

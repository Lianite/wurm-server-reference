// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.creatures.Creature;

public interface LootPoolChanceFunc
{
    boolean chance(final Creature p0, final Creature p1, final LootPool p2);
}

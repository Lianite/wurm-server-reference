// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import java.util.Iterator;
import java.util.Map;
import com.wurmonline.server.Server;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import com.wurmonline.server.creatures.Creature;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LootTable
{
    private List<LootPool> lootPools;
    
    public LootTable() {
        this.lootPools = new ArrayList<LootPool>();
    }
    
    public List<LootPool> getLootPools() {
        return this.lootPools;
    }
    
    public LootTable setLootPools(final List<LootPool> lootPools) {
        this.lootPools = lootPools;
        return this;
    }
    
    public LootTable setLootPools(final LootPool[] pools) {
        return this.setLootPools(Arrays.asList(pools));
    }
    
    public LootTable addLootPools(final List<LootPool> lootPools) {
        this.lootPools.addAll(lootPools);
        return this;
    }
    
    public LootTable addLootPools(final LootPool[] pools) {
        return this.addLootPools(Arrays.asList(pools));
    }
    
    public void awardAll(final Creature victim, final HashSet<Creature> receivers) {
        this.lootPools.forEach(pool -> pool.awardLootItem(victim, receivers));
    }
    
    public void awardOne(final Creature victim, final Creature receiver) {
        this.lootPools.forEach(pool -> pool.awardLootItem(victim, receiver));
    }
    
    public static byte rollForRarity(final Creature receiver) {
        final Map<Integer, Byte> chances = new HashMap<Integer, Byte>();
        chances.put(10000, (byte)3);
        chances.put(1000, (byte)2);
        chances.put(100, (byte)1);
        final List<Integer> keys = new ArrayList<Integer>(chances.keySet());
        keys.sort(Comparator.reverseOrder());
        for (final int c : keys) {
            if (Server.rand.nextInt(c) == 0) {
                receiver.getCommunicator().sendRarityEvent();
                return chances.get(c);
            }
        }
        return 0;
    }
}

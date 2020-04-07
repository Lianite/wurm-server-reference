// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;

public class DefaultItemCreateFunc implements ItemCreateFunc
{
    @Override
    public Optional<Item> create(final Creature victim, final Creature receiver, final LootItem lootItem) {
        return ItemFactory.createItemOptional(lootItem.getItemTemplateId(), 50.0f + Server.rand.nextFloat() * 40.0f, victim.getName());
    }
}

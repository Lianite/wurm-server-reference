// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

public class DefaultItemMessageFunc implements ItemMessageFunc
{
    @Override
    public void message(final Creature victim, final Creature receiver, final Item item) {
        receiver.getCommunicator().sendSafeServerMessage("You loot " + item.getNameWithGenus() + " from the corpse.", (byte)2);
        if (receiver.getCurrentTile() != null) {
            receiver.getCurrentTile().broadCastAction(receiver.getName() + " picks up " + item.getNameWithGenus() + " from the corpse.", receiver, false);
        }
    }
}

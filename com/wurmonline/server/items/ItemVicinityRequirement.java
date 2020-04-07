// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Items;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;

public final class ItemVicinityRequirement extends CreationRequirement
{
    public ItemVicinityRequirement(final int aNumber, final int aResourceTemplateId, final int aNumberNeeded, final boolean aConsume, final int aDistance) {
        super(aNumber, aResourceTemplateId, aNumberNeeded, aConsume);
        this.setDistance(aDistance);
    }
    
    public boolean fill(final Creature performer, final Item creation) {
        boolean toReturn = false;
        final VolaTile tile = performer.getCurrentTile();
        if (tile == null) {
            return false;
        }
        final VolaTile[] tiles = Zones.getTilesSurrounding(tile.tilex, tile.tiley, performer.isOnSurface(), this.getDistance());
        if (this.canBeFilled(tiles)) {
            if (this.willBeConsumed()) {
                int found = 0;
                for (int x = 0; x < tiles.length; ++x) {
                    final Item[] items = tiles[x].getItems();
                    for (int i = 0; i < items.length; ++i) {
                        if (items[i].getTemplateId() == this.getResourceTemplateId()) {
                            ++found;
                            Items.destroyItem(items[i].getWurmId());
                            if (found == this.getResourceNumber()) {
                                return true;
                            }
                        }
                    }
                }
            }
            else {
                toReturn = true;
            }
        }
        return toReturn;
    }
    
    public boolean canBeFilled(final VolaTile[] tiles) {
        int found = 0;
        for (int x = 0; x < tiles.length; ++x) {
            final Item[] items = tiles[x].getItems();
            for (int i = 0; i < items.length; ++i) {
                if (items[i].getTemplateId() == this.getResourceTemplateId() && ++found == this.getResourceNumber()) {
                    return true;
                }
            }
        }
        return false;
    }
}

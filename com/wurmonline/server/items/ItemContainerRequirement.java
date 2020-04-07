// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;

final class ItemContainerRequirement extends CreationRequirement
{
    ItemContainerRequirement(final int aNumber, final int aResourceTemplateId, final int aResourceNumber, final int aVolumeNeeded, final boolean aConsume) {
        super(aNumber, aResourceTemplateId, aResourceNumber, aConsume);
        this.setVolumeNeeded(aVolumeNeeded);
    }
    
    @Override
    boolean fill(final Creature performer, final Item container) {
        if (this.canBeFilled(container) && this.willBeConsumed()) {
            int found = 0;
            final Item[] items = container.getAllItems(false);
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
        return false;
    }
    
    private boolean canBeFilled(final Item container) {
        int found = 0;
        final Item[] items = container.getAllItems(false);
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getTemplateId() == this.getResourceTemplateId() && ++found == this.getResourceNumber()) {
                return true;
            }
        }
        return false;
    }
}

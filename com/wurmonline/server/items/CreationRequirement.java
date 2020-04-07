// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;

public class CreationRequirement
{
    private final int resourceTemplateId;
    private final int resourceNumber;
    private float qualityLevelNeeded;
    private float maxDamageAllowed;
    private int volumeNeeded;
    private final boolean consumed;
    private int distance;
    private final int number;
    
    public CreationRequirement(final int aNumber, final int aResourceTemplateId, final int aResourceNumber, final boolean aConsume) {
        this.maxDamageAllowed = 50.0f;
        this.volumeNeeded = 0;
        this.distance = 0;
        this.resourceTemplateId = aResourceTemplateId;
        this.resourceNumber = aResourceNumber;
        this.consumed = aConsume;
        this.number = aNumber;
    }
    
    public final int getNumber() {
        return this.number;
    }
    
    final void setQualityLevelNeeded(final float needed) {
        this.qualityLevelNeeded = needed;
    }
    
    final void setMaxDamageAllowed(final float allowed) {
        this.maxDamageAllowed = allowed;
    }
    
    int getDistance() {
        return this.distance;
    }
    
    void setDistance(final int aDistance) {
        this.distance = aDistance;
    }
    
    final void setVolumeNeeded(final int volume) {
        this.volumeNeeded = volume;
    }
    
    public final boolean willBeConsumed() {
        return this.consumed;
    }
    
    public final int getVolumeNeeded() {
        return this.volumeNeeded;
    }
    
    public final int getResourceTemplateId() {
        return this.resourceTemplateId;
    }
    
    public final int getResourceNumber() {
        return this.resourceNumber;
    }
    
    public final float getQualityLevelNeeded() {
        return this.qualityLevelNeeded;
    }
    
    public final float getMaxDamageAllowed() {
        return this.maxDamageAllowed;
    }
    
    boolean fill(final Creature performer, final Item creation) {
        if (this.canBeFilled(performer)) {
            int found = 0;
            final Item inventory = performer.getInventory();
            Item[] items = inventory.getAllItems(false);
            for (int i = 0; i < items.length; ++i) {
                if (items[i].getTemplateId() == this.resourceTemplateId) {
                    ++found;
                    Items.destroyItem(items[i].getWurmId());
                    if (found == this.resourceNumber) {
                        return true;
                    }
                }
            }
            final Item body = performer.getBody().getBodyItem();
            items = body.getAllItems(false);
            for (int j = 0; j < items.length; ++j) {
                if (items[j].getTemplateId() == this.resourceTemplateId) {
                    ++found;
                    Items.destroyItem(items[j].getWurmId());
                    if (found == this.resourceNumber) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    final boolean canBeFilled(final Creature performer) {
        int found = 0;
        final Item inventory = performer.getInventory();
        Item[] items = inventory.getAllItems(false);
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getTemplateId() == this.resourceTemplateId && ++found == this.resourceNumber) {
                return true;
            }
        }
        final Item body = performer.getBody().getBodyItem();
        items = body.getAllItems(false);
        for (int j = 0; j < items.length; ++j) {
            if (items[j].getTemplateId() == this.resourceTemplateId && ++found == this.resourceNumber) {
                return true;
            }
        }
        return false;
    }
    
    final boolean canRunOnce(final Creature performer) {
        final Item inventory = performer.getInventory();
        Item[] items = inventory.getAllItems(false);
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getTemplateId() == this.resourceTemplateId) {
                return true;
            }
        }
        final Item body = performer.getBody().getBodyItem();
        items = body.getAllItems(false);
        for (int j = 0; j < items.length; ++j) {
            if (items[j].getTemplateId() == this.resourceTemplateId) {
                return true;
            }
        }
        return false;
    }
    
    final boolean runOnce(final Creature performer) {
        final Item inventory = performer.getInventory();
        Item[] items = inventory.getAllItems(false);
        for (int i = 0; i < items.length; ++i) {
            if (items[i].getTemplateId() == this.resourceTemplateId) {
                Items.destroyItem(items[i].getWurmId());
                return true;
            }
        }
        final Item body = performer.getBody().getBodyItem();
        items = body.getAllItems(false);
        for (int j = 0; j < items.length; ++j) {
            if (items[j].getTemplateId() == this.resourceTemplateId) {
                Items.destroyItem(items[j].getWurmId());
                return true;
            }
        }
        return false;
    }
}

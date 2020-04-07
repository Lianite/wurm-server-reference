// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import com.wurmonline.server.Items;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.WurmId;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public final class Order implements MiscConstants, CounterTypes
{
    private final int tilex;
    private final int tiley;
    private final int layer;
    private final long wurmid;
    
    public Order(final int tx, final int ty, final int lay) {
        this.wurmid = -10L;
        this.tilex = tx;
        this.tiley = ty;
        this.layer = lay;
    }
    
    public Order(final long wid) {
        this.wurmid = wid;
        this.tilex = -1;
        this.tiley = -1;
        this.layer = 0;
    }
    
    public boolean isTile() {
        return this.tilex != -1;
    }
    
    private boolean isItem() {
        return this.wurmid != -10L && (WurmId.getType(this.wurmid) == 2 || WurmId.getType(this.wurmid) == 19 || WurmId.getType(this.wurmid) == 20);
    }
    
    public boolean isCreature() {
        return this.wurmid != -10L && (WurmId.getType(this.wurmid) == 1 || WurmId.getType(this.wurmid) == 0);
    }
    
    public boolean isResolved(final int tx, final int ty, final int lay) {
        return tx == this.tilex && ty == this.tiley && this.layer == lay;
    }
    
    public boolean isResolved(final long wid) {
        return wid == this.wurmid;
    }
    
    public Creature getCreature() {
        if (this.isCreature()) {
            try {
                return Server.getInstance().getCreature(this.wurmid);
            }
            catch (Exception ex) {}
        }
        return null;
    }
    
    public Item getItem() {
        if (this.isItem()) {
            try {
                return Items.getItem(this.wurmid);
            }
            catch (Exception ex) {}
        }
        return null;
    }
    
    public int getTileX() {
        return this.tilex;
    }
    
    public int getTileY() {
        return this.tiley;
    }
}

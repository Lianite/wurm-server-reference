// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.items.Item;

public abstract class GenericZone
{
    private Item zoneOwner;
    private float cachedQL;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    
    public GenericZone(final Item i) {
        this.zoneOwner = i;
        this.updateZone();
    }
    
    public abstract float getStrengthForTile(final int p0, final int p1, final boolean p2);
    
    public boolean containsTile(final int tileX, final int tileY) {
        if (this.zoneOwner == null) {
            return false;
        }
        if (this.cachedQL != this.getCurrentQL()) {
            this.updateZone();
        }
        return tileX >= this.startX && tileX <= this.endX && tileY >= this.startY && tileY <= this.endY;
    }
    
    public abstract void updateZone();
    
    public Item getZoneItem() {
        return this.zoneOwner;
    }
    
    public void setZoneItem(final Item i) {
        this.zoneOwner = i;
    }
    
    public void setCachedQL(final float ql) {
        this.cachedQL = ql;
    }
    
    public void setBounds(final int sx, final int sy, final int ex, final int ey) {
        this.startX = sx;
        this.startY = sy;
        this.endX = ex;
        this.endY = ey;
    }
    
    protected abstract float getCurrentQL();
}

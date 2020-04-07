// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.items.Item;

public class InfluenceZone extends GenericZone
{
    public InfluenceZone(final Item i) {
        super(i);
    }
    
    @Override
    public float getStrengthForTile(final int tileX, final int tileY, final boolean surfaced) {
        if (this.getZoneItem() == null) {
            return 0.0f;
        }
        final int xDiff = Math.abs(this.getZoneItem().getTileX() - tileX);
        final int yDiff = Math.abs(this.getZoneItem().getTileY() - tileY);
        return this.getCurrentQL() - Math.max(xDiff, yDiff);
    }
    
    @Override
    public void updateZone() {
        if (this.getZoneItem() == null) {
            this.setCachedQL(0.0f);
            return;
        }
        final int dist = (int)this.getZoneItem().getCurrentQualityLevel();
        this.setBounds(this.getZoneItem().getTileX() - dist, this.getZoneItem().getTileY() - dist, this.getZoneItem().getTileX() + dist, this.getZoneItem().getTileY() + dist);
        this.setCachedQL(this.getZoneItem().getCurrentQualityLevel());
    }
    
    @Override
    protected float getCurrentQL() {
        if (this.getZoneItem() == null) {
            return 0.0f;
        }
        return this.getZoneItem().getCurrentQualityLevel();
    }
}

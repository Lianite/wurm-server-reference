// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.items.Item;

public class TurretZone extends GenericZone
{
    public static final float DISTMOD_QLMULTIPLIER = 5.0f;
    public static final int DISTMOD_TURRET = 3;
    public static final int DISTMOD_ARCHERYTOWER = 5;
    
    public TurretZone(final Item i) {
        super(i);
        this.updateZone();
    }
    
    @Override
    public float getStrengthForTile(final int tileX, final int tileY, final boolean surfaced) {
        if (this.getZoneItem() == null) {
            return 0.0f;
        }
        if (this.getZoneItem().getTemplateId() == 934) {
            return 0.0f;
        }
        if (this.getZoneItem().isOnSurface() != surfaced) {
            return 0.0f;
        }
        if (!this.containsTile(tileX, tileY)) {
            return 0.0f;
        }
        final int xDiff = Math.abs(tileX - this.getZoneItem().getTileX()) * 4;
        final int yDiff = Math.abs(tileY - this.getZoneItem().getTileY()) * 4;
        final float actDist = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        return this.getCurrentQL() - actDist;
    }
    
    @Override
    public void updateZone() {
        if (this.getZoneItem() == null) {
            return;
        }
        final float ql = this.getCurrentQL();
        final float distanceModifier = ql / 100.0f * 5.0f;
        final int dist = (int)((this.getZoneItem().isEnchantedTurret() ? 3 : 5) * distanceModifier);
        this.setBounds(this.getZoneItem().getTileX() - dist, this.getZoneItem().getTileY() - dist, this.getZoneItem().getTileX() + dist, this.getZoneItem().getTileY() + dist);
        this.setCachedQL(ql);
    }
    
    @Override
    protected float getCurrentQL() {
        if (this.getZoneItem() == null) {
            return 0.0f;
        }
        if (this.getZoneItem().isEnchantedTurret() && !this.getZoneItem().isPlanted()) {
            return 0.0f;
        }
        return this.getZoneItem().getCurrentQualityLevel();
    }
}

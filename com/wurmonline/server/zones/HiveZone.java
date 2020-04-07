// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.items.Item;
import java.util.logging.Logger;

public final class HiveZone
{
    private static final Logger logger;
    private Item hive;
    private final int areaRadius;
    
    public HiveZone(final Item hive) {
        this.hive = hive;
        this.areaRadius = 1 + (int)Math.sqrt(hive.getCurrentQualityLevel());
    }
    
    public int getStrengthForTile(final int tileX, final int tileY, final boolean surfaced) {
        return this.areaRadius - Math.max(Math.abs(this.hive.getTileX() - tileX), Math.abs(this.hive.getTileY() - tileY));
    }
    
    public int getStartX() {
        return this.hive.getTileX() - this.areaRadius;
    }
    
    public int getStartY() {
        return this.hive.getTileY() - this.areaRadius;
    }
    
    public int getEndX() {
        return this.hive.getTileX() + this.areaRadius;
    }
    
    public int getEndY() {
        return this.hive.getTileY() + this.areaRadius;
    }
    
    public boolean containsTile(final int tileX, final int tileY) {
        return tileX > this.getStartX() && tileX < this.getEndX() && tileY > this.getStartY() && tileY < this.getEndY();
    }
    
    public boolean isCloseToTile(final int tileX, final int tileY) {
        return this.getDistanceFrom(tileX, tileY) < 10 + this.areaRadius;
    }
    
    public Item getCurrentHive() {
        return this.hive;
    }
    
    public boolean hasHive(final int tilex, final int tiley) {
        return this.hive.getTileX() == tilex && this.hive.getTileY() == tiley;
    }
    
    public int getDistanceFrom(final int tilex, final int tiley) {
        return Math.max(Math.abs(this.hive.getTileX() - tilex), Math.abs(this.hive.getTileY() - tiley));
    }
    
    public boolean isClose(final int tilex, final int tiley) {
        return this.getDistanceFrom(tilex, tiley) < 2;
    }
    
    static {
        logger = Logger.getLogger(HiveZone.class.getName());
    }
}

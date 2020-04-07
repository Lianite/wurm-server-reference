// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

public final class Track
{
    private final int tilex;
    private final int tiley;
    private final int tile;
    private final long time;
    private final String creatureName;
    private final byte direction;
    private final long id;
    
    Track(final long aId, final String aCreatureName, final int x, final int y, final int aTile, final long aTime, final byte aDirection) {
        this.tilex = x;
        this.tiley = y;
        this.id = aId;
        this.creatureName = aCreatureName;
        this.tile = aTile;
        this.time = aTime;
        this.direction = aDirection;
    }
    
    int getTileX() {
        return this.tilex;
    }
    
    int getTileY() {
        return this.tiley;
    }
    
    public long getTime() {
        return this.time;
    }
    
    int getTile() {
        return this.tile;
    }
    
    public String getCreatureName() {
        return this.creatureName;
    }
    
    public int getDirection() {
        return this.direction & 0xFF;
    }
    
    public long getId() {
        return this.id;
    }
}

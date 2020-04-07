// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

public class BuildTile
{
    private final int tilex;
    private final int tiley;
    private final int layer;
    
    public BuildTile(final int tx, final int ty, final int tileLayer) {
        this.tilex = tx;
        this.tiley = ty;
        this.layer = tileLayer;
    }
    
    public int getTileX() {
        return this.tilex;
    }
    
    public int getTileY() {
        return this.tiley;
    }
    
    public int getLayer() {
        return this.layer;
    }
}

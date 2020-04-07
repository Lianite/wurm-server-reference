// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

public class TilePoint
{
    private int tileX;
    private int tileY;
    
    public TilePoint(final int pTileX, final int pTileY) {
        this.tileX = pTileX;
        this.tileY = pTileY;
    }
    
    public void setTileX(final int val) {
        this.tileX = val;
    }
    
    public int getTileX() {
        return this.tileX;
    }
    
    public void setTileY(final int val) {
        this.tileY = val;
    }
    
    public int getTileY() {
        return this.tileY;
    }
    
    @Override
    public String toString() {
        return "[" + this.tileX + "," + this.tileY + "]";
    }
}

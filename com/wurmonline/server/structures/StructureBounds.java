// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.Zones;

public class StructureBounds
{
    TilePoint max;
    TilePoint min;
    
    StructureBounds(final int minTileX, final int minTileY, final int maxTileX, final int maxTileY) {
        this.max = new TilePoint(maxTileX, maxTileY);
        this.min = new TilePoint(minTileX, minTileY);
    }
    
    StructureBounds(final TilePoint pMax, final TilePoint pMin) {
        if (pMax.getTileX() > Zones.worldTileSizeX) {
            pMax.setTileX(Zones.worldTileSizeX);
        }
        if (pMax.getTileY() > Zones.worldTileSizeY) {
            pMax.setTileY(Zones.worldTileSizeY);
        }
        this.max = pMax;
        this.min = pMin;
    }
    
    public TilePoint getMax() {
        return this.max;
    }
    
    public TilePoint getMin() {
        return this.min;
    }
}

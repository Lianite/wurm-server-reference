// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

public final class CaveTile implements Cloneable
{
    private static final CaveTile[] tiles;
    static final CaveTile TILE_HOLE;
    static final CaveTile TILE_ROCK;
    final byte id;
    
    private CaveTile(final String name, final int id) {
        this.id = (byte)id;
        CaveTile.tiles[id] = this;
    }
    
    static CaveTile getTile(final int id) {
        return CaveTile.tiles[id];
    }
    
    byte getId() {
        return this.id;
    }
    
    public static float decodeHeightAsFloat(final int encodedTile) {
        return Tiles.decodeHeightAsFloat(encodedTile);
    }
    
    public static byte decodeCeilingTexture(final int encodedTile) {
        return (byte)(encodedTile >> 28 & 0xF);
    }
    
    public static byte decodeFloorTexture(final int encodedTile) {
        return (byte)(encodedTile >> 24 & 0xF);
    }
    
    public static int decodeCeilingHeight(final int encodedTile) {
        return encodedTile >> 16 & 0xFF;
    }
    
    public static float decodeCeilingHeightAsFloat(final int encodedTile) {
        return (encodedTile >> 16 & 0xFF) / 10.0f;
    }
    
    public static int decodeCaveTileDir(final long wurmId) {
        return (int)(wurmId >> 48) & 0xFF;
    }
    
    static {
        tiles = new CaveTile[256];
        TILE_HOLE = new CaveTile("hole", 0);
        TILE_ROCK = new CaveTile("Rock", 1);
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import com.wurmonline.math.Vector2f;
import javax.annotation.Nonnull;
import com.wurmonline.math.TilePos;

public final class CoordUtils
{
    public static int WorldToTile(final float woldPos) {
        return (int)woldPos >> 2;
    }
    
    @Nonnull
    public static TilePos WorldToTile(final float woldPosX, final float woldPosY) {
        return TilePos.fromXY(WorldToTile(woldPosX), WorldToTile(woldPosY));
    }
    
    @Nonnull
    public static TilePos WorldToTile(final Vector2f woldPos) {
        return TilePos.fromXY(WorldToTile(woldPos.x), WorldToTile(woldPos.y));
    }
    
    public static float TileToWorld(final int tilePos) {
        return tilePos << 2;
    }
    
    @Nonnull
    public static Vector2f TileToWorld(@Nonnull final TilePos tilePos) {
        return new Vector2f(TileToWorld(tilePos.x), TileToWorld(tilePos.y));
    }
    
    public static float TileToWorldTileCenter(final int tilePos) {
        return (tilePos << 2) + 2;
    }
    
    @Nonnull
    public static Vector2f TileToWorldTileCenter(@Nonnull final TilePos tilePos) {
        return new Vector2f(TileToWorldTileCenter(tilePos.x), TileToWorldTileCenter(tilePos.y));
    }
    
    public static int TileToWorldInt(final int tilePos) {
        return tilePos << 2;
    }
}

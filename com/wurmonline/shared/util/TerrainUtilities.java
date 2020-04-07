// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.util.Random;

public final class TerrainUtilities
{
    private static final Random random;
    
    public static float getTreePosX(final int xTile, final int yTile) {
        TerrainUtilities.random.setSeed(xTile * 31273612L + yTile * 4327864168313L);
        return TerrainUtilities.random.nextFloat() * 0.75f + 0.125f;
    }
    
    public static float getTreePosY(final int xTile, final int yTile) {
        TerrainUtilities.random.setSeed(xTile * 31273612L + yTile * 4327864168314L);
        return TerrainUtilities.random.nextFloat() * 0.75f + 0.125f;
    }
    
    public static float getTreeRotation(final int xTile, final int yTile) {
        TerrainUtilities.random.setSeed(xTile * 31273612L + yTile * 4327864168315L);
        return TerrainUtilities.random.nextFloat() * 360.0f;
    }
    
    static {
        random = new Random();
    }
}

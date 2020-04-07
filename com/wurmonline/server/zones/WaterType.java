// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.Constants;
import com.wurmonline.server.MeshTile;
import com.wurmonline.server.Server;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WaterType implements MiscConstants
{
    private static final Logger logger;
    public static final byte NOT_WATER = 0;
    public static final byte WATER = 1;
    public static final byte POND = 2;
    public static final byte LAKE = 3;
    public static final byte SEA = 4;
    public static final byte SEA_SHALLOWS = 5;
    public static final byte LAKE_SHALLOWS = 6;
    public static final byte SHALLOWS = 7;
    static final byte LAKE_RADIUS = 7;
    static final byte POND_RADIUS = 2;
    static final byte SHALLOWS_DEPTH = -25;
    static final byte[][] waterSurface;
    static final byte[][] waterCave;
    
    public static final void calcWaterTypes() {
        final long start = System.nanoTime();
        final int max = Server.surfaceMesh.getSize() - 1;
        surfaceByDepth(WaterType.waterSurface, (byte)0, 0, true, (byte)1);
        setBorder(WaterType.waterSurface, (byte)1, (byte)2);
        fill(WaterType.waterSurface, (byte)1, (byte)2);
        surfaceByDepth(WaterType.waterSurface, (byte)2, -25, false, (byte)5);
        setBorder(WaterType.waterSurface, (byte)2, (byte)4);
        fill(WaterType.waterSurface, (byte)2, (byte)4);
        setTypeByArea(WaterType.waterSurface, 7, (byte)1, (byte)3);
        fill(WaterType.waterSurface, (byte)1, (byte)3);
        setTypeByArea(WaterType.waterSurface, 7, (byte)2, (byte)3);
        fill(WaterType.waterSurface, (byte)2, (byte)3);
        surfaceByDepth(WaterType.waterSurface, (byte)3, -25, false, (byte)6);
        postProcessShallows(WaterType.waterSurface);
        setTypeByArea(WaterType.waterSurface, 2, (byte)1, (byte)2);
        fill(WaterType.waterSurface, (byte)1, (byte)2);
        caveByDepth(WaterType.waterCave, (byte)0, 0, true, (byte)1);
        setTypeByArea(WaterType.waterCave, 7, (byte)1, (byte)3);
        fill(WaterType.waterCave, (byte)1, (byte)3);
        setTypeByArea(WaterType.waterCave, 2, (byte)1, (byte)2);
        fill(WaterType.waterCave, (byte)1, (byte)2);
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        WaterType.logger.info("Calculated water types, size: " + max + " took " + lElapsedTime + " ms");
    }
    
    private static final void fill(final byte[][] map, final byte checkType, final byte setType) {
        final int max = Server.surfaceMesh.getSize() - 1;
        boolean looping = true;
        while (looping) {
            looping = false;
            for (int x = 1; x < max - 1; ++x) {
                for (int y = 1; y < max - 1; ++y) {
                    looping = fillTile(map, x, y, checkType, setType, looping);
                }
            }
            for (int x = max - 1; x > 1; --x) {
                for (int y = 1; y < max - 1; ++y) {
                    looping = fillTile(map, x, y, checkType, setType, looping);
                }
            }
            for (int x = 1; x < max - 1; ++x) {
                for (int y = max - 1; y > 1; --y) {
                    looping = fillTile(map, x, y, checkType, setType, looping);
                }
            }
            for (int x = max - 1; x > 1; --x) {
                for (int y = max - 1; y > 1; --y) {
                    looping = fillTile(map, x, y, checkType, setType, looping);
                }
            }
        }
    }
    
    private static final boolean fillTile(final byte[][] map, final int x, final int y, final byte checkType, final byte setType, final boolean looping) {
        if (map[x][y] == checkType && (map[x - 1][y] == setType || map[x + 1][y] == setType || map[x][y - 1] == setType || map[x][y + 1] == setType)) {
            map[x][y] = setType;
            return true;
        }
        return looping;
    }
    
    private static final void surfaceByDepth(final byte[][] map, final byte checkType, final int depth, final boolean under, final byte setType) {
        for (int max = Server.surfaceMesh.getSize() - 1, x = 0; x <= max; ++x) {
            for (int y = 0; y <= max; ++y) {
                if (map[x][y] == checkType) {
                    final MeshTile mt = new MeshTile(Server.surfaceMesh, x, y);
                    if (mt.isUnder(depth) == under) {
                        map[x][y] = setType;
                    }
                }
            }
        }
    }
    
    private static final void caveByDepth(final byte[][] map, final byte checkType, final int depth, final boolean under, final byte setType) {
        for (int max = Server.caveMesh.getSize() - 1, x = 0; x <= max; ++x) {
            for (int y = 0; y <= max; ++y) {
                final MeshTile mt = new MeshTile(Server.caveMesh, x, y);
                if (map[x][y] == checkType && mt.isUnder(depth) == under) {
                    map[x][y] = setType;
                }
            }
        }
    }
    
    private static final void setBorder(final byte[][] map, final byte checkType, final byte setType) {
        for (int max = Server.surfaceMesh.getSize() - 1, i = 0; i <= max; ++i) {
            if (map[i][0] == checkType) {
                map[i][0] = setType;
            }
            else if (map[i][max] == checkType) {
                map[i][max] = setType;
            }
            else if (map[0][i] == checkType) {
                map[0][i] = setType;
            }
            else if (map[max][i] == checkType) {
                map[max][i] = setType;
            }
        }
    }
    
    private static final void setTypeByArea(final byte[][] map, final int radius, final byte checkType, final byte setType) {
        for (int max = Server.surfaceMesh.getSize() - 1, x = radius; x <= max - radius; ++x) {
            for (int y = radius; y <= max - radius; ++y) {
                boolean ok = map[x][y] == checkType;
                for (int i = 1; i < radius && ok; ++i) {
                    if (map[x - i][y] != checkType) {
                        ok = false;
                    }
                    else if (map[x + i][y] != checkType) {
                        ok = false;
                    }
                    else if (map[x][y - i] != checkType) {
                        ok = false;
                    }
                    else if (map[x][y + i] != checkType) {
                        ok = false;
                    }
                }
                if (ok) {
                    map[x][y] = setType;
                }
            }
        }
    }
    
    private static final void postProcessShallows(final byte[][] map) {
        for (int max = Server.surfaceMesh.getSize() - 1, x = 0; x <= max; ++x) {
            for (int y = 0; y <= max; ++y) {
                if (map[x][y] == 5) {
                    byte closest = 5;
                    for (int ii = 1; ii < max && closest == 5; closest = checkShallows(map, x, y, ii, closest, (byte)4), closest = checkShallows(map, x, y, ii, closest, (byte)3), ++ii) {}
                    if (closest == 3) {
                        map[x][y] = 6;
                    }
                }
            }
        }
    }
    
    private static final byte checkShallows(final byte[][] map, final int x, final int y, final int ii, byte closest, final byte wType) {
        for (int jj = -ii; jj < ii && closest == 5; ++jj) {
            final int mapSize = Server.surfaceMesh.getSize() - 1;
            if (x + jj < mapSize && x - jj >= 1 && y + jj < mapSize) {
                if (y - jj >= 1) {
                    if (x + ii < mapSize && x - ii >= 1 && y + ii < mapSize) {
                        if (y - ii >= 1) {
                            if (map[x + jj][y - ii] == wType) {
                                closest = wType;
                            }
                            if (map[x + ii][y + jj] == wType) {
                                closest = wType;
                            }
                            if (map[x + jj][y + ii] == wType) {
                                closest = wType;
                            }
                            if (map[x - ii][y + jj] == wType) {
                                closest = wType;
                            }
                        }
                    }
                }
            }
        }
        return closest;
    }
    
    public static final byte getWaterType(final int tilex, final int tiley, final boolean onSurface) {
        if (onSurface) {
            return WaterType.waterSurface[tilex][tiley];
        }
        return WaterType.waterCave[tilex][tiley];
    }
    
    public static final String getWaterTypeString(final int tilex, final int tiley, final boolean onSurface) {
        final byte waterType = getWaterType(tilex, tiley, onSurface);
        return getWaterTypeString(waterType);
    }
    
    public static final String getWaterTypeString(final byte waterType) {
        switch (waterType) {
            case 0: {
                return "Not Water";
            }
            case 1: {
                return "Water";
            }
            case 2: {
                return "Pond";
            }
            case 3: {
                return "Lake";
            }
            case 4: {
                return "Sea";
            }
            case 5: {
                return "Sea Shallows";
            }
            case 6: {
                return "Lake Shallows";
            }
            case 7: {
                return "Shallows";
            }
            default: {
                return "Unknown (" + waterType + ")";
            }
        }
    }
    
    public static final boolean isBrackish(final int tilex, final int tiley, final boolean onSurface) {
        final byte waterType = getWaterType(tilex, tiley, onSurface);
        return waterType == 4 || waterType == 5;
    }
    
    static {
        logger = Logger.getLogger(WaterType.class.getName());
        waterSurface = new byte[1 << Constants.meshSize][1 << Constants.meshSize];
        waterCave = new byte[1 << Constants.meshSize][1 << Constants.meshSize];
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.meshgen;

import java.util.logging.Level;
import com.wurmonline.mesh.Tiles;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.Random;
import com.wurmonline.mesh.MeshIO;
import java.util.logging.Logger;

public final class IslandAdder
{
    private static final Logger logger;
    private final MeshIO topLayer;
    private final MeshIO rockLayer;
    private final Random random;
    private final Map<Integer, Set<Integer>> specials;
    public static final byte north = 0;
    public static final byte northeast = 1;
    public static final byte east = 2;
    public static final byte southeast = 3;
    public static final byte south = 4;
    public static final byte southwest = 5;
    public static final byte west = 6;
    public static final byte northwest = 7;
    
    public IslandAdder() throws IOException {
        this(MeshIO.open("top_layer.map"), MeshIO.open("rock_layer.map"));
    }
    
    public IslandAdder(final String directoryName) throws IOException {
        this(MeshIO.open(directoryName + File.separatorChar + "top_layer.map"), MeshIO.open(directoryName + File.separatorChar + "rock_layer.map"));
    }
    
    public IslandAdder(final MeshIO aTopLayer, final MeshIO aRockLayer) {
        this.random = new Random();
        this.specials = new HashMap<Integer, Set<Integer>>();
        this.topLayer = aTopLayer;
        this.rockLayer = aRockLayer;
    }
    
    public void addIslands(final int maxSize) {
        final int maxw = maxSize / 4;
        for (int minw = maxSize / 8, i = maxw; i >= minw; --i) {
            for (int j = 0; j < 2; ++j) {
                final int height;
                final int width = height = i;
                final int x = this.random.nextInt(this.topLayer.getSize() - width - 128) + 64;
                final int y = this.random.nextInt(maxSize - width - 128) + 64;
                final Map<Integer, Set<Integer>> changes = this.maybeAddIsland(x, y, x + width, y + height, false);
                if (changes != null) {
                    IslandAdder.logger.info("Added island size " + i + " @ " + (x + width / 2) + ", " + (y + height / 2));
                }
            }
        }
    }
    
    public Map<Integer, Set<Integer>> addOneIsland(final int maxSizeX, final int maxSizeY) {
        for (int i = 800; i >= 300; --i) {
            for (int j = 0; j < 2; ++j) {
                final int height;
                final int width = height = i;
                final int x = this.random.nextInt(maxSizeX - width - 128) + 64;
                final int y = this.random.nextInt(maxSizeY - width - 128) + 64;
                final Map<Integer, Set<Integer>> changes = this.maybeAddIsland(x, y, x + width, y + height, false);
                if (changes != null) {
                    IslandAdder.logger.info("Added island size " + i + " @ " + (x + width / 2) + ", " + (y + height / 2));
                    return changes;
                }
            }
        }
        return null;
    }
    
    public final Map<Integer, Set<Integer>> forceIsland(final int maxSizeX, final int maxSizeY, final int tilex, final int tiley) {
        final Map<Integer, Set<Integer>> changes = this.maybeAddIsland(tilex, tiley, tilex + maxSizeX, tiley + maxSizeY, true);
        if (changes != null) {
            IslandAdder.logger.info("Added island size " + maxSizeX + "," + maxSizeY + " @ " + (tilex + maxSizeX / 2) + ", " + (tilex + maxSizeY / 2));
            return changes;
        }
        return null;
    }
    
    public Map<Integer, Set<Integer>> addToSpecials(final int x, final int y) {
        Set<Integer> s = this.specials.get(x);
        if (s == null) {
            s = new HashSet<Integer>();
        }
        if (!s.contains(y)) {
            s.add(y);
        }
        this.specials.put(x, s);
        return this.specials;
    }
    
    public Map<Integer, Set<Integer>> addToChanges(final Map<Integer, Set<Integer>> changes, final int x, final int y) {
        Set<Integer> s = changes.get(x);
        if (s == null) {
            s = new HashSet<Integer>();
        }
        if (!s.contains(y)) {
            s.add(y);
        }
        changes.put(x, s);
        return changes;
    }
    
    public Map<Integer, Set<Integer>> createMultiPlateau(final int x0, final int y0, final int x1, final int y1, final int iterations, final int startHeight) {
        int lastx0 = x0;
        int lasty0 = y0;
        int lastx2 = x1;
        int lasty2 = y1;
        final Map<Integer, Set<Integer>> changes = this.createPlateau(x0, y0, x1, y1, startHeight);
        for (int i = 0; i < iterations; ++i) {
            int modx = (lastx2 - lastx0) / (1 + this.random.nextInt(4));
            int mody = (lasty2 - lasty0) / (1 + this.random.nextInt(4));
            if (this.random.nextBoolean()) {
                modx = -modx;
            }
            if (this.random.nextBoolean()) {
                mody = -mody;
            }
            final Map<Integer, Set<Integer>> changes2 = this.createPlateau(lastx0 + modx, lasty0 + mody, lastx2 + modx, lasty2 + mody, startHeight);
            for (final Integer inte : changes2.keySet()) {
                final Set<Integer> vals = changes2.get(inte);
                if (!changes.containsKey(inte)) {
                    changes.put(inte, vals);
                }
                else {
                    final Set<Integer> oldvals = changes.get(inte);
                    for (final Integer newint : vals) {
                        if (!oldvals.contains(newint)) {
                            oldvals.add(newint);
                        }
                    }
                }
            }
            if (this.random.nextBoolean()) {
                lastx0 += modx;
                lasty0 += mody;
                lastx2 += modx;
                lasty2 += mody;
            }
        }
        return changes;
    }
    
    public Map<Integer, Set<Integer>> createPlateau(final int x0, final int y0, final int x1, final int y1, final int startHeight) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        final double dirOffs = this.random.nextDouble() * 3.141592653589793 * 2.0;
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        final int branchCount = this.random.nextInt(7) + 3;
        final float[] branches = new float[branchCount];
        for (int i = 0; i < branchCount; ++i) {
            branches[i] = this.random.nextFloat() * 0.25f + 0.75f;
        }
        final ImprovedNoise noise = new ImprovedNoise(this.random.nextLong());
        int highestHeight = -32768;
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d = od;
                d /= pow;
                if (d < 1.0) {
                    d *= d;
                    d *= d;
                    d = 1.0 - d;
                    final int oldTile = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                    final int height = Tiles.decodeHeight(oldTile);
                    float n = (float)(noise.perlinNoise(x2, y2) * 64.0) + 100.0f;
                    n *= 2.0f;
                    final int hh = (int)(height + (n - height) * d);
                    if (hh > highestHeight) {
                        highestHeight = hh;
                    }
                }
            }
        }
        highestHeight += startHeight + this.random.nextInt(startHeight);
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d = od;
                d /= pow;
                if (d < 1.0) {
                    if (d < 0.30000001192092896) {
                        this.topLayer.setTile(x2, y2, Tiles.encode((short)(highestHeight * 0.7), Tiles.Tile.TILE_ROCK.id, (byte)0));
                        this.rockLayer.setTile(x2, y2, Tiles.encode((short)(highestHeight * 0.7), Tiles.Tile.TILE_ROCK.id, (byte)0));
                        changes = this.addToChanges(changes, x2, y2);
                    }
                    else {
                        final int oldTile = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                        final short height2 = Tiles.decodeHeight(oldTile);
                        final short newHeight = (short)(highestHeight * (1.0 - d) * pow);
                        if (newHeight > height2) {
                            this.topLayer.setTile(x2, y2, Tiles.encode(newHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                            this.rockLayer.setTile(x2, y2, Tiles.encode(newHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                            changes = this.addToChanges(changes, x2, y2);
                        }
                    }
                }
            }
        }
        return changes;
    }
    
    public Map<Integer, Set<Integer>> createRavine(final int startX, final int startY, final int length, final int direction) {
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        int modx = 0;
        int mody = 0;
        switch (direction) {
            case 0: {
                modx = 0;
                mody = -1;
                break;
            }
            case 1: {
                mody = -1;
                modx = 1;
                break;
            }
            case 2: {
                modx = 1;
                mody = 0;
                break;
            }
            case 3: {
                mody = 1;
                modx = 1;
                break;
            }
            case 4: {
                modx = 0;
                mody = 1;
                break;
            }
            case 5: {
                mody = 1;
                modx = -1;
                break;
            }
            case 6: {
                modx = -1;
                mody = 0;
                break;
            }
            case 7: {
                modx = -1;
                mody = -1;
                break;
            }
            default: {
                modx = 0;
                mody = 0;
                break;
            }
        }
        int width = 1;
        final int maxWidth = Math.max(4, length / 10);
        final float maximumLengthDepthPoint = 1 + length / 2;
        final float maximumWidthDepthPoint = 1 + maxWidth / 2;
        final float maxDepth = length / 3.0f;
        int currX = startX;
        int currY = startY;
        IslandAdder.logger.log(Level.INFO, "Max depth=" + maxDepth + " length=" + length + ", " + maximumLengthDepthPoint + "," + maximumWidthDepthPoint);
        for (int dist = 0; dist < length; ++dist) {
            final float currLengthDepth = 1.0f - Math.abs(maximumLengthDepthPoint - dist) / maximumLengthDepthPoint;
            for (int w = 0; w <= width; ++w) {
                final int tx = currX + modx * w;
                final int ty = currY + mody * w;
                final Set<Integer> yset = changes.get(tx);
                if (yset != null) {
                    if (yset.contains(ty)) {
                        continue;
                    }
                }
                try {
                    final int oldTile = this.topLayer.data[tx | ty << this.topLayer.getSizeLevel()];
                    final float height = Tiles.decodeHeightAsFloat(oldTile);
                    final int nt = this.topLayer.data[tx | ty - 1 << this.topLayer.getSizeLevel()];
                    final float nth = Tiles.decodeHeightAsFloat(nt);
                    final int st = this.topLayer.data[tx | ty + 1 << this.topLayer.getSizeLevel()];
                    final float sth = Tiles.decodeHeightAsFloat(st);
                    final int et = this.topLayer.data[tx + 1 | ty << this.topLayer.getSizeLevel()];
                    final float eth = Tiles.decodeHeightAsFloat(et);
                    final int wt = this.topLayer.data[tx - 1 | ty << this.topLayer.getSizeLevel()];
                    final float wth = Tiles.decodeHeightAsFloat(wt);
                    final float minPrevHeight = Math.min(nth, Math.min(sth, Math.min(eth, wth)));
                    float change = currLengthDepth * maxDepth;
                    if (change < height - minPrevHeight) {
                        change = Math.min(change, height - minPrevHeight - 3.0f);
                    }
                    else if (change > height - minPrevHeight) {
                        change = Math.min(change, height - minPrevHeight + 3.0f);
                    }
                    if (change != 0.0f) {
                        final float newDepth = height - change;
                        if (Tiles.decodeHeightAsFloat(this.rockLayer.data[tx | ty << this.rockLayer.getSizeLevel()]) >= newDepth) {
                            IslandAdder.logger.log(Level.INFO, "Setting rock at " + tx + "," + ty + " to " + newDepth);
                            this.topLayer.setTile(tx, ty, Tiles.encode(newDepth, Tiles.Tile.TILE_ROCK.id, (byte)0));
                            this.rockLayer.setTile(tx, ty, Tiles.encode(newDepth, Tiles.Tile.TILE_ROCK.id, (byte)0));
                        }
                        else {
                            IslandAdder.logger.log(Level.INFO, "Rock at " + tx + "," + ty + " is " + Tiles.decodeHeightAsFloat(this.rockLayer.data[tx | ty << this.rockLayer.getSizeLevel()]) + " so setting to " + newDepth);
                            if (this.random.nextInt(5) == 0) {
                                this.topLayer.setTile(tx, ty, Tiles.encode(newDepth, Tiles.decodeType(oldTile), Tiles.decodeData(oldTile)));
                            }
                            else {
                                this.topLayer.setTile(tx, ty, Tiles.encode(newDepth, Tiles.Tile.TILE_DIRT.id, (byte)0));
                            }
                        }
                        changes = this.addToChanges(changes, tx, ty);
                    }
                }
                catch (ArrayIndexOutOfBoundsException ex) {}
            }
            final int rand = this.random.nextInt(20);
            if (modx <= 0 && rand == 0) {
                ++currX;
            }
            else if (modx >= 0 && rand == 1) {
                --currX;
            }
            if (mody <= 0 && rand == 2) {
                ++currY;
            }
            else if (mody >= 0 && rand == 3) {
                --currY;
            }
            int wmod = 0;
            if (rand == 4) {
                wmod = 1;
            }
            else if (rand == 5) {
                wmod = -1;
            }
            currX += modx;
            currY += mody;
            width = (int)Math.max(4.0f, wmod + maxWidth * (currLengthDepth * 2.0f));
        }
        return changes;
    }
    
    private Map<Integer, Set<Integer>> createIndentationXxx(final int x0, final int y0, final int x1, final int y1, final byte newTopLayerTileId, final byte newTopLayerData) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        final double dirOffs = this.random.nextDouble() * 3.141592653589793 * 2.0;
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        final int branchCount = this.random.nextInt(7) + 3;
        final float[] branches = new float[branchCount];
        for (int i = 0; i < branchCount; ++i) {
            branches[i] = this.random.nextFloat() * 0.25f + 0.75f;
        }
        final ImprovedNoise noise = new ImprovedNoise(this.random.nextLong());
        int lowestHeight = 32767;
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d = od;
                d /= pow;
                if (d < 1.0) {
                    d *= d;
                    d *= d;
                    d = 1.0 - d;
                    final int oldTile = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                    final int height = Tiles.decodeHeight(oldTile);
                    float n = (float)(noise.perlinNoise(x2, y2) * 64.0) + 100.0f;
                    n *= 2.0f;
                    final int hh = (int)(height + (n - height) * d);
                    if (hh < lowestHeight) {
                        lowestHeight = hh;
                    }
                }
            }
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d = od;
                d /= pow;
                if (d < 1.0) {
                    this.topLayer.setTile(x2, y2, Tiles.encode((short)lowestHeight, newTopLayerTileId, newTopLayerData));
                    this.rockLayer.setTile(x2, y2, Tiles.encode((short)lowestHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                    changes = this.addToChanges(changes, x2, y2);
                }
                else if (this.random.nextInt(3) == 0) {
                    this.topLayer.setTile(x2, y2, Tiles.encode(Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    this.rockLayer.setTile(x2, y2, Tiles.encode(Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        return changes;
    }
    
    public Map<Integer, Set<Integer>> createRockIndentation(final int x0, final int y0, final int x1, final int y1) {
        return this.createIndentationXxx(x0, y0, x1, y1, Tiles.Tile.TILE_ROCK.id, (byte)0);
    }
    
    public Map<Integer, Set<Integer>> createVolcano(final int x0, final int y0, final int x1, final int y1) {
        Map<Integer, Set<Integer>> changes = this.createIndentationXxx(x0, y0, x1, y1, Tiles.Tile.TILE_LAVA.id, (byte)(-1));
        for (int x2 = x0; x2 < x1; ++x2) {
            for (int y2 = y0; y2 < y1; ++y2) {
                final int oldTile = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                final byte oldType = Tiles.decodeType(oldTile);
                final int height = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                if (oldType == Tiles.Tile.TILE_LAVA.id && !this.isTopLayerFlat(x2, y2)) {
                    this.topLayer.setTile(x2, y2, Tiles.encode((short)height, Tiles.Tile.TILE_ROCK.id, (byte)0));
                    this.rockLayer.setTile(x2, y2, Tiles.encode((short)height, Tiles.Tile.TILE_ROCK.id, (byte)0));
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        return changes;
    }
    
    public boolean isTopLayerFlat(final int tilex, final int tiley) {
        int heightChecked = -32768;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                final short ch = Tiles.decodeHeight(this.topLayer.getTile(tilex + x, tiley + y));
                if (heightChecked == -32768) {
                    heightChecked = ch;
                }
                if (ch != heightChecked) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Map<Integer, Set<Integer>> createCrater(final int x0, final int y0, final int x1, final int y1) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        final double dirOffs = this.random.nextDouble() * 3.141592653589793 * 2.0;
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        final int branchCount = this.random.nextInt(7) + 3;
        final float[] branches = new float[branchCount];
        for (int i = 0; i < branchCount; ++i) {
            branches[i] = this.random.nextFloat() * 0.25f + 0.75f;
        }
        final ImprovedNoise noise = new ImprovedNoise(this.random.nextLong());
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd * xd + yd * yd);
                double dir;
                for (dir = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d = od;
                d /= pow;
                final int oldTile = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                final byte oldType = Tiles.decodeType(oldTile);
                if (d < 1.0) {
                    d *= d;
                    d *= d;
                    d = 1.0 - d;
                    final int height = Tiles.decodeHeight(oldTile);
                    float n = (float)noise.perlinNoise(x2, y2) * 5.0f;
                    n *= 2.0f;
                    final int hh = (int)(height + (n - 1.0f) * d * 50.0);
                    byte type = Tiles.Tile.TILE_DIRT.id;
                    final int diff = hh - height;
                    if (diff < 0 && (oldType == Tiles.Tile.TILE_ROCK.id || oldType == Tiles.Tile.TILE_CLIFF.id)) {
                        type = oldType;
                    }
                    else {
                        if (hh <= 0 && this.random.nextInt(5) == 0) {
                            type = Tiles.Tile.TILE_SAND.id;
                        }
                        if (hh > 5 && this.random.nextInt(100) == 0) {
                            type = Tiles.Tile.TILE_GRASS.id;
                        }
                    }
                    this.topLayer.setTile(x2, y2, Tiles.encode((short)hh, type, (byte)0));
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                double d2 = Math.sqrt(xd * xd + yd * yd);
                final double od2 = d2 * (x1 - x0);
                double dir2;
                for (dir2 = (Math.atan2(yd, xd) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir2 < 0.0; ++dir2) {}
                while (dir2 >= 1.0) {
                    --dir2;
                }
                final int branch2 = (int)(dir2 * branchCount);
                final float step2 = (float)dir2 * branchCount - branch2;
                final float last2 = branches[branch2];
                final float next2 = branches[(branch2 + 1) % branchCount];
                final float pow2 = last2 + (next2 - last2) * step2;
                d2 /= pow2;
                final int height2 = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                int dd = 0;
                float hh2 = height2 / 10.0f - 8.0f;
                d2 = 1.0 - d2;
                if (d2 < 0.0) {
                    d2 = 0.0;
                }
                d2 = Math.sin(d2 * 3.141592653589793) * 2.0 - 1.0;
                if (d2 < 0.0) {
                    d2 = 0.0;
                }
                float n = (float)noise.perlinNoise(x2 / 2.0, y2 / 2.0);
                if (n > 0.5f) {
                    n -= (n - 0.5f) * 2.0f;
                }
                n /= 0.5f;
                if (n < 0.0f) {
                    n = 0.0f;
                }
                hh2 += (float)(n * (x1 - x0) / 8.0f * d2);
                final int oldTile2 = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                final byte oldType2 = Tiles.decodeType(oldTile2);
                if (oldType2 != Tiles.Tile.TILE_ROCK.id && oldType2 != Tiles.Tile.TILE_CLIFF.id) {
                    float ddd = (float)od2 / 16.0f;
                    if (ddd < 1.0f) {
                        ddd = ddd * 2.0f - 1.0f;
                        if (ddd > 1.0f) {
                            ddd = 1.0f;
                        }
                        if (ddd < 0.0f) {
                            ddd = 0.0f;
                        }
                        dd = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                        final float hh3 = Tiles.decodeHeightAsFloat(dd);
                        hh2 = Tiles.decodeHeightAsFloat(this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                        hh2 = hh3 - Math.min(5.0f, (hh3 - hh2) * ddd);
                        this.topLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                        changes = this.addToChanges(changes, x2, y2);
                    }
                    else {
                        dd = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                        hh2 = Tiles.decodeHeightAsFloat(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                        hh2 = hh2 * 0.5f + (int)hh2 / 2 * 2 * 0.5f;
                        if (hh2 > 0.0f) {
                            hh2 += 0.07f;
                        }
                        else {
                            hh2 -= 0.07f;
                        }
                        this.topLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                        if (hh2 < Tiles.decodeHeightAsFloat(this.rockLayer.data[x2 | y2 << this.topLayer.getSizeLevel()])) {
                            this.topLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.Tile.TILE_ROCK.id, (byte)0));
                            this.rockLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.Tile.TILE_ROCK.id, (byte)0));
                        }
                        else {
                            this.topLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                        }
                        changes = this.addToChanges(changes, x2, y2);
                    }
                }
                else {
                    dd = this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()];
                    this.topLayer.setTile(x2, y2, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                    changes = this.addToChanges(changes, x2, y2);
                }
            }
        }
        for (int x2 = x0; x2 < x1; ++x2) {
            for (int y3 = y0; y3 < y1; ++y3) {
                boolean rock = true;
                for (int xx = 0; xx < 2; ++xx) {
                    for (int yy = 0; yy < 2; ++yy) {
                        final int height3 = Tiles.decodeHeight(this.topLayer.data[x2 | y3 << this.topLayer.getSizeLevel()]);
                        final int groundHeight = Tiles.decodeHeight(this.rockLayer.data[x2 | y3 << this.topLayer.getSizeLevel()]);
                        if (groundHeight < height3) {
                            rock = false;
                        }
                        else {
                            final int dd2 = this.topLayer.data[x2 | y3 << this.topLayer.getSizeLevel()];
                            this.topLayer.setTile(x2, y3, Tiles.encode((short)groundHeight, Tiles.decodeType(dd2), Tiles.decodeData(dd2)));
                            changes = this.addToChanges(changes, x2, y3);
                        }
                    }
                }
                if (rock) {
                    final int dd3 = this.topLayer.data[x2 | y3 << this.topLayer.getSizeLevel()];
                    this.topLayer.setTile(x2, y3, Tiles.encode(Tiles.decodeHeight(dd3), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    changes = this.addToChanges(changes, x2, y3);
                }
            }
        }
        return changes;
    }
    
    public Map<Integer, Set<Integer>> maybeAddIsland(final int x0, final int y0, final int x1, final int y1, final boolean forced) {
        final int xm = (x1 + x0) / 2;
        final int ym = (y1 + y0) / 2;
        final double dirOffs = this.random.nextDouble() * 3.141592653589793 * 2.0;
        for (int x2 = x0; x2 < x1; ++x2) {
            final double xd = (x2 - xm) * 2.0 / (x1 - x0);
            for (int y2 = y0; y2 < y1; ++y2) {
                final double yd = (y2 - ym) * 2.0 / (y1 - y0);
                final double d = Math.sqrt(xd * xd + yd * yd);
                if (d < 1.0) {
                    final int height = Tiles.decodeHeight(this.topLayer.data[x2 | y2 << this.topLayer.getSizeLevel()]);
                    if (height > -5 && !forced) {
                        return null;
                    }
                }
            }
        }
        Map<Integer, Set<Integer>> changes = new HashMap<Integer, Set<Integer>>();
        final int branchCount = this.random.nextInt(7) + 3;
        final float[] branches = new float[branchCount];
        for (int i = 0; i < branchCount; ++i) {
            branches[i] = this.random.nextFloat() * 0.25f + 0.75f;
        }
        final ImprovedNoise noise = new ImprovedNoise(this.random.nextLong());
        for (int x3 = x0; x3 < x1; ++x3) {
            final double xd2 = (x3 - xm) * 2.0 / (x1 - x0);
            for (int y3 = y0; y3 < y1; ++y3) {
                final double yd2 = (y3 - ym) * 2.0 / (y1 - y0);
                final double od = Math.sqrt(xd2 * xd2 + yd2 * yd2);
                double dir;
                for (dir = (Math.atan2(yd2, xd2) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir < 0.0; ++dir) {}
                while (dir >= 1.0) {
                    --dir;
                }
                final int branch = (int)(dir * branchCount);
                final float step = (float)dir * branchCount - branch;
                final float last = branches[branch];
                final float next = branches[(branch + 1) % branchCount];
                final float pow = last + (next - last) * step;
                double d2 = od;
                d2 /= pow;
                if (d2 < 1.0) {
                    d2 *= d2;
                    d2 *= d2;
                    d2 = 1.0 - d2;
                    final int height2 = Tiles.decodeHeight(this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                    float n = (float)(noise.perlinNoise(x3, y3) * 64.0) + 100.0f;
                    n *= 2.0f;
                    int hh = (int)(height2 + (n - height2) * d2);
                    byte type = Tiles.Tile.TILE_DIRT.id;
                    if (hh > 5 && this.random.nextInt(100) == 0) {
                        type = Tiles.Tile.TILE_GRASS.id;
                    }
                    if (hh > 0) {
                        hh += (int)0.07f;
                    }
                    else {
                        hh -= (int)0.07f;
                    }
                    this.topLayer.setTile(x3, y3, Tiles.encode((short)hh, type, (byte)0));
                    changes = this.addToChanges(changes, x3, y3);
                }
            }
        }
        for (int x3 = x0; x3 < x1; ++x3) {
            final double xd2 = (x3 - xm) * 2.0 / (x1 - x0);
            for (int y3 = y0; y3 < y1; ++y3) {
                final double yd2 = (y3 - ym) * 2.0 / (y1 - y0);
                double d3 = Math.sqrt(xd2 * xd2 + yd2 * yd2);
                final double od2 = d3 * (x1 - x0);
                double dir2;
                for (dir2 = (Math.atan2(yd2, xd2) + 3.141592653589793) / 6.283185307179586 + dirOffs; dir2 < 0.0; ++dir2) {}
                while (dir2 >= 1.0) {
                    --dir2;
                }
                final int branch2 = (int)(dir2 * branchCount);
                final float step2 = (float)dir2 * branchCount - branch2;
                final float last2 = branches[branch2];
                final float next2 = branches[(branch2 + 1) % branchCount];
                final float pow2 = last2 + (next2 - last2) * step2;
                d3 /= pow2;
                final int height2 = Tiles.decodeHeight(this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                int dd = this.rockLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                float hh2 = height2 / 10.0f - 8.0f;
                d3 = 1.0 - d3;
                if (d3 < 0.0) {
                    d3 = 0.0;
                }
                d3 = Math.sin(d3 * 3.141592653589793) * 2.0 - 1.0;
                if (d3 < 0.0) {
                    d3 = 0.0;
                }
                float n2 = (float)noise.perlinNoise(x3 / 2.0, y3 / 2.0);
                if (n2 > 0.5f) {
                    n2 -= (n2 - 0.5f) * 2.0f;
                }
                n2 /= 0.5f;
                if (n2 < 0.0f) {
                    n2 = 0.0f;
                }
                hh2 += (float)(n2 * (x1 - x0) / 8.0f * d3);
                this.rockLayer.setTile(x3, y3, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                changes = this.addToChanges(changes, x3, y3);
                float ddd = (float)od2 / 16.0f;
                if (ddd < 1.0f) {
                    ddd = ddd * 2.0f - 1.0f;
                    if (ddd > 1.0f) {
                        ddd = 1.0f;
                    }
                    if (ddd < 0.0f) {
                        ddd = 0.0f;
                    }
                    dd = this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                    final float hh3 = Tiles.decodeHeightAsFloat(this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                    hh2 = Tiles.decodeHeightAsFloat(this.rockLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                    hh2 += (hh3 - hh2) * ddd;
                    this.topLayer.setTile(x3, y3, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                    changes = this.addToChanges(changes, x3, y3);
                }
                else {
                    dd = this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                    hh2 = Tiles.decodeHeightAsFloat(this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                    hh2 = hh2 * 0.5f + (int)hh2 / 2 * 2 * 0.5f;
                    if (hh2 > 0.0f) {
                        hh2 += 0.07f;
                    }
                    else {
                        hh2 -= 0.07f;
                    }
                    this.topLayer.setTile(x3, y3, Tiles.encode(hh2, Tiles.decodeType(dd), Tiles.decodeData(dd)));
                    changes = this.addToChanges(changes, x3, y3);
                }
            }
        }
        for (int x3 = x0; x3 < x1; ++x3) {
            final double xd2 = (x3 - xm) * 2.0 / (x1 - x0);
            for (int y3 = y0; y3 < y1; ++y3) {
                final double yd2 = (y3 - ym) * 2.0 / (y1 - y0);
                final double d3 = Math.sqrt(xd2 * xd2 + yd2 * yd2);
                final double od2 = d3 * (x1 - x0);
                boolean rock = true;
                for (int xx = 0; xx < 2; ++xx) {
                    for (int yy = 0; yy < 2; ++yy) {
                        final int height3 = Tiles.decodeHeight(this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                        final int groundHeight = Tiles.decodeHeight(this.rockLayer.data[x3 | y3 << this.topLayer.getSizeLevel()]);
                        if (groundHeight < height3) {
                            rock = false;
                        }
                        else {
                            final int dd2 = this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                            this.topLayer.setTile(x3, y3, Tiles.encode((short)groundHeight, Tiles.decodeType(dd2), Tiles.decodeData(dd2)));
                            changes = this.addToChanges(changes, x3, y3);
                        }
                    }
                }
                if (rock) {
                    final float ddd2 = (float)od2 / 16.0f;
                    if (ddd2 < 1.0f) {
                        final int dd3 = this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                        this.topLayer.setTile(x3, y3, Tiles.encode(Tiles.decodeHeight(dd3), Tiles.Tile.TILE_LAVA.id, (byte)0));
                        changes = this.addToChanges(changes, x3, y3);
                    }
                    else {
                        final int dd3 = this.topLayer.data[x3 | y3 << this.topLayer.getSizeLevel()];
                        this.topLayer.setTile(x3, y3, Tiles.encode(Tiles.decodeHeight(dd3), Tiles.Tile.TILE_ROCK.id, (byte)0));
                        changes = this.addToChanges(changes, x3, y3);
                    }
                }
            }
        }
        return changes;
    }
    
    public void save() throws IOException {
        this.topLayer.setAllRowsDirty();
        this.topLayer.saveAll();
        this.rockLayer.saveAll();
        this.topLayer.close();
        this.rockLayer.close();
    }
    
    public static void main(final String[] args) {
        try {
            IslandAdder.logger.info("Loading maps..");
            final IslandAdder islandAdder = new IslandAdder();
            IslandAdder.logger.info("Adding islands..");
            islandAdder.addIslands(2096);
            IslandAdder.logger.info("Saving islands..");
            islandAdder.save();
            IslandAdder.logger.info("Finished");
        }
        catch (IOException e) {
            IslandAdder.logger.log(Level.SEVERE, "Failed to add islands!", e);
        }
    }
    
    public MeshIO getTopLayer() {
        return this.topLayer;
    }
    
    public MeshIO getRockLayer() {
        return this.rockLayer;
    }
    
    static {
        logger = Logger.getLogger(IslandAdder.class.getName());
    }
}

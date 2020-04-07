// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.meshgen;

import com.wurmonline.mesh.MeshIO;
import java.util.logging.Level;
import java.awt.Color;
import java.awt.image.BufferedImage;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.BushData;
import com.wurmonline.server.Server;
import com.wurmonline.mesh.GrassData;
import java.util.ArrayList;
import com.wurmonline.server.Point;
import com.wurmonline.mesh.Tiles;
import java.util.Random;
import java.util.logging.Logger;

public final class MeshGen
{
    private static final Logger logger;
    private float[][] groundHeight;
    private float[][] height;
    private byte[][] textures;
    private byte[][] textureDatas;
    private int level;
    private int width;
    private static final float MAP_HEIGHT = 1000.0f;
    private static final boolean USE_SPLIT_TREES = true;
    private static final boolean USE_DROP_DIRT_METHOD = false;
    private static final int NUMBER_OF_DIRT_TO_DROP = 40;
    private static final boolean CHECK_STRAIGHT_SLOPES = true;
    private static final int MAX_STRAIGHT_SLOPE = 20;
    private static final boolean CHECK_DIAGONAL_SLOPES = true;
    private static final int MAX_DIAGONAL_SLOPE = 20;
    private static final boolean OLD_MAP_STYLE_ROTATE_AND_FLIP = true;
    private static final float waterBias = 0.1f;
    private int imageLayer;
    private static final byte TREEID;
    private static final byte BUSHID;
    private static final byte GRASSID;
    
    public MeshGen(final int level1, final MeshGenGui.Task task) throws Exception {
        this.imageLayer = 0;
        this.level = level1;
        this.width = 1 << level1;
        MeshGen.logger.info("Level: " + level1);
        MeshGen.logger.info("Width: " + this.width);
        task.setNote(0, "Allocating memory");
        task.setNote(1, "  heights");
        this.height = new float[this.width][this.width];
        task.setNote(25, "  textures");
        this.textures = new byte[this.width][this.width];
        task.setNote(50, "  ground heights");
        this.groundHeight = new float[this.width][this.width];
        task.setNote(75, "  texture data");
        this.textureDatas = new byte[this.width][this.width];
    }
    
    public MeshGen(final Random random, final int aLevel, final MeshGenGui.Task task) throws Exception {
        this(aLevel, task);
        final PerlinNoise perlin = new PerlinNoise(random, aLevel);
        final int steps = 3;
        task.setMax(this.width * 3 * 2);
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                this.textures[x][y] = -1;
                this.height[x][y] = 0.0f;
            }
        }
        perlin.setRandom(new Random(random.nextLong()));
        for (int i = 0; i < 3; ++i) {
            perlin.setRandom(new Random(random.nextLong()));
            task.setNote("Calculating perlin noise..");
            final float[][] hs1 = perlin.generatePerlinNoise(0.3f, (i != 0) ? 1 : 0, task, this.width * i * 2, 0);
            for (int x2 = 0; x2 < this.width; ++x2) {
                task.setNote(x2 + this.width * i * 2 + this.width);
                for (int y2 = 0; y2 < this.width; ++y2) {
                    float h = hs1[x2][y2] - this.height[x2][y2];
                    if (h < 0.0f) {
                        h = -h;
                    }
                    h = (float)Math.pow(h, 1.2);
                    this.height[x2][y2] = h;
                }
            }
        }
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    protected void setData(final float[] data, final MeshGenGui.Task task) throws Exception {
        task.setMax(this.width * 7);
        task.setNote(0, "Normalizing.");
        final Random grassRand = new Random();
        float lowest = Float.MAX_VALUE;
        float highest = Float.MIN_VALUE;
        float numsover50 = 0.0f;
        task.setNote("  Pass 1");
        for (int x = 0; x < this.width; ++x) {
            task.setNote(x);
            for (int y = 0; y < this.width; ++y) {
                this.height[x][y] = data[y + x * this.width];
                final float n1 = this.height[x][y];
                if (n1 < lowest) {
                    lowest = n1;
                }
                if (n1 > highest) {
                    highest = n1;
                }
                if (n1 > 0.1f) {
                    ++numsover50;
                }
                for (int xx = x - 1; xx <= x + 1; ++xx) {
                    for (int yy = y - 1; yy <= y + 1; ++yy) {
                        if (xx >= 0 && yy >= 0 && xx < this.width && yy < this.width) {
                            final float[] array = this.height[x];
                            final int n3 = y;
                            array[n3] += Math.min(data[y + x * this.width], data[yy + xx * this.width]);
                        }
                        else {
                            final float[] array2 = this.height[x];
                            final int n4 = y;
                            array2[n4] += data[y + x * this.width];
                        }
                    }
                }
                final float[] array3 = this.height[x];
                final int n5 = y;
                array3[n5] /= 10.0f;
            }
        }
        float maxHeight = highest - lowest;
        System.out.println("Before percent over 0.1=" + numsover50 / (this.width * this.width) + " highest=" + highest + " lowest=" + lowest + " maxheight=" + maxHeight);
        lowest = Float.MAX_VALUE;
        highest = Float.MIN_VALUE;
        numsover50 = 0.0f;
        task.setNote("  Pass 2");
        for (int x2 = 0; x2 < this.width; ++x2) {
            task.setNote(x2 + this.width);
            for (int y2 = 0; y2 < this.width; ++y2) {
                final float n2 = this.height[x2][y2];
                if (n2 < lowest) {
                    lowest = n2;
                }
                if (n2 > highest) {
                    highest = n2;
                }
                if (n2 > 0.1f) {
                    ++numsover50;
                }
            }
        }
        maxHeight = highest - lowest;
        System.out.println("After percent over 0.1=" + numsover50 / (this.width * this.width) + " highest=" + highest + " lowest=" + lowest + " maxheight=" + maxHeight);
        task.setNote("  Pass 3");
        for (int x2 = 0; x2 < this.width; ++x2) {
            task.setNote(x2 + this.width * 2);
            for (int y2 = 0; y2 < this.width; ++y2) {
                final float n2 = this.height[x2][y2];
                float h = (n2 - 0.1f) / 0.9f;
                if (h > 0.0f) {
                    h += 1.0E-4f;
                }
                else {
                    h *= 0.5f;
                    h -= 1.0E-4f;
                }
                this.height[x2][y2] = h;
                this.groundHeight[x2][y2] = h;
            }
        }
        lowest = Float.MAX_VALUE;
        highest = Float.MIN_VALUE;
        numsover50 = 0.0f;
        task.setNote("  Pass 4");
        for (int x2 = 0; x2 < this.width; ++x2) {
            task.setNote(x2 + this.width * 3);
            for (int y2 = 0; y2 < this.width; ++y2) {
                final float n2 = this.height[x2][y2];
                if (n2 < lowest) {
                    lowest = n2;
                }
                if (n2 > highest) {
                    highest = n2;
                }
                if (n2 > 0.1f) {
                    ++numsover50;
                }
            }
        }
        maxHeight = highest - lowest;
        System.out.println("After THIRD percent over 0.1=" + numsover50 / (this.width * this.width) + " highest=" + highest + " lowest=" + lowest + " maxheight=" + maxHeight);
        System.out.println("Creating rock layer.");
        final float waterConstant = 1.1f;
        final float mapSizeInfluence = 0.035f;
        final float mapSizeMod = this.width * 0.035f;
        final float influenceMod = 0.02f;
        System.out.println("mapSizeMod=" + mapSizeMod + ", waterConstant=" + 1.1f + ", influenceMod=" + 0.02f);
        task.setNote("  Pass 5 - Creating rock layer.");
        for (int x3 = 0; x3 < this.width; ++x3) {
            task.setNote(x3 + this.width * 4);
            for (int y3 = 0; y3 < this.width; ++y3) {
                final float hh = Math.max(0.0f, (this.height[x3][y3] + 0.1f) / 1.1f);
                final float heightModifier = 1.1f - hh;
                final float subtracted = (1.0f - this.getDirtSlope(x3, y3) * mapSizeMod) * 0.02f * heightModifier / 3.0f;
                if (x3 == 3274 && y3 == 1425) {
                    System.out.println("dslope=" + this.getDirtSlope(x3, y3) + ", subtracted=" + subtracted + " heightmod=" + heightModifier + " height " + x3 + "," + y3 + "=" + this.height[x3][y3]);
                }
                final float h2 = this.height[x3][y3] - subtracted;
                this.groundHeight[x3][y3] = h2;
                if (this.groundHeight[x3][y3] > this.height[x3][y3]) {
                    this.groundHeight[x3][y3] = this.height[x3][y3];
                }
            }
        }
        task.setNote("  Pass 6 - Applying Cliff, Rock, and Grass tiles.");
        for (int x3 = 0; x3 < this.width; ++x3) {
            task.setNote(x3 + this.width * 5);
            for (int y3 = 0; y3 < this.width; ++y3) {
                boolean rock = true;
                for (int xx2 = 0; xx2 < 2; ++xx2) {
                    for (int yy2 = 0; yy2 < 2; ++yy2) {
                        if (this.getGroundHeight(x3 + xx2, y3 + yy2) < this.getHeight(x3 + xx2, y3 + yy2)) {
                            if (x3 == 3274 && y3 == 1425) {
                                System.out.println("ggh=" + this.getGroundHeight(x3 + xx2, y3 + yy2) + ", gh=" + this.getHeight(x3 + xx2, y3 + yy2) + ": rock false");
                            }
                            rock = false;
                        }
                    }
                }
                if (rock) {
                    this.textures[x3][y3] = Tiles.Tile.TILE_ROCK.id;
                    this.textureDatas[x3][y3] = 0;
                }
                else {
                    this.setTile(MeshGen.GRASSID, x3, y3, grassRand);
                }
            }
        }
        task.setNote("  Pass 7 - Check Heights of Rock and Cliff tiles");
        for (int x3 = 0; x3 < this.width; ++x3) {
            task.setNote(x3 + this.width * 6);
            for (int y3 = 0; y3 < this.width; ++y3) {
                if ((this.textures[x3][y3] == Tiles.Tile.TILE_CLIFF.id || this.textures[x3][y3] == Tiles.Tile.TILE_ROCK.id) && this.getGroundHeight(x3, y3) < this.getHeight(x3, y3)) {
                    System.out.println("Cliff Error at " + x3 + ", " + y3);
                }
            }
        }
    }
    
    private float getDirtSlope(final int x, final int y) {
        final float hs1 = Math.abs(this.getHeight(x - 1, y) - this.getHeight(x, y));
        final float hs2 = Math.abs(this.getHeight(x + 1, y) - this.getHeight(x, y));
        final float vs1 = Math.abs(this.getHeight(x, y - 1) - this.getHeight(x, y));
        final float vs2 = Math.abs(this.getHeight(x, y + 1) - this.getHeight(x, y));
        final float hs3 = hs1 + hs2;
        final float vs3 = vs1 + vs2;
        return (float)Math.sqrt(vs3 * vs3 + hs3 * hs3);
    }
    
    public void generateGround(final Random random, final MeshGenGui.Task task) {
        final int blurSteps = 1;
        task.setMax(this.width * 1);
        task.setNote("Flowing water..");
        for (int i = 0; i < this.width * this.width / 1000; ++i) {
            task.setNote(i);
        }
        final float[][] h = new float[this.width][this.width];
        for (int j = 0; j < 1; ++j) {
            for (int x = 0; x < this.width; ++x) {
                for (int y = 0; y < this.width; ++y) {
                    System.out.println("Setting " + x + ", " + y + " to 0");
                    h[x][y] = 0.0f;
                }
            }
            for (int x = 0; x < this.width; ++x) {
                task.setNote(x + j * this.width);
                for (int y = 0; y < this.width; ++y) {}
            }
            for (int x = 0; x < this.width; ++x) {
                for (int y = 0; y < this.width; ++y) {
                    h[x][y] = 0.0f;
                }
            }
        }
        for (int x2 = 0; x2 < this.width; ++x2) {
            for (int y2 = 0; y2 < this.width; ++y2) {}
        }
    }
    
    public void generateWater() {
    }
    
    public float getHeight(final int x, final int y) {
        if (x < 0 || y < 0 || x > this.width - 1 || y > this.width - 1) {
            return 0.0f;
        }
        return this.height[x & this.width - 1][y & this.width - 1];
    }
    
    public float getGroundHeight(final int x, final int y) {
        if (x < 0 || y < 0 || x > this.width - 1 || y > this.width - 1) {
            return 0.0f;
        }
        return this.groundHeight[x & this.width - 1][y & this.width - 1];
    }
    
    public float getHeightAndWater(int x, int y) {
        x &= this.width - 1;
        y &= this.width - 1;
        return this.height[x][y];
    }
    
    public void setHeight(final int x, final int y, final float h) {
        this.height[x & this.width - 1][y & this.width - 1] = h;
    }
    
    public void dropADirt(final boolean forceDrop, final Random random) {
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                if (forceDrop || this.getHeight(x, y) > -1.0f) {
                    final Point p = this.findDropTile(x, y, random);
                    this.incHeight(p.getX(), p.getY());
                }
            }
        }
    }
    
    private void incHeight(final int x, final int y) {
        this.setHeight(x, y, this.height[x & this.width - 1][y & this.width - 1] + 1.0E-4f);
    }
    
    private Point findDropTile(final int tileX, final int tileY, final Random random) {
        final ArrayList<Point> slopes = new ArrayList<Point>();
        final short h = (short)(this.getHeight(tileX, tileY) * 1000.0f * 10.0f);
        for (int xx = 1; xx >= -1; --xx) {
            for (int yy = 1; yy >= -1; --yy) {
                final short th = (short)(this.getHeight(tileX + xx, tileY + yy) * 1000.0f * 10.0f);
                if (((xx == 0 && yy != 0) || (yy == 0 && xx != 0)) && th < h - 20) {
                    slopes.add(new Point(tileX + xx, tileY + yy));
                }
                if (xx != 0 && yy != 0 && th < h - 20) {
                    slopes.add(new Point(tileX + xx, tileY + yy));
                }
            }
        }
        if (slopes.size() > 0) {
            int r = 0;
            if (slopes.size() > 1) {
                r = random.nextInt(slopes.size());
            }
            return this.findDropTile(slopes.get(r).getX(), slopes.get(r).getY(), random);
        }
        return new Point(tileX, tileY);
    }
    
    public final void createReeds2() {
        System.out.println("Skipping reeds");
    }
    
    public final void createReeds() {
        MeshGen.logger.info("Creating reeds");
        final Random grassRand = new Random();
        final int grassSeed = grassRand.nextInt();
        final int grassMod = 5;
        final int grassCommonality = this.width / 20;
        for (int xk = 0; xk < this.width - 1; ++xk) {
            for (int yk = 0; yk < this.width - 1; ++yk) {
                final float theight = this.getHeight(xk, yk) * 1000.0f;
                if (theight <= 0.0f && theight > -40.0f) {
                    grassRand.setSeed(grassSeed + (xk / grassCommonality + yk / grassCommonality * 10000));
                    final int randXResult = grassRand.nextInt(5);
                    final int randYResult = grassRand.nextInt(5);
                    if (randXResult == 0 && randYResult == 0) {
                        grassRand.setSeed(System.nanoTime());
                        if (grassRand.nextBoolean()) {
                            byte tileType = Tiles.Tile.TILE_REED.id;
                            if (theight < -2.0f) {
                                tileType = Tiles.Tile.TILE_KELP.id;
                            }
                            final byte tileData = GrassData.encodeGrassTileData(GrassData.GrowthStage.SHORT, GrassData.FlowerType.NONE);
                            this.textures[xk][yk] = tileType;
                            this.textureDatas[xk][yk] = tileData;
                        }
                    }
                }
            }
        }
    }
    
    private void blotch(final byte id, final long amount, final int spread, final int sizeScale, final boolean underwater, final boolean nearRock, final boolean waterOrNoWater, final boolean shallowOnly, final Random random) {
        MeshGen.logger.info("Adding blotch of " + Tiles.getTile(id).getName() + ". Amount=" + amount + ", spread=" + spread + ", sizeSale=" + sizeScale + ", underwater=" + underwater + ", nearRock=" + nearRock + ", waterOrNoWater=" + waterOrNoWater + ", shallowOnly=" + shallowOnly);
        for (int i = 0; i < amount; ++i) {
            final int xo = random.nextInt(this.width);
            final int yo = random.nextInt(this.width);
            boolean ok = false;
            if (nearRock) {
                ok = this.isRock(xo, yo);
            }
            else if (underwater) {
                ok = this.isWater(xo, yo);
            }
            else {
                if (waterOrNoWater) {
                    System.out.println("Water or no water triggered:");
                    new Exception().printStackTrace();
                }
                ok = ((waterOrNoWater || !this.isWater(xo, yo)) && !this.isRock(xo, yo));
            }
            if (ok) {
                for (int size = (random.nextInt(80) + 10) * sizeScale, j = 0; j < size / 5; ++j) {
                    int x = xo;
                    int y = yo;
                    for (int k = 0; k < size / 5; ++k) {
                        if (random.nextInt(2) == 0) {
                            x = x + random.nextInt(spread * 2 + 1) - spread;
                        }
                        else {
                            y = y + random.nextInt(spread * 2 + 1) - spread;
                        }
                        x &= this.width - 1;
                        y &= this.width - 1;
                        for (int xk = x; xk < x + sizeScale / 2 + 1; ++xk) {
                            for (int yk = y; yk < y + sizeScale / 2 + 1; ++yk) {
                                final boolean tileSubmerged = this.isWater(xk, yk);
                                if (underwater && !tileSubmerged) {
                                    ++k;
                                }
                                if ((tileSubmerged && !underwater) || this.isRock(xk, yk)) {
                                    break;
                                }
                                final float theight = this.getHeight(xk, yk);
                                if (shallowOnly) {
                                    if (theight * 1000.0f > -1.0f) {
                                        this.setTile(id, xk & this.width - 1, yk & this.width - 1, random);
                                    }
                                }
                                else {
                                    this.setTile(id, xk & this.width - 1, yk & this.width - 1, random);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void setTile(final byte id, final int x, final int y, final Random random) {
        if (id == MeshGen.GRASSID) {
            final GrassData.GrowthStage growthStage = GrassData.GrowthStage.fromInt(random.nextInt(4));
            final GrassData.FlowerType flowerType = getRandomFlower(6);
            this.textures[x][y] = id;
            this.textureDatas[x][y] = GrassData.encodeGrassTileData(growthStage, flowerType);
        }
        else if (id == Tiles.Tile.TILE_REED.id) {
            final float theight = this.getHeight(x, y) * 1000.0f;
            if (theight < -2.0f) {
                this.textures[x][y] = Tiles.Tile.TILE_KELP.id;
            }
            else {
                this.textures[x][y] = Tiles.Tile.TILE_REED.id;
            }
            this.textureDatas[x][y] = GrassData.encodeGrassTileData(GrassData.GrowthStage.SHORT, GrassData.FlowerType.NONE);
        }
        else {
            this.textures[x][y] = id;
            this.textureDatas[x][y] = 0;
        }
    }
    
    public static GrassData.FlowerType getRandomFlower(final int chance) {
        final int rnd = Server.rand.nextInt(chance * 1000);
        if (rnd >= 1000) {
            return GrassData.FlowerType.NONE;
        }
        if (rnd > 998) {
            return GrassData.FlowerType.FLOWER_7;
        }
        if (rnd > 990) {
            return GrassData.FlowerType.FLOWER_6;
        }
        if (rnd > 962) {
            return GrassData.FlowerType.FLOWER_5;
        }
        if (rnd > 900) {
            return GrassData.FlowerType.FLOWER_4;
        }
        if (rnd > 800) {
            return GrassData.FlowerType.FLOWER_3;
        }
        if (rnd > 500) {
            return GrassData.FlowerType.FLOWER_2;
        }
        return GrassData.FlowerType.FLOWER_1;
    }
    
    private void exposeClay(final long amount, final Random random) {
        MeshGen.logger.info("Attempting to expose " + amount + " tiles of clay");
        for (int i = 0; i < amount; ++i) {
            final int xo = random.nextInt(this.width - 10) + 1;
            final int yo = random.nextInt(this.width - 10) + 1;
            final int w = random.nextInt(2) + 2;
            final int h = random.nextInt(2) + 2;
            boolean fail = false;
            final boolean below = this.height[xo][yo] * 1000.0f < -4.0f;
            final boolean above = this.height[xo][yo] * 1000.0f > 2.0f;
            if (below || above) {
                fail = true;
            }
            else if (this.height[xo][yo] < 0.0f && this.height[xo + w][yo + h] < 0.0f) {
                fail = true;
            }
            if (!fail) {
                for (int x = xo - 1; x < xo + w + 1; ++x) {
                    for (int y = yo - 1; y < yo + h + 1; ++y) {
                        if (this.textures[x][y] == Tiles.Tile.TILE_ROCK.id) {
                            fail = true;
                        }
                        if (this.textures[x][y] == Tiles.Tile.TILE_CLIFF.id) {
                            fail = true;
                        }
                        if (this.textures[x][y] == Tiles.Tile.TILE_TAR.id) {
                            fail = true;
                        }
                        if (this.textures[x][y] == Tiles.Tile.TILE_CLAY.id) {
                            fail = true;
                        }
                    }
                }
            }
            if (!fail) {
                System.out.print(".");
                for (int x = xo; x < xo + w + 1; ++x) {
                    for (int y = yo; y < yo + h + 1; ++y) {
                        if (this.height[x][y] > this.groundHeight[x][y]) {
                            this.height[x][y] = this.height[x][y] * 0.95f + this.groundHeight[x][y] * 0.05f;
                        }
                        if (x < xo + w && y < yo + h) {
                            this.textures[x][y] = Tiles.Tile.TILE_CLAY.id;
                        }
                    }
                }
            }
            else {
                --i;
            }
        }
    }
    
    private void exposeTar(final long amount, final Random random) {
        MeshGen.logger.info("Attempting to expose " + amount + " tiles of tar");
        for (int i = 0; i < amount; ++i) {
            final int xo = random.nextInt(this.width - 10) + 1;
            final int yo = random.nextInt(this.width - 10) + 1;
            final int w = random.nextInt(2) + 2;
            final int h = random.nextInt(2) + 2;
            boolean fail = false;
            for (int x = xo - 1; x < xo + w + 1; ++x) {
                for (int y = yo - 1; y < yo + h + 1; ++y) {
                    if (this.textures[x][y] == Tiles.Tile.TILE_ROCK.id) {
                        fail = true;
                    }
                    if (this.textures[x][y] == Tiles.Tile.TILE_CLIFF.id) {
                        fail = true;
                    }
                    if (this.textures[x][y] == Tiles.Tile.TILE_TAR.id) {
                        fail = true;
                    }
                }
            }
            if (!fail) {
                for (int x = xo; x < xo + w + 1; ++x) {
                    for (int y = yo; y < yo + h + 1; ++y) {
                        if (this.height[x][y] > this.groundHeight[x][y]) {
                            this.height[x][y] = this.height[x][y] * 0.95f + this.groundHeight[x][y] * 0.05f;
                        }
                        if (x < xo + w && y < yo + h) {
                            this.textures[x][y] = Tiles.Tile.TILE_TAR.id;
                        }
                    }
                }
            }
        }
    }
    
    private boolean isRock(final int x, final int y) {
        for (int xx = 0; xx < 2; ++xx) {
            for (int yy = 0; yy < 2; ++yy) {
                if (this.getGroundHeight(x + xx, y + yy) < this.getHeight(x + xx, y + yy)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isWater(final int x, final int y) {
        for (int xx = 0; xx < 2; ++xx) {
            for (int yy = 0; yy < 2; ++yy) {
                if (this.getHeight(x + xx, y + yy) < 0.0f) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void generateTextures(final Random random, final MeshGenGui.Task task) {
        MeshGen.logger.info("Generating texture");
        this.textureDatas = new byte[this.width][this.width];
        task.setMax(100);
        task.setNote(0, "  Convert underwater grass into dirt.");
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.width; ++y) {
                if (!this.isRock(x, y)) {
                    if (!this.isWater(x, y)) {
                        this.setTile(MeshGen.GRASSID, x, y, random);
                    }
                    else {
                        this.textures[x][y] = Tiles.Tile.TILE_DIRT.id;
                    }
                }
            }
        }
        task.setNote(10, "  Adding blotches.");
        final int wsqd = this.width * this.width / 10240;
        final int sizeMod = 1;
        task.setNote(10, "    dirt.");
        this.blotch(Tiles.Tile.TILE_DIRT.id, wsqd / 3, 2, 1, false, true, false, false, random);
        task.setNote(11, "    peat.");
        this.blotch(Tiles.Tile.TILE_PEAT.id, wsqd / 4, 3, 2, false, false, false, false, random);
        task.setNote(12, "    steppe.");
        this.blotch(Tiles.Tile.TILE_STEPPE.id, wsqd / 17 / 1, 4, 8, false, false, false, false, random);
        task.setNote(13, "    desert.");
        this.blotch(Tiles.Tile.TILE_SAND.id, wsqd / 15 / 1, 3, 8, false, false, false, false, random);
        task.setNote(14, "    tundra.");
        this.blotch(Tiles.Tile.TILE_TUNDRA.id, wsqd / 18 / 1, 3, 7, false, false, false, false, random);
        task.setNote(15, "    moss.");
        this.blotch(Tiles.Tile.TILE_MOSS.id, wsqd / 5, 1, 2, false, false, false, false, random);
        task.setNote(16, "    gravel.");
        this.blotch(Tiles.Tile.TILE_GRAVEL.id, wsqd / 3, 5, 2, false, true, false, true, random);
        task.setNote(17, "    underwater sand.");
        this.blotch(Tiles.Tile.TILE_SAND.id, wsqd * 4, 2, 3, true, false, true, false, random);
        task.setNote(18, "    marsh.");
        this.blotch(Tiles.Tile.TILE_MARSH.id, wsqd / 2, 4, 5, true, false, false, true, random);
        task.setNote(19, "    reeds and kelp.");
        this.blotch(Tiles.Tile.TILE_REED.id, wsqd / 2, 2, 3, true, false, false, true, random);
        MeshGen.logger.info("Adding random trees.");
        task.setNote(25, "  Adding random trees.");
        for (int i = 0; i < this.width * this.width * 1; ++i) {
            final int x2 = random.nextInt(this.width);
            final int y2 = random.nextInt(this.width);
            if (random.nextFloat() < 0.04f && this.isTreeCapable(x2, y2, 5)) {
                final int age = this.generateAge(random);
                final GrassData.GrowthTreeStage grassLen = GrassData.GrowthTreeStage.fromInt(random.nextInt(3) + 1);
                if (random.nextInt(4) == 2) {
                    final int type = random.nextInt(BushData.BushType.getLength());
                    this.textures[x2][y2] = BushData.BushType.fromInt(type).asNormalBush();
                    this.textureDatas[x2][y2] = Tiles.encodeTreeData(FoliageAge.fromByte((byte)age), false, false, grassLen);
                }
                else {
                    int type = random.nextInt(TreeData.TreeType.getLength());
                    if (type == TreeData.TreeType.OAK.getTypeId() && random.nextInt(3) != 0) {
                        type = TreeData.TreeType.BIRCH.getTypeId();
                    }
                    if (type == TreeData.TreeType.WILLOW.getTypeId() && random.nextInt(2) != 0) {
                        type = TreeData.TreeType.PINE.getTypeId();
                    }
                    this.textures[x2][y2] = TreeData.TreeType.fromInt(type).asNormalTree();
                    this.textureDatas[x2][y2] = Tiles.encodeTreeData(FoliageAge.fromByte((byte)age), false, false, grassLen);
                }
            }
        }
        MeshGen.logger.info("Making Forests.");
        task.setNote(30, "  Making forests.");
        int info = 31;
        final int infotick = this.width * 16 / 60;
        for (int j = 0; j < this.width * 16; ++j) {
            if ((j + 1) % infotick == 0) {
                task.setNote(info);
                ++info;
            }
            final int x3 = random.nextInt(this.width);
            final int y3 = random.nextInt(this.width);
            final Tiles.Tile tex = Tiles.getTile(this.textures[x3][y3]);
            if (tex.isBush() || tex.isTree()) {
                this.makeForest(x3, y3, tex, this.textureDatas[x3][y3], random);
            }
        }
        task.setNote(92, "  Adding grass plains...");
        this.blotch(Tiles.Tile.TILE_GRASS.id, wsqd / 2 / 1, 3, 20, false, false, false, false, random);
        task.setNote(95, "  Exposing some tar...");
        this.exposeTar(wsqd, random);
        task.setNote(96, "  Exposing some clay...");
        this.exposeClay(wsqd, random);
        MeshGen.logger.info("Finished!");
    }
    
    private void makeForest(final int x, final int y, final Tiles.Tile theTile, final byte data, final Random random) {
        final int sqrw = (int)Math.sqrt(this.width);
        final int maxForestSize = theTile.isBush() ? (this.width / sqrw / 2) : (this.width / sqrw);
        for (int i = 0; i < sqrw * 10; ++i) {
            int count = 0;
            final int scarcity = (theTile.isOak(data) || theTile.isWillow(data)) ? 0 : (random.nextInt(3) * 2 + 1);
            for (int j = 0; j < maxForestSize; ++j) {
                final int xx = x + random.nextInt(maxForestSize * 2 + 1) - maxForestSize;
                final int yy = y + random.nextInt(maxForestSize * 2 + 1) - maxForestSize;
                if (this.isTreeCapable(xx, yy, scarcity)) {
                    this.addTree(x, y, xx, yy, random);
                    ++count;
                }
            }
            if (count == 0) {
                break;
            }
        }
    }
    
    private void addTree(final int origx, final int origy, final int x, final int y, final Random random) {
        this.textures[x][y] = this.textures[origx][origy];
        final int age = this.generateAge(random);
        final GrassData.GrowthTreeStage grassLen = GrassData.GrowthTreeStage.fromInt(random.nextInt(3) + 1);
        this.textureDatas[x][y] = Tiles.encodeTreeData(FoliageAge.fromByte((byte)age), false, false, grassLen);
    }
    
    private int generateAge(final Random random) {
        int age = random.nextInt(13) + 1;
        int age2 = random.nextInt(13) + 1;
        if (age2 > age) {
            age = age2;
        }
        age2 = random.nextInt(13) + 1;
        if (age2 > age) {
            age = age2;
        }
        return age;
    }
    
    private boolean isTreeCapable(final int x, final int y, final int maxNeighbours) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.width) {
            return false;
        }
        if (this.textures[x & this.width - 1][y & this.width - 1] != MeshGen.GRASSID) {
            return false;
        }
        if (this.getGroundHeight(x, y) > 0.65f) {
            return false;
        }
        int neighborTrees = 0;
        for (int xx = x - 1; xx <= x + 1; ++xx) {
            for (int yy = y - 1; yy <= y + 1; ++yy) {
                final int xxx = xx & this.width - 1;
                final int yyy = yy & this.width - 1;
                final Tiles.Tile theTile = Tiles.getTile(this.textures[xxx][yyy]);
                if (theTile.isTree()) {
                    final byte data = this.textureDatas[xxx][yyy];
                    if (theTile.isOak(data) || theTile.isWillow(data)) {
                        return false;
                    }
                    ++neighborTrees;
                }
                else if (theTile.isBush()) {
                    ++neighborTrees;
                }
            }
        }
        return neighborTrees <= maxNeighbours;
    }
    
    public BufferedImage getImage(final MeshGenGui.Task task) {
        return this.getImage(this.imageLayer, task);
    }
    
    public BufferedImage getImage(final int layer, final MeshGenGui.Task task) {
        task.setMax(this.width + 50);
        task.setNote(0, "Generating image.");
        int lWidth = 16384;
        if (lWidth > this.width) {
            lWidth = this.width;
        }
        int yo = this.width - lWidth;
        if (yo < 0) {
            yo = 0;
        }
        int xo = this.width - lWidth;
        if (xo < 0) {
            xo = 0;
        }
        final Random random = new Random();
        if (xo > 0) {
            xo = random.nextInt(xo);
        }
        if (yo > 0) {
            yo = random.nextInt(yo);
        }
        final BufferedImage bi2 = new BufferedImage(lWidth, lWidth, 1);
        final float[] data = new float[lWidth * lWidth * 3];
        task.setNote(1, "  Generate colours.");
        for (int x = 0; x < lWidth; ++x) {
            task.setNote(x + 2);
            int alt = lWidth - 1;
            for (int y = lWidth - 1; y >= 0; --y) {
                float node = 0.0f;
                float node2 = 0.0f;
                if (layer == 0) {
                    node = this.getHeight(x + xo, y + yo);
                    node2 = this.getHeight(x + 1 + xo, y + 1 + yo);
                }
                else {
                    node = this.getGroundHeight(x + xo, y + yo);
                    node2 = this.getGroundHeight(x + 1 + xo, y + 1 + yo);
                }
                final byte tex = this.textures[x + xo][y + yo];
                final float hh = node;
                final float h = (node2 - node) * 1500.0f / 256.0f * (1 << this.level) / 128.0f + hh / 2.0f + 1.0f;
                float r;
                float b;
                float g = b = (r = h * 0.4f);
                if (layer == 0) {
                    final Color color = Tiles.getTile(tex).getColor();
                    r *= color.getRed() / 255.0f * 2.0f;
                    g *= color.getGreen() / 255.0f * 2.0f;
                    b *= color.getBlue() / 255.0f * 2.0f;
                }
                if (r < 0.0f) {
                    r = 0.0f;
                }
                if (r > 1.0f) {
                    r = 1.0f;
                }
                if (g < 0.0f) {
                    g = 0.0f;
                }
                if (g > 1.0f) {
                    g = 1.0f;
                }
                if (b < 0.0f) {
                    b = 0.0f;
                }
                if (b > 1.0f) {
                    b = 1.0f;
                }
                if (node < 0.0f) {
                    r = r * 0.2f + 0.16000001f;
                    g = g * 0.2f + 0.2f;
                    b = b * 0.2f + 0.4f;
                }
                for (int altTarget = y - (int)(this.getHeight(x, y) * 1000.0f / 4.0f); alt > altTarget && alt >= 0; --alt) {
                    data[(x + alt * lWidth) * 3 + 0] = r * 255.0f;
                    data[(x + alt * lWidth) * 3 + 1] = g * 255.0f;
                    data[(x + alt * lWidth) * 3 + 2] = b * 255.0f;
                }
            }
        }
        task.setNote(this.width + 10, "  Convert colours to image.");
        bi2.getRaster().setPixels(0, 0, lWidth, lWidth, data);
        return bi2;
    }
    
    protected int[] getData(final MeshGenGui.Task task) {
        MeshGen.logger.info("Getting data for a " + this.width + 'x' + this.width + " map. Map height is " + 1000.0f);
        task.setMax(this.width);
        task.setNote(0, "getting Surface Data");
        final int[] data = new int[this.width * this.width];
        int x = 0;
        int y = 0;
        try {
            for (y = 0; y < this.width; ++y) {
                task.setNote(y);
                for (x = 0; x < this.width; ++x) {
                    final float lHeight = this.getHeight(x, y) * 1000.0f;
                    final byte tex = this.textures[x][y];
                    final byte texdata = this.textureDatas[x][y];
                    data[x + (y << this.level)] = Tiles.encode(lHeight, tex, texdata);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            MeshGen.logger.log(Level.WARNING, "data: " + data.length + ", x: " + x + ", y: " + y + ", x + (y << (level + 1): " + (x + (y << this.level + 1)), e);
            throw e;
        }
        return data;
    }
    
    protected int[] getRockData(final MeshGenGui.Task task) {
        MeshGen.logger.info("Getting rock data for a " + this.width + 'x' + this.width + " map. Map height is " + 1000.0f);
        task.setMax(this.width);
        task.setNote(0, "getting Surface Data");
        final int[] data = new int[this.width * this.width];
        for (int y = 0; y < this.width; ++y) {
            task.setNote(y);
            for (int x = 0; x < this.width; ++x) {
                final float lHeight = this.getGroundHeight(x, y);
                final byte tex = 0;
                final byte texdata = 0;
                data[x + (y << this.level)] = Tiles.encode(lHeight * 1000.0f, (byte)0, (byte)0);
            }
        }
        return data;
    }
    
    protected int getLevel() {
        return this.level;
    }
    
    public void setData(final MeshIO meshIO, final MeshIO meshIO2, final MeshGenGui.Task task) {
        task.setMax(this.width * 2);
        task.setNote(0, "setting Data");
        for (int x = 0; x < this.width; ++x) {
            task.setNote(x);
            for (int y = 0; y < this.width; ++y) {
                final int tile = meshIO.getTile(x, y);
                this.setHeight(x, y, Tiles.decodeHeightAsFloat(tile) / 1000.0f);
                this.textures[x][y] = Tiles.decodeType(tile);
                this.textureDatas[x][y] = Tiles.decodeData(tile);
            }
        }
        for (int x = 0; x < this.width; ++x) {
            task.setNote(this.width + x);
            for (int y = 0; y < this.width; ++y) {
                final int tile = meshIO2.getTile(x, y);
                final float orgHeight = Tiles.decodeHeightAsFloat(tile) / 1000.0f;
                this.groundHeight[x][y] = orgHeight;
            }
        }
    }
    
    public int getImageLayer() {
        return this.imageLayer;
    }
    
    public void setImageLayer(final int aImageLayer) {
        this.imageLayer = aImageLayer;
    }
    
    static {
        logger = Logger.getLogger(MeshGen.class.getName());
        TREEID = Tiles.Tile.TILE_TREE.id;
        BUSHID = Tiles.Tile.TILE_BUSH.id;
        GRASSID = Tiles.Tile.TILE_GRASS.id;
    }
}

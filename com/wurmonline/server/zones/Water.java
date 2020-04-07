// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.util.Iterator;
import java.io.IOException;
import com.wurmonline.server.Constants;
import java.util.logging.Level;
import com.wurmonline.server.ServerDirInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import com.wurmonline.mesh.MeshIO;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Water extends Thread implements MiscConstants
{
    public boolean shouldStop;
    private static final int ROW_COUNT = 64;
    private static final Logger logger;
    private static MeshIO waterMesh;
    static final int[][] heightsArr;
    static final float[][] addedWaterArr;
    static final float[][] currentWaterArr;
    static final float[][] reservoirArr;
    private static final int INIT = 0;
    private static final int FLOW = 1;
    private static final int ADD_WATER = 2;
    private static final int EVAPORATE = 3;
    private static final int UPDATE = 4;
    private static int phase;
    private static final long SPRINGRAND;
    private final Random waterRand;
    private final ConcurrentHashMap<Long, WaterGenerator> springs;
    public static final int MAX_WATERLEVEL_DECI = 10;
    public static final int MAX_WATERLEVEL_CM = 100;
    private final ConcurrentHashMap<Long, WaterGenerator> changedTileCorners;
    private int rowsPerRun;
    private final int sleepTime = 15;
    private float nowRain;
    
    public Water() {
        super("Water-Thread");
        this.shouldStop = false;
        this.waterRand = new Random();
        this.springs = new ConcurrentHashMap<Long, WaterGenerator>();
        this.changedTileCorners = new ConcurrentHashMap<Long, WaterGenerator>();
        this.rowsPerRun = Math.max(1, Zones.worldTileSizeY / 64);
        this.nowRain = 0.0f;
    }
    
    public static final void loadWaterMesh() {
        final long start = System.nanoTime();
        try {
            Water.waterMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "water.map");
        }
        catch (IOException iex) {
            Water.logger.log(Level.INFO, "water mesh doesn't exist.. creating..");
            final int[] waterArr = new int[(1 << Constants.meshSize) * (1 << Constants.meshSize)];
            for (int x = 0; x < (1 << Constants.meshSize) * (1 << Constants.meshSize); ++x) {
                waterArr[x] = 0;
            }
            try {
                Water.waterMesh = MeshIO.createMap(ServerDirInfo.getFileDBPath() + "water.map", Constants.meshSize, waterArr);
            }
            catch (IOException iox) {
                Water.logger.log(Level.SEVERE, "Failed to create water mesh. Exiting.", iox);
                System.exit(0);
            }
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Water.logger.info("Loading water mesh, size: " + Water.waterMesh.getSize() + " took " + lElapsedTime + " ms");
        }
    }
    
    public final void loadSprings() {
        final int numSprings = (1 << Constants.meshSize) / 64;
        Water.logger.log(Level.INFO, "NUMBER OF SPRINGS=" + numSprings);
        final Random wrand = new Random(Water.SPRINGRAND);
        for (int x = 0; x < numSprings; ++x) {
            final int tx = wrand.nextInt(Zones.worldTileSizeX);
            final int ty = wrand.nextInt(Zones.worldTileSizeY);
            if (this.isAboveWater(tx, ty)) {
                final int th = 25 + wrand.nextInt(25);
                final WaterGenerator wg = new WaterGenerator(tx, ty, true, 0, th);
                this.springs.put(wg.getTileId(), wg);
                Water.logger.log(Level.INFO, "Spring at " + tx + "," + ty + " =" + th);
            }
        }
        Water.logger.log(Level.INFO, "NUMBER OF ACTIVE SPRINGS=" + this.springs.size());
        for (final WaterGenerator generator : this.springs.values()) {
            generator.updateItem();
        }
    }
    
    public static int getCaveWater(final int tilex, final int tiley) {
        final int value = Water.waterMesh.getTile(tilex, tiley);
        final int toReturn = value >> 16 & 0xFFFF;
        return toReturn;
    }
    
    public static void setCaveWater(final int tilex, final int tiley, final int newValue) {
        final int value = Water.waterMesh.getTile(tilex, tiley);
        if ((value >> 16 & 0xFFFF) != newValue) {
            Water.waterMesh.setTile(tilex, tiley, ((Math.max(0, newValue) & 0xFFFF) << 16) + (value & 0xFFFF));
        }
    }
    
    public static int getSurfaceWater(final int tilex, final int tiley) {
        final int value = Water.waterMesh.getTile(tilex, tiley);
        final int toReturn = value & 0xFFFF;
        return toReturn;
    }
    
    public static void setSurfaceWater(final int tilex, final int tiley, final int newValue) {
        final int value = Water.waterMesh.getTile(tilex, tiley);
        if ((value & 0xFFFF) != newValue) {
            Water.waterMesh.setTile(tilex, tiley, (value & 0xFFFF0000) + (Math.max(0, newValue) & 0xFFFF));
        }
    }
    
    private static float getWaterLevel(final int tilex, final int tiley) {
        return Water.currentWaterArr[tilex][tiley];
    }
    
    private static void setWaterLevel(final int tilex, final int tiley, final float newValue) {
        Water.currentWaterArr[tilex][tiley] = newValue;
    }
    
    private static float getAddedWater(final int tilex, final int tiley) {
        return Water.addedWaterArr[tilex][tiley];
    }
    
    private static void clrAddedWater(final int tilex, final int tiley) {
        Water.addedWaterArr[tilex][tiley] = 0.0f;
    }
    
    private static void incAddedWater(final int tilex, final int tiley, final float newValue) {
        final float[] array = Water.addedWaterArr[tilex];
        array[tiley] += newValue;
    }
    
    private static float getReservoir(final int tilex, final int tiley) {
        return Water.reservoirArr[tilex][tiley];
    }
    
    private static void setReservoir(final int tilex, final int tiley, final float newValue) {
        Water.reservoirArr[tilex][tiley] = newValue;
    }
    
    private static int getHeightCm(final int tilex, final int tiley) {
        return Water.heightsArr[tilex][tiley];
    }
    
    private static void setHeightDm(final int tilex, final int tiley, final int newValue) {
        Water.heightsArr[tilex][tiley] = newValue * 10;
    }
    
    public final boolean isAboveWater(final int x, final int y) {
        return Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) > 3;
    }
    
    @Override
    public void run() {
        Water.logger.log(Level.INFO, "WATER ROWS PER RUN=" + this.rowsPerRun + " SLEEPTIME=" + 15 + " SHOULD STOP=" + this.shouldStop);
        for (int y = 0; y < 1 << Constants.meshSize; ++y) {
            for (int x = 0; x < 1 << Constants.meshSize; ++x) {
                setWaterLevel(x, y, getSurfaceWater(x, y) * 10);
            }
        }
        int currY = 0;
        float maxRain = 0.0f;
        while (!this.shouldStop) {
            final int xTiles = Zones.worldTileSizeX;
            try {
                Thread.sleep(15L);
                final float rain = Server.getWeather().getRain();
                final float evaporation = Server.getWeather().getEvaporationRate();
                for (int y2 = 0; y2 < this.rowsPerRun; ++y2) {
                    for (int x2 = 0; x2 < xTiles; ++x2) {
                        if (this.isAboveWater(x2, currY) && x2 > 0 && x2 < Zones.worldTileSizeX && currY > 0 && currY < Zones.worldTileSizeY) {
                            switch (Water.phase) {
                                case 0: {
                                    final int encodedTile = Server.surfaceMesh.getTile(x2, currY);
                                    setHeightDm(x2, currY, Tiles.decodeHeight(encodedTile));
                                    clrAddedWater(x2, currY);
                                    break;
                                }
                                case 1: {
                                    this.doFlow(x2, currY);
                                    break;
                                }
                                case 2: {
                                    this.addWater(x2, currY, rain);
                                    break;
                                }
                                case 3: {
                                    this.doEvaporation(x2, currY, evaporation);
                                    break;
                                }
                                case 4: {
                                    final float newWaterLevel = getWaterLevel(x2, currY);
                                    final int waterLevel = (int)(newWaterLevel / 10.0f);
                                    setSurfaceWater(x2, currY, waterLevel);
                                    WaterGenerator wg = WaterGenerator.getWG(x2, currY);
                                    if (wg != null) {
                                        wg.setHeight(waterLevel);
                                    }
                                    else {
                                        wg = new WaterGenerator(x2, currY, 0, waterLevel);
                                    }
                                    this.addWater(wg);
                                    break;
                                }
                            }
                        }
                    }
                    if (++currY >= Zones.worldTileSizeY) {
                        currY = 0;
                    }
                }
                if (currY == 0) {
                    ++Water.phase;
                    if (Water.phase > 4) {
                        Water.phase = 0;
                    }
                }
            }
            catch (InterruptedException ex2) {}
            try {
                Water.waterMesh.saveNextDirtyRow();
            }
            catch (IOException ex) {
                this.shouldStop = true;
            }
            if ((int)(this.nowRain * 100.0f) > (int)(maxRain * 100.0f)) {
                maxRain = this.nowRain;
            }
        }
        Water.logger.info("Water mesh thread has finished");
        try {
            Water.waterMesh.saveAll();
            Water.waterMesh.close();
        }
        catch (IOException iox) {
            Water.logger.log(Level.WARNING, "Failed to save watermesh!", iox);
        }
    }
    
    private final void doFlow(final int x, final int y) {
        final float[][] moveArr = new float[3][3];
        for (int xx = -1; xx <= 1; ++xx) {
            for (int yy = -1; yy <= 1; ++yy) {
                moveArr[xx + 1][yy + 1] = 0.0f;
            }
        }
        final float amountToFlow = 1.0f;
        int lowerCount = 1;
        while (lowerCount > 0) {
            lowerCount = 0;
            float lowDiffs = 0.0f;
            final float nwl = getWaterLevel(x, y) + moveArr[1][1];
            final float wht = getHeightCm(x, y) + nwl;
            final boolean ok = nwl > 0.1f;
            if (ok) {
                for (int xx2 = -1; xx2 <= 1; ++xx2) {
                    for (int yy2 = -1; yy2 <= 1; ++yy2) {
                        if (xx2 != 0 || yy2 != 0) {
                            final float cht = getHeightCm(x + xx2, y + yy2) + getWaterLevel(x + xx2, y + yy2) + moveArr[xx2 + 1][yy2 + 1];
                            final float diff = wht - cht;
                            if (diff > 0.0f) {
                                ++lowerCount;
                                lowDiffs += diff;
                            }
                        }
                    }
                }
            }
            float toMovePerCm = 0.0f;
            if (lowerCount > 0 && lowDiffs > 0.0f) {
                toMovePerCm = amountToFlow / lowDiffs;
                for (int xx3 = -1; xx3 <= 1; ++xx3) {
                    for (int yy3 = -1; yy3 <= 1; ++yy3) {
                        if (xx3 != 0 || yy3 != 0) {
                            final float cht2 = getHeightCm(x + xx3, y + yy3) + getWaterLevel(x + xx3, y + yy3) + moveArr[xx3 + 1][yy3 + 1];
                            final float diff2 = wht - cht2;
                            if (ok && diff2 > 0.0f) {
                                final float toMove = toMovePerCm * diff2;
                                moveArr[xx3 + 1][yy3 + 1] += toMove;
                                moveArr[1][1] -= toMove;
                                incAddedWater(x + xx3, y + yy3, toMove);
                                incAddedWater(x, y, -toMove);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private final void addWater(final int x, final int y, final float rain) {
        if (rain > 0.0f) {
            if (rain > this.nowRain) {
                this.nowRain = rain;
            }
            incAddedWater(x, y, rain);
        }
        final WaterGenerator sp = this.springs.get(Tiles.getTileId(x, y, 0, true));
        if (sp != null) {
            incAddedWater(x, y, sp.getHeight() + this.waterRand.nextInt(25));
        }
    }
    
    private float getMaxWaterLeakage(final byte code) {
        switch (code) {
            case 0: {
                return 0.1f;
            }
            case 1: {
                return 0.2f;
            }
            case 2: {
                return 0.5f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    private float getMaxWaterInfiltration(final byte code) {
        switch (code) {
            case 0: {
                return 0.0f;
            }
            case 1: {
                return 0.25f;
            }
            case 2: {
                return 0.5f;
            }
            case 3: {
                return 1.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    private float getMaxWaterReservoir(final byte code) {
        switch (code) {
            case 0: {
                return 3.0f;
            }
            case 1: {
                return 7.0f;
            }
            case 2: {
                return 15.0f;
            }
            default: {
                return 0.0f;
            }
        }
    }
    
    private void doEvaporation(final int x, final int y, final float evaporation) {
        final float newWaterLevel = Math.max(0.0f, getWaterLevel(x, y) + getAddedWater(x, y));
        final int encodedTile = Server.surfaceMesh.getTile(x, y);
        final byte type = Tiles.decodeType(encodedTile);
        final Tiles.Tile tile = Tiles.getTile(type);
        final float maxWaterLeakage = this.getMaxWaterLeakage(tile.getWaterLeakageCode());
        final float maxWaterInfiltration = this.getMaxWaterInfiltration(tile.getWaterInfiltrationCode());
        final float maxWaterReservoir = this.getMaxWaterReservoir(tile.getWaterReservoirCode());
        float currentReservoir = getReservoir(x, y);
        if (maxWaterLeakage > 0.0f && currentReservoir > 0.0f) {
            currentReservoir = Math.max(0.0f, currentReservoir - maxWaterLeakage);
        }
        final float tempNewWaterLevel = getWaterLevel(x, y) + getAddedWater(x, y);
        if (maxWaterInfiltration > 0.0f && tempNewWaterLevel > 0.0f) {
            final float room = maxWaterReservoir - currentReservoir;
            final float toMove = Math.min(Math.min(room, maxWaterInfiltration), tempNewWaterLevel);
            currentReservoir += toMove;
            incAddedWater(x, y, -toMove);
        }
        setReservoir(x, y, currentReservoir);
        setWaterLevel(x, y, newWaterLevel);
    }
    
    public final void addWater(final WaterGenerator addedChange) {
        if (addedChange.changed()) {
            this.changedTileCorners.put(addedChange.getTileId(), addedChange);
        }
    }
    
    public final void propagateChanges() {
        for (final WaterGenerator generator : this.changedTileCorners.values()) {
            if (generator.changedSinceReset()) {
                for (final Player player : Players.getInstance().getPlayers()) {
                    if (player.isWithinTileDistanceTo(generator.x, generator.y, 0, 20)) {
                        player.getCommunicator().sendWater(generator.x, generator.y, generator.layer, generator.getHeight());
                    }
                }
            }
            generator.setReset(true);
        }
        this.changedTileCorners.clear();
    }
    
    private final void tickGenerators() {
        for (final WaterGenerator spring : this.springs.values()) {
            final int oldSurfaceVal = getSurfaceWater(spring.x, spring.y);
            if (oldSurfaceVal < 200) {
                this.waterRand.setSeed(spring.x + spring.y);
                setSurfaceWater(spring.x, spring.y, oldSurfaceVal + 1 + this.waterRand.nextInt(3));
                spring.setHeight(getSurfaceWater(spring.x, spring.y));
                this.addWater(spring);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Water.class.getName());
        heightsArr = new int[1 << Constants.meshSize][1 << Constants.meshSize];
        addedWaterArr = new float[1 << Constants.meshSize][1 << Constants.meshSize];
        currentWaterArr = new float[1 << Constants.meshSize][1 << Constants.meshSize];
        reservoirArr = new float[1 << Constants.meshSize][1 << Constants.meshSize];
        Water.phase = 0;
        SPRINGRAND = Servers.localServer.id + 127312634L;
    }
}

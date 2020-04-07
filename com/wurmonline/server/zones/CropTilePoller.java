// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Players;
import java.util.List;
import java.util.ArrayList;
import com.wurmonline.server.Servers;
import java.util.Iterator;
import java.util.HashMap;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import com.wurmonline.mesh.Tiles;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.TimeConstants;

public class CropTilePoller implements TimeConstants, SoundNames
{
    private static final Logger logger;
    public static boolean logTilePolling;
    private static volatile long lastPolledTiles;
    private static final Map<Long, CropTile> tiles;
    private static boolean isInitialized;
    
    private static Long cropTileIndexFor(final CropTile cropTile) {
        return Tiles.getTileId(cropTile.getX(), cropTile.getY(), 0, cropTile.isOnSurface());
    }
    
    public static void addCropTile(final int tileData, final int x, final int y, final int cropType, final boolean surface) {
        synchronized (CropTilePoller.tiles) {
            final CropTile cTile = new CropTile(tileData, x, y, cropType, surface);
            CropTilePoller.tiles.put(cropTileIndexFor(cTile), cTile);
        }
    }
    
    public static void initializeFields() {
        CropTilePoller.logger.log(Level.INFO, "CROPS_POLLER: Collecting tile data.");
        addAllFieldsInMesh(Server.surfaceMesh, true);
        addAllFieldsInMesh(Server.caveMesh, false);
        logCropFields();
        CropTilePoller.isInitialized = true;
        CropTilePoller.logger.log(Level.INFO, "CROPS_POLLER: Collecting tile Finished.");
    }
    
    private static void addAllFieldsInMesh(final MeshIO mesh, final boolean surface) {
        final int mapSize = mesh.getSize();
        final Tiles.Tile tileToLookFor = Tiles.Tile.TILE_FIELD;
        final Tiles.Tile tile2ToLookFor = Tiles.Tile.TILE_FIELD2;
        for (int x = 0; x < mapSize; ++x) {
            for (int y = 0; y < mapSize; ++y) {
                final int tileId = mesh.getTile(x, y);
                final byte type = Tiles.decodeType(tileId);
                final Tiles.Tile tileEnum = Tiles.getTile(type);
                if (tileEnum != null && (tileEnum.id == tileToLookFor.id || tileEnum.id == tile2ToLookFor.id)) {
                    final byte data = Tiles.decodeData(tileId);
                    final int crop = Crops.getCropNumber(type, data);
                    final CropTile cropTile = new CropTile(tileId, x, y, crop, surface);
                    CropTilePoller.tiles.put(cropTileIndexFor(cropTile), cropTile);
                }
            }
        }
    }
    
    private static void logCropFields() {
        final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (final CropTile tile : CropTilePoller.tiles.values()) {
            final Integer crop = tile.getCropType();
            Integer count = map.get(crop);
            if (count == null) {
                map.put(crop, 1);
            }
            else {
                ++count;
                map.put(crop, count);
            }
        }
        String text = "\n";
        final Iterator<Integer> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            final Integer crop = iterator.next();
            final Integer count = map.get(crop);
            final String cropName = Crops.getCropName(crop);
            text = text + cropName + " fields: " + count.toString() + "\n";
        }
        CropTilePoller.logger.log(Level.INFO, text);
    }
    
    public static void pollCropTiles() {
        if (!CropTilePoller.isInitialized) {
            return;
        }
        final long now = System.currentTimeMillis();
        final long fieldGrowthTime = Servers.localServer.getFieldGrowthTime();
        final long elapsedSinceLastPoll = now - CropTilePoller.lastPolledTiles;
        final boolean timeToPoll = elapsedSinceLastPoll >= fieldGrowthTime;
        if (!timeToPoll) {
            return;
        }
        final List<CropTile> toRemove = new ArrayList<CropTile>();
        synchronized (CropTilePoller.tiles) {
            if (now - CropTilePoller.lastPolledTiles < fieldGrowthTime) {
                return;
            }
            CropTilePoller.lastPolledTiles = System.currentTimeMillis();
            for (final CropTile cTile : CropTilePoller.tiles.values()) {
                MeshIO meshToUse = Server.surfaceMesh;
                if (!cTile.isOnSurface()) {
                    meshToUse = Server.caveMesh;
                }
                final int currTileId = meshToUse.getTile(cTile.getX(), cTile.getY());
                final byte type = Tiles.decodeType(currTileId);
                final Tiles.Tile tileEnum = Tiles.getTile(type);
                if (tileEnum == null || (tileEnum.id != Tiles.Tile.TILE_FIELD.id && tileEnum.id != Tiles.Tile.TILE_FIELD2.id)) {
                    toRemove.add(cTile);
                }
                else {
                    final byte data = Tiles.decodeData(currTileId);
                    checkForFarmGrowth(currTileId, cTile.getX(), cTile.getY(), type, data, meshToUse, cTile.isOnSurface());
                }
            }
            for (final CropTile t : toRemove) {
                CropTilePoller.tiles.remove(cropTileIndexFor(t));
            }
        }
        CropTilePoller.logger.fine("Completed poll of crop tiles.");
    }
    
    public static void checkForFarmGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData, final MeshIO currentMesh, final boolean pollingSurface) {
        if (Zones.protectedTiles[tilex][tiley]) {
            return;
        }
        int tileAge = Crops.decodeFieldAge(aData);
        final int crop = Crops.getCropNumber(type, aData);
        final boolean farmed = Crops.decodeFieldState(aData);
        if (CropTilePoller.logTilePolling) {
            CropTilePoller.logger.log(Level.INFO, "Polling farm at " + tilex + "," + tiley + ", age=" + tileAge + ", crop=" + crop + ", farmed=" + farmed);
        }
        if (tileAge == 7) {
            if (Server.rand.nextInt(100) <= 10) {
                currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
                Players.getInstance().sendChangedTile(tilex, tiley, pollingSurface, false);
                if (CropTilePoller.logTilePolling) {
                    CropTilePoller.logger.log(Level.INFO, "Set to dirt");
                }
            }
            if (WurmCalendar.isNight()) {
                SoundPlayer.playSound("sound.birdsong.bird1", tilex, tiley, pollingSurface, 2.0f);
            }
            else {
                SoundPlayer.playSound("sound.birdsong.bird3", tilex, tiley, pollingSurface, 2.0f);
            }
        }
        else if (tileAge < 7) {
            if ((tileAge == 5 || tileAge == 6) && Server.rand.nextInt(3) < 2) {
                return;
            }
            ++tileAge;
            final VolaTile tempvtile1 = Zones.getOrCreateTile(tilex, tiley, pollingSurface);
            if (tempvtile1.getStructure() != null) {
                currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
            }
            else {
                currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), type, Crops.encodeFieldData(false, tileAge, crop)));
                Server.modifyFlagsByTileType(tilex, tiley, type);
                if (CropTilePoller.logTilePolling) {
                    CropTilePoller.logger.log(Level.INFO, "Changed the tile");
                }
            }
            if (WurmCalendar.isNight()) {
                SoundPlayer.playSound("sound.ambient.night.crickets", tilex, tiley, pollingSurface, 0.0f);
            }
            else {
                SoundPlayer.playSound("sound.birdsong.bird2", tilex, tiley, pollingSurface, 1.0f);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, pollingSurface, false);
        }
        else {
            CropTilePoller.logger.log(Level.WARNING, "Strange, tile " + tilex + ", " + tiley + " is field but has age above 7:" + tileAge + " crop is " + crop + " farmed is " + farmed);
        }
    }
    
    static {
        logger = Logger.getLogger(CropTilePoller.class.getName());
        CropTilePoller.logTilePolling = false;
        CropTilePoller.lastPolledTiles = 0L;
        tiles = new HashMap<Long, CropTile>();
        CropTilePoller.isInitialized = false;
        CropTilePoller.lastPolledTiles = System.currentTimeMillis();
    }
}

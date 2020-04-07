// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import javax.annotation.Nullable;
import com.wurmonline.mesh.FieldData;
import com.wurmonline.server.Point4f;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.mesh.BushData;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.behaviours.Crops;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Features;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.TimeConstants;

public final class TilePoller implements TimeConstants, SoundNames, MiscConstants
{
    public static boolean logTilePolling;
    private static int MINIMUM_REED_HEIGHT;
    private static int MINIMUM_KELP_HEIGHT;
    private static int MAX_KELP_HEIGHT;
    public static MeshIO currentMesh;
    public static int currentPollTile;
    public static int pollModifier;
    private static final Logger logger;
    public static boolean pollingSurface;
    private static int nthTick;
    private static int currTick;
    public static int rest;
    private static boolean manyPerTick;
    private static final long FORAGE_PRIME = 101531L;
    private static final long IRON_PRIME = 103591L;
    private static final long HERB_PRIME = 102563L;
    private static final long INVESTIGATE_PRIME = 104149L;
    private static final long SEARCH_PRIME = 103025L;
    private static int forageChance;
    private static int herbChance;
    private static int investigateChance;
    private static int searchChance;
    private static final Random r;
    private static final long HIVE_PRIME;
    private static final int hiveFactor = 500;
    public static int pollround;
    private static boolean pollEruption;
    private static boolean createFlowers;
    public static int treeGrowth;
    private static int flowerCounter;
    public static boolean entryServer;
    private static final Random kelpRandom;
    public static int mask;
    public static int sentTilePollMessages;
    
    public static void calcRest() {
        final int ticksPerPeriod = 3456000;
        final int tiles = (1 << Constants.meshSize) * (1 << Constants.meshSize);
        TilePoller.logger.log(Level.INFO, "Current polltile=" + TilePoller.currentPollTile + ", rest=" + TilePoller.rest + " pollmodifier=" + TilePoller.pollModifier + ", pollround=" + TilePoller.pollround);
        if (3456000 >= tiles) {
            TilePoller.nthTick = 3456000 / tiles;
            if (TilePoller.currentPollTile == 0) {
                TilePoller.rest = 3456000 % tiles;
            }
        }
        else {
            TilePoller.nthTick = tiles / 3456000;
            if (TilePoller.currentPollTile == 0) {
                TilePoller.rest = tiles % 3456000;
            }
            TilePoller.manyPerTick = true;
        }
        TilePoller.logger.log(Level.INFO, "tiles=" + tiles + ", mask=" + TilePoller.mask + " ticksperday=" + 3456000 + ", Nthick=" + TilePoller.nthTick + ", rest=" + TilePoller.rest + ", manypertick=" + TilePoller.manyPerTick);
    }
    
    public static void pollNext() {
        if (Constants.isGameServer) {
            if (TilePoller.manyPerTick) {
                for (int x = 0; x < TilePoller.nthTick; ++x) {
                    TilePoller.pollingSurface = true;
                    pollNextTile();
                    TilePoller.pollingSurface = false;
                    pollNextTile();
                    checkPolltile();
                }
                if (TilePoller.rest > 0) {
                    for (int x = 0; x < Math.min(TilePoller.rest, TilePoller.nthTick); ++x) {
                        TilePoller.pollingSurface = true;
                        pollNextTile();
                        TilePoller.pollingSurface = false;
                        pollNextTile();
                        checkPolltile();
                        --TilePoller.rest;
                        if (TilePoller.rest == 0) {
                            TilePoller.logger.log(Level.INFO, "...and THERE all rest-tiles are gone.");
                        }
                    }
                }
            }
            else {
                ++TilePoller.currTick;
                if (TilePoller.currTick == TilePoller.nthTick) {
                    TilePoller.pollingSurface = true;
                    pollNextTile();
                    TilePoller.pollingSurface = false;
                    pollNextTile();
                    checkPolltile();
                    TilePoller.currTick = 0;
                    if (TilePoller.rest > 0) {
                        for (int x = 0; x < Math.min(TilePoller.rest, TilePoller.nthTick); ++x) {
                            TilePoller.pollingSurface = true;
                            pollNextTile();
                            TilePoller.pollingSurface = false;
                            pollNextTile();
                            checkPolltile();
                            --TilePoller.rest;
                            if (TilePoller.rest == 0) {
                                TilePoller.logger.log(Level.INFO, "...and THERE all rest-tiles are gone.");
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void pollNextTile() {
        if (TilePoller.pollingSurface) {
            TilePoller.currentMesh = Server.surfaceMesh;
        }
        else {
            TilePoller.currentMesh = Server.caveMesh;
        }
        if (TilePoller.pollingSurface) {
            try {
                final int temptile1 = TilePoller.currentMesh.data[TilePoller.currentPollTile];
                final int temptilex = TilePoller.currentPollTile & (1 << Constants.meshSize) - 1;
                final int temptiley = TilePoller.currentPollTile >> Constants.meshSize;
                final byte tempint1 = Tiles.decodeType(temptile1);
                final byte temptile2 = Tiles.decodeData(temptile1);
                checkEffects(temptile1, temptilex, temptiley, tempint1, temptile2);
                final VolaTile tempvtile1 = Zones.getTileOrNull(temptilex, temptiley, TilePoller.pollingSurface);
                if (tempvtile1 != null) {
                    tempvtile1.pollStructures(System.currentTimeMillis());
                }
            }
            catch (Exception e) {
                TilePoller.logger.severe("Indexoutofbounds: data array size: " + TilePoller.currentMesh.getData().length + " polltile: " + TilePoller.currentPollTile);
            }
        }
        else {
            final int temptile3 = TilePoller.currentMesh.data[TilePoller.currentPollTile];
            final byte tempbyte1 = Tiles.decodeType(temptile3);
            if (tempbyte1 == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(tempbyte1) || tempbyte1 == Tiles.Tile.TILE_CAVE_EXIT.id) {
                final int temptilex = TilePoller.currentPollTile & (1 << Constants.meshSize) - 1;
                final int temptiley = TilePoller.currentPollTile >> Constants.meshSize;
                final int data = Tiles.decodeData(temptile3) & 0xFF;
                checkCaveDecay(temptile3, temptilex, temptiley, tempbyte1, data);
                final VolaTile tempvtile2 = Zones.getTileOrNull(temptilex, temptiley, TilePoller.pollingSurface);
                if (tempvtile2 != null) {
                    tempvtile2.pollStructures(System.currentTimeMillis());
                }
            }
            else if (tempbyte1 == Tiles.Tile.TILE_CAVE_WALL.id) {
                final int temptilex = TilePoller.currentPollTile & (1 << Constants.meshSize) - 1;
                final int temptiley = TilePoller.currentPollTile >> Constants.meshSize;
                final byte state = Zones.getMiningState(temptilex, temptiley);
                if (state == 0) {
                    TilePoller.r.setSeed((temptilex + temptiley * Zones.worldTileSizeY) * 102533L);
                    if (TilePoller.r.nextInt(100) == 0) {
                        Server.caveMesh.setTile(temptilex, temptiley, Tiles.encode(Tiles.decodeHeight(temptile3), Tiles.Tile.TILE_CAVE_WALL_ROCKSALT.id, Tiles.decodeData(temptile3)));
                        Players.getInstance().sendChangedTile(temptilex, temptiley, false, true);
                    }
                    TilePoller.r.setSeed((temptilex + temptiley * Zones.worldTileSizeY) * 123307L);
                    if (TilePoller.r.nextInt(64) == 0) {
                        Server.caveMesh.setTile(temptilex, temptiley, Tiles.encode(Tiles.decodeHeight(temptile3), Tiles.Tile.TILE_CAVE_WALL_SANDSTONE.id, Tiles.decodeData(temptile3)));
                        Players.getInstance().sendChangedTile(temptilex, temptiley, false, true);
                    }
                }
            }
        }
    }
    
    private static void checkCaveDecay(final int tile, final int tilex, final int tiley, final byte type, final int _data) {
        if (Zones.protectedTiles[tilex][tiley]) {
            return;
        }
        final Village village = Villages.getVillage(tilex, tiley, true);
        if (village != null && village.isPermanent) {
            return;
        }
        final HighwayPos highwayPos = MethodsHighways.getHighwayPos(tilex, tiley, false);
        if (highwayPos != null && MethodsHighways.onHighway(highwayPos)) {
            return;
        }
        boolean decay = false;
        if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_EXIT.id) {
            if (Server.rand.nextInt(60) == 0 && !Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)))) {
                decay = true;
                if (Tiles.decodeType(TilePoller.currentMesh.getTile(tilex, tiley - 1)) != Tiles.Tile.TILE_CAVE_WALL.id && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex, tiley - 1)) != Tiles.Tile.TILE_CAVE.id) {
                    decay = false;
                }
                if (decay && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex, tiley + 1)) != Tiles.Tile.TILE_CAVE_WALL.id && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex, tiley + 1)) != Tiles.Tile.TILE_CAVE.id) {
                    decay = false;
                }
                if (decay && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + 1, tiley)) != Tiles.Tile.TILE_CAVE_WALL.id && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + 1, tiley)) != Tiles.Tile.TILE_CAVE.id) {
                    decay = false;
                }
                if (decay && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex - 1, tiley)) != Tiles.Tile.TILE_CAVE_WALL.id && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex - 1, tiley)) != Tiles.Tile.TILE_CAVE.id) {
                    decay = false;
                }
            }
        }
        else {
            final int tempint1 = Server.rand.nextInt(4);
            int tx = 0;
            int ty = -1;
            if (tempint1 == 1) {
                tx = -1;
                ty = 0;
            }
            else if (tempint1 == 2) {
                tx = 1;
                ty = 0;
            }
            else if (tempint1 == 3) {
                tx = 0;
                ty = 1;
            }
            final int temptile1 = TilePoller.currentMesh.getTile(tilex + tx, tiley + ty);
            if (Tiles.decodeType(temptile1) == Tiles.Tile.TILE_CAVE_WALL.id && Server.rand.nextFloat() <= 0.002f) {
                decay = true;
            }
            if (decay && Tiles.isReinforcedFloor(Tiles.decodeType(tile)) && Server.rand.nextInt(10) < 8) {
                decay = false;
            }
        }
        if (decay) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    final VolaTile tempvtile2 = Zones.getTileOrNull(tilex + x, tiley + y, false);
                    if (tempvtile2 != null && tempvtile2.getStructure() != null && tempvtile2.getStructure().isFinished()) {
                        return;
                    }
                }
            }
            final VolaTile tempvtile3 = Zones.getTileOrNull(tilex, tiley, false);
            if (tempvtile3 != null) {
                if (tempvtile3.getCreatures().length > 0) {
                    return;
                }
                tempvtile3.destroyEverything();
            }
            final byte state = Zones.getMiningState(tilex, tiley);
            if (type == Tiles.Tile.TILE_CAVE_EXIT.id) {
                Terraforming.setAsRock(tilex, tiley, true);
                if (state != 0) {
                    Zones.setMiningState(tilex, tiley, (byte)0, false);
                    Zones.deleteMiningTile(tilex, tiley);
                }
                if (TilePoller.logger.isLoggable(Level.FINER)) {
                    TilePoller.logger.finer("Caved in EXIT at " + tilex + ", " + tiley);
                }
            }
            else {
                Terraforming.caveIn(tilex, tiley);
                if (state != 0) {
                    Zones.setMiningState(tilex, tiley, (byte)0, false);
                    Zones.deleteMiningTile(tilex, tiley);
                }
                if (TilePoller.logger.isLoggable(Level.FINER)) {
                    TilePoller.logger.finer("Caved in " + tilex + ", " + tiley);
                }
            }
        }
    }
    
    public static void checkEffects(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (TilePoller.logTilePolling && TilePoller.sentTilePollMessages < 10) {
            TilePoller.logger.log(Level.INFO, "Tile Polling is working for " + tilex + "," + tiley);
            ++TilePoller.sentTilePollMessages;
        }
        final Tiles.Tile theTile = Tiles.getTile(type);
        if (!WurmCalendar.isSeasonWinter()) {
            Server.setGatherable(tilex, tiley, false);
            final HiveZone hz = Zones.getHiveZoneAt(tilex, tiley, true);
            if (hz != null) {
                checkHoneyProduction(hz, tilex, tiley, type, aData, theTile);
            }
        }
        else {
            if (!containsStructure(tilex, tiley)) {
                Server.setGatherable(tilex, tiley, true);
            }
            final Item[] hives = Zones.getActiveDomesticHives(tilex, tiley);
            if (hives != null) {
                for (final Item hive : hives) {
                    if (hive.hasTwoQueens() && hive.removeQueen() && Servers.isThisATestServer()) {
                        Players.getInstance().sendGmMessage(null, "System", "Debug: Removed queen @ " + tilex + "," + tiley + " Two:" + hive.hasTwoQueens(), false);
                    }
                    if (Server.rand.nextInt(10) == 0) {
                        final Item sugar = hive.getSugar();
                        if (sugar != null) {
                            Items.destroyItem(sugar.getWurmId());
                        }
                        else {
                            final Item honey = hive.getHoney();
                            if (honey != null) {
                                if (honey.getWeightGrams() < 1000 && Server.rand.nextInt(10) == 0 && hive.removeQueen() && Servers.isThisATestServer()) {
                                    Players.getInstance().sendGmMessage(null, "System", "Debug: Removed queen @ " + tilex + "," + tiley + " Two:" + hive.hasTwoQueens(), false);
                                }
                                honey.setWeight(Math.max(0, honey.getWeightGrams() - 10), true);
                            }
                            else if (Server.rand.nextInt(3) == 0 && hive.removeQueen() && Servers.isThisATestServer()) {
                                Players.getInstance().sendGmMessage(null, "System", "Debug: Removed queen @ " + tilex + "," + tiley + " No Honey!", false);
                            }
                        }
                    }
                }
            }
        }
        if (Zones.protectedTiles[tilex][tiley]) {
            return;
        }
        if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_KELP.id || type == Tiles.Tile.TILE_REED.id) {
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else if (Tiles.decodeHeight(tile) > 0) {
                checkForSeedGrowth(tile, tilex, tiley);
                checkForGrassGrowth(tile, tilex, tiley, type, aData, true);
            }
            else {
                checkForGrassGrowth(tile, tilex, tiley, type, aData, true);
            }
        }
        else if (type == Tiles.Tile.TILE_DIRT.id) {
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else if (Server.rand.nextInt(5) < 1) {
                checkForGrassSpread(tile, tilex, tiley, type, aData);
            }
            else if (Server.rand.nextInt(3) < 1) {
                checkGrubGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_SAND.id) {
            if (Server.rand.nextInt(10) < 1) {
                checkGrubGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_DIRT_PACKED.id) {
            if (Server.rand.nextInt(20) == 1) {
                checkForGrassSpread(tile, tilex, tiley, type, aData);
            }
        }
        else if (type == Tiles.Tile.TILE_LAWN.id) {
            if (Server.rand.nextInt(50) == 1) {
                final Village v = Villages.getVillage(tilex, tiley, true);
                if (v == null && Server.rand.nextInt(100) > 75) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_GRASS.id);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
            }
            else if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
        }
        else if (type == Tiles.Tile.TILE_STEPPE.id) {
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else if (Tiles.decodeHeight(tile) > 0) {
                checkForSeedGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_MOSS.id) {
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else if (Server.rand.nextInt(3) == 1 && Tiles.decodeHeight(tile) > 0) {
                checkForSeedGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_PEAT.id) {
            if (Server.rand.nextInt(5) == 1 && Tiles.decodeHeight(tile) > 0) {
                checkForSeedGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_TUNDRA.id) {
            if (Tiles.decodeHeight(tile) > 0) {
                if (Server.rand.nextInt(7) == 1) {
                    checkForSeedGrowth(tile, tilex, tiley);
                }
                if (Server.rand.nextInt(100) == 1) {
                    checkForLingonberryStart(tile, tilex, tiley);
                }
            }
        }
        else if (type == Tiles.Tile.TILE_MARSH.id) {
            if (Server.rand.nextInt(9) == 1 && Tiles.decodeHeight(tile) > -20) {
                checkForSeedGrowth(tile, tilex, tiley);
            }
        }
        else if (theTile.isNormalTree() || theTile.isEnchantedTree()) {
            checkForTreeGrowth(tile, tilex, tiley, type, aData);
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else {
                checkForSeedGrowth(tile, tilex, tiley);
                checkForTreeGrassGrowth(tile, tilex, tiley, type, aData);
            }
            if (Server.isCheckHive(tilex, tiley) && WurmCalendar.isSeasonAutumn() && Server.rand.nextInt(30) == 0) {
                final Item wildHive = Zones.getWildHive(tilex, tiley);
                if (wildHive != null) {
                    final Item honey2 = wildHive.getHoney();
                    if (honey2 == null) {
                        Server.setCheckHive(tilex, tiley, false);
                        if (Zones.removeWildHive(tilex, tiley) && Servers.isThisATestServer()) {
                            Players.getInstance().sendGmMessage(null, "System", "Debug: Removed wild hive due to no food in autumn @ " + tilex + "," + tiley, false);
                        }
                    }
                }
            }
            if (Server.isCheckHive(tilex, tiley) && WurmCalendar.isWinter()) {
                Server.setCheckHive(tilex, tiley, false);
                if (Zones.removeWildHive(tilex, tiley) && Servers.isThisATestServer()) {
                    Players.getInstance().sendGmMessage(null, "System", "Debug: Removed wild hive due to winter @ " + tilex + "," + tiley, false);
                }
            }
            if (!Server.isCheckHive(tilex, tiley) && WurmCalendar.isSpring() && Server.rand.nextInt(5) == 0) {
                Server.setCheckHive(tilex, tiley, true);
                addWildBeeHives(tilex, tiley, theTile, aData);
            }
            if (!Server.isCheckHive(tilex, tiley) && WurmCalendar.isSummer() && Server.rand.nextInt(15) == 0) {
                Server.setCheckHive(tilex, tiley, true);
                addWildBeeHives(tilex, tiley, theTile, aData);
            }
        }
        else if (theTile.isNormalBush() || theTile.isEnchantedBush()) {
            checkForTreeGrowth(tile, tilex, tiley, type, aData);
            if (Server.rand.nextInt(10) < 1) {
                checkForMycelGrowth(tile, tilex, tiley, type, aData);
            }
            else {
                checkForSeedGrowth(tile, tilex, tiley);
                checkForTreeGrassGrowth(tile, tilex, tiley, type, aData);
            }
        }
        else if (type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
            if (!Features.Feature.CROP_POLLER.isEnabled() && !Zones.protectedTiles[tilex][tiley]) {
                checkForFarmGrowth(tile, tilex, tiley, type, aData);
            }
        }
        else if (type == Tiles.Tile.TILE_MYCELIUM.id) {
            if (!Servers.isThisAPvpServer()) {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, aData);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            }
            else if (Server.rand.nextInt(3) < 1) {
                checkForGrassSpread(tile, tilex, tiley, type, aData);
                checkForSeedGrowth(tile, tilex, tiley);
            }
            else {
                checkForGrassGrowth(tile, tilex, tiley, type, aData, false);
                checkGrubGrowth(tile, tilex, tiley);
            }
        }
        else if (type == Tiles.Tile.TILE_MYCELIUM_LAWN.id) {
            if (!Servers.isThisAPvpServer()) {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_LAWN.id, aData);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            }
            else if (Server.rand.nextInt(25) == 1) {
                final Village v = Villages.getVillage(tilex, tiley, TilePoller.pollingSurface);
                if (v == null && Server.rand.nextInt(100) > 75) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_MYCELIUM.id, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_MYCELIUM.id);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
            }
            else if (Server.rand.nextInt(10) < 1) {
                checkForGrassSpread(tile, tilex, tiley, type, aData);
            }
        }
        else if (theTile.isMyceliumTree() || theTile.isMyceliumBush()) {
            if (!Servers.isThisAPvpServer()) {
                final byte newType = (byte)Tiles.toNormal(type);
                TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType, aData));
                Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
            }
            else {
                checkForTreeGrowth(tile, tilex, tiley, type, aData);
                if (Server.rand.nextInt(3) < 1) {
                    checkForGrassSpread(tile, tilex, tiley, type, aData);
                }
                else {
                    checkForGrassGrowth(tile, tilex, tiley, type, aData, false);
                    checkForSeedGrowth(tile, tilex, tiley);
                }
            }
        }
        else if (type == Tiles.Tile.TILE_LAVA.id) {
            checkForLavaFlow(tile, tilex, tiley, type, aData);
        }
        else if (type == Tiles.Tile.TILE_PLANKS.id) {
            if (!Zones.walkedTiles[tilex][tiley]) {
                if (aData == 0 || Server.rand.nextInt(10) == 0) {
                    checkForDecayToDirt(tile, tilex, tiley, type, aData);
                }
            }
            else {
                Zones.walkedTiles[tilex][tiley] = false;
            }
        }
        else if (TilePoller.pollingSurface && (type == Tiles.Tile.TILE_ROCK.id || type == Tiles.Tile.TILE_CLIFF.id)) {
            if (TilePoller.pollEruption) {
                checkForEruption(tile, tilex, tiley, type, aData);
            }
            else {
                checkCreateIronRock(tilex, tiley);
            }
        }
        else if (Tiles.isMineDoor(type)) {
            decayMineDoor(tile, tilex, tiley);
        }
        if (Tiles.isRoadType(type) || Tiles.isEnchanted(type) || type == Tiles.Tile.TILE_DIRT_PACKED.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id) {
            checkInvestigateGrowth(tile, tilex, tiley);
        }
    }
    
    private static void decayMineDoor(final int tile, final int tilex, final int tiley) {
        if (TilePoller.pollingSurface && Server.rand.nextInt(3) == 0) {
            final Village v = Villages.getVillage(tilex, tiley, true);
            if (v == null || v.lessThanWeekLeft()) {
                int currQl = Server.getWorldResource(tilex, tiley);
                currQl = Math.max(0, currQl - 100);
                Server.setWorldResource(tilex, tiley, currQl);
                if (currQl == 0) {
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_HOLE.id, (byte)0);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, true);
                    final MineDoorPermission perm = MineDoorPermission.getPermission(tilex, tiley);
                    if (perm != null) {
                        MineDoorPermission.removePermission(perm);
                    }
                    MineDoorPermission.deleteMineDoor(tilex, tiley);
                    Server.getInstance().broadCastMessage("A mine door crumbles and falls down with a crash.", tilex, tiley, true, 5);
                }
            }
        }
    }
    
    private static void checkPolltile() {
        if (TilePoller.currentPollTile == 0) {
            calcRest();
            WurmCalendar.checkSpring();
            TilePoller.createFlowers = (Server.rand.nextInt(5) == 0);
            ++TilePoller.pollround;
            TilePoller.pollModifier = (Server.rand.nextInt(Math.min(30000, TilePoller.currentMesh.data.length)) + 1) * 2 - 1;
            TilePoller.logger.log(Level.INFO, "New pollModifier: " + TilePoller.pollModifier + " eruptions=" + TilePoller.pollEruption);
        }
        TilePoller.currentPollTile = (TilePoller.currentPollTile + TilePoller.pollModifier & TilePoller.mask);
    }
    
    static boolean checkForGrassGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData, final boolean andFlowers) {
        GrassData.GrowthStage growthStage = GrassData.GrowthStage.decodeTileData(aData);
        GrassData.FlowerType flowerType = GrassData.FlowerType.decodeTileData(aData);
        boolean dataHasChanged = false;
        final short height = Tiles.decodeHeight(tile);
        final int seed = 100;
        int growthRate;
        if (WurmCalendar.isWinter()) {
            growthRate = 15;
        }
        else if (WurmCalendar.isSummer()) {
            growthRate = 40;
        }
        else if (WurmCalendar.isAutumn()) {
            growthRate = 20;
        }
        else {
            growthRate = 30;
        }
        if (type == Tiles.Tile.TILE_MYCELIUM.id) {
            growthRate = 50 - growthRate;
        }
        final int rnd = Server.rand.nextInt(100);
        if (growthRate >= rnd && !growthStage.isMax()) {
            growthStage = growthStage.getNextStage();
            dataHasChanged = true;
        }
        if (andFlowers && TilePoller.createFlowers) {
            final GrassData.FlowerType newFlowerType = Terraforming.getRandomFlower(flowerType, false);
            if (flowerType != newFlowerType) {
                flowerType = newFlowerType;
                dataHasChanged = true;
            }
        }
        if (dataHasChanged) {
            if (TilePoller.logger.isLoggable(Level.FINER)) {
                TilePoller.logger.log(Level.FINER, "tile [" + tilex + "," + tiley + "] changed: " + height + " type=" + Tiles.getTile(type).getName() + " stage=" + growthStage.toString() + " flower=" + flowerType.toString().toLowerCase() + ".");
            }
            final byte tileData = GrassData.encodeGrassTileData(growthStage, flowerType);
            TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(height, type, tileData));
            Server.modifyFlagsByTileType(tilex, tiley, type);
            Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
            return true;
        }
        return false;
    }
    
    static boolean checkForTreeGrassGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        GrassData.GrowthTreeStage growthStage = GrassData.GrowthTreeStage.decodeTileData(aData);
        boolean dataHasChanged = false;
        final short height = Tiles.decodeHeight(tile);
        final int seed = 100;
        int growthRate;
        if (WurmCalendar.isWinter()) {
            growthRate = 5;
        }
        else if (WurmCalendar.isSummer()) {
            growthRate = 15;
        }
        else if (WurmCalendar.isAutumn()) {
            growthRate = 10;
        }
        else {
            growthRate = 20;
        }
        if (Tiles.getTile(type).isMycelium()) {
            growthRate = 25 - growthRate;
        }
        final int rnd = Server.rand.nextInt(100);
        if (growthRate >= rnd && !growthStage.isMax()) {
            if (growthStage == GrassData.GrowthTreeStage.LAWN) {
                final Village v = Villages.getVillage(tilex, tiley, true);
                if (v == null && Server.rand.nextInt(100) > 75) {
                    growthStage = GrassData.GrowthTreeStage.SHORT;
                    dataHasChanged = true;
                }
            }
            else {
                growthStage = growthStage.getNextStage();
                dataHasChanged = true;
            }
        }
        if (dataHasChanged) {
            if (TilePoller.logger.isLoggable(Level.FINER)) {
                TilePoller.logger.log(Level.FINER, "tile [" + tilex + "," + tiley + "] changed: " + height + " type=" + Tiles.getTile(type).getName() + " stage=" + growthStage.toString() + ".");
            }
            final FoliageAge tage = FoliageAge.getFoliageAge(aData);
            final boolean hasFruit = TreeData.hasFruit(aData);
            final boolean incentre = TreeData.isCentre(aData);
            final byte tileData = Tiles.encodeTreeData(tage, hasFruit, incentre, growthStage);
            TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(height, type, tileData));
            Server.modifyFlagsByTileType(tilex, tiley, type);
            Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
            return true;
        }
        return false;
    }
    
    static boolean checkForSeedGrowth(final int tile, final int tilex, final int tiley) {
        final byte type = Tiles.decodeType(tile);
        final Tiles.Tile theTile = Tiles.getTile(type);
        TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 102563L);
        final boolean canHaveHerb = TilePoller.r.nextInt(TilePoller.herbChance) == 0 && theTile.canBotanize();
        TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 101531L);
        final boolean canHaveForage = TilePoller.r.nextInt(TilePoller.forageChance) == 0 && theTile.canForage();
        checkInvestigateGrowth(tile, tilex, tiley);
        if (containsStructure(tilex, tiley)) {
            return false;
        }
        if (canHaveForage || canHaveHerb) {
            boolean containsForage = Server.isForagable(tilex, tiley);
            boolean containsHerb = Server.isBotanizable(tilex, tiley);
            boolean changed = false;
            if (canHaveForage && !containsForage) {
                changed = true;
                containsForage = true;
            }
            if (canHaveHerb && !containsHerb) {
                changed = true;
                containsHerb = true;
            }
            if (changed) {
                setGrassHasSeeds(tilex, tiley, containsForage, containsHerb);
                return true;
            }
        }
        return false;
    }
    
    static void checkInvestigateGrowth(final int tile, final int tilex, final int tiley) {
        TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 104149L);
        final boolean canHaveInvestigate = TilePoller.r.nextInt(TilePoller.investigateChance) == 0;
        if (containsStructure(tilex, tiley)) {
            return;
        }
        if (canHaveInvestigate && !Server.isInvestigatable(tilex, tiley)) {
            Server.setInvestigatable(tilex, tiley, true);
        }
    }
    
    static void checkGrubGrowth(final int tile, final int tilex, final int tiley) {
        TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 103025L);
        final boolean canHaveGrub = TilePoller.r.nextInt(TilePoller.searchChance) == 0;
        if (containsStructure(tilex, tiley)) {
            return;
        }
        if (canHaveGrub) {
            Server.setGrubs(tilex, tiley, true);
        }
    }
    
    public static void setGrassHasSeeds(final int tilex, final int tiley, final boolean forageable, final boolean botanizeable) {
        if (TilePoller.logger.isLoggable(Level.FINEST)) {
            if (forageable) {
                TilePoller.logger.finest(tilex + ", " + tiley + " setting forageable.");
            }
            if (botanizeable) {
                TilePoller.logger.finest(tilex + ", " + tiley + " setting botanizable.");
            }
        }
        Server.setForagable(tilex, tiley, forageable);
        Server.setBotanizable(tilex, tiley, botanizeable);
        Players.getInstance().sendChangedTile(tilex, tiley, true, false);
    }
    
    public static void checkForFarmGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        int tileAge = Crops.decodeFieldAge(aData);
        final int crop = Crops.getCropNumber(type, aData);
        final boolean farmed = Crops.decodeFieldState(aData);
        if (TilePoller.logTilePolling) {
            TilePoller.logger.log(Level.INFO, "Polling farm at " + tilex + "," + tiley + ", age=" + tileAge + ", crop=" + crop + ", farmed=" + farmed);
        }
        if (tileAge == 7) {
            if (Server.rand.nextInt(100) <= 10) {
                TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
                Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                if (TilePoller.logTilePolling) {
                    TilePoller.logger.log(Level.INFO, "Set to dirt");
                }
            }
            if (WurmCalendar.isNight()) {
                SoundPlayer.playSound("sound.birdsong.bird1", tilex, tiley, TilePoller.pollingSurface, 2.0f);
            }
            else {
                SoundPlayer.playSound("sound.birdsong.bird3", tilex, tiley, TilePoller.pollingSurface, 2.0f);
            }
        }
        else if (tileAge < 7) {
            if ((tileAge == 5 || tileAge == 6) && Server.rand.nextInt(tileAge) != 0) {
                return;
            }
            ++tileAge;
            final VolaTile tempvtile1 = Zones.getOrCreateTile(tilex, tiley, TilePoller.pollingSurface);
            if (tempvtile1.getStructure() != null) {
                TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
            }
            else {
                TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), type, Crops.encodeFieldData(false, tileAge, crop)));
                Server.modifyFlagsByTileType(tilex, tiley, type);
                if (TilePoller.logTilePolling) {
                    TilePoller.logger.log(Level.INFO, "Changed the tile");
                }
            }
            if (WurmCalendar.isNight()) {
                SoundPlayer.playSound("sound.ambient.night.crickets", tilex, tiley, TilePoller.pollingSurface, 0.0f);
            }
            else {
                SoundPlayer.playSound("sound.birdsong.bird2", tilex, tiley, TilePoller.pollingSurface, 1.0f);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
        }
        else {
            TilePoller.logger.log(Level.WARNING, "Strange, tile " + tilex + ", " + tiley + " is field but has age above 7:" + tileAge + " crop is " + crop + " farmed is " + farmed);
        }
    }
    
    private static boolean containsStructure(final int tilex, final int tiley) {
        try {
            final Zone zone = Zones.getZone(tilex, tiley, TilePoller.pollingSurface);
            final VolaTile tempvtile1 = zone.getTileOrNull(tilex, tiley);
            if (tempvtile1 != null) {
                if (containsHouse(tempvtile1)) {
                    return true;
                }
                if (containsFences(tempvtile1)) {
                    return true;
                }
            }
        }
        catch (NoSuchZoneException nsz) {
            TilePoller.logger.log(Level.WARNING, "Weird, no zone for " + tilex + ", " + tiley + " surfaced=" + TilePoller.pollingSurface, nsz);
        }
        return false;
    }
    
    private static boolean containsFences(final VolaTile vtile) {
        return vtile != null && vtile.getFences().length > 0;
    }
    
    private static boolean containsHouse(final VolaTile vtile) {
        return vtile != null && vtile.getStructure() != null;
    }
    
    private static boolean containsTracks(final int tilex, final int tiley) {
        try {
            final Zone zone = Zones.getZone(tilex, tiley, TilePoller.pollingSurface);
            final Track[] tracks = zone.getTracksFor(tilex, tiley);
            if (tracks.length > 0) {
                return true;
            }
        }
        catch (NoSuchZoneException nsz) {
            TilePoller.logger.log(Level.WARNING, "Weird, no zone for " + tilex + ", " + tiley + " surfaced=" + TilePoller.pollingSurface, nsz);
        }
        return false;
    }
    
    private static void checkForEruption(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (TilePoller.pollingSurface && TilePoller.pollEruption && Tiles.decodeHeight(tile) > 100) {
            for (int xx = -1; xx <= 1; ++xx) {
                for (int yy = -1; yy <= 1; ++yy) {
                    if (tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < 1 << Constants.meshSize && tiley + yy < 1 << Constants.meshSize) {
                        final int temptile1 = Server.surfaceMesh.getTile(tilex + xx, tiley + yy);
                        final byte tempbyte1 = Tiles.decodeType(temptile1);
                        if (tempbyte1 != Tiles.Tile.TILE_LAVA.id && tempbyte1 != Tiles.Tile.TILE_HOLE.id && !Tiles.isMineDoor(tempbyte1)) {
                            final int temptile2 = Server.caveMesh.getTile(tilex + xx, tiley + yy);
                            final byte tempbyte2 = Tiles.decodeType(temptile2);
                            if (Tiles.isSolidCave(tempbyte2)) {
                                int tempint1 = Tiles.decodeHeight(temptile1);
                                tempint1 += 4;
                                final int tempint2 = Tiles.encode((short)tempint1, Tiles.Tile.TILE_LAVA.id, (byte)0);
                                for (int xn = 0; xn <= 1; ++xn) {
                                    for (int yn = 0; yn <= 1; ++yn) {
                                        try {
                                            final int tempint3 = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + xx + xn, tiley + yy + yn));
                                            Server.rockMesh.setTile(tilex + xx + xn, tiley + yy + yn, Tiles.encode((short)tempint3, Tiles.Tile.TILE_ROCK.id, (byte)0));
                                        }
                                        catch (Exception ex) {}
                                    }
                                }
                                Server.surfaceMesh.setTile(tilex + xx, tiley + yy, tempint2);
                                Server.modifyFlagsByTileType(tilex + xx, tiley + yy, Tiles.Tile.TILE_LAVA.id);
                                Server.caveMesh.setTile(tilex + xx, tiley + yy, Tiles.encode(Tiles.decodeHeight(temptile2), Tiles.Tile.TILE_CAVE_WALL_LAVA.id, Tiles.decodeData(temptile2)));
                                Players.getInstance().sendChangedTile(tilex + xx, tiley + yy, false, true);
                                Players.getInstance().sendChangedTile(tilex + xx, tiley + yy, TilePoller.pollingSurface, true);
                                TilePoller.pollEruption = false;
                            }
                        }
                    }
                }
            }
            if (!TilePoller.pollEruption) {
                TilePoller.logger.log(Level.INFO, "Eruption at " + tilex + ", " + tiley + "!");
            }
        }
    }
    
    public static final boolean checkCreateIronRock(final int tilex, final int tiley) {
        TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 103591L);
        final boolean canHaveRock = TilePoller.r.nextInt(TilePoller.forageChance) == 0;
        if (canHaveRock) {
            if (containsStructure(tilex, tiley)) {
                return false;
            }
            final boolean containsRock = Server.isForagable(tilex, tiley);
            if (!containsRock) {
                setGrassHasSeeds(tilex, tiley, true, false);
                return true;
            }
        }
        return false;
    }
    
    private static final void checkForLavaFlow(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (Server.rand.nextInt(30) == 0) {
            if ((Tiles.decodeData(tile) & 0xFF) != 0xFF) {
                final int tempint2 = Tiles.decodeHeight(tile);
                final int temptile1 = Tiles.encode((short)tempint2, Tiles.Tile.TILE_ROCK.id, (byte)0);
                checkCreateIronRock(tilex, tiley);
                TilePoller.currentMesh.setTile(tilex, tiley, temptile1);
                for (int xx = 0; xx <= 1; ++xx) {
                    for (int yy = 0; yy <= 1; ++yy) {
                        try {
                            final int tempint3 = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + xx, tiley + yy));
                            Server.rockMesh.setTile(tilex + xx, tiley + yy, Tiles.encode((short)tempint3, Tiles.Tile.TILE_ROCK.id, (byte)0));
                        }
                        catch (Exception ex) {}
                    }
                }
                Terraforming.caveIn(tilex, tiley);
            }
        }
        else if (Server.rand.nextInt(40) == 0) {
            boolean foundHole = false;
            if (Tiles.decodeHeight(tile) > 0 && TilePoller.pollingSurface) {
                final int currHeight = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley));
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int yy = -1; yy <= 1; ++yy) {
                        if ((xx == 0 && yy != 0) || (yy == 0 && xx != 0)) {
                            if (foundHole) {
                                break;
                            }
                            if (tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < 1 << Constants.meshSize && tiley + yy < 1 << Constants.meshSize) {
                                final int tempint4 = Tiles.decodeHeight(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                                if (tempint4 < currHeight) {
                                    final int t = TilePoller.currentMesh.getTile(tilex + xx, tiley + yy);
                                    final byte type2 = Tiles.decodeType(t);
                                    if (Server.rand.nextInt(2) == 0 && type2 != Tiles.Tile.TILE_LAVA.id && type2 != Tiles.Tile.TILE_HOLE.id && !Tiles.isMineDoor(type2)) {
                                        final int tempint5 = tempint4 + 4;
                                        final int temptile2 = Tiles.encode((short)tempint5, Tiles.Tile.TILE_LAVA.id, (byte)0);
                                        for (int xn = 0; xn <= 1; ++xn) {
                                            for (int yn = 0; yn <= 1; ++yn) {
                                                try {
                                                    final int tempint6 = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + xx + xn, tiley + yy + yn));
                                                    Server.rockMesh.setTile(tilex + xx + xn, tiley + yy + yn, Tiles.encode((short)tempint6, Tiles.Tile.TILE_ROCK.id, (byte)0));
                                                }
                                                catch (Exception ex2) {}
                                            }
                                        }
                                        TilePoller.currentMesh.setTile(tilex + xx, tiley + yy, temptile2);
                                        Server.modifyFlagsByTileType(tilex + xx, tiley + yy, Tiles.Tile.TILE_LAVA.id);
                                        Players.getInstance().sendChangedTile(tilex + xx, tiley + yy, TilePoller.pollingSurface, true);
                                    }
                                }
                                else if ((Tiles.decodeData(tile) & 0xFF) == 0xFF) {
                                    final int t = TilePoller.currentMesh.getTile(tilex + xx, tiley + yy);
                                    final byte type2 = Tiles.decodeType(t);
                                    if (type2 == Tiles.Tile.TILE_ROCK.id) {
                                        final int temptile3 = Server.caveMesh.getTile(tilex + xx, tiley + yy);
                                        final byte tempbyte2 = Tiles.decodeType(temptile3);
                                        if (Tiles.isSolidCave(tempbyte2)) {
                                            final int tempint7 = tempint4 + 4;
                                            final int temptile4 = Tiles.encode((short)tempint7, Tiles.Tile.TILE_LAVA.id, (byte)0);
                                            TilePoller.currentMesh.setTile(tilex + xx, tiley + yy, temptile4);
                                            Server.rockMesh.setTile(tilex + xx, tiley + yy, Tiles.encode((short)tempint7, Tiles.Tile.TILE_ROCK.id, (byte)0));
                                            Server.caveMesh.setTile(tilex + xx, tiley + yy, Tiles.encode(Tiles.decodeHeight(temptile3), Tiles.Tile.TILE_CAVE_WALL_LAVA.id, Tiles.decodeData(Server.caveMesh.getTile(tilex + yy, tiley + yy))));
                                            Players.getInstance().sendChangedTile(tilex + xx, tiley + yy, false, true);
                                            Players.getInstance().sendChangedTile(tilex + xx, tiley + yy, true, true);
                                        }
                                    }
                                }
                            }
                        }
                        else if (tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < 1 << Constants.meshSize && tiley + yy < 1 << Constants.meshSize) {
                            final int t2 = TilePoller.currentMesh.getTile(tilex + xx, tiley + yy);
                            final byte type3 = Tiles.decodeType(t2);
                            if (type3 == Tiles.Tile.TILE_HOLE.id || Tiles.isMineDoor(type3)) {
                                foundHole = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static boolean checkForGrassSpread(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        final short theight = Tiles.decodeHeight(tile);
        byte tileData = 0;
        final boolean isATree = Tiles.isTree(type) || Tiles.isBush(type);
        if (isATree) {
            tileData = aData;
        }
        if (theight > TilePoller.MAX_KELP_HEIGHT && TilePoller.pollingSurface) {
            if (theight < 0) {
                TilePoller.kelpRandom.setSeed(Servers.localServer.id * 25000 + tilex / 12 * tiley / 12);
                if (TilePoller.kelpRandom.nextInt(20) == 0) {
                    TilePoller.kelpRandom.setSeed(tilex * tiley);
                    if (TilePoller.kelpRandom.nextBoolean() && theight <= TilePoller.MINIMUM_REED_HEIGHT) {
                        byte newType = Tiles.Tile.TILE_REED.id;
                        if (theight < TilePoller.MINIMUM_KELP_HEIGHT) {
                            newType = Tiles.Tile.TILE_KELP.id;
                        }
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, newType, (byte)0));
                        Server.modifyFlagsByTileType(tilex, tiley, newType);
                        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                        return true;
                    }
                }
                else if (Server.rand.nextInt(10) == 1 && Tiles.isMycelium(type)) {
                    if (Kingdoms.getKingdomTemplateFor(Zones.getKingdom(tilex, tiley)) == 3) {
                        return false;
                    }
                    byte newType = (byte)Tiles.toNormal(type);
                    if (newType == Tiles.Tile.TILE_GRASS.id) {
                        newType = Tiles.Tile.TILE_DIRT.id;
                        if (Server.rand.nextInt(7) == 1) {
                            if (theight < TilePoller.MINIMUM_KELP_HEIGHT) {
                                newType = Tiles.Tile.TILE_KELP.id;
                            }
                            else {
                                newType = Tiles.Tile.TILE_REED.id;
                            }
                        }
                    }
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, newType, tileData));
                    Server.modifyFlagsByTileType(tilex, tiley, newType);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
                return false;
            }
            final boolean checkMycel = Servers.isThisAPvpServer() && Kingdoms.getKingdomTemplateFor(Zones.getKingdom(tilex, tiley)) == 3;
            if (Tiles.isMycelium(type) && checkMycel) {
                return false;
            }
            try {
                final Zone zone = Zones.getZone(tilex, tiley, TilePoller.pollingSurface);
                final VolaTile tempvtile1 = zone.getTileOrNull(tilex, tiley);
                if (tempvtile1 != null && containsHouse(tempvtile1)) {
                    return false;
                }
            }
            catch (NoSuchZoneException nsz) {
                TilePoller.logger.log(Level.WARNING, "Weird, no zone for " + tilex + ", " + tiley + " surfaced=" + TilePoller.pollingSurface, nsz);
            }
            if (containsTracks(tilex, tiley)) {
                return false;
            }
            if (type == Tiles.Tile.TILE_DIRT_PACKED.id && Villages.getVillage(tilex, tiley, TilePoller.pollingSurface) != null) {
                return false;
            }
            boolean foundGrass = false;
            boolean foundMycel = false;
            boolean foundSteppe = false;
            boolean foundMoss = false;
            int tundraCount = 0;
            for (int xx = -1; xx <= 1; ++xx) {
                for (int yy = -1; yy <= 1; ++yy) {
                    if (((xx == 0 && yy != 0) || (yy == 0 && xx != 0)) && tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < Zones.worldTileSizeX && tiley + yy < Zones.worldTileSizeY) {
                        if (xx >= 0 && yy >= 0) {
                            final float height = Tiles.decodeHeight(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                            if (height < 0.0f) {
                                return false;
                            }
                        }
                        final byte tempbyte2 = Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                        if (tempbyte2 == Tiles.Tile.TILE_GRASS.id && !isATree) {
                            foundGrass = true;
                            if (TilePoller.flowerCounter++ == 10) {
                                tileData = (byte)(Tiles.decodeData(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy)) & 0xF);
                                TilePoller.flowerCounter = 0;
                            }
                        }
                        else if (Tiles.isNormal(tempbyte2)) {
                            foundGrass = true;
                        }
                        else if (Tiles.isMycelium(tempbyte2) && checkMycel) {
                            foundMycel = true;
                        }
                        else if (tempbyte2 == Tiles.Tile.TILE_STEPPE.id) {
                            foundSteppe = true;
                        }
                        else if (tempbyte2 == Tiles.Tile.TILE_MOSS.id) {
                            foundMoss = true;
                        }
                    }
                    if ((xx != 0 || yy != 0) && tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < Zones.worldTileSizeX && tiley + yy < Zones.worldTileSizeY) {
                        final byte tempbyte2 = Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                        if (Tiles.isTundra(tempbyte2)) {
                            ++tundraCount;
                            if (xx == 0 || yy == 0) {
                                ++tundraCount;
                            }
                        }
                    }
                }
            }
            if (!Tiles.isMycelium(type)) {
                if (foundMoss && Server.rand.nextInt(10) == 1) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, Tiles.Tile.TILE_MOSS.id, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_MOSS.id);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    return true;
                }
                if (foundSteppe && Server.rand.nextInt(4) == 1) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, Tiles.Tile.TILE_STEPPE.id, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_STEPPE.id);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    return true;
                }
                if (tundraCount > 1) {
                    if (Server.rand.nextInt(15) == 1) {
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, Tiles.Tile.TILE_TUNDRA.id, (byte)0));
                        Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_TUNDRA.id);
                        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                        return true;
                    }
                    if (foundGrass && Server.rand.nextInt(5) != 0) {
                        return true;
                    }
                }
            }
            if (foundGrass || foundMycel) {
                if (Tiles.isMycelium(type)) {
                    byte newTileType = (byte)Tiles.toNormal(type);
                    if (type == Tiles.Tile.TILE_MYCELIUM_LAWN.id) {
                        newTileType = Tiles.Tile.TILE_LAWN.id;
                    }
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, newTileType, tileData));
                    Server.modifyFlagsByTileType(tilex, tiley, newTileType);
                }
                else {
                    byte newTileType;
                    if (theight < TilePoller.MINIMUM_KELP_HEIGHT) {
                        newTileType = Tiles.Tile.TILE_KELP.id;
                    }
                    else if (theight < 0) {
                        newTileType = Tiles.Tile.TILE_REED.id;
                    }
                    else {
                        newTileType = Tiles.Tile.TILE_GRASS.id;
                    }
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(theight, newTileType, tileData));
                    Server.modifyFlagsByTileType(tilex, tiley, newTileType);
                }
                Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                return true;
            }
        }
        else if (TilePoller.pollingSurface && Server.rand.nextInt(10) == 1 && WurmCalendar.isMorning()) {
            SoundPlayer.playSound("sound.fish.splash", tilex, tiley, TilePoller.pollingSurface, 0.0f);
        }
        return false;
    }
    
    private static boolean checkForDecayToDirt(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (TilePoller.pollingSurface && Server.rand.nextInt(10) == 0) {
            try {
                final Zone zone = Zones.getZone(tilex, tiley, TilePoller.pollingSurface);
                final VolaTile tempvtile1 = zone.getTileOrNull(tilex, tiley);
                if (tempvtile1 != null && !containsHouse(tempvtile1) && tempvtile1.getVillage() == null) {
                    boolean foundMarsh = false;
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int yy = -1; yy <= 1; ++yy) {
                            if (tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < 1 << Constants.meshSize && tiley + yy < 1 << Constants.meshSize && Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy)) == Tiles.Tile.TILE_MARSH.id) {
                                foundMarsh = true;
                                break;
                            }
                        }
                    }
                    if (Tiles.decodeHeight(tile) > 0 && foundMarsh && Server.rand.nextInt(3) == 0) {
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_MARSH.id, (byte)0));
                        Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_MARSH.id);
                    }
                    else {
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                        Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
                    }
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
            }
            catch (NoSuchZoneException nsz) {
                TilePoller.logger.log(Level.WARNING, "Weird, no zone for " + tilex + ", " + tiley + " surfaced=" + TilePoller.pollingSurface, nsz);
            }
        }
        return true;
    }
    
    private static boolean checkForMycelGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (Tiles.decodeHeight(tile) > 0 && TilePoller.pollingSurface) {
            if (containsStructure(tilex, tiley)) {
                return true;
            }
            final boolean checkMycel = Servers.isThisAPvpServer() && Kingdoms.getKingdomTemplateFor(Zones.getKingdom(tilex, tiley)) == 3;
            boolean foundMycel = false;
            boolean foundMoss = false;
            boolean foundSteppe = false;
            for (int xx = -1; xx <= 1; ++xx) {
                for (int yy = -1; yy <= 1; ++yy) {
                    if (((xx == 0 && yy != 0) || (yy == 0 && xx != 0)) && tilex + xx >= 0 && tiley + yy >= 0 && tilex + xx < 1 << Constants.meshSize && tiley + yy < 1 << Constants.meshSize) {
                        if (xx >= 0 && yy >= 0) {
                            final float height = Tiles.decodeHeight(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                            if (height < 0.0f) {
                                return false;
                            }
                        }
                        final byte type2 = Tiles.decodeType(TilePoller.currentMesh.getTile(tilex + xx, tiley + yy));
                        if (Tiles.isMycelium(type2) && checkMycel) {
                            foundMycel = true;
                        }
                        else if (type2 == Tiles.Tile.TILE_MOSS.id) {
                            foundMoss = true;
                        }
                        else if (type2 == Tiles.Tile.TILE_STEPPE.id) {
                            foundSteppe = true;
                        }
                    }
                }
            }
            if (foundMycel) {
                if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MOSS.id) {
                    byte newType = Tiles.Tile.TILE_MYCELIUM.id;
                    if (type == Tiles.Tile.TILE_LAWN.id) {
                        newType = Tiles.Tile.TILE_MYCELIUM_LAWN.id;
                    }
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, newType);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
                else if (Tiles.isNormalTree(type) || Tiles.isNormalBush(type)) {
                    final byte newType = (byte)Tiles.toMycelium(type);
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType, aData));
                    Server.modifyFlagsByTileType(tilex, tiley, newType);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                }
                return foundMycel;
            }
            if (type == Tiles.Tile.TILE_GRASS.id) {
                byte newType = 0;
                if (foundMoss && Server.rand.nextInt(300) == 0) {
                    newType = Tiles.Tile.TILE_MOSS.id;
                }
                else if (foundSteppe && Server.rand.nextInt(10) == 0) {
                    newType = Tiles.Tile.TILE_STEPPE.id;
                }
                if (newType != 0) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, newType);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    return true;
                }
            }
        }
        else if (TilePoller.pollingSurface && Server.rand.nextInt(10) == 1 && WurmCalendar.isMorning()) {
            SoundPlayer.playSound("sound.fish.splash", tilex, tiley, TilePoller.pollingSurface, 0.0f);
        }
        return false;
    }
    
    private static boolean checkForTreeSprout(final int tilex, final int tiley, final int origtype, final byte origdata) {
        final int newtilex = tilex - 10 + Server.rand.nextInt(21);
        final int newtiley = tiley - 10 + Server.rand.nextInt(21);
        if (!GeneralUtilities.isValidTileLocation(newtilex, newtiley)) {
            return true;
        }
        final int newtile = TilePoller.currentMesh.getTile(newtilex, newtiley);
        if (Tiles.decodeHeight(newtile) > 0 && TilePoller.pollingSurface) {
            if (newtilex == tilex && newtiley == tiley) {
                return true;
            }
            if (containsStructure(newtilex, newtiley) || containsTracks(newtilex, newtiley)) {
                return true;
            }
            final byte newtype = Tiles.decodeType(newtile);
            final Tiles.Tile theNewTile = Tiles.getTile(newtype);
            final Tiles.Tile theOrigTile = Tiles.getTile(origtype);
            final short newHeight = Tiles.decodeHeight(newtile);
            if (theNewTile == Tiles.Tile.TILE_GRASS || theNewTile == Tiles.Tile.TILE_MYCELIUM) {
                int foundTrees = 0;
                for (int xx = -3; xx <= 3; ++xx) {
                    for (int yy = -3; yy <= 3; ++yy) {
                        if (((xx == 0 && yy != 0) || (xx != 0 && yy == 0)) && GeneralUtilities.isValidTileLocation(newtilex + xx, newtiley + yy)) {
                            final int checkTile = TilePoller.currentMesh.getTile(newtilex + xx, newtiley + yy);
                            final byte checktype = Tiles.decodeType(checkTile);
                            final byte checkdata = Tiles.decodeData(checkTile);
                            final Tiles.Tile theCheckTile = Tiles.getTile(checktype);
                            if (theCheckTile.isTree()) {
                                if (theCheckTile.isOak(checkdata) || theOrigTile.isOak(origdata)) {
                                    ++foundTrees;
                                    break;
                                }
                                if (theCheckTile.isWillow(checkdata) || theOrigTile.isWillow(origdata)) {
                                    if (xx > -3 && xx < 3 && yy > -3 && yy < 3) {
                                        ++foundTrees;
                                        break;
                                    }
                                }
                                else if (xx > -2 && xx < 2 && yy > -2 && yy < 2) {
                                    ++foundTrees;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (foundTrees < 1) {
                    byte newdata = 0;
                    boolean evil = false;
                    if (Kingdoms.getKingdomTemplateFor(Zones.getKingdom(tilex, tiley)) == 3) {
                        evil = true;
                    }
                    byte newType;
                    if (theOrigTile.isTree()) {
                        final TreeData.TreeType treetype = theOrigTile.getTreeType(origdata);
                        if (evil && theOrigTile.isMycelium()) {
                            newType = treetype.asMyceliumTree();
                        }
                        else {
                            newType = treetype.asNormalTree();
                        }
                    }
                    else {
                        final BushData.BushType bushtype = theOrigTile.getBushType(origdata);
                        if (evil && theOrigTile.isMycelium()) {
                            newType = bushtype.asMyceliumBush();
                        }
                        else {
                            newType = bushtype.asNormalBush();
                        }
                    }
                    newdata = Tiles.encodeTreeData(FoliageAge.YOUNG_ONE, false, false, GrassData.GrowthTreeStage.SHORT);
                    TilePoller.currentMesh.setTile(newtilex, newtiley, Tiles.encode(newHeight, newType, newdata));
                    Server.modifyFlagsByTileType(newtilex, newtiley, newType);
                    Server.setWorldResource(newtilex, newtiley, 0);
                    if (WurmCalendar.isNight()) {
                        SoundPlayer.playSound("sound.birdsong.bird1", newtilex, newtiley, TilePoller.pollingSurface, 3.0f);
                    }
                    else {
                        SoundPlayer.playSound("sound.birdsong.bird4", newtilex, newtiley, TilePoller.pollingSurface, 0.3f);
                    }
                    Players.getInstance().sendChangedTile(newtilex, newtiley, TilePoller.pollingSurface, false);
                    return true;
                }
            }
        }
        else if (TilePoller.pollingSurface && Server.rand.nextInt(10) == 1 && WurmCalendar.isMorning()) {
            SoundPlayer.playSound("sound.fish.splash", newtilex, newtiley, TilePoller.pollingSurface, 0.0f);
        }
        return false;
    }
    
    private static void checkForTreeGrowth(final int tile, final int tilex, final int tiley, final byte type, final byte aData) {
        if (TilePoller.pollingSurface) {
            final Tiles.Tile theTile = Tiles.getTile(type);
            final boolean underwater = Tiles.decodeHeight(tile) <= -5;
            int age = aData >> 4 & 0xF;
            if (age != 15) {
                if (TilePoller.treeGrowth == 0 && age <= 1) {
                    TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, (byte)0));
                    Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_GRASS.id);
                    Server.setWorldResource(tilex, tiley, 0);
                    Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    return;
                }
                final int chance = TilePoller.entryServer ? Server.rand.nextInt(20) : Server.rand.nextInt(225);
                if (chance <= (16 - age) * (16 - age)) {
                    final byte partdata = (byte)(aData & 0xF);
                    final boolean isOak = theTile.isOak(partdata);
                    final boolean isWillow = theTile.isWillow(partdata);
                    if (!isOak || Server.rand.nextInt(5) == 0) {
                        if (theTile.isMycelium() && Kingdoms.getKingdomTemplateFor(Zones.getKingdom(tilex, tiley)) != 3 && Server.rand.nextInt(3) == 0) {
                            final byte newData = (byte)(aData & 0xF7);
                            byte newType;
                            if (theTile.isTree()) {
                                newType = theTile.getTreeType(partdata).asNormalTree();
                            }
                            else {
                                newType = theTile.getBushType(partdata).asNormalBush();
                            }
                            TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType, newData));
                            Server.modifyFlagsByTileType(tilex, tiley, newType);
                            Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                            return;
                        }
                        if (underwater) {
                            age = FoliageAge.SHRIVELLED.getAgeId();
                        }
                        else {
                            ++age;
                        }
                        if (chance > 8) {
                            if (WurmCalendar.isNight()) {
                                SoundPlayer.playSound("sound.birdsong.owl.short", tilex, tiley, TilePoller.pollingSurface, 4.0f);
                            }
                            else {
                                SoundPlayer.playSound("sound.ambient.day.crickets", tilex, tiley, TilePoller.pollingSurface, 0.0f);
                            }
                        }
                        Server.setWorldResource(tilex, tiley, 0);
                        final byte newData2 = (byte)((age << 4) + partdata & 0xFF);
                        final byte newType2 = convertToNewType(theTile, newData2);
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType2, newData2));
                        Server.modifyFlagsByTileType(tilex, tiley, newType2);
                        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                        if (age >= 15) {
                            Zones.removeWildHive(tilex, tiley);
                        }
                    }
                    if ((isOak || isWillow) && age >= 7) {
                        int rad = 1;
                        if (age >= 10) {
                            rad = 2;
                        }
                        final int maxX = Math.min(tilex + rad, TilePoller.currentMesh.getSize() - 1);
                        final int maxY = Math.min(tiley + rad, TilePoller.currentMesh.getSize() - 1);
                        for (int x = Math.max(tilex - rad, 0); x <= maxX; ++x) {
                            for (int y = Math.max(tiley - rad, 0); y <= maxY; ++y) {
                                if (x != tilex || y != tiley) {
                                    final int tt = TilePoller.currentMesh.getTile(x, y);
                                    final byte ttyp = Tiles.decodeType(tt);
                                    final Tiles.Tile ttile = Tiles.getTile(ttyp);
                                    byte newType3 = Tiles.Tile.TILE_GRASS.id;
                                    if (ttile.isMyceliumTree() || ttile.isMyceliumBush()) {
                                        newType3 = Tiles.Tile.TILE_MYCELIUM.id;
                                    }
                                    if (ttile.isTree() || ttile.isBush()) {
                                        final byte newData3 = 0;
                                        Server.setWorldResource(x, y, 0);
                                        TilePoller.currentMesh.setTile(x, y, Tiles.encode(Tiles.decodeHeight(tt), newType3, (byte)0));
                                        Server.modifyFlagsByTileType(x, y, newType3);
                                        Players.getInstance().sendChangedTile(x, y, TilePoller.pollingSurface, false);
                                        Zones.removeWildHive(x, y);
                                    }
                                }
                            }
                        }
                    }
                    Zones.reposWildHive(tilex, tiley, theTile, aData);
                }
                if (age < 15 && age > 8 && TilePoller.treeGrowth > 0 && !underwater && theTile != Tiles.Tile.TILE_BUSH_LINGONBERRY) {
                    final int growthChance = Server.rand.nextInt(TilePoller.treeGrowth);
                    if (growthChance < 1) {
                        checkForTreeSprout(tilex, tiley, type, aData);
                    }
                    else if (growthChance == 2) {
                        growMushroom(tilex, tiley);
                    }
                }
                if (theTile.isTree() && age == 15) {
                    Server.setGrubs(tilex, tiley, true);
                }
                if (theTile.isTree() && age == 14 && theTile == Tiles.Tile.TILE_TREE_BIRCH) {
                    checkGrubGrowth(tile, tilex, tiley);
                }
                if (theTile.isBush()) {
                    checkGrubGrowth(tile, tilex, tiley);
                }
            }
            else if (theTile == Tiles.Tile.TILE_BUSH_LINGONBERRY) {
                TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_TUNDRA.id, (byte)0));
                Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_TUNDRA.id);
                Server.setWorldResource(tilex, tiley, 0);
                Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
            }
            else {
                final int chance = Server.rand.nextInt(15);
                if (chance == 1) {
                    Zones.removeWildHive(tilex, tiley);
                    if (underwater) {
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                        Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_DIRT.id);
                        Server.setWorldResource(tilex, tiley, 0);
                        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    }
                    else {
                        byte newData4 = (byte)(aData & 0xF);
                        final byte newType4 = convertToNewType(theTile, aData);
                        boolean noChange = true;
                        final Village v = Villages.getVillage(tilex, tiley, true);
                        if (v == null) {
                            noChange = (Server.rand.nextInt(100) < 75);
                        }
                        final boolean inCenter = TreeData.isCentre(newData4) && noChange;
                        final GrassData.GrowthTreeStage stage = TreeData.getGrassLength(newData4);
                        newData4 = Tiles.encodeTreeData((byte)0, false, inCenter, stage);
                        Server.setWorldResource(tilex, tiley, 0);
                        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), newType4, newData4));
                        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
                    }
                }
                else {
                    checkGrubGrowth(tile, tilex, tiley);
                }
            }
        }
    }
    
    private static void checkForLingonberryStart(final int tile, final int tilex, final int tiley) {
        for (int x = tilex - 2; x < tilex + 2; ++x) {
            for (int y = tiley - 2; y < tiley + 2; ++y) {
                final int tt = TilePoller.currentMesh.getTile(x, y);
                final byte ttyp = Tiles.decodeType(tt);
                final Tiles.Tile ttile = Tiles.getTile(ttyp);
                if (ttile != Tiles.Tile.TILE_TUNDRA && ttile != Tiles.Tile.TILE_GRASS && ttile != Tiles.Tile.TILE_DIRT) {
                    return;
                }
            }
        }
        TilePoller.currentMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_BUSH_LINGONBERRY.id, (byte)0));
        Server.modifyFlagsByTileType(tilex, tiley, Tiles.Tile.TILE_BUSH_LINGONBERRY.id);
        Server.setWorldResource(tilex, tiley, 0);
        Players.getInstance().sendChangedTile(tilex, tiley, TilePoller.pollingSurface, false);
    }
    
    private static byte convertToNewType(final Tiles.Tile theTile, final byte data) {
        if (theTile.isNormalTree()) {
            return theTile.getTreeType(data).asNormalTree();
        }
        if (theTile.isMyceliumTree()) {
            return theTile.getTreeType(data).asMyceliumTree();
        }
        if (theTile.isEnchantedTree()) {
            return theTile.getTreeType(data).asEnchantedTree();
        }
        if (theTile.isNormalBush()) {
            return theTile.getBushType(data).asNormalBush();
        }
        if (theTile.isMyceliumBush()) {
            return theTile.getBushType(data).asMyceliumBush();
        }
        return theTile.getBushType(data).asEnchantedBush();
    }
    
    public static void growMushroom(final int tilex, final int tiley) {
        final int num = tilex + tiley;
        if (num % 128 == 0) {
            if (TilePoller.logger.isLoggable(Level.FINEST)) {
                TilePoller.logger.finest("Creating mushrrom at tile " + tilex + ", " + tiley);
            }
            final int chance = Server.rand.nextInt(100);
            int templ = 247;
            if (chance > 40) {
                if (chance < 60) {
                    templ = 246;
                }
                else if (chance < 80) {
                    templ = 248;
                }
                else if (chance < 90) {
                    templ = 249;
                }
                else if (chance < 99) {
                    templ = 251;
                }
                else {
                    templ = 250;
                }
            }
            final float posx = (tilex << 2) + Server.rand.nextFloat() * 4.0f;
            final float posy = (tiley << 2) + Server.rand.nextFloat() * 4.0f;
            try {
                ItemFactory.createItem(templ, 80.0f + Server.rand.nextInt(20), posx, posy, Server.rand.nextInt(180), TilePoller.pollingSurface, (byte)0, -10L, null);
            }
            catch (FailedException fe) {
                TilePoller.logger.log(Level.WARNING, fe.getMessage(), fe);
            }
            catch (NoSuchTemplateException nst) {
                TilePoller.logger.log(Level.WARNING, nst.getMessage(), nst);
            }
        }
    }
    
    private static void addWildBeeHives(final int tilex, final int tiley, final Tiles.Tile theTile, final byte aData) {
        if (Zones.isFarFromAnyHive(tilex, tiley, true)) {
            final byte age = FoliageAge.getAgeAsByte(aData);
            final TreeData.TreeType treeType = theTile.getTreeType(aData);
            if (age > 7 && age < 13) {
                boolean canHaveHive = false;
                switch (treeType) {
                    case BIRCH:
                    case PINE:
                    case CEDAR:
                    case WILLOW:
                    case MAPLE:
                    case FIR:
                    case LINDEN: {
                        canHaveHive = true;
                        break;
                    }
                }
                if (canHaveHive) {
                    TilePoller.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * TilePoller.HIVE_PRIME);
                    if (TilePoller.r.nextInt(500) == 0) {
                        boolean ok = true;
                        for (int x = -1; x <= 1; ++x) {
                            for (int y = -1; y <= 1; ++y) {
                                final VolaTile vt = Zones.getTileOrNull(x, y, true);
                                if (vt != null && vt.getStructure() != null) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                        if (ok) {
                            final Point4f p = MethodsItems.getHivePos(tilex, tiley, theTile, aData);
                            if (p.getPosZ() == 0.0f) {
                                return;
                            }
                            try {
                                final int ql = 30 + Server.rand.nextInt(41);
                                final Item wildHive = ItemFactory.createItem(1239, ql, treeType.getMaterial(), (byte)0, null);
                                wildHive.setPos(p.getPosX(), p.getPosY(), p.getPosZ(), p.getRot(), -10L);
                                wildHive.setLastOwnerId(-10L);
                                wildHive.setAuxData((byte)1);
                                final Zone zone = Zones.getZone(Zones.safeTileX(tilex), Zones.safeTileY(tiley), true);
                                zone.addItem(wildHive);
                                if (Servers.isThisATestServer()) {
                                    Players.getInstance().sendGmMessage(null, "System", "Debug: Adding Hive (" + wildHive.getWurmId() + ") @ " + tilex + "," + tiley + " ql:" + ql + " rot:" + p.getRot() + " ht:" + p.getPosZ() + " material:" + treeType.getName(), false);
                                }
                            }
                            catch (FailedException fe) {
                                TilePoller.logger.log(Level.WARNING, fe.getMessage(), fe);
                            }
                            catch (NoSuchTemplateException nst) {
                                TilePoller.logger.log(Level.WARNING, nst.getMessage(), nst);
                            }
                            catch (NoSuchZoneException e) {
                                TilePoller.logger.log(Level.WARNING, e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void checkHoneyProduction(final HiveZone hiveZone, final int tilex, final int tiley, final byte type, final byte data, final Tiles.Tile theTile) {
        final Item hive = hiveZone.getCurrentHive();
        if (!hive.isOnSurface()) {
            if (Servers.isThisATestServer()) {
                Players.getInstance().sendGmMessage(null, "System", "Debug: Queen bee died as hive is underground @ " + hive.getTileX() + "," + hive.getTileY() + " from " + hive.getWurmId() + " to " + hive.getWurmId(), false);
            }
            hive.removeQueen();
            return;
        }
        if (hiveZone.hasHive(tilex, tiley) && Server.rand.nextInt(5) == 0) {
            final Item sugar = hive.getSugar();
            if (sugar != null) {
                Items.destroyItem(sugar.getWurmId());
            }
            else {
                final Item honey = hive.getHoney();
                if (honey != null) {
                    honey.setWeight(Math.max(0, honey.getWeightGrams() - 10), true);
                    honey.setLastMaintained(WurmCalendar.currentTime);
                }
            }
        }
        if (hive.hasQueen()) {
            if (Server.rand.nextInt(3) == 0) {
                final VolaTile vt = Zones.getTileOrNull(tilex, tiley, true);
                final Item emptyHive = (vt != null) ? vt.findHive(1175, false) : null;
                if (emptyHive != null) {
                    boolean canMove = false;
                    if (hive.hasTwoQueens()) {
                        canMove = true;
                    }
                    else if (hive.getTemplateId() != 1175 && emptyHive.getCurrentQualityLevel() > hive.getCurrentQualityLevel()) {
                        final float distX = Math.abs(hive.getPosX() - emptyHive.getPosX());
                        final float distY = Math.abs(hive.getPosY() - emptyHive.getPosY());
                        final float dist = Math.max(distX, distY);
                        if (dist == 0.0f) {
                            TilePoller.logger.info("More than one hive on same tile " + hive.getPosX() + "," + hive.getPosY());
                        }
                        canMove = (Server.rand.nextInt(Math.max(1, (int)dist)) == 0);
                    }
                    if (canMove) {
                        if (Servers.isThisATestServer()) {
                            Players.getInstance().sendGmMessage(null, "System", "Debug: Queen bee migrated @ " + hive.getTileX() + "," + hive.getTileY() + " from " + hive.getWurmId() + " to " + emptyHive.getWurmId(), false);
                        }
                        hive.removeQueen();
                        emptyHive.addQueen();
                        if (!hive.hasQueen()) {
                            if (hive.getTemplateId() == 1239) {
                                for (final Item item : hive.getItemsAsArray()) {
                                    Items.destroyItem(item.getWurmId());
                                }
                                Items.destroyItem(hive.getWurmId());
                                return;
                            }
                            Zones.removeHive(hive, false);
                        }
                    }
                }
            }
            if (Server.rand.nextInt(3) == 0) {
                final Item honey2 = addHoney(tilex, tiley, hive, type, data, theTile);
                if (hive.hasQueen() && !hive.hasTwoQueens() && (WurmCalendar.isSeasonSpring() || WurmCalendar.isSeasonSummer()) && hiveZone.hasHive(tilex, tiley) && honey2 != null && honey2.getWeightGrams() > 1000 && Server.rand.nextInt(5) == 0) {
                    if (Servers.isThisATestServer()) {
                        Players.getInstance().sendGmMessage(null, "System", "Debug: Queen bee added @ " + hive.getTileX() + "," + hive.getTileY(), false);
                    }
                    hive.addQueen();
                }
            }
        }
        else {
            Zones.removeHive(hive, true);
        }
    }
    
    @Nullable
    private static Item addHoney(final int tilex, final int tiley, final Item hive, final byte type, final byte data, final Tiles.Tile theTile) {
        float nectarProduced = 0.0f;
        final int starfall = WurmCalendar.getStarfall();
        if (type == 7 || type == 43) {
            switch (FieldData.getAge(data)) {
                case 0: {
                    nectarProduced += 0.0f;
                    break;
                }
                case 1: {
                    nectarProduced += 4.0f;
                    break;
                }
                case 2: {
                    nectarProduced += 10.0f;
                    break;
                }
                case 3: {
                    nectarProduced += 15.0f;
                    break;
                }
                case 4: {
                    nectarProduced += 17.0f;
                    break;
                }
                case 5: {
                    nectarProduced += 8.0f;
                    break;
                }
                case 6: {
                    nectarProduced += 6.0f;
                    break;
                }
                case 7: {
                    nectarProduced += 0.0f;
                    break;
                }
            }
            final int worldResource = Server.getWorldResource(tilex, tiley);
            final int farmedCount = worldResource >>> 11;
            final int farmedChance = worldResource & 0x7FF;
            final int newfarmedChance = Math.min(farmedChance + (int)(nectarProduced * 10.0f), 2047);
            Server.setWorldResource(tilex, tiley, (farmedCount << 11) + newfarmedChance);
            Players.getInstance().sendChangedTile(tilex, tiley, true, false);
        }
        else if (type == 2) {
            if (starfall < 1) {
                nectarProduced += 0.0f;
            }
            else if (starfall < 4) {
                nectarProduced += 5.0f;
            }
            else if (starfall < 9) {
                nectarProduced += 5.0f;
            }
            else {
                nectarProduced += 2.0f;
            }
            final GrassData.FlowerType flowerType = GrassData.FlowerType.decodeTileData(data);
            if (flowerType != GrassData.FlowerType.NONE) {
                nectarProduced *= 1.0f + flowerType.getEncodedData() / 2.0f;
            }
        }
        else if (theTile.isEnchanted()) {
            if (starfall < 1) {
                ++nectarProduced;
            }
            else if (starfall < 4) {
                nectarProduced += 6.0f;
            }
            else if (starfall < 9) {
                nectarProduced += 6.0f;
            }
            else {
                nectarProduced += 3.0f;
            }
        }
        else if (type == 22) {
            if (starfall < 1) {
                ++nectarProduced;
            }
            else if (starfall < 4) {
                nectarProduced += 9.0f;
            }
            else if (starfall < 9) {
                nectarProduced += 11.0f;
            }
            else {
                nectarProduced += 7.0f;
            }
        }
        else if (theTile.isNormalBush() || theTile.isNormalTree()) {
            final int treeAge = FoliageAge.getAgeAsByte(data);
            if (treeAge < 4) {
                ++nectarProduced;
            }
            else if (treeAge < 8) {
                nectarProduced += 4.0f;
            }
            else if (treeAge < 12) {
                nectarProduced += 8.0f;
            }
            else if (treeAge < 14) {
                nectarProduced += 7.0f;
            }
            else if (treeAge < 15) {
                nectarProduced += 5.0f;
            }
            if (starfall < 1) {
                nectarProduced += 2.0f;
            }
            else if (starfall < 4) {
                nectarProduced += 8.0f;
            }
            else if (starfall < 9) {
                nectarProduced += 6.0f;
            }
            else {
                nectarProduced += 4.0f;
            }
        }
        if (starfall < 1) {
            nectarProduced *= 0.1f;
        }
        else if (starfall < 4) {
            nectarProduced *= 1.5f;
        }
        else if (starfall < 9) {
            nectarProduced *= 1.1f;
        }
        else {
            nectarProduced *= 0.7f;
        }
        final Item honey = hive.getHoney();
        if (nectarProduced < 5.0f) {
            return honey;
        }
        int newHoneyWeight;
        final int addedHoneyWeight = newHoneyWeight = (int)nectarProduced - 4;
        if (addedHoneyWeight > 0 && hive.getFreeVolume() > addedHoneyWeight) {
            final int waxcount = hive.getWaxCount();
            if (honey != null) {
                final float totalQL = honey.getWeightGrams() * honey.getCurrentQualityLevel() + addedHoneyWeight * hive.getCurrentQualityLevel();
                newHoneyWeight = honey.getWeightGrams() + addedHoneyWeight;
                final float newQL = totalQL / newHoneyWeight;
                honey.setWeight(newHoneyWeight, true);
                honey.setQualityLevel(newQL);
                honey.setDamage(0.0f);
                honey.setLastOwnerId(0L);
            }
            else {
                try {
                    final Item newhoney = ItemFactory.createItem(70, hive.getCurrentQualityLevel(), (byte)29, (byte)0, null);
                    newhoney.setWeight(newHoneyWeight, true);
                    newhoney.setLastOwnerId(0L);
                    hive.insertItem(newhoney);
                }
                catch (FailedException e) {
                    TilePoller.logger.log(Level.WARNING, e.getMessage(), e);
                }
                catch (NoSuchTemplateException e2) {
                    TilePoller.logger.log(Level.WARNING, e2.getMessage(), e2);
                }
            }
            if (nectarProduced > 10.0f && waxcount < 20 && Server.rand.nextInt(40) == 0) {
                try {
                    final Item newwax = ItemFactory.createItem(1254, hive.getCurrentQualityLevel(), (byte)29, (byte)0, null);
                    newwax.setLastOwnerId(0L);
                    if (hive.testInsertItem(newwax)) {
                        hive.insertItem(newwax);
                    }
                    else {
                        Items.destroyItem(newwax.getWurmId());
                    }
                }
                catch (FailedException e) {
                    TilePoller.logger.log(Level.WARNING, e.getMessage(), e);
                }
                catch (NoSuchTemplateException e2) {
                    TilePoller.logger.log(Level.WARNING, e2.getMessage(), e2);
                }
            }
        }
        return honey;
    }
    
    static {
        TilePoller.logTilePolling = false;
        TilePoller.MINIMUM_REED_HEIGHT = 0;
        TilePoller.MINIMUM_KELP_HEIGHT = -30;
        TilePoller.MAX_KELP_HEIGHT = -400;
        TilePoller.currentMesh = null;
        TilePoller.currentPollTile = 0;
        TilePoller.pollModifier = 7;
        logger = Logger.getLogger(TilePoller.class.getName());
        TilePoller.pollingSurface = true;
        TilePoller.manyPerTick = false;
        TilePoller.forageChance = 2;
        TilePoller.herbChance = 2;
        TilePoller.investigateChance = 2;
        TilePoller.searchChance = 2;
        r = new Random();
        HIVE_PRIME = 102700L + WurmCalendar.getYear();
        TilePoller.pollround = 0;
        TilePoller.pollEruption = false;
        TilePoller.createFlowers = false;
        TilePoller.treeGrowth = 20;
        TilePoller.flowerCounter = 0;
        TilePoller.entryServer = false;
        kelpRandom = new Random();
        TilePoller.mask = -1;
        TilePoller.sentTilePollMessages = 0;
    }
}

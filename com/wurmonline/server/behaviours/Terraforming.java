// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.TreeMap;
import java.util.Iterator;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.shared.util.TerrainUtilities;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.Point;
import com.wurmonline.server.players.Player;
import java.util.HashSet;
import java.util.Set;
import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.Servers;
import javax.annotation.Nullable;
import com.wurmonline.server.WurmCalendar;
import java.io.IOException;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.DbFence;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.mesh.BushData;
import com.wurmonline.mesh.TreeData;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.items.Materials;
import com.wurmonline.server.creatures.MineDoorPermission;
import edu.umd.cs.findbugs.annotations.NonNull;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.epic.HexMap;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.Features;
import com.wurmonline.server.utils.CoordUtils;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.Items;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Constants;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zone;
import java.util.logging.Level;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.mesh.Tiles;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.QuestionTypes;
import com.wurmonline.server.MiscConstants;

public final class Terraforming implements MiscConstants, QuestionTypes, ItemTypes, CounterTypes, ItemMaterials, SoundNames, VillageStatus, TimeConstants
{
    public static final String cvsversion = "$Id: Terraforming.java,v 1.61 2007-04-19 23:05:18 root Exp $";
    public static final short MAX_WATER_DIG_DEPTH = -7;
    public static final short MAX_PAVE_DEPTH = -100;
    public static final short MAX_HEIGHT_DIFF = 20;
    public static final short MAX_DIAG_HEIGHT_DIFF = 28;
    private static final Logger logger;
    private static int[][] flattenTiles;
    private static int[][] rockTiles;
    private static final int[] noCaveDoor;
    private static int flattenImmutable;
    private static byte newType;
    private static byte oldType;
    private static int newTile;
    private static final float DIGGING_SKILL_MULT = 3.0f;
    private static final int saltUsed = 1000;
    private static final Random r;
    
    static final boolean isImmutableTile(final byte type) {
        return Tiles.isTree(type) || Tiles.isBush(type) || type == Tiles.Tile.TILE_CLAY.id || type == Tiles.Tile.TILE_MARSH.id || type == Tiles.Tile.TILE_PEAT.id || type == Tiles.Tile.TILE_TAR.id || type == Tiles.Tile.TILE_HOLE.id || type == Tiles.Tile.TILE_MOSS.id || type == Tiles.Tile.TILE_LAVA.id || Tiles.isMineDoor(type);
    }
    
    static final boolean isImmutableOrRoadTile(final byte type) {
        return isRoad(type) || isImmutableTile(type);
    }
    
    static final boolean isTileOverriddenByDirt(final byte type) {
        return type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id;
    }
    
    public static final boolean isRoad(final byte type) {
        return Tiles.isRoadType(type);
    }
    
    public static final boolean isSculptable(final byte type) {
        return type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_ROCK.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_MARSH.id || type == Tiles.Tile.TILE_TUNDRA.id || type == Tiles.Tile.TILE_KELP.id || type == Tiles.Tile.TILE_REED.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id;
    }
    
    public static final boolean isNonDiggableTile(final byte type) {
        return Tiles.isTree(type) || Tiles.isBush(type) || type == Tiles.Tile.TILE_LAVA.id || Tiles.isSolidCave(type) || type == Tiles.Tile.TILE_CAVE_EXIT.id || Tiles.isMineDoor(type) || type == Tiles.Tile.TILE_HOLE.id || type == Tiles.Tile.TILE_CAVE.id;
    }
    
    static final boolean isTileTurnToDirt(final byte type) {
        return type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_TUNDRA.id || type == Tiles.Tile.TILE_REED.id || type == Tiles.Tile.TILE_KELP.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id || type == Tiles.Tile.TILE_FIELD2.id;
    }
    
    static final boolean isCultivatable(final byte type) {
        return type == Tiles.Tile.TILE_DIRT_PACKED.id || type == Tiles.Tile.TILE_MOSS.id || type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_MYCELIUM.id;
    }
    
    static final boolean isSwitchableTiles(final int templateId, final byte tileType) {
        return (templateId == 26 && tileType == Tiles.Tile.TILE_SAND.id) || (templateId == 298 && tileType == Tiles.Tile.TILE_DIRT.id);
    }
    
    static final boolean isTileGrowTree(final byte type) {
        return type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_MOSS.id || type == Tiles.Tile.TILE_REED.id || type == Tiles.Tile.TILE_KELP.id;
    }
    
    static final boolean isTileGrowHedge(final byte type) {
        return type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_MARSH.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_MOSS.id || Tiles.isTree(type) || Tiles.isBush(type) || type == Tiles.Tile.TILE_CLAY.id || type == Tiles.Tile.TILE_REED.id || type == Tiles.Tile.TILE_KELP.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_MYCELIUM_LAWN.id || type == Tiles.Tile.TILE_ENCHANTED_GRASS.id;
    }
    
    static final boolean isRockTile(final byte type) {
        return Tiles.isSolidCave(type) || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_EXIT.id || type == Tiles.Tile.TILE_CLIFF.id || type == Tiles.Tile.TILE_ROCK.id || type == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id;
    }
    
    static final boolean isBuildTile(final byte type) {
        return !isRockTile(type) && type != Tiles.Tile.TILE_FIELD.id && type != Tiles.Tile.TILE_FIELD2.id && type != Tiles.Tile.TILE_CLAY.id && type != Tiles.Tile.TILE_SAND.id && type != Tiles.Tile.TILE_HOLE.id && !Tiles.isTree(type) && !Tiles.isBush(type) && type != Tiles.Tile.TILE_LAVA.id && type != Tiles.Tile.TILE_MARSH.id && !Tiles.isMineDoor(type);
    }
    
    public static final boolean isBridgeableTile(final byte type) {
        return !Tiles.isTree(type) && !Tiles.isBush(type);
    }
    
    public static final boolean isCaveEntrance(final byte type) {
        return type == Tiles.Tile.TILE_HOLE.id || Tiles.isMineDoor(type);
    }
    
    static final boolean isPackable(final byte type) {
        return !isRockTile(type) && !isImmutableTile(type) && type != Tiles.Tile.TILE_DIRT_PACKED.id && !isRoad(type) && type != Tiles.Tile.TILE_SAND.id && !Tiles.isReinforcedFloor(type);
    }
    
    private static final boolean isCornerDone(final int x, final int y, final int preferredHeight) {
        return Tiles.decodeHeight(Terraforming.flattenTiles[x][y]) == Tiles.decodeHeight(Terraforming.rockTiles[x][y]) || Tiles.decodeHeight(Terraforming.flattenTiles[x][y]) == preferredHeight;
    }
    
    public static final boolean checkHouse(final Creature performer, final int tilex, final int tiley, final int xx, final int yy, final int preferredHeight) {
        for (int x = 0; x >= -1; --x) {
            for (int y = 0; y >= -1; --y) {
                if (!isCornerDone(xx + x, yy + y, preferredHeight)) {
                    try {
                        final Zone zone = Zones.getZone(tilex + x, tiley + y, performer.isOnSurface());
                        final VolaTile vtile = zone.getTileOrNull(tilex + x, tiley + y);
                        if (vtile != null && vtile.getStructure() != null) {
                            ++Terraforming.flattenImmutable;
                            performer.getCommunicator().sendNormalServerMessage("The structure is in the way.");
                            return true;
                        }
                        continue;
                    }
                    catch (NoSuchZoneException nsz) {
                        ++Terraforming.flattenImmutable;
                        performer.getCommunicator().sendNormalServerMessage("The water is too deep to flatten.");
                        return true;
                    }
                }
                Terraforming.logger.log(Level.INFO, "Corner at " + (xx + x) + "," + (yy + y) + " is ok already. Not checking");
            }
        }
        return false;
    }
    
    static boolean obliterateCave(final Creature performer, final Action act, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int decimeterDug) {
        boolean done = false;
        final boolean insta = performer.getPower() >= 5;
        final byte type = Tiles.decodeType(tile);
        final int rockTile = Server.rockMesh.getTile(tilex, tiley);
        final short rockHeight = Tiles.decodeHeight(rockTile);
        final int caveTile = Server.caveMesh.getTile(tilex, tiley);
        final short caveFloor = Tiles.decodeHeight(caveTile);
        final short caveCeilingHeight = (short)(Tiles.decodeData(caveTile) & 0xFF);
        final int dir = (int)(act.getTarget() >> 48) & 0xFF;
        final boolean obliteratingCeiling = type == Tiles.Tile.TILE_CAVE.id && dir == 1;
        if (caveCeilingHeight + decimeterDug > 254) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens. 1");
            return true;
        }
        if (obliteratingCeiling) {
            if (caveFloor + caveCeilingHeight + decimeterDug >= rockHeight) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens. 2");
                return true;
            }
        }
        else {
            if (caveFloor - decimeterDug < -150) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens.");
                return true;
            }
            if (caveFloor == rockHeight) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens.");
                return true;
            }
        }
        done = false;
        boolean abort = false;
        if (source.getQualityLevel() < decimeterDug + 1) {
            abort = true;
        }
        int lNewTile = Server.caveMesh.getTile(tilex - 1, tiley);
        if (checkSculptCaveTile(lNewTile, performer, caveFloor, caveCeilingHeight, decimeterDug, obliteratingCeiling)) {
            abort = true;
        }
        lNewTile = Server.caveMesh.getTile(tilex + 1, tiley);
        if (checkSculptCaveTile(lNewTile, performer, caveFloor, caveCeilingHeight, decimeterDug, obliteratingCeiling)) {
            abort = true;
        }
        lNewTile = Server.caveMesh.getTile(tilex, tiley - 1);
        if (checkSculptCaveTile(lNewTile, performer, caveFloor, caveCeilingHeight, decimeterDug, obliteratingCeiling)) {
            abort = true;
        }
        lNewTile = Server.caveMesh.getTile(tilex, tiley + 1);
        if (checkSculptCaveTile(lNewTile, performer, caveFloor, caveCeilingHeight, decimeterDug, obliteratingCeiling)) {
            abort = true;
        }
        if (abort) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens. 3");
            return true;
        }
        final int time = 30;
        for (int x = 0; x >= -1; --x) {
            for (int y = 0; y >= -1; --y) {
                try {
                    final Zone zone = Zones.getZone(tilex + x, tiley + y, false);
                    final VolaTile vtile = zone.getTileOrNull(tilex + x, tiley + y);
                    if (vtile != null) {
                        if (vtile.getStructure() != null) {
                            performer.getCommunicator().sendNormalServerMessage("The structure is in the way.");
                            return true;
                        }
                        if (x == 0 && y == 0) {
                            final Fence[] fences = vtile.getFences();
                            final int length = fences.length;
                            final int n = 0;
                            if (n < length) {
                                final Fence fence = fences[n];
                                performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                return true;
                            }
                        }
                        else if (x == -1 && y == 0) {
                            for (final Fence fence : vtile.getFences()) {
                                if (fence.isHorizontal()) {
                                    performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                    return true;
                                }
                            }
                        }
                        else if (y == -1 && x == 0) {
                            for (final Fence fence : vtile.getFences()) {
                                if (!fence.isHorizontal()) {
                                    performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                    return true;
                                }
                            }
                        }
                    }
                }
                catch (NoSuchZoneException nsz2) {
                    performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                    return true;
                }
            }
        }
        if (counter == 1.0f && !insta) {
            act.setTimeLeft(30);
            performer.getCommunicator().sendNormalServerMessage("You use the " + source.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " uses " + source.getNameWithGenus() + ".", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[118].getVerbString(), true, 30);
        }
        if (counter * 10.0f > 30.0f || insta) {
            done = true;
            final int newCeil = Math.min(255, caveCeilingHeight + decimeterDug);
            if (newCeil != 255 && !insta) {
                source.setQualityLevel(source.getQualityLevel() - decimeterDug);
            }
            if (obliteratingCeiling) {
                Server.caveMesh.setTile(tilex, tiley, Tiles.encode(caveFloor, type, (byte)newCeil));
            }
            else {
                Server.caveMesh.setTile(tilex, tiley, Tiles.encode((short)(caveFloor - decimeterDug), type, (byte)newCeil));
            }
            Players.getInstance().sendChangedTile(tilex, tiley, false, true);
            for (int x2 = -1; x2 <= 0; ++x2) {
                for (int y2 = -1; y2 <= 0; ++y2) {
                    try {
                        final Zone toCheckForChange = Zones.getZone(tilex + x2, tiley + y2, false);
                        toCheckForChange.changeTile(tilex + x2, tiley + y2);
                    }
                    catch (NoSuchZoneException nsz) {
                        Terraforming.logger.log(Level.INFO, "no such zone?: " + (tilex + x2) + "," + (tiley + y2), nsz);
                        performer.getCommunicator().sendNormalServerMessage("You can't mine there.");
                        return true;
                    }
                }
            }
            performer.getCommunicator().sendNormalServerMessage("You obliterate some rock.");
        }
        return done;
    }
    
    static boolean obliterate(final Creature performer, final Action act, final Item source, final int tilex, final int tiley, final int tile, final float counter, final int decimeterDug, final MeshIO mesh) {
        boolean done = false;
        final boolean insta = performer.getPower() >= 5;
        if (source.getAuxData() <= 0 || source.getQualityLevel() > 99.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates strongly!");
            source.setAuxData((byte)1);
            source.setQualityLevel(Math.min(source.getQualityLevel(), 60.0f));
            return true;
        }
        if (tilex < 1 || tilex > (1 << Constants.meshSize) - 2 || tiley < 1 || tiley > (1 << Constants.meshSize) - 2) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep there.");
            return true;
        }
        byte type = Tiles.decodeType(tile);
        if (Tiles.isSolidCave(type) || type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_EXIT.id) {
            return obliterateCave(performer, act, source, tilex, tiley, tile, counter, decimeterDug);
        }
        if (Math.abs(getMaxSurfaceDifference(Server.surfaceMesh.getTile(tilex, tiley), tilex, tiley)) > 60) {
            performer.getCommunicator().sendNormalServerMessage("That is too steep. Nothing happens.");
            return true;
        }
        final short tileHeight = Tiles.decodeHeight(tile);
        int rockTile = Server.rockMesh.getTile(tilex, tiley);
        short rockHeight = Tiles.decodeHeight(rockTile);
        final int caveTile = Server.caveMesh.getTile(tilex, tiley);
        final short caveFloor = Tiles.decodeHeight(caveTile);
        final int caveCeilingHeight = caveFloor + (short)(Tiles.decodeData(caveTile) & 0xFF);
        final short minHeight = -5000;
        if (tileHeight - decimeterDug > -5000) {
            if (tileHeight - decimeterDug <= caveCeilingHeight) {
                performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                return true;
            }
            done = false;
            if (!insta && source.getQualityLevel() < decimeterDug + 1) {
                performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " vibrates, but nothing happens.");
                return true;
            }
            if (!isSculptable(Tiles.decodeType(tile))) {
                performer.getCommunicator().sendNormalServerMessage("Nothing happens on the " + Tiles.getTile(Tiles.decodeType(tile)).tiledesc + ". The " + source.getName() + " only seems to work on grass, rock, dirt and similar terrain.");
                return true;
            }
            int lNewTile = mesh.getTile(tilex - 1, tiley);
            if (checkSculptTile(lNewTile, performer, tileHeight, decimeterDug)) {
                return true;
            }
            lNewTile = mesh.getTile(tilex + 1, tiley);
            if (checkSculptTile(lNewTile, performer, tileHeight, decimeterDug)) {
                return true;
            }
            lNewTile = mesh.getTile(tilex, tiley - 1);
            if (checkSculptTile(lNewTile, performer, tileHeight, decimeterDug)) {
                return true;
            }
            lNewTile = mesh.getTile(tilex, tiley + 1);
            if (checkSculptTile(lNewTile, performer, tileHeight, decimeterDug)) {
                return true;
            }
            final int time = 30;
            for (int x = 0; x >= -1; --x) {
                for (int y = 0; y >= -1; --y) {
                    try {
                        final Zone zone = Zones.getZone(tilex + x, tiley + y, performer.isOnSurface());
                        final VolaTile vtile = zone.getTileOrNull(tilex + x, tiley + y);
                        if (vtile != null) {
                            if (vtile.getStructure() != null) {
                                performer.getCommunicator().sendNormalServerMessage("The structure is in the way.");
                                return true;
                            }
                            if (x == 0 && y == 0) {
                                final Fence[] fences = vtile.getFences();
                                final int length = fences.length;
                                final int n = 0;
                                if (n < length) {
                                    final Fence fence = fences[n];
                                    performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                    return true;
                                }
                            }
                            else if (x == -1 && y == 0) {
                                for (final Fence fence : vtile.getFences()) {
                                    if (fence.isHorizontal()) {
                                        performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                        return true;
                                    }
                                }
                            }
                            else if (y == -1 && x == 0) {
                                for (final Fence fence : vtile.getFences()) {
                                    if (!fence.isHorizontal()) {
                                        performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    catch (NoSuchZoneException nsz2) {
                        performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
                        return true;
                    }
                }
            }
            if (counter == 1.0f && !insta) {
                act.setTimeLeft(30);
                performer.getCommunicator().sendNormalServerMessage("You use the " + source.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " uses " + source.getNameWithGenus() + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[118].getVerbString(), true, 30);
            }
            if (counter * 10.0f > 30.0f || insta) {
                done = true;
                int clayNum = Server.getDigCount(tilex, tiley);
                if (clayNum <= 0 || clayNum > 100) {
                    clayNum = 50 + Server.rand.nextInt(50);
                }
                boolean allCornersRock = false;
                for (int x2 = 0; x2 >= -1; --x2) {
                    for (int y2 = 0; y2 >= -1; --y2) {
                        boolean lChanged = false;
                        lNewTile = mesh.getTile(tilex + x2, tiley + y2);
                        type = Tiles.decodeType(lNewTile);
                        short newTileHeight = Tiles.decodeHeight(lNewTile);
                        rockTile = Server.rockMesh.getTile(tilex + x2, tiley + y2);
                        rockHeight = Tiles.decodeHeight(rockTile);
                        if (x2 == 0 && y2 == 0) {
                            lChanged = true;
                            newTileHeight -= (short)decimeterDug;
                            mesh.setTile(tilex + x2, tiley + y2, Tiles.encode(newTileHeight, type, Tiles.decodeData(lNewTile)));
                            if (newTileHeight < rockHeight) {
                                Server.rockMesh.setTile(tilex + x2, tiley + y2, Tiles.encode(newTileHeight, (short)0));
                                rockHeight = newTileHeight;
                            }
                            if (insta) {
                                performer.getCommunicator().sendNormalServerMessage("Tile " + (tilex + x2) + ", " + (tiley + y2) + " now at " + newTileHeight + ", rock at " + rockHeight + ".");
                            }
                        }
                        allCornersRock = allCornersAtRockLevel(tilex + x2, tiley + y2, mesh);
                        if (!isImmutableTile(type) && allCornersRock) {
                            lChanged = true;
                            Server.modifyFlagsByTileType(tilex + x2, tiley + y2, Tiles.Tile.TILE_ROCK.id);
                            mesh.setTile(tilex + x2, tiley + y2, Tiles.encode(newTileHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                        }
                        else if (isTileTurnToDirt(type)) {
                            lChanged = true;
                            Server.modifyFlagsByTileType(tilex + x2, tiley + y2, Tiles.Tile.TILE_DIRT.id);
                            mesh.setTile(tilex + x2, tiley + y2, Tiles.encode(newTileHeight, Tiles.Tile.TILE_DIRT.id, (byte)0));
                        }
                        if (lChanged) {
                            if (x2 == 0 && y2 == 0) {
                                source.setQualityLevel(source.getQualityLevel() - decimeterDug);
                            }
                            performer.getMovementScheme().touchFreeMoveCounter();
                            Players.getInstance().sendChangedTile(tilex + x2, tiley + y2, performer.isOnSurface(), true);
                            try {
                                final Zone toCheckForChange = Zones.getZone(tilex + x2, tiley + y2, performer.isOnSurface());
                                toCheckForChange.changeTile(tilex + x2, tiley + y2);
                            }
                            catch (NoSuchZoneException nsz) {
                                Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
                            }
                        }
                        if (performer.isOnSurface()) {
                            final Item[] foundArtifacts = EndGameItems.getArtifactDugUp(tilex + x2, tiley + y2, newTileHeight / 10.0f, allCornersRock);
                            if (foundArtifacts.length > 0) {
                                for (int aa = 0; aa < foundArtifacts.length; ++aa) {
                                    final VolaTile atile = Zones.getOrCreateTile(tilex + x2, tiley + y2, performer.isOnSurface());
                                    atile.addItem(foundArtifacts[aa], false, false);
                                    performer.getCommunicator().sendNormalServerMessage("You find something weird! You found the " + foundArtifacts[aa].getName() + "!");
                                    Terraforming.logger.log(Level.INFO, performer.getName() + " found the " + foundArtifacts[aa].getName() + " at tile " + (tilex + x2) + ", " + (tiley + y2) + "! " + foundArtifacts[aa]);
                                    HistoryManager.addHistory(performer.getName(), "reveals the " + foundArtifacts[aa].getName());
                                    final EndGameItem egi = EndGameItems.getEndGameItem(foundArtifacts[aa]);
                                    if (egi != null) {
                                        egi.setLastMoved(System.currentTimeMillis());
                                        foundArtifacts[aa].setAuxData((byte)120);
                                    }
                                }
                            }
                        }
                        final Item[] foundItems = Items.getHiddenItemsAt(tilex + x2, tiley + y2, newTileHeight / 10.0f, true);
                        if (foundItems.length > 0) {
                            for (int aa = 0; aa < foundItems.length; ++aa) {
                                foundItems[aa].setHidden(false);
                                Items.revealItem(foundItems[aa]);
                                final VolaTile atile = Zones.getOrCreateTile(tilex + x2, tiley + y2, performer.isOnSurface());
                                atile.addItem(foundItems[aa], false, false);
                                performer.getCommunicator().sendNormalServerMessage("You find something! You found a " + foundItems[aa].getName() + "!");
                                Terraforming.logger.log(Level.INFO, performer.getName() + " found a " + foundItems[aa].getName() + " at tile " + (tilex + x2) + ", " + (tiley + y2) + ".");
                            }
                        }
                    }
                }
                performer.getCommunicator().sendNormalServerMessage("You obliterate some matter.");
            }
        }
        else {
            done = true;
            performer.getCommunicator().sendNormalServerMessage("Nothing happens.");
        }
        return done;
    }
    
    public static final int getCaveDoorDifference(final int digTile, final int digTilex, final int digTiley) {
        short difference = 0;
        short maxdifference = 0;
        short digTileHeight = Tiles.decodeHeight(digTile);
        int lNewTile = Server.surfaceMesh.getTile(digTilex, digTiley + 1);
        short height = Tiles.decodeHeight(lNewTile);
        difference = (short)Math.abs(height - digTileHeight);
        if (difference > maxdifference) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex + 1, digTiley);
        digTileHeight = Tiles.decodeHeight(lNewTile);
        lNewTile = Server.surfaceMesh.getTile(digTilex + 1, digTiley + 1);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)Math.abs(height - digTileHeight);
        if (difference > maxdifference) {
            maxdifference = difference;
        }
        return maxdifference;
    }
    
    public static final int getMaxSurfaceDifference(final int digTile, final int digTilex, final int digTiley) {
        short difference = 0;
        short maxdifference = 0;
        final short digTileHeight = Tiles.decodeHeight(digTile);
        int lNewTile = Server.surfaceMesh.getTile(digTilex - 1, digTiley);
        short height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (Math.abs(difference) > Math.abs(maxdifference)) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex, digTiley + 1);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (Math.abs(difference) > Math.abs(maxdifference)) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex, digTiley - 1);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (Math.abs(difference) > Math.abs(maxdifference)) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex + 1, digTiley);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (Math.abs(difference) > Math.abs(maxdifference)) {
            maxdifference = difference;
        }
        return maxdifference;
    }
    
    public static final int getMaxSurfaceDownSlope(final int digTilex, final int digTiley) {
        final int digTile = Server.surfaceMesh.getTile(digTilex, digTiley);
        short difference = 0;
        short maxdifference = 0;
        final short digTileHeight = Tiles.decodeHeight(digTile);
        int lNewTile = Server.surfaceMesh.getTile(digTilex - 1, digTiley);
        short height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (difference < maxdifference) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex, digTiley + 1);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (difference < maxdifference) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex, digTiley - 1);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (difference < maxdifference) {
            maxdifference = difference;
        }
        lNewTile = Server.surfaceMesh.getTile(digTilex + 1, digTiley);
        height = Tiles.decodeHeight(lNewTile);
        difference = (short)(height - digTileHeight);
        if (difference < maxdifference) {
            maxdifference = difference;
        }
        return maxdifference;
    }
    
    public static int getTileResource(final byte type) {
        int templateId = 26;
        if (type == Tiles.Tile.TILE_CLAY.id) {
            templateId = 130;
        }
        else if (type == Tiles.Tile.TILE_SAND.id) {
            templateId = 298;
        }
        else if (type == Tiles.Tile.TILE_PEAT.id) {
            templateId = 467;
        }
        else if (type == Tiles.Tile.TILE_TAR.id) {
            templateId = 153;
        }
        else if (type == Tiles.Tile.TILE_MOSS.id) {
            templateId = 479;
        }
        return templateId;
    }
    
    static boolean dig(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final boolean corner, final MeshIO mesh) {
        return dig(performer, source, tilex, tiley, tile, counter, corner, mesh, false);
    }
    
    static boolean dig(final Creature performer, final Item source, final int tilex, final int tiley, final int tile, final float counter, final boolean corner, final MeshIO mesh, final boolean toPile) {
        boolean done = false;
        boolean dredging = false;
        final boolean digToPile = toPile && source != null && (!source.isDredgingTool() || source.isWand());
        try {
            final boolean insta = source.isWand() && performer.getPower() >= 2;
            if (source.isDredgingTool() && (source.getTemplateId() != 176 || performer.getPositionZ() < 0.0f)) {
                dredging = true;
            }
            int digTilex;
            int digTiley;
            if (corner) {
                digTilex = tilex;
                digTiley = tiley;
            }
            else {
                final Vector2f pos = performer.getPos2f();
                digTilex = CoordUtils.WorldToTile(pos.x + 2.0f);
                digTiley = CoordUtils.WorldToTile(pos.y + 2.0f);
            }
            if (digTilex < 0 || digTilex > 1 << Constants.meshSize || digTiley < 0 || digTiley > 1 << Constants.meshSize) {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep to dig.");
                return true;
            }
            if (Features.Feature.WAGONER.isEnabled() && MethodsHighways.onWagonerCamp(digTilex, digTiley, true)) {
                performer.getCommunicator().sendNormalServerMessage("The wagoner whips you once and tells you never to try digging here again.");
                return true;
            }
            final int digTile = mesh.getTile(digTilex, digTiley);
            byte type = Tiles.decodeType(digTile);
            final int templateId = getTileResource(type);
            final int currentTileHeight = Tiles.decodeHeight(digTile);
            final int currentTileRock = Server.rockMesh.getTile(digTilex, digTiley);
            final int currentRockHeight = Tiles.decodeHeight(currentTileRock);
            if (currentTileHeight <= currentRockHeight) {
                performer.getCommunicator().sendNormalServerMessage("You can not dig in the solid rock.");
                final HighwayPos highwayPos = MethodsHighways.getHighwayPos(digTilex, digTiley, true);
                if (!MethodsHighways.onHighway(highwayPos)) {
                    for (int x = 0; x >= -1; --x) {
                        for (int y = 0; y >= -1; --y) {
                            final int theTile = mesh.getTile(digTilex + x, digTiley + y);
                            final byte theType = Tiles.decodeType(theTile);
                            final boolean allCornersRock = allCornersAtRockLevel(digTilex + x, digTiley + y, mesh);
                            if (!isRockTile(theType) && !isImmutableTile(theType) && allCornersRock && !Tiles.isTree(type) && !Tiles.isBush(type)) {
                                final float oldTileHeight = Tiles.decodeHeightAsFloat(theTile);
                                Server.modifyFlagsByTileType(digTilex + x, digTiley + y, Tiles.Tile.TILE_ROCK.id);
                                mesh.setTile(digTilex + x, digTiley + y, Tiles.encode(oldTileHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                                Players.getInstance().sendChangedTile(digTilex + x, digTiley + y, performer.isOnSurface(), true);
                            }
                        }
                    }
                }
                return true;
            }
            Village village = null;
            final int encodedTile = Server.surfaceMesh.getTile(digTilex, digTiley);
            village = Zones.getVillage(digTilex, digTiley, performer.isOnSurface());
            int checkX = digTilex;
            int checkY = digTiley;
            if (village == null) {
                checkX = (int)performer.getStatus().getPositionX() - 2 >> 2;
                village = Zones.getVillage(checkX, checkY, performer.isOnSurface());
            }
            if (village == null) {
                checkY = (int)performer.getStatus().getPositionY() - 2 >> 2;
                village = Zones.getVillage(checkX, checkY, performer.isOnSurface());
            }
            if (village == null) {
                checkX = (int)performer.getStatus().getPositionX() + 2 >> 2;
                village = Zones.getVillage(checkX, checkY, performer.isOnSurface());
            }
            if (village != null && !village.isActionAllowed((short)144, performer, false, encodedTile, 0)) {
                if (!Zones.isOnPvPServer(tilex, tiley)) {
                    performer.getCommunicator().sendNormalServerMessage("This action is not allowed here, because the tile is on a player owned deed that has disallowed it.", (byte)3);
                    return true;
                }
                if (!village.isEnemy(performer) && performer.isLegal()) {
                    performer.getCommunicator().sendNormalServerMessage("That would be illegal here. You can check the settlement token for the local laws.", (byte)3);
                    return true;
                }
            }
            final int weight = ItemTemplateFactory.getInstance().getTemplate(templateId).getWeightGrams();
            if (!insta) {
                if (performer.getInventory().getNumItemsNotCoins() >= 100) {
                    performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the " + ItemTemplateFactory.getInstance().getTemplate(templateId).getName() + ". You need to drop some things first.");
                    return true;
                }
                if (!performer.canCarry(weight)) {
                    performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the " + ItemTemplateFactory.getInstance().getTemplate(templateId).getName() + ". You need to drop some things first.");
                    return true;
                }
                if (dredging && source.getFreeVolume() < 1000) {
                    performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " is full.");
                    return true;
                }
            }
            final short digTileHeight = Tiles.decodeHeight(digTile);
            int rockTile = Server.rockMesh.getTile(digTilex, digTiley);
            short rockHeight = Tiles.decodeHeight(rockTile);
            final short h = Tiles.decodeHeight(digTile);
            short minHeight = -7;
            short maxHeight = 20000;
            final Skills skills = performer.getSkills();
            Skill digging = null;
            try {
                digging = skills.getSkill(1009);
            }
            catch (Exception ex) {
                digging = skills.learn(1009, 0.0f);
            }
            if (insta) {
                minHeight = -300;
            }
            else if (dredging) {
                maxHeight = -1;
                minHeight = (short)(-Math.max(3.0, digging.getKnowledge(source, 0.0) * 3.0));
            }
            if (h > minHeight && h < maxHeight) {
                done = false;
                Skill shovel = null;
                double power = 0.0;
                Label_1075: {
                    if (!insta) {
                        try {
                            shovel = skills.getSkill(source.getPrimarySkill());
                        }
                        catch (Exception ex2) {
                            try {
                                shovel = skills.learn(source.getPrimarySkill(), 1.0f);
                            }
                            catch (NoSuchSkillException nse) {
                                if (performer.getPower() > 0) {
                                    break Label_1075;
                                }
                                Terraforming.logger.log(Level.WARNING, performer.getName() + " trying to dig with an item with no primary skill: " + source.getName());
                            }
                        }
                    }
                }
                short maxdifference = 0;
                if (!insta) {
                    if (checkIfTerraformingOnPermaObject(digTilex, digTiley)) {
                        performer.getCommunicator().sendNormalServerMessage("The object nearby prevents digging further down.");
                        return true;
                    }
                    if (Zones.isTileCornerProtected(digTilex, digTiley)) {
                        performer.getCommunicator().sendNormalServerMessage("Your shovel fails to penetrate the earth no matter what you try. Weird.");
                        return true;
                    }
                    if (isTileModBlocked(performer, digTilex, digTiley, true)) {
                        return true;
                    }
                    if ((Features.Feature.HIGHWAYS.isEnabled() || digTileHeight > 0) && wouldDestroyCobble(performer, digTilex, digTiley, false)) {
                        if (Features.Feature.HIGHWAYS.isEnabled()) {
                            performer.getCommunicator().sendNormalServerMessage("The highway would be too steep to traverse.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The road would be too steep to traverse.");
                        }
                        return true;
                    }
                    if (nextToTundra(mesh, digTilex, digTiley)) {
                        performer.getCommunicator().sendNormalServerMessage("The frozen soil is too hard to dig effectively.");
                        return true;
                    }
                    if (countNonDiggables(mesh, digTilex, digTiley) >= 3) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot dig in such terrain.");
                        return true;
                    }
                    int lNewTile = mesh.getTile(digTilex, digTiley - 1);
                    short height = Tiles.decodeHeight(lNewTile);
                    short difference = (short)Math.abs(height - digTileHeight);
                    if (difference > maxdifference) {
                        maxdifference = difference;
                    }
                    if (checkDigTile(lNewTile, performer, digging, digTileHeight, difference)) {
                        return true;
                    }
                    lNewTile = mesh.getTile(digTilex + 1, digTiley);
                    height = Tiles.decodeHeight(lNewTile);
                    difference = (short)Math.abs(height - digTileHeight);
                    if (difference > maxdifference) {
                        maxdifference = difference;
                    }
                    if (checkDigTile(lNewTile, performer, digging, digTileHeight, difference)) {
                        return true;
                    }
                    lNewTile = mesh.getTile(digTilex, digTiley + 1);
                    height = Tiles.decodeHeight(lNewTile);
                    difference = (short)Math.abs(height - digTileHeight);
                    if (difference > maxdifference) {
                        maxdifference = difference;
                    }
                    if (checkDigTile(lNewTile, performer, digging, digTileHeight, difference)) {
                        return true;
                    }
                    lNewTile = mesh.getTile(digTilex - 1, digTiley);
                    height = Tiles.decodeHeight(lNewTile);
                    difference = (short)Math.abs(height - digTileHeight);
                    if (difference > maxdifference) {
                        maxdifference = difference;
                    }
                    if (checkDigTile(lNewTile, performer, digging, digTileHeight, difference)) {
                        return true;
                    }
                }
                Action act = null;
                try {
                    act = performer.getCurrentAction();
                }
                catch (NoSuchActionException nsa) {
                    Terraforming.logger.log(Level.WARNING, "Weird: " + nsa.getMessage(), nsa);
                    return true;
                }
                int time = 1000;
                for (int x2 = 0; x2 >= -1; --x2) {
                    for (int y2 = 0; y2 >= -1; --y2) {
                        try {
                            final Zone zone = Zones.getZone(digTilex + x2, digTiley + y2, performer.isOnSurface());
                            final VolaTile vtile = zone.getTileOrNull(digTilex + x2, digTiley + y2);
                            if (vtile != null) {
                                if (vtile.getStructure() != null) {
                                    if (vtile.getStructure().isTypeHouse()) {
                                        performer.getCommunicator().sendNormalServerMessage("The house is in the way.");
                                        return true;
                                    }
                                    final BridgePart[] bps = vtile.getBridgeParts();
                                    if (bps.length == 1) {
                                        if (bps[0].getType().isSupportType()) {
                                            performer.getCommunicator().sendNormalServerMessage("The bridge support nearby prevents digging.");
                                            return true;
                                        }
                                        if ((x2 == -1 && bps[0].hasEastExit()) || (x2 == 0 && bps[0].hasWestExit()) || (y2 == -1 && bps[0].hasSouthExit()) || (y2 == 0 && bps[0].hasNorthExit())) {
                                            performer.getCommunicator().sendNormalServerMessage("The end of the bridge nearby prevents digging.");
                                            return true;
                                        }
                                    }
                                }
                                if (x2 == 0 && y2 == 0) {
                                    final Fence[] fences = vtile.getFences();
                                    final int length = fences.length;
                                    final int n = 0;
                                    if (n < length) {
                                        final Fence fence = fences[n];
                                        performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                        return true;
                                    }
                                }
                                else if (x2 == -1 && y2 == 0) {
                                    for (final Fence fence : vtile.getFences()) {
                                        if (fence.isHorizontal()) {
                                            performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                            return true;
                                        }
                                    }
                                }
                                else if (y2 == -1 && x2 == 0) {
                                    for (final Fence fence : vtile.getFences()) {
                                        if (!fence.isHorizontal()) {
                                            performer.getCommunicator().sendNormalServerMessage("The " + fence.getName() + " is in the way.");
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                        catch (NoSuchZoneException nsz2) {
                            performer.getCommunicator().sendNormalServerMessage("The water is too deep to dig in.");
                            return true;
                        }
                        if (performer.getStrengthSkill() < 20.0) {
                            Terraforming.newTile = mesh.getTile(digTilex + x2, digTiley + y2);
                            if (isRoad(Tiles.decodeType(Terraforming.newTile))) {
                                performer.getCommunicator().sendNormalServerMessage("You need to be stronger to dig on roads.");
                                return true;
                            }
                        }
                    }
                }
                if (counter == 1.0f && !insta) {
                    if (dredging && h < -0.5) {
                        time = Actions.getStandardActionTime(performer, digging, source, 0.0);
                        act.setTimeLeft(time);
                        performer.getCommunicator().sendNormalServerMessage("You start to dredge.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to dredge.", performer, 5);
                        performer.sendActionControl(Actions.actionEntrys[362].getVerbString(), true, time);
                        performer.getStatus().modifyStamina(-3000.0f);
                    }
                    else {
                        time = Actions.getStandardActionTime(performer, digging, source, 0.0);
                        act.setTimeLeft(time);
                        performer.getCommunicator().sendNormalServerMessage("You start to dig.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to dig.", performer, 5);
                        performer.sendActionControl(Actions.actionEntrys[144].getVerbString(), true, time);
                        performer.getStatus().modifyStamina(-1000.0f);
                    }
                    source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                }
                else if (!insta) {
                    time = act.getTimeLeft();
                    if (act.justTickedSecond() && ((time < 50 && act.currentSecond() % 2 == 0) || act.currentSecond() % 5 == 0)) {
                        String sstring = "sound.work.digging1";
                        final int x3 = Server.rand.nextInt(3);
                        if (x3 == 0) {
                            sstring = "sound.work.digging2";
                        }
                        else if (x3 == 1) {
                            sstring = "sound.work.digging3";
                        }
                        SoundPlayer.playSound(sstring, performer, 0.0f);
                        source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    }
                }
                if (counter * 10.0f > time || insta) {
                    final int performerTileX = performer.getTileX();
                    final int performerTileY = performer.getTileY();
                    final int performerTile = mesh.getTile(performerTileX, performerTileY);
                    final byte resType = Tiles.decodeType(performerTile);
                    if (!insta) {
                        if (act.getRarity() != 0) {
                            performer.playPersonalSound("sound.fx.drumroll");
                        }
                        double diff = 1 + maxdifference / 5;
                        if (resType == Tiles.Tile.TILE_CLAY.id) {
                            diff += 20.0;
                        }
                        else if (resType == Tiles.Tile.TILE_SAND.id) {
                            diff += 10.0;
                        }
                        else if (resType == Tiles.Tile.TILE_TAR.id) {
                            diff += 35.0;
                        }
                        else if (resType == Tiles.Tile.TILE_MOSS.id) {
                            diff += 10.0;
                        }
                        else if (resType == Tiles.Tile.TILE_MARSH.id) {
                            diff += 30.0;
                        }
                        else if (resType == Tiles.Tile.TILE_STEPPE.id) {
                            diff += 40.0;
                        }
                        else if (resType == Tiles.Tile.TILE_TUNDRA.id) {
                            diff += 20.0;
                        }
                        if (shovel != null) {
                            shovel.skillCheck(diff, source, 0.0, false, counter);
                        }
                        power = digging.skillCheck(diff, source, 0.0, false, counter);
                        if (power < 0.0) {
                            for (int i = 0; i < 20; ++i) {
                                power = digging.skillCheck(diff, source, 0.0, true, 1.0f);
                                if (power > 1.0) {
                                    break;
                                }
                                power = 1.0;
                            }
                        }
                        final float staminaCost = act.getTimeLeft() * -100;
                        performer.getStatus().modifyStamina(staminaCost);
                    }
                    done = true;
                    boolean hitRock = false;
                    boolean dealDirt = false;
                    short newDigHeight = 30000;
                    boolean dealClay = false;
                    boolean dealSand = false;
                    boolean dealPeat = false;
                    boolean dealTar = false;
                    boolean dealMoss = false;
                    int clayNum = Server.getDigCount(tilex, tiley);
                    if (clayNum <= 0 || clayNum > 100) {
                        clayNum = 50 + Server.rand.nextInt(50);
                    }
                    boolean allCornersRock2 = false;
                    for (int x4 = 0; x4 >= -1; --x4) {
                        for (int y3 = 0; y3 >= -1; --y3) {
                            boolean lChanged = false;
                            final int lNewTile2 = mesh.getTile(digTilex + x4, digTiley + y3);
                            type = Tiles.decodeType(lNewTile2);
                            short newTileHeight = Tiles.decodeHeight(lNewTile2);
                            rockTile = Server.rockMesh.getTile(digTilex + x4, digTiley + y3);
                            rockHeight = Tiles.decodeHeight(rockTile);
                            if (x4 == 0 && y3 == 0) {
                                if (resType == Tiles.Tile.TILE_CLAY.id) {
                                    dealClay = true;
                                }
                                else if (resType == Tiles.Tile.TILE_SAND.id) {
                                    dealSand = true;
                                }
                                else if (resType == Tiles.Tile.TILE_PEAT.id) {
                                    dealPeat = true;
                                }
                                else if (resType == Tiles.Tile.TILE_TAR.id) {
                                    dealTar = true;
                                }
                                else if (resType == Tiles.Tile.TILE_MOSS.id) {
                                    dealMoss = true;
                                }
                                if (newTileHeight > rockHeight) {
                                    dealDirt = true;
                                    lChanged = true;
                                    if (dealClay) {
                                        if (--clayNum == 0) {
                                            newTileHeight = (short)Math.max(newTileHeight - 1, rockHeight);
                                        }
                                    }
                                    else if (!dealTar && !dealMoss && !dealPeat) {
                                        newTileHeight = (short)Math.max(newTileHeight - 1, rockHeight);
                                    }
                                    if (insta) {
                                        performer.getCommunicator().sendNormalServerMessage("Tile " + (digTilex + x4) + ", " + (digTiley + y3) + " now at " + newTileHeight + ", rock at " + rockHeight + ".");
                                    }
                                    newDigHeight = newTileHeight;
                                    mesh.setTile(digTilex + x4, digTiley + y3, Tiles.encode(newTileHeight, type, Tiles.decodeData(lNewTile2)));
                                    if (performer.fireTileLog()) {
                                        TileEvent.log(digTilex + x4, digTiley + y3, 0, performer.getWurmId(), 144);
                                    }
                                }
                                if (newTileHeight <= rockHeight) {
                                    hitRock = true;
                                }
                            }
                            final HighwayPos highwayPos2 = MethodsHighways.getHighwayPos(digTilex + x4, digTiley + y3, true);
                            allCornersRock2 = allCornersAtRockLevel(digTilex + x4, digTiley + y3, mesh);
                            if (!isImmutableTile(type) && allCornersRock2 && !MethodsHighways.onHighway(highwayPos2)) {
                                lChanged = true;
                                Server.modifyFlagsByTileType(digTilex + x4, digTiley + y3, Tiles.Tile.TILE_ROCK.id);
                                mesh.setTile(digTilex + x4, digTiley + y3, Tiles.encode(newTileHeight, Tiles.Tile.TILE_ROCK.id, (byte)0));
                                TileEvent.log(digTilex + x4, digTiley + y3, 0, performer.getWurmId(), 144);
                            }
                            else if (isTileTurnToDirt(type)) {
                                if (type != Tiles.Tile.TILE_DIRT.id) {
                                    TileEvent.log(digTilex + x4, digTiley + y3, 0, performer.getWurmId(), 144);
                                }
                                lChanged = true;
                                Server.modifyFlagsByTileType(digTilex + x4, digTiley + y3, Tiles.Tile.TILE_DIRT.id);
                                mesh.setTile(digTilex + x4, digTiley + y3, Tiles.encode(newTileHeight, Tiles.Tile.TILE_DIRT.id, (byte)0));
                            }
                            else if (isRoad(type)) {
                                if (Methods.isActionAllowed(performer, (short)144, false, digTilex + x4, digTiley + y3, digTile, 0)) {
                                    lChanged = true;
                                    Server.modifyFlagsByTileType(digTilex + x4, digTiley + y3, type);
                                    mesh.setTile(digTilex + x4, digTiley + y3, Tiles.encode(newTileHeight, type, Tiles.decodeData(lNewTile2)));
                                }
                                if (performer.fireTileLog()) {
                                    TileEvent.log(digTilex + x4, digTiley + y3, 0, performer.getWurmId(), 144);
                                }
                            }
                            if (performer.getTutorialLevel() == 8 && !performer.skippedTutorial()) {
                                performer.missionFinished(true, true);
                            }
                            if (lChanged) {
                                performer.getMovementScheme().touchFreeMoveCounter();
                                Players.getInstance().sendChangedTile(digTilex + x4, digTiley + y3, performer.isOnSurface(), true);
                                try {
                                    final Zone toCheckForChange = Zones.getZone(digTilex + x4, digTiley + y3, performer.isOnSurface());
                                    toCheckForChange.changeTile(digTilex + x4, digTiley + y3);
                                }
                                catch (NoSuchZoneException nsz) {
                                    Terraforming.logger.log(Level.INFO, "no such zone?: " + digTilex + ", " + digTiley, nsz);
                                }
                            }
                            if (performer.isOnSurface()) {
                                final Tiles.Tile theTile2 = Tiles.getTile(type);
                                if (theTile2.isTree()) {
                                    final byte data = Tiles.decodeData(lNewTile2);
                                    Zones.reposWildHive(digTilex + x4, digTiley + y3, theTile2, data);
                                }
                                final Item[] foundArtifacts = EndGameItems.getArtifactDugUp(digTilex + x4, digTiley + y3, newTileHeight / 10.0f, allCornersRock2);
                                if (foundArtifacts.length > 0) {
                                    for (int aa = 0; aa < foundArtifacts.length; ++aa) {
                                        final VolaTile atile = Zones.getOrCreateTile(digTilex + x4, digTiley + y3, performer.isOnSurface());
                                        atile.addItem(foundArtifacts[aa], false, false);
                                        performer.getCommunicator().sendNormalServerMessage("You find something weird! You found the " + foundArtifacts[aa].getName() + "!", (byte)2);
                                        Terraforming.logger.log(Level.INFO, performer.getName() + " found the " + foundArtifacts[aa].getName() + " at tile " + (digTilex + x4) + ", " + (digTiley + y3) + "! " + foundArtifacts[aa]);
                                        HistoryManager.addHistory(performer.getName(), "reveals the " + foundArtifacts[aa].getName());
                                        final EndGameItem egi = EndGameItems.getEndGameItem(foundArtifacts[aa]);
                                        if (egi != null) {
                                            egi.setLastMoved(System.currentTimeMillis());
                                            foundArtifacts[aa].setAuxData((byte)120);
                                        }
                                    }
                                }
                            }
                            final Item[] foundItems = Items.getHiddenItemsAt(digTilex + x4, digTiley + y3, newTileHeight / 10.0f, true);
                            if (foundItems.length > 0) {
                                for (int aa2 = 0; aa2 < foundItems.length; ++aa2) {
                                    foundItems[aa2].setHidden(false);
                                    Items.revealItem(foundItems[aa2]);
                                    final VolaTile atile2 = Zones.getOrCreateTile(digTilex + x4, digTiley + y3, performer.isOnSurface());
                                    atile2.addItem(foundItems[aa2], false, false);
                                    performer.getCommunicator().sendNormalServerMessage("You find something! You found a " + foundItems[aa2].getName() + "!", (byte)2);
                                    Terraforming.logger.log(Level.INFO, performer.getName() + " found a " + foundItems[aa2].getName() + " at tile " + (digTilex + x4) + ", " + (digTiley + y3) + ".");
                                }
                            }
                        }
                    }
                    if (dealClay) {
                        Server.setDigCount(tilex, tiley, clayNum);
                    }
                    if (hitRock) {
                        performer.getCommunicator().sendNormalServerMessage("You hit rock.", (byte)3);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You dig a hole.");
                        Server.getInstance().broadCastAction(performer.getName() + " digs a hole.", performer, 5);
                    }
                    if (dealDirt) {
                        try {
                            if (!insta) {
                                final double dig = digging.getKnowledge(0.0);
                                if (power > dig) {
                                    power = dig;
                                }
                                else {
                                    power = Math.max(1.0, power);
                                }
                            }
                            else {
                                power = 50.0;
                            }
                            int createdItemTemplate = 26;
                            if (dealClay) {
                                createdItemTemplate = 130;
                            }
                            else if (dealSand) {
                                createdItemTemplate = 298;
                            }
                            else if (dealTar) {
                                createdItemTemplate = 153;
                            }
                            else if (dealPeat) {
                                createdItemTemplate = 467;
                            }
                            else if (dealMoss) {
                                createdItemTemplate = 479;
                            }
                            float modifier = 1.0f;
                            if (source.getSpellEffects() != null) {
                                modifier = source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                            }
                            final VolaTile ttile = Zones.getTileOrNull(tilex, tiley, performer.isOnSurface());
                            if (digToPile && ttile != null && ttile.getNumberOfItems(performer.getFloorLevel()) <= 99) {
                                final Item newItem = ItemFactory.createItem(createdItemTemplate, Math.min((float)(power + source.getRarity()) * modifier, 100.0f), performer.getPosX(), performer.getPosY(), Server.rand.nextFloat() * 360.0f, performer.isOnSurface(), act.getRarity(), -10L, null);
                                newItem.setLastOwnerId(performer.getWurmId());
                                performer.getCommunicator().sendNormalServerMessage("You drop a " + newItem.getName() + ".");
                            }
                            else {
                                final Item created = ItemFactory.createItem(createdItemTemplate, Math.min((float)(power + source.getRarity()) * modifier, 100.0f), null);
                                created.setRarity(act.getRarity());
                                if (dredging && h < -0.5) {
                                    boolean addedToBoat = false;
                                    if (performer.getVehicle() != -10L) {
                                        try {
                                            final Item ivehic = Items.getItem(performer.getVehicle());
                                            if (ivehic.isBoat() && ivehic.testInsertItem(created)) {
                                                ivehic.insertItem(created);
                                                performer.getCommunicator().sendNormalServerMessage("You put the " + created.getName() + " in the " + ivehic.getName() + ".");
                                                addedToBoat = true;
                                            }
                                        }
                                        catch (NoSuchItemException ex3) {}
                                    }
                                    if (!addedToBoat) {
                                        source.insertItem(created, true);
                                    }
                                }
                                else {
                                    performer.getInventory().insertItem(created);
                                }
                            }
                            if (Server.isDirtHeightLower(digTilex, digTiley, newDigHeight)) {
                                if (Server.rand.nextInt(2500) == 0) {
                                    int gemTemplateId = 374;
                                    if (Server.rand.nextFloat() * 100.0f >= 99.0f) {
                                        gemTemplateId = 375;
                                    }
                                    final float ql = Math.max(Math.min((float)(power + source.getRarity()), 100.0f), 1.0f);
                                    final Item gem = ItemFactory.createItem(gemTemplateId, ql, null);
                                    gem.setLastOwnerId(performer.getWurmId());
                                    gem.setRarity(act.getRarity());
                                    if (gem.getQualityLevel() > 99.0f) {
                                        performer.achievement(363);
                                    }
                                    else if (gem.getQualityLevel() > 90.0f) {
                                        performer.achievement(364);
                                    }
                                    if (act.getRarity() > 2) {
                                        performer.achievement(365);
                                    }
                                    performer.getInventory().insertItem(gem, true);
                                    performer.getCommunicator().sendNormalServerMessage("You find " + gem.getNameWithGenus() + "!", (byte)2);
                                }
                                if (act.getRarity() != 0 && performer.isPaying() && Server.rand.nextInt(100) == 0) {
                                    final float ql2 = Math.max(Math.min((float)(power + source.getRarity()), 100.0f), 1.0f);
                                    final Item bone = ItemFactory.createItem(867, ql2, null);
                                    bone.setRarity(act.getRarity());
                                    performer.getInventory().insertItem(bone, true);
                                    performer.getCommunicator().sendNormalServerMessage("You find something! You found a " + MethodsItems.getRarityName(act.getRarity()) + " " + bone.getName() + "!", (byte)2);
                                    performer.achievement(366);
                                }
                                if (Server.rand.nextInt(250) == 0) {
                                    final VolaTile t = Zones.getTileOrNull(digTilex, digTiley, performer.isOnSurface());
                                    if ((t != null && t.getVillage() == null) || t == null) {
                                        final EpicMission m = EpicServerStatus.getMISacrificeMission();
                                        if (m != null) {
                                            try {
                                                final Item missionItem = ItemFactory.createItem(737, 20 + Server.rand.nextInt(80), act.getRarity(), m.getEntityName());
                                                missionItem.setName(HexMap.generateFirstName(m.getMissionId()) + ' ' + HexMap.generateSecondName(m.getMissionId()));
                                                performer.getInventory().insertItem(missionItem);
                                                performer.getCommunicator().sendNormalServerMessage("You find a " + missionItem.getName() + " in amongst the dirt.");
                                            }
                                            catch (FailedException ex4) {}
                                            catch (NoSuchTemplateException ex5) {}
                                        }
                                    }
                                }
                            }
                            PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.DIG_TILE);
                        }
                        catch (FailedException fe) {
                            Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                        }
                    }
                }
            }
            else {
                done = true;
                if (dredging) {
                    if (h <= minHeight) {
                        performer.getCommunicator().sendNormalServerMessage("You do not have sufficient skill to dredge at that depth.", (byte)3);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The water is too shallow to be dredged.", (byte)3);
                    }
                }
                else if (h <= minHeight) {
                    performer.getCommunicator().sendNormalServerMessage("The water is too deep to dig.", (byte)3);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You do not have sufficient skill to dig at that height.", (byte)3);
                }
            }
        }
        catch (NoSuchTemplateException nst) {
            Terraforming.logger.log(Level.WARNING, nst.getMessage(), nst);
            done = true;
        }
        return done;
    }
    
    private static int countNonDiggables(final MeshIO mesh, final int digTilex, final int digTiley) {
        int nonDiggables = 0;
        int lNewTile = mesh.getTile(digTilex, digTiley);
        if (isNonDiggableTile(Tiles.decodeType(lNewTile))) {
            ++nonDiggables;
        }
        lNewTile = mesh.getTile(digTilex, digTiley - 1);
        if (isNonDiggableTile(Tiles.decodeType(lNewTile))) {
            ++nonDiggables;
        }
        lNewTile = mesh.getTile(digTilex - 1, digTiley - 1);
        if (isNonDiggableTile(Tiles.decodeType(lNewTile))) {
            ++nonDiggables;
        }
        lNewTile = mesh.getTile(digTilex - 1, digTiley);
        if (isNonDiggableTile(Tiles.decodeType(lNewTile))) {
            ++nonDiggables;
        }
        return nonDiggables;
    }
    
    public static boolean checkSculptCaveTile(final int lNewTile, final Creature performer, final short floorHeight, final int ceilingHeight, final int decimeterChange, final boolean obliterateCeiling) {
        if (Tiles.decodeType(lNewTile) == Tiles.Tile.TILE_CAVE_EXIT.id) {
            return true;
        }
        final short nfloorHeight = Tiles.decodeHeight(lNewTile);
        if (nfloorHeight != -100) {
            final int nceilingHeight = nfloorHeight + (short)(Tiles.decodeData(lNewTile) & 0xFF);
            if (obliterateCeiling) {
                final int checkedCeilingHeight = floorHeight + ceilingHeight + decimeterChange;
                if (Math.abs(checkedCeilingHeight - nceilingHeight) > 254) {
                    return true;
                }
            }
            else if (Math.abs(floorHeight - decimeterChange - nfloorHeight) > 254) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkMineSurfaceTile(final int lNewTile, final Creature performer, final short digTileHeight, final short maxDiff) {
        final short height = Tiles.decodeHeight(lNewTile);
        final short difference = (short)Math.abs(height - digTileHeight - 1);
        if (difference > maxDiff) {
            performer.getCommunicator().sendNormalServerMessage("You are not skilled enough to mine in the steep slope.");
            return true;
        }
        return false;
    }
    
    public static boolean checkSculptTile(final int lNewTile, final Creature performer, final short digTileHeight, final int decimeterChange) {
        if (!isSculptable(Tiles.decodeType(lNewTile))) {
            performer.getCommunicator().sendNormalServerMessage("Nothing happens on the " + Tiles.getTile(Tiles.decodeType(lNewTile)).tiledesc + ". The wand only seems to work on grass, rock, dirt and similar terrain.");
            return true;
        }
        final short height = Tiles.decodeHeight(lNewTile);
        final short difference = (short)Math.abs(height - digTileHeight - decimeterChange);
        if (difference > 200) {
            performer.getCommunicator().sendNormalServerMessage("Nothing happens on the steep slope.");
            return true;
        }
        return false;
    }
    
    public static boolean checkDigTile(final int lNewTile, final Creature performer, final Skill digging, final short digTileHeight, final short difference) {
        final double diffMod = digging.getKnowledge(0.0);
        if (difference > Math.max(10.0, 1.0 + diffMod * 3.0)) {
            performer.getCommunicator().sendNormalServerMessage("You are not skilled enough to dig in such steep slopes.", (byte)3);
            return true;
        }
        return false;
    }
    
    private static final boolean checkHeightDiff(final boolean raise, final short myHeight, final short checkHeight, final short maxHeightDiff) {
        if (raise) {
            if (myHeight - checkHeight > maxHeightDiff) {
                return true;
            }
        }
        else if (checkHeight - myHeight > maxHeightDiff) {
            return true;
        }
        return false;
    }
    
    public static final boolean nextToTundra(final MeshIO mesh, final int digTilex, final int digTiley) {
        return Tiles.isTundra(Tiles.decodeType(mesh.getTile(digTilex, digTiley))) || Tiles.isTundra(Tiles.decodeType(mesh.getTile(digTilex - 1, digTiley))) || Tiles.isTundra(Tiles.decodeType(mesh.getTile(digTilex, digTiley - 1))) || Tiles.isTundra(Tiles.decodeType(mesh.getTile(digTilex - 1, digTiley - 1)));
    }
    
    public static final boolean wouldDestroyCobble(final Creature performer, final int changeTileX, final int changeTileY, final boolean raise) {
        final short modDiff = (short)(raise ? 1 : -1);
        final MeshIO mesh = Server.surfaceMesh;
        final int mytile = mesh.getTile(changeTileX, changeTileY);
        final short myHeight = (short)(Tiles.decodeHeight(mytile) + modDiff);
        if (myHeight < 0) {
            return false;
        }
        int checkTile = mesh.getTile(changeTileX + 1, changeTileY);
        short checkHeight = Tiles.decodeHeight(checkTile);
        if (Tiles.isRoadType(mytile) && MethodsHighways.onHighway(changeTileX + 1, changeTileY, true)) {
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX, changeTileY + 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX + 1, changeTileY + 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)28)) {
                return true;
            }
        }
        checkTile = mesh.getTile(changeTileX - 1, changeTileY);
        checkHeight = Tiles.decodeHeight(checkTile);
        if (Tiles.isRoadType(checkTile) && MethodsHighways.onHighway(changeTileX - 1, changeTileY, true)) {
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX, changeTileY + 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX - 1, changeTileY + 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)28)) {
                return true;
            }
        }
        checkTile = mesh.getTile(changeTileX - 1, changeTileY - 1);
        if (Tiles.isRoadType(checkTile) && MethodsHighways.onHighway(changeTileX - 1, changeTileY - 1, true)) {
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)28)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX, changeTileY - 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX - 1, changeTileY);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
        }
        checkTile = mesh.getTile(changeTileX, changeTileY - 1);
        checkHeight = Tiles.decodeHeight(checkTile);
        if (Tiles.isRoadType(checkTile) && MethodsHighways.onHighway(changeTileX, changeTileY - 1, true)) {
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX + 1, changeTileY);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)20)) {
                return true;
            }
            checkTile = mesh.getTile(changeTileX + 1, changeTileY - 1);
            checkHeight = Tiles.decodeHeight(checkTile);
            if (checkHeightDiff(raise, myHeight, checkHeight, (short)28)) {
                return true;
            }
        }
        return false;
    }
    
    static boolean pack(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, final Action act) {
        boolean done = false;
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize || Tiles.decodeHeight(tile) < -100) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to pack the dirt here.");
            done = true;
        }
        else {
            final byte type = Tiles.decodeType(tile);
            if (isPackable(type)) {
                if (type != Tiles.Tile.TILE_DIRT_PACKED.id) {
                    done = false;
                    final Skills skills = performer.getSkills();
                    Skill paving = null;
                    Skill shovel = null;
                    try {
                        paving = skills.getSkill(10031);
                    }
                    catch (Exception ex) {
                        paving = skills.learn(10031, 1.0f);
                    }
                    Label_0208: {
                        if (performer.getPower() > 0) {
                            try {
                                shovel = skills.getSkill(source.getPrimarySkill());
                            }
                            catch (Exception ex) {
                                try {
                                    shovel = skills.learn(source.getPrimarySkill(), 1.0f);
                                }
                                catch (NoSuchSkillException nse) {
                                    if (performer.getPower() > 0) {
                                        break Label_0208;
                                    }
                                    Terraforming.logger.log(Level.WARNING, performer.getName() + " trying to pack with an item with no primary skill: " + source.getName());
                                }
                            }
                        }
                    }
                    int time = 2000;
                    if (counter == 1.0f) {
                        time = Actions.getStandardActionTime(performer, paving, source, 0.0);
                        act.setTimeLeft(time);
                        performer.getCommunicator().sendNormalServerMessage("You start to pack the ground.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to pack the ground.", performer, 5);
                        performer.sendActionControl(Actions.actionEntrys[154].getVerbString(), true, time);
                        source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                        performer.getStatus().modifyStamina(-1000.0f);
                    }
                    else {
                        time = act.getTimeLeft();
                        if (act.currentSecond() % 5 == 0) {
                            SoundPlayer.playSound("sound.work.digging.pack", tilex, tiley, onSurface, 0.0f);
                            performer.getStatus().modifyStamina(-10000.0f);
                            source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                        }
                    }
                    if (counter * 10.0f > time) {
                        if (shovel != null) {
                            shovel.skillCheck(10.0, source, 0.0, false, counter);
                        }
                        paving.skillCheck(1.0, source, 0.0, false, counter);
                        done = true;
                        final int t = Server.surfaceMesh.getTile(tilex, tiley);
                        final short h = Tiles.decodeHeight(t);
                        if (!isRockTile(Tiles.decodeType(t))) {
                            TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
                            Server.setSurfaceTile(tilex, tiley, h, Tiles.Tile.TILE_DIRT_PACKED.id, (byte)0);
                            performer.getCommunicator().sendNormalServerMessage("The dirt is packed and hard now.");
                            try {
                                final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
                                toCheckForChange.changeTile(tilex, tiley);
                                Players.getInstance().sendChangedTiles(tilex, tiley, 1, 1, onSurface, true);
                            }
                            catch (NoSuchZoneException nsz) {
                                Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The rock has been bared. No dirt remains to pack anymore.");
                        }
                    }
                }
                else {
                    done = true;
                    performer.getCommunicator().sendNormalServerMessage("The dirt is packed here already.");
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("You can't pack the dirt in that place. A " + source.getName() + " just won't do.");
            }
        }
        return done;
    }
    
    static boolean destroyPave(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter) {
        boolean done = false;
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to destroy the pavement here.");
            done = true;
        }
        else {
            if (performer.getStrengthSkill() < 20.0) {
                performer.getCommunicator().sendNormalServerMessage("You need to be stronger to destroy pavement.");
                return true;
            }
            if (Zones.protectedTiles[tilex][tiley]) {
                performer.getCommunicator().sendNormalServerMessage("Your muscles go limp and refuse. You just can't bring yourself to do this.");
                return true;
            }
            int digTile = Server.surfaceMesh.getTile(tilex, tiley);
            if (!onSurface) {
                digTile = Server.caveMesh.getTile(tilex, tiley);
            }
            final byte actualType = Tiles.decodeType(digTile);
            final byte type = (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? Server.getClientCaveFlags(tilex, tiley) : actualType;
            Action act = null;
            try {
                act = performer.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.WARNING, nsa.getMessage(), nsa);
                return true;
            }
            if (isRoad(type)) {
                final HighwayPos highwaypos = MethodsHighways.getHighwayPos(tilex, tiley, onSurface);
                if (MethodsHighways.onHighway(highwaypos)) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot remove paving next to a marker.");
                    return true;
                }
                done = false;
                final Skills skills = performer.getSkills();
                final Skill digging = skills.getSkillOrLearn(1009);
                int time = 6000;
                if (counter == 1.0f) {
                    if (digging.getRealKnowledge() < 10.0) {
                        if (type == Tiles.Tile.TILE_PLANKS.id || type == Tiles.Tile.TILE_PLANKS_TARRED.id) {
                            performer.getCommunicator().sendNormalServerMessage("You can't figure out how to remove the floor boards. You must become a bit better at digging first.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You can't figure out how to remove the stone. You must become a bit better at digging first.");
                        }
                        return true;
                    }
                    time = Actions.getDestroyActionTime(performer, digging, source, 0.0);
                    act.setTimeLeft(time);
                    if (type == Tiles.Tile.TILE_PLANKS.id || type == Tiles.Tile.TILE_PLANKS_TARRED.id) {
                        performer.getCommunicator().sendNormalServerMessage("You start to remove the floor boards.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to remove the floor boards.", performer, 5);
                    }
                    else {
                        final String fromWhere = onSurface ? "paved dirt." : ((actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? "cave exit." : "cave floor.");
                        performer.getCommunicator().sendNormalServerMessage("You start to remove the stones from the " + fromWhere);
                        Server.getInstance().broadCastAction(performer.getName() + " starts to remove the stones from the " + fromWhere, performer, 5);
                    }
                    performer.sendActionControl(Actions.actionEntrys[191].getVerbString(), true, time);
                    performer.getStatus().modifyStamina(-1500.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.currentSecond() % 5 == 0) {
                        performer.getStatus().modifyStamina(-2000.0f);
                        source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                    }
                }
                if (counter * 10.0f > time) {
                    if (digging != null) {
                        digging.skillCheck(40.0, source, 0.0, false, counter);
                    }
                    done = true;
                    final String fromWhere = onSurface ? "ground" : ((actualType == Tiles.Tile.TILE_CAVE_EXIT.id) ? "cave exit" : "cave floor");
                    final short h = Tiles.decodeHeight(digTile);
                    if (isRoad(type) || type == Tiles.Tile.TILE_PLANKS.id || type == Tiles.Tile.TILE_PLANKS_TARRED.id) {
                        TileEvent.log(tilex, tiley, performer.getLayer(), performer.getWurmId(), 191);
                        if (type == Tiles.Tile.TILE_PLANKS.id || type == Tiles.Tile.TILE_PLANKS_TARRED.id) {
                            performer.getCommunicator().sendNormalServerMessage("The " + fromWhere + " is no longer covered with planks.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The " + fromWhere + " is no longer paved with stones.");
                        }
                        if (onSurface) {
                            Server.setSurfaceTile(tilex, tiley, h, Tiles.Tile.TILE_DIRT.id, (byte)0);
                        }
                        else if (actualType == Tiles.Tile.TILE_CAVE_EXIT.id) {
                            Server.setClientCaveFlags(tilex, tiley, Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id);
                        }
                        else {
                            Server.caveMesh.setTile(tilex, tiley, Tiles.encode(h, Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id, Tiles.decodeData(digTile)));
                        }
                        performer.getMovementScheme().touchFreeMoveCounter();
                        Players.getInstance().sendChangedTile(tilex, tiley, onSurface, true);
                        try {
                            final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
                            toCheckForChange.changeTile(tilex, tiley);
                        }
                        catch (NoSuchZoneException nsz) {
                            Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
                        }
                        performer.performActionOkey(act);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The " + fromWhere + " isn't paved any longer, and your efforts are ruined.");
                    }
                }
            }
            else {
                done = true;
                if (onSurface) {
                    performer.getCommunicator().sendNormalServerMessage("The dirt isn't even paved here.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The cave floor isn't even paved here.");
                }
            }
        }
        return done;
    }
    
    static boolean pave(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, final Action act) {
        final Communicator comm = performer.getCommunicator();
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize) {
            comm.sendNormalServerMessage("The water is too deep to pave here.");
            return true;
        }
        if (Tiles.decodeHeight(tile) < -100) {
            comm.sendNormalServerMessage("The water is too deep to pave here.");
            return true;
        }
        final int digTile = Server.surfaceMesh.getTile(tilex, tiley);
        final byte type = Tiles.decodeType(digTile);
        final byte fType = Server.getClientCaveFlags(tilex, tiley);
        boolean repaving = false;
        if (Tiles.isRoadType(type) || (type == Tiles.Tile.TILE_CAVE_EXIT.id && Tiles.isRoadType(fType))) {
            repaving = true;
            final Village village = Villages.getVillageWithPerimeterAt(tilex, tiley, onSurface);
            if (village != null && !village.isActionAllowed(act.getNumber(), performer)) {
                comm.sendNormalServerMessage("You do not have permissions to do that.");
                return true;
            }
        }
        else if (type != Tiles.Tile.TILE_DIRT_PACKED.id) {
            comm.sendNormalServerMessage("The ground isn't packed here. You have to pack it first.");
            return true;
        }
        if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
            comm.sendNormalServerMessage("The amount of " + source.getName() + " is too little to pave. You may need to combine them with other " + source.getTemplate().getPlural() + ".");
            return true;
        }
        final int pavingItem = source.getTemplateId();
        final short actNumber = act.getNumber();
        if (counter == 1.0f) {
            final Skill paving = performer.getSkills().getSkillOrLearn(10031);
            final int time = Actions.getStandardActionTime(performer, paving, source, 0.0);
            act.setTimeLeft(time);
            if (pavingItem == 519) {
                comm.sendNormalServerMessage("You break up the collosus brick and start to pave with the parts.");
            }
            else if (repaving) {
                comm.sendNormalServerMessage("You start to repave with the " + source.getName() + ".");
            }
            else {
                comm.sendNormalServerMessage("You start to pave the packed dirt with the " + source.getName() + ".");
            }
            Server.getInstance().broadCastAction(performer.getName() + " starts to pave the packed dirt.", performer, 5);
            performer.sendActionControl(act.getActionEntry().getVerbString(), true, time);
            performer.getStatus().modifyStamina(-1000.0f);
            return false;
        }
        final int time2 = act.getTimeLeft();
        if (act.currentSecond() % 5 == 0) {
            performer.getStatus().modifyStamina(-10000.0f);
        }
        if (act.mayPlaySound()) {
            Methods.sendSound(performer, "sound.work.paving");
        }
        if (counter * 10.0f <= time2) {
            return false;
        }
        if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
            comm.sendNormalServerMessage("The amount of " + source.getName() + " is too little to pave. You may need to combine them with other " + source.getTemplate().getPlural() + ".");
            return true;
        }
        final int t = Server.surfaceMesh.getTile(tilex, tiley);
        final short h = Tiles.decodeHeight(t);
        if (Tiles.decodeType(t) != Tiles.Tile.TILE_DIRT_PACKED.id && !repaving) {
            comm.sendNormalServerMessage("The ground isn't fit for paving any longer, and your efforts are ruined.");
            return true;
        }
        final Skill paving2 = performer.getSkills().getSkillOrLearn(10031);
        paving2.skillCheck((pavingItem == 146) ? 5.0 : 30.0, source, 0.0, false, counter);
        TileEvent.log(tilex, tiley, 0, performer.getWurmId(), actNumber);
        final byte dir = getDiagonalDir(performer, tilex, tiley, actNumber);
        byte newTileType = 0;
        switch (pavingItem) {
            case 132: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE.id;
                break;
            }
            case 1122: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUND.id;
                break;
            }
            case 519: {
                newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUGH.id;
                break;
            }
            case 406: {
                newTileType = Tiles.Tile.TILE_STONE_SLABS.id;
                break;
            }
            case 1123: {
                newTileType = Tiles.Tile.TILE_SLATE_BRICKS.id;
                break;
            }
            case 771: {
                newTileType = Tiles.Tile.TILE_SLATE_SLABS.id;
                break;
            }
            case 1121: {
                newTileType = Tiles.Tile.TILE_SANDSTONE_BRICKS.id;
                break;
            }
            case 1124: {
                newTileType = Tiles.Tile.TILE_SANDSTONE_SLABS.id;
                break;
            }
            case 787: {
                newTileType = Tiles.Tile.TILE_MARBLE_SLABS.id;
                break;
            }
            case 786: {
                newTileType = Tiles.Tile.TILE_MARBLE_BRICKS.id;
                break;
            }
            case 776: {
                newTileType = Tiles.Tile.TILE_POTTERY_BRICKS.id;
                break;
            }
            default: {
                newTileType = Tiles.Tile.TILE_GRAVEL.id;
                break;
            }
        }
        Server.setSurfaceTile(tilex, tiley, h, newTileType, dir);
        Items.destroyItem(source.getWurmId());
        comm.sendNormalServerMessage("The ground is paved now.");
        Players.getInstance().sendChangedTiles(tilex, tiley, 1, 1, onSurface, true);
        try {
            final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
            toCheckForChange.changeTile(tilex, tiley);
        }
        catch (NoSuchZoneException ex) {}
        return true;
    }
    
    static boolean makeFloor(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter) {
        boolean done = false;
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to pave here.");
            done = true;
        }
        else if (Tiles.decodeHeight(tile) < -100) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to pave here.");
            done = true;
        }
        else if (source.getTemplateId() != 495) {
            performer.getCommunicator().sendNormalServerMessage("You need floor boards.");
            done = true;
        }
        else {
            final int digTile = Server.surfaceMesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(digTile);
            Action act = null;
            try {
                act = performer.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.WARNING, nsa.getMessage(), nsa);
                return true;
            }
            if (type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_MARSH.id) {
                done = false;
                int time = 2000;
                if (counter == 1.0f) {
                    final Skills skills = performer.getSkills();
                    Skill paving = null;
                    if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                        performer.getCommunicator().sendNormalServerMessage("The amount of planks is too little to do this. You may need to use another item.");
                        return true;
                    }
                    try {
                        paving = skills.getSkill(10031);
                    }
                    catch (Exception ex) {
                        paving = skills.learn(10031, 1.0f);
                    }
                    time = Actions.getStandardActionTime(performer, paving, source, 0.0);
                    act.setTimeLeft(time);
                    if (type == Tiles.Tile.TILE_MARSH.id) {
                        performer.getCommunicator().sendNormalServerMessage("You start to put the floorboard in the marsh.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to put the floorboard in the marsh.", performer, 5);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You start to fit the floorboard in the dirt.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to fit the floorboard in the dirt.", performer, 5);
                    }
                    performer.sendActionControl(Actions.actionEntrys[155].getVerbString(), true, time);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.currentSecond() % 5 == 0) {
                        performer.getStatus().modifyStamina(-10000.0f);
                    }
                }
                if (counter * 10.0f > time) {
                    final long parentId = source.getParentId();
                    act.setDestroyedItem(source);
                    if (parentId != -10L) {
                        try {
                            Items.getItem(parentId).dropItem(source.getWurmId(), false);
                        }
                        catch (NoSuchItemException nsi) {
                            Terraforming.logger.log(Level.INFO, performer.getName() + " tried to make floor with nonexistant floorboards.", nsi);
                        }
                    }
                    else {
                        Terraforming.logger.log(Level.WARNING, performer.getName() + " managed to pave with floorboards on ground?");
                        try {
                            final Zone zone = Zones.getZone((int)source.getPosX() >> 2, (int)source.getPosY() >> 2, source.isOnSurface());
                            zone.removeItem(source);
                        }
                        catch (NoSuchZoneException nsz) {
                            Terraforming.logger.log(Level.WARNING, performer.getName() + " failed to locate zone", nsz);
                        }
                    }
                    final Skills skills2 = performer.getSkills();
                    Skill paving2 = null;
                    try {
                        paving2 = skills2.getSkill(10031);
                    }
                    catch (Exception ex2) {
                        paving2 = skills2.learn(10031, 1.0f);
                    }
                    if (paving2 != null) {
                        paving2.skillCheck(5.0, source, 0.0, false, counter);
                    }
                    done = true;
                    final int t = Server.surfaceMesh.getTile(tilex, tiley);
                    final short h = Tiles.decodeHeight(t);
                    if (Tiles.decodeType(t) == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_MARSH.id) {
                        final byte dir = getDiagonalDir(performer, tilex, tiley, act.getNumber());
                        Server.setSurfaceTile(tilex, tiley, h, Tiles.Tile.TILE_PLANKS.id, dir);
                        if (Tiles.decodeType(t) == Tiles.Tile.TILE_MARSH.id) {
                            performer.getCommunicator().sendNormalServerMessage("You cover parts of the marsh with boards.");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The ground has some fine floor boards now.");
                        }
                        Players.getInstance().sendChangedTiles(tilex, tiley, 1, 1, onSurface, true);
                        try {
                            Items.decay(source.getWurmId(), source.getDbStrings());
                            act.setDestroyedItem(null);
                            final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
                            toCheckForChange.changeTile(tilex, tiley);
                        }
                        catch (NoSuchZoneException nsz2) {
                            Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz2);
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The ground isn't fit for floor boards any longer, and your efforts are ruined.");
                    }
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("The dirt isn't loose here. You have to cultivate it first.");
            }
        }
        return done;
    }
    
    public static final boolean switchTileTypes(final Creature performer, @NonNull final Item source, final int tilex, final int tiley, final float counter, final Action act) {
        boolean done = false;
        final int time = 50;
        final int digTile = Server.surfaceMesh.getTile(tilex, tiley);
        final byte type = Tiles.decodeType(digTile);
        final Village vill = Zones.getVillage(tilex, tiley, performer.isOnSurface());
        if (!isSwitchableTiles(source.getTemplateId(), type)) {
            performer.getCommunicator().sendNormalServerMessage("You can no longer switch here.");
            return true;
        }
        if (vill != null) {
            if (!vill.isActionAllowed((short)927, performer)) {
                return true;
            }
        }
        else {
            if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("You need to have a full weight " + source.getName() + " to switch the tile type.");
                return true;
            }
            if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize) {
                performer.getCommunicator().sendNormalServerMessage("You can't switch here.");
                return true;
            }
            if (!performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("You have to be on the surface to be able to do this.");
                return true;
            }
        }
        if (counter == 1.0f) {
            final Village v = Zones.getVillage(tilex, tiley, performer.isOnSurface());
            if (v != null && !v.isActionAllowed((short)927, performer)) {
                performer.getCommunicator().sendNormalServerMessage("You may not do that here.");
                return true;
            }
            act.setTimeLeft(50);
            performer.sendActionControl(act.getActionString(), true, act.getTimeLeft());
        }
        else if (counter * 10.0f > 50.0f && act.justTickedSecond()) {
            performer.getStatus().modifyStamina(-10000.0f);
            byte toSwitchTo = 0;
            switch (source.getTemplateId()) {
                case 26: {
                    toSwitchTo = 5;
                    break;
                }
                case 298: {
                    toSwitchTo = 1;
                    break;
                }
                default: {
                    toSwitchTo = 1;
                    Terraforming.logger.warning("Reached DEFAULT case in SWITCH. TemplateID = " + source.getTemplateId() + ". Performer = " + performer.getName());
                    break;
                }
            }
            source.setWeight(source.getWeightGrams() - source.getTemplate().getWeightGrams(), true);
            Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(digTile), toSwitchTo, (byte)0));
            Server.modifyFlagsByTileType(tilex, tiley, toSwitchTo);
            try {
                final Zone toCheckForChange = Zones.getZone(tilex, tiley, performer.isOnSurface());
                toCheckForChange.changeTile(tilex, tiley);
            }
            catch (NoSuchZoneException nsz) {
                Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, performer.isOnSurface(), true);
            done = true;
        }
        return done;
    }
    
    private static byte getDiagonalDir(final Creature performer, final int tilex, final int tiley, final short action) {
        if (action == 576 || action == 694 || action == 695) {
            final Vector2f pos = performer.getPos2f();
            final int digTilex = CoordUtils.WorldToTile(pos.x + 2.0f);
            final int digTiley = CoordUtils.WorldToTile(pos.y + 2.0f);
            if (tilex == digTilex && tiley == digTiley) {
                return Tiles.TileRoadDirection.DIR_NW.getCode();
            }
            if (tilex + 1 == digTilex && tiley == digTiley) {
                return Tiles.TileRoadDirection.DIR_NE.getCode();
            }
            if (tilex + 1 == digTilex && tiley + 1 == digTiley) {
                return Tiles.TileRoadDirection.DIR_SE.getCode();
            }
            if (tilex == digTilex && tiley + 1 == digTiley) {
                return Tiles.TileRoadDirection.DIR_SW.getCode();
            }
        }
        return 0;
    }
    
    static boolean tarFloor(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter) {
        boolean done = false;
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep here.");
            done = true;
        }
        else if (source.getTemplateId() != 153) {
            performer.getCommunicator().sendNormalServerMessage("You need tar.");
            done = true;
        }
        else if (Tiles.decodeHeight(tile) < -100) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to tar here.");
            done = true;
        }
        else {
            final MeshIO mesh = onSurface ? Server.surfaceMesh : Server.caveMesh;
            final int digTile = mesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(digTile);
            Action act = null;
            try {
                act = performer.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.WARNING, nsa.getMessage(), nsa);
                return true;
            }
            if (type == Tiles.Tile.TILE_PLANKS.id) {
                done = false;
                int time = 2000;
                if (counter == 1.0f) {
                    final Skills skills = performer.getSkills();
                    Skill paving = null;
                    if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                        performer.getCommunicator().sendNormalServerMessage("The amount of tar is too little to do this. You may need to use another item.");
                        return true;
                    }
                    try {
                        paving = skills.getSkill(10031);
                    }
                    catch (Exception ex) {
                        paving = skills.learn(10031, 1.0f);
                    }
                    time = Actions.getStandardActionTime(performer, paving, source, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start to put tar on the floorboards.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to put tar on the floorboards.", performer, 5);
                    performer.sendActionControl("tarring", true, time);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.currentSecond() % 5 == 0) {
                        performer.getStatus().modifyStamina(-10000.0f);
                    }
                }
                if (counter * 10.0f > time) {
                    final Skills skills = performer.getSkills();
                    Skill paving = null;
                    try {
                        paving = skills.getSkill(10031);
                    }
                    catch (Exception ex) {
                        paving = skills.learn(10031, 1.0f);
                    }
                    if (paving != null) {
                        paving.skillCheck(5.0, source, 0.0, false, counter);
                    }
                    done = true;
                    final int t = mesh.getTile(tilex, tiley);
                    final short h = Tiles.decodeHeight(t);
                    if (Tiles.decodeType(t) == Tiles.Tile.TILE_PLANKS.id) {
                        final byte data = Tiles.decodeData(t);
                        mesh.setTile(tilex, tiley, Tiles.encode(h, Tiles.Tile.TILE_PLANKS_TARRED.id, data));
                        performer.getCommunicator().sendNormalServerMessage("The floor boards are well protected now.");
                        TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
                        Players.getInstance().sendChangedTiles(tilex, tiley, 1, 1, onSurface, true);
                        try {
                            source.setWeight(source.getWeightGrams() - source.getTemplate().getWeightGrams(), true);
                            final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
                            toCheckForChange.changeTile(tilex, tiley);
                        }
                        catch (NoSuchZoneException nsz) {
                            Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The ground doesn't consist of floor boards any longer.");
                    }
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("The ground doesn't consist of floor boards any longer.");
            }
        }
        return done;
    }
    
    static boolean cultivate(final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter) {
        boolean done = false;
        if (tilex < 0 || tilex > 1 << Constants.meshSize || tiley < 0 || tiley > 1 << Constants.meshSize || Tiles.decodeHeight(tile) < 0) {
            performer.getCommunicator().sendNormalServerMessage("The water is too deep to cultivate here.");
            done = true;
        }
        else {
            final int digTile = Server.surfaceMesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(digTile);
            Action act = null;
            try {
                act = performer.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.WARNING, nsa.getMessage(), nsa);
                return true;
            }
            if (isCultivatable(type)) {
                done = false;
                int time = 2000;
                if (counter == 1.0f) {
                    final Skills skills = performer.getSkills();
                    Skill digging = null;
                    try {
                        digging = skills.getSkill(1009);
                    }
                    catch (Exception ex) {
                        digging = skills.learn(1009, 1.0f);
                    }
                    time = Actions.getStandardActionTime(performer, digging, source, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start to cultivate the soil.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to cultivate the soil.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[318].getVerbString(), true, time);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else {
                    time = act.getTimeLeft();
                    if (act.mayPlaySound()) {
                        performer.getStatus().modifyStamina(-10000.0f);
                        String sstring = "sound.work.digging1";
                        final int x = Server.rand.nextInt(3);
                        if (x == 0) {
                            sstring = "sound.work.digging2";
                        }
                        else if (x == 1) {
                            sstring = "sound.work.digging3";
                        }
                        Methods.sendSound(performer, sstring);
                    }
                    if (act.currentSecond() % 5 == 0) {
                        source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    }
                }
                if (counter * 10.0f > time) {
                    final Skills skills = performer.getSkills();
                    Skill digging = null;
                    try {
                        digging = skills.getSkill(1009);
                    }
                    catch (Exception ex) {
                        digging = skills.learn(1009, 1.0f);
                    }
                    if (digging != null) {
                        digging.skillCheck(14.0, source, 0.0, false, counter);
                    }
                    done = true;
                    final int t = Server.surfaceMesh.getTile(tilex, tiley);
                    final short h = Tiles.decodeHeight(t);
                    if (isCultivatable(Tiles.decodeType(t))) {
                        Server.setSurfaceTile(tilex, tiley, h, Tiles.Tile.TILE_DIRT.id, (byte)0);
                        performer.getCommunicator().sendNormalServerMessage("The ground is cultivated and ready to sow now.");
                        Players.getInstance().sendChangedTiles(tilex, tiley, 1, 1, onSurface, true);
                        try {
                            final Zone toCheckForChange = Zones.getZone(tilex, tiley, onSurface);
                            toCheckForChange.changeTile(tilex, tiley);
                        }
                        catch (NoSuchZoneException nsz) {
                            Terraforming.logger.log(Level.INFO, "no such zone?: " + tilex + ", " + tiley, nsz);
                        }
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The ground isn't fit for cultivating any longer, and your efforts are ruined.");
                    }
                }
            }
            else {
                done = true;
                performer.getCommunicator().sendNormalServerMessage("The soil isn't cultivatable here. You may have to pack it first.");
            }
        }
        return done;
    }
    
    public static boolean allCornersAtRockLevel(final int tilex, final int tiley, final MeshIO mesh) {
        int numberOfCornersAtRockHeight = 0;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                final int tile = mesh.getTile(tilex + x, tiley + y);
                final short tileHeight = Tiles.decodeHeight(tile);
                final int rockTile = Server.rockMesh.getTile(tilex + x, tiley + y);
                final short rockHeight = Tiles.decodeHeight(rockTile);
                if (tileHeight <= rockHeight) {
                    ++numberOfCornersAtRockHeight;
                }
            }
        }
        return numberOfCornersAtRockHeight == 4;
    }
    
    public static void setAsRock(final int tilex, final int tiley, final boolean natural) {
        setAsRock(tilex, tiley, natural, false);
    }
    
    public static void setAsRock(final int tilex, final int tiley, final boolean natural, final boolean lavaflow) {
        MethodsHighways.removeNearbyMarkers(tilex, tiley, false);
        boolean keepTopLeftHeight = false;
        boolean keepTopRightHeight = false;
        boolean keepLowerLeftHeight = false;
        boolean keepLowerRightHeight = false;
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                final int t = Server.caveMesh.getTile(tilex + x, tiley + y);
                final byte type = Tiles.decodeType(t);
                if (x == 0 && y == 0) {
                    if (type == Tiles.Tile.TILE_CAVE_EXIT.id) {
                        if (lavaflow) {
                            Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_LAVA.id, (byte)0);
                            Server.rockMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_CAVE_WALL_LAVA.id, (byte)0));
                            Server.setWorldResource(tilex, tiley, 0);
                            Server.setCaveResource(tilex, tiley, Server.rand.nextInt(10000));
                        }
                        else {
                            Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_ROCK.id, (byte)0);
                            Server.rockMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_ROCK.id, (byte)0));
                            Server.setWorldResource(tilex, tiley, 0);
                            Server.setCaveResource(tilex, tiley, Server.rand.nextInt(10000));
                        }
                        MineDoorPermission.deleteMineDoor(x, y);
                    }
                }
                else if (type == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(type) || type == Tiles.Tile.TILE_CAVE_EXIT.id) {
                    if (x == -1 && y == -1) {
                        keepTopLeftHeight = true;
                    }
                    else if (x == 0 && y == -1) {
                        keepTopRightHeight = true;
                        keepTopLeftHeight = true;
                    }
                    else if (x == 1 && y == -1) {
                        keepTopRightHeight = true;
                    }
                    else if (x == -1 && y == 0) {
                        keepTopLeftHeight = true;
                        keepLowerLeftHeight = true;
                    }
                    else if (x == 1 && y == 0) {
                        keepTopRightHeight = true;
                        keepLowerRightHeight = true;
                    }
                    else if (x == -1 && y == 1) {
                        keepLowerLeftHeight = true;
                    }
                    else if (x == 0 && y == 1) {
                        keepLowerRightHeight = true;
                        keepLowerLeftHeight = true;
                    }
                    else if (x == 1 && y == 1) {
                        keepLowerRightHeight = true;
                    }
                }
            }
        }
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                final int encodedTile = Server.caveMesh.getTile(tilex + x, tiley + y);
                if (x == 0 && y == 0) {
                    byte tileType = Tiles.Tile.TILE_CAVE_WALL.id;
                    if (lavaflow || (Tiles.decodeType(Server.surfaceMesh.getTile(tilex + x, tiley + y)) == Tiles.Tile.TILE_LAVA.id && (Tiles.decodeData(Server.surfaceMesh.getTile(tilex + x, tiley + y)) & 0xFF) == 0xFF)) {
                        tileType = Tiles.Tile.TILE_CAVE_WALL_LAVA.id;
                    }
                    else if (natural) {
                        tileType = TileRockBehaviour.prospect(tilex + x, tiley + y, false);
                    }
                    if (keepTopLeftHeight) {
                        Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode(Tiles.decodeHeight(encodedTile), tileType, Tiles.decodeData(encodedTile)));
                    }
                    else {
                        Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), tileType, (byte)0));
                    }
                }
                else if (x == 1 && y == 0) {
                    if (!keepTopRightHeight) {
                        Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(Server.caveMesh.getTile(tilex + x, tiley + y)), (byte)0));
                    }
                }
                else if (x == 0 && y == 1) {
                    if (!keepLowerLeftHeight) {
                        Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(Server.caveMesh.getTile(tilex + x, tiley + y)), (byte)0));
                    }
                }
                else if (x == 1 && y == 1 && !keepLowerRightHeight) {
                    Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(Server.caveMesh.getTile(tilex + x, tiley + y)), (byte)0));
                }
            }
        }
        Players.getInstance().sendChangedTiles(tilex, tiley, 2, 2, true, true);
        Players.getInstance().sendChangedTiles(tilex - 1, tiley - 1, 3, 3, false, true);
        Server.setCaveResource(tilex, tiley, 65535);
        byte block = 0;
        Terraforming.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 102533L);
        if (Terraforming.r.nextInt(100) == 0) {
            block = -1;
        }
        else {
            Terraforming.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 123307L);
            if (Terraforming.r.nextInt(64) == 0) {
                block = -1;
            }
        }
        Zones.setMiningState(tilex, tiley, block, false);
        Zones.deleteMiningTile(tilex, tiley);
    }
    
    public static void forceSetAsRock(final int tilex, final int tiley, final byte suggestedTile, int suggestedTileSeed) {
        suggestedTileSeed = Math.max(suggestedTileSeed, 1);
        final int surfaceTile = Server.surfaceMesh.getTile(tilex, tiley);
        final boolean isLava = Tiles.decodeType(surfaceTile) == Tiles.Tile.TILE_LAVA.id;
        final int initialTile = Server.caveMesh.getTile(tilex, tiley);
        final byte oldTType = Tiles.decodeType(initialTile);
        byte newTileType = Tiles.isOreCave(oldTType) ? oldTType : Tiles.Tile.TILE_CAVE_WALL.id;
        if (isLava) {
            newTileType = Tiles.Tile.TILE_CAVE_WALL_LAVA.id;
        }
        else if (suggestedTile > 0 && Server.rand.nextInt(suggestedTileSeed) == 0) {
            Terraforming.logger.log(Level.INFO, "Setting " + tilex + "," + tiley + " to suggested " + Tiles.getTile(suggestedTile).tiledesc);
            newTileType = suggestedTile;
        }
        final boolean changed = newTileType != oldTType;
        if (changed) {
            boolean keepTopLeftHeight = false;
            boolean keepTopRightHeight = false;
            boolean keepLowerLeftHeight = false;
            boolean keepLowerRightHeight = false;
            for (int x = -1; x <= 1; ++x) {
                for (int y = -1; y <= 1; ++y) {
                    final int t = Server.caveMesh.getTile(tilex + x, tiley + y);
                    final byte type = Tiles.decodeType(t);
                    if ((x != 0 || y != 0) && (type == Tiles.Tile.TILE_CAVE.id || type == Tiles.Tile.TILE_CAVE_EXIT.id || Tiles.isReinforcedFloor(type))) {
                        if (x == -1 && y == -1) {
                            keepTopLeftHeight = true;
                        }
                        else if (x == 0 && y == -1) {
                            keepTopRightHeight = true;
                            keepTopLeftHeight = true;
                        }
                        else if (x == 1 && y == -1) {
                            keepTopRightHeight = true;
                        }
                        else if (x == -1 && y == 0) {
                            keepTopLeftHeight = true;
                            keepLowerLeftHeight = true;
                        }
                        else if (x == 1 && y == 0) {
                            keepTopRightHeight = true;
                            keepLowerRightHeight = true;
                        }
                        else if (x == -1 && y == 1) {
                            keepLowerLeftHeight = true;
                        }
                        else if (x == 0 && y == 1) {
                            keepLowerRightHeight = true;
                            keepLowerLeftHeight = true;
                        }
                        else if (x == 1 && y == 1) {
                            keepLowerRightHeight = true;
                        }
                    }
                }
            }
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    final int encodedTile = Server.caveMesh.getTile(tilex + x, tiley + y);
                    boolean send = false;
                    if (x == 0 && y == 0) {
                        if (keepTopLeftHeight) {
                            if (Tiles.decodeType(encodedTile) != newTileType) {
                                Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode(Tiles.decodeHeight(encodedTile), newTileType, Tiles.decodeData(encodedTile)));
                                send = true;
                            }
                        }
                        else if (Tiles.decodeHeight(encodedTile) != -100 || Tiles.decodeType(encodedTile) != newTileType || Tiles.decodeData(encodedTile) != 0) {
                            Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), newTileType, (byte)0));
                            send = true;
                        }
                    }
                    else if (x == 1 && y == 0) {
                        if (!keepTopRightHeight && (Tiles.decodeHeight(encodedTile) != -100 || Tiles.decodeData(encodedTile) != 0)) {
                            Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(encodedTile), (byte)0));
                            send = true;
                        }
                    }
                    else if (x == 0 && y == 1) {
                        if (!keepLowerLeftHeight && (Tiles.decodeHeight(encodedTile) != -100 || Tiles.decodeData(encodedTile) != 0)) {
                            Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(encodedTile), (byte)0));
                            send = true;
                        }
                    }
                    else if (x == 1 && y == 1 && !keepLowerRightHeight && (Tiles.decodeHeight(encodedTile) != -100 || Tiles.decodeData(encodedTile) != 0)) {
                        Server.caveMesh.setTile(tilex + x, tiley + y, Tiles.encode((short)(-100), Tiles.decodeType(encodedTile), (byte)0));
                        send = true;
                    }
                    if (send) {
                        Players.getInstance().sendChangedTile(tilex + x, tiley + y, false, true);
                    }
                }
            }
            Server.setCaveResource(tilex, tiley, 65535);
            Zones.setMiningState(tilex, tiley, (byte)(-1), false);
            Zones.deleteMiningTile(tilex, tiley);
        }
    }
    
    public static void caveIn(final int tilex, final int tiley) {
        setAsRock(tilex, tiley, true);
    }
    
    public static boolean isAllCornersInsideHeightRange(final int tilex, final int tiley, final boolean surfaced, final short maxheight, final short minheight) {
        if (surfaced) {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                        return true;
                    }
                    final short h = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                    if (minheight <= h && h <= maxheight) {
                        return true;
                    }
                }
            }
        }
        else {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                        return true;
                    }
                    final short h = Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y));
                    if (minheight <= h || h <= maxheight) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isCornerUnderWater(final int tilex, final int tiley, final boolean surfaced) {
        if (surfaced) {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                        return true;
                    }
                    final short h = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                    if (h <= 0) {
                        return true;
                    }
                }
            }
        }
        else {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                        return true;
                    }
                    final short h = Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y));
                    if (h <= 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isTileUnderWater(final int tile, final int tilex, final int tiley, final boolean surfaced) {
        if (surfaced) {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                        return true;
                    }
                    if (Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y)) > -0.5) {
                        return false;
                    }
                }
            }
        }
        else {
            if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                return false;
            }
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y)) > -0.5) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    static final boolean isWater(final int tile, final int tilex, final int tiley, final boolean surfaced) {
        if (surfaced) {
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    if (Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y)) < 0) {
                        return true;
                    }
                }
            }
        }
        else {
            if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                return false;
            }
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    final int ttile = Server.caveMesh.getTile(tilex + x, tiley + y);
                    if (Tiles.decodeHeight(ttile) < 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isFlat(final int tilex, final int tiley, final boolean surfaced, final int maxDifference) throws IllegalArgumentException {
        int lAverageHeight = 0;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                if (tilex + x < 0 || tilex + x > 1 << Constants.meshSize || tiley + y < 0 || tiley + y > 1 << Constants.meshSize) {
                    throw new IllegalArgumentException("This tile is at the end of the world. Don't flatten it.");
                }
                short ch = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                if (!surfaced) {
                    ch = Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y));
                }
                lAverageHeight += ch;
            }
        }
        lAverageHeight = (short)(lAverageHeight / 4);
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                short h = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                if (!surfaced) {
                    h = Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y));
                }
                if (h > lAverageHeight + maxDifference) {
                    return false;
                }
                if (h < lAverageHeight - maxDifference) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static boolean plantSprout(final Creature performer, final Item sprout, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, final boolean inCenter) {
        boolean toReturn = true;
        if (sprout.getTemplateId() == 266) {
            if (!onSurface) {
                performer.getCommunicator().sendNormalServerMessage("The sprout would never grow inside a cave.");
                return true;
            }
            final byte type = Tiles.decodeType(tile);
            if (isTileGrowTree(type)) {
                if (!Methods.isActionAllowed(performer, (short)660, tilex, tiley)) {
                    return true;
                }
                final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, onSurface);
                if (vtile != null && vtile.getStructure() != null) {
                    if (vtile.getStructure().isTypeHouse()) {
                        performer.getCommunicator().sendNormalServerMessage("The sprout would never grow inside a house.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("The sprout would never grow under a bridge.");
                    }
                    return true;
                }
                try {
                    final Action act = performer.getCurrentAction();
                    double power = 0.0;
                    int time = 2000;
                    toReturn = false;
                    if (counter == 1.0f) {
                        Skill gardening = null;
                        try {
                            gardening = performer.getSkills().getSkill(10045);
                        }
                        catch (NoSuchSkillException nss) {
                            gardening = performer.getSkills().learn(10045, 1.0f);
                        }
                        if (isCornerUnderWater(tilex, tiley, onSurface)) {
                            performer.getCommunicator().sendNormalServerMessage("The ground is too moist here, so the sprout would rot.");
                            return true;
                        }
                        time = Actions.getStandardActionTime(performer, gardening, sprout, 0.0);
                        act.setTimeLeft(time);
                        performer.getCommunicator().sendNormalServerMessage("You start planting the sprout.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to plant a sprout.", performer, 5);
                        performer.sendActionControl(Actions.actionEntrys[186].getVerbString(), true, time);
                    }
                    else {
                        time = act.getTimeLeft();
                    }
                    if (counter * 10.0f > time) {
                        Skill gardening = null;
                        try {
                            gardening = performer.getSkills().getSkill(10045);
                        }
                        catch (NoSuchSkillException nss) {
                            gardening = performer.getSkills().learn(10045, 1.0f);
                        }
                        power = gardening.skillCheck(1.0f + sprout.getDamage(), sprout.getCurrentQualityLevel(), false, counter);
                        toReturn = true;
                        if (power > 0.0 && sprout.getMaterial() != 92) {
                            SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, onSurface, 0.0f);
                            final TreeData.TreeType treeType = Materials.getTreeTypeForWood(sprout.getMaterial());
                            if (treeType != null) {
                                final int newData = Tiles.encodeTreeData(FoliageAge.YOUNG_ONE, false, inCenter, GrassData.GrowthTreeStage.SHORT);
                                if (type == Tiles.Tile.TILE_MYCELIUM.id) {
                                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), treeType.asMyceliumTree(), (byte)newData);
                                }
                                else {
                                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), treeType.asNormalTree(), (byte)newData);
                                }
                            }
                            else {
                                final BushData.BushType bushType = Materials.getBushTypeForWood(sprout.getMaterial());
                                final int newData2 = Tiles.encodeTreeData(FoliageAge.YOUNG_ONE, false, inCenter, GrassData.GrowthTreeStage.SHORT);
                                if (type == Tiles.Tile.TILE_MYCELIUM.id) {
                                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), bushType.asMyceliumBush(), (byte)newData2);
                                }
                                else {
                                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), bushType.asNormalBush(), (byte)newData2);
                                }
                            }
                            Server.setWorldResource(tilex, tiley, 0);
                            performer.getMovementScheme().touchFreeMoveCounter();
                            Players.getInstance().sendChangedTile(tilex, tiley, onSurface, true);
                            if (performer.getDeity() != null && performer.getDeity().number == 1) {
                                performer.maybeModifyAlignment(1.0f);
                            }
                            String tosend = "You plant the sprout.";
                            performer.achievement(119);
                            final double gard = gardening.getKnowledge(0.0);
                            if (gard > 50.0) {
                                if (gard < 60.0) {
                                    tosend = "You plant the sprout, and you can almost feel it start sucking nutrition from the earth.";
                                }
                                else if (gard < 70.0) {
                                    tosend = "You plant the sprout, and you get a weird feeling that the plant thanks you.";
                                }
                                else if (gard < 80.0) {
                                    tosend = "You plant the sprout, and you see the plant perform an almost unnoticable bow as it whispers its thanks.";
                                }
                                else if (gard < 100.0) {
                                    tosend = "You plant the sprout. As you see the plant bow you hear the voice in your head of hundreds of plants thanking you.";
                                }
                            }
                            performer.getStatus().modifyStamina(-1000.0f);
                            performer.getCommunicator().sendNormalServerMessage(tosend);
                            Server.getInstance().broadCastAction(performer.getName() + " plants a sprout.", performer, 5);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("Sadly, the sprout does not survive despite your best efforts.");
                        }
                        Items.destroyItem(sprout.getWurmId());
                    }
                }
                catch (NoSuchActionException nsa) {
                    Terraforming.logger.log(Level.WARNING, performer.getName() + ": " + nsa.getMessage(), nsa);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot plant a tree there.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You need to plant with a sprout, not a " + sprout.getName() + ".");
        }
        return toReturn;
    }
    
    static final boolean createMagicWall(final Creature performer, final Item source, final int tilex, final int tiley, final int heightOffset, final boolean onSurface, final Tiles.TileBorderDirection dir, final float counter, final Action act) {
        boolean toReturn = true;
        if (source.getTemplateId() == 764) {
            if (source.getWeightGrams() < 1000) {
                performer.getCommunicator().sendNormalServerMessage("There is too little " + source.getName() + ". You probably will need at least " + 1000 + " g.");
                return true;
            }
            if (!onSurface) {
                performer.getCommunicator().sendNormalServerMessage("You can not reach that.");
                return true;
            }
            final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, onSurface);
            if (vtile != null && vtile.getStructure() != null) {
                performer.getCommunicator().sendNormalServerMessage("The source would never work inside.");
                return true;
            }
            double power = 0.0;
            int time = 2000;
            toReturn = false;
            if (counter == 1.0f) {
                final VolaTile t = Zones.getTileOrNull(tilex, tiley, onSurface);
                if (t != null) {
                    final Wall[] wallsForLevel;
                    final Wall[] walls = wallsForLevel = t.getWallsForLevel(heightOffset / 30);
                    for (final Wall wall : wallsForLevel) {
                        if (wall.isHorizontal() == (dir == Tiles.TileBorderDirection.DIR_HORIZ) && wall.getStartX() == tilex && wall.getStartY() == tiley) {
                            return true;
                        }
                    }
                    final Fence[] fencesForDir;
                    final Fence[] fences = fencesForDir = t.getFencesForDir(dir);
                    for (final Fence f : fencesForDir) {
                        if (f.getHeightOffset() == heightOffset) {
                            return true;
                        }
                    }
                }
                if (dir == Tiles.TileBorderDirection.DIR_DOWN) {
                    final VolaTile t2 = Zones.getTileOrNull(tilex, tiley, onSurface);
                    if (t2 != null) {
                        for (final Creature c : t2.getCreatures()) {
                            if (c.isPlayer()) {
                                return true;
                            }
                        }
                    }
                    final VolaTile t3 = Zones.getTileOrNull(tilex - 1, tiley, onSurface);
                    if (t3 != null) {
                        for (final Creature c2 : t3.getCreatures()) {
                            if (c2.isPlayer()) {
                                return true;
                            }
                        }
                    }
                }
                else {
                    final VolaTile t2 = Zones.getTileOrNull(tilex, tiley, onSurface);
                    if (t2 != null) {
                        for (final Creature c : t2.getCreatures()) {
                            if (c.isPlayer()) {
                                return false;
                            }
                        }
                    }
                    final VolaTile t3 = Zones.getTileOrNull(tilex, tiley - 1, onSurface);
                    if (t3 != null) {
                        for (final Creature c2 : t3.getCreatures()) {
                            if (c2.isPlayer()) {
                                return false;
                            }
                        }
                    }
                }
                Skill mind = null;
                try {
                    mind = performer.getSkills().getSkill(100);
                }
                catch (NoSuchSkillException nss) {
                    mind = performer.getSkills().learn(100, 1.0f);
                }
                time = Actions.getQuickActionTime(performer, mind, source, 0.0);
                act.setTimeLeft(time);
                performer.getCommunicator().sendNormalServerMessage("You start to weave the source.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to weave the source.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[512].getVerbString(), true, time);
            }
            else {
                time = act.getTimeLeft();
            }
            if (counter * 10.0f > time) {
                Skill mind2 = null;
                try {
                    mind2 = performer.getSkills().getSkill(100);
                }
                catch (NoSuchSkillException nss2) {
                    mind2 = performer.getSkills().learn(100, 1.0f);
                }
                power = mind2.skillCheck(mind2.getRealKnowledge(), source.getCurrentQualityLevel(), false, counter);
                toReturn = true;
                if (power > 0.0) {
                    SoundPlayer.playSound("sound.religion.channel", tilex, tiley, onSurface, 0.0f);
                    try {
                        final Zone zone = Zones.getZone(tilex, tiley, true);
                        final Fence fence = new DbFence(StructureConstantsEnum.FENCE_MAGIC_STONE, tilex, tiley, heightOffset, 1.0f, dir, zone.getId(), performer.getLayer());
                        fence.setState(fence.getFinishState());
                        fence.setQualityLevel((float)power);
                        fence.improveOrigQualityLevel((float)power);
                        zone.addFence(fence);
                        performer.achievement(320);
                        performer.getCommunicator().sendNormalServerMessage("You weave the source and create a wall.");
                        Server.getInstance().broadCastAction(performer.getName() + " creates a wall.", performer, 5);
                    }
                    catch (NoSuchZoneException ex) {}
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("Sadly, you fail to weave the source properly.");
                }
                source.setWeight(source.getWeightGrams() - 1000, true);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You need to use the source, not a " + source.getName() + ".");
        }
        return toReturn;
    }
    
    static final boolean plantFlowerbed(final Creature performer, final Item flower, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final float counter, final Action act) {
        boolean toReturn = true;
        if (flower.isFlower()) {
            if (!onSurface) {
                performer.getCommunicator().sendNormalServerMessage("You can not reach that.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            final int tile = Server.surfaceMesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(tile);
            int diffx = 0;
            int diffy = 0;
            if (dir == Tiles.TileBorderDirection.DIR_DOWN) {
                diffx = -1;
            }
            else {
                diffy = -1;
            }
            final int tile2 = Server.surfaceMesh.getTile(tilex + diffx, tiley + diffy);
            final byte type2 = Tiles.decodeType(tile2);
            if (isTileGrowHedge(type) || isTileGrowHedge(type2)) {
                final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, onSurface);
                if (vtile != null && vtile.getStructure() != null) {
                    performer.getCommunicator().sendNormalServerMessage("The flowers would never grow inside.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                double power = 0.0;
                int time = 2000;
                toReturn = false;
                if (counter == 1.0f) {
                    final StructureConstantsEnum fenceType = Fence.getFlowerbedType(flower.getTemplateId());
                    if (fenceType == StructureConstantsEnum.FENCE_PLAN_WOODEN) {
                        performer.getCommunicator().sendNormalServerMessage("Nobody has managed to grow those in flowerbeds yet.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    int found = 0;
                    boolean flowersFound = false;
                    boolean planksFound = false;
                    boolean nailsFound = false;
                    boolean dirtFound = false;
                    int plankCount = 0;
                    final Item[] allItems;
                    final Item[] inventoryItems = allItems = performer.getInventory().getAllItems(false);
                    for (final Item item : allItems) {
                        if (item.getTemplateId() == flower.getTemplateId() && !flowersFound) {
                            if (item != flower && ++found >= 4) {
                                flowersFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 22 && !planksFound) {
                            if (++plankCount >= 3) {
                                planksFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 26 && !dirtFound) {
                            if (item.getWeightGrams() >= item.getTemplate().getWeightGrams()) {
                                dirtFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 218 && !nailsFound) {
                            nailsFound = true;
                        }
                        if (flowersFound && planksFound && nailsFound && dirtFound) {
                            break;
                        }
                    }
                    if (!flowersFound || !planksFound || !nailsFound || !dirtFound) {
                        performer.getCommunicator().sendNormalServerMessage("You need to have at least 5 flowers of the same kind and 3 planks, 1 small nails and atleast 20kg of dirt in your inventory.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    Skill gardening = null;
                    try {
                        gardening = performer.getSkills().getSkill(10045);
                    }
                    catch (NoSuchSkillException nss) {
                        gardening = performer.getSkills().learn(10045, 1.0f);
                    }
                    if (isCornerUnderWater(tilex, tiley, onSurface)) {
                        performer.getCommunicator().sendNormalServerMessage("The ground is too moist here, so the flowers would rot.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    time = Actions.getStandardActionTime(performer, gardening, flower, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start planting the flowers.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to plant some flowers.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[563].getVerbString(), true, time);
                }
                else {
                    time = act.getTimeLeft();
                }
                if (counter * 10.0f > time) {
                    final StructureConstantsEnum fenceType = Fence.getFlowerbedType(flower.getTemplateId());
                    if (fenceType == StructureConstantsEnum.FENCE_PLAN_WOODEN) {
                        performer.getCommunicator().sendNormalServerMessage("Nobody has managed to grow those in flowerbeds yet.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    int found = 0;
                    boolean flowersFound = false;
                    boolean planksFound = false;
                    boolean nailsFound = false;
                    boolean dirtFound = false;
                    int plankCount = 0;
                    final Item[] allItems2;
                    final Item[] inventoryItems = allItems2 = performer.getInventory().getAllItems(false);
                    for (final Item item : allItems2) {
                        if (item.getTemplateId() == flower.getTemplateId() && !flowersFound) {
                            if (item != flower && ++found >= 4) {
                                flowersFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 22 && !planksFound) {
                            if (++plankCount >= 3) {
                                planksFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 26 && !dirtFound) {
                            if (item.getWeightGrams() >= item.getTemplate().getWeightGrams()) {
                                dirtFound = true;
                            }
                        }
                        else if (item.getTemplateId() == 218 && !nailsFound) {
                            nailsFound = true;
                        }
                        if (flowersFound && planksFound && nailsFound && dirtFound) {
                            break;
                        }
                    }
                    if (found < 4) {
                        performer.getCommunicator().sendNormalServerMessage("You need to have at least 5 flowers of the same kind and 3 planks, 1 small nails and atleast 20kg of dirt in your inventory.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    float ql = flower.getQualityLevel();
                    float dam = flower.getDamage();
                    boolean flowersDone = false;
                    boolean dirtDone = false;
                    boolean planksDone = false;
                    boolean nailsDone = false;
                    for (final Item item2 : inventoryItems) {
                        if (item2.getTemplateId() == flower.getTemplateId() && !flowersDone) {
                            if (item2 != flower && found > 0) {
                                ql += item2.getQualityLevel();
                                dam += item2.getDamage();
                                if (--found <= 0) {
                                    flowersDone = true;
                                }
                            }
                        }
                        else if (item2.getTemplateId() == 22 && !planksDone) {
                            if (plankCount > 0) {
                                ql += item2.getQualityLevel();
                                dam += item2.getDamage();
                                if (--plankCount <= 0) {
                                    planksDone = true;
                                }
                            }
                        }
                        else if (item2.getTemplateId() == 218 && !nailsDone) {
                            ql += item2.getQualityLevel();
                            dam += item2.getDamage();
                            nailsDone = true;
                        }
                        else if (item2.getTemplateId() == 26 && !dirtDone) {
                            ql += item2.getQualityLevel();
                            dam += item2.getDamage();
                            dirtDone = true;
                        }
                        if (flowersDone && dirtDone && planksDone && nailsDone) {
                            break;
                        }
                    }
                    ql /= 10.0f;
                    dam /= 10.0f;
                    Skill gardening2 = null;
                    try {
                        gardening2 = performer.getSkills().getSkill(10045);
                    }
                    catch (NoSuchSkillException nss2) {
                        gardening2 = performer.getSkills().learn(10045, 1.0f);
                    }
                    power = gardening2.skillCheck(1.0f + dam, ql, false, counter);
                    toReturn = true;
                    if (power > 0.0) {
                        SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, onSurface, 0.0f);
                        try {
                            final Zone zone = Zones.getZone(tilex, tiley, true);
                            final Fence fence = new DbFence(Fence.getFlowerbedType(flower.getTemplateId()), tilex, tiley, 0, 1.0f, dir, zone.getId(), performer.getLayer());
                            try {
                                fence.setState(fence.getFinishState());
                                fence.setQualityLevel((float)power);
                                fence.improveOrigQualityLevel((float)power);
                                fence.save();
                                zone.addFence(fence);
                            }
                            catch (IOException iox) {
                                Terraforming.logger.log(Level.WARNING, iox.getMessage(), iox);
                            }
                            if (performer.getDeity() != null && performer.getDeity().number == 1) {
                                performer.maybeModifyAlignment(1.0f);
                            }
                            found = 4;
                            plankCount = 3;
                            dirtDone = false;
                            planksDone = false;
                            flowersDone = false;
                            nailsDone = false;
                            for (final Item item3 : inventoryItems) {
                                if (item3.getTemplateId() == flower.getTemplateId() && !flowersDone) {
                                    if (item3 != flower && found > 0) {
                                        Items.destroyItem(item3.getWurmId());
                                        if (--found <= 0) {
                                            flowersDone = true;
                                        }
                                    }
                                }
                                else if (item3.getTemplateId() == 26 && !dirtDone) {
                                    Items.destroyItem(item3.getWurmId());
                                    dirtDone = true;
                                }
                                else if (item3.getTemplateId() == 22 && !planksDone) {
                                    Items.destroyItem(item3.getWurmId());
                                    if (--plankCount <= 0) {
                                        planksDone = true;
                                    }
                                }
                                else if (item3.getTemplateId() == 218 && !nailsDone) {
                                    Items.destroyItem(item3.getWurmId());
                                    nailsDone = true;
                                }
                                if (flowersDone && dirtDone && planksDone && nailsDone) {
                                    break;
                                }
                            }
                            String tosend = "You plant the flowers and create a fine flowerbed.";
                            performer.achievement(318);
                            final double gard = gardening2.getKnowledge(0.0);
                            if (gard > 50.0 && Server.rand.nextBoolean()) {
                                if (gard < 60.0) {
                                    tosend = "You plant the flowerbed, and you can almost feel the plants start sucking nutrition from the earth.";
                                }
                                else if (gard < 70.0) {
                                    tosend = "You plant the flowerbed, and you get a weird feeling that the plants thanks you.";
                                }
                                else if (gard < 80.0) {
                                    tosend = "You plant the flowerbed, and you see the plants perform an almost unnoticable bow as they whisper their thanks.";
                                }
                                else if (gard < 100.0) {
                                    tosend = "You plant the flowerbed. As you see the plants bow you hear the voice in your head of hundreds of other plants thanking you.";
                                    performer.getStatus().modifyStamina(-1000.0f);
                                }
                            }
                            TileEvent.log(fence.getTileX(), fence.getTileY(), 0, performer.getWurmId(), 563);
                            performer.getCommunicator().sendNormalServerMessage(tosend);
                            Server.getInstance().broadCastAction(performer.getName() + " plants a flowerbed.", performer, 5);
                            performer.getCommunicator().sendActionResult(true);
                        }
                        catch (NoSuchZoneException ex) {}
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Sadly, the flowers do not survive despite your best efforts.");
                        performer.getCommunicator().sendActionResult(false);
                    }
                    Items.destroyItem(flower.getWurmId());
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot plant a flowerbed there.");
                performer.getCommunicator().sendActionResult(false);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You need to plant with a flower, not a " + flower.getName() + ".");
            performer.getCommunicator().sendActionResult(false);
        }
        return toReturn;
    }
    
    static final boolean plantHedge(final Creature performer, final Item sprout, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final float counter, final Action act) {
        boolean toReturn = true;
        if (sprout.getTemplateId() == 266) {
            if (!onSurface || !performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("The hedge would never grow inside a cave.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            final int tile = Server.surfaceMesh.getTile(tilex, tiley);
            final byte type = Tiles.decodeType(tile);
            int diffx = 0;
            int diffy = 0;
            if (dir == Tiles.TileBorderDirection.DIR_DOWN) {
                diffx = -1;
            }
            else {
                diffy = -1;
            }
            final int tile2 = Server.surfaceMesh.getTile(tilex + diffx, tiley + diffy);
            final byte type2 = Tiles.decodeType(tile2);
            if (isTileGrowHedge(type) || isTileGrowHedge(type2)) {
                if (!Methods.isActionAllowed(performer, (short)660, tilex, tiley)) {
                    return true;
                }
                final byte treeMaterial = sprout.getMaterial();
                double power = 0.0;
                int time = 2000;
                toReturn = false;
                if (counter == 1.0f) {
                    final StructureConstantsEnum fenceType = Fence.getLowHedgeType(treeMaterial);
                    if (fenceType == StructureConstantsEnum.FENCE_PLAN_WOODEN) {
                        performer.getCommunicator().sendNormalServerMessage("Nobody has managed to grow those in hedges yet.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    int found = 0;
                    final Item[] allItems;
                    final Item[] inventoryItems = allItems = performer.getInventory().getAllItems(false);
                    for (final Item item : allItems) {
                        if (item.getTemplateId() == 266 && item != sprout && item.getMaterial() == treeMaterial && ++found >= 4) {
                            break;
                        }
                    }
                    if (found < 4) {
                        performer.getCommunicator().sendNormalServerMessage("You need to have at least 5 sprouts of the same kind in your inventory.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    Skill gardening = null;
                    try {
                        gardening = performer.getSkills().getSkill(10045);
                    }
                    catch (NoSuchSkillException nss) {
                        gardening = performer.getSkills().learn(10045, 1.0f);
                    }
                    if (isCornerUnderWater(tilex, tiley, onSurface)) {
                        performer.getCommunicator().sendNormalServerMessage("The ground is too moist here, so the sprout would rot.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    time = Actions.getStandardActionTime(performer, gardening, sprout, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start planting the sprout.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to plant a sprout.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[186].getVerbString(), true, time);
                }
                else {
                    time = act.getTimeLeft();
                }
                if (counter * 10.0f > time) {
                    final StructureConstantsEnum fenceType = Fence.getLowHedgeType(treeMaterial);
                    if (fenceType == StructureConstantsEnum.FENCE_PLAN_WOODEN) {
                        performer.getCommunicator().sendNormalServerMessage("Nobody has managed to grow those in hedges yet.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    int found = 0;
                    final Item[] allItems2;
                    final Item[] inventoryItems = allItems2 = performer.getInventory().getAllItems(false);
                    for (final Item item : allItems2) {
                        if (item.getTemplateId() == 266 && item != sprout && item.getMaterial() == treeMaterial && ++found >= 4) {
                            break;
                        }
                    }
                    if (found < 4) {
                        performer.getCommunicator().sendNormalServerMessage("You need to have at least 5 sprouts of the same kind in your inventory.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    float ql = sprout.getQualityLevel();
                    float dam = sprout.getDamage();
                    for (final Item item2 : inventoryItems) {
                        if (item2.getTemplateId() == 266 && item2 != sprout && found > 0 && item2.getMaterial() == treeMaterial) {
                            ql += item2.getQualityLevel();
                            dam += item2.getDamage();
                            if (--found <= 0) {
                                break;
                            }
                        }
                    }
                    ql /= 5.0f;
                    dam /= 5.0f;
                    Skill gardening2 = null;
                    try {
                        gardening2 = performer.getSkills().getSkill(10045);
                    }
                    catch (NoSuchSkillException nss2) {
                        gardening2 = performer.getSkills().learn(10045, 1.0f);
                    }
                    power = gardening2.skillCheck(1.0f + dam, ql, false, counter);
                    toReturn = true;
                    if (power > 0.0) {
                        SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, onSurface, 0.0f);
                        try {
                            final Zone zone = Zones.getZone(tilex, tiley, true);
                            final Fence fence = new DbFence(Fence.getLowHedgeType(treeMaterial), tilex, tiley, 0, 1.0f, dir, zone.getId(), performer.getLayer());
                            try {
                                fence.setState(fence.getFinishState());
                                fence.setQualityLevel((float)power);
                                fence.improveOrigQualityLevel((float)power);
                                fence.save();
                                zone.addFence(fence);
                            }
                            catch (IOException iox) {
                                Terraforming.logger.log(Level.WARNING, iox.getMessage(), iox);
                            }
                            if (performer.getDeity() != null && performer.getDeity().number == 1) {
                                performer.maybeModifyAlignment(1.0f);
                            }
                            found = 4;
                            for (final Item item3 : inventoryItems) {
                                if (item3.getTemplateId() == 266 && item3 != sprout && found > 0 && item3.getMaterial() == treeMaterial) {
                                    Items.destroyItem(item3.getWurmId());
                                    if (--found <= 0) {
                                        break;
                                    }
                                }
                            }
                            String tosend = "You plant the sprouts and create a fine hedge.";
                            performer.achievement(318);
                            final double gard = gardening2.getKnowledge(0.0);
                            if (gard > 50.0 && Server.rand.nextBoolean()) {
                                if (gard < 60.0) {
                                    tosend = "You plant the hedge, and you can almost feel the plants start sucking nutrition from the earth.";
                                }
                                else if (gard < 70.0) {
                                    tosend = "You plant the hedge, and you get a weird feeling that the plants thanks you.";
                                }
                                else if (gard < 80.0) {
                                    tosend = "You plant the hedge, and you see the plants perform an almost unnoticable bow as they whisper their thanks.";
                                }
                                else if (gard < 100.0) {
                                    tosend = "You plant the hedge. As you see the plants bow you hear the voice in your head of hundreds of other plants thanking you.";
                                    performer.getStatus().modifyStamina(-1000.0f);
                                }
                            }
                            TileEvent.log(fence.getTileX(), fence.getTileY(), 0, performer.getWurmId(), 186);
                            performer.getCommunicator().sendNormalServerMessage(tosend);
                            Server.getInstance().broadCastAction(performer.getName() + " plants a hedge.", performer, 5);
                            performer.getCommunicator().sendActionResult(true);
                        }
                        catch (NoSuchZoneException ex) {}
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Sadly, the sprout does not survive despite your best efforts.");
                        performer.getCommunicator().sendActionResult(false);
                    }
                    Items.destroyItem(sprout.getWurmId());
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You cannot plant a hedge there.");
                performer.getCommunicator().sendActionResult(false);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You need to plant with a sprout, not a " + sprout.getName() + ".");
            performer.getCommunicator().sendActionResult(false);
        }
        return toReturn;
    }
    
    static boolean plantFlower(final Creature performer, final Item flower, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter) {
        boolean toReturn = true;
        final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, onSurface);
        if (vtile != null && vtile.getStructure() != null && vtile.getStructure().isTypeHouse() && flower.getTemplateId() != 756) {
            performer.getCommunicator().sendNormalServerMessage("The " + flower.getName() + " would never grow inside a building.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)186, tilex, tiley)) {
            return true;
        }
        try {
            final Action act = performer.getCurrentAction();
            double power = 0.0;
            int time = 2000;
            toReturn = false;
            if (counter == 1.0f) {
                Skill gardening = null;
                try {
                    gardening = performer.getSkills().getSkill(10045);
                }
                catch (NoSuchSkillException nss) {
                    gardening = performer.getSkills().learn(10045, 1.0f);
                }
                if (isCornerUnderWater(tilex, tiley, onSurface)) {
                    performer.getCommunicator().sendNormalServerMessage("The ground is too moist here, so the " + flower.getName() + " would rot.");
                    return true;
                }
                time = Actions.getStandardActionTime(performer, gardening, flower, 0.0);
                act.setTimeLeft(time);
                performer.getCommunicator().sendNormalServerMessage("You start planting the " + flower.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to plant some " + flower.getName() + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[186].getVerbString(), true, time);
            }
            else {
                time = act.getTimeLeft();
            }
            if (counter * 10.0f > time) {
                Skill gardening = null;
                try {
                    gardening = performer.getSkills().getSkill(10045);
                }
                catch (NoSuchSkillException nss) {
                    gardening = performer.getSkills().learn(10045, 1.0f);
                }
                power = gardening.skillCheck(10.0f + flower.getDamage(), flower.getCurrentQualityLevel(), false, counter);
                toReturn = true;
                if (power > 0.0) {
                    final int newData = TileGrassBehaviour.getDataForFlower(flower.getTemplateId());
                    byte nty = Tiles.Tile.TILE_GRASS.id;
                    if (flower.getTemplateId() == 620) {
                        nty = Tiles.Tile.TILE_STEPPE.id;
                    }
                    else if (flower.getTemplateId() == 479) {
                        nty = Tiles.Tile.TILE_MOSS.id;
                    }
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), nty, (byte)newData);
                    Server.setWorldResource(tilex, tiley, 0);
                    Players.getInstance().sendChangedTile(tilex, tiley, onSurface, true);
                    try {
                        final Zone z = Zones.getZone(tilex, tiley, true);
                        z.changeTile(tilex, tiley);
                    }
                    catch (NoSuchZoneException ex) {}
                    String tosend = "You plant the " + flower.getName() + ".";
                    performer.achievement(123);
                    final double gard = gardening.getKnowledge(0.0);
                    if (gard > 60.0 && flower.getTemplate().isFlower()) {
                        if (gard < 70.0) {
                            tosend = "You plant the " + flower.getName() + ", and you can almost feel them start sucking nutrition from the earth.";
                        }
                        else if (gard < 80.0) {
                            tosend = "You plant the " + flower.getName() + ", and you get a weird feeling that they thank you.";
                        }
                        else if (gard < 90.0) {
                            tosend = "You plant the " + flower.getName() + ", and you see them perform an almost unnoticable bow. Or was it the wind?";
                        }
                        else if (gard < 100.0) {
                            tosend = "You plant the " + flower.getName() + ". As you see them bow you hear their thankful tiny voices in your head.";
                            performer.getStatus().modifyStamina(-1000.0f);
                        }
                    }
                    else {
                        tosend = "You plant the " + flower.getName();
                        performer.getStatus().modifyStamina(-1000.0f);
                    }
                    if (performer.getDeity() != null && performer.getDeity().number == 1) {
                        performer.maybeModifyAlignment(1.0f);
                    }
                    performer.getCommunicator().sendNormalServerMessage(tosend);
                    Server.getInstance().broadCastAction(performer.getName() + " plants some " + flower.getName() + ".", performer, 5);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("Sadly, the " + flower.getName() + " do not survive despite your best efforts.");
                }
                if (!flower.isFlower()) {
                    int weight = flower.getFullWeight();
                    if (weight <= flower.getTemplate().getWeightGrams()) {
                        Items.destroyItem(flower.getWurmId());
                    }
                    else {
                        weight -= flower.getTemplate().getWeightGrams();
                        flower.setWeight(weight, false);
                    }
                }
                else {
                    Items.destroyItem(flower.getWurmId());
                }
            }
        }
        catch (NoSuchActionException nsa) {
            Terraforming.logger.log(Level.WARNING, performer.getName() + ": " + nsa.getMessage(), nsa);
        }
        return toReturn;
    }
    
    static boolean pickFlower(final Creature performer, final Item sickle, final int tilex, final int tiley, final int tile, final float counter, final Action act) {
        boolean toReturn = true;
        if (sickle.getTemplateId() == 267 || sickle.getTemplateId() == 176) {
            final byte tileData = Tiles.decodeData(tile);
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("Your inventory is full. You would have no space to put the flowers.");
                return true;
            }
            final GrassData.FlowerType flowerType = GrassData.FlowerType.decodeTileData(tileData);
            if (Tiles.decodeType(tile) != Tiles.Tile.TILE_GRASS.id || flowerType == GrassData.FlowerType.NONE) {
                performer.getCommunicator().sendNormalServerMessage("No flowers grow here.");
                return true;
            }
            toReturn = false;
            int time = act.getTimeLeft();
            if (counter == 1.0f) {
                try {
                    final int weight = ItemTemplateFactory.getInstance().getTemplate(498).getWeightGrams();
                    if (!performer.canCarry(weight)) {
                        performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the flowers. You need to drop some things first.");
                        return true;
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Terraforming.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                    return true;
                }
                Skill gardening = null;
                Skill sickskill = null;
                try {
                    gardening = performer.getSkills().getSkill(10045);
                }
                catch (NoSuchSkillException nss) {
                    gardening = performer.getSkills().learn(10045, 1.0f);
                }
                try {
                    sickskill = performer.getSkills().getSkill(10046);
                }
                catch (NoSuchSkillException nss) {
                    sickskill = performer.getSkills().learn(10046, 1.0f);
                }
                time = Actions.getStandardActionTime(performer, gardening, sickle, sickskill.getKnowledge(0.0));
                performer.getCommunicator().sendNormalServerMessage("You start picking the flowers.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to pick some flowers.", performer, 5);
                performer.sendActionControl("picking flowers", true, time);
                act.setTimeLeft(time);
            }
            if (counter * 10.0f >= time) {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                try {
                    final int weight = ItemTemplateFactory.getInstance().getTemplate(498).getWeightGrams();
                    if (!performer.canCarry(weight)) {
                        performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the flowers. You need to drop some things first.");
                        return true;
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Terraforming.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                    return true;
                }
                sickle.setDamage(sickle.getDamage() + 0.003f * sickle.getDamageModifier());
                double bonus = 0.0;
                double power = 0.0;
                Skill gardening2 = null;
                Skill sickskill2 = null;
                try {
                    gardening2 = performer.getSkills().getSkill(10045);
                }
                catch (NoSuchSkillException nss2) {
                    gardening2 = performer.getSkills().learn(10045, 1.0f);
                }
                try {
                    sickskill2 = performer.getSkills().getSkill(10046);
                }
                catch (NoSuchSkillException nss2) {
                    sickskill2 = performer.getSkills().learn(10046, 1.0f);
                }
                bonus = Math.max(1.0, sickskill2.skillCheck(1.0, sickle, 0.0, false, counter));
                power = gardening2.skillCheck(1.0, sickle, bonus, false, counter);
                toReturn = true;
                try {
                    float modifier = 1.0f;
                    if (sickle.getSpellEffects() != null) {
                        modifier = sickle.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                    }
                    final GrassData.GrowthStage growthStage = GrassData.GrowthStage.decodeTileData(tileData);
                    final Item flower = ItemFactory.createItem(TileGrassBehaviour.getFlowerTypeFor(flowerType), Math.max(1.0f, Math.min(100.0f, (float)power * modifier + sickle.getRarity())), act.getRarity(), null);
                    if (power < 0.0) {
                        flower.setDamage((float)(-power) / 2.0f);
                    }
                    performer.getInventory().insertItem(flower);
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, GrassData.encodeGrassTileData(growthStage, GrassData.FlowerType.NONE));
                    Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                    performer.getCommunicator().sendNormalServerMessage("You pick some flowers.");
                    Server.getInstance().broadCastAction(performer.getName() + " picks some flowers.", performer, 5);
                }
                catch (NoSuchTemplateException nst2) {
                    Terraforming.logger.log(Level.WARNING, "No template for flowers!", nst2);
                    performer.getCommunicator().sendNormalServerMessage("You fail to pick the flowers. You realize something is wrong with the world.");
                }
                catch (FailedException fe) {
                    Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                    performer.getCommunicator().sendNormalServerMessage("You fail to pick the flowers. You realize something is wrong with the world.");
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot pick sprouts with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to pick sprout with a " + sickle.getName());
        }
        return toReturn;
    }
    
    static boolean growFarm(final Creature performer, final int tile, final int tilex, final int tiley, final boolean onSurface) {
        final int data = Tiles.decodeData(tile) & 0xFF;
        final byte type = Tiles.decodeType(tile);
        final int tileState = data >> 4;
        int tileAge = tileState & 0x7;
        if (tileAge < 7) {
            final int crop = data & 0xF;
            ++tileAge;
            if (!onSurface) {
                Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), type, (byte)((tileAge << 4) + crop & 0xFF)));
            }
            else {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), type, (byte)((tileAge << 4) + crop & 0xFF));
            }
            if (WurmCalendar.isNight()) {
                SoundPlayer.playSound("sound.birdsong.bird1", tilex, tiley, onSurface, 3.0f);
            }
            else {
                SoundPlayer.playSound("sound.birdsong.bird2", tilex, tiley, onSurface, 3.0f);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, onSurface, false);
        }
        return true;
    }
    
    static boolean harvest(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, @Nullable final Item item) {
        boolean done = true;
        final byte type = Tiles.decodeType(tile);
        if (type != Tiles.Tile.TILE_FIELD.id && type != Tiles.Tile.TILE_FIELD2.id) {
            return done;
        }
        final byte data = Tiles.decodeData(tile);
        final int tileAge = Crops.decodeFieldAge(data);
        final int crop = Crops.getCropNumber(type, data);
        if (!performer.getInventory().mayCreatureInsertItem()) {
            performer.getCommunicator().sendNormalServerMessage("Your inventory is full. You would have no space to keep whatever you harvest.");
            return true;
        }
        if (crop > 3 || (item != null && item.getTemplateId() == 268)) {
            if (tileAge != 0 && tileAge != 7) {
                final double diff = Crops.getDifficultyFor(crop);
                done = false;
                Action act = null;
                try {
                    act = performer.getCurrentAction();
                }
                catch (NoSuchActionException nsa) {
                    Terraforming.logger.log(Level.WARNING, nsa.getMessage(), nsa);
                    return true;
                }
                int time = 100;
                if (counter == 1.0f) {
                    final Skill farming = performer.getSkills().getSkillOrLearn(10049);
                    time = Actions.getStandardActionTime(performer, farming, null, 0.0);
                    act.setTimeLeft(time);
                    performer.getCommunicator().sendNormalServerMessage("You start harvesting the field.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts harvesting the field.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[152].getVerbString(), true, time);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else {
                    time = act.getTimeLeft();
                }
                if (crop <= 3 && item != null && act.justTickedSecond()) {
                    item.setDamage(item.getDamage() + 3.0E-4f * item.getDamageModifier());
                }
                if (act.justTickedSecond()) {
                    if (act.currentSecond() % 5 == 0) {
                        performer.getStatus().modifyStamina(-10000.0f);
                    }
                    if (act.mayPlaySound()) {
                        if (crop <= 3 && item != null && item.getTemplateId() == 268) {
                            Methods.sendSound(performer, "sound.work.farming.scythe");
                        }
                        else {
                            Methods.sendSound(performer, "sound.work.farming.harvest");
                        }
                    }
                }
                if (counter * 10.0f > time) {
                    if (act.getRarity() != 0) {
                        performer.playPersonalSound("sound.fx.drumroll");
                    }
                    final Skill farming = performer.getSkills().getSkillOrLearn(10049);
                    final double power = farming.skillCheck(diff, 0.0, false, counter);
                    Skill primskill = null;
                    byte itemRarity = 0;
                    if (crop <= 3 && item != null && item.getTemplateId() == 268) {
                        itemRarity = item.getRarity();
                        try {
                            final int primarySkill = item.getPrimarySkill();
                            primskill = performer.getSkills().getSkillOrLearn(primarySkill);
                        }
                        catch (NoSuchSkillException nss) {
                            Terraforming.logger.log(Level.WARNING, "Scythe has no prim skill? :" + nss.getMessage(), nss);
                        }
                    }
                    if (primskill != null) {
                        Math.max(0.0, primskill.skillCheck(diff, item, 0.0, false, counter));
                    }
                    TileEvent.log(tilex, tiley, 0, performer.getWurmId(), 152);
                    done = true;
                    final int templateId = Crops.getProductTemplate(crop);
                    final float knowledge = (float)farming.getKnowledge(0.0);
                    float ql = knowledge + (100.0f - knowledge) * ((float)power / 500.0f);
                    float ageYieldFactor = 0.0f;
                    float ageQLFactor = 0.0f;
                    boolean ripe = false;
                    String failMessage = "You realize you harvested so early that nothing had a chance to grow here.";
                    String passMessage = "";
                    if (tileAge >= 3) {
                        if (tileAge < 4) {
                            ageQLFactor = 0.7f;
                            ageYieldFactor = 0.5f;
                            failMessage = "You realize you harvested much too early. There was nothing here to harvest.";
                            passMessage = "You realize you harvested much too early. Only sprouts grew here.";
                        }
                        else if (tileAge < 5) {
                            ageQLFactor = 0.9f;
                            ageYieldFactor = 0.7f;
                            failMessage = "You realize you harvested too early. There was nothing here to harvest.";
                            passMessage = "You realize you harvested too early. The harvest is of low quality.";
                        }
                        else if (tileAge < 7) {
                            ripe = true;
                            ageQLFactor = 1.0f;
                            ageYieldFactor = 1.0f;
                            failMessage = "You realize you harvested in perfect time, tending the field would have resulted in a better yield.";
                            passMessage = "You realize you harvested in perfect time. The harvest is of top quality.";
                        }
                    }
                    final float realKnowledge = (float)farming.getKnowledge(0.0);
                    final int worldResource = Server.getWorldResource(tilex, tiley);
                    final int farmedCount = worldResource >>> 11;
                    final int farmedChance = worldResource & 0x7FF;
                    final short resource = (short)(farmedChance + act.getRarity() * 110 + itemRarity * 50 + Math.min(5, farmedCount) * 50);
                    final float div = 100.0f - realKnowledge / 15.0f;
                    final short bonusYield = (short)(resource / div / 1.5f);
                    final float baseYield = realKnowledge / 15.0f;
                    int quantity = (int)((baseYield + bonusYield) * ageYieldFactor);
                    Server.setWorldResource(tilex, tiley, 0);
                    Server.getInstance().broadCastAction(performer.getName() + " has harvested the field.", performer, 5);
                    ql *= ageQLFactor;
                    if (crop <= 3 && item != null && item.getSpellEffects() != null) {
                        final float modifier = item.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                        ql *= modifier;
                    }
                    if (quantity == 0 && ripe) {
                        quantity = 1;
                    }
                    if (quantity == 1 && farmedCount > 0 && ripe) {
                        ++quantity;
                    }
                    if (quantity == 2 && farmedCount >= 4 && ripe) {
                        ++quantity;
                    }
                    if (quantity == 0 || (ripe && quantity == 1)) {
                        performer.getCommunicator().sendNormalServerMessage(failMessage);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage(passMessage);
                    }
                    final String cropString = Crops.getCropName(crop);
                    performer.getCommunicator().sendNormalServerMessage("You managed to get a yield of " + quantity + " " + cropString + ".");
                    if (templateId == 144 && quantity >= 5) {
                        performer.achievement(544);
                    }
                    try {
                        for (int x = 0; x < quantity; ++x) {
                            final Item result = ItemFactory.createItem(templateId, Math.max(Math.min(ql, 100.0f), 1.0f), null);
                            if (!performer.getInventory().insertItem(result, true)) {
                                performer.getCommunicator().sendNormalServerMessage("You can't carry the harvest. It falls to the ground and is ruined!");
                            }
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        Terraforming.logger.log(Level.WARNING, "No such template", nst);
                    }
                    catch (FailedException fe) {
                        Terraforming.logger.log(Level.WARNING, "Failed to create harvest", fe);
                    }
                    if (onSurface) {
                        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0);
                    }
                    else {
                        Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT.id, (byte)0));
                    }
                    performer.getMovementScheme().touchFreeMoveCounter();
                    Players.getInstance().sendChangedTile(tilex, tiley, onSurface, false);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("There is nothing here to harvest.");
                done = true;
            }
        }
        return done;
    }
    
    private static final boolean checkIfTerraformingOnPermaObject(final int digTilex, final int digTiley) {
        final short hh = Tiles.decodeHeight(Server.surfaceMesh.getTile(digTilex, digTiley));
        if (hh < 1) {
            VolaTile t = Zones.getTileOrNull(digTilex, digTiley, true);
            if (t != null && t.hasOnePerTileItem(0)) {
                return true;
            }
            t = Zones.getTileOrNull(digTilex - 1, digTiley - 1, true);
            if (t != null && t.hasOnePerTileItem(0)) {
                return true;
            }
            t = Zones.getTileOrNull(digTilex, digTiley - 1, true);
            if (t != null && t.hasOnePerTileItem(0)) {
                return true;
            }
            t = Zones.getTileOrNull(digTilex - 1, digTiley, true);
            if (t != null && t.hasOnePerTileItem(0)) {
                return true;
            }
        }
        return false;
    }
    
    static boolean pickSprout(final Creature performer, final Item sickle, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter, final Action act) {
        boolean toReturn = true;
        final byte tileType = Tiles.decodeType(tile);
        if (sickle.getTemplateId() == 267 && !theTile.isEnchanted()) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("Your inventory is full. You would have no space to put the sprout.");
                return true;
            }
            final byte data = Tiles.decodeData(tile);
            int age = FoliageAge.getAgeAsByte(data);
            if (age == 7 || age == 9 || age == 11 || age == 13) {
                final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
                toReturn = false;
                final int time = Actions.getStandardActionTime(performer, forestry, sickle, 0.0);
                if (counter == 1.0f) {
                    try {
                        final int weight = ItemTemplateFactory.getInstance().getTemplate(266).getWeightGrams();
                        if (!performer.canCarry(weight)) {
                            performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the sprout. You need to drop some things first.");
                            return true;
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        Terraforming.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                        return true;
                    }
                    if (theTile.isBush()) {
                        performer.getCommunicator().sendNormalServerMessage("You start cutting a sprout from the bush.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to cut a sprout off a bush.", performer, 5);
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You start cutting a sprout from the tree.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to cut a sprout off a tree.", performer, 5);
                    }
                    performer.sendActionControl(Actions.actionEntrys[187].getVerbString(), true, time);
                }
                if (counter * 10.0f >= time) {
                    if (act.getRarity() != 0) {
                        performer.playPersonalSound("sound.fx.drumroll");
                    }
                    try {
                        final int weight = ItemTemplateFactory.getInstance().getTemplate(266).getWeightGrams();
                        if (!performer.canCarry(weight)) {
                            performer.getCommunicator().sendNormalServerMessage("You would not be able to carry the sprout. You need to drop some things first.");
                            return true;
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        Terraforming.logger.log(Level.WARNING, nst.getLocalizedMessage(), nst);
                        return true;
                    }
                    sickle.setDamage(sickle.getDamage() + 0.003f * sickle.getDamageModifier());
                    double bonus = 0.0;
                    double power = 0.0;
                    final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
                    bonus = Math.max(1.0, sickskill.skillCheck(1.0, sickle, 0.0, false, counter));
                    power = forestry.skillCheck(1.0, sickle, bonus, false, counter);
                    toReturn = true;
                    try {
                        byte material = 0;
                        if (theTile.isBush()) {
                            final BushData.BushType bushType = theTile.getBushType(data);
                            material = bushType.getMaterial();
                        }
                        else {
                            final TreeData.TreeType treeType = theTile.getTreeType(data);
                            material = treeType.getMaterial();
                        }
                        float modifier = 1.0f;
                        if (sickle.getSpellEffects() != null) {
                            modifier = sickle.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                        }
                        final Item sprout = ItemFactory.createItem(266, Math.max(1.0f, Math.min(100.0f, (float)power * modifier + sickle.getRarity())), material, act.getRarity(), null);
                        if (power < 0.0) {
                            sprout.setDamage((float)(-power) / 2.0f);
                        }
                        SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 2.0f);
                        --age;
                        performer.getInventory().insertItem(sprout);
                        final byte newData = (byte)((age << 4) + (data & 0xF) & 0xFF);
                        Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.decodeType(tile), newData);
                        Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                        if (theTile.isBush()) {
                            performer.getCommunicator().sendNormalServerMessage("You cut a sprout from the bush.");
                            Server.getInstance().broadCastAction(performer.getName() + " cuts a sprout off a bush.", performer, 5);
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You cut a sprout from the tree.");
                            Server.getInstance().broadCastAction(performer.getName() + " cuts a sprout off a tree.", performer, 5);
                        }
                    }
                    catch (NoSuchTemplateException nst2) {
                        Terraforming.logger.log(Level.WARNING, "No template for sprout!", nst2);
                        performer.getCommunicator().sendNormalServerMessage("You fail to pick the sprout. You realize something is wrong with the world.");
                    }
                    catch (FailedException fe) {
                        Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                        performer.getCommunicator().sendNormalServerMessage("You fail to pick the sprout. You realize something is wrong with the world.");
                    }
                }
            }
            else if (theTile.isBush()) {
                performer.getCommunicator().sendNormalServerMessage("The bush has no sprout to pick.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The tree has no sprout to pick.");
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot pick sprouts with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to pick sprout with a " + sickle.getName());
        }
        return toReturn;
    }
    
    public static final int getTreeHarvestingToolTemplate(final TreeData.TreeType type) {
        if (type == TreeData.TreeType.MAPLE) {
            return 421;
        }
        return 267;
    }
    
    static boolean harvestTree(final Action act, final Creature performer, final Item tool, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        final byte data = Tiles.decodeData(tile);
        final int age = FoliageAge.getAgeAsByte(data);
        final TreeData.TreeType treeType = theTile.getTreeType(data);
        if (tool.getTemplateId() == getTreeHarvestingToolTemplate(treeType)) {
            final String treeName = treeType.getName();
            if (counter == 1.0f && !TileTreeBehaviour.hasFruit(performer, tilex, tiley, age)) {
                performer.getCommunicator().sendNormalServerMessage("There is nothing to harvest on the " + treeName + ".");
                return true;
            }
            final int templateId = TileTreeBehaviour.getItem(tilex, tiley, age, treeType);
            if (templateId == -10) {
                performer.getCommunicator().sendNormalServerMessage("There is nothing to harvest on the " + treeName + ".");
                return true;
            }
            toReturn = false;
            int time = 150;
            final Skill skill = performer.getSkills().getSkillOrLearn(10048);
            Skill toolSkill = null;
            if (tool.getTemplateId() == 267) {
                toolSkill = performer.getSkills().getSkillOrLearn(10046);
            }
            if (counter == 1.0f) {
                if (!performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put what you harvest.");
                    return true;
                }
                if (tool.getTemplate().isContainerLiquid() && tool.getFreeVolume() <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("The " + tool.getName() + " is already full!");
                    return true;
                }
                final int maxSearches = calcMaxHarvest(tile, skill.getKnowledge(0.0), tool);
                time = Actions.getQuickActionTime(performer, skill, null, 0.0);
                act.setNextTick(time);
                act.setTickCount(1);
                act.setData(0L);
                final float totalTime = time * maxSearches;
                try {
                    performer.getCurrentAction().setTimeLeft((int)totalTime);
                }
                catch (NoSuchActionException nsa) {
                    Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
                }
                performer.getCommunicator().sendNormalServerMessage("You start to harvest the " + treeName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to harvest a tree.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[152].getVerbString(), true, (int)totalTime);
                performer.getStatus().modifyStamina(-500.0f);
            }
            if (tool != null && act.justTickedSecond()) {
                tool.setDamage(tool.getDamage() + 3.0E-4f * tool.getDamageModifier());
            }
            if (counter * 10.0f >= act.getNextTick()) {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                final int searchCount = act.getTickCount();
                final int maxSearches2 = calcMaxHarvest(tile, skill.getKnowledge(0.0), tool);
                act.incTickCount();
                act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
                final int knowledge = (int)skill.getKnowledge(0.0);
                performer.getStatus().modifyStamina(-1500 * searchCount);
                if (searchCount >= maxSearches2) {
                    toReturn = true;
                }
                act.setData(act.getData() + 1L);
                double bonus = 0.0;
                if (tool.getTemplateId() == 267) {
                    bonus = Math.max(1.0, toolSkill.skillCheck(1.0, tool, 0.0, false, counter / searchCount));
                }
                final double power = skill.skillCheck(skill.getKnowledge(0.0) - 5.0, tool, bonus, false, counter / searchCount);
                try {
                    float modifier = 1.0f;
                    if (tool.getSpellEffects() != null) {
                        modifier = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                    }
                    float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                    ql = Math.min(100.0f, (ql + tool.getRarity()) * modifier);
                    final Item harvested = ItemFactory.createItem(templateId, Math.max(1.0f, ql), act.getRarity(), null);
                    if (ql < 0.0f) {
                        harvested.setDamage(-ql / 2.0f);
                    }
                    if (tool.getTemplateId() == 267) {
                        performer.getInventory().insertItem(harvested);
                        SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                    }
                    else {
                        MethodsItems.fillContainer(act, tool, harvested, performer, false);
                        if (!harvested.deleted && harvested.getParentId() == -10L) {
                            performer.getCommunicator().sendNormalServerMessage("Not all the " + harvested.getName() + " would fit in the " + tool.getName() + ".");
                            Items.destroyItem(harvested.getWurmId());
                            toReturn = true;
                        }
                    }
                    if (searchCount == 1) {
                        TileTreeBehaviour.pick(tilex, tiley);
                    }
                    performer.getCommunicator().sendNormalServerMessage("You harvest " + harvested.getNameWithGenus() + " from the " + treeName + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " harvests " + harvested.getName() + " from a tree.", performer, 5);
                    if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                        performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                        toReturn = true;
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Terraforming.logger.log(Level.WARNING, "No template for " + templateId, nst);
                    performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
                }
                catch (FailedException fe) {
                    Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                    performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
                }
                if (searchCount < maxSearches2) {
                    act.setRarity(performer.getRarity());
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot harvest with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to harvest a tree with a " + tool.getName());
        }
        return toReturn;
    }
    
    static boolean harvestBush(final Action act, final Creature performer, final Item tool, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        if (tool.getTemplateId() == 267) {
            final byte data = Tiles.decodeData(tile);
            final int age = FoliageAge.getAgeAsByte(data);
            final BushData.BushType bushType = theTile.getBushType(data);
            final String treeName = bushType.getName();
            if (counter == 1.0f && !TileTreeBehaviour.hasFruit(performer, tilex, tiley, age)) {
                performer.getCommunicator().sendNormalServerMessage("There is nothing to harvest on the " + treeName + ".");
                return true;
            }
            final int templateId = TileTreeBehaviour.getItem(tilex, tiley, age, bushType);
            if (templateId == -10) {
                performer.getCommunicator().sendNormalServerMessage("There is nothing to harvest on the " + treeName + ".");
                return true;
            }
            toReturn = false;
            int time = 150;
            final Skill skill = performer.getSkills().getSkillOrLearn(10048);
            final Skill toolSkill = performer.getSkills().getSkillOrLearn(10046);
            if (counter == 1.0f) {
                if (!performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put what you harvest.");
                    return true;
                }
                final int maxSearches = calcMaxHarvest(tile, skill.getKnowledge(0.0), tool);
                time = Actions.getQuickActionTime(performer, skill, null, 0.0);
                act.setNextTick(time);
                act.setTickCount(1);
                act.setData(0L);
                final float totalTime = time * maxSearches;
                performer.getCommunicator().sendNormalServerMessage("You start to harvest the " + treeName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to harvest a bush.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[152].getVerbString(), true, (int)totalTime);
            }
            if (act.justTickedSecond()) {
                tool.setDamage(tool.getDamage() + 3.0E-4f * tool.getDamageModifier());
            }
            if (counter * 10.0f >= act.getNextTick()) {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                final int searchCount = act.getTickCount();
                final int maxSearches2 = calcMaxHarvest(tile, skill.getKnowledge(0.0), tool);
                act.incTickCount();
                act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
                final int knowledge = (int)skill.getKnowledge(0.0);
                performer.getStatus().modifyStamina(-1500 * searchCount);
                if (searchCount >= maxSearches2) {
                    toReturn = true;
                }
                act.setData(act.getData() + 1L);
                final double bonus = Math.max(1.0, toolSkill.skillCheck(1.0, tool, 0.0, false, counter / searchCount));
                final double power = skill.skillCheck(skill.getKnowledge(0.0) - 5.0, tool, bonus, false, counter / searchCount);
                try {
                    float modifier = 1.0f;
                    if (tool.getSpellEffects() != null) {
                        modifier = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                    }
                    float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                    ql = Math.min(100.0f, (ql + tool.getRarity()) * modifier);
                    final Item harvested = ItemFactory.createItem(templateId, Math.max(1.0f, ql), act.getRarity(), null);
                    if (ql < 0.0f) {
                        harvested.setDamage(-ql / 2.0f);
                    }
                    performer.getInventory().insertItem(harvested);
                    SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                    if (searchCount == 1) {
                        TileTreeBehaviour.pick(tilex, tiley);
                    }
                    performer.getCommunicator().sendNormalServerMessage("You harvest " + harvested.getName() + " from the " + treeName + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " harvests " + harvested.getName() + " from a bush.", performer, 5);
                    if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                        performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                        toReturn = true;
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Terraforming.logger.log(Level.WARNING, "No template for " + templateId, nst);
                    performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
                }
                catch (FailedException fe) {
                    Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                    performer.getCommunicator().sendNormalServerMessage("You fail to harvest. You realize something is wrong with the world.");
                }
                if (searchCount < maxSearches2) {
                    act.setRarity(performer.getRarity());
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot harvest with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to harvest a bush with a " + tool.getName());
        }
        return toReturn;
    }
    
    static boolean prune(final Action action, final Creature performer, final Item sickle, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        if (sickle.getTemplateId() == 267) {
            if (theTile.isEnchanted()) {
                performer.getCommunicator().sendNormalServerMessage("It does not make sense to prune that.");
                return true;
            }
            final byte data = Tiles.decodeData(tile);
            final FoliageAge age = FoliageAge.getFoliageAge(data);
            final String treeName = theTile.getTileName(data).toLowerCase();
            boolean ok = false;
            if (age.isPrunable() || (age == FoliageAge.SHRIVELLED && theTile.isThorn(data))) {
                ok = true;
            }
            if (!ok) {
                performer.getCommunicator().sendNormalServerMessage("It does not make sense to prune now.");
                return true;
            }
            toReturn = false;
            int time = 150;
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
            if (sickle.getTemplateId() == 267) {
                time = Actions.getStandardActionTime(performer, forestry, sickle, sickskill.getKnowledge(0.0));
            }
            if (counter == 1.0f) {
                performer.getCommunicator().sendNormalServerMessage("You start to prune the " + treeName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to prune the " + treeName + ".", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[373].getVerbString(), true, time);
            }
            if (action.justTickedSecond()) {
                sickle.setDamage(sickle.getDamage() + 3.0E-4f * sickle.getDamageModifier());
            }
            if (counter * 10.0f >= time) {
                double bonus = 0.0;
                double power = 0.0;
                bonus = Math.max(1.0, sickskill.skillCheck(1.0, sickle, 0.0, false, counter));
                power = forestry.skillCheck(forestry.getKnowledge(0.0) - 10.0, sickle, bonus, false, counter);
                toReturn = true;
                SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                if (power < 0.0) {
                    performer.getCommunicator().sendNormalServerMessage("You make a lot of errors and need to take a break.");
                    return toReturn;
                }
                final FoliageAge newage = age.getPrunedAge();
                final int newData = newage.encodeAsData() + (data & 0xF) & 0xFF;
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.decodeType(tile), (byte)newData);
                TileEvent.log(tilex, tiley, 0, performer.getWurmId(), 373);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                performer.getCommunicator().sendNormalServerMessage("You prune the " + treeName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " prunes the " + treeName + ".", performer, 5);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot prune with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to prune with a " + sickle.getName());
        }
        return toReturn;
    }
    
    static boolean pickWurms(final Action act, final Creature performer, final int tilex, final int tiley, final int tile, final float counter) {
        boolean toReturn = true;
        if (counter == 1.0f && !Server.hasGrubs(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("There see no wurms casts here.");
            return true;
        }
        toReturn = false;
        int time = 150;
        final Skill skill = performer.getSkills().getSkillOrLearn(10071);
        if (counter == 1.0f) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put any grubs.");
                return true;
            }
            final int maxSearches = calcMaxGrubs(skill.getKnowledge(0.0), null);
            time = Actions.getQuickActionTime(performer, skill, null, 0.0);
            act.setNextTick(time);
            act.setTickCount(1);
            act.setData(0L);
            final float totalTime = time * maxSearches;
            try {
                performer.getCurrentAction().setTimeLeft((int)totalTime);
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            performer.getCommunicator().sendNormalServerMessage("You start to search the dirt tile for wurms.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to search a dirt tile for wurms.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[935].getVerbString(), true, (int)totalTime);
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f >= act.getNextTick()) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int searchCount = act.getTickCount();
            final int maxSearches2 = calcMaxGrubs(skill.getKnowledge(0.0), null);
            act.incTickCount();
            act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
            final int knowledge = (int)skill.getKnowledge(0.0);
            performer.getStatus().modifyStamina(-1500 * searchCount);
            if (searchCount >= maxSearches2) {
                toReturn = true;
            }
            act.setData(act.getData() + 1L);
            final double bonus = 0.0;
            final double diff = skill.getKnowledge(0.0) - 10.0 + searchCount * 5;
            final double power = skill.skillCheck(diff, null, bonus, false, counter / searchCount);
            try {
                final float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                final Item wurm = ItemFactory.createItem(1362, Math.max(1.0f, ql), act.getRarity(), null);
                if (ql < 0.0f) {
                    wurm.setDamage(-ql / 2.0f);
                }
                performer.getInventory().insertItem(wurm);
                SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                if (searchCount == 1) {
                    Server.setGrubs(tilex, tiley, false);
                }
                performer.getCommunicator().sendNormalServerMessage("You do a rain danec on the dirt tile and " + wurm.getNameWithGenus() + " pops to the surface, which you grab.");
                Server.getInstance().broadCastAction(performer.getName() + " danecs on a dirt tile and grabs " + wurm.getNameWithGenus() + " that pops to the surface.", performer, 5);
                if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                    return true;
                }
                if (ql < 0.0f && searchCount < maxSearches2) {
                    performer.getCommunicator().sendNormalServerMessage("You make such a mess, you stop searching.");
                    return true;
                }
            }
            catch (NoSuchTemplateException nst) {
                Terraforming.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any wurms. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any wurms. You realize something is wrong with the world.");
            }
            if (searchCount < maxSearches2) {
                act.setRarity(performer.getRarity());
            }
        }
        return toReturn;
    }
    
    static boolean pickGrubs(final Action act, final Creature performer, final Item tool, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        final byte data = Tiles.decodeData(tile);
        final int age = FoliageAge.getAgeAsByte(data);
        final TreeData.TreeType treeType = theTile.getTreeType(data);
        if (tool.getTemplateId() == 390) {
            final String treeName = treeType.getName();
            if (counter == 1.0f && (age != 15 || !Server.hasGrubs(tilex, tiley))) {
                performer.getCommunicator().sendNormalServerMessage("You find no grubs on the " + treeName + ".");
                return true;
            }
            toReturn = false;
            int time = 150;
            final Skill skill = performer.getSkills().getSkillOrLearn(10048);
            if (counter == 1.0f) {
                if (!performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put any grubs.");
                    return true;
                }
                final int maxSearches = calcMaxGrubs(skill.getKnowledge(0.0), tool);
                time = Actions.getQuickActionTime(performer, skill, null, 0.0);
                act.setNextTick(time);
                act.setTickCount(1);
                act.setData(0L);
                final float totalTime = time * maxSearches;
                try {
                    performer.getCurrentAction().setTimeLeft((int)totalTime);
                }
                catch (NoSuchActionException nsa) {
                    Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
                }
                performer.getCommunicator().sendNormalServerMessage("You start to search the " + treeName + " for grubs.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to search a tree for grubs.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[935].getVerbString(), true, (int)totalTime);
                performer.getStatus().modifyStamina(-500.0f);
            }
            if (tool != null && act.justTickedSecond()) {
                tool.setDamage(tool.getDamage() + 3.0E-4f * tool.getDamageModifier());
            }
            if (counter * 10.0f >= act.getNextTick()) {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                final int searchCount = act.getTickCount();
                final int maxSearches2 = calcMaxGrubs(skill.getKnowledge(0.0), tool);
                act.incTickCount();
                act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
                final int knowledge = (int)skill.getKnowledge(0.0);
                performer.getStatus().modifyStamina(-1500 * searchCount);
                if (searchCount >= maxSearches2) {
                    toReturn = true;
                }
                act.setData(act.getData() + 1L);
                final double bonus = 0.0;
                if (tool.getTemplateId() == 390) {}
                final double diff = skill.getKnowledge(0.0) - 10.0 + searchCount * 5;
                final double power = skill.skillCheck(diff, tool, bonus, false, counter / searchCount);
                try {
                    float modifier = 1.0f;
                    if (tool.getSpellEffects() != null) {
                        modifier = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                    }
                    float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                    ql = Math.min(100.0f, (ql + tool.getRarity()) * modifier);
                    final Item grub = ItemFactory.createItem(1364, Math.max(1.0f, ql), act.getRarity(), null);
                    if (ql < 0.0f) {
                        grub.setDamage(-ql / 2.0f);
                    }
                    performer.getInventory().insertItem(grub);
                    SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                    if (searchCount == 1) {
                        Server.setGrubs(tilex, tiley, false);
                    }
                    performer.getCommunicator().sendNormalServerMessage("You prise " + grub.getNameWithGenus() + " from the " + treeName + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " prises a " + grub.getName() + " from a tree.", performer, 5);
                    if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                        performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                        return true;
                    }
                    if (ql < 0.0f && searchCount < maxSearches2) {
                        performer.getCommunicator().sendNormalServerMessage("You make such a mess, you stop searching.");
                        return true;
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Terraforming.logger.log(Level.WARNING, "No template for 1364", nst);
                    performer.getCommunicator().sendNormalServerMessage("You fail to find any grubs. You realize something is wrong with the world.");
                }
                catch (FailedException fe) {
                    Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                    performer.getCommunicator().sendNormalServerMessage("You fail to find any grubs. You realize something is wrong with the world.");
                }
                if (searchCount < maxSearches2) {
                    act.setRarity(performer.getRarity());
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You cannot prise with that.");
            Terraforming.logger.log(Level.WARNING, performer.getName() + " tried to prise grubs from a tree with a " + tool.getName());
        }
        return toReturn;
    }
    
    static boolean pickBark(final Action act, final Creature performer, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        final byte data = Tiles.decodeData(tile);
        final int age = FoliageAge.getAgeAsByte(data);
        final TreeData.TreeType treeType = theTile.getTreeType(data);
        final String treeName = treeType.getName();
        if (counter == 1.0f && (age != 14 || !Server.hasGrubs(tilex, tiley))) {
            performer.getCommunicator().sendNormalServerMessage("There see no loose bark on the " + treeName + ".");
            return true;
        }
        toReturn = false;
        int time = 150;
        final Skill skill = performer.getSkills().getSkillOrLearn(10048);
        if (counter == 1.0f) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put any bark.");
                return true;
            }
            final int maxSearches = calcMaxGrubs(skill.getKnowledge(0.0), null);
            time = Actions.getQuickActionTime(performer, skill, null, 0.0);
            act.setNextTick(time);
            act.setTickCount(1);
            act.setData(0L);
            final float totalTime = time * maxSearches;
            try {
                performer.getCurrentAction().setTimeLeft((int)totalTime);
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            performer.getCommunicator().sendNormalServerMessage("You start to search the " + treeName + " for loose bark.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to search a tree.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[935].getVerbString(), true, (int)totalTime);
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f >= act.getNextTick()) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int searchCount = act.getTickCount();
            final int maxSearches2 = calcMaxGrubs(skill.getKnowledge(0.0), null);
            act.incTickCount();
            act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
            final int knowledge = (int)skill.getKnowledge(0.0);
            performer.getStatus().modifyStamina(-1500 * searchCount);
            if (searchCount >= maxSearches2) {
                toReturn = true;
            }
            act.setData(act.getData() + 1L);
            final double bonus = 0.0;
            final double diff = skill.getKnowledge(0.0) - 10.0 + searchCount * 5;
            final double power = skill.skillCheck(diff, null, bonus, false, counter / searchCount);
            try {
                final float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                final Item bark = ItemFactory.createItem(1355, Math.max(1.0f, ql), act.getRarity(), null);
                if (ql < 0.0f) {
                    bark.setDamage(-ql / 2.0f);
                }
                performer.getInventory().insertItem(bark);
                SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                if (searchCount == 1) {
                    Server.setGrubs(tilex, tiley, false);
                }
                performer.getCommunicator().sendNormalServerMessage("You remove " + bark.getNameWithGenus() + " from the " + treeName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " break a piece of " + bark.getName() + " from a tree.", performer, 5);
                if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                    return true;
                }
                if (ql < 0.0f && searchCount < maxSearches2) {
                    performer.getCommunicator().sendNormalServerMessage("You make such a mess, you stop searching.");
                    return true;
                }
            }
            catch (NoSuchTemplateException nst) {
                Terraforming.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any loose bark. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any loose bark. You realize something is wrong with the world.");
            }
            if (searchCount < maxSearches2) {
                act.setRarity(performer.getRarity());
            }
        }
        return toReturn;
    }
    
    static boolean findTwigs(final Action act, final Creature performer, final int tilex, final int tiley, final int tile, final Tiles.Tile theTile, final float counter) {
        boolean toReturn = true;
        final byte data = Tiles.decodeData(tile);
        final int age = FoliageAge.getAgeAsByte(data);
        final BushData.BushType bushType = theTile.getBushType(data);
        final String bushName = bushType.getName();
        if (counter == 1.0f && (age != 14 || !Server.hasGrubs(tilex, tiley))) {
            performer.getCommunicator().sendNormalServerMessage("There see no twigs under the " + bushName + ".");
            return true;
        }
        toReturn = false;
        int time = 150;
        final Skill skill = performer.getSkills().getSkillOrLearn(10048);
        if (counter == 1.0f) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put any twigs.");
                return true;
            }
            final int maxSearches = calcMaxGrubs(skill.getKnowledge(0.0), null);
            time = Actions.getQuickActionTime(performer, skill, null, 0.0);
            act.setNextTick(time);
            act.setTickCount(1);
            act.setData(0L);
            final float totalTime = time * maxSearches;
            try {
                performer.getCurrentAction().setTimeLeft((int)totalTime);
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            performer.getCommunicator().sendNormalServerMessage("You start to search under the " + bushName + " for twigs.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to search under a bush.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[935].getVerbString(), true, (int)totalTime);
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f >= act.getNextTick()) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int searchCount = act.getTickCount();
            final int maxSearches2 = calcMaxGrubs(skill.getKnowledge(0.0), null);
            act.incTickCount();
            act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
            final int knowledge = (int)skill.getKnowledge(0.0);
            performer.getStatus().modifyStamina(-1500 * searchCount);
            if (searchCount >= maxSearches2) {
                toReturn = true;
            }
            act.setData(act.getData() + 1L);
            final double bonus = 0.0;
            final double diff = skill.getKnowledge(0.0) - 10.0 + searchCount * 5;
            final double power = skill.skillCheck(diff, null, bonus, false, counter / searchCount);
            try {
                float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                ql = Math.min(100.0f, ql);
                final Item twig = ItemFactory.createItem(1353, Math.max(1.0f, ql), act.getRarity(), null);
                twig.setMaterial(bushType.getMaterial());
                if (ql < 0.0f) {
                    twig.setDamage(-ql / 2.0f);
                }
                performer.getInventory().insertItem(twig);
                SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                if (searchCount == 1) {
                    Server.setGrubs(tilex, tiley, false);
                }
                performer.getCommunicator().sendNormalServerMessage("You find " + twig.getNameWithGenus() + " under the " + bushName + ".");
                Server.getInstance().broadCastAction(performer.getName() + " finds something under a bush.", performer, 5);
                if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                    return true;
                }
                if (ql < 0.0f && searchCount < maxSearches2) {
                    performer.getCommunicator().sendNormalServerMessage("You make such a mess, you stop searching.");
                    return true;
                }
            }
            catch (NoSuchTemplateException nst) {
                Terraforming.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any twigs. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any twigs. You realize something is wrong with the world.");
            }
            if (searchCount < maxSearches2) {
                act.setRarity(performer.getRarity());
            }
        }
        return toReturn;
    }
    
    static boolean findFeathers(final Action act, final Creature performer, final int tilex, final int tiley, final int tile, final float counter) {
        boolean toReturn = true;
        if (counter == 1.0f && !Server.hasGrubs(tilex, tiley)) {
            performer.getCommunicator().sendNormalServerMessage("The area looks picked clean.");
            return true;
        }
        toReturn = false;
        int time = 150;
        final Skill skill = performer.getSkills().getSkillOrLearn(10071);
        if (counter == 1.0f) {
            if (!performer.getInventory().mayCreatureInsertItem()) {
                performer.getCommunicator().sendNormalServerMessage("You have no space left in your inventory to put any feathers.");
                return true;
            }
            final int maxSearches = calcMaxGrubs(skill.getKnowledge(0.0), null);
            time = Actions.getQuickActionTime(performer, skill, null, 0.0);
            act.setNextTick(time);
            act.setTickCount(1);
            act.setData(0L);
            final float totalTime = time * maxSearches;
            try {
                performer.getCurrentAction().setTimeLeft((int)totalTime);
            }
            catch (NoSuchActionException nsa) {
                Terraforming.logger.log(Level.INFO, "This action does not exist?", nsa);
            }
            performer.getCommunicator().sendNormalServerMessage("You start to search the tile for feathers.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to search a tile for feathers.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[935].getVerbString(), true, (int)totalTime);
            performer.getStatus().modifyStamina(-500.0f);
        }
        if (counter * 10.0f >= act.getNextTick()) {
            if (act.getRarity() != 0) {
                performer.playPersonalSound("sound.fx.drumroll");
            }
            final int searchCount = act.getTickCount();
            final int maxSearches2 = calcMaxGrubs(skill.getKnowledge(0.0), null);
            act.incTickCount();
            act.incNextTick(Actions.getQuickActionTime(performer, skill, null, 0.0));
            final int knowledge = (int)skill.getKnowledge(0.0);
            performer.getStatus().modifyStamina(-1500 * searchCount);
            if (searchCount >= maxSearches2) {
                toReturn = true;
            }
            act.setData(act.getData() + 1L);
            final double bonus = 0.0;
            final double diff = skill.getKnowledge(0.0) - 10.0 + searchCount * 5;
            final double power = skill.skillCheck(diff, null, bonus, false, counter / searchCount);
            try {
                final float ql = knowledge + (100 - knowledge) * ((float)power / 500.0f);
                final Item feather = ItemFactory.createItem(1352, Math.max(1.0f, ql), act.getRarity(), null);
                if (ql < 0.0f) {
                    feather.setDamage(-ql / 2.0f);
                }
                performer.getInventory().insertItem(feather);
                SoundPlayer.playSound("sound.forest.branchsnap", tilex, tiley, true, 3.0f);
                if (searchCount == 1) {
                    Server.setGrubs(tilex, tiley, false);
                }
                performer.getCommunicator().sendNormalServerMessage("You find " + feather.getNameWithGenus() + " on the tile.");
                Server.getInstance().broadCastAction(performer.getName() + " finds a " + feather.getName() + " on the tile.", performer, 5);
                if (searchCount < maxSearches2 && !performer.getInventory().mayCreatureInsertItem()) {
                    performer.getCommunicator().sendNormalServerMessage("Your inventory is now full. You would have no space to put whatever you find.");
                    return true;
                }
                if (ql < 0.0f && searchCount < maxSearches2) {
                    performer.getCommunicator().sendNormalServerMessage("You make such a mess, you stop searching.");
                    return true;
                }
            }
            catch (NoSuchTemplateException nst) {
                Terraforming.logger.log(Level.WARNING, "No template for 1364", nst);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any feathers. You realize something is wrong with the world.");
            }
            catch (FailedException fe) {
                Terraforming.logger.log(Level.WARNING, fe.getMessage(), fe);
                performer.getCommunicator().sendNormalServerMessage("You fail to find any feathers. You realize something is wrong with the world.");
            }
            if (searchCount < maxSearches2) {
                act.setRarity(performer.getRarity());
            }
        }
        return toReturn;
    }
    
    static boolean pruneHedge(final Action action, final Creature performer, final Item sickle, final Fence hedge, final boolean onSurface, final float counter) {
        boolean toReturn = true;
        final boolean insta = sickle.getTemplateId() == 176 && performer.getPower() >= 2;
        if (sickle.getTemplateId() == 267 || insta) {
            if (!hedge.isHedge()) {
                performer.getCommunicator().sendNormalServerMessage("It does not make sense to prune that.");
                return true;
            }
            if (hedge.isLowHedge()) {
                performer.getCommunicator().sendNormalServerMessage("The hedge is too low to be pruned.");
                return true;
            }
            toReturn = false;
            int time = 1;
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill sickskill = performer.getSkills().getSkillOrLearn(10046);
            if (sickle.getTemplateId() == 267) {
                time = Actions.getStandardActionTime(performer, forestry, sickle, sickskill.getKnowledge(0.0));
            }
            if (counter == 1.0f && !insta) {
                performer.getCommunicator().sendNormalServerMessage("You start to prune the hedge.");
                Server.getInstance().broadCastAction(performer.getName() + " starts to prune the hedge.", performer, 5);
                performer.sendActionControl(Actions.actionEntrys[373].getVerbString(), true, time);
            }
            if (action.justTickedSecond() && sickle.getTemplateId() == 267) {
                sickle.setDamage(sickle.getDamage() + 3.0E-4f * sickle.getDamageModifier());
            }
            if (counter * 10.0f >= time) {
                double bonus = 0.0;
                double power = 0.0;
                bonus = Math.max(1.0, sickskill.skillCheck(1.0, sickle, 0.0, false, counter));
                power = forestry.skillCheck(forestry.getKnowledge(0.0) - 10.0, sickle, bonus, false, counter);
                toReturn = true;
                SoundPlayer.playSound("sound.forest.branchsnap", hedge.getTileX(), hedge.getTileY(), onSurface, 2.0f);
                if (power < 0.0) {
                    performer.getCommunicator().sendNormalServerMessage("You make a lot of errors and need to take a break.");
                    return toReturn;
                }
                hedge.setDamage(0.0f);
                hedge.setType(StructureConstantsEnum.getEnumByValue((byte)(hedge.getType().value - 1)));
                try {
                    hedge.save();
                    final VolaTile tile = Zones.getTileOrNull(hedge.getTileX(), hedge.getTileY(), hedge.isOnSurface());
                    if (tile != null) {
                        tile.updateFence(hedge);
                    }
                }
                catch (IOException iox) {
                    Terraforming.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
                TileEvent.log(hedge.getTileX(), hedge.getTileY(), 0, performer.getWurmId(), 373);
                performer.getCommunicator().sendNormalServerMessage("You prune the hedge.");
                Server.getInstance().broadCastAction(performer.getName() + " prunes the hedge.", performer, 5);
            }
        }
        return toReturn;
    }
    
    static boolean chopHedge(final Action act, final Creature performer, final Item tool, final Fence hedge, final boolean onSurface, final float counter) {
        boolean toReturn = true;
        final boolean insta = tool.getTemplateId() == 176 && performer.getPower() >= 2;
        if (!tool.isWeaponSlash() && tool.getTemplateId() != 24 && !insta) {
            return true;
        }
        if (!hedge.isHedge()) {
            performer.getCommunicator().sendNormalServerMessage("It does not make sense to chop that.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)96, hedge.getTileX(), hedge.getTileY())) {
            return true;
        }
        toReturn = false;
        int time = 1;
        try {
            final Skill forestry = performer.getSkills().getSkillOrLearn(10048);
            final Skill primskill = performer.getSkills().getSkillOrLearn(tool.getPrimarySkill());
            int hedgeAge = 4;
            if (hedge.isMediumHedge()) {
                hedgeAge = 9;
            }
            if (hedge.isHighHedge()) {
                hedgeAge = 14;
            }
            float qualityLevel = 0.0f;
            if (counter == 1.0f && !insta) {
                time = (int)(calcTime(hedgeAge, tool, primskill, forestry) * Actions.getStaminaModiferFor(performer, 20000));
                time = Math.min(65535, time);
                act.setTimeLeft(time);
                performer.getCommunicator().sendNormalServerMessage("You start to cut down the " + hedge.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to cut down the " + hedge.getName() + ".", performer, 5);
                act.setActionString("cutting down " + hedge.getName());
                performer.sendActionControl("cutting down " + hedge.getName(), true, time);
                performer.getStatus().modifyStamina(-1500.0f);
                if (tool.isWeaponAxe()) {
                    tool.setDamage(tool.getDamage() + 0.001f * tool.getDamageModifier());
                }
                else {
                    tool.setDamage(tool.getDamage() + 0.0025f * tool.getDamageModifier());
                }
            }
            else if (!insta) {
                time = act.getTimeLeft();
                if (act.justTickedSecond() && ((time < 50 && act.currentSecond() % 2 == 0) || act.currentSecond() % 5 == 0)) {
                    performer.getStatus().modifyStamina(-6000.0f);
                    if (tool.isWeaponAxe()) {
                        tool.setDamage(tool.getDamage() + 0.001f * tool.getDamageModifier());
                    }
                    else {
                        tool.setDamage(tool.getDamage() + 0.0025f * tool.getDamageModifier());
                    }
                }
                if (act.justTickedSecond() && counter * 10.0f < time - 30) {
                    if (tool.getTemplateId() != 24) {
                        if ((act.currentSecond() - 2) % 3 == 0) {
                            String sstring = "sound.work.woodcutting1";
                            final int x = Server.rand.nextInt(3);
                            if (x == 0) {
                                sstring = "sound.work.woodcutting2";
                            }
                            else if (x == 1) {
                                sstring = "sound.work.woodcutting3";
                            }
                            SoundPlayer.playSound(sstring, hedge.getTileX(), hedge.getTileY(), performer.isOnSurface(), 1.0f);
                        }
                    }
                    else if ((act.currentSecond() - 2) % 6 == 0 && counter * 10.0f < time - 50) {
                        final String sstring = "sound.work.carpentry.saw";
                        SoundPlayer.playSound("sound.work.carpentry.saw", hedge.getTileX(), hedge.getTileY(), performer.isOnSurface(), 1.0f);
                    }
                }
            }
            if (counter * 10.0f > time || insta) {
                toReturn = true;
                double bonus = 0.0;
                final double hedgeDifficulty = hedge.getDifficulty();
                float skillTick = 0.0f;
                if (Servers.localServer.challengeServer) {
                    skillTick = Math.min(20.0f, counter);
                }
                else {
                    skillTick = Math.min(20.0f, counter);
                }
                if (tool.getTemplateId() == 7 || tool.getTemplateId() == 24) {
                    bonus = Math.max(0.0, primskill.skillCheck(hedgeDifficulty, tool, 0.0, false, skillTick));
                }
                else {
                    bonus = Math.max(0.0, primskill.skillCheck(hedgeDifficulty, tool, 0.0, primskill.getKnowledge(0.0) > 20.0, skillTick));
                }
                qualityLevel = Math.max(1.0f, (float)forestry.skillCheck(hedgeDifficulty, tool, bonus, false, skillTick));
                final double imbueEnhancement = 1.0 + 0.23047 * tool.getSkillSpellImprovement(1007) / 100.0;
                final double woodc = forestry.getKnowledge(0.0) * imbueEnhancement;
                if (woodc < qualityLevel) {
                    qualityLevel = (float)woodc;
                }
                if (qualityLevel == 1.0f && imbueEnhancement > 1.0) {
                    qualityLevel = (float)(1.0 + Server.rand.nextFloat() * 10.0f * imbueEnhancement);
                }
                if (tool.getSpellEffects() != null) {
                    qualityLevel *= tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                }
                qualityLevel += tool.getRarity();
                final Skill strength = performer.getSkills().getSkillOrLearn(102);
                double damage = Weapon.getModifiedDamageForWeapon(tool, strength) * 2.0;
                if (tool.getTemplateId() == 7) {
                    damage = tool.getCurrentQualityLevel();
                    damage *= 1.0 + strength.getKnowledge(0.0) / 100.0;
                    damage *= (50.0f + Server.rand.nextFloat() * 50.0f) / 100.0f;
                }
                if (insta) {
                    damage = 100.0;
                }
                else {
                    damage += (15 - hedgeAge) / 15.0f * 100.0f;
                }
                final boolean destroyed = hedge.setDamage(hedge.getDamage() + (float)damage);
                if (destroyed) {
                    performer.getCommunicator().sendNormalServerMessage("You cut down the " + hedge.getName() + ".");
                    if (!insta) {
                        Server.getInstance().broadCastAction(performer.getName() + " cuts down the " + hedge.getName() + ".", performer, 5);
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You chip away some wood from the " + hedge.getName() + ".");
                    if (!insta) {
                        Server.getInstance().broadCastAction(performer.getName() + " chips away some wood from the " + hedge.getName() + ".", performer, 5);
                    }
                }
            }
        }
        catch (NoSuchSkillException ex) {
            Terraforming.logger.log(Level.WARNING, ex.getMessage(), ex);
            toReturn = true;
        }
        return toReturn;
    }
    
    static void rampantGrowth(final Creature performer, final int tilex, final int tiley) {
        Terraforming.logger.log(Level.INFO, performer.getName() + " creates trees and bushes at " + tilex + ", " + tiley);
        if (performer.getLogger() != null) {
            performer.getLogger().log(Level.INFO, "Creates trees and bushes at " + tilex + ", " + tiley);
        }
        for (int x = tilex - 5; x < tilex + 5; ++x) {
            for (int y = tiley - 5; y < tiley + 5; ++y) {
                final int t = Server.surfaceMesh.getTile(x, y);
                if (Tiles.decodeHeight(t) > 0 && (Tiles.decodeType(t) == Tiles.Tile.TILE_DIRT.id || Tiles.decodeType(t) == Tiles.Tile.TILE_GRASS.id || Tiles.decodeType(t) == Tiles.Tile.TILE_SAND.id) && Server.rand.nextInt(3) == 0) {
                    int type = 0;
                    if (Server.rand.nextBoolean()) {
                        type = Server.rand.nextInt(BushData.BushType.getLength());
                        Terraforming.newType = BushData.BushType.fromInt(type).asNormalBush();
                    }
                    else {
                        type = Server.rand.nextInt(TreeData.TreeType.getLength());
                        Terraforming.newType = TreeData.TreeType.fromInt(type).asNormalTree();
                    }
                    final byte tage = (byte)Server.rand.nextInt(FoliageAge.OVERAGED.getAgeId());
                    final byte grasslen = (byte)(Server.rand.nextInt(3) + 1);
                    Server.setSurfaceTile(x, y, Tiles.decodeHeight(t), Terraforming.newType, Tiles.encodeTreeData(tage, false, false, grasslen));
                    Players.getInstance().sendChangedTile(x, y, true, false);
                }
            }
        }
    }
    
    public static int[] getCaveOpeningCoords(final int tilex, final int tiley) {
        if (tilex > 0 && tilex < Zones.worldTileSizeX - 1 && tiley > 0 && tiley < Zones.worldTileSizeY - 1) {
            if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_HOLE.id) {
                return new int[] { tilex, tiley };
            }
            if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex - 1, tiley)) == Tiles.Tile.TILE_HOLE.id) {
                return new int[] { tilex - 1, tiley };
            }
            if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex + 1, tiley)) == Tiles.Tile.TILE_HOLE.id) {
                return new int[] { tilex + 1, tiley };
            }
            if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley - 1)) == Tiles.Tile.TILE_HOLE.id) {
                return new int[] { tilex, tiley - 1 };
            }
            if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley + 1)) == Tiles.Tile.TILE_HOLE.id) {
                return new int[] { tilex, tiley + 1 };
            }
        }
        return Terraforming.noCaveDoor;
    }
    
    public static Set<int[]> getAllMineDoors(final int tilex, final int tiley) {
        if (tilex > 0 && tilex < Zones.worldTileSizeX - 1 && tiley > 0 && tiley < Zones.worldTileSizeY - 1 && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex - 1, tiley)))) {
            final Set<int[]> toReturn = new HashSet<int[]>();
            toReturn.add(new int[] { tilex - 1, tiley });
            return getEastMineDoor(tilex, tiley, toReturn);
        }
        return getEastMineDoor(tilex, tiley, null);
    }
    
    public static Set<int[]> getEastMineDoor(final int tilex, final int tiley, @Nullable Set<int[]> toReturn) {
        if (tilex > 0 && tilex < Zones.worldTileSizeX - 1 && tiley > 0 && tiley < Zones.worldTileSizeY - 1 && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex + 1, tiley)))) {
            if (toReturn == null) {
                toReturn = new HashSet<int[]>();
            }
            toReturn.add(new int[] { tilex + 1, tiley });
        }
        return getNorthMineDoor(tilex, tiley, toReturn);
    }
    
    public static Set<int[]> getNorthMineDoor(final int tilex, final int tiley, @Nullable Set<int[]> toReturn) {
        if (tilex > 0 && tilex < Zones.worldTileSizeX - 1 && tiley > 0 && tiley < Zones.worldTileSizeY - 1 && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley - 1)))) {
            if (toReturn == null) {
                toReturn = new HashSet<int[]>();
            }
            toReturn.add(new int[] { tilex, tiley - 1 });
        }
        return getSouthMineDoor(tilex, tiley, toReturn);
    }
    
    public static Set<int[]> getSouthMineDoor(final int tilex, final int tiley, @Nullable Set<int[]> toReturn) {
        if (tilex > 0 && tilex < Zones.worldTileSizeX - 1 && tiley > 0 && tiley < Zones.worldTileSizeY - 1 && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley + 1)))) {
            if (toReturn == null) {
                toReturn = new HashSet<int[]>();
            }
            toReturn.add(new int[] { tilex, tiley + 1 });
        }
        return toReturn;
    }
    
    public static final float getDamageModifierForItem(final Item item, final byte tileid) {
        float mod = 0.0f;
        if (tileid == Tiles.Tile.TILE_MINE_DOOR_WOOD.id) {
            if (item.isWeaponAxe()) {
                mod = 0.03f;
            }
            else if (item.isWeaponCrush()) {
                mod = 0.02f;
            }
            else if (item.isWeaponSlash()) {
                mod = 0.015f;
            }
            else if (item.isWeaponPierce()) {
                mod = 0.01f;
            }
            else if (item.isWeaponMisc()) {
                mod = 0.007f;
            }
        }
        else if (tileid == Tiles.Tile.TILE_MINE_DOOR_STONE.id) {
            if (item.getTemplateId() == 20) {
                mod = 0.015f;
            }
            else if (item.isWeaponCrush()) {
                mod = 0.01f;
            }
            else if (item.isWeaponAxe()) {
                mod = 0.005f;
            }
            else if (item.isWeaponSlash()) {
                mod = 0.001f;
            }
            else if (item.isWeaponPierce()) {
                mod = 0.001f;
            }
            else if (item.isWeaponMisc()) {
                mod = 0.001f;
            }
        }
        else if (tileid == Tiles.Tile.TILE_MINE_DOOR_GOLD.id) {
            if (item.getTemplateId() == 20) {
                mod = 0.012f;
            }
            else if (item.isWeaponCrush()) {
                mod = 0.007f;
            }
            else if (item.isWeaponAxe()) {
                mod = 0.002f;
            }
            else if (item.isWeaponSlash()) {
                mod = 8.0E-4f;
            }
            else if (item.isWeaponPierce()) {
                mod = 8.0E-4f;
            }
            else if (item.isWeaponMisc()) {
                mod = 8.0E-4f;
            }
        }
        else if (tileid == Tiles.Tile.TILE_MINE_DOOR_SILVER.id) {
            if (item.getTemplateId() == 20) {
                mod = 0.012f;
            }
            else if (item.isWeaponCrush()) {
                mod = 0.007f;
            }
            else if (item.isWeaponAxe()) {
                mod = 0.002f;
            }
            else if (item.isWeaponSlash()) {
                mod = 8.0E-4f;
            }
            else if (item.isWeaponPierce()) {
                mod = 8.0E-4f;
            }
            else if (item.isWeaponMisc()) {
                mod = 8.0E-4f;
            }
        }
        else if (tileid == Tiles.Tile.TILE_MINE_DOOR_STEEL.id) {
            if (item.getTemplateId() == 20) {
                mod = 0.01f;
            }
            else if (item.isWeaponCrush()) {
                mod = 0.005f;
            }
            else if (item.isWeaponAxe()) {
                mod = 0.001f;
            }
            else if (item.isWeaponSlash()) {
                mod = 6.0E-4f;
            }
            else if (item.isWeaponPierce()) {
                mod = 6.0E-4f;
            }
            else if (item.isWeaponMisc()) {
                mod = 1.0E-4f;
            }
        }
        return mod;
    }
    
    public static final boolean removeMineDoor(final Creature performer, final Action act, final Item destroyItem, final int tilex, final int tiley, final boolean onSurface, final float counter) {
        if (!Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)))) {
            return true;
        }
        int time = 600;
        final boolean toReturn = false;
        final boolean insta = destroyItem.isWand();
        if (!onSurface) {
            performer.getCommunicator().sendNormalServerMessage("You need to do this from the outside.");
            return true;
        }
        if (counter == 1.0f) {
            if (!insta) {
                final Skills skills = performer.getSkills();
                try {
                    final Skill str = skills.getSkill(102);
                    if (str.getRealKnowledge() <= 21.0) {
                        performer.getCommunicator().sendNormalServerMessage("You are too weak to do that.");
                        return true;
                    }
                }
                catch (NoSuchSkillException nss) {
                    Terraforming.logger.log(Level.WARNING, "Weird, " + performer.getName() + " has no strength!");
                    performer.getCommunicator().sendNormalServerMessage("You are too weak to do that.");
                    return true;
                }
            }
            performer.getCommunicator().sendNormalServerMessage("You start to remove the door.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to remove a door.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[906].getVerbString(), true, time);
            act.setTimeLeft(time);
            performer.getStatus().modifyStamina(-1000.0f);
        }
        else {
            time = act.getTimeLeft();
        }
        if (act.currentSecond() % 5 == 0) {
            MethodsStructure.sendDestroySound(performer, destroyItem, Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == 25);
            performer.getStatus().modifyStamina(-5000.0f);
        }
        if (counter * 10.0f > time || insta) {
            final int currQl = Server.getWorldResource(tilex, tiley) / 100;
            try {
                final byte tile = Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley));
                final int doorType = getMineDoorTemplateForTile(tile);
                final Item removed = ItemFactory.createItem(doorType, Math.min(100.0f, Math.max(1.0f, currQl)), (byte)0, null);
                performer.getInventory().insertItem(removed);
            }
            catch (Exception ex) {
                Terraforming.logger.log(Level.SEVERE, "Factory failed to produce minedoor for " + performer.getName(), ex);
            }
            TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
            TileEvent.log(tilex, tiley, -1, performer.getWurmId(), act.getNumber());
            if (Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_HOLE.id, (byte)0);
            }
            else {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_ROCK.id, (byte)0);
            }
            Players.getInstance().sendChangedTile(tilex, tiley, true, true);
            try {
                final MineDoorPermission md = MineDoorPermission.getPermission(tilex, tiley);
                Zones.getZone(tilex, tiley, true).getOrCreateTile(tilex, tiley).removeMineDoor(md);
            }
            catch (NoSuchZoneException e) {
                Terraforming.logger.log(Level.WARNING, "Zone for mine door removal not found");
            }
            MineDoorPermission.deleteMineDoor(tilex, tiley);
            performer.getCommunicator().sendNormalServerMessage("You remove the mine door from the opening.");
            return true;
        }
        return false;
    }
    
    public static final boolean destroyMineDoor(final Creature performer, final Action act, final Item destroyItem, final int tilex, final int tiley, final boolean onSurface, final float counter) {
        if (!Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)))) {
            return true;
        }
        boolean toReturn = true;
        int time = 300;
        if (Servers.localServer.isChallengeServer()) {
            time = 100;
        }
        float mod = 1.0f;
        if (destroyItem == null && !(performer instanceof Player)) {
            mod = 0.003f;
        }
        else if (destroyItem != null) {
            mod = getDamageModifierForItem(destroyItem, Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)));
        }
        else {
            mod = 0.0f;
        }
        final boolean insta = destroyItem.isWand();
        if (!performer.isWithinDistanceTo(tilex, tiley, 1)) {
            performer.getCommunicator().sendNormalServerMessage("You are too far away from the mine door.");
            return true;
        }
        if (!onSurface) {
            performer.getCommunicator().sendNormalServerMessage("You need to do this from the outside.");
            return true;
        }
        if (mod <= 0.0f && !insta) {
            performer.getCommunicator().sendNormalServerMessage("You will not do any damage to the door with that.");
            return true;
        }
        toReturn = false;
        if (counter == 1.0f) {
            if (!insta) {
                final Skills skills = performer.getSkills();
                try {
                    final Skill str = skills.getSkill(102);
                    if (str.getRealKnowledge() <= 21.0) {
                        performer.getCommunicator().sendNormalServerMessage("You are too weak to do that.");
                        return true;
                    }
                }
                catch (NoSuchSkillException nss) {
                    Terraforming.logger.log(Level.WARNING, "Weird, " + performer.getName() + " has no strength!");
                    performer.getCommunicator().sendNormalServerMessage("You are too weak to do that.");
                    return true;
                }
            }
            performer.getCommunicator().sendNormalServerMessage("You start to destroy the door.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to destroy a door.", performer, 5);
            performer.sendActionControl(Actions.actionEntrys[82].getVerbString(), true, time);
            act.setTimeLeft(time);
            performer.getStatus().modifyStamina(-1000.0f);
        }
        else {
            time = act.getTimeLeft();
        }
        if (act.currentSecond() % 5 == 0) {
            MethodsStructure.sendDestroySound(performer, destroyItem, Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == 25);
            performer.getStatus().modifyStamina(-5000.0f);
            if (destroyItem != null && !destroyItem.isBodyPartAttached()) {
                destroyItem.setDamage(destroyItem.getDamage() + mod * destroyItem.getDamageModifier());
            }
        }
        if (counter * 10.0f > time || insta) {
            final Skills skills = performer.getSkills();
            Skill destroySkill = null;
            try {
                destroySkill = skills.getSkill(102);
            }
            catch (NoSuchSkillException nss2) {
                destroySkill = skills.learn(102, 1.0f);
            }
            destroySkill.skillCheck(20.0, destroyItem, 0.0, false, counter);
            toReturn = true;
            double damage = 1.0;
            int currQl = Server.getWorldResource(tilex, tiley);
            if (insta && mod == 0.0f) {
                damage = 20.0;
            }
            else if (destroyItem != null) {
                final boolean iswood = Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_MINE_DOOR_WOOD.id;
                if (iswood && destroyItem.isCarpentryTool()) {
                    damage = 100.0 * (1.0 + destroySkill.getKnowledge(0.0) / 100.0);
                }
                else {
                    damage = Weapon.getModifiedDamageForWeapon(destroyItem, destroySkill) * 2.0;
                }
                damage /= currQl / 20.0f;
                final VolaTile t = Zones.getOrCreateTile(tilex, tiley, true);
                final Village vill = t.getVillage();
                if (vill != null) {
                    if (MethodsStructure.isCitizenAndMayPerformAction((short)82, performer, vill)) {
                        damage *= 50.0;
                    }
                    else if (MethodsStructure.isAllyAndMayPerformAction((short)82, performer, vill)) {
                        damage *= 25.0;
                    }
                    else if (!vill.isChained()) {
                        damage *= 3.0;
                    }
                }
                else {
                    damage *= 5.0;
                }
                damage *= Weapon.getMaterialBashModifier(destroyItem.getMaterial());
                if (performer.getCultist() != null && performer.getCultist().doubleStructDamage()) {
                    damage *= 2.0;
                }
                damage = (float)(damage * mod * 100.0);
            }
            damage *= 100.0;
            if (Servers.localServer.isChallengeServer()) {
                damage *= 2.5;
            }
            currQl = (int)Math.max(0.0, currQl - damage);
            Server.setWorldResource(tilex, tiley, currQl);
            if (currQl == 0) {
                TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
                TileEvent.log(tilex, tiley, -1, performer.getWurmId(), act.getNumber());
                if (Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_HOLE.id, (byte)0);
                }
                else {
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), Tiles.Tile.TILE_ROCK.id, (byte)0);
                }
                Players.getInstance().sendChangedTile(tilex, tiley, true, true);
                try {
                    final MineDoorPermission md = MineDoorPermission.getPermission(tilex, tiley);
                    Zones.getZone(tilex, tiley, true).getOrCreateTile(tilex, tiley).removeMineDoor(md);
                }
                catch (NoSuchZoneException e) {
                    Terraforming.logger.log(Level.WARNING, "Zone for mine door removal not found");
                }
                MineDoorPermission.deleteMineDoor(tilex, tiley);
                performer.getCommunicator().sendNormalServerMessage("The last parts of the door fall down with a crash.");
                Server.getInstance().broadCastAction(performer.getName() + " damages a door and the last parts fall down with a crash.", performer, 5);
                if (performer.getDeity() != null) {
                    performer.performActionOkey(act);
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You damage the door.");
                Server.getInstance().broadCastAction(performer.getName() + " damages the door.", performer, 5);
            }
        }
        return toReturn;
    }
    
    public static final boolean isAltarBlocking(final Creature performer, final int tilex, final int tiley) {
        EndGameItem alt = EndGameItems.getEvilAltar();
        if (alt != null) {
            final int maxnorth = Zones.safeTileY(tiley - 20);
            final int maxsouth = Zones.safeTileY(tiley + 20);
            final int maxeast = Zones.safeTileX(tilex - 20);
            final int maxwest = Zones.safeTileX(tilex + 20);
            if (alt.getItem() != null && (int)alt.getItem().getPosX() >> 2 < maxwest && (int)alt.getItem().getPosX() >> 2 > maxeast && (int)alt.getItem().getPosY() >> 2 < maxsouth && (int)alt.getItem().getPosY() >> 2 > maxnorth) {
                return true;
            }
        }
        alt = EndGameItems.getGoodAltar();
        if (alt != null) {
            final int maxnorth = Zones.safeTileY(tiley - 20);
            final int maxsouth = Zones.safeTileY(tiley + 20);
            final int maxeast = Zones.safeTileX(tilex - 20);
            final int maxwest = Zones.safeTileX(tilex + 20);
            if (alt.getItem() != null && (int)alt.getItem().getPosX() >> 2 < maxwest && (int)alt.getItem().getPosX() >> 2 > maxeast && (int)alt.getItem().getPosY() >> 2 < maxsouth && (int)alt.getItem().getPosY() >> 2 > maxnorth) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean buildMineDoor(final Creature performer, final Item source, final Action act, final int tilex, final int tiley, final boolean onSurface, final float counter) {
        boolean done = true;
        if (Tiles.decodeType(Server.surfaceMesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_HOLE.id) {
            if (!performer.isOnSurface()) {
                performer.getCommunicator().sendNormalServerMessage("You need to do this from the outside.");
                return true;
            }
            if (performer.getPower() < 5) {
                if (Zones.isInPvPZone(tilex, tiley)) {
                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to build this in the PvP zone.");
                    return true;
                }
                if (getCaveDoorDifference(Server.surfaceMesh.getTile(tilex, tiley), tilex, tiley) > 90) {
                    performer.getCommunicator().sendNormalServerMessage("That hole is too big to be covered.");
                    return true;
                }
                if (isTileModBlocked(performer, tilex, tiley, true)) {
                    return true;
                }
                if (isAltarBlocking(performer, tilex, tiley)) {
                    performer.getCommunicator().sendSafeServerMessage("You cannot build here, since this is holy ground.");
                    return true;
                }
                if (!Methods.isActionAllowed(performer, (short)363)) {
                    return true;
                }
            }
            if (source.isMineDoor() || source.isWand()) {
                done = false;
                final boolean insta = performer.getPower() > 0;
                if (counter == 1.0f && !insta) {
                    final Skills skills = performer.getSkills();
                    try {
                        final Skill str = skills.getSkill(1008);
                        if (source.getTemplateId() != 592 && str.getRealKnowledge() <= 21.0) {
                            performer.getCommunicator().sendNormalServerMessage("You do not know how to do that effectively.");
                            return true;
                        }
                    }
                    catch (NoSuchSkillException nss) {
                        performer.getCommunicator().sendNormalServerMessage("You do not know how to do that effectively.");
                        return true;
                    }
                    performer.getCommunicator().sendNormalServerMessage("You start to fit the door in the entrance.");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to fit a door in the entrance.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[363].getVerbString(), true, 150);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                if (act.currentSecond() % 5 == 0) {
                    performer.getStatus().modifyStamina(-5000.0f);
                }
                if (act.mayPlaySound()) {
                    String s = (Server.rand.nextInt(2) == 0) ? "sound.work.carpentry.mallet1" : "sound.work.carpentry.mallet2";
                    if (source.isStone()) {
                        s = "sound.work.masonry";
                    }
                    if (source.isMetal()) {
                        s = "sound.work.smithing.metal";
                    }
                    SoundPlayer.playSound(s, performer, 1.0f);
                }
                if (counter > 15.0f || insta) {
                    final Skills skills = performer.getSkills();
                    Skill mining = null;
                    try {
                        mining = skills.getSkill(1008);
                        mining.skillCheck(20.0, source.getQualityLevel(), false, 15.0f);
                    }
                    catch (NoSuchSkillException ex) {}
                    if (!flattenTopMineBorder(performer, tilex, tiley)) {
                        performer.getCommunicator().sendNormalServerMessage("Mine door cannot be placed as the upper part of the entrance is not flat.");
                        return true;
                    }
                    if (MineDoorPermission.getPermission(tilex, tiley) != null) {
                        MineDoorPermission.deleteMineDoor(tilex, tiley);
                    }
                    final Village vill = Villages.getVillage(tilex, tiley, onSurface);
                    Server.setWorldResource(tilex, tiley, Math.max(1, (int)source.getCurrentQualityLevel() * 100));
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)), getNewTileTypeForMineDoor(source.getTemplateId()), (byte)0);
                    TileEvent.log(tilex, tiley, 0, performer.getWurmId(), act.getNumber());
                    TileEvent.log(tilex, tiley, -1, performer.getWurmId(), act.getNumber());
                    Players.getInstance().sendChangedTile(tilex, tiley, true, false);
                    new MineDoorPermission(tilex, tiley, performer.getWurmId(), vill, false, false, "", 0);
                    if (source.isMineDoor()) {
                        Items.destroyItem(source.getWurmId());
                    }
                    return true;
                }
            }
        }
        return done;
    }
    
    private static final boolean flattenTopMineBorder(final Creature performer, final int tilex, final int tiley) {
        final Point highestCorner = TileRockBehaviour.findHighestCorner(tilex, tiley);
        if (highestCorner == null) {
            return false;
        }
        final Point nextHighestCorner = TileRockBehaviour.findNextHighestCorner(tilex, tiley, highestCorner);
        if (nextHighestCorner == null) {
            return false;
        }
        if (nextHighestCorner.getH() != highestCorner.getH() && TileRockBehaviour.isStructureNear(highestCorner.getX(), highestCorner.getY())) {
            return false;
        }
        final short targetUpperHeight = (short)nextHighestCorner.getH();
        short tileData = Tiles.decodeTileData(Server.surfaceMesh.getTile(highestCorner.getX(), highestCorner.getY()));
        Server.surfaceMesh.setTile(highestCorner.getX(), highestCorner.getY(), Tiles.encode(targetUpperHeight, tileData));
        tileData = Tiles.decodeTileData(Server.rockMesh.getTile(highestCorner.getX(), highestCorner.getY()));
        Server.rockMesh.setTile(highestCorner.getX(), highestCorner.getY(), Tiles.encode(targetUpperHeight, tileData));
        tileData = Tiles.decodeTileData(Server.caveMesh.getTile(highestCorner.getX(), highestCorner.getY()));
        Players.getInstance().sendChangedTile(highestCorner.getX(), highestCorner.getY(), true, true);
        Players.getInstance().sendChangedTile(highestCorner.getX(), highestCorner.getY(), false, true);
        Players.getInstance().sendChangedTile(tilex, tiley, true, true);
        return true;
    }
    
    public static final int getMineDoorTemplateForTile(final byte tile) {
        if (tile == 27) {
            return 594;
        }
        if (tile == 25) {
            return 592;
        }
        if (tile == 26) {
            return 593;
        }
        if (tile == 28) {
            return 595;
        }
        if (tile == 29) {
            return 596;
        }
        return -1;
    }
    
    public static final byte getNewTileTypeForMineDoor(final int templateId) {
        if (templateId == 594) {
            return 27;
        }
        if (templateId == 592) {
            return 25;
        }
        if (templateId == 593) {
            return 26;
        }
        if (templateId == 595) {
            return 28;
        }
        if (templateId == 596) {
            return 29;
        }
        if (templateId == 315) {
            return 25;
        }
        if (templateId == 176) {
            return 25;
        }
        return 0;
    }
    
    public static final boolean enchantNature(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, final Action act) {
        boolean done = true;
        if (Methods.isActionAllowed(performer, (short)384) && performer.getCultist() != null && performer.getCultist().mayEnchantNature()) {
            Terraforming.oldType = Tiles.decodeType(tile);
            final Tiles.Tile oldTile = Tiles.getTile(Terraforming.oldType);
            Terraforming.newType = Tiles.Tile.TILE_ENCHANTED_GRASS.id;
            final byte oldData = Tiles.decodeData(tile);
            if (Terraforming.oldType == Tiles.Tile.TILE_KELP.id || Terraforming.oldType == Tiles.Tile.TILE_REED.id || Terraforming.oldType == Tiles.Tile.TILE_LAWN.id || Terraforming.oldType == Tiles.Tile.TILE_BUSH_LINGONBERRY.id) {
                performer.getCommunicator().sendNormalServerMessage("The area refuses to accept your love.");
                return true;
            }
            if (Terraforming.oldType == Tiles.Tile.TILE_GRASS.id || oldTile.isNormalTree() || oldTile.isNormalBush()) {
                done = false;
                final BlockingResult result = Blocking.getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), (tilex << 2) + 2, (tiley << 2) + 2, performer.getPositionZ(), Zones.getHeightForNode(tilex, tiley, 0), true, true, false, 5, -1L, performer.getBridgeId(), -10L, false);
                if (result != null) {
                    performer.getCommunicator().sendCombatNormalMessage("The " + result.getFirstBlocker().getName() + " is in the way. You fail to focus.");
                    return true;
                }
                if (counter == 1.0f) {
                    performer.getCommunicator().sendNormalServerMessage("You start to focus your love on the surroundings.");
                    Server.getInstance().broadCastAction(performer.getName() + " smiles and closes " + performer.getHisHerItsString() + " eyes.", performer, 5);
                    performer.sendActionControl(Actions.actionEntrys[388].getVerbString(), true, 50);
                    performer.getStatus().modifyStamina(-1000.0f);
                }
                else if (act.currentSecond() >= 5) {
                    TileEvent.log(tilex, tilex, 0, performer.getWurmId(), act.getNumber());
                    if (performer.getCultist() != null) {
                        performer.getCultist().touchCooldown2();
                    }
                    if (oldTile.isNormalTree()) {
                        Terraforming.newType = oldTile.getTreeType(oldData).asEnchantedTree();
                    }
                    else if (oldTile.isNormalBush()) {
                        Terraforming.newType = oldTile.getBushType(oldData).asEnchantedBush();
                    }
                    final byte newData = oldData;
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Terraforming.newType, newData);
                    performer.getCommunicator().sendNormalServerMessage("You let your love change the area.");
                    Server.getInstance().broadCastAction(performer.getName() + " changes the area with " + performer.getHisHerItsString() + " love.", performer, 5);
                    done = true;
                    performer.getMovementScheme().touchFreeMoveCounter();
                    Players.getInstance().sendChangedTile(tilex, tiley, onSurface, false);
                    return done;
                }
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You fail to enchant the spot.");
        }
        return done;
    }
    
    public static final boolean freezeLava(final Creature performer, final int tilex, final int tiley, final boolean onSurface, final int tile, final float counter, final boolean cultistSpawn) {
        final VolaTile vt = Zones.getOrCreateTile(tilex, tiley, onSurface);
        final byte type = Tiles.decodeType(tile);
        if (type != Tiles.Tile.TILE_LAVA.id && type != Tiles.Tile.TILE_CAVE_WALL_LAVA.id) {
            performer.getCommunicator().sendNormalServerMessage("The tile is not lava any longer.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)384)) {
            return true;
        }
        if (vt.getVillage() != null && !vt.getVillage().isCitizen(performer) && vt.getKingdom() == performer.getKingdomId() && !vt.getVillage().isAlly(performer)) {
            performer.getCommunicator().sendNormalServerMessage("Some psychological issue stops you from freezing the lava here. If you were an ally of the village maybe you would feel more comfortable.");
            return true;
        }
        boolean done = false;
        if (counter == 1.0f) {
            performer.getCommunicator().sendNormalServerMessage("You start concentrating on the lava.");
            Server.getInstance().broadCastAction(performer.getName() + " starts to stare intensely at the lava.", performer, 5);
            if (cultistSpawn) {
                performer.sendActionControl("Freezing", true, 1000);
            }
        }
        if (!cultistSpawn || counter > 100.0f) {
            done = true;
            if ((Tiles.decodeData(tile) & 0xFF) == 0xFF) {
                performer.getCommunicator().sendNormalServerMessage("Nothing happens with the lava.. the permanent flow from beneath is too powerful.");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The lava cools down and turns grey.");
                Server.getInstance().broadCastAction("The previously hot lava is now still and grey rock instead.", performer, 5);
                TileEvent.log(tilex, tiley, 0, performer.getWurmId(), 327);
                if (cultistSpawn) {
                    performer.getCultist().touchCooldown2();
                }
                if (type == Tiles.Tile.TILE_LAVA.id) {
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_ROCK.id, (byte)0);
                    for (int xx = 0; xx <= 1; ++xx) {
                        for (int yy = 0; yy <= 1; ++yy) {
                            try {
                                final int tempint3 = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + xx, tiley + yy));
                                Server.rockMesh.setTile(tilex + xx, tiley + yy, Tiles.encode((short)tempint3, Tiles.Tile.TILE_ROCK.id, (byte)0));
                            }
                            catch (Exception ex) {}
                        }
                    }
                    final int caveTile = Server.caveMesh.getTile(tilex, tiley);
                    final byte caveType = Tiles.decodeType(caveTile);
                    if (caveType == Tiles.Tile.TILE_CAVE_WALL_LAVA.id) {
                        setAsRock(tilex, tiley, false, false);
                    }
                    Players.getInstance().sendChangedTile(tilex, tiley, true, true);
                }
                else {
                    setAsRock(tilex, tiley, false, false);
                }
            }
        }
        return done;
    }
    
    public static boolean handleChopAction(final Action act, final Creature performer, final Item source, final int tilex, final int tiley, final boolean onSurface, final int heightOffset, final int tile, final short action, final float counter) {
        boolean done = false;
        if (!source.isWeaponSlash() && source.getTemplateId() != 24) {
            return true;
        }
        final byte tileType = Tiles.decodeType(tile);
        final Tiles.Tile theTile = Tiles.getTile(tileType);
        final byte tileData = Tiles.decodeData(tile);
        int treeAge = tileData >> 4 & 0xF;
        if (!theTile.isBush() && !theTile.isTree()) {
            return true;
        }
        if (Zones.protectedTiles[tilex][tiley]) {
            performer.getCommunicator().sendNormalServerMessage("Your muscles weaken as you try to cut down the tree. You just can't bring yourself to do it.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)96, false, tilex, tiley, tile, 0)) {
            return true;
        }
        final VolaTile vt = Zones.getTileOrNull(tilex, tiley, onSurface);
        Item hive = null;
        if (vt != null) {
            hive = vt.findHive(1239, true);
            if (hive != null && performer.getBestBeeSmoker() == null) {
                performer.getCommunicator().sendSafeServerMessage("The bees get angry and defend the wild hive by stinging you.");
                performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 5000.0f + Server.rand.nextFloat() * 7000.0f, 0.0f, 35.0f, false, false);
                return true;
            }
        }
        if (!onSurface) {
            performer.getCommunicator().sendNormalServerMessage("You can not reach that.");
            return true;
        }
        try {
            final Skill woodcutting = performer.getSkills().getSkillOrLearn(1007);
            final Skill primskill = performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
            int time = 0;
            float qualityLevel = 0.0f;
            if (counter == 1.0f) {
                time = (int)(calcTime(treeAge, source, primskill, woodcutting) * Actions.getStaminaModiferFor(performer, 20000));
                time = Math.min(65535, time);
                act.setTimeLeft(time);
                final String treeString = theTile.getTileName(tileData);
                performer.getCommunicator().sendNormalServerMessage("You start to cut down the " + treeString + ".");
                Server.getInstance().broadCastAction(performer.getName() + " starts to cut down the " + treeString + ".", performer, 5);
                act.setActionString("cutting down " + treeString);
                performer.sendActionControl("cutting down " + treeString, true, time);
                performer.getStatus().modifyStamina(-1500.0f);
                if (source.isWeaponAxe()) {
                    source.setDamage(source.getDamage() + 0.001f * source.getDamageModifier());
                }
                else {
                    source.setDamage(source.getDamage() + 0.0025f * source.getDamageModifier());
                }
            }
            else {
                time = act.getTimeLeft();
                if (act.justTickedSecond() && ((time < 50 && act.currentSecond() % 2 == 0) || act.currentSecond() % 5 == 0)) {
                    performer.getStatus().modifyStamina(-6000.0f);
                    if (source.isWeaponAxe()) {
                        source.setDamage(source.getDamage() + 0.001f * source.getDamageModifier());
                    }
                    else {
                        source.setDamage(source.getDamage() + 0.0025f * source.getDamageModifier());
                    }
                }
                if (act.justTickedSecond() && counter * 10.0f < time - 30) {
                    if (source.getTemplateId() != 24) {
                        if ((act.currentSecond() - 2) % 3 == 0) {
                            String sstring = "sound.work.woodcutting1";
                            final int x = Server.rand.nextInt(3);
                            if (x == 0) {
                                sstring = "sound.work.woodcutting2";
                            }
                            else if (x == 1) {
                                sstring = "sound.work.woodcutting3";
                            }
                            SoundPlayer.playSound(sstring, tilex, tiley, performer.isOnSurface(), 1.0f);
                        }
                    }
                    else if ((act.currentSecond() - 2) % 6 == 0 && counter * 10.0f < time - 50) {
                        final String sstring = "sound.work.carpentry.saw";
                        SoundPlayer.playSound("sound.work.carpentry.saw", tilex, tiley, performer.isOnSurface(), 1.0f);
                    }
                }
            }
            if (counter * 10.0f > time) {
                if (act.getRarity() != 0) {
                    performer.playPersonalSound("sound.fx.drumroll");
                }
                done = true;
                double bonus = 0.0;
                final int stumpAge;
                if ((stumpAge = treeAge) == 15) {
                    treeAge = 3;
                }
                double treeDifficulty = 2.0;
                float skillTick = 0.0f;
                if (Servers.localServer.challengeServer) {
                    skillTick = Math.min(20.0f, counter);
                    final int base = theTile.getWoodDificulity();
                    treeDifficulty = base * (1.0 + treeAge / 14.0);
                }
                else {
                    skillTick = Math.min(20.0f, counter);
                    treeDifficulty = 15 - treeAge;
                }
                if (source.getTemplateId() == 7 || source.getTemplateId() == 24) {
                    bonus = Math.max(0.0, primskill.skillCheck(treeDifficulty, source, 0.0, false, skillTick));
                }
                else {
                    bonus = Math.max(0.0, primskill.skillCheck(treeDifficulty, source, 0.0, primskill.getKnowledge(0.0) > 20.0, skillTick));
                }
                qualityLevel = Math.max(1.0f, (float)woodcutting.skillCheck(treeDifficulty, source, bonus, false, skillTick));
                final double imbueEnhancement = 1.0 + 0.23047 * source.getSkillSpellImprovement(1007) / 100.0;
                final double woodc = woodcutting.getKnowledge(0.0) * imbueEnhancement;
                if (woodc < qualityLevel) {
                    qualityLevel = (float)woodc;
                }
                if (qualityLevel == 1.0f && imbueEnhancement > 1.0) {
                    qualityLevel = (float)(1.0 + Server.rand.nextFloat() * 10.0f * imbueEnhancement);
                }
                if (source.getSpellEffects() != null) {
                    qualityLevel *= source.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_RESGATHERED);
                }
                qualityLevel += source.getRarity();
                final Skill strength = performer.getSkills().getSkillOrLearn(102);
                double damage = Weapon.getModifiedDamageForWeapon(source, strength) * 2.0;
                if (source.getTemplateId() == 7) {
                    damage = source.getCurrentQualityLevel();
                    damage *= 1.0 + strength.getKnowledge(0.0) / 100.0;
                    damage *= (50.0f + Server.rand.nextFloat() * 50.0f) / 100.0f;
                }
                damage += (15 - treeAge) / 15.0f * 100.0f;
                float dam = Server.getWorldResource(tilex, tiley);
                if (dam == 65535.0f) {
                    dam = 0.0f;
                }
                if (dam < 100.0f) {
                    dam += (float)damage;
                }
                final String treeString2 = theTile.getTileName(tileData);
                if (dam >= 100.0f) {
                    Server.setWorldResource(tilex, tiley, 0);
                    TileEvent.log(tilex, tiley, 0, performer.getWurmId(), action);
                    performer.getCommunicator().sendNormalServerMessage("You cut down the " + treeString2 + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " cuts down the " + treeString2 + ".", performer, 5);
                    MissionTriggers.activateTriggers(performer, source, 492, EpicServerStatus.getTileId(tilex, tiley), 1);
                    if (treeAge > 4) {
                        performer.achievement(129);
                        if (source.getTemplateId() == 25) {
                            performer.achievement(135);
                        }
                    }
                    final GrassData.GrowthTreeStage treeStage = TreeData.getGrassLength(tileData);
                    final int newGrassLength = Math.max(0, treeStage.getCode() - 1);
                    final GrassData.GrowthStage grassStage = GrassData.GrowthStage.fromInt(newGrassLength);
                    final GrassData.FlowerType flowerType = GrassData.FlowerType.NONE;
                    byte newData = GrassData.encodeGrassTileData(grassStage, flowerType);
                    byte newt = Tiles.Tile.TILE_GRASS.id;
                    if (allCornersAtRockLevel(tilex, tiley, Server.surfaceMesh)) {
                        newt = Tiles.Tile.TILE_ROCK.id;
                        newData = 0;
                    }
                    else if (theTile.isMycelium()) {
                        newt = Tiles.Tile.TILE_MYCELIUM.id;
                    }
                    else if (theTile.isEnchanted()) {
                        newt = Tiles.Tile.TILE_ENCHANTED_GRASS.id;
                        newData = 0;
                    }
                    else if (tileType == Tiles.Tile.TILE_BUSH_LINGONBERRY.id) {
                        newt = Tiles.Tile.TILE_TUNDRA.id;
                    }
                    Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), newt, newData);
                    try {
                        final Zone z = Zones.getZone(tilex, tiley, true);
                        z.changeTile(tilex, tiley);
                    }
                    catch (NoSuchZoneException ex3) {}
                    Players.getInstance().sendChangedTile(tilex, tiley, onSurface, true);
                    if (theTile.isTree()) {
                        final TreeData.TreeType treeType = theTile.getTreeType(tileData);
                        final boolean inCenter = TreeData.isCentre(tileData);
                        try {
                            final byte material = treeType.getMaterial();
                            int templateId = 9;
                            if (treeAge >= 8 && !treeType.isFruitTree()) {
                                templateId = 385;
                            }
                            double sizeMod = treeAge / 15.0;
                            if (treeType.isFruitTree()) {
                                sizeMod *= 0.25;
                            }
                            final float dir = Creature.normalizeAngle(TerrainUtilities.getTreeRotation(tilex, tiley));
                            float xNew = (tilex << 2) + 2.0f;
                            float yNew = (tiley << 2) + 2.0f;
                            if (!inCenter) {
                                xNew = (tilex << 2) + 4.0f * TerrainUtilities.getTreePosX(tilex, tiley);
                                yNew = (tiley << 2) + 4.0f * TerrainUtilities.getTreePosY(tilex, tiley);
                            }
                            final ItemTemplate t = ItemTemplateFactory.getInstance().getTemplate(templateId);
                            final int weight = (int)Math.max(1000.0, sizeMod * t.getWeightGrams());
                            if (weight < 1500) {
                                templateId = 169;
                            }
                            if (templateId == 385) {
                                SoundPlayer.playSound("sound.tree.falling", tilex, tiley, true, 1.0f);
                                final Item stump = ItemFactory.createItem(731, Math.min(100.0f, qualityLevel), xNew, yNew, dir, onSurface, material, act.getRarity(), -10L, null, (byte)stumpAge);
                                stump.setLastOwnerId(-10L);
                                stump.setWeight(weight, true);
                            }
                            final int ta = treeAge;
                            final Item newItem = ItemFactory.createItem(templateId, Math.min(100.0f, qualityLevel), xNew, yNew, dir, onSurface, material, act.getRarity(), -10L, null, (byte)ta);
                            if (templateId == 385) {
                                newItem.setAuxData((byte)treeAge);
                            }
                            if (treeAge >= 5 && performer.getDeity() != null && performer.getDeity().number == 3) {
                                performer.maybeModifyAlignment(1.0f);
                            }
                            newItem.setWeight(weight, true);
                            newItem.setLastOwnerId(performer.getWurmId());
                            if (performer.getTutorialLevel() == 3 && !performer.skippedTutorial()) {
                                if (templateId == 9) {
                                    performer.missionFinished(true, true);
                                }
                                else {
                                    final String text = "You should now chop the tree up. Right-click the " + newItem.getName() + " and chop it up. Then get the log.";
                                    final SimplePopup popup = new SimplePopup(performer, "Chop up the tree", text);
                                    popup.sendQuestion();
                                }
                            }
                        }
                        catch (Exception ex) {
                            Terraforming.logger.log(Level.WARNING, "Factory failed to produce item log", ex);
                        }
                    }
                    if (hive != null) {
                        if (performer.getBestBeeSmoker() == null) {
                            performer.addWoundOfType(null, (byte)5, 2, true, 1.0f, false, 4000.0f + Server.rand.nextFloat() * 3000.0f, 0.0f, 0.0f, false, false);
                        }
                        for (final Item item : hive.getItemsAsArray()) {
                            Items.destroyItem(item.getWurmId());
                        }
                        Items.destroyItem(hive.getWurmId());
                    }
                    PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.FELL_TREE);
                }
                else {
                    Server.setWorldResource(tilex, tiley, (short)dam);
                    performer.getCommunicator().sendNormalServerMessage("You chip away some wood from the " + treeString2 + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " chips away some wood from the " + treeString2 + ".", performer, 5);
                }
                PlayerTutorial.firePlayerTrigger(performer.getWurmId(), PlayerTutorial.PlayerTrigger.CUT_TREE);
            }
        }
        catch (NoSuchSkillException ex2) {
            Terraforming.logger.log(Level.WARNING, ex2.getMessage(), ex2);
            done = true;
        }
        catch (Exception noe) {
            Terraforming.logger.log(Level.WARNING, "Failed to chop at tree: ", noe);
            done = true;
        }
        return done;
    }
    
    static short calcTime(int treeage, final Item source, final Skill weaponSkill, final Skill woodcuttingskill) {
        if (treeage == 15) {
            treeage = 7;
        }
        int mintime = (short)(30 + treeage * 5);
        mintime = (int)(mintime * 0.75f + mintime * 0.2f * Math.max(1.0f, 100.0f - source.getSpellSpeedBonus()) / 100.0f);
        short time = (short)mintime;
        double bonus = 0.0;
        if (weaponSkill != null) {
            bonus = weaponSkill.getKnowledge(source, 0.0);
        }
        time *= (short)(1.0 + (100.0 - woodcuttingskill.getKnowledge(source, bonus)) / 100.0);
        time /= (short)Servers.localServer.getActionTimer();
        return time;
    }
    
    public static final boolean isTileModBlocked(final Creature performer, final int tilex, final int tiley, final boolean surfaced) {
        if (performer.getPower() <= 0) {
            if (Zones.isWithinDuelRing(tilex, tiley, true) != null) {
                performer.getCommunicator().sendAlertServerMessage("This is too close to the duelling ring.");
                return true;
            }
            if (Features.Feature.BLOCK_HOTA.isEnabled()) {
                for (final FocusZone fz : FocusZone.getZonesAt(tilex, tiley)) {
                    if ((fz.isBattleCamp() || fz.isPvPHota() || fz.isNoBuild()) && fz.covers(tilex, tiley)) {
                        performer.getCommunicator().sendAlertServerMessage("This land is protected by the deities and may not be modified.");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void paintTerrain(final Player player, final Item wand, final int tileX, final int tileY) {
        final byte aux = wand.getAuxData();
        if (aux == 0) {
            return;
        }
        final byte newtype = Tiles.getTile(aux).id;
        if (!player.isOnSurface()) {
            paintCaveTerrain(player, newtype, tileX, tileY);
            return;
        }
        int dx = Math.max(0, wand.getData1());
        int dy = Math.max(0, wand.getData2());
        if (dx > 10) {
            dx = 0;
        }
        if (dy > 10) {
            dy = 0;
        }
        if (dx > 10 || dy > 10 || dx < 0 || dy < 0) {
            player.getCommunicator().sendNormalServerMessage("The data1 and data2 range should be between 0 and 10.");
            return;
        }
        if (dx == 0 && dy == 0 && Tiles.decodeType(Server.surfaceMesh.getTile(tileX, tileY)) == newtype) {
            player.getCommunicator().sendNormalServerMessage("The terrain is already of that type.");
            return;
        }
        if (Tiles.isSolidCave(newtype) || newtype == Tiles.Tile.TILE_CAVE.id || newtype == Tiles.Tile.TILE_CAVE_FLOOR_REINFORCED.id) {
            if (player.getPower() >= 5) {
                if (Tiles.isSolidCave(newtype)) {
                    final Tiles.Tile theNewTile = Tiles.getTile(newtype);
                    if (theNewTile != null) {
                        Server.caveMesh.setTile(tileX, tileY, Tiles.encode(Tiles.decodeHeight(Server.caveMesh.getTile(tileX, tileY)), theNewTile.id, Tiles.decodeData(Server.caveMesh.getTile(tileX, tileY))));
                        Players.getInstance().sendChangedTiles(tileX, tileY, 1, 1, false, false);
                    }
                }
                else {
                    player.getCommunicator().sendNormalServerMessage("You can only change to solid rock types at the moment.");
                }
            }
            else {
                player.getCommunicator().sendNormalServerMessage("Only implementors may set the terrain to some sort of rock.");
            }
            return;
        }
        for (int x = 0; x < Math.max(1, dx); ++x) {
            for (int y = 0; y < Math.max(1, dy); ++y) {
                final byte oldType = Tiles.decodeType(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y));
                if (player.getPower() < 5 && (newtype == Tiles.Tile.TILE_ROCK.id || oldType == Tiles.Tile.TILE_ROCK.id || newtype == Tiles.Tile.TILE_CLIFF.id || oldType == Tiles.Tile.TILE_CLIFF.id)) {
                    player.getCommunicator().sendNormalServerMessage("That would have impact on the rock layer, and is not allowed for now.");
                }
                else {
                    final Tiles.Tile theNewTile2 = Tiles.getTile(newtype);
                    byte data = 0;
                    final byte theNewType;
                    if ((theNewType = newtype) == Tiles.Tile.TILE_GRASS.id) {
                        final GrassData.FlowerType flowerType = getRandomFlower(GrassData.FlowerType.NONE, false);
                        if (flowerType != GrassData.FlowerType.NONE) {
                            final GrassData.GrowthStage stage = GrassData.GrowthStage.decodeTileData(0);
                            data = GrassData.encodeGrassTileData(stage, flowerType);
                        }
                    }
                    if (newtype == Tiles.Tile.TILE_ROCK.id) {
                        Server.caveMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0));
                        Server.rockMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    }
                    else if (theNewTile2.isTree() || theNewTile2.isBush()) {
                        final byte treeAge = (byte)Server.rand.nextInt(FoliageAge.values().length);
                        final byte grass = (byte)(1 + Server.rand.nextInt(3));
                        data = Tiles.encodeTreeData(treeAge, false, false, grass);
                    }
                    if (Tiles.getTile(aux).id == Tiles.Tile.TILE_ROCK.id) {
                        Server.caveMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0));
                        Server.rockMesh.setTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), Tiles.Tile.TILE_ROCK.id, (byte)0));
                    }
                    else if (oldType != Tiles.Tile.TILE_HOLE.id && !Tiles.isMineDoor(oldType)) {
                        Server.setSurfaceTile(tileX - dx / 2 + x, tileY - dy / 2 + y, Tiles.decodeHeight(Server.surfaceMesh.getTile(tileX - dx / 2 + x, tileY - dy / 2 + y)), theNewType, data);
                    }
                }
            }
        }
        Players.getInstance().sendChangedTiles(tileX - dx / 2, tileY - dy / 2, Math.max(1, dx), Math.max(1, dy), true, true);
    }
    
    public static void paintCaveTerrain(final Player player, final byte newtype, final int tilex, final int tiley) {
        final int currentTile = Server.caveMesh.getTile(tilex, tiley);
        final byte currentType = Tiles.decodeType(currentTile);
        if (currentType != Tiles.Tile.TILE_CAVE_EXIT.id) {
            if (newtype == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(newtype) || Tiles.isSolidCave(newtype)) {
                boolean succeeded;
                if (Tiles.isSolidCave(currentType)) {
                    if (Tiles.isSolidCave(newtype)) {
                        succeeded = true;
                    }
                    else {
                        final int rtx = player.getTileX();
                        final int rty = player.getTileY();
                        int dir = 2;
                        if (rty - tiley < 0) {
                            dir = 5;
                        }
                        else if (rty - tiley > 0) {
                            dir = 3;
                        }
                        else if (rtx - tilex < 0) {
                            dir = 4;
                        }
                        succeeded = TileRockBehaviour.createInsideTunnel(tilex, tiley, currentTile, player, 145, dir, true, null);
                    }
                }
                else if (currentType == Tiles.Tile.TILE_CAVE.id || Tiles.isReinforcedFloor(currentType)) {
                    if (Tiles.isSolidCave(newtype)) {
                        setAsRock(tilex, tiley, false);
                        succeeded = true;
                    }
                    else {
                        succeeded = true;
                    }
                }
                else {
                    succeeded = true;
                }
                if (succeeded) {
                    final int returnTile = Server.caveMesh.getTile(tilex, tiley);
                    Server.caveMesh.setTile(tilex, tiley, Tiles.encode(Tiles.decodeHeight(returnTile), newtype, Tiles.decodeData(returnTile)));
                    Players.getInstance().sendChangedTile(tilex, tiley, false, true);
                }
            }
            else {
                player.getCommunicator().sendNormalServerMessage("You must select a cave tile to change to.");
            }
        }
        else if (currentType == Tiles.Tile.TILE_CAVE_EXIT.id) {
            if (Tiles.isRoadType(newtype)) {
                Server.setClientCaveFlags(tilex, tiley, newtype);
                Players.getInstance().sendChangedTile(tilex, tiley, false, true);
            }
            else {
                player.getCommunicator().sendNormalServerMessage("Removing cave openings is not supported. Use a shaker orb.");
            }
        }
        else {
            player.getCommunicator().sendNormalServerMessage("Removing cave openings is not supported. Use a shaker orb.");
        }
    }
    
    public static GrassData.FlowerType getRandomFlower(final GrassData.FlowerType flowerType, final boolean ignoreSeason) {
        final int rnd = Server.rand.nextInt(60000);
        if (rnd < 1000) {
            if (flowerType == GrassData.FlowerType.NONE && (ignoreSeason || !WurmCalendar.isAutumnWinter())) {
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
            else if (!ignoreSeason && WurmCalendar.isAutumnWinter()) {
                return GrassData.FlowerType.NONE;
            }
        }
        return flowerType;
    }
    
    public static boolean cannotMakeLawn(final Creature performer, final int tilex, final int tiley) {
        return !Methods.isActionAllowed(performer, (short)644, performer.getTileX(), performer.getTileY());
    }
    
    private static final boolean isCaveExitBorder(final int x, final int y) {
        final int currtile = Server.caveMesh.getTile(x, y);
        final short cceil = (short)(Tiles.decodeData(currtile) & 0xFF);
        return cceil == 0;
    }
    
    public static final void flattenImmediately(final Creature performer, final int stx, final int endtx, final int sty, final int endty, final float minDirtDist, final int lowerByAmount, final boolean toRock) {
        final float flattenToHeight = performer.getPositionZ() - lowerByAmount / 10.0f;
        final float totalHeightRock = flattenToHeight - (toRock ? 0.0f : minDirtDist);
        final float totalHeightDirt = flattenToHeight;
        if (performer.getPower() < 4) {
            Terraforming.logger.warning(performer.getName() + " attempted to use flattenImmediately with a power level of " + performer.getPower() + ", DENYING!");
            return;
        }
        Terraforming.logger.info(performer.getName() + " used flattenImmediately stx:" + stx + ", sty:" + sty + ", endx:" + endtx + ", endy:" + endty + ", lower by extra amount:" + lowerByAmount + ", minDirtDist:" + minDirtDist + ", flattenToRock:" + toRock);
        final double mapSize = Math.pow(2.0, Constants.meshSize) - 1.0;
        if (endtx > mapSize || endty > mapSize || stx < 1 || sty < 1) {
            performer.getCommunicator().sendAlertServerMessage("YOU CAN NOT MAKE THE FLATTEN ZONE EXPAND PAST A SERVER BORDER");
            return;
        }
        if (performer.getPositionZ() + minDirtDist > 32767.0f || performer.getPositionZ() + lowerByAmount > 32767.0f || performer.getPositionZ() + minDirtDist - lowerByAmount > 32767.0f) {
            performer.getCommunicator().sendAlertServerMessage("You may not set a (combined) value larger than 32767");
            return;
        }
        if (performer.getPositionZ() - minDirtDist < -32768.0f || performer.getPositionZ() - lowerByAmount < -32768.0f || performer.getPositionZ() - minDirtDist + lowerByAmount < -32768.0f) {
            performer.getCommunicator().sendAlertServerMessage("You may not set a (combined) value less than 32768");
            return;
        }
        class InstantFlattenHolder
        {
            private HolderCave hasCave;
            private float flattenToHeightDirt = toRock;
            private float flattenToHeightRock = totalHeightDirt;
            private final int x = currx;
            private final int y = curry;
            private final int storedSurface = currTileSurface;
            private final int storedRock = currTileCave;
            private final int storedCave = toPutRockHeight;
            final /* synthetic */ Creature val$performer;
            
            public InstantFlattenHolder(final int _x, final int _y, final int _surface, final int _rock, final int _cave, final int _flattenToHeightRock, final int _flattenToHeightDirt, final float val$performer) {
                this.val$performer = (Creature)val$performer;
                this.hasCave = HolderCave.NO_CAVE;
            }
            
            public HolderCave getCave() {
                return this.hasCave;
            }
            
            public void setCave(final HolderCave cave) {
                this.hasCave = cave;
            }
            
            public float getFlattenToHeightRock() {
                return this.flattenToHeightRock;
            }
            
            public void setFlattenToHeightRock(final float _flattenToHeightRock) {
                this.flattenToHeightRock = _flattenToHeightRock;
            }
            
            public float getFlattenToHeightDirt() {
                return this.flattenToHeightDirt;
            }
            
            public void setFlattenToHeightDirt(final float _flattenToHeightDirt) {
                this.flattenToHeightDirt = _flattenToHeightDirt;
            }
            
            public void handleCaveCalcMagic() {
                final float surfaceHeight = Tiles.decodeHeightAsFloat(this.storedSurface);
                final float rockHeight = Tiles.decodeHeightAsFloat(this.storedRock);
                final float caveHeight = Tiles.decodeHeightAsFloat(this.storedCave);
                final short ceilingHeight = (short)(Tiles.decodeData(this.storedCave) & 0xFF);
                final float totalCaveHeightCaves = ceilingHeight / 10.0f + caveHeight + 0.2f;
                final float totalCaveHeightExits = ceilingHeight / 10.0f + caveHeight;
                if (this.getCave().value == HolderCave.CAVE.value || this.getCave().value == HolderCave.CAVE_NEIGHBOUR.value) {
                    if (minDirtDist != 0.0f) {
                        if (this.flattenToHeightRock < totalCaveHeightCaves) {
                            this.setFlattenToHeightRock(totalCaveHeightCaves);
                            this.setFlattenToHeightDirt(totalCaveHeightCaves);
                        }
                    }
                    else {
                        final float toRockMod = (float)((minDirtDist != 0.0f) ? 0.0f : performer);
                        final float currentDifference = surfaceHeight - rockHeight;
                        final float dirtHeight = (currentDifference < toRockMod) ? currentDifference : toRockMod;
                        if (this.flattenToHeightRock < totalCaveHeightCaves) {
                            this.setFlattenToHeightRock(totalCaveHeightCaves);
                            this.setFlattenToHeightDirt(totalCaveHeightCaves + dirtHeight);
                        }
                    }
                }
                else if (this.getCave().value >= HolderCave.CAVE_EXIT.value) {
                    this.setFlattenToHeightDirt(totalCaveHeightExits);
                    this.setFlattenToHeightRock(totalCaveHeightExits);
                }
                else {
                    this.val$performer.getCommunicator().sendAlertServerMessage("This should never be reached, but were for [" + this.x + ", " + this.y + "] it has a getCave value of " + this.getCave().toString());
                    this.setCave(HolderCave.VALUE_OF_SHAME);
                }
            }
        }
        class compareCoordinates implements Comparable<compareCoordinates>
        {
            private final int x = currx;
            private final int y = curry;
            
            public compareCoordinates(final int _x, final int _y) {
            }
            
            public int getX() {
                return this.x;
            }
            
            public int getY() {
                return this.y;
            }
            
            @Override
            public int compareTo(final compareCoordinates o) {
                if (o.getX() == this.x) {
                    if (o.getY() == this.getY()) {
                        return 0;
                    }
                    if (o.getY() < this.getY()) {
                        return 1;
                    }
                    return -1;
                }
                else {
                    if (o.getX() < this.getX()) {
                        return 1;
                    }
                    return -1;
                }
            }
        }
        final TreeMap<compareCoordinates, InstantFlattenHolder> flattenArea = new TreeMap<compareCoordinates, InstantFlattenHolder>();
        for (int currx = stx; currx <= endtx + 1; ++currx) {
            for (int curry = sty; curry <= endty + 1; ++curry) {
                final int currTileCave = Server.caveMesh.getTile(currx, curry);
                final int currTileRock = Server.rockMesh.getTile(currx, curry);
                final int currTileSurface = Server.surfaceMesh.getTile(currx, curry);
                float toPutRockHeight;
                if (!toRock) {
                    final float currentRockLayerHeight = Tiles.decodeHeightAsFloat(currTileRock);
                    if (totalHeightRock > currentRockLayerHeight) {
                        toPutRockHeight = currentRockLayerHeight;
                    }
                    else {
                        toPutRockHeight = totalHeightRock;
                    }
                }
                else {
                    toPutRockHeight = totalHeightRock;
                }
                flattenArea.put(new compareCoordinates(curry), new InstantFlattenHolder(curry, currTileRock, toPutRockHeight, toRock));
            }
        }
        for (int currx = stx; currx <= endtx + 1; ++currx) {
            for (int curry = sty; curry <= endty + 1; ++curry) {
                final InstantFlattenHolder NW = flattenArea.get(new compareCoordinates(curry));
                final byte ID = Tiles.decodeType(NW.storedCave);
                if (ID == Tiles.Tile.TILE_CAVE_EXIT.id) {
                    final InstantFlattenHolder NE = flattenArea.get(new compareCoordinates(NW.y));
                    final InstantFlattenHolder SE = flattenArea.get(new compareCoordinates(NW.y + 1));
                    final InstantFlattenHolder SW = flattenArea.get(new compareCoordinates(NW.y + 1));
                    if (isCaveExitBorder(currx, curry)) {
                        if (isCaveExitBorder(currx + 1, curry)) {
                            if (NW != null) {
                                NW.setCave(HolderCave.CAVE_EXIT_NORTH_NW);
                            }
                            if (NE != null) {
                                NE.setCave(HolderCave.CAVE_EXIT_NORTH_NE);
                            }
                            if (SE != null) {
                                SE.setCave(HolderCave.CAVE_EXIT_NORTH_SE);
                            }
                            if (SW != null) {
                                SW.setCave(HolderCave.CAVE_EXIT_NORTH_SW);
                            }
                            if (Constants.devmode) {
                                performer.getCommunicator().sendAlertServerMessage("Cave entrance with north border at [" + currx + ", " + curry + "]");
                            }
                        }
                        else {
                            if (NW != null) {
                                NW.setCave(HolderCave.CAVE_EXIT_WEST_NW);
                            }
                            if (NE != null) {
                                NE.setCave(HolderCave.CAVE_EXIT_WEST_NE);
                            }
                            if (SE != null) {
                                SE.setCave(HolderCave.CAVE_EXIT_WEST_SE);
                            }
                            if (SW != null) {
                                SW.setCave(HolderCave.CAVE_EXIT_WEST_SW);
                            }
                            if (Constants.devmode) {
                                performer.getCommunicator().sendAlertServerMessage("Cave entrance with west border at [" + currx + ", " + curry + "]");
                            }
                        }
                    }
                    else if (isCaveExitBorder(currx + 1, curry)) {
                        if (NW != null) {
                            NW.setCave(HolderCave.CAVE_EXIT_EAST_NW);
                        }
                        if (NE != null) {
                            NE.setCave(HolderCave.CAVE_EXIT_EAST_NE);
                        }
                        if (SE != null) {
                            SE.setCave(HolderCave.CAVE_EXIT_EAST_SE);
                        }
                        if (SW != null) {
                            SW.setCave(HolderCave.CAVE_EXIT_EAST_SW);
                        }
                        if (Constants.devmode) {
                            performer.getCommunicator().sendAlertServerMessage("Cave entrance with east border at [" + currx + ", " + curry + "]");
                        }
                    }
                    else {
                        if (NW != null) {
                            NW.setCave(HolderCave.CAVE_EXIT_SOUTH_NW);
                        }
                        if (NE != null) {
                            NE.setCave(HolderCave.CAVE_EXIT_SOUTH_NE);
                        }
                        if (SE != null) {
                            SE.setCave(HolderCave.CAVE_EXIT_SOUTH_SE);
                        }
                        if (SW != null) {
                            SW.setCave(HolderCave.CAVE_EXIT_SOUTH_SW);
                        }
                        if (Constants.devmode) {
                            performer.getCommunicator().sendAlertServerMessage("Cave entrance with south border at [" + currx + ", " + curry + "]");
                        }
                    }
                }
                else if (!Tiles.isSolidCave(ID)) {
                    final InstantFlattenHolder NE = flattenArea.get(new compareCoordinates(NW.y));
                    final InstantFlattenHolder SE = flattenArea.get(new compareCoordinates(NW.y + 1));
                    final InstantFlattenHolder SW = flattenArea.get(new compareCoordinates(NW.y + 1));
                    if (NW != null && NW.getCave().value < HolderCave.CAVE.value) {
                        NW.setCave(HolderCave.CAVE);
                    }
                    if (NE != null && NE.getCave().value < HolderCave.CAVE_NEIGHBOUR.value) {
                        NE.setCave(HolderCave.CAVE_NEIGHBOUR);
                    }
                    if (SE != null && SE.getCave().value < HolderCave.CAVE_NEIGHBOUR.value) {
                        SE.setCave(HolderCave.CAVE_NEIGHBOUR);
                    }
                    if (SW != null && SW.getCave().value < HolderCave.CAVE_NEIGHBOUR.value) {
                        SW.setCave(HolderCave.CAVE_NEIGHBOUR);
                    }
                }
            }
        }
        for (final InstantFlattenHolder ifh : flattenArea.values()) {
            if (ifh.getCave() == HolderCave.NO_CAVE) {
                continue;
            }
            ifh.handleCaveCalcMagic();
        }
        for (final InstantFlattenHolder ifh : flattenArea.values()) {
            final byte rockData = Tiles.decodeData(ifh.storedRock);
            float toReturnRock;
            byte spawnedTypeRock;
            if (ifh.getCave().value == HolderCave.CAVE_EXIT_CORNER_MATTERS_ROCK.value || ifh.getCave().value == HolderCave.CAVE_EXIT_CORNER_MATTERS_BOTH.value) {
                toReturnRock = ifh.getFlattenToHeightRock();
                spawnedTypeRock = Tiles.decodeType(ifh.storedRock);
            }
            else {
                toReturnRock = ifh.getFlattenToHeightRock();
                spawnedTypeRock = 4;
            }
            Server.rockMesh.setTile(ifh.x, ifh.y, Tiles.encode(toReturnRock, spawnedTypeRock, rockData));
            byte spawnedTypeSurface;
            float toReturnDirtHeight;
            if (ifh.getCave() == HolderCave.VALUE_OF_SHAME) {
                spawnedTypeSurface = 37;
                toReturnDirtHeight = Tiles.decodeHeightAsFloat(ifh.storedRock);
            }
            else if (ifh.getCave().value == HolderCave.CAVE_EXIT_CORNER_MATTERS_DIRT.value || ifh.getCave().value == HolderCave.CAVE_EXIT_CORNER_MATTERS_BOTH.value) {
                toReturnDirtHeight = ifh.getFlattenToHeightRock();
                spawnedTypeSurface = Tiles.decodeType(ifh.storedSurface);
            }
            else if (ifh.getCave() == HolderCave.CAVE || ifh.getCave() == HolderCave.CAVE_NEIGHBOUR || ifh.getCave().value == HolderCave.CAVE_EXIT.value) {
                if (toRock) {
                    spawnedTypeSurface = 4;
                }
                else {
                    spawnedTypeSurface = (byte)((performer.getKingdomTemplateId() == 3) ? 10 : 2);
                }
                toReturnDirtHeight = ifh.getFlattenToHeightDirt();
            }
            else if (toRock) {
                spawnedTypeSurface = 4;
                toReturnDirtHeight = ifh.getFlattenToHeightDirt();
            }
            else {
                spawnedTypeSurface = (byte)((performer.getKingdomTemplateId() == 3) ? 10 : 2);
                toReturnDirtHeight = totalHeightDirt;
            }
            Server.surfaceMesh.setTile(ifh.x, ifh.y, Tiles.encode(toReturnDirtHeight, spawnedTypeSurface, (byte)0));
        }
        Players.getInstance().sendChangedTiles(stx, sty, endtx - stx + 2, endty - sty + 2, true, false);
        Players.getInstance().sendChangedTiles(stx, sty, endtx - stx + 2, endty - sty + 2, false, false);
    }
    
    static final boolean plantTrellis(final Creature performer, final Item trellis, final int tilex, final int tiley, final boolean onSurface, final Tiles.TileBorderDirection dir, final short action, final float counter, final Action act) {
        if (trellis.getCurrentQualityLevel() < 10.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " is of too poor quality to be planted.");
            return true;
        }
        if (trellis.getDamage() > 70.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " is too heavily damaged to be planted.");
            return true;
        }
        if (performer.getFloorLevel() != 0) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " can not be planted unless on ground level.");
            return true;
        }
        if (trellis.isSurfaceOnly() && !performer.isOnSurface()) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " can only be planted on the surface.");
            return true;
        }
        if (!onSurface) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " would never grow inside a cave.");
            return true;
        }
        if (trellis.isPlanted()) {
            performer.getCommunicator().sendNormalServerMessage("The " + trellis.getName() + " is already planted.", (byte)3);
            return true;
        }
        float rot = 0.0f;
        final float hoff = 0.3f;
        float hoffx = 2.0f;
        float hoffy = 2.0f;
        if (tilex == performer.getTileX()) {
            if (tiley == performer.getTileY()) {
                if (dir == Tiles.TileBorderDirection.DIR_HORIZ) {
                    hoffy = 0.3f;
                    hoffx = ((action == 746) ? 1.0f : ((action == 747) ? 3.0f : 2.0f));
                }
                else {
                    hoffx = 0.3f;
                    rot = 270.0f;
                    hoffy = ((action == 746) ? 3.0f : ((action == 747) ? 1.0f : 2.0f));
                }
            }
            else {
                if (tiley - 1 != performer.getTileY()) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot reach that far.");
                    return true;
                }
                hoffy = 3.7f;
                rot = 180.0f;
                hoffx = ((action == 746) ? 3.0f : ((action == 747) ? 1.0f : 2.0f));
            }
        }
        else {
            if (tilex - 1 != performer.getTileX()) {
                performer.getCommunicator().sendNormalServerMessage("You cannot reach that far.");
                return true;
            }
            if (tiley != performer.getTileY()) {
                performer.getCommunicator().sendNormalServerMessage("You cannot reach that far.");
                return true;
            }
            hoffx = 3.7f;
            rot = 90.0f;
            hoffy = ((action == 746) ? 1.0f : ((action == 747) ? 3.0f : 2.0f));
        }
        int time = Actions.getPlantActionTime(performer, trellis);
        if (counter == 1.0f) {
            if (performer instanceof Player) {
                final Player p = (Player)performer;
                try {
                    final Skills skills = p.getSkills();
                    final Skill dig = skills.getSkill(1009);
                    if (dig.getRealKnowledge() < 10.0) {
                        performer.getCommunicator().sendNormalServerMessage("You need to have 10 in the skill digging to secure " + trellis.getTemplate().getPlural() + " to the ground.", (byte)3);
                        return true;
                    }
                }
                catch (NoSuchSkillException nss) {
                    performer.getCommunicator().sendNormalServerMessage("You need 10 digging to plant " + trellis.getTemplate().getPlural() + ".", (byte)3);
                    return true;
                }
            }
            if (!Methods.isActionAllowed(performer, (short)176)) {
                return true;
            }
            final int tile = performer.getCurrentTileNum();
            if (Tiles.decodeHeight(tile) < 0) {
                performer.getCommunicator().sendNormalServerMessage("The water is too deep to plant the " + trellis.getName() + ".", (byte)3);
                return true;
            }
            if (performer.getStatus().getBridgeId() != -10L) {
                performer.getCommunicator().sendNormalServerMessage("You cannot plant a " + trellis.getName() + " on a bridge as no soil for it to grow from.", (byte)3);
                return true;
            }
            if (trellis.isFourPerTile()) {
                final VolaTile vt = Zones.getTileOrNull(performer.getTileX(), performer.getTileY(), trellis.isOnSurface());
                if (vt != null && vt.getFourPerTileCount(0) >= 4) {
                    performer.getCommunicator().sendNormalServerMessage("You cannot plant a " + trellis.getName() + " as there are four here already.", (byte)3);
                    return true;
                }
            }
            act.setTimeLeft(time);
            performer.getCommunicator().sendNormalServerMessage("You start to plant the " + trellis.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to plant " + trellis.getNameWithGenus() + ".", performer, 5);
            performer.sendActionControl("Planting " + trellis.getName(), true, time);
            performer.getStatus().modifyStamina(-400.0f);
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                performer.getStatus().modifyStamina(-1000.0f);
            }
        }
        if (counter * 10.0f > time) {
            try {
                if (trellis.isFourPerTile()) {
                    final VolaTile vt2 = Zones.getTileOrNull(performer.getTileX(), performer.getTileY(), trellis.isOnSurface());
                    if (vt2 != null && vt2.getFourPerTileCount(0) == 4) {
                        performer.getCommunicator().sendNormalServerMessage("You cannot plant a " + trellis.getName() + " as there are four here already.", (byte)3);
                        return true;
                    }
                }
                final long lParentId = trellis.getParentId();
                if (lParentId != -10L) {
                    final Item parent = Items.getItem(lParentId);
                    parent.dropItem(trellis.getWurmId(), false);
                }
                final int encodedTile = Server.surfaceMesh.getTile(performer.getTileX(), performer.getTileY());
                final float npsz = Tiles.decodeHeightAsFloat(encodedTile);
                trellis.setPos(performer.getTileX() * 4 + hoffx, performer.getTileY() * 4 + hoffy, npsz, rot, -10L);
                final Zone zone = Zones.getZone(Zones.safeTileX(performer.getTileX()), Zones.safeTileY(performer.getTileY()), performer.isOnSurface());
                zone.addItem(trellis);
                trellis.setIsPlanted(true);
                performer.getCommunicator().sendNormalServerMessage("You plant the " + trellis.getName() + ".");
                Server.getInstance().broadCastAction(performer.getName() + " plants the " + trellis.getName() + ".", performer, 5);
            }
            catch (NoSuchZoneException nsz) {
                performer.getCommunicator().sendNormalServerMessage("You fail to plant the " + trellis.getName() + ". Something is weird.");
                Terraforming.logger.log(Level.WARNING, performer.getName() + ": " + nsz.getMessage(), nsz);
            }
            catch (NoSuchItemException nsie) {
                performer.getCommunicator().sendNormalServerMessage("You fail to plant the " + trellis.getName() + ". Something is weird.");
                Terraforming.logger.log(Level.WARNING, performer.getName() + ": " + nsie.getMessage(), nsie);
            }
            return true;
        }
        return false;
    }
    
    private static int calcMaxGrubs(final double currentSkill, @Nullable final Item tool) {
        int bonus = 0;
        if (tool != null && tool.getSpellEffects() != null) {
            final float extraChance = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1.0f;
            if (extraChance > 0.0f && Server.rand.nextFloat() < extraChance) {
                ++bonus;
            }
        }
        return Math.min(4, (int)(currentSkill + 28.0) / 27 + bonus);
    }
    
    private static int calcMaxHarvest(final int tile, final double currentSkill, final Item tool) {
        final byte data = Tiles.decodeData(tile);
        final byte age = FoliageAge.getAgeAsByte(data);
        int maxByAge = 1;
        if (age < FoliageAge.OLD_ONE.getAgeId()) {
            maxByAge = 1;
        }
        else if (age < FoliageAge.OLD_TWO.getAgeId()) {
            maxByAge = 2;
        }
        else if (age < FoliageAge.VERY_OLD.getAgeId()) {
            maxByAge = 3;
        }
        else if (age < FoliageAge.OVERAGED.getAgeId()) {
            maxByAge = 4;
        }
        int bonus = 0;
        if (tool.getSpellEffects() != null) {
            final float extraChance = tool.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_FARMYIELD) - 1.0f;
            if (extraChance > 0.0f && Server.rand.nextFloat() < extraChance) {
                ++bonus;
            }
        }
        return Math.min(maxByAge, (int)(currentSkill + 28.0) / 27 + bonus);
    }
    
    static {
        logger = Logger.getLogger(Terraforming.class.getName());
        Terraforming.flattenTiles = new int[4][4];
        Terraforming.rockTiles = new int[4][4];
        noCaveDoor = new int[] { -1, -1 };
        Terraforming.flattenImmutable = 0;
        Terraforming.newType = 0;
        Terraforming.oldType = 0;
        Terraforming.newTile = 0;
        r = new Random();
    }
    
    enum HolderCave
    {
        NO_CAVE(0), 
        VALUE_OF_SHAME(0), 
        CAVE_NEIGHBOUR(1), 
        CAVE(2), 
        CAVE_EXIT(3), 
        CAVE_EXIT_CORNER_MATTERS_ROCK(20), 
        CAVE_EXIT_CORNER_MATTERS_DIRT(30), 
        CAVE_EXIT_CORNER_MATTERS_BOTH(40), 
        CAVE_EXIT_NORTH_NW(40), 
        CAVE_EXIT_NORTH_NE(20), 
        CAVE_EXIT_NORTH_SE(3), 
        CAVE_EXIT_NORTH_SW(3), 
        CAVE_EXIT_EAST_NW(30), 
        CAVE_EXIT_EAST_NE(20), 
        CAVE_EXIT_EAST_SE(20), 
        CAVE_EXIT_EAST_SW(3), 
        CAVE_EXIT_SOUTH_NW(30), 
        CAVE_EXIT_SOUTH_NE(3), 
        CAVE_EXIT_SOUTH_SE(20), 
        CAVE_EXIT_SOUTH_SW(3), 
        CAVE_EXIT_WEST_NW(40), 
        CAVE_EXIT_WEST_NE(3), 
        CAVE_EXIT_WEST_SE(3), 
        CAVE_EXIT_WEST_SW(3);
        
        private int value;
        
        private HolderCave(final int value) {
            this.value = value;
        }
    }
}

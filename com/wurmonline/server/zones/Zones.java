// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.ServerDirInfo;
import com.wurmonline.server.Point4f;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.creatures.ai.NoPathException;
import com.wurmonline.server.creatures.ai.PathFinder;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchItemException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.Hashtable;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.server.structures.Floor;
import javax.annotation.Nullable;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.creatures.CombatHandler;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.creatures.Creatures;
import javax.annotation.Nonnull;
import com.wurmonline.math.TilePos;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.wurmonline.server.structures.DbStructure;
import com.wurmonline.server.structures.Structures;
import java.util.Collection;
import com.wurmonline.server.Players;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.mesh.FoliageAge;
import com.wurmonline.server.Constants;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Items;
import com.wurmonline.server.deities.Deity;
import java.util.HashSet;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.ListIterator;
import com.wurmonline.server.kingdom.Kingdoms;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import com.wurmonline.server.Server;
import java.util.Iterator;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Features;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.io.File;
import java.util.Random;
import com.wurmonline.server.creatures.Creature;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import java.util.Set;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.shared.constants.EffectConstants;
import com.wurmonline.server.MiscConstants;

public final class Zones implements MiscConstants, EffectConstants, CombatConstants, TimeConstants
{
    private static Zone[][] surfaceZones;
    private static Zone[][] caveZones;
    private static final Map<Integer, VirtualZone> virtualZones;
    private static final Map<Integer, Map<Integer, Byte>> miningTiles;
    public static final int zoneSize = 64;
    public static final int zoneShifter = 6;
    private static final Set<Item> duelRings;
    public static final int worldTileSizeX;
    public static final int worldTileSizeY;
    public static final float worldMeterSizeX;
    public static final float worldMeterSizeY;
    public static boolean[][] protectedTiles;
    static boolean[][] walkedTiles;
    private static final byte[][] kingdoms;
    public static final int faithSizeX;
    public static final int faithSizeY;
    public static final int DOMAIN_DIVISION = 64;
    public static final int domainSizeX;
    public static final int domainSizeY;
    public static final int INFLUENCE_DIVISION = 256;
    public static final int influenceSizeX;
    public static final int influenceSizeY;
    public static final int HIVE_DIVISION = 32;
    public static final int hiveZoneSizeX;
    public static final int hiveZoneSizeY;
    private static boolean hasLoadedChristmas;
    private static final Logger logger;
    private static int currentSaveZoneX;
    private static int currentSaveZoneY;
    private static boolean loading;
    public static int numberOfZones;
    private static int rest;
    private static int maxRest;
    private static int zonesPerRun;
    private static boolean haslogged;
    private static int coverHolder;
    private static final FaithZone[][] surfaceDomains;
    private static final FaithZone[][] caveDomains;
    private static final LinkedList<Item> altars;
    private static final ArrayList<HashMap<Item, FaithZone>> altarZones;
    private static final ArrayList<HashMap<Item, InfluenceZone>> influenceZones;
    private static final ArrayList<ConcurrentHashMap<Item, HiveZone>> hiveZones;
    private static final ConcurrentHashMap<Item, TurretZone> turretZones;
    private static final byte[][] influenceCache;
    private static int pollnum;
    private static int MESHSIZE;
    private static final String UPDATE_MININGTILE = "UPDATE MINING SET STATE=? WHERE TILEX=? AND TILEY=?";
    private static final String INSERT_MININGTILE = "INSERT INTO MINING (STATE,TILEX,TILEY) VALUES(?,?,?)";
    private static final String DELETE_MININGTILE = "DELETE FROM MINING WHERE TILEX=? AND TILEY=?";
    private static final String GET_ALL_MININGTILES = "SELECT * FROM MINING";
    private static final String GET_MININGTILE = "SELECT STATE FROM MINING WHERE TILEX=? AND TILEY=?";
    private static final LinkedList<Item> guardTowers;
    private static final String protectedTileFile;
    public static boolean shouldCreateWarTargets;
    public static boolean shouldSourceSprings;
    private static Map<Byte, Float> landPercent;
    public static Creature evilsanta;
    public static Creature santa;
    public static Creature santaMolRehan;
    public static final ConcurrentHashMap<Long, Creature> santas;
    static final Random zrand;
    private static int currentPollZoneX;
    private static int currentPollZoneY;
    private static boolean devlog;
    private static final Object ZONE_SYNC_LOCK;
    private static LinkedList<LongPosition> posmap;
    private static boolean hasStartedYet;
    private static long lastCounted;
    
    static void setLandPercent(final byte kingdom, final float percent) {
        Zones.landPercent.put(kingdom, percent);
    }
    
    public static float getPercentLandForKingdom(final byte kingdom) {
        final Float f = Zones.landPercent.get(kingdom);
        if (f != null) {
            return f;
        }
        return 0.0f;
    }
    
    public static void saveProtectedTiles() {
        final File f = new File(Zones.protectedTileFile);
        try {
            f.createNewFile();
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        try {
            final DataOutputStream ds = new DataOutputStream(new FileOutputStream(f));
            final ObjectOutputStream oos = new ObjectOutputStream(ds);
            oos.writeObject(Zones.protectedTiles);
            oos.flush();
            oos.close();
            ds.close();
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
    }
    
    public static final void addGuardTowerInfluence(final Item tower, final boolean silent) {
        if (Features.Feature.NEW_KINGDOM_INF.isEnabled()) {
            if (Zones.influenceZones.isEmpty()) {
                initInfluenceZones();
            }
            final int actualZone = Math.max(0, tower.getTileY() / 256) * Zones.influenceSizeX + Math.max(0, tower.getTileX() / 256);
            HashMap<Item, InfluenceZone> thisZone = Zones.influenceZones.get(actualZone);
            if (thisZone == null) {
                thisZone = new HashMap<Item, InfluenceZone>();
            }
            final InfluenceZone newZone = new InfluenceZone(tower);
            thisZone.put(tower, newZone);
            Zones.influenceZones.set(actualZone, thisZone);
            for (int i = (int)(tower.getTileX() - tower.getCurrentQualityLevel()); i < tower.getTileX() + tower.getCurrentQualityLevel(); ++i) {
                for (int j = (int)(tower.getTileY() - tower.getCurrentQualityLevel()); j < tower.getTileY() + tower.getCurrentQualityLevel(); ++j) {
                    Zones.influenceCache[i][j] = -1;
                }
            }
        }
    }
    
    public static final void removeGuardTowerInfluence(final Item tower, final boolean silent) {
        if (Features.Feature.NEW_KINGDOM_INF.isEnabled()) {
            if (Zones.influenceZones.isEmpty()) {
                initInfluenceZones();
            }
            final int actualZone = Math.max(0, tower.getTileY() / 256) * Zones.influenceSizeX + Math.max(0, tower.getTileX() / 256);
            final HashMap<Item, InfluenceZone> thisZone = Zones.influenceZones.get(actualZone);
            if (thisZone == null) {
                return;
            }
            thisZone.remove(tower);
            for (int i = (int)(tower.getTileX() - tower.getCurrentQualityLevel()); i < tower.getTileX() + tower.getCurrentQualityLevel(); ++i) {
                for (int j = (int)(tower.getTileY() - tower.getCurrentQualityLevel()); j < tower.getTileY() + tower.getCurrentQualityLevel(); ++j) {
                    Zones.influenceCache[i][j] = -1;
                }
            }
        }
    }
    
    public static final byte getKingdom(final int tilex, final int tiley) {
        if (Servers.localServer.HOMESERVER) {
            return Servers.localServer.KINGDOM;
        }
        if (!Features.Feature.NEW_KINGDOM_INF.isEnabled()) {
            return Zones.kingdoms[safeTileX(tilex)][safeTileY(tiley)];
        }
        if (Zones.influenceZones.isEmpty()) {
            initInfluenceZones();
            if (!Zones.guardTowers.isEmpty()) {
                for (final Item i : Zones.guardTowers) {
                    addGuardTowerInfluence(i, true);
                }
            }
        }
        if (Zones.influenceCache[safeTileX(tilex)][safeTileY(tiley)] != -1) {
            return Zones.influenceCache[safeTileX(tilex)][safeTileY(tiley)];
        }
        final VolaTile t = getTileOrNull(tilex, tiley, true);
        if (t != null && t.getVillage() != null) {
            return t.getVillage().kingdom;
        }
        InfluenceZone toReturn = null;
        HashMap<Item, InfluenceZone> thisZone = null;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                final int actualZone = Math.max(0, Math.min(tiley / 256 + k, Zones.influenceSizeY - 1)) * Zones.influenceSizeX + Math.max(0, Math.min(tilex / 256 + j, Zones.influenceSizeX - 1));
                if (actualZone < Zones.influenceZones.size()) {
                    thisZone = Zones.influenceZones.get(actualZone);
                    if (thisZone != null) {
                        for (final InfluenceZone inf : thisZone.values()) {
                            if (inf.containsTile(tilex, tiley)) {
                                if (inf.getStrengthForTile(tilex, tiley, true) <= 0.0f) {
                                    continue;
                                }
                                if (toReturn == null) {
                                    toReturn = inf;
                                }
                                else {
                                    if (toReturn.getStrengthForTile(tilex, tiley, true) > inf.getStrengthForTile(tilex, tiley, true)) {
                                        continue;
                                    }
                                    toReturn = inf;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (toReturn == null) {
            return 0;
        }
        Zones.influenceCache[tilex][tiley] = toReturn.getZoneItem().getKingdom();
        return toReturn.getZoneItem().getKingdom();
    }
    
    public static boolean isKingdomBlocking(final int tilex, final int tiley, final int endx, final int endy, final byte founderKingdom, final int exclusionZoneX, final int exclusionZoneY, final int exclusionZoneEndX, final int exclusionZoneEndY) {
        final int startx = safeTileX(tilex);
        final int starty = safeTileY(tiley);
        final int ex = safeTileX(endx);
        final int ey = safeTileY(endy);
        final boolean hasExclusionZone = exclusionZoneX != -1;
        final int exclusionX = safeTileX(exclusionZoneX);
        final int exclusionY = safeTileY(exclusionZoneY);
        final int exclusionEndX = safeTileX(exclusionZoneEndX);
        final int exclusionEndY = safeTileY(exclusionZoneEndY);
        for (int x = startx; x < ex; ++x) {
            if (!hasExclusionZone || x < exclusionX || x > exclusionEndX) {
                for (int y = starty; y < ey; ++y) {
                    if (!hasExclusionZone || y < exclusionY || y > exclusionEndY) {
                        if (getKingdom(x, y) != 0 && getKingdom(x, y) != founderKingdom) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean isKingdomBlocking(final int tilex, final int tiley, final int endx, final int endy, final byte founderKingdom) {
        return isKingdomBlocking(tilex, tiley, endx, endy, founderKingdom, -1, -1, -1, -1);
    }
    
    public static boolean isWithinDuelRing(final int tilex, final int tiley, final int endx, final int endy) {
        final int startx = safeTileX(tilex);
        final int starty = safeTileY(tiley);
        final int ex = safeTileX(endx);
        final int ey = safeTileY(endy);
        for (int x = startx; x < ex; ++x) {
            for (int y = starty; y < ey; ++y) {
                final Item ring = isWithinDuelRing(x, y, true);
                if (ring != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final void setKingdom(final int tilex, final int tiley, final byte kingdom) {
        Zones.kingdoms[safeTileX(tilex)][safeTileY(tiley)] = kingdom;
        if (Server.getSecondsUptime() > 10) {
            setKingdomOn(tilex, tiley, kingdom, true);
            setKingdomOn(tilex, tiley, kingdom, false);
        }
    }
    
    private static final void setKingdomOn(final int tilex, final int tiley, final byte kingdom, final boolean onSurface) {
        final VolaTile t = getTileOrNull(tilex, tiley, onSurface);
        if (t != null) {
            final Creature[] creatures;
            final Creature[] crets = creatures = t.getCreatures();
            for (final Creature c : creatures) {
                c.setCurrentKingdom(kingdom);
            }
        }
    }
    
    public static final void setKingdom(final int tilex, final int tiley, final int sizex, final int sizey, final byte kingdom) {
        for (int x = tilex; x < tilex + sizex; ++x) {
            for (int y = tiley; y < tiley + sizey; ++y) {
                Zones.kingdoms[safeTileX(x)][safeTileY(y)] = kingdom;
            }
        }
    }
    
    public static void loadProtectedTiles() {
        Zones.logger.info("Loading protected tiles from file: " + Zones.protectedTileFile);
        final long start = System.nanoTime();
        final File f = new File(Zones.protectedTileFile);
        try {
            if (f.createNewFile()) {
                saveProtectedTiles();
                Zones.logger.log(Level.INFO, "Created first instance of protected tiles file.");
            }
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        try {
            final DataInputStream ds = new DataInputStream(new FileInputStream(f));
            final ObjectInputStream oos = new ObjectInputStream(ds);
            final boolean[][] protectedTileArr = (boolean[][])oos.readObject();
            oos.close();
            ds.close();
            final boolean[][] tmpTiles = new boolean[Zones.worldTileSizeX][Zones.worldTileSizeY];
            int protectedt = 0;
            final int wtx = Zones.worldTileSizeX;
            final int wty = Zones.worldTileSizeY;
            try {
                for (int x = 0; x < wtx; ++x) {
                    for (int y = 0; y < wty; ++y) {
                        tmpTiles[x][y] = protectedTileArr[x][y];
                        if (tmpTiles[x][y]) {
                            ++protectedt;
                        }
                    }
                }
                Zones.protectedTiles = tmpTiles;
            }
            catch (Exception ex) {
                for (int x2 = 0; x2 < wtx; ++x2) {
                    for (int y2 = 0; y2 < wty; ++y2) {
                        Zones.protectedTiles[x2][y2] = false;
                    }
                }
                f.delete();
            }
            Zones.logger.log(Level.INFO, "Loaded " + protectedt + " protected tiles. It took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        catch (ClassNotFoundException nsc) {
            Zones.logger.log(Level.WARNING, nsc.getMessage(), nsc);
        }
    }
    
    public static final boolean isTileProtected(final int tilex, final int tiley) {
        return Zones.protectedTiles[tilex][tiley];
    }
    
    public static final boolean isTileCornerProtected(final int tilex, final int tiley) {
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                if (isTileProtected(tilex - x, tiley - y)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static void initializeWalkTiles() {
        Zones.logger.info("Initialising walked tiles");
        final long start = System.nanoTime();
        final int wsx = Zones.worldTileSizeX;
        final int wsy = Zones.worldTileSizeY;
        final boolean[][] tmptiles = new boolean[wsx][wsy];
        for (int x = 0; x < wsx; ++x) {
            for (int y = 0; y < wsy; ++y) {
                tmptiles[x][y] = true;
            }
        }
        Zones.walkedTiles = tmptiles;
        Zones.logger.log(Level.INFO, "Initialised walked tiles. It took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
    }
    
    static void addGuardTower(final Item tower) {
        if (Zones.loading) {
            Zones.guardTowers.add(tower);
        }
        else {
            Kingdoms.addTower(tower);
        }
        addGuardTowerInfluence(tower, true);
    }
    
    public static void removeGuardTower(final Item tower) {
        Zones.guardTowers.remove(tower);
        removeGuardTowerInfluence(tower, true);
    }
    
    public static LinkedList<Item> getGuardTowers() {
        return Zones.guardTowers;
    }
    
    private static void initInfluenceZones() {
        if (!Zones.influenceZones.isEmpty()) {
            return;
        }
        for (int y = 0; y < Zones.influenceSizeY; ++y) {
            for (int x = 0; x < Zones.influenceSizeX; ++x) {
                Zones.influenceZones.add(null);
            }
        }
    }
    
    public static void loadTowers() {
        Zones.logger.info("Loading guard towers.");
        final long now = System.nanoTime();
        if (Features.Feature.NEW_KINGDOM_INF.isEnabled() && Zones.influenceZones.isEmpty()) {
            initInfluenceZones();
        }
        final ListIterator<Item> it = Zones.guardTowers.listIterator();
        while (it.hasNext()) {
            final Item gt = it.next();
            final Kingdom k = Kingdoms.getKingdom(gt.getAuxData());
            it.remove();
            if (!k.existsHere()) {
                Zones.logger.log(Level.INFO, "Removing tower for non-existent kingdom of " + k.getName());
                Kingdoms.destroyTower(gt, true);
            }
            else {
                Kingdoms.addTower(gt);
            }
        }
        if (Features.Feature.NEW_KINGDOM_INF.isEnabled()) {
            for (int i = 0; i < Zones.worldTileSizeX; ++i) {
                for (int j = 0; j < Zones.worldTileSizeY; ++j) {
                    getKingdom(i, j);
                }
            }
        }
        Zones.logger.log(Level.INFO, "Loaded " + Kingdoms.getNumberOfGuardTowers() + " Guard towers. That took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
    }
    
    public static final int getCoverHolder() {
        return Zones.coverHolder;
    }
    
    public static final void resetCoverHolder() {
        Zones.coverHolder = 0;
    }
    
    public static void calculateZones(final boolean overRide) {
        if (System.currentTimeMillis() - Zones.lastCounted > 60000L || overRide) {
            Zones.landPercent.clear();
            final long now = System.currentTimeMillis();
            final int[] zoneControl = new int[255];
            for (int x = 0; x < Zones.worldTileSizeX; ++x) {
                for (int y = 0; y < Zones.worldTileSizeY; ++y) {
                    final byte kingdom = getKingdom(x, y);
                    final int[] array = zoneControl;
                    final int n = kingdom & 0xFF;
                    ++array[n];
                }
            }
            Zones.lastCounted = System.currentTimeMillis();
            final long numberOfTiles = Zones.worldTileSizeX * Zones.worldTileSizeY;
            for (int x2 = 0; x2 < 255; ++x2) {
                if (zoneControl[x2] > 0) {
                    setLandPercent((byte)x2, zoneControl[x2] * 100.0f / numberOfTiles);
                }
            }
            if (System.currentTimeMillis() - now > 1000L) {
                Zones.logger.log(Level.INFO, "Calculating zones took " + (System.currentTimeMillis() - now) + " millis");
            }
        }
    }
    
    static void addAltar(final Item altar, final boolean silent) {
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            final int actualZone = Math.max(0, altar.getTileY() / 64) * Zones.domainSizeX + Math.max(0, altar.getTileX() / 64);
            HashMap<Item, FaithZone> thisZone = Zones.altarZones.get(actualZone);
            if (thisZone == null) {
                thisZone = new HashMap<Item, FaithZone>();
            }
            final FaithZone newZone = new FaithZone(altar);
            FaithZone oldZone = null;
            VolaTile tempTile = null;
            thisZone.put(altar, newZone);
            Zones.altarZones.set(actualZone, thisZone);
            if (!silent && newZone.getCurrentRuler() != null) {
                try {
                    for (int i = newZone.getStartX(); i < newZone.getEndX(); ++i) {
                        for (int j = newZone.getStartY(); j < newZone.getEndY(); ++j) {
                            tempTile = getTileOrNull(i, j, altar.isOnSurface());
                            if (tempTile != null) {
                                oldZone = getFaithZone(i, j, altar.isOnSurface());
                                if (newZone.getStrengthForTile(i, j, altar.isOnSurface()) > 0) {
                                    if (oldZone == null) {
                                        tempTile.broadCast("The domain of " + newZone.getCurrentRuler().getName() + " has now reached this place.");
                                    }
                                    else if (oldZone.getStrengthForTile(i, j, altar.isOnSurface()) < newZone.getStrengthForTile(i, j, altar.isOnSurface())) {
                                        tempTile.broadCast(newZone.getCurrentRuler().getName() + "'s domain is now the strongest here!");
                                    }
                                }
                            }
                        }
                    }
                }
                catch (NoSuchZoneException e) {
                    Zones.logger.log(Level.WARNING, "Error getting existing zones when adding new altar.");
                }
            }
        }
        else {
            Zones.altars.add(altar);
        }
    }
    
    static void removeAltar(final Item altar, final boolean silent) {
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            final int actualZone = Math.max(0, altar.getTileY() / 64) * Zones.domainSizeX + Math.max(0, altar.getTileX() / 64);
            final HashMap<Item, FaithZone> thisZone = Zones.altarZones.get(actualZone);
            if (thisZone == null) {
                Zones.logger.log(Level.WARNING, "AltarZone was NULL when it should not have been: " + actualZone);
                return;
            }
            final FaithZone oldZone = thisZone.remove(altar);
            FaithZone newZone = null;
            VolaTile tempTile = null;
            if (!silent && oldZone != null && oldZone.getCurrentRuler() != null) {
                try {
                    for (int i = oldZone.getStartX(); i < oldZone.getEndX(); ++i) {
                        for (int j = oldZone.getStartY(); j < oldZone.getEndY(); ++j) {
                            tempTile = getTileOrNull(i, j, altar.isOnSurface());
                            if (tempTile != null) {
                                newZone = getFaithZone(i, j, altar.isOnSurface());
                                if (newZone == null) {
                                    tempTile.broadCast(oldZone.getCurrentRuler().getName() + " has had to lose " + oldZone.getCurrentRuler().getHisHerItsString() + " hold over this area!");
                                }
                                else {
                                    tempTile.broadCast(newZone.getCurrentRuler().getName() + "'s domain is now the strongest here!");
                                }
                            }
                        }
                    }
                }
                catch (NoSuchZoneException e) {
                    Zones.logger.log(Level.WARNING, "Error getting existing zones when adding new altar.");
                }
            }
        }
        else {
            Zones.altars.remove(altar);
        }
    }
    
    public static void calcCreatures(final Creature responder) {
        int visible = 0;
        int offline = 0;
        int total = 0;
        int submerged = 0;
        int surfbelowsurface = 0;
        int cavebelowsurface = 0;
        for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
            for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                final Creature[] crets = Zones.surfaceZones[x][y].getAllCreatures();
                for (int c = 0; c < crets.length; ++c) {
                    ++total;
                    if (crets[c].isVisible()) {
                        ++visible;
                    }
                    if (crets[c].isOffline()) {
                        ++offline;
                    }
                    if (crets[c].getPositionZ() < -10.0f) {
                        ++surfbelowsurface;
                    }
                }
            }
        }
        for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
            for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                final Creature[] crets = Zones.caveZones[x][y].getAllCreatures();
                for (int c = 0; c < crets.length; ++c) {
                    ++total;
                    ++submerged;
                    if (crets[c].isVisible()) {
                        ++visible;
                    }
                    if (crets[c].isOffline()) {
                        ++offline;
                    }
                    if (crets[c].getPositionZ() < -10.0f) {
                        ++cavebelowsurface;
                    }
                }
            }
        }
        responder.getCommunicator().sendNormalServerMessage("Creatures total:" + total + ", On surface=" + (total - submerged) + " (of which " + surfbelowsurface + " are below -10 meters), in Caves=" + submerged + " (of which " + cavebelowsurface + " are below -10 meters), visible=" + visible + ", offline=" + offline + ".");
    }
    
    public static Item[] getAltars() {
        return Zones.altars.toArray(new Item[Zones.altars.size()]);
    }
    
    public static Item[] getAltars(final int deityId) {
        final Set<Item> lAltars = new HashSet<Item>();
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            for (final HashMap<Item, FaithZone> m : Zones.altarZones) {
                if (m != null) {
                    for (final Item altar : m.keySet()) {
                        if (altar == null) {
                            continue;
                        }
                        final Deity deity = altar.getBless();
                        if ((deity != null || deityId != 0) && (deity == null || deity.getNumber() != deityId)) {
                            continue;
                        }
                        lAltars.add(altar);
                    }
                }
            }
        }
        else {
            for (final Item altar2 : Zones.altars) {
                final Deity deity2 = altar2.getBless();
                if ((deity2 == null && deityId == 0) || (deity2 != null && deity2.getNumber() == deityId)) {
                    lAltars.add(altar2);
                }
            }
        }
        return lAltars.toArray(new Item[lAltars.size()]);
    }
    
    public static void checkAltars() {
        long now = 0L;
        if (Zones.logger.isLoggable(Level.FINER)) {
            Zones.logger.finer("Checking altars.");
            now = System.nanoTime();
        }
        for (final Item altar : Zones.altars) {
            addToDomains(altar);
        }
        if (Zones.logger.isLoggable(Level.FINEST)) {
            final int numberOfAltars = Zones.altars.size();
            Zones.logger.log(Level.FINEST, "Checked " + numberOfAltars + " altars. That took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
        }
        if (Zones.shouldCreateWarTargets) {
            Zones.shouldCreateWarTargets = false;
            final int x = Zones.worldTileSizeX / 6;
            int y = Zones.worldTileSizeY / 6;
            createCampsOnLine(x, y);
            y = Zones.worldTileSizeY / 2;
            createCampsOnLine(x, y);
            y += Zones.worldTileSizeY / 6;
            createCampsOnLine(x, y);
        }
        if (Zones.shouldSourceSprings) {
            Zones.shouldSourceSprings = false;
            createSprings();
        }
    }
    
    public static final void addWarDomains() {
        final Item[] targs = Items.getWarTargets();
        if (targs != null && targs.length > 0) {
            for (final Item target : targs) {
                Kingdoms.addWarTargetKingdom(target);
            }
        }
    }
    
    private static final void createSprings() {
        for (int springs = Zones.worldTileSizeX / 50, a = 0; a < springs; ++a) {
            boolean found = false;
            int tries = 0;
            Zones.logger.log(Level.INFO, "Trying to create spring " + a);
            while (!found && tries < 1000) {
                ++tries;
                final int tx = Zones.worldTileSizeX / 3 + Server.rand.nextInt(Zones.worldTileSizeY / 3);
                final int ty = Zones.worldTileSizeX / 3 + Server.rand.nextInt(Zones.worldTileSizeY / 3);
                final int tile = Server.surfaceMesh.getTile(tx, ty);
                if (Tiles.decodeHeight(tile) > 5) {
                    try {
                        int type = 766;
                        if (Server.rand.nextBoolean()) {
                            type = 767;
                        }
                        final Item target1 = ItemFactory.createItem(type, 100.0f, tx * 4 + 2, ty * 4 + 2, Server.rand.nextInt(360), true, (byte)0, -10L, "");
                        target1.setSizes(target1.getSizeX() + Server.rand.nextInt(1), target1.getSizeY() + Server.rand.nextInt(2), target1.getSizeZ() + Server.rand.nextInt(3));
                        Zones.logger.log(Level.INFO, "Created " + target1.getName() + " at " + target1.getTileX() + " " + target1.getTileY() + " sizes " + target1.getSizeX() + "," + target1.getSizeY() + "," + target1.getSizeZ() + ")");
                        Items.addSourceSpring(target1);
                        found = true;
                    }
                    catch (FailedException fe) {
                        Zones.logger.log(Level.WARNING, fe.getMessage(), fe);
                    }
                    catch (NoSuchTemplateException nst) {
                        Zones.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
        }
    }
    
    private static final void createCampsOnLine(int x, final int y) {
        final Village[] villages = Villages.getVillages();
        boolean found = false;
        int tries = 0;
        Zones.logger.log(Level.INFO, "Trying to create camp at " + x + "," + y);
        while (!found && tries < 1000) {
            ++tries;
            final int tx = safeTileY(x + Server.rand.nextInt(Zones.worldTileSizeX / 3));
            final int ty = safeTileY(y + Server.rand.nextInt(Zones.worldTileSizeY / 3));
            boolean inVillage = false;
            for (final Village v : villages) {
                if (v.coversWithPerimeterAndBuffer(tx, ty, 60)) {
                    inVillage = true;
                    break;
                }
            }
            if (!inVillage) {
                final int tile = Server.surfaceMesh.getTile(tx, ty);
                if (Tiles.decodeHeight(tile) <= 2 || !Terraforming.isAllCornersInsideHeightRange(tx, ty, true, (short)(Tiles.decodeHeight(tile) + 5), (short)(Tiles.decodeHeight(tile) - 5))) {
                    continue;
                }
                try {
                    int type = Server.rand.nextInt(3);
                    if (type == 0) {
                        type = 760;
                    }
                    else if (type == 1) {
                        type = 762;
                    }
                    else if (type == 2) {
                        type = 761;
                    }
                    final Item target1 = ItemFactory.createItem(type, 100.0f, tx * 4 + 2, ty * 4 + 2, Server.rand.nextInt(360), true, (byte)0, -10L, "");
                    target1.setName(createTargName(target1.getName()));
                    Zones.logger.log(Level.INFO, "Created " + target1.getName() + " at " + target1.getTileX() + " " + target1.getTileY());
                    Items.addWarTarget(target1);
                    new FocusZone(tx - 60, tx + 60, ty - 60, ty + 60, (byte)7, target1.getName(), "", true);
                    found = true;
                }
                catch (FailedException fe) {
                    Zones.logger.log(Level.WARNING, fe.getMessage(), fe);
                }
                catch (NoSuchTemplateException nst) {
                    Zones.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
            }
        }
        if (x == 0) {
            x = Zones.worldTileSizeX / 2 - Zones.worldTileSizeX / 6;
        }
        else {
            x = Zones.worldTileSizeX / 2 + Zones.worldTileSizeX / 6;
        }
    }
    
    public static final void createBattleCamp(final int tx, final int ty) {
        try {
            int type = Server.rand.nextInt(3);
            if (type == 0) {
                type = 760;
            }
            else if (type == 1) {
                type = 762;
            }
            else if (type == 2) {
                type = 761;
            }
            final Item target1 = ItemFactory.createItem(type, 100.0f, tx * 4 + 2, ty * 4 + 2, Server.rand.nextInt(360), true, (byte)0, -10L, "");
            target1.setName(createTargName(target1.getName()));
            Zones.logger.log(Level.INFO, "Created " + target1.getName() + " at " + target1.getTileX() + " " + target1.getTileY());
            Items.addWarTarget(target1);
            new FocusZone(tx - 60, tx + 60, ty - 60, ty + 60, (byte)7, target1.getName(), "", true);
        }
        catch (FailedException fe) {
            Zones.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
        catch (NoSuchTemplateException nst) {
            Zones.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
    }
    
    private static final String createTargName(final String origName) {
        switch (Server.rand.nextInt(30)) {
            case 0: {
                return origName + " Unicorn One";
            }
            case 1: {
                return origName + " Deepwoods";
            }
            case 2: {
                return origName + " Goldbar";
            }
            case 3: {
                return origName + " Stinger";
            }
            case 4: {
                return origName + " Forefront";
            }
            case 5: {
                return origName + " Pike Hill";
            }
            case 6: {
                return origName + " Glory Day";
            }
            case 7: {
                return origName + " Silver Anchor";
            }
            case 8: {
                return origName + " Bloody Tip";
            }
            case 9: {
                return origName + " Goreplain";
            }
            case 10: {
                return origName + " Of The Bull";
            }
            case 11: {
                return origName + " Muddyknee";
            }
            case 12: {
                return origName + " First Fist";
            }
            case 13: {
                return origName + " Golden Day";
            }
            case 14: {
                return origName + " Stone Valley";
            }
            case 15: {
                return origName + " New Day";
            }
            case 16: {
                return origName + " Ramona Hill";
            }
            case 17: {
                return "The High " + origName;
            }
            case 18: {
                return "The " + origName + " of Spite";
            }
            case 19: {
                return "The Trolls " + origName;
            }
            case 20: {
                return "Diamond " + origName;
            }
            case 21: {
                return "Silver " + origName;
            }
            case 22: {
                return "Jackal's " + origName;
            }
            case 23: {
                return "Stonefort " + origName;
            }
            case 24: {
                return "Three rings " + origName;
            }
            case 25: {
                return "Fifteen Tears " + origName;
            }
            case 26: {
                return "Final Days " + origName;
            }
            case 27: {
                return "Victory " + origName;
            }
            case 28: {
                return "Cappa Cat " + origName;
            }
            case 29: {
                return "Headstrong " + origName;
            }
            case 30: {
                return "No Surrender " + origName;
            }
            default: {
                return "No Way Back " + origName;
            }
        }
    }
    
    public static final FaithZone[][] getFaithZones(final boolean surfaced) {
        if (surfaced) {
            return Zones.surfaceDomains;
        }
        return Zones.caveDomains;
    }
    
    private static void addToDomains(final Item item) {
        if (item.getData1() != 0) {
            final Deity deity = item.getBless();
            if (deity != null) {
                final int tilex = item.getTileX();
                final int tiley = item.getTileY();
                final int ql = (int)(Servers.localServer.isChallengeServer() ? (item.getCurrentQualityLevel() / 3.0f) : item.getCurrentQualityLevel());
                final int minx = Math.max(0, tilex - ql);
                final int miny = Math.max(0, tiley - ql);
                final int maxx = Math.min(Zones.worldTileSizeX - 1, tilex + ql);
                final int maxy = Math.min(Zones.worldTileSizeY - 1, tiley + ql);
                final FaithZone[] lCoveredFaithZones = getFaithZonesCoveredBy(minx, miny, maxx, maxy, item.isOnSurface());
                if (lCoveredFaithZones != null) {
                    for (int x = 0; x < lCoveredFaithZones.length; ++x) {
                        final int dist = Math.max(Math.abs(tilex - lCoveredFaithZones[x].getCenterX()), Math.abs(tiley - lCoveredFaithZones[x].getCenterY()));
                        if (100 - dist > 0) {
                            lCoveredFaithZones[x].addToFaith(deity, Math.min(ql, 100 - dist));
                        }
                    }
                }
            }
        }
    }
    
    public static void fixTrees() {
        Zones.logger.log(Level.INFO, "Fixing trees.");
        int found = 0;
        final MeshIO mesh = Server.surfaceMesh;
        final Random random = new Random();
        final int ms = Constants.meshSize;
        for (int max = 1 << ms, x = 0; x < max; ++x) {
            for (int y = 0; y < max; ++y) {
                final int tile = mesh.getTile(x, y);
                final byte type = Tiles.decodeType(tile);
                final byte data = Tiles.decodeData(tile);
                final Tiles.Tile theTile = Tiles.getTile(type);
                if (type == Tiles.Tile.TILE_TREE.id || type == Tiles.Tile.TILE_BUSH.id || type == Tiles.Tile.TILE_ENCHANTED_TREE.id || type == Tiles.Tile.TILE_ENCHANTED_BUSH.id || type == Tiles.Tile.TILE_MYCELIUM_TREE.id || type == Tiles.Tile.TILE_MYCELIUM_BUSH.id) {
                    ++found;
                    final byte newLen = (byte)(1 + random.nextInt(3));
                    final byte age = FoliageAge.getAgeAsByte(data);
                    final byte newData = Tiles.encodeTreeData(age, false, false, newLen);
                    byte newType;
                    if (type == Tiles.Tile.TILE_TREE.id) {
                        newType = theTile.getTreeType(data).asNormalTree();
                    }
                    else if (type == Tiles.Tile.TILE_ENCHANTED_TREE.id) {
                        newType = theTile.getTreeType(data).asEnchantedTree();
                    }
                    else if (type == Tiles.Tile.TILE_MYCELIUM_TREE.id) {
                        newType = theTile.getTreeType(data).asMyceliumTree();
                    }
                    else if (type == Tiles.Tile.TILE_BUSH.id) {
                        newType = theTile.getBushType(data).asNormalBush();
                    }
                    else if (type == Tiles.Tile.TILE_ENCHANTED_BUSH.id) {
                        newType = theTile.getBushType(data).asEnchantedBush();
                    }
                    else {
                        newType = theTile.getBushType(data).asMyceliumBush();
                    }
                    mesh.setTile(x, y, Tiles.encode(Tiles.decodeHeight(tile), newType, newData));
                }
            }
        }
        try {
            mesh.saveAll();
            Zones.logger.log(Level.INFO, "Set " + found + " trees to new type.");
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, "Failed to fix trees", iox);
        }
        Constants.RUNBATCH = false;
    }
    
    public static void flash() {
        final int tilex = Server.rand.nextInt(Zones.worldTileSizeX);
        final int tiley = Server.rand.nextInt(Zones.worldTileSizeY);
        flash(tilex, tiley, true);
    }
    
    public static void flash(final int tilex, final int tiley, final boolean doDamage) {
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Flashing tile at " + tilex + ", " + tiley + ", damage: " + doDamage);
        }
        final int tile = Server.surfaceMesh.getTile(tilex, tiley);
        final float height = Math.max(0.0f, Tiles.decodeHeightAsFloat(tile));
        Players.getInstance().weatherFlash(tilex, tiley, height);
        if (doDamage) {
            final VolaTile t = getTileOrNull(tilex, tiley, true);
            if (t != null) {
                t.flashStrike();
            }
        }
    }
    
    public static void flashSpell(final int tilex, final int tiley, final float baseDamage, final Creature caster) {
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Flashing tile at " + tilex + ", " + tiley);
        }
        final int tile = Server.surfaceMesh.getTile(tilex, tiley);
        final float height = Math.max(0.0f, Tiles.decodeHeightAsFloat(tile));
        Players.getInstance().weatherFlash(tilex, tiley, height);
        final VolaTile t = getTileOrNull(tilex, tiley, true);
        if (t != null) {
            t.lightningStrikeSpell(baseDamage, caster);
        }
    }
    
    public static final void fixCaveResources() {
        final int ms = Constants.meshSize;
        final int min = 0;
        final int max = 1 << ms;
        int count = 0;
        final long now = System.nanoTime();
        for (int x = 0; x < max; ++x) {
            for (int y = 0; y < max; ++y) {
                if (Server.getCaveResource(x, y) == 0) {
                    ++count;
                    Server.setCaveResource(x, y, 65535);
                }
            }
        }
        try {
            Server.resourceMesh.saveAll();
        }
        catch (IOException iox) {
            Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        Zones.logger.log(Level.INFO, "Fixed " + count + " cave resources. It took " + (System.nanoTime() - now) / 1000000.0f + " ms.");
    }
    
    public static final void createInvestigatables() {
        final int ms = Constants.meshSize;
        final int min = 0;
        for (int max = 1 << ms, x = 0; x < max; ++x) {
            for (int y = 0; y < max; ++y) {
                if (Server.rand.nextFloat() < 0.6f) {
                    Server.setInvestigatable(x, y, true);
                }
            }
        }
    }
    
    public static final void createSeeds() {
        final MeshIO mesh = Server.surfaceMesh;
        final int ms = Constants.meshSize;
        final int min = 0;
        final int max = 1 << ms;
        int count = 0;
        final long now = System.nanoTime();
        for (int x = 0; x < max; ++x) {
            for (int y = 0; y < max; ++y) {
                final int tile = mesh.getTile(x, y);
                final byte tileType = Tiles.decodeType(tile);
                if (tileType == Tiles.Tile.TILE_GRASS.id || tileType == Tiles.Tile.TILE_MARSH.id || tileType == Tiles.Tile.TILE_MYCELIUM.id) {
                    if (Tiles.decodeHeight(tile) > -20 && TilePoller.checkForSeedGrowth(tile, x, y)) {
                        ++count;
                    }
                }
                else if (tileType == Tiles.Tile.TILE_STEPPE.id || tileType == Tiles.Tile.TILE_TUNDRA.id || tileType == Tiles.Tile.TILE_MOSS.id || tileType == Tiles.Tile.TILE_PEAT.id) {
                    if (Tiles.decodeHeight(tile) > 0 && TilePoller.checkForSeedGrowth(tile, x, y)) {
                        ++count;
                    }
                }
                else if (tileType == Tiles.Tile.TILE_TREE.id || tileType == Tiles.Tile.TILE_BUSH.id || tileType == Tiles.Tile.TILE_MYCELIUM_TREE.id || tileType == Tiles.Tile.TILE_MYCELIUM_BUSH.id) {
                    if (TilePoller.checkForSeedGrowth(tile, x, y)) {
                        ++count;
                    }
                }
                else if (tileType == Tiles.Tile.TILE_ROCK.id || tileType == Tiles.Tile.TILE_CLIFF.id) {
                    if (TilePoller.checkCreateIronRock(x, y)) {
                        ++count;
                    }
                }
                else if (Tiles.isRoadType(tileType) || Tiles.isEnchanted(tileType) || tileType == Tiles.Tile.TILE_DIRT_PACKED.id || tileType == Tiles.Tile.TILE_DIRT.id || tileType == Tiles.Tile.TILE_LAWN.id || tileType == Tiles.Tile.TILE_MYCELIUM_LAWN.id) {
                    TilePoller.checkInvestigateGrowth(tile, x, y);
                }
            }
        }
        final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
        Zones.logger.log(Level.INFO, "Created " + count + " seeds. It took " + lElapsedTime + " millis.");
    }
    
    public static final void addDuelRing(final Item item) {
        Zones.duelRings.add(item);
    }
    
    public static final void removeDuelRing(final Item item) {
        Zones.duelRings.remove(item);
    }
    
    public static final Item isWithinDuelRing(final int tileX, final int tileY, final boolean surfaced) {
        if (!surfaced) {
            return null;
        }
        final int maxDist = 20;
        for (final Item ring : Zones.duelRings) {
            if (ring.getZoneId() > 0 && !ring.deleted && ring.getTileX() < tileX + 20 && ring.getTileX() > tileX - 20 && ring.getTileY() < tileY + 20 && ring.getTileY() > tileY - 20) {
                return ring;
            }
        }
        return null;
    }
    
    public static boolean isTreeCapable(final int x, final int y, final MeshIO mesh, final int width, final int tile) {
        if (Tiles.decodeHeight(tile) < 0) {
            return false;
        }
        if (Tiles.decodeType(tile) != Tiles.Tile.TILE_DIRT.id) {
            return false;
        }
        int neighborTrees = 0;
        for (int xx = x - 1; xx <= x + 1; ++xx) {
            for (int yy = y - 1; yy <= y + 1; ++yy) {
                final int xxx = xx & width - 1;
                final int yyy = yy & width - 1;
                final int t = mesh.getTile(xxx, yyy);
                if (Tiles.decodeType(t) == Tiles.Tile.TILE_TREE.id) {
                    ++neighborTrees;
                }
            }
        }
        return neighborTrees < 5;
    }
    
    private static final void initializeDomains() {
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            for (int y = 0; y < Zones.domainSizeY; ++y) {
                for (int x = 0; x < Zones.domainSizeX; ++x) {
                    Zones.altarZones.add(null);
                }
            }
        }
        else {
            for (int x2 = 0; x2 < Zones.faithSizeX; ++x2) {
                for (int y2 = 0; y2 < Zones.faithSizeY; ++y2) {
                    Zones.surfaceDomains[x2][y2] = new FaithZone((short)(x2 * 8), (short)(y2 * 8), (short)(x2 * 8 + 7), (short)(y2 * 8 + 7));
                    Zones.caveDomains[x2][y2] = new FaithZone((short)(x2 * 8), (short)(y2 * 8), (short)(x2 * 8 + 7), (short)(y2 * 8 + 7));
                }
            }
            Zones.logger.log(Level.INFO, "Number of faithzones=" + Zones.faithSizeX * Zones.faithSizeX + " surfaced as well as underground.");
        }
    }
    
    public static final FaithZone getFaithZone(final int tilex, final int tiley, final boolean surfaced) throws NoSuchZoneException {
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            FaithZone toReturn = null;
            HashMap<Item, FaithZone> thisZone = null;
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    final int actualZone = Math.max(0, Math.min(tiley / 64 + j, Zones.domainSizeY - 1)) * Zones.domainSizeX + Math.max(0, Math.min(tilex / 64 + i, Zones.domainSizeX - 1));
                    if (actualZone < Zones.altarZones.size()) {
                        thisZone = Zones.altarZones.get(actualZone);
                        if (thisZone != null) {
                            for (final FaithZone f : thisZone.values()) {
                                if (f.getCurrentRuler() == null) {
                                    continue;
                                }
                                if (!f.containsTile(tilex, tiley)) {
                                    continue;
                                }
                                if (f.getStrengthForTile(tilex, tiley, surfaced) <= 0) {
                                    continue;
                                }
                                if (toReturn == null) {
                                    toReturn = f;
                                }
                                else if (toReturn.getStrengthForTile(tilex, tiley, surfaced) < f.getStrengthForTile(tilex, tiley, surfaced)) {
                                    toReturn = f;
                                }
                                else {
                                    if (toReturn.getStrengthForTile(tilex, tiley, surfaced) != f.getStrengthForTile(tilex, tiley, surfaced)) {
                                        continue;
                                    }
                                    final int fDist = Math.min(Math.abs(f.getCenterX() - tilex), Math.abs(f.getCenterY() - tiley));
                                    final int rDist = Math.min(Math.abs(toReturn.getCenterX() - tilex), Math.abs(toReturn.getCenterY() - tiley));
                                    if (fDist >= rDist) {
                                        continue;
                                    }
                                    toReturn = f;
                                }
                            }
                        }
                    }
                }
            }
            return toReturn;
        }
        if (tilex < 0 || tilex >= Zones.worldTileSizeX || tiley < 0 || tiley >= Zones.worldTileSizeY) {
            throw new NoSuchZoneException("No faith zone at " + tilex + ", " + tiley);
        }
        if (surfaced) {
            return Zones.surfaceDomains[tilex >> 3][tiley >> 3];
        }
        return Zones.caveDomains[tilex >> 3][tiley >> 3];
    }
    
    private static boolean areaOverlapsFaithZone(final FaithZone f, final int startx, final int starty, final int endx, final int endy) {
        for (int i = startx; i < endx; ++i) {
            for (int j = starty; j < endy; ++j) {
                if (f.containsTile(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final ArrayList<HashMap<Item, FaithZone>> getCoveredZones(final int startx, final int starty, final int endx, final int endy) {
        final ArrayList<HashMap<Item, FaithZone>> returnList = new ArrayList<HashMap<Item, FaithZone>>();
        for (int y = Math.min(0, starty / 64); y <= Math.min(0, endy / 64); ++y) {
            for (int x = Math.min(0, startx / 64); x <= Math.min(0, endx / 64); ++x) {
                final HashMap<Item, FaithZone> thisZone = Zones.altarZones.get(y * Zones.domainSizeX + x);
                if (thisZone != null && !returnList.contains(thisZone)) {
                    returnList.add(thisZone);
                }
            }
        }
        return returnList;
    }
    
    public static final FaithZone[] getFaithZonesCoveredBy(final int startx, final int starty, final int endx, final int endy, final boolean surfaced) {
        final Set<FaithZone> zoneList = new HashSet<FaithZone>();
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            final ArrayList<HashMap<Item, FaithZone>> coveredZones = getCoveredZones(startx, starty, endx, endy);
            for (final HashMap<Item, FaithZone> z : coveredZones) {
                for (final FaithZone f : z.values()) {
                    if (f.getCurrentRuler() != null && areaOverlapsFaithZone(f, startx, starty, endx, endy)) {
                        zoneList.add(f);
                    }
                }
            }
        }
        else {
            for (int x = startx >> 3; x <= endx >> 3; ++x) {
                for (int y = starty >> 3; y <= endy >> 3; ++y) {
                    FaithZone zone2 = Zones.surfaceDomains[x][y];
                    if (!surfaced) {
                        zone2 = Zones.caveDomains[x][y];
                    }
                    zoneList.add(zone2);
                }
            }
        }
        final FaithZone[] toReturn = new FaithZone[zoneList.size()];
        return zoneList.toArray(toReturn);
    }
    
    public static final FaithZone[] getFaithZones() {
        final ArrayList<FaithZone> allZones = new ArrayList<FaithZone>();
        for (final HashMap<Item, FaithZone> z : Zones.altarZones) {
            if (z != null) {
                allZones.addAll(z.values());
            }
        }
        final FaithZone[] toReturn = new FaithZone[allZones.size()];
        return allZones.toArray(toReturn);
    }
    
    private static void createZones() throws IOException {
        Zones.logger.log(Level.INFO, "Creating zones: size is " + Zones.worldTileSizeX + ", " + Zones.worldTileSizeY);
        Zones.loading = true;
        final long now = System.nanoTime();
        Zones.MESHSIZE = 1 << Constants.meshSize;
        initializeDomains();
        initializeHiveZones();
        int numz = 0;
        final int ms = Zones.MESHSIZE >> 6;
        final int zs = 6;
        final int zsize = 64;
        final boolean useDB = true;
        final Zone[][] szones = new Zone[Zones.worldTileSizeX >> 6][Zones.worldTileSizeY >> 6];
        Zones.numberOfZones = szones.length * szones[0].length;
        if (Zones.logger.isLoggable(Level.FINE)) {
            Zones.logger.fine("Number of zones x=" + (Zones.worldTileSizeX >> 6) + ", total=" + Zones.numberOfZones);
            Zones.logger.fine("This should equal zones x=" + (Zones.MESHSIZE >> 6));
            Zones.logger.finer("Zone 54, 54=3456, 3519,3456,3519");
        }
        for (int x = 0; x < ms; ++x) {
            for (int y = 0; y < ms; ++y) {
                szones[x][y] = new DbZone(x << 6, (x << 6) + 64 - 1, y << 6, (y << 6) + 64 - 1, true);
                ++numz;
            }
        }
        Zones.logger.info("Initialised surface zones - array size: [" + ms + "][" + ms + "];");
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("This should equal number of zones: " + numz);
        }
        final Zone[][] cZones = new Zone[Zones.worldTileSizeX >> 6][Zones.worldTileSizeY >> 6];
        for (int x2 = 0; x2 < ms; ++x2) {
            for (int y2 = 0; y2 < ms; ++y2) {
                cZones[x2][y2] = new DbZone(x2 << 6, (x2 << 6) + 64 - 1, y2 << 6, (y2 << 6) + 64 - 1, false);
            }
        }
        Zones.logger.info("Initialised cave zones - array size: [" + (Zones.worldTileSizeX >> 6) + "][" + (Zones.worldTileSizeY >> 6) + "];");
        Zones.logger.log(Level.INFO, "Seconds between polls=800, zonespolled=" + Zone.zonesPolled + ", maxnumberofzonespolled=" + Zone.maxZonesPolled);
        Zones.surfaceZones = szones;
        Zones.caveZones = cZones;
        Zones.logger.info("Loading surface and cave zone structures");
        Structures.loadAllStructures();
        numz = 0;
        for (int x2 = 0; x2 < ms; ++x2) {
            for (int y2 = 0; y2 < ms; ++y2) {
                Zones.surfaceZones[x2][y2].loadFences();
                Zones.caveZones[x2][y2].loadFences();
            }
        }
        Zones.logger.info("Loaded fences");
        DbStructure.loadBuildTiles();
        Zones.logger.info("Loaded build tiles");
        Structures.endLoadAll();
        Zones.logger.info("Ended loading of structures");
        for (int x2 = 0; x2 < ms; ++x2) {
            for (int y2 = 0; y2 < ms; ++y2) {
                ++numz;
                Zones.surfaceZones[x2][y2].loadAllItemsForZone();
                Zones.caveZones[x2][y2].loadAllItemsForZone();
            }
        }
        Zones.logger.info("Loaded zone items");
        Zones.logger.info("Loaded " + numz + " surface and cave zones");
        final float mod = 40.0f;
        Zones.zonesPerRun = (int)(Zones.numberOfZones / 40.0f);
        Zones.maxRest = (int)(Zones.numberOfZones - Zones.zonesPerRun * 40.0f);
        Zones.rest = Zones.maxRest;
        Zones.loading = false;
        final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
        Zones.logger.log(Level.INFO, "Zones created and loaded. Number of zones=" + Zones.numberOfZones + ". PollFraction is " + Zones.rest + ". That took " + lElapsedTime + " millis.");
    }
    
    public static void writeZones() {
        if (!Servers.localServer.LOGINSERVER) {
            final File f = new File("zones" + Server.rand.nextInt(10000) + ".txt");
            BufferedWriter output = null;
            try {
                output = new BufferedWriter(new FileWriter(f));
                output.write("Legend:\n");
                output.write("No kingdom: 0\n");
                output.write("J/K: =\n");
                output.write("J/K village: v\n");
                output.write("J/K tower: t\n");
                output.write("J/K village+tower: V\n");
                output.write("HOTS: #\n");
                output.write("HOTS village: w\n");
                output.write("HOTS tower: g\n");
                output.write("HOTS village+tower: W\n\n");
                for (int x = 0; x < Zones.MESHSIZE >> 6; ++x) {
                    for (int y = 0; y < Zones.MESHSIZE >> 6; ++y) {
                        Zones.surfaceZones[x][y].write(output);
                    }
                    output.write("\n");
                    output.flush();
                }
            }
            catch (IOException iox) {
                Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
                try {
                    if (output != null) {
                        output.close();
                    }
                }
                catch (IOException iox) {
                    Zones.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
            finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                }
                catch (IOException iox2) {
                    Zones.logger.log(Level.WARNING, iox2.getMessage(), iox2);
                }
            }
        }
    }
    
    public static boolean containsVillage(final int x, final int y, final boolean surfaced) {
        try {
            final Zone zone = getZone(x, y, surfaced);
            return zone.containsVillage(x, y);
        }
        catch (NoSuchZoneException ex) {
            return false;
        }
    }
    
    public static Village getVillage(@Nonnull final TilePos tilePos, final boolean surfaced) {
        return getVillage(tilePos.x, tilePos.y, surfaced);
    }
    
    public static Village getVillage(final int x, final int y, final boolean surfaced) {
        try {
            final Zone zone = getZone(x, y, surfaced);
            return zone.getVillage(x, y);
        }
        catch (NoSuchZoneException ex) {
            return null;
        }
    }
    
    public static void pollNextZones(final long sleepTime) throws IOException {
        int bonusZone;
        if (Zones.rest > 0) {
            bonusZone = 1;
            --Zones.rest;
        }
        else {
            bonusZone = 0;
        }
        synchronized (Zones.ZONE_SYNC_LOCK) {
            for (int x = 0; x < Zones.zonesPerRun + bonusZone; ++x) {
                if (Zones.currentPollZoneY == 1) {
                    Creatures.getInstance().pollAllCreatures(Zones.currentPollZoneX);
                }
                if (Zones.currentPollZoneX >= Zones.worldTileSizeX >> 6) {
                    Zones.currentPollZoneX = 0;
                    ++Zones.currentPollZoneY;
                }
                if (Zones.currentPollZoneY >= Zones.worldTileSizeY >> 6) {
                    Zones.currentPollZoneY = 0;
                    Zones.rest = Zones.maxRest;
                    if (Zones.pollnum > Zones.numberOfZones) {
                        Zones.pollnum = 0;
                    }
                    Zones.pollnum += 16;
                    Server.incrementCombatCounter();
                    Server.incrementSecondsUptime();
                    PlayerInfoFactory.pollPremiumPlayers();
                    FocusZone.pollAll();
                    if (Server.getCombatCounter() % 10 == 0) {
                        CombatHandler.resolveRound();
                    }
                    Players.getInstance().pollDeadPlayers();
                    Players.getInstance().pollKosWarnings();
                    if (Zones.logger.isLoggable(Level.FINEST)) {
                        Zones.logger.finest("Pollnum=" + Zones.pollnum + ", max=" + Zones.numberOfZones + " cpzy=" + Zones.currentPollZoneY + " cpzx=" + Zones.currentPollZoneX);
                    }
                }
                final Zone lSurfaceZone = Zones.surfaceZones[Zones.currentPollZoneX][Zones.currentPollZoneY];
                if (lSurfaceZone != null && lSurfaceZone.isLoaded()) {
                    lSurfaceZone.poll(Zones.pollnum);
                }
                final Zone lCaveZone = Zones.caveZones[Zones.currentPollZoneX][Zones.currentPollZoneY];
                if (lCaveZone != null && lCaveZone.isLoaded()) {
                    lCaveZone.poll(Zones.numberOfZones + Zones.pollnum);
                }
                ++Zones.currentPollZoneX;
            }
        }
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Polled " + (Zones.zonesPerRun + bonusZone) + " zones to " + Zones.currentPollZoneX + ", " + Zones.currentPollZoneY);
        }
    }
    
    public static void saveAllZones() {
        for (int worldSize = 1 << Constants.meshSize, x = 0; x < worldSize >> 6; ++x) {
            for (int y = 0; y < worldSize >> 6; ++y) {
                try {
                    if (Zones.surfaceZones[x][y].isLoaded()) {
                        Zones.surfaceZones[x][y].save();
                    }
                }
                catch (IOException iox) {
                    Zones.logger.log(Level.WARNING, "Failed to save surface zone " + x + ", " + y);
                }
                try {
                    if (Zones.caveZones[x][y].isLoaded()) {
                        Zones.caveZones[x][y].save();
                    }
                }
                catch (IOException iox) {
                    Zones.logger.log(Level.WARNING, "Failed to save cave zone " + x + ", " + y);
                }
            }
        }
    }
    
    public static void saveNextZone() throws IOException {
        if (Zones.currentSaveZoneX >= Zones.worldTileSizeX >> 6) {
            Zones.currentSaveZoneX = 0;
            ++Zones.currentSaveZoneY;
        }
        if (Zones.currentSaveZoneY >= Zones.worldTileSizeY >> 6) {
            Zones.currentSaveZoneY = 0;
        }
        final Zone lSurfaceSaveZone = Zones.surfaceZones[Zones.currentSaveZoneX][Zones.currentSaveZoneY];
        if (lSurfaceSaveZone != null && lSurfaceSaveZone.isLoaded()) {
            lSurfaceSaveZone.save();
        }
        final Zone lCaveSaveZone = Zones.caveZones[Zones.currentSaveZoneX][Zones.currentSaveZoneY];
        if (lCaveSaveZone != null && lCaveSaveZone.isLoaded()) {
            lCaveSaveZone.save();
        }
        ++Zones.currentSaveZoneX;
    }
    
    public static VirtualZone createZone(final Creature watcher, final int startX, final int startY, final int centerX, final int centerY, final int size, final boolean surface) {
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Creating virtual zone " + startX + ", " + startY + ", size: " + size + ", surface: " + surface + " for " + watcher.getName());
        }
        final VirtualZone zone = new VirtualZone(watcher, startX, startY, centerX, centerY, size, surface);
        Zones.virtualZones.put(zone.getId(), zone);
        return zone;
    }
    
    public static void removeZone(final int id) {
        Zones.virtualZones.remove(id);
    }
    
    static VirtualZone getVirtualZone(final int number) throws NoSuchZoneException {
        final VirtualZone toReturn = Zones.virtualZones.get(number);
        if (toReturn == null) {
            throw new NoSuchZoneException("No zone with number " + number);
        }
        return toReturn;
    }
    
    public static Zone getZone(final int number) throws NoSuchZoneException {
        final Zone toReturn = null;
        if (toReturn == null) {
            for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
                for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                    if (Zones.surfaceZones[x][y].getId() == number) {
                        return Zones.surfaceZones[x][y];
                    }
                }
            }
        }
        if (toReturn == null) {
            for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
                for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                    if (Zones.caveZones[x][y].getId() == number) {
                        return Zones.caveZones[x][y];
                    }
                }
            }
        }
        if (toReturn == null) {
            throw new NoSuchZoneException("No zone with number " + number);
        }
        return toReturn;
    }
    
    public static int getZoneIdFor(final int x, final int y, final boolean surfaced) throws NoSuchZoneException {
        final Zone toReturn = getZone(x, y, surfaced);
        return toReturn.getId();
    }
    
    public static Zone getZone(@Nonnull final TilePos tilePos, final boolean surfaced) throws NoSuchZoneException {
        return getZone(tilePos.x, tilePos.y, surfaced);
    }
    
    public static Zone getZone(final int tilex, final int tiley, final boolean surfaced) throws NoSuchZoneException {
        Zone toReturn = null;
        Label_0210: {
            if (surfaced) {
                try {
                    toReturn = Zones.surfaceZones[tilex >> 6][tiley >> 6];
                    if (!toReturn.covers(tilex, tiley)) {
                        Zones.logger.log(Level.WARNING, "Error in the way zones are fetched. Doesn't work for " + (tilex >> 6) + " to cover " + tilex + " or " + (tiley >> 6) + " to cover " + tiley);
                    }
                    break Label_0210;
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    throw new NoSuchZoneException("No such zone: x=" + (tilex >> 6) + ", y=" + (tiley >> 6), ex);
                }
            }
            try {
                toReturn = Zones.caveZones[tilex >> 6][tiley >> 6];
            }
            catch (Exception ex2) {
                throw new NoSuchZoneException("No such cave zone: x=" + (tilex >> 6) + ", y=" + (tiley >> 6), ex2);
            }
            try {
                if (!toReturn.isLoaded()) {
                    Zones.logger.log(Level.WARNING, "THIS SHOULD NOT HAPPEN - zone: x=" + (tilex >> 6) + ", y=" + (tiley >> 6) + " - surfaced: " + surfaced, new Exception());
                    toReturn.load();
                    toReturn.loadFences();
                }
            }
            catch (IOException ex3) {
                Zones.logger.log(Level.WARNING, "Failed to load zone " + (tilex >> 6) + ", " + (tiley >> 6) + ". Zone will be empty.", ex3);
            }
        }
        return toReturn;
    }
    
    public static Zone[] getZonesCoveredBy(final VirtualZone zone) {
        final Set<Zone> zoneList = new HashSet<Zone>();
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Getting zones covered by " + zone.getId() + ": " + zone.getStartX() + "," + zone.getEndX() + ",y:" + zone.getStartY() + "," + zone.getEndY() + " which starts at " + (zone.getStartX() >> 6) + "," + (zone.getStartY() >> 6) + " and ends at " + (zone.getEndX() >> 6) + "," + (zone.getEndY() >> 6));
        }
        for (int x = zone.getStartX() >> 6; x <= zone.getEndX() >> 6; ++x) {
            for (int y = zone.getStartY() >> 6; y <= zone.getEndY() >> 6; ++y) {
                try {
                    final Zone zone2 = getZone(x << 6, y << 6, zone.isOnSurface());
                    if (Zones.logger.isLoggable(Level.FINEST)) {
                        Zones.logger.finest("Adding " + zone2.getId() + ": " + zone2.getStartX() + "," + zone2.getStartY() + "-" + zone2.getEndX() + "," + zone2.getEndY() + " which is at " + x + "," + y);
                    }
                    zoneList.add(zone2);
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        final Zone[] toReturn = new Zone[zoneList.size()];
        return zoneList.toArray(toReturn);
    }
    
    public static final void checkAllSurfaceZones(final Creature checker) {
        for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
            for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                Zones.surfaceZones[x][y].checkIntegrity(checker);
            }
        }
    }
    
    public static final void checkAllCaveZones(final Creature checker) {
        for (int x = 0; x < Zones.worldTileSizeX >> 6; ++x) {
            for (int y = 0; y < Zones.worldTileSizeY >> 6; ++y) {
                Zones.caveZones[x][y].checkIntegrity(checker);
            }
        }
    }
    
    public static Zone[] getZonesCoveredBy(final int startx, final int starty, final int endx, final int endy, final boolean surfaced) {
        final Set<Zone> zoneList = new HashSet<Zone>();
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Getting zones covered by x: " + startx + "," + endx + ",y:" + starty + "," + endy + " which starts at " + (startx >> 6) + "," + (starty >> 6) + " and ends at " + (endx >> 6) + "," + (endy >> 6));
        }
        for (int x = startx >> 6; x <= endx >> 6; ++x) {
            for (int y = starty >> 6; y <= endy >> 6; ++y) {
                try {
                    final Zone zone2 = getZone(x << 6, y << 6, surfaced);
                    if (Zones.logger.isLoggable(Level.FINEST)) {
                        Zones.logger.finest("Adding " + zone2.getId() + ": " + zone2.getStartX() + "," + zone2.getStartY() + "-" + zone2.getEndX() + "," + zone2.getEndY() + " which is at " + x + "," + y);
                    }
                    zoneList.add(zone2);
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        final Zone[] toReturn = new Zone[zoneList.size()];
        return zoneList.toArray(toReturn);
    }
    
    public static boolean isStructureFinished(final Creature creature, final Wall wall) {
        try {
            final Structure struct = Structures.getStructure(wall.getStructureId());
            return struct.isFinished();
        }
        catch (NoSuchStructureException nss) {
            Zones.logger.log(Level.WARNING, "No structure for wall with id " + wall.getId());
            return false;
        }
    }
    
    public static int getTileIntForTile(final int xTile, final int yTile, final int layer) {
        if (layer < 0) {
            return Server.caveMesh.getTile(safeTileX(xTile), safeTileY(yTile));
        }
        return Server.surfaceMesh.getTile(safeTileX(xTile), safeTileY(yTile));
    }
    
    public static byte getTextureForTile(final int xTile, final int yTile, final int layer) {
        if (layer < 0) {
            if (xTile < 0 || xTile > Zones.worldTileSizeX || yTile < 0 || yTile > Zones.worldTileSizeY) {
                return Tiles.Tile.TILE_ROCK.id;
            }
            return Tiles.decodeType(Server.caveMesh.getTile(xTile, yTile));
        }
        else {
            if (xTile < 0 || xTile > Zones.worldTileSizeX || yTile < 0 || yTile > Zones.worldTileSizeY) {
                return Tiles.Tile.TILE_DIRT.id;
            }
            return Tiles.decodeType(Server.surfaceMesh.getTile(xTile, yTile));
        }
    }
    
    private static final double getDir(final float x, final float y) {
        double degree = 1.0;
        if (x != 0.0f) {
            if (y != 0.0f) {
                for (degree = Math.atan2(y, x), degree = degree * 57.29577951308232 + 90.0; degree < 0.0; degree += 360.0) {}
                while (degree >= 360.0) {
                    degree -= 360.0;
                }
            }
            else if (x < 0.0f) {
                degree = 270.0;
            }
            else {
                degree = 90.0;
            }
        }
        else if (y != 0.0f) {
            if (y > 0.0f) {
                degree = 180.0;
            }
            else {
                degree = 360.0;
            }
        }
        return degree;
    }
    
    public static int getTileZoneFor(final float posX, final float posY, final int tilex, final int tiley) {
        float xa = posX - tilex * 4;
        float ya = posY - tiley * 4;
        xa -= 2.0f;
        ya -= 2.0f;
        final double rot = getDir(xa, ya);
        if (Zones.logger.isLoggable(Level.FINEST)) {
            Zones.logger.finest("Rot to " + xa + ", " + ya + " is " + rot);
        }
        if (rot <= 45.0) {
            return 0;
        }
        if (rot <= 90.0) {
            return 1;
        }
        if (rot <= 135.0) {
            return 2;
        }
        if (rot <= 180.0) {
            return 3;
        }
        if (rot <= 225.0) {
            return 4;
        }
        if (rot <= 270.0) {
            return 5;
        }
        if (rot <= 315.0) {
            return 6;
        }
        return 7;
    }
    
    public static final float calculateRockHeight(final float posX, final float posY) throws NoSuchZoneException {
        float height = 0.0f;
        try {
            final int tilex = (int)posX >> 2;
            final int tiley = (int)posY >> 2;
            final MeshIO mesh = Server.rockMesh;
            final int[] meshData = mesh.data;
            float xa = posX / 4.0f - tilex;
            float ya = posY / 4.0f - tiley;
            if (xa > ya) {
                if (ya >= 0.999f) {
                    ya = 0.999f;
                }
                xa -= ya;
                xa /= 1.0f - ya;
                if (xa < 0.0f) {
                    xa = 0.0f;
                }
                if (xa > 1.0f) {
                    xa = 1.0f;
                }
                final float xheight1 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley << Constants.meshSize]) * (1.0f - xa) + Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley << Constants.meshSize]) * xa;
                final float xheight2 = Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley + 1 << Constants.meshSize]);
                height = xheight1 * (1.0f - ya) + xheight2 * ya;
            }
            else {
                if (ya <= 0.001f) {
                    ya = 0.001f;
                }
                xa /= ya;
                if (xa < 0.0f) {
                    xa = 0.0f;
                }
                if (xa > 1.0f) {
                    xa = 1.0f;
                }
                final float xheight1 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley << Constants.meshSize]);
                final float xheight2 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley + 1 << Constants.meshSize]) * (1.0f - xa) + Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley + 1 << Constants.meshSize]) * xa;
                height = xheight1 * (1.0f - ya) + xheight2 * ya;
            }
        }
        catch (Exception ex) {
            if (Zones.logger.isLoggable(Level.FINER)) {
                Zones.logger.log(Level.FINER, "No such zone at " + posX + ", " + posY, ex);
            }
            throw new NoSuchZoneException("No such zone", ex);
        }
        return height;
    }
    
    public static final float calculateHeight(final float posX, final float posY, final boolean surfaced) throws NoSuchZoneException {
        float height = 0.0f;
        try {
            final int tilex = (int)posX >> 2;
            final int tiley = (int)posY >> 2;
            MeshIO mesh = Server.surfaceMesh;
            if (!surfaced) {
                mesh = Server.caveMesh;
            }
            else if (Tiles.decodeType(mesh.getTile(tilex, tiley)) == Tiles.Tile.TILE_HOLE.id) {
                mesh = Server.caveMesh;
            }
            final int[] meshData = mesh.data;
            float xa = posX / 4.0f - tilex;
            float ya = posY / 4.0f - tiley;
            if (xa > ya) {
                if (ya >= 0.999f) {
                    ya = 0.999f;
                }
                xa -= ya;
                xa /= 1.0f - ya;
                if (xa < 0.0f) {
                    xa = 0.0f;
                }
                if (xa > 1.0f) {
                    xa = 1.0f;
                }
                final float xheight1 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley << Constants.meshSize]) * (1.0f - xa) + Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley << Constants.meshSize]) * xa;
                final float xheight2 = Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley + 1 << Constants.meshSize]);
                height = xheight1 * (1.0f - ya) + xheight2 * ya;
            }
            else {
                if (ya <= 0.001f) {
                    ya = 0.001f;
                }
                xa /= ya;
                if (xa < 0.0f) {
                    xa = 0.0f;
                }
                if (xa > 1.0f) {
                    xa = 1.0f;
                }
                final float xheight1 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley << Constants.meshSize]);
                final float xheight2 = Tiles.decodeHeightAsFloat(meshData[tilex | tiley + 1 << Constants.meshSize]) * (1.0f - xa) + Tiles.decodeHeightAsFloat(meshData[tilex + 1 | tiley + 1 << Constants.meshSize]) * xa;
                height = xheight1 * (1.0f - ya) + xheight2 * ya;
            }
        }
        catch (Exception ex) {
            if (Zones.logger.isLoggable(Level.FINER)) {
                Zones.logger.log(Level.FINER, "No such zone at " + posX + ", " + posY, ex);
            }
            throw new NoSuchZoneException("No such zone", ex);
        }
        return height;
    }
    
    public static final float calculatePosZ(final float posx, final float posy, final VolaTile tile, final boolean isOnSurface, final boolean floating, final float currentPosZ, @Nullable final Creature creature, final long bridgeId) {
        try {
            final float basePosZ = calculateHeight(posx, posy, isOnSurface);
            if (bridgeId > 0L) {
                final int xx = (int)StrictMath.floor(posx / 4.0f);
                final int yy = (int)StrictMath.floor(posy / 4.0f);
                final float xa = posx / 4.0f - xx;
                final float ya = posy / 4.0f - yy;
                final float[] hts = getNodeHeights(xx, yy, isOnSurface ? 0 : -1, bridgeId);
                return hts[0] * (1.0f - xa) * (1.0f - ya) + hts[1] * xa * (1.0f - ya) + hts[2] * (1.0f - xa) * ya + hts[3] * xa * ya;
            }
            int currentFloorLevel = 0;
            if (creature != null) {
                currentFloorLevel = creature.getFloorLevel();
            }
            else if (currentPosZ > basePosZ) {
                currentFloorLevel = (int)(currentPosZ - basePosZ) / 3;
            }
            if (tile != null) {
                final int dfl = tile.getDropFloorLevel(currentFloorLevel);
                final Floor[] f = tile.getFloors(dfl * 30, dfl * 30);
                final float h = basePosZ + Math.max(0, dfl * 3) + ((f.length > 0) ? 0.25f : 0.0f);
                return floating ? Math.max(0.0f + ((creature != null) ? creature.getTemplate().offZ : 0.0f), h) : h;
            }
            final VolaTile ttile = getTileOrNull((int)posx / 4, (int)posy / 4, isOnSurface);
            if (ttile != null) {
                final int dfl2 = ttile.getDropFloorLevel(currentFloorLevel);
                final Floor[] f2 = ttile.getFloors(dfl2 * 30, dfl2 * 30);
                final float h2 = basePosZ + Math.max(0, dfl2 * 3) + ((f2.length > 0) ? 0.25f : 0.0f);
                return floating ? Math.max(0.0f + ((creature != null) ? creature.getTemplate().offZ : 0.0f), h2) : h2;
            }
            if (floating) {
                return Math.max(0.0f + ((creature != null) ? creature.getTemplate().offZ : 0.0f), basePosZ);
            }
            return basePosZ;
        }
        catch (NoSuchZoneException nsz) {
            Zones.logger.log(Level.WARNING, "No Zone for tile " + posx + ", " + posy + " " + isOnSurface, new Exception());
            return currentPosZ;
        }
    }
    
    public static final float[] getNodeHeights(final int xNode, final int yNode, final int layer, final long bridgeId) {
        float NW = 0.0f;
        float NE = 0.0f;
        float SW = 0.0f;
        float SE = 0.0f;
        if (bridgeId > 0L) {
            final VolaTile vt = getTileOrNull(xNode, yNode, layer == 0);
            if (vt != null) {
                for (final BridgePart bp : vt.getBridgeParts()) {
                    if (bp.getStructureId() == bridgeId && bp.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED) {
                        NW = bp.getHeightOffset() / 10.0f;
                        SE = (bp.getHeightOffset() + bp.getSlope()) / 10.0f;
                        if (bp.getDir() == 0 || bp.getDir() == 4) {
                            NE = NW;
                            SW = SE;
                        }
                        else {
                            NE = SE;
                            SW = NW;
                        }
                        return new float[] { NW, NE, SW, SE };
                    }
                }
            }
        }
        NW = getHeightForNode(xNode, yNode, layer);
        NE = getHeightForNode(xNode + 1, yNode, layer);
        SW = getHeightForNode(xNode, yNode + 1, layer);
        SE = getHeightForNode(xNode + 1, yNode + 1, layer);
        return new float[] { NW, NE, SW, SE };
    }
    
    public static final float getHeightForNode(int xNode, int yNode, final int layer) {
        MeshIO mesh = Server.surfaceMesh;
        if (layer < 0) {
            mesh = Server.caveMesh;
        }
        if (xNode < 0 || xNode >= 1 << Constants.meshSize) {
            xNode = 0;
        }
        if (yNode < 0 || yNode >= 1 << Constants.meshSize) {
            yNode = 0;
        }
        return Tiles.decodeHeightAsFloat(mesh.getTile(xNode, yNode));
    }
    
    public static VolaTile getTileOrNull(@Nonnull final TilePos tilePos, final boolean surfaced) {
        return getTileOrNull(tilePos.x, tilePos.y, surfaced);
    }
    
    public static VolaTile getTileOrNull(final int tilex, final int tiley, final boolean surfaced) {
        VolaTile tile = null;
        try {
            final Zone zone = getZone(tilex, tiley, surfaced);
            tile = zone.getTileOrNull(tilex, tiley);
        }
        catch (NoSuchZoneException nsz) {
            if (!Zones.haslogged) {
                if (Zones.logger.isLoggable(Level.FINEST)) {
                    Zones.logger.log(Level.FINEST, "HERE _ No such zone: " + tilex + ", " + tiley + " surf=" + surfaced, nsz);
                }
                Zones.haslogged = true;
            }
        }
        return tile;
    }
    
    public static VolaTile getOrCreateTile(@Nonnull final TilePos tilePos, final boolean surfaced) {
        return getOrCreateTile(tilePos.x, tilePos.y, surfaced);
    }
    
    public static VolaTile getOrCreateTile(final int tilex, final int tiley, final boolean surfaced) {
        VolaTile tile = null;
        try {
            final Zone zone = getZone(tilex, tiley, surfaced);
            tile = zone.getOrCreateTile(tilex, tiley);
        }
        catch (NoSuchZoneException ex) {}
        return tile;
    }
    
    public static VolaTile[] getTilesSurrounding(final int tilex, final int tiley, final boolean surfaced, final int distance) {
        final Set<VolaTile> tiles = new HashSet<VolaTile>();
        for (int x = -distance; x <= distance; ++x) {
            for (int y = -distance; y <= distance; ++y) {
                final VolaTile tile = getTileOrNull(tilex + x, tiley + y, surfaced);
                if (tile != null) {
                    tiles.add(tile);
                }
            }
        }
        return tiles.toArray(new VolaTile[tiles.size()]);
    }
    
    public static final boolean isNoBuildZone(final int tilex, final int tiley) {
        return FocusZone.isNoBuildZoneAt(tilex, tiley);
    }
    
    public static final boolean isPremSpawnZoneAt(final int tilex, final int tiley) {
        if (Servers.localServer.isChaosServer()) {
            final Village v = Villages.getVillage(tilex, tiley, true);
            return v == null || !v.isPermanent;
        }
        return FocusZone.isPremSpawnOnlyZoneAt(tilex, tiley);
    }
    
    public static final boolean isInPvPZone(final int tilex, final int tiley) {
        return !FocusZone.isNonPvPZoneAt(tilex, tiley) && (FocusZone.isPvPZoneAt(tilex, tiley) || isWithinDuelRing(tilex, tiley, true) != null);
    }
    
    public static final boolean isOnPvPServer(@Nonnull final TilePos tilePos) {
        return isOnPvPServer(tilePos.x, tilePos.y);
    }
    
    public static final boolean isOnPvPServer(final int tilex, final int tiley) {
        if (Servers.localServer.PVPSERVER) {
            return !FocusZone.isNonPvPZoneAt(tilex, tiley);
        }
        return FocusZone.isPvPZoneAt(tilex, tiley) || isWithinDuelRing(tilex, tiley, true) != null;
    }
    
    public static final boolean willEnterStructure(final Creature creature, final float startx, final float starty, final float endx, final float endy, final boolean surfaced) {
        final int starttilex = (int)startx >> 2;
        final int starttiley = (int)starty >> 2;
        final int endtilex = (int)endx >> 2;
        final int endtiley = (int)endy >> 2;
        int max = 100;
        if (creature != null) {
            max = creature.getMaxHuntDistance() + 5;
        }
        if (Math.abs(endtilex - starttilex) > max || Math.abs(endtiley - starttiley) > max) {
            if (creature != null) {
                Zones.logger.log(Level.WARNING, creature.getName() + " checking more than his maxdist of " + creature.getMaxHuntDistance(), new Exception());
            }
            else {
                Zones.logger.log(Level.WARNING, "Too far: " + starttilex + "," + starttiley + "->" + endtilex + ", " + endtiley, new Exception());
            }
            return true;
        }
        if (starttilex == endtilex && starttiley == endtiley) {
            return false;
        }
        final double rot = getRotation(startx, starty, endx, endy);
        final double xPosMod = Math.sin(rot * 0.01745329238474369) * 2.0;
        final double yPosMod = -Math.cos(rot * 0.01745329238474369) * 2.0;
        double currX = startx;
        double currY = starty;
        int currTileX = starttilex;
        int currTileY = starttiley;
        int lastTileX = starttilex;
        int lastTileY = starttiley;
        final boolean found = false;
        while (Math.abs(endtilex - currTileX) > 1 || Math.abs(endtiley - currTileY) > 1) {
            currX += xPosMod;
            currY += yPosMod;
            currTileX = (int)currX >> 2;
            currTileY = (int)currY >> 2;
            if (Math.abs(currTileX - starttilex) > max || Math.abs(currTileY - starttiley) > max) {
                if (creature != null) {
                    Zones.logger.log(Level.WARNING, creature.getName() + " missed target " + creature.getMaxHuntDistance(), new Exception());
                }
                else {
                    Zones.logger.log(Level.WARNING, "missed target : " + starttilex + "," + starttiley + "->" + endtilex + ", " + endtiley, new Exception());
                }
                return true;
            }
            final int diffX = currTileX - lastTileX;
            final int diffY = currTileY - lastTileY;
            if (diffX == 0 && diffY == 0) {
                continue;
            }
            final VolaTile startTile = getTileOrNull(lastTileX, lastTileY, surfaced);
            final VolaTile endTile = getTileOrNull(currTileX, currTileY, surfaced);
            if (startTile != null) {
                if (endTile != null && startTile.getStructure() == null && endTile.getStructure() != null && endTile.getStructure().isFinished()) {
                    return true;
                }
            }
            else if (endTile != null && endTile.getStructure() != null && endTile.getStructure().isFinished()) {
                return true;
            }
            lastTileY = currTileY;
            lastTileX = currTileX;
        }
        final VolaTile startTile2 = getTileOrNull(currTileX, currTileY, surfaced);
        final VolaTile endTile2 = getTileOrNull(endtilex, endtiley, surfaced);
        if (startTile2 != null) {
            if (endTile2 != null && startTile2.getStructure() == null && endTile2.getStructure() != null && endTile2.getStructure().isFinished()) {
                return true;
            }
        }
        else if (endTile2 != null && endTile2.getStructure() != null && endTile2.getStructure().isFinished()) {
            return true;
        }
        return false;
    }
    
    @Nullable
    public static final Floor[] getFloorsAtTile(final int tilex, final int tiley, final int startHeightOffset, final int endHeightOffset, final int layer) {
        return getFloorsAtTile(tilex, tiley, startHeightOffset, endHeightOffset, layer != -1);
    }
    
    @Nullable
    public static final Floor[] getFloorsAtTile(final int tilex, final int tiley, final int startHeightOffset, final int endHeightOffset, final boolean onSurface) {
        final VolaTile tile = getTileOrNull(tilex, tiley, onSurface);
        if (tile != null) {
            final Floor[] floors = tile.getFloors(startHeightOffset, endHeightOffset);
            if (floors != null && floors.length > 0) {
                return floors;
            }
        }
        return null;
    }
    
    public static final Floor getFloor(final int tilex, final int tiley, final boolean onSurface, final int floorLevel) {
        final VolaTile tile = getTileOrNull(tilex, tiley, onSurface);
        if (tile != null) {
            return tile.getFloor(floorLevel);
        }
        return null;
    }
    
    public static final BridgePart[] getBridgePartsAtTile(final int tilex, final int tiley, final boolean onSurface) {
        final VolaTile tile = getTileOrNull(tilex, tiley, onSurface);
        if (tile != null) {
            final BridgePart[] bridgeParts = tile.getBridgeParts();
            if (bridgeParts != null && bridgeParts.length > 0) {
                return bridgeParts;
            }
        }
        return null;
    }
    
    @Nullable
    public static BridgePart getBridgePartFor(final int tilex, final int tiley, final boolean onSurface) {
        final BridgePart[] bps = getBridgePartsAtTile(tilex, tiley, onSurface);
        if (bps != null && bps.length > 0) {
            return bps[0];
        }
        return null;
    }
    
    public static Structure[] getStructuresInArea(final int startX, final int startY, final int endX, final int endY, final boolean surfaced) {
        final Set<Structure> set = new HashSet<Structure>();
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                final VolaTile tile = getTileOrNull(x, y, surfaced);
                if (tile != null) {
                    final Structure structure = tile.getStructure();
                    if (structure != null && !set.contains(structure) && structure.isTypeHouse()) {
                        set.add(structure);
                    }
                }
            }
        }
        Structure[] toReturn;
        if (set.size() > 0) {
            toReturn = set.toArray(new Structure[set.size()]);
        }
        else {
            toReturn = new Structure[0];
        }
        return toReturn;
    }
    
    private static final double getRotation(final float startx, final float starty, final float endx, final float endy) {
        final double newrot = Math.atan2(endy - starty, endx - startx);
        return newrot * 57.29577951308232 + 90.0;
    }
    
    public static final LongPosition getEndTile(final float startPosX, final float startPosY, final float rot, final int tiledist) {
        final float xPosMod = (float)Math.sin(rot * 0.017453292f) * (4 * tiledist);
        final float yPosMod = -(float)Math.cos(rot * 0.017453292f) * (4 * tiledist);
        final float newPosX = startPosX + xPosMod;
        final float newPosY = startPosY + yPosMod;
        return new LongPosition(0L, (int)newPosX >> 2, (int)newPosY >> 2);
    }
    
    static final Den getNorthTop(final int templateId) {
        for (int tries = 0; tries < 1000; ++tries) {
            final int startX = 0;
            final int startY = 0;
            final int endX = Zones.worldTileSizeX - 10;
            final int endY = Math.min(Zones.worldTileSizeY / 10, 500);
            final Zone[] zones = getZonesCoveredBy(0, 0, endX, endY, true);
            Zone highest = null;
            float top = 0.0f;
            for (int x = 0; x < zones.length; ++x) {
                if (zones[x].highest > top && (top == 0.0f || Server.rand.nextInt(2) == 0)) {
                    top = zones[x].highest;
                    highest = zones[x];
                }
            }
            if (highest != null) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x2 = highest.startX; x2 <= highest.endX; ++x2) {
                    for (int y = highest.startY; y < highest.endY; ++y) {
                        final int tile = mesh.getTile(x2, y);
                        final short hi = Tiles.decodeHeight(tile);
                        if (hi > h) {
                            h = hi;
                            tx = x2;
                            ty = y;
                        }
                    }
                }
                if (h > 0) {
                    final VolaTile t = getTileOrNull(safeTileX(tx), safeTileY(ty), true);
                    if (t == null && Villages.getVillageWithPerimeterAt(safeTileX(tx), safeTileY(ty), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx - 20), safeTileY(ty - 20), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx - 20), safeTileY(ty), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx + 20), safeTileY(ty), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx), safeTileY(ty), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx + 20), safeTileY(ty - 20), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx + 20), safeTileY(ty + 20), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx), safeTileY(ty + 20), true) == null && Villages.getVillageWithPerimeterAt(safeTileX(tx - 20), safeTileY(ty + 20), true) == null) {
                        Zones.logger.log(Level.INFO, "Created den for " + templateId + " after " + tries + " tries.");
                        return new Den(templateId, tx, ty, true);
                    }
                }
            }
        }
        return null;
    }
    
    static final Den getWestTop(final int templateId) {
        for (int a = 0; a < 100; ++a) {
            final int startX = 0;
            final int startY = Math.min(Zones.worldTileSizeY / 10, 500);
            final int endX = (Zones.worldTileSizeX - 10) / 2;
            final int endY = Zones.worldTileSizeY - Math.min(Zones.worldTileSizeY / 10, 500);
            final Zone[] zones = getZonesCoveredBy(0, startY, endX, endY, true);
            Zone highest = null;
            float top = 0.0f;
            for (int x = 0; x < zones.length; ++x) {
                if (zones[x].highest > top && (top == 0.0f || Server.rand.nextInt(2) == 0)) {
                    top = zones[x].highest;
                    highest = zones[x];
                }
            }
            if (highest != null) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x2 = highest.startX; x2 <= highest.endX; ++x2) {
                    for (int y = highest.startY; y < highest.endY; ++y) {
                        final Village v = Villages.getVillageWithPerimeterAt(x2, y, true);
                        if (v == null) {
                            final int tile = mesh.getTile(x2, y);
                            final short hi = Tiles.decodeHeight(tile);
                            if (hi > h) {
                                h = hi;
                                tx = x2;
                                ty = y;
                            }
                        }
                    }
                }
                if (h > 0) {
                    return new Den(templateId, tx, ty, true);
                }
            }
        }
        return null;
    }
    
    static final Den getSouthTop(final int templateId) {
        for (int a = 0; a < 100; ++a) {
            final int startX = 0;
            final int startY = Zones.worldTileSizeY - Math.min(Zones.worldTileSizeY / 10, 500);
            final int endX = Zones.worldTileSizeX - 10;
            final int endY = Zones.worldTileSizeY - 10;
            final Zone[] zones = getZonesCoveredBy(0, startY, endX, endY, true);
            Zone highest = null;
            float top = 0.0f;
            for (int x = 0; x < zones.length; ++x) {
                if (zones[x].highest > top && (top == 0.0f || Server.rand.nextInt(2) == 0)) {
                    top = zones[x].highest;
                    highest = zones[x];
                }
            }
            if (highest != null) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x2 = highest.startX; x2 <= highest.endX; ++x2) {
                    for (int y = highest.startY; y < highest.endY; ++y) {
                        final Village v = Villages.getVillageWithPerimeterAt(x2, y, true);
                        if (v == null) {
                            final int tile = mesh.getTile(x2, y);
                            final short hi = Tiles.decodeHeight(tile);
                            if (hi > h) {
                                h = hi;
                                tx = x2;
                                ty = y;
                            }
                        }
                    }
                }
                if (h > 0) {
                    return new Den(templateId, tx, ty, true);
                }
            }
        }
        return null;
    }
    
    public static final TilePos getRandomCenterLand() {
        return TilePos.fromXY(Zones.worldTileSizeX / 4 + Server.rand.nextInt(Zones.worldTileSizeX / 2), Zones.worldTileSizeY / 4 + Server.rand.nextInt(Zones.worldTileSizeY / 2));
    }
    
    public static final Den getRandomTop() {
        for (int a = 0; a < 100; ++a) {
            final int minHeight = 200;
            final int startx = Server.rand.nextInt(Zones.worldTileSizeX >> 6);
            final int starty = Server.rand.nextInt(Zones.worldTileSizeY >> 6);
            final int endx = Math.min(startx + 2, Zones.worldTileSizeX >> 6);
            final int endy = Math.min(starty + 2, Zones.worldTileSizeY >> 6);
            Zone highest = null;
            float top = 0.0f;
            for (int x = startx; x < endx; ++x) {
                for (int y = starty; y < endy; ++y) {
                    if (Zones.surfaceZones[x][y].highest > top) {
                        Zones.logger.info("Zone " + x + "," + y + " now highest for top.");
                        top = Zones.surfaceZones[x][y].highest;
                        if (top > 200.0f) {
                            highest = Zones.surfaceZones[x][y];
                        }
                    }
                }
            }
            if (highest != null) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x2 = highest.startX; x2 <= highest.endX; ++x2) {
                    for (int y2 = highest.startY; y2 < highest.endY; ++y2) {
                        final Village v = Villages.getVillageWithPerimeterAt(x2, y2, true);
                        if (v == null) {
                            final int tile = mesh.getTile(x2, y2);
                            final short hi = Tiles.decodeHeight(tile);
                            if (hi > h) {
                                h = hi;
                                tx = x2;
                                ty = y2;
                            }
                        }
                    }
                }
                if (h > 200) {
                    return new Den(0, tx, ty, true);
                }
            }
        }
        return null;
    }
    
    static final Den getEastTop(final int templateId) {
        for (int a = 0; a < 100; ++a) {
            final int startX = (Zones.worldTileSizeX - 5) / 2;
            final int startY = Math.min(Zones.worldTileSizeY / 10, 500);
            final int endX = Zones.worldTileSizeX - 10;
            final int endY = Zones.worldTileSizeY - Math.min(Zones.worldTileSizeY / 10, 500);
            final Zone[] zones = getZonesCoveredBy(startX, startY, endX, endY, true);
            Zone highest = null;
            float top = 0.0f;
            for (int x = 0; x < zones.length; ++x) {
                if (zones[x].highest > top && (top == 0.0f || Server.rand.nextInt(2) == 0)) {
                    top = zones[x].highest;
                    highest = zones[x];
                }
            }
            if (highest != null) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x2 = highest.startX; x2 <= highest.endX; ++x2) {
                    for (int y = highest.startY; y < highest.endY; ++y) {
                        final Village v = Villages.getVillageWithPerimeterAt(x2, y, true);
                        if (v == null) {
                            final int tile = mesh.getTile(x2, y);
                            final short hi = Tiles.decodeHeight(tile);
                            if (hi > h) {
                                h = hi;
                                tx = x2;
                                ty = y;
                            }
                        }
                    }
                }
                if (h > 0) {
                    return new Den(templateId, tx, ty, true);
                }
            }
        }
        return null;
    }
    
    static final Den getRandomForest(final int templateId) {
        for (int a = 0; a < 100; ++a) {
            final int num1 = Server.rand.nextInt(Zones.surfaceZones.length);
            final int num2 = Server.rand.nextInt(Zones.surfaceZones.length);
            final Zone zone = Zones.surfaceZones[num1][num2];
            if (zone.isForest) {
                int tx = 0;
                int ty = 0;
                short h = 0;
                final MeshIO mesh = Server.surfaceMesh;
                for (int x = zone.startX; x <= zone.endX; ++x) {
                    for (int y = zone.startY; y < zone.endY; ++y) {
                        final Village v = Villages.getVillageWithPerimeterAt(x, y, true);
                        if (v == null) {
                            final int tile = mesh.getTile(x, y);
                            final short hi = Tiles.decodeHeight(tile);
                            if (hi > h) {
                                h = hi;
                                tx = x;
                                ty = y;
                            }
                        }
                    }
                }
                if (h > 0) {
                    return new Den(templateId, tx, ty, true);
                }
            }
        }
        return null;
    }
    
    public static final void releaseAllCorpsesFor(final Creature creature) {
        final int worldSize = 1 << Constants.meshSize;
        for (int x = 0; x < worldSize >> 6; ++x) {
            for (int y = 0; y < worldSize >> 6; ++y) {
                final Item[] its = Zones.surfaceZones[x][y].getAllItems();
                for (int itx = 0; itx < its.length; ++itx) {
                    if (its[itx].getTemplateId() == 272 && its[itx].getName().equals("corpse of " + creature.getName())) {
                        its[itx].setProtected(false);
                    }
                }
            }
        }
        for (int x = 0; x < worldSize >> 6; ++x) {
            for (int y = 0; y < worldSize >> 6; ++y) {
                final Item[] its = Zones.caveZones[x][y].getAllItems();
                for (int itx = 0; itx < its.length; ++itx) {
                    if (its[itx].getTemplateId() == 272 && its[itx].getName().equals("corpse of " + creature.getName())) {
                        its[itx].setProtected(false);
                    }
                }
            }
        }
    }
    
    public static final byte getMiningState(final int x, final int y) {
        final Map<Integer, Byte> tab = Zones.miningTiles.get(x);
        if (tab == null) {
            return 0;
        }
        final Byte b = tab.get(y);
        if (b == null) {
            return 0;
        }
        return b;
    }
    
    public static final void setMiningState(final int x, final int y, final byte state, final boolean load) {
        Map<Integer, Byte> tab = Zones.miningTiles.get(x);
        boolean save = false;
        if (tab == null) {
            if (state > 0 || state == -1) {
                tab = new Hashtable<Integer, Byte>();
                tab.put(y, state);
                Zones.miningTiles.put(x, tab);
                save = true;
            }
        }
        else if (state > 0 || state == -1 || tab.get(y) != null) {
            tab.put(y, state);
            save = true;
        }
        if (!load && save) {
            saveMiningTile(x, y, state);
        }
    }
    
    private static final void loadAllMiningTiles() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final long start = System.nanoTime();
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MINING");
            rs = ps.executeQuery();
            int a = 0;
            while (rs.next()) {
                setMiningState(rs.getInt("TILEX"), rs.getInt("TILEY"), rs.getByte("STATE"), true);
                ++a;
            }
            Zones.logger.log(Level.INFO, "Loaded " + a + " mining tiles, that took " + (System.nanoTime() - start) / 1000000.0f + " ms");
        }
        catch (SQLException sqx) {
            Zones.logger.log(Level.WARNING, "Failed to load miningtiles", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void saveMiningTile(final int tilex, final int tiley, final byte state) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (!exists(dbcon, tilex, tiley)) {
                createMiningTile(dbcon, tilex, tiley, state);
                return;
            }
            ps = dbcon.prepareStatement("UPDATE MINING SET STATE=? WHERE TILEX=? AND TILEY=?");
            ps.setByte(1, state);
            ps.setInt(2, tilex);
            ps.setInt(3, tiley);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Zones.logger.log(Level.WARNING, "Failed to save miningtile " + tilex + ", " + tiley + ", " + state + ":" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void createMiningTile(final Connection dbcon, final int tilex, final int tiley, final byte state) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement("INSERT INTO MINING (STATE,TILEX,TILEY) VALUES(?,?,?)");
            ps.setByte(1, state);
            ps.setInt(2, tilex);
            ps.setInt(3, tiley);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Zones.logger.log(Level.WARNING, "Failed to save miningtile " + tilex + ", " + tiley + ", " + state + ":" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    public static final void deleteMiningTile(final int tilex, final int tiley) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MINING WHERE TILEX=? AND TILEY=?");
            ps.setInt(1, tilex);
            ps.setInt(2, tiley);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Zones.logger.log(Level.WARNING, "Failed to delete miningtile " + tilex + ", " + tiley + ":" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final boolean exists(final Connection dbcon, final int tilex, final int tiley) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT STATE FROM MINING WHERE TILEX=? AND TILEY=?");
            ps.setInt(1, tilex);
            ps.setInt(2, tiley);
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    private static final void spawnSanta(final byte kingdom) {
        Village v = Villages.getFirstPermanentVillageForKingdom(kingdom);
        if (v == null) {
            v = Villages.getCapital(kingdom);
        }
        if (v == null) {
            v = Villages.getFirstVillageForKingdom(kingdom);
        }
        int tilex = v.startx + (v.endx - v.startx) / 2;
        int tiley = v.starty + (v.endy - v.starty) / 2;
        if (v != null) {
            try {
                tilex = v.getToken().getTileX();
                tiley = v.getToken().getTileY();
            }
            catch (NoSuchItemException ex2) {}
        }
        int temp = 46;
        String name = "Santa Claus";
        final Kingdom k = Kingdoms.getKingdom(kingdom);
        if (k != null && k.getTemplate() == 3) {
            temp = 47;
            name = "Evil Santa";
        }
        try {
            Zones.evilsanta = Creature.doNew(temp, tilex * 4 + 1, tiley * 4 + 1, 154.0f, 0, name, (byte)0, kingdom);
        }
        catch (Exception ex) {
            Zones.logger.log(Level.WARNING, "Failed to create santa! " + ex.getMessage(), ex);
        }
        try {
            ItemFactory.createItem(442, 90.0f, tilex * 4, (tiley + 1) * 4, 154.0f, true, (byte)0, -10L, null);
            sendChristmasEffect(Zones.evilsanta, null);
        }
        catch (Exception ex) {
            Zones.logger.log(Level.WARNING, "Failed to create evil julbord " + ex.getMessage(), ex);
        }
    }
    
    public static final void loadChristmas() {
        if (!isHasLoadedChristmas()) {
            Server.getInstance().broadCastSafe("Merry Christmas!");
            if (!Servers.localServer.HOMESERVER) {
                int tilex = Servers.localServer.SPAWNPOINTLIBX - 2;
                int tiley = Servers.localServer.SPAWNPOINTLIBY - 2;
                Village v = Villages.getFirstPermanentVillageForKingdom((byte)3);
                if (v == null) {
                    v = Villages.getFirstVillageForKingdom((byte)3);
                }
                Zones.evilsanta = addSanta(v, tilex, tiley, 154, 47, "Evil Santa", (byte)3);
                tilex = Servers.localServer.SPAWNPOINTJENNX - 2;
                tiley = Servers.localServer.SPAWNPOINTJENNY - 2;
                v = Villages.getFirstPermanentVillageForKingdom((byte)1);
                if (v == null) {
                    v = Villages.getFirstVillageForKingdom((byte)1);
                }
                Zones.santa = addSanta(v, tilex, tiley, 154, 46, "Santa Claus", (byte)1);
                tilex = Servers.localServer.SPAWNPOINTMOLX - 2;
                tiley = Servers.localServer.SPAWNPOINTMOLY - 2;
                v = Villages.getFirstPermanentVillageForKingdom((byte)2);
                if (v == null) {
                    v = Villages.getFirstVillageForKingdom((byte)2);
                }
                Zones.santaMolRehan = addSanta(v, tilex, tiley, 94, 46, "Twin Santa", (byte)2);
                v = Villages.getFirstPermanentVillageForKingdom((byte)4);
                if (v == null) {
                    v = Villages.getFirstVillageForKingdom((byte)4);
                }
                if (v != null) {
                    final Creature santa = addSanta(v, tilex, tiley, 94, 46, "Santa Claus", (byte)4);
                    Zones.santas.put(santa.getWurmId(), santa);
                }
            }
            else {
                int tilex = Servers.localServer.SPAWNPOINTJENNX - 2;
                int tiley = Servers.localServer.SPAWNPOINTJENNY - 2;
                Village v = Villages.getFirstPermanentVillageForKingdom(Servers.localServer.KINGDOM);
                if (v == null) {
                    v = Villages.getFirstVillageForKingdom(Servers.localServer.KINGDOM);
                }
                if (v != null) {
                    try {
                        tilex = v.getToken().getTileX();
                        tiley = v.getToken().getTileY();
                    }
                    catch (NoSuchItemException ex2) {}
                }
                try {
                    Zones.santa = Creature.doNew(46, tilex * 4 + 1, tiley * 4 + 1, 90.0f, 0, "Santa Claus", (byte)0, Servers.localServer.KINGDOM);
                }
                catch (Exception ex) {
                    Zones.logger.log(Level.WARNING, "Failed to create santa! " + ex.getMessage(), ex);
                }
                try {
                    final Item julbord = ItemFactory.createItem(442, 90.0f, (tilex + 1) * 4, tiley * 4, 96.0f, true, (byte)0, -10L, null);
                    sendChristmasEffect(Zones.santa, null);
                }
                catch (Exception ex) {
                    Zones.logger.log(Level.WARNING, "Failed to create julbord " + ex.getMessage(), ex);
                }
            }
            setHasLoadedChristmas(true);
        }
    }
    
    @Nullable
    private static Creature addSanta(@Nullable final Village v, int tilex, int tiley, final int rot, final int santaTemplate, final String santaName, final byte kingdom) {
        Creature santa = null;
        if (v != null) {
            try {
                tilex = v.getToken().getTileX();
                tiley = v.getToken().getTileY();
            }
            catch (NoSuchItemException ex2) {}
        }
        try {
            santa = Creature.doNew(santaTemplate, tilex * 4 + 2, tiley * 4 + 0.75f, 90.0f, 0, santaName, (byte)0, kingdom);
            sendChristmasEffect(santa, null);
            try {
                ItemFactory.createItem(442, 90.0f, tilex * 4 + 1, tiley * 4 + 2, 90.0f, true, (byte)0, -10L, null);
            }
            catch (Exception ex) {
                Zones.logger.log(Level.WARNING, "Failed to create julbord " + ex.getMessage(), ex);
            }
        }
        catch (Exception ex) {
            Zones.logger.log(Level.WARNING, "Failed to create " + santaName + "! " + ex.getMessage(), ex);
        }
        return santa;
    }
    
    public static void sendChristmasEffect(final Creature creature, final Item item) {
        final Player[] players = Players.getInstance().getPlayers();
        for (int x = 0; x < players.length; ++x) {
            if (item != null) {
                players[x].getCommunicator().sendAddEffect(creature.getWurmId(), (short)4, item.getPosX(), item.getPosY(), item.getPosZ(), (byte)0);
            }
            else {
                players[x].getCommunicator().sendAddEffect(creature.getWurmId(), (short)4, creature.getPosX(), creature.getPosY(), creature.getPositionZ(), (byte)0);
            }
        }
    }
    
    public static void removeChristmasEffect(final Creature idcreature) {
        if (idcreature != null) {
            final Player[] players = Players.getInstance().getPlayers();
            for (int x = 0; x < players.length; ++x) {
                players[x].getCommunicator().sendRemoveEffect(idcreature.getWurmId());
            }
        }
    }
    
    public static void deleteChristmas() {
        if (Zones.hasLoadedChristmas) {
            Zones.logger.log(Level.INFO, "Starting christmas deletion.");
            setHasLoadedChristmas(false);
            if (Zones.evilsanta != null) {
                removeChristmasEffect(Zones.evilsanta);
                MethodsCreatures.destroyCreature(Zones.evilsanta);
                Zones.evilsanta = null;
            }
            if (Zones.santa != null) {
                removeChristmasEffect(Zones.santa);
                MethodsCreatures.destroyCreature(Zones.santa);
                Zones.santa = null;
            }
            if (Zones.santaMolRehan != null) {
                removeChristmasEffect(Zones.santaMolRehan);
                MethodsCreatures.destroyCreature(Zones.santaMolRehan);
                Zones.santaMolRehan = null;
            }
            if (!Zones.santas.isEmpty()) {
                for (final Creature santa : Zones.santas.values()) {
                    removeChristmasEffect(santa);
                    MethodsCreatures.destroyCreature(santa);
                }
                Zones.santas.clear();
            }
            Items.deleteChristmasItems();
            Zones.logger.log(Level.INFO, "Christmas deletion done.");
            Server.getInstance().broadCastSafe("Christmas is over!");
        }
    }
    
    public static TilePos safeTile(@Nonnull final TilePos tilePos) {
        tilePos.set(safeTileX(tilePos.x), safeTileY(tilePos.y));
        return tilePos;
    }
    
    public static final int safeTileX(final int tilex) {
        return Math.max(0, Math.min(tilex, Zones.worldTileSizeX - 1));
    }
    
    public static final int safeTileY(final int tiley) {
        return Math.max(0, Math.min(tiley, Zones.worldTileSizeY - 1));
    }
    
    public static int[] getClosestSpring(int tilex, int tiley, final int dist) {
        tilex = safeTileX(tilex);
        tiley = safeTileY(tiley);
        int closestX = -1;
        int closestY = -1;
        for (int x = safeTileX(tilex - dist); x < safeTileX(tilex + 1 + dist); ++x) {
            for (int y = safeTileY(tiley - dist); y < safeTileY(tiley + 1 + dist); ++y) {
                if (Zone.hasSpring(x, y)) {
                    if (closestX < 0 || closestY < 0) {
                        closestX = Math.abs(tilex - x);
                        closestY = Math.abs(tiley - y);
                    }
                    else {
                        final int dx1 = tilex - x;
                        final int dy1 = tiley - y;
                        final int dx2 = closestX;
                        final int dy2 = closestY;
                        if (Math.sqrt(dx1 * dx1 + dy1 * dy1) < Math.sqrt(dx2 * dx2 + dy2 * dy2)) {
                            closestX = Math.abs(dx1);
                            closestY = Math.abs(dy1);
                        }
                    }
                }
            }
        }
        return new int[] { closestX, closestY };
    }
    
    public static void setHasLoadedChristmas(final boolean aHasLoadedChristmas) {
        Zones.hasLoadedChristmas = aHasLoadedChristmas;
    }
    
    public static boolean isHasLoadedChristmas() {
        return Zones.hasLoadedChristmas;
    }
    
    public static boolean sendNewYear() {
        for (int a = 0; a < Math.min(500, Zones.worldTileSizeX / 4); ++a) {
            final short effectType = (short)(5 + Server.rand.nextInt(5));
            final float x = Server.rand.nextInt(Zones.worldTileSizeX) * 4;
            final float y = Server.rand.nextInt(Zones.worldTileSizeY) * 4;
            try {
                final float h = calculateHeight(x, y, true);
                Players.getInstance().sendEffect(effectType, x, y, Math.max(0.0f, h) + 10.0f + Server.rand.nextInt(30), true, 500.0f);
            }
            catch (NoSuchZoneException ex) {}
        }
        return true;
    }
    
    public static void removeNewYear() {
        if (Zones.hasStartedYet) {
            final Player[] players = Players.getInstance().getPlayers();
            final LongPosition l = Zones.posmap.removeFirst();
            for (int p = 0; p < players.length; ++p) {
                players[p].getCommunicator().sendRemoveEffect(l.getId());
            }
            if (Zones.posmap.isEmpty()) {
                Zones.hasStartedYet = false;
            }
        }
    }
    
    public static void sendNewYearsEffectsToPlayer(final Player p) {
        if (Zones.posmap.isEmpty()) {
            return;
        }
        final Iterator<LongPosition> it = Zones.posmap.iterator();
        while (it.hasNext()) {
            try {
                final LongPosition l = it.next();
                p.getCommunicator().sendAddEffect(l.getId(), l.getEffectType(), l.getTilex() << 2, l.getTiley() << 2, calculateHeight(l.getTilex() << 2, l.getTiley() << 2, true), (byte)0);
            }
            catch (NoSuchZoneException ex) {}
        }
    }
    
    public static final boolean interruptedRange(final Creature performer, final Creature defender) {
        if (performer == null || defender == null) {
            return true;
        }
        if (performer.equals(defender)) {
            return false;
        }
        if (performer.isOnSurface() != defender.isOnSurface()) {
            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view of " + defender.getName() + ".");
            return true;
        }
        resetCoverHolder();
        if (performer.isOnSurface()) {
            final BlockingResult blockers = Blocking.getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), defender.getPosX(), defender.getPosY(), performer.getPositionZ(), defender.getPositionZ(), performer.isOnSurface(), defender.isOnSurface(), true, 4, -1L, performer.getBridgeId(), performer.getBridgeId(), false);
            if (blockers != null && (blockers.getTotalCover() >= 100.0f || ((!performer.isOnPvPServer() || !defender.isOnPvPServer()) && blockers.getFirstBlocker() != null)) && (performer.getCitizenVillage() == null || !performer.getCitizenVillage().isEnemy(defender))) {
                performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view of " + defender.getName() + ".");
                return true;
            }
        }
        if (!defender.isWithinDistanceTo(performer, 4.0f) && !VirtualZone.isCreatureTurnedTowardsTarget(defender, performer, 60.0f, false)) {
            performer.getCommunicator().sendCombatNormalMessage("You must turn towards " + defender.getName() + " in order to see it.");
            return true;
        }
        final PathFinder pf = new PathFinder(true);
        try {
            final Path path = pf.rayCast(performer.getCurrentTile().tilex, performer.getCurrentTile().tiley, defender.getCurrentTile().tilex, defender.getCurrentTile().tiley, performer.isOnSurface(), ((int)Creature.getRange(performer, defender.getPosX(), defender.getPosY()) >> 2) + 5);
            final float initialHeight = Math.max(0.0f, performer.getPositionZ() + performer.getAltOffZ() + 1.4f);
            final float targetHeight = Math.max(0.0f, defender.getPositionZ() + defender.getAltOffZ() + 1.4f);
            double distx = Math.pow(performer.getCurrentTile().tilex - defender.getCurrentTile().tilex, 2.0);
            double disty = Math.pow(performer.getCurrentTile().tiley - defender.getCurrentTile().tiley, 2.0);
            final double dist = Math.sqrt(distx + disty);
            final double dx = (targetHeight - initialHeight) / dist;
            while (!path.isEmpty()) {
                final PathTile p = path.getFirst();
                distx = Math.pow(p.getTileX() - defender.getCurrentTile().tilex, 2.0);
                disty = Math.pow(p.getTileY() - defender.getCurrentTile().tiley, 2.0);
                final double currdist = Math.sqrt(distx + disty);
                final float currHeight = Math.max(0.0f, getLowestCorner(p.getTileX(), p.getTileY(), performer.getLayer()));
                final double distmod = currdist * dx;
                if (dx < 0.0) {
                    if (currHeight > targetHeight - distmod) {
                        performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view.");
                        return true;
                    }
                }
                else if (currHeight > targetHeight - distmod) {
                    performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view.");
                    return true;
                }
                path.removeFirst();
            }
        }
        catch (NoPathException np) {
            performer.getCommunicator().sendCombatNormalMessage("You fail to get a clear view.");
            return true;
        }
        return false;
    }
    
    public static final Set<PathTile> explode(final int tilex, final int tiley, final int floorLevel, final boolean followStructure, final int diameter) {
        Set<PathTile> toRet = new HashSet<PathTile>();
        final VolaTile current = getTileOrNull(tilex, tiley, floorLevel >= 0);
        if (current != null && current.getStructure() != null) {
            final Structure structure = current.getStructure();
            if (followStructure) {
                for (int x = -diameter; x <= diameter; ++x) {
                    for (int y = -diameter; y <= diameter; ++y) {
                        toRet = checkStructureTile(structure, tilex, tiley, x, y, floorLevel, toRet);
                    }
                }
            }
        }
        else {
            for (int x2 = -diameter; x2 <= diameter; ++x2) {
                for (int y2 = -diameter; y2 <= diameter; ++y2) {
                    toRet = checkStructureTile(null, tilex, tiley, x2, y2, floorLevel, toRet);
                }
            }
        }
        return toRet;
    }
    
    private static final Set<PathTile> checkStructureTile(final Structure structure, final int tilex, final int tiley, final int xmod, final int ymod, final int floorLevel, final Set<PathTile> toRet) {
        final int tx = tilex + xmod;
        final int ty = tiley + ymod;
        if (containsStructure(structure, tx, ty, floorLevel)) {
            toRet.add(getPathTile(tx, ty, floorLevel >= 0, floorLevel));
        }
        return toRet;
    }
    
    private static final boolean containsStructure(final Structure structure, final int tx, final int ty, final int floorLevel) {
        final VolaTile current = getTileOrNull(tx, ty, floorLevel >= 0);
        return (structure == null && (current == null || current.getStructure() == null)) || (current != null && current.getStructure() == structure);
    }
    
    public static final PathTile getPathTile(final int tx, final int ty, final boolean surfaced, final int floorLevel) {
        final boolean surface = floorLevel >= 0;
        final int tileNum2 = getMesh(surface).getTile(tx, ty);
        return new PathTile(tx, ty, tileNum2, surface, (byte)floorLevel);
    }
    
    public static final float getLowestCorner(int tilex, int tiley, final int layer) {
        tilex = safeTileX(tilex);
        tiley = safeTileY(tiley);
        if (layer >= 0) {
            float lowest = Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(tilex, tiley));
            for (int x = 0; x <= 1; ++x) {
                for (int y = 0; y <= 1; ++y) {
                    lowest = Math.min(Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(tilex + y, tiley + y)), lowest);
                }
            }
            return lowest;
        }
        float lowest = Tiles.decodeHeightAsFloat(Server.caveMesh.getTile(tilex, tiley));
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                lowest = Math.min(Tiles.decodeHeightAsFloat(Server.caveMesh.getTile(tilex + y, tiley + y)), lowest);
            }
        }
        return lowest;
    }
    
    public static final int getSpiritsForTile(final int tilex, final int tiley, final boolean surfaced) {
        if (!surfaced) {
            return 3;
        }
        for (int x = safeTileX(tilex - 2); x <= safeTileX(tilex + 2); ++x) {
            for (int y = safeTileY(tiley - 2); y <= safeTileY(tiley + 2); ++y) {
                if (Features.Feature.SURFACEWATER.isEnabled() && Water.getSurfaceWater(x, y) > 0) {
                    return 2;
                }
                if (Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) <= 0) {
                    return 2;
                }
            }
        }
        for (int x = safeTileX(tilex - 2); x <= safeTileX(tilex + 2); ++x) {
            for (int y = safeTileX(tiley - 2); y <= safeTileY(tiley + 2); ++y) {
                if (Tiles.decodeType(Server.surfaceMesh.getTile(x, y)) == Tiles.Tile.TILE_LAVA.id) {
                    return 1;
                }
                final VolaTile t = getTileOrNull(x, y, surfaced);
                if (t != null) {
                    for (final Item i : t.getItems()) {
                        if (i.getTemperature() > 1000) {
                            return 1;
                        }
                    }
                }
            }
        }
        if (Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex, tiley)) > 1000) {
            return 4;
        }
        return 0;
    }
    
    public static MeshIO getMesh(final boolean surface) {
        return surface ? Server.surfaceMesh : Server.caveMesh;
    }
    
    private static final void initializeHiveZones() {
        for (int y = 0; y < Zones.hiveZoneSizeY; ++y) {
            for (int x = 0; x < Zones.hiveZoneSizeX; ++x) {
                Zones.hiveZones.add(null);
            }
        }
    }
    
    public static void addHive(final Item hive, final boolean silent) {
        final int actualZone = Math.max(0, hive.getTileY() / 32) * Zones.hiveZoneSizeX + Math.max(0, hive.getTileX() / 32);
        ConcurrentHashMap<Item, HiveZone> thisZone = Zones.hiveZones.get(actualZone);
        if (thisZone == null) {
            thisZone = new ConcurrentHashMap<Item, HiveZone>();
        }
        thisZone.put(hive, new HiveZone(hive));
        Zones.hiveZones.set(actualZone, thisZone);
    }
    
    public static void removeHive(final Item hive, final boolean silent) {
        final int actualZone = Math.max(0, hive.getTileY() / 32) * Zones.hiveZoneSizeX + Math.max(0, hive.getTileX() / 32);
        final ConcurrentHashMap<Item, HiveZone> thisZone = Zones.hiveZones.get(actualZone);
        if (thisZone == null) {
            Zones.logger.log(Level.WARNING, "HiveZone was NULL when it should not have been: " + actualZone);
            return;
        }
        thisZone.remove(hive);
    }
    
    public static final HiveZone getHiveZoneAt(final int tilex, final int tiley, final boolean surfaced) {
        return getHiveZone(tilex, tiley, surfaced);
    }
    
    public static final boolean isFarFromAnyHive(final int tilex, final int tiley, final boolean surfaced) {
        if (!surfaced) {
            return false;
        }
        ConcurrentHashMap<Item, HiveZone> thisZone = null;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final int actualZone = Math.max(0, Math.min(tiley / 32 + j, Zones.hiveZoneSizeY - 1)) * Zones.hiveZoneSizeX + Math.max(0, Math.min(tilex / 32 + i, Zones.hiveZoneSizeX - 1));
                if (actualZone < Zones.hiveZones.size()) {
                    thisZone = Zones.hiveZones.get(actualZone);
                    if (thisZone != null) {
                        for (final HiveZone hz : thisZone.values()) {
                            if (hz.isCloseToTile(tilex, tiley)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @Nullable
    public static final HiveZone getHiveZone(final int tilex, final int tiley, final boolean surfaced) {
        if (!surfaced) {
            return null;
        }
        HiveZone toReturn = null;
        ConcurrentHashMap<Item, HiveZone> thisZone = null;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final int actualZone = Math.max(0, Math.min(tiley / 32 + j, Zones.hiveZoneSizeY - 1)) * Zones.hiveZoneSizeX + Math.max(0, Math.min(tilex / 32 + i, Zones.hiveZoneSizeX - 1));
                if (actualZone < Zones.hiveZones.size()) {
                    thisZone = Zones.hiveZones.get(actualZone);
                    if (thisZone != null) {
                        for (final HiveZone hz : thisZone.values()) {
                            if (hz.containsTile(tilex, tiley)) {
                                if (hz.getStrengthForTile(tilex, tiley, surfaced) <= 0) {
                                    continue;
                                }
                                if (toReturn == null) {
                                    toReturn = hz;
                                }
                                else if (toReturn.getStrengthForTile(tilex, tiley, surfaced) < hz.getStrengthForTile(tilex, tiley, surfaced)) {
                                    toReturn = hz;
                                }
                                else {
                                    if (toReturn.getStrengthForTile(tilex, tiley, surfaced) != hz.getStrengthForTile(tilex, tiley, surfaced) || hz.getCurrentHive().getCurrentQualityLevel() <= toReturn.getCurrentHive().getCurrentQualityLevel()) {
                                        continue;
                                    }
                                    toReturn = hz;
                                }
                            }
                        }
                    }
                }
            }
        }
        return toReturn;
    }
    
    public static final Item[] getHives(final int hiveType) {
        final Set<Item> hiveZoneSet = new HashSet<Item>();
        for (final ConcurrentHashMap<Item, HiveZone> thisZone : Zones.hiveZones) {
            if (thisZone == null) {
                continue;
            }
            for (final HiveZone hz : thisZone.values()) {
                if (hz.getCurrentHive().getTemplateId() == hiveType) {
                    hiveZoneSet.add(hz.getCurrentHive());
                }
            }
        }
        return hiveZoneSet.toArray(new Item[hiveZoneSet.size()]);
    }
    
    @Nullable
    public static final Item getCurrentHive(final int tilex, final int tiley) {
        final HiveZone z = getHiveZone(tilex, tiley, true);
        if (z != null && z.getCurrentHive() != null) {
            return z.getCurrentHive();
        }
        return null;
    }
    
    @Nullable
    public static final Item getWildHive(final int tilex, final int tiley) {
        ConcurrentHashMap<Item, HiveZone> thisZone = null;
        final int actualZone = Math.max(0, Math.min(tiley / 32, Zones.hiveZoneSizeY - 1)) * Zones.hiveZoneSizeX + Math.max(0, Math.min(tilex / 32, Zones.hiveZoneSizeX - 1));
        if (actualZone >= Zones.hiveZones.size()) {
            return null;
        }
        thisZone = Zones.hiveZones.get(actualZone);
        if (thisZone == null) {
            return null;
        }
        for (final HiveZone hz : thisZone.values()) {
            if (hz.hasHive(tilex, tiley)) {
                final Item hive = hz.getCurrentHive();
                if (hive.getTemplateId() == 1239) {
                    return hive;
                }
                continue;
            }
        }
        return null;
    }
    
    public static final Item[] getActiveDomesticHives(final int tilex, final int tiley) {
        final Set<Item> hiveZoneSet = new HashSet<Item>();
        ConcurrentHashMap<Item, HiveZone> thisZone = null;
        final int actualZone = Math.max(0, Math.min(tiley / 32, Zones.hiveZoneSizeY - 1)) * Zones.hiveZoneSizeX + Math.max(0, Math.min(tilex / 32, Zones.hiveZoneSizeX - 1));
        if (actualZone >= Zones.hiveZones.size()) {
            return null;
        }
        thisZone = Zones.hiveZones.get(actualZone);
        if (thisZone == null) {
            return null;
        }
        for (final HiveZone hz : thisZone.values()) {
            if (hz.hasHive(tilex, tiley)) {
                final Item hive = hz.getCurrentHive();
                if (hive.getTemplateId() != 1175) {
                    continue;
                }
                hiveZoneSet.add(hive);
            }
        }
        return hiveZoneSet.toArray(new Item[hiveZoneSet.size()]);
    }
    
    public static final boolean removeWildHive(final int tilex, final int tiley) {
        final Item hive = getWildHive(tilex, tiley);
        if (hive != null) {
            for (final Item item : hive.getItemsAsArray()) {
                Items.destroyItem(item.getWurmId());
            }
            Items.destroyItem(hive.getWurmId());
            return true;
        }
        return false;
    }
    
    public static boolean isGoodTileForSpawn(final int tilex, final int tiley, final boolean surfaced) {
        return isGoodTileForSpawn(tilex, tiley, surfaced, false);
    }
    
    public static boolean isGoodTileForSpawn(final int tilex, final int tiley, final boolean surfaced, final boolean canBeOccupied) {
        if (tilex < 0 || tiley < 0 || tilex > Zones.worldTileSizeX || tiley > Zones.worldTileSizeY) {
            return false;
        }
        final VolaTile t = getTileOrNull(tilex, tiley, surfaced);
        final short[] steepness = Creature.getTileSteepness(tilex, tiley, surfaced);
        return (canBeOccupied || t == null) && Tiles.decodeHeight(getTileIntForTile(tilex, tiley, surfaced ? 0 : -1)) > 0 && (steepness[0] < 23 || steepness[1] < 23);
    }
    
    public static boolean isVillagePremSpawn(final Village village) {
        return Servers.localServer.testServer || isPremSpawnZoneAt(village.getStartX(), village.getStartY()) || isPremSpawnZoneAt(village.getStartX(), village.getEndY()) || isPremSpawnZoneAt(village.getEndX(), village.getStartY()) || isPremSpawnZoneAt(village.getEndX(), village.getEndY());
    }
    
    public static void reposWildHive(final int tilex, final int tiley, final Tiles.Tile theTile, final byte aData) {
        final Item hive = getWildHive(tilex, tiley);
        if (hive == null) {
            return;
        }
        final Point4f p = MethodsItems.getHivePos(tilex, tiley, theTile, aData);
        if (p.getPosZ() > 0.0f) {
            hive.setPosZ(p.getPosZ());
            hive.updatePos();
        }
    }
    
    public static void addTurret(final Item turret, final boolean silent) {
        Zones.turretZones.put(turret, new TurretZone(turret));
        if (Servers.localServer.testServer) {
            Players.getInstance().sendGmMessage(null, "System", "Turret added to " + turret.getTileX() + "," + turret.getTileY(), false);
        }
    }
    
    public static void removeTurret(final Item turret, final boolean silent) {
        Zones.turretZones.remove(turret);
        if (Servers.localServer.testServer) {
            Players.getInstance().sendGmMessage(null, "System", "Turret removed from " + turret.getTileX() + "," + turret.getTileY(), false);
        }
    }
    
    public static TurretZone getTurretZone(final int tileX, final int tileY, final boolean surfaced) {
        TurretZone bestTurret = null;
        for (final TurretZone tz : Zones.turretZones.values()) {
            if (!tz.containsTile(tileX, tileY)) {
                continue;
            }
            if (bestTurret == null) {
                bestTurret = tz;
            }
            else {
                if (bestTurret.getStrengthForTile(tileX, tileY, surfaced) >= tz.getStrengthForTile(tileX, tileY, surfaced)) {
                    continue;
                }
                bestTurret = tz;
            }
        }
        if (bestTurret == null) {
            return null;
        }
        return bestTurret;
    }
    
    public static Item getCurrentTurret(final int tileX, final int tileY, final boolean surfaced) {
        final TurretZone tz = getTurretZone(tileX, tileY, surfaced);
        if (tz == null) {
            return null;
        }
        return tz.getZoneItem();
    }
    
    static {
        virtualZones = new HashMap<Integer, VirtualZone>();
        miningTiles = new Hashtable<Integer, Map<Integer, Byte>>();
        duelRings = new HashSet<Item>();
        worldTileSizeX = 1 << Constants.meshSize;
        worldTileSizeY = 1 << Constants.meshSize;
        worldMeterSizeX = (Zones.worldTileSizeX - 1) * 4;
        worldMeterSizeY = (Zones.worldTileSizeY - 1) * 4;
        Zones.protectedTiles = new boolean[Zones.worldTileSizeX][Zones.worldTileSizeY];
        Zones.walkedTiles = new boolean[Zones.worldTileSizeX][Zones.worldTileSizeY];
        kingdoms = new byte[Zones.worldTileSizeX][Zones.worldTileSizeY];
        faithSizeX = Zones.worldTileSizeX >> 3;
        faithSizeY = Zones.worldTileSizeY >> 3;
        domainSizeX = Zones.worldTileSizeX / 64;
        domainSizeY = Zones.worldTileSizeY / 64;
        influenceSizeX = Zones.worldTileSizeX / 256;
        influenceSizeY = Zones.worldTileSizeY / 256;
        hiveZoneSizeX = Zones.worldTileSizeX / 32;
        hiveZoneSizeY = Zones.worldTileSizeY / 32;
        Zones.hasLoadedChristmas = false;
        logger = Logger.getLogger(Zones.class.getName());
        Zones.currentSaveZoneX = 0;
        Zones.currentSaveZoneY = 0;
        Zones.loading = false;
        Zones.numberOfZones = 0;
        Zones.haslogged = false;
        Zones.coverHolder = 0;
        surfaceDomains = new FaithZone[Zones.faithSizeX][Zones.faithSizeY];
        caveDomains = new FaithZone[Zones.faithSizeX][Zones.faithSizeY];
        altars = new LinkedList<Item>();
        altarZones = new ArrayList<HashMap<Item, FaithZone>>();
        influenceZones = new ArrayList<HashMap<Item, InfluenceZone>>();
        hiveZones = new ArrayList<ConcurrentHashMap<Item, HiveZone>>();
        turretZones = new ConcurrentHashMap<Item, TurretZone>();
        influenceCache = new byte[Zones.worldTileSizeX][Zones.worldTileSizeY];
        Zones.pollnum = 0;
        guardTowers = new LinkedList<Item>();
        protectedTileFile = ServerDirInfo.getFileDBPath() + File.separator + "protectedTiles.bmap";
        Zones.shouldCreateWarTargets = false;
        Zones.shouldSourceSprings = false;
        Zones.landPercent = new HashMap<Byte, Float>();
        Zones.evilsanta = null;
        Zones.santa = null;
        Zones.santaMolRehan = null;
        santas = new ConcurrentHashMap<Long, Creature>();
        zrand = new Random();
        Zones.currentPollZoneX = Zones.zrand.nextInt(Zones.worldTileSizeX);
        Zones.currentPollZoneY = Zones.zrand.nextInt(Zones.worldTileSizeY);
        Zones.devlog = false;
        ZONE_SYNC_LOCK = new Object();
        Zones.posmap = new LinkedList<LongPosition>();
        Zones.hasStartedYet = false;
        try {
            createZones();
            loadAllMiningTiles();
            SpawnTable.createEncounters();
            initializeWalkTiles();
            loadProtectedTiles();
            Creatures.getInstance().numberOfZonesX = Zones.worldTileSizeX >> 6;
            for (int i = 0; i < Zones.worldTileSizeX; ++i) {
                for (int j = 0; j < Zones.worldTileSizeY; ++j) {
                    Zones.influenceCache[i][j] = -1;
                }
            }
        }
        catch (IOException ex) {
            Zones.logger.log(Level.SEVERE, "Failed to load zones!", ex);
        }
        Zones.lastCounted = 0L;
    }
}

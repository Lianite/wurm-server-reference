// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.structures.Door;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.structures.Fence;
import java.io.IOException;
import java.io.BufferedWriter;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.effects.Effect;
import javax.annotation.Nonnull;
import java.util.List;
import com.wurmonline.server.Players;
import com.wurmonline.server.Constants;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.Items;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.items.ItemFactory;
import java.util.Map;
import java.util.HashMap;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.Features;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.mesh.MeshIO;
import java.util.logging.Level;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Structure;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.villages.Village;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CreatureTypes;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;

public abstract class Zone implements CounterTypes, MiscConstants, ItemTypes, CreatureTemplateIds, CreatureTypes
{
    public static final String cvsversion = "$Id: Zone.java,v 1.55 2007-04-09 13:40:23 root Exp $";
    private static int ids;
    private static final Logger logger;
    private Set<Village> villages;
    final int startX;
    final int endX;
    final int startY;
    final int endY;
    private final ConcurrentHashMap<Integer, VolaTile> tiles;
    private final ArrayList<VolaTile> deletionQueue;
    Set<VirtualZone> zoneWatchers;
    Set<Structure> structures;
    private Tracks tracks;
    int id;
    boolean isLoaded;
    final boolean isOnSurface;
    boolean loading;
    private final int size;
    private int creatures;
    private int kingdomCreatures;
    int highest;
    private boolean allWater;
    private boolean allLand;
    boolean isForest;
    Den den;
    Item creatureSpawn;
    Item treasureChest;
    static int spawnPoints;
    static int treasureChests;
    private static final Random r;
    private static final long SPRING_PRIME = 7919L;
    private boolean hasRift;
    private final int spawnSeed;
    private static final VolaTile[] emptyTiles;
    private static final VirtualZone[] emptyWatchers;
    private static final Structure[] emptyStructures;
    private short pollTicker;
    public static final int secondsBetweenPolls = 800;
    static final int zonesPolled;
    public static final int maxZonesPolled;
    private static final long LOG_ELAPSED_TIME_THRESHOLD;
    private static final int breedingLimit;
    private static final LinkedList<Long> fogSpiders;
    static int totalItems;
    
    Zone(final int aStartX, final int aEndX, final int aStartY, final int aEndY, final boolean aIsOnSurface) {
        this.isLoaded = true;
        this.loading = false;
        this.creatures = 0;
        this.kingdomCreatures = 0;
        this.highest = 0;
        this.allWater = false;
        this.allLand = false;
        this.isForest = false;
        this.den = null;
        this.creatureSpawn = null;
        this.treasureChest = null;
        this.hasRift = false;
        this.pollTicker = 0;
        this.id = Zone.ids++;
        this.pollTicker = (short)(this.id / Zone.zonesPolled);
        this.startX = Zones.safeTileX(aStartX);
        this.startY = Zones.safeTileY(aStartY);
        this.endX = Zones.safeTileX(aEndX);
        this.endY = Zones.safeTileY(aEndY);
        this.size = aEndX - aStartX + 1;
        this.isOnSurface = aIsOnSurface;
        this.tiles = new ConcurrentHashMap<Integer, VolaTile>();
        this.deletionQueue = new ArrayList<VolaTile>();
        this.setTypes();
        this.spawnSeed = Zones.worldTileSizeX / 200;
    }
    
    final Item[] getAllItems() {
        if (this.tiles != null) {
            final Set<Item> items = new HashSet<Item>();
            for (final VolaTile tile : this.tiles.values()) {
                final Item[] items2;
                final Item[] its = items2 = tile.getItems();
                for (final Item lIt : items2) {
                    items.add(lIt);
                }
            }
            return items.toArray(new Item[items.size()]);
        }
        return new Item[0];
    }
    
    public final Creature[] getAllCreatures() {
        if (this.tiles != null) {
            final Set<Creature> crets = new HashSet<Creature>();
            for (final VolaTile tile : this.tiles.values()) {
                final Creature[] creatures;
                final Creature[] its = creatures = tile.getCreatures();
                for (final Creature lIt : creatures) {
                    crets.add(lIt);
                }
            }
            return crets.toArray(new Creature[crets.size()]);
        }
        return new Creature[0];
    }
    
    private void setTypes() {
        if (this.isOnSurface) {
            int forest = 0;
            final MeshIO mesh = Server.surfaceMesh;
            for (int x = this.startX; x <= this.endX; ++x) {
                for (int y = this.startY; y < this.endY; ++y) {
                    final int tile = mesh.getTile(x, y);
                    final int h = Tiles.decodeHeight(tile);
                    if (h > this.highest) {
                        this.highest = h;
                        this.allWater = false;
                    }
                    else if (h < 0) {
                        this.allLand = false;
                    }
                    if (Tiles.isTree(Tiles.decodeType(tile))) {
                        ++forest;
                    }
                }
            }
            if (forest > this.size * this.size / 6) {
                if (Zone.logger.isLoggable(Level.FINEST)) {
                    Zone.logger.finest("Zone at " + this.startX + "," + this.startY + "-" + this.endX + "," + this.endY + " is forest.");
                }
                this.isForest = true;
            }
        }
    }
    
    public final int getSize() {
        return this.size;
    }
    
    public final boolean isOnSurface() {
        return this.isOnSurface;
    }
    
    public final void addVillage(final Village village) {
        if (this.villages == null) {
            this.villages = new HashSet<Village>();
        }
        if (!this.villages.contains(village)) {
            this.villages.add(village);
            if (this.tiles != null) {
                for (final VolaTile tile : this.tiles.values()) {
                    if (village.covers(tile.getTileX(), tile.getTileY())) {
                        tile.setVillage(village);
                    }
                }
            }
            this.addMineDoors(village);
        }
    }
    
    public final void removeVillage(final Village village) {
        if (this.villages == null) {
            this.villages = new HashSet<Village>();
        }
        if (this.villages.contains(village)) {
            this.villages.remove(village);
            if (this.tiles != null) {
                for (final VolaTile tile : this.tiles.values()) {
                    if (village.covers(tile.getTileX(), tile.getTileY())) {
                        tile.setVillage(null);
                    }
                }
            }
            for (int x = this.startX; x < this.endX; ++x) {
                for (int y = this.startY; y < this.endY; ++y) {
                    final MineDoorPermission md = MineDoorPermission.getPermission(x, y);
                    if (md != null && village.covers(x, y)) {
                        village.removeMineDoor(md);
                    }
                }
            }
        }
    }
    
    public final void updateVillage(final Village village, final boolean shouldStay) {
        if (this.villages == null) {
            this.villages = new HashSet<Village>();
        }
        if (this.villages.contains(village)) {
            if (!shouldStay) {
                this.villages.remove(village);
            }
            if (this.tiles != null) {
                for (final VolaTile tile : this.tiles.values()) {
                    if (!village.covers(tile.getTileX(), tile.getTileY()) && tile.getVillage() == village) {
                        tile.setVillage(null);
                    }
                }
                for (final VolaTile tile : this.tiles.values()) {
                    if (village.covers(tile.getTileX(), tile.getTileY())) {
                        tile.setVillage(village);
                    }
                }
            }
            for (int x = this.startX; x < this.endX; ++x) {
                for (int y = this.startY; y < this.endY; ++y) {
                    final MineDoorPermission md = MineDoorPermission.getPermission(x, y);
                    if (md != null) {
                        if (!village.covers(x, y) && md.getVillage() == village) {
                            village.removeMineDoor(md);
                        }
                        if (village.covers(x, y)) {
                            village.addMineDoor(md);
                        }
                    }
                }
            }
            this.addMineDoors(village);
        }
        else if (shouldStay) {
            this.addVillage(village);
        }
    }
    
    final boolean containsVillage(final int x, final int y) {
        if (this.villages != null) {
            for (final Village village : this.villages) {
                if (village.covers(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    final Village getVillage(final int x, final int y) {
        if (this.villages != null) {
            for (final Village village : this.villages) {
                if (village.covers(x, y)) {
                    return village;
                }
            }
        }
        return null;
    }
    
    public final Village[] getVillages() {
        if (this.villages != null) {
            return this.villages.toArray(new Village[this.villages.size()]);
        }
        return new Village[0];
    }
    
    public final void poll(final int nums) {
        ++this.pollTicker;
        final boolean lPollStuff = this.pollTicker >= Zone.maxZonesPolled;
        final boolean spawnCreatures = lPollStuff || Creatures.getInstance().getNumberOfCreatures() < Servers.localServer.maxCreatures - 1000;
        final boolean checkAreaEffect = Server.rand.nextInt(5) == 0;
        final long now = System.nanoTime();
        for (final VolaTile lElement : this.tiles.values()) {
            lElement.poll(lPollStuff, this.pollTicker, checkAreaEffect);
        }
        for (final VolaTile toDelete : this.deletionQueue) {
            this.tiles.remove(toDelete.hashCode());
        }
        this.deletionQueue.clear();
        final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
        if (Zone.logger.isLoggable(Level.FINE) && lElapsedTime > 200.0f) {
            Zone.logger.fine("Zone at " + this.startX + ", " + this.startY + " polled " + this.tiles.size() + " tiles. That took " + lElapsedTime + " millis.");
        }
        else if (!Servers.localServer.testServer && lElapsedTime > 300.0f) {
            Zone.logger.log(Level.INFO, "Zone at " + this.startX + ", " + this.startY + " polled " + this.tiles.size() + " tiles. That took " + lElapsedTime + " millis.");
        }
        if (this.isOnSurface()) {
            if (Server.getWeather().getFog() > 0.5f && Zone.fogSpiders.size() < Zones.worldTileSizeX / 10) {
                try {
                    final TilePos tp = TilePos.fromXY(this.getStartX() + Server.rand.nextInt(this.size), this.getStartY() + Server.rand.nextInt(this.size));
                    if (Tiles.decodeHeight(Server.surfaceMesh.getTile(tp)) > 0) {
                        final VolaTile t = Zones.getTileOrNull(tp, true);
                        if ((t == null || (t.getStructure() == null && t.getVillage() == null)) && Villages.getVillage(tp, true) == null) {
                            final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(105);
                            final Creature cret = Creature.doNew(105, (tp.x << 2) + 2.0f, (tp.y << 2) + 2.0f, Server.rand.nextInt(360), 0, "", ctemplate.getSex(), (byte)0);
                            Zone.fogSpiders.add(cret.getWurmId());
                            if (Zone.fogSpiders.size() % 100 == 0) {
                                Zone.logger.log(Level.INFO, "Now " + Zone.fogSpiders.size() + " fog spiders.");
                            }
                        }
                    }
                }
                catch (Exception ex) {
                    Zone.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            else if (Server.getWeather().getFog() <= 0.0f && Zone.fogSpiders.size() > 0) {
                final long toDestroy = Zone.fogSpiders.removeFirst();
                try {
                    final Creature spider = Creatures.getInstance().getCreature(toDestroy);
                    spider.destroy();
                    if (Zone.fogSpiders.size() % 100 == 0) {
                        Zone.logger.log(Level.INFO, "Now " + Zone.fogSpiders.size() + " fog spiders.");
                    }
                }
                catch (Exception ex2) {}
            }
        }
        if (lPollStuff) {
            if (Zone.logger.isLoggable(Level.FINEST)) {
                Zone.logger.finest(this.id + " polling. Ticker=" + this.pollTicker + " max=" + Zone.maxZonesPolled);
            }
            this.pollTicker = 0;
            if (this.tracks != null) {
                this.tracks.decay();
            }
            if (Features.Feature.NEWDOMAINS.isEnabled()) {
                final FaithZone[] faithZonesCoveredBy;
                final FaithZone[] lFaithZonesCovered = faithZonesCoveredBy = Zones.getFaithZonesCoveredBy(this.startX, this.startY, this.endX, this.endY, this.isOnSurface);
                for (final FaithZone lElement2 : faithZonesCoveredBy) {
                    lElement2.pollMycelium();
                }
            }
            else {
                final FaithZone[] lFaithZonesCovered = Zones.getFaithZonesCoveredBy(this.startX, this.startY, this.endX, this.endY, this.isOnSurface);
                Deity old = null;
                for (final FaithZone lElement3 : lFaithZonesCovered) {
                    old = lElement3.getCurrentRuler();
                    if (lElement3.poll()) {
                        for (int x = lElement3.getStartX(); x < lElement3.getEndX(); ++x) {
                            for (int y = lElement3.getStartY(); y < lElement3.getEndY(); ++y) {
                                final VolaTile tile = this.getTileOrNull(x, y);
                                if (tile != null) {
                                    if (old == null) {
                                        tile.broadCast("The domain of " + lElement3.getCurrentRuler().getName() + " now has reached this place.");
                                    }
                                    else if (lElement3.getCurrentRuler() != null) {
                                        tile.broadCast(lElement3.getCurrentRuler().getName() + "'s domain now is the strongest here!");
                                    }
                                    else {
                                        tile.broadCast(old.getName() + " has had to lose " + old.getHisHerItsString() + " hold over this area!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (spawnCreatures && !this.isHasRift()) {
            boolean lSpawnKingdom = false;
            if (this.kingdomCreatures <= 0 && this.isOnSurface()) {
                if (!Servers.localServer.PVPSERVER) {
                    lSpawnKingdom = (Server.rand.nextInt(50) == 0);
                }
                else {
                    lSpawnKingdom = (Server.rand.nextInt(20) == 0);
                }
            }
            boolean spawnSeaHunter = false;
            boolean spawnSeaCreature = false;
            if (this.isOnSurface && this.creatures < 20 && Servers.localServer.maxCreatures > 100) {
                if (!lSpawnKingdom && Creatures.getInstance().getNumberOfSeaHunters() < 500) {
                    spawnSeaHunter = true;
                }
                if (!spawnSeaHunter && !lSpawnKingdom) {
                    spawnSeaCreature = true;
                }
            }
            if (Zone.logger.isLoggable(Level.FINEST)) {
                Zone.logger.finest(this.id + " " + (this.den != null && this.creatures < 20) + " || (" + Creatures.getInstance().getNumberOfCreatures() + "<" + Servers.localServer.maxCreatures + " && (" + (this.creatures < 5) + " || " + lSpawnKingdom + "))");
            }
            boolean doSpawn = this.den != null || this.creatureSpawn != null;
            if (doSpawn) {
                doSpawn = (this.creatures < 60 && Creatures.getInstance().getNumberOfTyped() < Servers.localServer.maxTypedCreatures);
            }
            if (!doSpawn) {
                if (this.creatures < 40 || lSpawnKingdom) {
                    doSpawn = true;
                }
                if (Creatures.getInstance().getNumberOfCreatures() > Servers.localServer.maxCreatures + Servers.localServer.maxTypedCreatures) {
                    lSpawnKingdom = false;
                    doSpawn = false;
                }
            }
            boolean createDen = false;
            if (Zone.spawnPoints < Servers.localServer.maxTypedCreatures / 40 && this.den == null && this.creatureSpawn == null && Server.rand.nextInt(10) == 0) {
                createDen = true;
            }
            if (doSpawn || createDen) {
                final int lSeed = Server.rand.nextInt(this.spawnSeed);
                if (lSeed == 0) {
                    final int sx = Server.rand.nextInt(this.endX - this.startX);
                    final int sy = Server.rand.nextInt(this.endY - this.startY);
                    final int tx = this.startX + sx;
                    final int ty = this.startY + sy;
                    for (int xa = -10; xa < 10; ++xa) {
                        for (int ya = -10; ya < 10; ++ya) {
                            final VolaTile t2 = Zones.getTileOrNull(tx + xa, ty + ya, this.isOnSurface);
                            if (t2 != null) {
                                final Creature[] creatures;
                                final Creature[] crets = creatures = t2.getCreatures();
                                for (final Creature lCret : creatures) {
                                    if (lCret.isPlayer()) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    final VolaTile t3 = this.getTileOrNull(tx, ty);
                    if (t3 != null) {
                        if (lSpawnKingdom && t3.getWalls().length == 0 && t3.getFences().length == 0 && t3.getStructure() == null && t3.getCreatures().length == 0) {
                            this.spawnCreature(tx, ty, lSpawnKingdom);
                        }
                    }
                    else {
                        final Village v = Villages.getVillage(tx, ty, this.isOnSurface);
                        if (v == null) {
                            if (createDen) {
                                this.createDen(tx, ty);
                            }
                            else {
                                this.spawnCreature(tx, ty, lSpawnKingdom);
                                if (Server.rand.nextInt(300) == 0) {
                                    this.createTreasureChest(tx, ty);
                                }
                            }
                        }
                    }
                }
            }
            else if (spawnSeaCreature || spawnSeaHunter) {
                this.spawnSeaCreature(spawnSeaHunter);
            }
        }
    }
    
    private final int getRandomSeaCreatureId() {
        if (Creatures.getInstance().getNumberOfSeaMonsters() < 4 && Server.rand.nextInt(86400) == 0) {
            return 70;
        }
        final Map<Integer, Integer> slotsForCreature = new HashMap<Integer, Integer>();
        slotsForCreature.put(100, Creatures.getInstance().getOpenSpawnSlotsForCreatureType(100));
        slotsForCreature.put(97, Creatures.getInstance().getOpenSpawnSlotsForCreatureType(97));
        slotsForCreature.put(99, Creatures.getInstance().getOpenSpawnSlotsForCreatureType(99));
        Integer[] crets = new Integer[slotsForCreature.keySet().size()];
        slotsForCreature.keySet().toArray(crets);
        for (int i = crets.length - 1; i >= 0; --i) {
            final Integer key = crets[i];
            if (slotsForCreature.get(key) == 0) {
                slotsForCreature.remove(key);
            }
        }
        final int validCount = slotsForCreature.keySet().size();
        if (validCount == 0) {
            return 0;
        }
        final int val = Server.rand.nextInt(validCount);
        if (crets.length != slotsForCreature.keySet().size()) {
            crets = new Integer[slotsForCreature.keySet().size()];
            slotsForCreature.keySet().toArray(crets);
        }
        return crets[val];
    }
    
    private final void spawnSeaCreature(final boolean spawnSeaHunter) {
        final int template = spawnSeaHunter ? 71 : this.getRandomSeaCreatureId();
        if (template == 0) {
            return;
        }
        final int sx = Server.rand.nextInt(this.endX - this.startX);
        final int sy = Server.rand.nextInt(this.endY - this.startY);
        final int tx = this.startX + sx;
        final int ty = this.startY + sy;
        for (int xa = -10; xa < 10; ++xa) {
            for (int ya = -10; ya < 10; ++ya) {
                final VolaTile t = Zones.getTileOrNull(tx + xa, ty + ya, this.isOnSurface);
                if (t != null) {
                    final Creature[] creatures;
                    final Creature[] crets = creatures = t.getCreatures();
                    for (final Creature lCret : creatures) {
                        if (lCret.isPlayer()) {
                            return;
                        }
                    }
                }
            }
        }
        final short[] tsteep = Creature.getTileSteepness(tx, ty, true);
        if (tsteep[0] > -200) {
            return;
        }
        try {
            final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(template);
            if (!spawnSeaHunter && !maySpawnCreatureTemplate(ctemplate, false, false)) {
                return;
            }
            byte sex = ctemplate.getSex();
            if (sex == 0 && !ctemplate.keepSex && Server.rand.nextInt(2) == 0) {
                sex = 1;
            }
            Creature.doNew(template, (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextInt(360), 0, "", sex);
        }
        catch (Exception ex) {
            Zone.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    private final void createTreasureChest(final int tx, final int ty) {
        if (Features.Feature.TREASURE_CHESTS.isEnabled()) {
            if (Zone.treasureChests > Zones.worldTileSizeX / 70) {
                return;
            }
            if (!this.allWater && (this.isForest || Server.rand.nextInt(5) == 0)) {
                int tile = Server.caveMesh.getTile(tx, ty);
                if (this.isOnSurface) {
                    tile = Server.surfaceMesh.getTile(tx, ty);
                }
                if (Tiles.decodeHeight(tile) > 0) {
                    boolean ok = !Tiles.isSolidCave(Tiles.decodeType(tile));
                    if (this.isOnSurface) {
                        ok = false;
                        if (!Tiles.isMineDoor(Tiles.decodeType(tile)) && Tiles.decodeType(tile) != Tiles.Tile.TILE_HOLE.id) {
                            ok = true;
                        }
                    }
                    if (ok) {
                        final short[] tsteep = Creature.getTileSteepness(tx, ty, true);
                        if (tsteep[1] >= 20) {
                            return;
                        }
                        try {
                            final Item i = ItemFactory.createItem(995, 50 + Server.rand.nextInt(30), (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextFloat() * 360.0f, this.isOnSurface, (byte)1, -10L, null);
                            i.setAuxData((byte)Server.rand.nextInt(8));
                            if (i.getAuxData() > 4) {
                                i.setRarity((byte)2);
                            }
                            if (Server.rand.nextBoolean()) {
                                final Item lock = ItemFactory.createItem(194, 30.0f + Server.rand.nextFloat() * 70.0f, null);
                                i.setLockId(lock.getWurmId());
                                final int tilex = i.getTileX();
                                final int tiley = i.getTileY();
                                SoundPlayer.playSound("sound.object.lockunlock", tilex, tiley, this.isOnSurface, 1.0f);
                                i.setLocked(true);
                            }
                            i.fillTreasureChest();
                        }
                        catch (Exception fe) {
                            Zone.logger.log(Level.WARNING, "Failed to create treasure chest: " + fe.getMessage(), fe);
                        }
                    }
                }
            }
        }
    }
    
    private final void createDen(final int tx, final int ty) {
        final byte type = (byte)Math.max(0, Server.rand.nextInt(22) - 10);
        final CreatureTemplate[] ctemps = CreatureTemplateFactory.getInstance().getTemplates();
        final CreatureTemplate selected = ctemps[Server.rand.nextInt(ctemps.length)];
        if (selected.hasDen() && (selected.isSubterranean() || this.isOnSurface)) {
            int tile = Server.caveMesh.getTile(tx, ty);
            if (this.isOnSurface) {
                tile = Server.surfaceMesh.getTile(tx, ty);
            }
            if (Tiles.decodeHeight(tile) > 0) {
                boolean ok = !Tiles.isSolidCave(Tiles.decodeType(tile));
                if (this.isOnSurface) {
                    ok = false;
                    if (!Tiles.isMineDoor(Tiles.decodeType(tile)) && Tiles.decodeType(tile) != Tiles.Tile.TILE_HOLE.id) {
                        ok = true;
                    }
                }
                if (ok) {
                    final short[] tsteep = Creature.getTileSteepness(tx, ty, true);
                    if (tsteep[1] >= 20) {
                        return;
                    }
                    if (tsteep[0] > 3000) {
                        return;
                    }
                    try {
                        final Item i = ItemFactory.createItem(521, 50 + Server.rand.nextInt(30), (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextFloat() * 360.0f, this.isOnSurface, (byte)0, -10L, null);
                        i.setAuxData(type);
                        i.setData1(selected.getTemplateId());
                        i.setName(selected.getDenName());
                    }
                    catch (Exception fe) {
                        Zone.logger.log(Level.WARNING, "Failed to create den: " + fe.getMessage(), fe);
                    }
                }
            }
        }
    }
    
    private final void spawnCreature(int tx, int ty, boolean _spawnKingdom) {
        int tile = 0;
        if (this.isOnSurface) {
            tile = Server.surfaceMesh.getTile(tx, ty);
            if (Tiles.isMineDoor(Tiles.decodeType(tile)) || Tiles.decodeType(tile) == Tiles.Tile.TILE_HOLE.id) {
                return;
            }
            byte kingdom = Zones.getKingdom(tx, ty);
            byte kingdomTemplate;
            if ((kingdomTemplate = kingdom) == 0) {
                kingdom = Zones.getKingdom(tx + 50, ty + 50);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx + 50, ty - 50);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx - 50, ty + 50);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx - 50, ty - 50);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx + 50, ty);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx - 50, ty);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx, ty + 50);
            }
            if (kingdom == 0) {
                kingdom = Zones.getKingdom(tx, ty - 50);
            }
            if (kingdom == 0) {
                _spawnKingdom = false;
            }
            else {
                final Kingdom k = Kingdoms.getKingdom(kingdom);
                if (k != null) {
                    kingdomTemplate = k.getTemplate();
                }
            }
            final float height = Tiles.decodeHeightAsFloat(tile);
            if (height > 0.0f) {
                if (_spawnKingdom) {
                    final short[] tsteep = Creature.getTileSteepness(tx, ty, this.isOnSurface);
                    if (tsteep[1] >= 40) {
                        return;
                    }
                    byte deity = 1;
                    int creatureTemplate = 37;
                    if (kingdomTemplate == 3) {
                        deity = 4;
                        creatureTemplate = 40;
                    }
                    else if (kingdomTemplate == 2) {
                        creatureTemplate = 39;
                        deity = 2;
                    }
                    else if (height < 1.0f) {
                        creatureTemplate = 38;
                        deity = 3;
                    }
                    try {
                        final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(creatureTemplate);
                        if (!maySpawnCreatureTemplate(ctemplate, false, _spawnKingdom)) {
                            return;
                        }
                        final Creature cret = Creature.doNew(creatureTemplate, (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextInt(360), this.isOnSurface ? 0 : -1, "", ctemplate.getSex(), kingdom);
                        cret.setDeity(Deities.getDeity(deity));
                    }
                    catch (Exception ex) {
                        Zone.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
                else if (this.den != null) {
                    try {
                        final CreatureTemplate ctemplate2 = CreatureTemplateFactory.getInstance().getTemplate(this.den.getTemplateId());
                        if (!maySpawnCreatureTemplate(ctemplate2, true, false)) {
                            return;
                        }
                        final short[] tsteep2 = Creature.getTileSteepness(tx, ty, this.isOnSurface);
                        if (tsteep2[1] >= 40) {
                            return;
                        }
                        byte sex = ctemplate2.getSex();
                        if (sex == 0 && !ctemplate2.keepSex && Server.rand.nextInt(2) == 0) {
                            sex = 1;
                        }
                        Creature.doNew(this.den.getTemplateId(), (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextInt(360), this.isOnSurface ? 0 : -1, "", sex);
                    }
                    catch (Exception ex2) {
                        Zone.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                    }
                }
                else {
                    final short[] tsteep = Creature.getTileSteepness(tx, ty, this.isOnSurface);
                    if (tsteep[1] >= 40) {
                        return;
                    }
                    if (this.creatureSpawn != null && Server.rand.nextInt(10) != 0) {
                        if (this.creatureSpawn.getData1() > 0) {
                            try {
                                final CreatureTemplate ctemplate3 = CreatureTemplateFactory.getInstance().getTemplate(this.creatureSpawn.getData1());
                                if (!maySpawnCreatureTemplate(ctemplate3, true, false)) {
                                    return;
                                }
                                byte sex = ctemplate3.getSex();
                                if (sex == 0 && !ctemplate3.keepSex && Server.rand.nextInt(2) == 0) {
                                    sex = 1;
                                }
                                byte ctype = this.creatureSpawn.getAuxData();
                                if (Server.rand.nextInt(40) == 0) {
                                    ctype = 99;
                                }
                                Creature.doNew(ctemplate3.getTemplateId(), ctype, (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextInt(360), this.isOnSurface ? 0 : -1, "", sex);
                                if (this.creatureSpawn.getDamage() < 99.0f) {
                                    this.creatureSpawn.setDamage(this.creatureSpawn.getDamage() + Server.rand.nextFloat() * 0.5f);
                                }
                                else {
                                    Items.destroyItem(this.creatureSpawn.getWurmId());
                                }
                            }
                            catch (Exception ex3) {
                                Zone.logger.log(Level.WARNING, ex3.getMessage(), ex3);
                            }
                        }
                    }
                    else {
                        byte type = Tiles.decodeType(tile);
                        byte elev = 0;
                        if (type == Tiles.Tile.TILE_LAVA.id) {
                            if ((Tiles.decodeData(tile) & 0xFF) != 0xFF) {
                                elev = -1;
                            }
                        }
                        else {
                            for (int x = 0; x <= 1; ++x) {
                                int y = 0;
                                while (y <= 1) {
                                    final int ntile = Server.surfaceMesh.getTile(tx + x, ty + y);
                                    final byte ntype = Tiles.decodeType(ntile);
                                    if (Tiles.getTile(ntype).isNormalTree()) {
                                        type = Tiles.Tile.TILE_TREE.id;
                                        break;
                                    }
                                    if (ntype == Tiles.Tile.TILE_LAVA.id) {
                                        if ((Tiles.decodeData(ntile) & 0xFF) != 0xFF) {
                                            elev = -1;
                                        }
                                        type = Tiles.Tile.TILE_LAVA.id;
                                        break;
                                    }
                                    if (Tiles.decodeHeight(ntile) < 0) {
                                        if (ntype == Tiles.Tile.TILE_ROCK.id) {
                                            elev = 1;
                                            type = Tiles.Tile.TILE_ROCK.id;
                                            break;
                                        }
                                        if (ntype == Tiles.Tile.TILE_SAND.id) {
                                            elev = 5;
                                            type = Tiles.Tile.TILE_SAND.id;
                                            break;
                                        }
                                        break;
                                    }
                                    else {
                                        ++y;
                                    }
                                }
                            }
                        }
                        final Encounter enc = SpawnTable.getRandomEncounter(type, elev);
                        this.spawnEncounter(tx, ty, enc);
                    }
                }
            }
            else {
                boolean spawnSeaHunter = false;
                if (Creatures.getInstance().getNumberOfSeaHunters() < 500) {
                    spawnSeaHunter = true;
                    this.spawnSeaCreature(true);
                }
                if (!spawnSeaHunter) {
                    this.spawnSeaCreature(false);
                }
            }
        }
        else {
            if (this.creatureSpawn != null) {
                tx = this.creatureSpawn.getTileX();
                ty = this.creatureSpawn.getTileY();
            }
            tile = Server.caveMesh.getTile(tx, ty);
            final byte type2 = Tiles.decodeType(tile);
            if (!Tiles.isSolidCave(type2)) {
                if (this.creatureSpawn != null) {
                    try {
                        final CreatureTemplate ctemplate4 = CreatureTemplateFactory.getInstance().getTemplate(this.creatureSpawn.getData1());
                        if (!maySpawnCreatureTemplate(ctemplate4, true, false)) {
                            return;
                        }
                        byte sex2 = ctemplate4.getSex();
                        if (sex2 == 0 && !ctemplate4.keepSex && Server.rand.nextInt(2) == 0) {
                            sex2 = 1;
                        }
                        byte ctype2 = this.creatureSpawn.getAuxData();
                        if (Server.rand.nextInt(40) == 0) {
                            ctype2 = 99;
                        }
                        final Creature cret2 = Creature.doNew(ctemplate4.getTemplateId(), ctype2, (tx << 2) + 2.0f, (ty << 2) + 2.0f, Server.rand.nextInt(360), this.isOnSurface ? 0 : -1, "", sex2);
                        if (this.creatureSpawn.getDamage() < 99.0f) {
                            this.creatureSpawn.setDamage(this.creatureSpawn.getDamage() + Server.rand.nextFloat());
                        }
                        else {
                            Items.destroyItem(this.creatureSpawn.getWurmId());
                        }
                    }
                    catch (Exception ex4) {
                        Zone.logger.log(Level.WARNING, ex4.getMessage(), ex4);
                    }
                }
                else if (Tiles.decodeHeight(tile) > 0) {
                    final Encounter enc2 = SpawnTable.getRandomEncounter(Tiles.Tile.TILE_CAVE.id, (byte)(-1));
                    this.spawnEncounter(tx, ty, enc2);
                }
            }
        }
    }
    
    public static final boolean maySpawnCreatureTemplate(final CreatureTemplate ctemplate, final boolean typed, final boolean kingdomCreature) {
        return maySpawnCreatureTemplate(ctemplate, typed, false, kingdomCreature);
    }
    
    public static final boolean maySpawnCreatureTemplate(final CreatureTemplate ctemplate, final boolean typed, final boolean breeding, final boolean kingdomCreature) {
        if ((ctemplate.isAggHuman() || ctemplate.isMonster()) && Creatures.getInstance().getNumberOfAgg() / Creatures.getInstance().getNumberOfCreatures() > Servers.localServer.percentAggCreatures / 100.0f) {
            return false;
        }
        if (typed && Creatures.getInstance().getNumberOfTyped() >= Servers.localServer.maxTypedCreatures) {
            return false;
        }
        if (kingdomCreature) {
            return Creatures.getInstance().getNumberOfKingdomCreatures() < Servers.localServer.maxCreatures / (Servers.localServer.PVPSERVER ? 50 : 200);
        }
        if (Creatures.getInstance().getNumberOfNice() > Servers.localServer.maxCreatures / 2 - (breeding ? Zone.breedingLimit : 0)) {
            return false;
        }
        final int nums = Creatures.getInstance().getCreatureByType(ctemplate.getTemplateId());
        return nums <= Servers.localServer.maxCreatures * ctemplate.getMaxPercentOfCreatures() && (!ctemplate.usesMaxPopulation() || nums < ctemplate.getMaxPopulationOfCreatures());
    }
    
    public static final boolean hasSpring(final int tilex, final int tiley) {
        Zone.r.setSeed((tilex + tiley * Zones.worldTileSizeY) * 7919L);
        return Zone.r.nextInt(128) == 0;
    }
    
    private void spawnEncounter(final int tx, final int ty, final Encounter enc) {
        if (enc != null) {
            final Map<Integer, Integer> encTypes = enc.getTypes();
            if (encTypes != null) {
                for (final Integer templateId : encTypes.keySet()) {
                    final boolean create = true;
                    final Integer nums = encTypes.get(templateId);
                    int n = nums;
                    if (n > 1) {
                        n = Math.max(1, Server.rand.nextInt(n));
                    }
                    try {
                        final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(templateId);
                        if (ctemplate.nonNewbie && Constants.isNewbieFriendly) {
                            continue;
                        }
                        if (!maySpawnCreatureTemplate(ctemplate, false, false)) {
                            return;
                        }
                        for (int x = 0; x < n; ++x) {
                            byte sex = ctemplate.getSex();
                            if (sex == 0 && !ctemplate.keepSex && Server.rand.nextInt(2) == 0) {
                                sex = 1;
                            }
                            final int tChance = Server.rand.nextInt(5);
                            byte rType;
                            if (ctemplate.hasDen()) {
                                if (tChance == 1) {
                                    final int cChance = Server.rand.nextInt(20);
                                    if (cChance == 1) {
                                        rType = 99;
                                    }
                                    else {
                                        rType = (byte)Server.rand.nextInt(11);
                                    }
                                }
                                else {
                                    rType = 0;
                                }
                            }
                            else {
                                rType = 0;
                            }
                            final Creature cret = Creature.doNew(templateId, rType, (tx << 2) + (1.0f + Server.rand.nextFloat() * 2.0f), (ty << 2) + (1.0f + Server.rand.nextFloat() * 2.0f), Server.rand.nextInt(360), this.isOnSurface ? 0 : -1, "", sex);
                            if (Servers.isThisATestServer() && ctemplate.hasDen()) {
                                Players.getInstance().sendGmMessage(null, "System", "Debug: " + cret.getNameWithGenus() + " was spawned @ " + tx + ", " + ty + ", type chance roll was " + tChance + ".", false);
                            }
                        }
                    }
                    catch (Exception ex) {
                        Zone.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        }
    }
    
    private VolaTile[] getTiles() {
        if (this.tiles != null) {
            return this.tiles.values().toArray(new VolaTile[this.tiles.size()]);
        }
        return Zone.emptyTiles;
    }
    
    private VirtualZone[] getWatchers() {
        if (this.zoneWatchers != null) {
            return this.zoneWatchers.toArray(new VirtualZone[this.zoneWatchers.size()]);
        }
        return Zone.emptyWatchers;
    }
    
    public List<Creature> getPlayerWatchers() {
        final List<Creature> playerWatchers = new ArrayList<Creature>();
        if (this.zoneWatchers != null) {
            for (final VirtualZone vz : this.zoneWatchers) {
                final Creature cWatcher = vz.getWatcher();
                if (cWatcher.isPlayer() && !playerWatchers.contains(cWatcher)) {
                    playerWatchers.add(vz.getWatcher());
                }
            }
        }
        return playerWatchers;
    }
    
    public final int getId() {
        return this.id;
    }
    
    public final int getStartX() {
        return this.startX;
    }
    
    public final int getStartY() {
        return this.startY;
    }
    
    public final int getEndX() {
        return this.endX;
    }
    
    public final int getEndY() {
        return this.endY;
    }
    
    public final boolean covers(final int x, final int y) {
        return x >= this.startX && x <= this.endX && y >= this.startY && y <= this.endY;
    }
    
    public final boolean isLoaded() {
        return this.isLoaded;
    }
    
    public final VolaTile getOrCreateTile(@Nonnull final TilePos tilePos) {
        return this.getOrCreateTile(tilePos.x, tilePos.y);
    }
    
    public final VolaTile getOrCreateTile(final int x, final int y) {
        if (!this.covers(x, y)) {
            Zone.logger.log(Level.WARNING, "Zone " + this.id + " at " + this.startX + ", " + this.endX + "-" + this.startY + "," + this.endY + " doesn't cover " + x + "," + y, new Exception());
            try {
                final Zone z = Zones.getZone(x, y, this.isOnSurface());
                Zone.logger.log(Level.INFO, "Adding to " + z.getId());
                return z.getOrCreateTile(x, y);
            }
            catch (NoSuchZoneException nsz) {
                Zone.logger.log(Level.WARNING, "No such zone: " + x + ", " + y + " at ", nsz);
            }
        }
        final VolaTile t = this.tiles.get(VolaTile.generateHashCode(x, y, this.isOnSurface));
        if (t != null) {
            return t;
        }
        final Set<VirtualZone> tileWatchers = new HashSet<VirtualZone>();
        if (this.zoneWatchers != null) {
            for (final VirtualZone watcher : this.zoneWatchers) {
                if (watcher.covers(x, y)) {
                    tileWatchers.add(watcher);
                }
            }
        }
        final VolaTile toReturn = new VolaTile(x, y, this.isOnSurface, tileWatchers, this);
        if (this.villages != null) {
            for (final Village village : this.villages) {
                if (village.covers(x, y)) {
                    toReturn.setVillage(village);
                    break;
                }
            }
        }
        this.tiles.put(VolaTile.generateHashCode(x, y, this.isOnSurface), toReturn);
        return toReturn;
    }
    
    final void removeTile(final VolaTile tile) {
        this.deletionQueue.add(tile);
        tile.setInactive(true);
    }
    
    @Deprecated
    public final VolaTile getTile(final int x, final int y) throws NoSuchTileException {
        final VolaTile t = this.tiles.get(VolaTile.generateHashCode(x, y, this.isOnSurface));
        if (t != null) {
            return t;
        }
        throw new NoSuchTileException(x + ", " + y);
    }
    
    public final VolaTile getTileOrNull(@Nonnull final TilePos tilePos) {
        return this.getTileOrNull(tilePos.x, tilePos.y);
    }
    
    public final VolaTile getTileOrNull(final int x, final int y) {
        final VolaTile t = this.tiles.get(VolaTile.generateHashCode(x, y, this.isOnSurface));
        return t;
    }
    
    public final void addEffect(final Effect effect, final boolean temp) {
        final int x = effect.getTileX();
        final int y = effect.getTileY();
        final VolaTile tile = this.getOrCreateTile(x, y);
        tile.addEffect(effect, temp);
    }
    
    public final void removeEffect(final Effect effect) {
        final int x = effect.getTileX();
        final int y = effect.getTileY();
        final VolaTile tile = this.getTileOrNull(x, y);
        if (tile != null) {
            if (!tile.removeEffect(effect)) {
                for (final VolaTile t : this.tiles.values()) {
                    if (t.removeEffect(effect)) {
                        Zone.logger.log(Level.WARNING, "Aimed to delete effect at " + x + "," + y + " but found it at " + t.getTileX() + ", " + t.getTileY() + " instead.");
                    }
                }
            }
        }
        else {
            Zone.logger.log(Level.WARNING, "Tile at " + x + "," + y + " failed to remove effect: No Tile Found");
        }
    }
    
    public int addCreature(final long creatureId) throws NoSuchCreatureException, NoSuchPlayerException {
        Creature creature = null;
        creature = Server.getInstance().getCreature(creatureId);
        ++this.creatures;
        if (creature.isDefendKingdom() || creature.isAggWhitie()) {
            ++this.kingdomCreatures;
        }
        if (creature.getTemplate().getTemplateId() == 105 && !Zone.fogSpiders.contains(creatureId)) {
            Zone.fogSpiders.add(creatureId);
        }
        final int x = creature.getTileX();
        final int y = creature.getTileY();
        final VolaTile tile = this.getOrCreateTile(x, y);
        return tile.addCreature(creature, 0.0f);
    }
    
    final void write(final BufferedWriter writer) throws IOException {
    }
    
    public final void removeCreature(final Creature creature, final boolean delete, final boolean removeAsTarget) {
        --this.creatures;
        if (creature.isDefendKingdom() || creature.isAggWhitie()) {
            --this.kingdomCreatures;
        }
        if (delete && this.zoneWatchers != null) {
            final VirtualZone[] watchers2;
            final VirtualZone[] watchers = watchers2 = this.getWatchers();
            for (final VirtualZone lWatcher : watchers2) {
                try {
                    lWatcher.deleteCreature(creature, removeAsTarget);
                }
                catch (NoSuchPlayerException nsp) {
                    Zone.logger.log(Level.WARNING, creature.getName() + ": " + nsp.getMessage(), nsp);
                }
                catch (NoSuchCreatureException nsc) {
                    Zone.logger.log(Level.WARNING, creature.getName() + ": " + nsc.getMessage(), nsc);
                }
            }
        }
    }
    
    public final void deleteCreature(final Creature creature, final boolean deleteFromTile) throws NoSuchCreatureException, NoSuchPlayerException {
        --this.creatures;
        if (creature.isDefendKingdom() || creature.isAggWhitie()) {
            --this.kingdomCreatures;
        }
        if (deleteFromTile) {
            final int x = creature.getTileX();
            final int y = creature.getTileY();
            final VolaTile tile = this.getTileOrNull(x, y);
            if (tile != null) {
                if (!tile.removeCreature(creature)) {
                    boolean ok = false;
                    if (creature.getCurrentTile() != null && creature.getCurrentTile().removeCreature(creature)) {
                        ok = true;
                    }
                }
            }
            else {
                boolean ok = false;
                if (creature.getCurrentTile() != null && creature.getCurrentTile().removeCreature(creature)) {
                    ok = true;
                }
                Zone.logger.log(Level.WARNING, this.id + " tile " + x + "," + y + " where " + creature.getName() + " should be didn't contain it. The creature.currentTile removed it=" + ok, new Exception());
            }
        }
        if (this.zoneWatchers != null) {
            for (final VirtualZone zone : this.zoneWatchers) {
                zone.deleteCreature(creature, true);
            }
            if (this.isOnSurface) {
                if (creature.getVisionArea() != null) {
                    final VirtualZone vz = creature.getVisionArea().getSurface();
                    this.zoneWatchers.remove(vz);
                }
            }
            else if (creature.getVisionArea() != null) {
                final VirtualZone vz = creature.getVisionArea().getUnderGround();
                this.zoneWatchers.remove(vz);
            }
        }
    }
    
    public final void addItem(final Item item) {
        this.addItem(item, false, false, false);
    }
    
    public final void addItem(final Item item, final boolean moving, final boolean newLayer, final boolean starting) {
        final int x = item.getTileX();
        final int y = item.getTileY();
        if (!this.covers(x, y)) {
            Zone.logger.log(Level.WARNING, this.id + " zone at " + this.startX + ", " + this.endX + "-" + this.startY + "," + this.endY + " surf=" + this.isOnSurface + " doesn't cover " + x + " (" + item.getPosX() + ") ," + y + " (" + item.getPosY() + "), a " + item.getName() + " id " + item.getWurmId(), new Exception());
            try {
                final Zone z = Zones.getZone(x, y, this.isOnSurface());
                Zone.logger.log(Level.INFO, "Adding to " + z.getId());
                z.addItem(item, moving, newLayer, starting);
            }
            catch (NoSuchZoneException nsz) {
                Zone.logger.log(Level.WARNING, "No such zone: " + x + ", " + y + " at ", nsz);
            }
        }
        else {
            if (item.isKingdomMarker() || item.getTemplateId() == 996) {
                if (!this.isOnSurface()) {
                    Kingdoms.destroyTower(item);
                    Items.decay(item.getWurmId(), item.getDbStrings());
                    return;
                }
                if (item.isGuardTower() && !moving) {
                    Zones.addGuardTower(item);
                }
            }
            else if (item.getTemplateId() == 521) {
                this.creatureSpawn = item;
                ++Zone.spawnPoints;
            }
            else if (item.getTemplateId() == 995) {
                this.treasureChest = item;
                ++Zone.treasureChests;
            }
            final VolaTile tile = this.getOrCreateTile(x, y);
            tile.addItem(item, moving, starting);
            if (newLayer) {
                tile.newLayer(item);
            }
        }
    }
    
    public final void removeItem(final Item item) {
        this.removeItem(item, false, false);
    }
    
    public void updateModelName(final Item item) {
        final int x = item.getTileX();
        final int y = item.getTileY();
        final VolaTile tile = this.getTileOrNull(x, y);
        if (tile != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.getWatcher().getCommunicator().sendChangeModelName(item);
                }
                catch (Exception e) {
                    Zone.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        else {
            Zone.logger.log(Level.WARNING, "Failed to remove " + item.getName() + " at " + x + ", " + y + ". Duplicate methods calling?");
        }
    }
    
    public void updatePile(final Item pile) {
        final int x = pile.getTileX();
        final int y = pile.getTileY();
        final VolaTile tile = this.getTileOrNull(x, y);
        if (tile != null) {
            tile.updatePile(pile);
        }
        else {
            Zone.logger.log(Level.WARNING, "Failed to update pile at x: " + x + " y: " + y);
        }
    }
    
    public void removeItem(final Item item, final boolean moving, final boolean newLayer) {
        item.setZoneId(-10, this.isOnSurface);
        final int x = item.getTileX();
        final int y = item.getTileY();
        final VolaTile tile = this.getTileOrNull(x, y);
        if (tile != null) {
            tile.removeItem(item, moving);
            if (newLayer) {
                tile.newLayer(item);
            }
            if ((item.isUnfinished() || item.isUseOnGroundOnly()) && item.getWatcherSet() != null) {
                for (final Creature cret : item.getWatcherSet()) {
                    cret.getCommunicator().sendRemoveFromCreationWindow(item.getWurmId());
                }
            }
        }
        else {
            Zone.logger.log(Level.WARNING, "Failed to remove " + item.getName() + " at " + x + ", " + y + ". Duplicate methods calling?");
        }
        if (item.getTemplateId() == 521) {
            this.creatureSpawn = null;
            --Zone.spawnPoints;
        }
        else if (item.getTemplateId() == 995) {
            this.treasureChest = null;
            --Zone.treasureChests;
        }
    }
    
    public final void removeStructure(final Structure structure) {
        if (this.structures != null) {
            this.structures.remove(structure);
        }
    }
    
    public final void addStructure(final Structure structure) {
        if (this.structures == null) {
            this.structures = new HashSet<Structure>();
        }
        if (!this.structures.contains(structure)) {
            this.structures.add(structure);
        }
    }
    
    public final void addFence(final Fence fence) {
        final int tilex = fence.getTileX();
        final int tiley = fence.getTileY();
        final VolaTile tile = this.getOrCreateTile(tilex, tiley);
        tile.addFence(fence);
    }
    
    public final void removeFence(final Fence fence) {
        final int tilex = fence.getTileX();
        final int tiley = fence.getTileY();
        final VolaTile tile = this.getOrCreateTile(tilex, tiley);
        tile.removeFence(fence);
        if (fence.isDoor() && fence.isFinished()) {
            final FenceGate gate = FenceGate.getFenceGate(fence.getId());
            if (gate != null) {
                gate.removeFromVillage();
                gate.removeFromTiles();
                gate.delete();
            }
            else {
                Zone.logger.log(Level.WARNING, "fencegate did not exist for fence " + this.id, new Exception());
            }
        }
    }
    
    public final Structure[] getStructures() {
        if (this.structures != null) {
            return this.structures.toArray(new Structure[this.structures.size()]);
        }
        return Zone.emptyStructures;
    }
    
    final void linkTo(final VirtualZone virtualZone, final int aStartX, final int aStartY, final int aEndX, final int aEndY) {
        final long now = System.nanoTime();
        final VolaTile[] tiles;
        final VolaTile[] lTileArray = tiles = this.getTiles();
        for (final VolaTile lElement : tiles) {
            final int centerX = virtualZone.getCenterX();
            final int centerY = virtualZone.getCenterY();
            if (lElement.tilex < aStartX || lElement.tilex > aEndX || lElement.tiley < aStartY || lElement.tiley > aEndY) {
                lElement.removeWatcher(virtualZone);
            }
            else if (lElement.tilex == aStartX || lElement.tilex == aEndX) {
                if (lElement.tiley >= aStartY || lElement.tiley <= aEndY) {
                    lElement.addWatcher(virtualZone);
                }
            }
            else if (lElement.tiley == aStartY || lElement.tiley == aEndY) {
                if (lElement.tilex >= aStartX || lElement.tilex <= aEndX) {
                    lElement.addWatcher(virtualZone);
                }
            }
            else if (virtualZone.getWatcher().isPlayer()) {
                lElement.linkTo(virtualZone, false);
            }
            else {
                final int distancex = Math.abs(lElement.tilex - centerX);
                final int distancey = Math.abs(lElement.tiley - centerY);
                final int distance = Math.max(distancex, distancey);
                if (distance < Math.min(virtualZone.getSize() / 2, 7)) {
                    lElement.linkTo(virtualZone, false);
                }
                else if (distance == 10 && this.size >= 10) {
                    lElement.linkTo(virtualZone, false);
                }
                else if (distance == 20 && this.size >= 20) {
                    lElement.linkTo(virtualZone, false);
                }
            }
        }
        final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
        if (lElapsedTime > Zone.LOG_ELAPSED_TIME_THRESHOLD) {
            Zone.logger.info("linkTo in zone: " + virtualZone + ", which took " + lElapsedTime + " millis.");
        }
    }
    
    final void addWatcher(final int zoneNum) throws NoSuchZoneException {
        final VirtualZone zone = Zones.getVirtualZone(zoneNum);
        if (this.zoneWatchers == null) {
            this.zoneWatchers = new HashSet<VirtualZone>();
        }
        final VirtualZone[] watchers;
        final VirtualZone[] warr = watchers = this.getWatchers();
        for (final VirtualZone lElement : watchers) {
            if (lElement.getWatcher() == null || lElement.getWatcher().getWurmId() == zone.getWatcher().getWurmId()) {
                if (lElement.getWatcher() != null) {
                    Zone.logger.log(Level.WARNING, "Old virtualzone being removed:" + lElement.getWatcher().getName(), new Exception());
                }
                else {
                    Zone.logger.log(Level.WARNING, "Old virtualzone being removed: watcher=null", new Exception());
                }
                this.removeWatcher(lElement);
            }
        }
        if (!this.zoneWatchers.contains(zone)) {
            this.zoneWatchers.add(zone);
            final VolaTile[] tiles;
            final VolaTile[] lTileArray = tiles = this.getTiles();
            for (final VolaTile lElement2 : tiles) {
                if (zone.covers(lElement2.getTileX(), lElement2.getTileY())) {
                    lElement2.addWatcher(zone);
                }
            }
        }
    }
    
    final void removeWatcher(final VirtualZone zone) throws NoSuchZoneException {
        for (final VolaTile tile : this.tiles.values()) {
            tile.removeWatcher(zone);
        }
        if (this.zoneWatchers != null) {
            this.zoneWatchers.remove(zone);
        }
    }
    
    final Track[] getTracksFor(final int tilex, final int tiley) {
        if (this.tracks == null) {
            return new Track[0];
        }
        return this.tracks.getTracksFor(tilex, tiley);
    }
    
    public final Track[] getTracksFor(final int tilex, final int tiley, final int dist) {
        if (this.tracks == null) {
            return new Track[0];
        }
        return this.tracks.getTracksFor(tilex, tiley, dist);
    }
    
    private void addTrack(final Track track) {
        if (this.tracks == null) {
            this.tracks = new Tracks();
        }
        this.tracks.addTrack(track);
    }
    
    final void createTrack(final Creature creature, final int tileX, final int tileY, final int diffTileX, final int diffTileY) {
        if (!creature.isGhost() && creature.getPower() <= 0 && creature.getFloorLevel() <= 0 && !creature.isFish()) {
            final long now = System.nanoTime();
            Track toAdd = null;
            if (tileX + diffTileX >= 0 && tileX + diffTileX < 1 << Constants.meshSize && tileY + diffTileY >= 0 && tileX + diffTileX < 1 << Constants.meshSize) {
                int tilenum = Server.surfaceMesh.getTile(tileX + diffTileX, tileY + diffTileY);
                if (!this.isOnSurface) {
                    tilenum = Server.caveMesh.getTile(tileX + diffTileX, tileY + diffTileY);
                }
                else {
                    Zones.walkedTiles[tileX + diffTileX][tileY + diffTileY] = true;
                }
                if (diffTileX < 0) {
                    if (diffTileY == 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)6);
                    }
                    else if (diffTileY < 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)7);
                    }
                    else if (diffTileY > 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)5);
                    }
                }
                else if (diffTileX > 0) {
                    if (diffTileY == 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)2);
                    }
                    else if (diffTileY < 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)1);
                    }
                    else if (diffTileY > 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)3);
                    }
                }
                else if (diffTileY > 0) {
                    if (diffTileX == 0) {
                        toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)4);
                    }
                }
                else if (diffTileY < 0 && diffTileX == 0) {
                    toAdd = new Track(creature.getWurmId(), creature.getName(), tileX - diffTileX, tileY - diffTileY, tilenum, System.currentTimeMillis(), (byte)0);
                }
                if (toAdd != null) {
                    this.addTrack(toAdd);
                }
                if (Server.rand.nextInt(100) == 0 && this.tracks.getTracksFor(tileX - diffTileX, tileY - diffTileY).length > 20 && creature.isOnSurface() && (!creature.isTypeFleeing() || (creature.getCurrentVillage() == null && Server.rand.nextInt(20) == 0) || (creature.getCurrentVillage() != null && Server.rand.nextInt(50) == 0))) {
                    final MeshIO mesh = Server.surfaceMesh;
                    final byte type = Tiles.decodeType(tilenum);
                    if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_LAWN.id || type == Tiles.Tile.TILE_REED.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_MYCELIUM.id) {
                        mesh.setTile(tileX + diffTileX, tileY + diffTileY, Tiles.encode(Tiles.decodeHeight(tilenum), Tiles.Tile.TILE_DIRT_PACKED.id, Tiles.decodeData(tilenum)));
                        Players.getInstance().sendChangedTile(tileX + diffTileX, tileY + diffTileY, creature.isOnSurface(), true);
                    }
                }
            }
            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
            if (lElapsedTime > Zone.LOG_ELAPSED_TIME_THRESHOLD) {
                Zone.logger.info("createTrack, Creature id, " + creature.getWurmId() + ", which took " + lElapsedTime + " millis. - " + creature);
            }
        }
    }
    
    public final void changeTile(final int x, final int y) {
        final VolaTile tile1 = this.getOrCreateTile(x, y);
        final Creature[] creatures;
        Creature[] crets = creatures = tile1.getCreatures();
        for (final Creature lCret2 : creatures) {
            lCret2.setChangedTileCounter();
        }
        VolaTile tile2 = Zones.getTileOrNull(x - 1, y, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures2;
            crets = (creatures2 = tile2.getCreatures());
            for (final Creature lCret3 : creatures2) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x - 1, y - 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures3;
            crets = (creatures3 = tile2.getCreatures());
            for (final Creature lCret3 : creatures3) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x, y - 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures4;
            crets = (creatures4 = tile2.getCreatures());
            for (final Creature lCret3 : creatures4) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x + 1, y - 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures5;
            crets = (creatures5 = tile2.getCreatures());
            for (final Creature lCret3 : creatures5) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x + 1, y, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures6;
            crets = (creatures6 = tile2.getCreatures());
            for (final Creature lCret3 : creatures6) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x - 1, y + 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures7;
            crets = (creatures7 = tile2.getCreatures());
            for (final Creature lCret3 : creatures7) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x, y + 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures8;
            crets = (creatures8 = tile2.getCreatures());
            for (final Creature lCret3 : creatures8) {
                lCret3.setChangedTileCounter();
            }
        }
        tile2 = Zones.getTileOrNull(x + 1, y + 1, this.isOnSurface);
        if (tile2 != null) {
            final Creature[] creatures9;
            crets = (creatures9 = tile2.getCreatures());
            for (final Creature lCret3 : creatures9) {
                lCret3.setChangedTileCounter();
            }
        }
        tile1.change();
    }
    
    public final void addGates(final Village village) {
        for (final VolaTile tile : this.tiles.values()) {
            final Door[] doors = tile.getDoors();
            if (doors != null) {
                for (final Door lDoor : doors) {
                    if (lDoor instanceof FenceGate && village.covers(tile.getTileX(), tile.getTileY())) {
                        village.addGate((FenceGate)lDoor);
                    }
                }
            }
        }
    }
    
    public final void addMineDoors(final Village village) {
        for (int x = this.startX; x < this.endX; ++x) {
            for (int y = this.startY; y < this.endY; ++y) {
                final MineDoorPermission md = MineDoorPermission.getPermission(x, y);
                if (md != null && village.covers(x, y)) {
                    village.addMineDoor(md);
                }
            }
        }
    }
    
    protected final void loadAllItemsForZone() {
        if (Items.getAllItemsForZone(this.id) != null) {
            for (final Item item : Items.getAllItemsForZone(this.id)) {
                this.addItem(item, false, false, true);
            }
        }
    }
    
    protected void getItemsByZoneId() {
    }
    
    abstract void save() throws IOException;
    
    abstract void load() throws IOException;
    
    abstract void loadFences() throws IOException;
    
    final void checkIntegrity(final Creature checker) {
        for (final VolaTile t : this.tiles.values()) {
            for (final VolaTile t2 : this.tiles.values()) {
                if (t != t2 && t.tilex == t2.tilex && t.tiley == t2.tiley) {
                    checker.getCommunicator().sendNormalServerMessage("Z " + this.getId() + " multiple tiles:" + t.tilex + ", " + t.tiley);
                }
            }
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder lBuilder = new StringBuilder(200);
        lBuilder.append("Zone [id: ").append(this.id);
        lBuilder.append(", startXY: ").append(this.startX).append(',').append(this.startY);
        lBuilder.append(", endXY: ").append(this.endX).append(',').append(this.endY);
        lBuilder.append(", size: ").append(this.size);
        lBuilder.append(", highest: ").append(this.highest);
        lBuilder.append(", isForest: ").append(this.isForest);
        lBuilder.append(", isLoaded: ").append(this.isLoaded);
        lBuilder.append(", isOnSurface: ").append(this.isOnSurface);
        lBuilder.append(']');
        return super.toString();
    }
    
    public boolean isHasRift() {
        return this.hasRift;
    }
    
    public void setHasRift(final boolean hasRift) {
        this.hasRift = hasRift;
    }
    
    static {
        Zone.ids = 0;
        logger = Logger.getLogger(Zone.class.getName());
        Zone.spawnPoints = 0;
        Zone.treasureChests = 0;
        r = new Random();
        emptyTiles = new VolaTile[0];
        emptyWatchers = new VirtualZone[0];
        emptyStructures = new Structure[0];
        zonesPolled = Math.max(2, Zones.numberOfZones * 2 / 800);
        maxZonesPolled = Zones.numberOfZones * 2 / Zone.zonesPolled;
        LOG_ELAPSED_TIME_THRESHOLD = Constants.lagThreshold;
        breedingLimit = Servers.localServer.maxCreatures / 25;
        fogSpiders = new LinkedList<Long>();
        Zone.totalItems = 0;
    }
}

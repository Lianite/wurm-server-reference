// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.structures.Blocker;
import java.util.ArrayList;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.math.Vector3f;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.creatures.NoArmourException;
import com.wurmonline.server.bodys.TempWound;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.server.Features;
import com.wurmonline.server.structures.DbWall;
import com.wurmonline.shared.constants.StructureMaterialEnum;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.Items;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.behaviours.Vehicles;
import javax.annotation.Nonnull;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.epic.EpicTargetItems;
import com.wurmonline.server.structures.TempFence;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.highways.HighwayFinder;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Message;
import javax.annotation.Nullable;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.List;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.SpellResist;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Servers;
import java.util.HashSet;
import java.util.Collection;
import com.wurmonline.server.sounds.Sound;
import com.wurmonline.server.highways.Node;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.creatures.Wagoner;
import java.util.Iterator;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.villages.NoSuchRoleException;
import java.io.IOException;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.structures.FenceGate;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.structures.Fence;
import java.util.Map;
import com.wurmonline.server.structures.StructureSupport;
import com.wurmonline.server.structures.Door;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.effects.Effect;
import java.util.Set;
import com.wurmonline.server.structures.Structure;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MovementListener;

public final class VolaTile implements MovementListener, ItemTypes, MiscConstants, CounterTypes, ItemMaterials
{
    private static final Logger logger;
    private VolaTileItems vitems;
    private Structure structure;
    private Set<Effect> effects;
    private Set<Creature> creatures;
    private Set<Wall> walls;
    private Set<Floor> floors;
    private Set<BridgePart> bridgeParts;
    private Set<MineDoorPermission> mineDoors;
    public final int tilex;
    public final int tiley;
    private final boolean surfaced;
    private Set<VirtualZone> watchers;
    private final Zone zone;
    private Set<Door> doors;
    private Set<Door> unlockedDoors;
    private boolean inactive;
    private boolean isLava;
    private static final Set<StructureSupport> emptySupports;
    private Map<Long, Fence> fences;
    private Map<Long, Fence> magicFences;
    private Village village;
    public boolean isTransition;
    private static final Creature[] emptyCreatures;
    private static final Item[] emptyItems;
    private static final Wall[] emptyWalls;
    private static final Fence[] emptyFences;
    private static final Floor[] emptyFloors;
    private static final BridgePart[] emptyBridgeParts;
    private static final VirtualZone[] emptyWatchers;
    private static final Effect[] emptyEffects;
    private static final Door[] emptyDoors;
    static final Set<Wall> toRemove;
    
    VolaTile(final int x, final int y, final boolean isSurfaced, final Set<VirtualZone> aWatchers, final Zone zon) {
        this.vitems = null;
        this.inactive = false;
        this.isLava = false;
        this.tilex = x;
        this.tiley = y;
        this.surfaced = isSurfaced;
        this.zone = zon;
        this.watchers = aWatchers;
        this.checkTransition();
        this.checkIsLava();
    }
    
    private final void checkTransition() {
        this.isTransition = (Tiles.decodeType(Server.caveMesh.getTile(this.tilex, this.tiley)) == Tiles.Tile.TILE_CAVE_EXIT.id);
    }
    
    public boolean isOnSurface() {
        return this.surfaced;
    }
    
    private boolean isLava() {
        return this.isLava;
    }
    
    private void checkIsLava() {
        this.isLava = ((this.isOnSurface() && Tiles.decodeType(Server.surfaceMesh.getTile(this.tilex, this.tiley)) == Tiles.Tile.TILE_LAVA.id) || (!this.isOnSurface() && Tiles.decodeType(Server.caveMesh.getTile(this.tilex, this.tiley)) == Tiles.Tile.TILE_CAVE_WALL_LAVA.id));
    }
    
    public int getNumberOfItems(final int floorLevel) {
        if (this.vitems == null) {
            return 0;
        }
        return this.vitems.getNumberOfItems(floorLevel);
    }
    
    public final int getNumberOfDecorations(final int floorLevel) {
        if (this.vitems == null) {
            return 0;
        }
        return this.vitems.getNumberOfDecorations(floorLevel);
    }
    
    public void addFence(final Fence fence) {
        if (this.fences == null) {
            this.fences = new ConcurrentHashMap<Long, Fence>();
        }
        if (fence.isMagic()) {
            if (this.magicFences == null) {
                this.magicFences = new ConcurrentHashMap<Long, Fence>();
            }
            this.magicFences.put(fence.getId(), fence);
        }
        if (fence.isTemporary()) {
            final Fence f = this.fences.get(fence.getId());
            if (f != null && !f.isTemporary()) {
                return;
            }
        }
        this.fences.put(fence.getId(), fence);
        if (fence.getZoneId() != this.zone.getId()) {
            fence.setZoneId(this.zone.getId());
        }
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.addFence(fence);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    void setVillage(final Village aVillage) {
        final MineDoorPermission md = MineDoorPermission.getPermission(this.tilex, this.tiley);
        if (this.village == null || !this.village.equals(aVillage)) {
            if (this.doors != null) {
                for (final Door door : this.doors) {
                    if (door instanceof FenceGate) {
                        if (aVillage != null) {
                            aVillage.addGate((FenceGate)door);
                        }
                        else {
                            if (this.village == null) {
                                continue;
                            }
                            this.village.removeGate((FenceGate)door);
                        }
                    }
                }
            }
            if (md != null) {
                if (aVillage != null) {
                    aVillage.addMineDoor(md);
                }
                else if (this.village != null) {
                    this.village.removeMineDoor(md);
                }
            }
            if (this.creatures != null) {
                for (final Creature c : this.creatures) {
                    c.setCurrentVillage(aVillage);
                    if (c.isWagoner() && aVillage == null) {
                        final Wagoner wagoner = c.getWagoner();
                        if (wagoner != null) {
                            wagoner.clrVillage();
                        }
                    }
                    if (c.isNpcTrader() && c.getCitizenVillage() == null) {
                        final Shop s = Economy.getEconomy().getShop(c);
                        if (s.getOwnerId() != -10L) {
                            continue;
                        }
                        if (aVillage != null) {
                            try {
                                VolaTile.logger.log(Level.INFO, "Adding " + c.getName() + " as citizen to " + aVillage.getName());
                                aVillage.addCitizen(c, aVillage.getRoleForStatus((byte)3));
                            }
                            catch (IOException iox) {
                                VolaTile.logger.log(Level.INFO, iox.getMessage());
                            }
                            catch (NoSuchRoleException nsx) {
                                VolaTile.logger.log(Level.INFO, nsx.getMessage());
                            }
                        }
                        else {
                            c.setCitizenVillage(null);
                        }
                    }
                }
            }
            if (this.vitems != null) {
                for (final Item i : this.vitems.getAllItemsAsSet()) {
                    if (i.getTemplateId() == 757) {
                        if (aVillage != null) {
                            aVillage.addBarrel(i);
                        }
                        else {
                            if (this.village == null) {
                                continue;
                            }
                            this.village.removeBarrel(i);
                        }
                    }
                    else {
                        if (i.getTemplateId() != 1112) {
                            continue;
                        }
                        if (aVillage != null) {
                            final Node node = Routes.getNode(i.getWurmId());
                            if (node == null) {
                                continue;
                            }
                            node.setVillage(aVillage);
                        }
                        else {
                            if (this.village == null) {
                                continue;
                            }
                            final Node node = Routes.getNode(i.getWurmId());
                            if (node == null) {
                                continue;
                            }
                            node.setVillage(null);
                        }
                    }
                }
            }
            this.village = aVillage;
        }
        else {
            if (this.doors != null) {
                for (final Door door : this.doors) {
                    if (door instanceof FenceGate) {
                        aVillage.addGate((FenceGate)door);
                    }
                }
            }
            if (md != null) {
                aVillage.addMineDoor(md);
            }
        }
    }
    
    public Village getVillage() {
        return this.village;
    }
    
    public void removeFence(final Fence fence) {
        if (this.fences != null) {
            final Fence f = this.fences.remove(fence.getId());
            if (f != null) {
                if (f.isMagic() && this.magicFences != null) {
                    this.magicFences.remove(fence.getId());
                    if (this.magicFences.isEmpty()) {
                        this.magicFences = null;
                    }
                }
                if (fence.isTemporary() && !f.isTemporary()) {
                    this.fences.put(f.getId(), f);
                }
                else {
                    for (final VirtualZone vz : this.getWatchers()) {
                        try {
                            vz.removeFence(f);
                        }
                        catch (Exception e) {
                            VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    }
                    if (this.fences.isEmpty()) {
                        this.fences = null;
                    }
                }
            }
        }
    }
    
    public void addSound(final Sound sound) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.playSound(sound);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateFence(final Fence fence) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.removeFence(fence);
                vz.addFence(fence);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateMagicalFence(final Fence fence) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.addFence(fence);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public Fence[] getFences() {
        if (this.fences != null) {
            return this.fences.values().toArray(new Fence[this.fences.size()]);
        }
        return VolaTile.emptyFences;
    }
    
    public Collection<Fence> getFencesList() {
        if (this.fences != null) {
            return this.fences.values();
        }
        return null;
    }
    
    public Fence[] getAllFences() {
        final Set<Fence> fenceSet = new HashSet<Fence>();
        if (this.fences != null) {
            for (final Fence f : this.fences.values()) {
                fenceSet.add(f);
            }
        }
        final VolaTile eastTile = this.zone.getTileOrNull(this.tilex + 1, this.tiley);
        if (eastTile != null) {
            final Fence[] eastFences = eastTile.getFencesForDir(Tiles.TileBorderDirection.DIR_DOWN);
            for (int x = 0; x < eastFences.length; ++x) {
                fenceSet.add(eastFences[x]);
            }
        }
        final VolaTile southTile = this.zone.getTileOrNull(this.tilex, this.tiley + 1);
        if (southTile != null) {
            final Fence[] southFences = southTile.getFencesForDir(Tiles.TileBorderDirection.DIR_HORIZ);
            for (int x2 = 0; x2 < southFences.length; ++x2) {
                fenceSet.add(southFences[x2]);
            }
        }
        if (fenceSet.size() == 0) {
            return VolaTile.emptyFences;
        }
        return fenceSet.toArray(new Fence[fenceSet.size()]);
    }
    
    public boolean hasFenceOnCorner(final int floorLevel) {
        if (this.fences != null && this.getFencesForLevel(floorLevel).length > 0) {
            return true;
        }
        final VolaTile westTile = this.zone.getTileOrNull(this.tilex - 1, this.tiley);
        if (westTile != null) {
            final Fence[] westFences = westTile.getFencesForDirAndLevel(Tiles.TileBorderDirection.DIR_HORIZ, floorLevel);
            if (westFences.length > 0) {
                return true;
            }
        }
        final VolaTile northTile = this.zone.getTileOrNull(this.tilex, this.tiley - 1);
        if (northTile != null) {
            final Fence[] northFences = northTile.getFencesForDirAndLevel(Tiles.TileBorderDirection.DIR_DOWN, floorLevel);
            if (northFences.length > 0) {
                return true;
            }
        }
        return false;
    }
    
    public Fence[] getFencesForDirAndLevel(final Tiles.TileBorderDirection dir, final int floorLevel) {
        if (this.fences != null) {
            final Set<Fence> fenceSet = new HashSet<Fence>();
            for (final Fence f : this.fences.values()) {
                if (f.getDir() == dir && f.getFloorLevel() == floorLevel) {
                    fenceSet.add(f);
                }
            }
            return fenceSet.toArray(new Fence[fenceSet.size()]);
        }
        return VolaTile.emptyFences;
    }
    
    public Fence[] getFencesForDir(final Tiles.TileBorderDirection dir) {
        if (this.fences != null) {
            final Set<Fence> fenceSet = new HashSet<Fence>();
            for (final Fence f : this.fences.values()) {
                if (f.getDir() == dir) {
                    fenceSet.add(f);
                }
            }
            return fenceSet.toArray(new Fence[fenceSet.size()]);
        }
        return VolaTile.emptyFences;
    }
    
    public Fence[] getFencesForLevel(final int floorLevel) {
        if (this.fences != null) {
            final Set<Fence> fenceSet = new HashSet<Fence>();
            for (final Fence f : this.fences.values()) {
                if (f.getFloorLevel() == floorLevel) {
                    fenceSet.add(f);
                }
            }
            return fenceSet.toArray(new Fence[fenceSet.size()]);
        }
        return VolaTile.emptyFences;
    }
    
    public Fence getFence(final long id) {
        if (this.fences != null) {
            return this.fences.get(id);
        }
        return null;
    }
    
    public void addDoor(final Door door) {
        if (this.doors == null) {
            this.doors = new HashSet<Door>();
        }
        if (!this.doors.contains(door)) {
            this.doors.add(door);
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        door.addWatcher(vz);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    public void removeDoor(final Door door) {
        if (this.doors != null && this.doors.contains(door)) {
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        door.removeWatcher(vz);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            this.doors.remove(door);
            if (this.unlockedDoors != null) {
                this.unlockedDoors.remove(door);
            }
            if (this.doors.isEmpty()) {
                this.doors = null;
            }
        }
    }
    
    public void addUnlockedDoor(final Door door) {
        if (this.unlockedDoors == null) {
            this.unlockedDoors = new HashSet<Door>();
        }
        if (!this.unlockedDoors.contains(door)) {
            this.unlockedDoors.add(door);
        }
    }
    
    public void removeUnlockedDoor(final Door door) {
        if (this.unlockedDoors == null) {
            return;
        }
        this.unlockedDoors.remove(door);
        if (this.unlockedDoors.isEmpty()) {
            this.unlockedDoors = null;
        }
    }
    
    public void addMineDoor(final MineDoorPermission door) {
        if (this.mineDoors == null) {
            this.mineDoors = new HashSet<MineDoorPermission>();
        }
        if (this.mineDoors != null && !this.mineDoors.contains(door)) {
            this.mineDoors.add(door);
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        door.addWatcher(vz);
                        vz.addMineDoor(door);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    public void removeMineDoor(final MineDoorPermission door) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                door.removeWatcher(vz);
                vz.removeMineDoor(door);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (this.mineDoors == null) {
            return;
        }
        this.mineDoors.remove(door);
        if (this.mineDoors.isEmpty()) {
            this.mineDoors = null;
        }
    }
    
    public void checkChangedAttitude(final Creature creature) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.sendAttitude(creature);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void sendUpdateTarget(final Creature creature) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.sendUpdateHasTarget(creature);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public Door[] getDoors() {
        if (this.doors != null && this.doors.size() > 0) {
            return this.doors.toArray(new Door[this.doors.size()]);
        }
        return VolaTile.emptyDoors;
    }
    
    public final int getTileX() {
        return this.tilex;
    }
    
    public final int getTileY() {
        return this.tiley;
    }
    
    public final float getPosX() {
        return (this.tilex << 2) + 2;
    }
    
    public final float getPosY() {
        return (this.tiley << 2) + 2;
    }
    
    public final void pollMagicFences(final long time) {
        if (this.magicFences != null) {
            for (final Fence f : this.magicFences.values()) {
                f.pollMagicFences(time);
            }
        }
    }
    
    public void pollStructures(final long time) {
        if (this.floors != null) {
            for (final Floor floor : this.getFloors()) {
                if (floor.poll(time, this, this.structure)) {
                    this.removeFloor(floor);
                }
            }
        }
        if (this.walls != null) {
            final Wall[] lTempWalls = this.getWalls();
            for (int x = 0; x < lTempWalls.length; ++x) {
                lTempWalls[x].poll(time, this, this.structure);
            }
        }
        if (this.fences != null) {
            for (final Fence f : this.getFences()) {
                f.poll(time);
            }
        }
        if (this.bridgeParts != null) {
            for (final BridgePart bridgePart : this.getBridgeParts()) {
                if (bridgePart.poll(time, this.structure)) {
                    this.removeBridgePart(bridgePart);
                }
            }
        }
        if (this.structure != null) {
            this.structure.poll(time);
        }
    }
    
    public void poll(final boolean pollItems, final int seed, final boolean setAreaEffectFlag) {
        final boolean lava = this.isLava();
        final long now = System.nanoTime();
        if (this.vitems != null) {
            this.vitems.poll(pollItems, seed, lava, this.structure, this.isOnSurface(), this.village, now);
        }
        this.pollMagicFences(now);
        if (lava) {
            for (final Creature c : this.getCreatures()) {
                c.setDoLavaDamage(true);
            }
        }
        if (setAreaEffectFlag && this.getAreaEffect() != null) {
            for (final Creature c : this.getCreatures()) {
                c.setDoAreaEffect(true);
            }
        }
        this.pollAllUnlockedDoorsOnThisTile();
        this.applyLavaDamageToWallsAndFences();
        this.checkDeletion();
        if (Servers.isThisAPvpServer()) {
            this.pollOnDeedEnemys();
        }
    }
    
    private void pollOnDeedEnemys() {
        if (this.getVillage() != null) {
            for (final Creature c : this.getCreatures()) {
                if (c.getPower() < 1 && c.isPlayer() && this.getVillage().kingdom != c.getKingdomId()) {
                    try {
                        c.currentVillage.getToken().setLastOwnerId(System.currentTimeMillis());
                    }
                    catch (NoSuchItemException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public final boolean doAreaDamage(final Creature aCreature) {
        boolean dead = false;
        if (!aCreature.isInvulnerable() && !aCreature.isGhost() && !aCreature.isUnique()) {
            final AreaSpellEffect aes = this.getAreaEffect();
            if (aes != null) {
                System.out.println("AREA DAMAGE " + aCreature.getName());
                if (aes.getFloorLevel() != aCreature.getFloorLevel()) {
                    final int heightOffset = aes.getHeightOffset();
                    if (heightOffset != 0) {
                        final int pz = aCreature.getPosZDirts();
                        if (Math.abs(pz - heightOffset) > 10) {
                            System.out.println("AREA DAMAGE FAILED");
                            return false;
                        }
                    }
                }
                final byte type = this.getAreaEffect().getType();
                Creature caster = null;
                try {
                    caster = Server.getInstance().getCreature(aes.getCreator());
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
                if (caster != null) {
                    try {
                        if (aCreature.getAttitude(caster) == 2 || (caster.getCitizenVillage() != null && caster.getCitizenVillage().isEnemy(aCreature))) {
                            boolean ok = true;
                            if (!caster.isOnPvPServer() || !aCreature.isOnPvPServer()) {
                                final Village v = aCreature.getCurrentVillage();
                                if (v != null && !v.mayAttack(caster, aCreature)) {
                                    ok = false;
                                }
                            }
                            if (ok) {
                                aCreature.addAttacker(caster);
                                if (type == 36 || type == 53) {
                                    final byte pos = aCreature.getBody().getRandomWoundPos();
                                    this.sendAttachCreatureEffect(aCreature, (byte)6, (byte)0, (byte)0, (byte)0, (byte)0);
                                    double damage = this.getAreaEffect().getPower() * 4.0;
                                    damage += 150.0;
                                    final double resistance = SpellResist.getSpellResistance(aCreature, 414);
                                    damage *= resistance;
                                    SpellResist.addSpellResistance(aCreature, 414, damage);
                                    damage = Spell.modifyDamage(aCreature, damage);
                                    dead = CombatEngine.addWound(caster, aCreature, (byte)8, pos, damage, 1.0f, "", null, 0.0f, 0.0f, false, false, true, true);
                                }
                                else if (type == 35 || type == 51) {
                                    final byte pos = aCreature.getBody().getRandomWoundPos();
                                    double damage = this.getAreaEffect().getPower() * 2.75;
                                    damage += 300.0;
                                    final double resistance = SpellResist.getSpellResistance(aCreature, 420);
                                    damage *= resistance;
                                    SpellResist.addSpellResistance(aCreature, 420, damage);
                                    damage = Spell.modifyDamage(aCreature, damage);
                                    dead = CombatEngine.addWound(caster, aCreature, (byte)4, pos, damage, 1.0f, "", null, 0.0f, 0.0f, false, false, true, true);
                                }
                                else if (type == 34) {
                                    final byte pos = aCreature.getBody().getRandomWoundPos();
                                    double damage = this.getAreaEffect().getPower() * 1.0;
                                    damage += 400.0;
                                    final double resistance = SpellResist.getSpellResistance(aCreature, 418);
                                    damage *= resistance;
                                    SpellResist.addSpellResistance(aCreature, 418, damage);
                                    damage = Spell.modifyDamage(aCreature, damage);
                                    dead = CombatEngine.addWound(caster, aCreature, (byte)0, pos, damage, 1.0f, "", null, 1.0f, 0.0f, false, false, true, true);
                                }
                                else if (type == 37) {
                                    this.sendAttachCreatureEffect(aCreature, (byte)7, (byte)0, (byte)0, (byte)0, (byte)0);
                                    final byte pos = aCreature.getBody().getRandomWoundPos();
                                    double damage = this.getAreaEffect().getPower() * 2.0;
                                    damage += 350.0;
                                    final double resistance = SpellResist.getSpellResistance(aCreature, 433);
                                    damage *= resistance;
                                    SpellResist.addSpellResistance(aCreature, 433, damage);
                                    damage = Spell.modifyDamage(aCreature, damage);
                                    dead = CombatEngine.addWound(caster, aCreature, (byte)5, pos, damage, 1.0f, "", null, 0.0f, 3.0f, false, false, true, true);
                                }
                            }
                        }
                    }
                    catch (Exception exe) {
                        VolaTile.logger.log(Level.WARNING, exe.getMessage(), exe);
                    }
                }
            }
        }
        return dead;
    }
    
    private void pollAllUnlockedDoorsOnThisTile() {
        if (this.unlockedDoors != null && this.unlockedDoors.size() > 0) {
            final Iterator<Door> it = this.unlockedDoors.iterator();
            while (it.hasNext()) {
                if (it.next().pollUnlocked()) {
                    it.remove();
                }
            }
        }
        if (this.unlockedDoors != null && this.unlockedDoors.isEmpty()) {
            this.unlockedDoors = null;
        }
    }
    
    private void applyLavaDamageToWallsAndFences() {
        if (this.isLava()) {
            if (this.walls != null) {
                final Wall[] lTempWalls = this.getWalls();
                for (int x = 0; x < lTempWalls.length; ++x) {
                    lTempWalls[x].setDamage(lTempWalls[x].getDamage() + 1.0f);
                }
            }
            if (this.fences != null) {
                for (final Fence f : this.getFences()) {
                    f.setDamage(f.getDamage() + 1.0f);
                }
            }
        }
    }
    
    private void pollAllWatchersOfThisTile() {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher() instanceof Player && !vz.getWatcher().hasLink()) {
                    this.removeWatcher(vz);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    private void pollAllCreaturesOnThisTile(final boolean lava, final boolean areaEffect) {
        final long lStart = System.nanoTime();
        final Creature[] lTempCreatures = this.getCreatures();
        for (int x = 0; x < lTempCreatures.length; ++x) {
            this.pollOneCreatureOnThisTile(lava, lTempCreatures[x], areaEffect);
        }
        if ((System.nanoTime() - lStart) / 1000000.0f > 300.0f && !Servers.localServer.testServer) {
            int destroyed = 0;
            for (int y = 0; y < lTempCreatures.length; ++y) {
                if (lTempCreatures[y].isDead()) {
                    ++destroyed;
                }
            }
            VolaTile.logger.log(Level.INFO, "Tile at " + this.tilex + ", " + this.tiley + " polled " + lTempCreatures.length + " creatures. Of those were " + destroyed + " destroyed. It took " + (System.nanoTime() - lStart) / 1000000.0f + " ms");
        }
    }
    
    public final boolean isVisibleToPlayers() {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher() != null && vz.getWatcher().isPlayer()) {
                    return true;
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return false;
    }
    
    private void pollOneCreatureOnThisTile(final boolean lava, final Creature aCreature, final boolean areaEffect) {
        try {
            boolean dead = false;
            if (aCreature.poll()) {
                this.deleteCreature(aCreature);
            }
            else if (lava) {
                if (!aCreature.isInvulnerable() && !aCreature.isGhost() && !aCreature.isUnique() && (aCreature.getDeity() == null || !aCreature.getDeity().isMountainGod() || aCreature.getFaith() < 35.0f) && aCreature.getFarwalkerSeconds() <= 0) {
                    Wound wound = null;
                    try {
                        final byte pos = aCreature.getBody().getRandomWoundPos((byte)10);
                        if (Server.rand.nextInt(10) <= 6 && aCreature.getBody().getWounds() != null) {
                            wound = aCreature.getBody().getWounds().getWoundAtLocation(pos);
                            if (wound != null) {
                                dead = wound.modifySeverity((int)(5000.0f + Server.rand.nextInt(5000) * (100.0f - aCreature.getSpellDamageProtectBonus()) / 100.0f));
                                wound.setBandaged(false);
                                aCreature.setWounded();
                            }
                        }
                        if (wound == null && !aCreature.isGhost() && !aCreature.isUnique() && !aCreature.isKingdomGuard()) {
                            dead = aCreature.addWoundOfType(null, (byte)4, pos, false, 1.0f, true, 5000.0f + Server.rand.nextInt(5000) * (100.0f - aCreature.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                        }
                        aCreature.getCommunicator().sendAlertServerMessage("You are burnt by lava!");
                        if (dead) {
                            aCreature.achievement(142);
                            this.deleteCreature(aCreature);
                        }
                    }
                    catch (Exception ex) {
                        VolaTile.logger.log(Level.WARNING, aCreature.getName() + " " + ex.getMessage(), ex);
                    }
                }
            }
            else if (!dead && areaEffect && !aCreature.isInvulnerable() && !aCreature.isGhost() && !aCreature.isUnique()) {
                final AreaSpellEffect aes = this.getAreaEffect();
                if (aes != null && aes.getFloorLevel() == aCreature.getFloorLevel()) {
                    final byte type = aes.getType();
                    Creature caster = null;
                    try {
                        caster = Server.getInstance().getCreature(aes.getCreator());
                    }
                    catch (NoSuchCreatureException ex3) {}
                    catch (NoSuchPlayerException ex4) {}
                    if (caster != null) {
                        try {
                            if (aCreature.getAttitude(caster) == 2 || (caster.getCitizenVillage() != null && caster.getCitizenVillage().isEnemy(aCreature))) {
                                boolean ok = true;
                                if (!caster.isOnPvPServer() || !aCreature.isOnPvPServer()) {
                                    final Village v = aCreature.getCurrentVillage();
                                    if (v != null && !v.mayAttack(caster, aCreature)) {
                                        ok = false;
                                    }
                                }
                                if (ok) {
                                    aCreature.addAttacker(caster);
                                }
                            }
                        }
                        catch (Exception exe) {
                            VolaTile.logger.log(Level.WARNING, exe.getMessage(), exe);
                        }
                    }
                }
            }
        }
        catch (Exception ex2) {
            VolaTile.logger.log(Level.WARNING, "Failed to poll creature " + aCreature.getWurmId() + " " + ex2.getMessage(), ex2);
            try {
                Server.getInstance().getCreature(aCreature.getWurmId());
            }
            catch (Exception nsc) {
                VolaTile.logger.log(Level.INFO, "Failed to locate creature. Removing from tile. Creature: " + aCreature);
                if (this.creatures == null) {
                    return;
                }
                this.creatures.remove(aCreature);
            }
        }
    }
    
    public void deleteCreature(final Creature creature) {
        creature.setNewTile(null, 0.0f, false);
        this.removeCreature(creature);
        try {
            this.zone.deleteCreature(creature, false);
        }
        catch (NoSuchCreatureException nsc) {
            VolaTile.logger.log(Level.WARNING, nsc.getMessage(), nsc);
        }
        catch (NoSuchPlayerException nsp) {
            VolaTile.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        final Door[] _doors = this.getDoors();
        for (int d = 0; d < _doors.length; ++d) {
            _doors[d].removeCreature(creature);
        }
    }
    
    boolean containsCreature(final Creature creature) {
        return this.creatures != null && this.creatures.contains(creature);
    }
    
    public void deleteCreatureQuick(final Creature creature) {
        creature.setNewTile(null, 0.0f, false);
        this.zone.removeCreature(creature, true, false);
        final Door[] lDoors = this.getDoors();
        for (int d = 0; d < lDoors.length; ++d) {
            lDoors[d].removeCreature(creature);
        }
    }
    
    public void broadCastMulticolored(final List<MulticolorLineSegment> segments, final Creature performer, @Nullable final Creature receiver, final boolean combat, final byte onScreenMessage) {
        if (this.creatures != null) {
            for (final Creature creature : this.creatures) {
                if (!creature.equals(performer) && (receiver == null || !creature.equals(receiver)) && performer.isVisibleTo(creature) && !creature.getCommunicator().isInvulnerable()) {
                    if (combat) {
                        creature.getCommunicator().sendColoredMessageCombat(segments, onScreenMessage);
                    }
                    else {
                        creature.getCommunicator().sendColoredMessageEvent(segments, onScreenMessage);
                    }
                }
            }
        }
    }
    
    public void broadCastAction(final String message, final Creature performer, final boolean combat) {
        this.broadCastAction(message, performer, null, combat);
    }
    
    public void broadCastAction(final String message, final Creature performer, @Nullable final Creature receiver, final boolean combat) {
        if (this.creatures != null) {
            for (final Creature creature : this.creatures) {
                if (!creature.equals(performer) && (receiver == null || !creature.equals(receiver)) && performer.isVisibleTo(creature) && !creature.getCommunicator().isInvulnerable()) {
                    if (combat) {
                        creature.getCommunicator().sendCombatNormalMessage(message);
                    }
                    else {
                        creature.getCommunicator().sendNormalServerMessage(message);
                    }
                }
            }
        }
    }
    
    public void broadCast(final String message) {
        if (this.creatures != null) {
            for (final Creature creature : this.creatures) {
                if (!creature.getCommunicator().isInvulnerable()) {
                    creature.getCommunicator().sendNormalServerMessage(message);
                }
            }
        }
    }
    
    public void broadCastMessage(final Message message) {
        if (this.watchers != null) {
            for (final VirtualZone z : this.watchers) {
                z.broadCastMessage(message);
            }
        }
    }
    
    void broadCastMessageLocal(final Message message) {
        if (this.creatures != null) {
            for (final Creature creature : this.creatures) {
                if (!creature.getCommunicator().isInvulnerable()) {
                    creature.getCommunicator().sendMessage(message);
                }
            }
        }
    }
    
    void addWatcher(final VirtualZone watcher) {
        if (this.watchers == null) {
            this.watchers = new HashSet<VirtualZone>();
        }
        if (!this.watchers.contains(watcher)) {
            this.watchers.add(watcher);
            this.linkTo(watcher, false);
            if (this.doors != null) {
                for (final Door door : this.doors) {
                    door.addWatcher(watcher);
                }
            }
            if (this.mineDoors != null) {
                for (final MineDoorPermission door2 : this.mineDoors) {
                    door2.addWatcher(watcher);
                }
            }
        }
    }
    
    void removeWatcher(final VirtualZone watcher) {
        if (this.watchers != null) {
            if (this.watchers.contains(watcher)) {
                this.watchers.remove(watcher);
                this.linkTo(watcher, true);
                if (this.doors != null) {
                    for (final Door door : this.doors) {
                        door.removeWatcher(watcher);
                    }
                }
                if (this.mineDoors != null) {
                    for (final MineDoorPermission door2 : this.mineDoors) {
                        door2.removeWatcher(watcher);
                    }
                }
                if (!this.isVisibleToPlayers()) {
                    for (final Creature c : this.getCreatures()) {
                        c.setVisibleToPlayers(false);
                    }
                }
            }
            if (VolaTile.logger.isLoggable(Level.FINEST)) {
                VolaTile.logger.finest("Tile: " + this.tilex + ", " + this.tiley + "removing watcher " + watcher.getId());
            }
        }
        else if (VolaTile.logger.isLoggable(Level.FINEST)) {
            VolaTile.logger.finest("Tile: " + this.tilex + ", " + this.tiley + " tried to remove but watchers is null though.");
        }
    }
    
    void addEffect(final Effect effect, final boolean temp) {
        if (this.isTransition && this.surfaced) {
            this.getCaveTile().addEffect(effect, temp);
            return;
        }
        if (!temp) {
            if (this.effects == null) {
                this.effects = new HashSet<Effect>();
            }
            this.effects.add(effect);
        }
        if (this.watchers != null) {
            final Iterator<VirtualZone> it = this.watchers.iterator();
            while (it.hasNext()) {
                it.next().addEffect(effect, temp);
            }
        }
        effect.setSurfaced(this.surfaced);
        try {
            effect.save();
        }
        catch (IOException iox) {
            VolaTile.logger.log(Level.INFO, iox.getMessage(), iox);
        }
    }
    
    int addCreature(final Creature creature, final float diffZ) throws NoSuchCreatureException, NoSuchPlayerException {
        if (this.inactive) {
            VolaTile.logger.log(Level.WARNING, "AT 1 adding " + creature.getName() + " who is at " + creature.getTileX() + ", " + creature.getTileY() + " to inactive tile " + this.tilex + "," + this.tiley, new Exception());
            VolaTile.logger.log(Level.WARNING, "The zone " + this.zone.id + " covers " + this.zone.startX + ", " + this.zone.startY + " to " + this.zone.endX + "," + this.zone.endY);
        }
        if (!creature.setNewTile(this, diffZ, false)) {
            return 0;
        }
        if (this.creatures == null) {
            this.creatures = new HashSet<Creature>();
        }
        for (final Creature c : this.creatures) {
            if (!c.isFriendlyKingdom(creature.getKingdomId())) {
                c.setStealth(false);
            }
        }
        this.creatures.add(creature);
        creature.setCurrentVillage(this.village);
        creature.calculateZoneBonus(this.tilex, this.tiley, this.surfaced);
        if (creature.isPlayer()) {
            try {
                final FaithZone z = Zones.getFaithZone(this.tilex, this.tiley, this.surfaced);
                if (z != null) {
                    creature.setCurrentDeity(z.getCurrentRuler());
                }
                else {
                    creature.setCurrentDeity(null);
                }
            }
            catch (NoSuchZoneException nsz) {
                VolaTile.logger.log(Level.WARNING, "No faith zone here? " + this.tilex + ", " + this.tiley + ", surf=" + this.surfaced);
            }
        }
        if (creature.getHighwayPathDestination().length() > 0 || creature.isWagoner()) {
            HighwayPos currentHighwayPos = null;
            if (creature.getBridgeId() != -10L) {
                final BridgePart bridgePart = Zones.getBridgePartFor(this.tilex, this.tiley, this.surfaced);
                if (bridgePart != null) {
                    currentHighwayPos = MethodsHighways.getHighwayPos(bridgePart);
                }
            }
            if (currentHighwayPos == null && creature.getFloorLevel() > 0) {
                final Floor floor = Zones.getFloor(this.tilex, this.tiley, this.surfaced, creature.getFloorLevel());
                if (floor != null) {
                    currentHighwayPos = MethodsHighways.getHighwayPos(floor);
                }
            }
            if (currentHighwayPos == null) {
                currentHighwayPos = MethodsHighways.getHighwayPos(this.tilex, this.tiley, this.surfaced);
            }
            if (currentHighwayPos != null) {
                Item waystone = this.getWaystone(currentHighwayPos);
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)1));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)(-128)));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)64));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)32));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)2));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)4));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)16));
                }
                if (waystone == null) {
                    waystone = this.getWaystone(MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)8));
                }
                if (waystone != null && creature.getLastWaystoneChecked() != waystone.getWurmId()) {
                    if (creature.isWagoner()) {
                        creature.setLastWaystoneChecked(waystone.getWurmId());
                    }
                    else {
                        final Node startNode = Routes.getNode(waystone.getWurmId());
                        final String goingto = creature.getHighwayPathDestination();
                        if (startNode.getVillage() == null || creature.currentVillage != null || !startNode.getVillage().getName().equalsIgnoreCase(goingto)) {
                            creature.setLastWaystoneChecked(waystone.getWurmId());
                            try {
                                final Village destinationVillage = Villages.getVillage(goingto);
                                HighwayFinder.queueHighwayFinding(creature, startNode, destinationVillage, (byte)0);
                            }
                            catch (NoSuchVillageException e) {
                                creature.getCommunicator().sendNormalServerMessage("Destination village (" + goingto + ") cannot be found.");
                            }
                        }
                    }
                }
            }
        }
        return this.creatures.size();
    }
    
    @Nullable
    private Item getWaystone(@Nullable final HighwayPos highwayPos) {
        if (highwayPos == null) {
            return null;
        }
        final Item marker = MethodsHighways.getMarker(highwayPos);
        if (marker != null && marker.getTemplateId() == 1112) {
            return marker;
        }
        return null;
    }
    
    public boolean removeCreature(final Creature creature) {
        if (this.creatures == null) {
            return false;
        }
        final boolean removed = this.creatures.remove(creature);
        if (this.creatures.isEmpty()) {
            this.creatures = null;
        }
        if (!removed) {
            return false;
        }
        final Door[] doorArr = this.getDoors();
        for (int d = 0; d < doorArr.length; ++d) {
            if (!doorArr[d].covers(creature.getPosX(), creature.getPosY(), creature.getPositionZ(), creature.getFloorLevel(), creature.followsGround())) {
                doorArr[d].removeCreature(creature);
            }
        }
        if (this.watchers != null) {
            for (final VirtualZone watchingZone : this.watchers) {
                watchingZone.removeCreature(creature);
            }
        }
        return true;
    }
    
    public boolean checkOpportunityAttacks(final Creature creature) {
        final Creature[] lTempCreatures = this.getCreatures();
        for (int x = 0; x < lTempCreatures.length; ++x) {
            if (lTempCreatures[x] != creature && !lTempCreatures[x].isMoving() && lTempCreatures[x].getAttitude(creature) == 2 && VirtualZone.isCreatureTurnedTowardsTarget(creature, lTempCreatures[x])) {
                return lTempCreatures[x].opportunityAttack(creature);
            }
        }
        return false;
    }
    
    public void makeInvisible(final Creature creature) {
        if (this.watchers != null) {
            for (final VirtualZone watchingZone : this.watchers) {
                watchingZone.makeInvisible(creature);
            }
        }
    }
    
    public void makeVisible(final Creature creature) throws NoSuchCreatureException, NoSuchPlayerException {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.addCreature(creature.getWurmId(), false);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void makeInvisible(final Item item) {
        if (this.watchers != null) {
            for (final VirtualZone watchingZone : this.watchers) {
                watchingZone.removeItem(item);
            }
        }
    }
    
    public void makeVisible(final Item item) {
        if (this.watchers != null) {
            for (final VirtualZone watchingZone : this.watchers) {
                watchingZone.addItem(item, this, -10L, true);
            }
        }
    }
    
    private VolaTile getCaveTile() {
        try {
            final Zone z = Zones.getZone(this.tilex, this.tiley, false);
            return z.getOrCreateTile(this.tilex, this.tiley);
        }
        catch (NoSuchZoneException ex) {
            VolaTile.logger.log(Level.WARNING, "No cave tile for " + this.tilex + ", " + this.tiley);
            return this;
        }
    }
    
    private VolaTile getSurfaceTile() {
        try {
            final Zone z = Zones.getZone(this.tilex, this.tiley, true);
            return z.getOrCreateTile(this.tilex, this.tiley);
        }
        catch (NoSuchZoneException ex) {
            VolaTile.logger.log(Level.WARNING, "No surface tile for " + this.tilex + ", " + this.tiley);
            return this;
        }
    }
    
    public void addItem(final Item item, final boolean moving, final boolean starting) {
        this.addItem(item, moving, -10L, starting);
    }
    
    public void addItem(final Item item, final boolean moving, final long creatureId, final boolean starting) {
        if (this.inactive) {
            VolaTile.logger.log(Level.WARNING, "adding " + item.getName() + " to inactive tile " + this.tilex + "," + this.tiley + " surf=" + this.surfaced + " itemsurf=" + item.isOnSurface(), new Exception());
            VolaTile.logger.log(Level.WARNING, "The zone " + this.zone.id + " covers " + this.zone.startX + ", " + this.zone.startY + " to " + this.zone.endX + "," + this.zone.endY);
        }
        if (item.hidden) {
            return;
        }
        if (this.isTransition && this.surfaced && !item.isVehicle()) {
            if (VolaTile.logger.isLoggable(Level.FINEST)) {
                VolaTile.logger.finest("Adding " + item.getName() + " to cave level instead.");
            }
            boolean stayOnSurface = false;
            if (Zones.getTextureForTile(this.tilex, this.tiley, 0) != Tiles.Tile.TILE_HOLE.id) {
                stayOnSurface = true;
            }
            if (!stayOnSurface) {
                this.getCaveTile().addItem(item, moving, starting);
                return;
            }
        }
        if (item.isTileAligned()) {
            item.setPosXY((this.tilex << 2) + 2, (this.tiley << 2) + 2);
            item.setOwnerId(-10L);
            if (item.isFence() && this.isOnSurface()) {
                int offz = 0;
                try {
                    offz = (int)((item.getPosZ() - Zones.calculateHeight(item.getPosX(), item.getPosY(), this.surfaced)) / 10.0f);
                }
                catch (NoSuchZoneException nsz) {
                    VolaTile.logger.log(Level.WARNING, "Dropping fence item outside zones.");
                }
                final float rot = Creature.normalizeAngle(item.getRotation());
                if (rot >= 45.0f && rot < 135.0f) {
                    final VolaTile next = Zones.getOrCreateTile(this.tilex + 1, this.tiley, this.surfaced);
                    next.addFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex + 1, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_DOWN, next.getZone().getId(), this.getLayer()));
                }
                else if (rot >= 135.0f && rot < 225.0f) {
                    final VolaTile next = Zones.getOrCreateTile(this.tilex, this.tiley + 1, this.surfaced);
                    next.addFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley + 1, offz, item, Tiles.TileBorderDirection.DIR_HORIZ, next.getZone().getId(), this.getLayer()));
                }
                else if (rot >= 225.0f && rot < 315.0f) {
                    this.addFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_DOWN, this.getZone().getId(), this.getLayer()));
                }
                else {
                    this.addFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_HORIZ, this.getZone().getId(), this.getLayer()));
                }
            }
        }
        else if (item.getTileX() != this.tilex || item.getTileY() != this.tiley) {
            this.putRandomOnTile(item);
            item.setOwnerId(-10L);
        }
        if (!this.surfaced && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(this.tilex, this.tiley)))) {
            if (!this.getSurfaceTile().isTransition) {
                this.getSurfaceTile().addItem(item, moving, creatureId, starting);
                VolaTile.logger.log(Level.INFO, "adding " + item.getName() + " in rock at " + this.tilex + ", " + this.tiley + " ");
            }
            return;
        }
        item.setZoneId(this.zone.getId(), this.surfaced);
        if (!starting && !item.getTemplate().hovers()) {
            item.updatePosZ(this);
        }
        if (this.vitems == null) {
            this.vitems = new VolaTileItems();
        }
        if (this.vitems.addItem(item, starting)) {
            if (item.getTemplateId() == 726) {
                Zones.addDuelRing(item);
            }
            if (!item.isDecoration()) {
                Item pile = this.vitems.getPileItem(item.getFloorLevel());
                if (this.vitems.checkIfCreatePileItem(item.getFloorLevel()) || pile != null) {
                    if (pile == null) {
                        pile = this.createPileItem(item, starting);
                        this.vitems.addPileItem(pile);
                    }
                    pile.insertItem(item, true);
                    final int data = pile.getData1();
                    if (data != -1 && item.getTemplateId() != data) {
                        pile.setData1(-1);
                        pile.setName(pile.getTemplate().getName());
                        final String modelname = pile.getTemplate().getModelName().replaceAll(" ", "") + "unknown.";
                        if (this.watchers != null) {
                            final Iterator<VirtualZone> it = this.watchers.iterator();
                            while (it.hasNext()) {
                                it.next().renameItem(pile, pile.getName(), modelname);
                            }
                        }
                    }
                }
                else if (this.watchers != null) {
                    boolean onGroundLevel = true;
                    if (item.getFloorLevel() > 0) {
                        onGroundLevel = false;
                    }
                    else if (this.getFloors(0, 0).length > 0) {
                        onGroundLevel = false;
                    }
                    for (final VirtualZone vz : this.getWatchers()) {
                        try {
                            if (vz.isVisible(item, this)) {
                                vz.addItem(item, this, creatureId, onGroundLevel);
                            }
                        }
                        catch (Exception e) {
                            VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    }
                }
            }
            else if (this.watchers != null) {
                boolean onGroundLevel2 = true;
                if (item.getFloorLevel() > 0) {
                    onGroundLevel2 = false;
                }
                else if (this.getFloors(0, 0).length > 0) {
                    onGroundLevel2 = false;
                }
                for (final VirtualZone vz2 : this.getWatchers()) {
                    try {
                        if (vz2.isVisible(item, this)) {
                            vz2.addItem(item, this, creatureId, onGroundLevel2);
                        }
                    }
                    catch (Exception e2) {
                        VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                    }
                }
            }
            if (item.isDomainItem()) {
                Zones.addAltar(item, moving);
            }
            if ((item.getTemplateId() == 1175 || item.getTemplateId() == 1239) && item.getAuxData() > 0) {
                Zones.addHive(item, moving);
            }
            if (item.getTemplateId() == 939 || item.isEnchantedTurret()) {
                Zones.addTurret(item, moving);
            }
            if (item.isEpicTargetItem()) {
                EpicTargetItems.addRitualTargetItem(item);
            }
            if (this.village != null && item.getTemplateId() == 757) {
                this.village.addBarrel(item);
            }
        }
        else {
            item.setZoneId(this.zone.getId(), this.surfaced);
            if (!item.deleted) {
                VolaTile.logger.log(Level.WARNING, "tile already contained item " + item.getName() + " (ID: " + item.getWurmId() + ") at " + this.tilex + ", " + this.tiley, new Exception());
            }
        }
    }
    
    public void updatePile(final Item pile) {
        this.checkIfRenamePileItem(pile);
    }
    
    protected void removeItem(final Item item, final boolean moving) {
        if (this.vitems != null) {
            if (!this.vitems.isEmpty()) {
                if (item.getTemplateId() == 726) {
                    Zones.removeDuelRing(item);
                }
                final Item pileItem = this.vitems.getPileItem(item.getFloorLevel());
                if (pileItem != null && item.getWurmId() == pileItem.getWurmId()) {
                    this.vitems.removePileItem(item.getFloorLevel());
                    if (this.vitems.isEmpty()) {
                        this.vitems.destroy(this);
                        this.vitems = null;
                    }
                }
                else if (this.vitems.removeItem(item)) {
                    this.vitems.destroy(this);
                    this.vitems = null;
                    if (pileItem != null) {
                        this.destroyPileItem(pileItem.getFloorLevel());
                    }
                }
                else if (pileItem != null && this.vitems.checkIfRemovePileItem(pileItem.getFloorLevel())) {
                    if (!moving) {
                        this.destroyPileItem(pileItem.getFloorLevel());
                    }
                }
                else if (!item.isDecoration() && pileItem != null) {
                    this.checkIfRenamePileItem(pileItem);
                }
                if (item.isDomainItem()) {
                    Zones.removeAltar(item, moving);
                }
                if (item.getTemplateId() == 1175 || item.getTemplateId() == 1239) {
                    Zones.removeHive(item, moving);
                }
                if (item.getTemplateId() == 939 || item.isEnchantedTurret()) {
                    Zones.removeTurret(item, moving);
                }
                if (item.isEpicTargetItem()) {
                    EpicTargetItems.removeRitualTargetItem(item);
                }
                if (item.isKingdomMarker() && item.getTemplateId() != 328) {
                    Kingdoms.destroyTower(item);
                }
                if (item.getTemplateId() == 521) {
                    this.zone.creatureSpawn = null;
                    --Zone.spawnPoints;
                }
                if (this.village != null && item.getTemplateId() == 757) {
                    this.village.removeBarrel(item);
                }
            }
            else {
                final Item pileItem = this.vitems.getPileItem(item.getFloorLevel());
                if (pileItem != null && item.getWurmId() == pileItem.getWurmId()) {
                    this.vitems.removePileItem(item.getFloorLevel());
                }
                else if (pileItem != null && this.vitems.checkIfRemovePileItem(item.getFloorLevel())) {
                    this.destroyPileItem(item.getFloorLevel());
                }
            }
        }
        if (item.isFence()) {
            int offz = 0;
            try {
                offz = (int)((item.getPosZ() - Zones.calculateHeight(item.getPosX(), item.getPosY(), item.isOnSurface())) / 10.0f);
            }
            catch (NoSuchZoneException nsz) {
                VolaTile.logger.log(Level.WARNING, "Dropping fence item outside zones.");
            }
            final float rot = Creature.normalizeAngle(item.getRotation());
            if (rot >= 45.0f && rot < 135.0f) {
                final VolaTile next = Zones.getOrCreateTile(this.tilex + 1, this.tiley, item.isOnSurface() || this.isTransition);
                next.removeFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex + 1, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_DOWN, next.getZone().getId(), Math.max(0, next.getLayer())));
            }
            else if (rot >= 135.0f && rot < 225.0f) {
                final VolaTile next = Zones.getOrCreateTile(this.tilex, this.tiley + 1, item.isOnSurface() || this.isTransition);
                next.removeFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley + 1, offz, item, Tiles.TileBorderDirection.DIR_HORIZ, next.getZone().getId(), Math.max(0, next.getLayer())));
            }
            else if (rot >= 225.0f && rot < 315.0f) {
                this.removeFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_DOWN, this.getZone().getId(), Math.max(0, this.getLayer())));
            }
            else {
                this.removeFence(new TempFence(StructureConstantsEnum.FENCE_SIEGEWALL, this.tilex, this.tiley, offz, item, Tiles.TileBorderDirection.DIR_HORIZ, this.getZone().getId(), Math.max(0, this.getLayer())));
            }
        }
        this.sendRemoveItem(item, moving);
        if (!moving) {
            item.setZoneId(-10, this.surfaced);
        }
    }
    
    private void checkIfRenamePileItem(final Item pileItem) {
        if (pileItem.getData1() == -1) {
            int itid = -1;
            byte material = 0;
            boolean multipleMaterials = false;
            for (final Item item : pileItem.getItems()) {
                if (!multipleMaterials && item.getMaterial() != material) {
                    if (material == 0) {
                        material = item.getMaterial();
                    }
                    else {
                        material = 0;
                        multipleMaterials = true;
                    }
                }
                if (itid == -1) {
                    itid = item.getTemplateId();
                }
                else {
                    if (itid != item.getTemplateId()) {
                        itid = -1;
                        break;
                    }
                    continue;
                }
            }
            if (itid != -1) {
                try {
                    String name = pileItem.getTemplate().getName();
                    pileItem.setData1(itid);
                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(itid);
                    final String tname = template.getName();
                    name = "Pile of " + template.sizeString + tname;
                    pileItem.setMaterial(material);
                    final StringBuilder build = new StringBuilder();
                    build.append(pileItem.getTemplate().getModelName());
                    build.append(tname);
                    build.append(".");
                    build.append(MaterialUtilities.getMaterialString(pileItem.getMaterial()));
                    final String modelname = build.toString().replaceAll(" ", "").trim();
                    pileItem.setName(name);
                    if (this.watchers != null) {
                        final Iterator<VirtualZone> it = this.watchers.iterator();
                        while (it.hasNext()) {
                            it.next().renameItem(pileItem, name, modelname);
                        }
                    }
                }
                catch (NoSuchTemplateException ex) {}
            }
        }
    }
    
    private void sendRemoveItem(final Item item, final boolean moving) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (!moving || !vz.isVisible(item, this)) {
                        vz.removeItem(item);
                    }
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public final void sendSetBridgeId(final Creature creature, final long bridgeId, final boolean sendToSelf) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (sendToSelf) {
                        if (creature.getWurmId() == vz.getWatcher().getWurmId()) {
                            vz.sendBridgeId(-1L, bridgeId);
                        }
                        else if (creature.isVisibleTo(vz.getWatcher())) {
                            vz.sendBridgeId(creature.getWurmId(), bridgeId);
                        }
                    }
                    else if (creature.getWurmId() != vz.getWatcher().getWurmId() && creature.isVisibleTo(vz.getWatcher())) {
                        vz.sendBridgeId(creature.getWurmId(), bridgeId);
                    }
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public final void sendSetBridgeId(final Item item, final long bridgeId) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (vz.isVisible(item, this)) {
                        vz.sendBridgeId(item.getWurmId(), bridgeId);
                    }
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void removeWall(final Wall wall, final boolean silent) {
        if (wall != null) {
            if (this.walls != null) {
                this.walls.remove(wall);
                if (this.walls.size() == 0) {
                    this.walls = null;
                }
            }
            if (this.watchers != null && !silent) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        vz.removeWall(this.structure.getWurmId(), wall);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    boolean removeEffect(final Effect effect) {
        boolean removed = false;
        if (this.effects != null && this.effects.contains(effect)) {
            this.effects.remove(effect);
            if (this.watchers != null) {
                final Iterator<VirtualZone> it = this.watchers.iterator();
                while (it.hasNext()) {
                    it.next().removeEffect(effect);
                }
            }
            if (this.effects.size() == 0) {
                this.effects = null;
            }
            removed = true;
        }
        return removed;
    }
    
    @Override
    public void creatureMoved(final long creatureId, final float diffX, final float diffY, final float diffZ, final int diffTileX, final int diffTileY) throws NoSuchCreatureException, NoSuchPlayerException {
        this.creatureMoved(creatureId, diffX, diffY, diffZ, diffTileX, diffTileY, false);
    }
    
    public void creatureMoved(final long creatureId, final float diffX, final float diffY, final float diffZ, final int diffTileX, final int diffTileY, final boolean passenger) throws NoSuchCreatureException, NoSuchPlayerException {
        final Creature creature = Server.getInstance().getCreature(creatureId);
        final int tileX = this.tilex + diffTileX;
        final int tileY = this.tiley + diffTileY;
        boolean changedLevel = false;
        if (diffTileX != 0 || diffTileY != 0) {
            if (!creature.isPlayer()) {
                final boolean following = creature.getLeader() != null || creature.isRidden() || creature.getHitched() != null;
                boolean godown = false;
                if (this.surfaced && following) {
                    if (creature.isRidden() || creature.getHitched() != null) {
                        if (creature.getHitched() != null) {
                            final Creature rider = Server.getInstance().getCreature(creature.getHitched().pilotId);
                            if (!rider.isOnSurface()) {
                                godown = true;
                            }
                        }
                        else {
                            try {
                                if (creature.getMountVehicle() != null) {
                                    final Creature rider = Server.getInstance().getCreature(creature.getMountVehicle().pilotId);
                                    if (!rider.isOnSurface()) {
                                        godown = true;
                                    }
                                }
                                else {
                                    VolaTile.logger.log(Level.WARNING, "Mount Vehicle is null for ridden " + creature.getWurmId());
                                }
                            }
                            catch (NoSuchCreatureException ex2) {}
                            catch (NoSuchPlayerException ex3) {}
                        }
                    }
                    else if (creature.getLeader() != null && !creature.getLeader().isOnSurface()) {
                        godown = true;
                    }
                }
                if (!this.surfaced) {
                    if (this.isTransition && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tileX, tileY)))) {
                        changedLevel = true;
                        creature.getStatus().setLayer(0);
                    }
                    else if (creature.getLeader() != null && creature.getLeader().isOnSurface() && !this.isTransition) {
                        changedLevel = true;
                        creature.getStatus().setLayer(0);
                    }
                }
                else if (Tiles.decodeType(Server.surfaceMesh.getTile(tileX, tileY)) == Tiles.Tile.TILE_HOLE.id || (godown && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tileX, tileY))))) {
                    changedLevel = true;
                    creature.getStatus().setLayer(-1);
                }
                final VolaTile nextTile = Zones.getTileOrNull(tileX, tileY, this.isOnSurface());
                if (nextTile != null) {
                    if (nextTile.getStructure() != null && creature.getBridgeId() != nextTile.getStructure().getWurmId()) {
                        if (nextTile.getBridgeParts().length > 0) {
                            for (final BridgePart bp : nextTile.getBridgeParts()) {
                                final int ht = Math.max(0, creature.getPosZDirts());
                                if (Math.abs(ht - bp.getHeightOffset()) < 25 && bp.hasAnExit()) {
                                    creature.setBridgeId(nextTile.structure.getWurmId());
                                }
                            }
                        }
                    }
                    else if (creature.getBridgeId() > 0L && (nextTile.getStructure() == null || nextTile.getStructure().getWurmId() != creature.getBridgeId())) {
                        boolean leave = true;
                        final BridgePart[] parts = this.getBridgeParts();
                        if (parts != null) {
                            for (final BridgePart bp2 : parts) {
                                if (bp2.isFinished()) {
                                    if (bp2.getDir() == 0 || bp2.getDir() == 4) {
                                        if (this.getTileY() == nextTile.getTileY()) {
                                            leave = false;
                                        }
                                    }
                                    else if (this.getTileX() == nextTile.getTileX()) {
                                        leave = false;
                                    }
                                }
                            }
                        }
                        if (leave) {
                            creature.setBridgeId(-10L);
                        }
                    }
                }
            }
            if (!changedLevel && this.zone.covers(tileX, tileY)) {
                final VolaTile newTile = this.zone.getOrCreateTile(tileX, tileY);
                newTile.addCreature(creature, diffZ);
            }
            else {
                try {
                    this.zone.removeCreature(creature, changedLevel, false);
                    final Zone newZone = Zones.getZone(tileX, tileY, creature.isOnSurface());
                    newZone.addCreature(creature.getWurmId());
                }
                catch (NoSuchZoneException sex) {
                    VolaTile.logger.log(Level.INFO, sex.getMessage() + " this tile at " + this.tilex + "," + this.tiley + ", diff=" + diffTileX + ", " + diffTileY, sex);
                }
            }
            if (!passenger) {
                this.zone.createTrack(creature, this.tilex, this.tiley, diffTileX, diffTileY);
            }
        }
        if (this.isTransition) {
            if (!passenger) {
                updateNeighbourTileDoors(creature, this.tilex, this.tiley);
            }
        }
        else if (!passenger) {
            this.doorCreatureMoved(creature, diffTileX, diffTileY);
        }
        if (!changedLevel && !passenger) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (diffZ * 10.0f <= 127.0f && diffZ * 10.0f >= -128.0f) {
                        if (vz.creatureMoved(creatureId, diffX, diffY, diffZ, diffTileX, diffTileY)) {
                            VolaTile.logger.log(Level.INFO, "Forcibly removing watcher " + vz);
                            this.removeWatcher(vz);
                        }
                    }
                    else {
                        if (VolaTile.logger.isLoggable(Level.FINEST)) {
                            VolaTile.logger.finest(creature.getName() + " moved more than byte max (" + 127 + ") or min (" + -128 + ") in z: " + diffZ + " at " + this.tilex + ", " + this.tiley + " surfaced=" + this.isOnSurface());
                        }
                        this.makeInvisible(creature);
                        this.makeVisible(creature);
                    }
                }
                catch (Exception ex) {
                    VolaTile.logger.log(Level.WARNING, "Exception when " + creature.getName() + " moved at " + this.tilex + ", " + this.tiley + " tile surf=" + this.isOnSurface() + " cret onsurf=" + creature.isOnSurface() + ": ", ex);
                }
            }
        }
        if (creature instanceof Player && !passenger && creature.getBridgeId() != -10L && this.getStructure() != null && this.getStructure().isTypeBridge()) {
            this.getStructure().setWalkedOnBridge(System.currentTimeMillis());
        }
    }
    
    private static void updateNeighbourTileDoors(final Creature creature, final int tilex, final int tiley) {
        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley - 1)))) {
            final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley - 1, true);
            newTile.getSurfaceTile().doorCreatureMoved(creature, 0, 0);
        }
        else {
            final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley - 1, false);
            newTile.getCaveTile().doorCreatureMoved(creature, 0, 0);
        }
        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex + 1, tiley)))) {
            final VolaTile newTile = Zones.getOrCreateTile(tilex + 1, tiley, true);
            newTile.getSurfaceTile().doorCreatureMoved(creature, 0, 0);
        }
        else {
            final VolaTile newTile = Zones.getOrCreateTile(tilex + 1, tiley, false);
            newTile.getCaveTile().doorCreatureMoved(creature, 0, 0);
        }
        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley + 1)))) {
            final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley + 1, true);
            newTile.getSurfaceTile().doorCreatureMoved(creature, 0, 0);
        }
        else {
            final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley + 1, false);
            newTile.getCaveTile().doorCreatureMoved(creature, 0, 0);
        }
        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex - 1, tiley)))) {
            final VolaTile newTile = Zones.getOrCreateTile(tilex - 1, tiley, true);
            newTile.getSurfaceTile().doorCreatureMoved(creature, 0, 0);
        }
        else {
            final VolaTile newTile = Zones.getOrCreateTile(tilex - 1, tiley, false);
            newTile.getCaveTile().doorCreatureMoved(creature, 0, 0);
        }
    }
    
    private void doorCreatureMoved(final Creature creature, final int diffTileX, final int diffTileY) {
        if (this.doors != null) {
            for (final Door door : this.doors) {
                door.creatureMoved(creature, diffTileX, diffTileY);
            }
        }
    }
    
    @Nonnull
    public Creature[] getCreatures() {
        if (this.creatures != null) {
            return this.creatures.toArray(new Creature[this.creatures.size()]);
        }
        return VolaTile.emptyCreatures;
    }
    
    public Item[] getItems() {
        if (this.vitems != null) {
            return this.vitems.getAllItemsAsArray();
        }
        return VolaTile.emptyItems;
    }
    
    final Effect[] getEffects() {
        if (this.effects != null) {
            return this.effects.toArray(new Effect[this.effects.size()]);
        }
        return VolaTile.emptyEffects;
    }
    
    public VirtualZone[] getWatchers() {
        if (this.watchers != null) {
            return this.watchers.toArray(new VirtualZone[this.watchers.size()]);
        }
        return VolaTile.emptyWatchers;
    }
    
    public final int getMaxFloorLevel() {
        if (!this.surfaced || this.isTransition) {
            return 3;
        }
        int toRet = 0;
        if (this.floors != null) {
            toRet = 1;
            for (final Floor f : this.floors) {
                if (f.getFloorLevel() > toRet) {
                    toRet = f.getFloorLevel();
                }
            }
        }
        if (this.bridgeParts != null) {
            toRet = 1;
            for (final BridgePart b : this.bridgeParts) {
                if (b.getFloorLevel() > toRet) {
                    toRet = b.getFloorLevel();
                }
            }
        }
        return toRet;
    }
    
    public final int getDropFloorLevel(final int maxFloorLevel) {
        int toRet = 0;
        if (this.floors != null) {
            for (final Floor f : this.floors) {
                if (f.isSolid()) {
                    if (f.getFloorLevel() == maxFloorLevel) {
                        return maxFloorLevel;
                    }
                    if (f.getFloorLevel() >= maxFloorLevel || f.getFloorLevel() <= toRet) {
                        continue;
                    }
                    toRet = f.getFloorLevel();
                }
            }
        }
        return toRet;
    }
    
    void sendRemoveItem(final Item item) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.removeItem(item);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void change() {
        this.checkTransition();
        Item stumpToDestroy = null;
        if (this.vitems != null) {
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.getTileX() == this.tilex && item.getTileY() == this.tiley && item.getBridgeId() == -10L) {
                    boolean ok = true;
                    if (item.isVehicle()) {
                        final Vehicle vehic = Vehicles.getVehicle(item);
                        if (vehic.getHitched().length > 0) {
                            ok = false;
                        }
                        else {
                            for (final Seat seat : vehic.getSeats()) {
                                if (seat.occupant > 0L) {
                                    ok = false;
                                }
                            }
                        }
                    }
                    if (item.getTemplateId() == 731) {
                        stumpToDestroy = item;
                    }
                    else {
                        final int oldFloorLevel = item.getFloorLevel();
                        item.updatePosZ(this);
                        final Item pileItem = this.vitems.getPileItem(item.getFloorLevel());
                        if (oldFloorLevel != item.getFloorLevel()) {
                            this.vitems.moveToNewFloorLevel(item, oldFloorLevel);
                            VolaTile.logger.log(Level.INFO, item.getName() + " moving from " + oldFloorLevel + " fl=" + item.getFloorLevel());
                        }
                        if (ok && (pileItem == null || item.isDecoration())) {
                            boolean onGroundLevel = true;
                            if (item.getFloorLevel() > 0) {
                                onGroundLevel = false;
                            }
                            else if (this.getFloors(0, 0).length > 0) {
                                onGroundLevel = false;
                            }
                            for (final VirtualZone vz : this.getWatchers()) {
                                try {
                                    vz.removeItem(item);
                                    if (vz.isVisible(item, this)) {
                                        vz.addItem(item, this, onGroundLevel);
                                    }
                                }
                                catch (Exception e) {
                                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.vitems != null) {
            for (final Item pileItem2 : this.vitems.getPileItems()) {
                if (pileItem2.getBridgeId() == -10L) {
                    final int oldFloorLevel2 = pileItem2.getFloorLevel();
                    pileItem2.updatePosZ(this);
                    boolean destroy = false;
                    if (pileItem2.getFloorLevel() != oldFloorLevel2) {
                        destroy = this.vitems.movePileItemToNewFloorLevel(pileItem2, oldFloorLevel2);
                    }
                    if (!destroy) {
                        boolean onGroundLevel2 = true;
                        if (pileItem2.getFloorLevel() > 0) {
                            onGroundLevel2 = false;
                        }
                        else if (this.getFloors(0, 0).length > 0) {
                            onGroundLevel2 = false;
                        }
                        for (final VirtualZone vz2 : this.getWatchers()) {
                            try {
                                vz2.removeItem(pileItem2);
                                if (vz2.isVisible(pileItem2, this)) {
                                    vz2.addItem(pileItem2, this, onGroundLevel2);
                                }
                            }
                            catch (Exception e2) {
                                VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                            }
                        }
                    }
                    else {
                        this.destroyPileItem(pileItem2);
                    }
                }
            }
        }
        if (this.effects != null) {
            for (final Effect effect : this.effects) {
                if (effect.getTileX() == this.tilex && effect.getTileY() == this.tiley) {
                    try {
                        if (this.structure == null) {
                            if (this.isTransition) {
                                effect.setPosZ(Zones.calculateHeight(effect.getPosX(), effect.getPosY(), false));
                            }
                            else {
                                final long owner = effect.getOwner();
                                long bridgeId = -10L;
                                if (WurmId.getType(owner) == 2) {
                                    try {
                                        final Item i = Items.getItem(owner);
                                        bridgeId = i.onBridge();
                                    }
                                    catch (NoSuchItemException ex) {}
                                }
                                else if (WurmId.getType(owner) == 1) {
                                    try {
                                        final Creature c = Creatures.getInstance().getCreature(owner);
                                        bridgeId = c.getBridgeId();
                                    }
                                    catch (NoSuchCreatureException ex2) {}
                                }
                                else if (WurmId.getType(owner) == 0) {
                                    try {
                                        final Player p = Players.getInstance().getPlayer(owner);
                                        bridgeId = p.getBridgeId();
                                    }
                                    catch (NoSuchPlayerException ex3) {}
                                }
                                final float height = Zones.calculatePosZ(effect.getPosX(), effect.getPosY(), this, this.surfaced, false, effect.getPosZ(), null, bridgeId);
                                effect.setPosZ(height);
                            }
                        }
                    }
                    catch (NoSuchZoneException nsz) {
                        VolaTile.logger.log(Level.WARNING, effect.getId() + " moved out of zone.");
                    }
                    for (final VirtualZone vz3 : this.getWatchers()) {
                        try {
                            vz3.removeEffect(effect);
                            vz3.addEffect(effect, false);
                        }
                        catch (Exception e3) {
                            VolaTile.logger.log(Level.WARNING, e3.getMessage(), e3);
                        }
                    }
                }
            }
        }
        if (this.creatures != null) {
            for (final Creature creature : this.creatures) {
                if (creature.getBridgeId() == -10L && !(creature instanceof Player)) {
                    if (creature.isSubmerged()) {
                        creature.submerge();
                    }
                    else {
                        if (creature.getVehicle() >= 0L) {
                            continue;
                        }
                        final float oldPosZ = creature.getStatus().getPositionZ();
                        float newPosZ = 1.0f;
                        boolean surf = this.surfaced;
                        if (this.isTransition) {
                            surf = false;
                        }
                        newPosZ = Zones.calculatePosZ(creature.getPosX(), creature.getPosY(), this, surf, false, creature.getPositionZ(), null, creature.getBridgeId());
                        creature.setPositionZ(Math.max(-1.25f, newPosZ));
                        try {
                            creature.savePosition(this.zone.id);
                        }
                        catch (Exception iox) {
                            VolaTile.logger.log(Level.WARNING, creature.getName() + ": " + iox.getMessage(), iox);
                        }
                        for (final VirtualZone vz4 : this.getWatchers()) {
                            try {
                                vz4.creatureMoved(creature.getWurmId(), 0.0f, 0.0f, newPosZ - oldPosZ, 0, 0);
                            }
                            catch (NoSuchCreatureException nsc) {
                                VolaTile.logger.log(Level.INFO, "Creature not found when changing height of tile.", nsc);
                            }
                            catch (NoSuchPlayerException nsp) {
                                VolaTile.logger.log(Level.INFO, "Player not found when changing height of tile.", nsp);
                            }
                            catch (Exception e4) {
                                VolaTile.logger.log(Level.WARNING, e4.getMessage(), e4);
                            }
                        }
                    }
                }
            }
        }
        if (stumpToDestroy != null) {
            Items.destroyItem(stumpToDestroy.getWurmId());
        }
        this.checkIsLava();
    }
    
    public Wall[] getWalls() {
        if (this.walls != null) {
            return this.walls.toArray(new Wall[this.walls.size()]);
        }
        return VolaTile.emptyWalls;
    }
    
    public BridgePart[] getBridgeParts() {
        if (this.bridgeParts != null) {
            return this.bridgeParts.toArray(new BridgePart[this.bridgeParts.size()]);
        }
        return VolaTile.emptyBridgeParts;
    }
    
    public final Set<StructureSupport> getAllSupport() {
        if (this.walls == null && this.fences == null && this.floors == null) {
            return VolaTile.emptySupports;
        }
        final Set<StructureSupport> toReturn = new HashSet<StructureSupport>();
        if (this.walls != null) {
            for (final Wall w : this.walls) {
                toReturn.add(w);
            }
        }
        if (this.fences != null) {
            toReturn.addAll(this.fences.values());
        }
        if (this.floors != null) {
            toReturn.addAll(this.floors);
        }
        return toReturn;
    }
    
    public Wall[] getWallsForLevel(final int floorLevel) {
        if (this.walls != null) {
            final Set<Wall> wallsSet = new HashSet<Wall>();
            for (final Wall w : this.walls) {
                if (w.getFloorLevel() == floorLevel) {
                    wallsSet.add(w);
                }
            }
            return wallsSet.toArray(new Wall[wallsSet.size()]);
        }
        return VolaTile.emptyWalls;
    }
    
    public Wall[] getExteriorWalls() {
        if (this.walls != null) {
            final Set<Wall> wallsSet = new HashSet<Wall>();
            for (final Wall w : this.walls) {
                if (!w.isIndoor()) {
                    wallsSet.add(w);
                }
            }
            return wallsSet.toArray(new Wall[wallsSet.size()]);
        }
        return VolaTile.emptyWalls;
    }
    
    Wall getWall(final long wallId) throws NoSuchWallException {
        if (this.walls != null) {
            for (final Wall wall : this.walls) {
                if (wall.getId() == wallId) {
                    return wall;
                }
            }
        }
        throw new NoSuchWallException("There are no walls on this tile so cannot find wallid: " + wallId);
    }
    
    Wall getWall(final int startX, final int startY, final int endX, final int endY, final boolean horizontal) {
        if (this.walls != null) {
            for (final Wall wall : this.walls) {
                if (wall.getStartX() == startX && wall.getStartY() == startY && wall.getEndX() == endX && wall.getEndY() == endY && wall.isHorizontal() == horizontal) {
                    return wall;
                }
            }
        }
        return null;
    }
    
    public Structure getStructure() {
        return this.structure;
    }
    
    public void deleteStructure(final long wurmStructureId) {
        if (this.structure == null) {
            return;
        }
        if (this.structure.getWurmId() != wurmStructureId) {
            VolaTile.logger.log(Level.WARNING, "Tried to delete structure " + wurmStructureId + " from VolaTile [" + this.tilex + "," + this.tiley + "] but it was structure " + this.structure.getWurmId() + " so nothing was deleted.");
            return;
        }
        if (this.walls != null) {
            final Iterator<Wall> it = this.walls.iterator();
            while (it.hasNext()) {
                final Wall wall = it.next();
                if (wall.getStructureId() == wurmStructureId) {
                    if (wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR || wall.isArched()) {
                        final Door[] alld = this.getDoors();
                        for (int x = 0; x < alld.length; ++x) {
                            try {
                                if (alld[x].getWall() == wall) {
                                    alld[x].removeFromTiles();
                                    alld[x].delete();
                                }
                            }
                            catch (NoSuchWallException nsw) {
                                VolaTile.logger.log(Level.WARNING, nsw.getMessage(), nsw);
                            }
                        }
                    }
                    wall.delete();
                    it.remove();
                }
            }
        }
        if (this.floors != null) {
            final Iterator<Floor> it2 = this.floors.iterator();
            while (it2.hasNext()) {
                final Floor floor = it2.next();
                if (floor.getStructureId() == wurmStructureId) {
                    floor.delete();
                    it2.remove();
                    if (!floor.isStair()) {
                        continue;
                    }
                    Stairs.removeStair(this.hashCode(), floor.getFloorLevel());
                }
            }
        }
        if (this.bridgeParts != null) {
            final Iterator<BridgePart> it3 = this.bridgeParts.iterator();
            while (it3.hasNext()) {
                final BridgePart bridgepart = it3.next();
                if (bridgepart.getStructureId() == wurmStructureId) {
                    bridgepart.delete();
                    it3.remove();
                }
            }
        }
        if (this.fences != null) {
            for (final Fence fence : this.getFences()) {
                if (fence.getFloorLevel() > 0) {
                    fence.destroy();
                }
            }
        }
        final VolaTile tw = Zones.getTileOrNull(this.getTileX() + 1, this.getTileY(), this.isOnSurface());
        if (tw != null && tw.getStructure() == null) {
            for (final Fence fence2 : tw.getFences()) {
                if (fence2.getFloorLevel() > 0) {
                    fence2.destroy();
                }
            }
        }
        final VolaTile ts = Zones.getTileOrNull(this.getTileX(), this.getTileY() + 1, this.isOnSurface());
        if (ts != null && ts.getStructure() == null) {
            for (final Fence fence3 : ts.getFences()) {
                if (fence3.getFloorLevel() > 0) {
                    fence3.destroy();
                }
            }
        }
        if (this.watchers != null) {
            VolaTile.logger.log(Level.INFO, "deleteStructure " + wurmStructureId + " (Watchers  " + this.watchers.size() + ")");
            for (final VirtualZone lZone : this.watchers) {
                lZone.deleteStructure(this.structure);
            }
        }
        if (this.vitems != null) {
            for (final Item pileItem : this.vitems.getPileItems()) {
                if (pileItem.getFloorLevel() > 0) {
                    final float pileHeight = pileItem.getPosZ();
                    float tileHeight = 0.0f;
                    try {
                        tileHeight = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface());
                    }
                    catch (NoSuchZoneException ex) {}
                    if (tileHeight != pileHeight) {
                        this.destroyPileItem(pileItem.getFloorLevel());
                    }
                }
                else {
                    pileItem.updatePosZ(this);
                    for (final VirtualZone vz : this.getWatchers()) {
                        try {
                            if (vz.isVisible(pileItem, this)) {
                                vz.removeItem(pileItem);
                                vz.addItem(pileItem, this, true);
                            }
                        }
                        catch (Exception e) {
                            VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    }
                }
            }
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.getParentId() == -10L) {
                    item.updatePosZ(this);
                    item.updateIfGroundItem();
                    item.setOnBridge(-10L);
                }
            }
        }
        if (this.creatures != null) {
            for (final Creature c : this.creatures) {
                if (!c.isPlayer()) {
                    final float oldposz = c.getPositionZ();
                    final float newPosz = c.calculatePosZ();
                    final float diffz = newPosz - oldposz;
                    c.setPositionZ(newPosz);
                    c.moved(0.0f, 0.0f, diffz, 0, 0);
                }
                else {
                    c.getCommunicator().setGroundOffset(0, true);
                }
                c.setBridgeId(-10L);
            }
        }
        this.structure = null;
        if (this.walls != null) {
            for (final Wall wall2 : this.walls) {
                final long sid = wall2.getStructureId();
                try {
                    final Structure struct = Structures.getStructure(sid);
                    (this.structure = struct).addBuildTile(this, false);
                    break;
                }
                catch (NoSuchStructureException sns) {
                    VolaTile.logger.log(Level.WARNING, sns.getMessage(), " for wall " + wall2);
                }
                catch (NoSuchZoneException nsz) {
                    VolaTile.logger.log(Level.INFO, "Out of bounds?: " + nsz.getMessage(), nsz);
                }
            }
        }
    }
    
    private void updateStructureForZone(final VirtualZone vzone, final Structure _structure, final int aTilex, final int aTiley) {
        vzone.sendStructureWalls(_structure);
    }
    
    public void addStructure(final Structure _structure) {
        if (this.structure == null && this.watchers != null) {
            for (final VirtualZone vzone : this.watchers) {
                this.updateStructureForZone(vzone, _structure, this.tilex, this.tiley);
            }
        }
        this.structure = _structure;
    }
    
    public void addBridge(final Structure bridge) {
        if (this.structure == null && this.watchers != null) {
            for (final VirtualZone vzone : this.watchers) {
                vzone.addStructure(bridge);
            }
        }
        this.structure = bridge;
    }
    
    public void addBuildMarker(final Structure _structure) {
        if (this.structure == null && this.watchers != null) {
            for (final VirtualZone vzone : this.watchers) {
                vzone.addBuildMarker(_structure, this.tilex, this.tiley);
            }
        }
        this.structure = _structure;
    }
    
    public void setStructureAtLoad(final Structure _structure) {
        this.structure = _structure;
    }
    
    public void removeBuildMarker(final Structure _structure, final int _tilex, final int _tiley) {
        if (this.structure != null) {
            if (this.watchers != null) {
                for (final VirtualZone vzone : this.watchers) {
                    vzone.removeBuildMarker(_structure, _tilex, _tiley);
                }
            }
            this.structure = null;
        }
        else {
            VolaTile.logger.log(Level.INFO, "Hmm tried to remove buildmarker from a tile that didn't contain it.");
        }
    }
    
    public void finalizeBuildPlan(final long oldStructureId, final long newStructureId) {
        if (this.structure != null && this.watchers != null) {
            for (final VirtualZone lZone : this.watchers) {
                lZone.finalizeBuildPlan(oldStructureId, newStructureId);
            }
        }
    }
    
    public void addWall(final StructureTypeEnum type, final int x1, final int y1, final int x2, final int y2, final float qualityLevel, final long structureId, final boolean isIndoor) {
        if (VolaTile.logger.isLoggable(Level.FINEST)) {
            VolaTile.logger.finest("StructureID: " + structureId + " adding wall at " + x1 + "-" + y1 + "," + x2 + "-" + y2 + ", QL: " + qualityLevel);
        }
        final Wall inside = new DbWall(type, this.tilex, this.tiley, x1, y1, x2, y2, qualityLevel, structureId, StructureMaterialEnum.WOOD, isIndoor, 0, this.getLayer());
        this.addWall(inside);
        this.updateWall(inside);
    }
    
    public void addWall(final Wall wall) {
        if (this.walls == null) {
            this.walls = new HashSet<Wall>();
        }
        boolean removedOneWall = false;
        for (final Wall w : this.walls) {
            removedOneWall = false;
            if (wall.heightOffset == 0 && w.heightOffset == 0 && wall.x1 == w.x1 && wall.x2 == w.x2 && wall.y1 == w.y1 && wall.y2 == w.y2) {
                if (wall.getType().value <= w.getType().value) {
                    removedOneWall = true;
                    break;
                }
                if (w.getType().value >= wall.getType().value) {
                    continue;
                }
                VolaTile.toRemove.add(w);
            }
        }
        if (!removedOneWall) {
            this.walls.add(wall);
        }
        if (removedOneWall) {
            VolaTile.logger.log(Level.INFO, "Not adding wall at " + wall.getTileX() + ", " + wall.getTileY() + ", structure: " + this.structure);
        }
        for (final Wall torem : VolaTile.toRemove) {
            VolaTile.logger.log(Level.INFO, "Deleting wall at " + torem.getTileX() + ", " + torem.getTileY() + ", structure: " + this.structure);
            torem.delete();
        }
        VolaTile.toRemove.clear();
    }
    
    public void updateFloor(final Floor floor) {
        if (this.structure != null) {
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        vz.updateFloor(this.structure.getWurmId(), floor);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            try {
                floor.save();
            }
            catch (IOException iox) {
                VolaTile.logger.log(Level.WARNING, "Failed to save structure floor: " + floor.getId() + '.', iox);
            }
            if (floor.isFinished() && this.vitems != null) {
                for (final Item item : this.vitems.getAllItemsAsArray()) {
                    item.updatePosZ(this);
                    item.updateIfGroundItem();
                }
            }
            if (this.vitems != null) {
                for (final Item pile : this.vitems.getPileItems()) {
                    pile.updatePosZ(this);
                    for (final VirtualZone vz2 : this.getWatchers()) {
                        try {
                            if (vz2.isVisible(pile, this)) {
                                vz2.removeItem(pile);
                                vz2.addItem(pile, this, true);
                            }
                        }
                        catch (Exception e2) {
                            VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                        }
                    }
                }
            }
        }
    }
    
    public void updateBridgePart(final BridgePart bridgePart) {
        if (!this.isOnSurface() && !Features.Feature.CAVE_BRIDGES.isEnabled()) {
            this.getSurfaceTile().updateBridgePart(bridgePart);
        }
        else if (this.structure != null) {
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        vz.updateBridgePart(this.structure.getWurmId(), bridgePart);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            try {
                bridgePart.save();
            }
            catch (IOException iox) {
                VolaTile.logger.log(Level.WARNING, "Failed to save structure bridge part: " + bridgePart.getId() + '.', iox);
            }
            if (bridgePart.getState() != BridgeConstants.BridgeState.COMPLETED.getCode()) {
                if (this.vitems != null) {
                    for (final Item item : this.vitems.getAllItemsAsArray()) {
                        if (item.onBridge() == this.structure.getWurmId()) {
                            item.setOnBridge(-10L);
                            for (final VirtualZone vz2 : this.getWatchers()) {
                                try {
                                    if (item.getParentId() == -10L && vz2.isVisible(item, this)) {
                                        vz2.removeItem(item);
                                        item.setPosZ(-3000.0f);
                                        vz2.addItem(item, this, true);
                                    }
                                }
                                catch (Exception e2) {
                                    VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                                }
                            }
                        }
                    }
                    for (final Item pile : this.vitems.getPileItems()) {
                        if (pile.onBridge() == this.structure.getWurmId()) {
                            pile.setOnBridge(-10L);
                            for (final VirtualZone vz2 : this.getWatchers()) {
                                try {
                                    if (vz2.isVisible(pile, this)) {
                                        pile.setPosZ(-3000.0f);
                                        vz2.removeItem(pile);
                                        vz2.addItem(pile, this, true);
                                    }
                                }
                                catch (Exception e2) {
                                    VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                                }
                            }
                        }
                    }
                }
                if (this.creatures != null) {
                    for (final Creature c : this.creatures) {
                        if (c.getBridgeId() == this.structure.getWurmId()) {
                            c.setBridgeId(-10L);
                            if (c.isPlayer()) {
                                continue;
                            }
                            final float oldposz = c.getPositionZ();
                            final float newPosz = c.calculatePosZ();
                            final float diffz = newPosz - oldposz;
                            c.setPositionZ(newPosz);
                            c.moved(0.0f, 0.0f, diffz, 0, 0);
                        }
                    }
                }
            }
        }
    }
    
    public void updateWall(final Wall wall) {
        if (this.structure != null) {
            if (this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        vz.updateWall(this.structure.getWurmId(), wall);
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            if (this.structure.isFinalized()) {
                try {
                    wall.save();
                }
                catch (IOException iox) {
                    VolaTile.logger.log(Level.WARNING, "Failed to save structure wall: " + wall.getId() + '.', iox);
                }
            }
        }
    }
    
    void linkTo(final VirtualZone aZone, final boolean aRemove) {
        this.linkStructureToZone(aZone, aRemove);
        this.linkFencesToZone(aZone, aRemove);
        this.linkDoorsToZone(aZone, aRemove);
        this.linkMineDoorsToZone(aZone, aRemove);
        this.linkCreaturesToZone(aZone, aRemove);
        this.linkItemsToZone(aZone, aRemove);
        this.linkPileToZone(aZone, aRemove);
        this.linkEffectsToZone(aZone, aRemove);
        this.linkAreaEffectsToZone(aZone, aRemove);
    }
    
    private void linkAreaEffectsToZone(final VirtualZone aZone, final boolean aRemove) {
        if (aZone.getWatcher().isPlayer()) {
            final AreaSpellEffect ae = this.getAreaEffect();
            if (ae != null && !aRemove && aZone.covers(this.tilex, this.tiley)) {
                aZone.addAreaSpellEffect(ae, true);
            }
            else if (ae != null) {
                aZone.removeAreaSpellEffect(ae);
            }
        }
    }
    
    private void linkDoorsToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.doors != null) {
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                for (final Door door : this.doors) {
                    aZone.addDoor(door);
                }
            }
            else {
                for (final Door door : this.doors) {
                    aZone.removeDoor(door);
                }
            }
        }
    }
    
    private void linkMineDoorsToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.mineDoors != null) {
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                for (final MineDoorPermission door : this.mineDoors) {
                    aZone.addMineDoor(door);
                }
            }
            else {
                for (final MineDoorPermission door : this.mineDoors) {
                    aZone.removeMineDoor(door);
                }
            }
        }
    }
    
    private void linkFencesToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.fences != null) {
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                for (final Fence f : this.getFences()) {
                    aZone.addFence(f);
                }
            }
            else {
                for (final Fence f : this.getFences()) {
                    aZone.removeFence(f);
                }
            }
        }
    }
    
    protected void linkCreaturesToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.creatures != null) {
            final Creature[] crets = this.getCreatures();
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                for (int x = 0; x < crets.length; ++x) {
                    if (!crets[x].isDead()) {
                        try {
                            aZone.addCreature(crets[x].getWurmId(), false);
                        }
                        catch (NoSuchCreatureException cnf) {
                            this.creatures.remove(crets[x]);
                            VolaTile.logger.log(Level.INFO, crets[x].getName() + "," + cnf.getMessage(), cnf);
                        }
                        catch (NoSuchPlayerException nsp) {
                            this.creatures.remove(crets[x]);
                            VolaTile.logger.log(Level.INFO, crets[x].getName() + "," + nsp.getMessage(), nsp);
                        }
                    }
                }
            }
            else {
                for (int x = 0; x < crets.length; ++x) {
                    try {
                        aZone.deleteCreature(crets[x], true);
                    }
                    catch (NoSuchPlayerException nsp) {
                        VolaTile.logger.log(Level.INFO, crets[x].getName() + "," + nsp.getMessage(), nsp);
                    }
                    catch (NoSuchCreatureException nsc) {
                        VolaTile.logger.log(Level.INFO, crets[x].getName() + "," + nsc.getMessage(), nsc);
                    }
                }
            }
        }
    }
    
    private void linkPileToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.vitems != null) {
            for (final Item pileItem : this.vitems.getPileItems()) {
                if (pileItem != null) {
                    if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                        if (aZone.isVisible(pileItem, this)) {
                            boolean onGroundLevel = true;
                            if (pileItem.getFloorLevel() > 0) {
                                onGroundLevel = false;
                            }
                            else if (this.getFloors(0, 0).length > 0) {
                                onGroundLevel = false;
                            }
                            aZone.addItem(pileItem, this, onGroundLevel);
                        }
                        else {
                            aZone.removeItem(pileItem);
                        }
                    }
                    else {
                        aZone.removeItem(pileItem);
                    }
                }
            }
        }
    }
    
    public byte getKingdom() {
        return Zones.getKingdom(this.tilex, this.tiley);
    }
    
    private void linkItemsToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.vitems != null) {
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                final Item[] lTempItemsLink = this.vitems.getAllItemsAsArray();
                for (int x = 0; x < lTempItemsLink.length; ++x) {
                    final Item pileItem = this.vitems.getPileItem(lTempItemsLink[x].getFloorLevel());
                    if (pileItem == null || lTempItemsLink[x].isDecoration()) {
                        if (aZone.isVisible(lTempItemsLink[x], this)) {
                            boolean onGroundLevel = true;
                            if (lTempItemsLink[x].getFloorLevel() > 0) {
                                onGroundLevel = false;
                            }
                            else if (this.getFloors(0, 0).length > 0) {
                                onGroundLevel = false;
                            }
                            if (!aZone.addItem(lTempItemsLink[x], this, onGroundLevel)) {
                                try {
                                    Items.getItem(lTempItemsLink[x].getWurmId());
                                    this.removeItem(lTempItemsLink[x], false);
                                    final Zone z = Zones.getZone(lTempItemsLink[x].getTileX(), lTempItemsLink[x].getTileY(), this.isOnSurface());
                                    z.addItem(lTempItemsLink[x]);
                                    VolaTile.logger.log(Level.INFO, this.tilex + ", " + this.tiley + " removing " + lTempItemsLink[x].getName() + " with id " + lTempItemsLink[x].getWurmId() + " and added it to " + lTempItemsLink[x].getTileX() + "," + lTempItemsLink[x].getTileY() + " where it belongs.");
                                }
                                catch (NoSuchItemException nsi) {
                                    VolaTile.logger.log(Level.INFO, this.tilex + ", " + this.tiley + " removing " + lTempItemsLink[x].getName() + " with id " + lTempItemsLink[x].getWurmId() + " since it doesn't belong here.");
                                    this.removeItem(lTempItemsLink[x], false);
                                }
                                catch (NoSuchZoneException nsz) {
                                    VolaTile.logger.log(Level.INFO, this.tilex + ", " + this.tiley + " removed " + lTempItemsLink[x].getName() + " with id " + lTempItemsLink[x].getWurmId() + ". It is in no valid zone.");
                                }
                            }
                        }
                        else {
                            aZone.removeItem(lTempItemsLink[x]);
                        }
                    }
                }
            }
            else {
                for (final Item item : this.vitems.getAllItemsAsSet()) {
                    if (item.getSizeZ() < 500) {
                        aZone.removeItem(item);
                    }
                }
            }
        }
    }
    
    private void linkEffectsToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.effects != null) {
            if (!aRemove && aZone.covers(this.tilex, this.tiley)) {
                for (final Effect effect : this.effects) {
                    aZone.addEffect(effect, false);
                }
            }
            else {
                for (final Effect effect : this.effects) {
                    aZone.removeEffect(effect);
                }
            }
        }
    }
    
    private void linkStructureToZone(final VirtualZone aZone, final boolean aRemove) {
        if (this.structure != null) {
            if (VolaTile.logger.isLoggable(Level.FINEST)) {
                VolaTile.logger.log(Level.INFO, "linkStructureToZone: " + this.structure.getWurmId() + " " + aZone.getId());
            }
            if (!aRemove) {
                aZone.addStructure(this.structure);
            }
            else {
                aZone.removeStructure(this.structure);
            }
        }
    }
    
    private boolean checkDeletion() {
        if ((this.creatures == null || this.creatures.size() == 0) && (this.vitems == null || this.vitems.isEmpty()) && (this.walls == null || this.walls.size() == 0) && this.structure == null && this.fences == null && (this.doors == null || this.doors.size() == 0) && (this.effects == null || this.effects.size() == 0) && (this.floors == null || this.floors.size() == 0) && (this.mineDoors == null || this.mineDoors.size() == 0)) {
            this.zone.removeTile(this);
            return true;
        }
        return false;
    }
    
    public void changeStructureName(final String newName) {
        if (this.watchers != null && this.structure != null) {
            for (final VirtualZone vzone : this.watchers) {
                vzone.changeStructureName(this.structure.getWurmId(), newName);
            }
        }
    }
    
    private final Item createPileItem(final Item posItem, final boolean starting) {
        try {
            final Item pileItem = ItemFactory.createItem(177, 60.0f, null);
            final float newXPos = (this.tilex << 2) + 1 + Server.rand.nextFloat() * 2.0f;
            final float newYPos = (this.tiley << 2) + 1 + Server.rand.nextFloat() * 2.0f;
            float height = posItem.getPosZ();
            if (Server.getSecondsUptime() > 0) {
                height = Zones.calculatePosZ(newXPos, newYPos, this, this.isOnSurface(), false, posItem.getPosZ(), null, posItem.onBridge());
            }
            pileItem.setPos(newXPos, newYPos, height, posItem.getRotation(), posItem.getBridgeId());
            pileItem.setZoneId(this.zone.getId(), this.surfaced);
            int data = posItem.getTemplateId();
            pileItem.setData1(data);
            byte material = 0;
            boolean multipleMaterials = false;
            if (this.vitems != null) {
                for (final Item item : this.vitems.getAllItemsAsArray()) {
                    if (!item.isDecoration() && item.getFloorLevel() == pileItem.getFloorLevel()) {
                        if (!starting) {
                            this.sendRemoveItem(item, false);
                        }
                        if (!multipleMaterials && item.getMaterial() != material) {
                            if (material == 0) {
                                material = item.getMaterial();
                            }
                            else {
                                material = 0;
                                multipleMaterials = true;
                            }
                        }
                        if (!item.equals(posItem)) {
                            pileItem.insertItem(item, true);
                        }
                        if (data != -1 && item.getTemplateId() != data) {
                            pileItem.setData1(-1);
                            data = -1;
                        }
                    }
                }
            }
            String name = pileItem.getName();
            String modelname = pileItem.getModelName();
            if (data != -1) {
                final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(data);
                final String tname = template.getName();
                name = "Pile of " + template.sizeString + tname;
                if (material == 0) {
                    pileItem.setMaterial(template.getMaterial());
                }
                else {
                    pileItem.setMaterial(material);
                }
                final StringBuilder build = new StringBuilder();
                build.append(pileItem.getTemplate().getModelName());
                build.append(tname);
                build.append(".");
                build.append(MaterialUtilities.getMaterialString(material));
                modelname = build.toString().replaceAll(" ", "").trim();
                pileItem.setName(name);
            }
            if (!starting && this.watchers != null) {
                for (final VirtualZone vz : this.getWatchers()) {
                    try {
                        if (vz.isVisible(pileItem, this)) {
                            boolean onGroundLevel = true;
                            if (pileItem.getFloorLevel() > 0) {
                                onGroundLevel = false;
                            }
                            else if (this.getFloors(0, 0).length > 0) {
                                onGroundLevel = false;
                            }
                            vz.addItem(pileItem, this, onGroundLevel);
                            if (data != -1) {
                                vz.renameItem(pileItem, name, modelname);
                            }
                        }
                    }
                    catch (Exception e) {
                        VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                    }
                }
            }
            return pileItem;
        }
        catch (FailedException fe) {
            VolaTile.logger.log(Level.WARNING, fe.getMessage(), fe);
        }
        catch (NoSuchTemplateException nst) {
            VolaTile.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        return null;
    }
    
    public void renameItem(final Item item) {
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (vz.isVisible(item, this)) {
                        vz.renameItem(item, item.getName(), item.getModelName());
                    }
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void putRandomOnTile(final Item item) {
        final float newPosX = (this.tilex << 2) + 0.5f + Server.rand.nextFloat() * 3.0f;
        final float newPosY = (this.tiley << 2) + 0.5f + Server.rand.nextFloat() * 3.0f;
        item.setPosXY(newPosX, newPosY);
    }
    
    private void destroyPileItem(final int floorLevel) {
        if (this.vitems != null) {
            final Item pileItem = this.vitems.getPileItem(floorLevel);
            this.destroyPileItem(pileItem);
            if (floorLevel == 0) {
                this.vitems.removePileItem(floorLevel);
            }
        }
    }
    
    private final void destroyPileItem(final Item pileItem) {
        if (pileItem != null) {
            try {
                final Creature[] iwatchers = pileItem.getWatchers();
                for (int x = 0; x < iwatchers.length; ++x) {
                    iwatchers[x].getCommunicator().sendCloseInventoryWindow(pileItem.getWurmId());
                }
            }
            catch (NoSuchCreatureException ex) {}
            if (this.vitems != null) {
                final Item[] itemarra = this.vitems.getAllItemsAsArray();
                for (int x = 0; x < itemarra.length; ++x) {
                    if (!itemarra[x].isDecoration() && itemarra[x].getFloorLevel() == pileItem.getFloorLevel()) {
                        this.vitems.removeItem(itemarra[x]);
                    }
                }
                final Item p = this.vitems.getPileItem(pileItem.getFloorLevel());
                if (p != null && p != pileItem) {
                    Items.destroyItem(p.getWurmId());
                }
            }
            Items.destroyItem(pileItem.getWurmId());
        }
    }
    
    @Override
    public int hashCode() {
        int result = this.tilex + 1;
        result += Zones.worldTileSizeY * (this.tiley + 1);
        return result * (this.surfaced ? 1 : 2);
    }
    
    public static int generateHashCode(final int _tilex, final int _tiley, final boolean _surfaced) {
        int result = _tilex + 1;
        result += (_tiley + 1) * Zones.worldTileSizeY;
        return result * (_surfaced ? 1 : 2);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object.getClass() == this.getClass()) {
            final VolaTile tile = (VolaTile)object;
            return tile.getTileX() == this.tilex && tile.getTileY() == this.tiley && tile.surfaced == this.surfaced;
        }
        return false;
    }
    
    public boolean isGuarded() {
        return this.village != null && this.village.guards.size() > 0;
    }
    
    public boolean hasFire() {
        return this.vitems != null && this.vitems.hasFire();
    }
    
    public Zone getZone() {
        return this.zone;
    }
    
    boolean isInactive() {
        return this.inactive;
    }
    
    void setInactive(final boolean aInactive) {
        this.inactive = aInactive;
    }
    
    public boolean isTransition() {
        return this.isTransition;
    }
    
    public final boolean hasOnePerTileItem(final int floorLevel) {
        return this.vitems != null && this.vitems.hasOnePerTileItem(floorLevel);
    }
    
    public final int getFourPerTileCount(final int floorLevel) {
        if (this.vitems == null) {
            return 0;
        }
        return this.vitems.getFourPerTileCount(floorLevel);
    }
    
    public final Item getOnePerTileItem(final int floorLevel) {
        if (this.vitems == null) {
            return null;
        }
        return this.vitems.getOnePerTileItem(floorLevel);
    }
    
    void lightningStrikeSpell(final float baseDamage, final Creature caster) {
        if (this.structure == null || !this.structure.isFinished()) {
            if (this.creatures != null) {
                final Creature[] crets = this.getCreatures();
                for (int c = 0; c < crets.length; ++c) {
                    Wound wound = null;
                    if (!crets[c].isPlayer()) {
                        wound = new TempWound((byte)4, (byte)1, baseDamage, crets[c].getWurmId(), 0.0f, 0.0f, true);
                    }
                    else {
                        if (Servers.localServer.PVPSERVER) {
                            float mod = 1.0f;
                            try {
                                final Item armour = crets[c].getArmour((byte)1);
                                if (armour != null) {
                                    if (armour.isMetal()) {
                                        mod = 2.0f;
                                    }
                                    else if (armour.isLeather() || armour.isCloth()) {
                                        mod = 0.5f;
                                    }
                                    armour.setDamage(armour.getDamage() + armour.getDamageModifier());
                                }
                            }
                            catch (NoArmourException ex) {}
                            catch (NoSpaceException nsp) {
                                VolaTile.logger.log(Level.WARNING, crets[c].getName() + " no armour space on loc " + 1);
                            }
                            crets[c].getCommunicator().sendAlertServerMessage("YOU ARE HIT BY LIGHTNING! OUCH!");
                            if (Servers.isThisATestServer()) {
                                crets[c].getCommunicator().sendNormalServerMessage("Lightning damage mod: " + mod);
                            }
                            crets[c].addWoundOfType(null, (byte)4, 1, false, 1.0f, false, baseDamage * mod, 0.0f, 0.0f, false, true);
                        }
                        crets[c].addAttacker(caster);
                    }
                }
            }
            if (Servers.localServer.PVPSERVER && this.vitems != null) {
                final Item[] ttempItems = this.vitems.getAllItemsAsArray();
                for (int x = 0; x < ttempItems.length; ++x) {
                    if (!ttempItems[x].isIndestructible() && !ttempItems[x].isHugeAltar()) {
                        ttempItems[x].setDamage(ttempItems[x].getDamage() + ttempItems[x].getDamageModifier() * ((ttempItems[x].isLocked() || ttempItems[x].isDecoration() || ttempItems[x].isMetal() || ttempItems[x].isStone()) ? 0.1f : 10.0f));
                    }
                }
            }
        }
    }
    
    void flashStrike() {
        if (this.structure == null || !this.structure.isFinished()) {
            if (this.creatures != null) {
                final Creature[] crets = this.getCreatures();
                for (int c = 0; c < crets.length; ++c) {
                    Wound wound = null;
                    if (!crets[c].isPlayer()) {
                        wound = new TempWound((byte)4, (byte)1, 10000.0f, crets[c].getWurmId(), 0.0f, 0.0f, false);
                    }
                    else {
                        float mod = 1.0f;
                        try {
                            final Item armour = crets[c].getArmour((byte)1);
                            if (armour != null) {
                                if (armour.isMetal()) {
                                    mod = 2.0f;
                                }
                                else if (armour.isLeather() || armour.isCloth()) {
                                    mod = 0.5f;
                                }
                                armour.setDamage(armour.getDamage() + armour.getDamageModifier() * 10.0f);
                            }
                            final Item[] lItems = crets[c].getBody().getContainersAndWornItems();
                            for (int x = 0; x < lItems.length; ++x) {
                                if ((lItems[x].isArmour() || lItems[x].isWeapon()) && lItems[x].isMetal()) {
                                    mod += 0.1f;
                                    lItems[x].setDamage(lItems[x].getDamage() + lItems[x].getDamageModifier() * 10.0f);
                                }
                            }
                        }
                        catch (NoArmourException ex) {}
                        catch (NoSpaceException nsp) {
                            VolaTile.logger.log(Level.WARNING, crets[c].getName() + " no armour space on loc " + 1);
                        }
                        crets[c].getCommunicator().sendAlertServerMessage("YOU ARE HIT BY LIGHTNING! OUCH!");
                        crets[c].addWoundOfType(null, (byte)4, 1, false, 1.0f, false, 3000.0f * mod, 0.0f, 0.0f, false, false);
                        HistoryManager.addHistory(crets[c].getName(), "was hit by lightning!");
                        if (VolaTile.logger.isLoggable(Level.FINER)) {
                            VolaTile.logger.finer(crets[c].getName() + " was hit by lightning!");
                        }
                        final Skills skills = crets[c].getSkills();
                        Skill mindspeed = null;
                        try {
                            mindspeed = skills.getSkill(101);
                            final double knowl = mindspeed.getKnowledge();
                            mindspeed.setKnowledge(knowl + 1.0f * mod, false);
                        }
                        catch (NoSuchSkillException nss) {
                            mindspeed = skills.learn(101, 21.0f);
                        }
                        crets[c].getCommunicator().sendNormalServerMessage("A strange dizziness runs through your head, eventually sharpening your senses.");
                    }
                }
            }
            if (this.vitems != null) {
                final Item[] ttempItems = this.vitems.getAllItemsAsArray();
                for (int x2 = 0; x2 < ttempItems.length; ++x2) {
                    ttempItems[x2].setDamage(ttempItems[x2].getDamage() + ttempItems[x2].getDamageModifier() * 10.0f);
                }
            }
        }
    }
    
    public void moveItem(final Item item, final float newPosX, final float newPosY, float newPosZ, final float newRot, final boolean surf, final float oldPosZ) {
        final float diffX = newPosX - item.getPosX();
        final float diffY = newPosY - item.getPosY();
        if (diffX != 0.0f || diffY != 0.0f) {
            final int newTileX = (int)newPosX >> 2;
            final int newTileY = (int)newPosY >> 2;
            long newBridgeId = item.getBridgeId();
            final long oldBridgeId = item.getBridgeId();
            if (newTileX != this.tilex || newTileY != this.tiley || surf != this.isOnSurface()) {
                final VolaTile dt = Zones.getTileOrNull(Zones.safeTileX(newTileX), Zones.safeTileY(newTileY), surf);
                if (item.onBridge() == -10L && dt != null && dt.getStructure() != null && dt.getStructure().isTypeBridge()) {
                    if (item.getBridgeId() == -10L) {
                        final BridgePart bp = Zones.getBridgePartFor(newTileX, newTileY, surf);
                        if (bp != null && bp.isFinished() && bp.hasAnExit()) {
                            if (Servers.isThisATestServer() && item.isWagonerWagon()) {
                                Players.getInstance().sendGmMessage(null, "System", "Debug: Wagon " + item.getName() + " bid:" + oldBridgeId + " z:" + item.getPosZ() + " fl:" + item.getFloorLevel() + " bp:" + bp.getStructureId() + " N:" + bp.getNorthExit() + " E:" + bp.getEastExit() + " S:" + bp.getSouthExit() + " W:" + bp.getWestExit() + " @" + item.getTileX() + "," + item.getTileY() + " to " + newTileX + "," + newTileY + "," + surf, false);
                            }
                            if (newTileY < item.getTileY() && bp.getSouthExitFloorLevel() == item.getFloorLevel()) {
                                newBridgeId = bp.getStructureId();
                            }
                            else if (newTileX > item.getTileX() && bp.getWestExitFloorLevel() == item.getFloorLevel()) {
                                newBridgeId = bp.getStructureId();
                            }
                            else if (newTileY > item.getTileY() && bp.getNorthExitFloorLevel() == item.getFloorLevel()) {
                                newBridgeId = bp.getStructureId();
                            }
                            else if (newTileX < item.getTileX() && bp.getEastExitFloorLevel() == item.getFloorLevel()) {
                                newBridgeId = bp.getStructureId();
                            }
                            if (Servers.isThisATestServer() && newBridgeId != oldBridgeId) {
                                Players.getInstance().sendGmMessage(null, "System", "Debug: Wagon " + item.getName() + " obid:" + oldBridgeId + " z:" + item.getPosZ() + " fl:" + item.getFloorLevel() + " nbid:" + newBridgeId + " N:" + bp.getNorthExit() + " E:" + bp.getEastExit() + " S:" + bp.getSouthExit() + " W:" + bp.getWestExit() + " @" + item.getTileX() + "," + item.getTileY() + " to " + newTileX + "," + newTileY + "," + surf, false);
                            }
                        }
                        else {
                            newBridgeId = -10L;
                            item.setOnBridge(-10L);
                            this.sendSetBridgeId(item, -10L);
                            item.calculatePosZ(dt, null);
                        }
                    }
                    else {
                        final BridgePart bp = Zones.getBridgePartFor(newTileX, newTileY, surf);
                        if (bp == null) {
                            newBridgeId = -10L;
                            item.setOnBridge(-10L);
                            this.sendSetBridgeId(item, -10L);
                            item.calculatePosZ(dt, null);
                        }
                    }
                    if (item.onBridge() != newBridgeId) {
                        final float nz = Zones.calculatePosZ(newPosX, newPosY, dt, this.isOnSurface(), false, oldPosZ, null, newBridgeId);
                        if (Servers.isThisATestServer() && item.isWagonerWagon()) {
                            Players.getInstance().sendGmMessage(null, "System", "Debug: Wagon " + item.getName() + " moving onto, or off, a bridge from bid:" + oldBridgeId + " z:" + item.getPosZ() + " fl:" + item.getFloorLevel() + " to bp:" + newBridgeId + " newZ:" + nz + " @" + item.getTileX() + "," + item.getTileY() + " to " + newTileX + "," + newTileY + "," + surf, false);
                        }
                        if (Math.abs(oldPosZ - nz) < 10.0f && !item.isBoat()) {
                            item.setOnBridge(newBridgeId);
                            newPosZ = nz;
                            this.sendSetBridgeId(item, newBridgeId);
                        }
                    }
                }
                else if (item.onBridge() > 0L && (dt == null || dt.getStructure() == null || dt.getStructure().getWurmId() != item.onBridge())) {
                    boolean leave = true;
                    final BridgePart bp2 = Zones.getBridgePartFor(newTileX, newTileY, surf);
                    if (bp2 != null && bp2.isFinished()) {
                        if (bp2.getDir() == 0 || bp2.getDir() == 4) {
                            if (this.getTileX() != newTileX) {
                                leave = false;
                            }
                        }
                        else if (this.getTileY() != newTileY) {
                            leave = false;
                        }
                    }
                    if (leave) {
                        newBridgeId = -10L;
                        item.setOnBridge(-10L);
                        this.sendSetBridgeId(item, -10L);
                    }
                }
                if (surf != this.isOnSurface()) {
                    item.newLayer = (byte)(this.isOnSurface() ? -1 : 0);
                }
                this.removeItem(item, true);
                if (diffX != 0.0f && diffY != 0.0f) {
                    item.setPosXYZRotation(newPosX, newPosY, newPosZ, newRot);
                }
                else {
                    item.setRotation(newRot);
                    if (diffX != 0.0f) {
                        item.setPosX(newPosX);
                    }
                    if (diffY != 0.0f) {
                        item.setPosY(newPosY);
                    }
                    item.setPosZ(newPosZ);
                }
                try {
                    final Zone _zone = Zones.getZone((int)newPosX >> 2, (int)newPosY >> 2, surf);
                    _zone.addItem(item, true, surf != this.isOnSurface(), false);
                }
                catch (NoSuchZoneException nsz) {
                    VolaTile.logger.log(Level.WARNING, item.getName() + ", " + nsz.getMessage(), nsz);
                }
                if (surf != this.isOnSurface()) {
                    item.newLayer = -128;
                }
            }
            else {
                if (diffX != 0.0f) {
                    item.setTempXPosition(newPosX);
                }
                if (diffY != 0.0f) {
                    item.setTempYPosition(newPosY);
                }
                item.setTempZandRot(newPosZ, newRot);
            }
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    if (vz.isVisible(item, this)) {
                        if (item.getFloorLevel() <= 0 && item.onBridge() <= 0L) {
                            if (Structure.isGroundFloorAtPosition(newPosX, newPosY, item.isOnSurface())) {
                                vz.sendMoveMovingItemAndSetZ(item.getWurmId(), item.getPosX(), item.getPosY(), item.getPosZ(), (int)(newRot * 256.0f / 360.0f));
                            }
                            else {
                                vz.sendMoveMovingItem(item.getWurmId(), item.getPosX(), item.getPosY(), (int)(newRot * 256.0f / 360.0f));
                            }
                        }
                        else {
                            vz.sendMoveMovingItemAndSetZ(item.getWurmId(), item.getPosX(), item.getPosY(), item.getPosZ(), (int)(newRot * 256.0f / 360.0f));
                        }
                    }
                    else {
                        vz.removeItem(item);
                    }
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void destroyEverything() {
        final Creature[] crets = this.getCreatures();
        for (int x = 0; x < crets.length; ++x) {
            crets[x].getCommunicator().sendNormalServerMessage("The rock suddenly caves in! You are crushed!");
            crets[x].die(true, "Cave collapse");
        }
        final Fence[] fenceArr = this.getFences();
        for (int x2 = 0; x2 < fenceArr.length; ++x2) {
            if (fenceArr[x2] != null) {
                fenceArr[x2].destroy();
            }
        }
        final Wall[] wallArr = this.getWalls();
        for (int x3 = 0; x3 < wallArr.length; ++x3) {
            if (wallArr[x3] != null) {
                wallArr[x3].destroy();
            }
        }
        final Floor[] floorArr = this.getFloors();
        for (int x4 = 0; x4 < floorArr.length; ++x4) {
            if (floorArr[x4] != null) {
                floorArr[x4].destroy();
            }
        }
        final Item[] ttempItems = this.getItems();
        for (int x5 = 0; x5 < ttempItems.length; ++x5) {
            Items.destroyItem(ttempItems[x5].getWurmId());
        }
    }
    
    protected void sendNewLayerToWatchers(final Item item) {
        VolaTile.logger.log(Level.INFO, "Tile at " + this.tilex + ", " + this.tiley + " sending secondary");
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.justSendNewLayer(item);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    protected void newLayer(final Item item) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.newLayer(item);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (!this.isOnSurface()) {
            for (final VirtualZone vz : this.getSurfaceTile().getWatchers()) {
                try {
                    vz.addItem(item, this.getSurfaceTile(), true);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public void newLayer(final Creature creature) {
        if (creature.isOnSurface() != this.isOnSurface()) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.newLayer(creature, this.isOnSurface());
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        try {
            final Zone newzone = Zones.getZone(this.tilex, this.tiley, creature.getLayer() >= 0);
            final VolaTile currentTile = newzone.getOrCreateTile(this.tilex, this.tiley);
            this.removeCreature(creature);
            currentTile.addCreature(creature, 0.0f);
        }
        catch (NoSuchZoneException ex) {}
        catch (NoSuchPlayerException ex2) {}
        catch (NoSuchCreatureException ex3) {}
    }
    
    public void addLightSource(final Item lightSource) {
        if (lightSource.getTemplateId() == 1243) {
            return;
        }
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (lightSource.getColor() != -1) {
                    int lightStrength = Math.max(WurmColor.getColorRed(lightSource.getColor()), WurmColor.getColorGreen(lightSource.getColor()));
                    lightStrength = Math.max(lightStrength, WurmColor.getColorBlue(lightSource.getColor()));
                    if (lightStrength == 0) {
                        lightStrength = 1;
                    }
                    final byte r = (byte)(WurmColor.getColorRed(lightSource.getColor()) * 128 / lightStrength);
                    final byte g = (byte)(WurmColor.getColorGreen(lightSource.getColor()) * 128 / lightStrength);
                    final byte b = (byte)(WurmColor.getColorBlue(lightSource.getColor()) * 128 / lightStrength);
                    vz.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, r, g, b, lightSource.getRadius());
                }
                else if (lightSource.isLightBright()) {
                    final int lightStrength = (int)(80.0f + lightSource.getCurrentQualityLevel() / 100.0f * 40.0f);
                    vz.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, Item.getRLight(lightStrength), Item.getGLight(lightStrength), Item.getBLight(lightStrength), lightSource.getRadius());
                }
                else {
                    vz.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, (byte)80, (byte)80, (byte)80, lightSource.getRadius());
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void removeLightSource(final Item lightSource) {
        if (lightSource.getTemplateId() == 1243) {
            return;
        }
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendRemoveEffect(lightSource.getWurmId(), (byte)0);
                vz.sendRemoveEffect(lightSource.getWurmId(), (byte)4);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void setHasLightSource(final Creature creature, @Nullable final Item lightSource) {
        if (lightSource != null && lightSource.getTemplateId() == 1243) {
            return;
        }
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (lightSource == null) {
                    if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                        vz.sendRemoveEffect(-1L, (byte)0);
                    }
                    else {
                        vz.sendRemoveEffect(creature.getWurmId(), (byte)0);
                    }
                }
                else if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    if (lightSource.getColor() != -1) {
                        int lightStrength = Math.max(WurmColor.getColorRed(lightSource.color), WurmColor.getColorGreen(lightSource.color));
                        lightStrength = Math.max(lightStrength, WurmColor.getColorBlue(lightSource.color));
                        final byte r = (byte)(WurmColor.getColorRed(lightSource.color) * 128 / lightStrength);
                        final byte g = (byte)(WurmColor.getColorGreen(lightSource.color) * 128 / lightStrength);
                        final byte b = (byte)(WurmColor.getColorBlue(lightSource.color) * 128 / lightStrength);
                        vz.sendAttachCreatureEffect(null, (byte)0, r, g, b, lightSource.getRadius());
                    }
                    else if (lightSource.isLightBright()) {
                        final int lightStrength = (int)(80.0f + lightSource.getCurrentQualityLevel() / 100.0f * 40.0f);
                        vz.sendAttachCreatureEffect(null, (byte)0, Item.getRLight(lightStrength), Item.getGLight(lightStrength), Item.getBLight(lightStrength), lightSource.getRadius());
                    }
                    else {
                        vz.sendAttachCreatureEffect(null, (byte)0, Item.getRLight(80), Item.getGLight(80), Item.getBLight(80), lightSource.getRadius());
                    }
                }
                else if (lightSource.getColor() != -1) {
                    int lightStrength = Math.max(WurmColor.getColorRed(lightSource.color), WurmColor.getColorGreen(lightSource.color));
                    lightStrength = Math.max(lightStrength, WurmColor.getColorBlue(lightSource.color));
                    final byte r = (byte)(WurmColor.getColorRed(lightSource.color) * 128 / lightStrength);
                    final byte g = (byte)(WurmColor.getColorGreen(lightSource.color) * 128 / lightStrength);
                    final byte b = (byte)(WurmColor.getColorBlue(lightSource.color) * 128 / lightStrength);
                    vz.sendAttachCreatureEffect(creature, (byte)0, r, g, b, lightSource.getRadius());
                }
                else if (lightSource.isLightBright()) {
                    final int lightStrength = (int)(80.0f + lightSource.getCurrentQualityLevel() / 100.0f * 40.0f);
                    vz.sendAttachCreatureEffect(creature, (byte)0, Item.getRLight(lightStrength), Item.getGLight(lightStrength), Item.getBLight(lightStrength), lightSource.getRadius());
                }
                else {
                    vz.sendAttachCreatureEffect(creature, (byte)0, Item.getRLight(80), Item.getGLight(80), Item.getBLight(80), lightSource.getRadius());
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void setHasLightSource(final Creature creature, final byte colorRed, final byte colorGreen, final byte colorBlue, final byte radius) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendAttachCreatureEffect(null, (byte)0, colorRed, colorGreen, colorBlue, radius);
                }
                else {
                    vz.sendAttachCreatureEffect(creature, (byte)0, colorRed, colorGreen, colorBlue, radius);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAttachCreatureEffect(final Creature creature, final byte effectType, final byte data0, final byte data1, final byte data2, final byte radius) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendAttachCreatureEffect(null, effectType, data0, data1, data2, radius);
                }
                else {
                    vz.sendAttachCreatureEffect(creature, effectType, data0, data1, data2, radius);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRemoveCreatureEffect(final Creature creature, final byte effectType) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendRemoveEffect(-1L, effectType);
                }
                else {
                    vz.sendRemoveEffect(creature.getWurmId(), effectType);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendProjectile(final long itemid, final byte type, final String modelName, final String name, final byte material, final float startX, final float startY, final float startH, final float rot, final byte layer, final float endX, final float endY, final float endH, final long sourceId, final long targetId, final float projectedSecondsInAir, final float actualSecondsInAir) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == targetId) {
                    if (vz.getWatcher().getWurmId() == sourceId) {
                        vz.sendProjectile(itemid, type, modelName, name, material, startX, startY, startH, rot, layer, endX, endY, endH, -1L, -1L, projectedSecondsInAir, actualSecondsInAir);
                    }
                    else {
                        vz.sendProjectile(itemid, type, modelName, name, material, startX, startY, startH, rot, layer, endX, endY, endH, sourceId, -1L, projectedSecondsInAir, actualSecondsInAir);
                    }
                }
                else if (vz.getWatcher().getWurmId() == sourceId) {
                    if (vz.getWatcher().getWurmId() == targetId) {
                        vz.sendProjectile(itemid, type, modelName, name, material, startX, startY, startH, rot, layer, endX, endY, endH, -1L, -1L, projectedSecondsInAir, actualSecondsInAir);
                    }
                    else {
                        vz.sendProjectile(itemid, type, modelName, name, material, startX, startY, startH, rot, layer, endX, endY, endH, -1L, targetId, projectedSecondsInAir, actualSecondsInAir);
                    }
                }
                else {
                    vz.sendProjectile(itemid, type, modelName, name, material, startX, startY, startH, rot, layer, endX, endY, endH, sourceId, targetId, projectedSecondsInAir, actualSecondsInAir);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendNewProjectile(final long itemid, final byte type, final String modelName, final String name, final byte material, final Vector3f startingPosition, final Vector3f startingVelocity, final Vector3f endingPosition, final float rotation, final boolean surface) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendNewProjectile(itemid, type, modelName, name, material, startingPosition, startingVelocity, endingPosition, rotation, surface);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendHorseWear(final long creatureId, final int itemId, final byte material, final byte slot, final byte aux_data) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendHorseWear(creatureId, itemId, material, slot, aux_data);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRemoveHorseWear(final long creatureId, final int itemId, final byte slot) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendRemoveHorseWear(creatureId, itemId, slot);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendBoatAttachment(final long itemId, final int templateId, final byte material, final byte slot, final byte aux) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendBoatAttachment(itemId, templateId, material, slot, aux);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendBoatDetachment(final long itemId, final int templateId, final byte slot) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendBoatDetachment(itemId, templateId, slot);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendWearItem(final long creatureId, final int itemId, final byte bodyPart, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue, final byte material, final byte rarity) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creatureId) {
                    vz.sendWearItem(-1L, itemId, bodyPart, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue, material, rarity);
                }
                else {
                    vz.sendWearItem(creatureId, itemId, bodyPart, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue, material, rarity);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRemoveWearItem(final long creatureId, final byte bodyPart) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creatureId) {
                    vz.sendRemoveWearItem(-1L, bodyPart);
                }
                else {
                    vz.sendRemoveWearItem(creatureId, bodyPart);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendWieldItem(final long creatureId, final byte slot, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creatureId) {
                    vz.sendWieldItem(-1L, slot, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
                }
                else {
                    vz.sendWieldItem(creatureId, slot, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendUseItem(final Creature creature, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendUseItem(null, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
                }
                else if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendUseItem(creature, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendStopUseItem(final Creature creature) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendStopUseItem(null);
                }
                else if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendStopUseItem(creature);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAnimation(final Creature creature, final String animationName, final boolean looping, final long target) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendAnimation(null, animationName, looping, target);
                }
                else if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendAnimation(creature, animationName, looping, target);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendStance(final Creature creature, final byte stance) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creature.getWurmId()) {
                    vz.sendStance(null, stance);
                }
                else if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendStance(creature, stance);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAnimation(final Creature initiator, final Item item, final String animationName, final boolean looping, final boolean freeze) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                final Creature watcher = vz.getWatcher();
                if (watcher != null && vz.isVisible(item, this) && (initiator == null || initiator.isVisibleTo(watcher))) {
                    watcher.getCommunicator().sendAnimation(item.getWurmId(), animationName, looping, freeze);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendCreatureDamage(final Creature creature, final float damPercent) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendCreatureDamage(creature, damPercent);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendFishingLine(final Creature creature, final float posX, final float posY, final byte floatType) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendFishingLine(creature, posX, posY, floatType);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendFishHooked(final Creature creature, final byte fishType, final long fishId) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendFishHooked(creature, fishType, fishId);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendFishingStopped(final Creature creature) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendFishingStopped(creature);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendSpearStrike(final Creature creature, final float posX, final float posY) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (creature.isVisibleTo(vz.getWatcher())) {
                    vz.sendSpearStrike(creature, posX, posY);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRepaint(final Item item) {
        final boolean noPaint = item.color == -1;
        final boolean noPaint2 = item.color2 == -1;
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendRepaint(item.getWurmId(), (byte)WurmColor.getColorRed(item.getColor()), (byte)WurmColor.getColorGreen(item.getColor()), (byte)WurmColor.getColorBlue(item.getColor()), (byte)(noPaint ? 0 : -1), (byte)0);
                if (item.supportsSecondryColor()) {
                    vz.sendRepaint(item.getWurmId(), (byte)WurmColor.getColorRed(item.getColor2()), (byte)WurmColor.getColorGreen(item.getColor2()), (byte)WurmColor.getColorBlue(item.getColor2()), (byte)(noPaint2 ? 0 : -1), (byte)1);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAttachCreature(final long creatureId, final long targetId, final float offx, final float offy, final float offz, final int seatId) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creatureId) {
                    vz.sendAttachCreature(-1L, targetId, offx, offy, offz, seatId);
                }
                else if (vz.getWatcher().getWurmId() == targetId) {
                    vz.sendAttachCreature(creatureId, -1L, offx, offy, offz, seatId);
                }
                else {
                    vz.sendAttachCreature(creatureId, targetId, offx, offy, offz, seatId);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAttachCreature(final long creatureId, final long targetId, final float offx, final float offy, final float offz, final int seatId, final boolean ignoreOrigin) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() == creatureId) {
                    if (!ignoreOrigin) {
                        vz.sendAttachCreature(-1L, targetId, offx, offy, offz, seatId);
                    }
                }
                else if (vz.getWatcher().getWurmId() == targetId) {
                    vz.sendAttachCreature(creatureId, -1L, offx, offy, offz, seatId);
                }
                else {
                    vz.sendAttachCreature(creatureId, targetId, offx, offy, offz, seatId);
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public Set<VolaTile> getThisAndSurroundingTiles(final int dist) {
        final Set<VolaTile> surr = new HashSet<VolaTile>();
        VolaTile t = null;
        for (int x = -dist; x <= dist; ++x) {
            for (int y = -dist; y <= dist; ++y) {
                t = Zones.getTileOrNull(Zones.safeTileX(this.tilex + x), Zones.safeTileY(this.tiley + y), this.surfaced);
                if (t != null) {
                    surr.add(t);
                }
            }
        }
        return surr;
    }
    
    public void checkDiseaseSpread() {
        int dist = 1;
        if (this.village != null && this.village.getCreatureRatio() < Village.OPTIMUMCRETRATIO) {
            dist = 2;
        }
        final Set<VolaTile> set = this.getThisAndSurroundingTiles(dist);
        for (final VolaTile t : set) {
            final Creature[] creatures;
            final Creature[] crets = creatures = t.getCreatures();
            for (final Creature c : creatures) {
                if (!c.isPlayer() && !c.isKingdomGuard() && !c.isSpiritGuard() && !c.isUnique() && Server.rand.nextInt(100) == 0 && c.getDisease() == 0) {
                    VolaTile.logger.log(Level.INFO, "Disease spreads to " + c.getName() + " at " + t);
                    c.setDisease((byte)1);
                }
            }
        }
    }
    
    public void checkVisibility(final Creature watched, final boolean makeInvis) {
        float lStealthMod;
        if (makeInvis) {
            lStealthMod = MethodsCreatures.getStealthTerrainModifier(watched, this.tilex, this.tiley, this.surfaced);
        }
        else {
            lStealthMod = 0.0f;
        }
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                if (vz.getWatcher().getWurmId() != watched.getWurmId()) {
                    if (makeInvis && !watched.visibilityCheck(vz.getWatcher(), lStealthMod)) {
                        vz.makeInvisible(watched);
                    }
                    else {
                        try {
                            vz.addCreature(watched.getWurmId(), false);
                        }
                        catch (NoSuchCreatureException ex) {}
                        catch (NoSuchPlayerException ex2) {}
                    }
                }
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void checkCaveOpening() {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.getWatcher().getVisionArea().checkCaves(false);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void setNewFace(final Creature c) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.setNewFace(c);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void setNewRarityShader(final Creature c) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.setNewRarityShader(c);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendActionControl(final Creature c, final String actionString, final boolean start, final int timeLeft) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendActionControl(c.getWurmId(), actionString, start, timeLeft);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendActionControl(final Item item, final String actionString, final boolean start, final int timeLeft) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendActionControl(item.getWurmId(), actionString, start, timeLeft);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRotate(final Item item, final float rotation) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendRotate(item, rotation);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public int getLayer() {
        if (this.surfaced) {
            return 0;
        }
        return -1;
    }
    
    public void sendAddTileEffect(final AreaSpellEffect effect, final boolean loop) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.addAreaSpellEffect(effect, loop);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendAddQuickTileEffect(final byte effect, final int floorOffset) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.sendAddTileEffect(this.tilex, this.tiley, this.getLayer(), effect, floorOffset, false);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void sendRemoveTileEffect(final AreaSpellEffect effect) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.removeAreaSpellEffect(effect);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateFenceState(final Fence fence) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.updateFenceDamageState(fence);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateTargetStatus(final long targetId, final byte type, final float status) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.updateTargetStatus(targetId, type, status);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateWallDamageState(final Wall wall) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.updateWallDamageState(wall);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateFloorDamageState(final Floor floor) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.updateFloorDamageState(floor);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    public void updateBridgePartDamageState(final BridgePart bridgePart) {
        for (final VirtualZone vz : this.getWatchers()) {
            try {
                vz.updateBridgePartDamageState(bridgePart);
            }
            catch (Exception e) {
                VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    private AreaSpellEffect getAreaEffect() {
        return AreaSpellEffect.getEffect(this.tilex, this.tiley, this.getLayer());
    }
    
    public final boolean isInPvPZone() {
        return Zones.isOnPvPServer(this.tilex, this.tiley);
    }
    
    public final void lightLamps() {
        if (this.vitems != null) {
            for (final Item i : this.vitems.getAllItemsAsSet()) {
                if (i.isStreetLamp() && i.isPlanted()) {
                    i.setAuxData((byte)120);
                    i.setTemperature((short)10000);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "VolaTile [X: " + this.tilex + ", Y: " + this.tiley + ", surf=" + this.surfaced + "]";
    }
    
    public Floor[] getFloors(final int startHeightOffset, final int endHeightOffset) {
        if (this.floors == null) {
            return VolaTile.emptyFloors;
        }
        final List<Floor> toReturn = new ArrayList<Floor>();
        for (final Floor floor : this.floors) {
            if (floor.getHeightOffset() >= startHeightOffset && floor.getHeightOffset() <= endHeightOffset) {
                toReturn.add(floor);
            }
        }
        return toReturn.toArray(new Floor[toReturn.size()]);
    }
    
    @Nullable
    public Floor getFloor(final int floorLevel) {
        if (this.floors != null) {
            for (final Floor floor : this.floors) {
                if (floor.getFloorLevel() == floorLevel) {
                    return floor;
                }
            }
        }
        return null;
    }
    
    public Floor[] getFloors() {
        if (this.floors == null) {
            return VolaTile.emptyFloors;
        }
        return this.floors.toArray(new Floor[this.floors.size()]);
    }
    
    public final void addFloor(final Floor floor) {
        if (this.floors == null) {
            this.floors = new HashSet<Floor>();
        }
        this.floors.add(floor);
        if (floor.isStair()) {
            Stairs.addStair(this.hashCode(), floor.getFloorLevel());
        }
        if (this.vitems != null) {
            for (final Item pile : this.vitems.getPileItems()) {
                pile.updatePosZ(this);
            }
        }
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.updateFloor(this.structure.getWurmId(), floor);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public final void removeFloor(final Blocker floor) {
        if (this.floors != null) {
            Floor toRem = null;
            for (final Floor fl : this.floors) {
                if (fl.getId() == floor.getId()) {
                    toRem = fl;
                    break;
                }
            }
            if (toRem != null) {
                this.removeFloor(toRem);
            }
        }
    }
    
    public final void removeFloor(final Floor floor) {
        final int floorLevel = floor.getFloorLevel();
        if (this.floors != null) {
            this.floors.remove(floor);
            if (floor.isStair()) {
                Stairs.removeStair(this.hashCode(), floorLevel);
            }
            if (this.floors.size() == 0) {
                this.floors = null;
            }
        }
        if (this.structure == null) {
            return;
        }
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.removeFloor(this.structure.getWurmId(), floor);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        if (floorLevel > 0) {
            this.destroyPileItem(floorLevel);
        }
        else if (this.vitems != null) {
            final Item pileItem = this.vitems.getPileItem(floorLevel);
            if (pileItem != null) {
                pileItem.updatePosZ(this);
                for (final VirtualZone vz2 : this.getWatchers()) {
                    try {
                        if (vz2.isVisible(pileItem, this)) {
                            vz2.removeItem(pileItem);
                            vz2.addItem(pileItem, this, true);
                        }
                    }
                    catch (Exception e2) {
                        VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                    }
                }
            }
        }
        if (this.vitems != null) {
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.isDecoration() && item.getFloorLevel() == floorLevel) {
                    item.updatePosZ(this);
                    item.updateIfGroundItem();
                }
            }
        }
        if (this.creatures != null) {
            for (final Creature c : this.creatures) {
                if (c.getFloorLevel() == floorLevel && !c.isPlayer()) {
                    final float oldposz = c.getPositionZ();
                    final float newPosz = c.calculatePosZ();
                    final float diffz = newPosz - oldposz;
                    c.setPositionZ(newPosz);
                    c.moved(0.0f, 0.0f, diffz, 0, 0);
                }
            }
        }
        this.checkDeletion();
    }
    
    public final void addBridgePart(final BridgePart bridgePart) {
        if (this.bridgeParts == null) {
            this.bridgeParts = new HashSet<BridgePart>();
        }
        this.bridgeParts.add(bridgePart);
        if (this.vitems != null) {
            for (final Item pile : this.vitems.getPileItems()) {
                pile.updatePosZ(this);
            }
        }
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.updateBridgePart(this.structure.getWurmId(), bridgePart);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
    }
    
    public final void removeBridgePart(final BridgePart bridgePart) {
        if (this.bridgeParts != null) {
            this.bridgeParts.remove(bridgePart);
            if (this.bridgeParts.size() == 0) {
                this.bridgeParts = null;
            }
        }
        if (this.structure == null) {
            return;
        }
        if (this.watchers != null) {
            for (final VirtualZone vz : this.getWatchers()) {
                try {
                    vz.removeBridgePart(this.structure.getWurmId(), bridgePart);
                }
                catch (Exception e) {
                    VolaTile.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        if (this.vitems != null) {
            for (final Item pile : this.vitems.getPileItems()) {
                pile.setOnBridge(-10L);
                pile.updatePosZ(this);
                for (final VirtualZone vz2 : this.getWatchers()) {
                    try {
                        if (vz2.isVisible(pile, this)) {
                            vz2.removeItem(pile);
                            vz2.addItem(pile, this, true);
                        }
                    }
                    catch (Exception e2) {
                        VolaTile.logger.log(Level.WARNING, e2.getMessage(), e2);
                    }
                }
            }
        }
        if (this.vitems != null) {
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.getBridgeId() == this.structure.getWurmId()) {
                    item.setOnBridge(-10L);
                    item.updatePosZ(this);
                    item.updateIfGroundItem();
                }
            }
        }
        if (this.creatures != null) {
            for (final Creature c : this.creatures) {
                if (c.getBridgeId() == this.structure.getWurmId()) {
                    c.setBridgeId(-10L);
                    if (c.isPlayer()) {
                        continue;
                    }
                    final float oldposz = c.getPositionZ();
                    final float newPosz = c.calculatePosZ();
                    final float diffz = newPosz - oldposz;
                    c.setPositionZ(newPosz);
                    c.moved(0.0f, 0.0f, diffz, 0, 0);
                }
            }
        }
        this.checkDeletion();
    }
    
    public final Floor getTopFloor() {
        if (this.floors != null) {
            Floor toret = null;
            for (final Floor floor : this.floors) {
                if (toret == null || floor.getFloorLevel() > toret.getFloorLevel()) {
                    toret = floor;
                }
            }
            return toret;
        }
        return null;
    }
    
    public final Fence getTopFence() {
        if (this.fences != null) {
            Fence toret = null;
            for (final Fence f : this.fences.values()) {
                if (toret == null || f.getFloorLevel() > toret.getFloorLevel()) {
                    toret = f;
                }
            }
            return toret;
        }
        return null;
    }
    
    public final Wall getTopWall() {
        if (this.walls != null) {
            Wall toret = null;
            for (final Wall f : this.walls) {
                if ((toret == null || f.getFloorLevel() > toret.getFloorLevel()) && f.isFinished()) {
                    toret = f;
                }
            }
            return toret;
        }
        return null;
    }
    
    public final boolean isNextTo(final VolaTile t) {
        return t != null && t.getLayer() == this.getLayer() && (((t.getTileX() == this.getTileX() - 1 || t.getTileX() == this.getTileX() + 1) && t.getTileY() == this.getTileY()) || (t.getTileX() == this.getTileX() && (t.getTileY() == this.getTileY() - 1 || t.getTileY() == this.getTileY() + 1)));
    }
    
    public final void damageFloors(final int minFloorLevel, final int maxFloorLevel, final float addedDamage) {
        final Floor[] floors;
        final Floor[] floorArr = floors = this.getFloors(minFloorLevel * 30, maxFloorLevel * 30);
        for (final Floor floor : floors) {
            floor.setDamage(floor.getDamage() + addedDamage);
            if (floor.getDamage() >= 100.0f) {
                this.removeFloor(floor);
            }
        }
    }
    
    public final boolean hasStair(final int floorLevel) {
        return Stairs.hasStair(this.hashCode(), floorLevel);
    }
    
    public Item findHive(final int hiveType) {
        if (this.vitems != null) {
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.getTemplateId() == hiveType) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public Item findHive(final int hiveType, final boolean withQueen) {
        if (this.vitems != null) {
            for (final Item item : this.vitems.getAllItemsAsArray()) {
                if (item.getTemplateId() == hiveType) {
                    if (withQueen && item.getAuxData() > 0) {
                        return item;
                    }
                    if (!withQueen && item.getAuxData() == 0) {
                        return item;
                    }
                }
            }
        }
        return null;
    }
    
    static {
        logger = Logger.getLogger(VolaTile.class.getName());
        emptySupports = new HashSet<StructureSupport>();
        emptyCreatures = new Creature[0];
        emptyItems = new Item[0];
        emptyWalls = new Wall[0];
        emptyFences = new Fence[0];
        emptyFloors = new Floor[0];
        emptyBridgeParts = new BridgePart[0];
        emptyWatchers = new VirtualZone[0];
        emptyEffects = new Effect[0];
        emptyDoors = new Door[0];
        toRemove = new HashSet<Wall>();
    }
}

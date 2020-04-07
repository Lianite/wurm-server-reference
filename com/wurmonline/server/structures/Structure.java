// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.math.TilePos;
import com.wurmonline.server.utils.CoordUtils;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.creatures.AnimalSettings;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.behaviours.MethodsStructure;
import java.util.Collection;
import com.wurmonline.server.utils.StringUtil;
import java.util.List;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.DbStrings;
import com.wurmonline.server.Server;
import com.wurmonline.shared.constants.StructureTypeEnum;
import java.util.Iterator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import java.io.IOException;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Constants;
import java.util.HashSet;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.zones.VolaTile;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.shared.constants.StructureConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public abstract class Structure implements MiscConstants, CounterTypes, TimeConstants, StructureConstants, PermissionsPlayerList.ISettings
{
    private static Logger logger;
    private long wurmId;
    Set<VolaTile> structureTiles;
    Set<BuildTile> buildTiles;
    int minX;
    int maxX;
    int minY;
    int maxY;
    protected boolean surfaced;
    private long creationDate;
    private byte roof;
    private String name;
    private boolean isLoading;
    private boolean hasLoaded;
    boolean finished;
    Set<Door> doors;
    long writid;
    boolean finalfinished;
    boolean allowsVillagers;
    boolean allowsAllies;
    boolean allowsKingdom;
    private String planner;
    long ownerId;
    private Permissions permissions;
    int villageId;
    private byte structureType;
    private long lastPolled;
    public static final float DAMAGE_STATE_DIVIDER = 60.0f;
    public static final int[] noEntrance;
    
    Structure(final byte theStructureType, final String aName, final long aId, final int aStartX, final int aStartY, final boolean aSurfaced) {
        this.buildTiles = new HashSet<BuildTile>();
        this.minX = 1 << Constants.meshSize;
        this.maxX = 0;
        this.minY = 1 << Constants.meshSize;
        this.maxY = 0;
        this.isLoading = false;
        this.hasLoaded = false;
        this.finished = false;
        this.writid = -10L;
        this.finalfinished = false;
        this.allowsVillagers = false;
        this.allowsAllies = false;
        this.allowsKingdom = false;
        this.planner = "";
        this.ownerId = -10L;
        this.permissions = new Permissions();
        this.villageId = -1;
        this.structureType = 0;
        this.lastPolled = System.currentTimeMillis();
        this.structureType = theStructureType;
        this.wurmId = aId;
        this.name = aName;
        this.structureTiles = new HashSet<VolaTile>();
        if (aStartX > this.maxX) {
            this.maxX = aStartX;
        }
        if (aStartX < this.minX) {
            this.minX = aStartX;
        }
        if (aStartY > this.maxY) {
            this.maxY = aStartY;
        }
        if (aStartY < this.minY) {
            this.minY = aStartY;
        }
        this.surfaced = aSurfaced;
        this.creationDate = System.currentTimeMillis();
        try {
            final Zone zone = Zones.getZone(aStartX, aStartY, aSurfaced);
            final VolaTile tile = zone.getOrCreateTile(aStartX, aStartY);
            this.structureTiles.add(tile);
            if (theStructureType == 0) {
                tile.addBuildMarker(this);
                this.clearAllWallsAndMakeWallsForStructureBorder(tile);
            }
            else {
                tile.addStructure(this);
            }
        }
        catch (NoSuchZoneException nsz) {
            Structure.logger.log(Level.WARNING, "No such zone: " + aStartX + ", " + aStartY + ", StructureId: " + this.wurmId, nsz);
        }
    }
    
    Structure(final byte theStructureType, final String aName, final long aId, final boolean aIsSurfaced, final byte _roof, final boolean _finished, final boolean finFinished, final long _writid, final String aPlanner, final long aOwnerId, final int aSettings, final int aVillageId, final boolean allowsCitizens, final boolean allowAllies, final boolean allowKingdom) {
        this.buildTiles = new HashSet<BuildTile>();
        this.minX = 1 << Constants.meshSize;
        this.maxX = 0;
        this.minY = 1 << Constants.meshSize;
        this.maxY = 0;
        this.isLoading = false;
        this.hasLoaded = false;
        this.finished = false;
        this.writid = -10L;
        this.finalfinished = false;
        this.allowsVillagers = false;
        this.allowsAllies = false;
        this.allowsKingdom = false;
        this.planner = "";
        this.ownerId = -10L;
        this.permissions = new Permissions();
        this.villageId = -1;
        this.structureType = 0;
        this.lastPolled = System.currentTimeMillis();
        this.structureType = theStructureType;
        this.wurmId = aId;
        this.writid = _writid;
        this.name = aName;
        this.structureTiles = new HashSet<VolaTile>();
        this.surfaced = aIsSurfaced;
        this.roof = _roof;
        this.finished = _finished;
        this.finalfinished = finFinished;
        this.allowsVillagers = allowsCitizens;
        this.allowsAllies = allowAllies;
        this.allowsKingdom = allowKingdom;
        this.planner = aPlanner;
        this.ownerId = aOwnerId;
        this.setSettings(aSettings);
        this.villageId = aVillageId;
        this.setMaxAndMin();
    }
    
    Structure(final long id) throws IOException, NoSuchStructureException {
        this.buildTiles = new HashSet<BuildTile>();
        this.minX = 1 << Constants.meshSize;
        this.maxX = 0;
        this.minY = 1 << Constants.meshSize;
        this.maxY = 0;
        this.isLoading = false;
        this.hasLoaded = false;
        this.finished = false;
        this.writid = -10L;
        this.finalfinished = false;
        this.allowsVillagers = false;
        this.allowsAllies = false;
        this.allowsKingdom = false;
        this.planner = "";
        this.ownerId = -10L;
        this.permissions = new Permissions();
        this.villageId = -1;
        this.structureType = 0;
        this.lastPolled = System.currentTimeMillis();
        this.wurmId = id;
        this.structureTiles = new HashSet<VolaTile>();
        this.load();
        this.setMaxAndMin();
    }
    
    public final void addBuildTile(final BuildTile toadd) {
        this.buildTiles.add(toadd);
    }
    
    public final void clearBuildTiles() {
        this.buildTiles.clear();
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final void setPlanner(final String newPlanner) {
        this.planner = newPlanner;
    }
    
    public final String getPlanner() {
        return this.planner;
    }
    
    final void setSettings(final int newSettings) {
        this.permissions.setPermissionBits(newSettings);
    }
    
    public final Permissions getSettings() {
        return this.permissions;
    }
    
    public final void setName(final String aName, final boolean saveIt) {
        this.name = aName.substring(0, Math.min(255, aName.length()));
        final VolaTile[] vtiles = this.getStructureTiles();
        for (int x = 0; x < vtiles.length; ++x) {
            vtiles[x].changeStructureName(aName);
        }
        if (saveIt) {
            try {
                this.saveName();
            }
            catch (IOException e) {
                Structure.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    @Override
    public final String getTypeName() {
        return "Building";
    }
    
    @Override
    public final String getObjectName() {
        return this.name;
    }
    
    @Override
    public final boolean setObjectName(final String newName, final Creature creature) {
        if (this.writid != -10L) {
            try {
                final Item writ = Items.getItem(this.getWritId());
                if (writ.getOwnerId() != creature.getWurmId()) {
                    return false;
                }
                writ.setDescription(newName);
            }
            catch (NoSuchItemException nsi) {
                this.writid = -10L;
                try {
                    this.saveWritId();
                }
                catch (IOException e) {
                    Structure.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        this.setName(newName, false);
        return true;
    }
    
    final void setRoof(final byte aRoof) {
        this.roof = aRoof;
    }
    
    public final byte getRoof() {
        return this.roof;
    }
    
    public final long getOwnerId() {
        if (this.writid != -10L) {
            try {
                final Item writ = Items.getItem(this.writid);
                if (this.ownerId != writ.getOwnerId()) {
                    this.ownerId = writ.getOwnerId();
                    this.saveOwnerId();
                }
                return writ.getOwnerId();
            }
            catch (NoSuchItemException nsi) {
                this.setWritid(-10L, true);
            }
            catch (IOException ioe) {
                Structure.logger.log(Level.WARNING, ioe.getMessage(), ioe);
            }
        }
        if (this.ownerId == -10L && this.planner.length() > 0) {
            final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithName(this.planner);
            if (pInfo != null) {
                this.ownerId = pInfo.wurmId;
                try {
                    this.saveOwnerId();
                }
                catch (IOException ioe2) {
                    Structure.logger.log(Level.WARNING, ioe2.getMessage(), ioe2);
                }
            }
        }
        return this.ownerId;
    }
    
    final void setOwnerId(final long newOwnerId) {
        this.ownerId = newOwnerId;
    }
    
    public final int getVillageId() {
        return this.villageId;
    }
    
    public final void setVillageId(final int newVillageId) {
        this.villageId = newVillageId;
    }
    
    public final Village getManagedByVillage() {
        if (this.villageId >= 0) {
            try {
                return Villages.getVillage(this.villageId);
            }
            catch (NoSuchVillageException ex) {}
        }
        return null;
    }
    
    public final long getWritId() {
        return this.writid;
    }
    
    public boolean isEnemy(final Creature creature) {
        if (creature.getPower() > 1) {
            return false;
        }
        if (this.isGuest(creature)) {
            return false;
        }
        final Village vil = this.getPermissionsVillage();
        if (vil != null) {
            return vil.isEnemy(creature);
        }
        return !this.isSameKingdom(creature);
    }
    
    public boolean isEnemyAllowed(final Creature creature, final short action) {
        final Village v = this.getVillage();
        if (v != null && Actions.actionEntrys[action] != null) {
            if (Actions.actionEntrys[action].isEnemyAllowedWhenNoGuards() && v.getGuards().length != 0) {
                return false;
            }
            if (Actions.actionEntrys[action].isEnemyNeverAllowed()) {
                return false;
            }
            if (Actions.actionEntrys[action].isEnemyAlwaysAllowed()) {
                return true;
            }
        }
        return true;
    }
    
    public boolean mayLockPick(final Creature creature) {
        if (Servers.isThisAPvpServer() && this.isEnemyAllowed(creature, (short)101)) {
            return true;
        }
        final Village v = (this.getManagedByVillage() == null) ? this.getVillage() : this.getManagedByVillage();
        if (v != null) {
            return v.getRoleFor(creature).mayPickLocks();
        }
        return this.mayManage(creature) || Servers.isThisAPvpServer();
    }
    
    @Override
    public boolean isCitizen(final Creature creature) {
        final Village vil = this.getPermissionsVillage();
        return vil != null && vil.isCitizen(creature);
    }
    
    public boolean isActionAllowed(final Creature performer, final short action) {
        if (performer.getPower() > 1) {
            return true;
        }
        if (this.isEnemy(performer) && !this.isEnemyAllowed(performer, action)) {
            return false;
        }
        if (Actions.isActionAttachLock(action)) {
            return this.isEnemy(performer) || this.mayManage(performer);
        }
        if (Actions.isActionChangeBuilding(action)) {
            return this.mayManage(performer);
        }
        if (Actions.isActionLockPick(action)) {
            return this.mayLockPick(performer);
        }
        if (Actions.isActionTake(action) || Actions.isActionPullPushTurn(action) || 671 == action || 672 == action || Actions.isActionPick(action)) {
            return this.isEnemy(performer) || this.mayPickup(performer);
        }
        if (Actions.isActionPickupPlanted(action)) {
            return this.mayPickupPlanted(performer);
        }
        if (Actions.isActionPlaceMerchants(action)) {
            return this.mayPlaceMerchants(performer);
        }
        if (Actions.isActionDestroy(action) || Actions.isActionBuild(action) || Actions.isActionDestroyFence(action) || Actions.isActionPlanBuilding(action) || Actions.isActionPack(action) || Actions.isActionPave(action) || Actions.isActionDestroyItem(action)) {
            return this.isEnemy(performer) || this.mayModify(performer);
        }
        if (Actions.isActionLoad(action) || Actions.isActionUnload(action)) {
            return this.mayLoad(performer);
        }
        return (Actions.isActionDrop(action) && this.isEnemy(performer)) || (!Actions.isActionImproveOrRepair(action) && !Actions.isActionDrop(action)) || this.isEnemy(performer) || this.mayPass(performer);
    }
    
    @Override
    public boolean isAllied(final Creature creature) {
        final Village vil = this.getPermissionsVillage();
        return vil != null && vil.isAlly(creature);
    }
    
    @Override
    public boolean isSameKingdom(final Creature creature) {
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            return vill.kingdom == creature.getKingdomId();
        }
        return Players.getInstance().getKingdomForPlayer(this.getOwnerId()) == creature.getKingdomId();
    }
    
    public Village getVillage() {
        final Village village = Villages.getVillage(this.getMinX(), this.getMinY(), this.isSurfaced());
        if (village == null) {
            return null;
        }
        Village v = Villages.getVillage(this.getMinX(), this.getMaxY(), this.isSurfaced());
        if (v == null || v.getId() != village.getId()) {
            return null;
        }
        v = Villages.getVillage(this.getMaxX(), this.getMaxY(), this.isSurfaced());
        if (v == null || v.getId() != village.getId()) {
            return null;
        }
        v = Villages.getVillage(this.getMinX(), this.getMinY(), this.isSurfaced());
        if (v == null || v.getId() != village.getId()) {
            return null;
        }
        return village;
    }
    
    private Village getPermissionsVillage() {
        final Village vill = this.getManagedByVillage();
        if (vill != null) {
            return vill;
        }
        final long wid = this.getOwnerId();
        if (wid != -10L) {
            return Villages.getVillageForCreature(wid);
        }
        return null;
    }
    
    private String getVillageName(final Player player) {
        String sName = "";
        final Village vill = this.getVillage();
        if (vill != null) {
            sName = vill.getName();
        }
        else {
            sName = player.getVillageName();
        }
        return sName;
    }
    
    @Override
    public boolean canChangeName(final Creature creature) {
        return creature.getPower() > 1 || this.isOwner(creature.getWurmId());
    }
    
    @Override
    public boolean canChangeOwner(final Creature creature) {
        return (this.isActualOwner(creature.getWurmId()) || creature.getPower() > 1) && this.writid == -10L;
    }
    
    @Override
    public String getWarning() {
        if (!this.isFinished()) {
            return "NEEDS TO BE COMPLETE FOR INTERIOR PERMISSIONS TO WORK";
        }
        return "";
    }
    
    @Override
    public PermissionsPlayerList getPermissionsPlayerList() {
        return StructureSettings.getPermissionsPlayerList(this.getWurmId());
    }
    
    public Floor[] getFloors() {
        final Set<Floor> floors = new HashSet<Floor>();
        for (final VolaTile tile : this.structureTiles) {
            final Floor[] fArr = tile.getFloors();
            for (int x = 0; x < fArr.length; ++x) {
                floors.add(fArr[x]);
            }
        }
        final Floor[] toReturn = new Floor[floors.size()];
        return floors.toArray(toReturn);
    }
    
    public Floor[] getFloorsAtTile(final int tilex, final int tiley, final int offsetHeightStart, final int offsetHeightEnd) {
        final Set<Floor> floors = new HashSet<Floor>();
        for (final VolaTile tile : this.structureTiles) {
            if (tile.getTileX() == tilex && tile.getTileY() == tiley) {
                final Floor[] fArr = tile.getFloors(offsetHeightStart, offsetHeightEnd);
                for (int x = 0; x < fArr.length; ++x) {
                    floors.add(fArr[x]);
                }
            }
        }
        final Floor[] toReturn = new Floor[floors.size()];
        return floors.toArray(toReturn);
    }
    
    public final Wall[] getWalls() {
        final Set<Wall> walls = new HashSet<Wall>();
        for (final VolaTile tile : this.structureTiles) {
            final Wall[] wArr = tile.getWalls();
            for (int x = 0; x < wArr.length; ++x) {
                walls.add(wArr[x]);
            }
        }
        final Wall[] toReturn = new Wall[walls.size()];
        return walls.toArray(toReturn);
    }
    
    public final Wall[] getExteriorWalls() {
        final Set<Wall> walls = new HashSet<Wall>();
        for (final VolaTile tile : this.structureTiles) {
            final Wall[] wArr = tile.getExteriorWalls();
            for (int x = 0; x < wArr.length; ++x) {
                walls.add(wArr[x]);
            }
        }
        final Wall[] toReturn = new Wall[walls.size()];
        return walls.toArray(toReturn);
    }
    
    public BridgePart[] getBridgeParts() {
        final Set<BridgePart> bridgeParts = new HashSet<BridgePart>();
        for (final VolaTile tile : this.structureTiles) {
            final BridgePart[] fArr = tile.getBridgeParts();
            for (int x = 0; x < fArr.length; ++x) {
                bridgeParts.add(fArr[x]);
            }
        }
        final BridgePart[] toReturn = new BridgePart[bridgeParts.size()];
        return bridgeParts.toArray(toReturn);
    }
    
    public final VolaTile getTileFor(final Wall wall) {
        for (int xx = 1; xx >= -1; --xx) {
            for (int yy = 1; yy >= -1; --yy) {
                try {
                    final Zone zone = Zones.getZone(wall.tilex + xx, wall.tiley + yy, this.surfaced);
                    final VolaTile tile = zone.getTileOrNull(wall.tilex + xx, wall.tiley + yy);
                    if (tile != null) {
                        final Wall[] walls = tile.getWalls();
                        for (int s = 0; s < walls.length; ++s) {
                            if (walls[s] == wall) {
                                return tile;
                            }
                        }
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        return null;
    }
    
    public final void poll(final long time) {
        if (time - this.lastPolled > 3600000L) {
            this.lastPolled = System.currentTimeMillis();
            if (!this.isFinalized()) {
                if (time - this.creationDate > 10800000L) {
                    Structure.logger.log(Level.INFO, "Deleting unfinished structure " + this.getName());
                    this.totallyDestroy();
                }
            }
            else {
                boolean destroy = false;
                if (time - this.creationDate > 172800000L) {
                    destroy = true;
                    if (this.structureType == 0) {
                        if (this.hasWalls()) {
                            destroy = false;
                        }
                    }
                    else if (this.getBridgeParts().length != 0) {
                        destroy = false;
                    }
                }
                if (destroy) {
                    this.totallyDestroy();
                }
            }
        }
    }
    
    public final boolean hasWalls() {
        for (final VolaTile tile : this.structureTiles) {
            final Wall[] wallArr = tile.getWalls();
            for (int x = 0; x < wallArr.length; ++x) {
                if (wallArr[x].getType() != StructureTypeEnum.PLAN) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final void totallyDestroy() {
        Players.getInstance().setStructureFinished(this.wurmId);
        if (this.isFinalized()) {
            Label_0090: {
                if (this.getWritId() == -10L) {
                    if (this.structureType == 1) {
                        break Label_0090;
                    }
                }
                try {
                    final Item writ = Items.getItem(this.getWritId());
                    try {
                        Server.getInstance().getCreature(writ.getOwnerId());
                        Items.destroyItem(this.getWritId());
                    }
                    catch (NoSuchCreatureException nsc) {
                        Items.decay(this.getWritId(), null);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Items.decay(this.getWritId(), null);
                    }
                }
                catch (NoSuchItemException ex) {}
            }
            if (this.structureType == 0) {
                for (final VolaTile vt : this.structureTiles) {
                    final VolaTile vtNorth = Zones.getTileOrNull(vt.getTileX(), vt.getTileY() - 1, vt.isOnSurface());
                    if (vtNorth != null) {
                        final Structure structNorth = vtNorth.getStructure();
                        if (structNorth != null && structNorth.isTypeBridge()) {
                            final BridgePart[] bps = vtNorth.getBridgeParts();
                            if (bps.length == 1 && bps[0].hasHouseSouthExit()) {
                                structNorth.totallyDestroy();
                            }
                        }
                    }
                    final VolaTile vtEast = Zones.getTileOrNull(vt.getTileX() + 1, vt.getTileY(), vt.isOnSurface());
                    if (vtEast != null) {
                        final Structure structEast = vtEast.getStructure();
                        if (structEast != null && structEast.isTypeBridge()) {
                            final BridgePart[] bps2 = vtEast.getBridgeParts();
                            if (bps2.length == 1 && bps2[0].hasHouseWestExit()) {
                                structEast.totallyDestroy();
                            }
                        }
                    }
                    final VolaTile vtSouth = Zones.getTileOrNull(vt.getTileX(), vt.getTileY() + 1, vt.isOnSurface());
                    if (vtSouth != null) {
                        final Structure structSouth = vtSouth.getStructure();
                        if (structSouth != null && structSouth.isTypeBridge()) {
                            final BridgePart[] bps3 = vtSouth.getBridgeParts();
                            if (bps3.length == 1 && bps3[0].hasHouseNorthExit()) {
                                structSouth.totallyDestroy();
                            }
                        }
                    }
                    final VolaTile vtWest = Zones.getTileOrNull(vt.getTileX() - 1, vt.getTileY(), vt.isOnSurface());
                    if (vtWest != null) {
                        final Structure structWest = vtWest.getStructure();
                        if (structWest == null || !structWest.isTypeBridge()) {
                            continue;
                        }
                        final BridgePart[] bps4 = vtWest.getBridgeParts();
                        if (bps4.length != 1 || !bps4[0].hasHouseEastExit()) {
                            continue;
                        }
                        structWest.totallyDestroy();
                    }
                }
            }
            MissionTargets.destroyStructureTargets(this.getWurmId(), null);
        }
        for (final VolaTile tile : this.structureTiles) {
            tile.deleteStructure(this.getWurmId());
        }
        this.remove();
        this.delete();
    }
    
    public final boolean hasBridgeEntrance() {
        for (final VolaTile vt : this.structureTiles) {
            if (vt.isOnSurface()) {
                final VolaTile vtNorth = Zones.getTileOrNull(vt.getTileX(), vt.getTileY() - 1, vt.isOnSurface());
                if (vtNorth != null) {
                    final Structure structNorth = vtNorth.getStructure();
                    if (structNorth != null && structNorth.isTypeBridge()) {
                        final BridgePart[] bps = vtNorth.getBridgeParts();
                        if (bps.length == 1 && bps[0].hasHouseSouthExit()) {
                            return true;
                        }
                    }
                }
                final VolaTile vtEast = Zones.getTileOrNull(vt.getTileX() + 1, vt.getTileY(), vt.isOnSurface());
                if (vtEast != null) {
                    final Structure structEast = vtEast.getStructure();
                    if (structEast != null && structEast.isTypeBridge()) {
                        final BridgePart[] bps2 = vtEast.getBridgeParts();
                        if (bps2.length == 1 && bps2[0].hasHouseWestExit()) {
                            return true;
                        }
                    }
                }
                final VolaTile vtSouth = Zones.getTileOrNull(vt.getTileX(), vt.getTileY() + 1, vt.isOnSurface());
                if (vtSouth != null) {
                    final Structure structSouth = vtSouth.getStructure();
                    if (structSouth != null && structSouth.isTypeBridge()) {
                        final BridgePart[] bps3 = vtSouth.getBridgeParts();
                        if (bps3.length == 1 && bps3[0].hasHouseNorthExit()) {
                            return true;
                        }
                    }
                }
                final VolaTile vtWest = Zones.getTileOrNull(vt.getTileX() - 1, vt.getTileY(), vt.isOnSurface());
                if (vtWest == null) {
                    continue;
                }
                final Structure structWest = vtWest.getStructure();
                if (structWest == null || !structWest.isTypeBridge()) {
                    continue;
                }
                final BridgePart[] bps4 = vtWest.getBridgeParts();
                if (bps4.length == 1 && bps4[0].hasHouseEastExit()) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public final void remove() {
        if (this.structureTiles.size() > 0) {
            final Zone[] zones = Zones.getZonesCoveredBy(this.minX, this.minY, this.maxX, this.maxY, this.surfaced);
            for (int x = 0; x < zones.length; ++x) {
                zones[x].removeStructure(this);
            }
        }
        Structures.removeStructure(this.wurmId);
    }
    
    @Override
    public final boolean canHavePermissions() {
        return true;
    }
    
    @Override
    public final boolean mayShowPermissions(final Creature creature) {
        return this.mayManage(creature);
    }
    
    public final boolean canManage(final Creature creature) {
        if (StructureSettings.isExcluded(this, creature)) {
            return false;
        }
        if (StructureSettings.canManage(this, creature)) {
            return true;
        }
        if (creature.getCitizenVillage() == null) {
            return false;
        }
        final Village vill = this.getManagedByVillage();
        return vill != null && vill.isCitizen(creature) && vill.isActionAllowed((short)664, creature);
    }
    
    public boolean mayManage(final Creature creature) {
        return creature.getPower() > 1 || this.canManage(creature);
    }
    
    public final boolean maySeeHistory(final Creature creature) {
        return creature.getPower() > 1 || this.isOwner(creature);
    }
    
    public final boolean mayModify(final Creature creature) {
        return !StructureSettings.isExcluded(this, creature) && StructureSettings.mayModify(this, creature);
    }
    
    final boolean isExcluded(final Creature creature) {
        return StructureSettings.isExcluded(this, creature);
    }
    
    public final boolean mayPass(final Creature creature) {
        return !StructureSettings.isExcluded(this, creature) && StructureSettings.mayPass(this, creature);
    }
    
    public final boolean mayPickup(final Creature creature) {
        return this.isEnemy(creature) || (!StructureSettings.isExcluded(this, creature) && StructureSettings.mayPickup(this, creature));
    }
    
    @Override
    public boolean isGuest(final Creature creature) {
        return this.isGuest(creature.getWurmId());
    }
    
    @Override
    public boolean isGuest(final long playerId) {
        return StructureSettings.isGuest(this, playerId);
    }
    
    @Override
    public final void addGuest(final long guestId, final int aSettings) {
        StructureSettings.addPlayer(this.getWurmId(), guestId, aSettings);
    }
    
    @Override
    public final void removeGuest(final long guestId) {
        StructureSettings.removePlayer(this.getWurmId(), guestId);
    }
    
    public final long getCreationDate() {
        return this.creationDate;
    }
    
    public final int getSize() {
        return this.structureTiles.size();
    }
    
    public final int getLimit() {
        return this.structureTiles.size() + this.getExteriorWalls().length;
    }
    
    public final int getLimitFor(final int tilex, final int tiley, final boolean onSurface, final boolean adding) {
        final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley, onSurface);
        int points = this.getLimit();
        if (this.contains(tilex, tiley) && adding) {
            return points;
        }
        int newTilePoints = 5;
        if (adding) {
            final Set<VolaTile> neighbors = createNeighbourStructureTiles(this, newTile);
            newTilePoints -= neighbors.size();
            points -= neighbors.size();
            return points + newTilePoints;
        }
        if (this.contains(tilex, tiley)) {
            final Set<VolaTile> neighbors = createNeighbourStructureTiles(this, newTile);
            newTilePoints -= neighbors.size();
            points += neighbors.size();
            return points - newTilePoints;
        }
        return points;
    }
    
    private void setMaxAndMin() {
        this.maxX = 0;
        this.minX = 1 << Constants.meshSize;
        this.maxY = 0;
        this.minY = 1 << Constants.meshSize;
        if (this.structureTiles != null) {
            for (final VolaTile tile : this.structureTiles) {
                final int xx = tile.getTileX();
                final int yy = tile.getTileY();
                if (xx > this.maxX) {
                    this.maxX = xx;
                }
                if (xx < this.minX) {
                    this.minX = xx;
                }
                if (yy > this.maxY) {
                    this.maxY = yy;
                }
                if (yy < this.minY) {
                    this.minY = yy;
                }
            }
        }
    }
    
    static final StructureBounds getStructureBounds(final List<Wall> structureWalls) {
        return null;
    }
    
    final StructureBounds secureOuterWalls(final List<Wall> structureWalls) {
        final TilePoint max = new TilePoint(0, 0);
        final TilePoint min = new TilePoint(Zones.worldTileSizeX, Zones.worldTileSizeY);
        final StructureBounds structBounds = new StructureBounds(max, min);
        for (final Wall wall : structureWalls) {
            if (wall.getStartX() > structBounds.max.getTileX()) {
                structBounds.getMax().setTileX(wall.getStartX());
            }
            if (wall.getStartY() > structBounds.max.getTileY()) {
                structBounds.getMax().setTileY(wall.getStartY());
            }
            if (wall.getStartX() < structBounds.min.getTileX()) {
                structBounds.getMin().setTileX(wall.getStartX());
            }
            if (wall.getStartY() < structBounds.min.getTileY()) {
                structBounds.getMin().setTileY(wall.getStartY());
            }
        }
        return structBounds;
    }
    
    private void fixWalls(final VolaTile tile) {
        for (final BuildTile bt : this.buildTiles) {
            if (bt.getTileX() == tile.getTileX() && bt.getTileY() == tile.getTileY() && tile.isOnSurface() == (bt.getLayer() == 0)) {
                return;
            }
        }
        for (final Wall wall : tile.getWalls()) {
            final int x = tile.getTileX();
            final int y = tile.getTileY();
            int newTileX = 0;
            int newTileY = 0;
            boolean found = false;
            Structure s = null;
            if (wall.isHorizontal()) {
                s = Structures.getStructureForTile(x, y - 1, tile.isOnSurface());
                if (s != null && s.isTypeHouse()) {
                    newTileX = x;
                    newTileY = y - 1;
                    found = true;
                }
                s = Structures.getStructureForTile(x, y + 1, tile.isOnSurface());
                if (s != null && s.isTypeHouse()) {
                    newTileX = x;
                    newTileY = y + 1;
                    found = true;
                }
            }
            else {
                s = Structures.getStructureForTile(x - 1, y, tile.isOnSurface());
                if (s != null && s.isTypeHouse()) {
                    newTileX = x - 1;
                    newTileY = y;
                    found = true;
                }
                s = Structures.getStructureForTile(x + 1, y, tile.isOnSurface());
                if (s != null && s.isTypeHouse()) {
                    newTileX = x + 1;
                    newTileY = y;
                    found = true;
                }
            }
            if (!found) {
                Structure.logger.log(Level.WARNING, StringUtil.format("Wall with WALL.ID = %d is orphan, but belongs to structure %d. Does the structure exist?", wall.getNumber(), wall.getStructureId()));
                return;
            }
            final VolaTile t = Zones.getTileOrNull(newTileX, newTileY, tile.isOnSurface());
            tile.removeWall(wall, true);
            wall.setTile(newTileX, newTileY);
            t.addWall(wall);
            Structure.logger.log(Level.WARNING, StringUtil.format("fixWalls found a wall %d at %d,%d and moved it to %d,%d for structure %d", wall.getNumber(), x, y, newTileX, newTileY, wall.getStructureId()));
        }
    }
    
    final boolean loadStructureTiles(final List<Wall> structureWalls) {
        boolean toReturn = true;
        if (!this.buildTiles.isEmpty()) {
            toReturn = false;
            for (final BuildTile buildTile : this.buildTiles) {
                try {
                    final Zone zone = Zones.getZone(buildTile.getTileX(), buildTile.getTileY(), buildTile.getLayer() == 0);
                    final VolaTile tile = zone.getOrCreateTile(buildTile.getTileX(), buildTile.getTileY());
                    this.addBuildTile(tile, true);
                }
                catch (NoSuchZoneException nsz) {
                    Structure.logger.log(Level.WARNING, "Structure with id " + this.wurmId + " is built on the edge of the world at " + buildTile.getTileX() + ", " + buildTile.getTileY(), nsz);
                }
            }
        }
        int tilex = 0;
        int tiley = 0;
        for (final Wall wall : structureWalls) {
            try {
                tilex = wall.getTileX();
                tiley = wall.getTileY();
                final Zone zone2 = Zones.getZone(tilex, tiley, this.isSurfaced());
                final VolaTile tile2 = zone2.getOrCreateTile(tilex, tiley);
                tile2.addWall(wall);
                if (!this.structureTiles.contains(tile2)) {
                    Structure.logger.log(Level.WARNING, "Wall with  WURMZONES.WALLS.ID =" + wall.getId() + " exists outside a structure! ");
                    this.fixWalls(tile2);
                }
            }
            catch (NoSuchZoneException nsz2) {
                Structure.logger.log(Level.WARNING, "Failed to locate zone at " + tilex + ", " + tiley);
            }
            if (wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR) {
                if (this.doors == null) {
                    this.doors = new HashSet<Door>();
                }
                final Door door = new DbDoor(wall);
                this.addDoor(door);
                door.addToTiles();
                try {
                    door.load();
                }
                catch (IOException e) {
                    Structure.logger.log(Level.WARNING, "Failed to load a door: " + e.getMessage(), e);
                }
            }
        }
        this.buildTiles.clear();
        return toReturn;
    }
    
    final boolean fillHoles() {
        final int numTiles = this.structureTiles.size() + 3;
        final Set<VolaTile> tilesToAdd = new HashSet<VolaTile>();
        final Set<VolaTile> tilesChecked = new HashSet<VolaTile>();
        final Set<VolaTile> tilesRemaining = new HashSet<VolaTile>();
        tilesRemaining.addAll(this.structureTiles);
        int iterations = 0;
        while (iterations++ < numTiles) {
            for (final VolaTile tile : tilesRemaining) {
                tilesChecked.add(tile);
                final Wall[] walls = tile.getWalls();
                boolean checkNorth = true;
                boolean checkEast = true;
                boolean checkSouth = true;
                boolean checkWest = true;
                for (int x = 0; x < walls.length; ++x) {
                    if (!walls[x].isIndoor()) {
                        if (walls[x].getHeight() > 0) {
                            Structure.logger.log(Level.INFO, "Wall at " + tile.getTileX() + "," + tile.getTileY() + " not indoor at height " + walls[x].getHeight());
                        }
                        if (walls[x].isHorizontal()) {
                            if (walls[x].getStartY() == tile.getTileY()) {
                                checkNorth = false;
                            }
                            else {
                                checkSouth = false;
                            }
                        }
                        else if (walls[x].getStartX() == tile.getTileX()) {
                            checkWest = false;
                        }
                        else {
                            checkEast = false;
                        }
                    }
                }
                if (checkNorth) {
                    try {
                        final VolaTile t = Zones.getZone(tile.tilex, tile.tiley - 1, this.surfaced).getOrCreateTile(tile.tilex, tile.tiley - 1);
                        if (!this.structureTiles.contains(t) && !tilesToAdd.contains(t)) {
                            tilesToAdd.add(t);
                        }
                    }
                    catch (NoSuchZoneException nsz2) {
                        Structure.logger.log(Level.WARNING, "CN Structure with id " + this.wurmId + " is built on the edge of the world at " + tile.getTileX() + ", " + tile.getTileY());
                    }
                }
                if (checkEast) {
                    try {
                        final VolaTile t = Zones.getZone(tile.tilex + 1, tile.tiley, this.surfaced).getOrCreateTile(tile.tilex + 1, tile.tiley);
                        if (!this.structureTiles.contains(t) && !tilesToAdd.contains(t)) {
                            tilesToAdd.add(t);
                        }
                    }
                    catch (NoSuchZoneException nsz2) {
                        Structure.logger.log(Level.WARNING, "CE Structure with id " + this.wurmId + " is built on the edge of the world at " + tile.getTileX() + ", " + tile.getTileY());
                    }
                }
                if (checkWest) {
                    try {
                        final VolaTile t = Zones.getZone(tile.tilex - 1, tile.tiley, this.surfaced).getOrCreateTile(tile.tilex - 1, tile.tiley);
                        if (!this.structureTiles.contains(t) && !tilesToAdd.contains(t)) {
                            tilesToAdd.add(t);
                        }
                    }
                    catch (NoSuchZoneException nsz2) {
                        Structure.logger.log(Level.WARNING, "CW Structure with id " + this.wurmId + " is built on the edge of the world at " + tile.getTileX() + ", " + tile.getTileY());
                    }
                }
                if (checkSouth) {
                    try {
                        final VolaTile t = Zones.getZone(tile.tilex, tile.tiley + 1, this.surfaced).getOrCreateTile(tile.tilex, tile.tiley + 1);
                        if (this.structureTiles.contains(t) || tilesToAdd.contains(t)) {
                            continue;
                        }
                        tilesToAdd.add(t);
                    }
                    catch (NoSuchZoneException nsz2) {
                        Structure.logger.log(Level.WARNING, "CS Structure with id " + this.wurmId + " is built on the edge of the world at " + tile.getTileX() + ", " + tile.getTileY());
                    }
                }
            }
            tilesRemaining.removeAll(tilesChecked);
            if (tilesToAdd.size() <= 0) {
                return false;
            }
            for (final VolaTile tile : tilesToAdd) {
                try {
                    if (tile.getTileX() > this.maxX) {
                        this.maxX = tile.getTileX();
                    }
                    if (tile.getTileX() < this.minX) {
                        this.minX = tile.getTileX();
                    }
                    if (tile.getTileY() > this.maxY) {
                        this.maxY = tile.getTileY();
                    }
                    if (tile.getTileY() < this.minY) {
                        this.minY = tile.getTileY();
                    }
                    final Zone zone = Zones.getZone(tile.getTileX(), tile.getTileY(), this.isSurfaced());
                    zone.addStructure(this);
                    this.structureTiles.add(tile);
                    this.addNewBuildTile(tile.getTileX(), tile.getTileY(), tile.getLayer());
                    tile.setStructureAtLoad(this);
                }
                catch (NoSuchZoneException nsz) {
                    Structure.logger.log(Level.WARNING, "Structure with id " + this.wurmId + " is built on the edge of the world at " + tile.getTileX() + ", " + tile.getTileY(), nsz);
                }
            }
            tilesRemaining.addAll(tilesToAdd);
            tilesToAdd.clear();
        }
        Structure.logger.log(Level.WARNING, "Iterations went over " + numTiles + " for " + this.getName() + " at " + this.getCenterX() + ", " + this.getCenterY());
        return false;
    }
    
    static final boolean isEqual(final Structure struct1, final Structure struct2) {
        return struct1 != null && struct2 != null && struct1.getWurmId() == struct2.getWurmId();
    }
    
    static final Set<VolaTile> createNeighbourStructureTiles(final Structure struct, final VolaTile modifiedTile) {
        final Set<VolaTile> toReturn = new HashSet<VolaTile>();
        VolaTile t = Zones.getTileOrNull(modifiedTile.getTileX() + 1, modifiedTile.getTileY(), modifiedTile.isOnSurface());
        if (t != null && isEqual(t.getStructure(), struct)) {
            toReturn.add(t);
        }
        t = Zones.getTileOrNull(modifiedTile.getTileX(), modifiedTile.getTileY() + 1, modifiedTile.isOnSurface());
        if (t != null && isEqual(t.getStructure(), struct)) {
            toReturn.add(t);
        }
        t = Zones.getTileOrNull(modifiedTile.getTileX(), modifiedTile.getTileY() - 1, modifiedTile.isOnSurface());
        if (t != null && isEqual(t.getStructure(), struct)) {
            toReturn.add(t);
        }
        t = Zones.getTileOrNull(modifiedTile.getTileX() - 1, modifiedTile.getTileY(), modifiedTile.isOnSurface());
        if (t != null && isEqual(t.getStructure(), struct)) {
            toReturn.add(t);
        }
        return toReturn;
    }
    
    public static void adjustWallsAroundAddedStructureTile(final Structure structure, final int tilex, final int tiley) {
        final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley, structure.isOnSurface());
        final Set<VolaTile> neighbourTiles = createNeighbourStructureTiles(structure, newTile);
        structure.adjustSurroundingWallsAddedStructureTile(tilex, tiley, neighbourTiles);
    }
    
    public static void adjustWallsAroundRemovedStructureTile(final Structure structure, final int tilex, final int tiley) {
        final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley, structure.isOnSurface());
        final Set<VolaTile> neighbourTiles = createNeighbourStructureTiles(structure, newTile);
        structure.adjustSurroundingWallsRemovedStructureTile(tilex, tiley, neighbourTiles);
    }
    
    public void updateWallIsInner(final Structure localStructure, final VolaTile volaTile, final Wall wall, final boolean isInner) {
        if (localStructure.getWurmId() != this.getWurmId()) {
            Structure.logger.log(Level.WARNING, "Warning structures too close to eachother: " + localStructure.getWurmId() + " and " + this.getWurmId() + " at " + volaTile.getTileX() + "," + volaTile.getTileY());
            return;
        }
        if (wall.getHeight() > 0) {
            wall.setIndoor(true);
        }
        else {
            wall.setIndoor(isInner);
        }
        volaTile.updateWall(wall);
    }
    
    public void adjustSurroundingWallsAddedStructureTile(final int tilex, final int tiley, final Set<VolaTile> neighbourTiles) {
        final VolaTile newTile = Zones.getOrCreateTile(tilex, tiley, this.isOnSurface());
        for (final VolaTile neighbourTile : neighbourTiles) {
            final Structure localStructure = neighbourTile.getStructure();
            final Wall[] walls2;
            final Wall[] walls = walls2 = neighbourTile.getWalls();
            for (final Wall wall : walls2) {
                if (wall.isHorizontal() && wall.getStartY() == tiley && wall.getEndY() == tiley && wall.getStartX() == tilex && wall.getEndX() == tilex + 1) {
                    if (this.isFree(tilex, tiley - 1)) {
                        this.updateWallIsInner(localStructure, newTile, wall, false);
                    }
                    else {
                        this.updateWallIsInner(localStructure, newTile, wall, true);
                    }
                }
                if (wall.isHorizontal() && wall.getStartY() == tiley + 1 && wall.getEndY() == tiley + 1 && wall.getStartX() == tilex && wall.getEndX() == tilex + 1) {
                    if (this.isFree(tilex, tiley + 1)) {
                        this.updateWallIsInner(localStructure, newTile, wall, false);
                    }
                    else {
                        this.updateWallIsInner(localStructure, newTile, wall, true);
                    }
                }
                if (!wall.isHorizontal() && wall.getStartX() == tilex + 1 && wall.getEndX() == tilex + 1 && wall.getStartY() == tiley && wall.getEndY() == tiley + 1) {
                    if (this.isFree(tilex + 1, tiley)) {
                        this.updateWallIsInner(localStructure, newTile, wall, false);
                    }
                    else {
                        this.updateWallIsInner(localStructure, newTile, wall, true);
                    }
                }
                if (!wall.isHorizontal() && wall.getStartX() == tilex && wall.getEndX() == tilex && wall.getStartY() == tiley && wall.getEndY() == tiley + 1) {
                    if (this.isFree(tilex - 1, tiley)) {
                        this.updateWallIsInner(localStructure, newTile, wall, false);
                    }
                    else {
                        this.updateWallIsInner(localStructure, newTile, wall, true);
                    }
                }
            }
        }
    }
    
    public void adjustSurroundingWallsRemovedStructureTile(final int tilex, final int tiley, final Set<VolaTile> neighbourTiles) {
        final VolaTile removedTile = Zones.getOrCreateTile(tilex, tiley, this.isOnSurface());
        for (final VolaTile neighbourTile : neighbourTiles) {
            final Structure localStructure = neighbourTile.getStructure();
            final Wall[] walls2;
            final Wall[] walls = walls2 = neighbourTile.getWalls();
            for (final Wall wall : walls2) {
                if (wall.isHorizontal() && wall.getStartX() == tilex && wall.getEndX() == tilex + 1 && wall.getStartY() == tiley && wall.getEndY() == tiley) {
                    if (this.isFree(tilex, tiley - 1)) {
                        Structure.logger.log(Level.WARNING, "Wall exist.");
                    }
                    else {
                        this.updateWallIsInner(localStructure, removedTile, wall, false);
                    }
                }
                if (wall.isHorizontal() && wall.getStartX() == tilex && wall.getEndX() == tilex + 1 && wall.getStartY() == tiley + 1 && wall.getEndY() == tiley + 1) {
                    if (this.isFree(tilex, tiley + 1)) {
                        Structure.logger.log(Level.WARNING, "Wall exist.");
                    }
                    else {
                        this.updateWallIsInner(localStructure, removedTile, wall, false);
                    }
                }
                if (!wall.isHorizontal() && wall.getStartX() == tilex + 1 && wall.getEndX() == tilex + 1 && wall.getStartY() == tiley && wall.getEndY() == tiley + 1) {
                    if (this.isFree(tilex + 1, tiley)) {
                        Structure.logger.log(Level.WARNING, "Walls exist.");
                    }
                    else {
                        this.updateWallIsInner(localStructure, removedTile, wall, false);
                    }
                }
                if (!wall.isHorizontal() && wall.getStartX() == tilex && wall.getEndX() == tilex && wall.getStartY() == tiley && wall.getEndY() == tiley + 1) {
                    if (this.isFree(tilex - 1, tiley)) {
                        Structure.logger.log(Level.WARNING, "Walls exist.");
                    }
                    else {
                        this.updateWallIsInner(localStructure, removedTile, wall, false);
                    }
                }
            }
        }
    }
    
    public void addMissingWallPlans(final VolaTile tile) {
        boolean lacksNorth = true;
        boolean lacksSouth = true;
        boolean lacksWest = true;
        boolean lacksEast = true;
        for (final Wall w : tile.getWallsForLevel(0)) {
            if (w.isHorizontal()) {
                if (w.getStartY() == tile.tiley) {
                    lacksNorth = false;
                }
                if (w.getStartY() == tile.tiley + 1) {
                    lacksSouth = false;
                }
            }
            else {
                if (w.getStartX() == tile.tilex) {
                    lacksWest = false;
                }
                if (w.getStartX() == tile.tilex + 1) {
                    lacksEast = false;
                }
            }
        }
        if (lacksWest && this.isFree(tile.tilex - 1, tile.tiley)) {
            tile.addWall(StructureTypeEnum.PLAN, tile.tilex, tile.tiley, tile.tilex, tile.tiley + 1, 10.0f, this.wurmId, false);
        }
        if (lacksEast && this.isFree(tile.tilex + 1, tile.tiley)) {
            tile.addWall(StructureTypeEnum.PLAN, tile.tilex + 1, tile.tiley, tile.tilex + 1, tile.tiley + 1, 10.0f, this.wurmId, false);
        }
        if (lacksNorth && this.isFree(tile.tilex, tile.tiley - 1)) {
            tile.addWall(StructureTypeEnum.PLAN, tile.tilex, tile.tiley, tile.tilex + 1, tile.tiley, 10.0f, this.wurmId, false);
        }
        if (lacksSouth && this.isFree(tile.tilex, tile.tiley + 1)) {
            tile.addWall(StructureTypeEnum.PLAN, tile.tilex, tile.tiley + 1, tile.tilex + 1, tile.tiley + 1, 10.0f, this.wurmId, false);
        }
    }
    
    public static final VolaTile expandStructureToTile(final Structure structure, final VolaTile toAdd) throws NoSuchZoneException {
        structure.structureTiles.add(toAdd);
        toAdd.getZone().addStructure(structure);
        return toAdd;
    }
    
    public final void addBuildTile(final VolaTile toAdd, final boolean loading) throws NoSuchZoneException {
        if (toAdd.tilex > this.maxX) {
            this.maxX = toAdd.tilex;
        }
        if (toAdd.tilex < this.minX) {
            this.minX = toAdd.tilex;
        }
        if (toAdd.tiley > this.maxY) {
            this.maxY = toAdd.tiley;
        }
        if (toAdd.tiley < this.minY) {
            this.minY = toAdd.tiley;
        }
        if (this.buildTiles.isEmpty() && this.isFinalized()) {
            this.addNewBuildTile(toAdd.tilex, toAdd.tiley, toAdd.getLayer());
        }
        expandStructureToTile(this, toAdd);
        if (this.structureType == 0) {
            toAdd.addBuildMarker(this);
        }
        else if (loading) {
            toAdd.setStructureAtLoad(this);
        }
    }
    
    private static final VolaTile getFirstNeighbourTileOrNull(final VolaTile structureTile) {
        VolaTile t = Zones.getTileOrNull(structureTile.getTileX() + 1, structureTile.getTileY(), structureTile.isOnSurface());
        if (t != null && t.getStructure() == structureTile.getStructure()) {
            return t;
        }
        t = Zones.getTileOrNull(structureTile.getTileX(), structureTile.getTileY() + 1, structureTile.isOnSurface());
        if (t != null && t.getStructure() == structureTile.getStructure()) {
            return t;
        }
        t = Zones.getTileOrNull(structureTile.getTileX(), structureTile.getTileY() - 1, structureTile.isOnSurface());
        if (t != null && t.getStructure() == structureTile.getStructure()) {
            return t;
        }
        t = Zones.getTileOrNull(structureTile.getTileX() - 1, structureTile.getTileY(), structureTile.isOnSurface());
        if (t != null && t.getStructure() == structureTile.getStructure()) {
            return t;
        }
        return null;
    }
    
    public final boolean testRemove(final VolaTile tileToCheck) {
        if (this.structureTiles.size() <= 2) {
            return true;
        }
        final Set<VolaTile> remainingTiles = new HashSet<VolaTile>();
        final Set<VolaTile> removedTiles = new HashSet<VolaTile>();
        remainingTiles.addAll(this.structureTiles);
        remainingTiles.remove(tileToCheck);
        final VolaTile firstNeighbour = getFirstNeighbourTileOrNull(tileToCheck);
        if (firstNeighbour == null) {
            return true;
        }
        removedTiles.add(firstNeighbour);
        final Set<VolaTile> tilesToRemove = new HashSet<VolaTile>();
        while (true) {
            for (final VolaTile removed : removedTiles) {
                for (final VolaTile remaining : remainingTiles) {
                    if (removed.isNextTo(remaining)) {
                        tilesToRemove.add(remaining);
                    }
                }
            }
            if (tilesToRemove.isEmpty()) {
                break;
            }
            removedTiles.addAll(tilesToRemove);
            remainingTiles.removeAll(tilesToRemove);
            tilesToRemove.clear();
        }
        return remainingTiles.isEmpty();
    }
    
    public final boolean removeTileFromFinishedStructure(final Creature performer, final int tilex, final int tiley, final int layer) {
        if (this.structureTiles == null) {
            return false;
        }
        VolaTile toRemove = null;
        for (final VolaTile tile : this.structureTiles) {
            final int xx = tile.getTileX();
            final int yy = tile.getTileY();
            if (xx == tilex && yy == tiley) {
                toRemove = tile;
                break;
            }
        }
        if (!this.testRemove(toRemove)) {
            return false;
        }
        final Wall[] walls2;
        final Wall[] walls = walls2 = toRemove.getWalls();
        for (final Wall wall : walls2) {
            toRemove.removeWall(wall, false);
            wall.delete();
        }
        final Floor[] floors2;
        final Floor[] floors = floors2 = toRemove.getFloors();
        for (final Floor floor : floors2) {
            toRemove.removeFloor(floor);
            floor.delete();
        }
        this.structureTiles.remove(toRemove);
        this.removeBuildTile(tilex, tiley, layer);
        MethodsStructure.removeBuildMarker(this, tilex, tiley);
        this.setMaxAndMin();
        final VolaTile westTile = Zones.getTileOrNull(toRemove.getTileX() - 1, toRemove.getTileY(), toRemove.isOnSurface());
        if (westTile != null && westTile.getStructure() == this) {
            this.addMissingWallPlans(westTile);
        }
        final VolaTile eastTile = Zones.getTileOrNull(toRemove.getTileX() + 1, toRemove.getTileY(), toRemove.isOnSurface());
        if (eastTile != null && eastTile.getStructure() == this) {
            this.addMissingWallPlans(eastTile);
        }
        final VolaTile northTile = Zones.getTileOrNull(toRemove.getTileX(), toRemove.getTileY() - 1, toRemove.isOnSurface());
        if (northTile != null && northTile.getStructure() == this) {
            this.addMissingWallPlans(northTile);
        }
        final VolaTile southTile = Zones.getTileOrNull(toRemove.getTileX(), toRemove.getTileY() + 1, toRemove.isOnSurface());
        if (southTile != null && southTile.getStructure() == this) {
            this.addMissingWallPlans(southTile);
        }
        adjustWallsAroundRemovedStructureTile(this, tilex, tiley);
        return true;
    }
    
    public final boolean removeTileFromPlannedStructure(final Creature aPlanner, final int tilex, final int tiley) {
        boolean allowed = false;
        if (this.structureTiles == null) {
            return false;
        }
        VolaTile toRemove = null;
        for (final VolaTile tile : this.structureTiles) {
            final int xx = tile.getTileX();
            final int yy = tile.getTileY();
            if (xx == tilex && yy == tiley) {
                toRemove = tile;
                break;
            }
        }
        if (toRemove == null) {
            Structure.logger.warning("Tile " + tilex + "," + tiley + " was not part of structure '" + this.getWurmId() + "'");
            return false;
        }
        if (this.testRemove(toRemove)) {
            allowed = true;
            final Wall[] walls2;
            final Wall[] walls = walls2 = toRemove.getWalls();
            for (final Wall wall : walls2) {
                toRemove.removeWall(wall, false);
                wall.delete();
            }
            this.structureTiles.remove(toRemove);
            MethodsStructure.removeBuildMarker(this, tilex, tiley);
            this.setMaxAndMin();
            final VolaTile westTile = Zones.getTileOrNull(toRemove.getTileX() - 1, toRemove.getTileY(), toRemove.isOnSurface());
            if (westTile != null && westTile.getStructure() == this) {
                this.addMissingWallPlans(westTile);
            }
            final VolaTile eastTile = Zones.getTileOrNull(toRemove.getTileX() + 1, toRemove.getTileY(), toRemove.isOnSurface());
            if (eastTile != null && eastTile.getStructure() == this) {
                this.addMissingWallPlans(eastTile);
            }
            final VolaTile northTile = Zones.getTileOrNull(toRemove.getTileX(), toRemove.getTileY() - 1, toRemove.isOnSurface());
            if (northTile != null && northTile.getStructure() == this) {
                this.addMissingWallPlans(northTile);
            }
            final VolaTile southTile = Zones.getTileOrNull(toRemove.getTileX(), toRemove.getTileY() + 1, toRemove.isOnSurface());
            if (southTile != null && southTile.getStructure() == this) {
                this.addMissingWallPlans(southTile);
            }
        }
        if (this.structureTiles.isEmpty()) {
            aPlanner.setStructure(null);
            try {
                aPlanner.save();
            }
            catch (Exception iox) {
                Structure.logger.log(Level.WARNING, "Failed to save player " + aPlanner.getName() + ", StructureId: " + this.wurmId, iox);
            }
            Structures.removeStructure(this.wurmId);
        }
        return allowed;
    }
    
    public final void addDoor(final Door door) {
        if (this.doors == null) {
            this.doors = new HashSet<Door>();
        }
        if (!this.doors.contains(door)) {
            this.doors.add(door);
            door.setStructureId(this.wurmId);
        }
    }
    
    public final Door[] getAllDoors() {
        Door[] toReturn = new Door[0];
        if (this.doors != null && this.doors.size() != 0) {
            toReturn = this.doors.toArray(new Door[this.doors.size()]);
        }
        return toReturn;
    }
    
    public final Door[] getAllDoors(final boolean includeAll) {
        final Set<Door> ldoors = new HashSet<Door>();
        if (this.doors != null && this.doors.size() != 0) {
            for (final Door door : this.doors) {
                if (includeAll || door.hasLock()) {
                    ldoors.add(door);
                }
            }
        }
        return ldoors.toArray(new Door[ldoors.size()]);
    }
    
    public final void removeDoor(final Door door) {
        if (this.doors != null) {
            this.doors.remove(door);
            door.delete();
        }
    }
    
    public final void unlockAllDoors() {
        final Door[] lDoors = this.getAllDoors();
        for (int x = 0; x < lDoors.length; ++x) {
            lDoors[x].unlock(x == 0);
        }
    }
    
    public final void lockAllDoors() {
        final Door[] lDoors = this.getAllDoors();
        for (int x = 0; x < lDoors.length; ++x) {
            lDoors[x].lock(x == 0);
        }
    }
    
    public final boolean isLocked() {
        final Door[] lDoors = this.getAllDoors();
        for (int x = 0; x < lDoors.length; ++x) {
            if (!lDoors[x].isLocked()) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean isLockable() {
        final Door[] lDoors = this.getAllDoors();
        for (int x = 0; x < lDoors.length; ++x) {
            try {
                lDoors[x].getLock();
            }
            catch (NoSuchLockException nsl) {
                return false;
            }
        }
        return true;
    }
    
    public final boolean isTypeBridge() {
        return this.structureType == 1;
    }
    
    public final boolean isTypeHouse() {
        return this.structureType == 0;
    }
    
    private void finalizeBuildPlanForTiles(final long oldStructureId) throws IOException {
        for (final VolaTile tile : this.structureTiles) {
            tile.finalizeBuildPlan(oldStructureId, this.wurmId);
            this.addNewBuildTile(tile.tilex, tile.tiley, tile.getLayer());
            final Wall[] walls = tile.getWalls();
            for (int x = 0; x < walls.length; ++x) {
                walls[x].setStructureId(this.wurmId);
                walls[x].save();
            }
            final Floor[] floors = tile.getFloors();
            for (int x2 = 0; x2 < floors.length; ++x2) {
                floors[x2].setStructureId(this.wurmId);
                floors[x2].save();
            }
            final BridgePart[] bridgeParts = tile.getBridgeParts();
            for (int x3 = 0; x3 < bridgeParts.length; ++x3) {
                bridgeParts[x3].setStructureId(this.wurmId);
                bridgeParts[x3].save();
            }
        }
    }
    
    public final boolean makeFinal(final Creature aOwner, final String aName) throws IOException, NoSuchZoneException {
        final int size = this.structureTiles.size();
        if (size > 0) {
            String sName;
            if (this.structureType == 1) {
                sName = aName;
                Achievements.triggerAchievement(aOwner.getWurmId(), 557);
            }
            else if (size <= 2) {
                sName = aName + "shed";
            }
            else if (size <= 3) {
                sName = aName + "shack";
            }
            else if (size <= 5) {
                sName = aName + "cottage";
            }
            else if (size <= 6) {
                sName = aName + "house";
            }
            else if (size <= 10) {
                sName = aName + "villa";
            }
            else if (size <= 20) {
                sName = aName + "mansion";
            }
            else if (size <= 30) {
                sName = aName + "estate";
            }
            else {
                sName = aName + "stronghold";
            }
            final long oldStructureId = this.wurmId;
            this.wurmId = WurmId.getNextStructureId();
            Structures.removeStructure(oldStructureId);
            this.name = sName;
            Structures.addStructure(this);
            this.finalizeBuildPlanForTiles(oldStructureId);
            Zone northW = null;
            Zone northE = null;
            Zone southW = null;
            Zone southE = null;
            try {
                northW = Zones.getZone(this.minX, this.minY, this.surfaced);
                northW.addStructure(this);
            }
            catch (NoSuchZoneException ex) {}
            try {
                northE = Zones.getZone(this.maxX, this.minY, this.surfaced);
                if (northE != northW) {
                    northE.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex2) {}
            try {
                southE = Zones.getZone(this.maxX, this.maxY, this.surfaced);
                if (southE != northE && southE != northW) {
                    southE.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex3) {}
            try {
                southW = Zones.getZone(this.minX, this.maxY, this.surfaced);
                if (southW != northE && southW != northW && southW != southE) {
                    southW.addStructure(this);
                }
            }
            catch (NoSuchZoneException ex4) {}
            this.writid = -10L;
            this.setPlanner(aOwner.getName());
            this.setOwnerId(aOwner.getWurmId());
            this.save();
            return true;
        }
        return false;
    }
    
    public void clearAllWallsAndMakeWallsForStructureBorder(final VolaTile toAdd) {
        for (final VolaTile tile : createNeighbourStructureTiles(this, toAdd)) {
            this.destroyWallsBorderingToTile(tile, toAdd);
        }
        this.addMissingWallPlans(toAdd);
    }
    
    private void destroyWallsBorderingToTile(final VolaTile start, final VolaTile target) {
        boolean destroy = false;
        for (final Wall wall : start.getWalls()) {
            destroy = false;
            if (wall.isHorizontal() && wall.getMinX() == target.getTileX()) {
                if (wall.getMinY() == target.getTileY()) {
                    destroy = true;
                }
                else if (wall.getMinY() == target.getTileY() + 1) {
                    destroy = true;
                }
            }
            if (!wall.isHorizontal() && wall.getMinY() == target.getTileY()) {
                if (wall.getMinX() == target.getTileX()) {
                    destroy = true;
                }
                else if (wall.getMinX() == target.getTileX() + 1) {
                    destroy = true;
                }
            }
            if (destroy) {
                start.removeWall(wall, false);
                wall.delete();
            }
        }
    }
    
    private boolean isFree(final int x, final int y) {
        return !this.contains(x, y);
    }
    
    public final boolean isFinished() {
        return this.finished;
    }
    
    public final boolean isFinalFinished() {
        return this.finalfinished;
    }
    
    public final boolean needsDoor() {
        int free = 0;
        for (final VolaTile tile : this.structureTiles) {
            final Wall[] wallArr = tile.getWallsForLevel(0);
            for (int x = 0; x < wallArr.length; ++x) {
                final StructureTypeEnum type = wallArr[x].getType();
                if (type == StructureTypeEnum.DOOR) {
                    return false;
                }
                if (type == StructureTypeEnum.DOUBLE_DOOR) {
                    return false;
                }
                if (Wall.isArched(type)) {
                    return false;
                }
                if (type == StructureTypeEnum.PORTCULLIS) {
                    return false;
                }
                if (type == StructureTypeEnum.CANOPY_DOOR) {
                    return false;
                }
                if (type == StructureTypeEnum.PLAN) {
                    ++free;
                }
            }
        }
        return free < 2;
    }
    
    public final int getDoors() {
        int numdoors = 0;
        for (final VolaTile tile : this.structureTiles) {
            final Wall[] wallArr = tile.getWalls();
            for (int x = 0; x < wallArr.length; ++x) {
                final StructureTypeEnum type = wallArr[x].getType();
                if (type == StructureTypeEnum.DOOR) {
                    ++numdoors;
                }
                if (type == StructureTypeEnum.DOUBLE_DOOR) {
                    ++numdoors;
                }
                if (Wall.isArched(type)) {
                    ++numdoors;
                }
                if (type == StructureTypeEnum.PORTCULLIS) {
                    ++numdoors;
                }
                if (type == StructureTypeEnum.CANOPY_DOOR) {
                    ++numdoors;
                }
            }
        }
        return numdoors;
    }
    
    public boolean updateStructureFinishFlag() {
        for (final VolaTile tile : this.structureTiles) {
            if (this.structureType == 0) {
                final Wall[] wallArr = tile.getWalls();
                for (int x = 0; x < wallArr.length; ++x) {
                    if (!wallArr[x].isIndoor() && !wallArr[x].isFinished()) {
                        this.setFinished(false);
                        this.setFinalFinished(false);
                        return false;
                    }
                }
            }
            else {
                final BridgePart[] bridgeParts = tile.getBridgeParts();
                for (int x = 0; x < bridgeParts.length; ++x) {
                    if (!bridgeParts[x].isFinished()) {
                        this.setFinished(false);
                        this.setFinalFinished(false);
                        return false;
                    }
                }
            }
        }
        this.setFinished(true);
        this.setFinalFinished(true);
        Players.getInstance().setStructureFinished(this.wurmId);
        return true;
    }
    
    public final boolean isFinalized() {
        return WurmId.getType(this.wurmId) == 4;
    }
    
    public final boolean contains(final int tilex, final int tiley) {
        if (this.structureTiles == null) {
            Structure.logger.log(Level.WARNING, "StructureTiles is null in building with id " + this.wurmId);
            return true;
        }
        for (final VolaTile tile : this.structureTiles) {
            if (tilex == tile.tilex && tiley == tile.tiley) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean isOnSurface() {
        return this.surfaced;
    }
    
    @Override
    public final long getWurmId() {
        return this.wurmId;
    }
    
    @Override
    public int getTemplateId() {
        return -10;
    }
    
    @Override
    public int getMaxAllowed() {
        return AnimalSettings.getMaxAllowed();
    }
    
    @Override
    public boolean isActualOwner(final long playerId) {
        return this.getOwnerId() == playerId;
    }
    
    @Override
    public boolean isOwner(final Creature creature) {
        return this.isOwner(creature.getWurmId());
    }
    
    @Override
    public boolean isOwner(final long playerId) {
        if (this.isManaged()) {
            final Village vill = this.getManagedByVillage();
            if (vill != null) {
                return vill.isMayor(playerId);
            }
        }
        return this.isActualOwner(playerId);
    }
    
    public boolean mayPlaceMerchants(final Creature creature) {
        return !StructureSettings.isExcluded(this, creature) && StructureSettings.mayPlaceMerchants(this, creature);
    }
    
    public boolean mayUseBed(final Creature creature) {
        return this.isOwner(creature) || this.isGuest(creature) || (this.allowsCitizens() && this.isInOwnerSettlement(creature)) || (this.allowsAllies() && this.isInOwnerAlliance(creature));
    }
    
    public boolean mayPickupPlanted(final Creature creature) {
        return !StructureSettings.isExcluded(this, creature) && StructureSettings.mayPickupPlanted(this, creature);
    }
    
    public boolean mayLoad(final Creature creature) {
        return this.isEnemy(creature) || (!StructureSettings.isExcluded(this, creature) && StructureSettings.mayLoad(this, creature));
    }
    
    public boolean isInOwnerSettlement(final Creature creature) {
        if (creature.getCitizenVillage() != null) {
            final long wid = this.getOwnerId();
            if (wid != -10L) {
                final Village creatorVillage = Villages.getVillageForCreature(wid);
                return creatorVillage != null && creature.getCitizenVillage().getId() == creatorVillage.getId();
            }
        }
        return false;
    }
    
    public boolean isInOwnerAlliance(final Creature creature) {
        if (creature.getCitizenVillage() != null) {
            final long wid = this.getOwnerId();
            if (wid != -10L) {
                final Village creatorVillage = Villages.getVillageForCreature(wid);
                return creatorVillage != null && creature.getCitizenVillage().isAlly(creatorVillage);
            }
        }
        return false;
    }
    
    public final int getCenterX() {
        return this.minX + Math.max(1, this.maxX - this.minX) / 2;
    }
    
    public final int getCenterY() {
        return this.minY + Math.max(1, this.maxY - this.minY) / 2;
    }
    
    public final int getMaxX() {
        return this.maxX;
    }
    
    public final int getMaxY() {
        return this.maxY;
    }
    
    public final int getMinX() {
        return this.minX;
    }
    
    public final int getMinY() {
        return this.minY;
    }
    
    public final VolaTile[] getStructureTiles() {
        final VolaTile[] tiles = new VolaTile[this.structureTiles.size()];
        return this.structureTiles.toArray(tiles);
    }
    
    public boolean allowsAllies() {
        return this.allowsAllies;
    }
    
    public boolean allowsKingdom() {
        return this.allowsKingdom;
    }
    
    public boolean allowsCitizens() {
        return this.allowsVillagers;
    }
    
    @Override
    public boolean isManaged() {
        return this.permissions.hasPermission(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit());
    }
    
    @Override
    public boolean isManageEnabled(final Player player) {
        if (player.getPower() > 1) {
            return true;
        }
        if (this.isManaged()) {
            final Village vil = this.getPermissionsVillage();
            return vil != null && vil.isMayor(player);
        }
        return this.isOwner(player);
    }
    
    @Override
    public void setIsManaged(final boolean newIsManaged, final Player player) {
        final int oldId = this.villageId;
        if (newIsManaged) {
            final Village v = this.getVillage();
            if (v != null) {
                this.villageId = v.getId();
            }
            else {
                final Village cv = player.getCitizenVillage();
                if (cv == null) {
                    return;
                }
                this.villageId = cv.getId();
            }
        }
        else {
            this.villageId = -1;
        }
        if (oldId != this.villageId && StructureSettings.exists(this.getWurmId())) {
            StructureSettings.remove(this.getWurmId());
            PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), -10L, "Auto", "Cleared Permissions");
        }
        this.permissions.setPermissionBit(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit(), newIsManaged);
    }
    
    @Override
    public String mayManageText(final Player player) {
        final String sName = this.getVillageName(player);
        if (sName.length() > 0) {
            return "Settlement \"" + sName + "\" may manage";
        }
        return sName;
    }
    
    @Override
    public String mayManageHover(final Player aPlayer) {
        return "";
    }
    
    @Override
    public String messageOnTick() {
        return "This gives full control to the settlement";
    }
    
    @Override
    public String questionOnTick() {
        return "Did you realy mean to do that?";
    }
    
    @Override
    public String messageUnTick() {
        return "Doing this reverts the control back to the owner.";
    }
    
    @Override
    public String questionUnTick() {
        return "Are you really positive you want to do that?";
    }
    
    @Override
    public String getSettlementName() {
        String sName = "";
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            sName = vill.getName();
        }
        if (sName.length() > 0) {
            return "Citizens of \"" + sName + "\"";
        }
        return sName;
    }
    
    @Override
    public String getAllianceName() {
        String aName = "";
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            aName = vill.getAllianceName();
        }
        if (aName.length() > 0) {
            return "Alliance of \"" + aName + "\"";
        }
        return "";
    }
    
    @Override
    public String getKingdomName() {
        byte kingdom = 0;
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            kingdom = vill.kingdom;
        }
        else {
            kingdom = Players.getInstance().getKingdomForPlayer(this.getOwnerId());
        }
        return "Kingdom of \"" + Kingdoms.getNameFor(kingdom) + "\"";
    }
    
    @Override
    public boolean canAllowEveryone() {
        return false;
    }
    
    @Override
    public String getRolePermissionName() {
        return "";
    }
    
    final boolean hasLoaded() {
        return this.hasLoaded;
    }
    
    final void setHasLoaded(final boolean aHasLoaded) {
        this.hasLoaded = aHasLoaded;
    }
    
    final boolean isLoading() {
        return this.isLoading;
    }
    
    final void setLoading(final boolean aIsLoading) {
        this.isLoading = aIsLoading;
    }
    
    final boolean isSurfaced() {
        return this.surfaced;
    }
    
    public final byte getLayer() {
        if (this.surfaced) {
            return 0;
        }
        return -1;
    }
    
    final void setSurfaced(final boolean aSurfaced) {
        this.surfaced = aSurfaced;
    }
    
    final void setStructureType(final byte theStructureType) {
        this.structureType = theStructureType;
    }
    
    public final byte getStructureType() {
        return this.structureType;
    }
    
    final long getWritid() {
        return this.writid;
    }
    
    public final void setWritid(final long aWritid, final boolean save) {
        this.writid = aWritid;
        if (save) {
            try {
                this.saveWritId();
            }
            catch (IOException iox) {
                Structure.logger.log(Level.INFO, "Problems saving WritId " + aWritid + ", StructureId: " + this.wurmId + iox.getMessage(), iox);
            }
        }
    }
    
    public final Set<StructureSupport> getAllSupports() {
        final Set<StructureSupport> toReturn = new HashSet<StructureSupport>();
        if (this.structureTiles == null) {
            return toReturn;
        }
        for (final VolaTile tile : this.structureTiles) {
            toReturn.addAll(tile.getAllSupport());
        }
        return toReturn;
    }
    
    private static final void addAllGroundStructureSupportToSet(final Set<StructureSupport> supportingSupports, final Set<StructureSupport> remainingSupports) {
        final Set<StructureSupport> toMove = new HashSet<StructureSupport>();
        for (final StructureSupport remaining : remainingSupports) {
            if (remaining.isSupportedByGround()) {
                toMove.add(remaining);
            }
        }
        supportingSupports.addAll(toMove);
        remainingSupports.removeAll(toMove);
    }
    
    public final boolean wouldCreateFlyingStructureIfRemoved(final StructureSupport supportToCheck) {
        final Set<StructureSupport> allSupports = this.getAllSupports();
        final Set<StructureSupport> supportingSupports = new HashSet<StructureSupport>();
        allSupports.remove(supportToCheck);
        StructureSupport match = null;
        for (final StructureSupport csupport : allSupports) {
            if (csupport.getId() == supportToCheck.getId()) {
                match = csupport;
            }
        }
        if (match != null) {
            allSupports.remove(match);
        }
        addAllGroundStructureSupportToSet(supportingSupports, allSupports);
        final Set<StructureSupport> toRemove = new HashSet<StructureSupport>();
        while (!allSupports.isEmpty()) {
            for (final StructureSupport checked : supportingSupports) {
                for (final StructureSupport remaining : allSupports) {
                    if (checked.supports(remaining)) {
                        toRemove.add(remaining);
                    }
                }
            }
            if (toRemove.isEmpty()) {
                break;
            }
            supportingSupports.addAll(toRemove);
            allSupports.removeAll(toRemove);
            toRemove.clear();
        }
        return !allSupports.isEmpty();
    }
    
    public final int[] getNortEntrance() {
        return new int[] { this.minX, this.minY - 1 };
    }
    
    public final int[] getSouthEntrance() {
        return new int[] { this.maxX, this.maxY + 1 };
    }
    
    public final int[] getWestEntrance() {
        return new int[] { this.minX - 1, this.minY };
    }
    
    public final int[] getEastEntrance() {
        return new int[] { this.maxX + 1, this.maxY };
    }
    
    public final boolean isHorizontal() {
        return this.minX < this.maxX && this.minY == this.maxY;
    }
    
    public final boolean containsSettlement(final int[] tileCoords, final int layer) {
        if (tileCoords[0] == -1) {
            return false;
        }
        final VolaTile t = Zones.getTileOrNull(tileCoords[0], tileCoords[1], layer == 0);
        return t != null && t.getVillage() != null;
    }
    
    public final int[] findBestBridgeEntrance(final Creature creature, final int tilex, final int tiley, final int layer, final long bridgeId, final int currentPathFindCounter) {
        final int lMaxX = this.isHorizontal() ? (this.getMaxX() + 1) : this.getMaxX();
        final int lMinX = this.isHorizontal() ? (this.getMinX() - 1) : this.getMinX();
        final int lMinY = this.isHorizontal() ? this.getMinY() : (this.getMinY() - 1);
        final int lMaxY = this.isHorizontal() ? this.getMaxY() : (this.getMaxY() + 1);
        final int[] min = { lMinX, lMinY };
        final int[] max = { lMaxX, lMaxY };
        if (!creature.isUnique() && this.containsSettlement(min, layer) && this.containsSettlement(max, layer)) {
            return Structure.noEntrance;
        }
        final boolean switchEntrance = currentPathFindCounter > 5 && Server.rand.nextBoolean();
        Label_0227: {
            if (this.isHorizontal()) {
                if (creature.getTileX() < lMaxX) {
                    break Label_0227;
                }
            }
            else if (creature.getTileY() < lMaxY) {
                break Label_0227;
            }
            if ((!this.containsSettlement(max, layer) || creature.isUnique()) && !switchEntrance) {
                return max;
            }
        }
        Label_0281: {
            if (this.isHorizontal()) {
                if (creature.getTileX() > lMinX) {
                    break Label_0281;
                }
            }
            else if (creature.getTileY() > lMinY) {
                break Label_0281;
            }
            if ((!this.containsSettlement(min, layer) || creature.isUnique()) && !switchEntrance) {
                return min;
            }
        }
        final int diffMax = Math.abs(this.isHorizontal() ? (creature.getTileX() - lMaxX) : (creature.getTileY() - lMaxY));
        final int diffMin = this.isHorizontal() ? (creature.getTileX() - lMinX) : (creature.getTileY() - lMinY);
        if (diffMax <= diffMin) {
            if ((!this.containsSettlement(max, layer) || creature.isUnique()) && !switchEntrance) {
                return max;
            }
            return min;
        }
        else if (diffMin <= diffMax) {
            if ((!this.containsSettlement(min, layer) || creature.isUnique()) && !switchEntrance) {
                return min;
            }
            return max;
        }
        else if (Server.rand.nextBoolean()) {
            if ((!this.containsSettlement(max, layer) || creature.isUnique()) && !switchEntrance) {
                return max;
            }
            return min;
        }
        else {
            if ((!this.containsSettlement(min, layer) || creature.isUnique()) && !switchEntrance) {
                return min;
            }
            return max;
        }
    }
    
    public boolean isBridgeJustPlans() {
        if (this.structureType != 1) {
            return false;
        }
        for (final BridgePart bp : this.getBridgeParts()) {
            if (bp.getBridgePartState() != BridgeConstants.BridgeState.PLANNED) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isBridgeGone() {
        if (this.isBridgeJustPlans()) {
            for (final BridgePart bp : this.getBridgeParts()) {
                bp.destroy();
            }
            this.totallyDestroy();
            return true;
        }
        return false;
    }
    
    void addDefaultAllyPermissions() {
        if (!this.getPermissionsPlayerList().exists(-20L)) {
            final int value = StructureSettings.StructurePermissions.PASS.getValue() + StructureSettings.StructurePermissions.PICKUP.getValue();
            this.addNewGuest(-20L, value);
        }
    }
    
    @Override
    public void addDefaultCitizenPermissions() {
        if (!this.getPermissionsPlayerList().exists(-30L)) {
            final int value = StructureSettings.StructurePermissions.PASS.getValue() + StructureSettings.StructurePermissions.PICKUP.getValue();
            this.addNewGuest(-30L, value);
        }
    }
    
    void addDefaultKingdomPermissions() {
        if (!this.getPermissionsPlayerList().exists(-40L)) {
            final int value = StructureSettings.StructurePermissions.PASS.getValue();
            this.addNewGuest(-40L, value);
        }
    }
    
    public final void setWalkedOnBridge(final long now) {
        long lastUsed = 0L;
        for (final BridgePart bp : this.getBridgeParts()) {
            if (bp.isFinished() && lastUsed < bp.getLastUsed()) {
                lastUsed = bp.getLastUsed();
            }
        }
        if (lastUsed < now - 86400000L) {
            for (final BridgePart bp : this.getBridgeParts()) {
                bp.setLastUsed(now);
            }
        }
    }
    
    @Override
    public boolean setNewOwner(final long playerId) {
        if (this.writid != -10L) {
            return false;
        }
        if (!this.isManaged() && StructureSettings.exists(this.getWurmId())) {
            StructureSettings.remove(this.getWurmId());
            PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), -10L, "Auto", "Cleared Permissions");
        }
        this.ownerId = playerId;
        try {
            this.saveOwnerId();
            return true;
        }
        catch (IOException ex) {
            return false;
        }
    }
    
    @Override
    public String getOwnerName() {
        this.getOwnerId();
        if (this.writid != -10L) {
            return "has writ";
        }
        return PlayerInfoFactory.getPlayerName(this.ownerId);
    }
    
    public boolean convertToNewPermissions() {
        boolean didConvert = false;
        final PermissionsPlayerList ppl = StructureSettings.getPermissionsPlayerList(this.wurmId);
        if (this.allowsAllies && !ppl.exists(-20L)) {
            this.addDefaultAllyPermissions();
            didConvert = true;
        }
        if (this.allowsVillagers && !ppl.exists(-30L)) {
            this.addDefaultCitizenPermissions();
            didConvert = true;
        }
        if (this.allowsKingdom && !ppl.exists(-40L)) {
            this.addDefaultKingdomPermissions();
            didConvert = true;
        }
        if (didConvert) {
            try {
                this.saveSettings();
            }
            catch (IOException e) {
                Structure.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        for (final Door d : this.getAllDoors()) {
            d.setIsManaged(true, null);
        }
        return didConvert;
    }
    
    public byte getKingdomId() {
        byte kingdom = 0;
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            kingdom = vill.kingdom;
        }
        else {
            kingdom = Players.getInstance().getKingdomForPlayer(this.getOwnerId());
        }
        return kingdom;
    }
    
    public static boolean isGroundFloorAtPosition(final float x, final float y, final boolean isOnSurface) {
        final TilePos tilePos = CoordUtils.WorldToTile(x, y);
        final VolaTile tile = Zones.getOrCreateTile(tilePos, isOnSurface);
        if (tile != null) {
            final Floor[] floors = tile.getFloors(0, 0);
            if (floors != null && floors.length > 0 && floors[0].getType() == FloorType.FLOOR) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isDestroyed() {
        if (this.isTypeBridge()) {
            return this.isBridgeJustPlans() || this.isBridgeGone();
        }
        final Wall[] walls = this.getWalls();
        final Floor[] floors = this.getFloors();
        boolean destroyed = true;
        for (final Wall wall : walls) {
            if (!wall.isWallPlan()) {
                destroyed = false;
            }
        }
        for (final Floor floor : floors) {
            if (!floor.isAPlan()) {
                destroyed = false;
            }
        }
        return destroyed;
    }
    
    abstract void setFinalFinished(final boolean p0);
    
    public abstract void setFinished(final boolean p0);
    
    public abstract void endLoading() throws IOException;
    
    abstract void load() throws IOException, NoSuchStructureException;
    
    @Override
    public abstract void save() throws IOException;
    
    public abstract void saveWritId() throws IOException;
    
    public abstract void saveOwnerId() throws IOException;
    
    public abstract void saveSettings() throws IOException;
    
    public abstract void saveName() throws IOException;
    
    abstract void delete();
    
    abstract void removeStructureGuest(final long p0);
    
    abstract void addNewGuest(final long p0, final int p1);
    
    public abstract void setAllowAllies(final boolean p0);
    
    public abstract void setAllowVillagers(final boolean p0);
    
    public abstract void setAllowKingdom(final boolean p0);
    
    @Override
    public String toString() {
        return "Structure [wurmId=" + this.wurmId + ", surfaced=" + this.surfaced + ", name=" + this.name + ", writid=" + this.writid + "]";
    }
    
    public abstract void removeBuildTile(final int p0, final int p1, final int p2);
    
    public abstract void addNewBuildTile(final int p0, final int p1, final int p2);
    
    public abstract void deleteAllBuildTiles();
    
    static {
        Structure.logger = Logger.getLogger(Structure.class.getName());
        noEntrance = new int[] { -1, -1 };
    }
}

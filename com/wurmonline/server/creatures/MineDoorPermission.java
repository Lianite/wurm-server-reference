// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.structures.StructureSettings;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Players;
import java.util.Iterator;
import com.wurmonline.server.players.PermissionsHistories;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Point;
import com.wurmonline.server.behaviours.TileRockBehaviour;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.players.Player;
import com.wurmonline.mesh.Tiles;
import java.util.HashSet;
import com.wurmonline.server.zones.VirtualZone;
import java.util.Set;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.villages.Village;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.MiscConstants;

public final class MineDoorPermission implements MiscConstants, PermissionsPlayerList.ISettings
{
    private static final Logger logger;
    private static final String CREATE_MINEDOOR = "INSERT INTO MINEDOOR (CREATOR,VILLAGE,ALLOWALL,ALLOWALLIES,NAME,SETTINGS,ID) VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_MINEDOOR = "UPDATE MINEDOOR SET CREATOR=?,VILLAGE=?,ALLOWALL=?,ALLOWALLIES=?,NAME=?,SETTINGS=? WHERE ID=?";
    private static final String DELETE_MINEDOOR = "DELETE FROM MINEDOOR WHERE ID=?";
    private static final String GET_ALL_MINEDOORS = "SELECT * FROM MINEDOOR";
    private static final Map<Integer, MineDoorPermission> mineDoorPermissions;
    private final int id;
    private final long wurmId;
    private long creator;
    private int villageId;
    private String name;
    private boolean allowAll;
    private boolean allowAllies;
    private Village village;
    private Permissions permissions;
    private long closingTime;
    Set<Creature> creaturesOpened;
    Set<VirtualZone> watchers;
    boolean open;
    
    public MineDoorPermission(final int tilex, final int tiley, final long _creator, final Village currentvillage, final boolean _allowAll, final boolean _allowAllies, final String _name, final int settings) {
        this.creator = -10L;
        this.villageId = -1;
        this.name = "";
        this.allowAll = false;
        this.allowAllies = false;
        this.village = null;
        this.permissions = new Permissions();
        this.closingTime = 0L;
        this.creaturesOpened = new HashSet<Creature>();
        this.watchers = new HashSet<VirtualZone>();
        this.open = false;
        this.id = makeId(tilex, tiley);
        this.wurmId = Tiles.getTileId(tilex, tiley, 0);
        this.creator = _creator;
        this.allowAllies = _allowAllies;
        this.name = _name;
        this.permissions.setPermissionBits(settings);
        this.village = currentvillage;
        if (this.village != null) {
            this.setIsManaged(true, null);
        }
        else {
            this.setIsManaged(false, null);
        }
        try {
            this.save();
        }
        catch (IOException e) {
            MineDoorPermission.logger.log(Level.WARNING, e.getMessage(), e);
        }
        addPermission(this);
    }
    
    private MineDoorPermission(final int _id, final long _creator, final int _villageid, final boolean _allowall, final boolean _allowAllies, final String _name, final int settings) {
        this.creator = -10L;
        this.villageId = -1;
        this.name = "";
        this.allowAll = false;
        this.allowAllies = false;
        this.village = null;
        this.permissions = new Permissions();
        this.closingTime = 0L;
        this.creaturesOpened = new HashSet<Creature>();
        this.watchers = new HashSet<VirtualZone>();
        this.open = false;
        this.id = _id;
        this.wurmId = Tiles.getTileId(decodeTileX(this.id), decodeTileY(this.id), 0);
        this.creator = _creator;
        this.villageId = _villageid;
        this.allowAll = _allowall;
        this.allowAllies = _allowAllies;
        this.name = _name;
        this.permissions.setPermissionBits(settings);
        addPermission(this);
    }
    
    @Override
    public int getMaxAllowed() {
        return MineDoorSettings.getMaxAllowed();
    }
    
    public final void setVillage(@Nullable final Village vill) {
        this.village = vill;
        if (vill != null) {
            this.setIsManaged(true, null);
        }
        else if (this.villageId != -1) {
            this.setIsManaged(false, null);
        }
    }
    
    public boolean setVillageId(final int newid) {
        if (this.villageId == newid) {
            return false;
        }
        this.villageId = newid;
        return true;
    }
    
    public int getVillageId() {
        return this.villageId;
    }
    
    public Village getVillage() {
        return this.village;
    }
    
    private Village getPermissionsVillage() {
        final Village vill = this.getManagedByVillage();
        if (vill != null) {
            return vill;
        }
        return this.getOwnerVillage();
    }
    
    public final Village getOwnerVillage() {
        return Villages.getVillageForCreature(this.creator);
    }
    
    public final Village getManagedByVillage() {
        if (this.villageId >= 0) {
            try {
                return Villages.getVillage(this.villageId);
            }
            catch (NoSuchVillageException e) {
                this.setVillageId(-1);
            }
        }
        return null;
    }
    
    public boolean setAllowAll(final boolean allow) {
        if (this.allowAll == allow) {
            return false;
        }
        this.allowAll = allow;
        return true;
    }
    
    public boolean isAllowAll() {
        return this.allowAll;
    }
    
    public boolean setAllowAllies(final boolean allow) {
        if (this.allowAllies == allow) {
            return false;
        }
        this.allowAllies = allow;
        return true;
    }
    
    public boolean isAllowAllies() {
        return this.allowAllies;
    }
    
    public boolean setController(final long newid) {
        if (this.creator == newid) {
            return false;
        }
        this.creator = newid;
        return true;
    }
    
    public long getController() {
        return this.creator;
    }
    
    public void removeMDPerm(final long creatureId) {
        MineDoorSettings.removePlayer(this.id, creatureId);
    }
    
    public void addMDPerm(final long creatureId, final int settings) {
        MineDoorSettings.addPlayer(this.id, creatureId, settings);
    }
    
    public static final void removePermission(final MineDoorPermission perm) {
        MineDoorPermission.mineDoorPermissions.remove(perm.id);
        final int x = perm.getTileX();
        final int y = perm.getTileY();
        try {
            Zones.getZone(perm.getTileX(), perm.getTileY(), true).getOrCreateTile(x, y).removeMineDoor(perm);
        }
        catch (NoSuchZoneException e) {
            MineDoorPermission.logger.log(Level.SEVERE, "Could not find zone for removing a mine door at " + x + ":" + y + "!");
        }
    }
    
    private static final void addPermission(final MineDoorPermission perm) {
        MineDoorPermission.mineDoorPermissions.put(perm.id, perm);
        final int x = perm.getTileX();
        final int y = perm.getTileY();
        try {
            if (isVisibleDoorTile(Tiles.decodeType(Server.surfaceMesh.getTile(x, y)))) {
                final Point highestCorner = TileRockBehaviour.findHighestCorner(x, y);
                final Point nextHighestCorner = TileRockBehaviour.findNextHighestCorner(x, y, highestCorner);
                if (highestCorner.getH() == nextHighestCorner.getH()) {
                    Zones.getZone(perm.getTileX(), perm.getTileY(), true).getOrCreateTile(x, y).addMineDoor(perm);
                }
            }
        }
        catch (NoSuchZoneException e) {
            MineDoorPermission.logger.log(Level.SEVERE, "Could not find zone for adding a mine door at " + x + ":" + y + "!");
        }
    }
    
    private static final boolean isVisibleDoorTile(final int tile) {
        return tile == 27 || tile == 25 || tile == 28 || tile == 29;
    }
    
    public static final MineDoorPermission getPermission(final long wurmId) {
        final int tilex = Tiles.decodeTileX(wurmId);
        final int tiley = Tiles.decodeTileY(wurmId);
        return getPermission(tilex, tiley);
    }
    
    public static final MineDoorPermission getPermission(final int tilex, final int tiley) {
        return getPermission(makeId(tilex, tiley));
    }
    
    public static final MineDoorPermission getPermission(final int mineDoorId) {
        return MineDoorPermission.mineDoorPermissions.get(mineDoorId);
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
        if (MineDoorSettings.isExcluded(this, this.id, creature)) {
            return false;
        }
        if (MineDoorSettings.canManage(this, this.id, creature)) {
            return true;
        }
        if (creature.getCitizenVillage() == null) {
            return false;
        }
        final Village vill = this.getManagedByVillage();
        return vill != null && vill.isCitizen(creature) && vill.isActionAllowed((short)364, creature);
    }
    
    public final boolean mayManage(final Creature creature) {
        return creature.getPower() > 1 || this.canManage(creature);
    }
    
    public final boolean maySeeHistory(final Creature creature) {
        return creature.getPower() > 1 || this.isOwner(creature);
    }
    
    public final boolean mayPass(final Creature creature) {
        return !MineDoorSettings.isExcluded(this, this.id, creature) && MineDoorSettings.mayPass(this, this.id, creature);
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE MINEDOOR SET CREATOR=?,VILLAGE=?,ALLOWALL=?,ALLOWALLIES=?,NAME=?,SETTINGS=? WHERE ID=?");
            ps.setLong(1, this.creator);
            ps.setInt(2, this.villageId);
            ps.setBoolean(3, this.allowAll);
            ps.setBoolean(4, this.allowAllies);
            ps.setString(5, this.name);
            ps.setInt(6, this.permissions.getPermissions());
            ps.setInt(7, this.id);
            if (ps.executeUpdate() == 0) {
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("INSERT INTO MINEDOOR (CREATOR,VILLAGE,ALLOWALL,ALLOWALLIES,NAME,SETTINGS,ID) VALUES (?,?,?,?,?,?,?)");
                ps.setLong(1, this.creator);
                ps.setInt(2, this.villageId);
                ps.setBoolean(3, this.allowAll);
                ps.setBoolean(4, this.allowAllies);
                ps.setString(5, this.name);
                ps.setInt(6, this.permissions.getPermissions());
                ps.setInt(7, this.id);
                ps.executeUpdate();
            }
        }
        catch (SQLException sqex) {
            MineDoorPermission.logger.log(Level.WARNING, "Failed to create mine door: " + this.id + ", " + this.creator + ", " + this.villageId + ":" + sqex.getMessage(), sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteMineDoor(final int tilex, final int tiley) {
        final int mdId = makeId(tilex, tiley);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MINEDOOR WHERE ID=?");
            ps.setInt(1, mdId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            MineDoorPermission.logger.log(Level.WARNING, "Failed to delete mine door: " + mdId + ":" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        MineDoorSettings.remove(mdId);
        PermissionsHistories.remove(mdId);
    }
    
    public static final void loadAllMineDoors() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MINEDOOR");
            rs = ps.executeQuery();
            while (rs.next()) {
                new MineDoorPermission(rs.getInt("ID"), rs.getLong("CREATOR"), rs.getInt("VILLAGE"), rs.getBoolean("ALLOWALL"), rs.getBoolean("ALLOWALLIES"), rs.getString("NAME"), rs.getInt("SETTINGS"));
            }
        }
        catch (SQLException sqex) {
            MineDoorPermission.logger.log(Level.WARNING, "Failed to load all mine doors: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void addWatcher(final VirtualZone watcher) {
        if (this.watchers == null) {
            this.watchers = new HashSet<VirtualZone>();
        }
        if (!this.watchers.contains(watcher)) {
            this.watchers.add(watcher);
        }
    }
    
    public final void removeWatcher(final VirtualZone watcher) {
        if (this.watchers != null && this.watchers.contains(watcher)) {
            this.watchers.remove(watcher);
        }
    }
    
    public void open(final Creature creature) {
        this.open = true;
        if (this.creaturesOpened.isEmpty()) {
            for (final VirtualZone z : this.watchers) {
                if (creature.isVisibleTo(z.getWatcher())) {
                    z.openMineDoor(this);
                }
            }
        }
        this.creaturesOpened.add(creature);
    }
    
    public void close(final Creature creature) {
        this.open = true;
        this.creaturesOpened.remove(creature);
        if (this.creaturesOpened.isEmpty()) {
            for (final VirtualZone z : this.watchers) {
                z.closeMineDoor(this);
            }
        }
    }
    
    public long getClosingTime() {
        return this.closingTime;
    }
    
    public void setClosingTime(final long aClosingTime) {
        this.closingTime = aClosingTime;
    }
    
    public final boolean isWideOpen() {
        return this.getClosingTime() > System.currentTimeMillis();
    }
    
    public static int makeId(final int tilex, final int tiley) {
        return (tilex << 16) + tiley;
    }
    
    public static int decodeTileX(final int mdId) {
        return mdId >> 16 & 0xFFFF;
    }
    
    public static int decodeTileY(final int mdId) {
        return mdId & 0xFFFF;
    }
    
    public static MineDoorPermission[] getAllMineDoors() {
        return MineDoorPermission.mineDoorPermissions.values().toArray(new MineDoorPermission[MineDoorPermission.mineDoorPermissions.size()]);
    }
    
    public static final MineDoorPermission[] getManagedMineDoorsFor(final Player player, final int villageId, final boolean includeAll) {
        final Set<MineDoorPermission> mineDoors = new HashSet<MineDoorPermission>();
        for (final MineDoorPermission mineDoor : MineDoorPermission.mineDoorPermissions.values()) {
            final int encodedTile = Server.surfaceMesh.getTile(mineDoor.getTileX(), mineDoor.getTileY());
            final byte type = Tiles.decodeType(encodedTile);
            if (!Tiles.isMineDoor(type)) {
                removePermission(mineDoor);
            }
            else {
                if (mineDoor.canManage(player)) {
                    mineDoors.add(mineDoor);
                }
                if (!includeAll || ((villageId < 0 || mineDoor.getVillageId() != villageId) && (mineDoor.getVillage() == null || !mineDoor.getVillage().isMayor(player.getWurmId())))) {
                    continue;
                }
                mineDoors.add(mineDoor);
            }
        }
        return mineDoors.toArray(new MineDoorPermission[mineDoors.size()]);
    }
    
    public static final MineDoorPermission[] getOwnedMinedoorsFor(final Player player) {
        final Set<MineDoorPermission> mineDoors = new HashSet<MineDoorPermission>();
        for (final MineDoorPermission mineDoor : MineDoorPermission.mineDoorPermissions.values()) {
            final int encodedTile = Server.surfaceMesh.getTile(mineDoor.getTileX(), mineDoor.getTileY());
            final byte type = Tiles.decodeType(encodedTile);
            if (!Tiles.isMineDoor(type)) {
                removePermission(mineDoor);
            }
            else {
                if (!mineDoor.isOwner(player) && !mineDoor.isActualOwner(player.getWurmId())) {
                    continue;
                }
                mineDoors.add(mineDoor);
            }
        }
        return mineDoors.toArray(new MineDoorPermission[mineDoors.size()]);
    }
    
    public static final void unManageMineDoorsFor(final int villageId) {
        for (final MineDoorPermission md : getAllMineDoors()) {
            if (md.getVillageId() == villageId) {
                md.setVillage(null);
            }
        }
    }
    
    @Override
    public long getWurmId() {
        return this.wurmId;
    }
    
    @Override
    public int getTemplateId() {
        return -10;
    }
    
    public int getTileX() {
        return decodeTileX(this.id);
    }
    
    public int getTileY() {
        return decodeTileY(this.id);
    }
    
    @Override
    public String getObjectName() {
        return this.name;
    }
    
    @Override
    public String getTypeName() {
        final int encodedTile = Server.surfaceMesh.getTile(this.getTileX(), this.getTileY());
        final byte type = Tiles.decodeType(encodedTile);
        final Tiles.Tile tile = Tiles.getTile(type);
        return tile.getDesc();
    }
    
    @Override
    public boolean setObjectName(final String newName, final Creature creature) {
        if (this.name.equals(newName)) {
            return true;
        }
        this.name = newName;
        return true;
    }
    
    @Override
    public boolean isActualOwner(final long playerId) {
        return this.creator == playerId;
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
    
    @Override
    public boolean canChangeName(final Creature creature) {
        return creature.getPower() > 1 || this.isOwner(creature.getWurmId());
    }
    
    @Override
    public boolean canChangeOwner(final Creature creature) {
        return creature.getPower() > 1 || this.isActualOwner(creature.getWurmId());
    }
    
    private boolean showWarning() {
        return false;
    }
    
    @Override
    public String getWarning() {
        if (this.showWarning()) {
            return "NEEDS A LOCK";
        }
        return "";
    }
    
    @Override
    public PermissionsPlayerList getPermissionsPlayerList() {
        return MineDoorSettings.getPermissionsPlayerList(this.id);
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
            final Village vil = Villages.getVillage(this.getTileX(), this.getTileY(), true);
            if (vil != null) {
                return false;
            }
        }
        return this.isOwner(player);
    }
    
    @Override
    public void setIsManaged(final boolean newIsManaged, final Player player) {
        final int oldId = this.villageId;
        if (newIsManaged) {
            if (this.village != null) {
                this.setVillageId(this.village.getId());
            }
            else {
                final Village cv = this.getOwnerVillage();
                if (cv != null) {
                    this.setVillageId(cv.getId());
                }
                else {
                    this.setVillageId(-1);
                }
            }
        }
        else {
            this.setVillageId(-1);
        }
        if (oldId != this.villageId && MineDoorSettings.exists(this.id)) {
            MineDoorSettings.remove(this.id);
            PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), -10L, "Auto", "Cleared Permissions");
        }
        this.permissions.setPermissionBit(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit(), this.villageId != -1);
        try {
            this.save();
        }
        catch (IOException e) {
            MineDoorPermission.logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    @Override
    public boolean isCitizen(final Creature creature) {
        Village vill = this.getManagedByVillage();
        if (vill == null) {
            vill = this.getOwnerVillage();
        }
        return vill != null && vill.isCitizen(creature);
    }
    
    @Override
    public boolean isAllied(final Creature creature) {
        Village vill = this.getManagedByVillage();
        if (vill == null) {
            vill = this.getOwnerVillage();
        }
        return vill != null && vill.isAlly(creature);
    }
    
    @Override
    public boolean isSameKingdom(final Creature creature) {
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            return vill.kingdom == creature.getKingdomId();
        }
        return Players.getInstance().getKingdomForPlayer(this.creator) == creature.getKingdomId();
    }
    
    @Override
    public void addGuest(final long guestId, final int settings) {
        MineDoorSettings.addPlayer(this.id, guestId, settings);
    }
    
    @Override
    public void removeGuest(final long guestId) {
        MineDoorSettings.removePlayer(this.id, guestId);
    }
    
    @Override
    public boolean isGuest(final Creature creature) {
        return this.isGuest(creature.getWurmId());
    }
    
    @Override
    public boolean isGuest(final long playerId) {
        return MineDoorSettings.isGuest(this, playerId);
    }
    
    public final long getOwnerId() {
        return this.creator;
    }
    
    @Override
    public String mayManageText(final Player aPlayer) {
        String vName = "";
        Village vill = this.getManagedByVillage();
        if (vill != null) {
            vName = "Settlement \"" + vill.getName() + "\" may manage";
        }
        else {
            vill = this.getVillage();
            if (vill != null) {
                vName = "Settlement \"" + vill.getName() + "\" may manage";
            }
            else {
                vill = Villages.getVillageForCreature(this.getOwnerId());
                if (vill != null) {
                    vName = "Settlement \"" + vill.getName() + "\" may manage";
                }
            }
        }
        return vName;
    }
    
    @Override
    public String mayManageHover(final Player aPlayer) {
        return "";
    }
    
    @Override
    public String messageOnTick() {
        return "By selecting this you are giving full control to settlement.";
    }
    
    @Override
    public String questionOnTick() {
        return "Are you positive you want to give your control away?";
    }
    
    @Override
    public String messageUnTick() {
        return "By doing this you are reverting the control to owner";
    }
    
    @Override
    public String questionUnTick() {
        return "Are you sure you want them to have control?";
    }
    
    @Override
    public String getSettlementName() {
        String sName = "";
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            sName = vill.getName();
        }
        if (sName.length() == 0) {
            return sName;
        }
        return "Citizens of \"" + sName + "\"";
    }
    
    @Override
    public String getAllianceName() {
        String aName = "";
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            aName = vill.getAllianceName();
        }
        if (aName.length() == 0) {
            return aName;
        }
        return "Alliance of \"" + aName + "\"";
    }
    
    @Override
    public String getKingdomName() {
        byte kingdom = 0;
        final Village vill = this.getPermissionsVillage();
        if (vill != null) {
            kingdom = vill.kingdom;
        }
        else {
            kingdom = Players.getInstance().getKingdomForPlayer(this.creator);
        }
        return "Kingdom of \"" + Kingdoms.getNameFor(kingdom) + "\"";
    }
    
    @Override
    public String getRolePermissionName() {
        return "";
    }
    
    @Override
    public boolean canAllowEveryone() {
        return true;
    }
    
    @Override
    public boolean setNewOwner(final long playerId) {
        if (!this.isManaged() && MineDoorSettings.exists(this.id)) {
            MineDoorSettings.remove(this.id);
            PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), playerId, "Auto", "Cleared Permissions");
        }
        return this.setController(playerId);
    }
    
    @Override
    public String getOwnerName() {
        return PlayerInfoFactory.getPlayerName(this.creator);
    }
    
    void addDefaultAllyPermissions() {
        if (!this.getPermissionsPlayerList().exists(-20L)) {
            final int value = MineDoorSettings.MinedoorPermissions.PASS.getValue();
            this.addGuest(-20L, value);
        }
    }
    
    @Override
    public void addDefaultCitizenPermissions() {
        if (!this.getPermissionsPlayerList().exists(-30L)) {
            final int value = MineDoorSettings.MinedoorPermissions.PASS.getValue();
            this.addGuest(-30L, value);
        }
    }
    
    void addDefaultKingdomPermission() {
        if (!this.getPermissionsPlayerList().exists(-40L)) {
            final int value = MineDoorSettings.MinedoorPermissions.PASS.getValue();
            this.addGuest(-40L, value);
        }
    }
    
    public boolean convertToNewPermissions() {
        boolean didConvert = false;
        final PermissionsPlayerList ppl = StructureSettings.getPermissionsPlayerList(this.getWurmId());
        if (this.allowAllies && !ppl.exists(-20L)) {
            this.addDefaultAllyPermissions();
            didConvert = true;
        }
        if (this.getVillageId() >= 0) {
            this.setIsManaged(true, null);
            didConvert = true;
        }
        if (this.getVillageId() >= 0 && !ppl.exists(-30L)) {
            this.addDefaultCitizenPermissions();
            didConvert = true;
        }
        if (this.allowAll && !ppl.exists(-40L)) {
            this.addDefaultKingdomPermission();
            didConvert = true;
        }
        if (didConvert) {
            try {
                this.save();
            }
            catch (IOException e) {
                MineDoorPermission.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return didConvert;
    }
    
    @Override
    public boolean isItem() {
        return false;
    }
    
    static {
        logger = Logger.getLogger(MineDoorPermission.class.getName());
        mineDoorPermissions = new ConcurrentHashMap<Integer, MineDoorPermission>();
    }
}

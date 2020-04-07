// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.players.PermissionsPlayerList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.Player;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Map;
import com.wurmonline.server.villages.Village;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public abstract class FenceGate extends Door implements MiscConstants
{
    private static final Logger logger;
    final Fence fence;
    private Village village;
    int villageId;
    int openTime;
    int closeTime;
    static final Map<Long, FenceGate> gates;
    
    FenceGate(final Fence aFence) {
        this.village = null;
        this.villageId = -1;
        this.openTime = 0;
        this.closeTime = 0;
        this.fence = aFence;
        FenceGate.gates.put(new Long(aFence.getId()), this);
        try {
            this.load();
        }
        catch (IOException iox) {
            FenceGate.logger.log(Level.WARNING, "Failed to load/save " + this.name + "," + aFence.getId(), iox);
        }
    }
    
    @Override
    public final float getQualityLevel() {
        return this.fence.getCurrentQualityLevel();
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
    
    public final Village getVillage() {
        return this.village;
    }
    
    private Village getPermissionsVillage() {
        final Village vill = this.getVillage();
        if (vill != null) {
            return vill;
        }
        final long wid = this.getOwnerId();
        if (wid != -10L) {
            return Villages.getVillageForCreature(wid);
        }
        return null;
    }
    
    public final int getVillageId() {
        return this.villageId;
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
    
    public final Fence getFence() {
        return this.fence;
    }
    
    public final int getOpenTime() {
        return this.openTime;
    }
    
    public final int getCloseTime() {
        return this.closeTime;
    }
    
    @Override
    public final void addToTiles() {
        this.innerTile = this.fence.getTile();
        final int tilex = this.innerTile.getTileX();
        final int tiley = this.innerTile.getTileY();
        this.innerTile.addDoor(this);
        if (this.fence.isHorizontal()) {
            (this.outerTile = Zones.getOrCreateTile(tilex, tiley - 1, this.fence.isOnSurface())).addDoor(this);
        }
        else {
            (this.outerTile = Zones.getOrCreateTile(tilex - 1, tiley, this.fence.isOnSurface())).addDoor(this);
        }
        this.calculateArea();
    }
    
    @Override
    public final boolean canBeOpenedBy(final Creature creature, final boolean passedThroughDoor) {
        if (creature == null) {
            return false;
        }
        if (MissionTriggers.isDoorOpen(creature, this.getWurmId(), 1)) {
            return true;
        }
        if (creature.getPower() > 1) {
            return true;
        }
        if (creature.isKingdomGuard() || creature.isGhost()) {
            return true;
        }
        if (creature.getLeader() != null && this.canBeOpenedBy(creature.getLeader(), false)) {
            return true;
        }
        if (!creature.canOpenDoors()) {
            return false;
        }
        if (this.village != null && this.village.isEnemy(creature)) {
            return this.canBeUnlockedBy(creature);
        }
        return (creature.isPlayer() && this.mayPass(creature)) || this.canBeUnlockedBy(creature);
    }
    
    @Override
    public final boolean canBeUnlockedBy(final Creature creature) {
        if (creature.getPower() > 1) {
            return true;
        }
        if (this.lockCounter > 0) {
            creature.sendToLoggers("Lock counter=" + this.lockCounter);
            return true;
        }
        if (this.lock == -10L) {
            creature.sendToLoggers("No lock ");
            return true;
        }
        Item doorlock = null;
        try {
            doorlock = Items.getItem(this.lock);
        }
        catch (NoSuchItemException nsi) {
            FenceGate.logger.log(Level.INFO, "Lock has decayed? Id was " + this.lock);
            creature.sendToLoggers("Lock id " + this.lock + " has decayed?");
            return true;
        }
        if (!doorlock.isLocked()) {
            creature.sendToLoggers("It's not locked");
            return true;
        }
        final Item[] items = creature.getKeys();
        for (int x = 0; x < items.length; ++x) {
            if (doorlock.isUnlockedBy(items[x].getWurmId())) {
                creature.sendToLoggers("I have key");
                return true;
            }
        }
        if (this.mayLock(creature)) {
            creature.sendToLoggers("I have Lock Permission");
            return true;
        }
        return false;
    }
    
    @Override
    public final void creatureMoved(final Creature creature, final int diffTileX, final int diffTileY) {
        if (this.covers(creature.getStatus().getPositionX(), creature.getStatus().getPositionY(), creature.getPositionZ(), creature.getFloorLevel(), creature.followsGround())) {
            this.addCreature(creature);
        }
        else {
            this.removeCreature(creature);
        }
    }
    
    @Override
    public final void removeCreature(final Creature creature) {
        if (this.creatures != null) {
            if (this.creatures.contains(creature)) {
                this.creatures.remove(creature);
                if (this.isOpen() && !creature.isGhost()) {
                    creature.getCommunicator().sendCloseFence(this.fence, false, true);
                    boolean close = true;
                    for (final Creature checked : this.creatures) {
                        if (this.canBeOpenedBy(checked, false)) {
                            close = false;
                        }
                    }
                    if (close) {
                        this.close();
                        if (this.watchers != null && creature.isVisible()) {
                            for (final VirtualZone z : this.watchers) {
                                if (z.getWatcher() != creature) {
                                    z.closeFence(this.fence, false, false);
                                }
                            }
                        }
                    }
                }
            }
            if (this.creatures.size() == 0) {
                this.creatures = null;
            }
        }
    }
    
    public final boolean containsCreature(final Creature creature) {
        return this.creatures != null && this.creatures.contains(creature);
    }
    
    @Override
    public void updateDoor(final Creature creature, final Item key, final boolean removedKey) {
        final boolean isOpenToCreature = this.canBeOpenedBy(creature, false);
        if (removedKey) {
            if (this.creatures != null) {
                if (this.creatures.contains(creature) && !isOpenToCreature && this.canBeUnlockedByKey(key)) {
                    creature.getCommunicator().sendCloseFence(this.fence, false, true);
                    if (this.isOpen()) {
                        boolean close = true;
                        for (final Creature checked : this.creatures) {
                            if (this.canBeOpenedBy(checked, false)) {
                                close = false;
                            }
                        }
                        if (close && creature.isVisible() && !creature.isGhost()) {
                            this.close();
                            if (this.watchers != null && creature.isVisible()) {
                                for (final VirtualZone z : this.watchers) {
                                    if (z.getWatcher() != creature) {
                                        z.closeFence(this.fence, false, false);
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.creatures.size() == 0) {
                    this.creatures = null;
                }
            }
        }
        else if (this.creatures != null && this.creatures.contains(creature) && isOpenToCreature && this.canBeUnlockedByKey(key) && !this.isOpen() && creature.isVisible() && !creature.isGhost()) {
            if (this.watchers != null && creature.isVisible()) {
                for (final VirtualZone z2 : this.watchers) {
                    if (z2.getWatcher() != creature) {
                        z2.openFence(this.fence, false, false);
                    }
                }
            }
            this.open();
            creature.getCommunicator().sendOpenFence(this.fence, true, true);
        }
    }
    
    @Override
    public final boolean addCreature(final Creature creature) {
        if (this.creatures == null) {
            this.creatures = new HashSet<Creature>();
        }
        if (!this.creatures.contains(creature)) {
            this.creatures.add(creature);
            if (this.canBeOpenedBy(creature, false) && !creature.isGhost()) {
                if (!this.isOpen()) {
                    if (this.watchers != null && creature.isVisible()) {
                        for (final VirtualZone z : this.watchers) {
                            if (z.getWatcher() != creature) {
                                z.openFence(this.fence, false, false);
                            }
                        }
                    }
                    this.open();
                    if (creature.getEnemyPresense() > 0 && this.getVillage() == null) {
                        this.setLockCounter((short)120);
                    }
                    creature.getCommunicator().sendOpenFence(this.fence, true, true);
                }
                else {
                    creature.getCommunicator().sendOpenFence(this.fence, true, true);
                }
            }
            return true;
        }
        return false;
    }
    
    public final boolean keyFits(final long keyId) throws NoSuchLockException {
        if (this.lock == -10L) {
            throw new NoSuchLockException("No ID");
        }
        try {
            final Item doorlock = Items.getItem(this.lock);
            return doorlock.isUnlockedBy(keyId);
        }
        catch (NoSuchItemException nsi) {
            FenceGate.logger.log(Level.INFO, "Lock has decayed? Id was " + this.lock);
            return false;
        }
    }
    
    public final boolean isOpenTime() {
        return false;
    }
    
    @Override
    final void close() {
        this.open = false;
    }
    
    @Override
    final void open() {
        this.open = true;
    }
    
    public final void removeFromVillage() {
        if (this.village != null) {
            this.village.removeGate(this);
        }
    }
    
    public static final FenceGate getFenceGate(final long id) {
        final Long lid = new Long(id);
        final FenceGate toReturn = FenceGate.gates.get(lid);
        return toReturn;
    }
    
    public static final FenceGate[] getAllGates() {
        return FenceGate.gates.values().toArray(new FenceGate[FenceGate.gates.size()]);
    }
    
    public static final FenceGate[] getManagedGatesFor(final Player player, final int villageId, final boolean includeAll) {
        final Set<FenceGate> fenceGates = new HashSet<FenceGate>();
        for (final FenceGate gate : FenceGate.gates.values()) {
            if ((gate.canManage(player) || (villageId >= 0 && gate.getVillageId() == villageId)) && (includeAll || gate.hasLock())) {
                fenceGates.add(gate);
            }
        }
        return fenceGates.toArray(new FenceGate[fenceGates.size()]);
    }
    
    public static final FenceGate[] getOwnedGatesFor(final Player player) {
        final Set<FenceGate> fenceGates = new HashSet<FenceGate>();
        for (final FenceGate gate : FenceGate.gates.values()) {
            if (gate.isOwner(player) || gate.isActualOwner(player.getWurmId())) {
                fenceGates.add(gate);
            }
        }
        return fenceGates.toArray(new FenceGate[fenceGates.size()]);
    }
    
    public static final void unManageGatesFor(final int villageId) {
        for (final FenceGate gate : getAllGates()) {
            if (gate.getVillageId() == villageId) {
                gate.setIsManaged(false, null);
            }
        }
    }
    
    public final Village getOwnerVillage() {
        return Villages.getVillageForCreature(this.getOwnerId());
    }
    
    @Override
    public final boolean covers(final float x, final float y, final float posz, final int floorLevel, final boolean followGround) {
        return ((this.fence != null && this.fence.isWithinZ(posz + 1.0f, posz, followGround)) || (this.isTransition() && floorLevel <= 0)) && x >= this.startx && x <= this.endx && y >= this.starty && y <= this.endy;
    }
    
    @Override
    public final int getFloorLevel() {
        return this.fence.getFloorLevel();
    }
    
    public abstract void setOpenTime(final int p0);
    
    public abstract void setCloseTime(final int p0);
    
    @Override
    public abstract void setLock(final long p0);
    
    @Override
    public abstract void save() throws IOException;
    
    @Override
    abstract void load() throws IOException;
    
    @Override
    public abstract void delete();
    
    public final long getOwnerId() {
        if (this.lock != -10L) {
            try {
                final Item doorlock = Items.getItem(this.lock);
                return doorlock.getLastOwnerId();
            }
            catch (NoSuchItemException nsi) {
                return -10L;
            }
        }
        return -10L;
    }
    
    @Override
    public long getWurmId() {
        return this.fence.getId();
    }
    
    @Override
    public boolean setObjectName(final String aNewName, final Creature aCreature) {
        this.setName(aNewName);
        this.outerTile.updateFence(this.getFence());
        return true;
    }
    
    @Override
    public boolean isActualOwner(final long playerId) {
        final long wid = this.getOwnerId();
        return this.lock != -10L && wid == playerId;
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
    public boolean canChangeOwner(final Creature creature) {
        return this.hasLock() && (creature.getPower() > 1 || this.isActualOwner(creature.getWurmId()));
    }
    
    @Override
    public String getWarning() {
        if (this.lock == -10L) {
            return "NEEDS TO HAVE A LOCK FOR PERMISSIONS TO WORK";
        }
        if (!this.isLocked()) {
            return "NEEDS TO BE LOCKED OTHERWISE EVERYONE CAN PASS";
        }
        return "";
    }
    
    @Override
    public PermissionsPlayerList getPermissionsPlayerList() {
        return DoorSettings.getPermissionsPlayerList(this.getWurmId());
    }
    
    @Override
    public final boolean canHavePermissions() {
        return this.isLocked();
    }
    
    @Override
    public final boolean mayShowPermissions(final Creature creature) {
        return this.hasLock() && this.mayManage(creature);
    }
    
    @Override
    public boolean canManage(final Creature creature) {
        if (DoorSettings.isExcluded(this, creature)) {
            return false;
        }
        if (DoorSettings.canManage(this, creature)) {
            return true;
        }
        if (creature.getCitizenVillage() == null) {
            return false;
        }
        final Village vill = this.getManagedByVillage();
        return vill != null && vill.isCitizen(creature) && vill.isActionAllowed((short)667, creature);
    }
    
    @Override
    public boolean mayManage(final Creature creature) {
        return creature.getPower() > 1 || this.canManage(creature);
    }
    
    @Override
    public boolean isManaged() {
        return this.fence != null && this.fence.getSettings().hasPermission(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit());
    }
    
    @Override
    public boolean isManageEnabled(final Player player) {
        if (player.getPower() > 1) {
            return true;
        }
        if (this.isManaged()) {
            final Village vil = this.getVillage();
            if (vil != null) {
                return false;
            }
        }
        return this.isOwner(player);
    }
    
    @Override
    public void setIsManaged(final boolean newIsManaged, @Nullable final Player player) {
        if (this.fence != null) {
            final int oldId = this.villageId;
            if (newIsManaged) {
                final Village v = this.getVillage();
                if (v != null) {
                    this.setVillageId(v.getId());
                }
                else {
                    final Village cv = this.getOwnerVillage();
                    if (cv == null) {
                        return;
                    }
                    this.setVillageId(cv.getId());
                }
            }
            else {
                this.setVillageId(-1);
            }
            if (oldId != this.villageId && DoorSettings.exists(this.getWurmId())) {
                DoorSettings.remove(this.getWurmId());
                PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), -10L, "Auto", "Cleared Permissions");
            }
            this.fence.getSettings().setPermissionBit(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit(), newIsManaged);
            this.fence.savePermissions();
            try {
                this.save();
            }
            catch (IOException e) {
                FenceGate.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
    
    @Override
    public String mayManageText(final Player aPlayer) {
        String vName = "";
        Village vill = this.getManagedByVillage();
        if (vill != null) {
            vName = vill.getName();
        }
        else {
            vill = this.getVillage();
            if (vill != null) {
                vName = vill.getName();
            }
            else {
                vill = Villages.getVillageForCreature(this.getOwnerId());
                if (vill != null) {
                    vName = vill.getName();
                }
            }
        }
        if (vName.length() > 0) {
            return "Settlement \"" + vName + "\" may manage";
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
            kingdom = Players.getInstance().getKingdomForPlayer(this.getOwnerId());
        }
        return "Kingdom of \"" + Kingdoms.getNameFor(kingdom) + "\"";
    }
    
    @Override
    public void addDefaultCitizenPermissions() {
        if (!this.getPermissionsPlayerList().exists(-30L)) {
            final int value = DoorSettings.DoorPermissions.PASS.getValue();
            this.addGuest(-30L, value);
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
        return Players.getInstance().getKingdomForPlayer(this.getOwnerId()) == creature.getKingdomId();
    }
    
    @Override
    public boolean isGuest(final Creature creature) {
        return this.isGuest(creature.getWurmId());
    }
    
    @Override
    public boolean isGuest(final long playerId) {
        return DoorSettings.isGuest(this, playerId);
    }
    
    @Override
    public void addGuest(final long guestId, final int aSettings) {
        DoorSettings.addPlayer(this.getWurmId(), guestId, aSettings);
    }
    
    @Override
    public void removeGuest(final long guestId) {
        DoorSettings.removePlayer(this.getWurmId(), guestId);
    }
    
    @Override
    public final boolean mayPass(final Creature creature) {
        if (!this.isLocked()) {
            return true;
        }
        if (DoorSettings.exists(this.getWurmId())) {
            if (DoorSettings.isExcluded(this, creature)) {
                return false;
            }
            if (DoorSettings.mayPass(this, creature)) {
                return true;
            }
        }
        if (this.isManaged()) {
            final Village vill = this.getManagedByVillage();
            final VillageRole vr = (vill == null) ? null : vill.getRoleFor(creature);
            return vr != null && vr.mayPassGates();
        }
        return this.isOwner(creature);
    }
    
    public final boolean mayAttachLock(final Creature creature) {
        if (!this.hasLock()) {
            return true;
        }
        if (this.village != null) {
            final VillageRole vr = this.village.getRoleFor(creature);
            return vr != null && vr.mayAttachLock();
        }
        return this.isOwner(creature);
    }
    
    @Override
    public final boolean mayLock(final Creature creature) {
        if (DoorSettings.exists(this.getWurmId())) {
            if (DoorSettings.isExcluded(this, creature)) {
                return false;
            }
            if (DoorSettings.mayLock(this, creature)) {
                return true;
            }
        }
        if (this.isManaged()) {
            final Village vill = this.getManagedByVillage();
            final VillageRole vr = (vill == null) ? null : vill.getRoleFor(creature);
            return vr != null && vr.mayAttachLock();
        }
        return this.isOwner(creature);
    }
    
    @Override
    public String getTypeName() {
        if (this.fence == null) {
            return "No Fence!";
        }
        return this.fence.getTypeName();
    }
    
    @Override
    public boolean isNotLockpickable() {
        return this.fence.isNotLockpickable();
    }
    
    @Override
    public boolean setNewOwner(final long playerId) {
        try {
            if (!this.isManaged() && DoorSettings.exists(this.getWurmId())) {
                DoorSettings.remove(this.getWurmId());
                PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), -10L, "Auto", "Cleared Permissions");
            }
            final Item theLock = this.getLock();
            FenceGate.logger.info("Overwritting owner (" + theLock.getLastOwnerId() + ") of lock " + theLock.getWurmId() + " to " + playerId);
            theLock.setLastOwnerId(playerId);
            return true;
        }
        catch (NoSuchLockException ex) {
            return false;
        }
    }
    
    @Override
    public String getOwnerName() {
        try {
            final Item theLock = this.getLock();
            return PlayerInfoFactory.getPlayerName(theLock.getLastOwnerId());
        }
        catch (NoSuchLockException ex) {
            return "";
        }
    }
    
    public final boolean maySeeHistory(final Creature creature) {
        return creature.getPower() > 1 || this.isOwner(creature);
    }
    
    public boolean convertToNewPermissions() {
        boolean didConvert = false;
        if (this.village != null) {
            this.setIsManaged(true, null);
            didConvert = true;
        }
        if (didConvert) {
            this.fence.savePermissions();
        }
        return didConvert;
    }
    
    public boolean fixForNewPermissions() {
        boolean didConvert = false;
        if (this.village != null) {
            this.addDefaultCitizenPermissions();
            didConvert = true;
        }
        return didConvert;
    }
    
    public abstract void setVillageId(final int p0);
    
    static {
        logger = Logger.getLogger(FenceGate.class.getName());
        gates = new ConcurrentHashMap<Long, FenceGate>();
    }
}

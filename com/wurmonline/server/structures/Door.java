// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.creatures.AnimalSettings;
import java.io.IOException;
import java.util.Iterator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.zones.NoSuchTileException;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import java.util.HashSet;
import com.wurmonline.server.Server;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.creatures.Creature;
import java.util.Set;
import com.wurmonline.server.zones.VolaTile;
import java.util.logging.Logger;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.MiscConstants;

public abstract class Door implements MiscConstants, SoundNames, TimeConstants, PermissionsPlayerList.ISettings
{
    Wall wall;
    private static Logger logger;
    long lock;
    long structure;
    boolean open;
    VolaTile outerTile;
    VolaTile innerTile;
    protected int startx;
    protected int starty;
    protected int endx;
    protected int endy;
    Set<Creature> creatures;
    Set<VirtualZone> watchers;
    short lockCounter;
    boolean preAlertLockedStatus;
    String name;
    
    Door() {
        this.lock = -10L;
        this.structure = -10L;
        this.open = false;
        this.lockCounter = 0;
        this.preAlertLockedStatus = false;
        this.name = "";
    }
    
    Door(final Wall _wall) {
        this.lock = -10L;
        this.structure = -10L;
        this.open = false;
        this.lockCounter = 0;
        this.preAlertLockedStatus = false;
        this.name = "";
        this.wall = _wall;
    }
    
    public final void setLockCounter(final short newcounter) {
        if (this.lockCounter <= 0 || newcounter <= 0) {
            this.playLockSound();
        }
        if (newcounter > this.lockCounter) {
            this.lockCounter = newcounter;
        }
        if (this.lockCounter > 0 && this.getInnerTile() != null) {
            this.getInnerTile().addUnlockedDoor(this);
        }
    }
    
    public short getLockCounter() {
        return this.lockCounter;
    }
    
    public String getLockCounterTime() {
        final int m = this.lockCounter / 120;
        final int s = this.lockCounter % 120 / 2;
        if (m > 0) {
            return m + " minutes and " + s + " seconds.";
        }
        return s + " seconds.";
    }
    
    private void playLockSound() {
        if (this.innerTile != null) {
            SoundPlayer.playSound("sound.object.lockunlock", this.innerTile.tilex, this.innerTile.tiley, this.innerTile.isOnSurface(), 1.0f);
            Server.getInstance().broadCastMessage("A loud *click* is heard.", this.innerTile.tilex, this.innerTile.tiley, this.innerTile.isOnSurface(), 5);
        }
        else if (this.outerTile != null) {
            SoundPlayer.playSound("sound.object.lockunlock", this.outerTile.tilex, this.outerTile.tiley, this.outerTile.isOnSurface(), 1.0f);
            Server.getInstance().broadCastMessage("A loud *click* is heard.", this.outerTile.tilex, this.outerTile.tiley, this.outerTile.isOnSurface(), 5);
        }
    }
    
    public final void setStructureId(final long structureId) {
        this.structure = structureId;
    }
    
    public final long getStructureId() {
        return this.structure;
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
    
    public final String getName() {
        return this.name;
    }
    
    public final VolaTile getOuterTile() {
        return this.outerTile;
    }
    
    public final VolaTile getInnerTile() {
        return this.innerTile;
    }
    
    public int getTileX() {
        if (this.outerTile != null) {
            return this.outerTile.tilex;
        }
        if (this.innerTile != null) {
            return this.innerTile.tilex;
        }
        return -1;
    }
    
    public int getTileY() {
        if (this.outerTile != null) {
            return this.outerTile.tiley;
        }
        if (this.innerTile != null) {
            return this.innerTile.tiley;
        }
        return -1;
    }
    
    public final Wall getWall() throws NoSuchWallException {
        if (this.wall == null) {
            throw new NoSuchWallException("null inner wall for tilex=" + this.getTileX() + ", tiley=" + this.getTileY() + " structure=" + this.getStructureId());
        }
        return this.wall;
    }
    
    final void calculateArea() {
        final int innerTileStartX = this.innerTile.getTileX();
        final int outerTileStartX = this.outerTile.getTileX();
        final int innerTileStartY = this.innerTile.getTileY();
        final int outerTileStartY = this.outerTile.getTileY();
        if (innerTileStartX == outerTileStartX) {
            this.starty = (Math.min(innerTileStartY, outerTileStartY) << 2) + 2;
            this.endy = (Math.max(innerTileStartY, outerTileStartY) << 2) + 2;
            this.startx = innerTileStartX << 2;
            this.endx = innerTileStartX + 1 << 2;
        }
        else {
            this.starty = innerTileStartY << 2;
            this.endy = innerTileStartY + 1 << 2;
            this.startx = (Math.min(innerTileStartX, outerTileStartX) << 2) + 2;
            this.endx = (Math.max(innerTileStartX, outerTileStartX) << 2) + 2;
        }
    }
    
    public final boolean isTransition() {
        return this.innerTile.isTransition() || this.outerTile.isTransition();
    }
    
    public boolean covers(final float x, final float y, final float posz, final int floorLevel, final boolean followGround) {
        return ((this.wall != null && this.wall.isWithinZ(posz + 1.0f, posz, followGround)) || (this.isTransition() && floorLevel <= 0)) && x >= this.startx && x <= this.endx && y >= this.starty && y <= this.endy;
    }
    
    public void addToTiles() {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            (this.outerTile = this.wall.getOrCreateOuterTile(struct.isSurfaced())).addDoor(this);
            (this.innerTile = this.wall.getOrCreateInnerTile(struct.isSurfaced())).addDoor(this);
            this.calculateArea();
        }
        catch (NoSuchStructureException nss) {
            Door.logger.log(Level.WARNING, "No such structure? structure: " + this.structure, nss);
        }
        catch (NoSuchZoneException nsz) {
            Door.logger.log(Level.WARNING, "No such zone - wall: " + this.wall + " - " + nsz.getMessage(), nsz);
        }
        catch (NoSuchTileException nst) {
            Door.logger.log(Level.WARNING, "No such tile - wall: " + this.wall + " - " + nst.getMessage(), nst);
        }
    }
    
    public final void removeFromTiles() {
        if (this.outerTile != null) {
            this.outerTile.removeDoor(this);
        }
        if (this.innerTile != null) {
            this.innerTile.removeDoor(this);
        }
    }
    
    public boolean canBeOpenedBy(final Creature creature, final boolean wentThroughDoor) {
        return creature != null && (creature.isKingdomGuard() || creature.isGhost() || MissionTriggers.isDoorOpen(creature, this.wall.getId(), 1) || creature.getPower() > 0 || (creature.getLeader() != null && this.canBeOpenedBy(creature.getLeader(), false)) || (creature.canOpenDoors() && this.canBeUnlockedBy(creature)));
    }
    
    public boolean canBeUnlockedByKey(final Item key) {
        Item doorlock = null;
        try {
            doorlock = Items.getItem(this.lock);
        }
        catch (NoSuchItemException nsi) {
            return false;
        }
        return doorlock.isLocked() && doorlock.isUnlockedBy(key.getWurmId());
    }
    
    public boolean canBeUnlockedBy(final Creature creature) {
        return this.mayPass(creature);
    }
    
    public void creatureMoved(final Creature creature, final int diffTileX, final int diffTileY) {
        if (this.covers(creature.getStatus().getPositionX(), creature.getStatus().getPositionY(), creature.getPositionZ(), creature.getFloorLevel(), creature.followsGround())) {
            if (!this.addCreature(creature) && (diffTileX != 0 || diffTileY != 0)) {
                if (!this.canBeOpenedBy(creature, true)) {
                    try {
                        final int tilex = creature.getTileX();
                        final int tiley = creature.getTileY();
                        final VolaTile tile = Zones.getZone(tilex, tiley, creature.isOnSurface()).getTileOrNull(tilex, tiley);
                        if (tile != null) {
                            if (tile == this.innerTile) {
                                final int oldTileX = tilex - diffTileX;
                                final int oldTileY = tiley - diffTileY;
                                if (creature instanceof Player) {
                                    creature.getCommunicator().sendAlertServerMessage("You cannot enter that building.");
                                    Door.logger.log(Level.WARNING, creature.getName() + " a cheater? Passed through door at " + creature.getStatus().getPositionX() + ", " + creature.getStatus().getPositionY() + ", z=" + creature.getPositionZ() + ", minZ=" + this.wall.getMinZ());
                                    creature.setTeleportPoints((short)oldTileX, (short)oldTileY, creature.getLayer(), creature.getFloorLevel());
                                    creature.startTeleporting();
                                    creature.getCommunicator().sendTeleport(false);
                                }
                            }
                        }
                        else {
                            Door.logger.log(Level.WARNING, "A door on no tile at " + creature.getStatus().getPositionX() + ", " + creature.getStatus().getPositionY() + ", structure: " + this.structure);
                        }
                    }
                    catch (NoSuchZoneException nsz) {
                        Door.logger.log(Level.WARNING, "A door in no zone at " + creature.getStatus().getPositionX() + ", " + creature.getStatus().getPositionY() + ", structure: " + this.structure + " - " + nsz);
                    }
                }
                else if (this.structure != -10L && creature.isPlayer()) {
                    final int tilex = creature.getTileX();
                    final int tiley = creature.getTileY();
                    final VolaTile tile = Zones.getTileOrNull(tilex, tiley, creature.isOnSurface());
                    if (tile == this.innerTile) {
                        if (creature.getEnemyPresense() > 0 && tile.getVillage() == null) {
                            this.setLockCounter((short)120);
                        }
                    }
                    else if (tile == this.outerTile) {
                        final int oldTileX = tilex - diffTileX;
                        final int oldTileY = tiley - diffTileY;
                        try {
                            final VolaTile oldtile = Zones.getZone(oldTileX, oldTileY, creature.isOnSurface()).getTileOrNull(oldTileX, oldTileY);
                            if (oldtile != null && oldtile == this.innerTile && creature.getEnemyPresense() > 0 && oldtile.getVillage() == null) {
                                this.setLockCounter((short)120);
                            }
                        }
                        catch (NoSuchZoneException nsz2) {
                            Door.logger.log(Level.WARNING, "A door in no zone at " + creature.getStatus().getPositionX() + ", " + creature.getStatus().getPositionY() + ", structure: " + this.structure + " - " + nsz2);
                        }
                    }
                }
            }
        }
        else {
            this.removeCreature(creature);
        }
    }
    
    public void updateDoor(final Creature creature, final Item key, final boolean removedKey) {
        final boolean isOpenToCreature = this.canBeOpenedBy(creature, false);
        if (removedKey) {
            if (this.creatures != null) {
                if (this.creatures.contains(creature) && !isOpenToCreature && this.canBeUnlockedByKey(key) && this.isOpen()) {
                    boolean close = true;
                    for (final Creature checked : this.creatures) {
                        if (this.canBeOpenedBy(checked, false)) {
                            close = false;
                        }
                    }
                    if (close && creature.isVisible() && !creature.isGhost()) {
                        this.close();
                    }
                }
                if (this.creatures.size() == 0) {
                    this.creatures = null;
                }
            }
        }
        else if (this.creatures != null && this.creatures.contains(creature) && isOpenToCreature && this.canBeUnlockedByKey(key)) {
            if (!this.isOpen() && creature.isVisible() && !creature.isGhost()) {
                this.open();
            }
            creature.getCommunicator().sendPassable(true, this);
        }
    }
    
    public void removeCreature(final Creature creature) {
        if (this.creatures != null) {
            if (this.creatures.contains(creature)) {
                this.creatures.remove(creature);
                creature.setCurrentDoor(null);
                creature.getCommunicator().sendPassable(false, this);
                if (this.isOpen()) {
                    boolean close = true;
                    for (final Creature checked : this.creatures) {
                        if (this.canBeOpenedBy(checked, false)) {
                            close = false;
                        }
                    }
                    if (close && creature.isVisible() && !creature.isGhost()) {
                        this.close();
                    }
                }
            }
            if (this.creatures.size() == 0) {
                this.creatures = null;
            }
        }
    }
    
    public boolean addCreature(final Creature creature) {
        if (this.creatures == null) {
            this.creatures = new HashSet<Creature>();
        }
        if (!this.creatures.contains(creature)) {
            this.creatures.add(creature);
            creature.setCurrentDoor(this);
            if (this.canBeOpenedBy(creature, false)) {
                if (!this.isOpen() && creature.isVisible() && !creature.isGhost()) {
                    this.open();
                }
                creature.getCommunicator().sendPassable(true, this);
            }
            return true;
        }
        return false;
    }
    
    public void setLock(final long lockid) {
        this.lock = lockid;
        try {
            this.save();
        }
        catch (IOException iox) {
            Door.logger.log(Level.WARNING, "Failed to save door for structure with id " + this.structure);
        }
    }
    
    public final long getLockId() throws NoSuchLockException {
        if (this.lock == -10L) {
            throw new NoSuchLockException("No ID");
        }
        return this.lock;
    }
    
    boolean keyFits(final long keyId) throws NoSuchLockException {
        if (this.lock == -10L) {
            throw new NoSuchLockException("No ID");
        }
        try {
            final Structure struct = Structures.getStructure(this.structure);
            if (struct.getWritId() == keyId) {
                return true;
            }
        }
        catch (NoSuchStructureException nss) {
            Door.logger.log(Level.WARNING, "This door's structure does not exist! " + this.startx + ", " + this.starty + "-" + this.endx + ", " + this.endy + ", structure: " + this.structure + " - " + nss, nss);
        }
        try {
            final Item doorlock = Items.getItem(this.lock);
            return doorlock.isUnlockedBy(keyId);
        }
        catch (NoSuchItemException nsi) {
            Door.logger.log(Level.INFO, "Lock has decayed? Id was " + this.lock + ", structure: " + this.structure + " - " + nsi);
            return false;
        }
    }
    
    public final boolean isOpen() {
        return this.open;
    }
    
    void close() {
        if (this.wall != null && this.wall.isFinished()) {
            if (this.wall.isAlwaysOpen()) {
                return;
            }
            if (this.innerTile != null) {
                SoundPlayer.playSound("sound.door.close", this.innerTile.tilex, this.innerTile.tiley, this.innerTile.isOnSurface(), 1.0f);
            }
            else if (this.outerTile != null) {
                SoundPlayer.playSound("sound.door.close", this.outerTile.tilex, this.outerTile.tiley, this.outerTile.isOnSurface(), 1.0f);
            }
        }
        this.open = false;
        if (this.watchers != null) {
            for (final VirtualZone z : this.watchers) {
                z.closeDoor(this);
            }
        }
    }
    
    void open() {
        if (this.wall != null && this.wall.isFinished()) {
            if (this.wall.isAlwaysOpen()) {
                return;
            }
            if (this.innerTile != null) {
                SoundPlayer.playSound("sound.door.open", this.innerTile.tilex, this.innerTile.tiley, this.innerTile.isOnSurface(), 1.0f);
            }
            else if (this.outerTile != null) {
                SoundPlayer.playSound("sound.door.open", this.outerTile.tilex, this.outerTile.tiley, this.outerTile.isOnSurface(), 1.0f);
            }
        }
        this.open = true;
        if (this.watchers != null) {
            for (final VirtualZone z : this.watchers) {
                z.openDoor(this);
            }
        }
    }
    
    public float getQualityLevel() {
        return this.wall.getCurrentQualityLevel();
    }
    
    public final Item getLock() throws NoSuchLockException {
        if (this.lock == -10L) {
            throw new NoSuchLockException("No ID");
        }
        try {
            final Item toReturn = Items.getItem(this.lock);
            return toReturn;
        }
        catch (NoSuchItemException nsi) {
            throw new NoSuchLockException(nsi);
        }
    }
    
    public final boolean pollUnlocked() {
        if (this.lockCounter > 0) {
            --this.lockCounter;
            if (this.lockCounter == 0) {
                this.playLockSound();
                return true;
            }
        }
        return false;
    }
    
    public final boolean startAlert(final boolean playSound) {
        if (!(this.preAlertLockedStatus = this.isLocked())) {
            this.lock(playSound);
            return true;
        }
        return false;
    }
    
    public final boolean endAlert(final boolean playSound) {
        if (!this.preAlertLockedStatus) {
            this.unlock(playSound);
            return true;
        }
        return false;
    }
    
    public final void lock(final boolean playSound) {
        try {
            final Item lLock = this.getLock();
            lLock.lock();
            if (playSound) {
                this.playLockSound();
            }
        }
        catch (NoSuchLockException ex) {}
    }
    
    public final void unlock(final boolean playSound) {
        try {
            final Item lLock = this.getLock();
            lLock.unlock();
            if (playSound) {
                this.playLockSound();
            }
        }
        catch (NoSuchLockException ex) {}
    }
    
    public final boolean isUnlocked() {
        return !this.isLocked();
    }
    
    public final boolean isLocked() {
        if (this.lockCounter > 0) {
            return false;
        }
        try {
            final Item lLock = this.getLock();
            return lLock.isLocked();
        }
        catch (NoSuchLockException nsi) {
            return false;
        }
    }
    
    public void setNewName(final String newname) {
        this.name = newname;
        this.innerTile.updateWall(this.wall);
        this.outerTile.updateWall(this.wall);
    }
    
    @Override
    public abstract void save() throws IOException;
    
    abstract void load() throws IOException;
    
    public abstract void delete();
    
    public abstract void setName(final String p0);
    
    public int getFloorLevel() {
        return this.wall.getFloorLevel();
    }
    
    @Override
    public int getMaxAllowed() {
        return AnimalSettings.getMaxAllowed();
    }
    
    @Override
    public long getWurmId() {
        return this.wall.getId();
    }
    
    @Override
    public int getTemplateId() {
        return -10;
    }
    
    @Override
    public String getObjectName() {
        return this.getName();
    }
    
    @Override
    public boolean setObjectName(final String aNewName, final Creature aCreature) {
        this.setName(aNewName);
        return true;
    }
    
    @Override
    public boolean isActualOwner(final long playerId) {
        return this.isOwner(playerId);
    }
    
    @Override
    public boolean isOwner(final Creature creature) {
        return this.isOwner(creature.getWurmId());
    }
    
    @Override
    public boolean isOwner(final long playerId) {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.isOwner(playerId);
        }
        catch (NoSuchStructureException e) {
            return false;
        }
    }
    
    @Override
    public boolean canChangeName(final Creature creature) {
        return this.isOwner(creature) || creature.getPower() > 1;
    }
    
    @Override
    public boolean canChangeOwner(final Creature creature) {
        return false;
    }
    
    private boolean showWarning() {
        try {
            this.getLock();
            return false;
        }
        catch (NoSuchLockException e) {
            return true;
        }
    }
    
    @Override
    public String getWarning() {
        if (this.showWarning()) {
            return "NEEDS TO HAVE A LOCK!";
        }
        return "";
    }
    
    @Override
    public PermissionsPlayerList getPermissionsPlayerList() {
        return DoorSettings.getPermissionsPlayerList(this.getWurmId());
    }
    
    @Override
    public boolean isManaged() {
        return this.wall != null && this.wall.getSettings().hasPermission(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit());
    }
    
    @Override
    public boolean isManageEnabled(final Player player) {
        return this.mayManage(player) || player.getPower() > 1;
    }
    
    @Override
    public void setIsManaged(final boolean newIsManaged, final Player player) {
        if (this.wall != null) {
            if (newIsManaged && DoorSettings.exists(this.getWurmId())) {
                DoorSettings.remove(this.getWurmId());
                if (player != null) {
                    PermissionsHistories.addHistoryEntry(this.getWurmId(), System.currentTimeMillis(), player.getWurmId(), player.getName(), "Removed all permissions");
                }
            }
            this.wall.getSettings().setPermissionBit(Permissions.Allow.SETTLEMENT_MAY_MANAGE.getBit(), newIsManaged);
            this.wall.savePermissions();
        }
    }
    
    @Override
    public void addDefaultCitizenPermissions() {
    }
    
    @Override
    public boolean isCitizen(final Creature aCreature) {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.isCitizen(aCreature);
        }
        catch (NoSuchStructureException e) {
            return false;
        }
    }
    
    @Override
    public boolean isAllied(final Creature aCreature) {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.isAllied(aCreature);
        }
        catch (NoSuchStructureException e) {
            return false;
        }
    }
    
    @Override
    public boolean isSameKingdom(final Creature aCreature) {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.isSameKingdom(aCreature);
        }
        catch (NoSuchStructureException e) {
            return false;
        }
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
    public boolean canHavePermissions() {
        return this.isLocked();
    }
    
    @Override
    public boolean mayShowPermissions(final Creature creature) {
        return !this.isManaged() && this.hasLock() && this.mayManage(creature);
    }
    
    public boolean canManage(final Creature creature) {
        if (this.wall == null) {
            return false;
        }
        final Structure structure = Structures.getStructureOrNull(this.wall.getStructureId());
        return structure != null && structure.canManage(creature);
    }
    
    public boolean mayManage(final Creature creature) {
        return creature.getPower() > 1 || this.canManage(creature);
    }
    
    public boolean mayPass(final Creature creature) {
        if (creature.getPower() > 1) {
            return true;
        }
        if (this.wall == null) {
            return true;
        }
        final Structure structure = Structures.getStructureOrNull(this.wall.getStructureId());
        if (structure == null) {
            return true;
        }
        if (!this.isLocked()) {
            return true;
        }
        if (structure.isExcluded(creature)) {
            return false;
        }
        if (this.isManaged()) {
            return structure.mayPass(creature);
        }
        return !DoorSettings.isExcluded(this, creature) && DoorSettings.mayPass(this, creature);
    }
    
    public boolean mayLock(final Creature creature) {
        if (creature.getPower() > 1) {
            return true;
        }
        if (this.wall == null) {
            return true;
        }
        final Structure structure = Structures.getStructureOrNull(this.wall.getStructureId());
        return structure == null || (!structure.isExcluded(creature) && this.isManaged() && structure.mayModify(creature));
    }
    
    public boolean hasLock() {
        try {
            this.getLock();
        }
        catch (NoSuchLockException e) {
            this.lock = -10L;
        }
        return this.lock != -10L;
    }
    
    @Override
    public String getTypeName() {
        if (this.wall == null) {
            return "No Wall!";
        }
        return this.wall.getTypeName();
    }
    
    @Override
    public String mayManageText(final Player aPlayer) {
        return "Controlled By Building";
    }
    
    @Override
    public String mayManageHover(final Player aPlayer) {
        return "If ticked, then building controls entry.";
    }
    
    @Override
    public String messageOnTick() {
        return "This will allow the building to Control this door.";
    }
    
    @Override
    public String questionOnTick() {
        return "Are you sure?";
    }
    
    @Override
    public String messageUnTick() {
        return "This will allow the door to be independant of the building 'May Enter' setting.";
    }
    
    @Override
    public String questionUnTick() {
        return "Are you sure?";
    }
    
    @Override
    public String getSettlementName() {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.getSettlementName();
        }
        catch (NoSuchStructureException e) {
            return "";
        }
    }
    
    @Override
    public String getAllianceName() {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.getAllianceName();
        }
        catch (NoSuchStructureException e) {
            return "";
        }
    }
    
    @Override
    public String getKingdomName() {
        try {
            final Structure struct = Structures.getStructure(this.structure);
            return struct.getKingdomName();
        }
        catch (NoSuchStructureException e) {
            return "";
        }
    }
    
    @Override
    public boolean canAllowEveryone() {
        return false;
    }
    
    @Override
    public String getRolePermissionName() {
        return "";
    }
    
    @Override
    public boolean setNewOwner(final long playerId) {
        return false;
    }
    
    @Override
    public String getOwnerName() {
        return "";
    }
    
    public boolean isNotLockable() {
        return this.wall.isNotLockable();
    }
    
    public boolean isNotLockpickable() {
        return this.wall.isNotLockpickable();
    }
    
    public byte getLayer() {
        return this.wall.getLayer();
    }
    
    static {
        Door.logger = Logger.getLogger(Door.class.getName());
    }
}

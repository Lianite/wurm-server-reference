// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.math.Vector3f;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.sounds.Sound;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;
import java.util.Optional;
import java.util.ListIterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.players.MovementEntity;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.bodys.BodyTemplate;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.Features;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.Servers;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.Player;
import java.util.HashSet;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Server;
import com.wurmonline.server.kingdom.GuardTower;
import java.util.Iterator;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.creatures.Npc;
import com.wurmonline.server.Message;
import com.wurmonline.server.villages.Village;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.CreatureMove;
import java.util.Map;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Door;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.items.Item;
import java.util.Set;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.shared.constants.AttitudeConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;

public final class VirtualZone implements MiscConstants, CounterTypes, AttitudeConstants, ProtoConstants, TimeConstants
{
    private final Creature watcher;
    private Zone[] watchedZones;
    private Set<Item> items;
    private Set<Effect> effects;
    private Set<AreaSpellEffect> areaEffects;
    private Set<Long> finalizedBuildings;
    private Set<Door> doors;
    private Set<Fence> fences;
    private Set<MineDoorPermission> mineDoors;
    private int centerx;
    private int centery;
    private int startX;
    private int endX;
    private int startY;
    private int endY;
    private Set<Structure> structures;
    private final Map<Long, CreatureMove> creatures;
    private static final Logger logger;
    private int size;
    private final int id;
    private static int ids;
    private static final int worldTileSizeX;
    private static final int worldTileSizeY;
    private final boolean isOnSurface;
    private static final Long[] emptyLongArray;
    private float MOVELIMIT;
    private ArrayList<Structure> nearbyStructureList;
    private static final Set<VirtualZone> allZones;
    private static final int surfaceToSurfaceLocalDistance = 80;
    private static final int caveToSurfaceLocalDistance;
    private static final int surfaceToCaveLocalDistance = 20;
    private static final int caveToCaveLocalDistance = 20;
    public static final int ITEM_INSIDE_RENDERDIST = 15;
    public static final int HOUSEITEMS_RENDERDIST = 5;
    boolean hasReceivedLocalMessageOnChaos;
    
    public VirtualZone(final Creature aWatcher, final int aStartX, final int aStartY, final int centerX, final int centerY, final int aSz, final boolean aIsOnSurface) {
        this.startX = 0;
        this.endX = 0;
        this.startY = 0;
        this.endY = 0;
        this.creatures = new HashMap<Long, CreatureMove>();
        this.MOVELIMIT = 0.05f;
        this.nearbyStructureList = new ArrayList<Structure>();
        this.hasReceivedLocalMessageOnChaos = false;
        this.isOnSurface = aIsOnSurface;
        this.startX = Math.max(0, aStartX);
        this.startY = Math.max(0, aStartY);
        this.centerx = Math.max(0, centerX);
        this.centery = Math.max(0, centerY);
        this.centerx = Math.min(VirtualZone.worldTileSizeX - 1, centerX);
        this.centery = Math.min(VirtualZone.worldTileSizeY - 1, centerY);
        this.endX = Math.min(VirtualZone.worldTileSizeX - 1, this.centerx + aSz);
        this.endY = Math.min(VirtualZone.worldTileSizeY - 1, this.centery + aSz);
        this.id = VirtualZone.ids++;
        this.size = aSz;
        this.watcher = aWatcher;
        VirtualZone.allZones.add(this);
    }
    
    public boolean covers(final int x, final int y) {
        return x >= this.startX && x <= this.endX && y >= this.startY && y <= this.endY;
    }
    
    public Creature getWatcher() {
        return this.watcher;
    }
    
    public int getId() {
        return this.id;
    }
    
    public boolean isOnSurface() {
        return this.isOnSurface;
    }
    
    public int getStartX() {
        return this.startX;
    }
    
    public int getStartY() {
        return this.startY;
    }
    
    public int getEndX() {
        return this.endX;
    }
    
    public int getEndY() {
        return this.endY;
    }
    
    public void initialize() {
        if (!this.watcher.isDead()) {
            this.watchedZones = Zones.getZonesCoveredBy(this);
            for (int i = 0; i < this.watchedZones.length; ++i) {
                try {
                    this.watchedZones[i].addWatcher(this.id);
                }
                catch (NoSuchZoneException nze) {
                    VirtualZone.logger.log(Level.INFO, nze.getMessage(), nze);
                }
            }
        }
    }
    
    void addVillage(final Village newVillage) {
    }
    
    void broadCastMessage(final Message message) {
        if (!this.watcher.isIgnored(message.getSender().getWurmId())) {
            if (this.watcher.isNpc() && message.getSender().getWurmId() != this.watcher.getWurmId()) {
                ((Npc)this.watcher).getChatManager().addLocalChat(message);
            }
            if (!this.watcher.getCommunicator().isInvulnerable()) {
                this.watcher.getCommunicator().sendMessage(message);
            }
        }
    }
    
    public void callGuards() {
        boolean found = false;
        if (this.items != null) {
            for (final Item i : this.items) {
                if (i.isKingdomMarker() && i.getKingdom() == this.watcher.getKingdomId()) {
                    final GuardTower tower = Kingdoms.getTower(i);
                    if (tower == null || !tower.alertGuards(this.watcher)) {
                        continue;
                    }
                    this.watcher.getCommunicator().sendSafeServerMessage("Guards from " + i.getName() + " runs to the rescue!");
                    found = true;
                }
            }
        }
        if (!found) {
            this.watcher.getCommunicator().sendSafeServerMessage("No guards seem to respond to your call.");
        }
    }
    
    public int getCenterX() {
        return this.centerx;
    }
    
    public int getCenterY() {
        return this.centery;
    }
    
    int getSize() {
        return this.size;
    }
    
    public boolean shouldSeeCaves() {
        if (this.watcher.isPlayer()) {
            if (!this.watcher.isOnSurface()) {
                return true;
            }
            for (int x = this.startX + 10; x <= this.endX - 10; ++x) {
                for (int y = this.startY + 10; y <= this.endY - 10; ++y) {
                    if (Tiles.decodeType(Server.caveMesh.data[x | y << Constants.meshSize]) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void move(final int xChange, final int yChange) {
        this.centerx = Math.max(0, this.centerx + xChange);
        this.centery = Math.max(0, this.centery + yChange);
        this.centerx = Math.min(VirtualZone.worldTileSizeX - 1, this.centerx);
        this.centery = Math.min(VirtualZone.worldTileSizeY - 1, this.centery);
        this.startX = Math.max(0, this.centerx - this.size);
        this.startY = Math.max(0, this.centery - this.size);
        this.endX = Math.min(VirtualZone.worldTileSizeX - 1, this.centerx + this.size);
        this.endY = Math.min(VirtualZone.worldTileSizeY - 1, this.centery + this.size);
    }
    
    private final int getSizeX() {
        return this.endX - this.startX;
    }
    
    private final int getSizeY() {
        return this.endY - this.startY;
    }
    
    public void stopWatching() {
        final Zone[] checkedZones = Zones.getZonesCoveredBy(Math.max(0, this.startX - 100), Math.max(0, this.startY - 100), Math.min(Zones.worldTileSizeX - 1, this.endX + 100), Math.min(Zones.worldTileSizeY - 1, this.endY + 100), this.isOnSurface);
        for (int x = 0; x < checkedZones.length; ++x) {
            try {
                checkedZones[x].removeWatcher(this);
            }
            catch (NoSuchZoneException sex) {
                VirtualZone.logger.log(Level.WARNING, sex.getMessage(), sex);
            }
        }
        this.watchedZones = null;
        this.pruneDestroy();
        this.size = 0;
        VirtualZone.allZones.remove(this);
        if (Server.rand.nextInt(1000) == 0) {
            final int cs = Creatures.getInstance().getNumberOfCreatures();
            final int ps = Players.getInstance().getNumberOfPlayers();
            if (VirtualZone.allZones.size() > ps * 2 + cs * 2 + 100) {
                VirtualZone.logger.log(Level.INFO, "Number of virtual zones now: " + VirtualZone.allZones.size() + ". Creatures*2=" + cs * 2 + ", players*2=" + ps * 2);
            }
        }
    }
    
    public Long[] getCreatures() {
        if (this.creatures != null) {
            return this.creatures.keySet().toArray(new Long[this.creatures.size()]);
        }
        return VirtualZone.emptyLongArray;
    }
    
    public boolean containsCreature(final Creature creature) {
        return this.creatures != null && this.creatures.keySet().contains(creature.getWurmId());
    }
    
    public void refreshAttitudes() {
        for (final Long l : this.creatures.keySet()) {
            try {
                final Creature cret = Creatures.getInstance().getCreature(l);
                this.sendAttitude(cret);
            }
            catch (NoSuchCreatureException ex) {}
        }
    }
    
    private void pruneDestroy() {
        this.removeAllStructures();
        this.finalizedBuildings = null;
        if (this.doors != null) {
            for (final Door door : this.doors) {
                door.removeWatcher(this);
            }
            this.doors = null;
        }
        if (this.creatures != null) {
            for (final Long l : this.creatures.keySet()) {
                this.watcher.getCommunicator().sendDeleteCreature(l);
            }
            this.creatures.clear();
        }
        if (this.fences != null) {
            for (final Fence fence : this.fences) {
                this.watcher.getCommunicator().sendRemoveFence(fence);
            }
            this.fences = null;
        }
        if (this.items != null) {
            for (final Item item : this.items) {
                if (item.isMovingItem()) {
                    this.watcher.getCommunicator().sendDeleteMovingItem(item.getWurmId());
                }
                else {
                    this.watcher.getCommunicator().sendRemoveItem(item);
                }
            }
            this.items = null;
        }
        if (this.effects != null) {
            for (final Effect effect : this.effects) {
                this.watcher.getCommunicator().sendRemoveEffect(effect.getOwner());
            }
            this.effects = null;
        }
    }
    
    void addFence(final Fence fence) {
        if (this.fences == null) {
            this.fences = new HashSet<Fence>();
        }
        if (!this.fences.contains(fence) && this.covers(fence.getTileX(), fence.getTileY())) {
            this.fences.add(fence);
            this.watcher.getCommunicator().sendAddFence(fence);
            if (fence.getDamage() >= 60.0f) {
                this.watcher.getCommunicator().sendDamageState(fence.getId(), (byte)fence.getDamage());
            }
        }
    }
    
    void removeFence(final Fence fence) {
        if (this.fences != null) {
            if (this.fences.contains(fence) && this.watcher != null) {
                this.watcher.getCommunicator().sendRemoveFence(fence);
            }
            this.fences.remove(fence);
        }
    }
    
    public void addMineDoor(final MineDoorPermission door) {
        if (this.mineDoors == null) {
            this.mineDoors = new HashSet<MineDoorPermission>();
        }
        if (!this.mineDoors.contains(door)) {
            this.mineDoors.add(door);
            this.watcher.getCommunicator().sendAddMineDoor(door);
        }
    }
    
    public void removeMineDoor(final MineDoorPermission door) {
        if (this.mineDoors != null && this.mineDoors.contains(door)) {
            if (this.watcher != null) {
                this.watcher.getCommunicator().sendRemoveMineDoor(door);
            }
            this.mineDoors.remove(door);
        }
    }
    
    void renameItem(final Item item, final String newName, final String newModelName) {
        if (this.items != null && this.items.contains(item) && this.watcher != null) {
            this.watcher.getCommunicator().sendRename(item, newName, newModelName);
        }
    }
    
    void sendAttitude(final Creature creature) {
        if (this.creatures != null && this.creatures.keySet().contains(new Long(creature.getWurmId())) && this.watcher instanceof Player) {
            this.watcher.getCommunicator().changeAttitude(creature.getWurmId(), creature.getAttitude(this.watcher));
        }
    }
    
    void sendUpdateHasTarget(final Creature creature) {
        if (this.creatures != null && this.creatures.keySet().contains(new Long(creature.getWurmId())) && this.watcher instanceof Player) {
            if (creature.getTarget() != null) {
                this.watcher.getCommunicator().sendHasTarget(creature.getWurmId(), true);
            }
            else {
                this.watcher.getCommunicator().sendHasTarget(creature.getWurmId(), false);
            }
        }
    }
    
    private byte getLayer() {
        if (this.isOnSurface()) {
            return 0;
        }
        return -1;
    }
    
    boolean addCreature(final long creatureId, final boolean overRideRange) throws NoSuchCreatureException, NoSuchPlayerException {
        return this.addCreature(creatureId, overRideRange, -10L, 0.0f, 0.0f, 0.0f);
    }
    
    public final boolean addCreature(final long creatureId, final boolean overRideRange, final long copyId, final float offx, final float offy, final float offz) throws NoSuchCreatureException, NoSuchPlayerException {
        final Creature creature = Server.getInstance().getCreature(creatureId);
        if (this.coversCreature(creature) || (this.watcher != null && this.watcher.isPlayer() && overRideRange)) {
            if (this.watcher != null && this.watcher.getWurmId() != creatureId && creature.isVisibleTo(this.watcher) && (!this.watcher.isPlayer() || this.watcher.hasLink())) {
                if (this.creatures.keySet().contains(creatureId) && copyId == -10L) {
                    return false;
                }
                if (!this.creatures.keySet().contains(creatureId)) {
                    if (this.watcher.isPlayer()) {
                        this.creatures.put(creatureId, new CreatureMove());
                        creature.setVisibleToPlayers(true);
                    }
                    else {
                        this.creatures.put(creatureId, null);
                    }
                }
                if (this.watcher.hasLink()) {
                    String suff = "";
                    String pre = "";
                    if (!this.watcher.hasFlag(56)) {
                        if (!creature.hasFlag(24)) {
                            pre = creature.getAbilityTitle();
                        }
                        if (creature.getCultist() != null && !creature.hasFlag(25)) {
                            suff = suff + " " + creature.getCultist().getCultistTitleShort();
                        }
                    }
                    boolean enemy = false;
                    if (creature.getPower() > 0 && !Servers.localServer.testServer) {
                        if (creature.getPower() == 1) {
                            suff = " (HERO)";
                        }
                        else if (creature.getPower() == 2) {
                            suff = " (GM)";
                        }
                        else if (creature.getPower() == 3) {
                            suff = " (GOD)";
                        }
                        else if (creature.getPower() == 4) {
                            suff = " (ARCH)";
                        }
                        else if (creature.getPower() == 5) {
                            suff = " (ADMIN)";
                        }
                    }
                    else {
                        if (creature.isKing()) {
                            suff = suff + " [" + King.getRulerTitle(creature.getSex() == 0, creature.getKingdomId()) + "]";
                        }
                        if (this.watcher.getKingdomId() != 0 && creature.getKingdomId() != 0 && !creature.isFriendlyKingdom(this.watcher.getKingdomId())) {
                            if (creature.getPower() < 2 && this.watcher.getPower() < 2 && creature.isPlayer()) {
                                if (this.watcher.getCultist() != null && this.watcher.getCultist().getLevel() > 8 && this.watcher.getCultist().getPath() == 3) {
                                    suff += " (ENEMY)";
                                }
                                else {
                                    suff = " (ENEMY)";
                                }
                                enemy = true;
                            }
                        }
                        else if (creature.getKingdomTemplateId() != 3 && creature.getReputation() < 0) {
                            suff += " (OUTLAW)";
                            enemy = true;
                        }
                        else if (this.watcher.getCitizenVillage() != null && creature.isPlayer() && this.watcher.getCitizenVillage().isEnemy(creature)) {
                            suff = " (ENEMY)";
                            enemy = true;
                        }
                        else if (creature.hasAttackedUnmotivated()) {
                            suff = " (HUNTED)";
                        }
                        else if ((!this.watcher.isPlayer() || !this.watcher.hasFlag(56)) && (creature.getTitle() != null || (Features.Feature.COMPOUND_TITLES.isEnabled() && creature.getSecondTitle() != null)) && !creature.getTitleString().isEmpty()) {
                            suff += " [";
                            suff += creature.getTitleString();
                            suff += "]";
                        }
                        if (creature.isChampion() && creature.getDeity() != null) {
                            suff = suff + " [Champion of " + creature.getDeity().name + "]";
                        }
                    }
                    if (enemy && creature.getPower() < 2 && this.watcher.getPower() < 2 && creature.isPlayer() && (creature.getFightingSkill().getRealKnowledge() > 20.0 || creature.getFaith() > 25.0f) && Servers.isThisAPvpServer()) {
                        this.watcher.addEnemyPresense();
                    }
                    byte layer = (byte)creature.getLayer();
                    if (overRideRange) {
                        layer = this.getLayer();
                    }
                    final String hoverText = creature.getHoverText(this.watcher);
                    this.watcher.getCommunicator().sendNewCreature((copyId != -10L) ? copyId : creatureId, pre + StringUtilities.raiseFirstLetterOnly(creature.getName()) + suff, hoverText, creature.isUndead() ? creature.getUndeadModelName() : creature.getModelName(), creature.getStatus().getPositionX() + offx, creature.getStatus().getPositionY() + offy, creature.getStatus().getPositionZ() + offz, creature.getStatus().getBridgeId(), creature.getStatus().getRotation(), layer, creature.getBridgeId() <= 0L && !creature.isSubmerged() && (creature.getPower() == 0 || creature.getMovementScheme().onGround) && creature.getFloorLevel() <= 0 && creature.getMovementScheme().getGroundOffset() <= 0.0f, false, creature.getTemplate().getTemplateId() != 119, creature.getKingdomId(), creature.getFace(), creature.getBlood(), creature.isUndead(), copyId != -10L || creature.isNpc(), creature.getStatus().getModType());
                    if (creature.getRarityShader() != 0) {
                        this.setNewRarityShader(creature);
                    }
                    if (copyId != -10L) {
                        this.watcher.getCommunicator().setCreatureDamage(copyId, creature.getStatus().calcDamPercent());
                        if (creature.getRarityShader() != 0) {
                            this.watcher.getCommunicator().updateCreatureRarity(copyId, creature.getRarityShader());
                        }
                    }
                    for (final Item item : creature.getBody().getContainersAndWornItems()) {
                        if (item != null) {
                            try {
                                final byte armorSlot = item.isArmour() ? BodyTemplate.convertToArmorEquipementSlot((byte)item.getParent().getPlace()) : BodyTemplate.convertToItemEquipementSlot((byte)item.getParent().getPlace());
                                if (creature.isAnimal() && creature.isVehicle()) {
                                    this.watcher.getCommunicator().sendHorseWear(creature.getWurmId(), item.getTemplateId(), item.getMaterial(), armorSlot, item.getAuxData());
                                }
                                else {
                                    this.watcher.getCommunicator().sendWearItem((copyId != -10L) ? copyId : creature.getWurmId(), item.getTemplateId(), armorSlot, WurmColor.getColorRed(item.getColor()), WurmColor.getColorGreen(item.getColor()), WurmColor.getColorBlue(item.getColor()), WurmColor.getColorRed(item.getColor2()), WurmColor.getColorGreen(item.getColor2()), WurmColor.getColorBlue(item.getColor2()), item.getMaterial(), item.getRarity());
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }
                    if (creature.hasCustomColor()) {
                        this.sendRepaint((copyId != -10L) ? copyId : creatureId, creature.getColorRed(), creature.getColorGreen(), creature.getColorBlue(), (byte)(-1), (byte)creature.getPaintMode());
                    }
                    if (creature.hasCustomSize() || creature.isFish()) {
                        this.sendResizeCreature((copyId != -10L) ? copyId : creatureId, creature.getSizeModX(), creature.getSizeModY(), creature.getSizeModZ());
                    }
                    if (creature.getBestLightsource() != null) {
                        this.addLightSource(creature, creature.getBestLightsource());
                    }
                    else if (creature.isPlayer()) {
                        ((Player)creature).sendLantern(this);
                    }
                    if (this.watcher.isPlayer()) {
                        this.sendCreatureDamage(creature, creature.getStatus().calcDamPercent());
                    }
                    if (creature.isOnFire()) {
                        this.sendAttachCreatureEffect(creature, (byte)1, creature.getFireRadius(), (byte)(-1), (byte)(-1), (byte)1);
                    }
                    if (creature.isGhost()) {
                        this.watcher.getCommunicator().sendAttachEffect(creature.getWurmId(), (byte)2, (byte)(creature.isSpiritGuard() ? -56 : 100), (byte)(creature.isSpiritGuard() ? 1 : 1), (byte)0, (byte)1);
                        this.watcher.getCommunicator().sendAttachEffect(creature.getWurmId(), (byte)3, (byte)50, (byte)(creature.isSpiritGuard() ? 50 : 50), (byte)50, (byte)1);
                    }
                    else if (creature.hasGlow()) {
                        if (creature.hasCustomColor()) {
                            this.watcher.getCommunicator().sendAttachEffect(creature.getWurmId(), (byte)3, (byte)1, (byte)1, (byte)1, (byte)1);
                        }
                        else {
                            this.watcher.getCommunicator().sendAttachEffect(creature.getWurmId(), (byte)3, creature.getColorRed(), creature.getColorGreen(), creature.getColorGreen(), (byte)1);
                        }
                    }
                    this.sendCreatureItems(creature);
                    if ((creature.isPlayer() || creature.isNpc()) && (!Servers.localServer.PVPSERVER || this.watcher.isPaying())) {
                        this.watcher.getCommunicator().sendAddLocal(creature.getName(), creatureId);
                    }
                }
                if (this.watcher.isTypeFleeing() && (creature.isPlayer() || creature.isAggHuman() || creature.isHuman() || creature.isCarnivore() || creature.isMonster())) {
                    final float newDistance = creature.getPos2f().distance(this.watcher.getPos2f());
                    if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                        final int baseCounter = (int)(Math.max(1.0f, creature.getBaseCombatRating() - this.watcher.getBaseCombatRating()) * 5.0f);
                        if (baseCounter - newDistance > 0.0f) {
                            this.watcher.setFleeCounter((int)Math.min(60.0f, Math.max(3.0f, baseCounter - newDistance)));
                        }
                    }
                    else {
                        this.watcher.setFleeCounter(60);
                    }
                }
                this.checkIfAttack(creature, creatureId);
                final byte att = creature.getAttitude(this.watcher);
                if (att != 0) {
                    this.watcher.getCommunicator().changeAttitude((copyId != -10L) ? copyId : creatureId, att);
                }
                if (creature.getVehicle() != -10L) {
                    final Vehicle vehic = Vehicles.getVehicleForId(creature.getVehicle());
                    if (vehic != null) {
                        final Seat s = vehic.getSeatFor(creature.getWurmId());
                        if (s != null) {
                            this.sendAttachCreature(creatureId, creature.getVehicle(), s.offx, s.offy, s.offz, vehic.getSeatNumberFor(s));
                        }
                    }
                }
                if (creature.getHitched() != null) {
                    final Seat s2 = creature.getHitched().getHitchSeatFor(creature.getWurmId());
                    if (s2 != null) {
                        this.sendAttachCreature(creatureId, creature.getHitched().wurmid, s2.offx, s2.offy, s2.offz, 0);
                    }
                }
                if (creature.getTarget() != null) {
                    this.watcher.getCommunicator().sendHasTarget(creature.getWurmId(), true);
                }
                if (creature.isRidden()) {
                    final Vehicle vehic = Vehicles.getVehicleForId(creatureId);
                    if (vehic != null) {
                        final Seat[] seats = vehic.getSeats();
                        for (int x = 0; x < seats.length; ++x) {
                            if (seats[x].isOccupied()) {
                                if (!this.creatures.containsKey(seats[x].occupant)) {
                                    try {
                                        this.addCreature(seats[x].occupant, true);
                                    }
                                    catch (NoSuchCreatureException nsc) {
                                        VirtualZone.logger.log(Level.INFO, nsc.getMessage(), nsc);
                                    }
                                    catch (NoSuchPlayerException nsp) {
                                        VirtualZone.logger.log(Level.INFO, nsp.getMessage(), nsp);
                                    }
                                }
                                this.sendAttachCreature(seats[x].occupant, creatureId, seats[x].offx, seats[x].offy, seats[x].offz, x);
                            }
                        }
                    }
                }
                return true;
            }
        }
        else {
            this.removeCreature(creature);
        }
        return false;
    }
    
    public void sendCreatureItems(final Creature creature) {
        if (creature.isPlayer()) {
            try {
                final Item lTempItem = creature.getEquippedWeapon((byte)37);
                if (lTempItem != null && !lTempItem.isBodyPartAttached()) {
                    this.sendWieldItem((creature.getWurmId() == this.watcher.getWurmId()) ? -1L : creature.getWurmId(), (byte)0, lTempItem.getModelName(), lTempItem.getRarity(), WurmColor.getColorRed(lTempItem.getColor()), WurmColor.getColorGreen(lTempItem.getColor()), WurmColor.getColorBlue(lTempItem.getColor()), WurmColor.getColorRed(lTempItem.getColor2()), WurmColor.getColorGreen(lTempItem.getColor2()), WurmColor.getColorBlue(lTempItem.getColor2()));
                }
            }
            catch (NoSpaceException nsp) {
                VirtualZone.logger.log(Level.WARNING, creature.getName() + " could not get equipped weapon for left hand due to " + nsp.getMessage(), nsp);
            }
            try {
                final Item lTempItem = creature.getEquippedWeapon((byte)38);
                if (lTempItem != null && !lTempItem.isBodyPartAttached()) {
                    this.sendWieldItem((creature.getWurmId() == this.watcher.getWurmId()) ? -1L : creature.getWurmId(), (byte)1, lTempItem.getModelName(), lTempItem.getRarity(), WurmColor.getColorRed(lTempItem.getColor()), WurmColor.getColorGreen(lTempItem.getColor()), WurmColor.getColorBlue(lTempItem.getColor()), WurmColor.getColorRed(lTempItem.getColor2()), WurmColor.getColorGreen(lTempItem.getColor2()), WurmColor.getColorBlue(lTempItem.getColor2()));
                }
            }
            catch (NoSpaceException nsp) {
                VirtualZone.logger.log(Level.WARNING, creature.getName() + " could not get equipped weapon for right hand due to " + nsp.getMessage(), nsp);
            }
        }
    }
    
    void newLayer(final Creature creature, final boolean tileIsSurfaced) {
        if (creature != null && this.watcher.getWurmId() != creature.getWurmId()) {
            if (this.creatures.containsKey(creature.getWurmId())) {
                if (this.watcher.hasLink()) {
                    this.watcher.getCommunicator().sendCreatureChangedLayer(creature.getWurmId(), (byte)creature.getLayer());
                }
                if (this.isOnSurface()) {
                    if (this.watcher.getVisionArea().getUnderGround() != null && this.watcher.getVisionArea().getUnderGround().coversCreature(creature)) {
                        final CreatureMove cm = this.creatures.remove(creature.getWurmId());
                        this.addToVisionArea(creature, cm, this.watcher.getVisionArea().getUnderGround());
                    }
                    else {
                        try {
                            this.deleteCreature(creature, true);
                        }
                        catch (NoSuchCreatureException ex) {}
                        catch (NoSuchPlayerException ex2) {}
                    }
                }
                else {
                    final CreatureMove cm = this.creatures.remove(creature.getWurmId());
                    this.addToVisionArea(creature, cm, this.watcher.getVisionArea().getSurface());
                }
            }
        }
        else if (creature != null && this.watcher.getWurmId() == creature.getWurmId() && this.watcher.getVehicle() != -10L && !this.watcher.isVehicleCommander()) {
            this.watcher.getCommunicator().sendCreatureChangedLayer(-1L, (byte)creature.getLayer());
        }
    }
    
    public void justSendNewLayer(final Item item) {
        if (this.watcher.getVehicle() != item.getWurmId()) {
            this.watcher.getCommunicator().sendCreatureChangedLayer(item.getWurmId(), item.newLayer);
        }
    }
    
    public void addToVisionArea(final Creature creature, CreatureMove cm, final VirtualZone newzone) {
        newzone.addCreatureToMap(creature, cm);
        if (creature.isRidden()) {
            final Set<Long> riders = creature.getRiders();
            for (final Long rider : riders) {
                cm = this.creatures.remove((long)rider);
                try {
                    newzone.addCreature(rider, true);
                }
                catch (Exception nex) {
                    VirtualZone.logger.log(Level.WARNING, nex.getMessage(), nex);
                }
            }
        }
    }
    
    void newLayer(final Item vehicle) {
        if (vehicle != null && this.items != null && this.items.contains(vehicle)) {
            byte newlayer = (byte)(vehicle.isOnSurface() ? 0 : -1);
            if (vehicle.newLayer != -128) {
                newlayer = vehicle.newLayer;
            }
            if (this.watcher.hasLink()) {
                this.watcher.getCommunicator().sendCreatureChangedLayer(vehicle.getWurmId(), newlayer);
            }
            if (newlayer < 0) {
                if (this.watcher.getVisionArea().getUnderGround() != null && this.watcher.getVisionArea().getUnderGround().covers(vehicle.getTileX(), vehicle.getTileY())) {
                    this.watcher.getVisionArea().getUnderGround().addItem(vehicle, null, true);
                }
                else {
                    this.removeItem(vehicle);
                }
            }
            else {
                this.watcher.getVisionArea().getSurface().addItem(vehicle, null, true);
            }
            this.items.remove(vehicle);
        }
    }
    
    public void addCreatureToMap(final Creature creature, final CreatureMove cm) {
        if (cm != null) {
            this.creatures.put(creature.getWurmId(), cm);
        }
    }
    
    public void checkForEnemies() {
        for (final Long cid : this.creatures.keySet()) {
            if (this.watcher.target != -10L) {
                return;
            }
            try {
                final Creature creature = Server.getInstance().getCreature(cid);
                this.checkIfAttack(creature, cid);
            }
            catch (NoSuchCreatureException ex) {}
            catch (NoSuchPlayerException ex2) {}
        }
    }
    
    private void checkIfAttack(final Creature creature, final long creatureId) {
        if (this.watcher.getTemplate().getCreatureAI() != null && this.watcher.getTemplate().getCreatureAI().maybeAttackCreature(this.watcher, this, creature)) {
            return;
        }
        if (creature.isTransferring()) {
            return;
        }
        if (this.watcher.isPlayer()) {
            if (creature.addingAfterTeleport && this.watcher.lastOpponent == creature && creature.isWithinDistanceTo(this.watcher.getPosX(), this.watcher.getPosY(), this.watcher.getPositionZ() + this.watcher.getAltOffZ(), 12.0f)) {
                this.watcher.setTarget(creatureId, false);
            }
            return;
        }
        if (creature.isNpc() && this.watcher.isNpc() && creature.getAttitude(this.watcher) != 2) {
            return;
        }
        if (creature.getLayer() == this.watcher.getLayer() || this.watcher.isKingdomGuard() || this.watcher.isUnique() || this.watcher.isWarGuard()) {
            if (creature.fleeCounter > 0) {
                return;
            }
            if (creature.getVehicle() > -10L && this.watcher.isNoAttackVehicles()) {
                return;
            }
            if (creature.getCultist() != null && (creature.getCultist().hasFearEffect() || creature.getCultist().hasLoveEffect())) {
                return;
            }
            if (!creature.isWithinDistanceTo(this.watcher.getPosX(), this.watcher.getPosY(), this.watcher.getPositionZ() + this.watcher.getAltOffZ(), (this.watcher.isSpiritGuard() || this.watcher.isKingdomGuard() || this.watcher.isWarGuard() || this.watcher.isUnique()) ? 30.0f : 12.0f) && !isCreatureTurnedTowardsTarget(creature, this.watcher) && !this.watcher.isKingdomGuard() && !this.watcher.isWarGuard()) {
                return;
            }
            if (creature.isBridgeBlockingAttack(this.watcher, true)) {
                return;
            }
            if (this.watcher.getAttitude(creature) == 2) {
                if (this.watcher.target == -10L) {
                    if (this.watcher.isKingdomGuard()) {
                        if (creature.getCurrentTile().getKingdom() == this.watcher.getKingdomId() || this.watcher.getKingdomId() == 0) {
                            final GuardTower gt = Kingdoms.getTower(this.watcher);
                            if (gt != null) {
                                final int tpx = gt.getTower().getTileX();
                                final int tpy = gt.getTower().getTileY();
                                if (creature.isWithinTileDistanceTo(tpx, tpy, (int)gt.getTower().getPosZ(), 50)) {
                                    if (creature.isRidden()) {
                                        if (Server.rand.nextInt(50) == 0) {
                                            this.watcher.setTarget(creatureId, false);
                                        }
                                    }
                                    else if (creature.isPlayer() || creature.isDominated()) {
                                        this.watcher.setTarget(creatureId, false);
                                    }
                                    else if (this.watcher.getAlertSeconds() > 0 && creature.isAggHuman()) {
                                        this.watcher.setTarget(creatureId, false);
                                        if (this.watcher.target == creatureId) {
                                            GuardTower.yellHunt(this.watcher, creature, false);
                                        }
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if (this.watcher.isWarGuard()) {
                        final Item target = Kingdoms.getClosestWarTarget(this.watcher.getTileX(), this.watcher.getTileY(), this.watcher);
                        if (target != null && this.watcher.isWithinTileDistanceTo(target.getTileX(), target.getTileY(), 0, 15) && creature.isWithinTileDistanceTo(target.getTileX(), target.getTileY(), 0, 5)) {
                            this.watcher.setTarget(creatureId, false);
                            return;
                        }
                    }
                    else if (this.watcher.isDominated()) {
                        this.watcher.setTarget(creatureId, false);
                    }
                    if (!this.watcher.isSpiritGuard()) {
                        if (creature.isRidden() && Server.rand.nextInt(10) == 0) {
                            this.watcher.setTarget(creatureId, false);
                        }
                        else if (creature.isDominated() && Server.rand.nextInt(10) == 0) {
                            this.watcher.setTarget(creatureId, false);
                        }
                    }
                    if (creature instanceof Player && this.watcher.isAggHuman()) {
                        if (!creature.hasLink()) {
                            return;
                        }
                        if (creature.getSpellEffects() != null && creature.getSpellEffects().getSpellEffect((byte)73) != null && !creature.isWithinDistanceTo(this.watcher, 7.0f)) {
                            return;
                        }
                        if (creature.addingAfterTeleport || Server.rand.nextInt(100) <= this.watcher.getAggressivity() * this.watcher.getStatus().getAggTypeModifier()) {
                            this.watcher.setTarget(creatureId, false);
                        }
                    }
                    else if (this.watcher.isAggHuman() && creature.isKingdomGuard()) {
                        if (creature.addingAfterTeleport || (creature.getAlertSeconds() > 0 && Server.rand.nextInt((int)Math.max(1.0f, this.watcher.getAggressivity() * this.watcher.getStatus().getAggTypeModifier())) == 0)) {
                            this.watcher.setTarget(creatureId, false);
                        }
                    }
                    else if (this.watcher.isAggHuman() && creature.isSpiritGuard()) {
                        if (creature.getCitizenVillage() != null) {
                            if (!creature.getCitizenVillage().isEnemy(this.watcher)) {
                                return;
                            }
                            if (creature.addingAfterTeleport || Server.rand.nextInt((int)Math.max(1.0f, this.watcher.getAggressivity() * this.watcher.getStatus().getAggTypeModifier())) == 0) {
                                this.watcher.setTarget(creatureId, false);
                            }
                        }
                    }
                    else if (this.watcher.isSpiritGuard()) {
                        if (this.watcher.getCitizenVillage() == null) {
                            if (creature.addingAfterTeleport || Server.rand.nextInt(100) <= 80) {
                                this.watcher.setTarget(creatureId, false);
                            }
                        }
                        else if (creature.isRidden()) {
                            if (!this.watcher.getCitizenVillage().isEnemy(creature)) {
                                return;
                            }
                            if (Server.rand.nextInt(100) == 0) {
                                this.watcher.setTarget(creatureId, false);
                            }
                        }
                        else if ((creature.isPlayer() || creature.isBreakFence() || creature.isDominated()) && this.watcher.getCitizenVillage().isWithinAttackPerimeter(creature.getTileX(), creature.getTileY())) {
                            if (!this.watcher.getCitizenVillage().isEnemy(creature)) {
                                return;
                            }
                            this.watcher.setTarget(creatureId, false);
                        }
                    }
                    else {
                        this.watcher.setTarget(creatureId, false);
                    }
                }
            }
            else if (this.watcher.getTemplate().getLeaderTemplateId() > 0 && this.watcher.leader == null && !this.watcher.isDominated() && creature.getTemplate().getTemplateId() == this.watcher.getTemplate().getLeaderTemplateId() && (!this.watcher.isHerbivore() || !this.watcher.isHungry()) && (creature.getPositionZ() >= -0.71 || (creature.isSwimming() && this.watcher.isSwimming())) && creature.mayLeadMoreCreatures()) {
                creature.addFollower(this.watcher, null);
                this.watcher.setLeader(creature);
            }
        }
    }
    
    void addAreaSpellEffect(final AreaSpellEffect effect, final boolean loop) {
        if (effect != null) {
            if (this.areaEffects == null) {
                this.areaEffects = new HashSet<AreaSpellEffect>();
            }
            if (!this.areaEffects.contains(effect)) {
                this.areaEffects.add(effect);
                if (this.watcher.hasLink()) {
                    this.watcher.getCommunicator().sendAddAreaSpellEffect(effect.getTilex(), effect.getTiley(), effect.getLayer(), effect.getType(), effect.getFloorLevel(), effect.getHeightOffset(), loop);
                }
            }
        }
    }
    
    void removeAreaSpellEffect(final AreaSpellEffect effect) {
        if (this.areaEffects != null && this.areaEffects.contains(effect)) {
            this.areaEffects.remove(effect);
            if (effect != null && this.watcher.hasLink()) {
                this.watcher.getCommunicator().sendRemoveAreaSpellEffect(effect.getTilex(), effect.getTiley(), effect.getLayer());
            }
        }
    }
    
    public void addEffect(final Effect effect, final boolean temp) {
        if (this.effects == null) {
            this.effects = new HashSet<Effect>();
        }
        if (!this.effects.contains(effect) || temp) {
            Label_0096: {
                if (!temp) {
                    if (WurmId.getType(effect.getOwner()) != 2) {
                        if (WurmId.getType(effect.getOwner()) != 6) {
                            break Label_0096;
                        }
                    }
                    try {
                        final Item effectHolder = Items.getItem(effect.getOwner());
                        if (this.items == null || !this.items.contains(effectHolder)) {
                            return;
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        return;
                    }
                }
            }
            if (!temp) {
                this.effects.add(effect);
            }
            if (this.watcher.hasLink()) {
                this.watcher.getCommunicator().sendAddEffect(effect.getOwner(), effect.getType(), effect.getPosX(), effect.getPosY(), effect.getPosZ(), effect.getLayer(), effect.getEffectString(), effect.getTimeout(), effect.getRotationOffset());
            }
        }
    }
    
    public void removeEffect(final Effect effect) {
        if (this.effects != null && this.effects.contains(effect)) {
            this.effects.remove(effect);
            if (this.watcher.hasLink()) {
                this.watcher.getCommunicator().sendRemoveEffect(effect.getOwner());
            }
        }
    }
    
    void removeCreature(final Creature creature) {
        if (!this.coversCreature(creature) || creature.isLoggedOut()) {
            if (!this.watcher.isSpiritGuard() && creature.getWurmId() == this.watcher.target) {
                this.watcher.setTarget(-10L, true);
            }
            if (this.creatures != null && this.creatures.keySet().contains(creature.getWurmId())) {
                this.creatures.remove(creature.getWurmId());
                this.checkIfEnemyIsPresent(false);
                if (creature.getCurrentTile() == null || !creature.getCurrentTile().isVisibleToPlayers()) {
                    creature.setVisibleToPlayers(false);
                }
                if (this.watcher.hasLink()) {
                    this.watcher.getCommunicator().sendDeleteCreature(creature.getWurmId());
                    if (creature instanceof Player || creature.isNpc()) {
                        this.watcher.getCommunicator().sendRemoveLocal(creature.getName());
                    }
                    if (this.watcher.isPlayer()) {
                        final Vehicle vehic = Vehicles.getVehicleForId(creature.getWurmId());
                        if (vehic != null) {
                            final Seat[] seats = vehic.getSeats();
                            for (int x = 0; x < seats.length; ++x) {
                                if (seats[x].isOccupied()) {
                                    try {
                                        final Creature occ = Server.getInstance().getCreature(seats[x].occupant);
                                        this.watcher.getCommunicator().sendRemoveLocal(occ.getName());
                                    }
                                    catch (NoSuchCreatureException nsc) {
                                        VirtualZone.logger.log(Level.WARNING, nsc.getMessage(), nsc);
                                    }
                                    catch (NoSuchPlayerException nsp) {
                                        VirtualZone.logger.log(Level.WARNING, nsp.getMessage(), nsp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean checkIfEnemyIsPresent(final boolean checkedFromOtherVirtualZone) {
        if (this.watcher.isPlayer() && !this.watcher.isTeleporting() && this.watcher.hasLink() && this.watcher.getVisionArea() != null && this.watcher.getVisionArea().isInitialized()) {
            boolean foundEnemy = false;
            if (this.creatures != null) {
                final Long[] crets = this.getCreatures();
                for (int x = 0; x < crets.length; ++x) {
                    if (WurmId.getType(crets[x]) == 0) {
                        try {
                            final Creature creature = Server.getInstance().getCreature(crets[x]);
                            if (this.watcher.getKingdomId() != 0 && creature.getKingdomId() != 0 && !this.watcher.isFriendlyKingdom(creature.getKingdomId())) {
                                if (creature.getPower() < 2 && this.watcher.getPower() < 2 && creature.getFightingSkill().getRealKnowledge() > 20.0) {
                                    foundEnemy = true;
                                }
                            }
                            else if (this.watcher.getCitizenVillage() != null && this.watcher.getCitizenVillage().isEnemy(creature) && creature.getPower() < 2 && this.watcher.getPower() < 2 && creature.getFightingSkill().getRealKnowledge() > 20.0) {
                                foundEnemy = true;
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            if (!foundEnemy) {
                if (!checkedFromOtherVirtualZone) {
                    boolean found = false;
                    if (this.watcher.getVisionArea() != null) {
                        if (this.isOnSurface) {
                            found = this.watcher.getVisionArea().getUnderGround().checkIfEnemyIsPresent(true);
                        }
                        else {
                            found = this.watcher.getVisionArea().getSurface().checkIfEnemyIsPresent(true);
                        }
                    }
                    if (!found) {
                        this.watcher.removeEnemyPresense();
                    }
                    return found;
                }
                return false;
            }
        }
        return true;
    }
    
    void makeInvisible(final Creature creature) {
        if (this.creatures != null && this.creatures.keySet().contains(new Long(creature.getWurmId()))) {
            this.creatures.remove(new Long(creature.getWurmId()));
            this.checkIfEnemyIsPresent(false);
            if (this.watcher.hasLink()) {
                this.watcher.getCommunicator().sendDeleteCreature(creature.getWurmId());
            }
            if (creature instanceof Player || creature.isNpc()) {
                this.watcher.getCommunicator().sendRemoveLocal(creature.getName());
            }
        }
    }
    
    private boolean coversCreature(final Creature creature) {
        if (creature.isDead()) {
            return false;
        }
        if (creature == this.watcher) {
            return true;
        }
        if (creature.isPlayer() && Servers.localServer.PVPSERVER) {
            return (this.watcher.isOnSurface() && creature.isOnSurface() && this.watcher.isWithinDistanceTo(creature.getTileX(), creature.getTileY(), 80)) || (!this.watcher.isOnSurface() && creature.isOnSurface() && this.watcher.isWithinDistanceTo(creature.getTileX(), creature.getTileY(), VirtualZone.caveToSurfaceLocalDistance)) || (this.watcher.isOnSurface() && !creature.isOnSurface() && this.watcher.isWithinDistanceTo(creature.getTileX(), creature.getTileY(), 20)) || (!this.watcher.isOnSurface() && !creature.isOnSurface() && this.watcher.isWithinDistanceTo(creature.getTileX(), creature.getTileY(), 20));
        }
        return this.covers(creature.getTileX(), creature.getTileY());
    }
    
    public void moveAllCreatures() {
        final Map.Entry<Long, CreatureMove>[] arr = this.creatures.entrySet().toArray(new Map.Entry[this.creatures.size()]);
        for (int x = 0; x < arr.length; ++x) {
            if (arr[x].getValue().timestamp != 0L) {
                try {
                    final Creature creature = Server.getInstance().getCreature(arr[x].getKey());
                    if (!isMovingSameLevel(creature) || Structure.isGroundFloorAtPosition(creature.getPosX(), creature.getPosY(), creature.isOnSurface())) {
                        this.watcher.getCommunicator().sendMoveCreatureAndSetZ(arr[x].getKey(), creature.getPosX(), creature.getPosY(), creature.getPositionZ(), arr[x].getValue().rotation);
                    }
                    else {
                        this.watcher.getCommunicator().sendMoveCreature(arr[x].getKey(), creature.getPosX(), creature.getPosY(), arr[x].getValue().rotation, creature.isMoving());
                    }
                    this.clearCreatureMove(creature, arr[x].getValue());
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
            }
        }
    }
    
    private static final boolean isMovingSameLevel(final Creature creature) {
        return creature.getBridgeId() <= 0L && ((creature.isPlayer() && creature.getMovementScheme().onGround) || !creature.isSubmerged()) && creature.getFloorLevel() <= 0 && creature.getMovementScheme().getGroundOffset() == 0.0f;
    }
    
    boolean creatureMoved(final long creatureId, final float diffX, final float diffY, final float diffZ, final int diffTileX, final int diffTileY) throws NoSuchCreatureException, NoSuchPlayerException {
        if (this.watcher == null || (this.watcher.isPlayer() && !this.watcher.hasLink())) {
            return true;
        }
        final Creature creature = Server.getInstance().getCreature(creatureId);
        if (this.watcher.equals(creature)) {
            if (this.watcher.getPower() > 2 && this.watcher.loggerCreature1 != -10L) {
                this.watcher.getCommunicator().sendAck(this.watcher.getPosX(), this.watcher.getPosY());
            }
            if (this.watcher.isPlayer() && (diffTileX != 0 || diffTileY != 0)) {
                this.getStructuresWithinDistance(5);
            }
            return false;
        }
        if (!this.coversCreature(creature)) {
            this.removeCreature(creature);
            return false;
        }
        if (!creature.isVisibleTo(this.watcher)) {
            return false;
        }
        if (!this.addCreature(creatureId, false)) {
            if (!this.watcher.isPlayer()) {
                this.watcher.creatureMoved(creature, diffX, diffY, diffZ);
            }
            else if (this.watcher.hasLink()) {
                if (creature.isPlayer()) {
                    final Set<MovementEntity> illusions = Creature.getIllusionsFor(creatureId);
                    if (illusions != null) {
                        for (final MovementEntity e : illusions) {
                            this.watcher.getCommunicator().sendMoveCreature(e.getWurmid(), creature.getPosX() + e.getMovePosition().diffX, creature.getPosY() + e.getMovePosition().diffY, (int)(creature.getStatus().getRotation() * 256.0f / 360.0f), true);
                            if (e.shouldExpire()) {
                                this.watcher.getCommunicator().sendDeleteCreature(e.getWurmid());
                            }
                        }
                    }
                }
                final CreatureMove cmove = this.creatures.get(new Long(creatureId));
                final boolean moveSameLevel = isMovingSameLevel(creature);
                if (diffX != 0.0f || diffY != 0.0f || diffZ != 0.0f) {
                    this.MOVELIMIT = Math.max(0.05f, Math.min(0.7f, Creature.rangeTo(creature, this.watcher) / 100.0f));
                    if (Math.abs(cmove.diffX + diffX) > this.MOVELIMIT || Math.abs(cmove.diffY + diffY) > this.MOVELIMIT || Math.abs(cmove.diffZ + diffZ) > this.MOVELIMIT) {
                        if (!moveSameLevel || Structure.isGroundFloorAtPosition(creature.getPosX(), creature.getPosY(), creature.isOnSurface())) {
                            this.watcher.getCommunicator().sendMoveCreatureAndSetZ(creatureId, creature.getPosX(), creature.getPosY(), creature.getPositionZ(), cmove.rotation);
                        }
                        else {
                            this.watcher.getCommunicator().sendMoveCreature(creatureId, creature.getPosX(), creature.getPosY(), cmove.rotation, creature.isMoving());
                        }
                        cmove.resetXYZ();
                        cmove.timestamp = System.currentTimeMillis();
                        cmove.rotation = (int)(creature.getStatus().getRotation() * 256.0f / 360.0f);
                    }
                    else if (creature.getAttitude(this.watcher) == 2 || Math.abs(diffZ) > 0.3f) {
                        if (creature.isSubmerged() && creature.getPositionZ() < 0.0f) {
                            this.watcher.getCommunicator().sendMoveCreatureAndSetZ(creatureId, creature.getPosX(), creature.getPosY(), creature.getPositionZ(), (int)(creature.getStatus().getRotation() * 256.0f / 360.0f));
                            this.clearCreatureMove(creature, cmove);
                        }
                        else {
                            if (!moveSameLevel || Structure.isGroundFloorAtPosition(creature.getPosX(), creature.getPosY(), creature.isOnSurface())) {
                                this.watcher.getCommunicator().sendMoveCreatureAndSetZ(creatureId, creature.getPosX(), creature.getPosY(), creature.getPositionZ(), (int)(creature.getStatus().getRotation() * 256.0f / 360.0f));
                            }
                            else {
                                this.watcher.getCommunicator().sendMoveCreature(creatureId, creature.getPosX(), creature.getPosY(), (int)(creature.getStatus().getRotation() * 256.0f / 360.0f), creature.isMoving());
                            }
                            this.clearCreatureMove(creature, cmove);
                        }
                    }
                    else {
                        cmove.timestamp = System.currentTimeMillis();
                        final CreatureMove creatureMove = cmove;
                        creatureMove.diffX += diffX;
                        final CreatureMove creatureMove2 = cmove;
                        creatureMove2.diffY += diffY;
                        final CreatureMove creatureMove3 = cmove;
                        creatureMove3.diffZ += diffZ;
                        cmove.rotation = (int)(creature.getStatus().getRotation() * 256.0f / 360.0f);
                    }
                }
                else if (creature.getAttitude(this.watcher) == 2) {
                    if (!moveSameLevel || Structure.isGroundFloorAtPosition(creature.getPosX(), creature.getPosY(), creature.isOnSurface())) {
                        this.watcher.getCommunicator().sendMoveCreatureAndSetZ(creatureId, creature.getPosX(), creature.getPosY(), creature.getPositionZ(), (int)(creature.getStatus().getRotation() * 256.0f / 360.0f));
                    }
                    else {
                        this.watcher.getCommunicator().sendMoveCreature(creatureId, creature.getPosX(), creature.getPosY(), (int)(creature.getStatus().getRotation() * 256.0f / 360.0f), creature.isMoving());
                    }
                    this.clearCreatureMove(creature, cmove);
                }
                else {
                    cmove.timestamp = System.currentTimeMillis();
                    cmove.rotation = (int)(creature.getStatus().getRotation() * 256.0f / 360.0f);
                }
            }
        }
        if (diffTileX != 0 || diffTileY != 0) {
            this.checkIfAttack(creature, creatureId);
            if (creature.getVehicle() != -10L) {
                try {
                    final Item itemVehicle = Items.getItem(creature.getVehicle());
                    final Vehicle vehicle = Vehicles.getVehicle(itemVehicle);
                    for (final Seat seat : vehicle.getSeats()) {
                        final PlayerInfo oInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(seat.getOccupant());
                        if (oInfo != null) {
                            try {
                                final Player oPlayer = Players.getInstance().getPlayer(oInfo.wurmId);
                                if (oPlayer.hasLink()) {
                                    this.checkIfAttack(oPlayer, oPlayer.getWurmId());
                                }
                            }
                            catch (NoSuchPlayerException ex) {}
                        }
                    }
                }
                catch (NoSuchItemException ex2) {}
            }
        }
        return false;
    }
    
    public void clearCreatureMove(final Creature creature, final CreatureMove cmove) {
        cmove.timestamp = 0L;
        cmove.diffX = 0.0f;
        cmove.diffY = 0.0f;
        cmove.diffZ = 0.0f;
    }
    
    public void clearMovementForCreature(final long creatureId) {
        final CreatureMove cmove = this.creatures.get(creatureId);
        if (cmove != null) {
            cmove.timestamp = 0L;
            cmove.diffX = 0.0f;
            cmove.diffY = 0.0f;
            cmove.diffZ = 0.0f;
        }
    }
    
    public void linkVisionArea() {
        this.checkNewZone();
        if (this.size > 0) {
            for (int x = 0; x < this.watchedZones.length; ++x) {
                this.watchedZones[x].linkTo(this, this.startX, this.startY, this.endX, this.endY);
            }
        }
        else {
            VirtualZone.logger.log(Level.WARNING, "Size is 0 for creature " + this.watcher.getName());
        }
    }
    
    private void checkNewZone() {
        if (this.size <= 0) {
            return;
        }
        final Zone[] checkedZones = Zones.getZonesCoveredBy(this);
        final LinkedList<Zone> newZones = new LinkedList<Zone>(Arrays.asList(checkedZones));
        if (this.watchedZones == null) {
            this.watchedZones = new Zone[0];
        }
        final LinkedList<Zone> oldZones = new LinkedList<Zone>(Arrays.asList(this.watchedZones));
        final ListIterator<Zone> it = newZones.listIterator();
        while (it.hasNext()) {
            final Zone newZ = it.next();
            if (VirtualZone.logger.isLoggable(Level.FINEST)) {
                VirtualZone.logger.finest("new zone is " + newZ.getStartX() + "," + newZ.getEndX() + "," + newZ.getStartY() + "," + newZ.getEndY());
            }
            final ListIterator<Zone> it2 = oldZones.listIterator();
            while (it2.hasNext()) {
                final Zone oldZ = it2.next();
                if (VirtualZone.logger.isLoggable(Level.FINEST)) {
                    VirtualZone.logger.finest("old zone is " + oldZ.getStartX() + "," + oldZ.getEndX() + "," + oldZ.getStartY() + "," + oldZ.getEndY());
                }
                if (newZ.equals(oldZ)) {
                    it.remove();
                    it2.remove();
                }
            }
        }
        for (final Zone toAdd : newZones) {
            if (VirtualZone.logger.isLoggable(Level.FINEST)) {
                VirtualZone.logger.finest("Adding zone " + this.getId() + " as watcher to " + toAdd.getId());
            }
            try {
                toAdd.addWatcher(this.id);
            }
            catch (NoSuchZoneException nze) {
                VirtualZone.logger.log(Level.INFO, nze.getMessage(), nze);
            }
        }
        for (final Zone toRemove : oldZones) {
            try {
                if (VirtualZone.logger.isLoggable(Level.FINEST)) {
                    VirtualZone.logger.finest("Removing zone " + this.getId() + " as watcher to " + toRemove.getId());
                }
                toRemove.removeWatcher(this);
            }
            catch (NoSuchZoneException sex) {
                VirtualZone.logger.log(Level.WARNING, "Zone with id does not exist!", sex);
            }
        }
        this.watchedZones = checkedZones;
    }
    
    void deleteCreature(final Creature creature, final boolean removeAsTarget) throws NoSuchCreatureException, NoSuchPlayerException {
        if (this.watcher == null) {
            VirtualZone.logger.log(Level.WARNING, "Watcher is null when linking: " + creature.getName(), new Exception());
            return;
        }
        if (removeAsTarget) {
            boolean removeTarget = true;
            if (creature.isTeleporting()) {
                removeTarget = (Math.abs(this.watcher.getPosX() - creature.getTeleportX()) > 20.0f || Math.abs(this.watcher.getPosY() - creature.getTeleportY()) > 20.0f);
            }
            else if (this.watcher.isTeleporting()) {
                removeTarget = (Math.abs(creature.getPosX() - this.watcher.getTeleportX()) > 20.0f || Math.abs(creature.getPosY() - this.watcher.getTeleportY()) > 20.0f);
            }
            if (creature.isDead()) {
                removeTarget = true;
            }
            if (this.watcher.isDead()) {
                removeTarget = true;
            }
            if (removeTarget) {
                if (creature.getWurmId() == this.watcher.target && (this.watcher.getVisionArea() == null || this.watcher.getVisionArea().getSurface() == null || !this.watcher.getVisionArea().getSurface().containsCreature(creature))) {
                    this.watcher.setTarget(-10L, true);
                }
                if (creature.target == this.watcher.getWurmId() && (creature.getVisionArea() == null || creature.getVisionArea().getSurface() == null || !creature.getVisionArea().getSurface().containsCreature(this.watcher))) {
                    creature.setTarget(-10L, true);
                }
            }
        }
        if (this.creatures != null && this.creatures.keySet().contains(new Long(creature.getWurmId()))) {
            this.creatures.remove(new Long(creature.getWurmId()));
            if (removeAsTarget) {
                this.checkIfEnemyIsPresent(false);
            }
            if (this.watcher.hasLink()) {
                if (this.watcher != null && !this.watcher.equals(creature)) {
                    this.watcher.getCommunicator().sendDeleteCreature(creature.getWurmId());
                }
                if (creature instanceof Player || creature.isNpc()) {
                    this.watcher.getCommunicator().sendRemoveLocal(creature.getName());
                }
            }
            if (creature.getVehicle() != -10L && WurmId.getType(creature.getVehicle()) == 2) {
                final Vehicle vehic = Vehicles.getVehicleForId(creature.getVehicle());
                if (vehic != null) {
                    boolean shouldRemove = true;
                    final Seat[] seats = vehic.getSeats();
                    for (int x = 0; x < seats.length; ++x) {
                        if (seats[x].isOccupied() && this.creatures.containsKey(seats[x].occupant)) {
                            shouldRemove = false;
                            break;
                        }
                    }
                    if (shouldRemove) {
                        try {
                            final Item vc = Items.getItem(creature.getVehicle());
                            final VolaTile tile = Zones.getOrCreateTile(vc.getTileX(), vc.getTileY(), vc.isOnSurface());
                            if (!this.isVisible(vc, tile)) {
                                if (vc.isMovingItem()) {
                                    this.watcher.getCommunicator().sendDeleteMovingItem(vc.getWurmId());
                                }
                                else {
                                    if (vc.isWarTarget()) {
                                        this.watcher.getCommunicator().sendRemoveEffect(vc.getWurmId());
                                    }
                                    this.watcher.getCommunicator().sendRemoveItem(vc);
                                }
                            }
                        }
                        catch (NoSuchItemException ex) {}
                    }
                }
            }
        }
    }
    
    public final void pollVisibleVehicles() {
        if (this.items != null) {
            final Iterator<Item> it = this.items.iterator();
            while (it.hasNext()) {
                final Item i = it.next();
                if (i.isVehicle()) {
                    if (i.deleted) {
                        it.remove();
                        this.sendRemoveItem(i);
                    }
                    else {
                        final VolaTile t = Zones.getTileOrNull(i.getTileX(), i.getTileY(), i.isOnSurface());
                        if (this.isVisible(i, t)) {
                            continue;
                        }
                        it.remove();
                        this.sendRemoveItem(i);
                    }
                }
            }
        }
    }
    
    public boolean isVisible(final Item item, final VolaTile tile) {
        if (item.getTemplateId() == 344) {
            return this.watcher.getPower() > 0;
        }
        if (tile == null) {
            return false;
        }
        final int distancex = Math.abs(tile.getTileX() - this.centerx);
        final int distancey = Math.abs(tile.getTileY() - this.centery);
        final int distance = Math.max(distancex, distancey);
        if (item.isVehicle()) {
            final Vehicle vehic = Vehicles.getVehicleForId(item.getWurmId());
            if (vehic != null) {
                final Seat[] seats = vehic.getSeats();
                for (int x = 0; x < seats.length; ++x) {
                    if (seats[x].isOccupied() && this.creatures.containsKey(seats[x].occupant)) {
                        return true;
                    }
                }
                if (this.watcher.isPlayer()) {
                    return Math.max(Math.abs(this.centerx - item.getTileX()), Math.abs(this.centery - item.getTileY())) <= this.size;
                }
            }
        }
        else if (this.watcher.isPlayer() && item.getSizeZ() >= 500) {
            return true;
        }
        if (item.isLight()) {
            return true;
        }
        if (distance > this.size) {
            return false;
        }
        final int isize = item.getSizeZ();
        int mod = 3;
        if (isize >= 300) {
            mod = 128;
        }
        else if (isize >= 200) {
            mod = 64;
        }
        else if (isize >= 100) {
            mod = 32;
        }
        else if (isize >= 50) {
            mod = 16;
        }
        else if (isize >= 10) {
            mod = 8;
        }
        if (item.isBrazier()) {
            return distance <= Math.max(mod, 16);
        }
        if (item.isCarpet()) {
            mod = ((distance <= Math.max(mod, 10)) ? Math.max(mod, 10) : mod);
        }
        final Structure itemStructure = tile.getStructure();
        if (itemStructure != null && itemStructure.isTypeHouse()) {
            if (this.watcher.isPlayer() && this.nearbyStructureList != null && this.nearbyStructureList.contains(itemStructure)) {
                return distance <= mod;
            }
            if (distance > 15) {
                return false;
            }
        }
        if (VirtualZone.logger.isLoggable(Level.FINEST)) {
            VirtualZone.logger.finest(item.getName() + " distance=" + distance + ", size=" + item.getSizeZ() / 10);
        }
        return distance <= mod;
    }
    
    private final ArrayList<Structure> getStructuresWithinDistance(final int tileDistance) {
        if (this.nearbyStructureList == null) {
            this.nearbyStructureList = new ArrayList<Structure>();
        }
        this.nearbyStructureList.clear();
        for (int i = this.watcher.getTileX() - tileDistance; i < this.watcher.getTileX() + tileDistance; ++i) {
            for (int j = this.watcher.getTileY() - tileDistance; j < this.watcher.getTileY() + tileDistance; ++j) {
                final VolaTile tile = Zones.getTileOrNull(Zones.safeTileX(i), Zones.safeTileY(j), this.watcher.isOnSurface());
                if (tile != null && tile.getStructure() != null && tile.getStructure().isTypeHouse() && !this.nearbyStructureList.contains(tile.getStructure())) {
                    this.nearbyStructureList.add(tile.getStructure());
                }
            }
        }
        return this.nearbyStructureList;
    }
    
    private final Structure getStructureAtWatcherPosition() {
        try {
            final Zone zone = Zones.getZone(this.watcher.getTileX(), this.watcher.getTileY(), this.watcher.isOnSurface());
            final VolaTile tile = zone.getOrCreateTile(this.watcher.getTileX(), this.watcher.getTileY());
            return tile.getStructure();
        }
        catch (NoSuchZoneException e) {
            VirtualZone.logger.log(Level.WARNING, "Unable to find the zone at the watchers tile position.", e);
            return null;
        }
    }
    
    void sendMoveMovingItem(final long aId, final float x, final float y, final int rot) {
        if (this.watcher.hasLink()) {
            this.watcher.getCommunicator().sendMoveMovingItem(aId, x, y, rot);
        }
    }
    
    void sendMoveMovingItemAndSetZ(final long aId, final float x, final float y, final float z, final int rot) {
        if (this.watcher.hasLink()) {
            this.watcher.getCommunicator().sendMoveMovingItemAndSetZ(aId, x, y, z, rot);
        }
    }
    
    boolean addItem(final Item item, final VolaTile tile, final boolean onGroundLevel) {
        return this.addItem(item, tile, -10L, onGroundLevel);
    }
    
    boolean addItem(final Item item, final VolaTile tile, final long creatureId, final boolean onGroundLevel) {
        if (this.items == null) {
            this.items = new HashSet<Item>();
        }
        if (item.isMovingItem() || this.covers(item.getTileX(), item.getTileY())) {
            if (!this.items.contains(item)) {
                this.items.add(item);
                if (this.watcher.hasLink()) {
                    if (item.isMovingItem()) {
                        byte newlayer = (byte)(item.isOnSurface() ? 0 : -1);
                        if (item.newLayer != -128) {
                            newlayer = item.newLayer;
                        }
                        this.watcher.getCommunicator().sendNewMovingItem(item.getWurmId(), item.getName(), item.getModelName(), item.getPosX(), item.getPosY(), item.getPosZ(), item.onBridge(), item.getRotation(), newlayer, item.getFloorLevel() <= 0, item.isFloating() && item.getCurrentQualityLevel() >= 10.0f, true, item.getMaterial(), item.getRarity());
                        final Vehicle vehic = Vehicles.getVehicleForId(item.getWurmId());
                        if (vehic != null) {
                            final Seat[] seats = vehic.getSeats();
                            for (int x = 0; x < seats.length; ++x) {
                                if (seats[x].isOccupied() && this.watcher.getWurmId() != seats[x].occupant) {
                                    final Creature occ = Server.getInstance().getCreatureOrNull(seats[x].occupant);
                                    if (occ != null && !occ.equals(this.watcher) && occ.isVisibleTo(this.watcher)) {
                                        if ((!Servers.localServer.PVPSERVER || this.watcher.isPaying()) && occ.isPlayer()) {
                                            this.watcher.getCommunicator().sendAddLocal(occ.getName(), seats[x].occupant);
                                        }
                                        if (!this.creatures.containsKey(seats[x].occupant)) {
                                            if (this.watcher.isPlayer()) {
                                                this.creatures.put(creatureId, new CreatureMove());
                                            }
                                            else {
                                                this.creatures.put(creatureId, null);
                                            }
                                        }
                                        this.sendAttachCreature(seats[x].occupant, item.getWurmId(), seats[x].offx, seats[x].offy, seats[x].offz, x);
                                    }
                                }
                            }
                            final Seat[] hitched = vehic.hitched;
                            for (int x2 = 0; x2 < hitched.length; ++x2) {
                                if (hitched[x2].isOccupied() && this.creatures.containsKey(hitched[x2].occupant) && this.watcher.getWurmId() != hitched[x2].occupant) {
                                    this.sendAttachCreature(hitched[x2].occupant, item.getWurmId(), hitched[x2].offx, hitched[x2].offy, hitched[x2].offz, x2);
                                }
                                else if (this.watcher.getWurmId() == hitched[x2].occupant) {
                                    VirtualZone.logger.log(Level.WARNING, "This should be unused code.");
                                    this.sendAttachCreature(-1L, item.getWurmId(), hitched[x2].offx, hitched[x2].offy, hitched[x2].offz, x2);
                                }
                            }
                        }
                    }
                    else {
                        this.watcher.getCommunicator().sendItem(item, creatureId, onGroundLevel);
                        if (item.isWarTarget()) {
                            this.watcher.getCommunicator().sendAddEffect(item.getWurmId(), (short)24, item.getPosX(), item.getPosY(), item.getData1(), (byte)(item.isOnSurface() ? 0 : -1));
                            this.watcher.getCommunicator().sendTargetStatus(item.getWurmId(), (byte)item.getData2(), item.getData1());
                        }
                        if (item.getTemplate().hasViewableSubItems() && item.getItemCount() > 0) {
                            final boolean normalContainer = item.getTemplate().isContainerWithSubItems();
                            for (final Item i : item.getItems()) {
                                if (normalContainer && !i.isPlacedOnParent()) {
                                    continue;
                                }
                                this.watcher.getCommunicator().sendItem(i, -10L, false);
                                if (i.isLight() && i.isOnFire()) {
                                    this.addLightSource(i);
                                }
                                if (i.getEffects().length > 0) {
                                    for (final Effect e : i.getEffects()) {
                                        this.addEffect(e, false);
                                    }
                                }
                                if (i.getColor() != -1) {
                                    this.sendRepaint(i.getWurmId(), (byte)WurmColor.getColorRed(i.getColor()), (byte)WurmColor.getColorGreen(i.getColor()), (byte)WurmColor.getColorBlue(i.getColor()), (byte)(-1), (byte)0);
                                }
                                if (i.getColor2() == -1) {
                                    continue;
                                }
                                this.sendRepaint(i.getWurmId(), (byte)WurmColor.getColorRed(i.getColor2()), (byte)WurmColor.getColorGreen(i.getColor2()), (byte)WurmColor.getColorBlue(i.getColor2()), (byte)(-1), (byte)1);
                            }
                        }
                    }
                    if (item.isLight() && item.isOnFire()) {
                        this.addLightSource(item);
                        if (item.getEffects().length > 0) {
                            final Effect[] effs = item.getEffects();
                            for (int x3 = 0; x3 < effs.length; ++x3) {
                                this.addEffect(effs[x3], false);
                            }
                        }
                    }
                    if (!item.isLight() || item.getTemplateId() == 1396) {
                        if (item.getColor() != -1) {
                            this.sendRepaint(item.getWurmId(), (byte)WurmColor.getColorRed(item.getColor()), (byte)WurmColor.getColorGreen(item.getColor()), (byte)WurmColor.getColorBlue(item.getColor()), (byte)(-1), (byte)0);
                        }
                        if (item.supportsSecondryColor() && item.getColor2() != -1) {
                            this.sendRepaint(item.getWurmId(), (byte)WurmColor.getColorRed(item.getColor2()), (byte)WurmColor.getColorGreen(item.getColor2()), (byte)WurmColor.getColorBlue(item.getColor2()), (byte)(-1), (byte)1);
                        }
                    }
                    if (item.getExtra() != -1L && (item.getTemplateId() == 491 || item.getTemplateId() == 490)) {
                        final Optional<Item> extraItem = Items.getItemOptional(item.getExtra());
                        if (extraItem.isPresent()) {
                            this.sendBoatAttachment(item.getWurmId(), extraItem.get().getTemplateId(), extraItem.get().getMaterial(), (byte)1, extraItem.get().getAuxData());
                        }
                    }
                }
                if (item.isHugeAltar()) {
                    if (this.watcher.getMusicPlayer() != null && this.watcher.getMusicPlayer().isItOkToPlaySong(true)) {
                        if (item.getTemplateId() == 327) {
                            this.watcher.getMusicPlayer().checkMUSIC_WHITELIGHT_SND();
                        }
                        else {
                            this.watcher.getMusicPlayer().checkMUSIC_BLACKLIGHT_SND();
                        }
                    }
                }
                else if (item.getTemplateId() == 518 && this.watcher.getMusicPlayer() != null && this.watcher.getMusicPlayer().isItOkToPlaySong(true)) {
                    this.watcher.getMusicPlayer().checkMUSIC_COLOSSUS_SND();
                }
            }
        }
        else {
            final int tilex = item.getTileX();
            final int tiley = item.getTileY();
            try {
                item.getParent();
            }
            catch (NoSuchItemException ex) {}
            if (item.getContainerSizeZ() < 500 && !item.isVehicle()) {
                final VolaTile vtile = Zones.getTileOrNull(tilex, tiley, this.isOnSurface);
                if (vtile == null) {
                    return false;
                }
                if (!vtile.equals(tile)) {
                    return false;
                }
                if (!this.covers(tile.getTileX(), tile.getTileY())) {
                    vtile.removeWatcher(this);
                }
            }
        }
        return true;
    }
    
    private void sendRemoveItem(final Item item) {
        if (this.watcher.hasLink()) {
            if (item.isMovingItem()) {
                this.watcher.getCommunicator().sendDeleteMovingItem(item.getWurmId());
                if (this.watcher.isPlayer()) {
                    final Vehicle vehic = Vehicles.getVehicleForId(item.getWurmId());
                    if (vehic != null) {
                        final Seat[] seats = vehic.getSeats();
                        for (int x = 0; x < seats.length; ++x) {
                            if (seats[x].isOccupied()) {
                                try {
                                    final Creature occ = Server.getInstance().getCreature(seats[x].occupant);
                                    if (occ != null && !occ.equals(this.watcher)) {
                                        if (this.creatures != null) {
                                            this.creatures.remove(seats[x].occupant);
                                        }
                                        this.watcher.getCommunicator().sendRemoveLocal(occ.getName());
                                    }
                                }
                                catch (NoSuchCreatureException nsc) {
                                    VirtualZone.logger.log(Level.WARNING, nsc.getMessage(), nsc);
                                }
                                catch (NoSuchPlayerException ex) {}
                            }
                        }
                    }
                }
            }
            else {
                if (item.isWarTarget()) {
                    this.watcher.getCommunicator().sendRemoveEffect(item.getWurmId());
                }
                if (item.getTemplate().hasViewableSubItems() && item.getItemCount() > 0) {
                    final boolean normalContainer = item.getTemplate().isContainerWithSubItems();
                    for (final Item i : item.getAllItems(false)) {
                        if (!normalContainer || i.isPlacedOnParent()) {
                            this.watcher.getCommunicator().sendRemoveItem(i);
                        }
                    }
                }
                this.watcher.getCommunicator().sendRemoveItem(item);
            }
        }
    }
    
    void removeItem(final Item item) {
        if (this.items != null && this.items.contains(item)) {
            this.items.remove(item);
            this.sendRemoveItem(item);
        }
    }
    
    void removeStructure(final Structure structure) {
        if (VirtualZone.logger.isLoggable(Level.FINEST)) {
            VirtualZone.logger.finest(this.watcher.getName() + " removing structure " + structure);
        }
        if (this.structures != null && this.structures.contains(structure)) {
            boolean stillHere = false;
            final VolaTile[] tiles = structure.getStructureTiles();
            for (int x = 0; x < tiles.length; ++x) {
                if (this.covers(tiles[x].getTileX(), tiles[x].getTileY())) {
                    stillHere = true;
                    break;
                }
            }
            if (!stillHere) {
                this.structures.remove(structure);
                this.watcher.getCommunicator().sendRemoveStructure(structure.getWurmId());
            }
        }
    }
    
    void deleteStructure(final Structure structure) {
        if (this.structures != null && this.structures.contains(structure)) {
            this.structures.remove(structure);
            this.watcher.getCommunicator().sendRemoveStructure(structure.getWurmId());
        }
    }
    
    private void removeAllStructures() {
        if (this.structures != null) {
            for (final Structure structure : this.structures) {
                this.watcher.getCommunicator().sendRemoveStructure(structure.getWurmId());
            }
        }
        this.structures = null;
    }
    
    void sendStructureWalls(final Structure structure) {
        final Wall[] wallArr = structure.getWalls();
        for (int x = 0; x < wallArr.length; ++x) {
            this.updateWall(structure.getWurmId(), wallArr[x]);
        }
    }
    
    void addStructure(final Structure structure) {
        if (VirtualZone.logger.isLoggable(Level.FINEST)) {
            VirtualZone.logger.finest(this.watcher.getName() + " adding structure " + structure);
        }
        if (this.structures == null) {
            this.structures = new HashSet<Structure>();
        }
        if (!this.structures.contains(structure)) {
            this.structures.add(structure);
            this.watcher.getCommunicator().sendAddStructure(structure.getName(), (short)structure.getCenterX(), (short)structure.getCenterY(), structure.getWurmId(), structure.getStructureType(), structure.getLayer());
            if (structure.isTypeHouse()) {
                this.watcher.getCommunicator().sendMultipleBuildMarkers(structure.getWurmId(), structure.getStructureTiles(), structure.getLayer());
                this.sendStructureWalls(structure);
            }
            final Floor[] floorArr = structure.getFloors();
            if (floorArr != null) {
                for (int x = 0; x < floorArr.length; ++x) {
                    this.updateFloor(structure.getWurmId(), floorArr[x]);
                }
            }
            final BridgePart[] bridgePartArr = structure.getBridgeParts();
            if (bridgePartArr != null) {
                for (int x2 = 0; x2 < bridgePartArr.length; ++x2) {
                    this.updateBridgePart(structure.getWurmId(), bridgePartArr[x2]);
                }
            }
        }
    }
    
    void addBuildMarker(final Structure structure, final int tilex, final int tiley) {
        if (this.structures == null) {
            this.structures = new HashSet<Structure>();
        }
        if (!this.structures.contains(structure)) {
            this.addStructure(structure);
        }
        else {
            this.watcher.getCommunicator().sendSingleBuildMarker(structure.getWurmId(), tilex, tiley, this.getLayer());
        }
    }
    
    void removeBuildMarker(final Structure structure, final int tilex, final int tiley) {
        if (this.structures != null && this.structures.contains(structure)) {
            boolean stillHere = false;
            final VolaTile[] tiles = structure.getStructureTiles();
            for (int x = 0; x < tiles.length; ++x) {
                if (this.covers(tiles[x].getTileX(), tiles[x].getTileY())) {
                    stillHere = true;
                    break;
                }
            }
            if (stillHere) {
                if (VirtualZone.logger.isLoggable(Level.FINEST)) {
                    VirtualZone.logger.finest(this.watcher.getName() + " removing build marker for structure " + structure.getWurmId());
                }
                this.watcher.getCommunicator().sendSingleBuildMarker(structure.getWurmId(), tilex, tiley, this.getLayer());
            }
            else {
                this.removeStructure(structure);
            }
        }
        else {
            VirtualZone.logger.log(Level.INFO, "Hmm tried to remove buildmarker from a zone that didn't contain it.");
        }
    }
    
    void finalizeBuildPlan(final long oldStructureId, final long newStructureId) {
        if (this.finalizedBuildings == null) {
            this.finalizedBuildings = new HashSet<Long>();
        }
        if (!this.finalizedBuildings.contains(new Long(newStructureId))) {
            try {
                final Structure structure = Structures.getStructure(newStructureId);
                if (structure.isTypeHouse()) {
                    this.watcher.getCommunicator().sendRemoveStructure(oldStructureId);
                }
                this.watcher.getCommunicator().sendAddStructure(structure.getName(), (short)structure.getCenterX(), (short)structure.getCenterY(), structure.getWurmId(), structure.getStructureType(), structure.getLayer());
                if (structure.isTypeHouse()) {
                    this.watcher.getCommunicator().sendMultipleBuildMarkers(structure.getWurmId(), structure.getStructureTiles(), structure.getLayer());
                }
                final Wall[] wallArr = structure.getWalls();
                for (int x = 0; x < wallArr.length; ++x) {
                    if (wallArr[x].getType() != StructureTypeEnum.PLAN) {
                        this.watcher.getCommunicator().sendAddWall(structure.getWurmId(), wallArr[x]);
                        if (wallArr[x].getDamage() >= 60.0f) {
                            this.watcher.getCommunicator().sendWallDamageState(structure.getWurmId(), wallArr[x].getId(), (byte)wallArr[x].getDamage());
                        }
                    }
                }
            }
            catch (NoSuchStructureException nss) {
                VirtualZone.logger.log(Level.WARNING, "The new building doesn't exist.", nss);
            }
            this.finalizedBuildings.add(new Long(newStructureId));
        }
    }
    
    void addDoor(final Door door) {
        if (this.doors == null) {
            this.doors = new HashSet<Door>();
        }
        if (!this.doors.contains(door)) {
            this.doors.add(door);
            if (door.isOpen()) {
                if (door instanceof FenceGate) {
                    this.openFence(((FenceGate)door).getFence(), false, true);
                }
                else {
                    this.openDoor(door);
                }
            }
        }
    }
    
    void removeDoor(final Door door) {
        if (this.doors != null) {
            this.doors.remove(door);
        }
    }
    
    public void openDoor(final Door door) {
        this.watcher.getCommunicator().sendOpenDoor(door);
    }
    
    public void closeDoor(final Door door) {
        this.watcher.getCommunicator().sendCloseDoor(door);
    }
    
    public void openFence(final Fence fence, final boolean passable, final boolean changedPassable) {
        this.watcher.getCommunicator().sendOpenFence(fence, passable, changedPassable);
    }
    
    public void closeFence(final Fence fence, final boolean passable, final boolean changedPassable) {
        this.watcher.getCommunicator().sendCloseFence(fence, passable, changedPassable);
    }
    
    public void openMineDoor(final MineDoorPermission door) {
        this.watcher.getCommunicator().sendOpenMineDoor(door);
    }
    
    public void closeMineDoor(final MineDoorPermission door) {
        this.watcher.getCommunicator().sendCloseMineDoor(door);
    }
    
    void updateFloor(final long structureId, final Floor floor) {
        this.watcher.getCommunicator().sendAddFloor(structureId, floor);
        if (floor.getDamage() >= 60.0f) {
            this.watcher.getCommunicator().sendWallDamageState(floor.getStructureId(), floor.getId(), (byte)floor.getDamage());
        }
    }
    
    void updateBridgePart(final long structureId, final BridgePart bridgePart) {
        this.watcher.getCommunicator().sendAddBridgePart(structureId, bridgePart);
        if (bridgePart.getDamage() >= 60.0f) {
            this.watcher.getCommunicator().sendWallDamageState(bridgePart.getStructureId(), bridgePart.getId(), (byte)bridgePart.getDamage());
        }
    }
    
    void updateWall(final long structureId, final Wall wall) {
        this.watcher.getCommunicator().sendAddWall(structureId, wall);
        if (wall.getDamage() >= 60.0f) {
            this.watcher.getCommunicator().sendWallDamageState(wall.getStructureId(), wall.getId(), (byte)wall.getDamage());
        }
    }
    
    void removeWall(final long structureId, final Wall wall) {
        this.watcher.getCommunicator().sendRemoveWall(structureId, wall);
    }
    
    void removeFloor(final long structureId, final Floor floor) {
        this.watcher.getCommunicator().sendRemoveFloor(structureId, floor);
    }
    
    void removeBridgePart(final long structureId, final BridgePart bridgePart) {
        this.watcher.getCommunicator().sendRemoveBridgePart(structureId, bridgePart);
    }
    
    void changeStructureName(final long structureId, final String newName) {
        this.watcher.getCommunicator().sendChangeStructureName(structureId, newName);
    }
    
    void playSound(final Sound sound) {
        this.watcher.getCommunicator().sendSound(sound);
    }
    
    public static boolean isCreatureTurnedTowardsTarget(final Creature target, final Creature performer) {
        return isCreatureTurnedTowardsTarget(target, performer, 180.0f, false);
    }
    
    public static boolean isCreatureShieldedVersusTarget(final Creature target, final Creature performer) {
        if (performer.isWithinDistanceTo(target, 1.5f)) {
            if (Servers.localServer.testServer && target.isPlayer() && performer.isPlayer()) {
                target.getCommunicator().sendNormalServerMessage(performer.getName() + " is so close he auto blocks you.");
            }
            return true;
        }
        return isCreatureTurnedTowardsTarget(target, performer, 135.0f, true);
    }
    
    public static boolean isCreatureTurnedTowardsItem(final Item target, final Creature performer, final float angle) {
        final double newrot = Math.atan2(target.getPosY() - (int)performer.getStatus().getPositionY(), target.getPosX() - (int)performer.getStatus().getPositionX());
        float attAngle = (float)(newrot * 57.29577951308232) + 90.0f;
        attAngle = Creature.normalizeAngle(attAngle);
        final float prot = Creature.normalizeAngle(performer.getStatus().getRotation() - attAngle);
        return prot <= angle / 2.0f || prot >= 360.0f - angle / 2.0f;
    }
    
    public static boolean isItemTurnedTowardsCreature(final Creature target, final Item performer, final float angle) {
        final double newrot = Math.atan2(target.getPosY() - (int)performer.getPosY(), target.getPosX() - (int)performer.getPosX());
        float attAngle = (float)(newrot * 57.29577951308232) - 90.0f;
        attAngle = Creature.normalizeAngle(attAngle);
        final float prot = Creature.normalizeAngle(performer.getRotation() - attAngle);
        return prot <= angle / 2.0f || prot >= 360.0f - angle / 2.0f;
    }
    
    public static boolean isCreatureTurnedTowardsTarget(final Creature target, final Creature performer, final float angle, final boolean leftWinged) {
        final boolean log = leftWinged && Servers.localServer.testServer && target.isPlayer() && performer.isPlayer();
        final double newrot = Math.atan2(target.getPosY() - (int)performer.getStatus().getPositionY(), target.getPosX() - (int)performer.getStatus().getPositionX());
        float attAngle = (float)(newrot * 57.29577951308232) + 90.0f;
        attAngle = Creature.normalizeAngle(attAngle);
        final float crot = Creature.normalizeAngle(performer.getStatus().getRotation());
        final float prot = Creature.normalizeAngle(attAngle - crot);
        float rightAngle = angle / 2.0f;
        float leftAngle = 360.0f - angle / 2.0f;
        if (leftWinged) {
            leftAngle -= 45.0f;
            rightAngle -= 45.0f;
        }
        leftAngle = Creature.normalizeAngle(leftAngle);
        rightAngle = Creature.normalizeAngle(rightAngle);
        if (log) {
            target.getCommunicator().sendNormalServerMessage(attAngle + ", " + crot + ", prot=" + prot);
        }
        if (prot > rightAngle && prot < leftAngle) {
            if (log) {
                target.getCommunicator().sendNormalServerMessage("1.5 " + performer.getName() + " will not block you. Angle to me= " + attAngle + ", creature angle=" + crot + ", difference=" + prot + ". Max left=" + leftAngle + ", right=" + rightAngle);
            }
            return false;
        }
        if (log) {
            target.getCommunicator().sendNormalServerMessage("1.5 " + performer.getName() + " will block you. Angle to me= " + attAngle + ", creature angle=" + crot + ", difference=" + prot + ". Max left=" + leftAngle + ", right=" + rightAngle);
        }
        return true;
    }
    
    private void addLightSource(final Item lightSource) {
        int colorToUse = lightSource.getColor();
        if (lightSource.getTemplateId() == 1396) {
            colorToUse = lightSource.getColor2();
        }
        if (colorToUse != -1) {
            int lightStrength = Math.max(WurmColor.getColorRed(colorToUse), WurmColor.getColorGreen(colorToUse));
            lightStrength = Math.max(1, Math.max(lightStrength, WurmColor.getColorBlue(colorToUse)));
            final byte r = (byte)(WurmColor.getColorRed(colorToUse) * 128 / lightStrength);
            final byte g = (byte)(WurmColor.getColorGreen(colorToUse) * 128 / lightStrength);
            final byte b = (byte)(WurmColor.getColorBlue(colorToUse) * 128 / lightStrength);
            this.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, r, g, b, lightSource.getRadius());
        }
        else if (lightSource.isLightBright()) {
            final int lightStrength = (int)(80.0f + lightSource.getCurrentQualityLevel() / 100.0f * 40.0f);
            this.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, Item.getRLight(lightStrength), Item.getGLight(lightStrength), Item.getBLight(lightStrength), lightSource.getRadius());
        }
        else {
            this.sendAttachItemEffect(lightSource.getWurmId(), (byte)4, Item.getRLight(80), Item.getGLight(80), Item.getBLight(80), lightSource.getRadius());
        }
    }
    
    private void addLightSource(final Creature creature, final Item lightSource) {
        if (lightSource.getColor() != -1) {
            int lightStrength = Math.max(WurmColor.getColorRed(lightSource.getColor()), WurmColor.getColorGreen(lightSource.getColor()));
            lightStrength = Math.max(1, Math.max(lightStrength, WurmColor.getColorBlue(lightSource.getColor())));
            final byte r = (byte)(WurmColor.getColorRed(lightSource.getColor()) * 128 / lightStrength);
            final byte g = (byte)(WurmColor.getColorGreen(lightSource.getColor()) * 128 / lightStrength);
            final byte b = (byte)(WurmColor.getColorBlue(lightSource.getColor()) * 128 / lightStrength);
            this.sendAttachCreatureEffect(creature, (byte)0, r, g, b, lightSource.getRadius());
        }
        else if (lightSource.isLightBright()) {
            final int lightStrength = (int)(80.0f + lightSource.getCurrentQualityLevel() / 100.0f * 40.0f);
            this.sendAttachCreatureEffect(creature, (byte)0, Item.getRLight(lightStrength), Item.getGLight(lightStrength), Item.getBLight(lightStrength), lightSource.getRadius());
        }
        else {
            this.sendAttachCreatureEffect(creature, (byte)0, Item.getRLight(80), Item.getGLight(80), Item.getBLight(80), lightSource.getRadius());
        }
    }
    
    public void sendAttachCreatureEffect(final Creature creature, final byte effectType, final byte data0, final byte data1, final byte data2, final byte radius) {
        if (creature == null) {
            this.watcher.getCommunicator().sendAttachEffect(-1L, effectType, data0, data1, data2, radius);
        }
        else if (this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendAttachEffect(creature.getWurmId(), effectType, data0, data1, data2, radius);
        }
    }
    
    void sendAttachItemEffect(final long targetId, final byte effectType, final byte data0, final byte data1, final byte data2, final byte radius) {
        this.watcher.getCommunicator().sendAttachEffect(targetId, effectType, data0, data1, data2, radius);
    }
    
    void sendRemoveEffect(final long targetId, final byte effectType) {
        if (WurmId.getType(targetId) == 2) {
            this.watcher.getCommunicator().sendRemoveEffect(targetId, effectType);
        }
        else if (targetId == -1L || this.creatures.containsKey(targetId)) {
            this.watcher.getCommunicator().sendRemoveEffect(targetId, effectType);
        }
    }
    
    void sendHorseWear(final long creatureId, final int itemId, final byte material, final byte slot, final byte aux_data) {
        if (this.creatures.containsKey(creatureId)) {
            this.watcher.getCommunicator().sendHorseWear(creatureId, itemId, material, slot, aux_data);
        }
    }
    
    void sendRemoveHorseWear(final long creatureId, final int itemId, final byte slot) {
        if (this.creatures.containsKey(creatureId)) {
            this.watcher.getCommunicator().sendRemoveHorseWear(creatureId, itemId, slot);
        }
    }
    
    void sendBoatAttachment(final long itemId, final int templateId, final byte material, final byte slot, final byte aux) {
        this.watcher.getCommunicator().sendHorseWear(itemId, templateId, material, slot, aux);
    }
    
    void sendBoatDetachment(final long itemId, final int templateId, final byte slot) {
        this.watcher.getCommunicator().sendRemoveHorseWear(itemId, templateId, slot);
    }
    
    void sendWearItem(final long creatureId, final int itemId, final byte bodyPart, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue, final byte material, final byte rarity) {
        if (creatureId == -1L || this.creatures.containsKey(creatureId)) {
            this.watcher.getCommunicator().sendWearItem(creatureId, itemId, bodyPart, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue, material, rarity);
        }
    }
    
    void sendRemoveWearItem(final long creatureId, final byte bodyPart) {
        if (creatureId == -1L || this.creatures.containsKey(creatureId)) {
            this.watcher.getCommunicator().sendRemoveWearItem(creatureId, bodyPart);
        }
    }
    
    void sendWieldItem(final long creatureId, final byte slot, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (creatureId == -1L || this.creatures.containsKey(creatureId)) {
            this.watcher.getCommunicator().sendWieldItem(creatureId, slot, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
        }
    }
    
    void sendUseItem(final Creature creature, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (creature == null) {
            this.watcher.getCommunicator().sendUseItem(-1L, modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
        }
        else if (!creature.isTeleporting() && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendUseItem(creature.getWurmId(), modelname, rarity, colorRed, colorGreen, colorBlue, secondaryColorRed, secondaryColorGreen, secondaryColorBlue);
        }
    }
    
    void sendStopUseItem(final Creature creature) {
        if (creature == null) {
            this.watcher.getCommunicator().sendStopUseItem(-1L);
        }
        else if (!creature.isTeleporting() && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendStopUseItem(creature.getWurmId());
        }
    }
    
    public void sendRepaint(final long wurmid, final byte red, final byte green, final byte blue, final byte alpha, final byte paintType) {
        this.watcher.getCommunicator().sendRepaint(wurmid, red, green, blue, alpha, paintType);
    }
    
    private void sendResizeCreature(final long wurmid, final byte xscaleMod, final byte yscaleMod, final byte zscaleMod) {
        this.watcher.getCommunicator().sendResize(wurmid, xscaleMod, yscaleMod, zscaleMod);
    }
    
    void sendAnimation(final Creature creature, final String animationName, final boolean looping, final long target) {
        if (creature == null) {
            if (target <= 0L) {
                this.watcher.getCommunicator().sendAnimation(-1L, animationName, looping, animationName.equals("die"));
            }
            else {
                this.watcher.getCommunicator().sendAnimation(-1L, animationName, looping, false, target);
            }
        }
        else if (this.creatures.containsKey(creature.getWurmId())) {
            if (target <= 0L) {
                this.watcher.getCommunicator().sendAnimation(creature.getWurmId(), animationName, looping, animationName.equals("die"));
            }
            else {
                this.watcher.getCommunicator().sendAnimation(creature.getWurmId(), animationName, looping, false, target);
            }
        }
    }
    
    void sendStance(final Creature creature, final byte stance) {
        if (creature == null) {
            this.watcher.getCommunicator().sendStance(-1L, stance);
        }
        else if (this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendStance(creature.getWurmId(), stance);
        }
    }
    
    void sendCreatureDamage(final Creature creature, final float currentDamage) {
        if (creature != null && this.watcher != null && !creature.equals(this.watcher) && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().setCreatureDamage(creature.getWurmId(), currentDamage);
        }
    }
    
    void sendFishingLine(final Creature creature, final float posX, final float posY, final byte floatType) {
        if (creature != null && this.watcher != null && !creature.equals(this.watcher) && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendFishCasted(creature.getWurmId(), posX, posY, floatType);
        }
    }
    
    void sendFishHooked(final Creature creature, final byte fishType, final long fishId) {
        if (creature != null && this.watcher != null && !creature.equals(this.watcher) && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendFishBite(fishType, fishId, creature.getWurmId());
        }
    }
    
    void sendFishingStopped(final Creature creature) {
        if (creature != null && this.watcher != null && !creature.equals(this.watcher) && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendFishSubCommand((byte)15, creature.getWurmId());
        }
    }
    
    void sendSpearStrike(final Creature creature, final float posX, final float posY) {
        if (creature != null && this.watcher != null && !creature.equals(this.watcher) && this.creatures.containsKey(creature.getWurmId())) {
            this.watcher.getCommunicator().sendSpearStrike(creature.getWurmId(), posX, posY);
        }
    }
    
    void sendAttachCreature(final long creatureId, final long targetId, final float offx, final float offy, final float offz, final int seatId) {
        boolean send = true;
        Label_0396: {
            if (targetId != -1L) {
                if (WurmId.getType(targetId) == 1 || WurmId.getType(targetId) == 0) {
                    if (!this.creatures.containsKey(targetId)) {
                        try {
                            this.addCreature(targetId, true);
                            send = false;
                        }
                        catch (NoSuchCreatureException ex) {}
                        catch (NoSuchPlayerException ex2) {}
                    }
                }
                else {
                    if (WurmId.getType(targetId) != 2 && WurmId.getType(targetId) != 19) {
                        if (WurmId.getType(targetId) != 20) {
                            break Label_0396;
                        }
                    }
                    try {
                        final Item item = Items.getItem(targetId);
                        if (this.items == null || !this.items.contains(item)) {
                            if (this.watcher.getVisionArea() != null) {
                                if (this.isOnSurface()) {
                                    if (!item.isOnSurface() && (this.watcher.getVisionArea().getUnderGround() == null || this.watcher.getVisionArea().getUnderGround().items == null || !this.watcher.getVisionArea().getUnderGround().items.contains(item))) {
                                        if (this.watcher.getVisionArea().getUnderGround() != null) {
                                            if (this.watcher.getVisionArea().getUnderGround().covers(item.getTileX(), item.getTileY())) {
                                                this.watcher.getVisionArea().getUnderGround().addItem(item, null, true);
                                            }
                                        }
                                        else {
                                            this.addItem(item, null, true);
                                        }
                                        send = false;
                                    }
                                }
                                else if (item.isOnSurface() && (this.watcher.getVisionArea().getSurface() == null || this.watcher.getVisionArea().getSurface().items == null || !this.watcher.getVisionArea().getSurface().items.contains(item))) {
                                    if (this.watcher.getVisionArea().getSurface() != null) {
                                        this.watcher.getVisionArea().getSurface().addItem(item, null, true);
                                    }
                                    else {
                                        this.addItem(item, null, true);
                                    }
                                    send = false;
                                }
                            }
                            else {
                                this.addItem(item, null, true);
                                send = false;
                            }
                        }
                    }
                    catch (NoSuchItemException ex3) {}
                }
            }
        }
        Label_0538: {
            if (creatureId != -1L) {
                if (WurmId.getType(creatureId) == 1 || WurmId.getType(creatureId) == 0) {
                    if (this.watcher.getWurmId() != creatureId && !this.creatures.containsKey(creatureId) && targetId != -1L) {
                        try {
                            this.addCreature(creatureId, true);
                            send = false;
                        }
                        catch (NoSuchCreatureException ex4) {}
                        catch (NoSuchPlayerException ex5) {}
                    }
                }
                else {
                    if (WurmId.getType(creatureId) != 2 && WurmId.getType(creatureId) != 19) {
                        if (WurmId.getType(creatureId) != 20) {
                            break Label_0538;
                        }
                    }
                    try {
                        final Item item = Items.getItem(creatureId);
                        if (!this.items.contains(item)) {
                            this.addItem(item, null, true);
                        }
                    }
                    catch (NoSuchItemException ex6) {}
                }
            }
        }
        if (send) {
            if (creatureId == this.watcher.getWurmId()) {
                this.watcher.getCommunicator().attachCreature(-1L, targetId, offx, offy, offz, seatId);
            }
            else {
                this.watcher.getCommunicator().attachCreature(creatureId, targetId, offx, offy, offz, seatId);
            }
        }
    }
    
    public void sendRotate(final Item item, final float rotation) {
        if (this.items != null && this.items.contains(item)) {
            this.watcher.getCommunicator().sendRotate(item.getWurmId(), rotation);
        }
    }
    
    @Override
    public String toString() {
        return "VirtualZone [ID: " + this.id + ", Watcher: " + this.watcher.getWurmId() + ']';
    }
    
    public void sendHostileCreatures() {
        int nums = 0;
        String layer = "Above ground";
        if (!this.isOnSurface) {
            layer = "Below ground";
        }
        for (final Long c : this.creatures.keySet()) {
            try {
                final Creature creat = Server.getInstance().getCreature(c);
                if (creat.getAttitude(this.watcher) != 2) {
                    continue;
                }
                final int tilex = creat.getTileX();
                final int tiley = creat.getTileY();
                if (this.watcher.getCurrentTile() == null) {
                    continue;
                }
                ++nums;
                final int ctx = this.watcher.getCurrentTile().tilex;
                final int cty = this.watcher.getCurrentTile().tiley;
                final int mindist = Math.max(Math.abs(tilex - ctx), Math.abs(tiley - cty));
                final int dir = MethodsCreatures.getDir(this.watcher, tilex, tiley);
                final String direction = MethodsCreatures.getLocationStringFor(this.watcher.getStatus().getRotation(), dir, "you");
                this.watcher.getCommunicator().sendNormalServerMessage(EndGameItems.getDistanceString(mindist, creat.getName(), direction, false) + layer);
            }
            catch (NoSuchCreatureException nsc) {
                VirtualZone.logger.log(Level.WARNING, nsc.getMessage(), nsc);
            }
            catch (NoSuchPlayerException nsp) {
                VirtualZone.logger.log(Level.WARNING, nsp.getMessage(), nsp);
            }
        }
        if (nums == 0) {
            this.watcher.getCommunicator().sendNormalServerMessage("No hostile creatures found " + layer.toLowerCase() + ".");
        }
    }
    
    public void sendAddTileEffect(final int tilex, final int tiley, final int layer, final byte effect, final int floorLevel, final boolean loop) {
        this.watcher.getCommunicator().sendAddAreaSpellEffect(tilex, tiley, layer, effect, floorLevel, 0, loop);
    }
    
    public void sendRemoveTileEffect(final int tilex, final int tiley, final int layer) {
        this.watcher.getCommunicator().sendRemoveAreaSpellEffect(tilex, tiley, layer);
    }
    
    public void updateWallDamageState(final Wall wall) {
        this.watcher.getCommunicator().sendWallDamageState(wall.getStructureId(), wall.getId(), (byte)wall.getDamage());
    }
    
    public void updateFloorDamageState(final Floor floor) {
        this.watcher.getCommunicator().sendWallDamageState(floor.getStructureId(), floor.getId(), (byte)floor.getDamage());
    }
    
    public void updateBridgePartDamageState(final BridgePart bridgePart) {
        this.watcher.getCommunicator().sendWallDamageState(bridgePart.getStructureId(), bridgePart.getId(), (byte)bridgePart.getDamage());
    }
    
    public void updateFenceDamageState(final Fence fence) {
        this.watcher.getCommunicator().sendDamageState(fence.getId(), (byte)fence.getDamage());
    }
    
    public void updateTargetStatus(final long targetId, final byte statusType, final float status) {
        this.watcher.getCommunicator().sendTargetStatus(targetId, statusType, status);
    }
    
    public void setNewFace(final Creature c) {
        if (c.getWurmId() == this.watcher.getWurmId()) {
            this.watcher.getCommunicator().sendNewFace(-10L, c.getFace());
        }
        else if (this.containsCreature(c)) {
            this.watcher.getCommunicator().sendNewFace(c.getWurmId(), c.getFace());
        }
    }
    
    public void setNewRarityShader(final Creature c) {
        if (c.getWurmId() == this.watcher.getWurmId()) {
            this.watcher.getCommunicator().updateCreatureRarity(-10L, c.getRarityShader());
        }
        else if (this.containsCreature(c)) {
            this.watcher.getCommunicator().updateCreatureRarity(c.getWurmId(), c.getRarityShader());
        }
    }
    
    public void sendActionControl(final long creatureId, final String actionString, final boolean start, final int timeLeft) {
        if (creatureId == this.watcher.getWurmId()) {
            this.watcher.getCommunicator().sendActionControl(-1L, actionString, start, timeLeft);
        }
        else {
            this.watcher.getCommunicator().sendActionControl(creatureId, actionString, start, timeLeft);
        }
    }
    
    public void sendProjectile(final long itemid, final byte type, final String modelName, final String name, final byte material, final float _startX, final float _startY, final float startH, final float rot, final byte layer, final float _endX, final float _endY, final float endH, final long sourceId, final long targetId, final float projectedSecondsInAir, final float actualSecondsInAir) {
        this.watcher.getCommunicator().sendProjectile(itemid, type, modelName, name, material, _startX, _startY, startH, rot, layer, _endX, _endY, endH, sourceId, targetId, projectedSecondsInAir, actualSecondsInAir);
    }
    
    public void sendNewProjectile(final long itemid, final byte type, final String modelName, final String name, final byte material, final Vector3f startingPosition, final Vector3f startingVelocity, final Vector3f endingPosition, final float rotation, final boolean surface) {
        this.watcher.getCommunicator().sendNewProjectile(itemid, type, modelName, name, material, startingPosition, startingVelocity, endingPosition, rotation, surface);
    }
    
    public final void sendBridgeId(final long creatureId, final long bridgeId) {
        this.watcher.getCommunicator().sendBridgeId(creatureId, bridgeId);
    }
    
    static {
        logger = Logger.getLogger(VirtualZone.class.getName());
        VirtualZone.ids = 0;
        worldTileSizeX = 1 << Constants.meshSize;
        worldTileSizeY = 1 << Constants.meshSize;
        emptyLongArray = new Long[0];
        allZones = new HashSet<VirtualZone>();
        caveToSurfaceLocalDistance = (Servers.localServer.EPIC ? 20 : 80);
    }
}

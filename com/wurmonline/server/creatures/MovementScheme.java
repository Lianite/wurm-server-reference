// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.modifiers.FixedDoubleValueModifier;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.WurmId;
import com.wurmonline.mesh.FoliageAge;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import com.wurmonline.server.Servers;
import com.wurmonline.server.behaviours.Terraforming;
import java.util.Iterator;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.items.Trade;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.BlockingResult;
import java.io.IOException;
import com.wurmonline.server.structures.Blocking;
import javax.annotation.Nullable;
import com.wurmonline.server.Players;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.PlonkData;
import java.util.ListIterator;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.behaviours.Vehicle;
import java.util.List;
import com.wurmonline.server.modifiers.DoubleValueModifier;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import com.wurmonline.shared.constants.Enchants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.modifiers.ValueModifiedListener;
import com.wurmonline.server.modifiers.ModifierTypes;
import com.wurmonline.shared.util.MovementChecker;

public final class MovementScheme extends MovementChecker implements ModifierTypes, ValueModifiedListener, ProtoConstants, CounterTypes, TimeConstants, Enchants
{
    private final Creature creature;
    boolean halted;
    private boolean encumbered;
    public Item draggedItem;
    private static final Logger logger;
    private Set<DoubleValueModifier> modifiers;
    private float baseModifier;
    private static final DoubleValueModifier dragMod;
    private static final DoubleValueModifier ramDragMod;
    private static final DoubleValueModifier combatMod;
    private static final DoubleValueModifier drunkMod;
    private static final DoubleValueModifier mooreMod;
    private static final DoubleValueModifier farwalkerMod;
    private boolean webArmoured;
    private boolean hasSpiritSpeed;
    private final DoubleValueModifier webArmourMod;
    private boolean justWebSlowArmour;
    private static final DoubleValueModifier chargeMod;
    DoubleValueModifier stealthMod;
    private static final DoubleValueModifier freezeMod;
    private static final long NOID = -10L;
    public final DoubleValueModifier armourMod;
    private final List<Float> movementSpeeds;
    private final List<Byte> windImpacts;
    private final List<Short> mountSpeeds;
    private final Set<Integer> intraports;
    public int samePosCounts;
    private static Vehicle vehic;
    private static Creature cretVehicle;
    public static Item itemVehicle;
    private static Player passenger;
    private int climbSkill;
    Map<Long, Float> oldmoves;
    private int changedTileCounter;
    private int errors;
    private boolean hasWetFeet;
    private boolean outAtSea;
    private boolean m300m;
    private boolean m700m;
    private boolean m1400m;
    private boolean m2180m;
    static Creature toRemove;
    
    MovementScheme(final Creature _creature) {
        this.halted = false;
        this.encumbered = false;
        this.baseModifier = 1.0f;
        this.webArmoured = false;
        this.hasSpiritSpeed = false;
        this.webArmourMod = new DoubleValueModifier(7, 0.0);
        this.justWebSlowArmour = false;
        this.armourMod = new DoubleValueModifier(0.0);
        this.intraports = new HashSet<Integer>();
        this.samePosCounts = 0;
        this.climbSkill = 10;
        this.oldmoves = new HashMap<Long, Float>();
        this.changedTileCounter = 0;
        this.errors = 0;
        this.hasWetFeet = false;
        this.outAtSea = false;
        this.m300m = false;
        this.m700m = false;
        this.m1400m = false;
        this.m2180m = false;
        this.creature = _creature;
        this.movementSpeeds = new ArrayList<Float>();
        this.windImpacts = new ArrayList<Byte>();
        this.mountSpeeds = new ArrayList<Short>();
        if (!this.creature.isPlayer()) {
            this.onGround = true;
        }
        else {
            this.halted = true;
        }
        this.setLog(true);
    }
    
    public void initalizeModifiersWithTemplate() {
        this.addModifier(this.armourMod);
        this.armourMod.addListener(this);
    }
    
    @Override
    public float getTileSteepness(final int tilex, final int tiley, final int clayer) {
        if (this.creature != null && this.creature.getBridgeId() > 0L) {
            return 0.0f;
        }
        float highest = -100.0f;
        float lowest = 32000.0f;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                if (tilex + x < Zones.worldTileSizeX && tiley + y < Zones.worldTileSizeY) {
                    if (clayer >= 0) {
                        final float height = Tiles.decodeHeightAsFloat(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                        if (height > highest) {
                            highest = height;
                        }
                        if (height < lowest) {
                            lowest = height;
                        }
                    }
                    else {
                        final float height = Tiles.decodeHeightAsFloat(Server.caveMesh.getTile(tilex + x, tiley + y));
                        if (height > highest) {
                            highest = height;
                        }
                        if (height < lowest) {
                            lowest = height;
                        }
                    }
                }
            }
        }
        return highest - lowest;
    }
    
    @Override
    protected byte getTextureForTile(final int xTile, final int yTile, final int layer, final long bridgeId) {
        if (bridgeId > 0L) {
            final VolaTile vt = Zones.getTileOrNull(xTile, yTile, layer == 0);
            if (vt != null) {
                final BridgePart[] bridgeParts = vt.getBridgeParts();
                final int length = bridgeParts.length;
                int i = 0;
                while (i < length) {
                    final BridgePart bp = bridgeParts[i];
                    if (bp.getStructureId() == bridgeId) {
                        if (bp.getMaterial() == BridgeConstants.BridgeMaterial.WOOD || bp.getMaterial() == BridgeConstants.BridgeMaterial.ROPE) {
                            return Tiles.Tile.TILE_PLANKS.id;
                        }
                        if (bp.getMaterial() == BridgeConstants.BridgeMaterial.BRICK) {
                            return Tiles.Tile.TILE_COBBLESTONE.id;
                        }
                        return Tiles.Tile.TILE_STONE_SLABS.id;
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        MeshIO mesh = Server.surfaceMesh;
        if (layer < 0) {
            if (xTile < 0 || xTile > Zones.worldTileSizeX || yTile < 0 || yTile > Zones.worldTileSizeY) {
                return Tiles.Tile.TILE_ROCK.id;
            }
            mesh = Server.caveMesh;
        }
        else {
            if (this.creature.hasOpenedMineDoor(xTile, yTile)) {
                return Tiles.Tile.TILE_HOLE.id;
            }
            if (xTile < 0 || xTile > Zones.worldTileSizeX || yTile < 0 || yTile > Zones.worldTileSizeY) {
                return Tiles.Tile.TILE_DIRT.id;
            }
        }
        return Tiles.decodeType(mesh.getTile(xTile, yTile));
    }
    
    @Override
    public final float getHeightOfBridge(final int xTile, final int yTile, final int layer) {
        final VolaTile vt = Zones.getTileOrNull(xTile, yTile, layer == 0);
        if (vt != null) {
            final BridgePart[] bridgeParts = vt.getBridgeParts();
            final int length = bridgeParts.length;
            final int n = 0;
            if (n < length) {
                final BridgePart bp = bridgeParts[n];
                return bp.getHeight() / 10.0f;
            }
        }
        return -1000.0f;
    }
    
    public boolean isIntraTeleporting() {
        return !this.intraports.isEmpty();
    }
    
    public void addIntraTeleport(final int teleportNumber) {
        this.intraports.add(teleportNumber);
    }
    
    public boolean removeIntraTeleport(final int teleportNumber) {
        this.intraports.remove(teleportNumber);
        return this.intraports.isEmpty();
    }
    
    public void clearIntraports() {
        this.intraports.clear();
    }
    
    @Override
    protected float getCeilingForNode(final int xTile, final int yTile) {
        return Tiles.decodeData(Server.caveMesh.getTile(xTile, yTile) & 0xFF) / 10.0f;
    }
    
    public boolean removeWindMod(final byte impact) {
        final ListIterator<Byte> it = this.windImpacts.listIterator();
        while (it.hasNext()) {
            final Byte b = it.next();
            it.remove();
            if (b == impact) {
                if (this.creature.isPlayer()) {
                    ((Player)this.creature).sentWind = 0L;
                }
                return true;
            }
        }
        return false;
    }
    
    public void setWindMod(final byte impact) {
        this.setWindImpact(impact / 200.0f);
    }
    
    public boolean addWindImpact(final byte impact) {
        if (this.windImpacts.isEmpty() || this.windImpacts.get(this.windImpacts.size() - 1) != impact) {
            this.windImpacts.add(impact);
            this.creature.getCommunicator().sendWindImpact(impact);
            return true;
        }
        return false;
    }
    
    public void resendMountSpeed() {
        if (this.mountSpeeds.size() > 0) {
            this.creature.getCommunicator().sendMountSpeed(this.mountSpeeds.get(0));
        }
    }
    
    public boolean removeMountSpeed(final short speed) {
        final ListIterator<Short> it = this.mountSpeeds.listIterator();
        while (it.hasNext()) {
            final Short b = it.next();
            it.remove();
            if (b == speed) {
                if (this.creature.isPlayer()) {
                    ((Player)this.creature).sentMountSpeed = 0L;
                }
                return true;
            }
        }
        return false;
    }
    
    public void setMountSpeed(final short newMountSpeed) {
        if (this.commandingBoat) {
            this.setMountSpeed(newMountSpeed / 1000.0f);
        }
        else {
            this.setMountSpeed(newMountSpeed / 200.0f);
        }
    }
    
    public boolean addMountSpeed(final short speed) {
        if (this.mountSpeeds.isEmpty() || this.mountSpeeds.get(this.mountSpeeds.size() - 1) != speed) {
            this.mountSpeeds.add(speed);
            this.creature.getCommunicator().sendMountSpeed(speed);
            return true;
        }
        return false;
    }
    
    boolean removeSpeedMod(final float speedmod) {
        final ListIterator<Float> it = this.movementSpeeds.listIterator();
        while (it.hasNext()) {
            final Float f = it.next();
            it.remove();
            if (f == speedmod) {
                return true;
            }
        }
        return false;
    }
    
    public void sendSpeedModifier() {
        if (this.addSpeedMod(Math.max(0.0f, this.getSpeedModifier()))) {
            this.creature.getCommunicator().sendSpeedModifier(Math.max(0.0f, this.getSpeedModifier()));
        }
    }
    
    private boolean addSpeedMod(final float speedmod) {
        this.oldmoves.put(new Long(System.currentTimeMillis()), new Float(speedmod));
        if (this.oldmoves.size() > 20) {
            final Long[] longs = this.oldmoves.keySet().toArray(new Long[this.oldmoves.size()]);
            for (int x = 0; x < 10; ++x) {
                this.oldmoves.remove(longs[x]);
            }
        }
        if (this.movementSpeeds.isEmpty() || this.movementSpeeds.get(this.movementSpeeds.size() - 1) != speedmod) {
            this.movementSpeeds.add(speedmod);
            return true;
        }
        return false;
    }
    
    public void hitGround(final float speed) {
        if (this.creature instanceof Player && !this.creature.isDead() && this.creature.isOnSurface() && this.creature.getVisionArea() != null && this.creature.getVisionArea().isInitialized() && !this.creature.getCommunicator().isInvulnerable() && !this.creature.getCommunicator().stillLoggingIn() && this.creature.getFarwalkerSeconds() <= 0 && (this.creature.getPower() < 1 || this.creature.loggerCreature1 > 0L) && speed > 0.5f) {
            if (this.creature.getLayer() >= 0) {
                final float distFromBorder = 0.09f;
                final float tilex = (int)this.getX() / 4;
                final float tiley = (int)this.getY() / 4;
                if (this.getX() / 4.0f - tilex < distFromBorder) {
                    final VolaTile current = Zones.getTileOrNull((int)tilex, (int)tiley, true);
                    if (current != null) {
                        final Wall[] walls2;
                        final Wall[] walls = walls2 = current.getWalls();
                        for (final Wall w : walls2) {
                            if (w.getFloorLevel() == this.creature.getFloorLevel() && w.getTileX() == tilex && !w.isHorizontal()) {
                                return;
                            }
                        }
                        final Fence[] fences2;
                        final Fence[] fences = fences2 = current.getFences();
                        for (final Fence f : fences2) {
                            if (f.getTileX() == tilex && !f.isHorizontal() && f.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                    }
                }
                if (this.getY() / 4.0f - tiley < distFromBorder) {
                    final VolaTile current = Zones.getTileOrNull((int)tilex, (int)tiley, true);
                    if (current != null) {
                        final Wall[] walls3;
                        final Wall[] walls = walls3 = current.getWalls();
                        for (final Wall w : walls3) {
                            if (w.getTileY() == tiley && w.isHorizontal() && w.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                        final Fence[] fences3;
                        final Fence[] fences = fences3 = current.getFences();
                        for (final Fence f : fences3) {
                            if (f.getTileY() == tiley && f.isHorizontal() && f.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                    }
                }
                if (tilex + 1.0f - this.getX() / 4.0f < distFromBorder) {
                    final VolaTile current = Zones.getTileOrNull((int)tilex + 1, (int)tiley, true);
                    if (current != null) {
                        final Wall[] walls4;
                        final Wall[] walls = walls4 = current.getWalls();
                        for (final Wall w : walls4) {
                            if (w.getTileX() == tilex + 1.0f && !w.isHorizontal() && w.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                        final Fence[] fences4;
                        final Fence[] fences = fences4 = current.getFences();
                        for (final Fence f : fences4) {
                            if (f.getTileX() == tilex + 1.0f && !f.isHorizontal() && f.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                    }
                }
                if (tiley + 1.0f - this.getY() / 4.0f < distFromBorder) {
                    final VolaTile current = Zones.getTileOrNull((int)tilex, (int)tiley + 1, true);
                    if (current != null) {
                        final Wall[] walls5;
                        final Wall[] walls = walls5 = current.getWalls();
                        for (final Wall w : walls5) {
                            if (w.getTileY() == tiley + 1.0f && w.isHorizontal() && w.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                        final Fence[] fences5;
                        final Fence[] fences = fences5 = current.getFences();
                        for (final Fence f : fences5) {
                            if (f.getTileY() == tiley + 1.0f && f.isHorizontal() && f.getFloorLevel() == this.creature.getFloorLevel()) {
                                return;
                            }
                        }
                    }
                }
            }
            final float baseDam = 1.0f + speed;
            try {
                final float damMod = 20.0f;
                float dam = baseDam * baseDam * baseDam * 24.0f * 60.0f * 20.0f / 15.0f;
                dam = Math.max(dam, 300.0f);
                this.creature.getCommunicator().sendNormalServerMessage("Ouch! That hurt!");
                this.creature.sendToLoggers("Speed=" + speed + ", baseDam=" + baseDam + " damMod=" + 20.0f + " weightCarried=" + this.creature.getCarriedWeight() + " dam=" + dam);
                this.creature.achievement(88);
                if (!PlonkData.FALL_DAMAGE.hasSeenThis(this.creature)) {
                    PlonkData.FALL_DAMAGE.trigger(this.creature);
                }
                this.creature.addWoundOfType(null, (byte)0, 1, true, 1.0f, false, dam, 0.0f, 0.0f, false, false);
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    protected float getHeightForNode(final int xNode, final int yNode, final int layer) {
        return Zones.getHeightForNode(xNode, yNode, layer);
    }
    
    @Override
    protected float[] getNodeHeights(final int xNode, final int yNode, final int layer, final long bridgeId) {
        return Zones.getNodeHeights(xNode, yNode, layer, bridgeId);
    }
    
    @Override
    protected boolean handleWrongLayer(final int clientInputLayer, final int expectedLayer) {
        if (this.creature.getVehicle() != -10L) {
            return false;
        }
        if (this.creature.getPower() >= 2 && Tiles.decodeType(Server.caveMesh.getTile(this.creature.getTileX(), this.creature.getTileY())) != Tiles.Tile.TILE_CAVE_EXIT.id) {
            this.creature.getCommunicator().sendAlertServerMessage("You were detected to be on a different layer from what is shown in your client, setting layer to the one in your client.");
        }
        return true;
    }
    
    @Override
    protected void handlePlayerInRock() {
        if (!this.creature.isDead()) {
            final int tilex = this.creature.getTileX();
            final int tiley = this.creature.getTileY();
            if (Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)) != Tiles.Tile.TILE_CAVE.id && Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)) != Tiles.Tile.TILE_CAVE_EXIT.id) {
                if (this.creature.getVehicle() == -10L) {
                    for (int x = -1; x <= 1; ++x) {
                        for (int y = -1; y <= 1; ++y) {
                            final byte type = Tiles.decodeType(Server.caveMesh.getTile(Zones.safeTileX(x + tilex), Zones.safeTileY(y + tiley)));
                            final Tiles.Tile tempTile = Tiles.getTile(type);
                            if (type == Tiles.Tile.TILE_CAVE_EXIT.id) {
                                this.creature.setTeleportPoints((short)Zones.safeTileX(x + tilex), (short)Zones.safeTileY(y + tiley), 0, 0);
                                this.creature.startTeleporting();
                                this.creature.getCommunicator().sendTeleport(false, false, (byte)0);
                                return;
                            }
                            if (!tempTile.isSolidCave() && tempTile != null) {
                                this.creature.setTeleportPoints((short)Zones.safeTileX(x + tilex), (short)Zones.safeTileY(y + tiley), -1, 0);
                                this.creature.startTeleporting();
                                this.creature.getCommunicator().sendTeleport(false, false, (byte)0);
                                return;
                            }
                        }
                    }
                    this.creature.getCommunicator().sendAlertServerMessage("You manage to become stuck in the rock, and quickly suffocate.");
                    this.creature.die(false, "Suffocated in Rock (2)");
                }
            }
            else {
                ((Player)this.creature).intraTeleport(this.creature.getPosX(), this.creature.getPosY(), this.creature.getPositionZ(), this.creature.getStatus().getRotation(), this.creature.getLayer(), "in rock commanding=" + this.creature.isVehicleCommander() + " height=" + this.creature.getPositionZ());
                if (this.creature.isVehicleCommander()) {
                    final Vehicle creatureVehicle = Vehicles.getVehicleForId(this.creature.getVehicle());
                    if (creatureVehicle != null) {
                        if (!creatureVehicle.isCreature()) {
                            try {
                                final Item ivehicle = Items.getItem(this.creature.getVehicle());
                                if (!ivehicle.isOnSurface()) {
                                    MovementScheme.itemVehicle.newLayer = (byte)this.creature.getLayer();
                                    try {
                                        final Zone z1 = Zones.getZone(ivehicle.getTileX(), ivehicle.getTileY(), false);
                                        z1.removeItem(ivehicle);
                                        ivehicle.setPosXY(this.creature.getPosX(), this.creature.getPosY());
                                        final Zone z2 = Zones.getZone(ivehicle.getTileX(), ivehicle.getTileY(), false);
                                        z2.addItem(ivehicle);
                                    }
                                    catch (NoSuchZoneException ex) {}
                                    MovementScheme.itemVehicle.newLayer = -128;
                                }
                            }
                            catch (NoSuchItemException ex2) {}
                        }
                        else {
                            try {
                                final Creature cvehicle = Creatures.getInstance().getCreature(this.creature.getVehicle());
                                if (!cvehicle.isOnSurface()) {
                                    try {
                                        final Zone z1 = Zones.getZone(cvehicle.getTileX(), cvehicle.getTileY(), false);
                                        z1.removeCreature(cvehicle, true, false);
                                        cvehicle.setPositionX(this.creature.getPosX());
                                        cvehicle.setPositionY(this.creature.getPosY());
                                        final Zone z2 = Zones.getZone(cvehicle.getTileX(), cvehicle.getTileY(), false);
                                        z2.addCreature(cvehicle.getWurmId());
                                    }
                                    catch (NoSuchZoneException ex3) {}
                                    catch (NoSuchPlayerException ex4) {}
                                }
                            }
                            catch (NoSuchCreatureException ex5) {}
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected void setLayer(final int layer) {
        this.creature.setLayer(layer, false);
    }
    
    @Override
    protected boolean handleMoveTooFar(final float clientInput, final float expectedDistance) {
        if (!this.creature.isDead() && (this.creature.getPower() < 1 || this.creature.loggerCreature1 != -10L) && clientInput - expectedDistance * 1.1f > 0.0f && this.changedTileCounter == 0) {
            this.setErrors(this.getErrors() + 1);
            if (this.getErrors() > 4) {
                this.creature.getStatus().setNormalRegen(false);
                MovementScheme.logger.log(Level.WARNING, this.creature.getName() + " TOO FAR, input=" + clientInput + ", expected=" + expectedDistance + " :  " + this.getCurrx() + "(" + this.getXold() + "); " + this.getCurry() + "(" + this.getYold() + ") bridge=" + this.creature.getBridgeId());
                ((Player)this.creature).intraTeleport(this.creature.getPosX(), this.creature.getPosY(), this.creature.getPositionZ(), this.creature.getStatus().getRotation(), this.creature.getLayer(), "Moved too far");
                this.setAbort(true);
                if (this.getErrors() > 10) {
                    Players.getInstance().sendGlobalGMMessage(this.creature, " movement too far (" + (clientInput - expectedDistance) + ") at " + this.getCurrx() / 4.0f + "," + this.getCurry() / 4.0f);
                }
                return true;
            }
        }
        return false;
    }
    
    public void setServerClimbing(final boolean climb) {
        this.setClimbing(climb);
    }
    
    @Override
    protected boolean handleMoveTooShort(final float clientInput, final float expectedDistance) {
        if (this.creature.isTeleporting() || this.isIntraTeleporting() || this.creature.isDead() || (!this.creature.getCommunicator().hasReceivedTicks() && this.isClimbing()) || this.creature.getPower() >= 2 || this.changedTileCounter != 0 || this.creature.getCurrentTile() != null) {}
        return false;
    }
    
    @Override
    public final boolean movedOnStair() {
        final VolaTile t = this.creature.getCurrentTile();
        if (t != null && (t.hasStair(this.creature.getFloorLevel() + 1) || t.hasStair(this.creature.getFloorLevel()))) {
            this.setBridgeCounter(10);
            this.wasOnStair = true;
        }
        return this.wasOnStair;
    }
    
    @Override
    protected boolean handleZError(final float clientInput, final float expectedPosition) {
        if (this.changedTileCounter == 0 && this.creature.getPower() < 2 && !this.creature.isTeleporting() && !this.creature.isDead() && (this.creature.getCommunicator().hasReceivedTicks() || !this.isClimbing())) {
            if (this.getTargetGroundOffset() != this.getGroundOffset()) {
                return false;
            }
            if (this.creature.getVisionArea() != null && this.creature.getVisionArea().isInitialized()) {
                this.setErrors(this.getErrors() + 1);
                if (this.getErrors() > 4) {
                    ((Player)this.creature).intraTeleport(this.creature.getPosX(), this.creature.getPosY(), this.creature.getPositionZ(), this.creature.getStatus().getRotation(), this.creature.getLayer(), "Error in z=" + clientInput + ", expected=" + expectedPosition);
                    this.setAbort(true);
                    this.setErrors(this.getErrors() + 1);
                    if (this.getErrors() > 10) {
                        Players.getInstance().sendGlobalGMMessage(this.creature, " movement too high (" + (clientInput - expectedPosition) + ") at " + this.getCurrx() / 4.0f + "," + this.getCurry() / 4.0f);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void movePassengers(final Vehicle aVehicle, @Nullable final Creature driver, final boolean isCreature) {
        if (isCreature) {
            for (int x = 0; x < aVehicle.seats.length; ++x) {
                if (aVehicle.seats[x].type == 1 && aVehicle.seats[x].isOccupied()) {
                    try {
                        MovementScheme.passenger = Players.getInstance().getPlayer(aVehicle.seats[x].occupant);
                        final float r = (-MovementScheme.cretVehicle.getStatus().getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -aVehicle.seats[x].offx - c * -aVehicle.seats[x].offy;
                        final float yo = c * -aVehicle.seats[x].offx + s * -aVehicle.seats[x].offy;
                        float newposx = MovementScheme.cretVehicle.getPosX() + xo;
                        float newposy = MovementScheme.cretVehicle.getPosY() + yo;
                        newposx = Math.max(3.0f, newposx);
                        newposx = Math.min(Zones.worldMeterSizeX - 3.0f, newposx);
                        newposy = Math.max(3.0f, newposy);
                        newposy = Math.min(Zones.worldMeterSizeY - 3.0f, newposy);
                        int diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                        int diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                        boolean move = true;
                        if (diffy != 0 || diffx != 0) {
                            final BlockingResult result = Blocking.getBlockerBetween(MovementScheme.passenger, MovementScheme.passenger.getStatus().getPositionX(), MovementScheme.passenger.getStatus().getPositionY(), newposx, newposy, MovementScheme.passenger.getPositionZ(), MovementScheme.passenger.getPositionZ(), MovementScheme.passenger.isOnSurface(), MovementScheme.passenger.isOnSurface(), false, 6, -1L, MovementScheme.passenger.getBridgeId(), MovementScheme.passenger.getBridgeId(), MovementScheme.cretVehicle.followsGround());
                            if (result != null) {
                                final Blocker first = result.getFirstBlocker();
                                if ((!first.isDoor() || (!first.canBeOpenedBy(MovementScheme.passenger, false) && driver != null && !first.canBeOpenedBy(driver, false))) && !(first instanceof BridgePart)) {
                                    if (driver != null) {
                                        newposx = driver.getPosX();
                                        newposy = driver.getPosY();
                                        diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                                        diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                                    }
                                    else {
                                        move = false;
                                        MovementScheme.passenger.disembark(false);
                                    }
                                }
                            }
                            if (move && MovementScheme.passenger.getLayer() < 0 && (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newposx >> 2, (int)newposy >> 2))) || Blocking.isDiagonalRockBetween(MovementScheme.passenger, MovementScheme.passenger.getTileX(), MovementScheme.passenger.getTileY(), (int)newposx >> 2, (int)newposy >> 2) != null) && driver != null) {
                                newposx = driver.getPosX();
                                newposy = driver.getPosY();
                                diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                                diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                            }
                            if (MovementScheme.passenger.getStatus().isTrading()) {
                                final Trade trade = MovementScheme.passenger.getStatus().getTrade();
                                Creature lOpponent = null;
                                if (trade.creatureOne == MovementScheme.passenger) {
                                    lOpponent = trade.creatureTwo;
                                }
                                else {
                                    lOpponent = trade.creatureOne;
                                }
                                if (Creature.rangeTo(MovementScheme.passenger, lOpponent) > 6) {
                                    trade.end(MovementScheme.passenger, false);
                                }
                            }
                        }
                        Label_0976: {
                            if (move) {
                                MovementScheme.passenger.getStatus().setPositionXYZ(newposx, newposy, MovementScheme.cretVehicle.getPositionZ() + aVehicle.seats[x].offz);
                                MovementScheme.passenger.getMovementScheme().setPosition(MovementScheme.passenger.getStatus().getPositionX(), MovementScheme.passenger.getStatus().getPositionY(), MovementScheme.passenger.getStatus().getPositionZ(), MovementScheme.passenger.getStatus().getRotation(), MovementScheme.passenger.getLayer());
                                Label_0899: {
                                    if (diffy == 0) {
                                        if (diffx == 0) {
                                            break Label_0899;
                                        }
                                    }
                                    try {
                                        if (MovementScheme.passenger.hasLink() && MovementScheme.passenger.getVisionArea() != null) {
                                            MovementScheme.passenger.getVisionArea().move(diffx, diffy);
                                            MovementScheme.passenger.getVisionArea().linkZones(diffy, diffx);
                                        }
                                        final Zone z = Zones.getZone(MovementScheme.passenger.getTileX(), MovementScheme.passenger.getTileY(), MovementScheme.passenger.isOnSurface());
                                        MovementScheme.passenger.getStatus().savePosition(MovementScheme.passenger.getWurmId(), true, z.getId(), false);
                                    }
                                    catch (IOException iox) {
                                        MovementScheme.logger.log(Level.WARNING, iox.getMessage(), iox);
                                        MovementScheme.passenger.setLink(false);
                                    }
                                    catch (NoSuchZoneException nsz) {
                                        MovementScheme.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                                        MovementScheme.passenger.setLink(false);
                                    }
                                    catch (Exception ex) {}
                                }
                                diffx = ((int)newposx >> 2) - MovementScheme.passenger.getCurrentTile().tilex;
                                diffy = ((int)newposy >> 2) - MovementScheme.passenger.getCurrentTile().tiley;
                                if (diffy == 0) {
                                    if (diffx == 0) {
                                        break Label_0976;
                                    }
                                }
                                try {
                                    MovementScheme.passenger.getCurrentTile().creatureMoved(MovementScheme.passenger.getWurmId(), 0.0f, 0.0f, 0.0f, diffx, diffy, true);
                                }
                                catch (NoSuchPlayerException ex2) {}
                                catch (NoSuchCreatureException ex3) {}
                            }
                        }
                    }
                    catch (NoSuchPlayerException ex4) {}
                }
            }
        }
        else {
            for (int x = 0; x < aVehicle.seats.length; ++x) {
                if (aVehicle.seats[x].type == 1 && aVehicle.seats[x].isOccupied()) {
                    try {
                        MovementScheme.passenger = Players.getInstance().getPlayer(aVehicle.seats[x].occupant);
                        final float r = (-MovementScheme.itemVehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -aVehicle.seats[x].offx - c * -aVehicle.seats[x].offy;
                        final float yo = c * -aVehicle.seats[x].offx + s * -aVehicle.seats[x].offy;
                        float newposx = MovementScheme.itemVehicle.getPosX() + xo;
                        float newposy = MovementScheme.itemVehicle.getPosY() + yo;
                        newposx = Math.max(3.0f, newposx);
                        newposx = Math.min(Zones.worldMeterSizeX - 3.0f, newposx);
                        newposy = Math.max(3.0f, newposy);
                        newposy = Math.min(Zones.worldMeterSizeY - 3.0f, newposy);
                        int diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                        int diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                        boolean move = true;
                        if (diffy != 0 || diffx != 0) {
                            if (MovementScheme.passenger.isOnSurface()) {
                                final BlockingResult result = Blocking.getBlockerBetween(MovementScheme.passenger, MovementScheme.passenger.getStatus().getPositionX(), MovementScheme.passenger.getStatus().getPositionY(), newposx, newposy, MovementScheme.passenger.getPositionZ(), MovementScheme.passenger.getPositionZ(), MovementScheme.passenger.isOnSurface(), MovementScheme.passenger.isOnSurface(), false, 6, -1L, MovementScheme.passenger.getBridgeId(), MovementScheme.passenger.getBridgeId(), MovementScheme.itemVehicle.getFloorLevel() == 0 && MovementScheme.itemVehicle.getBridgeId() <= 0L);
                                if (result != null) {
                                    final Blocker first = result.getFirstBlocker();
                                    if ((!first.isDoor() || (!first.canBeOpenedBy(MovementScheme.passenger, false) && driver != null && !first.canBeOpenedBy(driver, false))) && !(first instanceof BridgePart)) {
                                        if (driver != null) {
                                            newposx = driver.getPosX();
                                            newposy = driver.getPosY();
                                            diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                                            diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                                        }
                                        else {
                                            move = false;
                                            MovementScheme.passenger.disembark(false);
                                        }
                                    }
                                }
                            }
                            if (move && MovementScheme.passenger.getLayer() < 0 && (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newposx >> 2, (int)newposy >> 2))) || Blocking.isDiagonalRockBetween(MovementScheme.passenger, MovementScheme.passenger.getTileX(), MovementScheme.passenger.getTileY(), (int)newposx >> 2, (int)newposy >> 2) != null) && driver != null) {
                                newposx = driver.getPosX();
                                newposy = driver.getPosY();
                                diffx = ((int)newposx >> 2) - MovementScheme.passenger.getTileX();
                                diffy = ((int)newposy >> 2) - MovementScheme.passenger.getTileY();
                            }
                            if (MovementScheme.passenger.getStatus().isTrading()) {
                                final Trade trade2 = MovementScheme.passenger.getStatus().getTrade();
                                Creature lOpponent2 = null;
                                if (trade2.creatureOne == MovementScheme.passenger) {
                                    lOpponent2 = trade2.creatureTwo;
                                }
                                else {
                                    lOpponent2 = trade2.creatureOne;
                                }
                                if (Creature.rangeTo(MovementScheme.passenger, lOpponent2) > 6) {
                                    trade2.end(MovementScheme.passenger, false);
                                }
                            }
                        }
                        Label_1955: {
                            if (move) {
                                MovementScheme.passenger.getStatus().setPositionXYZ(newposx, newposy, MovementScheme.itemVehicle.getPosZ() + aVehicle.seats[x].offz);
                                MovementScheme.passenger.getMovementScheme().setPosition(MovementScheme.passenger.getStatus().getPositionX(), MovementScheme.passenger.getStatus().getPositionY(), MovementScheme.passenger.getStatus().getPositionZ(), MovementScheme.passenger.getStatus().getRotation(), MovementScheme.passenger.getLayer());
                                Label_1878: {
                                    if (diffy == 0) {
                                        if (diffx == 0) {
                                            break Label_1878;
                                        }
                                    }
                                    try {
                                        if (MovementScheme.passenger.hasLink() && MovementScheme.passenger.getVisionArea() != null) {
                                            MovementScheme.passenger.getVisionArea().move(diffx, diffy);
                                            MovementScheme.passenger.getVisionArea().linkZones(diffy, diffx);
                                        }
                                        final Zone z = Zones.getZone(MovementScheme.passenger.getTileX(), MovementScheme.passenger.getTileY(), MovementScheme.passenger.isOnSurface());
                                        MovementScheme.passenger.getStatus().savePosition(MovementScheme.passenger.getWurmId(), true, z.getId(), false);
                                    }
                                    catch (IOException iox) {
                                        MovementScheme.passenger.setLink(false);
                                    }
                                    catch (NoSuchZoneException nsz) {
                                        MovementScheme.passenger.setLink(false);
                                    }
                                    catch (Exception ex5) {}
                                }
                                diffx = ((int)newposx >> 2) - MovementScheme.passenger.getCurrentTile().tilex;
                                diffy = ((int)newposy >> 2) - MovementScheme.passenger.getCurrentTile().tiley;
                                if (diffy == 0) {
                                    if (diffx == 0) {
                                        break Label_1955;
                                    }
                                }
                                try {
                                    MovementScheme.passenger.getCurrentTile().creatureMoved(MovementScheme.passenger.getWurmId(), 0.0f, 0.0f, 0.0f, diffx, diffy, true);
                                }
                                catch (NoSuchPlayerException ex6) {}
                                catch (NoSuchCreatureException ex7) {}
                            }
                        }
                    }
                    catch (NoSuchPlayerException ex8) {}
                }
            }
        }
    }
    
    public void moveVehicle(float diffX, float diffY, final float diffZ) {
        if (MovementScheme.vehic.isChair()) {
            return;
        }
        if (MovementScheme.vehic.creature) {
            try {
                MovementScheme.cretVehicle = Creatures.getInstance().getCreature(MovementScheme.vehic.wurmid);
                if (this.creature.isOnSurface() == MovementScheme.cretVehicle.isOnSurface()) {
                    final VolaTile t = Zones.getTileOrNull(MovementScheme.cretVehicle.getTileX(), MovementScheme.cretVehicle.getTileY(), MovementScheme.cretVehicle.isOnSurface());
                    if (t == null) {
                        try {
                            final Zone z = Zones.getZone(MovementScheme.cretVehicle.getTileX(), MovementScheme.cretVehicle.getTileY(), MovementScheme.cretVehicle.isOnSurface());
                            z.removeCreature(MovementScheme.cretVehicle, false, false);
                            z.addCreature(MovementScheme.vehic.wurmid);
                        }
                        catch (NoSuchZoneException ex) {}
                        this.creature.disembark(true);
                        return;
                    }
                    final Seat driverseat = MovementScheme.vehic.getPilotSeat();
                    final float diffrot = Creature.normalizeAngle(this.getVehicleRotation()) - MovementScheme.cretVehicle.getStatus().getRotation();
                    MovementScheme.cretVehicle.setRotation(Creature.normalizeAngle(this.getVehicleRotation()));
                    final float _r = (-MovementScheme.cretVehicle.getStatus().getRotation() + 180.0f) * 3.1415927f / 180.0f;
                    final float _s = (float)Math.sin(_r);
                    final float _c = (float)Math.cos(_r);
                    final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
                    final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
                    float nPosX = this.creature.getPosX() - xo;
                    float nPosY = this.creature.getPosY() - yo;
                    final float nPosZ = this.creature.getPositionZ() - driverseat.offz;
                    nPosX = Math.max(3.0f, nPosX);
                    nPosX = Math.min(Zones.worldMeterSizeX - 3.0f, nPosX);
                    nPosY = Math.max(3.0f, nPosY);
                    nPosY = Math.min(Zones.worldMeterSizeY - 3.0f, nPosY);
                    if (!MovementScheme.cretVehicle.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)nPosX >> 2, (int)nPosY >> 2))) && Tiles.decodeType(Server.caveMesh.getTile(MovementScheme.cretVehicle.getTileX(), MovementScheme.cretVehicle.getTileY())) != 201) {
                        return;
                    }
                    diffX = nPosX - MovementScheme.cretVehicle.getPosX();
                    diffY = nPosY - MovementScheme.cretVehicle.getPosY();
                    if (diffX != 0.0f || diffY != 0.0f) {
                        final int dtx = ((int)nPosX >> 2) - MovementScheme.cretVehicle.getTileX();
                        final int dty = ((int)nPosY >> 2) - MovementScheme.cretVehicle.getTileY();
                        MovementScheme.cretVehicle.setPositionX(nPosX);
                        MovementScheme.cretVehicle.setPositionY(nPosY);
                        MovementScheme.cretVehicle.setPositionZ(nPosZ);
                        try {
                            MovementScheme.cretVehicle.getVisionArea().move(dtx, dty);
                            MovementScheme.cretVehicle.getVisionArea().linkZones(dtx, dty);
                        }
                        catch (IOException ex2) {}
                        t.creatureMoved(MovementScheme.vehic.wurmid, diffX, diffY, diffZ, dtx, dty);
                    }
                    else if (diffrot != 0.0f) {
                        t.creatureMoved(MovementScheme.vehic.wurmid, 0.0f, 0.0f, diffZ, 0, 0);
                    }
                }
                else {
                    Zone zone = null;
                    try {
                        zone = Zones.getZone(MovementScheme.cretVehicle.getTileX(), MovementScheme.cretVehicle.getTileY(), MovementScheme.cretVehicle.isOnSurface());
                        zone.removeCreature(MovementScheme.cretVehicle, false, false);
                    }
                    catch (NoSuchZoneException ex3) {}
                    final Seat driverseat = MovementScheme.vehic.getPilotSeat();
                    MovementScheme.cretVehicle.setRotation(Creature.normalizeAngle(this.getVehicleRotation()));
                    final float _r2 = (-MovementScheme.cretVehicle.getStatus().getRotation() + 180.0f) * 3.1415927f / 180.0f;
                    final float _s2 = (float)Math.sin(_r2);
                    final float _c2 = (float)Math.cos(_r2);
                    final float xo2 = _s2 * -driverseat.offx - _c2 * -driverseat.offy;
                    final float yo2 = _c2 * -driverseat.offx + _s2 * -driverseat.offy;
                    float nPosX2 = MovementScheme.cretVehicle.getPosX() - xo2;
                    float nPosY2 = MovementScheme.cretVehicle.getPosY() - yo2;
                    nPosX2 = Math.max(3.0f, nPosX2);
                    nPosX2 = Math.min(Zones.worldMeterSizeX - 3.0f, nPosX2);
                    nPosY2 = Math.max(3.0f, nPosY2);
                    nPosY2 = Math.min(Zones.worldMeterSizeY - 3.0f, nPosY2);
                    MovementScheme.cretVehicle.setPositionX(nPosX2);
                    MovementScheme.cretVehicle.setPositionY(nPosY2);
                    MovementScheme.cretVehicle.setLayer(this.creature.getLayer(), false);
                    try {
                        zone = Zones.getZone(MovementScheme.cretVehicle.getTileX(), MovementScheme.cretVehicle.getTileY(), this.creature.isOnSurface());
                        zone.addCreature(MovementScheme.cretVehicle.getWurmId());
                    }
                    catch (NoSuchZoneException ex4) {}
                }
                movePassengers(MovementScheme.vehic, this.creature, true);
            }
            catch (NoSuchCreatureException ex5) {}
            catch (NoSuchPlayerException ex6) {}
        }
        else {
            try {
                MovementScheme.itemVehicle = Items.getItem(MovementScheme.vehic.wurmid);
                if (this.creature.isOnSurface() == MovementScheme.itemVehicle.isOnSurface()) {
                    this.moveItemVehicleSameLevel();
                }
                else {
                    this.moveItemVehicleOtherLevel();
                }
                movePassengers(MovementScheme.vehic, this.creature, false);
            }
            catch (NoSuchItemException ex7) {}
        }
    }
    
    public void moveItemVehicleOtherLevel() {
        final Seat driverseat = MovementScheme.vehic.getPilotSeat();
        final float _r = (-MovementScheme.itemVehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
        final float _s = (float)Math.sin(_r);
        final float _c = (float)Math.cos(_r);
        final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
        final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
        float nPosX = MovementScheme.itemVehicle.getPosX() - xo;
        float nPosY = MovementScheme.itemVehicle.getPosY() - yo;
        nPosX = Math.max(3.0f, nPosX);
        nPosX = Math.min(Zones.worldMeterSizeX - 3.0f, nPosX);
        nPosY = Math.max(3.0f, nPosY);
        nPosY = Math.min(Zones.worldMeterSizeY - 3.0f, nPosY);
        if (!this.creature.isOnSurface()) {
            final int caveTile = Server.caveMesh.getTile((int)nPosX >> 2, (int)nPosY >> 2);
            if (Tiles.isSolidCave(Tiles.decodeType(caveTile))) {
                this.moveItemVehicleSameLevel();
                return;
            }
        }
        Zone zone = null;
        MovementScheme.itemVehicle.newLayer = (byte)this.creature.getLayer();
        try {
            zone = Zones.getZone((int)MovementScheme.itemVehicle.getPosX() >> 2, (int)MovementScheme.itemVehicle.getPosY() >> 2, MovementScheme.itemVehicle.isOnSurface());
            zone.removeItem(MovementScheme.itemVehicle, true, true);
        }
        catch (NoSuchZoneException ex) {}
        MovementScheme.itemVehicle.setPosXY(nPosX, nPosY);
        try {
            zone = Zones.getZone((int)MovementScheme.itemVehicle.getPosX() >> 2, (int)MovementScheme.itemVehicle.getPosY() >> 2, MovementScheme.itemVehicle.newLayer >= 0);
            zone.addItem(MovementScheme.itemVehicle, false, false, false);
        }
        catch (NoSuchZoneException ex2) {}
        MovementScheme.itemVehicle.newLayer = -128;
        final Seat[] seats = MovementScheme.vehic.hitched;
        if (seats != null) {
            for (int x = 0; x < seats.length; ++x) {
                if (seats[x] != null && seats[x].occupant != -10L) {
                    try {
                        final Creature c = Server.getInstance().getCreature(seats[x].occupant);
                        c.getStatus().setLayer(MovementScheme.itemVehicle.isOnSurface() ? 0 : -1);
                        c.getCurrentTile().newLayer(c);
                    }
                    catch (NoSuchPlayerException ex3) {}
                    catch (NoSuchCreatureException ex4) {}
                }
            }
        }
        final Seat[] pseats = MovementScheme.vehic.seats;
        if (pseats != null) {
            for (int x2 = 0; x2 < pseats.length; ++x2) {
                if (x2 > 0 && pseats[x2] != null && pseats[x2].occupant != -10L) {
                    try {
                        final Creature c2 = Server.getInstance().getCreature(pseats[x2].occupant);
                        MovementScheme.logger.log(Level.INFO, c2.getName() + " Setting to new layer " + (MovementScheme.itemVehicle.isOnSurface() ? 0 : -1));
                        c2.getStatus().setLayer(MovementScheme.itemVehicle.isOnSurface() ? 0 : -1);
                        c2.getCurrentTile().newLayer(c2);
                        if (c2.isPlayer()) {
                            if (MovementScheme.itemVehicle.isOnSurface()) {
                                c2.getCommunicator().sendNormalServerMessage("You leave the cave.");
                            }
                            else {
                                c2.getCommunicator().sendNormalServerMessage("You enter the cave.");
                                if (c2.getVisionArea() != null) {
                                    c2.getVisionArea().initializeCaves();
                                }
                            }
                        }
                    }
                    catch (NoSuchPlayerException ex5) {}
                    catch (NoSuchCreatureException ex6) {}
                }
            }
        }
    }
    
    public void moveItemVehicleSameLevel() {
        final VolaTile t = Zones.getTileOrNull(MovementScheme.itemVehicle.getTileX(), MovementScheme.itemVehicle.getTileY(), MovementScheme.itemVehicle.isOnSurface());
        if (t == null) {
            try {
                final Zone z = Zones.getZone(MovementScheme.itemVehicle.getTileX(), MovementScheme.itemVehicle.getTileY(), MovementScheme.itemVehicle.isOnSurface());
                z.removeItem(MovementScheme.itemVehicle);
                z.addItem(MovementScheme.itemVehicle);
            }
            catch (NoSuchZoneException ex) {}
            this.creature.disembark(true);
            return;
        }
        final Seat driverseat = MovementScheme.vehic.getPilotSeat();
        if (driverseat == null) {
            MovementScheme.logger.warning("Driverseat null for " + this.creature.getName());
            this.creature.disembark(true);
            return;
        }
        final float _r = (-MovementScheme.itemVehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
        final float _s = (float)Math.sin(_r);
        final float _c = (float)Math.cos(_r);
        final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
        final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
        float nPosX = this.creature.getPosX() - xo;
        float nPosY = this.creature.getPosY() - yo;
        final float nPosZ = this.creature.getPositionZ() - driverseat.offz;
        nPosX = Math.max(3.0f, nPosX);
        nPosX = Math.min(Zones.worldMeterSizeX - 3.0f, nPosX);
        nPosY = Math.max(3.0f, nPosY);
        nPosY = Math.min(Zones.worldMeterSizeY - 3.0f, nPosY);
        final int diffdecx = (int)(nPosX * 100.0f - MovementScheme.itemVehicle.getPosX() * 100.0f);
        final int diffdecy = (int)(nPosY * 100.0f - MovementScheme.itemVehicle.getPosY() * 100.0f);
        if (diffdecx != 0 || diffdecy != 0) {
            nPosX = MovementScheme.itemVehicle.getPosX() + diffdecx * 0.01f;
            nPosY = MovementScheme.itemVehicle.getPosY() + diffdecy * 0.01f;
        }
        if (!MovementScheme.itemVehicle.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)nPosX >> 2, (int)nPosY >> 2))) && Tiles.decodeType(Server.caveMesh.getTile((int)MovementScheme.itemVehicle.getPosX() >> 2, (int)MovementScheme.itemVehicle.getPosY() >> 2)) != 201) {
            return;
        }
        t.moveItem(MovementScheme.itemVehicle, nPosX, nPosY, nPosZ, Creature.normalizeAngle(this.getVehicleRotation()), MovementScheme.itemVehicle.isOnSurface(), MovementScheme.itemVehicle.getPosZ());
        if (MovementScheme.vehic.draggers != null) {
            for (final Creature c : MovementScheme.vehic.draggers) {
                if (c.isDead()) {
                    MovementScheme.toRemove = c;
                }
                else {
                    this.moveDragger(c);
                }
            }
            if (MovementScheme.toRemove != null) {
                MovementScheme.vehic.removeDragger(MovementScheme.toRemove);
                MovementScheme.toRemove = null;
            }
        }
    }
    
    public void moveDragger(final Creature c) {
        final Vehicle v = c.getHitched();
        final Seat seat = v.getHitchSeatFor(c.getWurmId());
        final float _r = (-MovementScheme.itemVehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
        final float _s = (float)Math.sin(_r);
        final float _c = (float)Math.cos(_r);
        final float xo = _s * -seat.offx - _c * -seat.offy;
        final float yo = _c * -seat.offx + _s * -seat.offy;
        float nPosX = this.creature.getPosX() + xo;
        float nPosY = this.creature.getPosY() + yo;
        nPosX = Math.max(3.0f, nPosX);
        nPosX = Math.min(Zones.worldMeterSizeX - 3.0f, nPosX);
        nPosY = Math.max(3.0f, nPosY);
        nPosY = Math.min(Zones.worldMeterSizeY - 3.0f, nPosY);
        final int diffdecx = (int)(nPosX * 100.0f - MovementScheme.itemVehicle.getPosX() * 100.0f);
        final int diffdecy = (int)(nPosY * 100.0f - MovementScheme.itemVehicle.getPosY() * 100.0f);
        if (diffdecx != 0 || diffdecy != 0) {
            nPosX = MovementScheme.itemVehicle.getPosX() + diffdecx * 0.01f;
            nPosY = MovementScheme.itemVehicle.getPosY() + diffdecy * 0.01f;
        }
        this.moveDragger(c, nPosX, nPosY, MovementScheme.itemVehicle.getPosZ(), Creature.normalizeAngle(this.getVehicleRotation()), false);
    }
    
    public void moveDragger(final Creature c, final float nPosX, final float nPosY, final float nPosZ, final float newRot, final boolean addRemove) {
        int diffx = ((int)nPosX >> 2) - c.getCurrentTile().tilex;
        int diffy = ((int)nPosY >> 2) - c.getCurrentTile().tiley;
        if (c.getLayer() < 0 && MovementScheme.itemVehicle.isOnSurface()) {
            if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)nPosX >> 2, (int)nPosY >> 2)))) {
                c.setLayer(0, false);
            }
            else {
                final byte typeSurf = Tiles.decodeType(Server.surfaceMesh.getTile((int)nPosX >> 2, (int)nPosY >> 2));
                if (typeSurf != 0) {
                    c.setLayer(0, false);
                }
            }
        }
        c.getStatus().setPositionXYZ(nPosX, nPosY, nPosZ);
        c.getMovementScheme().setPosition(c.getStatus().getPositionX(), c.getStatus().getPositionY(), c.getStatus().getPositionZ(), newRot, c.getLayer());
        if (Math.abs(c.getStatus().getRotation() - newRot) > 10.0f) {
            c.setRotation(newRot);
            c.moved(0.0f, 0.0f, 0.0f, 0, 0);
        }
        Label_0301: {
            if (diffy == 0) {
                if (diffx == 0) {
                    break Label_0301;
                }
            }
            try {
                if (c.getVisionArea() != null) {
                    c.getVisionArea().move(diffx, diffy);
                    c.getVisionArea().linkZones(diffy, diffx);
                }
                final Zone z = Zones.getZone(c.getTileX(), c.getTileY(), c.isOnSurface());
                c.getStatus().savePosition(c.getWurmId(), c.isPlayer(), z.getId(), true);
                try {
                    c.getCurrentTile().creatureMoved(c.getWurmId(), 0.0f, 0.0f, 0.0f, diffx, diffy, true);
                }
                catch (NoSuchPlayerException ex) {}
                catch (NoSuchCreatureException ex2) {}
            }
            catch (IOException ex3) {}
            catch (NoSuchZoneException ex4) {}
        }
        if (addRemove) {
            diffx = ((int)nPosX >> 2) - MovementScheme.passenger.getCurrentTile().tilex;
            diffy = ((int)nPosY >> 2) - MovementScheme.passenger.getCurrentTile().tiley;
            if (diffy == 0) {
                if (diffx == 0) {
                    return;
                }
            }
            try {
                MovementScheme.passenger.getCurrentTile().creatureMoved(MovementScheme.passenger.getWurmId(), 0.0f, 0.0f, 0.0f, diffx, diffy, true);
            }
            catch (NoSuchPlayerException ex5) {}
            catch (NoSuchCreatureException ex6) {}
        }
    }
    
    public void move(final float diffX, final float diffY, final float diffZ) {
        MovementScheme.vehic = null;
        if (!this.isIntraTeleporting()) {
            int weight = this.creature.getCarriedWeight();
            if (this.draggedItem != null) {
                weight += this.draggedItem.getWeightGrams() / 100;
            }
            final double beforeZMod;
            double moveDiff = beforeZMod = Math.sqrt(diffX * diffX + diffY * diffY) * 15.0;
            if (this.isClimbing() && this.creature.getVehicle() == -10L && !this.creature.isUsingLastGasp()) {
                if (weight <= 10000) {
                    weight = 10000;
                }
                if (this.creature.getLayer() <= 0) {
                    final short[] steepness = Creature.getTileSteepness(this.creature.getTileX(), this.creature.getTileY(), this.creature.isOnSurface());
                    if (diffZ != 0.0f && (steepness[1] > 23 || steepness[1] < -23)) {
                        final float gsbon = this.creature.getBonusForSpellEffect((byte)38) / 20.0f;
                        if (gsbon > 0.0f) {
                            moveDiff += Math.max(1.0f, Math.abs(diffZ) * 500.0f / Math.max(1.0f, gsbon));
                        }
                        else {
                            moveDiff += Math.max(1.0f, Math.abs(diffZ) * 500.0f) / Math.max(1.0, Math.pow(this.climbSkill / 10, 0.3));
                        }
                        if (Server.rand.nextInt(this.climbSkill * 2) == 0) {
                            this.climbSkill = Math.max(10, (int)this.creature.getClimbingSkill().getKnowledge(0.0));
                            if (this.creature.getStatus().getStamina() < 10000) {
                                final int stam = (int)(101.0f - Math.max(10.0f, this.creature.getStatus().getStamina() / 100.0f));
                                if ((gsbon <= 0.0f || (this.creature.getStatus().getStamina() < 25 && Server.rand.nextInt(this.climbSkill) == 0)) && this.creature.getClimbingSkill().skillCheck(stam, 0.0, this.creature.getStatus().getStamina() < 25, Math.max(1, this.climbSkill / 20)) < 0.0) {
                                    try {
                                        this.creature.getCommunicator().sendNormalServerMessage("You need to catch your breath, and stop climbing.");
                                        this.creature.setClimbing(false);
                                        this.creature.getCommunicator().sendToggle(0, this.creature.isClimbing());
                                    }
                                    catch (IOException iox) {
                                        MovementScheme.logger.log(Level.WARNING, this.creature.getName() + ' ' + iox.getMessage(), iox);
                                    }
                                }
                            }
                            else {
                                this.creature.getClimbingSkill().skillCheck(Math.max(-this.climbSkill / 2, Math.min(this.climbSkill * 1.25f, steepness[1] - this.climbSkill * 2)), 0.0, gsbon > 0.0f, Math.max(1, this.climbSkill / 20));
                            }
                        }
                    }
                }
            }
            else if (diffZ < 0.0f) {
                moveDiff += Math.max(-moveDiff, Math.min(-1.0f, diffZ * 100.0f));
            }
            else if (diffZ != 0.0f && moveDiff > 0.0) {
                moveDiff += Math.max(0.5, diffZ * 50.0f);
            }
            if (this.creature.isStealth()) {
                if (weight <= 10000) {
                    weight = 10000;
                }
                weight += 5000;
            }
            if (diffX != 0.0f || diffY != 0.0f || diffZ != 0.0f) {
                this.creature.getStatus().setMoving(true);
                MeshIO mesh = Server.surfaceMesh;
                if (this.creature.getLayer() < 0) {
                    mesh = Server.caveMesh;
                }
                final int tile = mesh.getTile(this.creature.getCurrentTile().tilex, this.creature.getCurrentTile().tiley);
                if (this.creature.isPlayer()) {
                    final short height = Tiles.decodeHeight(tile);
                    if (height > 21800) {
                        if (!this.m2180m) {
                            this.m2180m = true;
                            this.creature.achievement(81);
                        }
                    }
                    else if (height > 14000) {
                        if (!this.m1400m) {
                            this.m1400m = true;
                            this.creature.achievement(80);
                        }
                    }
                    else if (height > 7000) {
                        if (!this.m700m) {
                            this.m700m = true;
                            this.creature.achievement(79);
                        }
                    }
                    else if (height > 3000) {
                        if (!this.m300m) {
                            this.m300m = true;
                            this.creature.achievement(78);
                        }
                    }
                    else if (height < -300 && !this.outAtSea) {
                        this.outAtSea = true;
                        this.creature.achievement(77);
                    }
                }
                if (this.creature.isStealth() && Terraforming.isRoad(Tiles.decodeType(tile)) && Server.rand.nextInt(20) == 0) {
                    this.creature.setStealth(false);
                }
                final VolaTile vtile = Zones.getTileOrNull(this.creature.getCurrentTile().tilex, this.creature.getCurrentTile().tiley, this.creature.isOnSurface());
                if (vtile != null && (this.creature.getPower() < 2 || this.creature.loggerCreature1 > 0L) && vtile.hasOnePerTileItem(this.creature.getFloorLevel()) && Servers.localServer.PVPSERVER) {
                    final Item barrier = vtile.getOnePerTileItem(this.creature.getFloorLevel());
                    if (barrier != null && barrier.getTemplateId() == 938 && this.creature.getFarwalkerSeconds() <= 0) {
                        final Rectangle rect = new Rectangle(this.creature.getCurrentTile().tilex * 4, this.creature.getCurrentTile().tiley * 4 + 1, 4, 1);
                        final AffineTransform transform = new AffineTransform();
                        transform.rotate(barrier.getRotation() - 90.0f, rect.getX() + rect.width / 2, rect.getY() + rect.height / 2);
                        final Shape transformed = transform.createTransformedShape(rect);
                        if (transformed.contains(this.creature.getPosX(), this.creature.getPosY())) {
                            Wound wound = null;
                            boolean dead = false;
                            try {
                                final byte pos = this.creature.getBody().getRandomWoundPos();
                                if (Server.rand.nextInt(10) <= 6 && this.creature.getBody().getWounds() != null) {
                                    wound = this.creature.getBody().getWounds().getWoundAtLocation(pos);
                                    if (wound != null) {
                                        dead = wound.modifySeverity((int)(1000.0f + Server.rand.nextInt(4000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f));
                                        wound.setBandaged(false);
                                        this.creature.setWounded();
                                    }
                                    barrier.setDamage(barrier.getDamage() + Server.rand.nextFloat() * 5.0f);
                                }
                                if (wound == null) {
                                    dead = this.creature.addWoundOfType(null, (byte)2, pos, false, 1.0f, true, 500.0f + Server.rand.nextInt(6000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                                }
                                Server.getInstance().broadCastAction(this.creature.getNameWithGenus() + " is pierced by the barrier.", this.creature, 2);
                                this.creature.getCommunicator().sendAlertServerMessage("You are pierced by the barrier!");
                                if (dead) {
                                    this.creature.achievement(143);
                                    return;
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }
                }
                if (Tiles.decodeType(tile) == Tiles.Tile.TILE_LAVA.id) {
                    if (this.creature.getPower() < 1 && (this.creature.getDeity() == null || !this.creature.getDeity().isMountainGod() || this.creature.getFaith() < 35.0f) && this.creature.getFarwalkerSeconds() <= 0 && Server.rand.nextInt(10) == 0) {
                        Wound wound2 = null;
                        boolean dead2 = false;
                        try {
                            byte pos2 = 15;
                            if (Server.rand.nextBoolean()) {
                                pos2 = 16;
                            }
                            if (Server.rand.nextInt(10) <= 6 && this.creature.getBody().getWounds() != null) {
                                wound2 = this.creature.getBody().getWounds().getWoundAtLocation(pos2);
                                if (wound2 != null) {
                                    dead2 = wound2.modifySeverity((int)(1000.0f + Server.rand.nextInt(4000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f));
                                    wound2.setBandaged(false);
                                    this.creature.setWounded();
                                }
                            }
                            if (wound2 == null && this.creature.isPlayer()) {
                                dead2 = this.creature.addWoundOfType(null, (byte)4, pos2, false, 1.0f, true, 1000.0f + Server.rand.nextInt(4000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                            }
                            this.creature.getCommunicator().sendAlertServerMessage("You are burnt by lava!");
                            if (dead2) {
                                this.creature.achievement(142);
                                return;
                            }
                        }
                        catch (Exception ex2) {}
                    }
                }
                else if (this.creature.getDeity() == null || !this.creature.getDeity().isForestGod() || this.creature.getFaith() < 35.0f) {
                    if (this.creature.getPower() < 1) {
                        final Tiles.Tile theTile = Tiles.getTile(Tiles.decodeType(tile));
                        if (theTile.isNormalBush() || theTile.isMyceliumBush()) {
                            final byte data = Tiles.decodeData(tile);
                            final byte age = FoliageAge.getAgeAsByte(data);
                            if (theTile.isThorn(data) && age > FoliageAge.OLD_TWO.getAgeId() && this.creature.getFarwalkerSeconds() <= 0 && Server.rand.nextInt(10) == 0) {
                                Wound wound3 = null;
                                boolean dead3 = false;
                                try {
                                    final byte pos3 = this.creature.getBody().getRandomWoundPos();
                                    if (Server.rand.nextInt(10) <= 6 && this.creature.getBody().getWounds() != null) {
                                        wound3 = this.creature.getBody().getWounds().getWoundAtLocation(pos3);
                                        if (wound3 != null) {
                                            if (Tiles.getTile(Tiles.decodeType(tile)).isMyceliumBush() && this.creature.getKingdomTemplateId() == 3) {
                                                dead3 = wound3.modifySeverity((int)(500.0f + Server.rand.nextInt(2000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f));
                                            }
                                            else {
                                                dead3 = wound3.modifySeverity((int)(1000.0f + Server.rand.nextInt(4000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f));
                                            }
                                            wound3.setBandaged(false);
                                            this.creature.setWounded();
                                        }
                                    }
                                    if (wound3 == null && WurmId.getType(this.creature.getWurmId()) == 0) {
                                        if (Tiles.getTile(Tiles.decodeType(tile)).isMyceliumBush() && this.creature.getKingdomId() == 3) {
                                            dead3 = this.creature.addWoundOfType(null, (byte)2, pos3, false, 1.0f, true, 500.0f + Server.rand.nextInt(4000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                                        }
                                        else {
                                            dead3 = this.creature.addWoundOfType(null, (byte)2, pos3, false, 1.0f, true, 500.0f + Server.rand.nextInt(6000) * (100.0f - this.creature.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                                        }
                                    }
                                    this.creature.getCommunicator().sendAlertServerMessage("You are pierced by the sharp thorns!");
                                    if (dead3) {
                                        this.creature.achievement(143);
                                        return;
                                    }
                                }
                                catch (Exception ex3) {}
                            }
                        }
                    }
                }
                else if (this.creature.getDeity() != null) {
                    if (this.creature.getDeity().isForestGod() && this.creature.getFaith() >= 70.0f) {
                        final byte type = Tiles.decodeType(tile);
                        final Tiles.Tile theTile2 = Tiles.getTile(type);
                        if (theTile2.isNormalTree() || theTile2.isMyceliumTree() || type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_TUNDRA.id) {
                            weight *= (int)0.5;
                        }
                    }
                    else if (this.creature.getDeity().isRoadProtector() && this.creature.getFaith() >= 60.0f && this.creature.getFavor() > 30.0f) {
                        final byte type = Tiles.decodeType(tile);
                        if (Terraforming.isRoad(type)) {
                            weight *= (int)0.5;
                        }
                    }
                    else if (this.creature.getDeity().isMountainGod() && this.creature.getFaith() >= 60.0f && this.creature.getFavor() > 30.0f) {
                        final byte type = Tiles.decodeType(tile);
                        if (type == Tiles.Tile.TILE_ROCK.id || (type == Tiles.Tile.TILE_SAND.id && this.creature.getKingdomId() == 2)) {
                            weight *= (int)0.5;
                        }
                    }
                    else if (this.creature.getDeity().isHateGod() && this.creature.getFaith() >= 60.0f && this.creature.getFavor() > 30.0f) {
                        final byte type = Tiles.decodeType(tile);
                        final Tiles.Tile theTile2 = Tiles.getTile(type);
                        if (type == Tiles.Tile.TILE_MYCELIUM.id || theTile2.isMyceliumTree()) {
                            weight *= (int)0.5;
                        }
                    }
                }
                if (this.draggedItem != null) {
                    try {
                        if (this.moveDraggedItem() && this.draggedItem != null) {
                            MovementScheme.vehic = Vehicles.getVehicleForId(this.draggedItem.getWurmId());
                            if (MovementScheme.vehic != null) {
                                if (MovementScheme.vehic.creature) {
                                    try {
                                        MovementScheme.cretVehicle = Server.getInstance().getCreature(MovementScheme.vehic.wurmid);
                                    }
                                    catch (NoSuchCreatureException ex4) {}
                                    catch (NoSuchPlayerException ex5) {}
                                }
                                else {
                                    try {
                                        MovementScheme.itemVehicle = Items.getItem(MovementScheme.vehic.wurmid);
                                    }
                                    catch (NoSuchItemException ex6) {}
                                }
                                movePassengers(MovementScheme.vehic, this.creature, MovementScheme.vehic.creature);
                            }
                            weight += this.draggedItem.getFullWeight() / 10;
                            if (moveDiff > 0.0) {
                                weight *= 2;
                            }
                        }
                    }
                    catch (NoSuchZoneException nsz) {
                        Items.stopDragging(this.draggedItem);
                    }
                }
                if (this.creature.isVehicleCommander()) {
                    MovementScheme.vehic = Vehicles.getVehicleForId(this.creature.getVehicle());
                    if (MovementScheme.vehic != null && !MovementScheme.vehic.isChair()) {
                        this.moveVehicle(diffX, diffY, diffZ);
                    }
                    try {
                        weight += Items.getItem(this.creature.getVehicle()).getWeightGrams() / 100;
                    }
                    catch (NoSuchItemException ex7) {}
                }
                if (this.creature.getPower() < 2 && this.creature.getPositionZ() < -1.3 && this.creature.getVehicle() == -10L) {
                    if (!this.hasWetFeet) {
                        this.hasWetFeet = true;
                        this.creature.achievement(70);
                    }
                    if (!PlonkData.SWIMMING.hasSeenThis(this.creature)) {
                        PlonkData.SWIMMING.trigger(this.creature);
                    }
                    ++moveDiff;
                    weight += 20000;
                    if (this.creature.getStatus().getStamina() < 50 && !this.creature.isSubmerged() && !this.creature.isUndead() && Server.rand.nextInt(100) == 0) {
                        this.creature.addWoundOfType(null, (byte)7, 2, false, 1.0f, false, (4000.0f + Server.rand.nextFloat() * 3000.0f) * ItemBonus.getDrownDamReduction(this.creature), 0.0f, 0.0f, false, false);
                        this.creature.getCommunicator().sendAlertServerMessage("You are drowning!");
                    }
                }
            }
            else {
                if (this.getBitMask() == 0) {
                    this.creature.getStatus().setMoving(false);
                }
                if (this.creature.isVehicleCommander()) {
                    MovementScheme.vehic = Vehicles.getVehicleForId(this.creature.getVehicle());
                    if (MovementScheme.vehic != null) {
                        this.moveVehicle(diffX, diffY, diffZ);
                    }
                }
            }
            if ((beforeZMod > 0.0 || moveDiff > 0.0) && (this.creature.getVehicle() == -10L || this.isMovingVehicle() || this.draggedItem != null)) {
                this.creature.getStatus().setNormalRegen(false);
                if (moveDiff > 0.0) {
                    this.creature.getStatus().modifyStamina((int)(-moveDiff * weight / 5000.0));
                }
            }
        }
    }
    
    public final void setHasSpiritSpeed(final boolean hasSpeed) {
        this.hasSpiritSpeed = hasSpeed;
    }
    
    public float getSpeedModifier() {
        if (this.halted) {
            return 0.0f;
        }
        double bonus = 0.0;
        if (this.modifiers != null) {
            double webHurtModifier = 0.0;
            for (final DoubleValueModifier lDoubleValueModifier : this.modifiers) {
                if (lDoubleValueModifier.getType() == 7) {
                    if (lDoubleValueModifier.getModifier() >= webHurtModifier) {
                        continue;
                    }
                    webHurtModifier = lDoubleValueModifier.getModifier();
                }
                else {
                    bonus += lDoubleValueModifier.getModifier();
                }
            }
            bonus += webHurtModifier;
        }
        if (bonus < -4.0) {
            return 0.0f;
        }
        if (this.encumbered) {
            return 0.05f;
        }
        if (this.hasSpiritSpeed) {
            bonus *= 1.0499999523162842;
        }
        return (float)Math.max(0.05000000074505806, this.baseModifier + bonus);
    }
    
    public void setEncumbered(final boolean enc) {
        if (this.creature.getVehicle() != -10L) {
            this.encumbered = false;
        }
        else {
            this.encumbered = enc;
        }
    }
    
    public void setBaseModifier(final float base) {
        if (this.creature.getVehicle() != -10L) {
            this.baseModifier = 1.0f;
        }
        else {
            this.baseModifier = base;
        }
        this.update();
    }
    
    public void update() {
        if (!this.creature.isPlayer()) {
            if (this.creature.isRidden()) {
                this.creature.forceMountSpeedChange();
            }
            return;
        }
        if (!this.halted) {
            this.sendSpeedModifier();
        }
    }
    
    public void haltSpeedModifier() {
        this.addSpeedMod(0.0f);
        this.creature.getCommunicator().sendSpeedModifier(0.0f);
        this.halted = true;
    }
    
    public void resumeSpeedModifier() {
        this.halted = false;
        this.sendSpeedModifier();
    }
    
    public void stopSendingSpeedModifier() {
        this.halted = true;
    }
    
    public Item getDraggedItem() {
        return this.draggedItem;
    }
    
    public void setDraggedItem(@Nullable final Item dragged) {
        if (this.draggedItem != null && !this.draggedItem.equals(dragged)) {
            if (dragged != null) {
                Items.stopDragging(this.draggedItem);
            }
            else {
                this.creature.getCommunicator().sendNormalServerMessage("You stop dragging " + this.draggedItem.getNameWithGenus() + '.');
                if (this.draggedItem.getTemplateId() == 1125) {
                    this.removeModifier(MovementScheme.ramDragMod);
                }
                else {
                    this.removeModifier(MovementScheme.dragMod);
                }
            }
        }
        this.draggedItem = dragged;
        if (this.draggedItem != null) {
            this.creature.getCommunicator().sendNormalServerMessage("You start dragging " + this.draggedItem.getNameWithGenus() + '.');
            if (this.draggedItem.getTemplateId() == 1125) {
                this.addModifier(MovementScheme.ramDragMod);
            }
            else {
                this.addModifier(MovementScheme.dragMod);
            }
        }
    }
    
    public void setFightMoveMod(final boolean fighting) {
        if (fighting) {
            this.addModifier(MovementScheme.combatMod);
        }
        else {
            this.removeModifier(MovementScheme.combatMod);
        }
    }
    
    public void setFarwalkerMoveMod(final boolean add) {
        if (add) {
            this.addModifier(MovementScheme.farwalkerMod);
        }
        else {
            this.removeModifier(MovementScheme.farwalkerMod);
        }
    }
    
    public boolean setWebArmourMod(final boolean add, final float power) {
        if (add) {
            if (!this.webArmoured) {
                this.webArmoured = true;
                this.justWebSlowArmour = true;
                this.webArmourMod.setModifier(-power / 200.0f);
                this.addModifier(this.webArmourMod);
            }
        }
        else if (this.webArmoured) {
            if (this.justWebSlowArmour) {
                this.justWebSlowArmour = false;
            }
            else {
                this.webArmoured = false;
                this.removeModifier(this.webArmourMod);
                this.webArmourMod.setModifier(0.0);
            }
        }
        return this.webArmoured;
    }
    
    public double getWebArmourMod() {
        if (this.webArmoured) {
            return this.webArmourMod.getModifier();
        }
        return 0.0;
    }
    
    public void setChargeMoveMod(final boolean add) {
        if (add) {
            this.addModifier(MovementScheme.chargeMod);
        }
        else {
            this.removeModifier(MovementScheme.chargeMod);
        }
    }
    
    public void setDrunkMod(final boolean drunk) {
        if (drunk) {
            this.addModifier(MovementScheme.drunkMod);
        }
        else {
            this.removeModifier(MovementScheme.drunkMod);
        }
    }
    
    public void setMooredMod(final boolean moored) {
        if (moored) {
            this.addModifier(MovementScheme.mooreMod);
        }
        else {
            this.removeModifier(MovementScheme.mooreMod);
        }
    }
    
    public void setStealthMod(final boolean stealth) {
        if (stealth) {
            this.addModifier(this.stealthMod);
        }
        else {
            this.removeModifier(this.stealthMod);
        }
    }
    
    public void setFreezeMod(final boolean frozen) {
        if (frozen) {
            this.addModifier(MovementScheme.freezeMod);
        }
        else {
            this.removeModifier(MovementScheme.freezeMod);
        }
    }
    
    private final float getDragDistanceMod(final int templateId) {
        switch (templateId) {
            case 539: {
                return -2.0f;
            }
            case 853:
            case 1410: {
                return -3.0f;
            }
            default: {
                return -1.5f;
            }
        }
    }
    
    protected boolean moveDraggedItem() throws NoSuchZoneException {
        final int weight = this.draggedItem.getFullWeight(true);
        final int left = this.creature.getCarryingCapacityLeft();
        if ((this.draggedItem.getTemplateId() != 539 || weight >= left) && weight >= left * 10) {
            this.creature.getCommunicator().sendNormalServerMessage("The " + this.draggedItem.getName() + " is too heavy.");
            Items.stopDragging(this.draggedItem);
            return false;
        }
        final float iposx = this.creature.getStatus().getPositionX();
        final float iposy = this.creature.getStatus().getPositionY();
        final float rot = this.creature.getStatus().getRotation();
        final float oldPosZ = this.creature.getPositionZ();
        final float distMod = this.getDragDistanceMod(this.draggedItem.getTemplateId());
        final float xPosMod = (float)Math.sin(rot * 0.017453292f) * distMod;
        final float yPosMod = -(float)Math.cos(rot * 0.017453292f) * distMod;
        float newPosX = iposx + xPosMod;
        float newPosY = iposy + yPosMod;
        if (!this.creature.isOnSurface() && (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newPosX >> 2, (int)newPosY >> 2))) || Blocking.isDiagonalRockBetween(this.creature, (int)iposx >> 2, (int)iposy >> 2, (int)newPosX >> 2, (int)newPosY >> 2) != null)) {
            if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)iposx >> 2, (int)iposy >> 2)))) {
                Items.stopDragging(this.draggedItem);
                return false;
            }
            newPosX = iposx;
            newPosY = iposy;
        }
        if (this.draggedItem.onBridge() > 0L) {
            final int ntx = (int)newPosX >> 2;
            final int nty = (int)newPosY >> 2;
            if (this.draggedItem.getTileX() != ntx || this.draggedItem.getTileY() != nty) {
                final VolaTile newTile = Zones.getOrCreateTile(ntx, nty, this.draggedItem.isOnSurface());
                if (newTile == null || newTile.getStructure() == null || newTile.getStructure().getWurmId() != this.draggedItem.onBridge()) {
                    final VolaTile oldTile = Zones.getOrCreateTile(this.draggedItem.getTileX(), this.draggedItem.getTileY(), this.draggedItem.isOnSurface());
                    boolean leavingOnSide = false;
                    if (oldTile != null) {
                        final BridgePart[] bridgeParts = oldTile.getBridgeParts();
                        if (bridgeParts != null) {
                            final BridgePart[] array = bridgeParts;
                            final int length = array.length;
                            final int n = 0;
                            if (n < length) {
                                final BridgePart bp = array[n];
                                if ((bp.getDir() == 0 || bp.getDir() == 4) && this.draggedItem.getTileX() != ntx) {
                                    leavingOnSide = true;
                                }
                                else if ((bp.getDir() == 2 || bp.getDir() == 6) && this.draggedItem.getTileY() != nty) {
                                    leavingOnSide = true;
                                }
                            }
                        }
                    }
                    if (leavingOnSide) {
                        newPosX = iposx;
                        newPosY = iposy;
                    }
                }
            }
        }
        final float newPosZ = Zones.calculatePosZ(newPosX, newPosY, null, this.draggedItem.isOnSurface(), false, oldPosZ, null, this.draggedItem.onBridge());
        float maxDepth = -6.0f;
        if (this.draggedItem.isVehicle() && !this.draggedItem.isBoat()) {
            final Vehicle lVehicle = Vehicles.getVehicle(this.draggedItem);
            maxDepth = lVehicle.getMaxDepth();
        }
        if (this.draggedItem.isFloating() && newPosZ > 0.3) {
            this.creature.getCommunicator().sendAlertServerMessage("The " + this.draggedItem.getName() + " gets stuck in the ground.", (byte)3);
            Items.stopDragging(this.draggedItem);
            return false;
        }
        if (!this.draggedItem.isFloating() && newPosZ < maxDepth && this.draggedItem.onBridge() <= 0L) {
            this.creature.getCommunicator().sendAlertServerMessage("The " + this.draggedItem.getName() + " gets stuck on the bottom.", (byte)3);
            Items.stopDragging(this.draggedItem);
            return false;
        }
        if (this.creature.isOnSurface() == this.draggedItem.isOnSurface()) {
            final VolaTile t = Zones.getTileOrNull(this.draggedItem.getTileX(), this.draggedItem.getTileY(), this.draggedItem.isOnSurface());
            if (t == null) {
                Items.stopDragging(this.draggedItem);
                return false;
            }
            if (!this.draggedItem.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newPosX >> 2, (int)newPosY >> 2))) && Tiles.decodeType(Server.caveMesh.getTile(this.draggedItem.getTileX(), this.draggedItem.getTileY())) != 201) {
                return false;
            }
            t.moveItem(this.draggedItem, newPosX, newPosY, newPosZ, Creature.normalizeAngle(rot), this.creature.isOnSurface(), oldPosZ);
        }
        else {
            Zone zone = null;
            if (this.creature.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newPosX >> 2, (int)newPosY >> 2)))) {
                newPosX = iposx;
                newPosY = iposy;
            }
            try {
                zone = Zones.getZone(this.draggedItem.getTileX(), this.draggedItem.getTileY(), this.draggedItem.isOnSurface());
                zone.removeItem(this.draggedItem);
            }
            catch (NoSuchZoneException ex) {}
            this.draggedItem.setPosXYZ(newPosX, newPosY, newPosZ);
            this.draggedItem.newLayer = (byte)(this.creature.isOnSurface() ? 0 : -1);
            zone = Zones.getZone((int)newPosX >> 2, (int)newPosY >> 2, this.creature.isOnSurface());
            zone.addItem(this.draggedItem, true, this.creature.isOnSurface(), false);
            this.draggedItem.newLayer = -128;
            if (this.draggedItem.isVehicle()) {
                final Vehicle vehicle = Vehicles.getVehicleForId(this.draggedItem.getWurmId());
                if (vehicle != null) {
                    final Seat[] seats = vehicle.getSeats();
                    if (seats != null) {
                        for (int x = 0; x < seats.length; ++x) {
                            if (seats[x] != null && seats[x].occupant != -10L) {
                                try {
                                    final Creature c = Server.getInstance().getCreature(seats[x].occupant);
                                    c.setLayer(this.creature.getLayer(), false);
                                    c.refreshVisible();
                                    c.getCommunicator().attachCreature(-1L, this.draggedItem.getWurmId(), seats[x].offx, seats[x].offy, seats[x].offz, x);
                                }
                                catch (NoSuchPlayerException ex2) {}
                                catch (NoSuchCreatureException ex3) {}
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public void addModifier(final DoubleValueModifier modifier) {
        if (!this.creature.isPlayer() && !this.creature.isVehicle()) {
            return;
        }
        if (this.modifiers == null) {
            this.modifiers = new HashSet<DoubleValueModifier>();
        }
        if (!this.modifiers.contains(modifier)) {
            this.modifiers.add(modifier);
        }
        this.update();
    }
    
    public void removeModifier(final DoubleValueModifier modifier) {
        if (this.modifiers != null) {
            this.modifiers.remove(modifier);
        }
        this.update();
    }
    
    @Override
    public void valueChanged(final double oldValue, final double newValue) {
        this.update();
    }
    
    public final void touchFreeMoveCounter() {
        this.changedTileCounter = 5;
    }
    
    public final void decreaseFreeMoveCounter() {
        if (this.changedTileCounter > 0) {
            --this.changedTileCounter;
        }
    }
    
    @Override
    public int getMaxTargetGroundOffset(final int suggestedOffset) {
        if (this.creature.getPower() > 0) {
            return suggestedOffset;
        }
        float xPos = this.getCurrx();
        float yPos = this.getCurry();
        if (xPos == 0.0f && yPos == 0.0f) {
            xPos = this.getX();
            yPos = this.getY();
        }
        final VolaTile t = Zones.getOrCreateTile((int)xPos / 4, (int)yPos / 4, this.getLayer() >= 0);
        if (t == null) {
            return 0;
        }
        final int max = t.getMaxFloorLevel() * 30 + 30;
        return max;
    }
    
    public int getErrors() {
        return this.errors;
    }
    
    public void setErrors(final int errors) {
        this.errors = errors;
    }
    
    static {
        logger = Logger.getLogger(MovementScheme.class.getName());
        dragMod = new FixedDoubleValueModifier(-0.5);
        ramDragMod = new FixedDoubleValueModifier(-0.75);
        combatMod = new FixedDoubleValueModifier(-0.7);
        drunkMod = new FixedDoubleValueModifier(-0.6);
        mooreMod = new FixedDoubleValueModifier(-5.0);
        farwalkerMod = new FixedDoubleValueModifier(0.5);
        chargeMod = new FixedDoubleValueModifier(0.10000000149011612);
        freezeMod = new FixedDoubleValueModifier(-5.0);
        MovementScheme.toRemove = null;
    }
}

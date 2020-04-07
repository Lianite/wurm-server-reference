// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.spells.Cooldowns;
import com.wurmonline.server.questions.QuestionParser;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.Servers;
import com.wurmonline.server.spells.RiteEvent;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.Iterator;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import java.util.HashSet;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.creatures.Creature;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.MiscConstants;

public final class Vehicle implements MiscConstants, ProtoConstants, TimeConstants
{
    private static final Logger logger;
    static final Seat[] EMPTYSEATS;
    public Seat[] seats;
    public Seat[] hitched;
    private float maxSpeed;
    private byte windImpact;
    public boolean creature;
    String pilotName;
    public long pilotId;
    String embarkString;
    String embarksString;
    public String name;
    public final long wurmid;
    public float maxDepth;
    public float maxHeight;
    public float maxHeightDiff;
    public float skillNeeded;
    private int maxAllowedLoadDistance;
    private boolean unmountable;
    private byte maxPassengers;
    public Set<Creature> draggers;
    private boolean chair;
    private boolean bed;
    public byte commandType;
    boolean canHaveEquipment;
    private ServerEntry destinationServer;
    public static final long plotCoursePvPCooldown = 1800000L;
    
    Vehicle(final long aWurmId) {
        this.seats = Vehicle.EMPTYSEATS;
        this.hitched = Vehicle.EMPTYSEATS;
        this.maxSpeed = 1.0f;
        this.windImpact = 0;
        this.creature = false;
        this.pilotName = "driver";
        this.pilotId = -10L;
        this.embarkString = "embark";
        this.embarksString = "embarks";
        this.name = "vehicle";
        this.maxDepth = -2500.0f;
        this.maxHeight = 2500.0f;
        this.maxHeightDiff = 2000.0f;
        this.skillNeeded = 20.1f;
        this.maxAllowedLoadDistance = 4;
        this.unmountable = false;
        this.maxPassengers = 0;
        this.draggers = null;
        this.chair = false;
        this.bed = false;
        this.commandType = 0;
        this.canHaveEquipment = false;
        this.wurmid = aWurmId;
    }
    
    public boolean addDragger(final Creature aCreature) {
        if (this.hitched.length > 0) {
            if (this.draggers == null) {
                this.draggers = new HashSet<Creature>();
            }
            if (this.draggers.size() < this.hitched.length) {
                if (this.draggers.add(aCreature)) {
                    for (int x = 0; x < this.hitched.length; ++x) {
                        if (this.hitched[x].occupant == -10L) {
                            this.hitched[x].setOccupant(aCreature.getWurmId());
                            if (this.getPilotId() > -10L) {
                                try {
                                    final Creature c = Server.getInstance().getCreature(this.getPilotId());
                                    c.getMovementScheme().addMountSpeed(this.calculateNewVehicleSpeed(true));
                                }
                                catch (Exception ex) {}
                            }
                            return true;
                        }
                    }
                    Vehicle.logger.log(Level.WARNING, "error when adding to hitched seat - no free space.");
                    this.draggers.remove(aCreature);
                }
            }
            else {
                Vehicle.logger.log(Level.WARNING, "draggers.size=" + this.draggers.size() + ", hitched.length=" + this.hitched.length + " - no space");
            }
        }
        return false;
    }
    
    public void purgeDraggers() {
        if (this.draggers != null) {
            for (final Creature dragger : this.draggers) {
                for (int x = 0; x < this.hitched.length; ++x) {
                    if (this.hitched[x].occupant == dragger.getWurmId()) {
                        this.hitched[x].setOccupant(-10L);
                        break;
                    }
                }
                dragger.setHitched(null, false);
                Server.getInstance().broadCastMessage(dragger.getName() + " stops dragging a " + this.getName() + ".", dragger.getTileX(), dragger.getTileY(), dragger.isOnSurface(), 5);
            }
            if (this.getPilotId() > -10L) {
                try {
                    final Creature c = Server.getInstance().getCreature(this.getPilotId());
                    c.getMovementScheme().addMountSpeed(this.calculateNewVehicleSpeed(true));
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public boolean removeDragger(final Creature aCreature) {
        if (this.hitched.length > 0 && this.draggers != null && this.draggers.remove(aCreature)) {
            for (int x = 0; x < this.hitched.length; ++x) {
                if (this.hitched[x].occupant == aCreature.getWurmId()) {
                    this.hitched[x].setOccupant(-10L);
                    break;
                }
            }
            aCreature.setHitched(null, false);
            String hitchedType = "stop dragging";
            if (!this.creature) {
                try {
                    final Item dragged = Items.getItem(this.getWurmid());
                    if (dragged.isTent()) {
                        hitchedType = "is no longer hitched to";
                    }
                }
                catch (NoSuchItemException ex) {}
            }
            Server.getInstance().broadCastMessage(aCreature.getName() + " " + hitchedType + " a " + this.getName() + ".", aCreature.getTileX(), aCreature.getTileY(), aCreature.isOnSurface(), 5);
            if (this.getPilotId() > -10L) {
                try {
                    final Creature c = Server.getInstance().getCreature(this.getPilotId());
                    c.getMovementScheme().addMountSpeed(this.calculateNewVehicleSpeed(true));
                }
                catch (Exception ex2) {}
            }
            return true;
        }
        return false;
    }
    
    public void updateDraggedSpeed(final boolean hitching) {
        if (this.hitched.length > 0 && this.draggers != null && this.getPilotId() > -10L) {
            try {
                final Creature c = Server.getInstance().getCreature(this.getPilotId());
                c.getMovementScheme().addMountSpeed(this.calculateNewVehicleSpeed(hitching));
            }
            catch (Exception ex) {}
        }
    }
    
    public Seat[] getHitched() {
        return this.hitched;
    }
    
    public void setHitched(final Seat[] aHitched) {
        this.hitched = aHitched;
    }
    
    public float getMaxDepth() {
        return this.maxDepth;
    }
    
    public void setMaxDepth(final float aMaxDepth) {
        this.maxDepth = aMaxDepth;
    }
    
    public float getMaxHeight() {
        return this.maxHeight;
    }
    
    public void setMaxHeight(final float aMaxHeight) {
        this.maxHeight = aMaxHeight;
    }
    
    public float getMaxHeightDiff() {
        return this.maxHeightDiff;
    }
    
    public void setMaxHeightDiff(final float aMaxHeightDiff) {
        this.maxHeightDiff = aMaxHeightDiff;
    }
    
    public float getSkillNeeded() {
        return this.skillNeeded;
    }
    
    public boolean getCanHaveEquipment() {
        return this.canHaveEquipment;
    }
    
    public void setSkillNeeded(final float aSkillNeeded) {
        this.skillNeeded = aSkillNeeded;
    }
    
    public Set<Creature> getDraggers() {
        return this.draggers;
    }
    
    public boolean isDragger(final Creature aCreature) {
        return this.hitched.length > 0 && this.draggers != null && this.draggers.contains(aCreature);
    }
    
    public boolean hasHumanDragger() {
        if (this.draggers != null) {
            for (final Creature dragger : this.draggers) {
                if (dragger.isPlayer()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean mayAddDragger() {
        return this.hitched.length > 0 && (this.draggers == null || this.draggers.size() < this.hitched.length);
    }
    
    public void addHitchSeats(final Seat[] hitchSeats) {
        if (hitchSeats == null) {
            this.hitched = Vehicle.EMPTYSEATS;
        }
        else {
            this.hitched = hitchSeats;
        }
    }
    
    void createPassengerSeats(final int aNumber) {
        this.maxPassengers = (byte)aNumber;
        if (aNumber >= 0) {
            (this.seats = new Seat[aNumber + 1])[0] = new Seat((byte)0);
            for (int x = 1; x <= aNumber; ++x) {
                this.seats[x] = new Seat((byte)1);
            }
        }
        else {
            Vehicle.logger.warning("Can only create a positive number of seats not " + aNumber);
        }
    }
    
    void createOnlyPassengerSeats(final int aNumber) {
        if (aNumber >= 0) {
            this.seats = new Seat[aNumber];
            for (int x = 0; x < aNumber; ++x) {
                this.seats[x] = new Seat((byte)1);
            }
        }
        else {
            Vehicle.logger.warning("Can only create a positive number of seats not " + aNumber);
        }
    }
    
    public byte getMaxPassengers() {
        return this.maxPassengers;
    }
    
    public boolean setSeatOffset(final int aNumber, final float aOffx, final float aOffy, final float aOffz) {
        if (aNumber > this.seats.length - 1 || aNumber < 0) {
            return false;
        }
        this.seats[aNumber].offx = aOffx;
        this.seats[aNumber].offy = aOffy;
        this.seats[aNumber].offz = aOffz;
        return true;
    }
    
    public boolean setSeatOffset(final int aNumber, final float aOffx, final float aOffy, final float aOffz, final float aAltOffz) {
        if (aNumber > this.seats.length - 1 || aNumber < 0) {
            return false;
        }
        this.seats[aNumber].offx = aOffx;
        this.seats[aNumber].offy = aOffy;
        this.seats[aNumber].offz = aOffz;
        this.seats[aNumber].setAltOffz(aAltOffz);
        return true;
    }
    
    public boolean setSeatFightMod(final int aNumber, final float aCover, final float aManouvre) {
        if (aNumber > this.seats.length - 1 || aNumber < 0) {
            return false;
        }
        this.seats[aNumber].cover = aCover;
        this.seats[aNumber].manouvre = aManouvre;
        return true;
    }
    
    public Seat getPilotSeat() {
        if (this.seats.length != 0 && this.seats[0].type == 0) {
            return this.seats[0];
        }
        return null;
    }
    
    public Seat getSeatFor(final long aCreatureId) {
        for (int x = 0; x < this.seats.length; ++x) {
            if (this.seats[x].occupant == aCreatureId) {
                return this.seats[x];
            }
        }
        return null;
    }
    
    public final int getSeatNumberFor(final Seat seat) {
        for (int i = 0; i < this.seats.length; ++i) {
            if (this.seats[i].getId() == seat.getId()) {
                return i;
            }
        }
        return -1;
    }
    
    public Seat getHitchSeatFor(final long aCreatureId) {
        for (int x = 0; x < this.hitched.length; ++x) {
            if (this.hitched[x].occupant == aCreatureId) {
                return this.hitched[x];
            }
        }
        return null;
    }
    
    public void kickAll() {
        for (int x = 0; x < this.seats.length; ++x) {
            this.seats[x].leave(this);
        }
        this.pilotId = -10L;
        this.pilotName = "";
    }
    
    public Seat[] getSeats() {
        return this.seats;
    }
    
    void setSeats(final Seat[] aSeats) {
        this.seats = aSeats;
    }
    
    public final boolean isAnySeatOccupied() {
        return this.isAnySeatOccupied(true);
    }
    
    public final boolean isAnySeatOccupied(final boolean countOffline) {
        if (this.seats != null) {
            for (int i = 0; i < this.seats.length; ++i) {
                if (this.seats[i].isOccupied()) {
                    if (countOffline) {
                        return true;
                    }
                    try {
                        final long occupantId = this.seats[i].getOccupant();
                        final Player p = Players.getInstance().getPlayer(occupantId);
                        if (p.isOffline()) {}
                    }
                    catch (NoSuchPlayerException e) {}
                }
            }
        }
        return false;
    }
    
    public final boolean isAnythingHitched() {
        if (this.hitched != null) {
            for (int i = 0; i < this.hitched.length; ++i) {
                if (this.hitched[i].isOccupied()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public byte calculateNewBoatSpeed(final boolean disembarking) {
        int numsOccupied = 0;
        float qlMod = 0.0f;
        for (int x = 0; x < this.seats.length; ++x) {
            if (this.seats[x].isOccupied()) {
                ++numsOccupied;
                try {
                    final long occupantId = this.seats[x].getOccupant();
                    final Player p = Players.getInstance().getPlayer(occupantId);
                    if (p.isOffline()) {
                        --numsOccupied;
                    }
                }
                catch (NoSuchPlayerException e) {
                    --numsOccupied;
                }
            }
        }
        try {
            final Item itemVehicle = Items.getItem(this.wurmid);
            numsOccupied = Math.min(this.seats.length, numsOccupied + itemVehicle.getRarity());
            qlMod = Math.max(0.0f, itemVehicle.getCurrentQualityLevel() - 10.0f) / 90.0f;
            if (qlMod > 0.0f) {
                ++qlMod;
            }
        }
        catch (NoSuchItemException nsi) {
            return 0;
        }
        if (disembarking) {
            --numsOccupied;
        }
        float percentOccupied = 1.0f;
        percentOccupied = 1.0f + numsOccupied / this.seats.length;
        float maxSpeed = this.getMaxSpeed();
        if (RiteEvent.isActive(403)) {
            maxSpeed *= 2.0f;
        }
        if (Servers.localServer.PVPSERVER) {
            return (byte)Math.min(127.0f, percentOccupied * 9.0f * maxSpeed + qlMod * 3.0f * maxSpeed);
        }
        return (byte)Math.min(127.0f, percentOccupied * 3.0f * maxSpeed + qlMod * 9.0f * maxSpeed);
    }
    
    private final int getMinimumDraggers(final Item vehicleItem) {
        if (vehicleItem == null) {
            return 0;
        }
        if (vehicleItem.getTemplateId() == 850) {
            return 2;
        }
        if (!vehicleItem.isBoat()) {
            return 1;
        }
        return 0;
    }
    
    public byte calculateNewVehicleSpeed(final boolean hitching) {
        if (this.isChair()) {
            return 0;
        }
        if (this.hitched.length <= 0) {
            return (byte)Math.min(127.0f, 10.0f * this.getMaxSpeed());
        }
        boolean isWagon = false;
        int bisonCount = 0;
        if (this.draggers == null) {
            return 0;
        }
        double strength = 0.0;
        try {
            final Item itemVehicle = Items.getItem(this.wurmid);
            strength = itemVehicle.getRarity() * 0.1f;
            if (this.getDraggers().size() < this.getMinimumDraggers(itemVehicle)) {
                return 0;
            }
            if (itemVehicle.getTemplateId() == 850) {
                isWagon = true;
            }
        }
        catch (NoSuchItemException nsi) {
            return 0;
        }
        for (final Creature next : this.draggers) {
            if (isWagon && next.getTemplate().getTemplateId() == 82) {
                ++bisonCount;
            }
            strength += next.getStrengthSkill() / (this.hitched.length * 10) * next.getMountSpeedPercent(hitching);
        }
        return (byte)Math.min(127.0, 10.0 * strength * this.getMaxSpeed() + 1 * bisonCount * this.getMaxSpeed());
    }
    
    public byte calculateNewMountSpeed(final Creature mount, final boolean mounting) {
        final double strength = mount.getMountSpeedPercent(mounting);
        if (mount.getTemplateId() == 64 && strength * this.getMaxSpeed() >= 42.0 && this.getPilotId() != -10L) {
            Achievements.triggerAchievement(this.getPilotId(), 584);
        }
        return (byte)Math.max(0.0, Math.min(127.0, strength * this.getMaxSpeed()));
    }
    
    float getMaxSpeed() {
        try {
            final Item itemVehicle = Items.getItem(this.wurmid);
            if (itemVehicle != null && itemVehicle.getSpellEffects() != null) {
                final float modifier = itemVehicle.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_VEHCSPEED);
                return this.maxSpeed * modifier;
            }
        }
        catch (NoSuchItemException nsi) {
            return this.maxSpeed;
        }
        return this.maxSpeed;
    }
    
    void setMaxSpeed(final float aMaxSpeed) {
        this.maxSpeed = aMaxSpeed;
    }
    
    public byte getWindImpact() {
        float modifier = 1.0f;
        try {
            final Item itemVehicle = Items.getItem(this.wurmid);
            if (itemVehicle != null && itemVehicle.getSpellEffects() != null) {
                modifier = itemVehicle.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_WIND);
            }
        }
        catch (NoSuchItemException ex) {}
        return (byte)Math.min(127.0f, this.windImpact * modifier);
    }
    
    void setWindImpact(final byte impact) {
        this.windImpact = (byte)Math.min(127, impact);
    }
    
    public boolean isCreature() {
        return this.creature;
    }
    
    void setCreature(final boolean aCreature) {
        this.creature = aCreature;
    }
    
    String getPilotName() {
        return this.pilotName;
    }
    
    void setPilotName(final String aPilotName) {
        this.pilotName = aPilotName;
    }
    
    public long getPilotId() {
        return this.pilotId;
    }
    
    void setPilotId(final long aPilotId) {
        this.pilotId = aPilotId;
    }
    
    String getEmbarkString() {
        return this.embarkString;
    }
    
    void setEmbarkString(final String aEmbarkString) {
        this.embarkString = aEmbarkString;
    }
    
    String getName() {
        return this.name;
    }
    
    public static final String getVehicleName(final Vehicle vehicle) {
        if (vehicle.isCreature()) {
            try {
                final Creature mount = Creatures.getInstance().getCreature(vehicle.getWurmid());
                return mount.getName();
            }
            catch (NoSuchCreatureException nsc) {
                Vehicle.logger.log(Level.WARNING, StringUtil.format("Unable to find creature with id: %d.", vehicle.getWurmid()), nsc);
            }
        }
        if (vehicle.isChair()) {
            try {
                final Item chair = Items.getItem(vehicle.getWurmid());
                return chair.getName();
            }
            catch (NoSuchItemException nsi) {
                Vehicle.logger.log(Level.WARNING, StringUtil.format("Unable to find item with id: %d.", vehicle.getWurmid()), nsi);
            }
        }
        return vehicle.getName();
    }
    
    void setName(final String aName) {
        this.name = aName;
    }
    
    long getWurmid() {
        return this.wurmid;
    }
    
    @Override
    public String toString() {
        final StringBuilder lBuilder = new StringBuilder(200);
        lBuilder.append("Vehicle [id: ").append(this.wurmid);
        lBuilder.append(", Name: ").append(this.name);
        lBuilder.append(", PilotId: ").append(this.pilotId);
        lBuilder.append(", PilotName: ").append(this.pilotName);
        lBuilder.append(", MaxSpeed: ").append(this.getMaxSpeed());
        lBuilder.append(", EmbarkString: ").append(this.embarkString);
        lBuilder.append(", Creature: ").append(this.creature);
        lBuilder.append(']');
        return lBuilder.toString();
    }
    
    public int getSeatPosForPassenger(final long _wurmid) {
        for (int x = 0; x < this.seats.length; ++x) {
            if (this.seats[x].occupant == _wurmid) {
                return x;
            }
        }
        return -1;
    }
    
    public final int getFloorLevel() {
        if (this.creature) {
            try {
                return Server.getInstance().getCreature(this.wurmid).getFloorLevel();
            }
            catch (Exception ex) {
                return 0;
            }
        }
        try {
            return Items.getItem(this.wurmid).getFloorLevel();
        }
        catch (NoSuchItemException nsi) {
            return 0;
        }
    }
    
    public final float getPosZ() {
        if (this.creature) {
            try {
                return Server.getInstance().getCreature(this.wurmid).getPositionZ();
            }
            catch (Exception ex) {
                return 0.0f;
            }
        }
        try {
            return Items.getItem(this.wurmid).getPosZ();
        }
        catch (NoSuchItemException nsi) {
            return 0.0f;
        }
    }
    
    public boolean positionDragger(final Creature dragger, final Creature performer) {
        Item itemVehicle = null;
        try {
            itemVehicle = Items.getItem(this.wurmid);
        }
        catch (NoSuchItemException nsi) {
            return false;
        }
        for (int x = 0; x < this.hitched.length; ++x) {
            if (this.hitched[x].type == 2 && this.hitched[x].getOccupant() == dragger.getWurmId()) {
                final float r = (-itemVehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                final float s = (float)Math.sin(r);
                final float c = (float)Math.cos(r);
                final Seat pilotSeat = this.getPilotSeat();
                final float xo2 = (pilotSeat == null) ? 0.0f : (s * -pilotSeat.offx - c * -pilotSeat.offy);
                final float yo2 = (pilotSeat == null) ? 0.0f : (c * -pilotSeat.offx + s * -pilotSeat.offy);
                float origposx = itemVehicle.getPosX() + xo2;
                float origposy = itemVehicle.getPosY() + yo2;
                origposx = Math.max(3.0f, origposx);
                origposx = Math.min(Zones.worldMeterSizeX - 3.0f, origposx);
                origposy = Math.max(3.0f, origposy);
                origposy = Math.min(Zones.worldMeterSizeY - 3.0f, origposy);
                final float xo3 = s * -this.hitched[x].offx - c * -this.hitched[x].offy;
                final float yo3 = c * -this.hitched[x].offx + s * -this.hitched[x].offy;
                float newposx = itemVehicle.getPosX() + xo3;
                float newposy = itemVehicle.getPosY() + yo3;
                if (itemVehicle.isTent()) {
                    newposx = performer.getPosX();
                    newposy = performer.getPosY();
                }
                newposx = Math.max(3.0f, newposx);
                newposx = Math.min(Zones.worldMeterSizeX - 3.0f, newposx);
                newposy = Math.max(3.0f, newposy);
                newposy = Math.min(Zones.worldMeterSizeY - 3.0f, newposy);
                final int diffx = ((int)newposx >> 2) - ((int)origposx >> 2);
                final int diffy = ((int)newposy >> 2) - ((int)origposy >> 2);
                boolean move = true;
                if (!itemVehicle.isTent() && (diffy != 0 || diffx != 0)) {
                    final BlockingResult result = Blocking.getBlockerBetween(dragger, origposx, origposy, newposx, newposy, dragger.getPositionZ(), dragger.getPositionZ(), dragger.getLayer() >= 0, dragger.getLayer() >= 0, false, 6, -1L, itemVehicle.getBridgeId(), itemVehicle.getBridgeId(), false);
                    if (result != null) {
                        final Blocker first = result.getFirstBlocker();
                        if (!first.isDoor() || !first.canBeOpenedBy(dragger, false)) {
                            move = false;
                        }
                    }
                    if (move && dragger.getLayer() < 0 && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newposx >> 2, (int)newposy >> 2)))) {
                        move = false;
                    }
                }
                if (!move) {
                    newposx = origposx;
                    newposy = origposy;
                }
                try {
                    Zones.getZone(dragger.getCurrentTile().tilex, dragger.getCurrentTile().tiley, dragger.isOnSurface()).removeCreature(dragger, true, false);
                }
                catch (Exception ex) {
                    Vehicle.logger.log(Level.WARNING, dragger.getWurmId() + "," + ex.getMessage(), ex);
                }
                dragger.getStatus().setPositionX(newposx);
                dragger.getStatus().setPositionY(newposy);
                dragger.setBridgeId(itemVehicle.getBridgeId());
                final float z = Zones.calculatePosZ(newposx, newposy, Zones.getTileOrNull(dragger.getTilePos(), dragger.isOnSurface()), dragger.isOnSurface(), false, dragger.getStatus().getPositionZ(), dragger, dragger.getBridgeId());
                dragger.getMovementScheme().setPosition(dragger.getStatus().getPositionX(), dragger.getStatus().getPositionY(), z, dragger.getStatus().getRotation(), dragger.getLayer());
                dragger.destroyVisionArea();
                try {
                    Zones.getZone(dragger.getTileX(), dragger.getTileY(), dragger.isOnSurface()).addCreature(dragger.getWurmId());
                    dragger.createVisionArea();
                }
                catch (Exception ex2) {
                    Vehicle.logger.log(Level.WARNING, dragger.getWurmId() + "," + ex2.getMessage(), ex2);
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean isUnmountable() {
        return this.unmountable;
    }
    
    public void setUnmountable(final boolean aUnmountable) {
        this.unmountable = aUnmountable;
    }
    
    public boolean isChair() {
        return this.chair;
    }
    
    public void setChair(final boolean isChair) {
        this.chair = isChair;
    }
    
    public boolean isBed() {
        return this.bed;
    }
    
    public void setBed(final boolean isBed) {
        this.bed = isBed;
    }
    
    public int getMaxAllowedLoadDistance() {
        return this.maxAllowedLoadDistance;
    }
    
    public void setMaxAllowedLoadDistance(final int newMaxDist) {
        this.maxAllowedLoadDistance = newMaxDist;
    }
    
    public ServerEntry getDestinationServer() {
        return this.destinationServer;
    }
    
    public boolean hasDestinationSet() {
        return this.destinationServer != null;
    }
    
    public void setDestination(final ServerEntry entry) {
        this.destinationServer = entry;
    }
    
    public void clearDestination() {
        this.destinationServer = null;
    }
    
    public void alertPassengerOfEnemies(final Creature performer, final ServerEntry entry, final boolean embarking) {
        if ((entry.PVPSERVER && (!entry.EPIC || Server.getInstance().isPS())) || entry.isChaosServer()) {
            final byte pKingdom = (byte)((((Player)performer).getSaveFile().getChaosKingdom() == 0) ? 4 : ((Player)performer).getSaveFile().getChaosKingdom());
            for (final Seat lSeat : this.seats) {
                final PlayerInfo oInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(lSeat.getOccupant());
                if (oInfo != null) {
                    final byte oKingdom = (byte)((oInfo.getChaosKingdom() == 0) ? 4 : oInfo.getChaosKingdom());
                    if (oKingdom != pKingdom) {
                        performer.getCommunicator().sendAlertServerMessage("Warning: " + oInfo.getName() + " will be an enemy when you cross into " + entry.getName() + "!");
                        if (embarking) {
                            try {
                                final Player oPlayer = Players.getInstance().getPlayer(oInfo.wurmId);
                                oPlayer.getCommunicator().sendAlertServerMessage("Warning: " + performer.getName() + " will be an enemy when you cross into " + entry.getName() + "!");
                            }
                            catch (NoSuchPlayerException ex) {}
                        }
                    }
                }
            }
        }
    }
    
    public void alertAllPassengersOfEnemies(final ServerEntry entry) {
        for (final Seat lSeat : this.seats) {
            final PlayerInfo oInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(lSeat.getOccupant());
            if (oInfo != null) {
                try {
                    final Player oPlayer = Players.getInstance().getPlayer(oInfo.wurmId);
                    this.alertPassengerOfEnemies(oPlayer, entry, false);
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
    }
    
    public void notifyAllPassengers(final String message, final boolean includeDriver, final boolean alert) {
        for (final Seat lSeat : this.seats) {
            if (includeDriver || lSeat != this.getPilotSeat()) {
                final PlayerInfo oInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(lSeat.getOccupant());
                if (oInfo != null) {
                    try {
                        final Player oPlayer = Players.getInstance().getPlayer(oInfo.wurmId);
                        if (alert) {
                            oPlayer.getCommunicator().sendAlertServerMessage(message);
                        }
                        else {
                            oPlayer.getCommunicator().sendNormalServerMessage(message);
                        }
                    }
                    catch (NoSuchPlayerException ex) {}
                }
            }
        }
    }
    
    public void alertPassengersOfKingdom(final ServerEntry entry, final boolean includeDriver) {
        for (final Seat lSeat : this.seats) {
            if (includeDriver || lSeat != this.getPilotSeat()) {
                final PlayerInfo oInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(lSeat.getOccupant());
                if (oInfo != null) {
                    final byte oKingdom = (byte)((oInfo.getChaosKingdom() == 0) ? 4 : oInfo.getChaosKingdom());
                    try {
                        final Player oPlayer = Players.getInstance().getPlayer(oInfo.wurmId);
                        if ((!Server.getInstance().isPS() && entry.isChaosServer()) || (entry.PVPSERVER && !Servers.localServer.PVPSERVER)) {
                            String kingdomMsg = "This course will take you into hostile territory";
                            if (oKingdom != oPlayer.getKingdomId()) {
                                kingdomMsg = kingdomMsg + ", and you will join the " + Kingdoms.getNameFor(oKingdom) + " kingdom until you return";
                            }
                            oPlayer.getCommunicator().sendAlertServerMessage(kingdomMsg + ".");
                        }
                        else if ((!Server.getInstance().isPS() && Servers.localServer.isChaosServer()) || (Servers.localServer.PVPSERVER && entry.HOMESERVER && !entry.PVPSERVER)) {
                            String kingdomMsg = "This course will take you into friendly territory";
                            if (oKingdom != entry.getKingdom()) {
                                kingdomMsg = kingdomMsg + ", and you will join the " + Kingdoms.getNameFor(entry.getKingdom()) + " kingdom until you return";
                            }
                            oPlayer.getCommunicator().sendNormalServerMessage(kingdomMsg + ".");
                        }
                        if (entry.PVPSERVER && !Servers.localServer.PVPSERVER && oPlayer.getDeity() != null && !QuestionParser.doesKingdomTemplateAcceptDeity(Kingdoms.getKingdomTemplateFor(oKingdom), oPlayer.getDeity())) {
                            oPlayer.getCommunicator().sendAlertServerMessage("Warning: " + oPlayer.getDeity().getName() + " does not align with your kingdom of " + Kingdoms.getNameFor(oKingdom) + ". If you continue travel to " + entry.getName() + " you will lose all faith and abilities granted by your deity.");
                        }
                    }
                    catch (NoSuchPlayerException ex) {}
                }
            }
        }
    }
    
    public boolean checkPassengerPermissions(final Creature performer) {
        boolean toReturn = false;
        if (!Servers.localServer.PVPSERVER) {
            try {
                final Item ivehic = Items.getItem(this.wurmid);
                if (!ivehic.isGuest(performer) || !ivehic.mayCommand(performer)) {
                    performer.getCommunicator().sendNormalServerMessage("You may not leave the server with this boat. You need to be explicitly specified in the boat's permissions.");
                    toReturn = true;
                }
                else {
                    for (final Seat seat : this.getSeats()) {
                        if (seat.isOccupied() && seat.type == 1 && !ivehic.isGuest(seat.getOccupant())) {
                            try {
                                final Creature c = Server.getInstance().getCreature(seat.occupant);
                                if (!ivehic.mayPassenger(c)) {
                                    performer.getCommunicator().sendNormalServerMessage("You may not leave the server with this boat as one of your passengers will not have passenger permission on new server.");
                                    toReturn = true;
                                    break;
                                }
                            }
                            catch (NoSuchCreatureException ex) {}
                            catch (NoSuchPlayerException ex2) {}
                        }
                    }
                }
            }
            catch (NoSuchItemException ex3) {}
        }
        return !toReturn;
    }
    
    public void touchPlotCourseCooldowns() {
        this.touchPlotCourseCooldowns(1800000L);
    }
    
    public void touchPlotCourseCooldowns(final long cooldown) {
        for (final Seat seat : this.getSeats()) {
            final Cooldowns cd = Cooldowns.getCooldownsFor(seat.getOccupant(), true);
            cd.addCooldown(717, System.currentTimeMillis() + cooldown, false);
        }
    }
    
    public long getPlotCourseCooldowns() {
        long currentTimer = 0L;
        for (final Seat seat : this.getSeats()) {
            final Cooldowns cd = Cooldowns.getCooldownsFor(seat.getOccupant(), false);
            if (cd != null) {
                final long remain = cd.isAvaibleAt(717);
                if (remain > currentTimer) {
                    currentTimer = remain;
                }
            }
        }
        return currentTimer;
    }
    
    public String checkCourseRestrictions() {
        long currentTimer = 0L;
        for (final Seat seat : this.getSeats()) {
            try {
                final Player p = Players.getInstance().getPlayer(seat.getOccupant());
                if ((p.isFighting() || p.getEnemyPresense() > 0) && p.getSecondsPlayed() > 300.0f) {
                    return "There are enemies in the vicinity. You fail to focus on a course.";
                }
            }
            catch (NoSuchPlayerException ex) {}
            final Cooldowns cd = Cooldowns.getCooldownsFor(seat.getOccupant(), false);
            if (cd != null) {
                final long remain = cd.isAvaibleAt(717);
                if (remain > currentTimer) {
                    currentTimer = remain;
                }
            }
        }
        if (currentTimer > 0L) {
            return "You must wait another " + Server.getTimeFor(currentTimer) + " to plot a course.";
        }
        return "";
    }
    
    public boolean isPvPBlocking() {
        for (final Seat lSeat : this.seats) {
            try {
                final Player oPlayer = Players.getInstance().getPlayer(lSeat.getOccupant());
                if (oPlayer.isBlockingPvP()) {
                    return true;
                }
            }
            catch (NoSuchPlayerException ex) {}
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(Vehicle.class.getName());
        EMPTYSEATS = new Seat[0];
    }
}

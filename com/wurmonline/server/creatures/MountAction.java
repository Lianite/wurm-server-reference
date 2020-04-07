// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zones;
import javax.annotation.Nullable;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.MiscConstants;

public final class MountAction implements MiscConstants
{
    private Creature creature;
    private Item item;
    private Vehicle vehicle;
    private int seatNum;
    private boolean asDriver;
    private float offz;
    
    public MountAction(@Nullable final Creature aCreature, @Nullable final Item aItem, final Vehicle aVehicle, final int aSeatNum, final boolean aAsDriver, final float aOffz) {
        this.creature = null;
        this.item = null;
        this.vehicle = null;
        this.seatNum = 0;
        this.asDriver = false;
        this.offz = 0.0f;
        this.creature = aCreature;
        this.item = aItem;
        this.vehicle = aVehicle;
        this.seatNum = aSeatNum;
        this.asDriver = aAsDriver;
        this.offz = aOffz;
    }
    
    boolean isBoat() {
        return this.item != null && this.item.isBoat();
    }
    
    Creature getCreature() {
        return this.creature;
    }
    
    Item getItem() {
        return this.item;
    }
    
    Vehicle getVehicle() {
        return this.vehicle;
    }
    
    int getSeatNum() {
        return this.seatNum;
    }
    
    boolean isAsDriver() {
        return this.asDriver;
    }
    
    public float getOffZ() {
        return this.offz;
    }
    
    void sendData(final Creature performer) {
        if (this.item != null) {
            if (this.asDriver) {
                performer.getCurrentTile().sendAttachCreature(performer.getWurmId(), this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum, true);
                final VolaTile t = Zones.getTileOrNull(this.item.getTileX(), this.item.getTileY(), this.item.isOnSurface());
                if (t != null) {
                    t.sendAttachCreature(performer.getWurmId(), this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum, true);
                }
                performer.getMovementScheme().setVehicleRotation(this.item.getRotation());
                performer.getCommunicator().setVehicleController(-1L, this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.vehicle.maxDepth, this.vehicle.maxHeight, this.vehicle.maxHeightDiff, this.item.getRotation(), this.seatNum);
                if (this.item.isBoat()) {
                    performer.getMovementScheme().addMountSpeed(this.vehicle.calculateNewBoatSpeed(false));
                    performer.getMovementScheme().commandingBoat = true;
                    if (this.item.isMooredBoat()) {
                        performer.getCommunicator().sendNormalServerMessage("The " + this.item.getName() + " is currently moored and won't move.");
                        performer.getMovementScheme().setMooredMod(true);
                        performer.getMovementScheme().addWindImpact((byte)0);
                        return;
                    }
                    performer.getMovementScheme().addWindImpact(this.vehicle.getWindImpact());
                }
                else {
                    performer.getMovementScheme().addMountSpeed(this.vehicle.calculateNewVehicleSpeed(true));
                }
            }
            else {
                if (performer.hasLink()) {
                    performer.getCommunicator().attachCreature(-1L, this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                }
                performer.getCurrentTile().sendAttachCreature(performer.getWurmId(), this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                final VolaTile t = Zones.getTileOrNull(this.item.getTileX(), this.item.getTileY(), this.item.isOnSurface());
                t.sendAttachCreature(performer.getWurmId(), this.item.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                if (this.vehicle.pilotId != -10L) {
                    try {
                        final Creature pilot = Server.getInstance().getCreature(this.vehicle.pilotId);
                        if (this.item.isBoat()) {
                            pilot.getMovementScheme().addMountSpeed(this.vehicle.calculateNewBoatSpeed(false));
                        }
                        else {
                            pilot.getMovementScheme().addMountSpeed(this.vehicle.calculateNewVehicleSpeed(true));
                        }
                    }
                    catch (Exception ex) {}
                }
                performer.getMovementScheme().resetBm();
            }
        }
        else if (this.creature != null) {
            if (this.asDriver) {
                performer.getCurrentTile().sendAttachCreature(performer.getWurmId(), this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                final VolaTile t = Zones.getTileOrNull(this.creature.getTileX(), this.creature.getTileY(), this.creature.isOnSurface());
                if (t != null) {
                    t.sendAttachCreature(performer.getWurmId(), this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                }
                performer.getMovementScheme().setVehicleRotation(this.creature.getStatus().getRotation());
                performer.getCommunicator().setVehicleController(-1L, this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.vehicle.maxDepth, this.vehicle.maxHeight, this.vehicle.maxHeightDiff, this.creature.getStatus().getRotation(), this.seatNum);
                performer.getMovementScheme().addMountSpeed(this.vehicle.calculateNewMountSpeed(this.creature, true));
            }
            else {
                if (performer.hasLink()) {
                    performer.getCommunicator().attachCreature(-1L, this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                }
                performer.getCurrentTile().sendAttachCreature(performer.getWurmId(), this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                final VolaTile t = Zones.getTileOrNull(this.creature.getTileX(), this.creature.getTileY(), this.creature.isOnSurface());
                if (t != null) {
                    t.sendAttachCreature(performer.getWurmId(), this.creature.getWurmId(), this.vehicle.seats[this.seatNum].offx, this.vehicle.seats[this.seatNum].offy, this.vehicle.seats[this.seatNum].offz, this.seatNum);
                }
                if (this.vehicle.pilotId != -10L) {
                    try {
                        final Creature pilot = Server.getInstance().getCreature(this.vehicle.pilotId);
                        pilot.getMovementScheme().addMountSpeed(this.vehicle.calculateNewMountSpeed(this.creature, true));
                    }
                    catch (Exception ex2) {}
                }
            }
        }
        performer.removeCarriedWeight(0);
        this.clear();
    }
    
    private void clear() {
        this.creature = null;
        this.item = null;
        this.vehicle = null;
        this.seatNum = 0;
        this.asDriver = false;
        this.offz = 0.0f;
    }
}

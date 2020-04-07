// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Seat implements MiscConstants
{
    private static Logger logger;
    public static final byte TYPE_DRIVER = 0;
    public static final byte TYPE_PASSENGER = 1;
    public static final byte TYPE_HITCHED = 2;
    public final byte type;
    public long occupant;
    public float offx;
    public float offy;
    public float offz;
    private float altOffz;
    public float cover;
    public float manouvre;
    private static int allids;
    int id;
    
    Seat(final byte _type) {
        this.occupant = -10L;
        this.offx = 0.0f;
        this.offy = 0.0f;
        this.offz = 0.0f;
        this.altOffz = 0.0f;
        this.cover = 0.5f;
        this.manouvre = 0.5f;
        this.id = 0;
        this.id = Seat.allids++;
        this.type = _type;
    }
    
    public boolean isOccupied() {
        return this.occupant != -10L;
    }
    
    public boolean occupy(final Vehicle vehicle, final Creature creature) {
        if (this.occupant != -10L) {
            return false;
        }
        if (creature != null) {
            this.occupant = creature.getWurmId();
            final String vehicleName = Vehicle.getVehicleName(vehicle);
            if (this.type == 0) {
                creature.getCommunicator().sendNormalServerMessage(StringUtil.format("You %s on the %s as the %s.", vehicle.embarkString, vehicleName, vehicle.pilotName));
                Server.getInstance().broadCastAction(StringUtil.format("%s %s on the %s as the %s.", creature.getName(), vehicle.embarksString, vehicleName, vehicle.pilotName), creature, 5);
            }
            else if (vehicle.isChair() || vehicle.isBed()) {
                creature.getCommunicator().sendNormalServerMessage(StringUtil.format("You %s on the %s.", vehicle.embarkString, vehicleName));
                Server.getInstance().broadCastAction(StringUtil.format("%s %s on the %s.", creature.getName(), vehicle.embarksString, vehicleName), creature, 5);
            }
            else {
                creature.getCommunicator().sendNormalServerMessage(StringUtil.format("You %s on the %s as a passenger.", vehicle.embarkString, vehicleName));
                Server.getInstance().broadCastAction(StringUtil.format("%s %s on the %s as a passenger.", creature.getName(), vehicle.embarksString, vehicleName), creature, 5);
            }
            return true;
        }
        Seat.logger.warning("A null Creature cannot occupy a seat (" + this + ") in a Vehicle (" + vehicle + ')');
        return false;
    }
    
    boolean leave(final Vehicle vehicle) {
        if (this.occupant != -10L) {
            try {
                final Creature cret = Server.getInstance().getCreature(this.occupant);
                cret.disembark(true);
            }
            catch (NoSuchPlayerException ex) {}
            catch (NoSuchCreatureException ex2) {}
            this.occupant = -10L;
            return true;
        }
        return false;
    }
    
    public float getCover() {
        return this.cover;
    }
    
    public void setCover(final float aCover) {
        this.cover = aCover;
    }
    
    public float getManouvre() {
        return this.manouvre;
    }
    
    public void setManouvre(final float aManouvre) {
        this.manouvre = aManouvre;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int aId) {
        this.id = aId;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public long getOccupant() {
        return this.occupant;
    }
    
    void setOccupant(final long aOccupant) {
        this.occupant = aOccupant;
    }
    
    float getOffx() {
        return this.offx;
    }
    
    void setOffx(final float aOffx) {
        this.offx = aOffx;
    }
    
    float getOffy() {
        return this.offy;
    }
    
    void setOffy(final float aOffy) {
        this.offy = aOffy;
    }
    
    float getOffz() {
        return this.offz;
    }
    
    void setOffz(final float aOffz) {
        this.offz = aOffz;
    }
    
    @Override
    public String toString() {
        final StringBuilder lBuilder = new StringBuilder(200);
        lBuilder.append("Seat [Type: ").append(this.type);
        lBuilder.append(", Occupant: ").append(this.occupant);
        lBuilder.append(", OffsetX: ").append(this.offx);
        lBuilder.append(", OffsetY: ").append(this.offy);
        lBuilder.append(", OffsetZ: ").append(this.offz);
        lBuilder.append(']');
        return lBuilder.toString();
    }
    
    public float getAltOffz() {
        return this.altOffz;
    }
    
    public void setAltOffz(final float aAltOffz) {
        this.altOffz = aAltOffz;
    }
    
    static {
        Seat.logger = Logger.getLogger(Seat.class.getName());
        Seat.allids = 0;
    }
}

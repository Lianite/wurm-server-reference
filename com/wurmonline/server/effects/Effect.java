// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.zones.VolaTile;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;

public abstract class Effect implements Serializable
{
    private static final long serialVersionUID = -7768268294902679751L;
    private long owner;
    private short type;
    private float posX;
    private float posY;
    private float posZ;
    private boolean surfaced;
    private int id;
    private static final Logger logger;
    private long startTime;
    private String effectString;
    private float timeout;
    private float rotationOffset;
    
    Effect(final long aOwner, final short aType, final float aPosX, final float aPosY, final float aPosZ, final boolean aSurfaced) {
        this.id = 0;
        this.timeout = -1.0f;
        this.rotationOffset = 0.0f;
        this.owner = aOwner;
        this.type = aType;
        this.posX = aPosX;
        this.posY = aPosY;
        this.posZ = aPosZ;
        this.surfaced = aSurfaced;
        this.startTime = System.currentTimeMillis();
        try {
            this.save();
        }
        catch (IOException iox) {
            Effect.logger.log(Level.WARNING, "Failed to save effect", iox);
        }
    }
    
    Effect(final int num, final long ownerid, final short typ, final float posx, final float posy, final float posz, final long stime) {
        this.id = 0;
        this.timeout = -1.0f;
        this.rotationOffset = 0.0f;
        this.id = num;
        this.owner = ownerid;
        this.type = typ;
        this.posX = posx;
        this.posY = posy;
        this.posZ = posz;
        this.startTime = stime;
    }
    
    Effect(final long aOwner, final int aNumber) throws IOException {
        this.id = 0;
        this.timeout = -1.0f;
        this.rotationOffset = 0.0f;
        this.owner = aOwner;
        this.id = aNumber;
        this.load();
    }
    
    public final long getStartTime() {
        return this.startTime;
    }
    
    final void setStartTime(final long aStartTime) {
        this.startTime = aStartTime;
    }
    
    public final int getId() {
        return this.id;
    }
    
    final void setId(final int aId) {
        this.id = aId;
    }
    
    public final int getTileX() {
        return (int)this.posX >> 2;
    }
    
    public final int getTileY() {
        return (int)this.posY >> 2;
    }
    
    public final float getPosX() {
        return this.posX;
    }
    
    public final void setPosX(final float positionX) {
        this.posX = positionX;
    }
    
    public final void setPosY(final float positionY) {
        this.posY = positionY;
    }
    
    public final float getPosY() {
        return this.posY;
    }
    
    public final float getPosZ() {
        return this.posZ;
    }
    
    public final void setPosZ(final float aPosZ) {
        this.posZ = aPosZ;
    }
    
    public final float calculatePosZ(final VolaTile tile) {
        return Zones.calculatePosZ(this.getPosX(), this.getPosY(), tile, this.isOnSurface(), false, this.getPosZ(), null, -10L);
    }
    
    public final long getOwner() {
        return this.owner;
    }
    
    final void setOwner(final long aOwner) {
        this.owner = aOwner;
    }
    
    public final short getType() {
        return this.type;
    }
    
    public final boolean isGlobal() {
        return this.type == 2 || this.type == 3 || this.type == 16 || this.type == 19 || this.type == 4 || this.type == 25;
    }
    
    final void setType(final short aType) {
        this.type = aType;
    }
    
    public final void setSurfaced(final boolean aSurfaced) {
        this.surfaced = aSurfaced;
    }
    
    public final boolean isOnSurface() {
        return this.surfaced;
    }
    
    public final byte getLayer() {
        return (byte)(this.surfaced ? 0 : -1);
    }
    
    public final void setEffectString(final String effString) {
        this.effectString = effString;
    }
    
    public final String getEffectString() {
        return this.effectString;
    }
    
    public final void setTimeout(final float timeout) {
        this.timeout = timeout;
    }
    
    public final float getTimeout() {
        return this.timeout;
    }
    
    public final void setRotationOffset(final float rotationOffset) {
        this.rotationOffset = rotationOffset;
    }
    
    public final float getRotationOffset() {
        return this.rotationOffset;
    }
    
    public void setPosXYZ(final float posX, final float posY, final float posZ, final boolean sendUpdate) {
        if (posX == this.getPosX() && posY == this.getPosY()) {
            if (posZ == this.getPosZ()) {
                return;
            }
        }
        try {
            if (sendUpdate) {
                final Zone zone = Zones.getZone((int)(this.getPosX() / 4.0f), (int)(this.getPosY() / 4.0f), this.isOnSurface());
                zone.removeEffect(this);
            }
            this.setPosX(posX);
            this.setPosY(posY);
            this.setPosZ(posZ);
            if (sendUpdate) {
                final Zone zone = Zones.getZone((int)(this.getPosX() / 4.0f), (int)(this.getPosY() / 4.0f), this.isOnSurface());
                zone.addEffect(this, false);
            }
        }
        catch (NoSuchZoneException ex) {}
    }
    
    public abstract void save() throws IOException;
    
    abstract void load() throws IOException;
    
    abstract void delete();
    
    static {
        logger = Logger.getLogger(Effect.class.getName());
    }
}

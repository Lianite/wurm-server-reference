// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.creatures.Creature;

public abstract class Guard
{
    final int villageId;
    final Creature creature;
    long expireDate;
    
    Guard(final int aVillageId, final Creature aCreature, final long aExpireDate) {
        this.villageId = aVillageId;
        this.creature = aCreature;
        this.expireDate = aExpireDate;
    }
    
    public final long getExpireDate() {
        return this.expireDate;
    }
    
    public final Creature getCreature() {
        return this.creature;
    }
    
    public final int getVillageId() {
        return this.villageId;
    }
    
    abstract void save();
    
    abstract void setExpireDate(final long p0);
    
    abstract void delete();
    
    @Override
    public String toString() {
        return "Guard [villageId=" + this.villageId + ", expireDate=" + this.expireDate + ", creature=" + this.creature + ']';
    }
}

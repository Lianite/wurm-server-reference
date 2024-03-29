// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

public abstract class VillageWar
{
    final Village villone;
    public final Village villtwo;
    
    VillageWar(final Village vone, final Village vtwo) {
        this.villone = vone;
        this.villtwo = vtwo;
    }
    
    public final Village getVillone() {
        return this.villone;
    }
    
    public final Village getVilltwo() {
        return this.villtwo;
    }
    
    abstract void save();
    
    abstract void delete();
    
    @Override
    public final String toString() {
        return "VillageWar [" + this.villone + " and " + this.villtwo + ']';
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

public abstract class Alliance
{
    final Village villone;
    final Village villtwo;
    
    Alliance(final Village vone, final Village vtwo) {
        this.villone = vone;
        this.villtwo = vtwo;
    }
    
    final Village getVillone() {
        return this.villone;
    }
    
    final Village getVilltwo() {
        return this.villtwo;
    }
    
    abstract void save();
    
    abstract void delete();
    
    @Override
    public final String toString() {
        return "Alliance [" + this.villone + " and " + this.villtwo + ']';
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

public class ClosestVillage
{
    private final String name;
    private final short distance;
    
    ClosestVillage(final String name, final short distance) {
        this.name = name;
        this.distance = distance;
    }
    
    public String getName() {
        return this.name;
    }
    
    public short getDistance() {
        return this.distance;
    }
}

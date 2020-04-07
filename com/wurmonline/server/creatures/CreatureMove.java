// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

public final class CreatureMove
{
    public long timestamp;
    public float diffX;
    public float diffY;
    public float diffZ;
    public int rotation;
    
    public CreatureMove() {
        this.timestamp = 0L;
    }
    
    public void resetXYZ() {
        this.diffX = 0.0f;
        this.diffY = 0.0f;
        this.diffZ = 0.0f;
    }
}

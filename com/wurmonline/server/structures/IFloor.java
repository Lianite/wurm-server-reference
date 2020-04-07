// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.items.Item;

public interface IFloor extends StructureSupport
{
    float getDamageModifierForItem(final Item p0);
    
    long getStructureId();
    
    VolaTile getTile();
    
    boolean isOnPvPServer();
    
    int getTileX();
    
    int getTileY();
    
    float getCurrentQualityLevel();
    
    float getDamage();
    
    boolean setDamage(final float p0);
    
    float getQualityLevel();
    
    void destroyOrRevertToPlan();
    
    boolean isAPlan();
    
    boolean isThatch();
    
    boolean isStone();
    
    boolean isSandstone();
    
    boolean isSlate();
    
    boolean isMarble();
    
    boolean isMetal();
    
    boolean isWood();
    
    boolean isFinished();
    
    int getRepairItemTemplate();
    
    boolean setQualityLevel(final float p0);
    
    void setLastUsed(final long p0);
    
    boolean isOnSurface();
    
    boolean equals(final StructureSupport p0);
}

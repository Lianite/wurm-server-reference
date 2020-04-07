// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

public interface StructureSupport
{
    boolean supports(final StructureSupport p0);
    
    int getFloorLevel();
    
    int getStartX();
    
    int getStartY();
    
    int getMinX();
    
    int getMinY();
    
    boolean isHorizontal();
    
    boolean isFloor();
    
    boolean isFence();
    
    boolean isWall();
    
    int getEndX();
    
    int getEndY();
    
    boolean isSupportedByGround();
    
    boolean supports();
    
    String getName();
    
    long getId();
    
    boolean equals(final StructureSupport p0);
}

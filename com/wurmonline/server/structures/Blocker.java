// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.math.TilePos;
import com.wurmonline.math.Vector3f;
import com.wurmonline.server.creatures.Creature;

public interface Blocker
{
    public static final int TYPE_NONE = 0;
    public static final int TYPE_FENCE = 1;
    public static final int TYPE_WALL = 2;
    public static final int TYPE_FLOOR = 3;
    public static final int TYPE_ALL = 4;
    public static final int TYPE_ALL_BUT_OPEN = 5;
    public static final int TYPE_MOVEMENT = 6;
    public static final int TYPE_TARGET_TILE = 7;
    public static final int TYPE_NOT_DOOR = 8;
    
    boolean isFence();
    
    boolean isStone();
    
    boolean isWood();
    
    boolean isMetal();
    
    boolean isWall();
    
    boolean isDoor();
    
    boolean isFloor();
    
    boolean isRoof();
    
    boolean isTile();
    
    boolean isStair();
    
    boolean canBeOpenedBy(final Creature p0, final boolean p1);
    
    int getFloorLevel();
    
    String getName();
    
    Vector3f getNormal();
    
    Vector3f getCenterPoint();
    
    int getTileX();
    
    int getTileY();
    
    boolean isOnSurface();
    
    float getPositionX();
    
    float getPositionY();
    
    boolean isHorizontal();
    
    boolean isWithinFloorLevels(final int p0, final int p1);
    
    Vector3f isBlocking(final Creature p0, final Vector3f p1, final Vector3f p2, final Vector3f p3, final int p4, final long p5, final boolean p6);
    
    float getBlockPercent(final Creature p0);
    
    float getDamageModifier();
    
    boolean setDamage(final float p0);
    
    float getDamage();
    
    long getId();
    
    long getTempId();
    
    float getMinZ();
    
    float getMaxZ();
    
    float getFloorZ();
    
    boolean isWithinZ(final float p0, final float p1, final boolean p2);
    
    boolean isOnSouthBorder(final TilePos p0);
    
    boolean isOnNorthBorder(final TilePos p0);
    
    boolean isOnWestBorder(final TilePos p0);
    
    boolean isOnEastBorder(final TilePos p0);
}

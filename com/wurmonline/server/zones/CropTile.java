// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

public class CropTile implements Comparable<CropTile>
{
    private int data;
    private int x;
    private int y;
    private int cropType;
    private boolean onSurface;
    
    public CropTile(final int tileData, final int tileX, final int tileY, final int typeOfCrop, final boolean surface) {
        this.data = tileData;
        this.x = tileX;
        this.y = tileY;
        this.cropType = typeOfCrop;
        this.onSurface = surface;
    }
    
    public final int getData() {
        return this.data;
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    public final int getCropType() {
        return this.cropType;
    }
    
    public final boolean isOnSurface() {
        return this.onSurface;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CropTile)) {
            return false;
        }
        final CropTile c = (CropTile)obj;
        return c.getCropType() == this.getCropType() && c.getData() == this.getData() && c.getX() == this.getX() && c.getY() == this.getY() && c.isOnSurface() == this.isOnSurface();
    }
    
    @Override
    public int compareTo(final CropTile o) {
        final int EQUAL = 0;
        final int AFTER = 1;
        final int BEFORE = -1;
        if (o.equals(this)) {
            return 0;
        }
        if (o.getCropType() > this.getCropType()) {
            return 1;
        }
        return -1;
    }
}

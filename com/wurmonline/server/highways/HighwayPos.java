// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import javax.annotation.Nullable;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.BridgePart;

public class HighwayPos
{
    private int tilex;
    private int tiley;
    private boolean onSurface;
    private BridgePart bridgePart;
    private Floor floor;
    
    public HighwayPos(final int tilex, final int tiley, final boolean onSurface, @Nullable final BridgePart bridgePart, @Nullable final Floor floor) {
        this.tilex = tilex;
        this.tiley = tiley;
        this.onSurface = onSurface;
        this.bridgePart = bridgePart;
        this.floor = floor;
    }
    
    public int getTilex() {
        return this.tilex;
    }
    
    public int getTiley() {
        return this.tiley;
    }
    
    public boolean isOnSurface() {
        return this.onSurface;
    }
    
    public boolean isSurfaceTile() {
        return this.onSurface && this.bridgePart == null && this.floor == null;
    }
    
    public boolean isCaveTile() {
        return !this.onSurface && this.bridgePart == null && this.floor == null;
    }
    
    @Nullable
    public BridgePart getBridgePart() {
        return this.bridgePart;
    }
    
    @Nullable
    public Floor getFloor() {
        return this.floor;
    }
    
    public long getBridgeId() {
        if (this.bridgePart == null) {
            return -10L;
        }
        return this.bridgePart.getStructureId();
    }
    
    public int getFloorLevel() {
        if (this.floor == null) {
            return 0;
        }
        return this.floor.getFloorLevel();
    }
    
    public void setX(final int tilex) {
        this.tilex = tilex;
    }
    
    public void setY(final int tiley) {
        this.tiley = tiley;
    }
    
    public void setOnSurface(final boolean onSurface) {
        this.onSurface = onSurface;
    }
    
    public void setBridgePart(@Nullable final BridgePart bridgePart) {
        this.bridgePart = bridgePart;
    }
    
    public void setFloor(@Nullable final Floor floor) {
        this.floor = floor;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.ai.PathTile;

public class LongTarget extends PathTile
{
    private Creature ctarget;
    private Item itemTarget;
    private long target;
    private int epicMission;
    private int missionTrigger;
    private final int startx;
    private final int starty;
    private final long startTime;
    
    public LongTarget(final int tx, final int ty, final int t, final boolean surf, final int aFloorLevel, final Creature starter) {
        super(tx, ty, t, surf, aFloorLevel);
        this.target = -10L;
        this.epicMission = -1;
        this.missionTrigger = -1;
        this.startx = starter.getTileX();
        this.starty = starter.getTileY();
        this.startTime = System.currentTimeMillis();
    }
    
    public Creature getCreatureTarget() {
        return this.ctarget;
    }
    
    public void setCreaturetarget(final Creature ctarget) {
        this.ctarget = ctarget;
    }
    
    public Item getItemTarget() {
        return this.itemTarget;
    }
    
    public void setItemTarget(final Item itemTarget) {
        this.itemTarget = itemTarget;
    }
    
    public void setTileX(final int tileX) {
        this.tilex = tileX;
    }
    
    public void setTileY(final int tileY) {
        this.tiley = tileY;
    }
    
    public long getMissionTarget() {
        return this.target;
    }
    
    public void setMissionTarget(final long target) {
        this.target = target;
    }
    
    public int getEpicMission() {
        return this.epicMission;
    }
    
    public void setEpicMission(final int epicMission) {
        this.epicMission = epicMission;
    }
    
    public int getMissionTrigger() {
        return this.missionTrigger;
    }
    
    public void setMissionTrigger(final int missionTrigger) {
        this.missionTrigger = missionTrigger;
    }
    
    public int getStartx() {
        return this.startx;
    }
    
    public int getStarty() {
        return this.starty;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.Date;
import com.wurmonline.math.TilePos;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public class Rift implements TimeConstants, MiscConstants
{
    private static final Logger logger;
    
    public Rift(final TilePos center, final float size) {
    }
    
    public TilePos getCenterPos() {
        return new TilePos();
    }
    
    public float getSize() {
        return 0.0f;
    }
    
    public void setSize(final float size) {
    }
    
    public int getWave() {
        return 0;
    }
    
    public void setWave(final int wave) {
    }
    
    public float getPercentWaveCompletion() {
        return 100.0f;
    }
    
    public void setPercentWaveCompletion(final float percent, final boolean save) {
    }
    
    private static final void createTable() {
    }
    
    public final void save(final boolean create) {
    }
    
    public final void saveActivated() {
    }
    
    public final void saveEnded() {
    }
    
    public static final void loadRifts() {
    }
    
    public int getNumber() {
        return 0;
    }
    
    public void setNumber(final int number) {
    }
    
    public int getState() {
        return 0;
    }
    
    public void setState(final int state) {
    }
    
    public boolean isActive() {
        return false;
    }
    
    public void setActive(final boolean active) {
    }
    
    public Date getInitiated() {
        return new Date(System.currentTimeMillis());
    }
    
    public void setInitiated(final Date initiated, final boolean initiate) {
    }
    
    public Date getActivated() {
        return this.getInitiated();
    }
    
    public void setActivated(final Date activated, final boolean saveNow) {
    }
    
    public Date getEnded() {
        return this.getInitiated();
    }
    
    public void setEnded(final Date ended, final boolean saveNow) {
    }
    
    public final void poll() {
    }
    
    public final void activateWave() {
    }
    
    private final void spawnRiftItems() {
    }
    
    private final void spawnTraps() {
    }
    
    private static final int getRandomTrapType() {
        return 0;
    }
    
    private final void spawnCreatures() {
    }
    
    public static final boolean waterFound(final int tilex, final int tiley) {
        return false;
    }
    
    public String getDescription() {
        return "";
    }
    
    public void setDescription(final String description) {
    }
    
    public static Rift getActiveRift() {
        return null;
    }
    
    public static void setActiveRift(final Rift activeRift) {
    }
    
    public byte getType() {
        return 0;
    }
    
    public void setType(final byte type) {
    }
    
    public static Rift getLastRift() {
        return null;
    }
    
    public static void setLastRift(final Rift lastRift) {
    }
    
    static {
        logger = Logger.getLogger(Rift.class.getName());
    }
}

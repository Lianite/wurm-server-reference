// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

public class ChallengeScore
{
    private final int type;
    private float points;
    private float lastPoints;
    private long lastUpdated;
    
    public ChallengeScore(final int scoreType, final float numPoints, final long aLastUpdated, final float aLastPoints) {
        this.type = scoreType;
        this.setPoints(numPoints);
        this.setLastPoints(aLastPoints);
        this.setLastUpdated(aLastUpdated);
    }
    
    public int getType() {
        return this.type;
    }
    
    public float getPoints() {
        return this.points;
    }
    
    public void setPoints(final float aPoints) {
        this.points = aPoints;
    }
    
    public long getLastUpdated() {
        return this.lastUpdated;
    }
    
    public void setLastUpdated(final long aLastUpdated) {
        this.lastUpdated = aLastUpdated;
    }
    
    public float getLastPoints() {
        return this.lastPoints;
    }
    
    public void setLastPoints(final float aLastPoints) {
        this.lastPoints = aLastPoints;
    }
}

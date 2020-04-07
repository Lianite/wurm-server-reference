// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.math.Vector3f;
import java.util.LinkedList;

public final class BlockingResult
{
    private LinkedList<Blocker> blockers;
    private LinkedList<Vector3f> intersections;
    private float totalCover;
    private static final Blocker[] emptyBlockers;
    private float estimatedBlockingTime;
    private float actualBlockingTime;
    
    public final float addBlocker(final Blocker blockerToAdd, final Vector3f intersection, final float factorToAdd) {
        if (this.blockers == null) {
            this.blockers = new LinkedList<Blocker>();
            this.intersections = new LinkedList<Vector3f>();
        }
        this.blockers.add(blockerToAdd);
        this.intersections.add(intersection);
        this.addBlockingFactor(factorToAdd);
        return this.totalCover;
    }
    
    public final void addBlockingFactor(final float factorToAdd) {
        this.totalCover += factorToAdd;
    }
    
    public final Blocker getFirstBlocker() {
        if (this.blockers != null && !this.blockers.isEmpty()) {
            return this.blockers.getFirst();
        }
        return null;
    }
    
    public final Blocker getLastBlocker() {
        if (this.blockers != null && !this.blockers.isEmpty()) {
            return this.blockers.getLast();
        }
        return null;
    }
    
    public final Vector3f getFirstIntersection() {
        if (this.intersections != null && !this.intersections.isEmpty()) {
            return this.intersections.getFirst();
        }
        return null;
    }
    
    public final Vector3f getLastIntersection() {
        if (this.intersections != null && !this.intersections.isEmpty()) {
            return this.intersections.getLast();
        }
        return null;
    }
    
    public final float getTotalCover() {
        return this.totalCover;
    }
    
    public final void removeBlocker(final Blocker blocker) {
        if (this.blockers != null) {
            this.blockers.remove(blocker);
        }
    }
    
    public final Blocker[] getBlockerArray() {
        if (this.blockers == null || this.blockers.isEmpty()) {
            return BlockingResult.emptyBlockers;
        }
        return this.blockers.toArray(new Blocker[this.blockers.size()]);
    }
    
    public float getActualBlockingTime() {
        return this.actualBlockingTime;
    }
    
    public void setActualBlockingTime(final float aActualBlockingTime) {
        this.actualBlockingTime = aActualBlockingTime;
    }
    
    public float getEstimatedBlockingTime() {
        return this.estimatedBlockingTime;
    }
    
    public void setEstimatedBlockingTime(final float aEstimatedBlockingTime) {
        this.estimatedBlockingTime = aEstimatedBlockingTime;
    }
    
    static {
        emptyBlockers = new Blocker[0];
    }
}

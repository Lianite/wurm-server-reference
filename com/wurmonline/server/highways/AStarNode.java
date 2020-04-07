// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public abstract class AStarNode implements Comparable<AStarNode>
{
    AStarNode pathParent;
    float costFromStart;
    float estimatedCostToGoal;
    Route pathRoute;
    
    public float getCost() {
        return this.costFromStart + this.estimatedCostToGoal;
    }
    
    @Override
    public int compareTo(final AStarNode other) {
        final float v = this.getCost() - other.getCost();
        return (v > 0.0f) ? 1 : ((v < 0.0f) ? -1 : 0);
    }
    
    public abstract float getCost(final AStarNode p0);
    
    public abstract float getEstimatedCost(final AStarNode p0);
    
    public abstract List<AStarNode> getNeighbours(final byte p0);
    
    public abstract ConcurrentHashMap<Byte, Route> getRoutes(final byte p0);
}

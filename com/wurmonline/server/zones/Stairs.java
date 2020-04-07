// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class Stairs
{
    public static final Map<Integer, Set<Integer>> stairTiles;
    
    public static final void addStair(final int volatileId, final int floorLevel) {
        Set<Integer> stairSet = Stairs.stairTiles.get(volatileId);
        if (stairSet == null) {
            stairSet = new HashSet<Integer>();
        }
        stairSet.add(floorLevel);
        Stairs.stairTiles.put(volatileId, stairSet);
    }
    
    public static final boolean hasStair(final int volatileId, final int floorLevel) {
        final Set<Integer> stairSet = Stairs.stairTiles.get(volatileId);
        return stairSet != null && stairSet.contains(floorLevel);
    }
    
    public static final void removeStair(final int volatileId, final int floorLevel) {
        final Set<Integer> stairSet = Stairs.stairTiles.get(volatileId);
        if (stairSet == null) {
            return;
        }
        stairSet.remove(floorLevel);
    }
    
    static {
        stairTiles = new ConcurrentHashMap<Integer, Set<Integer>>();
    }
}

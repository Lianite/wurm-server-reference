// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public final class Encounter
{
    private final Map<Integer, Integer> types;
    
    public Encounter() {
        this.types = new HashMap<Integer, Integer>();
    }
    
    public void addType(final int creatureTemplateId, final int nums) {
        this.types.put(creatureTemplateId, nums);
    }
    
    public Map<Integer, Integer> getTypes() {
        return this.types;
    }
    
    @Override
    public final String toString() {
        String toRet = "";
        for (final Map.Entry<Integer, Integer> entry : this.types.entrySet()) {
            toRet = toRet + "Type " + entry.getKey() + " Numbers=" + entry.getValue() + ", ";
        }
        return toRet;
    }
}

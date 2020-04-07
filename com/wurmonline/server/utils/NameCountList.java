// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.utils;

import java.util.Iterator;
import com.wurmonline.shared.util.StringUtilities;
import java.util.HashMap;
import java.util.Map;

public class NameCountList
{
    final Map<String, Integer> localMap;
    
    public NameCountList() {
        this.localMap = new HashMap<String, Integer>();
    }
    
    public void add(final String name) {
        int cnt = 1;
        if (this.localMap.containsKey(name)) {
            cnt = this.localMap.get(name) + 1;
        }
        this.localMap.put(name, cnt);
    }
    
    public boolean isEmpty() {
        return this.localMap.isEmpty();
    }
    
    @Override
    public String toString() {
        String line = "";
        int count = 0;
        for (final Map.Entry<String, Integer> entry : this.localMap.entrySet()) {
            ++count;
            if (line.length() > 0) {
                if (count == this.localMap.size()) {
                    line += " and ";
                }
                else {
                    line += ", ";
                }
            }
            line = line + StringUtilities.getWordForNumber(entry.getValue()) + " " + entry.getKey();
        }
        return line;
    }
}

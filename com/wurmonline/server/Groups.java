// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;

public final class Groups
{
    private static final Map<String, Group> groups;
    
    public static void addGroup(final Group group) {
        Groups.groups.put(group.getName(), group);
    }
    
    public static void removeGroup(final String name) {
        Groups.groups.remove(name);
    }
    
    public static void renameGroup(final String oldName, final String newName) {
        final Group g = Groups.groups.remove(oldName);
        if (g != null) {
            g.setName(newName);
            Groups.groups.put(newName, g);
        }
    }
    
    public static Group getGroup(final String name) throws NoSuchGroupException {
        final Group toReturn = Groups.groups.get(name);
        if (toReturn == null) {
            throw new NoSuchGroupException(name);
        }
        return toReturn;
    }
    
    public static final Team getTeamForOfflineMember(final long wurmid) {
        for (final Group g : Groups.groups.values()) {
            if (g.isTeam() && g.containsOfflineMember(wurmid)) {
                return (Team)g;
            }
        }
        return null;
    }
    
    static {
        groups = new ConcurrentHashMap<String, Group>();
    }
}

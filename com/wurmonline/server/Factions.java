// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;
import java.util.Map;

public final class Factions
{
    private static Factions instance;
    @GuardedBy("FACTIONS_RW_LOCK")
    private static Map<String, Faction> factions;
    private static final ReentrantReadWriteLock FACTIONS_RW_LOCK;
    
    public static Factions getInstance() {
        if (Factions.instance == null) {
            Factions.instance = new Factions();
        }
        return Factions.instance;
    }
    
    private Factions() {
        Factions.FACTIONS_RW_LOCK.writeLock().lock();
        try {
            Factions.factions = new HashMap<String, Faction>();
        }
        finally {
            Factions.FACTIONS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public static void addFaction(final Faction faction) {
        Factions.FACTIONS_RW_LOCK.writeLock().lock();
        try {
            Factions.factions.put(faction.getName(), faction);
        }
        finally {
            Factions.FACTIONS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public static Faction getFaction(final String name) throws Exception {
        Factions.FACTIONS_RW_LOCK.readLock().lock();
        try {
            final Faction toReturn = Factions.factions.get(name);
            if (toReturn == null) {
                throw new WurmServerException("No faction with name " + name);
            }
            return toReturn;
        }
        finally {
            Factions.FACTIONS_RW_LOCK.readLock().unlock();
        }
    }
    
    static {
        Factions.instance = null;
        FACTIONS_RW_LOCK = new ReentrantReadWriteLock();
    }
}

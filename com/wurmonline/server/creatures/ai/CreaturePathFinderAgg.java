// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import java.util.TimerTask;

public class CreaturePathFinderAgg extends TimerTask
{
    private final Map<Creature, PathTile> pathTargets;
    private boolean keeprunning;
    public static final long SLEEP_TIME = 25L;
    private static final StaticPathFinderAgg pathFinder;
    private static Logger logger;
    private static boolean log;
    
    public final void addTarget(final Creature c, final PathTile target) {
        this.pathTargets.put(c, target);
    }
    
    public final void removeTarget(final Creature c) {
        this.pathTargets.remove(c);
    }
    
    public CreaturePathFinderAgg() {
        this.pathTargets = new ConcurrentHashMap<Creature, PathTile>();
        this.keeprunning = true;
    }
    
    public boolean isLog() {
        return CreaturePathFinderAgg.log;
    }
    
    public final void toggleLog() {
        this.setLog(!this.isLog());
    }
    
    public void setLog(final boolean nlog) {
        CreaturePathFinderAgg.log = nlog;
    }
    
    public final void startRunning() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 30000L, 25L);
    }
    
    public final void shutDown() {
        this.keeprunning = false;
    }
    
    @Override
    public void run() {
        if (this.keeprunning) {
            final long now = System.currentTimeMillis();
            if (!this.pathTargets.isEmpty()) {
                final Iterator<Map.Entry<Creature, PathTile>> it = this.pathTargets.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry<Creature, PathTile> entry = it.next();
                    final Creature creature = entry.getKey();
                    final PathTile p = entry.getValue();
                    try {
                        final Path path = creature.findPath(p.getTileX(), p.getTileY(), CreaturePathFinderAgg.pathFinder);
                        if (path == null) {
                            continue;
                        }
                        if (p.hasSpecificPos()) {
                            final PathTile lastTile = path.getPathTiles().getLast();
                            lastTile.setSpecificPos(p.getPosX(), p.getPosY());
                        }
                        creature.getStatus().setPath(path);
                        creature.receivedPath = true;
                        it.remove();
                    }
                    catch (NoPathException np) {
                        it.remove();
                        creature.setPathing(false, false);
                    }
                }
            }
            if (CreaturePathFinderAgg.log && System.currentTimeMillis() - now > 0L) {
                CreaturePathFinderAgg.logger.log(Level.INFO, "AGG Finding paths took " + (System.currentTimeMillis() - now) + " ms for " + this.pathTargets.size());
            }
        }
        else {
            CreaturePathFinderAgg.logger.log(Level.INFO, "Shutting down Agg pathfinder");
            this.cancel();
        }
    }
    
    static {
        pathFinder = new StaticPathFinderAgg();
        CreaturePathFinderAgg.logger = Logger.getLogger(CreaturePathFinderAgg.class.getName());
        CreaturePathFinderAgg.log = false;
    }
}

// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.highways;

import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Items;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class HighwayFinder extends Thread implements MiscConstants
{
    private static Logger logger;
    private static final ConcurrentLinkedDeque<PathToCalculate> pathingQueue;
    private boolean shouldStop;
    private boolean sleeping;
    private int waystoneno;
    
    public HighwayFinder() {
        super("HighwayFinder-Thread");
        this.shouldStop = false;
        this.sleeping = false;
        this.waystoneno = 0;
    }
    
    @Override
    public void run() {
        while (!this.shouldStop) {
            try {
                final PathToCalculate nextPath = getNextPathToCalculate();
                if (nextPath != null) {
                    nextPath.calculate();
                    this.sleeping = true;
                    Thread.sleep(100L);
                    this.sleeping = false;
                }
                else {
                    this.sleeping = true;
                    Thread.sleep(15000L);
                    this.sleeping = false;
                    final int nextwaystone = this.waystoneno++;
                    final Item[] waystones = Items.getWaystones();
                    if (nextwaystone >= waystones.length) {
                        this.waystoneno = 0;
                    }
                    else {
                        final Item waystone = waystones[nextwaystone];
                        final Node startNode = Routes.getNode(waystone);
                        HighwayFinder.pathingQueue.add(new PathToCalculate(null, startNode, null, (byte)0));
                    }
                }
            }
            catch (InterruptedException e) {
                this.sleeping = false;
            }
        }
    }
    
    public void shouldStop() {
        this.shouldStop = true;
        this.interrupt();
    }
    
    boolean isSleeping() {
        return this.sleeping;
    }
    
    public static final void queueHighwayFinding(final Creature creature, final Node startNode, final Village village, final byte checkDir) {
        final HighwayFinder highwayThread = Server.getInstance().getHighwayFinderThread();
        if (highwayThread != null) {
            HighwayFinder.pathingQueue.add(new PathToCalculate(creature, startNode, village, checkDir));
            if (highwayThread.isSleeping()) {
                highwayThread.interrupt();
            }
        }
    }
    
    private static final PathToCalculate getNextPathToCalculate() {
        return HighwayFinder.pathingQueue.pollFirst();
    }
    
    static {
        HighwayFinder.logger = Logger.getLogger(HighwayFinder.class.getName());
        pathingQueue = new ConcurrentLinkedDeque<PathToCalculate>();
    }
}
